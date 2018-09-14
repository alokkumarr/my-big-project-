#
# MariaDB Galera cluster arbitrator configuration
#
GALERA_GROUP="sip"
GALERA_NODES="
{%- for host in groups['saw-security'] -%}
  {{ host }}:4567{% if not loop.last %} {% endif %}
{%- endfor -%}
"
