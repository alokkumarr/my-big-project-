//export { k6_opts, rtisURL, rtisURI, payload_json, headers, check_rtis_sr }

import http from "k6/http";
import { check, fail } from 'k6';

export const k6_opts = {
//  iterations: 2,
  vus: 1,
//  duration: "10s",
  _ign: ""
}

// SIP PC DEV
//export const rtisURL = "https://rtis.pc.sip.dev.cloud.synchronoss.net/sr";

// Sales Demo (POC)
//export const rtisURL = "https://rtis-sncrbda-poc.synchronoss.net"

// SIP PAC
//export const rtisURL = "https://realtime-pac-sip-vaste.sncrcorp.net"
//export const rtisURL = "http://172.31.216.79:9950"

export const rtisURL = (()=>{
  const ev = __ENV["RTIS_URL"] || "pac-public"
  const rv = ev.match(/pac.*pub/i) ?
    "https://realtime-pac-sip-vaste.sncrcorp.net"
  : ev.match(/pac.*int/i) ?
    "http://172.31.216.79:9950"
  : ev.match(/poc.*pub/i) ?
    "https://rtis-sncrbda-poc.synchronoss.net"
  : ev.match(/https?:\/\/[^:]+:\d+/i) ?
    ev
  : fail(`bad RTIS_URL: ${ev}`)
  return rv;
})()

export const rtisURI = rtisURL+"/genericlog?CID=c&LOG_TYPE=l";
export const payload_json = { "radiation_level": "190", "ambient_temperature": "09.646", "sensor_uuid": "probe-7b6300e0", "humidity": "70.4364", "photosensor": "679.57", "timestamp": 1530557277 };
export const headers = { headers: { "Content-Type": "text/json" } };

export function check_rtis_sr() {
  const srURL = rtisURL + "/sr";
  log(`check_rtis_sr: srURL=${srURL}`) 
  let resp = http.get(srURL);
  let rc = check(
    resp, {
      "status was 200": (r) => r.status === 200,
      "RTIS is Alive": (r) => r.json()['status'] ===  "Alive",
      "transaction time OK": (r) => r.timings.duration < 100,
      _99: (r) => true
      });
  log(`check_rtis_sr: rc=${rc}`)
  return rc
}

/***
export const conf = {
    config : {
        stages: [
            {duration: '20s', target: 40},
            {duration: '15s', target: 20},
            {duration: '10s', target: 10},
            {duration: '5s', target: 0}
           ],
           iterations : 10,
           vus : 20
     }
}
***/

/*
$ rsync -avcz *.js pac-mapr05:./k6
*/
