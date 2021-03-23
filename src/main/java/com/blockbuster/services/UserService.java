package com.blockbuster.services;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.blockbuster.exceptions.PasswordMismatchException;
import com.blockbuster.exceptions.UserNotFoundException;
import com.blockbuster.models.Rental;
import com.blockbuster.models.User;
import com.blockbuster.repositories.UserDAO;

import io.micrometer.core.instrument.MeterRegistry;

@Service
public class UserService {
	
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	private MeterRegistry meterRegistry;
	private static final String CONNECTIONATTEMPT = "connection_attempt";
	private static final String TYPE = "type";
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	@Autowired
	private HttpServletRequest req;
	
	@Autowired
	private UserDAO userDAO;

	public Set<User> findAll() {
		MDC.put("findAll", "user");
		log.info("Retrieving all users");
		MDC.clear();
		
		Set<User> allUsers = Collections.emptySet();
		
		try {
			allUsers = userDAO.findAll()
				.stream()
				.collect(Collectors.toSet());
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}
		
		return allUsers;
	}
	
	public User insert(User u) {
		MDC.put("insert", u);
		log.info("Updating/inserting user");
		MDC.clear();
		
		User newUser = null;
		
		try {
			newUser = userDAO.save(u);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}
		
		return newUser;
	}
	
	public Set<Rental> getRentals(String username){
		MDC.put("username", username);
		Optional<User> u = null;
		
		try {
			u = userDAO.findByUsername(username);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}
		
		if (u.isPresent()) {
			log.info("Retrieving user's rentals list");
			MDC.clear();
			return u.get().getRentals();
		}
		
		log.error("Unable to retrieve user's rentals list");
		MDC.clear();
		return Collections.emptySet();
	}
	
	public User findByUsername(String username) {
		MDC.put("username", username);
		Optional<User> u = null;
		
		try {
			u = userDAO.findByUsername(username);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}		
		
		if(u.isPresent()) {
			log.info("Found user");
			MDC.clear();
			return u.get();
		}
		
		log.error("Unable to find user");
		MDC.clear();
		return null;
	}
	
	public User findById(int userId) {
		MDC.put("userId", Integer.toString(userId));
		User u = null;
		
		try {
			u = userDAO.findById(userId).orElse(null);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}	
		
		if(u != null) {
			log.info("User found");
		} else {
			log.error("Unable to find user");
		}
		
		MDC.clear();
		return u;
	}
	
	public boolean deleteById(int userId) {
		MDC.put("userId", Integer.toString(userId));
		Optional<User> u = null;
		
		try {
			u = userDAO.findById(userId);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}	
		
		if(u.isPresent()) {
			try {
				userDAO.deleteById(userId);
				meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
				log.info("User deleted from DB");
				MDC.clear();
				return true;
			} catch(DataAccessException e) {
				log.error("User does not exist", e);
				MDC.clear();
				meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
				return false;
			}
		}
		
		log.error("User does not exist");
		MDC.clear();
		return false;
	}
	
	public User login(String username, String password) {
		MDC.put("login", username);
		Optional <User> u = null;
		
		try {
			u = userDAO.findByUsername(username);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}	
		
		if(u.get().getPassword().equals(password)) {
			HttpSession session = req.getSession();
			session.setAttribute("currentUser", u.get());
			log.info("User successfully logged in");
			MDC.clear();
			return u.get();
		} else {
			throw new PasswordMismatchException();
		}
	}
	
	public void logout() {
		
		HttpSession session = req.getSession(false);
		
		if(session == null) {
			// No one was logged in
			
			return;
		}
		
		session.invalidate();
		log.info("Logged out");
		MDC.clear();
	}
}
