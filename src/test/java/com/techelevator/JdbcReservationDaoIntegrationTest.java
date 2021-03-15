package com.techelevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.venues.Reservation;
import com.techelevator.venues.Space;
import com.techelevator.venues.jdbc.JdbcReservationDao;

public class JdbcReservationDaoIntegrationTest extends DAOIntegrationTest{
	
	private long cityId;
	private long venueId;
	private long spaceId;
	
	private JdbcReservationDao reservationDao;
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup() {
		reservationDao = new JdbcReservationDao(getDataSource());
		jdbcTemplate = new JdbcTemplate(getDataSource());
		
		String sqlCity = "INSERT INTO city (id, name, state_abbreviation) VALUES (DEFAULT, 'TestName', 'OH') RETURNING id";
		SqlRowSet rowCity = jdbcTemplate.queryForRowSet(sqlCity);
		rowCity.next();
		cityId = rowCity.getLong("id");
		
		String sqlVenue = "INSERT INTO venue (id, name, city_id, description) VALUES (DEFAULT, 'TestName', ?, 'TestDescription') RETURNING id";
		SqlRowSet rowVenue = jdbcTemplate.queryForRowSet(sqlVenue, this.cityId);
		rowVenue.next();
		venueId = rowVenue.getLong("id");
		
		createTestSpace();

	}
	
	@Test
	public void is_reservation_allowed() throws ParseException {
		createTestReservation();
		assertFalse(reservationDao.isReservationAllowed(this.spaceId, LocalDate.parse("2021-03-12"), 2));
	}
	
	@Test
	public void place_reservation() {
		Reservation testReservation = createTestReservation();
		reservationDao.placeReservation(testReservation);
		
	}
	
	@Test
	public void get_reservation_by_id() {
		Reservation testReservation = createTestReservation();
		Reservation actualReservation = reservationDao.getReservationById(testReservation.getReservationId());
		assertEquals(testReservation, actualReservation);
	}
	
	private Reservation createTestReservation() {
		Reservation testReservation = new Reservation();
		testReservation.setSpaceId(this.spaceId);
		testReservation.setNumberOfAttendees(100);
		testReservation.setStartDate(LocalDate.parse("2021-02-12"));
		testReservation.setEndDate(LocalDate.parse("2021-06-12"));
		testReservation.setReservedFor("TestName");
		String sql = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, " +
					"start_date, end_date, reserved_for) VALUES (DEFAULT, ?, ?, ?, ?, ?) RETURNING reservation_id";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, testReservation.getSpaceId(), testReservation.getNumberOfAttendees(),
					testReservation.getStartDate(), testReservation.getEndDate(), testReservation.getReservedFor());
		row.next();
		testReservation.setReservationId(row.getLong("reservation_id"));
		return testReservation;
	}
	private Space createTestSpace() {
		Space space = new Space();
		space.setVenueId(this.venueId);
		space.setName("TestSpace");
		space.setAccessible(false);
		space.setOpenFrom(2);
		space.setOpenTo(8);
		BigDecimal dailyRate = new BigDecimal(250);
		space.setDailyRate(dailyRate);
		space.setMaxOccupancy(100);
		String sqlSpace = "INSERT INTO space (id, venue_id, name, is_accessible, open_from, open_to , " +
				"daily_rate, max_occupancy) VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
		SqlRowSet rowSpace = jdbcTemplate.queryForRowSet(sqlSpace, space.getVenueId(), space.getName(), 
						space.isAccessible(), space.getOpenFrom(), space.getOpenTo(), space.getDailyRate(), space.getMaxOccupancy());
		rowSpace.next();
		spaceId = rowSpace.getLong("id");
		space.setSpaceId(spaceId);
		return space;
	}
	

}
