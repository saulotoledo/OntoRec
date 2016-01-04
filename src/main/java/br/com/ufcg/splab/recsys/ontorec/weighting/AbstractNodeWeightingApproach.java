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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ufcg.splab.recsys.ontorec.Node;
import br.com.ufcg.splab.recsys.ontorec.NodeFeatureMappingStructure;

public abstract class AbstractNodeWeightingApproach<T> implements
        NodeWeightingApproach<T>
{
    /**
     * The application logger.
     */
    protected static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractNodeWeightingApproach.class);

    public AbstractNodeWeightingApproach()
    {
        super();
    }

    protected Map<String, Double> updateFeaturesWeightMap(
            Map<String, Double> map, Set<String> selectedFeatures,
            Integer pathsSum)
    {
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

    protected Map<String, Integer> computeDistancesToFeatures(
            Set<String> selectedFeatures, String referenceFeature,
            NodeFeatureMappingStructure<T> currentFeatureMappingStructure,
            Map<String, NodeFeatureMappingStructure<T>> featureMapping,
            Map<Node<T>, Integer> affectedNodesDistances,
            Boolean achieveOtherMappedNodes)
    {
        Set<String> mappedFeatures = featureMapping.keySet();
        Map<String, Integer> result = new HashMap<String, Integer>();

        for (String destinyFeature : mappedFeatures) {

            Integer currentDistance = 0;
            if (!destinyFeature.equals(referenceFeature)
                    && (!selectedFeatures.contains(destinyFeature) || (achieveOtherMappedNodes && selectedFeatures
                            .contains(destinyFeature)))) {

                NodeFeatureMappingStructure<T> destinyMapping = featureMapping
                        .get(destinyFeature);

                Node<T> destinyNode = destinyMapping.getNode();

                if (currentFeatureMappingStructure.isMappingToAttribute()) {
                    // The distance from the feature to the node where
                    // it is attached:
                    currentDistance += 1;
                }

                // If the node isn't at affectedNodesDistances map, it is
                // because it is unreachable by the given k:
                Boolean theNodeIsReachable = (affectedNodesDistances
                        .get(destinyNode) != null);

                if (theNodeIsReachable) {
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
        }
        return result;
    }

    protected Map<String, Double> mergeResultMaps(
            Map<String, Double> resultMap1, Map<String, Double> resultMap2)
    {
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
