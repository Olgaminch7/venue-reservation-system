package com.techelevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.venues.Space;
import com.techelevator.venues.jdbc.JdbcSpaceDao;



public class JdbcSpaceDaoIntegrationTesting extends DAOIntegrationTest {
	
	private long cityId;
	private long venueId;
	
	private JdbcSpaceDao spaceDao;
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup() {
		spaceDao = new JdbcSpaceDao(getDataSource());
		jdbcTemplate = new JdbcTemplate(getDataSource());
		String sqlCity = "INSERT INTO city (id, name, state_abbreviation) VALUES (DEFAULT, 'TestCity', 'OH') RETURNING id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sqlCity);
		row.next();
		cityId = row.getLong("id");
		String sql = "INSERT INTO venue (id, name, city_id, description) VALUES (DEFAULT, 'TestName', ?, 'TestDescription') RETURNING id";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql, this.cityId);
		result.next();
		venueId = result.getLong("id");
	}
	
	@Test
	public void get_all_spaces_by_venue_id() {
		List<Space> originalSpaces = spaceDao.getAllSpacesByVenueId(this.venueId);
		createTestSpace();
		createTestSpace();
		List<Space> actualSpaces = spaceDao.getAllSpacesByVenueId(this.venueId);
		assertEquals(originalSpaces.size() + 2, actualSpaces.size());;
	}
	
	@Test
	public void get_space_by_venue_id() {
		Space testSpace = createTestSpace();
		Space expectedSpace = spaceDao.getSpaceBySpaceId(testSpace.getSpaceId());
		assertSpacesEquals(testSpace, expectedSpace);
	}
	
	@Test
	public void get_top_five_spaces() {
		createTestSpace();
		createTestSpace();
		createTestSpace();
		createTestSpace();
		createTestSpace();
		createTestSpace();
		List<Space> testSpaces = spaceDao.getTopFiveSpaces(this.venueId);
		assertTrue(testSpaces.size() == 5);
	}
	
	private void assertSpacesEquals(Space expected, Space actual) {
		assertEquals(expected.getSpaceId(), actual.getSpaceId());
		assertEquals(expected.getVenueId(), actual.getVenueId());
		assertEquals(expected.getName(), actual.getName());
		assertEquals(expected.isAccessible(), actual.isAccessible());
		assertEquals(expected.getOpenFrom(), actual.getOpenFrom());
		assertEquals(expected.getOpenTo(), actual.getOpenTo());
		assertEquals(expected.getDailyRate().stripTrailingZeros(), actual.getDailyRate().stripTrailingZeros());
		assertEquals(expected.getMaxOccupancy(), actual.getMaxOccupancy());
	}
	
	private Space createTestSpace() {
		Space space = new Space();
		space.setName("TestName");
		space.setVenueId(this.venueId);
		space.setAccessible(false);
		space.setOpenFrom(2);
		space.setOpenTo(8);
		BigDecimal dailyRate = new BigDecimal(250);
		space.setDailyRate(dailyRate);
		space.setMaxOccupancy(100);
		String sql = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, open_to , " +
					"daily_rate, max_occupancy) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, space.getVenueId(), space.getName(), space.isAccessible(), space.getOpenFrom(), 
					space.getOpenTo(), space.getDailyRate(), space.getMaxOccupancy());
		rows.next();
		space.setSpaceId(rows.getLong("id"));
		return space;
	}
	
	

}
