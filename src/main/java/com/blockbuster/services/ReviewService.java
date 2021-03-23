package com.blockbuster.services;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.blockbuster.models.Review;
import com.blockbuster.repositories.ReviewDAO;

import io.micrometer.core.instrument.MeterRegistry;

@Service
public class ReviewService {
	
	private static final Logger log = LoggerFactory.getLogger(ReviewService.class);
	private MeterRegistry meterRegistry;
	private static final String CONNECTIONATTEMPT = "connection_attempt";
	private static final String TYPE = "type";
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	@Autowired
	private ReviewDAO reviewDAO;
	
	public Set<Review> findAll() {
		MDC.put("findAll", "review");
		log.info("Retrieving all reviews");
		MDC.clear();
		
		Set<Review> allReviews = Collections.emptySet();
		
		try {
			allReviews = reviewDAO.findAll()
				.stream()
				.collect(Collectors.toSet());
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}
		
		return allReviews;
	}
	
	public Review insert(Review r) {
		MDC.put("insert", r);
		log.info("Updating review");
		MDC.clear();
		
		Review newReview = null;
		
		try {
			newReview = reviewDAO.save(r);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}
		
		return newReview;
	}
	
	public boolean deleteById(int reviewId) {
		MDC.put("reviewId", Integer.toString(reviewId));
		try {
			reviewDAO.deleteById(reviewId);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
			log.info("Review has been deleted");
			MDC.clear();
			return true;
		} catch(DataAccessException e) {
			log.error("Review unable to be deleted", e);
			MDC.clear();
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
			return false;
		}
	}
	
	public double getAverageRating(int gameId) {
		MDC.put("gameId", Integer.toString(gameId));
		log.info("Average score retrieved");
		MDC.clear();
		
		Double averageRating = 0.0;
		
		try {
			averageRating = reviewDAO.getAverageRating(gameId);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}
		
		return averageRating;
	}
	
	public Review findById(int id) {
		MDC.put("reviewId", Integer.toString(id));
		Review r = null;
		
		try {
			r = reviewDAO.findById(id).orElse(null);
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, SUCCESS);
		} catch (DataAccessException e) {
			meterRegistry.counter(CONNECTIONATTEMPT, TYPE, FAIL);
		}
		
		if(r != null) {
			log.info("Average score retrieved");
		} else {
			log.error("Couldn't retrieve average score");
		}
		
		MDC.clear();
		return r;
	}
}
