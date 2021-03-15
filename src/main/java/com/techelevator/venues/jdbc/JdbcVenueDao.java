package com.techelevator.venues.jdbc;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.venues.City;
import com.techelevator.venues.State;
import com.techelevator.venues.Venue;
import com.techelevator.venues.VenueDao;

public class JdbcVenueDao implements VenueDao{
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcVenueDao(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Venue> getAllVenues() {
		List<Venue> venues = new ArrayList<Venue>();
		String sql = "SELECT venue.id, venue.name, venue.city_id as city_id, city.name as city_name, state.abbreviation, state.name as state_name, venue.description " + 
				"FROM venue " + 
				"JOIN city ON city.id = venue.city_id " + 
				"JOIN state ON state.abbreviation = city.state_abbreviation " +
				"ORDER BY venue.name ASC";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		while(rows.next()) {
			venues.add(mapRowToVenue(rows));
		}
		return venues;
	}

	@Override
	public Venue getVenueDetails(long venueId) throws NullPointerException {
		Venue venue = null;
		String sql = "SELECT venue.id, venue.name, venue.city_id as city_id, city.name as city_name, state.abbreviation, state.name as state_name, venue.description " + 
				"FROM venue " + 
				"JOIN city ON city.id = venue.city_id " + 
				"JOIN state ON state.abbreviation = city.state_abbreviation " +
				"WHERE venue.id = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, venueId);
		if (row.next()) {
			venue = mapRowToVenue(row);
		}
		return venue;
	}
	
	private Venue mapRowToVenue(SqlRowSet row) {
		City city = new City();
		State state = new State();
		Venue venue = new Venue(city, state);
		venue.setVenueId(row.getLong("id"));
		venue.setName(row.getString("name"));
		city.setCityId(row.getLong("city_id"));
		city.setName(row.getString("city_name"));
		state.setAbbreviation(row.getString("abbreviation"));
		state.setName(row.getString("state_name"));
		venue.setDescription(row.getString("description"));
		return venue;
	}

}
