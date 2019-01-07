'use strict';

import http from "k6/http"
import { check, fail } from "k6"

function log(...s){ console.log(...s) }

import {
  RTIS_URI,
  EVT_GEN,
  PRM_GEN
} from "./custom-recgen.js"

export let options = {
  vus: 1,         // -u
  // duration: "1s", // -d ..
  // iterations: 1, // -i ..
  _ign: null
};

function queryString(params){
  return Object.keys(params).map((key) => {
    return encodeURIComponent(key) + '=' + encodeURIComponent(params[key])
  }).join('&')
}

// Warm up test
function pre_test() {
  log(`RTIS_URI: ${RTIS_URI}`)

  let evt = EVT_GEN()
  log(`EVT: ${JSON.stringify(evt)}`)

  let prm = PRM_GEN(evt)
  log(`PRM: ${JSON.stringify(prm)}`)

  log("pre_test OK");

}

export function setup(){
  pre_test()
  return null;
}

// evt = EVT_GEN()
// prm = PRM_GEN(evt)
// http.post(RTIS_URI, evt, prm)

function show_rec(rec){
  let jrec = JSON.stringify(rec)
  console.log(`${__VU}/${__ITER} ${jrec}`);
}
export default function(_ign){
  let evt = EVT_GEN()
  check(evt, {
    'Event has APP_VERSION': (v)=>v.hasOwnProperty("APP_VERSION"),
    'Event has EVENT_ID': (v)=>v.hasOwnProperty("EVENT_ID"),
    'Event has EVENT_TYPE': (v)=>v.hasOwnProperty("EVENT_TYPE"),
    'Event has @timestamp': (v)=>v.hasOwnProperty("@timestamp"),
    'Event has path': (v)=>v.hasOwnProperty("path"),
    'Event has payload': (v)=>v.hasOwnProperty("payload"),
  })
  //show_rec(rec)
  let params = PRM_GEN(evt)
  check(params, {
    'Param has APP_KEY': (v)=>v.hasOwnProperty("APP_KEY"),
    'Param has APP_MODULE': (v)=>v.hasOwnProperty("APP_MODULE"),
    'Param has APP_VERSION': (v)=>v.hasOwnProperty("APP_VERSION"),
    'Param has EVENT_ID': (v)=>v.hasOwnProperty("EVENT_ID"),
    'Param has EVENT_DATE': (v)=>v.hasOwnProperty("EVENT_DATE"),
    'Param has EVENT_TYPE': (v)=>v.hasOwnProperty("EVENT_TYPE"),
  } )

  let headers = { "Content-Type": "application/json; charset=UTF-8" }
  let qs = queryString(params)
  let rsp = http.post(`${RTIS_URI}?${qs}`, JSON.stringify(evt), { headers: headers } );
  check(rsp, {
    'status is ok': (r)=>(r.status === 200)
  } )
  //console.log( `RSP: ${rsp.body}` )
}

export function teardown(_ign){
  console.log("teardown OK");
}

// k6 run -u 300 -i 10000 generate-n-send.js
