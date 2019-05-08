package com.sncr.saw.security.app.sso;

public class SSOResponse {
		
	private String aToken;
	private String rToken;
	private boolean validity;
	private String message;

    /**
     * Gets aToken
     *
     * @return value of aToken
     */
    public String getaToken() {
        return aToken;
    }

    /**
     * Sets aToken
     */
    public void setaToken(String aToken) {
        this.aToken = aToken;
    }

    /**
     * Gets rToken
     *
     * @return value of rToken
     */
    public String getrToken() {
        return rToken;
    }

    /**
     * Sets rToken
     */
    public void setrToken(String rToken) {
        this.rToken = rToken;
    }

    /**
     * Gets validity
     *
     * @return value of validity
     */
    public boolean isValidity() {
        return validity;
    }

    /**
     * Sets validity
     */
    public void setValidity(boolean validity) {
        this.validity = validity;
    }

    /**
     * Gets message
     *
     * @return value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}