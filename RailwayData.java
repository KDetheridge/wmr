import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;

import java.io.FileNotFoundException;

import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;

import Graph.Graph;


public class RailwayData {
	private Scanner sc;
	// Store the stations
	private ArrayList<String> wmrStations;
	private ArrayList<String> wmrStationsStepFree;
	//Store the stations for each line in a map for quick lookup
	private HashMap<String, Integer> stationIndexMap = new HashMap<String, Integer>();
	private HashMap<String,HashSet<String>> lineStationsMap = new HashMap<String,HashSet<String>>();

	//Store each line. The Key is the line name, the value is a linked list of all the stations, in order, on that line, with weights attached to each.
	private HashMap<String, Graph<String>> lineStationsGraphMap = new HashMap<String, Graph<String>>();
	private HashMap<String, ArrayList<String>> lineStationsSequenceArrMap = new HashMap<String, ArrayList<String>>();
	//Store the lines for each station in a map for quick lookup
	private HashMap<String,HashSet<String>> stationLinesMap = new HashMap<String,HashSet<String>>();
	//The graph to store all of the vertices for the entire network
	private Graph<String> railwayNetwork = new Graph<String>();
	private Set<String> accessibleStations = new HashSet<String>();
	// private HashMap<RailLine,Station> lineStationMap;
	// private HashMap<Station,RailLine> stationLineMap;

	public RailwayData(){
		
		importData("..\\DC2310_cwk_data\\WMRlines.csv");
		importAccessibleStationData("..\\DC2310_cwk_data\\WMRstationsWithStepFreeAccess.csv");
		
		//System.out.println(stationLinesMap.get("Birmingham New Street"));
		//System.out.println(lineStationsMap.get("Birmingham -- Walsall -- Rugeley"));
	}
	
	public static void main(String[] args) {
		RailwayData rd = new RailwayData();
	}


	/**
		 * Reads data from a csv file.
		 * @param filepath The absolute filepath to the required data, including the filename.
		 */
		private void importData(String filepath){
			Scanner sc;
			try{
				sc = new Scanner(new File(filepath));
				System.out.println("Loaded file successfully.");

				//Use the carriage return/line feed sequence to split each row into a new list item.
				sc.useDelimiter(Pattern.compile("[\\r\\n]+"));
				String toAdd;
				String[] parsedData;
				
				if (sc.hasNext()){
					//Skip the header row
					sc.next();
				}

				String prevLine = null;
				int stationAIdx;
				int stationBIdx;
				
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
					if (currLine != null && prevLine !=null &&!(prevLine.equals(currLine))){
						
						//lineStationsGraph
						//System.out.println(currLine + ": \n\t\t" + currGraph);
						currGraph = new Graph<String>();
						//System.out.println( prevLine + ": \n" + currStationsOrdered);
						currStationsOrdered = new ArrayList<String>();
						
						lineStationsSequenceArrMap.get(currLine).add(sourceStation);
						
						//lineStationsSequenceArrMap.put(currLine, currStationsOrdered);
						//Create an edge in the new graph from source station to target station and assign it's weight (travel time)
						currGraph.addEdge(sourceStation, targetStation, weight);
						
					}else{
						currGraph.addEdge(sourceStation,targetStation,weight);
						
					}
					currStationsOrdered.add(sourceStation);
					currStationsOrdered.add(targetStation);
					
					
					//add or update the lineStationsGraphMap entry for the current Line
					lineStationsGraphMap.put(currLine,currGraph);
							

					

					//System.out.println("stationIndexMap size before insert: " + stationIndexMap.size());
					
					//If the source station for this pair has previously been indexed
					if (stationIndexMap.containsKey(sourceStation)){
						stationAIdx = stationIndexMap.get(sourceStation);
						
					}
					
					else{
						//create a new index and retrieve its value into stationAIdx
						stationAIdx = stationIndexMap.size();
						stationIndexMap.put(sourceStation, stationAIdx);
					}
					//If the target station for this pair has previously been indexed
					if (stationIndexMap.containsKey(targetStation)){
						stationBIdx = stationIndexMap.get(targetStation);
						
					}
					else{
						//create a new index and retrieve its value into stationAIdx
						stationBIdx = stationIndexMap.size();
						stationIndexMap.put(targetStation, stationBIdx);
					}
					
					
					
					//System.out.println("stationIndexMap size after insert: " + stationIndexMap.size());
					//System.out.println(stationIndexMap.keySet());
					//addlineStationsLinkedListItems(splt);
					addLineStations(splt);
					addStationLines(splt);
					
					//Add the Station to the lineStationsMap list for this line
					
					//Add the Line to the stationLinesMap List for this station
					prevLine = currLine;
				}
				


		 		// Set<Map.Entry<String,HashMap<String, Integer>>> entrySet = railwayNetwork.entrySet();
				// for (Map.Entry<String,HashMap<String, Integer>> e : entrySet){
				// 	System.out.println(e);
				// }

				

				//pull from the graph all nodes and edges associated with that line
				//print outAny nodes that have just one station
					
				

		}


		catch (FileNotFoundException e){
			System.err.println("Could not find the data file specified. \n Please Contact an administrator for assistance.");
			System.exit(1);
		}



	}
	private void importAccessibleStationData(String filepath){
		Scanner sc;
		try{
			sc = new Scanner(new File(filepath));
			System.out.println("Loaded file successfully.");

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
			System.out.println(accessibleStations);
		}
		catch (FileNotFoundException e){
			System.err.println("Could not find the data file specified. \n Please Contact an administrator for assistance.");
			System.exit(1);
		}
	}

	public Set<String> getAccessibleStations(){
		return accessibleStations;
	}

	// private void addlineStationsLinkedListItems(String[] splt){
	// 	if (lineStationsLinkedListMap.get(splt[0]) == null){
	// 		//Create a new set to hold the line names
	// 		LinkedList<String> toAdd = new LinkedList<String>();
	// 		//Add the line name to the LinkedList
	// 		toAdd.add(splt[1]);
	// 		toAdd.add(splt[2]);
			
	// 		//put the source station name into the LinkedList
	// 		lineStationsLinkedListMap.put(splt[0], toAdd);
	// 	}
	// 	else{
	// 		LinkedList<String> stationList = lineStationsLinkedListMap.get(splt[0]);
			
	// 		if(stationList.contains)
	// 		}
			
	// 	} 

	// }

	public HashMap<String,Integer> getEdges(String station){
		return railwayNetwork.getEdges(station);
	}

	public HashMap<String,HashMap<String,Integer>> getAllEdges(){
		return railwayNetwork.getEdges();
	}
	public String listAllTermini(String targetLine){

		StringBuilder s = new StringBuilder();
		Graph<String> targetGraph = lineStationsGraphMap.get(targetLine);
		Set<Map.Entry<String,Integer>> nodeEdgeCounts = targetGraph.getNodeEdgeCountMap().entrySet();
		Set<String> checked = new HashSet<String>();
		for (Map.Entry<String,Integer> e : nodeEdgeCounts){
			if (e.getValue() == 1 && !checked.contains(e.getKey())){
				checked.add(e.getKey());
				s.append(e.getKey() + "\n");
			}
		}
		
		return s.toString();
	}

	
	private void addStationLines(String[] splt){

		//If the station does not already exist in the stationLinesMap HashMap
		if (stationLinesMap.get(splt[1]) == null){
			//Create a new set to hold the line names
			HashSet<String> toAdd = new HashSet<String>();
			//Add the line name to the set
			toAdd.add(splt[0]);
			//put the source station name into the array
			stationLinesMap.put(splt[1], toAdd);
		}
		else{
			HashSet<String> currLines = stationLinesMap.get(splt[1]);
			currLines.add(splt[0]);

		}

		//If the target station does not already exist in the stationLinesMap HashMap
		if (stationLinesMap.get(splt[2]) == null){
			//Create a new set to hold the line names
			HashSet<String> toAdd = new HashSet<String>();
			//Add the line name to the set
			toAdd.add(splt[0]);
			//put the source station name into the array
			stationLinesMap.put(splt[2], toAdd);
		}
		else{
			HashSet<String> currLines = stationLinesMap.get(splt[2]);
			currLines.add(splt[0]);

		}
	}

	public Set<String> getStationLines(String station){
		return stationLinesMap.get(station);
	}

	private void addLineStations(String[] splt){

		if(lineStationsMap.get(splt[0]) == null){
			HashSet<String> stationsToAdd = new HashSet<String>();
			stationsToAdd.add(splt[1]);
			stationsToAdd.add(splt[2]);
			
			lineStationsMap.put(splt[0],stationsToAdd);
			//System.out.println(lineStationsMap.get(splt[0]));
			// try{
			// 	System.in.read();
			// }
			// catch (Exception e){
			// 	System.out.println(e.getMessage());
			// }
		}
		else{
			//Retrieve a list of the currently recorded stations for this line
			HashSet<String> currStations = lineStationsMap.get(splt[0]);
			//Add the new items to the list (by reference, no reassignment to the lineStationsMap map required)
			currStations.add(splt[1]);
			currStations.add(splt[2]);

		}
	}

	public Set<String> getLineStationsSet(String line){
		return lineStationsMap.get(line);
	}

	public ArrayList<String> getLineStationsList(String line){
		return lineStationsSequenceArrMap.get(line);
	}


}
