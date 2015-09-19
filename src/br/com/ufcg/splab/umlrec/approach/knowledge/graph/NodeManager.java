package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.umlrec.approach.knowledge.graph.weighting.NodeWeightingApproach;

/**
 * Creates a node manager.
 * @author Saulo Toledo
 *
 * @param <T> The node type.
 */
public class NodeManager<T> {

    /**
     * A nodes map to avoid repetitions. Each node is registered here when
     * created.
     */
    private Map<T, Node<T>> nodeMap = new HashMap<T, Node<T>>();

    /**
     * Connects feature names to Nodes.
     */
    private Map<String, NodeFeatureMappingStructure<T>> featureMapping =
    		new HashMap<String, NodeFeatureMappingStructure<T>>();

    private NodeWeightingApproach<T> nodeWeightingApproach;

    public NodeManager(NodeWeightingApproach<T> nodeWeightingApproach) {
        this.nodeWeightingApproach = nodeWeightingApproach;
    }

    public NodeManager<T> setNodeWeightingApproach(
            NodeWeightingApproach<T> nodeWeightingApproach) {

        this.nodeWeightingApproach = nodeWeightingApproach;
        return this;
    }

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

    public NodeAttribute createAttribute(
    		String attributeName, Node<T> attachedNode) {
        NodeAttribute attribute = new NodeAttribute(attributeName);
        attachedNode.addAttribute(attribute);

        return attribute;
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

    public NodeManager<T> addFeatureMapping(String featureName, Node<T> node)
            throws Exception {
        return this.addFeatureMapping(featureName, node, null);
    }

    public NodeManager<T> addFeatureMapping(
            String featureName, Node<T> node, NodeAttribute attribute)
            throws Exception {

        NodeFeatureMappingStructure<T> featureMappingStructure;
        if (attribute == null) {
            featureMappingStructure = new NodeFeatureMappingStructure<T>(
                    featureName, node);
        } else {
            featureMappingStructure = new NodeFeatureMappingStructure<T>(
                    featureName, node, attribute);
        }

    	if (!this.featureMapping.containsKey(featureName)) {
    		this.featureMapping.put(featureName, featureMappingStructure);
    	}

        return this;
    }

    public boolean removeFeatureMapping(String featureName) {
        if (this.featureMapping.containsKey(featureName)) {
            this.featureMapping.remove(featureName);
            return true;
        }

        return false;
    }

    public Set<Node<T>> getMappedNodes() {
    	Set<Node<T>> mappedNodes = new HashSet<Node<T>>();

    	for (NodeFeatureMappingStructure<T> featureMappingStructure :
		        this.featureMapping.values()) {

    		if (!featureMappingStructure.isMappingToAttribute()
    		        && !mappedNodes.contains(
	                featureMappingStructure.getNode())) {
    			mappedNodes.add(featureMappingStructure.getNode());
    		}
		}

    	return mappedNodes;
    }

    public Set<String> getMappedFeatures() {
    	Set<String> mappedFeatures = new HashSet<String>();

    	for (NodeFeatureMappingStructure<T> featureMappingStructure :
    		this.featureMapping.values()) {

    		if (!mappedFeatures.contains(
    		        featureMappingStructure.getFeatureName())) {
    			mappedFeatures.add(featureMappingStructure.getFeatureName());
    		}
		}

    	return mappedFeatures;
    }

    public Set<Node<T>> getAttributeNodes() {
    	Set<Node<T>> mappedNodes = new HashSet<Node<T>>();

        for (NodeFeatureMappingStructure<T> featureMappingStructure :
                this.featureMapping.values()) {

            if (featureMappingStructure.isMappingToAttribute()
                    && !mappedNodes.contains(
                    featureMappingStructure.getNode())) {
                mappedNodes.add(featureMappingStructure.getNode());
            }
        }

    	return mappedNodes;
    }

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
    		Integer k) {
    	return this.getFeaturesWeight(selectedFeatures, k, true);
    }

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
    		Integer k, Boolean ignoreOnlyBegottenFathers) {

        Set<Node<T>> directMappedNodes = this.getMappedNodes();
        Set<Node<T>> attributeNodes = this.getAttributeNodes();

        return this.nodeWeightingApproach.getFeaturesWeight(selectedFeatures,
                directMappedNodes, attributeNodes, this.featureMapping, k,
                ignoreOnlyBegottenFathers);
    }
}
