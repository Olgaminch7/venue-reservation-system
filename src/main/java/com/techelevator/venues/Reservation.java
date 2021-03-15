package com.techelevator.venues;
import java.time.LocalDate;

public class Reservation {
	
	private long reservationId;
	private long spaceId;
	private int numberOfAttendees;
	private LocalDate startDate;
	private LocalDate endDate;
	private String reservedFor;
	private Venue venue;
	private Space space;
	
	public Reservation(Venue venue, Space space) {
		this.setVenue(venue);
		this.setSpace(space);
	}
	
	public Reservation() {
	}

	public long getReservationId() {
		return reservationId;
	}
	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}
	public int getNumberOfAttendees() {
		return numberOfAttendees;
	}
	public void setNumberOfAttendees(int numberOfAttendees) {
		this.numberOfAttendees = numberOfAttendees;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public String getReservedFor() {
		return reservedFor;
	}
	public void setReservedFor(String reservedFor) {
		this.reservedFor = reservedFor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result + numberOfAttendees;
		result = prime * result + (int) (reservationId ^ (reservationId >>> 32));
		result = prime * result + ((reservedFor == null) ? 0 : reservedFor.hashCode());
		result = prime * result + (int) (spaceId ^ (spaceId >>> 32));
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reservation other = (Reservation) obj;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (numberOfAttendees != other.numberOfAttendees)
			return false;
		if (reservationId != other.reservationId)
			return false;
		if (reservedFor == null) {
			if (other.reservedFor != null)
				return false;
		} else if (!reservedFor.equals(other.reservedFor))
			return false;
		if (spaceId != other.spaceId)
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}
	
	

}
