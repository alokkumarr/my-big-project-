# This script for loading Reports from JSON into MYSQL DB:
#
# Usage : ./loadReportUtil.sh <PATH_TO_JSON_FOLDER> <OPERATION_TYPE>
#
#!/bin/sh


if [ $# -eq 2 ]
then
	java -Xms32M -Xmx512M -Dspring.config.location=../../../resources/saw-utility.properties -Dlogging.config=../../../resources/logback.xml -Draw.log.file=../../../logs/saw-utility/saw-utility -Dquartz.properties.location=../../../resources/ -jar ../../../dist/saw-utility/saw-utility.jar $1 $2

else
		echo "-------------------------------------------------------------------------------------------------------"
		echo -e "\r"
		echo "The Usage of this script should follow pattern ./loadReportUtil.sh <PATH_TO_JSON_FOLDER> <OPERATION_TYPE>"
		echo "Follow Confluence page:http://vm-bes.razorsight.com:8090/display/EN/RAW+Utility+%3A+Usage+Details"
		echo "-------------------------------------------------------------------------------------------------------"

		exit 1
fi