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
    		Map<String, Double> map, Set<String> selectedFeatures,
    		Integer pathsSum) {

        Map<String, Double> result = new HashMap<String, Double>(map);

        for (String feature : new HashSet<String>(result.keySet())) {
            if (!selectedFeatures.contains(feature)) {
                Double oldValue = result.get(feature);
                Double newValue = 0d;

                if (pathsSum != 0) {
                    newValue = 1 - (oldValue / pathsSum.doubleValue());
                }

                result.remove(feature);
                result.put(feature, newValue);
            }
        }

        for (String feature : selectedFeatures) {
            if (result.containsKey(feature)) {
                result.remove(feature);
            }
            result.put(feature, 1d);
        }

		return result;
    }

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
    		Integer k) {
    	return this.getFeaturesWeight(selectedFeatures, k, true);
    }

    // TODO: complete and fix method
    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
    		Integer k, Boolean ignoreOnlyBegottenFathers) {

    	Map<String, Double> result = new HashMap<String, Double>();

        Set<Node<T>> directMappedNodes = this.getMappedNodes();
        Set<Node<T>> attributeNodes = this.getAttributeNodes();
        Set<Node<T>> allMappedRelatedNodes = new HashSet<Node<T>>();
        allMappedRelatedNodes.addAll(directMappedNodes);
        allMappedRelatedNodes.addAll(attributeNodes);

    	NodeFeatureMappingStructure featureMappingStructure;

    	for (String referenceFeature : selectedFeatures) {
            Integer pathsSum = 0;
            Map<String, Double> partialResult = new HashMap<String, Double>();

    		featureMappingStructure = this.featureMapping.get(referenceFeature);

            Node<T> currentNode = featureMappingStructure.getNode();
            Map<Node<T>, Integer> affectedNodesDistances =
                    currentNode.getDistancesTo(allMappedRelatedNodes, k,
                            ignoreOnlyBegottenFathers);

            Map<String, Integer> distancesToFeatures =
                    this.computeDistancesToFeatures(selectedFeatures,
                            referenceFeature, featureMappingStructure,
                            affectedNodesDistances);

            for (String feature : distancesToFeatures.keySet()) {
                Integer distance = distancesToFeatures.get(feature);
                pathsSum += distance;

                partialResult.put(feature, distance.doubleValue());
            }

            partialResult = this.updateFeaturesWeightMap(partialResult, selectedFeatures,
                    pathsSum);

            result = this.mergeResultMaps(result, partialResult);
    	}

    	return result;
    }

    private Map<String, Double> mergeResultMaps(Map<String, Double> resultMap1,
            Map<String, Double> resultMap2) {

        Map<String, Double> result = new HashMap<String, Double>(resultMap1);

        for (String feature : resultMap2.keySet()) {
            if (!result.containsKey(feature)) {
                result.put(feature, resultMap2.get(feature));
            } else {
                Double oldValue = result.get(feature);
                result.remove(feature);
                result.put(feature, (oldValue + resultMap2.get(feature)) / 2);
            }
        }
        return result;
    }

    private Map<String, Integer> computeDistancesToFeatures(
            Set<String> selectedFeatures, String referenceFeature,
            NodeFeatureMappingStructure currentFeatureMappingStructure,
            Map<Node<T>, Integer> affectedNodesDistances) {

        Set<String> mappedFeatures = this.featureMapping.keySet();
        Map<String, Integer> result = new HashMap<String, Integer>();

        for (String destinyFeature : mappedFeatures) {

            Integer currentDistance = 0;
            if (!selectedFeatures.contains(destinyFeature)) {

                if (currentFeatureMappingStructure.isMappingToAttribute()) {
                    // The distance from the feature to the node where
                    // it is attached:
                    currentDistance += 1;
                }

                NodeFeatureMappingStructure destinyMapping =
                        this.featureMapping.get(destinyFeature);

                Node<T> destinyNode = destinyMapping.getNode();
                currentDistance += affectedNodesDistances.get(destinyNode);

                if (destinyMapping.isMappingToAttribute()) {
                    // The distance from the destiny node to the mapped
                    // feature attached to it:
                    currentDistance += 1;
                }

                if (!result.containsKey(destinyFeature)) {
                    result.put(destinyFeature, currentDistance);
                } else {
                    Integer oldValue = result.get(destinyFeature);
                    if (oldValue > currentDistance) {
                        result.remove(destinyFeature);
                        result.put(destinyFeature, currentDistance);
                    }
                }
            }
        }
        return result;
    }
}
