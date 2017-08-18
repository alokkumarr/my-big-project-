package com.synchronoss.saw.composite.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoutingArchitecture {


	@Pointcut("@annotation(com.synchronoss.saw.composite.aspects.RoutingLogging) ")
	public void mediaAnnotation(){}
	
	@Pointcut("within(com.synchronoss.saw.composite..*)")
	public void inRouteLayer(){}
	
	@Pointcut("within(@org.springframework.stereotype.Controller *)")
	public void controller() {
	}}
