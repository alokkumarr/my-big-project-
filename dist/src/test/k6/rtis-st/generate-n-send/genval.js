
//function isArray(a){ return a.__proto__.constructor === Array }

// const value generator
export {
  gen_const,
  gen_int,
  gen_float,
  gen_string,
  gen_date,
  gen_uid,
  gen_some,
  gen_val
}

// const value generator
function gen_const(val,...ign) {
  return ()=>val
}

function rnd(mi,ma) {
  mi = Math.floor(mi)
  ma = Math.ceil(ma)-mi
  return Math.floor(mi+Math.random()*ma)
}

// int value generator
function gen_int(mi,ma,...ign) {
  mi = Math.floor(mi)
  ma = Math.ceil(ma)-mi
  return ()=>Math.floor(mi+Math.random()*ma)
}

// float value generator
function gen_float(mi,ma,...ign) {
  return ()=>parseFloat(mi+Math.random()*(ma-mi))
}

// string of size [mi,ma)
function gen_string(mi,ma,...ign){
  return ()=>{
    let sr
    ma = rnd(mi,ma)
    sr = Math.random().toString(36).substr(2)
    while( sr.length < ma ) {
      sr += Math.random().toString(36).substr(2)
    }
    return sr.substr(0,ma)
  }
}

// timestamp value generator
function gen_date(...args) {
  return ()=>new Date(...args)
}

// TIME+random UID
/*
> 0xffffffffff/1000/60/60/24/365 -> 34.865285000475644
-> milliseconds repeats in 34.8 years
> (Date.now()%0xffffffffff).toString(16)
-> '66dc49ef6b' - 10 chars of UID
*/
function gen_uid(...ign){
  const dts=()=>(Date.now()%0xffffffffff).toString(16).padStart(10,'0')
  const rns=()=>(Math.floor(Math.random()*0xfffffffffff)).toString(16).padStart(11,'0')
  return ()=>dts()+"-"+rns()+"-"+rns()
}

function gen_some(...vals){
  return ()=>gen_val(vals[Math.floor(Math.random()*vals.length)]).call()
}

// Return value generator function
function hasAnyKey(x){
  for( let k in x ){
    return true
  }
  return false
}
function gen_val_x(val) {
  if( val instanceof Function ) return val
  if( (val instanceof Object) && hasAnyKey(val) ){
    const ctor = ( val instanceof Array ) ? Array : Object
    return ()=>{
      let res = new ctor
      for( let k in val ){
        res[k] = gen_val(val[k]).call()
      }
      return res
    }
  }
  return ()=>val
}

function gen_val(val) {
  switch(typeof(val)){
    case 'function': return val
    case 'object':
      //if( hasAnyKey(val) ){
        const ctor = ( val instanceof Array ) ? Array : Object
        return ()=>{
          let res = new ctor
          for( let k in val ){
            res[k] = gen_val(val[k]).call()
          }
          return res
        }
      //}
      console.log(`no key: ${val}`);
    default:
      return ()=>val
  }
}
/*
gen_val( {
  ID: gen_uid(),
  DATE: gen_date(),
  CSTR: gen_string(6,12),
  SOME: gen_some("A","B"),
  CVAL: 99,
  IVAL: gen_int(100,200),
  FVAL: gen_float(1,10),
  AVAL: [
    gen_uid(),
    gen_date(),
    99,
    gen_int(100,200),
    gen_float(1,10)
    ],
  OVAL: {
    ID: gen_uid(),
    DATE: gen_date(),
    SUBC: 99,
    SUBI: gen_int(100,200),
    SUBF: gen_float(1,10)
    }
} )()
*/
