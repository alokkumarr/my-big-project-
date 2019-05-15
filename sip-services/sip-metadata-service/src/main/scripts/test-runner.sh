#!/bin/bash

## Will be used to testing purpose. Not to be merged with master

ANALYSIS_FILE_PATH=${1}

METADATA_SERVICE_DIR="/opt/bda/sip-metadata-service"
JAR_FILE="sip-metadata.jar"

MIGRATION_CLASS="com.synchronoss.saw.analysis.service.MigrateAnalysis"

CMD="java -cp ${METADATA_SERVICE_DIR}/${JAR_FILE} -Dloader.main=${MIGRATION_CLASS} org.springframework.boot.loader.PropertiesLauncher ${ANALYSIS_FILE_PATH}"

echo "Running ${CMD}"

${CMD}
