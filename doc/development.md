# Introduction

This document describes how to develop the Synchronoss Analytics
Workbench (SAW) project.

# Requirements

To prepare for building the project, execute the following steps:

1. Install Java 1.8

2. Install Maven 3.5.0

3. Install [Docker] (for integration tests)

4. Configure Docker to allocate at least 8 GB memory and the maximum
   number of CPUs for containers

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
