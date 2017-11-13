#!/bin/bash
# Call jar file for ddl3xdf

# Check and set verbose mode
[[ $VERBOSE ]] && set -x

# Check and set eval mode
eval_it=eval
[[ $DRYRUN ]] && eval_it=echo

# Script directory
CMD_DIR=$( dirname $0 )
# Lib directory
LIB_DIR=$( cd $CMD_DIR/../lib && pwd ) || exit

# Check lib dir exists
( cd $LIB_DIR ) || exit

# Get mapr classpath
mapr_classpath=$( mapr classpath )
: ${mapr_classpath:?no value}

# Collect all jars in lib
lib_jars=$( echo $LIB_DIR/*.jar )

# Build classpath
xdf_classpath=${lib_jars// /:}

# Final classpath
java_cpl=$xdf_classpath:$mapr_classpath

# Java class to execute
klass=synchronoss.util.DDLtoXDFConverter

# Call JVM
$eval_it java -cp $java_cpl $klass "$@"

# Save exit status in var
rc=$?

# Print DRYRUN info
[[ $DRYRUN ]] && echo DRYRUN exit

exit $rc
