#
# MariaDB Galera cluster arbitrator configuration
#
GALERA_GROUP="sip"
GALERA_NODES="
{%- for host in groups['saw-security'] + groups['saw-security-arbitrator'] -%}
  {{ host }}:4567{% if not loop.last %} {% endif %}
{%- endfor -%}
"
#GALERA_OPTIONS="
#{%- if sip_secure -%}
#socket.ssl_cert=/etc/bda/{{ansible_host}}-certificate.crt;socket.ssl_key=/etc/bda/{{ansible_host}}-key.pem
#{%- endif -%}
#"
