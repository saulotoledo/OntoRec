package br.com.ufcg.splab.umlrec.approach.knowledge.graph.weighting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.umlrec.approach.knowledge.graph.Node;
import br.com.ufcg.splab.umlrec.approach.knowledge.graph.NodeFeatureMappingStructure;

public class BFSPathNodeWeightingApproach<T>
    implements NodeWeightingApproach<T> {

    @Override
    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
            Set<Node<T>> directMappedNodes, Set<Node<T>> attributeNodes,
            Map<String, NodeFeatureMappingStructure<T>> featureMapping,
            Integer k, Boolean ignoreOnlyBegottenFathers) {

        Map<String, Double> result = new HashMap<String, Double>();

        Set<Node<T>> allMappedRelatedNodes = new HashSet<Node<T>>();
        allMappedRelatedNodes.addAll(directMappedNodes);
        allMappedRelatedNodes.addAll(attributeNodes);

        NodeFeatureMappingStructure<T> featureMappingStructure;

        for (String referenceFeature : selectedFeatures) {
            Integer pathsSum = 0;
            Map<String, Double> partialResult = new HashMap<String, Double>();

            featureMappingStructure = featureMapping.get(referenceFeature);

            Node<T> currentNode = featureMappingStructure.getNode();
            Map<Node<T>, Integer> affectedNodesDistances =
                    currentNode.getDistancesTo(allMappedRelatedNodes, k,
                            ignoreOnlyBegottenFathers);

            Map<String, Integer> distancesToFeatures =
                    this.computeDistancesToFeatures(selectedFeatures,
                            referenceFeature, featureMappingStructure,
                            featureMapping, affectedNodesDistances);

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

    private Map<String, Integer> computeDistancesToFeatures(
            Set<String> selectedFeatures, String referenceFeature,
            NodeFeatureMappingStructure<T> currentFeatureMappingStructure,
            Map<String, NodeFeatureMappingStructure<T>> featureMapping,
            Map<Node<T>, Integer> affectedNodesDistances) {

        Set<String> mappedFeatures = featureMapping.keySet();
        Map<String, Integer> result = new HashMap<String, Integer>();

        for (String destinyFeature : mappedFeatures) {

            Integer currentDistance = 0;
            if (!selectedFeatures.contains(destinyFeature)) {

                if (currentFeatureMappingStructure.isMappingToAttribute()) {
                    // The distance from the feature to the node where
                    // it is attached:
                    currentDistance += 1;
                }

                NodeFeatureMappingStructure<T> destinyMapping =
                        featureMapping.get(destinyFeature);

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

}
