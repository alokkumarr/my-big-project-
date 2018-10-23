package com.synchronoss.saw.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface ExtensionMapping {
	
	public String id();
	
	public String title() default "";
	
	public String ver() default "1.0.0";
	
	public String desc() default "";
	
}
