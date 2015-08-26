package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a node manager.
 * @author Saulo Toledo
 *
 * @param <T> The node type.
 */
public class NodeManager<T> {

    /**
     * A node map to avoid repetitions. Each node is registered here when
     * created.
     */
    private Map<T, Node<T>> nodeMap = new HashMap<T, Node<T>>();

    /**
     * Returns a registered node or creates a new one, if it does not exists.
     *
     * @return The node with the informed data.
     */
    public Node<T> getNode(T data) {
        Node<T> node;
        if (this.nodeExists(data)) {
            node = this.nodeMap.get(data);
        } else {
            node = new Node<T>(data);
            this.nodeMap.put(data, node);
        }
        return node;
    }

    /**
     * Verify if a node exists by using its data.
     *
     * @param  data The node data.
     * @return true if the node exists, false otherwise.
     */
    public boolean nodeExists(T data) {
        return this.nodeMap.keySet().contains(data);
    }

    /**
     * Verify if a node exists.
     *
     * @param  element The node to find.
     * @return true if the node exists, false otherwise.
     */
    public boolean nodeExists(Node<T> element) {
        return this.nodeMap.containsValue(element);
    }
}
