package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

/**
 * Defines a node's attribute.
 *
 * @author Saulo Toledo
 *
 */
public class NodeAttribute {

    /**
     * The attribute's name.
     */
    private String name;

    /**
     * Creates a node attribute.
     *
     * @param name The attribute's name.
     */
    public NodeAttribute(String name) {
        this.name = name;
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
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of the object.
     */
    @Override
    public String toString() {
        return this.getName();
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
