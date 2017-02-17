package com.razor.conf.log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogMonitor {
	
	@AfterReturning("execution(* com..*Service.*(..))")
	public void logServiceAccess(JoinPoint joinPoint) {
		Logger logger = LoggerFactory.getLogger("com.service.*");
		logger.debug("Completed: " + joinPoint);
	}
	
	@AfterReturning("execution(* com..*dao.*(..))")
	public void logRepositoryAccess(JoinPoint joinPoint) {
		Logger logger = LoggerFactory.getLogger("com.dao.*");
		logger.debug("Completed: " + joinPoint);
	}
}
