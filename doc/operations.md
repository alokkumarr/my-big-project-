# Introduction

This document describes how to install and configure SAW and its
modules in an environment.

# Prerequisites

Before starting an installation of SAW ensure the following
is provided:

1. At least one host for running SAW (the target host), with 32 GB of
   memory and CentOS 7 as the operating system

2. A host for deploying from (the deploy host), running CentOS 7 as
   the operating system.  This host will be used to run the deploy
   command and store the environment configuration.  The deploy host
   is typically common to the entire environment.

3. SAW environment configuration (see next section)

Please note that the target hosts must be allocated exclusively for
SAW use.  The underlying infrastructure up to the operating system
level are expected to be managed externally, while SAW fully manages
packages and configuration on the target hosts.

Currently SAW only supports deploying to a single target host.

# Configuring

Each SAW environment requires its own configuration.  A sample
configuration has been provided in the `config` file, which defaults
to installing on `localhost`.  At a minimum, copy this sample file and
change the `localhost` entries to the target host.

The sample configuration defaults to installing all available modules:
SAW Web, SAW Services and SAW Security.  To install only a subset of
these modules, edit the environment configuration to only mention the
desired modules.

Note that the environment configuration file must be preserved between
installations and upgrades.  It is recommended to put it under version
control that is stored on the deploy host and backed up to another
location.

# Installing

Execute the following steps to install SAW and its modules:

1. Get the SAW modules bundle package (named `saw.tgz`)

2. Prepare a SAW environment configuration file, as described in the
   previous section

3. Extract the package and execute the deploy command, giving it the
   path to the environment configuration file as an argument

        tar -xzf saw.tgz
        cd saw
        ./deploy <config>

Note: Configure passwordless SSH access to the target host for a
smoother installation experience.  The deploy command should be run as
a normal user.  The deploy command will use sudo to request privileges
for relevant operations.

# Upgrading

To upgrade an existing installation, follow the same steps as for
installing an entirely new environment.  The deploy command will
detect an already existing installation and upgrade it.

# Interfaces

The SAW system is exposed on port 80 of the target host.  It provides
both SAW Web, SAW Security endpoints.
