
import {
  gen_const,
  gen_int,
  gen_float,
  gen_string,
  gen_date,
  gen_uid,
  gen_some,
  gen_val
} from "./genval.js";

// module exports
const ME = {}
/*
export {
  RTIS_URI,
  EVT_GEN,
  PRM_GEN
}
*/

//const RTIS_URI = 10
//const EVT_GEN  = 20
//const PRM_GEN  = 30

//################
// Generic
//################
// RTIS HTTP: POST /publishevent, params:
// APP_KEY, APP_VERSION, APP_MODULE,
// EVENT_ID, EVENT_DATE, EVENT_TYPE

const APP_KEY     = "rtis-samp-client" // mapping.app_key in RTIS conf
const APP_VERSION = "1.0"
const APP_MODULE  = "DV"

// This file must define 3 custom vars for RTIS events processing:
// RTIS_URI - String - RTIS End point URL, ex <HOST>:/publishevent
// EVT_GEN - event function generator: () -> (() -> Hash)
// PRM_GEN - function ( <event:Hash> ) -> Hash

//################
//# Env specific values
//################

// SIP RD
// https://confluence.synchronoss.net:8443/pages/viewpage.action?pageId=177065278
//RTIS_URI = "https://realtime-rd-sip-vaste.sncrcorp.net/publishevent"
//APP_KEY  = "sip-rtis" // mapping.app_key in RTIS conf

// SIP PAC

ME.RTIS_URI = "https://realtime-pac-sip-vaste.sncrcorp.net/publishevent"
//ME.RTIS_URI = "http://localhost:8080/publishevent"

//# Event object generator initialization
//## Mandatory fields (6):
// APP_KEY, APP_MODULE, APP_VERSION,
// EVENT_DATE, EVENT_TYPE, EVENT_ID,
//## Custom fields (6):
// @timestamp
// path
// payload

ME.EVT_GEN = gen_val( {
  APP_VERSION: APP_VERSION,
  EVENT_ID: gen_uid(),
  EVENT_TYPE: gen_some("KPI","SALT","ACCESS"),
  "@timestamp": gen_date(),
  path: ()=>("/"+gen_string(10,20)()),
  payload: gen_val( [
    gen_uid(),
    0,
    gen_date(),
    "",
    gen_int(1000,1100),
    gen_some(10,20,30,40,50),
    "",
    gen_float(1,10),
    gen_string(4,4)
  ] )
} )

// POST params constructor
ME.PRM_GEN = (evt)=>( {
  APP_KEY:     APP_KEY,
  APP_MODULE:  APP_MODULE,
  APP_VERSION: evt.APP_VERSION,
  EVENT_ID:    evt.EVENT_ID,
  EVENT_DATE:  evt["@timestamp"],
  EVENT_TYPE:  evt.EVENT_TYPE
} )

//= To generarate HTTP POST body (event):
// evt = EVT_GEN.call() --> Hash
// JSON.stringify( evt ) --> Json
//= To generate HTTP POST params:
// PRM_GEN(evt) --> Hash

module.exports = ME
