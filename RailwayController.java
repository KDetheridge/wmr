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
    public RailwayController(){
        rd = new RailwayData();
        lineIndexMap = new HashMap<String, String>();
        
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
    private String findNearestAccessibleStation(HashMap<String,Integer> station){
        //Store a set of the immediate neighbours
        ArrayList<Map.Entry<String,Integer>> neighbourList = new ArrayList(station.entrySet());
        //neighbourListAccessible
        //Set<Map.Entry<String,Integer>> stationNeighbours = station.entrySet();
        //intersect the neighbourList set with the accessibleStations set.
        //Get accessibleStations = O(1)
        //retainAll(intersection) is O(n), but reduces o(n) by (numAccessibleStations/numTotalStations) * numNeighbours)
        //in average case, but 0 in worst

        //sort is O(n)

        //Then the for loop to iterate over the sorted list


        //1 + n + n + n = O(n)

        //Sort the neighbourList in value ascending order - to order the neighbours by distance from the current station
        Collections.sort(neighbourList , new Comparator<Map.Entry<String, Integer>>() {

            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
                return o1.getValue().compareTo(o2.getValue());
            }
    
        });

        for (Map.Entry<String,Integer) e : neighbourlist){

        }

    }
    public String findAccessiblePath(String stationA, String stationB){
        //A set of all stations that are wheelchair accessible.
        Set<String> accessibleStations = rd.getAccessibleStations();

        //Retrieve all of the edges stored in the railway data graph
        HashMap<String,HashMap<String,Integer>> railwayNetwork = rd.getAllEdges();

        //An array list to be used as a queue to store the stations along the route found by this algorithm.
        ArrayList<String> route = new ArrayList<String>();

        //A set to record all of the stations visited by this algorithm.
        //Used to prevent an endless loop
        Set<String> visitedStations = new HashSet<String>();
        //a is not an accessible station
        if (!accessibleStations.contains(stationA)){
            //find nearest stations to a that has accessibility
        }

        //b is not an accessible station
        if (!accessibleStations.contains(stationB)){
            //find nearest stations to b that has accessibility
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
