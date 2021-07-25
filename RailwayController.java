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
    String lineName = lineIndexMap.get(line);
    String result = rd.listAllTermini(lineName);
    if (result == null){
        return "Invalid line, please try again.";
    }
    else{
        return result;
    }
   
    }
    @Override
    public String listStationsInLine(String line){
        StringBuilder s = new StringBuilder();
        String lineName = lineIndexMap.get(line);
        if (lineName == null){
            return "Invalid selection. Please try again.";
        }
        ArrayList<String> stationSequence = rd.getLineStationsList(lineName);
        HashMap<String,HashMap<String,Integer>> edgeMap = rd.getAllEdges();
        String src;
        String dst;
        for(int i = 0; i < stationSequence.size()-1; i++){
            src = stationSequence.get(i);
            dst = stationSequence.get(i+1);
            s.append(src + " <- " + edgeMap.get(src).get(dst) + " -> " + dst);
        }
        return s.toString();
    }

    @Override   
    public String showAccessiblePath(String plannedStartTime, String stationA, String stationB){
        
        //Find a path between A and B, or the nearest accessible stations from each station
        String path = findAccessiblePath(stationA, stationB);
        String boardTime = calculateBoardingTime(plannedStartTime);
        return path + "\n" + boardTime;

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
        boolean success = true;
        StringBuilder s = new StringBuilder();
        if (!validateStation(stationA)){
            s.append("Station \"" + stationA +"\" does not exist. Please check the spelling and capitalisation and try again\n");
            success = false;
        }

        if (!validateStation(stationB)){
            s.append("Station \"" + stationB +"\" does not exist. Please check the spelling and capitalisation and try again\n");
            success = false;
        }
        //if either of the provided stations could not be found within the network,
        if (!success){
            //return an error message to the user.
            return s.toString();
        }

        //a is not an accessible station
        if (!accessibleStationSet.contains(stationA)){
            s.append(stationA);
            //find nearest stations to a that has step-free access
            stationA = findNearestAccessibleStation(stationA, new HashSet<String>());
            stationA = null;
            if (stationA != null && stationA != ""){
                s.append(" is not accessible. Replaced with the nearest accessible station: ");
                s.append(stationA + "\n");
            }
            else{
                s.append(" not a step-free station, and there are no other step-free stations in the network.\n");
            }
            
        }

        //b is not an accessible station
        if (!accessibleStationSet.contains(stationB)){
            s.append(stationB + " is not accessible. Replaced with the nearest accessible station: ");

            //find nearest stations to b that has step-free access
            stationB = findNearestAccessibleStation(stationB, new HashSet<String>());
            s.append(stationB + "\n"); 
        }

        String route = calculateRoute(stationA,stationB,new HashSet<String>(), true);
        s.append(route);
        return  s.toString();
        
    }


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

        //Store the immediate neighbours in a set
        Set<Map.Entry<String,Integer>> neighbourSet = new HashSet<Map.Entry<String,Integer>>(stationNeighbours.entrySet());

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
    public boolean validateStation(String station){
        //return true if the station exists
        return rd.getAllEdges().keySet().contains(station);
    }

    private String calculateBoardingTime(String plannedStartTime){

        int timeInMinutes = 0;
        plannedStartTime.replace(":","");
        plannedStartTime.replace(".","");
        if (!(plannedStartTime.matches("[0-1]{1}[0-9]{1}[0-5]{1}[0-9]{1}")) && !(plannedStartTime.matches("2{1}[0-3]{1}[0-5]{1}[0-9]{1}")) ){
            return "Please enter a valid time";
        }
        String[] plannedStartTimeArr =  plannedStartTime.split("");
        for (int i = 0;i<2; i++){
            timeInMinutes = Integer.valueOf(plannedStartTimeArr[i])*60;
        }
        timeInMinutes += (Integer.valueOf(plannedStartTimeArr[2])*10 + Integer.valueOf(plannedStartTimeArr[3]));
        

        String hour = String.valueOf(timeInMinutes/60);
        if (hour.length()==1){
            hour = "0" + hour;
        }
        String minutes = String.valueOf(timeInMinutes % 60);
        if (minutes.length()==1){
            minutes = "0" + minutes;
        }
        return hour + ":" + minutes;

        
    }
    private String calculateRoute(String stationA, String stationB, Set<String> visited){
        StringBuilder s = new StringBuilder();
        //Add the current station to the visited list so we don't revisit it later from a neighbour
        visited.add(stationA);

        HashMap<String,Integer> stationNeighbours = rd.getEdges(stationA);

        //String set of all neighboring station names
        Set<String> neighbourNameSet = new HashSet<String>(stationNeighbours.keySet());

        ///If the target is one of stationA's neighbours
        if (neighbourNameSet.contains(stationB)){
            s.append(stationA);
            s.append(" -> ");
            s.append(stationB);
            //return the path from stationA to stationB
            return s.toString();
        }

        //remove from the neighbourNameSet all the stations that have already been visited
        neighbourNameSet.removeAll(visited);

        //current stationA has no neighbours that are unvisited
        if (neighbourNameSet.size() == 0) {
            return "";
        }

        //Store the immediate neighbours in a set
        Set<Map.Entry<String,Integer>> neighbourSet = new HashSet<Map.Entry<String,Integer>>(stationNeighbours.entrySet());

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
        for (Map.Entry<String,Integer> e : neighbourList){
            String neighbourStationName = e.getKey();

            Set<String> neighbourStationLines = rd.getStationLines(neighbourStationName);
            Set<String> currStationLines = rd.getStationLines(stationA);

            //Keep all lines on which both stationA and the neighbour station exist.
            neighbourStationLines.retainAll(currStationLines);

            //if the neighbour does not share
            if (neighbourStationLines.isEmpty()){
                // and the current station or the neighbour do not have step-free access, 
                if (!(accessibleStationSet.contains(neighbourStationName))|| !(accessibleStationSet.contains(neighbourStationName))){
                    //check the next neighbour.
                    continue;
                }
                else{

                }
            }

            


        }
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
