package br.com.ufcg.splab.umlrec.approach.knowledge.graph.weighting;

import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.umlrec.approach.knowledge.graph.Node;
import br.com.ufcg.splab.umlrec.approach.knowledge.graph.NodeFeatureMappingStructure;

public interface NodeWeightingApproach<T> {

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
            Set<Node<T>> directMappedNodes, Set<Node<T>> attributeNodes,
            Map<String, NodeFeatureMappingStructure<T>> featureMapping,
            Integer k, Boolean ignoreOnlyBegottenFathers);

}
