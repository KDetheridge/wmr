import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import Graph.Graph;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
public class RailwayController implements Controller{
    RailwayData rd;
    HashMap<String, String> lineIndexMap;
    Set<String> accessibleStationSet;
    public RailwayController(){
        rd = new RailwayData();
        lineIndexMap = new HashMap<String, String>();
        //Set of all accessible stations in the network
        accessibleStationSet = rd.getAccessibleStations();
        lineIndexMap.put("a","Birmingham -- Dorridge -- Leamington Spa");
        lineIndexMap.put("b","Cross City Line");
        lineIndexMap.put("c","Birmingham -- Rugby -- Northampton -- London");
        lineIndexMap.put("d","Nuneaton -- Coventry");
        lineIndexMap.put("e","Watford -- St Albans Abbey");
        lineIndexMap.put("f","Bletchley -- Bedford");
        lineIndexMap.put("g","Crewe -- Stoke -- Stafford -- London");
        lineIndexMap.put("h","Worcester -- Birmingham");
        lineIndexMap.put("i","Smethwick Galton Bridge Connections");
        lineIndexMap.put("j","Birmingham -- Stratford-upon-Avon");
        lineIndexMap.put("k","Birmingham -- Wolverhampton -- Telford -- Shrewsbury");
        lineIndexMap.put("l","Birmingham -- Worcester -- Hereford");
        lineIndexMap.put("m","Birmingham -- Walsall -- Rugeley");
    }

    @Override
    public String listAllTermini(String line){
        StringBuilder s = new StringBuilder();
    String lineName = lineIndexMap.get(line);
    if (lineName == null){
        return("Please enter a valid selection");
    }
    Set<String> terminiSet = rd.listAllTermini(lineName);
    if (terminiSet == null){
        return "This station has no termini.";
    }
    else{
        s.append("\nTermini of " + lineName + ": \n");
        for (String t : terminiSet){
            s.append(t + "\n");
        }
        return s.toString();
        
    }
   
    }
    @Override
    public String listStationsInLine(String line){
        StringBuilder s = new StringBuilder();
        String lineName = lineIndexMap.get(line);
        Set<String> terminiSet = rd.listAllTermini(lineName);

        if (lineName == null){
            return "Invalid selection. Please try again.";
        }
        ArrayList<String> stationSequence = rd.getLineStationsSequence(lineName);

        HashMap<String,HashMap<String,Integer>> edgeMap = rd.getAllEdges();
        String src;
        s.append(stationSequence.get(0));
        String dst;
        int travelTime = 0;
        int totalTravelTime = 0;
        for (int i = 1; i< stationSequence.size()-1;  i++){
            src = stationSequence.get(i);
            dst = stationSequence.get(i+1);
            
            if (!(edgeMap.get(src).get(dst) == null)){
                travelTime = edgeMap.get(src).get(dst);
                s.append(" -" + travelTime + "-> " + dst);
                totalTravelTime += travelTime;
            }
            
            
        }
        
        return  "\nCumulative Travel Time for line " + lineName + ": " + totalTravelTime + "\n" + s.toString();
    }

    @Override   
    public String showAccessiblePath(String plannedStartTime, String stationA, String stationB){
        StringBuilder s = new StringBuilder();
        if (stationA.compareTo(stationB) == 0){
            return "Please enter two different stations.";
        }
        if(!isStationValid(stationA)){
            s.append("Station \"" + stationA +"\" does not exist. Please check the spelling and capitalisation and try again\n");
            return s.toString();
        }  

        if(!isStationValid(stationB)){
            s.append("Station \"" + stationB +"\" does not exist. Please check the spelling and capitalisation and try again\n");
            return s.toString();
        }  

        //a is not an accessible station
        if (!accessibleStationSet.contains(stationA)){
            s.append(stationA);
            //find nearest stations to a that has step-free access
            stationA = findNearestAccessibleStation(stationA, new HashSet<String>());

            if (stationA != null && stationA != ""){
                s.append(" is not accessible. Replaced with the nearest accessible station: ");
                s.append(stationA + "\n");
            }
            else{
                s.append(" is not a step-free station, and there are no other step-free stations in the network.\n");
                return s.toString();
            }
            
        }
        
        //b is not an accessible station
        if (!accessibleStationSet.contains(stationB)){
            s.append(stationB);
            //find nearest stations to B that has step-free access
            stationB = findNearestAccessibleStation(stationB, new HashSet<String>());

            if (stationB != null && stationB != ""){
                s.append(" is not accessible. Replaced with the nearest accessible station: ");
                s.append(stationB + "\n");
            }
            else{
                s.append(" is not a step-free station, and there are no other step-free stations in the network.\n");
                return s.toString();
            }
            
        }
        if (stationA.compareTo(stationB) == 0){
            return "No valid route as the stations provided are inaccessible and the nearest station to both of them is " + stationB;
        }

        //Find a path between A and B, or the nearest accessible stations from each station
        String path = findAccessiblePath(stationA, stationB);
        String boardTime = calculateBoardingTime(plannedStartTime,stationA);
        return s.toString() + boardTime + "\n" + path ;

    }

    private boolean isStationValid(String stationName){

        StringBuilder s = new StringBuilder();
        if (!checkStationExists(stationName)){
            
            return false;
        }
        return true;
    }
    

    /**
     * Find the nearest station with step-free access to a given station without step-free access.
     * Recursively looks at each station from a given station until a station with step-free access is found.
     * @param stationName The string name of the station.
     * @param visited A set of all previously visited stations. 
     */

    /**
     * Finds an accessible path between a start station and an end station
     */
    public String findAccessiblePath(String stationA, String stationB){
        StringBuilder s = new StringBuilder();



        String route = calculateRoute(stationA,stationB,new HashSet<String>());
        s.append(route);
        return  s.toString();
        
    }

    /**
     * A recursive function to find the nearest station with step-free access from a provided station, if the station itself does not have step-free access.
     * @param stationName String of the station name.
     * @param visited A set of visited stations. These stations should be ignored on subsequent calls to prevent an infinite loop.
     * @return String name of the nearest station with step-free access.
     */
    private String findNearestAccessibleStation(String stationName, Set<String> visited){
        //Add the current station to the visited list so we don't revisit it later from a neighbour
        visited.add(stationName);
        HashMap<String,Integer> stationNeighbours = rd.getEdges(stationName);
        //String set of all neighboring station names
        Set<String> neighbourNameSet = new HashSet<String>(stationNeighbours.keySet());

        //remove from the neighbourNameSet all the stations that have already been visited
        neighbourNameSet.removeAll(visited);

        //current station does not have step-free access and it has no neighbours that are unvisited
        if (neighbourNameSet.size() == 0) {
            return "";
        }
        //A copy of neighbourNameSet to be filtered for accessible stations only
        Set<String> accessibleNeighbourNameSet = new HashSet<String>(neighbourNameSet);

        //intersect the accessibleNeighbourNameSet with the accessibleStationsSet.
        //Keep only elements that exist in both, i.e. filter accessibleNeighbourNameSet for accessible stations.
        accessibleNeighbourNameSet.retainAll(accessibleStationSet);

        //define a new comparator to be used for sorting the map entries of the neighbourList
        Comparator<Map.Entry<String, Integer>> mapEntrySort = new Comparator<Map.Entry<String, Integer>>() {

            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
                return o1.getValue().compareTo(o2.getValue());
            }
            
        };

        ArrayList<Map.Entry<String,Integer>> neighbourList = new ArrayList<Map.Entry<String,Integer>>(stationNeighbours.entrySet());

        Collections.sort(neighbourList, mapEntrySort);

        //if no neighbours have step-free access
        if (accessibleNeighbourNameSet.isEmpty()){

            for (Map.Entry<String, Integer> st : neighbourList){
                //recursively call the function until at least one neighbour has step-free access.
                //Assign the found value to foundStation
                String foundStation = findNearestAccessibleStation(st.getKey(), visited);
                if (foundStation != ""){
                    return foundStation;
                }
            }
            //All neighbours did not have an accessible station attached, return blank
            return "";
        }
        //One of the neighbours has step-free access
        else{
            for (Map.Entry<String, Integer> st : neighbourList){
                String shortestDistanceStation = st.getKey();
                //the station is accessible
                //Return the first station name from the neighbourList that is an accessible station
                if(accessibleNeighbourNameSet.contains(shortestDistanceStation)){
                    return shortestDistanceStation;
                }

            }
            return "";

             
   
        }
        
    }

    /**
     * Checks if a station exists.
     * @param station the station to check for
     * @return true or false
     */
    public boolean checkStationExists(String station){
        //return true if the station exists
        return rd.getAllEdges().keySet().contains(station);
    }

    /**
     * Calculate the time that the journey should start at a given station.
     * @param plannedStartTime the time the user specifies their journey should start at.
     * @param stationName the station the user is travelling from
     * @return String representing the boarding time with some extra information.
     */
    private String calculateBoardingTime(String plannedStartTime, String stationName){
        //Trains start at 5am.
        int networkStartTimeInMinutes = 5*60;
        int timeInMinutes = 0;
        plannedStartTime = plannedStartTime.replace(":","");
        plannedStartTime = plannedStartTime.replace(".","");
        
        if (!(plannedStartTime.matches("[0-1]{1}[0-9]{1}[0-5]{1}[0-9]{1}")) && !(plannedStartTime.matches("2{1}[0-3]{1}[0-5]{1}[0-9]{1}")) ){
            return "Please enter a valid time";
        }
        String[] plannedStartTimeArr =  plannedStartTime.split("");
        for (int i = 0;i<2; i++){
            timeInMinutes = Integer.valueOf(plannedStartTimeArr[i])*60;
        }
        timeInMinutes += (Integer.valueOf(plannedStartTimeArr[2])*10 + Integer.valueOf(plannedStartTimeArr[3]));

        Set<String> stationLines = rd.getStationLines(stationName);
        //The stations for the associated line, in order
        ArrayList<String> lineStations = new ArrayList<String>();
        
        for (String l : stationLines){
            lineStations = rd.getLineStationsSequence(l);
            break;
        }

        String hour = String.valueOf(timeInMinutes/60);
        if (hour.length()==1){
            hour = "0" + hour;
        }
        String minutes = String.valueOf(timeInMinutes % 60);
        if (minutes.length()==1){
            minutes = "0" + minutes;
        }
        return hour + ":" + minutes;
        // String hour = String.valueOf(timeInMinutes/60);
        // if (hour.length()==1){
        //     hour = "0" + hour;
        // }
        // String minutes = String.valueOf(timeInMinutes % 60);
        // if (minutes.length()==1){
        //     minutes = "0" + minutes;
        // }
        // return hour + ":" + minutes;

        
    }

    /**
     * Calculate a route between the two specified stations. Recursive until a route is found or there are no remaining paths to check
     * @param stationA The start station
     * @param stationB the target station.
     * @return a formatted string of the route.
     */
    private String calculateRoute(String stationA, String stationB, Set<String> visited){
        StringBuilder routeString = new StringBuilder();
        //Add the current station to the visited list so we don't revisit it later from a neighbour
        visited.add(stationA);

        HashMap<String,Integer> stationNeighbours = rd.getEdges(stationA);


        //String set of all neighboring station names
        Set<String> neighbourNameSet = new HashSet<String>(stationNeighbours.keySet());

        ///If the target is one of stationA's neighbours
        if (neighbourNameSet.contains(stationB)){
            routeString.append(stationA);
            routeString.append(" -"+stationNeighbours.get(stationB)+"-> ");
            routeString.append(stationB);
            //return the path from stationA to stationB
            return routeString.toString();
        }

        //remove from the neighbourNameSet all the stations that have already been visited
        neighbourNameSet.removeAll(visited);

        //current stationA has no neighbours that are unvisited
        if (neighbourNameSet.size() == 0) {
            return "";
        }
        //A copy of neighbourNameSet to be filtered for accessible stations only
        Set<String> accessibleNeighbourNameSet = new HashSet<String>(neighbourNameSet);

        //intersect the accessibleNeighbourNameSet with the accessibleStationsSet.
        //Keep only elements that exist in both, i.e. filter accessibleNeighbourNameSet for accessible stations.
        accessibleNeighbourNameSet.retainAll(accessibleStationSet);

        //define a new comparator to be used for sorting the map entries of the neighbourList
        Comparator<Map.Entry<String, Integer>> mapEntrySort = new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
                return o1.getValue().compareTo(o2.getValue());
            }  
        };
        
        ArrayList<Map.Entry<String,Integer>> neighbourList = new ArrayList<Map.Entry<String,Integer>>(stationNeighbours.entrySet());
        
        Collections.sort(neighbourList, mapEntrySort);

        //else, calculateRoute(neighbour,stationB,visited);
        //for each neighbour,
        for (Map.Entry<String,Integer> st : neighbourList){
                String neighbourName = st.getKey();
                if (visited.contains(neighbourName)){
                    continue;
                }
                String foundRoute = calculateRoute(neighbourName,stationB,visited);
                if (foundRoute != ""){
                    routeString.append(stationA + " -"+stationNeighbours.get(neighbourName)+"-> ");
                    routeString.append(foundRoute);
                    return routeString.toString();
                }
        }
        return "";
    }  


    
    private String getRouteString(ArrayList<String> stationNameArrList){
        StringBuilder s = new StringBuilder();
        for(String stationName : stationNameArrList){
            s.append(stationName);
            s.append(" -> ");
        }
        return s.toString();
    }
    private String getRouteString(Set<Map.Entry<String,Integer>> routeSet){
        StringBuilder s = new StringBuilder();
        for(Map.Entry<String,Integer> e : routeSet){
            s.append(e.getKey());
            s.append(" -> ");
        }
        return s.toString();
    }
}
