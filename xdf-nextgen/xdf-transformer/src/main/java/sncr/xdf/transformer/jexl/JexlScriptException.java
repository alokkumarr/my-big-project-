package sncr.xdf.transformer.jexl;

public class JexlScriptException extends RuntimeException {
	
	private static final long serialVersionUID = -3941435994691811628L;

	public JexlScriptException(String message) {
		super(message);
	}
	
	public JexlScriptException (String message, Throwable e){
		super(message, e);
	}

}
