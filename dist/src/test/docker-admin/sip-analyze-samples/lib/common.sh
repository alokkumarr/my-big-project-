#
# Functions shared across sample SAW metric load scripts
#
sudo_mapr="sudo -u mapr"
hadoop_put="hadoop fs -put -f"
mdcli="sshpass -p root ssh sip-app1 $sudo_mapr /opt/saw/service/bin/mdcli.sh"
datalake_home=/var/sip/services/saw-analyze-samples
semantic_service=http://sip-app1:9500
sample_location=/root/saw-analyze-samples

wait_maprfs() {
    # The MapR container might still be starting up, so wait until MapR-FS
    # has been verified being available
    echo "Waiting for MapR-FS to become available"
    while ! hadoop fs -ls / > /dev/null 2>&1; do
        sleep 5
        echo "Retrying accessing MapR-FS"
    done
}

wait_semantic_service(){
  # The docker instances & it's services might still be starting up, so wait until semantic service
  # and it's associated services has been verified being available
RETRIES=10
while :
do
STATUS=$(curl -s -o /dev/null -w '%{http_code}' "$semantic_service/actuator/health")
if [ $STATUS -eq 200 ];
then
 while :
 do
      set -o pipefail
      local semantic_status=$(curl -H "Content-Type:application/json" -XGET "$semantic_service/actuator/health" | jq -r .status)
      STATUS=$(curl -s -o /dev/null -w '%{http_code}' "$semantic_service""/actuator/health")
      echo "HTTP STATUS: $STATUS"
      if [ $semantic_status != "UP" ]
      then
        sleep 5
        echo "Retrying semantic service to be available"
      else
        echo "semantic service is up & running"
        break
      fi
 done
else
  sleep 5
  RETRIES=$((RETRIES-1))
  echo "GOT HTTP STATUS $STATUS while invoking service $semantic_service/actuator/health"
  echo "Keeping on retrying until $RETRIES reaches to 0"
fi
if [ $STATUS -eq 200 ] || [ $RETRIES -eq 0 ];
then
   echo "Either service is up with code HTTP STATUS $STATUS or retry has been exhausted if $RETRIES is equal to 0"
   break
fi
done
}

insert_json_store_sample(){
  # This function will add the data directly to the MapRDB store
  # It is using semantic service API instead of using directly MaprDB
 echo "Inserting the sample data starts here."
 local data_location=$1
 echo "data to be inserted $data_location"
 inserted_sample=$(curl -H "Content-type:application/json" -XPOST $semantic_service"/internal/semantic/workbench/create" -d "@$data_location" | jq '.')
 echo "output after inserting into store $inserted_sample"
 echo "Inserting the sample data ends here."
}
