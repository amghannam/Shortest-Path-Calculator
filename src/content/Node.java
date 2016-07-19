/**
 ********************************************************************
 * File: Node.java
 * Author: Ahmed Ghannam (0910337) 
 * 
 * Represents a node in a network or graph. Together with links, nodes make up 
 * the basic building blocks of a network. In actuality, these nodes may be routers, 
 * switches, or even complete networks. While we do not refer to them 
 * with any particular name in this project, it is easier to think of them as router 
 * devices that send and receive packets of data to other routers within the same network.
 */
package Content;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ahmed
 */
public class Node implements Comparable<Node> {
    
    public final String name; //The display name of this node
    
    /* The ID of this node. In multilayer networks, this value MUST start with a 'u' 
     * to indicate upper-layer nodes, and an 'l' to indicate lower layer nodes. */ 
    public final String id;  
    
    public List<Link> adjacencies; //The adjacent edges connecting this node
    
    /* The shortest path to this node this value is initialized to Infinity by default, 
     * as part of Dijkstra's algorithm. A node whose distance from another is marked as 
     * Infinity is unreachable from the current node. This is a common sight in the case 
     * of directed graphs. */
    public double minDistance = Double.POSITIVE_INFINITY; 
    
    public Node previous; //The node preceding this node. 

    /**
     * Constructs a Node object with its ID and name. 
     * 
     * @param argId The ID of this node.
     * @param argName The name of this node. 
     */
    public Node(String argId, String argName) {
        id = argId; 
        name = argName;
        adjacencies = new ArrayList<Link>();
    }
    
    /**
     * Sets the distance to this node. This value is dynamically changed and calculated 
     * as part of Dijkstra's algorithm. 
     * 
     * @param minDistance The value of the shortest path to a node. 
     */
    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }
    
    /**
     * Sets the node preceding this node. This is needed, for example,  
     * to display the actual shortest path taken rather than its cost. 
     * 
     * @param previous The previous node. 
     */
    public void setPrevious(Node previous) {
        this.previous = previous;
    }
    
    /**
     * Adds a new adjacent link to connect to a node. 
     * 
     * @param link The link to connect to the current node. 
     */
    public void addEdge(Link link) {
        adjacencies.add(link);
    }
    
    /** 
     * Returns the name of this node. This value can be anything as long as it fits 
     * the current scenario. For readability reasons, in multilayer networks, the name
     * should end with a numerical value representing the layer to which it belongs. 
     * As a general rule, nodes in the upper layer are referred to as A0, B0, etc.
     * whereas nodes in the lower layer are referred to as A1, B1, and so on.
     * 
     * @return The name of this node. 
     */ 
    public String toString() {
        return name; 
    }
    
    /**
     * Helper function to compare the distances of two nodes. 
     * 
     * @param other The node whose distance is to be compared with. 
     * @return Returns a numerical value indicating which is smaller/larger. 
     */
    public int compareTo(Node other) {
        return Double.compare(minDistance, other.minDistance);
    }
}
