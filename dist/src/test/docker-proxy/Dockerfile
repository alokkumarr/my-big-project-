#
# Testing container image for SIP Proxy node.
#

# Start from SIP base image
FROM sip-base:${project.version}

# Add SAW start page with links to services
COPY dev /usr/share/nginx/html/dev/

# Add Squid proxy server to access internal services during
# development
RUN yum -y -q install squid
RUN systemctl enable squid
ENV squid_conf=/etc/squid/squid.conf
RUN echo "http_access allow all" > $squid_conf
RUN echo "http_port 3128" >>  $squid_conf
