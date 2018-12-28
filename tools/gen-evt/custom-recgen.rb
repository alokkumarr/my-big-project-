# Assume 'valgen.rb' loaded

###
# This file must define 3 custom vars for RTIS events processing:
# RTIS_URI - String - RTIS End point URL, ex <HOST>:/publishevent
# EVT_GEN - event generator function: () -> Hash
# PRM_GEN - function ( <event:Hash> ) -> Hash
###

################
# Env specific values
################

###
# SIP RD
# https://confluence.synchronoss.net:8443/pages/viewpage.action?pageId=177065278
#RTIS_URI = "https://realtime-rd-sip-vaste.sncrcorp.net/publishevent"
#APP_KEY  = "sip-rtis" # mapping.app_key in RTIS conf

###
# SIP PAC
RTIS_URI = "https://realtime-pac-sip-vaste.sncrcorp.net/publishevent"
APP_KEY   = "rtis-samp-client" # mapping.app_key in RTIS conf


################
# Generic
################
# RTIS HTTP: POST /publishevent, params:
# APP_KEY, APP_VERSION, APP_MODULE,
# EVENT_ID, EVENT_DATE, EVENT_TYPE
APP_VERSION = "1.0"
APP_MODULE  = "DV"
# EVENT_ID
# EVENT_DATE
# EVENT_TYPE
# HTTP POST URL PARAMS

# POST params generator
PRM_GEN = ->(event) do
  {
    APP_KEY:     APP_KEY,
    APP_MODULE:  APP_MODULE,
    APP_VERSION: event["APP_VERSION"],
    EVENT_ID:    event["EVENT_ID"],
    EVENT_DATE:  event["@timestamp"],
    EVENT_TYPE:  event["EVENT_TYPE"]
  }
end
#URI.encode_www_form(PRM_GEN.call(evt))

#
# Event object generator initialization
EVT_GEN = rec_gen(
  "APP_VERSION" => const_gen(APP_VERSION),
  "EVENT_ID" => uuid_gen(),
  "EVENT_TYPE" => one_of_gen(const_gen("KPI"),const_gen("SALT"),const_gen("ACCESS")),
  "@timestamp" => time_gen(),
  "path" => str_gen(40,20),
  # "@timestamp": time_gen(),
  # "RECEIVED_TS": time_gen(),
  # "APP_MODULE": const_gen("DV"),
  # "EVENT_DATE": time_gen(),
  # "APP_KEY": const_gen("PC.BT"),
  # "UID": uuid_gen()
  "payload" => arr_gen( [
    hex_gen(4),
    const_gen(""),
    uuid_gen(),
    const_gen(""),
    hex_gen(4),
    str_gen(4,4),
    str_gen(4,4),
    str_gen(4,4),
    str_gen(4,4),
    hex_gen(4,4)
    ] )
  )
# manual test
1||(
  require 'pp'
  msg = EVT_GEN.call.to_json
  puts "JSON:#{msg}"
  puts
  obj = JSON.load(msg)
  pp obj
)

# To generarate record
# EVT_GEN.call --> Hash
# EVT_GEN.call.to_json --> Json
