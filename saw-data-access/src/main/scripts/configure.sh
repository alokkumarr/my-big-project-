#!/usr/bin/env bash
if [[ $# -lt 1 ]]; then
  echo "Mandatory parameter: executor_home is required"
	exit 1 
fi
echo "SQL_EXECUTOR_HOME=$1" > /etc/saw/executor.env
chown mapr:mapr /etc/saw/executor.env
mkdir -p /var/saw/service/executor/log
chown -R mapr:mapr /var/saw/service/executor/log
exit 0
