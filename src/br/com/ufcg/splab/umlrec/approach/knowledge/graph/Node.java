package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import java.util.HashSet;
import java.util.LinkedList;
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

        for (Node<T> parent: this.getParents()) {
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

        for (Node<T> child: this.getChildren()) {
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
     * produce the same integer result".
     * HashSets, for example, won't even invoke the equals method if their
     * hashCodes are different.
     * The typical implementation is to convert the internal address of the
     * object into an integer, but we change this behavior here to certify that
     * the objects with the same name have the same hashCode.
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

        for (Node<T> parent: referenceNode.getParents()) {
            partialPaths.addAll(this.buildPaths(parent));
        }

        for (LinkedList<Node<T>> partialPath: partialPaths) {
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
     * TODO: doc
     */
    public Set<LinkedList<Node<T>>> getSubgraphMaxHeightPath(int k) {
        return this.getSubgraphMaxHeightPath(k, true);
    }

    /**
     * TODO: doc
     */
    public Set<LinkedList<Node<T>>> getSubgraphMaxHeightPath(
            int k, boolean ignoreOnlyBegottenFathers) {
        return this.buildSubgraphMaxHeightPath(this, k, ignoreOnlyBegottenFathers);
    }

    /**
     * TODO: doc
     */
    private Set<LinkedList<Node<T>>> buildSubgraphMaxHeightPath(
            Node<T> referenceNode, int k, boolean ignoreOnlyBegottenFathers) {

        Set<LinkedList<Node<T>>> partialPaths = new HashSet<LinkedList<Node<T>>>();


        if (k > 0) {

            int iteractionK;
            for (Node<T> parent: referenceNode.getParents()) {

                if (referenceNode.getChildren().size() == 1) {
                    iteractionK = k;
                } else {
                    iteractionK = k - 1;
                }

                partialPaths.addAll(this.buildSubgraphMaxHeightPath(
                    parent,
                    iteractionK,
                    ignoreOnlyBegottenFathers
                ));
            }
        }

        for (LinkedList<Node<T>> partialPath: partialPaths) {
            partialPath.addFirst(referenceNode);
        }

        if (k == 0) {
            LinkedList<Node<T>> pathSet = new LinkedList<Node<T>>();
            pathSet.add(referenceNode);
            partialPaths.add(pathSet);
        }

        return partialPaths;
    }
}
