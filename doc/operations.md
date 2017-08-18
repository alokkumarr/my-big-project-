# Introduction

This document describes how to install and configure SAW Services in
an environment.

# Prerequisites

Before starting an installation of SAW Services ensure the following
is provided:

1. The environment has a MapR 5.2 cluster installed and configured

2. The MapR cluster has Spark 2.1 installed and configured

3. The SAW Services target host has the MapR client installed and a
   `mapr` user (using the same UID on all hosts in the cluster)

4. The target host has the Spark client installed and that there is a
   `/opt/mapr/spark/spark-current` symlink pointing to the current
   Spark version

5. SAW Services environment configuration

# Installing

To install SAW Security, use the SAW bundle package which coordinates
installation of SAW modules.  See the SAW bundle package Operations
Guide for closer details about installing.

# After installation

Before executing step 6 i.e. start the service You need to change or
make sure that one vm argument has right value in the script i.e
esproxy URL
`-Durl=http://saw03.bda.poc.velocity-va.sncrcorp.net:9200/`
  
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
