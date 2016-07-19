/**
 ********************************************************************
 * File: Link.java
 * Author: Ahmed Ghannam (0910337)
 * 
 * Represents a link connecting two nodes in a network. All links are associated
 * with a real-valued number that represents their cost, or weight. This value, 
 * which does not necessarily refer to a distance, is the basis on which the shortest 
 * path between connecting any two nodes is calculated. 
 */
package Content;

/**
 *
 * @author Ahmed
 */
public class Link {

    public final Node target; // The node on the other end of this link
    public final double cost; // The associated cost 
    
    /**
     * Constructs a new link containing a specified end-node and an associated 
     * cost representing the distance to this end-node. 
     * 
     * @param argTarget The node to which this link leads. 
     * @param argCost The cost or weight of this link. 
     */
    public Link(Node argTarget, double argCost) {
        target = argTarget;
        cost = argCost;
    }
}
