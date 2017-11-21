# Use CentOS systemd image
FROM centos/systemd

# Enable installing documentation for packages
RUN sed -i -e '/^tsflags=nodocs$/d' /etc/yum.conf

# Install system utilities
RUN yum -y -q --nogpgcheck install epel-release
RUN yum -y -q --nogpgcheck install man less file iproute lsof nc bind-utils \
    smem sudo mlocate

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

# Install Elasticsearch
ADD elasticsearch.repo /etc/yum.repos.d
RUN rpm --import https://artifacts.elastic.co/GPG-KEY-elasticsearch
RUN yum -y -q install elasticsearch
RUN echo "http.port: 8200" >> /etc/elasticsearch/elasticsearch.yml
RUN echo "transport.tcp.port: 8300" >> /etc/elasticsearch/elasticsearch.yml
RUN sed -i -e 's/^-Xms2g$/-Xms256m/' -e 's/^-Xmx2g$/-Xmx256m/' \
    /etc/elasticsearch/jvm.options
RUN systemctl enable elasticsearch
# Install Kibana
RUN yum -y -q install kibana
RUN systemctl enable kibana
RUN echo 'server.host: "0.0.0.0"' >> /etc/kibana/kibana.yml
RUN echo 'elasticsearch.url: "http://localhost:8200"' >> /etc/kibana/kibana.yml
RUN sed -i -e s/logstash-/sample/ \
    /usr/share/kibana/src/core_plugins/kibana/ui_setting_defaults.js

# Add oneshot service to start application deployment at startup
ADD saw-deploy/saw-deploy /root/
ADD saw-deploy/saw-config /root/
COPY saw-deploy/saw-deploy.service /etc/systemd/system/
RUN systemctl enable saw-deploy

# Add SAW test metrics
COPY saw-metrics /root/saw-metrics/
RUN chmod a+x /root
COPY saw-metrics/saw-metrics.service /etc/systemd/system/
RUN systemctl enable saw-metrics

# Add systemd journal gateway for browsing logs
RUN yum -y -q --nogpgcheck install systemd-journal-gateway
RUN systemctl enable systemd-journal-gatewayd.socket
# Workaround: Installing the systemd journal gateway causes the
# services below to fail, so mask them.  Remove when fixed in newer
# systemd version.
RUN systemctl mask systemd-machine-id-commit.service
RUN systemctl mask systemd-remount-fs.service

# Add SAW start page with links to services
ADD start /usr/share/nginx/html/start/

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

# Add assembly from Docker Maven plug-in
COPY maven /maven/

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD systemctl is-system-running --quiet
