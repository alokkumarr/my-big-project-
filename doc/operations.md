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
   
2. Go to /opt/saw/service/conf : open application.conf in your favorite editor & change the below attribute value accordingly
   
   `metadata = {
  path = "/main/metadata"
  zookeeper-quorum = "mapr01.bda.poc.velocity-va.sncrcorp.net,mapr02.bda.poc.velocity-   va.sncrcorp.net,mapr03.bda.poc.velocity-va.sncrcorp.net,mapr04.bda.poc.velocity-va.sncrcorp.net,mapr05.bda.poc.velocity-va.sncrcorp.net,mapr06.bda.poc.velocity-va.sncrcorp.net,mapr07.bda.poc.velocity-va.sncrcorp.net,mapr08.bda.poc.velocity-va.sncrcorp.net,mapr09.bda.poc.velocity-va.sncrcorp.net,mapr10.bda.poc.velocity-va.sncrcorp.net"
  user = "mapr"
} `


3. Then change spark related attributes under 
   `spark = {....}`
   
   
4. Then change elastic search related attributes under  
  `es = {
  host = "10.48.22.179"
  timeout = 30
  port = 9200
  username = "elastic"
  password = "xuw3dUraHapret"
  protocol = "http"
}`

   `Note: It does not support https protocol`
 
5. Now before executing step 6 i.e. start the service
   You need to change or make sure that one vm argument has right value in the script i.e esproxy URL
   ` -Durl=http://saw03.bda.poc.velocity-va.sncrcorp.net:9200/ `
   
  
5. Install the SAW Transport Service by copying the RPM package to the
   host that it will run on.  Then execute `sudo rpm -i
   saw-transport-service-*.rpm`.  Finally execute `sudo -u mapr
   /opt/saw/service/bin/sawsrvc_start.sh` to start the service.

6. Install the SAW Scheduler Service by copying the RPM package to the
   host that it will run on.  Then execute `sudo rpm -i
   saw-scheduler-service-*.rpm`.  No additional steps needed, as the
   scheduler will automatically be invoked by the system services when
   required.

7. Configure a URL in a front-end proxy to point to port 9200 of the
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
