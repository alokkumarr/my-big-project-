#
# SIP testing base image
#
# Provides packages and common configuration used by all testing
# containers.
#

# Use CentOS base image.  Note: The version should follow the CentOS
# version requirement stated in the SIP Operations Guide, which in
# turn depends on the MapR Operating System Support Matrix.
FROM centos:7.7.1908

# initilize the arugument to download and install OpenJDK Java latest.
# since the required package is not available in configured 7.6 repo's,
# we are downloading the java packages and its dependencies directly.
# rpmd1 and rpmd2 are dependencies for rpm (as configured below).
# openjdk-devel is being installed to satisfy the mapr dependency.

ARG path=http://mirror.centos.org/centos/7.7.1908/updates/x86_64/Packages/
ARG rpmd1=java-1.8.0-openjdk-headless-1.8.0.222.b10-1.el7_7.x86_64.rpm
ARG rpmd2=java-1.8.0-openjdk-1.8.0.222.b10-1.el7_7.x86_64.rpm
ARG rpm=java-1.8.0-openjdk-devel-1.8.0.222.b10-1.el7_7.x86_64.rpm

ARG secure_enviroment

# Workaround: Package installations (like mapr-core which depends on
# initscript) can force a CentOS upgrades, which go beyond the CentOS
# versions supported by MapR.  So use the CentOS Vault repositories to
# fix the version to the release chosen above.
RUN echo "7.7.1908" > /etc/yum/vars/releasever
RUN rm /etc/yum.repos.d/*.repo
COPY centos.repo /etc/yum.repos.d

# Workaround: Preferably the "centos/systemd" Docker base image would
# be used, but it does not have exact version tags which are needed to
# lock down the image to the CentOS version required by SIP.  So use
# the "centos" base image which does have exact version tags, and
# replicate the steps of "centos/systemd" here instead.
ENV container docker
RUN (cd /lib/systemd/system/sysinit.target.wants/; \
for i in *; do [ $i == systemd-tmpfiles-setup.service ] || rm -f $i; done); \
rm -f /lib/systemd/system/multi-user.target.wants/*;\
rm -f /etc/systemd/system/*.wants/*;\
rm -f /lib/systemd/system/local-fs.target.wants/*; \
rm -f /lib/systemd/system/sockets.target.wants/*udev*; \
rm -f /lib/systemd/system/sockets.target.wants/*initctl*; \
rm -f /lib/systemd/system/basic.target.wants/*;\
rm -f /lib/systemd/system/anaconda.target.wants/*;
VOLUME ["/sys/fs/cgroup"]
CMD ["/usr/sbin/init"]

# Enable memory accounting for analyzing memory consumption
RUN echo "DefaultMemoryAccounting=yes" >> /etc/systemd/system.conf

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
    smem sudo mlocate jq

# Install log sender
RUN yum -y -q install systemd-journal-gateway
COPY sip-journal-upload.conf \
  /etc/systemd/system/systemd-journal-upload.service.d/
COPY sip-tmpfiles-journal.conf /etc/tmpfiles.d/
RUN systemctl enable systemd-journal-upload.service
RUN echo "URL=http://sip-admin:19532" >> /etc/systemd/journal-upload.conf
RUN install -d -o systemd-journal-upload -g systemd-journal-upload \
  /var/lib/systemd/journal-upload

# Install SSH server in each container so that the deployment
# automation can log in and perform installation steps
RUN yum -y -q install openssh-server
RUN systemctl enable sshd
# In testing containers, set known password for use by deployment
# automation and integration testing
RUN echo root | passwd --stdin root
RUN yum -y -q install sshpass
RUN echo "StrictHostKeyChecking no" >> /etc/ssh/ssh_config

# Install SSH client in each container
RUN yum -y -q install openssh-clients

# Add MapR package repository
COPY maprtech.repo /etc/yum.repos.d
RUN rpm --import http://package.mapr.com/releases/pub/maprgpg.key

# Set up MapR user
RUN groupadd mapr -g 5000
RUN useradd mapr -u 500 -g mapr
# Set password for "mapr" user to allow logging into the MapR Control
# System web application
RUN echo mapr | passwd --stdin mapr

# Installing Java OpenJDK

RUN curl -O $path/$rpm \
    && curl -O $path/$rpmd1 \
    && curl -O $path/$rpmd2 \
    && export GNUPGHOME="$(mktemp -d)" \
    && yum install -y -q $rpmd1 \
    && yum install -y -q $rpmd2 \
    && yum install -y -q $rpm \
    && rm -r $rpm $rpmd1 $rpmd2 \
    && yum clean all

ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b10-1.el7_7.x86_64/

# COPY java.security into the directory
COPY java.security /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.222.b10-1.el7_7.x86_64/jre/lib/security/

# Add SIP container health check command
COPY sip-health-check /root/

ENV secure_env=$secure_enviroment

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD /root/sip-health-check

