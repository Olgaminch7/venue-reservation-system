package com.techelevator.venues;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

public interface ReservationDao {
	
	/**
	 * Gets all Reservations for a space from the datastore and returns boolean false
	 * 
	 * @param id is the Id for the Space
	 * @return all available spaces as Space objects in a List
	 * 
	 * need to convert Integer numOfDays to endDate
	 * @throws ParseException 
	 */	
	
	public boolean isReservationAllowed(long spaceId, LocalDate startDate, int numOfDays) throws ParseException;
		
	
	/**
	 * Insert (Place) a new reservation
	 * 
	 * @param is a space by Id to insert into reservation
	 * @param is a new Reservation with new Reservation Id
	 */	
	public Reservation placeReservation(Reservation newReservation);
	
	/**
	 * Gets a Reservation for a space from the datastore and returns Reservation
	 * 
	 * @param id is the Id for the Reservation
	 * @return Reservation
	 * 
	 */	
	
	public Reservation getReservationById(long reservationId);
	
	/**
	 * Gets reservation for last 30 days
	 * @return List with Reservation objects
	 */
	
	public List<Reservation> getReservationsForTheLastThirtyDays();
	
	
	/**
	 * Gets end date based on start date and numOfDays user input
	 * 
	 * @param startDate user Input
	 * @param numOfDays user Input
	 * @return String endDate
	 * @throws ParseException
	 */
	
	public String getEndDate(LocalDate startDate, int numOfDays) throws ParseException;
	

}
