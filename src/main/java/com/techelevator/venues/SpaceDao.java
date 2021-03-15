package com.techelevator.venues;
import java.math.BigDecimal;
import java.util.List;

public interface SpaceDao {
	
	/**
	 * Gets all spaces for a venue from the datastore and returns them in a List
	 * 
	 * @param id is the id for the Venue
	 * @return all the spaces as Space objects in a List
	 */	
	
	public List<Space> getAllSpacesByVenueId(long venueId);
	
	
	/**
	 * Gets One space from the datastore based on a spaceId
	 * 
	 * @param id is the Id for the Space
	 * @return space as a Space object
	 */
	
	public Space getSpaceBySpaceId(long spaceId);
	
	/*
	 * Get List of availabale Spaces based on boolean isSpaceClosed, isNumOfPeople in range
	 * 
	 * @param boolean isReservation allowed, 
	 */
	
	public List<Space> getAllAvailableSpaces(int numOfPeople, int month, long venueId);
	
	
	/**
	 * Gets TOP 5 Spaces from the datastore and returns them in a List
	 * 
	 * need to calculate endDate
	 * @param id is the Id for the Venue
	 * @return all the spaces as Space objects in a List
	 */	
	
	public List<Space> getTopFiveSpaces(long venueId);

//	/*
//	 * Returns true if numOfPeople is in range
//	 * 
//	 * @param numOfPeople
//	 * @return boolean isNumOfPeopleInRange;
//	 */
//	
//	public boolean isNumOfPeopleInRange(int numOfPeople);
//	
//	/**
//	 * Returns true if Space is open 
//	 * 
//	 * @param startDate for Reservation from user input
//	 * @return boolean isSpaceOpen
//	 * 
//	 */	
//	
//	public boolean isSpaceClosed(LocalDate startDate);
	
	
	
	
	
	
	
}
