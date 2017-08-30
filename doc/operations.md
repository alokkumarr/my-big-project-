# Introduction

This document describes how to install and configure SAW Services in
an environment.

# Prerequisites

Before starting an installation of SAW Services ensure the following
is provided:

1. The environment has a MapR 5.2 cluster installed and configured

2. The MapR cluster has Spark 2.1 installed and configured

3. The SAW Services target host has the MapR Yum repositories enabled

4. SAW Services environment configuration

# Installing of SAW Services

To install SAW Security, use the SAW bundle package which coordinates
installation of SAW modules.  See the SAW bundle package Operations
Guide for closer details about installing.

# Status check

To check the status of all SAW Services units execute:

        `$ systemctl list-units --all saw-\*`

To check the status of all SAW Services timers execute:

        `$ systemctl list-timers --all saw-\*`

# Logs

The SAW Services logs are found using the `journalctl` command.  To
view the logs of individual services, use the `-u` option:

        `$ sudo journalctl -u saw-scheduler.timer`
        `$ sudo journalctl -u saw-scheduler.service`

Note: For now the SAW Metadata Service does not use syslog and instead
logs into `/var/saw/service/log`.

# Installing of SAW Gateway Service

To install SAW Gateway, use the SAW bundle package which coordinates
installation of SAW modules.  See the SAW bundle package Operations
Guide for closer details about installing.

# Prerequisites

1. The environment has a MapR 5.2 cluster installed and configured

2. The MapR cluster has Spark 2.1 installed and configured

3. The SAW Services target host has the MapR Yum repositories enabled

4. SAW Services environment configuration

5. SAW Services must be installed. SAW Transport Service must be up & running on the same host on port 9200

6. SAW Security Service must be installed on the localhost using on port number 9000 (which is default configured on    gateway `application.yml`) or accessible to the gateway hosting node using `internal IP` or `hostname:portNumber` (if it is true, then configure the same on `application.yml`)

Note: Once saw-gateway service rpm has been installed. You can find the `application.yml` under `/opt/bda/saw-gateway/conf`

# Logs

The SAW Services logs are found using the `journalctl` command.  To
view the logs of individual services, use the `-u` option:

        ``$ sudo journalctl -u saw-gateway.service`

Note: For now the SAW Metadata Service does not use syslog and instead
logs into `/var/bda/service/saw/gateway/logs`.
