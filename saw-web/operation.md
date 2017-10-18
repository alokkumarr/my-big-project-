# Introduction

This document describes how to install and configure SAW Web in
testing and production environments.

# Prerequisites

Before starting an installation of SAW Web ensure the following
is provided by Synchronoss operations:

1. Ensure the environment has a Java 8.x installed and
   configured

2. Ensure the MapR cluster has Spark 2.1 installed and configured

3. Ensure there are hosts dedicated to SAW Services: two hosts, each
   with 32 GB of memory and running CentOS 7 as the operating system

4. Ensure there are hosts dedicated to SAW Security service and it's API is accessible
   over http with 8 GB of memory and runnnig on CentOS7 as the operating system

5. Ensure SAW Services hosts have the MapR client installed and a
   `mapr` user (using the same UID on all hosts in the cluster)

6. Ensure SAW Services hosts have the Spark client installed and that
   there is a `/opt/mapr/spark/spark-current` symlink pointing to the
   current Spark version

7. Ensure the environment has a Apache Tomcat 7.x has been installed and
   it's URL is accessible from web browser.



# Installing

Execute the following steps to install SAW Services in the
environment:

1. Locate the artifacts required for installing SAW Web: saw.war

2. Go to `$TOMCAT_HOME/webapps`

3. Copy the saw.war under this directory

4. Before start the tomcat server using ./catalina.sh start or systemctl start start

5. Go to tomcat/conf update the property in server.xml
    `<Connector port="7070" protocol="HTTP/1.1"
               maxHttpHeaderSize="8192"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" redirectPort="8443" acceptCount="100"
               connectionTimeout="20000" disableUploadTimeout="true"
               compression="on"
         compressionMinSize="2048"
         noCompressionUserAgents="gozilla, traviata"
         compressableMimeType="application/json,application/xml,text/html,text/xml,
         text/plain,text/javascript,image/x-icon,application/javascript,application/x-javascript,
         application/xml,application/xhtml+xml,application/x-font-truetype,font/opentype,font/otf,text/css"/>`

 6. Start the tomcat server using script or service.

 # Upgrading

 1. Locate the artifacts: saw.war

 2. stop the tomcat server using script or service i.e systemctl stop tomcat

 3. copy the latest saw.war under `$TOMCAT_HOME/webapps`

 4. Start the tomcat server using script or service.
