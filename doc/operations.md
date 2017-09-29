# Introduction

This document describes how to install and configure SAW and its
modules in an environment.

# Prerequisites

Before starting an installation of SAW ensure the following has been
provided in the environment:

1. Install and configure a MapR version 5.2 cluster in the environment

2. Install and configure Apache Spark version 2.1 from the MapR
   Ecosystem Pack on the MapR cluster

3. Configure a MapR user in the MapR cluster (with user ID 500)

4. Provision a host to deploy from (the deploy host), running CentOS 7
   as the operating system.  This host will be used to run the deploy
   command and store the environment configuration.  The deploy host
   is typically common to the entire environment.

5. Provision a host for running SAW (the target host), with 32 GB of
   memory and CentOS 7 as the operating system.  The host should be
   dedicated to SAW and not run other applications.

6. Install and configure MapR client on the SAW target host

Please note that the target hosts must be allocated exclusively for
SAW use.  The underlying infrastructure up to the operating system
level are expected to be managed externally, while SAW fully manages
packages and configuration on the target hosts.

Currently SAW only supports deploying to a single target host.

# Configuring

Each SAW environment requires its own configuration.  A sample
configuration has been provided in the `config` file, which defaults
to installing on `localhost`.  At a minimum, copy this sample file and
change the `localhost` entries to the SAW target host.

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

Note: Configure passwordless SSH access to the SAW target host for a
smoother installation experience.  The deploy command should be run as
a normal user.  The deploy command will use sudo to request privileges
for relevant operations.

# Upgrading

To upgrade an existing installation, follow the same steps as for
installing an entirely new environment.  The deploy command will
detect an already existing installation and upgrade it.

# Listing services and checking their status

To list services and check the status of all SAW systemd units and
timers, execute the following commands:

        $ sudo systemctl list-units --all saw-\*
        $ sudo systemctl list-timers --all saw-\*

Please note that it is normal for the `saw-scheduler` service to be in
an inactive state most of the time.  It is activated on a set schedule
by `saw-scheduler.timer`.

# Starting, stopping and restarting services

Under normal circumstances there should be no need to start, stop or
restart SAW services manually.  However, if needed it can be done
using the following commands:

        $ sudo systemctl stop <saw-service>
        $ sudo systemctl start <saw-service>
        $ sudo systemctl restart <saw-service>

Where `<saw-service>` is one of the SAW systemd services (for example
`saw-gateway`), which can be listed using the `sudo systemctl
list-units saw-\*` command shown in the previous section.

# Logs

The SAW systemd services system logs can be accessed using the `sudo
journalctl` command.  To view the logs of individual services, use the
`-u` option:

        $ sudo journalctl -u saw-\*

# Entrypoints

The SAW Web module and supporting services are exposed on port 80 of
the SAW target host, i.e. `http://<saw-target-host>/`.  The SAW Web
application will automatically discover the endpoints for SAW Security
and SAW Services based on the URL it is being served from.

Nothing else in the SAW deployment, except for port 80 on the SAW
target host, is accessed by external parties.

# Loading semantic metadata

To enable creating analyses in SAW, load semantic metadata as follows:

        $ ssh <saw-services-host>
        $ sudo -u mapr /opt/saw/service/bin/mdcli.sh -i \
            file://<nodes-json> -o file:///tmp/log.json

The semantic metadata JSON is stored in the `<nodes-json>` file.
