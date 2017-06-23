# Introduction

This document describes how to develop the application.

# Building and running tests

To build and run tests do the following:

1. Install Java and Maven

2. Run the project unit tests using Maven:

        $ mvn test

3. Run the project integration tests using Maven:

        $ mvn verify

# Updating SAW Transport Service routes

To regenerate SAW Transport Service route files:

        $ saw-transport-service/generate-routes

Note: This is a workaround until the SAW Transport Service has been
migrated to Java and Spring Framework.
