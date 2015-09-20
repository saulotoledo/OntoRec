package br.com.ufcg.splab.ontorec.weighting;

import java.util.Map;
import java.util.Set;

import br.com.ufcg.splab.ontorec.Node;
import br.com.ufcg.splab.ontorec.NodeFeatureMappingStructure;

public interface NodeWeightingApproach<T> {

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
            Set<Node<T>> directMappedNodes, Set<Node<T>> attributeNodes,
            Map<String, NodeFeatureMappingStructure<T>> featureMapping,
            Integer k, Boolean ignoreOnlyBegottenFathers);

}
