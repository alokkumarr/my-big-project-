# Introduction

This document describes how to install and configure SAW Services in
an environment.

# Prerequisites

Before starting an installation of SAW Services ensure the following
is provided:

1. The environment has a MapR 5.2 cluster installed and configured

2. The MapR cluster has Spark 2.1 installed and configured

3. A host for running SAW Services, with 32 GB of memory and running
   CentOS 7 as the operating system (the target host)

4. A host for deploying SAW Services from (the deploy host), which
   will be used to run the deploy command and store the environment
   configuration

4. The target host has the MapR client installed and a `mapr` user
   (using the same UID on all hosts in the cluster)

5. The target host has the Spark client installed and that there is a
   `/opt/mapr/spark/spark-current` symlink pointing to the current
   Spark version

6. SAW Services environment configuration

# Installing

Execute the following steps to install SAW Services:

1. Get the SAW Services release package (named
   `saw-services-*.tgz`) for the desired version

2. Extract the release package and execute the deploy script

        tar -xzf saw-services-*.tgz
        cd saw-services-*
        ./deploy <config>

3. Now before executing step 6 i.e. start the service
   You need to change or make sure that one vm argument has right value in the script i.e esproxy URL
   ` -Durl=http://saw03.bda.poc.velocity-va.sncrcorp.net:9200/ `
  
4. Configure a URL in a front-end proxy to point to port 9200 of the
   host that SAW Services has been installed on.  This URL should then
   be used to configure the endpoints in the SAW web front-end.

Note: Configure passwordless SSH access to the target host for a
smoother installation experience.

# Upgrading

To upgrade an existing installation, follow the same steps as for
installing an entirely new environment.  The deploy command will
detect an already existing installation and upgrade it.

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
