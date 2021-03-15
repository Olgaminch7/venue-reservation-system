package com.techelevator.venues.jdbc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.venues.Reservation;
import com.techelevator.venues.ReservationDao;
import com.techelevator.venues.Space;
import com.techelevator.venues.Venue;

public class JdbcReservationDao implements ReservationDao {
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcReservationDao(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public boolean isReservationAllowed(long spaceId, LocalDate startDate, int numOfDays) throws ParseException {
		boolean isReservationAllowed = false;
		String endDateString = getEndDate(startDate, numOfDays);
		LocalDate endDate = LocalDate.parse(endDateString);
		String sql = "SELECT count(*) = 0 as allow_reservation " + 
				"FROM reservation " + 
				"WHERE space_id = ? AND (start_date, end_date) overlaps (?, ?)";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, spaceId, startDate, endDate);
		if (row.next()) {
			isReservationAllowed = row.getBoolean("allow_reservation");
		}
		return isReservationAllowed;
	}

	@Override
	public Reservation placeReservation(Reservation newReservation) {
		String sql = "INSERT INTO reservation (reservation_id, space_id, number_of_attendees, start_date, " +
					"end_date, reserved_for) VALUES (DEFAULT, ?, ?, ?, ?, ?) RETURNING reservation_id";
	//	long reservationId = getNextReservationId();
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, newReservation.getSpaceId(), newReservation.getNumberOfAttendees(), 
					newReservation.getStartDate(), newReservation.getEndDate(), newReservation.getReservedFor());
		if (row.next()) {
			newReservation.setReservationId(row.getLong("reservation_id"));
		}
		return newReservation;
	}
	
	@Override
	public Reservation getReservationById(long reservationId) {
		Reservation reservation = null;
		String sql = "SELECT reservation_id, space_id, number_of_attendees, start_date, end_date, reserved_for " + 
				"FROM reservation " + 
				"WHERE reservation_id = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, reservationId);
		row.next();
		reservation = mapRowToReservation(row);
		return reservation;
	}
	
	@Override
	public List<Reservation> getReservationsForTheLastThirtyDays() {
		List<Reservation> reservations = new ArrayList<>();
		String sql = "SELECT reservation_id, space_id, venue.id as venue_id, venue.name as venue_name, space.name as space_name, reserved_for, start_date, end_date " + 
				"FROM reservation " + 
				"JOIN space ON space.id = reservation.space_id " + 
				"JOIN venue ON venue.id = space.id " + 
				"WHERE start_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 30 " + 
				"ORDER BY start_date ASC";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		while(rows.next()) {
			reservations.add(mapReservationThirtyDays(rows));
		}
		return reservations;
	}	
	
	public String getEndDate(LocalDate startDate, int numOfDays) throws ParseException {
		String endDate = startDate.toString();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calender = Calendar.getInstance();
		calender.setTime(dateFormat.parse(endDate));
		calender.add(Calendar.DATE, numOfDays);
		endDate = dateFormat.format(calender.getTime());
		return endDate;
	}
	
//	private long getNextReservationId() {
//		SqlRowSet nextIdResult = jdbcTemplate.queryForRowSet("SELECT nextval('reservation_reservation_id_seq')");
//		if (nextIdResult.next()) {
//			return nextIdResult.getLong(1);
//		} else {
//			throw new RuntimeException("Something went wrong while getting an id for the new reservation");
//		}
//	}
	
	private Reservation mapRowToReservation(SqlRowSet row) {
		Reservation reservation = new Reservation();
		reservation.setReservationId(row.getLong("reservation_id"));
		reservation.setSpaceId(row.getLong("space_id"));
		reservation.setNumberOfAttendees(row.getInt("number_of_attendees"));
		reservation.setStartDate(row.getDate("start_date").toLocalDate());
		reservation.setEndDate(row.getDate("end_date").toLocalDate());
		reservation.setReservedFor(row.getString("reserved_for"));
		return reservation;
	
	}
	
	private Reservation mapReservationThirtyDays(SqlRowSet row) {
		Venue venue = new Venue();
		Space space = new Space();
		Reservation reservation = new Reservation(venue, space);
		reservation.setReservationId(row.getLong("reservation_id"));
		reservation.setSpaceId(row.getLong("space_id"));
		venue.setVenueId(row.getLong("venue_id"));
		venue.setName(row.getString("venue_name"));
		space.setName(row.getString("space_name"));
		reservation.setReservedFor(row.getString("reserved_for"));
		reservation.setStartDate(row.getDate("start_date").toLocalDate());
		reservation.setEndDate(row.getDate("end_date").toLocalDate());
		return reservation;
	}

}
