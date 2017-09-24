# Use project standard operating system
FROM maprtech/mapr-1base:5.2.0

# Enable installing documentation for packages
RUN sed -i -e '/tsflags=nodocs/d' /etc/yum.conf

# Install system utilities
RUN yum -y -q --nogpgcheck install lsof

# Workaround: Bamboo builds fail with no space left error, so reduce
# loopack disk size.  Additionally put on volume, which has more space
# than root filesystem.
RUN sed -i -e 's/20G/5G/' -e '2imkdir -p /mapr-data' \
    -e 's,/opt/mapr/docker.disk,/mapr-data/docker.disk,g' /usr/bin/init-script
VOLUME /mapr-data
# Also log amount of disk available in container, to track disk space
# available in Bamboo agents
RUN sed -i -e '2idf -h' /usr/bin/init-script

# Workaround: Using "/usr/sbin/lsof -i :7222" or similar as
# healthcheck indicates success too early, as the MapR image
# /usr/bin/init-script restarts services once, so hook into the init
# script instead
RUN sed -i -e 's,while true,touch /mapr-init-done\nwhile true,' /usr/bin/init-script

# Add health check to be used by Docker Maven plug-in
HEALTHCHECK --interval=5s CMD test -e /mapr-init-done && hadoop fs -ls /
