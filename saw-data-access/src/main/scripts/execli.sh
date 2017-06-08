#!/usr/bin/env bash
source /etc/saw/executor.env || exit 1

declare -r app_mainclass="sncr.datalake.cli.ExecutionRunner"
declare -r lib_dir=$LIB_PATH
declare -r conf_dir=$SQL_EXECUTOR_HOME/conf
declare -r bin_dir=$SQL_EXECUTOR_HOME/bin
declare -r log_dir=/var/saw/service/executor/log

echo "Application home : $SQL_EXECUTOR_HOME"
echo "Configuration dir: $conf_dir"
echo "Application bin: $bin_dir"
echo "Library dir: $lib_dir"
echo "Log dir: $log_dir"

export HADOOP_HOME=/opt/mapr/hadoop/hadoop-$(cat /opt/mapr/hadoop/hadoopversion)

echo Hadoop home: $HADOOP_HOME
declare app_classpath="$conf_dir"


# Direct JVM section
for j in `ls $lib_dir`; do
 app_classpath=${app_classpath}:"${lib_dir}/${j}"
done
for j in `ls /opt/mapr/spark/spark-current/jars/*.jar`; do
 app_classpath=${app_classpath}:"${j}"
done
app_classpath=${app_classpath}:$(mapr classpath)

export SQL_EXECUTOR_HOME

cmd="java -Dconfig=${SQL_EXECUTOR_HOME}/conf -Dlog.dir=${log_dir} -Dhadoop.home.dir=${HADOOP_HOME} -classpath $app_classpath $app_mainclass $@"
echo $cmd
$cmd

