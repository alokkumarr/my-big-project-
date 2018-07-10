#
# SIP testing image for application nodes
#
# Application nodes run SIP services that act as MapR and
# Elasticsearch clients.
#

# Start from SIP base image
FROM sip-base-maprclient:${project.version}

# Preinstall MariaDB packages to save time during deployment
RUN yum -y -q install mariadb-server
