package com.blockbuster.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.blockbuster.models.ROLE;

@Retention(RUNTIME)
@Target(METHOD)
public @interface Authorized {
	public ROLE[] allowedRoles() default {};
}
