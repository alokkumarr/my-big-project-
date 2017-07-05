# Introduction

This document describes how to install and configure SAW Services in
testing and production environments.

# Prerequisites

Before starting an installation of SAW Services ensure the following
is provided by Synchronoss operations:

1. Ensure the environment has a MapR 5.2 cluster installed and
   configured

2. Ensure the MapR cluster has Spark 2.1 installed and configured

3. Ensure there are hosts dedicated to SAW Services: two hosts, each
   with 32 GB of memory and running CentOS 7 as the operating system

4. Ensure SAW Services hosts have the MapR client installed and a
   `mapr` user (using the same UID on all hosts in the cluster)

5. Ensure SAW Services hosts have the Spark client installed and that
   there is a `/opt/mapr/spark/spark-current` symlink pointing to the
   current Spark version

# Installing

Execute the following steps to install SAW Services in the
environment:

1. Locate the artifacts required for installing SAW Services: RPM
   packages for each service to be installed

1. Install the SAW Transport Service by copying the RPM package to the
   host that it will run on.  Then execute `sudo rpm -i
   saw-transport-service-*.rpm`.  Finally execute `sudo -u mapr
   /opt/saw/service/bin/sawsrvc_start.sh` to start the service.

2. Install the SAW Scheduler Service by copying the RPM package to the
   host that it will run on.  Then execute `sudo rpm -i
   saw-scheduler-service-*.rpm`.  No additional steps needed, as the
   scheduler will automatically be invoked by the system services when
   required.

3. Configure a URL in a front-end proxy to point to port 9200 of the
   host that SAW Services has been installed on.  This URL should then
   be used to configure the endpoints in the SAW web front-end.

# Upgrading

To upgrade an existing SAW Services installation, follow the same
steps as for installing an entirely new environment.

# Status check

To check the status of all SAW Services units execute:

        $ systemctl list-units --all saw-\*

To check the status of all SAW Services timers execute:

        $ systemctl list-timers --all saw-\*

# Logs

The SAW Services logs are found using the `journalctl` command.  To
view the logs of individual services, use the `-u` option:

        $ sudo journalctl -u saw-scheduler.timer
        $ sudo journalctl -u saw-scheduler.service

Note: For now the SAW Metadata Service does not use syslog and instead
logs into `/var/saw/service/log`.
