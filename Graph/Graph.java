// import Vertex;
// import Edge;
package Graph;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;



/**
 * @author Kieran Detheridge
 * A generic Graph class to hold graph-like data.
 * The edges are stored in the form of a HashMap containing more HashMaps as values.
 * The adjWeightMap contains the entire graph as a map of nodes to other nodes indicating the presence of edges.
 */
public class Graph<T> {
    //A 2D integer array to hold the weight of a given edge in the graph.
    private HashMap<T,HashMap<T,Integer>> adjWeightMap = new HashMap<T,HashMap<T,Integer>>();
    //The total number of vertices within a graph.
    private  HashMap<T, Integer> nodeEdgeCount = new HashMap<T, Integer>();
    private Map<T, List<T>> map = new HashMap<>();

    /**
     * Adds a new edge to the graph.
     * @param src The source node.
     * @param dst The target node.
     * @param weight the weight for the edge from @param src to @param dst .
     */
    public void addEdge(T src, T dst, int weight) {

        // src not exist and dst exist
        // src exist and dst exist

        //Source does not exist
        if (!adjWeightMap.containsKey(src)){
            HashMap<T,Integer> srcEdges = new HashMap<T,Integer>();
            
            //if dst does not exist, add it and create an edge to src
            if (!adjWeightMap.containsKey(dst)){

              HashMap<T,Integer> dstEdges = new HashMap<T,Integer>();
              dstEdges.put(src,weight);
              adjWeightMap.put(dst,dstEdges);
              

              srcEdges.put(dst,weight);
              adjWeightMap.put(src,srcEdges);

              nodeEdgeCount.put(dst, 1);
              nodeEdgeCount.put(src, 1);
              
            }
            //src does not exist, dst does exist
            else{
              HashMap<T,Integer> dstEdges = adjWeightMap.get(dst);
              dstEdges.put(src,weight);
              adjWeightMap.put(dst,dstEdges);

              srcEdges.put(dst,weight);
              adjWeightMap.put(src,srcEdges);

              nodeEdgeCount.put(dst, nodeEdgeCount.get(dst) + 1);
              nodeEdgeCount.put(src, 1);
            }
        }
        //src exists
        else{
          HashMap<T,Integer> srcEdges = adjWeightMap.get(src);
          //Src exists, dst does not exist
          //FAIL: Example: Duddeston - Birmingham New Street
          //Duddeston created first, BNS created during this failing mapping
          //BNS gets Duddeston, but Duddeston does not get BNS. Failure to map dst to src
          if(!adjWeightMap.containsKey(dst)){

            nodeEdgeCount.put(src, nodeEdgeCount.get(src) + 1);
            
              HashMap<T,Integer> dstEdges = new HashMap<T,Integer>();

              srcEdges.put(dst,weight);
              
              adjWeightMap.put(src,srcEdges);

              dstEdges.put(src,weight);
              adjWeightMap.put(dst,dstEdges);

              nodeEdgeCount.put(dst, 1);


          }
          //Src exists, dst exists
          else{
            HashMap<T,Integer> dstEdges = adjWeightMap.get(dst);
            
            srcEdges.put(dst,weight);
            adjWeightMap.put(src,srcEdges);

            dstEdges.put(src,weight);
            adjWeightMap.put(dst,dstEdges);

            nodeEdgeCount.put(dst, nodeEdgeCount.get(dst) + 1);

            
          } 

        }

        
    }
  
    // Remove edges
    // public void removeEdge(int src, int dst) {
    //   //Delete forward edge
    //   adjWeightMap[src][dst] = 0;
    //   //Delete backward edge
    //   adjWeightMap[dst][src] = 0;
    // }
  
    // Print the matrix
    /**
     * @return a map containing the node keys and how many edges each has
     */
    public HashMap<T, Integer> getNodeEdgeCountMap(){
      return nodeEdgeCount;
    }
    /**
     * @param node The node to retrieve the edge count of.
     * @return The count of edges for the node.
     */
    public Integer getNodeEdgeCount(T node){
      return nodeEdgeCount.get(node);
    }
    /**
     * @param key the key to check for.
     * @return true or false.
     */
    public boolean containsKey(T key){
      return adjWeightMap.containsKey(key);
    }
    /**
     * @param key the key to grab the value of.
     */
    public HashMap<T,Integer> getEdges(T key){
      return adjWeightMap.get(key);
    }
    /**
     * @return all edges in the graph.
     */
    public HashMap<T,HashMap<T,Integer>> getEdges(){
      return adjWeightMap;
    }
    /**
     * Return the weight of an edge between two nodes.
     * @param nodeA the source node
     * @param nodeB the target node
     * @return the integer weight of the edge between nodeA and nodeB
     */
    public Integer getEdgeWeight(T nodeA, T nodeB){

      Integer valueFound = adjWeightMap.get(nodeA).get(nodeB);
      if(valueFound == null){
      return 0;
      }
      return valueFound;
    }

    /**
     * @return an entrySet view of all the edges in the graph.
     */
    public Set<Map.Entry<T,HashMap<T,Integer>>> entrySet(){
      return adjWeightMap.entrySet();
    }
    public String toString() {
      StringBuilder s = new StringBuilder();
      Set<Map.Entry<T,HashMap<T,Integer>>> entrySet = new HashSet<Map.Entry<T,HashMap<T,Integer>>>();
      entrySet.addAll(adjWeightMap.entrySet());
      for (Map.Entry<T,HashMap<T,Integer>> e: entrySet){
        s.append(e);
        s.append("\n");
      }

      return s.toString();
    }

  }