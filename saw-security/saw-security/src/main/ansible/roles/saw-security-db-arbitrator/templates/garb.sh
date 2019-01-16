#
# MariaDB Galera cluster arbitrator configuration
#
GALERA_GROUP="sip"
GALERA_NODES="
{%- for host in groups['saw-security'] + groups['saw-security-arbitrator'] -%}
  {{ host }}:4567{% if not loop.last %} {% endif %}
{%- endfor -%}
"
