package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Creates a node manager.
 * @author Saulo Toledo
 *
 * @param <T> The node type.
 */
public class NodeManager<T> {

	/*
	class NodeFeatureMappingStructure {
		private String featureName;
		private Node<T> node;
		private Boolean isAttribute;

		public NodeFeatureMappingStructure(
				String featureName, Node<T> node, Boolean isAttribute)
		{
			this.featureName = featureName;
			this.node = node;
			this.isAttribute = isAttribute;
		}

		public String getFeatureName() {
			return this.featureName;
		}

		public Node<T> getNode() {
			return this.node;
		}

		public Boolean isAttribute() {
			return this.isAttribute;
		}

		@Override
		public boolean equals(Object obj) {
			NodeManager<?>.NodeFeatureMappingStructure compareObj;
			if (obj instanceof NodeManager<?>.NodeFeatureMappingStructure) {
				compareObj = (NodeFeatureMappingStructure) obj;

				return
					this.getFeatureName().equals(compareObj.getFeatureName())
					&& this.getNode().equals(compareObj.getNode());
			}
			return false;
		}
	}
	*/

    /**
     * A nodes map to avoid repetitions. Each node is registered here when
     * created.
     */
    private Map<T, Node<T>> nodeMap = new HashMap<T, Node<T>>();

    /**
     * An attributes set to facilitate access to attributes. Each attribute is
     * registered here when created.
     */
    private Set<NodeAttribute> attibuteSet = new HashSet<NodeAttribute>();


    /**
     * Connects feature names to Nodes.
     */
    //private Set<NodeFeatureMappingStructure> featureMappingSet =
    //		new HashSet<NodeFeatureMappingStructure>();

    private Map<String, Mappable> featureMappingMap =
    		new HashMap<String, Mappable>();

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

    public NodeManager<T> createAttribute(
    		String attributeName, Node<T> attachedNode) {
        NodeAttribute attribute = new NodeAttribute(attributeName);
        attachedNode.addAttribute(attribute);

        return this;
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
    /*
    public NodeManager<T> addFeatureMapping(
    		String featureName, Node<T> node, Boolean isAttribute) {
    	NodeFeatureMappingStructure featureMapping =
    			new NodeFeatureMappingStructure(featureName, node, isAttribute);

    	if (!this.featureMappingSet.contains(featureMapping)) {
    		this.featureMappingSet.add(featureMapping);
    	}

        return this;
    }

    public boolean removeFeatureMapping(String featureName, Node<T> node) {
    	NodeFeatureMappingStructure featureMapping =
    			new NodeFeatureMappingStructure(featureName, node, null);

        if (this.featureMappingSet.contains(featureMapping)) {
			this.featureMappingSet.remove(featureMapping);
			return true;
		}
        return false;
    }

    public Set<Node<T>> getMappedNodes() {
    	Set<Node<T>> mappedNodes = new HashSet<Node<T>>();
    	for (NodeFeatureMappingStructure featureMapping :
    		this.featureMappingSet) {

    		if (!mappedNodes.contains(featureMapping.getNode())) {
    			mappedNodes.add(featureMapping.getNode());
    		}
		}
    	return mappedNodes;
    }

    public Set<String> getMappedFeatures() {
    	Set<String> mappedFeatures = new HashSet<String>();
    	for (NodeFeatureMappingStructure featureMapping :
    		this.featureMappingSet) {

    		if (!mappedFeatures.contains(featureMapping.getFeatureName())) {
    			mappedFeatures.add(featureMapping.getFeatureName());
    		}
		}
    	return mappedFeatures;
    }
	*/

    public NodeManager<T> addFeatureMapping(
    		String featureName, Mappable mappable, Boolean isAttribute) {

    	if (!this.featureMappingMap.containsKey(featureName)) {
    		this.featureMappingMap.put(featureName, mappable);
    	}

        return this;
    }

    public boolean removeFeatureMapping(String featureName) {
    	if (this.featureMappingMap.containsKey(featureName)) {
			this.featureMappingMap.remove(featureName);
			return true;
		}

        return false;
    }

    public Set<Node<T>> getMappedNodes() {
    	Set<Node<T>> mappedNodes = new HashSet<Node<T>>();

    	for (String featureName : this.featureMappingMap.keySet()) {
    		Mappable current = this.featureMappingMap.get(featureName);
    		if (current instanceof Node<?> && !mappedNodes.contains(current)) {
    			mappedNodes.add((Node<T>) current);
    		}
		}

    	return mappedNodes;
    }

    public Set<Node<T>> getAttributeNodes() {
    	Set<Node<T>> mappedNodes = new HashSet<Node<T>>();

    	for (String featureName : this.featureMappingMap.keySet()) {
    		Mappable current = this.featureMappingMap.get(featureName);
    		if (current instanceof NodeAttribute
    				&& !mappedNodes.contains(current)) {
    			mappedNodes.add(
    					(Node<T>) ((NodeAttribute) current).getAttachedNode());
    		}
		}

    	return mappedNodes;
    }

    private Map<String, Double> updateFeaturesWeightMap(
    		Map<String, Double> map, String feature, Double value) {

    	if (!map.containsKey(feature)) {
    		map.put(feature, value);
    	} else {
    		Double oldValue = map.get(feature);

    		//TODO: This cannot happen yet, we are using this function to update values based on path lengths, not at final weight. This need to be changed.
    		if (oldValue != 0 && value != 0) {
				value = (value + oldValue) / 2;
			}

    		if (value == 0) {
    			value = oldValue;
    		}

    		map.remove(feature);
    		map.put(feature, value);
    	}

		return map;
    }

    public Map<String, Double> getFeaturesWeight(Set<Node<T>> selectedNodes,
    		Integer k) {
    	return this.getFeaturesWeight(selectedNodes, k, true);
    }

    // TODO: complete and fix method
    public Map<String, Double> getFeaturesWeight(Set<Node<T>> selectedNodes,
    		Integer k, Boolean ignoreOnlyBegottenFathers) {

    	Map<String, Double> result = new HashMap<String, Double>();

    	Set<String> mappedFeatures = this.featureMappingMap.keySet();
    	for (String feature : mappedFeatures) {
    		if (!result.containsKey(feature)) {
				result.put(feature, 0d);
			}
		}

    	Set<Node<T>> directMappedNodes = this.getMappedNodes();
    	Set<Node<T>> attributeNodes = this.getAttributeNodes();
    	Set<Node<T>> allMappedRelatedNodes = new HashSet<Node<T>>();
    	allMappedRelatedNodes.addAll(directMappedNodes);
    	allMappedRelatedNodes.addAll(attributeNodes);

    	Map<Node<T>, Integer> affectedNodesDistances;
    	for (Node<T> selectedNode : selectedNodes) {
    		affectedNodesDistances = selectedNode.getDistancesTo(
    				allMappedRelatedNodes, k, ignoreOnlyBegottenFathers);

    		for (String feature : this.featureMappingMap.keySet()) {
				Double value = 0d;
				if (affectedNodesDistances.keySet().contains(selectedNode)) {
					value =
						affectedNodesDistances.get(selectedNode).doubleValue();
				}

				//TODO: wrong behavior, see todo at method
				result = this.updateFeaturesWeightMap(result, feature, value);
			}
			//result.put(feature, initialFeaturesWeight.get(feature));
		}

    	return result;

    	/*
    	Set<String> mappedFeatures = this.getMappedFeatures();
    	for (String feature : mappedFeatures) {
    		if (!result.containsKey(feature)) {
				result.put(feature, 0d);
			}
		}

    	Set<Node<T>> mappedNodes = this.getMappedNodes();
    	Map<Node<T>, Integer> affectedNodesDistances;
    	for (Node<T> selectedNode : selectedNodes) {
    		affectedNodesDistances = selectedNode.getDistancesTo(
    				mappedNodes, k, ignoreOnlyBegottenFathers);

    		for (Node<T> node : affectedNodesDistances.keySet()) {
				result = this.updateFeaturesWeightMap(result, feature, value);
			}
			result.put(feature, initialFeaturesWeight.get(feature));
		}
    	for (Node<T> mappedNode : mappedNodes) {
    		mappedNode.get

		}

    	return result;
    	*/
    }
}
