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
# CentOS defaults to MariaDB 10 or higher.
COPY mariadb.repo /etc/yum.repos.d

# Preinstall MariaDB packages to save time during deployment
RUN yum -y -q install mariadb-server
