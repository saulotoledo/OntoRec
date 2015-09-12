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

	class NodeFeatureMappingStructure {
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
			NodeManager<?>.NodeFeatureMappingStructure compareObj;
			if (obj instanceof NodeManager<?>.NodeFeatureMappingStructure) {
				compareObj = (NodeFeatureMappingStructure) obj;

				return
					this.getFeatureName().equals(compareObj.getFeatureName())
					&& this.getNode().equals(compareObj.getNode())
					&& this.getAttribute().equals(compareObj.getAttribute());
			}
			return false;
		}
	}

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
    private Map<String, NodeFeatureMappingStructure> featureMapping =
    		new HashMap<String, NodeFeatureMappingStructure>();

    //private Map<String, Mappable> featureMappingMap =
    //		new HashMap<String, Mappable>();

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
    //TODO: doc -> The node has to have the attr
    //TODO: because inherited attrs
    public NodeManager<T> addFeatureMapping(
            String featureName, Node<T> node, NodeAttribute attribute)
            throws Exception {

        NodeFeatureMappingStructure featureMappingStructure;
        if (attribute == null) {
            featureMappingStructure = new NodeFeatureMappingStructure(
                    featureName, node);
        } else {
            featureMappingStructure = new NodeFeatureMappingStructure(
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

    	for (NodeFeatureMappingStructure featureMappingStructure :
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

    	for (NodeFeatureMappingStructure featureMappingStructure :
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

        for (NodeFeatureMappingStructure featureMappingStructure :
                this.featureMapping.values()) {

            if (featureMappingStructure.isMappingToAttribute()
                    && !mappedNodes.contains(
                    featureMappingStructure.getNode())) {
                mappedNodes.add(featureMappingStructure.getNode());
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

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
    		Integer k) {
    	return this.getFeaturesWeight(selectedFeatures, k, true);
    }

    // TODO: complete and fix method
    // TODO: check distance to the node itself
    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
    		Integer k, Boolean ignoreOnlyBegottenFathers) {

    	Map<String, Double> result = new HashMap<String, Double>();

        Set<Node<T>> directMappedNodes = this.getMappedNodes();
        Set<Node<T>> attributeNodes = this.getAttributeNodes();
        Set<Node<T>> allMappedRelatedNodes = new HashSet<Node<T>>();
        allMappedRelatedNodes.addAll(directMappedNodes);
        allMappedRelatedNodes.addAll(attributeNodes);

    	Set<String> mappedFeatures = this.featureMapping.keySet();
    	NodeFeatureMappingStructure featureMappingStructure;
    	Map<Node<T>, Integer> affectedNodesDistances;
    	Node<T> currentNode;
    	Integer pathsSum = 0;

    	for (String selectedFeature : selectedFeatures) {
    		featureMappingStructure = this.featureMapping.get(selectedFeature);

    		currentNode = featureMappingStructure.getNode();
            affectedNodesDistances = currentNode.getDistancesTo(
                    allMappedRelatedNodes, k, ignoreOnlyBegottenFathers);

            for (String destinyFeature : mappedFeatures) {
                if (selectedFeature != destinyFeature) {

                    if (featureMappingStructure.isMappingToAttribute()) {
                        if (!result.containsKey(selectedFeature)) {
                            result.put(selectedFeature, 0d);
                        } else {

                        }
                    }
                }
            }
    	}

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
