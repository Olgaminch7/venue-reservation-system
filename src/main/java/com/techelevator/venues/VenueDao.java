package com.techelevator.venues;

import java.util.List;

public interface VenueDao {
	
	/**
	 * Gets all venues from the datastore and returns them in a List
	 * 
	 * @return all the venues as Venue objects in a List
	 */	
	
	public List<Venue> getAllVenues();

	/**
	 * Gets one venue from the datastore by its id and returns a Venue Object
	 * 
	 * @param id is the venue id
	 * @return all the venues as Venue objects in a List
	 */	
	
	public Venue getVenueDetails(long venueId);
}
