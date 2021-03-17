package com.blockbuster;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
import com.blockbuster.models.Review;
import com.blockbuster.repositories.ReviewDAO;
import com.blockbuster.services.ReviewService;

@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceTests {
	@InjectMocks
	ReviewService reviewService;

	@Mock
	ReviewDAO reviewDAO;
	
	@Mock
	Game g;
	
	@Mock
	Review r;
	
	private static final int testId = 1;
	private static Optional<Review> testReview;
	private static Game testGame;
	
	@BeforeClass
	public static void setUpAll() {
		testGame = new Game(1, "First", GENRE.ACTION_ADVENTURE, CONSOLES.SNES, "Publisher", "Developer", LocalDate.of(1983, 1, 13));
		testReview = Optional.ofNullable(
				new Review(1, testGame, 10, "First")
		);
	}
	
	@AfterClass
	public static void tearDownAll() {
		testReview = null;
	}
	
	@Test
	public void testFindAll() {
		List<Review> result = new ArrayList<Review>();
		result.add(new Review(1, g, 10, "First review"));
		result.add(new Review(2, g, 6, "Second review"));
	
		when(reviewDAO.findAll()).thenReturn(result);
		
		assertEquals(2, reviewService.findAll().size());
		
		verify(reviewDAO, times(1)).findAll();
	}
	
	@Test
	public void testInsert() {
		when(reviewDAO.save(r)).thenReturn(r);
		
		assertSame(r, reviewService.insert(r));
		
		verify(reviewDAO, times(1)).save(r);
	}
	
	@Test
	public void testDeleteById() {
		doNothing().when(reviewDAO).deleteById(testId);
		
		assertTrue(reviewService.deleteById(testId));
		
		verify(reviewDAO, times(1)).deleteById(testId);
	}
	
	@Test
	public void testFailedDeleteById() {
		doThrow(IllegalArgumentException.class).when(reviewDAO).deleteById(testId);
		
		assertFalse(reviewService.deleteById(testId));
		
		verify(reviewDAO, times(1)).deleteById(testId);
	}
	
	@Test
	public void testGetAverageRating() {
		double testResult = 7.7;
		
		when(reviewDAO.getAverageRating(testId)).thenReturn(testResult);
		
		assertEquals(testResult, reviewService.getAverageRating(testId));
		
		verify(reviewDAO, times(1)).getAverageRating(testId);
	}
	
	@Test
	public void testFindById() {
		when(reviewDAO.findById(r.getId())).thenReturn(testReview);
		
		assertSame(testReview.get(), reviewService.findById(r.getId()));
		
		verify(reviewDAO, times(1)).findById(r.getId());
	}
	
	@Test
	public void testFailFindById() {
		assertNull(reviewService.findById(r.getId()));
		
		verify(reviewDAO, times(1)).findById(r.getId());
	}
}
