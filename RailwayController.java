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
        return "";
    }

    @Override   
    public String showAccessiblePath(String plannedStartTime, String stationA, String stationB){
        //Find a path between A and B, or the nearest accessible stations from each station
        String path = findAccessiblePath(stationA, stationB);
        
        return path;

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
    public String findAccessiblePath(String stationA, String stationB){
        StringBuilder s = new StringBuilder();


        //Retrieve all of the edges stored in the railway data graph
        HashMap<String,HashMap<String,Integer>> railwayNetwork = rd.getAllEdges();

        //An array list to be used as a queue to store the stations along the route found by this algorithm.
        ArrayList<String> route = new ArrayList<String>();

        //A set to record all of the stations visited by this algorithm.
        //Used to prevent an endless loop
        Set<String> visitedStations = new HashSet<String>();
        //a is not an accessible station
        if (!accessibleStationSet.contains(stationA)){
            s.append(stationA + " is not accessible. Replaced with the nearest accessible station: ");
            //find nearest stations to a that has accessibility
            stationA = findNearestAccessibleStation(stationA, new HashSet<String>());
            s.append(stationA + "\n\n");
            return s.toString();            
        }

        //b is not an accessible station
        if (!accessibleStationSet.contains(stationB)){
            //find nearest stations to b that has accessibility
            stationA = findNearestAccessibleStation(stationA, new HashSet<String>());
            return s.toString(); 
        }
        String routeString = getRouteString(rd.getEdges(stationA).entrySet());
        return  routeString;
    }


    public String getRouteString(ArrayList<String> stationNameArrList){
        StringBuilder s = new StringBuilder();
        for(String stationName : stationNameArrList){
            s.append(stationName);
            s.append(" -> ");
        }
        return s.toString();
    }
    public String getRouteString(Set<Map.Entry<String,Integer>> routeSet){
        StringBuilder s = new StringBuilder();
        for(Map.Entry<String,Integer> e : routeSet){
            s.append(e.getKey());
            s.append(" -> ");
        }
        return s.toString();
    }
    }
