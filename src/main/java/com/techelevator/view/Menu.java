package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import com.techelevator.venues.Category;
import com.techelevator.venues.City;
import com.techelevator.venues.Reservation;
import com.techelevator.venues.Space;
import com.techelevator.venues.State;
import com.techelevator.venues.Venue;

public class Menu {
	
	private PrintWriter out;
	private Scanner in;
	
	public Menu(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output);
		this.in = new Scanner(input);
	}
	
	public void printMessage(String message) {
		out.print(message);
		out.flush();
	}
	
	public void printHeading(String headingText) {
		out.println("\n"+headingText);
		for(int i = 0; i < headingText.length(); i++) {
			out.print("-");
		}
		out.println();
		out.flush();
	}
	
	public void listVenues(List<Venue> venues) {
		if(venues.size() > 0) {
			int index = 1;
			for(Venue venue : venues) {
				System.out.println(index + ") " + venue.getName() + " - " + venue.getVenueId());
				index++;
			}	
		} else {
			out.println("\n*** No results ***");
		}
		out.flush();
	}
	
	public void printVenueDetails(List<Category> categories, Venue venue) {
		City city = venue.getCity();
		State state = venue.getState();
		if(venue.getVenueId() > 0) {
			out.println(venue.getName());
			out.println("Location: " + city.getName() + ", " + state.getName());
			out.print("Category: ");
			if (categories.size() > 0) {
				for (int i = 0; i < categories.size(); i++) {
					out.println(categories.get(i).getName());
					for (int y = 1; y < categories.size(); y++) {
						out.printf("%18s \n", categories.get(y).getName());
					}	
					break;
				}
			} else if (categories.size() <= 0){ 
				out.println("The venue doesn't belong to any category.");
			}
			out.println("");
			out.println(venue.getDescription());
		}
		out.flush();
	}
	
	public void printConfirmation(Reservation reservation, Venue venue, Space space, int numOfDays) {
		out.println("");
		out.print(String.format("%15s %-50s \n", "Confirmation: ", reservation.getReservationId()));
		out.print(String.format("%15s %-50s \n", "Venue: ", venue.getName()));
		out.print(String.format("%15s %-50s \n", "Space: ", space.getName()));
		out.print(String.format("%15s %-50s \n", "Reserved for: ", reservation.getReservedFor()));
		out.print(String.format("%15s %-50s \n", "Attendees: ", reservation.getNumberOfAttendees()));
		out.print(String.format("%15s %-50s \n", "Arrival Date: ", reservation.getStartDate().toString()));
		out.print(String.format("%15s %-50s \n", "Depart Date: ", reservation.getEndDate().toString()));
		out.print(String.format("%15s %-50s \n", "Total Cost: ", "$" + space.getTotalCost(numOfDays)));
		out.flush();
	}
	
	public void printError(String error) {
		out.println(error);
		out.flush();
	}
	
	
	public String getUserInput(String prompt) {
		out.print(prompt + " >>> ");
		out.flush();
		return in.nextLine();
	}
	
	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while(choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		return choice;
	}
	
	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if(selectedOption > 0 && selectedOption <= options.length - 1) {
				choice = options[selectedOption - 1];
			} else if (selectedOption <= 0) {
				out.println("\nPlease enter a valid number range for the menu.");
			}
		} catch(NumberFormatException e) {
			// an error message will be displayed below since choice will be null
		}
		if (userInput.toUpperCase().equals("Q") && options[options.length - 1].toString().startsWith("Q")) {
			choice = "Quit";
		} else if (userInput.toUpperCase().equals("R") && options[options.length - 1].toString().startsWith("R")) {
			choice = "Return to Previous Screen";
		} else if(choice == null) {
			out.println("\n*** "+userInput+" is not a valid option ***\n");
		}
		return choice;
	}
	
	private void displayMenuOptions(Object[] options) {
		out.println();
		for(int i = 0; i < options.length - 1; i++) {
			int optionNum = i+1;
			out.println(optionNum+") " + options[i]);
		}
		String lastItem = (String) options[options.length - 1];
		if(lastItem.contentEquals("Quit")) {
			out.println("Q) " + options[options.length - 1]);
		}else if(lastItem.contentEquals("Return to Previous Screen")) {
			out.println("R) " + options[options.length - 1]);
		}
		out.print("\nWhat would you like to do >>> ");
		out.flush();
	}

	public void exit() {
		in.close();
		out.close();
		System.exit(0);
	}

}
