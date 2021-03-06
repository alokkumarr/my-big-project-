#
# SIP testing image for Elasticsearch.
#
# Provides Elasticsearch for testing environments.
#

# Start from SIP base image
FROM sip-base:${project.version}

# Install Elasticsearch
ENV version=7.6.0
COPY elasticsearch.repo /etc/yum.repos.d
RUN rpm --import https://artifacts.elastic.co/GPG-KEY-elasticsearch
RUN yum -y -q install elasticsearch-$version
RUN echo "http.port: 8200" >> /etc/elasticsearch/elasticsearch.yml
RUN echo "transport.tcp.port: 8300" >> /etc/elasticsearch/elasticsearch.yml
RUN echo "network.host: _eth0_" >> /etc/elasticsearch/elasticsearch.yml
RUN echo "discovery.type: single-node" >> /etc/elasticsearch/elasticsearch.yml

RUN sed -i -e 's/^-Xms2g$/-Xms256m/' -e 's/^-Xmx2g$/-Xmx256m/' \
    /etc/elasticsearch/jvm.options
RUN systemctl enable elasticsearch
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install -b \
    https://github.com/vvanholl/elasticsearch-prometheus-exporter/releases/download/7.6.0.0/prometheus-exporter-7.6.0.0.zip
