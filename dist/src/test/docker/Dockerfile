# Use project standard operating system
FROM centos:7

# For systemd container interface
ENV container docker

# Workaround: https://bugzilla.redhat.com/show_bug.cgi?id=1472439
RUN systemctl mask systemd-machine-id-commit.service
# Workaround: Remounting root filesystem fails, so disable for now
RUN systemctl mask systemd-remount-fs.service
# Workaround: Getty service occasionally fails, so disable for now
RUN systemctl mask getty@tty1.service

# Enable installing documentation for packages
RUN sed -i '/tsflags=nodocs/d' /etc/yum.conf

# Install system utilities
RUN yum -y -q --nogpgcheck install less man nc lsof

# Install Ansible from EPEL repository
RUN yum -y -q --nogpgcheck install epel-release
RUN yum -y -q --nogpgcheck install ansible rsync

# Workaround: See https://github.com/ansible/ansible/issues/12208
RUN yum -y -q --nogpgcheck install openssh-clients

# Add oneshot service to start application deployment at startup
COPY saw-deploy.service /etc/systemd/system/
RUN systemctl enable saw-deploy

# Add MapR Yum repositories
ADD maprtech.repo /etc/yum.repos.d
ADD maprecosystem.repo /etc/yum.repos.d

# Speed up deployment by preinstalling packages in container image
RUN yum -y -q --nogpgcheck install mapr-client mapr-spark
RUN yum -y -q --nogpgcheck install java-1.8.0-openjdk-headless perl

# Add deploy script and local SAW environment configuration
ADD deploy /root/
ADD config /root/

# Add assembly from Docker Maven plug-in
COPY maven /maven/

# Start systemd
CMD /usr/lib/systemd/systemd

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD systemctl is-system-running --quiet
