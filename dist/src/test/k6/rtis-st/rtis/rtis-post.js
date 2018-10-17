// K6 script - test RTIS post
import http from "k6/http";
import { check, fail, sleep } from "k6"; 

import { k6_opts, rtisURL, rtisURI, payload_json, headers, check_rtis_sr } from "./rtis-conf.js"

export const options = k6_opts
//options.iterations=2
//options.vus=1
//options.duration="10s"

export function setup(){
  log(`setup: rtisURL=${rtisURL}`)
  log(`setup: rtisURI=${rtisURI}`)
  //
  if( !check_rtis_sr() ){
    fail("RTIS check failed")
  }

  let   payload_jstr = JSON.stringify(payload_json);
  log(`setup: payload_jstr=${payload_jstr}`);
  let params = headers;
  let r = http.post(rtisURI, payload_jstr, params);
  log(`1st POST: r.status=${r.status}, r.body=${r.body}`)
  
  return ""
/*  
 NB: return value is converted to JSON,
 can't have functions
*/
}

export default function(data) {
  let payload_jstr = JSON.stringify(payload_json);
  //log(`default: payload_jstr=${payload_jstr}`);
  let params = headers;
  //
  let resp = http.post(rtisURI, payload_jstr, params);
  //
  check(resp, {
    "01 status was 200": (r) => {
      //log(`RS:${r.status}`);
      return r.status === 200;
    },
    "02 body has success": (r) => {
      //log(`RB:${r.body}`);
      return r.body.indexOf("success") >= 0;
    },
//    "03 Content-Type is text/plain": (r) => r.headers['Content-Type'].indexOf("text/plain") >= 0,
//    "04 transaction time OK": (r) => r.timings.duration < 200,
    "99_": (r) => true
  });
  //let msg = "RESP: " + toString(resp);
  //log( msg );
};

export function teardown(ctx){
  log("teardown OK");
}

function log(s){ console.log(s); } 

// k6 run --vus 100 --duration 30s rtis-post.js
