package br.com.ufcg.splab.umlrec.approach.knowledge.graph.weighting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.umlrec.approach.knowledge.graph.Node;
import br.com.ufcg.splab.umlrec.approach.knowledge.graph.NodeFeatureMappingStructure;

public class KnthAncestorNodeWeightingApproach<T> implements
        NodeWeightingApproach<T> {

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

            // TODO: (k - 1) if is mapped to attr (here and at BFS approach)
            //if ()
            Set<Node<T>> maxNodesFromK = currentNode.extractMaxNodesFromK(k);

            Map<Node<T>, Integer> affectedNodesDistances =
                    new HashMap<Node<T>, Integer>();



            //TODO: add "k" to path length
            for (Node<T> currentMaxNode : maxNodesFromK) {
                Map<Node<T>, Integer> distancesToMappedNodes =
                        this.getBFSDistancesAtDescendantsTo(currentMaxNode,
                        allMappedRelatedNodes, ignoreOnlyBegottenFathers);

                for (Node<T> node : distancesToMappedNodes.keySet()) {
                    if (!affectedNodesDistances.containsKey(node)) {
                        affectedNodesDistances.put(node,
                                distancesToMappedNodes.get(node));
                    } else {
                        Integer currentValue = affectedNodesDistances.get(node);
                        if (distancesToMappedNodes.get(node) < currentValue) {
                            affectedNodesDistances.remove(node);
                            affectedNodesDistances.put(node,
                                    distancesToMappedNodes.get(node));
                        }
                    }
                }
            }

            System.out.println(affectedNodesDistances);

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




    // TODO: Duplicated, move to abstract class if working
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

    // TODO: Duplicated, move to abstract class if working
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


    // TODO: Duplicated, move to abstract class if working
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











    /**
     * Returns all distances from the current node to a set of reference nodes
     * that are his descendants. If the reference node is not a descendant of
     * the current one, it will be ignored at results.
     *
     * @param  referenceNodes A set of reference nodes to consider at results.
     * @param  ignoreOnlyBegottenFathers Allows to define if only begotten
     *         fathers are ignored or not at results.
     * @return A map with the distances from the current node to each descendant
     *         that are at reference nodes list. At result, the key is a node
     *         and the value is the distance relative to the current node.
     */
    private Map<Node<T>, Integer> getBFSDistancesAtDescendantsTo(
            Node<T> currentNode, Set<Node<T>> referenceNodes,
            boolean ignoreOnlyBegottenFathers) {

        Map<Node<T>, LinkedList<Node<T>>> paths =
                this.getBFSPathsAtDescendantsTo(currentNode, referenceNodes,
                        ignoreOnlyBegottenFathers);

        Map<Node<T>, Integer> result = new HashMap<Node<T>, Integer>();

        for (Node<T> referenceNode : paths.keySet()) {
            result.put(referenceNode, paths.get(referenceNode).size() - 1);
        }

        return result;
    }

    /**
     * Returns all the shortest paths from the current node to each descendant
     * that is at reference nodes set (one path for each descendant). The result
     * is reached by applying a Breadth First Search (BFS) approach. If there
     * are two paths with the same length, the algorithm will return only one of
     * them.
     *
     * @TODO: Different paths can result in different affected descendants. The BFS approach chooses automatically what path with the same length will be returned and this will impact at algorithm results. A deepest analysis must be done here.
     *
     * @param  referenceNodes A set of reference nodes to consider at results.
     * @param  ignoreOnlyBegottenFathers Allows to define if only begotten
     *         fathers are ignored or not at results.
     * @return A map with the paths from the current node to each descendant
     *         that are at reference nodes list. At result, the key is a node
     *         and the value is the full path considering the
     *         ignoreOnlyBegottenFathers parameter.
     */
    private Map<Node<T>, LinkedList<Node<T>>> getBFSPathsAtDescendantsTo(
            Node<T> currentNode, Set<Node<T>> referenceNodes,
            boolean ignoreOnlyBegottenFathers) {

        // Breadth First Search algorithm:
        List<Node<T>> nodesQueue  = new LinkedList<Node<T>>();
        Map<Node<T>, Node<T>> cameFrom = new HashMap<Node<T>, Node<T>>();

        nodesQueue.add(currentNode);
        cameFrom.put(currentNode, null);

        Node<T> current;
        while (nodesQueue.size() != 0) {
            current = nodesQueue.remove(0);

            for (Node<T> child : current.getChildren()) {
                if (!cameFrom.keySet().contains(child)) {
                    nodesQueue.add(child);
                    cameFrom.put(child, current);
                }
            }
        }

        // Paths construction:
        Map<Node<T>, LinkedList<Node<T>>> result =
                new HashMap<Node<T>, LinkedList<Node<T>>>();

        for (Node<T> node : referenceNodes) {
            // Unreachable nodes are not at cameFrom map:
            if (cameFrom.keySet().contains(node)) {
                LinkedList<Node<T>> path = this.bfsReconstructPath(
                        cameFrom, node, ignoreOnlyBegottenFathers);
                result.put(node, path);
            }
        }

        return result;
    }

    /**
     * Builds a path based on a node mapping built by a Breadth First Search
     * (BFS) approach
     *
     * @param  cameFrom A node mapping built by a Breadth First Search (BFS)
     *         approach.
     * @param  goal The reference node to build the path.
     * @param  ignoreOnlyBegottenFathers Allows to define if only begotten
     *         fathers are ignored or not at results.
     * @return A list containing the path from the higher node at cameFrom
     *         mapping to the goal node.
     */
    //TODO: Duplicated method (in Node.java), this needs refactoring!
    private LinkedList<Node<T>> bfsReconstructPath(
            Map<Node<T>, Node<T>> cameFrom, Node<T> goal,
            boolean ignoreOnlyBegottenFathers)
    {
        LinkedList<Node<T>> path = new LinkedList<Node<T>>();

        Node<T> current = goal;
        path.add(current);
        while (cameFrom.get(current) != null) {
            current = cameFrom.get(current);

            if (ignoreOnlyBegottenFathers) {
                if (current.getChildren().size() != 1) {
                    path.addFirst(current);
                }
            } else {
                path.addFirst(current);
            }
        }

        return path;
    }
}
