/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec.weighting;

import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.recsys.ontorec.Node;
import br.com.ufcg.splab.recsys.ontorec.NodeFeatureMappingStructure;

public interface NodeWeightingApproach<T> {

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
            Set<Node<T>> directMappedNodes, Set<Node<T>> attributeNodes,
            Map<String, NodeFeatureMappingStructure<T>> featureMapping,
            Integer k, Boolean ignoreOnlyBegottenFathers,
            Boolean achieveOtherMappedNodes);

}
