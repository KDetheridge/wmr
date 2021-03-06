/**
 * 
 */

import java.util.Scanner;

/**
 * A simple text-based user interface for showing various information 
 * about a West Midlands Railway network.
 * 
 * @author S H S Wong
 * @version 22/05/2021
 */
public class TUI {

	private Controller controller;  
	private Scanner stdIn;
	public static void main(String[] args) {
		Controller controller = new RailwayController();
		TUI tui = new TUI(controller);
	}

	public TUI(Controller controller) {
		
		this.controller = controller;
		
		// Creates a Scanner object for obtaining user input
		stdIn = new Scanner(System.in);
		
		while (true) {
			displayMenu();
			getAndProcessUserOption();
		}
	}

	/**
	 * Displays the header of this application and a summary of menu options.
	 */
	private void displayMenu() {
		display(header());
		display(menu());
	}
	
	/**
	 * Obtains an user option and processes it.
	 */
	private void getAndProcessUserOption() {
		String command = stdIn.nextLine().trim();
		System.out.println("\n");
		switch (command) {
		case "1" : // Lists all termini along a specified line
			display("Lists all termini along a specific WMR line...");
			display("Enter the ID of the required line:");
			display(allWMRlines());
			String line = stdIn.nextLine().trim();

			display(controller.listAllTermini(line));
			break;
		case "2" : // Lists all stations and their cumulative travel time in a line
			display("Lists all stations along a line...");
			display("Enter the ID of the line you'd like to view:");
			display(allWMRlines());
			display(controller.listStationsInLine(stdIn.nextLine().trim()));
			break;
		case "3" : // Finds an accessible path between two stations
			display("Finds an accessible path between two stations...");
			display("Enter your planned start time of travel:");
			String plannedStartTime = stdIn.nextLine().trim();
			display("Enter the name of the start station:");
			String stationA = stdIn.nextLine().trim();
			display("Enter the name of the end station:");
			String stationB = stdIn.nextLine().trim();
			display("\n\n");
			display(controller.showAccessiblePath(plannedStartTime, stationA, stationB));
			break;
		case "4" : // Exits the application
			display("Goodbye!");
			System.exit(0);
			break;
		default : // Not a known command option
			display(unrecognisedCommandErrorMsg(command));
		}
	}

	/*
	 * Returns a string representation of all railway lines in this application.
	 * @return info about all West Midlands Railway lines
	 */
	private static String allWMRlines() {
		return "\na. Birmingham -- Dorridge -- Leamington Spa" +
		       "\nb. Cross City Line" + 
			   "\nc. Birmingham -- Rugby -- Northampton -- London" +
		       "\nd. Nuneaton -- Coventry" +
			   "\ne. Watford -- St Albans Abbey" +
		       "\nf. Bletchley -- Bedford" +
			   "\ng. Crewe -- Stoke -- Stafford -- London" +
		       "\nh. Worcester -- Birmingham" + 
			   "\ni. Smethwick Galton Bridge Connections" +
		       "\nj. Birmingham -- Stratford-upon-Avon" +
			   "\nk. Birmingham -- Wolverhampton -- Telford -- Shrewsbury" +
		       "\nl. Birmingham -- Worcester -- Hereford" +
		       "\nm. Birmingham -- Walsall -- Rugeley";
	}
	
	/*
	 * Returns a string representation of a brief title for this application as the header.
	 * @return	a header
	 */
	private static String header() {
		return "\nWMR Information Centre\n";
	}
	
	/*
	 * Returns a string representation of the user menu.
	 * @return	the user menu
	 */
	private static String menu() {
		return "Enter the number associated with your chosen menu option.\n" +
			   "1: List all termini in a specified line\n" +
			   "2: List all stations and their cumulative travel time along a specific line\n" +
			   "3: Find an accessible path between two stations\n" +
			   "4: Exit this application\n";
	}
	
	/*
	 * Displays the specified info for the user to view.
	 * @param info	info to be displayed on the screen
	 */
	private void display(String info) {
		System.out.println(info);
	}
	
    /*
     * Returns an error message for an unrecognised command.
     * 
     * @param error the unrecognised command
     * @return      an error message
     */
    private static String unrecognisedCommandErrorMsg(String error) {
            return String.format("Cannot recognise the given command: %s.%n", error);
    }

}
