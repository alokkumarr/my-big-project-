# Introduction

This document describes how to develop the Synchronoss Analytics
Workbench (SAW) project.

# Requirements

To prepare for building the project, execute the following steps:

1. Install Java

2. Install Maven

3. Install Docker (for system tests)

If you are on a Mac the above can be installed using [Homebrew] as
follows:

        $ brew cask install java
        $ brew install maven
        $ brew cask install docker

[Homebrew]: http://brew.sh/

# Building and testing

To build the project execute the following command:

        $ mvn package
    
The release package will be located at `target/saw.tgz`.

# Testing deployment

To build and run a Docker container that runs the SAW system in
development mode, execute the following command:

        $ mvn package docker:build docker:start -DskipTests

To enter a shell inside the container, execute the following command:

        $ docker exec -it $(docker ps -ql) bash

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
