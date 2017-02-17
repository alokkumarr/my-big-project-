package com.razor.raw.core.common.exception;

public class RAWDSException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public RAWDSException()
	{
		
	}
	

	public RAWDSException(String s)
	{
		super(s);
	}
	

	public RAWDSException(Throwable t)
	{
		super(t);
	}
	
	public RAWDSException(String s, Throwable t)
	{
		super(s, t);
	}
}
