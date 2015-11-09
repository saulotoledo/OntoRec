/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

/**
 * Maps a feature to a Node or NodeAttribute.
 *
 * @author Saulo Toledo
 * @param <T> The node type.
 */
public class NodeFeatureMappingStructure<T> {

    /**
     * The feature name.
     */
    private String featureName;

    /**
     * The mapped node.
     */
    private Node<T> node;

    /**
     * The mapped attribute, if appropriate.
     */
    private NodeAttribute attribute;

    /**
     * Creates a mapping to a node.
     *
     * @param featureName The feature name.
     * @param node The mapped node.
     */
    public NodeFeatureMappingStructure(String featureName, Node<T> node) {
        this.featureName = featureName;
        this.node = node;
        this.attribute = null;
    }

    /**
     * Creates a mapping to an attribute considering a node. The referenced node
     * must contains or inherit the attribute.
     *
     * @param  featureName The feature name.
     * @param  node The node that contains the attribute related to the feature.
     *         The node must be informed because all descendant nodes inherit
     *         the attribute, and from the attribute we can only get the node
     *         that is directly connected to it. To be possible to map the
     *         feature for some descendant node, it must be informed.
     * @param  attribute The mapped attribute.
     * @throws Exception If the referenced node does not contains the attribute.
     */
    public NodeFeatureMappingStructure(
            String featureName, Node<T> node, NodeAttribute attribute)
            throws Exception {

        if (!node.getAllAttributes().contains(attribute)) {
            throw new Exception(String.format(
                    "'%s' does not contains the attribute '%s'",
                    node.toString(), attribute.getName()));
        }

        this.featureName = featureName;
        this.node = node;
        this.attribute = attribute;
        this.attribute.setIsMappedTo(node);
    }

    /**
     * Returns the feature name.
     *
     * @return The feature name.
     */
    public String getFeatureName() {
        return this.featureName;
    }

    /**
     * Returns the related node.
     *
     * @return The related node.
     */
    public Node<T> getNode() {
        return this.node;
    }

    /**
     * Returns the mapped attribute, if appropriate.
     *
     * @return The mapped attribute, if appropriate.
     */
    public NodeAttribute getAttribute() {
        return this.attribute;
    }

    // TODO: Attribute and Node mappings should be different classes. Implement in future.
    /**
     * Returns if the current structure maps to an attribute.
     *
     * @return True if the current structure maps to an attribute, false
     *         otherwise.
     */
    public Boolean isMappingToAttribute() {
        return (this.attribute != null);
    }


    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        NodeFeatureMappingStructure<T> compareObj;
        if (obj instanceof NodeFeatureMappingStructure) {
            compareObj = (NodeFeatureMappingStructure<T>) obj;

            return
                this.getFeatureName().equals(compareObj.getFeatureName())
                && this.getNode().equals(compareObj.getNode())
                && this.getAttribute().equals(compareObj.getAttribute());
        }
        return false;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return A string representation of the object.
     */
    @Override
    public String toString() {
        if (this.isMappingToAttribute()) {
            return String.format("%s -> [%s] (%s)", this.getFeatureName(),
                    this.getAttribute().getName(), this.getNode());
        }
        return String.format("%s -> (%s)", this.getFeatureName(),
                this.getNode());
    }
}
