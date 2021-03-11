package com.blockbuster.exceptions;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

	
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){
		String bodyOfResponse = "User dose not exist";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);		
	}

	@ExceptionHandler(AuthorizationException.class)
	public ResponseEntity<Object> handleAuthorizationException(AuthorizationException ex, WebRequest request){
		String bodyOfResponse = "User is not authorized";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
	}
	
	@ExceptionHandler(UnsuccessfulLoginException.class)
	public ResponseEntity<Object> handleUnsuccessfulLoginException(UnsuccessfulLoginException ex, WebRequest request){
		String bodyOfResponse = "Email or password is wrong";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
	}
	
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex, WebRequest request){
		String bodyOfResponse = "Email trying to register with already exists";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
	}
	
	@ExceptionHandler(UsernameAlreadyExistException.class)
	public ResponseEntity<Object> handleUsernameAlreadyExistsException(UsernameAlreadyExistException ex, WebRequest request){
		String bodyOfResponse = "Username trying to register with already exists";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
	}
	
	@ExceptionHandler(PasswordMismatchException.class)
	public ResponseEntity<Object> handlePasswordMismatchException(PasswordMismatchException ex, WebRequest request){
		String bodyOfResponse = "The passwords did not match";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_ACCEPTABLE, request);
	}
}
