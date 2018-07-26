#
# Functions shared across sample SAW metric load scripts
#

sudo_mapr="sudo -u mapr"
hadoop_put="hadoop fs -put -f"
mdcli="ssh sip-saw1 $sudo_mapr /opt/saw/service/bin/mdcli.sh"
datalake_home=/var/sip/services/saw-analyze-samples

wait_maprfs() {
    # The MapR container might still be starting up, so wait until MapR-FS
    # has been verified being available
    echo "Waiting for MapR-FS to become available"
    while ! hadoop fs -ls / > /dev/null 2>&1; do
        sleep 5
        echo "Retrying accessing MapR-FS"
    done
}
