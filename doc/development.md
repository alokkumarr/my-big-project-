# Introduction

This document describes how to develop the Synchronoss Analytics
Workbench (SAW) project.

# Requirements

To prepare for building the project, execute the following steps:

1. Install Java 1.8

2. Install Maven 3.5.0

3. Install Docker (for system tests)

If you are on a Mac the above can be installed using [Homebrew] as
follows:

        $ brew cask install java
        $ brew install maven
        $ brew cask install docker

After installing the above on a Mac, open the Docker application to
install the command line tools and launch the Docker daemon:

        $ open -a Docker

[Homebrew]: http://brew.sh/

# Building and testing

To build the project execute the following command:

        $ mvn package
    
The release package will be located at `target/saw.tgz`.

Note: The Docker daemon must be running while building to ensure the
system tests can be run.

# Testing deployment

To build and run a Docker container that runs the SAW system in
development mode, execute the following command:

        $ mvn package docker:build docker:start -DskipTests

To enter a shell inside the container, execute the following command:

        $ docker exec -it $(docker ps -ql) bash

While inside the container, the following commands can be used as
starting points to investigate installed SAW services and packages:

        $ systemctl status saw-*
        $ systemctl list-units saw-*
        $ systemctl list-timers saw-*
        $ journalctl -u saw-*
        $ rpm -qa saw-*

Note: The Docker daemon must be running to be able to build and run
containers.

# Making a release

To make a release of the project, execute the following steps:

1. Tag the revision in version control and push the tag to the central
   repository:

        $ git tag -a -m "Version 1.0.0" v1.0.0 <commit>
        $ git push --tags origin

2. Start a customized build of the [project] on the continuous
   integration server.  Enter the tag created in the previous step in
   the Revision field.

[project]: https://bamboo.synchronoss.net:8443/browse/BDA-BDASAW
