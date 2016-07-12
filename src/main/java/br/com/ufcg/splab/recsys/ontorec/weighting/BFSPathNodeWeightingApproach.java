/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3. See the LICENSE
 * file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec.weighting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.recsys.ontorec.Node;
import br.com.ufcg.splab.recsys.ontorec.NodeFeatureMappingStructure;

public class BFSPathNodeWeightingApproach<T> extends
        AbstractNodeWeightingApproach<T>
{
    @Override
    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
            Set<Node<T>> directMappedNodes, Set<Node<T>> attributeNodes,
            Map<String, NodeFeatureMappingStructure<T>> featureMapping,
            Integer tau, Boolean lambda,
            Boolean upsilon)
    {
        LOGGER.debug(String
                .format("Starting the calculation of weights by using the %s approach for τ=%d and the selected features set '%s'",
                        this.getClass().getSimpleName(), tau, selectedFeatures));

        Map<String, Double> result = new HashMap<String, Double>();

        // TODO: Possible error: map an attribute for a node, and the own node
        // (the node will appear 2 times in allMappedNodes:
        Set<Node<T>> allMappedRelatedNodes = new HashSet<Node<T>>();
        allMappedRelatedNodes.addAll(directMappedNodes);
        allMappedRelatedNodes.addAll(attributeNodes);

        for (String referenceFeature : selectedFeatures) {

            Integer pathsSum = 0;
            Map<String, Double> partialResult = new HashMap<String, Double>();
            NodeFeatureMappingStructure<T> featureMappingStructure = featureMapping
                    .get(referenceFeature);

            LOGGER.debug(String
                    .format("The currently observed feature is '%s'. It is currently mapped as '%s'",
                            referenceFeature, featureMappingStructure));

            Node<T> currentNode = featureMappingStructure.getNode();

            Map<Node<T>, Integer> affectedNodesDistances;
            if (featureMappingStructure.isMappingToAttribute()) {
                affectedNodesDistances = currentNode
                        .getDistancesTo(allMappedRelatedNodes, tau - 1,
                                lambda);
            } else {
                affectedNodesDistances = currentNode.getDistancesTo(
                        allMappedRelatedNodes, tau, lambda);
            }

            Map<String, Integer> distancesToFeatures = this
                    .computeDistancesToFeatures(selectedFeatures,
                            referenceFeature, featureMappingStructure,
                            featureMapping, affectedNodesDistances,
                            upsilon);

            LOGGER.debug(String
                    .format("The calculated distances from the feature '%s' to the other features are '%s'",
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

            LOGGER.debug(String
                    .format("Updating the final result for the calculations, we have %s",
                            partialResult));
        }

        LOGGER.debug(String.format("The final calculated result is %s", result));

        return result;
    }

}
