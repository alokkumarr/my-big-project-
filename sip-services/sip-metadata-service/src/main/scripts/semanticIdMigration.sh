#!/bin/sh
###########################################################################
# semanticIdMigration.sh
# Author: Alok KumarR
# Creation Date: JUNE 4 2019
###########################################################################


NOW=$(date +"%m-%d-%Y %r")

ANALYSIS_FILE_PATH=${1}
METADATA_SERVICE_DIR="/opt/bda/sip-metadata-service"
JAR_FILE="sip-metadata.jar"
MIGRATION_CLASS="com.synchronoss.saw.analysis.service.migrationservice.SemanticIdMigrationUtility"

echo
echo
echo SEMANTIC ID MIGRATION SHELL SCRIPT EXECUTING AT $NOW
echo ---------------------------------------------------------------------
echo Analysis File Path                    : $ANALYSIS_FILE_PATH
echo ---------------------------------------------------------------------
echo

CMD="java -cp ${METADATA_SERVICE_DIR}/${JAR_FILE} -Dloader.main=${MIGRATION_CLASS} org.springframework.boot.loader.PropertiesLauncher ${ANALYSIS_FILE_PATH}"

$CMD
RETVAL=$?
echo ---------------------------------------------------------------------
echo JOB RETURN CODE:  $RETVAL
echo ---------------------------------------------------------------------
exit $RETVAL
