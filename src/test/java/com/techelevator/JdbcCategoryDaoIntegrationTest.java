package com.techelevator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import com.techelevator.venues.Category;
import com.techelevator.venues.jdbc.JdbcCategoryDao;

public class JdbcCategoryDaoIntegrationTest extends DAOIntegrationTest {
	
	private long cityId;
	private long venueId;
	
	private JdbcCategoryDao categoryDao;
	private JdbcTemplate jdbcTemplate;
	
	@Before
	public void setup() {
		categoryDao = new JdbcCategoryDao(getDataSource());
		jdbcTemplate = new JdbcTemplate(getDataSource());
		
		//need to create venue_id so we would be able to insert venue_id in category_venue table to test SELECT read() method
		String sqlCity = "INSERT INTO city (id, name, state_abbreviation) VALUES (DEFAULT, 'TestName', 'OH') RETURNING id";
		SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlCity);
		rowSet.next();
		cityId = rowSet.getLong("id");
		String sql = "INSERT INTO venue (id, name, city_id, description) VALUES (DEFAULT, 'TestVenue', ?, 'TestDescription') RETURNING id";
		SqlRowSet result = jdbcTemplate.queryForRowSet(sql, this.cityId);
		result.next();
		venueId = result.getLong("id");
	}
	
	@Test
	public void testing_get_all_categories_by_venue_id() {
		truncateCategoryVenueTable();
		Category category = saveCategory();
		Category secondCategory = saveCategory();
		List<Category> testCategories = new ArrayList<>();
		testCategories.add(category);
		testCategories.add(secondCategory);
		String sql = "INSERT INTO category_venue (venue_id, category_id) VALUES (?, ?)";
		jdbcTemplate.update(sql, this.venueId, category.getCategoryId());
		jdbcTemplate.update(sql, this.venueId, secondCategory.getCategoryId());
		List<Category> actualResult = categoryDao.getAllCategoriesByVenueId(this.venueId);
		assertEquals(testCategories.size(), actualResult.size());
	}
	
	private Category saveCategory() {
		Category category = new Category();
		String sql = "INSERT INTO category (id, name) VALUES (DEFAULT, ?) RETURNING id";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, category.getName());
		rows.next();
		category.setCategoryId(rows.getLong("id"));
		category.setName("TestName");
		return category;
	}	
	
	private void truncateCategoryVenueTable() {
		String sql = "TRUNCATE category_venue CASCADE";
		jdbcTemplate.update(sql);
	}

}
