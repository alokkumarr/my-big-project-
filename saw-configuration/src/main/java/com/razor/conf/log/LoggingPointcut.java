package com.razor.conf.log;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingPointcut {

	@Pointcut("within(com.razor.*.web..*)")
	  public void webLayer() {
		System.out.println("webLayer");
	  }

	  @Pointcut("within(com.razor.*.service..*)")
	  public void serviceLayer() {
		  System.out.println("serviceLayer");
	  }

	  @Pointcut("within(com.razor.dao..*)")
	  public void dataAccessLayer() {
		  System.out.println("dataAccessLayer");
	  }
	  
	  @Pointcut("within(com.razor.repository..*)")
	  public void dataAccessRepositoryLayer() {
		  System.out.println("dataAccessRepositoryLayer");
	  }
	  
	  @Pointcut("within(com.razor..*)")
	  public void testRepositoryLayer() {
		  System.out.println("88888888888888888888888888testRepositoryLayer");
	  }

}
