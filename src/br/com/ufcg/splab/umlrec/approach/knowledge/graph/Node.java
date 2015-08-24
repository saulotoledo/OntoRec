package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private List<Node<T>> parents = new ArrayList<Node<T>>();

    /**
     * The node's children list.
     */
    private List<Node<T>> children = new ArrayList<Node<T>>();

    /**
     * The node's data.
     */
    private T data;

    /**
     * This node's attributes list.
     */
    private List<NodeAttribute> attributes = new ArrayList<NodeAttribute>();


    /**
     * Creates a node with the informed data.
     *
     * @param data The node data.
     */
    public Node(T data) {
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
    public List<Node<T>> getChildren() {
        return this.children;
    }

    /**
     * Returns the node's parents list.
     *
     * @return The node's parents list.
     */
    public List<Node<T>> getParents() {
        return this.parents;
    }

    /**
     * Creates a node with the informed data and adds the created node as a
     * parent of the current node.
     *
     * @param parent The future parent node.
     */
    public boolean addParent(T parent) {
        Node<T> parentNode = new Node<T>(parent);
        return this.addParent(parentNode);
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
     * Creates a node with the informed data and adds the created node as a
     * child of the current one.
     *
     * @param parent The future child node.
     */
    public boolean addChild(T data) {
        Node<T> child = new Node<T>(data);
        return this.addChild(child);
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


    // TODO: Add attr por nome

    /**
     * Adds a new attribute to the current node.
     *
     * @param  attribute The attribute to add.
     * @return true if the attribute does not exist, false otherwise.
     */
    public boolean addAttribute(NodeAttribute attribute) {
        Set<NodeAttribute> allAttributes = this.getAllInheritedAttributes();

        if (!this.getAttributes().contains(attribute)
                && !allAttributes.contains(attribute)) {
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
        //TODO: verificar attr herdado
    }

    /**
     * Returns the node's own attributes.
     *
     * @return The node's own attributes.
     */
    public List<NodeAttribute> getAttributes() {
        return this.attributes;
    }

    /**
     * Returns all the node's attributes (own plus inherited ones).
     *
     * @return All the node's attributes.
     */
    public List<NodeAttribute> getAllAttributes() {
        Set<NodeAttribute> allAttributes = this.getAllInheritedAttributes();

        allAttributes.addAll(this.getAttributes());

        return new ArrayList<NodeAttribute>(allAttributes);
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
}
