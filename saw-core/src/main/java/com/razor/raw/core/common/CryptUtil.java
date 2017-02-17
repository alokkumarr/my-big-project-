package com.razor.raw.core.common;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

/**
 * 
 * @author surendra.rajaneni
 *
 */
public class CryptUtil {

	/**
	 * @param args
	 */
	 private static Base64 base64Codec;
	 public static String MESSAGE_DIGEST_ALGORITHM;
	private static MessageDigest messageDigest;
	 static 
	    {
	       
	        MESSAGE_DIGEST_ALGORITHM = "MD5";
	        try
	        {
	            messageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
	            base64Codec = new Base64();
	        }
	        catch(NoSuchAlgorithmException nosuchalgorithmexception)
	        {
	            throw new ExceptionInInitializerError("NoSuchAlgorithmException Exception encountered");
	        }
	    }
		 public static String encryptText(String s)
		    {
		        String s1 = null;
		        if(s != null)
		        {
		            byte abyte0[] = base64Codec.encode(s.getBytes());
		            s1 = new String(abyte0);
		        }
		        return s1;
		    }

		    public static String decryptText(String s)
		    {
		        String s1 = null;
		        if(s != null)
		        {
		            byte abyte0[] = base64Codec.decode(s.getBytes());
		            s1 = new String(abyte0);
		        }
		        return s1;
		    }

		/*    public static void main(String args[])
		    {
		    	if( args != null && args.length > 0 )
				{
		    		String s = encryptText(args[0]);
		    		System.out.println((new StringBuilder()).append("Encrypted text == ").append(s).toString());
		    		System.out.println((new StringBuilder()).append("Original text == ").append(decryptText(s)).toString());
				}
		    }*/

		public String encrypt(String s)
		    {
		        String s1 = null;
		        if(s != null)
		        {
		            byte abyte0[] = messageDigest.digest(s.getBytes());
		            abyte0 = base64Codec.encode(abyte0);
		            s1 = new String(abyte0);
		        }
		        return s1;
		    }
		 

	

}
