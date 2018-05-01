#  Place Holder for Cloud Docker.
##!/bin/sh
#set -eux
#
### Note: In case of SAW build package structure changes in future,This script need to be adjusted acordingly.
#
## Store the working directory to run the upgrade Test.
##workingDir=$(pwd)
#
## Master directory used to create an earlier docker instance for upgrade Test.
##DIRECTORY=../../master
#
##if [ -d "$DIRECTORY" ]; then
# # cd $DIRECTORY/saw
# # git pull origin master
#  # In case upgrade needs to be run with earlier release package use the below command
#  #git checkout -b latestTag_v2.5.1 v2.5.1
# # else
#  mkdir $DIRECTORY
#  cd $DIRECTORY
#  git clone ssh://git@stash.synchronoss.net:7999/bda/saw.git
#  #git checkout -b latestTag_v2.5.1 v2.5.1
#  cd saw
#fi
#
#mvn clean package -DskipTests
#### Start docker with master build.
#mvn -Ddocker-start=cloud
#
#cd $workingDir
#
#mvn clean package
#
#
## Upgrade Tests start here ....
#docker-machine scp dist/target/saw-2.tgz saw:/tmp
#docker-machine scp dist/src/test/docker/saw-deploy/saw-config saw:/saw
#docker-machine ssh saw
#tar -xvzf /tmp/$(ls dist/target|grep .tgz)
#chmod a+x /saw/saw-deploy
#sh /saw/saw-deploy /saw/saw-config
#
#
