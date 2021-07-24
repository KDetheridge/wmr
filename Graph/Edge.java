/**
 * 
 * A class to represent a weighted edge between two vertices in a given graph.
 * 
 * @author K Detheridge
 * @version 31/05/2021
 */
public class Edge {
    private Vertex v1;
    private Vertex v2;
    private int weight;

    public Edge(Vertex v1, Vertex v2, int weight) {
        this.v1 = v1;
        this.v2 = v2;
        this.weight = weight;
    }
}