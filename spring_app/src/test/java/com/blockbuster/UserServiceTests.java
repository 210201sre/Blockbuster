package com.blockbuster;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.blockbuster.models.User;
import com.blockbuster.exceptions.PasswordMismatchException;
import com.blockbuster.models.ROLE;
import com.blockbuster.models.STATES;
import com.blockbuster.repositories.UserDAO;
import com.blockbuster.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTests {
	
	@InjectMocks
	UserService userService;
	
	@Mock
	UserDAO userDAO;
	
	@Mock
	User u;
	
	@Mock
	HttpServletRequest request;
	
	@Mock
	HttpSession session;
	
	static Optional<User> testUser;
	
	@BeforeClass
	public static void setUpAll() {
		testUser = Optional.ofNullable(
				new User(1, "First", "password", LocalDate.of(1983, 1, 13), 
						"123 Some St.", "City", STATES.MA, 12345, Collections.emptySet(), ROLE.CUSTOMER)
		);
	}
	
	@AfterClass 
	public static void tearDownAll() {
		testUser = null;
	}
	
	@Test
	public void testFindAll() {
		System.out.println("Testing findAll()");
		
		List<User> result = new ArrayList<User>();
		result.add(testUser.get());
		result.add(new User(2, "Second", "password", LocalDate.of(1991, 6, 23), "234 Some St.", "Town", STATES.PA, 23456, Collections.emptySet(), ROLE.CUSTOMER));
		result.add(new User(3, "Third", "password", LocalDate.of(2001, 5, 17), "345 Some St.", "Suburb", STATES.CA, 34567, Collections.emptySet(), ROLE.CUSTOMER));
		result.add(new User(4, "Fourth", "password", LocalDate.of(2017, 11, 4), "456 Some St.", "Rural", STATES.LA, 45678, Collections.emptySet(), ROLE.CUSTOMER));
		
		when(userDAO.findAll()).thenReturn(result);
		
		assertEquals(4, userService.findAll().size());
		
		verify(userDAO, times(1)).findAll();
	}
	
	@Test
	public void testInsert() {
		System.out.println("Testing insert()");

		when(userDAO.save(u)).thenReturn(u);
		
		assertSame(u, userService.insert(u));
		verify(userDAO, times(1)).save(u);
	}
	
	@Test
	public void testGetRentals() {
		System.out.println("Testing getRentals()");
		
		when(userDAO.findByUsername("First")).thenReturn(testUser);
		
		assertSame(testUser.get(), userService.findByUsername("First"));
		assertEquals(0, userService.getRentals("First").size());
		verify(userDAO, times(2)).findByUsername("First");
	}
	
	@Test
	public void testFindByUsername() {
		System.out.println("Testing findByUsername()");
		
		when(userDAO.findByUsername("First")).thenReturn(testUser);
		assertSame(testUser.get(), userService.findByUsername("First"));
		verify(userDAO, times(1)).findByUsername("First");
	}
	
	@Test
	public void testFindByIdDeleteById() {
		System.out.println("Testing findById()");																																																																					Optional<User> testUser = Optional.ofNullable(new User(1, "First", "password", LocalDate.of(1983, 1, 13), "123 Some St.", "City", STATES.MA, 12345, Collections.emptySet(), ROLE.CUSTOMER));
		
		when(userDAO.findById(1)).thenReturn(testUser);
		
		assertSame(testUser.get(), userService.findById(1));
		verify(userDAO, times(1)).findById(1);
	}
	
	@Test
	public void testDeleteById() {
		System.out.println("Testing deleteById()");
		
		when(userDAO.findById(1)).thenReturn(testUser);
		doNothing().when(userDAO).deleteById(1);
		
		assertSame(testUser.get(), userService.findById(1));
		assertTrue(userService.deleteById(1));
		
		verify(userDAO, times(2)).findById(1);
		verify(userDAO, times(1)).deleteById(1);
	}
	
	@Test
	public void testLogin() {
		System.out.println("Testing login()");
		
		when(userDAO.findByUsername("First")).thenReturn(testUser);
		when(request.getSession()).thenReturn(session);
		
		assertSame(testUser.get(), userService.login("First", "password"));
		
		verify(userDAO, times(1)).findByUsername("First");
		verify(request, times(1)).getSession();
	}
	
	@Test(expected = PasswordMismatchException.class)
	public void testFailedLogin() {
		System.out.println("Testing failed login()");
		
		when(userDAO.findByUsername("First")).thenReturn(testUser);
		
		userService.login("First", "fail");
		
		verify(userDAO, times(1)).findByUsername("First");
	}
	
	@Test
	public void testLogout() {
		System.out.println("Testing logout()");
		
		when(request.getSession(false)).thenReturn(session);
		
		userService.logout();
		
		verify(session, times(1)).invalidate();
	}
}
