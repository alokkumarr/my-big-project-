# Adding spark-current soft link

sudo ln -s /opt/mapr/spark/spark-2.4.4 /opt/mapr/spark/spark-current

# Moving XDA files to MapR-FS

su - mapr -c "hadoop fs -mkdir -p /data/bda/xdf-sample-data/raw /opt/bda/apps/"
su - mapr -c "hadoop fs -put /home/mapr/sip-xda-sampleapp/ hdfs:///opt/bda/apps"

# Register XDA Application

su - mapr -c "/opt/sip/sip-xdf/bin/xdf-mdcli.sh file:///home/mapr/sip-xda-sampleapp/meta/project.json"

