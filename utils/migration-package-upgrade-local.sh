
#!/bin/sh
set -eux

## Note: In case of SAW build package structure changes in future,This script need to be adjusted acordingly.

##Clean the local docker instance before start upgrade.
#docker rm -f $(docker ps -a -q)

# Store the working directory to run the upgrade Test.
workingDir=$(pwd)

# Master directory used to create an earlier docker instance for upgrade Test. 
DIRECTORY=../../master

if [ -d "$DIRECTORY" ]; then
  cd $DIRECTORY/saw
  git pull origin master
  # In case upgrade needs to be run with earlier release package use the below command
  #git checkout -b latestTag_v2.5.1 v2.5.1
  else
  mkdir $DIRECTORY
  cd $DIRECTORY
  git clone ssh://git@stash.synchronoss.net:7999/bda/sip.git
  #git checkout -b latestTag_v2.5.1 v2.5.1
  cd saw 
fi

mvn clean package -DskipTests
### Start docker with master build.
mvn -Ddocker-start=local
#In case of docker cloud instance. 
#mvn -Ddocker-start=cloud

cd $workingDir

mvn clean package

# Upgrade Tests start here ....
docker cp $(find dist/target -name saw*.tgz) sip-admin:/tmp
docker exec sip-admin tar -xvzf /tmp/$(ls dist/target|grep .tgz)
docker exec sip-admin chmod a+x /saw/saw-deploy
docker cp dist/src/test/docker-admin/saw-deploy/saw-config sip-admin:/saw
docker exec sip-admin /saw/saw-deploy /saw/saw-config


