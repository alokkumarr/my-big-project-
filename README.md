# Introduction

This is the SAW Services source code repository.  SAW Services are
back-end microservices that provide REST API for the SAW front-end
user interface.

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

# Operations

See the [operations guide](doc/operations.md) for details about
operations and monitoring.
