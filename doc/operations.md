# Introduction

This document describes how to install and configure SAW Security in
an environment.

# Prerequisites

Before starting an installation of SAW Security ensure the following
is provided:

1. A host for running SAW Security, with 32 GB of memory and CentOS 7
   as the operating system (the target host)

2. A host for deploying SAW Security from (the deploy host), which
   will be used to run the deploy command and store the environment
   configuration

3. SAW Security environment configuration

# Installing

Execute the following steps to install SAW Security:

1. Get the SAW Security release package (named
   `bda-saw-security-*.tgz`) for the desired version

2. Extract the release package and execute the deploy script

        tar -xzf bda-saw-security-*.tgz
        cd bda-saw-security-*
        ./deploy <config>

Note: Configure passwordless SSH access to the target host for a
smoother installation experience.

# Upgrading

To upgrade an existing installation, follow the same steps as for
installing an entirely new environment.  The deploy command will
detect an already existing installation and upgrade it.
