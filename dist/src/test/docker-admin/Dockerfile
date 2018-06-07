#
# Testing container image for SIP deploy node.
#
# SIP installation is triggered from the deploy node using the deploy
# command provided with the SIP package.  The deployment automation
# then connects to the other hosts specified in the SIP environment
# configuration and performs installation.
#

# Start from SIP base image
FROM sip-base:${project.version}

# Install Ansible from EPEL repository
RUN yum -y -q --nogpgcheck install ansible rsync

# Workaround: See https://github.com/ansible/ansible/issues/12208
RUN yum -y -q --nogpgcheck install openssh-clients

# Add oneshot service to start application deployment at startup
COPY saw-deploy/saw-deploy /root/
COPY saw-deploy/saw-config /root/
COPY saw-deploy/saw-deploy.service /etc/systemd/system/
RUN systemctl enable saw-deploy
# Workaround: Set execute bit for Windows users
RUN chmod a+x /root/saw-deploy

RUN chmod a+x /root
# Add SAW Analyze test data
COPY saw-analyze-samples /root/saw-analyze-samples/
COPY saw-analyze-samples/sample-spark/saw-analyze-spark-samples.service /etc/systemd/system/
COPY saw-analyze-samples/sample-elasticsearch/saw-analyze-elasticsearch-samples.service /etc/systemd/system/
RUN systemctl enable saw-analyze-spark-samples
RUN systemctl enable saw-analyze-elasticsearch-samples
# Add SAW Workbench test data
COPY saw-workbench-samples /root/saw-workbench-samples/
COPY saw-workbench-samples/saw-workbench-samples.service /etc/systemd/system/
RUN systemctl enable saw-workbench-samples
# Workaround: Set execute bit for Windows users
RUN chmod a+x /root/saw-*/sample-*/load*

RUN groupadd mapr -g 5000
RUN useradd mapr -u 500 -g mapr

# Add systemd journal gateway for browsing logs
RUN yum -y -q --nogpgcheck install systemd-journal-gateway
RUN systemctl enable systemd-journal-gatewayd.socket
# Workaround: Installing the systemd journal gateway causes the
# services below to fail, so mask them.  Remove when fixed in newer
# systemd version.
RUN systemctl mask systemd-machine-id-commit.service
RUN systemctl mask systemd-remount-fs.service

# Add assembly from Docker Maven plug-in
COPY maven /maven/
