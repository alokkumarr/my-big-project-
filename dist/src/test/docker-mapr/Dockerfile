#
# SIP testing MapR cluster container
#
# Single-node MapR cluster that provides Apache Spark and other MapR
# and ecosystem services for SIP development.
#

# Start from SIP base image
FROM sip-base:${project.version}

# Workaround: MapR configuration gives the error
# "/opt/mapr/server/manageSSLKeys.sh: line 146: openssl: command not
# found", so install OpenSSL for it
RUN yum -y -q install openssl

# Workaround: MapR services result in the error "network.service:
# control process exited, code=exited status=6", so create the
# "/etc/sysconfig/network" file to silence that
RUN touch /etc/sysconfig/network

# Install MapR packages
RUN yum -y -q install mapr-core mapr-cldb mapr-zookeeper mapr-webserver
# Workaround: MapR packages enable mapr-warden and mapr-zookeeper by
# default, causing them to start up before MapR has been configured.
# So disable them here and let the MapR configure script enable them
# again as part of configuring.
RUN systemctl disable mapr-warden mapr-zookeeper

# Configure MapR using configure script
COPY sip-mapr-configure /root/
COPY sip-mapr-configure.service /etc/systemd/system/
RUN systemctl enable sip-mapr-configure

# Install Apache Spark in standalone mode
RUN yum -y -q install mapr-spark-master
COPY mapr-spark-worker.service /etc/systemd/system/
RUN systemctl enable mapr-spark-worker

ENV spark_home=/opt/mapr/spark/spark-2.2.1
RUN echo "export SPARK_MASTER_HOST=sip-mapr" >> $spark_home/conf/spark-env.sh
RUN echo "export SPARK_MASTER_IP=sip-mapr" >> $spark_home/conf/spark-env.sh
RUN echo 'export SPARK_DIST_CLASSPATH=$MAPR_SPARK_CLASSPATH:$(hadoop classpath)' >> $spark_home/conf/spark-env.sh
RUN sed -i -e 's/^export SPARK_WORKER_MEMORY=16g/export SPARK_WORKER_MEMORY=1g/' \
    $spark_home/conf/spark-env.sh

# Install Apache Livy
RUN yum -y -q install mapr-livy
ENV livy_home=/opt/mapr/livy/livy-0.3.0
ENV livy_conf=$livy_home/conf/livy.conf
RUN echo "livy.spark.master = spark://sip-mapr:7077" > $livy_conf
# Workaround: The Workbench JAR exceeds the default maximum upload
# file size of Livy, so increase it.
RUN echo "livy.file.upload.max.size = 500000000" >> $livy_conf

RUN echo 'export CLASSPATH=$(mapr classpath):$(hadoop classpath):$(yarn classpath)' \
  >> $livy_home/conf/livy-env.sh.template
# Workaround: The MapR "yarn classpath" command gives an error about
# the "which" not being found, so install it here
RUN yum -y -q install which

# Workaround: MapR does not log into the systemd journal.  So install
# MapR log forwrding as a workaround, so that all logs are aggregated
# into a single place.
COPY sip-mapr-logger@.service /etc/systemd/system/
RUN systemctl enable \
  sip-mapr-logger@$(systemd-escape /opt/mapr/logs/warden.log) \
  sip-mapr-logger@$(systemd-escape /opt/mapr/logs/cldb.log) \
  sip-mapr-logger@$(systemd-escape $livy_home/logs/livy-mapr-server.out)
