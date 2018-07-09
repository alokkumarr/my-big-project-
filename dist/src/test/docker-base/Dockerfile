#
# Testing container base image for SIP
#
# Provides packages used by all testing containers.
#

# Use CentOS systemd image
FROM centos/systemd

# Enable installing documentation for packages
RUN sed -i -e '/^tsflags=nodocs$/d' /etc/yum.conf

# Make EPEL package repository available
RUN yum -y -q --nogpgcheck install epel-release
# Workaround: The repository mirrors seem to have an issue which
# causes "repomd.xml does not match metalink for epel" errors, so
# enable the repository baseurl for now.
RUN sed -i -e 's/^#baseurl=/baseurl=/' /etc/yum.repos.d/epel.repo

# Install system utilities
RUN yum -y -q --nogpgcheck install man less file iproute lsof nc bind-utils \
    smem sudo mlocate

# Forward container system logs to Docker logs
COPY saw-logger/saw-logger.service /etc/systemd/system/
RUN systemctl enable saw-logger

# Install SSH server in each container so that the deployment
# automation can log in and perform installation steps
RUN yum -y -q --nogpgcheck install openssh-server
RUN systemctl enable sshd
# In testing containers, remove passwords so that deployment
# automation can log in without authentication
RUN passwd -d root
RUN echo "PermitEmptyPasswords yes" >> /etc/ssh/sshd_config
RUN echo "StrictHostKeyChecking no" >> /etc/ssh/ssh_config

# Add MapR Yum repository
COPY maprtech.repo /etc/yum.repos.d
COPY maprecosystem.repo /etc/yum.repos.d

#
# Speed up SAW deployment by preinstalling certain packages in
# container image.  Note: The packages are only installed, not enabled
# here.
#
# SAW
RUN yum -y -q --nogpgcheck install nginx
# SAW Web
RUN yum -y -q --nogpgcheck install tomcat
# SAW Services
RUN yum -y -q --nogpgcheck install mapr-client mapr-spark perl
# SAW Security
RUN yum -y -q --nogpgcheck install mariadb-server
RUN yum -y -q --nogpgcheck install java-1.8.0-openjdk-headless MySQL-python

# Configure MapR client
COPY mapr-configure /root/
# Workaround: Docker is supposed to set the execute bit for Windows
# users automatically, but apparently in some cases does not
# (https://github.com/moby/moby/issues/30511).  So set the execute bit
# explicitly here until fixed in Docker.
RUN chmod a+x /root/mapr-configure
RUN /root/mapr-configure

# Workaround: The MapR logging configuration refers to a file that is
# not writable by the "mapr" user, so change the path to a writable
# location.
RUN sed -i -e 's,log4.log,/tmp/log4.log,' /opt/mapr/conf/log4j.properties

# Workaround: Despite the workaround above, there seems to be some
# additional logging configuration embedded in MapR libraries that
# tries to write to a file "log4.log" in the current working
# directory.  SAW services are run as the "mapr" user and do not have
# write permissions to "log4.log" in the root directory, and generate
# an error and long stacktrace in the logs.  This is repeated for
# every Transport Service and Executor process.  So create a writable
# file as a workaround to silence these errors.
RUN log=log4.log && touch $log && chmod a+w $log

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD systemctl is-system-running --quiet
