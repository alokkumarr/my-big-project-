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
RUN sed -i -e '/^tsflags=nodocs$/d' /etc/yum.conf

# Install system utilities
RUN yum -y -q --nogpgcheck install epel-release
RUN yum -y -q --nogpgcheck install man less file iproute lsof nc bind-utils \
    smem sudo

# Install Ansible from EPEL repository
RUN yum -y -q --nogpgcheck install ansible rsync

# Workaround: See https://github.com/ansible/ansible/issues/12208
RUN yum -y -q --nogpgcheck install openssh-clients

# Add MapR Yum repository
ADD maprtech.repo /etc/yum.repos.d
ADD maprecosystem.repo /etc/yum.repos.d

#
# Speed up SAW deployment by preinstalling certain packages in
# container image
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
ADD mapr-configure /root/
# Workaround: As Docker DNS lookups do not work inside CentOS 7
# containers for now, Docker links are used as a workaround.  The
# hostname and port for Docker links is only available at runtime, so
# therefore the MapR client has to be configured in "saw-deploy" at
# runtime instead of in the Dockerfile at container image build time.
# Move MapR client configuration back to the Dockerfile when Docker
# DNS lookups start working in CentOS 7.  RUN /root/mapr-configure

# Tunneling for MapR client: enable if connecting to a MapR cluster in
# a shared environment.  Works for MapR clients and Elasticsearch.
# Spark drivers however require incoming connections so executing
# Spark jobs will not work over the tunnel.
#RUN yum -y -q --nogpgcheck install sshuttle sshpass
#ADD saw-tunnel /root/
#COPY saw-tunnel.service /etc/systemd/system/
#RUN systemctl enable saw-tunnel

# Add oneshot service to start application deployment at startup
ADD saw-deploy/saw-deploy /root/
ADD saw-deploy/saw-config /root/
COPY saw-deploy/saw-deploy.service /etc/systemd/system/
RUN systemctl enable saw-deploy

# Add SAW test metrics
ADD saw-metrics /root/saw-metrics/

# Add assembly from Docker Maven plug-in
COPY maven /maven/

# Start systemd
CMD /usr/lib/systemd/systemd

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD systemctl is-system-running --quiet
