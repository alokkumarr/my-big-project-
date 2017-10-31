# Use CentOS systemd image
FROM centos/systemd

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
RUN /root/mapr-configure

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

# Workaround: There seems to be some logging configuration embedded in
# MapR libraries that tries to write to a file "log4.log" in the
# current working directory.  SAW services are run as the "mapr" user
# and do not have write permissions to "log4.log" in the root
# directory, and generate an error and long stacktrace in the logs.
# This is repeated for every Transport Service and Executor process.
# So create a writable file as a workaround to silence these errors.
RUN log=log4.log && touch $log && chmod a+w $log

# Add assembly from Docker Maven plug-in
COPY maven /maven/

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD systemctl is-system-running --quiet
