package com.techelevator.venues.jdbc;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.venues.Category;
import com.techelevator.venues.CategoryDao;

public class JdbcCategoryDao implements CategoryDao {
	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcCategoryDao(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Category> getAllCategoriesByVenueId(long venueId) {
		List<Category> categories = new ArrayList<>();
		String sql = "SELECT category_venue.venue_id, category_venue.category_id, category.name FROM category " + 
				"JOIN category_venue ON category_venue.category_id = category.id " + 
				"WHERE category_venue.venue_id = ?";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, venueId);
		while(rows.next()) {
			categories.add(mapRowToCategory(rows));
		}
		return categories;
	}
	
	private Category mapRowToCategory(SqlRowSet row) {
		Category category = new Category();
		category.setCategoryId(row.getLong("category_id"));
		category.setName(row.getString("name"));
		return category;
	}


}
