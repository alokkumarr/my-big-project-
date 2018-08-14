// K6 script
// RTIS post
import http from "k6/http";
import { check, sleep } from "k6"; 

export let options = {
  vus: 100,
  duration: "10s",
//  iterations: 1,
  _ign: null
};

const rtisURL = "https://rtis-sncrbda-poc.synchronoss.net";
const rtisURI = rtisURL+"/genericlog?CID=c&LOG_TYPE=l"

const payload = { "radiation_level": "190", "ambient_temperature": "09.646", "sensor_uuid": "probe-7b6300e0", "humidity": "70.4364", "photosensor": "679.57", "timestamp": 1530557277 };
const headers = { headers: { "Content-Type": "text/json" } };

export default function() {
  let payloadStr = JSON.stringify(payload);
  let params = headers;
  //
  let resp = http.post(rtisURI, payloadStr, params);
  //
  check(resp, {
    "01 status was 200": (r) => r.status == 200,
    "02 body has success": (r) => r.body.indexOf("success") >= 0,
    "03 Content-Type is text/plain": (r) => r.headers['Content-Type'].indexOf("text/plain") >= 0,
    "04 transaction time OK": (r) => r.timings.duration < 200,
    "99_": (r) => true
  });
/**
  log("=== RESPONSE")
  for( let p in resp ){
    console.log( p, resp[p] );
  }
  log("HEADERS")
  let h = resp.headers
  for( let p in h ){
    console.log( p, h[p] );
  }
**/
  //let msg = "RESP: " + toString(resp);
  //log( msg );
};

function log(s){ console.log( s ) }

// k6 run --vus 100 --duration 30s rtis_post.js
