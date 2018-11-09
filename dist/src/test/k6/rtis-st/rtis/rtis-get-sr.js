// K6 script
// get-rtis
import http from 'k6/http';
import { check, sleep } from "k6";

export let options = {
  vus: 100,
  duration: "10s",
//  iterations: 1,
  _ign: null
};

const rtisURL = "https://rtis-sncrbda-poc.synchronoss.net/sr";

export default function() {
  let resp = http.get(rtisURL);
  check(resp, {
    "status was 200": (r) => r.status == 200,
    "RTIS is Alive": (r) => r.json()['status'] ==  "Alive",
    "transaction time OK": (r) => r.timings.duration < 100,
    _: (r) => true
  });
//  sleep(1);
//  console.log("BOO")
}; 

// k6 run --vus 100 --duration 10s get-rtis.js
//$ k6 run --out influxdb=http://localhost:8086/myk6db rtis-get-sr.js
