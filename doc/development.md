# Introduction

This document describes how to develop the Synchronoss Analytics
Workbench (SAW) project.

# Requirements

To prepare for building the project, execute the following steps:

1. Install Java 8

2. Install Maven 3.5.0 (for building, unit tests and packaging)

3. Install [Docker] (for integration tests and local deployment)

4. Install [Firefox] (for end-to-end tests)

5. Install [PlantUML] (for rendering external documentation)

6. Configure Docker to allocate at least 8 GB memory and the maximum
   number of CPUs for containers

Because of the Docker memory requirements mentioned above, the
development machine needs to have at least 16 GB of memory.

Note: There is currently an incompatibility with Java 9 (SAW Transport
Service gives a "scala.reflect.internal.MissingRequirementError"
errors when building), so Java 8 specifically must be used.

Note: Instructions for how to set up the above on a Mac can be found
in the [Mac setup instructions].  Additionally there are [Windows
setup instructions].

[Docker]: https://www.docker.com/community-edition
[Firefox]: https://getfirefox.com/
[PlantUML]: http://plantuml.com/
[Mac setup instructions]: development-mac.md
[Windows setup instructions]: development-windows.md

# Building and testing

To build and test the project execute the following commands:

        $ cd saw
        $ mvn verify

This includes running both unit and integration tests.  The release
package will be located at `dist/target/saw-*.tgz`.

Note: The Docker daemon must be running while building to ensure the
integration tests can run.

# Running full system locally

To build and run the full SAW system locally in development mode,
execute the following commands to start SAW in Docker containers:

        $ cd saw
        $ mvn package
        $ mvn -Ddocker-start=local

Note: The Docker daemon must be running to be able to build and run
containers.  If you are unable to run Docker containers locally, see
instructions for using the Docker Machine [cloud] alternative.

The first run will take longer as Docker downloads and builds images
that will subsequently be available in the image build cache.  After
the command has completed the SAW start page can be accessed
at [http://localhost/](http://localhost/).

To enter a shell inside the main SAW container, execute the following
command:

        $ docker exec -it saw bash

While inside the container, the following commands can be used as
starting points to investigate installed SAW services and packages:

        $ systemctl status saw-*
        $ systemctl list-units saw-*
        $ systemctl list-timers saw-*
        $ journalctl -u saw-*
        $ rpm -qa saw-*

The SAW containers can be stopped using the following command:

        $ docker stop saw saw-mapr

In case containers have been left behind and prevent running new SAW
containers, existing containers can be removed by executing the
following command:

        $ docker rm -f saw saw-mapr

[cloud]: development-cloud.md

# Running system tests using local deployment

All system tests are run automatically as part of `mvn verify`.
However, as each such invocation will involve building the entire
package and starting a new container, it can take minutes to complete.
To iterate faster on specific system tests against an existing local
deployment, run the following command:

        mvn test-compile failsafe:integration-test -Pdocker-start

The above command will immediately start executing system tests
against an existing local SAW deployment.  Therefore it is possible to
edit system tests, run them and get feedback in less than a minute.

# Running SAW Web in development mode using local deployment

When doing front-end development developers typically run the SAW Web
application using NPM commands out of the `saw-web` source code
directory.  To configure SAW Web to connect to a local SAW deployment,
edit the `saw-web/appConfig.js` file as follows to replace the second
occurrence of `apiUrl`:

        apiUrl = 'http://localhost';

# Editing datasets in local SAW deployment

When deploying SAW locally, it is possible to edit datasets and have
changes immediately reflected in analysis executions.  This can be
useful for exploring how different SAW features behave with varying
data.

## Editing report datasets

To edit the sample report metric and data in a running container,
execute the following commands:

        $ docker exec -it saw bash
        $ cd /root/saw-metrics/sample-spark
        $ vi data.ndjson
        $ vi semantic.json
        $ ./load-metric

To edit the sample report metric and data permanently, edit the
`data.ndjson` and `semantic.json` files in the source code tree and
rebuild the container.

The test data is read in using the [Spark JSON datasets] support.  The
Spark documentation does not seem to specify the mapping from JSON
data types to Spark data types.  However, by knowing the [JSON data
types], the [Spark data types] and then looking at the [Spark JSON
reader] source code it is possible to derive the mapping.

[Spark JSON datasets]: https://spark.apache.org/docs/2.1.2/sql-programming-guide.html#json-datasets
[JSON data types]: https://tools.ietf.org/html/rfc7159#section-3
[Spark data types]: http://spark.apache.org/docs/2.1.2/sql-programming-guide.html#data-types
[Spark JSON reader]: https://github.com/apache/spark/blob/branch-2.1/sql/catalyst/src/main/scala/org/apache/spark/sql/catalyst/json/JacksonParser.scala

## Editing pivot and chart datasets

To edit the sample pivot/chart metric and data in a running container,
execute the following commands:

        $ docker exec -it saw bash
        $ cd /root/saw-metrics/sample-elasticsearch
        $ vi data-*.json
        $ vi semantic.json
        $ ./load-metric

To edit the sample pivot/chart metric and data permanently, edit the
`data-*.json` and `semantic.json` files in the source code tree and
rebuild the container.

The test data is loaded by sending it in JSON format to the
Elasticsearch REST API.  The [JSON data types] are mapped to
[Elasticsearch data types] according to the [Elasticsearch dynamic
field mapping].

[JSON data types]: https://tools.ietf.org/html/rfc7159#section-3
[Elasticsearch data types]: https://www.elastic.co/guide/en/elasticsearch/reference/5.2/mapping-types.html
[Elasticsearch dynamic field mapping]: https://www.elastic.co/guide/en/elasticsearch/reference/5.2/dynamic-field-mapping.html

# Rendering documentation

Documentation that describes the SAW implementation and design is
stored in version control together with the source code (not in
Confluence).  This is required for a number of reasons, including to
ensure that the documentation accurately reflects the implementation
taking into account different versions and branches of the product.

The documentation source files are located in the
[dist/src/main/asciidoc] directory and are automatically rendered
as part of the project build process.  To render the documentation
without running other parts of the project build, execute the
following command:

        $ mvn -pl dist asciidoctor:process-asciidoc -Dasciidoctor.backend=html

The output can be inspected in the `dist/target/generated-docs`
directory.

[dist/src/main/asciidoc]: dist/src/main/asciidoc

# Continuous integration

After a pull request has been merged, the changes will be picked up
for building and testing in the project's continuous integration
[Bamboo build plan].  If the build plan succeeds, it will also trigger
automatic deployment of the changes to the shared development
environment using the related [Bamboo deployment project].

[Bamboo build plan]: https://bamboo.synchronoss.net:8443/browse/BDA-BDASAW
[Bamboo deployment project]: https://bamboo.synchronoss.net:8443/browse/BDA-BDASAW/deployments

# Making a release

To make a release of the project, execute the following steps:

1. Find the Git revision to be used for the release.  This will
   typically be `origin/master` after all required pull requests have
   been merged and your local repository has been updated to include
   the latest merges.

2. Tag the revision using Git and push the tag to the central
   repository:

        $ git tag -a -m "Version 1.0.0" v1.0.0 <commit>
        $ git push --tags

3. Note: The placeholder `<commit>` refers to the revision that has
   been selected for the release.

4. Start a customized build of the [project] on the continuous
   integration server using the newly created release tag (click the
   "Run" dropdown menu and select "Run customized..." and enter the
   release tag, for example `v1.0.0`, in the Revision field).

5. After starting the build, go to the Build result summary page and
   add the label "release" (which ensures it will be available for
   download indefinitely, instead of eventually being expired and
   deleted by the continuous integration server).

6. When the build finishes successfully, announce it by publishing a
   link to the artifacts page in the project Slack channel.  Also add
   the link to the corresponding [release notes] in Confluence.

If additional fixes are needed after tagging, just repeat the same
process above but increase the patch component of the version (for
example 1.0.1, 1.0.2 and so on).

[project]: https://bamboo.synchronoss.net:8443/browse/BDA-BDASAW
[release notes]: https://confluence.synchronoss.net:8443/display/BDA/SAW+Releases+Documentation


# Troubleshooting

1. To completely reset a docker environment, first delete all containers.
   Containers can be viewed by running `docker ps`. Further, to delete a
   container, run `docker container rm {container-id}`.
   Next, delete all the images. To get a list of images, run `docker images`.
   To delete an image, run `docker rmi {image-id}`.

2. If you get an error that a container already exists, copy the
   container id from the error message. Next, remove the container by
   running `docker container rm {container-id}`. You may need to do this
   more than once. Keep deleting the container and restarting the mvn command
   to start docker until you can get a clean start.
