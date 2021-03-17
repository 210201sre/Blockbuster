package com.blockbuster.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.blockbuster.models.Review;

public interface ReviewDAO extends JpaRepository<Review, Integer>{
	
	@Query(value = "SELECT AVG(rating) FROM rentals_vg.reviews WHERE game_id = :gameId", nativeQuery = true)
	public double getAverageRating(@Param("gameId")int gameId);
}
