package com.razor.raw.core.pojo;

import java.io.Serializable;

public class Lookup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4983509130749176433L;
	String value;
	public Lookup() {
	}
	
	public Lookup(String value) {
		this.value = value;
		
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
}
