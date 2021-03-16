package com.blockbuster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.blockbuster.models.CONSOLES;
import com.blockbuster.models.GENRE;
import com.blockbuster.models.Game;
import com.blockbuster.models.ROLE;
import com.blockbuster.models.Rental;
import com.blockbuster.models.STATES;
import com.blockbuster.models.User;
import com.blockbuster.repositories.GameDAO;
import com.blockbuster.repositories.RentalDAO;
import com.blockbuster.repositories.UserDAO;
import com.blockbuster.services.RentalService;

@RunWith(MockitoJUnitRunner.class)
public class RentalServiceTests {
	@InjectMocks
	RentalService rentalService;
	
	@Mock
	RentalDAO rentalDAO;
	
	@Mock
	UserDAO userDAO;
	
	@Mock
	GameDAO gameDAO;
	
	@Mock
	User u;
	
	@Mock
	Game g;
	
	static Optional<Game> testGame;
	static Optional<User> testUser;
	
	@BeforeClass
	public static void setUpAll() {
		testGame = Optional.ofNullable(
				new Game(1, "First", GENRE.ACTION_ADVENTURE, CONSOLES.SNES, "Publisher", "Developer", LocalDate.of(1983, 1, 13))
		);
		
		testUser = Optional.ofNullable(
				new User(1, "First", "password", LocalDate.of(1983, 1, 13), 
						"123 Some St.", "City", STATES.MA, 12345, Collections.emptySet(), ROLE.CUSTOMER)
		);
	}
	
	@AfterClass 
	public static void tearDownAll() {
		testGame = null;
		testUser = null;
	}

	@Test
	public void testInsert() {
		System.out.println("Testing insert()");
		Rental testRental = new Rental(testUser.get(), testGame.get());
		
		when(userDAO.findById(1)).thenReturn(testUser);
		when(gameDAO.findById(1)).thenReturn(testGame);
		when(rentalDAO.save(testRental)).thenReturn(testRental);
		when(gameDAO.save(testGame.get())).thenReturn(testGame.get());
		
		assertSame(testRental, rentalService.insert(1, 1));
	}
	
	@Test
	public void testFailedInsert() {
		System.out.println("Testing failed insert()");
		
		assertNull(rentalService.insert(testUser.get().getId(), testGame.get().getId()));
	}
	
	@Test
	public void testDeleteById() {
		System.out.println("Testing deleteById()");
	}
	
	@Test
	public void testFailedDeleteById() {
		System.out.println("Testing failed deleteById()");
		
		doThrow(IllegalArgumentException.class).when(rentalDAO).deleteById(1);
		
		assertFalse(rentalService.deleteById(1));
	}
	
	@Test
	public void testSendEmail() {
		
	}
	
	@Test
	public void testToggleOverdue() {
		
	}
	
	@Test
	public void testChangeDate() {
		
	}
}
