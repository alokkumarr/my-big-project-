#!/bin/bash

echo " "
echo " ############ "
echo " "
read -p "Enter the Component name( parser , transformer , sql , esloader , pipeline ): " component
echo " "
echo " ############ "
echo " "
echo " Parser sample Config: /home/mapr/sip-xda-sampleapp/conf/parser/testparser.jconf  "
echo " Transformer sample Config: /home/mapr/sip-xda-sampleapp/conf/transformer/testtransformer.jconf  "
echo " SQL sample Config: /home/mapr/sip-xda-sampleapp/conf/sql/testsql.jconf  "
echo " ESLoader sample Config: /home/mapr/sip-xda-sampleapp/conf/esloader/test-esloader.jconf  "
echo " Pipeline sample Config: /home/mapr/sip-xda-sampleapp/conf/pipeline/pipeline.jconf "
echo " "
echo " ############ "
echo " "
read -p " *** Enter the Config file path ***: " configpath
echo " "

case "$component" in
'parser')
echo " "
echo " Sample Input file: /home/mapr/sip-xda-sampleapp/data/sample.csv "
echo " "
read -p " *** Enter the input file ***: "  dirpath
su - mapr -c "hadoop fs -put $dirpath hdfs:///data/bda/xdf-sample-data/raw"
echo " "
echo "Running Parser component"
echo " "
su - mapr -c "/opt/sip/sip-xdf/bin/execute-component.sh -r hdfs:///var/sip -a sip-xda-sampleapp -b TEST -m parser -c   file://$configpath"
;;
'transformer')
echo "Running Transformer component"
echo " "
su - mapr -c "/opt/sip/sip-xdf/bin/execute-component.sh -r hdfs:///var/sip -a sip-xda-sampleapp -b TEST -m transformer -c   file://$configpath"
;;
'sql')
echo "Running SQL component"
echo " "
su - mapr -c "/opt/sip/sip-xdf/bin/execute-component.sh -r hdfs:///var/sip -a sip-xda-sampleapp -b TEST -m sql -c   file://$configpath"
;;
'esloader')
echo "Running ESLoader component"
echo " "
su - mapr -c "/opt/sip/sip-xdf/bin/execute-component.sh -r hdfs:///var/sip -a sip-xda-sampleapp -b TEST -m es-loader -c   file://$configpath"
;;
'pipeline')
echo "Running Pipeline component"
echo " "
echo " Sample Input file: /home/mapr/sip-xda-sampleapp/data/sample.csv "
echo " "
read -p " *** Enter the input file ***: "  pipelineinput
su - mapr -c "hadoop fs -put $pipelineinput hdfs:///data/bda/xdf-sample-data/raw"
echo " "
su - mapr -c "/opt/sip/sip-xdf/bin/execute-component.sh -r hdfs:///var/sip -a sip-xda-sampleapp -b TEST -m pipeline -c   file://$configpath"
;;
esac
