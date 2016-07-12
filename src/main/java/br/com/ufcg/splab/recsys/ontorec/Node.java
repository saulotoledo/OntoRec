/*
 * OntoRec, Ontology Based Recommender Systems Algorithm License: GNU Lesser
 * General Public License (LGPL), version 3. See the LICENSE file in the root
 * directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates a node from a simple graph.
 *
 * @author Saulo Toledo
 * @param <T> The node type.
 */
public class Node<T> implements Mappable
{
    /**
     * The application logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Node.class);

    /**
     * The node's parents list.
     */
    private final Set<Node<T>> parents = new HashSet<Node<T>>();

    /**
     * The node's children list.
     */
    private final Set<Node<T>> children = new HashSet<Node<T>>();

    /**
     * The node's data.
     */
    private T data;

    /**
     * This node's attributes list.
     */
    private final Set<NodeAttribute> attributes = new HashSet<NodeAttribute>();

    /**
     * Creates a node with the informed data.
     *
     * @param data The node data.
     */
    Node(T data)
    {
        this.data = data;
    }

    /**
     * Returns the node's data.
     *
     * @return The node's data.
     */
    public T getData()
    {
        return this.data;
    }

    /**
     * Sets the node's data.
     *
     * @param data The new node's data.
     */
    public Node<T> setData(T data)
    {
        this.data = data;
        return this;
    }

    /**
     * Returns the node's children list.
     *
     * @return The node's children list.
     */
    public Set<Node<T>> getChildren()
    {
        return this.children;
    }

    /**
     * Returns the node's parents list.
     *
     * @return The node's parents list.
     */
    public Set<Node<T>> getParents()
    {
        return this.parents;
    }

    /**
     * Adds a node as a parent of the current node, if it is not there already.
     *
     * @param parent The future parent node.
     */
    public boolean addParent(Node<T> parent)
    {
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
     * @param parent The parent node to remove.
     * @return true if the node is a parent of the current node, false
     *         otherwise.
     */
    public boolean removeParent(Node<T> parent)
    {
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
    public boolean addChild(Node<T> child)
    {
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
     * @param child The child node to remove.
     * @return true if the node is a child of the current node, false otherwise.
     */
    public boolean removeChild(Node<T> child)
    {
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
    public boolean isRoot()
    {
        return (this.parents.size() == 0);
    }

    /**
     * Verify if the current node is leaf (a node that has no children).
     *
     * @return true if leaf, false otherwise.
     */
    public boolean isLeaf()
    {
        return (this.children.size() == 0);
    }

    /**
     * Adds a new attribute to the current node.
     *
     * @param attribute The attribute to add.
     * @return true if the attribute does not exist in the current node neither
     *         the parents nor the children, false otherwise.
     */
    public boolean addAttribute(NodeAttribute attribute)
    {
        Set<NodeAttribute> allAttributes = this.getAllInheritedAttributes();
        Set<NodeAttribute> childrenAttributes = this.getAllChildrenAttributes();

        if ( !this.getAttributes().contains(attribute)
            && !allAttributes.contains(attribute)
            && !childrenAttributes.contains(attribute)) {

            LOGGER.debug(String.format(
                "A new attribute '%s' was created in the node '%s'",
                attribute.getName(), this.getData().toString()));

            this.attributes.add(attribute);
            attribute.setAttachedNode(this);
            return true;
        }

        String whoContainsTheAttr = "";
        if ( !this.getAttributes().contains(attribute)) {
            whoContainsTheAttr = "this node";
        } else if ( !allAttributes.contains(attribute)) {
            whoContainsTheAttr = "a parent node";
        } else if (childrenAttributes.contains(attribute)) {
            whoContainsTheAttr = "a child node";
        }
        LOGGER.debug(String.format(
            "It was not possible to create the attribute '%s' in the node '%s' because '%s' already contains an attribute with this name",
            attribute.getName(), this.getData().toString(),
            whoContainsTheAttr));

        return false;
    }

    /**
     * Removes an attribute from the current node.
     *
     * @param attribute The attribute to remove.
     * @return true if the attribute exists and was successfully removed, false
     *         otherwise.
     */
    public boolean removeAttribute(NodeAttribute attribute)
    {
        Boolean result = this.attributes.remove(attribute);

        if (result) {
            LOGGER.debug(String.format(
                "The attribute '%s' was successfully removed from the node '%s'",
                attribute.getName(), this.getData().toString()));
        } else {
            LOGGER.debug(String.format(
                "Failed to remove the attribute '%s' from the node '%s'",
                attribute.getName(), this.getData().toString()));
        }

        return result;
    }

    /**
     * Returns the node's own attributes.
     *
     * @return The node's own attributes.
     */
    public Set<NodeAttribute> getAttributes()
    {
        return this.attributes;
    }

    /**
     * Returns the NodeAttribute object for a given node (even if the attribute
     * is inherited), or null if the node does not have the attribute.
     *
     * @return The NodeAttribute object for a given node (even if the attribute
     *         is inherited), or null if the node does not have the attribute.
     */
    public NodeAttribute getOwnOrInheritedAttributeByName(String attributeName)
    {
        Set<NodeAttribute> attrs = this.getAllAttributes();

        for (NodeAttribute attr : attrs) {
            if (attr.getName().equals(attributeName)) {
                return attr;
            }
        }

        return null;
    }

    /**
     * Returns all the node's attributes (own plus inherited ones).
     *
     * @return All the node's attributes.
     */
    public Set<NodeAttribute> getAllAttributes()
    {
        Set<NodeAttribute> allAttributes = this.getAllInheritedAttributes();
        allAttributes.addAll(this.getAttributes());

        return new HashSet<NodeAttribute>(allAttributes);
    }

    /**
     * Returns all inherited attributes.
     *
     * @return All inherited attributes.
     */
    private Set<NodeAttribute> getAllInheritedAttributes()
    {
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
    private Set<NodeAttribute> getAllChildrenAttributes()
    {
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
     * @param node The reference node.
     * @return true if the current note is child of the reference node, false
     *         otherwise.
     */
    public boolean isChildOf(Node<T> node)
    {
        return this.parents.contains(node);
    }

    /**
     * Verify if the current node is parent of another one.
     *
     * @param node The reference node.
     * @return true if the current note is parent of the reference node, false
     *         otherwise.
     */
    public boolean isParentOf(Node<T> node)
    {
        return this.children.contains(node);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Node<?>) {
            return this.getData().equals( ((Node<?>) obj).getData());
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
     * @see <http://docs.oracle.com/javase/6/docs/api/java/lang/Object.html#hashCode>
     * @return The hash code.
     */
    @Override
    public int hashCode()
    {
        return this.getData().toString().length();
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString()
    {
        return String.format("%s: %s", this.getClass().getSimpleName(),
            this.getData().toString());
    }

    /**
     * Returns a set of paths containing all paths from this node to the root
     * element. Each path is represented as a list where the first element is
     * the current node and the last one is the root element.
     *
     * @return A set of paths containing all paths from this node to the root
     *         element.
     */
    public Set<LinkedList<Node<T>>> getAllPathsToRoot()
    {
        return this.buildPathsToRoot(this);
    }

    /**
     * Returns a set of paths containing all paths from a reference node to the
     * root element. Each path is represented as a list where the first element
     * is the reference node and the last one is the root element.
     *
     * @param referenceNode The reference node.
     * @return A set of paths containing all paths from a reference node to the
     *         root element.
     */
    private Set<LinkedList<Node<T>>> buildPathsToRoot(Node<T> referenceNode)
    {

        Set<LinkedList<Node<T>>> partialPaths = new HashSet<LinkedList<Node<T>>>();

        for (Node<T> parent : referenceNode.getParents()) {
            partialPaths.addAll(this.buildPathsToRoot(parent));
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
     * Returns a set of paths from the current node to its τ-nth ancestor,
     * ignoring only begotten fathers at results. Each path is represented as a
     * list where the first element is the reference node and the last one is
     * the root element.
     *
     * @param tau The number of ancestors to return. This parameter ignores only
     *        begotten fathers at counts.
     * @return A set of possible paths for the given parameter.
     */
    public Set<LinkedList<Node<T>>> getSubgraphMaxHeightPaths(int tau)
    {
        return this.getSubgraphMaxHeightPaths(tau, true);
    }

    /**
     * Returns a set of paths from the current node to its τ-nth ancestor. Each
     * path is represented as a list where the first element is the reference
     * node and the last one is the τ-nth ancestor element.
     *
     * @param tau The number of ancestors to return.
     * @param lambda If true, only begotten fathers will be
     *        ignored at ancestors.
     * @return A set of possible paths for the given parameters.
     */
    public Set<LinkedList<Node<T>>> getSubgraphMaxHeightPaths(int tau,
        boolean lambda)
    {
        return this.buildSubgraphMaxHeightPaths(this, tau,
            lambda);
    }

    // TODO: doc
    public Boolean hasMappedAttributes()
    {
        for (NodeAttribute attr : this.getAllAttributes()) {
            if (attr.getIsMappedTo() != null
                && attr.getIsMappedTo().equals(this)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a set of paths from a reference node to its τ-nth ancestor. Each
     * path is represented as a list where the first element is the reference
     * node and the last one is the τ-nth ancestor element.
     *
     * @param referenceNode The reference node.
     * @param tau The number of ancestors to return.
     * @param lambda If true, only begotten fathers will be
     *        ignored at ancestors.
     * @return A set of possible paths for the given parameters.
     */
    private Set<LinkedList<Node<T>>> buildSubgraphMaxHeightPaths(
        Node<T> referenceNode, int tau, boolean lambda)
    {

        Set<LinkedList<Node<T>>> partialPaths = new HashSet<LinkedList<Node<T>>>();

        if (tau > 0) {
            int iteractionTau;
            for (Node<T> parent : referenceNode.getParents()) {

                if (lambda
                    && parent.getChildren().size() == 1
                    && !parent.hasMappedAttributes()) {
                    iteractionTau = tau;
                } else {
                    iteractionTau = tau - 1;
                }

                partialPaths.addAll(this.buildSubgraphMaxHeightPaths(parent,
                    iteractionTau, lambda));
            }
        }

        for (LinkedList<Node<T>> partialPath : partialPaths) {
            if (lambda) {
                if (referenceNode.equals(this)
                    || referenceNode.getChildren().size() != 1
                    || (referenceNode.getChildren().size() == 1
                        && referenceNode.hasMappedAttributes())) {
                    partialPath.addFirst(referenceNode);
                }
            } else {
                partialPath.addFirst(referenceNode);
            }
        }

        if (tau == 0) {
            LinkedList<Node<T>> pathSet = new LinkedList<Node<T>>();
            pathSet.add(referenceNode);
            partialPaths.add(pathSet);
        }

        return partialPaths;
    }

    /**
     * Returns the distances from the current node to each reference node by
     * using BFS to discover the lesser non directional path (where does not
     * matter if the next node at path is parent or child). The unreachable
     * nodes at reference set for the given τ are removed from the result. The
     * distance increases by 1 each time that we move from a node to another.
     *
     * @param referenceNodes A set of reference nodes.
     * @param tau The current node's max ancestor that defines the reachable
     *        descendant nodes.
     * @return A map with the distances from the current node to each reference
     *         node, where the key is a node and the value is the distance
     *         relative to the current node.
     */
    public Map<Node<T>, Integer> getDistancesTo(Set<Node<T>> referenceNodes,
        int tau)
    {
        return this.getDistancesTo(referenceNodes, tau, true);
    }

    /**
     * Returns the distances from the current node to each reference node by
     * using BFS to discover the lesser non directional path (where does not
     * matter if the next node at path is parent or child). The unreachable
     * nodes at reference set for the given τ are removed from the result. The
     * distance increases by 1 each time that we move from a node to another.
     *
     * @param referenceNodes A set of reference nodes.
     * @param tau The current node's max ancestor that defines the reachable
     *        descendant nodes.
     * @param lambda Allows to define if only begotten
     *        fathers are ignored or not at results.
     * @return A map with the distances from the current node to each reference
     *         node, where the key is a node and the value is the distance
     *         relative to the current node.
     */
    public Map<Node<T>, Integer> getDistancesTo(Set<Node<T>> referenceNodes,
        int tau, boolean lambda)
    {
        

        Set<Node<T>> maxNodesFromTau = this.extractMaxNodesFromTau(tau,
            lambda);
        Set<Node<T>> subgraphNodes = new HashSet<Node<T>>();

        LOGGER.debug(String.format(
            "For the current setup with τ=%d, we can reach the nodes in the set '%s'",
            tau, maxNodesFromTau));

        for (Node<T> pseudoRoot : maxNodesFromTau) {
            Set<Node<T>> reachableSubgraphNodes = pseudoRoot
                .bfsDiscoverSubgraphNodes();
            subgraphNodes.addAll(reachableSubgraphNodes);

            LOGGER.debug(String.format(
                "From the node '%s' we can reach the nodes in the set '%s'",
                pseudoRoot, reachableSubgraphNodes));
        }

        Map<Node<T>, Integer> result = this
            .bfsDiscoverLesserNonDirectionalDistanceTo(subgraphNodes,
                referenceNodes, lambda);

        LOGGER.debug(String.format(
            "The distances starting from '%s' for the mapped reachable nodes are '%s'",
            this.getData().toString(), result));

        return result;
    }

    /**
     * Uses BFS to discover the subgraph where the current node is the root and
     * it's all descendant nodes are reachable.
     *
     * @return A set of nodes containing the current node and it's all
     *         descendant nodes.
     */
    private Set<Node<T>> bfsDiscoverSubgraphNodes()
    {
        List<Node<T>> nodesQueue = new LinkedList<Node<T>>();
        Set<Node<T>> visitedNodes = new HashSet<Node<T>>();

        nodesQueue.add(this);
        visitedNodes.add(this);

        Node<T> current;
        while (nodesQueue.size() != 0) {
            current = nodesQueue.remove(0);

            for (Node<T> child : current.getChildren()) {
                if ( !visitedNodes.contains(child)) {
                    nodesQueue.add(child);
                    visitedNodes.add(child);
                }
            }
        }

        return visitedNodes;
    }

    /**
     * Uses BFS to discover the length of the lesser non directional path (where
     * does not matter if the next node at path is parent or child) to the
     * reference nodes.
     *
     * @param subgraphNodes The nodes to consider in the search. Any nodes that
     *        are not here are ignored as nonexistent.
     * @param referenceNodes The nodes for which to search the length of the
     *        paths.
     * @param lambda Allows to define if only begotten
     *        fathers are ignored or not at results.
     * @return A map where each key points to the length of the lesser non
     *         directional path from the current node to the key node found by
     *         the algorithm.
     */
    private Map<Node<T>, Integer> bfsDiscoverLesserNonDirectionalDistanceTo(
        Set<Node<T>> subgraphNodes, Set<Node<T>> referenceNodes,
        boolean lambda)
    {

        Map<Node<T>, LinkedList<Node<T>>> paths = this
            .bfsDiscoverLesserNonDirectionalPathTo(subgraphNodes,
                referenceNodes, lambda);

        Map<Node<T>, Integer> result = new HashMap<Node<T>, Integer>();

        for (Node<T> referenceNode : paths.keySet()) {
            result.put(referenceNode, paths.get(referenceNode).size() - 1);
        }

        return result;
    }

    /**
     * Uses BFS to discover the lesser non directional path (where does not
     * matter if the next node at path is parent or child) to the reference
     * nodes.
     *
     * @param subgraphNodes The nodes to consider in the search. Any nodes that
     *        are not here are ignored as nonexistent.
     * @param referenceNodes The nodes for which to search the paths.
     * @param lambda Allows to define if only begotten
     *        fathers are ignored or not at results.
     * @return A map where each key points to a list containing the lesser non
     *         directional path from the current node to the key node found by
     *         the algorithm.
     */
    private Map<Node<T>, LinkedList<Node<T>>> bfsDiscoverLesserNonDirectionalPathTo(
        Set<Node<T>> subgraphNodes, Set<Node<T>> referenceNodes,
        boolean lambda)
    {

        // Breadth First Search algorithm:
        List<Node<T>> nodesQueue = new LinkedList<Node<T>>();
        Map<Node<T>, Node<T>> cameFrom = new HashMap<Node<T>, Node<T>>();

        nodesQueue.add(this);
        cameFrom.put(this, null);

        Node<T> current;
        Set<Node<T>> neighborhood;
        while (nodesQueue.size() != 0) {
            current = nodesQueue.remove(0);

            neighborhood = new HashSet<Node<T>>();
            neighborhood.addAll(current.getChildren());
            neighborhood.addAll(current.getParents());

            // Nodes that are not at subgraphs should not be here:
            for (Node<T> node : new HashSet<Node<T>>(neighborhood)) {
                if ( !subgraphNodes.contains(node)) {
                    neighborhood.remove(node);
                }
            }

            for (Node<T> neighbor : neighborhood) {
                if ( !cameFrom.keySet().contains(neighbor)) {
                    nodesQueue.add(neighbor);
                    cameFrom.put(neighbor, current);
                }
            }
        }

        // Paths construction:
        Map<Node<T>, LinkedList<Node<T>>> result = new HashMap<Node<T>, LinkedList<Node<T>>>();

        for (Node<T> node : referenceNodes) {
            // Unreachable nodes are not at cameFrom map:
            if (cameFrom.keySet().contains(node)) {
                LinkedList<Node<T>> path = this.bfsReconstructPath(cameFrom,
                    node, lambda);
                result.put(node, path);
            }
        }

        return result;
    }

    /**
     * Builds a path based on a node mapping built by a Breadth First Search
     * (BFS) approach
     *
     * @param cameFrom A node mapping built by a Breadth First Search (BFS)
     *        approach.
     * @param goal The reference node to build the path.
     * @param lambda Allows to define if only begotten
     *        fathers are ignored or not at results.
     * @return A list containing the path from the higher node at cameFrom
     *         mapping to the goal node.
     */
    // TODO: Duplicate on TaunthAncestorNode, needs refactoring (there)!
    private LinkedList<Node<T>> bfsReconstructPath(
        Map<Node<T>, Node<T>> cameFrom, Node<T> goal,
        boolean lambda)
    {
        LinkedList<Node<T>> path = new LinkedList<Node<T>>();

        Node<T> current = goal;
        path.add(current);
        while (cameFrom.get(current) != null) {
            current = cameFrom.get(current);

            if (lambda) {
                if (current.getChildren().size() != 1
                    || (current.getChildren().size() == 1
                        && current.hasMappedAttributes())) {
                    path.addFirst(current);
                }
            } else {
                path.addFirst(current);
            }
        }

        return path;
    }

    /**
     * Returns a set containing all the τ-nth ancestors of this node. It gets
     * all possible paths for the given parameter and extracts their last nodes.
     *
     * @param tau The level of the ancestor to return.
     * @param lambda If true, only begotten fathers will be
     *        ignored at ancestors.
     * @return A set containing all the τ-nth ancestors of this node.
     */
    public Set<Node<T>> extractMaxNodesFromTau(int tau,
        Boolean lambda)
    {
        Set<LinkedList<Node<T>>> allPaths = this.getSubgraphMaxHeightPaths(tau,
            lambda);

        LOGGER.debug(String.format(
            "The paths for the node '%s' with τ=%s are %s", this, tau, allPaths));

        Set<Node<T>> result = new HashSet<Node<T>>();
        Node<T> maxNode;
        for (LinkedList<Node<T>> path : allPaths) {
            maxNode = path.get(path.size() - 1);
            if ( !result.contains(maxNode)) {
                result.add(maxNode);
            }
        }
        return result;
    }
}
