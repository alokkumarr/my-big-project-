#!/usr/bin/env bash
if [[ $# -lt 1 ]]; then
  echo "Mandatory parameters is required: executor_home"
	exit 1 
fi
echo "SQL_EXECUTOR_HOME=$1" > /etc/saw/executor.env
if [[ ! -z $2 ]]  && [[ $2 = "TR" ]]; then
    echo "LIB_PATH=$1/sparklib" >> /etc/saw/executor.env
else
    echo "LIB_PATH=$1/lib" >> /etc/saw/executor.env
fi

chown mapr:mapr /etc/saw/executor.env
mkdir -p /var/saw/service/executor/log
chown -R mapr:mapr /var/saw/service/executor/log
exit 0
