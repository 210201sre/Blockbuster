package com.blockbuster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
	
//	@Mock
//	RentalService mockRentalService;
	
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
	
	private static final int testId = 1;
	static Optional<Game> testGame;
	static Optional<User> testUser;
	static Optional<Rental> testRental;
	
	@BeforeClass
	public static void setUpAll() {
		testGame = Optional.ofNullable(
				new Game(1, "First", GENRE.ACTION_ADVENTURE, CONSOLES.SNES, "Publisher", "Developer", LocalDate.of(1983, 1, 13))
		);
		
		testUser = Optional.ofNullable(
				new User(1, "First", "password", LocalDate.of(1983, 1, 13), 
						"123 Some St.", "City", STATES.MA, 12345, Collections.emptySet(), ROLE.CUSTOMER)
		);
		
		testRental = Optional.ofNullable(
				new Rental(testUser.get(), testGame.get())
		);
	}
	
	@AfterClass 
	public static void tearDownAll() {
		testGame = null;
		testUser = null;
		testRental = null;
	}

	@Test
	public void testInsert() {
		System.out.println("Testing insert()");
		
		when(userDAO.findById(testUser.get().getId())).thenReturn(testUser);
		when(gameDAO.findById(testGame.get().getId())).thenReturn(testGame);
		when(rentalDAO.save(testRental.get())).thenReturn(testRental.get());
		when(gameDAO.save(testGame.get())).thenReturn(testGame.get());
		
		assertSame(testRental.get(), rentalService.insert(1, 1));
		
		verify(userDAO, times(1)).findById(testUser.get().getId());
		verify(gameDAO, times(1)).findById(testGame.get().getId());
		verify(rentalDAO, times(1)).save(testRental.get());
		verify(gameDAO, times(1)).save(testGame.get());
	}
	
	@Test
	public void testFailedInsert() {
		System.out.println("Testing failed insert()");
		
		assertNull(rentalService.insert(testUser.get().getId(), testGame.get().getId()));
	
		verify(userDAO, times(1)).findById(testUser.get().getId());
		verify(gameDAO, times(1)).findById(testGame.get().getId());
	}
	
	@Test
	public void testDeleteById() {
		System.out.println("Testing deleteById()");
		
		doNothing().when(rentalDAO).deleteById(testId);
		when(gameDAO.findById(testId)).thenReturn(testGame);
		when(gameDAO.save(testGame.get())).thenReturn(testGame.get());
		
		assertTrue(rentalService.deleteById(testId));
		
		verify(rentalDAO, times(1)).deleteById(testId);
		verify(gameDAO, times(1)).findById(testId);
		verify(gameDAO, times(1)).save(testGame.get());
	}
	
	@Test
	public void testFailedDeleteById1() {
		System.out.println("Testing first failed deleteById()");
		
		doNothing().when(rentalDAO).deleteById(testId);
		
		assertFalse(rentalService.deleteById(testId));
		
		verify(rentalDAO, times(1)).deleteById(testId);
		verify(gameDAO, times(1)).findById(testId);
	}
	
	@Test
	public void testFailedDeleteById2() {
		System.out.println("Testing second failed deleteById()");
		
		doThrow(IllegalArgumentException.class).when(rentalDAO).deleteById(testId);
		
		assertFalse(rentalService.deleteById(testId));
		
		verify(rentalDAO, times(1)).deleteById(testId);
	}
	
	@Test
	public void testToggleOverdue() {
		System.out.println("Testing toggleOverdue()");
		
		when(rentalDAO.findById(testId)).thenReturn(testRental);
		
		assertSame(testRental.get().getGame(), rentalService.toggleOverdue(testId).getGame());
		assertSame(testRental.get().getUser(), rentalService.toggleOverdue(testId).getUser());
		assertEquals(testRental.get().getDueDate(), rentalService.toggleOverdue(testId).getDueDate());
		assertNotEquals(testRental.get().isOverDue(), rentalService.toggleOverdue(testId).isOverDue());

		verify(rentalDAO, times(4)).findById(testId);
		verify(rentalDAO, times(4)).save(testRental.get());
	}
	
	@Test
	public void testFailedToggleOverdue() {
		System.out.println("Testing toggleOverdue() when rental doesn't exist");
		
		assertNull(rentalService.toggleOverdue(g.getId()));
		
		verify(rentalDAO, times(1)).findById(g.getId());
	}
	
	@Test
	public void testChangeDate() {
		System.out.println("Testing changeDate()");
		
		when(rentalDAO.findById(testGame.get().getId())).thenReturn(testRental);
		
		assertEquals(LocalDate.of(1991, 6, 23), rentalService.changeDate(testGame.get().getId(), LocalDate.of(1991, 6, 23)).getDueDate());
		
		verify(rentalDAO, times(1)).findById(testGame.get().getId());
		verify(rentalDAO, times(1)).save(testRental.get());
	}
}
