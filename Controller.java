/**
 * 
 */
package wmr;

/**
 * A controller for the West Midlands Railway Information Centre system.
 * This controller includes the 3 features that the intended
 * prototype West Midlands Railway Information Centre system. 
 * is expected to have.
 * 
 * @author Sylvia Wong
 * @version 22/05/2021
 */
public interface Controller {
	
	/**
	 * Lists all termini of a specified railway line.
	 * @param line The ID of the required line as shown in the TUI.
	 * @return the name of all stations that are the end point of the specified lines in the network. 
	 */
	public String listAllTermini(String line);

		
	
	

	
	/**
	 * Lists all stations in their respective order along the specified West Midlands Railway line
	 * and the expected cumulative travel time along the stations on the line.
	 * @param line	The ID of the required line as shown in the TUI.
	 * @return	a String representation of all stations and their accumulative travel time in the specified line.
	 */
	String listStationsInLine(String line);

	/**
	 * Show an accessible path between the specified stations, and 
	 * the boarding time of the first train along the identified route.
	 * The path is represented as a sequence of the name of the stations between the specified stations. 
	 * @param plannedStartTime	the planned start time of the journey
	 * @param stationA	the name of the start station
	 * @param stationB	the name of the destination station
	 * @return	a String representation of a path between the specified stations and the expected travel time.
	 */
	String showAccessiblePath(String plannedStartTime, String stationA, String stationB);
}
