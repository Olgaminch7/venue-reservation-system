package com.techelevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.venues.City;
import com.techelevator.venues.State;
import com.techelevator.venues.Venue;
import com.techelevator.venues.jdbc.JdbcVenueDao;

public class JdbcVenueDaoIntegrationTest extends DAOIntegrationTest{
	
	private City city;
	private State state;
	
	private JdbcVenueDao venueDao;
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup() {
		venueDao = new JdbcVenueDao(getDataSource());
		jdbcTemplate = new JdbcTemplate(getDataSource());
		state = createTestState();
		city = createTestCity();
	}
	
	@Test
	public void testing_get_all_venues() {
		List<Venue> originalList = venueDao.getAllVenues();
		int originalCount = originalList.size();
		createTestVenue();
		List<Venue> afterInserts = venueDao.getAllVenues();
		assertEquals(originalCount + 1, afterInserts.size());	
	}
	
	@Test
	public void testing_get_venue_details() {
		Venue testVenue = createTestVenue();
		Venue venueFromDatabase = venueDao.getVenueDetails(testVenue.getVenueId());
		assertNotNull(venueFromDatabase);
		assertEquals(testVenue, venueFromDatabase);
	}
	
	private Venue createTestVenue() {
		Venue venue = new Venue(city, state);
		venue.setName("TestName");
		venue.setDescription("TestDescription");
		String sql = "INSERT INTO venue (id, name, city_id, description) VALUES (DEFAULT, ?, ?, ?) RETURNING id";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, venue.getName(), city.getCityId(), venue.getDescription());
		rows.next();
		venue.setVenueId(rows.getLong("id"));
		return venue;
	}
		
	private City createTestCity() {
		City city = new City();
		city.setName("TestCity");
		city.setStateAbbreviation(state.getAbbreviation());
		String sqlCity = "INSERT INTO city (id, name, state_abbreviation) VALUES (DEFAULT, ?, ?) RETURNING id";
		SqlRowSet rowCity = jdbcTemplate.queryForRowSet(sqlCity, city.getName(), city.getStateAbbreviation());
		rowCity.next();
		city.setCityId(rowCity.getLong("id"));
		return city;
	}

	private State createTestState() {
		State state = new State();
		state.setAbbreviation("XZ");
		state.setName("TestState");
		String sqlState = "INSERT INTO state (abbreviation, name) VALUES (?, ?) RETURNING abbreviation";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sqlState, state.getAbbreviation(), state.getName());
		rows.next();
		return state;
	}
	
	

}
