# Setup instructions for using Docker Machine in the cloud with jumpbox

Execute the following steps to deploy SIP using Docker Machine in the
cloud with jumpbox :

1. Get the sip-jumpbox server details from the [SIP Environments] confluence page

2. Verify and install `rsync` 
   
   Note:Windows users can install and use cygwin with rsync
   
3. Verify the connectivity with jumpbox using Synchronoss AD credential to login into jumpbox, 
   if failed to login then create servicedesk ticket to <servicedesk@synchronoss.com> to add 
   username in ug-bda-dev group to grant the jumpbox access.
   
4. Perform code sync, build and deploy SIP on the jumpbox as follows:
   
        host=<usename>@<jumpboxHostName>
        temp=temp-sip
        cd sip
        rsync -avz --exclude 'saw-web/node_modules' --exclude 'saw-web/target' . $host:$temp
        ssh $host mvn -f $temp package -DskipTests
        ssh $host mvn -f $temp -Ddocker-start=cloud
        
     Tips : 
     * You can setup password less ssh connection between 
     local machine and jumpbox to avoid multiple password prompt
     while running the above commands. 
     * Above command can be stored as cloud-deployment.sh file in parent
     directory of sip and can trigger as single script run command to avoid multiple command run.
     
        
 5. Get the URL of the cloud SIP environment using:
 
        ssh $host docker-machine ssh sip-<user0001> sip-url
        
 6. To manage the docker-machine instance run `ssh $host` and can use docker-machine 
    commands to manage the instances. 
    
         Example : 
         $ docker-machine ssh sip-<user0001>
         $ docker-machine scp -r setup.sh sip-<user0001>:/home/centos/
    
[SIP Environments]:https://confluence.synchronoss.net:8443/display/BDA/SIP+Environments

