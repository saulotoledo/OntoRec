/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

/**
 * Defines a node's attribute.
 *
 * @author Saulo Toledo
 *
 */
class NodeAttribute implements Mappable {

    /**
     * The attribute's name.
     */
    private String name;

    /**
     * Contains the node where this attribute is attached.
     */
    private Node<?> attachedNode;

    /**
     * 
     */
    private Node<?> isMappedTo;

	/**
     * Creates a node attribute.
     *
     * @param name The attribute's name.
     */
    public NodeAttribute(String name) {
        this.name = name;
    }
    
    public Node<?> getIsMappedTo() {
		return this.isMappedTo;
	}

	public NodeAttribute setIsMappedTo(Node<?> isMappedTo) {
		this.isMappedTo = isMappedTo;
		return this;
	}

    /**
     * Returns the attribute's name.
     *
     * @return The attribute's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the attribute's name.
     *
     * @param name The attribute's name.
     */
    public NodeAttribute setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the node where this attribute is attached.
     *
     * @return The node where this attribute is attached.
     */
    Node<?> getAttachedNode() {
        return this.attachedNode;
    }

    /**
     * Defines the node where this attribute should be attached.
     *
     * @param attachedNode The node where this attribute should be attached.
     */
    NodeAttribute setAttachedNode(Node<?> attachedNode) {
        Node<?> oldNode = this.attachedNode;
        this.attachedNode = attachedNode;

        if (oldNode != null) {
        	if (oldNode.getAttributes().contains(this)) {
        		oldNode.removeAttribute(this);
        	}
        }

        return this;
    }

    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        return String.format("%s (%s)", this.getName(), this.getAttachedNode());
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeAttribute) {
            return this.getName().equals(((NodeAttribute) obj).getName());
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
        return this.getName().length();
    }
}
