/*
 * OntoRec, Ontology Based Recommender Systems Algorithm License: GNU Lesser
 * General Public License (LGPL), version 3. See the LICENSE file in the root
 * directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec.weighting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.recsys.ontorec.Node;
import br.com.ufcg.splab.recsys.ontorec.NodeFeatureMappingStructure;

/**
 * Calculates the distances from the current node to each reference node
 * considering the number of ancestors. This distance is calculated in two
 * steps: (i) by navigating to all τ-nths ancestors and (ii) by finding each
 * node among the reference set that is descendant of the current ancestor. The
 * unreachable nodes at reference set for the given τ are removed from the
 * result. The distance increases by 1 each time that we move from a node to
 * another.
 *
 * @author Saulo Toledo
 * @param <T> The node type.
 */
public class TaunthAncestorNodeWeightingApproach<T>
    extends AbstractNodeWeightingApproach<T>
{
    @Override
    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
        Set<Node<T>> directMappedNodes, Set<Node<T>> attributeNodes,
        Map<String, NodeFeatureMappingStructure<T>> featureMapping, Integer tau,
        Boolean lambda, Boolean upsilon)
    {
        LOGGER.debug(String.format(
            "Starting the calculation of weights by using the %s approach for τ=%d and the selected features set '%s'",
            this.getClass().getSimpleName(), tau, selectedFeatures));

        Map<String, Double> result = new HashMap<String, Double>();

        Set<Node<T>> allMappedRelatedNodes = new HashSet<Node<T>>();
        allMappedRelatedNodes.addAll(directMappedNodes);
        allMappedRelatedNodes.addAll(attributeNodes);

        NodeFeatureMappingStructure<T> featureMappingStructure;

        for (String referenceFeature : selectedFeatures) {
            Integer pathsSum = 0;
            Map<String, Double> partialResult = new HashMap<String, Double>();

            featureMappingStructure = featureMapping.get(referenceFeature);

            LOGGER.debug(String.format(
                "The currently observed feature is '%s'. It is currently mapped as '%s'",
                referenceFeature, featureMappingStructure));

            Node<T> currentNode = featureMappingStructure.getNode();

            // TODO: (tau - 1) if is mapped to attr (now at BFS approach)

            Set<Node<T>> maxNodesFromTau;
            if (featureMappingStructure.isMappingToAttribute()) {
                maxNodesFromTau = currentNode.extractMaxNodesFromTau(tau - 1,
                    lambda);
            } else {
                maxNodesFromTau = currentNode.extractMaxNodesFromTau(tau,
                    lambda);
            }

            Map<Node<T>, Integer> affectedNodesDistances = new HashMap<Node<T>, Integer>();

            for (Node<T> currentMaxNode : maxNodesFromTau) {

                Map<Node<T>, Integer> distancesToMappedNodes = this
                    .getBFSDistancesAtDescendantsTo(currentMaxNode,
                        allMappedRelatedNodes, lambda);

                LOGGER.debug(String.format(
                    "-> Going from '%s' to '%s' we have the following distances: %s",
                    featureMappingStructure.getNode(), currentMaxNode,
                    distancesToMappedNodes));

                for (Node<T> node : distancesToMappedNodes.keySet()) {

                    Integer distance;
                    if (node.equals(currentNode)) {
                        distance = 0;
                        LOGGER.debug(
                            "-> The destiny node is the same of the origin node and the distance is 0");
                    } else {
                        distance = tau + distancesToMappedNodes.get(node);
                        LOGGER.debug(String.format(
                            "-> The final distance is τ (%s) + the distance (%s), that is %s",
                            tau, distancesToMappedNodes.get(node), distance));
                    }

                    if ( !affectedNodesDistances.containsKey(node)) {
                        if (distance > 0) {
                            affectedNodesDistances.put(node, distance);
                            LOGGER.debug(String.format(
                                "-> We are adding the distance for the node '%s' (%s) in the final result",
                                node, distance));
                        }
                    } else {
                        Integer currentValue = affectedNodesDistances.get(node);
                        if (distance > 0 && distance < currentValue) {
                            affectedNodesDistances.remove(node);
                            affectedNodesDistances.put(node, distance);

                            LOGGER.debug(String.format(
                                "-> We are updating the distance for the node '%s' (%s) in the final result (the old value was %s)",
                                node, distance, currentValue));
                        }
                    }
                }
            }

            LOGGER.debug(
                String.format("The final distances to the nodes are '%s'",
                    affectedNodesDistances));

            Map<String, Integer> distancesToFeatures = this
                .computeDistancesToFeatures(selectedFeatures, referenceFeature,
                    featureMappingStructure, featureMapping,
                    affectedNodesDistances, upsilon);

            LOGGER.debug(String.format(
                "The calculated distances from the feature '%s' to the other features are '%s'",
                referenceFeature, distancesToFeatures));

            for (String feature : distancesToFeatures.keySet()) {
                Integer distance = distancesToFeatures.get(feature);
                pathsSum += distance;

                partialResult.put(feature, distance.doubleValue());
            }

            LOGGER.debug(String.format("The total of covered distances is %d",
                pathsSum));

            partialResult = this.updateFeaturesWeightMap(partialResult,
                selectedFeatures, pathsSum);

            LOGGER.debug(String.format(
                "The result for the current feature calculations is %s",
                partialResult));

            result = this.mergeResultMaps(result, partialResult);

            LOGGER.debug(String.format(
                "Updating the final result for the calculations, we have %s",
                partialResult));
        }

        LOGGER
            .debug(String.format("The final calculated result is %s", result));

        return result;
    }

    /**
     * Returns all distances from the current node to a set of reference nodes
     * that are his descendants. If the reference node is not a descendant of
     * the current one, it will be ignored at results.
     *
     * @param referenceNodes A set of reference nodes to consider at results.
     * @param lambda Allows to define if only begotten
     *        fathers are ignored or not at results.
     * @return A map with the distances from the current node to each descendant
     *         that are at reference nodes list. At result, the key is a node
     *         and the value is the distance relative to the current node.
     */
    private Map<Node<T>, Integer> getBFSDistancesAtDescendantsTo(
        Node<T> currentNode, Set<Node<T>> referenceNodes,
        boolean lambda)
    {
        Map<Node<T>, LinkedList<Node<T>>> paths = this
            .getBFSPathsAtDescendantsTo(currentNode, referenceNodes,
                lambda);

        Map<Node<T>, Integer> result = new HashMap<Node<T>, Integer>();

        for (Node<T> referenceNode : paths.keySet()) {
            result.put(referenceNode, paths.get(referenceNode).size() - 1);
        }

        LOGGER.debug(String.format(
            "The BFS distances from the node '%s' to its descendants are %s",
            currentNode.getData(), result));

        return result;
    }

    /**
     * Returns all the shortest paths from the current node to each descendant
     * that is at reference nodes set (one path for each descendant). The result
     * is reached by applying a Breadth First Search (BFS) approach. If there
     * are two paths with the same length, the algorithm will return only one of
     * them.
     *
     * @param referenceNodes A set of reference nodes to consider at results.
     * @param lambda Allows to define if only begotten
     *        fathers are ignored or not at results.
     * @return A map with the paths from the current node to each descendant
     *         that are at reference nodes list. At result, the key is a node
     *         and the value is the full path considering the
     *         lambda parameter.
     */
    private Map<Node<T>, LinkedList<Node<T>>> getBFSPathsAtDescendantsTo(
        Node<T> currentNode, Set<Node<T>> referenceNodes,
        boolean lambda)
    {
        // Breadth First Search algorithm:
        List<Node<T>> nodesQueue = new LinkedList<Node<T>>();
        Map<Node<T>, Node<T>> cameFrom = new HashMap<Node<T>, Node<T>>();

        nodesQueue.add(currentNode);
        cameFrom.put(currentNode, null);

        Node<T> current;
        while (nodesQueue.size() != 0) {
            current = nodesQueue.remove(0);

            for (Node<T> child : current.getChildren()) {
                if ( !cameFrom.keySet().contains(child)) {
                    nodesQueue.add(child);
                    cameFrom.put(child, current);
                }
            }
        }

        // Paths construction:
        Map<Node<T>, LinkedList<Node<T>>> result = new HashMap<Node<T>, LinkedList<Node<T>>>();

        for (Node<T> node : referenceNodes) {
            // Unreachable nodes are not at cameFrom map:
            if (cameFrom.keySet().contains(node)) {
                LinkedList<Node<T>> path = this.bfsReconstructPath(cameFrom,
                    node, lambda);
                result.put(node, path);
            }
        }

        LOGGER
            .debug(String.format("The BFS descendants for the node '%s' are %s",
                currentNode.getData(), result));

        return result;
    }

    /**
     * Builds a path based on a node mapping built by a Breadth First Search
     * (BFS) approach.
     *
     * @param cameFrom A node mapping built by a Breadth First Search (BFS)
     *        approach.
     * @param goal The reference node to build the path.
     * @param lambda Allows to define if only begotten
     *        fathers are ignored or not at results.
     * @return A list containing the path from the higher node at cameFrom
     *         mapping to the goal node.
     */
    // TODO: Duplicated method (in Node.java), this needs refactoring in future!
    private LinkedList<Node<T>> bfsReconstructPath(
        Map<Node<T>, Node<T>> cameFrom, Node<T> goal,
        boolean lambda)
    {
        LinkedList<Node<T>> path = new LinkedList<Node<T>>();

        Node<T> current = goal;
        path.add(current);
        while (cameFrom.get(current) != null) {
            current = cameFrom.get(current);

            if (lambda) {
                if (current.getChildren().size() != 1
                    || (current.getChildren().size() == 1
                        && current.hasMappedAttributes())) {
                    path.addFirst(current);
                }
            } else {
                path.addFirst(current);
            }
        }

        return path;

    }
}
