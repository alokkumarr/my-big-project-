#
# SIP testing image for administration node.
#
# SIP installation is triggered from the deploy node using the deploy
# command provided with the SIP package.  The deployment automation
# then connects to the other hosts specified in the SIP environment
# configuration and performs installation.
#

# Start from SIP base image
FROM sip-base-maprclient:${project.version}

# Install log receiver
COPY sip-journal-remote.conf \
  /etc/systemd/system/systemd-journal-remote.service.d/
COPY sip-tmpfiles-journal-remote.conf /etc/tmpfiles.d/
RUN systemctl enable systemd-journal-remote.socket
# Disable the log sender on the admin node itself
RUN systemctl disable systemd-journal-upload.service

# Forward system logs to Docker logs (only needed on admin node as it
# aggregates the logs of other nodes)
COPY sip-logger/sip-logger.service /etc/systemd/system/
RUN systemctl enable sip-logger

# Install mail server for testing
RUN yum -y -q install postfix
RUN echo "inet_interfaces = all" >> /etc/postfix/main.cf
RUN echo "inet_protocols = ipv4" >> /etc/postfix/main.cf
RUN systemctl enable postfix

# Install Ansible from EPEL repository
RUN yum -y -q install ansible rsync

# Workaround: See https://github.com/ansible/ansible/issues/12208
RUN yum -y -q install openssh-clients

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
# workaround: Set recursive execute bit for Windows (cygwin rsync) users
RUN chmod -R 0755 /root/saw-analyze-samples
RUN systemctl enable saw-analyze-spark-samples
RUN systemctl enable saw-analyze-elasticsearch-samples
# Add SAW Workbench test data
COPY saw-workbench-samples /root/saw-workbench-samples/
COPY saw-workbench-samples/saw-workbench-samples.service /etc/systemd/system/
# workaround: Set recursive execute bit for Windows (cygwin rsync) users
RUN chmod -R 0755 /root/saw-workbench-samples
RUN systemctl enable saw-workbench-samples
# Workaround: Set execute bit for Windows users
RUN chmod a+x /root/saw-*/sample-*/load*

# Add SAW Batch sample test data
COPY saw-batch-samples /root/saw-batch-samples/
# Workaround: The Batch Ingestion Service uses a large number of
# sessions when transfering a large amount of files, so increase the
# MaxSession parameters as a workaround until Batch Ingestion Service
# has been fixed to work better with the defaults.
RUN echo "MaxSessions 50000" >> /etc/ssh/sshd_config

# Add systemd journal gateway for browsing logs
RUN yum -y -q install systemd-journal-gateway
RUN systemctl enable systemd-journal-gatewayd.socket
# Workaround: Installing the systemd journal gateway causes the
# services below to fail, so mask them.  Remove when fixed in newer
# systemd version.
RUN systemctl mask systemd-machine-id-commit.service
RUN systemctl mask systemd-remount-fs.service

# Add assembly from Docker Maven plug-in
COPY maven /maven/
