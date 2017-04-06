package com.synchronoss.saw.composite.aspects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoutingLogging {
	String businessLayer() default "unassigned"; // TODO: Needs to be implemented at Aspect class
}
