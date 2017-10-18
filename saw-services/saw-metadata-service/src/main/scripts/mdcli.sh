#!/usr/bin/env bash
# Script to start local RTIS service on given port
source /etc/saw/service.env || exit 1

declare -r app_mainclass="sncr.metadata.MetadataCLI"
declare -r lib_dir=${SAW_SERVICE_HOME}/lib
declare -r conf_dir=${SAW_SERVICE_HOME}/conf
declare -r log_dir=/var/saw/service/log
declare -r bin_dir=${SAW_SERVICE_HOME}/sbin

echo "Application home : $SAW_SERVICE_HOME"
echo "Configuration dir: $conf_dir"
echo "Application bin: $bin_dir"
echo "Library dir: $lib_dir"
echo "Log dir: /var/saw/service/log"

export HADOOP_HOME=/opt/mapr/hadoop/hadoop-2.7.0

echo Hadoop home: $HADOOP_HOME
declare app_classpath="$conf_dir"
for j in `ls $lib_dir`; do
 app_classpath=${app_classpath}:"${lib_dir}/${j}"
done
app_classpath=${app_classpath}:$(mapr classpath)

cmd="java -Dlog.dir=${log_dir} -Dhadoop.home.dir=${HADOOP_HOME} -classpath $app_classpath $app_mainclass $@"

$cmd
