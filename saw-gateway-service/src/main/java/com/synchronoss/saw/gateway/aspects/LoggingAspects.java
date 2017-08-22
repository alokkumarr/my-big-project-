package com.synchronoss.saw.gateway.aspects;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This Aspect class
 * @author saurav.paul
 * @since 1.0.0
 */
@Aspect
@Component
public class LoggingAspects {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(LoggingAspects.class);

	@Around("com.synchronoss.saw.gateway.aspects.RoutingArchitecture.mediaAnnotation() || "
			+ "com.synchronoss.saw.gateway.aspects.RoutingArchitecture.inRouteLayer() || com.synchronoss.saw.gateway.aspects.RoutingArchitecture.controller()")
	public Object inRouteAroundAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
	{
		Object returnedValueFromMethod = null;
		 
		MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
		Method method = signature.getMethod();
		
		if (method.getDeclaringClass().isInterface()) {

			try {
				method = proceedingJoinPoint
						.getTarget()
						.getClass()
						.getDeclaredMethod(
								proceedingJoinPoint.getSignature().getName(),
								method.getParameterTypes());
			} 
			catch (final SecurityException exception) {
				LOGGER.error("Exception occurred while tracing the method at" + this.getClass().getMethods()[0]+ " :",exception);
			} 
			catch (final NoSuchMethodException exception) {
				LOGGER.error("Exception occurred while tracing the method at " + this.getClass().getMethods()[0]+ " :",exception);
			}
	}	
		try 
		{
			if (LOGGER.isTraceEnabled()) {
		   		//mitoPerformanceMonitor.startMonitor();
			}
			
			StringBuffer stringBuffer = null; 
					stringBuffer = new StringBuffer();
					stringBuffer.append(" Method : ").append(method.getName());
					stringBuffer.append("() ").append(" of the class :").append(proceedingJoinPoint.getTarget().getClass());
					stringBuffer.append(" with the arguments");
					stringBuffer.append(" starts here ");
					Object[] args = proceedingJoinPoint.getArgs();
					if(args.length>0){
						   for (int i = 0; i < args.length; i++) {
							   stringBuffer.append(System.getProperty("line.separator"));
							   stringBuffer.append("arg-value ").append(i+1).append(": ").append(args[i]);
						}
					}
					LOGGER.debug(stringBuffer.toString());
			
			returnedValueFromMethod = proceedingJoinPoint.proceed();
			
				stringBuffer = null;
				stringBuffer = new StringBuffer();
				if (returnedValueFromMethod != null) {
					stringBuffer.append(" Returning Value :").append(
							returnedValueFromMethod);
					stringBuffer.append(System.getProperty("line.separator"));
				}
				stringBuffer.append(" Method : ").append(method.getName());
				stringBuffer.append("() ").append(" of the class :").append(proceedingJoinPoint.getTarget().getClass());
				stringBuffer.append(" with the arguments :").append(Arrays.toString(proceedingJoinPoint.getArgs())).append(" ends here ");
				LOGGER.debug(stringBuffer.toString());
			    stringBuffer = null;
		} 
		catch (RuntimeException nestedException){
			LOGGER.error("An Exception has been thrown in :" + proceedingJoinPoint.getSignature().getName());
			LOGGER.error("Cause :" + nestedException.getMessage());
			throw nestedException;
		}
		catch (Throwable throwable) 
		{
			LOGGER.error("An Exception has been thrown in :" + proceedingJoinPoint.getSignature().getName());
			LOGGER.error("Cause :" + throwable.getMessage());
			throw throwable;
		}

		return returnedValueFromMethod;
	}
}
