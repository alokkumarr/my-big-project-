# Introduction

This document describes how to develop the application.

# Requirements

- Java
- Maven
- Docker (for system tests)

# Building and running tests

To build and run tests do the following:

1. Run the project unit tests using Maven:

        $ mvn test

2. Run the project system tests using Maven:

        $ mvn verify

# Updating SAW Transport Service routes

After editing the `saw-transport-service/conf/routes` file, the Play
framework generated source code files need to be regenerated.  This is
done as follows:

        $ saw-transport-service/generate-routes

Note: This is a workaround until the SAW Transport Service has been
migrated to Java and Spring Framework.

The generated files pattern can be found in the
`saw-transport-service/generate-routes` script near the `rm -rf`
command.

# Updating sample semantic metadata in SAW Metadata Service

To load the sample semantic metadata JSON files into the SAW Metadata
Service:

        $ saw-mddb-init/update
        
The sample semantic metadata JSON files themselves are in
the [`saw-mddb-init`](../saw-mddb-init) directory.
