package com.razor.raw.core.common.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author creddy
 *
 * This class is used for capturing runtime exceptions
 */
public class RazorsightRuntimeException extends RuntimeException {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    /**
     * The root cause of the type Throwable which is wrapped by this class
     */
    protected Throwable rootCause = null;
    /**
     * The list which holds the multiple exceptions.
     */
    @SuppressWarnings("rawtypes")
	private List exceptions = new ArrayList();
    
    /**
     * The overloaded constructor , which accepts rootCause of type Throwable
     * @param rootCause The root cause which is going to be wrapped by the application.
     */ 
    public RazorsightRuntimeException(Throwable rootCause) {
        this.rootCause = rootCause;
    }
    /**
     * Constructor that takes a message. The message will be set to the parent exception
     * as the exception message
     */ 
    public RazorsightRuntimeException(String message) {
        super(message) ;
    }
    /**
     * Constructor that takes a message and a cuse exception. The message will 
     * be set to the parent exception as the exception message
     */ 
    public RazorsightRuntimeException(String message,Throwable rootCause) {
        super(message) ;
        setRootCause(rootCause) ;
    }
    /**
     * This is the getter for all the list of exceptions(chained/multiple exceptions).
     * @return this will return a list of exceptions.
     */
    @SuppressWarnings("rawtypes")
	public List getExceptions() {
        return exceptions;
    }
    /**
     * This method will add multiple exceptions to the array list.
     * @param ex This will accept an exception of the type FedAdProdRunTimeException 
     */
    @SuppressWarnings("unchecked")
	public void addException(RazorsightRuntimeException ex){
        exceptions.add(ex);
    }

    /**
     * This method will return the root cause of the exception of type Throwable
     * @return root cause of the type throwable.
     */
    public Throwable getRootCause() {
        return rootCause;
    }

    /**
     * This method set an exception list to this object
     * @param list The excepion list
     */
    @SuppressWarnings("rawtypes")
	public void setExceptions(List list) {
        exceptions = list;
    }

    /**
     * This method will set the root cause to the object.
     * @param throwable root cause is of the type Throwable
     */
    public void setRootCause(Throwable throwable) {
        rootCause = throwable;
    }
    
    /**
     * Prints the stack trace to System.err.  This method overrides
     * the method from the Throwable class.
     */
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    /**
     * Prints the stack trace to the specified PrintStream.  This method
     * overrides the method from the Throwable class.
     *
     * @param ps the PrintStream to which the stack trace will be printed.
     */
    public void printStackTrace(PrintStream ps) {
        super.printStackTrace(ps);
        
        if (this.getRootCause() != null) {
            this.getRootCause().printStackTrace(ps);
        }
    }

    /**
     * Prints the stack trace to the specified PrintWriter.  This method
     * overrides the method from the Throwable class.
     *
     * @param ps the PrintWriter to which the stack trace will be printed.
     */
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        
        if (this.getRootCause() != null) {
            this.getRootCause().printStackTrace(pw);
        }
    }
}
