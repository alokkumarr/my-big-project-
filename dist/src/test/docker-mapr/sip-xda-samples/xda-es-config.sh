#!/bin/bash

echo " "
echo "Tacking Backup file"
cp /etc/elasticsearch/elasticsearch.yml /etc/elasticsearch/elasticsearch.yml_backup
echo " "
echo "Updating Http ES Port"
sudo sed -i 's/8200/9200/' /etc/elasticsearch/elasticsearch.yml
echo " "
echo "Updating TCP Port"
sudo sed -i 's/8300/9300/' /etc/elasticsearch/elasticsearch.yml
echo " "
echo "Restarting ES Service"
echo " "
sudo systemctl restart elasticsearch.service
