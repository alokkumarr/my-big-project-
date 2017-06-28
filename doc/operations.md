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

# Installing

Execute the following steps to install SAW Services in the
environment:

1. Install the SAW Metadata Service by...

2. Install the SAW Scheduler Service by copying the
   `saw-scheduler-service.jar` file to
   `/opt/saw-scheduler-service/saw-scheduler-service.jar` on one of
   the SAW nodes.  Additionally create an executable file
   `saw-scheduler-service-daily` with the following content:

        sudo -u mapr java -jar /opt/saw-scheduler-service/saw-scheduler-service.jar 2>&1 | logger -t saw-scheduler-service

# Upgrading

To upgrade an existing SAW Services installation, follow the same
steps as for installing an entirely new environments.

# Logs

The application logs are found in `/var/log/messages`.  For now the
SAW Metadata Service does not use syslog and instead logs into
`/var/saw/service/log`.
