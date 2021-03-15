package com.techelevator;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techelevator.months.April;
import com.techelevator.months.August;
import com.techelevator.months.December;
import com.techelevator.months.February;
import com.techelevator.months.January;
import com.techelevator.months.July;
import com.techelevator.months.June;
import com.techelevator.months.March;
import com.techelevator.months.May;
import com.techelevator.months.Month;
import com.techelevator.months.MonthHandler;
import com.techelevator.months.November;
import com.techelevator.months.October;
import com.techelevator.months.September;
import com.techelevator.venues.Category;
import com.techelevator.venues.CategoryDao;
import com.techelevator.venues.Reservation;
import com.techelevator.venues.ReservationDao;
import com.techelevator.venues.Space;
import com.techelevator.venues.SpaceDao;
import com.techelevator.venues.Venue;
import com.techelevator.venues.VenueDao;
import com.techelevator.venues.jdbc.JdbcCategoryDao;
import com.techelevator.venues.jdbc.JdbcReservationDao;
import com.techelevator.venues.jdbc.JdbcSpaceDao;
import com.techelevator.venues.jdbc.JdbcVenueDao;
import com.techelevator.view.Menu;


public class ExcelsiorCLI {
	
	private static final Month[] MONTHS = new Month[] {new January(), new February(), new March(), new April(), new May(),
			new June(), new July(), new August(), new September(), new October(), 
			new November(), new December()};
	
	private static final String MAIN_MENU_OPTION_LIST_VENUES = "List Venues";
	private static final String MAIN_MENU_OPTION_QUIT = "Quit";
	private static final String[] MAIN_MENU_OPTIONS = new String[] { MAIN_MENU_OPTION_LIST_VENUES, 
																	 MAIN_MENU_OPTION_QUIT };
	
	private static final String MENU_OPTION_RETURN_TO_PREVIOUS= "Return to Previous Screen";
	
	private static final String SPACE_MENU_OPTION_VIEW_SPACES = "View Spaces";
	private static final String SPACE_MENU_OPTION_DISPLAY_RESERVATIONS = "Display Reservations";
	private static final String[] SPACE_MENU_OPTIONS = new String[] { SPACE_MENU_OPTION_VIEW_SPACES,
																	SPACE_MENU_OPTION_DISPLAY_RESERVATIONS,
																	MENU_OPTION_RETURN_TO_PREVIOUS };
	
	private static final String RESERVE_MENU_OPTION = "Reserve a Space";
	private static final String[] RESERVE_SPACE_MENU_OPTIONS = new String[] {RESERVE_MENU_OPTION,
																		MENU_OPTION_RETURN_TO_PREVIOUS};
	
	private Menu menu;
	private VenueDao venueDao;
	private ReservationDao reservationDao;
	private SpaceDao spaceDao;
	private CategoryDao categoryDao;
	private MonthHandler monthHandler;
	private long venueSelection;
	private LocalDate startDate;
	private int numOfPeople;
	private int numOfDays;
	


	public static void main(String[] args) {
		ExcelsiorCLI application = new ExcelsiorCLI();
		application.run();
	}

	public ExcelsiorCLI() {
		this.menu = new Menu(System.in, System.out);
		
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/venues");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		
		venueDao = new JdbcVenueDao(dataSource);
		spaceDao = new JdbcSpaceDao(dataSource);
		reservationDao = new JdbcReservationDao(dataSource);
		categoryDao = new JdbcCategoryDao(dataSource);
		
	}

	/*
	 * Main menu should list all venues and prompt to Space Menu
	 */
	public void run() {
		boolean running = true;
		while(running) {
			menu.printHeading("Welcome to Excelsior Venues");
			String choice = (String)menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			switch (choice) {
			case (MAIN_MENU_OPTION_LIST_VENUES):
				displayVenues();
				showSpaceMenu();
				break;
			case (MAIN_MENU_OPTION_QUIT):
				running = false;
				break;
			}
		}
	}
	
	/*
	 * Space menu should list All Spaces 
	 * 				should be able to search for reservation
	 */
	
	public void showSpaceMenu() {
		boolean running = true;
		while(running) {
			menu.printHeading("Spaces for the venue");
			String choice = (String)menu.getChoiceFromOptions(SPACE_MENU_OPTIONS);
			switch (choice) {
			case (SPACE_MENU_OPTION_VIEW_SPACES):
				displayAllSpaces();
				showReservationsMenu();
				break;
			case (SPACE_MENU_OPTION_DISPLAY_RESERVATIONS):
				listReservForTheLastThirtyDays();
				break;
			case (MENU_OPTION_RETURN_TO_PREVIOUS):
				running = false;
			    break;
			}
		}
	}
	
	/*
	 * Reservations menu should list allAvailable spaces and accept
	 * user input for date, days, people
	 */
	
	public void showReservationsMenu() {
		boolean running = true;
		while(running) {
			menu.printHeading("Reservations menu");
			String choice = (String)menu.getChoiceFromOptions(RESERVE_SPACE_MENU_OPTIONS);
			switch (choice) {
			case (RESERVE_MENU_OPTION):
				placeReservation();
				handleReservation();
				running = false;
				break;
			case MENU_OPTION_RETURN_TO_PREVIOUS: 
				running = false;
				break;
			}
		}
	}
	
	private void listReservForTheLastThirtyDays() {
		List<Reservation> reservations = reservationDao.getReservationsForTheLastThirtyDays();
		 
		menu.printMessage("The following reservations are coming up in the next 30 days:\n");
		menu.printHeading(String.format("%-35s %-25s %-35s %-15s %-15s \n", 
				"Venue", "Space", "Reserved For", "From", "To"));
		for(Reservation reservation : reservations) {
			Venue venue = venueDao.getVenueDetails(reservation.getVenue().getVenueId());
			Space space = spaceDao.getSpaceBySpaceId(reservation.getSpaceId());
			System.out.print(String.format("%-35s %-25s %-35s %-15s %-15s \n", 
						venue.getName(), space.getName(), reservation.getReservedFor(), 
						reservation.getStartDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
						reservation.getEndDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
		}
	}
	
	private void placeReservation() {
		while(true) {
			List<Space> spaces = null;
			
			String dateInput = menu.getUserInput("\nWhen do you need the space?");
			int monthFromUser;
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
				formatter = formatter.withLocale(Locale.US);
				startDate = LocalDate.parse(dateInput, formatter);
				if (startDate.isAfter(LocalDate.now())) {
					monthFromUser = startDate.getMonthValue();
				} else {
					menu.printError("\nPlease enter a future date for reservation.");
					continue;
				}
			
			} catch (DateTimeParseException e) {
				menu.printError("\nPlease enter date in format mm/dd/yyyy.");
				continue;
			}
			
			String numOfDaysInput = menu.getUserInput("How many days will you need the space for?");
			
			try {
				numOfDays = Integer.parseInt(numOfDaysInput);
				if (numOfDays <= 0) {
					menu.printError("\nPlease enter a correct number of days.");
					continue;
				}
			} catch (NumberFormatException e) {
				menu.printError("\nPlease enter a number of days.");
				continue;
			}
			
			String numOfPeopleInput = menu.getUserInput("How many people will be in attendance?");
			
			try {
				numOfPeople = Integer.parseInt(numOfPeopleInput);
				if(numOfPeople <= 0) {
					menu.printError("\nPlease enter a correct number of people.");
					continue;
				}
			} catch (NumberFormatException e) {
				menu.printError("\nPlease enter a number of people.");
				continue;
			}
			
			spaces = spaceDao.getAllAvailableSpaces(numOfPeople, monthFromUser, venueSelection);
			
			if (spaces.size() <= 0) {
				displayTopFiveSpaces(numOfDays);
			} else {
				listAllAvailableSpaces(spaces, numOfDays);
				
				break;
			}
			break;
		}
	}
	
	private void listAllAvailableSpaces(List<Space> spaces, int numOfDays) {
		menu.printMessage("\nThe following spaces are available based on your needs: \n");
		menu.printHeading(String.format("%-7s %-35s %-15s %-15s %-15s %-15s", 
						"Space#", "Name", "Daily Rate", "Max Occup.", "Accessible?", "Total Cost"));
		for(Space space : spaces) {
			String accessible;
			if (space.isAccessible() == true) {
				accessible = "Yes";
			} else {
				accessible = "No";
			}
			menu.printMessage(String.format("%-7s %-35s %-15s %-15s %-15s %-15s \n", 
			space.getSpaceId(), space.getName(), space.getDailyRate(), space.getMaxOccupancy(), accessible, 
				"$" + space.getTotalCost(numOfDays)));
		}
	}
	
	public void listSpaces(List<Space> spaces) {
		menu.printHeading(String.format("%5s %-35s %-8s %-8s %-15s %-15s", "", "Name", "Open", "Close", "Daily Rate", "Max. Occupancy"));
		if(spaces.size() > 0) {
			int index = 1;
			for(int y = 0; y < spaces.size(); y++) {
				int openMonth = spaces.get(y).getOpenFrom();
				int closingMonth = spaces.get(y).getOpenTo();
				monthHandler = new MonthHandler(MONTHS);
				String openMonthName = monthHandler.getMonthName(openMonth);
				String closingName = monthHandler.getMonthName(closingMonth);
				menu.printMessage(String.format("%5s %-35s %-8s %-8s %-15s %-15s \n", 
				"# " + index, spaces.get(y).getName(), openMonthName,
				closingName, "$" + spaces.get(y).getDailyRate(), spaces.get(y).getMaxOccupancy()));
				index++;
			}	
		} else {
			menu.printError("\n*** No results ***");
		}
	
	}
	
	private void handleReservation() {
		boolean running = true;
		while(running) {
			Venue venue = venueDao.getVenueDetails(venueSelection);
			//Space space = spaceDao.getSpaceBySpaceId(spaceIdFromUser);
			Reservation reservation = new Reservation();
			long spaceIdFromUser = 0;
			String userInput = menu.getUserInput("\nWhich space would you like to reserve (enter 0 to cancel)?");
			
			try {
				spaceIdFromUser = Long.parseLong(userInput);
				if (spaceIdFromUser == 0) {
					running = false;
					break;
				}
			} catch (NumberFormatException e) {
				menu.printError("\nPlease enter a number for space.");
				continue;
			}
			
			spaceDao.getSpaceBySpaceId(spaceIdFromUser);
			boolean isAllowed = false;
			try {
				isAllowed = reservationDao.isReservationAllowed(spaceIdFromUser, startDate, numOfPeople);
			} catch (ParseException e) {
				menu.printError("\n" + e.getMessage());
			}
			String endDate = null;
			try {
				endDate = reservationDao.getEndDate(startDate, numOfDays);
			} catch (ParseException e) {
				menu.printError("\n" + e.getMessage());
			}
			
			if (isAllowed == true) {
				String userInputName = menu.getUserInput("\nWho is this reservation for?");
				reservation.setSpaceId(spaceIdFromUser);
				reservation.setNumberOfAttendees(numOfPeople);
				reservation.setStartDate(startDate);
				reservation.setEndDate(LocalDate.parse(endDate));
				reservation.setReservedFor(userInputName);
				Reservation userReservation = reservationDao.placeReservation(reservation);
				menu.printMessage("\nThank you for submitting your reservation! The details for your event are listed below:");
				Space space = spaceDao.getSpaceBySpaceId(reservation.getSpaceId());
				menu.printConfirmation(userReservation, venue, space, numOfDays);
				menu.exit();
			} else if (isAllowed == false) {
				displayTopFiveSpaces(numOfDays);
				continue;
			}
			break;
		}
	}
	
	private void displayTopFiveSpaces (int numOfDays) {
		while(true) {
			String userInput = menu.getUserInput("\nNo space(s) available, would you like to try a different search? Y/N");
			if (userInput.toUpperCase().equals("Y")) {
				List<Space> topFiveSpaces = spaceDao.getTopFiveSpaces(venueSelection);
		
				menu.printHeading(String.format("%-8s %-35s %-10s %-15s %-15s %-15s", 
											"Space#", "Name", "Daily Rate", "Max Occup.", "Accessible", "Total Cost"));
				for (Space space : topFiveSpaces) {
					String accessible;
					if (space.isAccessible() == true) {
						accessible = "Yes";
					} else {
						accessible = "No";
					}
					BigDecimal totalCost = space.getTotalCost(numOfDays);
					menu.printMessage(String.format("%-8s %-35s %-10s %-15s %-15s %-15s \n", 
									space.getSpaceId(), space.getName(), space.getDailyRate(), space.getMaxOccupancy(),
									accessible, totalCost));
				}
			} else if (userInput.toUpperCase().equals("N")) {
				displayAllSpaces();
				break;
			}
			placeReservation();
			break;
		}
	}
	
	private void displayAllSpaces() {
		Venue venue = venueDao.getVenueDetails(venueSelection);
		System.out.println("");
		menu.printMessage(venue.getName() + " Spaces");
		List<Space> allSpaces = spaceDao.getAllSpacesByVenueId(venueSelection);
		listSpaces(allSpaces);
	}
	
	private void displayVenues() {
		menu.printHeading("List all Venues");
		List<Venue> allVenues = venueDao.getAllVenues();
		menu.listVenues(allVenues);
		displayDetailsForSelectedVenue();
	}
	
	private void displayDetailsForSelectedVenue(){
		while(true) {
			Venue venue = null;
			String venueInput = menu.getUserInput("\nWhich venue would you like to view?");
			venueSelection = 0;
			
			try { 
				venueSelection = Integer.parseInt(venueInput);
			} catch (NumberFormatException e) {
				continue;
			}
			
			try {
				venue = venueDao.getVenueDetails(venueSelection);
				if (venueSelection == venue.getVenueId()) {
					menu.printHeading("Venue Description");
					displayVenueDetails(venue);
				} 
			} catch (NullPointerException e) {
				menu.printError("\nPlease enter a number for a venue from the list.");
				continue;
			}
			break;
		}
	}
	
	private void displayVenueDetails(Venue venue) {
		List<Category> categories = categoryDao.getAllCategoriesByVenueId(venueSelection);
		menu.printVenueDetails(categories, venue);
		
	}
	
}
