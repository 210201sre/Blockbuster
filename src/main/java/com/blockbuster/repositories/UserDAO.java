package com.blockbuster.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blockbuster.models.Rental;
import com.blockbuster.models.User;

public interface UserDAO extends JpaRepository<User, Integer>{
	public Optional<User> findByUsername(String username);
}
