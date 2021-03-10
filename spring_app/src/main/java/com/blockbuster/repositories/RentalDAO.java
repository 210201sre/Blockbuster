package com.blockbuster.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blockbuster.models.Rental;

public interface RentalDAO extends JpaRepository<Rental, Integer>{

}
