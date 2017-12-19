#!/bin/bash

DEFAULT=9999
read -e -p "Enter temporary port for running shell (Default: 9999): " port
port="${port:-${DEFAULT}}"
echo $port

# assume we are inside docker and start executing from following file
war_fnm=( /opt/bda/saw-security/lib/*saw-security*.war )

java -Dspring.config.location=/opt/bda/saw-security/conf/application.properties -Dlogging.config=/opt/bda/saw-security/conf/logback.xml -Dquartz.properties.location=/opt/bda/saw-security/conf -jar $war_fnm SawSecurityShell --server.port=$port