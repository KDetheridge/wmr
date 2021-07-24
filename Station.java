/**
 * A class to house the information for a single station.
 * A station must have a name, and can be accessible or inaccessible to wheelchair users.
 */
public class Station extends Graph.Vertex{
    private String stationName;
    private String lineName;
    private int travelTime;
    //Indicate the step-free accessibility for wheelchair users
    private Boolean accessible;
    public Station(String stationName, String lineName, int travelTime){
        //Call the Vertex superclass constructor and pass in the station name as the ID.
        super(stationName);

        this.stationName = stationName;
        this.travelTime = travelTime;
        this.lineName = lineName;
    }

    /**
     * Return the name of the station
     */
    public String getName() {
        return stationName;
    }
    /**
     * Indicate whether or not this station is accessible
     */
    public Boolean isAccessible() {
        return accessible;
    }

    public void setAccessible(Boolean accessible){
        this.accessible = accessible;
    }
}
