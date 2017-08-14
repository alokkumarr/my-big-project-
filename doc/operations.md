# Introduction

This document describes how to install and configure SAW Security in
an environment.

# Prerequisites

Before starting an installation of SAW Security ensure the following
is provided:

1. A host for running SAW Security, with 32 GB of memory and CentOS 7
   as the operating system

# Installing

Execute the following steps to install SAW Security in the
environment:

1. Get the SAW Security release package (named
   `bda-saw-security-*.tgz`) for the desired version

3. Configure the hosts file to point to the target host

3. Extract the release package and execute the deploy script

        tar -xzf bda-saw-security-*.tgz
        cd bda-saw-security-*
        ./deploy <hosts>

Note: Configure passwordless SSH access to the target host for a
smoother installation experience.

# Upgrading

To upgrade an existing installation, follow the same steps as for
installing an entirely new environment.  The deploy command will
detect an already existing installation and upgrade it.
