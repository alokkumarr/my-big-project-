# Introduction

This document describes how to develop the Synchronoss Analytics
Workbench (SAW) project.

# Requirements

To prepare for building the project, execute the following steps:

1. Install Java 8

2. Install Maven 3.5.0 (for building, unit tests and packaging)

3. Install [Docker] (for integration tests and local deployment)

4. Configure Docker to allocate at least 8 GB memory and the maximum
   number of CPUs for containers

Note: There is currently an incompatibility with Java 9 (SAW Transport
Service gives a "scala.reflect.internal.MissingRequirementError"
errors when building), so Java 8 specifically must be used.

Note: Instructions for how to set up the above on a Mac can be found
in the [Mac setup instructions](development-mac.md).

[Docker]: https://www.docker.com/community-edition

# Building and testing

To build and test the project execute the following commands:

        $ cd saw
        $ mvn verify

This includes running both unit and integration tests.  The release
package will be located at `saw-dist/target/saw-*.tgz`.

Note: The Docker daemon must be running while building to ensure the
integration tests can run.

# Running full system locally

To build and run the full SAW system locally in development mode,
execute the following commands to start SAW in Docker containers:

        $ cd saw
        $ mvn package
        $ mvn -pl saw-dist docker:build docker:run -P docker-saw

Note: The Docker daemon must be running to be able to build and run
containers.  Also, the first run will take longer as Docker downloads
and builds images that will subsequently be available in the image
build cache.

After the above command has completed the SAW Web application can be
accessed at [http://localhost/](http://localhost/).

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

To stop the SAW containers, simply send an interrupt to the Maven
process used to start the containers.  In case containers have been
left behind and prevent running new SAW containers, existing
containers can be cleared out by executing the following command:

        $ cd saw
        $ mvn -pl saw-dist docker:stop

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

1. Tag the revision in version control and push the tag to the central
   repository:

        $ git tag -a -m "Version 1.0.0" v1.0.0 <commit>
        $ git push --tags

2. Start a customized build of the [project] on the continuous
   integration server.  Enter the tag created in the previous step in
   the Revision field.

3. Add the "release" label to the build result (to prevent it from
   being expired by the continuous integration server)

[project]: https://bamboo.synchronoss.net:8443/browse/BDA-BDASAW
