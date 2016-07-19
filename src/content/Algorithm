/**
 ************************************************************
 * File: Dijkstra.java 
 * Author: Ahmed Ghannam (0910337) 
 * 
 * Implements Dijkstra's shortest-path algorithm. This class contains two basic 
 * versions of Dijkstra's algorithm: the binary heap version, and the Fibonacci 
 * heap version.  
 * 
 * Dijkstra's algorithm is a greedy algorithm that works by maintaining a 
 * priority queue of nodes whose priorities are the lengths of some path from 
 * the source node to the node in question.  At each step, the algorithm dequeues 
 * a node from this priority queue, records that node as being at the indicated
 * distance from the source, and then updates the priorities of all nodes
 * in the graph by considering all outgoing edges from the recently-
 * dequeued node to those nodes.
 *
 * In the course of this algorithm, the code makes up to |E| calls to
 * decrease-key on the heap (since in the worst case every edge from every
 * node will yield a shorter path to some node than before) and |V| calls
 * to dequeue-min (since each node is removed from the priority queue
 * at most once).  Using a Fibonacci heap, this gives a very good runtime
 * guarantee of O(|E| + |V| log |V|).
 */
package Content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 *
 * @author Ahmed
 */
public class Dijkstra {
    
    /** 
     * Applies Dijkstra's algorithm to compute the shortest paths from a given source node
     * to all other nodes in this graph.  This implementation uses a binary heap.
     * 
     * @param graph An array representation of the graph containing only the nodes. 
     * @param source The source node from which the shortest paths are calculated.
     */
    public void computePathsBinaryHeap(Node[] graph, Node source) {
        reinitializeGraph(graph);
        source.minDistance = 0.;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(source);

        while (!pq.isEmpty()) {
            Node u = pq.poll();

            for (Link e : u.adjacencies) {
                Node v = e.target;
                double cost = e.cost;
                double distanceThroughU = u.minDistance + cost;
                if (distanceThroughU < v.minDistance) {
                    pq.remove(v);
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    pq.add(v);
                }
            }
        }
    }
    
    /** 
     * Applies Dijkstra's algorithm to compute the shortest path from a source node 
     * to a destination node in a single iteration.  This is a modified version 
     * implemented to fit multilayer architectures.  Also uses a binary heap.  
     * 
     * @param graph An array representation of the graph containing all nodes across all layers.
     * @param source An upper-layer source node from which to find the shortest path.
     * @param dest An upper-layer target node to which the shortest path is calculated. 
     */
    public void dijkstraMultilayer(Node[] graph, Node source, Node dest) {
        reinitializeGraph(graph);
        source.minDistance = 0.;
        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(source);

        while (!pq.isEmpty()) {
            Node u = pq.poll();

            for (Link e : u.adjacencies) {
                dest = e.target;
                double cost = e.cost;
                double distanceThroughU = u.minDistance + cost;
                if (distanceThroughU < dest.minDistance) {
                    pq.remove(dest);
                    dest.minDistance = distanceThroughU;
                    dest.previous = u;
                    pq.add(dest);
                }
            }
        }
    }

    /**
     * Given a directed, weighted graph G and a source node s, produces the
     * distances from s to each other node in the graph. If any nodes in the
     * graph are unreachable from s, they will be reported at distance
     * +infinity. This implementation uses a Fibonacci heap. 
     *
     * @param graph The graph upon which to run Dijkstra's algorithm.
     * @param source The source node in the graph.
     * @return A map from nodes in the graph to their distances from the source.
     */
    public <Node> Map<Node, Double> computePathsFibonacciHeap(Graph<Node> graph, Node source) {
        /* Create a Fibonacci heap storing the distances of unvisited nodes
         * from the source node.
         */
        FibonacciHeap<Node> priorityQueue = new FibonacciHeap<>();

        /* The Fibonacci heap uses an internal representation that hands back
         * Entry objects for every stored element.  This map associates each
         * node in the graph with its corresponding Entry.
         */
        Map<Node, FibonacciHeap.Entry<Node>> entries = new HashMap<>();

        /* Maintain a map from nodes to their distances.  Whenever we expand a
         * node for the first time, we'll put it in here.
         */
        Map<Node, Double> result = new HashMap<>();

        /* Add each node to the Fibonacci heap at distance +infinity since
         * initially all nodes are unreachable.
         */
        for (Node node : graph) {
            entries.put(node, priorityQueue.enqueue(node, Double.POSITIVE_INFINITY));
        }

        /* Update the source so that it's at distance 0.0 from itself; after
         * all, we can get there with a path of length zero!
         */
        priorityQueue.decreaseKey(entries.get(source), 0.0);

        /* Keep processing the queue until no nodes remain. */
        while (!priorityQueue.isEmpty()) {
            /* Grab the current node.  The algorithm guarantees that we now
             * have the shortest distance to it.
             */
            FibonacciHeap.Entry<Node> curr = priorityQueue.dequeueMin();

            /* Store this in the result table. */
            result.put(curr.getValue(), curr.getPriority());

            /* Update the priorities of all of its edges. */
            for (Map.Entry<Node, Double> arc : graph.edgesFrom(curr.getValue()).entrySet()) {
                /* If we already know the shortest path from the source to
                 * this node, don't add the edge.
                 */
                if (result.containsKey(arc.getKey())) {
                    continue;
                }

                /* Compute the cost of the path from the source to this node,
                 * which is the cost of this node plus the cost of this edge.
                 */
                double pathCost = curr.getPriority() + arc.getValue();

                /* If the length of the best-known path from the source to
                 * this node is longer than this potential path cost, update
                 * the cost of the shortest path accordingly.
                 */
                FibonacciHeap.Entry<Node> dest = entries.get(arc.getKey());
                if (pathCost < dest.getPriority()) {
                    priorityQueue.decreaseKey(dest, pathCost);
                }
            }
        }

        /* Finally, report the distances we've found. */
        return result;
    }
    
    /**
     * A modified version of Dijkstra's algorithm to find the cost of the shortest path 
     * from a source node to a target node in a multilayer network.  Uses a Fibonacci heap. 
     *   
     * @param graph The graph upon which to run Dijkstra's algorithm. 
     * @param source An upper-layer source node from which to find the shortest path. 
     * @param target An upper-layer target node to which the shortest path is calculated. 
     * @return A map containing the calculated cost of the shortest multilayer path.  
     */
    public <Node> Map<Node, Double> dijkstraFibonacciHeapMultilayer(Graph<Node> graph, Node source, Node target) {
        FibonacciHeap<Node> priorityQueue = new FibonacciHeap<>();
        Map<Node, FibonacciHeap.Entry<Node>> entries = new HashMap<>();
        Map<Node, Double> result = new HashMap<>();
        
        for (Node node : graph) {
            entries.put(node, priorityQueue.enqueue(node, Double.POSITIVE_INFINITY));
        }
        priorityQueue.decreaseKey(entries.get(source), 0.0);

        while (!priorityQueue.isEmpty()) {
            FibonacciHeap.Entry<Node> curr = priorityQueue.dequeueMin();
            if (curr.getValue().toString().equals(target.toString())) {
                result.put(curr.getValue(), curr.getPriority());
            }
            
            for (Map.Entry<Node, Double> arc : graph.edgesFrom(curr.getValue()).entrySet()) {
                if (result.containsKey(arc.getKey())) {
                    continue;
                }
                double pathCost = curr.getPriority() + arc.getValue();

                FibonacciHeap.Entry<Node> dest = entries.get(arc.getKey());
                if (pathCost < dest.getPriority()) {
                    priorityQueue.decreaseKey(dest, pathCost);
                }
            }
        }
        return result;
    }
    
    /**
     * Prints the actual shortest path from a source node to an arbitrary target node. 
     * This method relies on the binary heap versions of Dijkstra's algorithm; 
     * it does not work with the Fibonacci heap implementation.  More specifically, 
     * either one of <code>computePathsBinaryHeap</code> or <code>dijkstraMultilayer</code> 
     * must have been executed beforehand, otherwise a null path will be returned. 
     * 
     * @param target The node to which the shortest path is printed. 
     * @return A list containing the nodes leading up to the specified target. 
     */
    public List<Node> getShortestPathTo(Node target) {
        List<Node> path = new ArrayList<>();
        for (Node node = target; node != null; node = node.previous) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }
    
    /** 
     * Helper function to reintialize a graph in order to run Dijkstra's algorithm 
     * for different arbitrary input parameters at runtime. Reintializing guarantees that no costs 
     * or paths will overwrite one another upon successive executions of the algorithm, 
     * which is especially useful when computing the shortest paths from all nodes 
     * in a graph at once.
     * 
     * @param nodes A list representing the nodes in a given graph. 
     */
    private void reinitializeGraph(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].setMinDistance(Double.POSITIVE_INFINITY);
            nodes[i].setPrevious(null);
        }
    }
}
