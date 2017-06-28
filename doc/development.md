# Introduction

This document describes how to develop the application.

# Building and running tests

To build and run tests do the following:

1. Install Java and Maven

2. Run the project unit tests using Maven:

        $ mvn test

3. Run the project integration tests using Maven:

        $ mvn verify

# Deploying

To deploy the system to the shared development environment run:

    ./deploy

Also see development [deployment](deploy/README.md) notes.

# Updating SAW Transport Service routes

After editing the `saw-transport-service/conf/routes` file, the Play
framework generated source code files need to be regenerated.  This is
done as follows:

        $ saw-transport-service/generate-routes

Note: This is a workaround until the SAW Transport Service has been
migrated to Java and Spring Framework.

# Updating sample semantic metadata in SAW Metadata Service

To load the sample semantic metadata JSON files into the SAW Metadata
Service:

        $ saw-mddb-init/update
        
The sample semantic metadata JSON files themselves are in
the [`saw-mddb-init`](../saw-mddb-init) directory.
