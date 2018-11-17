import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Filename:   GraphImpl.java
 * Project:    p4
 * Course:     cs400 
 * Authors:    Aron Denenberg  
 * Due Date:   11/17/18
 * 
 * T is the label of a vertex, and List<T> is a list of
 * adjacent vertices for that vertex.
 *
 * Additional credits: 
 *
 * Bugs or other notes: 
 *
 * @param <T> type of a vertex
 */
public class GraphImpl<T> implements GraphADT<T> {

    // YOU MAY ADD ADDITIONAL private members
    // YOU MAY NOT ADD ADDITIONAL public members

    /**
     * Store the vertices and the vertice's adjacent vertices
     */
    private Map<T, List<T>> verticesMap; 
    
    
    /**
     * Construct and initialize and empty Graph
     */ 
    public GraphImpl() {
        verticesMap = new HashMap<T, List<T>>();
        // you may initialize additional data members here
    }
    
    /**
     * Adds given vertex to the graph. Returns without error if the input is null or is a vertex that already
     * exists in the graph
     * 
     * @param vertex to search for in graph
     * 
     */
    public void addVertex(T vertex) {
        if (vertex == null || verticesMap.containsKey(vertex)) {
        	return;
        }
        verticesMap.put(vertex, new ArrayList<T>());
    }
    
    /**
     * Removes the given vertex from the graph. Returns without error if the input is null or is a 
     * vertex that doesn't already exist in the graph
     */
    public void removeVertex(T vertex) {
    	if (vertex == null || !verticesMap.containsKey(vertex)) {
        	return;
        }
        verticesMap.remove(vertex);
    }
    
    /**
     * Adds edges between the two given vertices if they are both not null, both exist in the graph, and an edge does not 
     * already exist between them.
     * 
     * @param vertex1 is the vertex that the edge will be added from
     * @param vertex2 is the vertex that the edge will be added to
     */
    public void addEdge(T vertex1, T vertex2) {
        if ((vertex1 == null || vertex2 == null) || (!hasVertex(vertex1) || !hasVertex(vertex2))
        		|| edgeExists(vertex1, vertex2)) {
        	return;
        }
        verticesMap.get(vertex1).add(vertex2);
    }
    
    /**
     * Removes edges between the two given vertices if they are both not null, both exist in the graph, and an edge does 
     * already exist between them.
     * 
     * @param vertex1 is the vertex that the edge will be removed from
     * @param vertex2 is the vertex that the edge will be removed to
     */
    public void removeEdge(T vertex1, T vertex2) {
    	if ((vertex1 == null || vertex2 == null) || (!hasVertex(vertex1) || !hasVertex(vertex2))
        		|| !edgeExists(vertex1, vertex2)) {
        	return;
        }
    	verticesMap.get(vertex1).remove(vertex2);
    }    
    
    /**
     * Returns a set that contains all the vertices
     * 
     * @return Set of all the key values in the vertices map
     */
    public Set<T> getAllVertices() {
        return verticesMap.keySet();
    }
    
    /**
     * Retrieves all the vertices that are adjacent to the given vertex
     * 
     * @param vertex to check for adjacent vertices
     * @return list of all vertices adjacent to the given vertex
     */
    public List<T> getAdjacentVerticesOf(T vertex) {
    	// quit with nothing if the given vertex isn't in the graph
        if (!hasVertex(vertex)) {
        	return null;
        }
        
        // list of adjacent vertices to return, initialized with all vertices the 
        // given vertex has an edge to
        List<T> adjacentVertices = verticesMap.get(vertex);
        /*Set<T> allVertices = getAllVertices(); // set of all vertices in the graph to iterate over to see if
        // an edge exists to the given vertex
        Iterator<T> itr = allVertices.iterator(); // iterator over T values in the set of all vertices
        
        // Loop to iterate over all vertices and check whether they have an edge to the given vertex
        while (itr.hasNext()) {
        	T newVertex = itr.next();
        	
        	// skip vertex if it is the given vertex
        	if (newVertex.equals(vertex)) { continue; }
        	
        	// check if any of the vertex's edges point to the given vertex
        	for (T adjacent : verticesMap.get(newVertex)) {
        		if (adjacent.equals(vertex)) {
        			adjacentVertices.add(newVertex);
        			break;
        		}
        	}
        }*/
        return adjacentVertices;
    }
    
    /**
     * Checks whether the graph has the given value as a vertex
     * 
     * @param vertex to check if it exists in the graph
     * @return true if vertex exists in the graph, false otherwise
     */
    public boolean hasVertex(T vertex) {
    	return verticesMap.containsKey(vertex);        
    }
    
    /**
     * Gets the number of vertices in the graph
     * 
     * @return the number of vertices in the graph
     */
    public int order() {
        return getAllVertices().size();
    }
    
    /**
     * Gets the number of edges in the graph
     * 
     * @return number of edges in the graph
     */
    public int size() {
        Set<T> allVertices = getAllVertices(); // set of all vertices in the graph to iterate over
        Iterator<T> itr = allVertices.iterator(); // iterator over T values in the set of all vertices
        int size = 0; // will ultimately store the size of the graph to be returned, initialized to 0
        while (itr.hasNext()) {
        	size += verticesMap.get(itr.next()).size();
        }
        return size;
    }
    
    
    /**
     * Prints the graph for the reference
     * DO NOT EDIT THIS FUNCTION
     * DO ENSURE THAT YOUR verticesMap is being used 
     * to represent the vertices and edges of this graph.
     */
    public void printGraph() {

        for ( T vertex : verticesMap.keySet() ) {
            if ( verticesMap.get(vertex).size() != 0) {
                for (T edges : verticesMap.get(vertex)) {
                    System.out.println(vertex + " -> " + edges + " ");
                }
            } else {
                System.out.println(vertex + " -> " + " " );
            }
        }
    }
    
    /**
     * Private function to support the addEdge() function. Checks if an edge exists between two vertices
     * @param vertex1 is the from vertex to check for an edge
     * @param vertex2 is the to vertex to check for an edge
     * @return boolean value saying whether or not an edge exists between the two vertices
     */
    private boolean edgeExists(T vertex1, T vertex2) {
    	List<T> edges = verticesMap.get(vertex1);
    	for (T edge:edges) {
    		if (edge == vertex2) {
    			return true;
    		}
    	}
    	return false;
    	
    	
    }
}

