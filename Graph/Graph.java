// import Vertex;
// import Edge;
package Graph;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;




public class Graph<T> {
    //A 2D integer array to hold the weight of a given edge in the graph.
    private HashMap<T,HashMap<T,Integer>> adjWeightMatrix = new HashMap<T,HashMap<T,Integer>>();
    //The total number of vertices within a graph.
    private  HashMap<T, Integer> nodeEdgeCount = new HashMap<T, Integer>();
    private Map<T, List<T>> map = new HashMap<>();

    /**
     * Adds a new edge to the graph.
     * @param src The source node.
     * @param dst The target node.
     * @param weight the weight for the edge from @param src to @param dst .
     * @return Success (true) or Failure (false) to add the given edge.
     */
    public void addEdge(T src, T dst, int weight) {

        // src not exist and dst exist
        // src exist and dst exist

        //Source does not exist
        if (!adjWeightMatrix.containsKey(src)){
            HashMap<T,Integer> srcEdges = new HashMap<T,Integer>();
            
            //if dst does not exist, add it and create an edge to src
            if (!adjWeightMatrix.containsKey(dst)){

              HashMap<T,Integer> dstEdges = new HashMap<T,Integer>();
              dstEdges.put(src,weight);
              adjWeightMatrix.put(dst,dstEdges);
              

              srcEdges.put(dst,weight);
              adjWeightMatrix.put(src,srcEdges);

              nodeEdgeCount.put(dst, 1);
              nodeEdgeCount.put(src, 1);
              
            }
            //src does not exist, dst does exist
            else{
              HashMap<T,Integer> dstEdges = adjWeightMatrix.get(dst);
              dstEdges.put(src,weight);
              adjWeightMatrix.put(dst,dstEdges);

              srcEdges.put(dst,weight);
              adjWeightMatrix.put(src,srcEdges);

              nodeEdgeCount.put(dst, nodeEdgeCount.get(dst) + 1);
              nodeEdgeCount.put(src, 1);
            }
        }
        //src exists
        else{
          HashMap<T,Integer> srcEdges = adjWeightMatrix.get(src);
          //Src exists, dst does not exist
          //FAIL: Example: Duddeston - Birmingham New Street
          //Duddeston created first, BNS created during this failing mapping
          //BNS gets Duddeston, but Duddeston does not get BNS. Failure to map dst to src
          if(!adjWeightMatrix.containsKey(dst)){

            nodeEdgeCount.put(src, nodeEdgeCount.get(src) + 1);
            
              HashMap<T,Integer> dstEdges = new HashMap<T,Integer>();

              srcEdges.put(dst,weight);
              
              adjWeightMatrix.put(src,srcEdges);

              dstEdges.put(src,weight);
              adjWeightMatrix.put(dst,dstEdges);

              nodeEdgeCount.put(dst, 1);


          }
          //Src exists, dst exists
          else{
            HashMap<T,Integer> dstEdges = adjWeightMatrix.get(dst);
            
            srcEdges.put(dst,weight);
            adjWeightMatrix.put(src,srcEdges);

            dstEdges.put(src,weight);
            adjWeightMatrix.put(dst,dstEdges);

            nodeEdgeCount.put(dst, nodeEdgeCount.get(dst) + 1);

            
          } 

        }

        
    }
  
    // Remove edges
    // public void removeEdge(int src, int dst) {
    //   //Delete forward edge
    //   adjWeightMatrix[src][dst] = 0;
    //   //Delete backward edge
    //   adjWeightMatrix[dst][src] = 0;
    // }
  
    // Print the matrix
    
    public HashMap<T, Integer> getNodeEdgeCountMap(){
      return nodeEdgeCount;
    }

    public Integer getNodeEdgeCount(T node){
      return nodeEdgeCount.get(node);
    }

    public boolean containsKey(T key){
      return adjWeightMatrix.containsKey(key);
    }

    public HashMap<T,Integer> getEdges(T src){
      return adjWeightMatrix.get(src);
    }
    public HashMap<T,HashMap<T,Integer>> getEdges(){
      return adjWeightMatrix;
    }

    public Set<Map.Entry<T,HashMap<T,Integer>>> entrySet(){
      return adjWeightMatrix.entrySet();
    }
    public String toString() {
      StringBuilder s = new StringBuilder();
      Set<Map.Entry<T,HashMap<T,Integer>>> entrySet = new HashSet<Map.Entry<T,HashMap<T,Integer>>>();
      entrySet.addAll(adjWeightMatrix.entrySet());
      for (Map.Entry<T,HashMap<T,Integer>> e: entrySet){
        s.append(e);
        s.append("\n");
      }

        
      
      return s.toString();
    }

    

  }