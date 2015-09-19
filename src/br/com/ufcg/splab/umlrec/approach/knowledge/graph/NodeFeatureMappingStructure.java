package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

public class NodeFeatureMappingStructure<T> {
    private String featureName;
    private Node<T> node;
    private NodeAttribute attribute;

    public NodeFeatureMappingStructure(String featureName, Node<T> node) {
        this.featureName = featureName;
        this.node = node;
        this.attribute = null;
    }

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
    }

    public String getFeatureName() {
        return this.featureName;
    }

    public Node<T> getNode() {
        return this.node;
    }

    public NodeAttribute getAttribute() {
        return this.attribute;
    }

    // TODO: Attribute and Node mappings should be different classes. Implement in future.
    public Boolean isMappingToAttribute() {
        return (this.attribute != null);
    }

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