
//function isArray(a){ return a.__proto__.constructor === Array }

// const value generator
export {gen_const, gen_int, gen_float, gen_rec, gen_arr}

// const value generator
function gen_const(val,...ign) {
  return ()=>val
}

// int value generator
function gen_int(mi,ma,...ign) {
      return ()=>parseInt(mi+Math.random()*(ma-mi))
}

// float value generator
function gen_float(mi,ma,...ign) {
    return ()=>parseFloat(mi+Math.random()*(ma-mi))
}

// Object generator
function gen_rec(gcode) {
  return ()=>{
    let res = new Object
    for( let k in gcode ){
      res[k] = gcode[k].call()
    }
    return res
  }
}

// Array generator
function gen_arr(gcode) {
  return ()=>{
    let res = new Array
    for( let k in gcode ){
      res[k] = gcode[k].call()
    }
    return res
  }
}
