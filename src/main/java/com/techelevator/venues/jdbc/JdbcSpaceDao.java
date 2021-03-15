package com.techelevator.venues.jdbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.venues.Category;
import com.techelevator.venues.Space;
import com.techelevator.venues.SpaceDao;
import com.techelevator.venues.Venue;

public class JdbcSpaceDao implements SpaceDao{
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcSpaceDao(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Space> getAllSpacesByVenueId(long venueId) {
		List<Space> spaces = new ArrayList<Space>();
		String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate::numeric, max_occupancy " + 
				"FROM space " + 
				"WHERE venue_id = ?";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, venueId);
		while(rows.next()) {
			spaces.add(mapToSpaceRow(rows));
		}
		return spaces;
	}

	@Override
	public Space getSpaceBySpaceId(long spaceId) {
		Space space = null;
		String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate::numeric, max_occupancy " + 
				"FROM space " + 
				"WHERE id = ?";
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, spaceId);
		if (row.next()) {
			space = mapToSpaceRow(row);
		}
		return space;
	}

	@Override
	public List<Space> getTopFiveSpaces(long venueId) {
		List<Space> spaces = new ArrayList<Space>();
		String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate::numeric, max_occupancy " + 
				"FROM space " + 
				"WHERE venue_id = ? " + 
				"LIMIT 5";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, venueId);
		while(rows.next()) {
			spaces.add(mapToSpaceRow(rows));
		}
		return spaces;
	}
	
	@Override
	public List<Space> getAllAvailableSpaces(int numOfPeople, int month, long venueId) {
		List<Space> spaces = new ArrayList<Space>();
		String sql = "SELECT id, venue_id, name, is_accessible, open_from, open_to, daily_rate::numeric, max_occupancy " + 
				"FROM space " + 
				"WHERE venue_id = ? AND max_occupancy >= ? " + 
				"AND (? BETWEEN open_from and open_to OR open_from IS NULL OR open_to IS NULL)";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, venueId, numOfPeople, month);
		while(rows.next()) {
			spaces.add(mapToSpaceRow(rows));
		}
		return spaces;
	}
	
	private Space mapToSpaceRow(SqlRowSet row) {
		Space space = new Space();
		space.setSpaceId(row.getLong("id"));
		space.setVenueId(row.getLong("venue_id"));
		space.setName(row.getString("name"));
		space.setAccessible(row.getBoolean("is_accessible"));
		space.setOpenFrom(row.getInt("open_from"));
		space.setOpenTo(row.getInt("open_to"));
		space.setDailyRate(row.getBigDecimal("daily_rate"));
		space.setMaxOccupancy(row.getInt("max_occupancy"));
		return space;
	}


//	@Override
//	public boolean isNumOfPeopleInRange(int numOfPeople) {
//		String sql = "SELECT count(*) > 0 as num_of_people_not_in_range " + 
//				"FROM space\n" + 
//				"WHERE max_occupancy >= ?";
//		SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
//		row.next();
//		boolean isNumOfPeopleInRange = row.getBoolean("num_of_people_not_in_range");
//		return isNumOfPeopleInRange;
//	}
//	
//	@Override
//	public boolean isSpaceClosed(LocalDate startDate) {
//		int month = startDate.getMonthValue();
//		String sql = "SELECT count(*) = 0 as space_closed" + 
//				"FROM space" + 
//				"WHERE (? BETWEEN open_from and open_to OR open_from IS NULL OR open_to IS NULL) " + 
//				"GROUP BY id";
//		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, month);
//		row.next();
//		boolean isClosed = row.getBoolean("space_closed");
//		return isClosed;
//	}

}
