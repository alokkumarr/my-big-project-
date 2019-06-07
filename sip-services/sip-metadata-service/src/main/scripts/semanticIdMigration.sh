#!/bin/sh
###########################################################################
# semanticIdMigration.sh
# Author: Alok KumarR
# Creation Date: JUNE 4 2019
###########################################################################


NOW=$(date +"%m-%d-%Y %r")

BASE_PATH=${1}  # eg: maprfs:///var/sip3
SEMANTIC_METADATA=${2} # semanticDataStore
TABLE_NAME=${3} #analysisMetadata
MAPR_LIB_PATH=${4}  # Sample MAPR Library - /opt/mapr/conf:/opt/mapr/lib/maprfs-6.0.1-mapr-tests.jar:/opt/mapr/lib/maprfs-6.0.1-mapr.jar:/opt/mapr/lib/maprfs-diagnostic-tools-6.0.1-mapr.jar
# :/opt/mapr/lib/maprdb-6.0.1-mapr-tests.jar:/opt/mapr/lib/maprdb-6.0.1-mapr.jar:/opt/mapr/lib/maprdb-cdc-6.0.1-mapr-tests.jar:/opt/mapr/lib/maprdb-cdc-6.0.1-mapr.jar
# :/opt/mapr/lib/maprdb-mapreduce-6.0.1-mapr-tests.jar:/opt/mapr/lib/maprdb-mapreduce-6.0.1-mapr.jar:/opt/mapr/lib/maprdb-shell-6.0.1-mapr.jar:/opt/mapr/lib/mapr-hbase-6.0.1-mapr-tests.jar
# :/opt/mapr/lib/mapr-hbase-6.0.1-mapr.jar:/opt/mapr/lib/mapr-ojai-driver-6.0.1-mapr-tests.jar:/opt/mapr/lib/mapr-ojai-driver-6.0.1-mapr.jar:/opt/mapr/lib/mapr-streams-6.0.1-mapr-tests.jar
# :/opt/mapr/lib/mapr-streams-6.0.1-mapr.jar:/opt/mapr/lib/mapr-tools-6.0.1-mapr-tests.jar:/opt/mapr/lib/mapr-tools-6.0.1-mapr.jar:/opt/mapr/lib/slf4j-api-1.7.12.jar
# :/opt/mapr/lib/slf4j-log4j12-1.7.12.jar:/opt/mapr/lib/log4j-1.2.17.jar:/opt/mapr/lib/central-logging-6.0.1-mapr.jar:/opt/mapr/lib/antlr4-runtime-4.5.jar
# :/opt/mapr/lib/commons-logging-1.1.3-api.jar:/opt/mapr/lib/commons-logging-1.1.3.jar:/opt/mapr/lib/commons-lang-2.5.jar:/opt/mapr/lib/commons-configuration-1.6.jar
# :/opt/mapr/lib/commons-collections-3.2.2.jar:/opt/mapr/lib/hadoop-common-2.7.0.jar:/opt/mapr/lib/hadoop-auth-2.7.0-mapr-1803.jar:/opt/mapr/lib/guava-14.0.1.jar
# :/opt/mapr/lib/jackson-annotations-2.7.2.jar:/opt/mapr/lib/jackson-core-2.7.2.jar:/opt/mapr/lib/jackson-databind-2.7.2.jar:/opt/mapr/lib/jline-2.11.jar:/opt/mapr/lib/json-1.8.jar
# :/opt/mapr/lib/kafka-clients-1.0.1-mapr-1803.jar:/opt/mapr/lib/ojai-2.0.1-mapr-1804.jar:/opt/mapr/lib/ojai-mapreduce-2.0.1-mapr-1804.jar
# :/opt/mapr/lib/ojai-scala-2.0.1-mapr-1804.jar:/opt/mapr/lib/protobuf-java-2.5.0.jar:/opt/mapr/lib/trove4j-3.0.3.jar:/opt/mapr/lib/zookeeper-3.4.5-mapr-1710.jar
# :/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/common/lib/commons-cli-1.2.jar:/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/common/lib/commons-codec-1.4.jar
# :/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/common/lib/commons-io-2.4.jar:/opt/mapr/hadoop/hadoop-2.7.0/etc/hadoop:/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/common/lib/*
# :/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/common/*:/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/hdfs:/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/hdfs/lib/*
# :/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/hdfs/*:/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/yarn/lib/*:/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/yarn/*
# :/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/mapreduce/lib/*:/opt/mapr/hadoop/hadoop-2.7.0/share/hadoop/mapreduce/*:/opt/mapr/hadoop/hadoop-2.7.0/contrib/capacity-scheduler/*.jar
# :/opt/mapr/lib/kvstore*.jar:/opt/mapr/lib/libprotodefs*.jar:/opt/mapr/lib/baseutils*.jar:/opt/mapr/lib/maprutil*.jar:/opt/mapr/lib/json-1.8.jar:/opt/mapr/lib/flexjson-2.1.jar

METADATA_SERVICE_DIR="/opt/bda/sip-metadata-service"
JAR_FILE="sip-metadata.jar"
MIGRATION_CLASS="com.synchronoss.saw.analysis.service.migrationservice.SemanticIdMigrationUtility"

echo
echo
echo SEMANTIC ID MIGRATION SHELL SCRIPT EXECUTING AT $NOW
echo ---------------------------------------------------------------------
echo Table Base Path             :            ${BASE_PATH}
echo Semantic Metadata Table     :            ${SEMANTIC_METADATA}
echo Table Name                  :            ${TABLE_NAME}
echo MAPR LIB PATH               :            ${MAPR_LIB_PATH}
echo ---------------------------------------------------------------------
echo
CMD="java -cp ${METADATA_SERVICE_DIR}/${JAR_FILE}:${MAPR_LIB_PATH} -Dloader.main=${MIGRATION_CLASS} org.springframework.boot.loader.PropertiesLauncher $@"

echo "Running ${CMD}"

sudo su mapr -c "$CMD"
RETVAL=$?
echo ---------------------------------------------------------------------
echo JOB RETURN CODE:  $RETVAL
echo ---------------------------------------------------------------------
exit $RETVAL