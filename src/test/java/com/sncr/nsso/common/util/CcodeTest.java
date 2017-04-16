package com.sncr.nsso.common.util;

import static org.junit.Assert.*;

import org.junit.Test;
/**
 * 
 * @author ssom0002
 *
 */
public class CcodeTest {

    @Test
    public void test() {
        
        String password = "$#!some38.Very*coMpleX-p=@ss^0rD~";
        //System.out.print("PW:"+password+"::\n");
        String encryptedPassword = Ccode.cencode(password);
        //System.out.print("EP:"+encryptedPassword+"::\n");

        String password_Actual   = Ccode.cdecode(encryptedPassword);
        assertEquals(password, password_Actual);        

        String encrypted_Actual  = Ccode.cencode(password_Actual);
        assertEquals(encryptedPassword, encrypted_Actual);
    }

}

/* Console test
(
pass='$#!some38.Very*coMpleX-p=@ss^0rD~'
encpw="$( java -cp saw-security-2.0.0-classes.jar com.sncr.nsso.common.util.Ccode "$pass" )"
decpw="$( java -cp saw-security-2.0.0-classes.jar com.sncr.nsso.common.util.Ccode "$encpw" 1 )"
[[ "$pass" = "$decpw" ]] && echo PASSED
)
*/
