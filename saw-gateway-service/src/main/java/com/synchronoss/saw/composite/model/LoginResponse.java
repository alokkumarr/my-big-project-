package com.synchronoss.saw.composite.model;

import java.io.Serializable;

public class LoginResponse implements Serializable {

	private static final long serialVersionUID = 2207580055226488165L;
	public String token;


	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	



}

