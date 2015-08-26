package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import java.util.HashMap;
import java.util.Map;

public class NodeManager<T> {

    private Map<T, Node<T>> nodeMap = new HashMap<T, Node<T>>();

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

    public boolean nodeExists(T data) {
        return this.nodeMap.keySet().contains(data);
    }

    public boolean nodeExists(Node<T> element) {
        return this.nodeMap.containsValue(element);
    }
}
