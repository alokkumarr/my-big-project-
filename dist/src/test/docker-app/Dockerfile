#
# SIP testing image for application nodes
#
# Application nodes run SIP services that act as MapR and
# Elasticsearch clients.
#

# Start from SIP base image
FROM sip-base-maprclient:${project.version}

# Workaround: See related comment about MariaDB version in
# "saw-security/tasks/main.yml".  Remove the MariaDB repository when
# CentOS defaults to MariaDB 10 or higher.  Note: Currently disabled
# as the preinstall below is also disabled.
#COPY mariadb.repo /etc/yum.repos.d

# Preinstall MariaDB packages to save time during deployment
#
# Workaround: Preinstalling MariaDB 10.3 during image building gives
# the error "chown: invalid user: 'mysql'" and seems to cause
# configuration settings in /etc/my.cnf.d to not take effect.  So
# disable preinstalling for now and reenable when CentOS defaults to
# MariaDB 10 or higher.
#RUN yum -y -q install mariadb-server
