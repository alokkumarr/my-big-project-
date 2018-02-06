# Use project standard operating system
FROM maprtech/mapr-1base:5.2.0

# Enable installing documentation for packages
RUN sed -i -e '/tsflags=nodocs/d' /etc/yum.conf

# Install system utilities
# Workaround: Docker image builds occasionally fail with a "failure:
# smem-1.4-1.el6.noarch.rpm from epel: [Errno 256] No more mirrors to
# try" error.  Possibly the MapR base image has outdated Yum mirror
# information.  So disable installing these packages by default for
# now, as they are anyway optional.
#RUN yum -y -q --nogpgcheck install lsof smem
RUN yum -y -q --nogpgcheck install sudo

#
# Install YARN
#
RUN yum -y -q install mapr-resourcemanager mapr-nodemanager

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
    -e 's/export SPARK_WORKER_MEMORY=16g/export SPARK_WORKER_MEMORY=1g/' \
    /opt/mapr/spark/spark-current/conf/spark-env.sh

#
# Install Livy
#
RUN yum -y -q install mapr-hue-livy
RUN echo "livy.spark.master = yarn" \
    >> /opt/mapr/hue-livy/hue-livy-3.12.0/conf/livy.conf
RUN sed -i -e '/^spark.master$/d' \
    /opt/mapr/hue-livy/hue-livy-3.12.0/conf/spark-blacklist.conf
# Workaround: Livy won't start due to root owning the logs directory,
# so change the owner to the "mapr" user
RUN chown mapr:mapr /opt/mapr/hue-livy/hue-livy-3.12.0/logs
# Workaround: The Workbench JAR exceeds the default maximum upload
# file size of Livy, so increase it.
RUN echo "livy.file.upload.max.size = 500000000" \
    >> /opt/mapr/hue-livy/hue-livy-3.12.0/conf/livy.conf

# Add MapR container initialization script
COPY saw-mapr-init /root/
RUN sed -i -e 's,while true,/root/saw-mapr-init\nwhile true,' \
    /usr/bin/init-script
# Workaround: Set execute bit for Windows users
RUN chmod a+x /root/saw-mapr-init

# Try reducing MapR container startup time
RUN sed -i -e 's/^sleep 60/sleep 30/' /usr/bin/init-script

# Workaround: Bamboo builds fail with no space left error, so reduce
# loopack disk size.  Additionally put on volume, which has more space
# than root filesystem.  Note: The volume directive is inserted into
# the Dockerfile from the Bamboo plan, to avoid creating dangling
# volumes in other environments which do not need the workaround.
RUN sed -i -e 's/20G/10G/' -e '2imkdir -p /mapr-data' \
    -e 's,/opt/mapr/docker.disk,/mapr-data/docker.disk,g' /usr/bin/init-script

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD test -e /saw-mapr-init-done
