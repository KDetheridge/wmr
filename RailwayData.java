import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;

import java.io.FileNotFoundException;

import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

import java.util.Map;
import java.util.HashMap;

import Graph.Graph;


public class RailwayData {
private HashMap<String,HashSet<String>> lineStationsMap = new HashMap<String,HashSet<String>>();

	//Store each line. The Key is the line name, the value is a linked list of all the stations, in order, on that line, with weights attached to each.
	private HashMap<String, Graph<String>> lineStationsGraphMap = new HashMap<String, Graph<String>>();
	private HashMap<String, ArrayList<String>> lineStationsSequenceArrMap = new HashMap<String, ArrayList<String>>();
	//Store the lines for each station in a map for quick lookup
	private HashMap<String,HashSet<String>> stationLinesMap = new HashMap<String,HashSet<String>>();
	//The graph to store all of the vertices for the entire network
	private Graph<String> railwayNetwork = new Graph<String>();
	private Set<String> accessibleStations = new HashSet<String>();

	public RailwayData(){
		
		importData("..\\DC2310_cwk_data\\WMRlines.csv");
		importAccessibleStationData("..\\DC2310_cwk_data\\WMRstationsWithStepFreeAccess.csv");

	}

	/**
		 * Reads data from a csv file. 
		 * Populates several data structures that are key for using the application:
		 * A graph of the entire railway network,
		 * A mapping of stations to lines each station exists in,
		 * A mapping of each line to the contained stations
		 * A mapping for each Line and the stations in order of appearance in the file
		 * @param filepath The absolute filepath to the required data, including the filename.
		 */
		private void importData(String filepath){
			Scanner sc;
			try{
				sc = new Scanner(new File(filepath));


				//Use the carriage return/line feed sequence to split each row into a new list item.
				sc.useDelimiter(Pattern.compile("[\\r\\n]+"));
				String toAdd;

				if (sc.hasNext()){
					//Skip the header row
					sc.next();
				}

				String prevLine = null;
				
				Graph<String> currGraph = new Graph<String>();
				ArrayList<String> currStationsOrdered = new ArrayList<String>();
				while (sc.hasNext()){
					
					//Get the next line
					toAdd = sc.next();
					
					//Split the current row of data into an array around commas
					String[] splt = toAdd.split(",");
					//trim leading and trailing whitespace from each splt value
					for (int i = 0; i < splt.length; i++){
						splt[i] = splt[i].trim();
					}

					String currLine = splt[0];
					String sourceStation = splt[1];
					String targetStation = splt[2];
					int weight = Integer.valueOf(splt[3]);
					railwayNetwork.addEdge(sourceStation,targetStation,weight);
					//If this is not the first item

					//if the current row continues the current railway line
					if (currLine != null && prevLine !=null && (prevLine.equals(currLine))){
						currStationsOrdered = lineStationsSequenceArrMap.get(currLine);
						currStationsOrdered.add(targetStation);

						lineStationsSequenceArrMap.put(currLine,currStationsOrdered);
						

					}
					//New railway line
					else{
						currGraph = new Graph<String>();
						//create a new array and add the source and target stations of this edge.
						currStationsOrdered = new ArrayList<String>();
						currStationsOrdered.add(sourceStation);
						currStationsOrdered.add(targetStation);
						lineStationsSequenceArrMap.put(currLine,currStationsOrdered);
					}
					prevLine = currLine;

					
					currGraph.addEdge(sourceStation,targetStation,weight);
					//add or update the lineStationsGraphMap entry for the current Line
					lineStationsGraphMap.put(currLine,currGraph);

					//Add the source and target stations to the lineStationsMap for this line
					addLineStations(currLine, sourceStation);
					addLineStations(currLine, targetStation);
					//Add the source and target stations to the stationLinesMap for this line

					addStationLines(sourceStation, currLine);
					addStationLines(targetStation, currLine);
					
				}

		}


		catch (FileNotFoundException e){
			System.err.println("Could not find the data file specified. \n Please Contact an administrator for assistance.");
			System.exit(1);
		}



	}
	/**
	 * Import the data that records which stations have step-free access.
	 * @param filepath The absolute or relative filepath to the data file
	 */
	private void importAccessibleStationData(String filepath){
		Scanner sc;
		try{
			sc = new Scanner(new File(filepath));

			//Use the carriage return/line feed sequence to split each row into a new list item.
			sc.useDelimiter(Pattern.compile("[\\r\\n]+"));

			if (sc.hasNext()){
				//Skip the header row
				sc.next();
			}
			while (sc.hasNext()){
				
				//Get the next line
				String stationName = sc.next().trim();

				accessibleStations.add(stationName);
			}
		}
		catch (FileNotFoundException e){
			System.err.println("Could not find the data file specified. \n Please Contact an administrator for assistance.");
			System.exit(1);
		}
	}

	/**
	 * @return a Set of the names of all accessible (step-free) stations in the network
	 */
	public Set<String> getAccessibleStations(){
		return accessibleStations;
	}

	
	/**
	 * Get all edges for a given station.
	 * @param station the station for which the edges should be returned.
	 * @return a HashMap of the edges and their weights from the input station.
	 */
	public HashMap<String,Integer> getEdges(String station){
		return railwayNetwork.getEdges(station);
	}

	/**
	 * @return A list of all edges(connections) within the railwayNetwork.
	 */
	public HashMap<String,HashMap<String,Integer>> getAllEdges(){
		return railwayNetwork.getEdges();
	}
	/**
	 * Return the travel time in minutes between two stations
	 * @param stationA the source station
	 * @param stationB the target station
	 * @return integer of the distance between stationA and stationB
	 */
	public int getEdgeWeight(String stationA, String stationB){
		if (railwayNetwork.getEdges(stationA).get(stationB) != null){
			return railwayNetwork.getEdges(stationA).get(stationB);

		}
		else{
			return 0;
		}
	}
	/**
	 * Get a set view of all termini for a specified line.
	 * A termini is defined as being at the end of a route i.e. only having one connection to another station)
	 * @param targetLine the line for which to return the termini
	 * @return Set<String> of the termini of the targetLine.
	 */
	public Set<String> listAllTermini(String targetLine){
		Graph<String> targetGraph = lineStationsGraphMap.get(targetLine);
		Set<Map.Entry<String,Integer>> nodeEdgeCounts = targetGraph.getNodeEdgeCountMap().entrySet();
		Set<String> checked = new HashSet<String>();
		for (Map.Entry<String,Integer> e : nodeEdgeCounts){
			if (e.getValue() == 1 && !checked.contains(e.getKey())){
				checked.add(e.getKey());

			}
		}
		
		return checked;
	}

	/**
	 * Add a line to a given station within the stationLinesMap.
	 * @param station the station to add the mapping to
	 * @param line the line to add
	 */
	private void addStationLines(String station, String line){

		//If the station does not already exist in the stationLinesMap HashMap
		if (stationLinesMap.get(station) == null){
			//Create a new set to hold the line names
			HashSet<String> toAdd = new HashSet<String>();
			//Add the line name to the set
			toAdd.add(line);
			//put the source station name into the array
			stationLinesMap.put(station, toAdd);
		}
		else{
			HashSet<String> currLines = stationLinesMap.get(station);
			currLines.add(line);

		}

	}
	/**
	 * Returns a set view of the lines that a given station is a part of.
	 * @param station the station to look for
	 * @return a String set of the line names
	 */
	public Set<String> getStationLines(String station){
		return stationLinesMap.get(station);
	}
	/**
	 * Add a station to a line map.
	 * @param line The line to add the station to
	 * @param station the station to add to the line map
	 * 
	 */
	private void addLineStations(String line, String station){

		if(lineStationsMap.get(line) == null){
			HashSet<String> stationsToAdd = new HashSet<String>();
			stationsToAdd.add(station);
			
			lineStationsMap.put(line,stationsToAdd);
		}
		else{
			//Retrieve a list of the currently recorded stations for this line
			HashSet<String> currStations = lineStationsMap.get(line);
			//Add the new items to the list (by reference, no reassignment to the lineStationsMap map required)
			currStations.add(station);

		}
	}
	/**
	 * returns a Set view of the station names for a given line
	 * @param line The line to retrieve station names for.
	 * @return a string set of the station names
	 */
	public Set<String> getLineStationsSet(String line){
		return lineStationsMap.get(line);
	}
	/**
	 * returns an ArrayList of the station names for a given line in the order they were inserted.
	 * @param line The line to retrieve station names for.
	 * @return a string ArrayList of the station names
	 */
	public ArrayList<String> getLineStationsSequence(String line){
		return lineStationsSequenceArrMap.get(line);
	}


}
