
import { gen_const, gen_int, gen_float, gen_rec, gen_arr } from "./genval.js"

// record genration function
export const GEN_REC = gen_rec( {
    CVAL: gen_const(99),
    IVAL: gen_int(100,200),
    FVAL: gen_float(1,10),
    AVAL: gen_arr( [
    	gen_const(99),
    	gen_int(100,200),
    	gen_float(1,10)
    	] ),
    OVAL: gen_rec( { 
     	SUBC: gen_const(99),
     	SUBI: gen_int(100,200),
     	SUBF: gen_float(1,10)
    	} )
	} );

module.exports = GEN_REC
