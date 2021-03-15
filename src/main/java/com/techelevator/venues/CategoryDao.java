package com.techelevator.venues;

import java.util.List;
import java.util.Map;
import com.techelevator.venues.Category;

public interface CategoryDao {
	
	/**
	 * Gets all available categories for a Venue Id from the datastore returns a List 
	 * 
	 * @param id is the venue id
	 * @return all the categories as Category objects in a List
	 */	
	
	List<Category> getAllCategoriesByVenueId(long venueId);

}
