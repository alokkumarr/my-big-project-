#!/bin/bash

DEFAULT=9999
#read -e -p "Enter temporary port for running shell (Default: 9999): " port
port="${port:-${DEFAULT}}"
echo $port

source /etc/bda/security.env

admin_jar_fnm=( /opt/bda/saw-security/lib/saw-security-command-line-tool-*.jar )
security_jar_fnm=( /opt/bda/saw-security/lib/saw-security-*-classes.jar )

if [ "$SECURE" = true ] ;
then
java -Dspring.config.location=/opt/bda/saw-security/conf/application-secure.properties -Dlogging.config=/opt/bda/saw-security/conf/logback.xml -Dquartz.properties.location=/opt/bda/saw-security/conf -jar $admin_jar_fnm -cp $security_jar_fnm --server.port=$port
else
java -Dspring.config.location=/opt/bda/saw-security/conf/application.properties -Dlogging.config=/opt/bda/saw-security/conf/logback.xml -Dquartz.properties.location=/opt/bda/saw-security/conf -jar $admin_jar_fnm -cp $security_jar_fnm --server.port=$port
fi
