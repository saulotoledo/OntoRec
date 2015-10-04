/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec.weighting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.recsys.ontorec.Node;
import br.com.ufcg.splab.recsys.ontorec.NodeFeatureMappingStructure;

public class BFSPathNodeWeightingApproach<T>
    extends AbstractNodeWeightingApproach<T> {

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
            Set<Node<T>> directMappedNodes, Set<Node<T>> attributeNodes,
            Map<String, NodeFeatureMappingStructure<T>> featureMapping,
            Integer k, Boolean ignoreOnlyBegottenFathers,
            Boolean achieveOtherMappedNodes) {

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
                            featureMapping, affectedNodesDistances,
                            achieveOtherMappedNodes);

            for (String feature : distancesToFeatures.keySet()) {
                Integer distance = distancesToFeatures.get(feature);
                pathsSum += distance;

                partialResult.put(feature, distance.doubleValue());
            }

            partialResult = this.updateFeaturesWeightMap(partialResult,
                    selectedFeatures, pathsSum);

            result = this.mergeResultMaps(result, partialResult);
        }

        return result;
    }

}
