# Use project standard operating system
FROM maprtech/mapr-1base:5.2.0

# Enable installing documentation for packages
RUN sed -i -e '/tsflags=nodocs/d' /etc/yum.conf

# Install system utilities
RUN yum -y -q --nogpgcheck install lsof smem

#
# Install Spark
#
# Workaround: Replace default MapR Ecosystem Pack repository with
# newer to get Spark 2.1
RUN sed -i -e 's,ecosystem-5.x,MEP/MEP-3.0.1,' \
    /etc/yum.repos.d/mapr_ecosystem.repo
# Workaround: Installing mapr-spark gives RPM checksum error if patch
# repository is enabled, so disable it for now and rebuild Yum cache
RUN rm /etc/yum.repos.d/mapr_patch.repo && yum clean all
RUN yum -y -q --nogpgcheck install mapr-spark-master mapr-spark
RUN ln -s /opt/mapr/spark/spark-* /opt/mapr/spark/spark-current
# Workaround: Replace default container hostname with localhost
RUN sed -i -e 's,72ba20be3774,localhost,' \
    /opt/mapr/spark/spark-current/conf/spark-defaults.conf \
    /opt/mapr/spark/spark-current/conf/spark-env.sh
# Configure Spark slaves
RUN echo localhost > /opt/mapr/spark/spark-current/conf/slaves
# Workaround: The default configuration binds to localhost by default,
# so remove those lines to bind to all interfaces and allow access
# from other containers
RUN sed -i -e '/^export SPARK_MASTER_[A-Z]*=localhost$/d' \
    /opt/mapr/spark/spark-current/conf/spark-env.sh

# Add MapR container initialization script
ADD saw-mapr-init /root/
RUN sed -i -e 's,while true,/root/saw-mapr-init\nwhile true,' \
    /usr/bin/init-script

# Try reducing MapR container startup time
RUN sed -i -e 's/^sleep 60/sleep 30/' /usr/bin/init-script

# Workaround: Bamboo builds fail with no space left error, so reduce
# loopack disk size.  Additionally put on volume, which has more space
# than root filesystem.
RUN sed -i -e 's/20G/5G/' -e '2imkdir -p /mapr-data' \
    -e 's,/opt/mapr/docker.disk,/mapr-data/docker.disk,g' /usr/bin/init-script
VOLUME /mapr-data
# Also log amount of disk available in container, to track disk space
# available in Bamboo agents
#RUN sed -i -e '2idf -h' /usr/bin/init-script

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD test -e /saw-mapr-init-done && hadoop fs -ls /
