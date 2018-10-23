'use strict';

import http from 'k6/http'
import { check, fail } from "k6";

// generators
//const gr = require("./valgen.js")
import { gen_const, gen_int, gen_float, gen_rec, gen_arr } from "./genval.js"
//import gr from "./valgen.js"

// record generator function
//const rg = require('./rec-gcode.js')
import rg from './rec-gcode.js'

//const vgSamp = JSON.parse(open("./json/vg.json"));

export let options = {
  vus: 2,
  duration: "2s",
//  iterations: 500,
  _ign: null
};

// Warm up test
function pre_test() {

  let v,c;

  v = gen_const(33).call()
  console.log(`gen_const: ${v}`)
  //c = check( v, {"const val 33" : (v) => v==33} )
  //console.log(`check: ${v==c}`);

  v = gen_int(10,20).call()
  console.log(`gen_int: ${v}`)
  //c = check( v, {"random int [10,20)" : (v) => 10<=v && v<20} )
  //console.log(`check: ${c}`);

  v = gen_float(1,2).call()
  console.log(`gen_float: ${v}`)
  //c = check( v, {"random float [1,2)" : (v) => 1<=v && v<2} )
  //console.log(`check: ${c}`);

  v = gen_arr([ gen_const(99), gen_int(10,20), gen_float(1,2) ]).call()
  console.log(`gen_arr: ${JSON.stringify(v)}`)
  //console.log(`${v}, ${typeof(v)} ${v instanceof Array} ${v.length}`)
  //c = check( v, {"gen_arr (3)" : (v) => ((v.length==3)&&(v[0]==99)) } )
  //console.log(`check: ${c}`);

  v = gen_rec( {
    VC: gen_const(99),
    VA: gen_arr([ gen_const(99), gen_int(10,20), gen_float(1,2)])
    } ).call()
  console.log(`gen_rec: ${JSON.stringify(v)}`)
//  c = check( v, {"gen_rec" : (v) => (v.VC==99&&v.VA[0]==99) } )
//  console.log(`check: ${c}`);

  console.log("pre_test OK");

}

export function setup(){
  pre_test()
  return null;
}

function show_rec(rec){
  let jrec = JSON.stringify(rec)
  console.log(`${__VU}/${__ITER} ${jrec}`);
}
export default function(_ign){
  let rec = rg()
  check(rec, {
    'CVAL is 99':(v)=>v.CVAL===99,
    'AVAL[0] is 99':(v)=>v.AVAL[0]===99,
    'OVAL.SUBC is 99':(v)=>v.OVAL.SUBC===99
  })
  //show_rec(rec)
}

export function teardown(_ign){
  console.log("teardown OK");
}

// k6 run -d 3s t-genval.js
