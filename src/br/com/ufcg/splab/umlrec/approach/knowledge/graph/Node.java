package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Creates a node from a simple graph.
 * @author Saulo Toledo
 *
 * @param <T> The node type.
 */
public class Node<T> {

    /**
     * The node's parents list.
     */
    private Set<Node<T>> parents = new HashSet<Node<T>>();

    /**
     * The node's children list.
     */
    private Set<Node<T>> children = new HashSet<Node<T>>();

    /**
     * The node's data.
     */
    private T data;

    /**
     * This node's attributes list.
     */
    private Set<NodeAttribute> attributes = new HashSet<NodeAttribute>();


    /**
     * Creates a node with the informed data.
     *
     * @param data The node data.
     */
    Node(T data) {
        this.data = data;
    }

    /**
     * Returns the node's data.
     *
     * @return The node's data.
     */
    public T getData() {
        return this.data;
    }

    /**
     * Sets the node's data.
     * @param data The new node's data.
     */
    public Node<T> setData(T data) {
        this.data = data;
        return this;
    }

    /**
     * Returns the node's children list.
     *
     * @return The node's children list.
     */
    public Set<Node<T>> getChildren() {
        return this.children;
    }

    /**
     * Returns the node's parents list.
     *
     * @return The node's parents list.
     */
    public Set<Node<T>> getParents() {
        return this.parents;
    }

    /**
     * Adds a node as a parent of the current node, if it is not there already.
     *
     * @param parent The future parent node.
     */
    public boolean addParent(Node<T> parent) {
        if (this.parents.contains(parent)) {
            return false;
        }

        this.parents.add(parent);
        parent.addChild(this);

        return true;
    }

    /**
     * Removes a node from the current one's parents list
     *
     * @param  parent The parent node to remove.
     * @return true if the node is a parent of the current node, false
     *         otherwise.
     */
    public boolean removeParent(Node<T> parent) {
        boolean result = false;
        result = this.parents.remove(parent);

        if (parent.isParentOf(this)) {
            // This "OR" is necessary because of the recursion:
            result = parent.removeChild(this) || result;
        }

        return result;
    }

    /**
     * Adds a node as a child of the current node.
     *
     * @param parent The future child node.
     */
    public boolean addChild(Node<T> child) {
        if (this.children.contains(child)) {
            return false;
        }

        this.children.add(child);
        child.addParent(this);

        return true;
    }

    /**
     * Removes a node from the current one's children list.
     *
     * @param  child The child node to remove.
     * @return true if the node is a child of the current node, false
     *         otherwise.
     */
    public boolean removeChild(Node<T> child) {
        boolean result;
        result = this.children.remove(child);

        if (child.isChildOf(this)) {
            // This "OR" is necessary because of the recursion:
            result = child.removeParent(this) || result;
        }

        return result;
    }

    /**
     * Verify if the current node is root (a node that has no parents).
     *
     * @return true if root, false otherwise.
     */
    public boolean isRoot() {
        return (this.parents.size() == 0);
    }

    /**
     * Verify if the current node is leaf (a node that has no children).
     *
     * @return true if leaf, false otherwise.
     */
    public boolean isLeaf() {
        return (this.children.size() == 0);
    }

    /**
     * Adds a new attribute to the current node.
     *
     * @param  attribute The attribute to add.
     * @return true if the attribute does not exist in the current node neither
     *         the parents nor the children, false otherwise.
     */
    public boolean addAttribute(NodeAttribute attribute) {
        Set<NodeAttribute> allAttributes = this.getAllInheritedAttributes();
        Set<NodeAttribute> childrenAttributes = this.getAllChildrenAttributes();

        if (!this.getAttributes().contains(attribute)
                && !allAttributes.contains(attribute)
                && !childrenAttributes.contains(attribute)) {
            this.attributes.add(attribute);
            return true;
        }

        return false;
    }

    /**
     * Removes an attribute from the current node.
     *
     * @param  attribute The attribute to remove.
     * @return true if the attribute exists and was successfully removed, false
     *         otherwise.
     */
    public boolean removeAttribute(NodeAttribute attribute) {
        return this.attributes.remove(attribute);
    }

    /**
     * Returns the node's own attributes.
     *
     * @return The node's own attributes.
     */
    public Set<NodeAttribute> getAttributes() {
        return this.attributes;
    }

    /**
     * Returns all the node's attributes (own plus inherited ones).
     *
     * @return All the node's attributes.
     */
    public Set<NodeAttribute> getAllAttributes() {
        Set<NodeAttribute> allAttributes = this.getAllInheritedAttributes();

        allAttributes.addAll(this.getAttributes());

        return new HashSet<NodeAttribute>(allAttributes);
    }

    /**
     * Returns all inherited attributes.
     *
     * @return All inherited attributes.
     */
    private Set<NodeAttribute> getAllInheritedAttributes() {
        Set<NodeAttribute> allAttributes = new HashSet<NodeAttribute>();

        for (Node<T> parent : this.getParents()) {
            allAttributes.addAll(parent.getAllAttributes());
        }

        return allAttributes;
    }

    /**
     * Returns all children's attributes.
     *
     * @return All children's attributes.
     */
    private Set<NodeAttribute> getAllChildrenAttributes() {
        Set<NodeAttribute> allAttributes = new HashSet<NodeAttribute>();

        for (Node<T> child : this.getChildren()) {
            allAttributes.addAll(child.getAttributes());
            allAttributes.addAll(child.getAllChildrenAttributes());
        }

        return allAttributes;
    }

    /**
     * Verify if the current node is child of another one.
     *
     * @param  node The reference node.
     * @return true if the current note is child of the reference node, false
     *         otherwise.
     */
    public boolean isChildOf(Node<T> node) {
        return this.parents.contains(node);
    }

    /**
     * Verify if the current node is parent of another one.
     *
     * @param  node The reference node.
     * @return true if the current note is parent of the reference node, false
     *         otherwise.
     */
    public boolean isParentOf(Node<T> node) {
        return this.children.contains(node);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node<?>) {
            return this.getData().equals(((Node<?>) obj).getData());
        }
        return false;
    }

    /**
     * According to java docs, this method "returns a hash code value for the
     * object" and "if two objects are equal according to the equals(Object)
     * method, then calling the hashCode method on each of the two objects must
     * produce the same integer result". HashSets, for example, won't even
     * invoke the equals method if their hashCodes are different. The typical
     * implementation is to convert the internal address of the object into an
     * integer, but we change this behavior here to certify that the objects
     * with the same name have the same hashCode.
     *
     * @see http://docs.oracle.com/javase/6/docs/api/java/lang/Object.html#hashCode
     */
    @Override
    public int hashCode() {
        return this.getData().toString().length();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        return String.format("%s: %s", this.getClass().getSimpleName(), this.getData().toString());
    }

    /**
     * Returns a set of paths containing all paths from this node to the root
     * element. Each path is represented as a list where the first element
     * is the current node and the last one is the root element.
     *
     * @return A set of paths containing all paths from this node to the root
     *         element.
     */
    public Set<LinkedList<Node<T>>> getAllPathsToRoot() {
        return this.buildPaths(this);
    }

    /**
     * Returns a set of paths containing all paths from a reference node to the
     * root element. Each path is represented as a list where the first element
     * is the reference node and the last one is the root element.
     *
     * @param  referenceNode The reference node.
     * @return A set of paths containing all paths from a reference node to the
     *         root element.
     */
    private Set<LinkedList<Node<T>>> buildPaths(Node<T> referenceNode) {

        Set<LinkedList<Node<T>>> partialPaths = new HashSet<LinkedList<Node<T>>>();

        for (Node<T> parent : referenceNode.getParents()) {
            partialPaths.addAll(this.buildPaths(parent));
        }

        for (LinkedList<Node<T>> partialPath : partialPaths) {
            partialPath.addFirst(referenceNode);
        }

        if (referenceNode.getParents().size() == 0) {
        	LinkedList<Node<T>> pathSet = new LinkedList<Node<T>>();
        	pathSet.add(referenceNode);
            partialPaths.add(pathSet);
        }

        return partialPaths;
    }

    /**
     * Returns a set of paths from the current node to its k-nth ancestor,
     * ignoring only begotten fathers at results. Each path is represented as a
     * list where the first element is the reference node and the last one is
     * the root element.
     *
     * @param  k The number of ancestors to return. This parameter ignores only
     *         begotten fathers at counts.
     * @return A set of possible paths for the given parameter.
     */
    public Set<LinkedList<Node<T>>> getSubgraphMaxHeightPaths(int k) {
        return this.getSubgraphMaxHeightPaths(k, true);
    }

    /**
     * Returns a set of paths from the current node to its k-nth ancestor. Each
     * path is represented as a list where the first element is the reference
     * node and the last one is the k-nth ancestor element.
     *
     * @param  k The number of ancestors to return. This parameter ignores only
     *         begotten fathers at counts.
     * @param  ignoreOnlyBegottenFathers If false, only begotten fathers,
     *         despite ignored at counts, are shown at result.
     * @return A set of possible paths for the given parameters.
     */
    public Set<LinkedList<Node<T>>> getSubgraphMaxHeightPaths(
            int k, boolean ignoreOnlyBegottenFathers) {
        return this.buildSubgraphMaxHeightPaths(this, k, ignoreOnlyBegottenFathers);
    }

    /**
     * Returns a set of paths from a reference node to its k-nth ancestor. Each
     * path is represented as a list where the first element is the reference
     * node and the last one is the k-nth ancestor element.
     *
     * @param referenceNode The reference node.
     * @param  k The number of ancestors to return. This parameter ignores only
     *         begotten fathers at counts.
     * @param  ignoreOnlyBegottenFathers If false, only begotten fathers,
     *         despite ignored at counts, are shown at result.
     * @return A set of possible paths for the given parameters.
     */
    private Set<LinkedList<Node<T>>> buildSubgraphMaxHeightPaths(
            Node<T> referenceNode, int k, boolean ignoreOnlyBegottenFathers) {

        Set<LinkedList<Node<T>>> partialPaths =
                new HashSet<LinkedList<Node<T>>>();

        if (k > 0) {
            int iteractionK;
            for (Node<T> parent : referenceNode.getParents()) {

                if (referenceNode.getChildren().size() == 1) {
                    iteractionK = k;
                } else {
                    iteractionK = k - 1;
                }

                partialPaths.addAll(this.buildSubgraphMaxHeightPaths(
                    parent,
                    iteractionK,
                    ignoreOnlyBegottenFathers
                ));
            }
        }

        for (LinkedList<Node<T>> partialPath : partialPaths) {
            if (ignoreOnlyBegottenFathers) {
                if (referenceNode.getChildren().size() != 1) {
                    partialPath.addFirst(referenceNode);
                }
            } else {
                partialPath.addFirst(referenceNode);
            }
        }

        if (k == 0) {
            LinkedList<Node<T>> pathSet = new LinkedList<Node<T>>();
            pathSet.add(referenceNode);
            partialPaths.add(pathSet);
        }

        return partialPaths;
    }

    /**
     * Returns the distances from the current node to each reference node
     * considering the number of ancestors. This distance is calculated in two
     * steps: (i) by navigating to all k-nths ancestors and (ii) by finding each
     * node among the reference set that is descendant of the current ancestor.
     * The unreachable nodes at reference set for the given k are removed from
     * the result. The distance increases by 1 each time that we move from a
     * node to another. This method automatically ignores only begotten fathers
     * at results.
     *
     * @param  referenceNodes A set of reference nodes.
     * @param  k The current node's max ancestor to visit before start finding
     *         the reference nodes.
     * @return A map with the distances from the current node to each reference
     *         node, where the key is a node and the value is the distance
     *         relative to the current node.
     */
    public Map<Node<T>, Integer> getDistancesTo(
            Set<Node<T>> referenceNodes, int k) {
        return this.getDistancesTo(referenceNodes, k, true);
    }

    /**
     * Returns the distances from the current node to each reference node
     * considering the number of ancestors. This distance is calculated in two
     * steps: (i) by navigating to all k-nths ancestors and (ii) by finding each
     * node among the reference set elements that is descendant of the current
     * ancestor. The unreachable nodes at reference set for the given k are
     * removed from the result. The distance increases by 1 each time that we
     * move from a node to another.
     *
     * @param  referenceNodes A set of reference nodes.
     * @param  k The current node's max ancestor to visit before start finding
     *         the reference nodes.
     * @param  ignoreOnlyBegottenFathers Allows to define if only begotten
     *         fathers are ignored or not at results.
     * @return A map with the distances from the current node to each reference
     *         node, where the key is a node and the value is the distance
     *         relative to the current node.
     */
    public Map<Node<T>, Integer> getDistancesTo(
            Set<Node<T>> referenceNodes, int k,
            boolean ignoreOnlyBegottenFathers) {

         Map<Node<T>, Integer> result = new HashMap<Node<T>, Integer>();
         Set<Node<T>> maxNodesFromK = this.extractMaxNodesFromK(k);

         for (Node<T> pseudoRoot : maxNodesFromK) {
             Map<Node<T>, Integer> partialDistances =
                     pseudoRoot.getBFSDistancesAtDescendantsTo(
                             referenceNodes, ignoreOnlyBegottenFathers);

             for (Node<T> node : partialDistances.keySet()) {
                 Integer distance = k + partialDistances.get(node);

                 if (!result.containsKey(node)) {
                     result.put(node, distance);
                 } else {
                     Integer currentValue = result.get(node);
                     if (distance < currentValue) {
                         result.remove(node);
                         result.put(node, distance);
                     }
                 }
             }
         }

         return result;
     }

    /**
     * Returns all distances from the current node to a set of reference nodes
     * that are his descendants. If the reference node is not a descendant of
     * the current one, it will be ignored at results.
     *
     * @param  referenceNodes A set of reference nodes to consider at results.
     * @param  ignoreOnlyBegottenFathers Allows to define if only begotten
     *         fathers are ignored or not at results.
     * @return A map with the distances from the current node to each descendant
     *         that are at reference nodes list. At result, the key is a node
     *         and the value is the distance relative to the current node.
     */
    private Map<Node<T>, Integer> getBFSDistancesAtDescendantsTo(
            Set<Node<T>> referenceNodes, boolean ignoreOnlyBegottenFathers)
    {
        Map<Node<T>, LinkedList<Node<T>>> paths =
                this.getBFSPathsAtDescendantsTo(
                        referenceNodes, ignoreOnlyBegottenFathers);

        Map<Node<T>, Integer> result = new HashMap<Node<T>, Integer>();

        for (Node<T> referenceNode : paths.keySet()) {
            result.put(referenceNode, paths.get(referenceNode).size() - 1);
        }

        return result;
    }

    /**
     * Returns all the shortest paths from the current node to each descendant
     * that is at reference nodes set (one path for each descendant). The result
     * is reached by applying a Breadth First Search (BFS) approach. If there
     * are two paths with the same length, the algorithm will return only one of
     * them.
     *
     * @TODO: Different paths can result in different affected descendants. The BFS approach chooses automatically what path with the same length will be returned and this will impact at algorithm results. A deepest analysis must be done here.
     *
     * @param  referenceNodes A set of reference nodes to consider at results.
     * @param  ignoreOnlyBegottenFathers Allows to define if only begotten
     *         fathers are ignored or not at results.
     * @return A map with the paths from the current node to each descendant
     *         that are at reference nodes list. At result, the key is a node
     *         and the value is the full path considering the
     *         ignoreOnlyBegottenFathers parameter.
     */
    private Map<Node<T>, LinkedList<Node<T>>> getBFSPathsAtDescendantsTo(
            Set<Node<T>> referenceNodes, boolean ignoreOnlyBegottenFathers)
    {
        // Breadth First Search algorithm:
        List<Node<T>> nodesQueue  = new LinkedList<Node<T>>();
        Map<Node<T>, Node<T>> cameFrom = new HashMap<Node<T>, Node<T>>();

        nodesQueue.add(this);
        cameFrom.put(this, null);

        Node<T> current;
        while (nodesQueue.size() != 0) {
            current = nodesQueue.remove(0);

            for (Node<T> child : current.getChildren()) {
                if (!cameFrom.keySet().contains(child)) {
                    nodesQueue.add(child);
                    cameFrom.put(child, current);
                }
            }
        }

        // Paths construction:
        Map<Node<T>, LinkedList<Node<T>>> result =
                new HashMap<Node<T>, LinkedList<Node<T>>>();

        for (Node<T> node : referenceNodes) {
            // Unreachable nodes are not at cameFrom map:
            if (cameFrom.keySet().contains(node)) {
                LinkedList<Node<T>> path = this.bfsReconstructPath(
                        cameFrom, node, ignoreOnlyBegottenFathers);
                result.put(node, path);
            }
        }

        return result;
    }

    /**
     * Builds a path based on a node mapping built by a Breadth First Search
     * (BFS) approach
     *
     * @param  cameFrom A node mapping built by a Breadth First Search (BFS)
     *         approach.
     * @param  goal The reference node to build the path.
     * @param  ignoreOnlyBegottenFathers Allows to define if only begotten
     *         fathers are ignored or not at results.
     * @return A list containing the path from the higher node at cameFrom
     *         mapping to the goal node.
     */
    private LinkedList<Node<T>> bfsReconstructPath(
            Map<Node<T>, Node<T>> cameFrom, Node<T> goal,
            boolean ignoreOnlyBegottenFathers)
    {
        LinkedList<Node<T>> path = new LinkedList<Node<T>>();

        Node<T> current = goal;
        path.add(current);
        while (cameFrom.get(current) != null) {
            current = cameFrom.get(current);

            if (ignoreOnlyBegottenFathers) {
                if (current.getChildren().size() != 1) {
                    path.addFirst(current);
                }
            } else {
                path.addFirst(current);
            }
        }

        return path;
    }

    /**
     * Returns a set containing all the k-nth ancestors of this node. It gets
     * all possible paths for the given parameter and extracts their last
     * nodes.
     *
     * @param  k The level of the ancestor to return.
     * @return A set containing all the k-nth ancestors of this node.
     */
    private Set<Node<T>> extractMaxNodesFromK(int k) {
        Set<LinkedList<Node<T>>> allPaths =
                 this.getSubgraphMaxHeightPaths(k, true);

        Set<Node<T>> result = new HashSet<Node<T>>();
        Node<T> maxNode;
        for (LinkedList<Node<T>> path : allPaths) {
            maxNode = path.get(path.size() - 1);
            if (!result.contains(maxNode)) {
                result.add(maxNode);
            }
        }
        return result;
    }
}
