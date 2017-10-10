# Introduction

This document describes how to develop the Synchronoss Analytics
Workbench (SAW) project.

# Requirements

To prepare for building the project, execute the following steps:

1. Install Java 1.8

2. Install Maven 3.5.0

3. Install Docker (for system tests)

4. Install RPM (for building RPM packages, until SAW Security and
   Transport Service have been migrated to the pure Java RPM builder
   that has no dependencies on external tools)

If you are on a Mac the above can be installed using [Homebrew] as
follows:

        $ brew cask install java
        $ brew install maven
        $ brew cask install docker
        $ brew install rpm

After installing the above on a Mac, open the Docker application to
install the command line tools and launch the Docker daemon:

        $ open -a Docker

[Homebrew]: http://brew.sh/

# Building and testing

To build the project execute the following command (the prepare step
currently requires Linux or equivalent environment):

        $ ext/prepare
        $ mvn package

The release package will be located at `target/saw.tgz`.

Note: The Docker daemon must be running while building to ensure the
system tests can be run.

# Testing deployment

To build and run a Docker container that runs the SAW system in
development mode, execute the following command:

        $ ext/prepare
        $ mvn -pl saw-dist -am package
        $ mvn -pl saw-dist docker:build docker:start

Note: The above assumes that `saw`, `saw-security`, `saw-services` and
`saw-web` are all checked out in the same directory.  The
`ext/prepare-dev` will exit with an error if they are not.

After that the SAW Web application can be accessed
at [http://localhost/](http://localhost/).  To enter a shell inside
the container, execute the following command:

        $ docker exec -it saw bash

While inside the container, the following commands can be used as
starting points to investigate installed SAW services and packages:

        $ systemctl status saw-*
        $ systemctl list-units saw-*
        $ systemctl list-timers saw-*
        $ journalctl -u saw-*
        $ rpm -qa saw-*

Note: The Docker daemon must be running to be able to build and run
containers.

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
        $ git push --tags origin

2. Start a customized build of the [project] on the continuous
   integration server.  Enter the tag created in the previous step in
   the Revision field.

3. Add the "release" label to the build result (to prevent it from
   being expired by the continuous integration server)

[project]: https://bamboo.synchronoss.net:8443/browse/BDA-BDASAW
