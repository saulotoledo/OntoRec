/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.recsys.ontorec.weighting.NodeWeightingApproach;

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
     * Defines if this manager should ignore only begotten fathers in its
     * operations.
     */
    private Boolean ignoreOnlyBegottenFathers;

    /**
     * Defines if this manager should achieve all the other mapped nodes when
     * performing weight calculations for each mapped node that is being
     * processed. This will impact at total number of verified paths and will
     * change the algorithm results.
     */
    private Boolean achieveOtherMappedNodes;

    /**
     * Connects feature names to Nodes.
     */
    private Map<String, NodeFeatureMappingStructure<T>> featureMapping =
    		new HashMap<String, NodeFeatureMappingStructure<T>>();

    private NodeWeightingApproach<T> nodeWeightingApproach;

    public NodeManager(NodeWeightingApproach<T> nodeWeightingApproach,
           Boolean ignoreOnlyBegottenFathers, Boolean achieveOtherMappedNodes) {

        this.nodeWeightingApproach = nodeWeightingApproach;
        this.ignoreOnlyBegottenFathers = ignoreOnlyBegottenFathers;
        this.achieveOtherMappedNodes = achieveOtherMappedNodes;
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

        Set<Node<T>> directMappedNodes = this.getMappedNodes();
        Set<Node<T>> attributeNodes = this.getAttributeNodes();

        return this.nodeWeightingApproach.getFeaturesWeight(selectedFeatures,
                directMappedNodes, attributeNodes, this.featureMapping, k,
                this.getIgnoreOnlyBegottenFathers(),
                this.getAchieveOtherMappedNodes());
    }

    /**
     * Returns if this manager ignores only begotten fathers in its operations.
     *
     * @return True if ignores, false otherwise.
     */
    public Boolean getIgnoreOnlyBegottenFathers() {
        return this.ignoreOnlyBegottenFathers;
    }

    /**
     * Returns if this manager should achieve all the other mapped nodes when
     * performing weight calculations.
     *
     * @return True if achieve, false otherwise.
     */
    public Boolean getAchieveOtherMappedNodes() {
        return this.achieveOtherMappedNodes;
    }
}
