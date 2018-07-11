#
# SIP testing base image
#
# Provides packages and common configuration used by all testing
# containers.
#

# Use CentOS systemd image
FROM centos/systemd

# Enable installing documentation for packages
RUN sed -i -e '/^tsflags=nodocs$/d' /etc/yum.conf

# Make EPEL package repository available
RUN yum -y -q install epel-release
# Workaround: The repository mirrors seem to have an issue which
# causes "repomd.xml does not match metalink for epel" errors, so
# enable the repository baseurl for now.
RUN sed -i -e 's/^#baseurl=/baseurl=/' /etc/yum.repos.d/epel.repo

# Install system utilities
RUN yum -y -q install man less file iproute lsof nc bind-utils \
    smem sudo mlocate

# Forward container system logs to Docker logs
COPY saw-logger/saw-logger.service /etc/systemd/system/
RUN systemctl enable saw-logger

# Install SSH server in each container so that the deployment
# automation can log in and perform installation steps
RUN yum -y -q install openssh-server
RUN systemctl enable sshd
# In testing containers, remove passwords so that deployment
# automation can log in without authentication
RUN passwd -d root
RUN echo "PermitEmptyPasswords yes" >> /etc/ssh/sshd_config
RUN echo "StrictHostKeyChecking no" >> /etc/ssh/ssh_config

# Add MapR package repository
COPY maprtech.repo /etc/yum.repos.d
RUN rpm --import http://package.mapr.com/releases/pub/maprgpg.key

# Set up MapR user
RUN groupadd mapr -g 5000
RUN useradd mapr -u 500 -g mapr
# Set password for "mapr" user to allow logging into the MapR Control
# System web application
RUN echo mapr | passwd --stdin mapr

# Install Java
RUN yum -y -q install java-1.8.0-openjdk-headless

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD systemctl is-system-running --quiet
