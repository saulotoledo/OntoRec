/*
 * OntoRec, Ontology Based Recommender Systems Algorithm License: GNU Lesser
 * General Public License (LGPL), version 3. See the LICENSE file in the root
 * directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.ufcg.splab.recsys.ontorec.weighting.NodeWeightingApproach;

/**
 * Creates a node manager.
 *
 * @author Saulo Toledo
 * @param <T> The node type.
 */
public class NodeManager<T>
{
    /**
     * The application logger.
     */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(NodeManager.class);

    /**
     * A nodes map to avoid repetitions. Each node is registered here when
     * created.
     */
    private final Map<T, Node<T>> nodeMap = new HashMap<T, Node<T>>();

    /**
     * Defines if this manager should ignore only begotten fathers in its
     * operations.
     */
    private final Boolean lambda;

    /**
     * Defines if this manager should achieve all the other mapped nodes when
     * performing weight calculations for each mapped node that is being
     * processed. This will impact at total number of verified paths and will
     * change the algorithm results.
     */
    private final Boolean upsilon;

    /**
     * Connects feature names to Nodes.
     */
    private final Map<String, NodeFeatureMappingStructure<T>> featureMapping = new HashMap<String, NodeFeatureMappingStructure<T>>();

    private final NodeWeightingApproach<T> nodeWeightingApproach;

    public NodeManager(NodeWeightingApproach<T> nodeWeightingApproach,
        Boolean lambda, Boolean upsilon)
    {
        LOGGER.debug(
            "--------------------------------------------------------------------------");
        LOGGER.debug(String.format(
            "A NodeManager was created by using the %s approach, λ = '%s' and υ = '%s'",
            nodeWeightingApproach.getClass().getSimpleName(),
            String.valueOf(lambda),
            String.valueOf(upsilon)));

        this.nodeWeightingApproach = nodeWeightingApproach;
        this.lambda = lambda;
        this.upsilon = upsilon;
    }

    /**
     * Returns a registered node or creates a new one, if it does not exists.
     *
     * @return The node with the informed data.
     */
    public Node<T> getNode(T data)
    {
        Node<T> node;
        if (this.nodeExists(data)) {
            node = this.nodeMap.get(data);
        } else {
            node = new Node<T>(data);
            this.nodeMap.put(data, node);

            LOGGER.debug(
                String.format("A new node was created for this manager: '%s'",
                    node.toString()));
        }
        return node;
    }

    public NodeAttribute createAttribute(String attributeName,
        Node<T> attachedNode)
    {
        NodeAttribute attribute = new NodeAttribute(attributeName);
        attachedNode.addAttribute(attribute);

        return attribute;
    }

    /**
     * Verify if a node exists by using its data.
     *
     * @param data The node data.
     * @return true if the node exists, false otherwise.
     */
    public boolean nodeExists(T data)
    {
        return this.nodeMap.keySet().contains(data);
    }

    /**
     * Verify if a node exists.
     *
     * @param element The node to find.
     * @return true if the node exists, false otherwise.
     */
    public boolean nodeExists(Node<T> element)
    {
        return this.nodeMap.containsValue(element);
    }

    public NodeManager<T> addFeatureMapping(String featureName, Node<T> node)
        throws Exception
    {
        return this.addFeatureMapping(featureName, node, null);
    }

    public NodeManager<T> addFeatureMapping(String featureName, Node<T> node,
        NodeAttribute attribute) throws Exception
    {
        NodeFeatureMappingStructure<T> featureMappingStructure;
        if (attribute == null) {
            LOGGER.debug(String.format(
                "A new mapping was initialized to this NodeManager. The feature '%s' was directly mapped to the node '%s'",
                featureName, node.toString()));

            featureMappingStructure = new NodeFeatureMappingStructure<T>(
                featureName, node);
        } else {
            LOGGER.debug(String.format(
                "A new mapping was initialized to this NodeManager. The feature '%s' was mapped to the attribute '%s' in the node '%s'",
                featureName, attribute.toString(), node.toString()));

            if ( !node.getAllAttributes().contains(attribute)) {
                String errorMessage = String.format(
                    "The node '$s' do not have (or inherits) the attribute '$s'",
                    node.toString(), attribute.getName());

                LOGGER.error(errorMessage);
                throw new Exception(errorMessage);
            }

            featureMappingStructure = new NodeFeatureMappingStructure<T>(
                featureName, node, attribute);
        }

        if ( !this.featureMapping.containsKey(featureName)) {
            this.featureMapping.put(featureName, featureMappingStructure);
        } else {
            LOGGER.debug(String.format(
                "The feature '%s' is already mapped, skipping!", featureName));
        }

        return this;
    }

    public boolean removeFeatureMapping(String featureName)
    {
        if (this.featureMapping.containsKey(featureName)) {
            LOGGER.debug(String.format(
                "The feature mapping for the feature '%s' was successfully removed",
                featureName));

            this.featureMapping.remove(featureName);
            return true;
        }

        LOGGER.debug(String.format(
            "It was not possible to remove the feature mapping for the feature '%s'",
            featureName));

        return false;
    }

    public Set<Node<T>> getMappedNodes()
    {
        Set<Node<T>> mappedNodes = new HashSet<Node<T>>();

        for (NodeFeatureMappingStructure<T> featureMappingStructure : this.featureMapping
            .values()) {

            if ( !featureMappingStructure.isMappingToAttribute()
                && !mappedNodes.contains(featureMappingStructure.getNode())) {
                mappedNodes.add(featureMappingStructure.getNode());
            }
        }

        return mappedNodes;
    }

    public Set<String> getMappedFeatures()
    {
        Set<String> mappedFeatures = new HashSet<String>();

        for (NodeFeatureMappingStructure<T> featureMappingStructure : this.featureMapping
            .values()) {

            if ( !mappedFeatures
                .contains(featureMappingStructure.getFeatureName())) {
                mappedFeatures.add(featureMappingStructure.getFeatureName());
            }
        }

        return mappedFeatures;
    }

    public Set<Node<T>> getAttributeNodes()
    {
        Set<Node<T>> mappedNodes = new HashSet<Node<T>>();

        for (NodeFeatureMappingStructure<T> featureMappingStructure : this.featureMapping
            .values()) {

            if (featureMappingStructure.isMappingToAttribute()
                && !mappedNodes.contains(featureMappingStructure.getNode())) {
                mappedNodes.add(featureMappingStructure.getNode());
            }
        }

        return mappedNodes;
    }

    public Map<String, Double> getFeaturesWeight(Set<String> selectedFeatures,
        Integer tau)
    {
        LOGGER.debug(
            String.format("Getting the features' weight for τ = '%s'", tau));

        Set<Node<T>> directMappedNodes = this.getMappedNodes();
        Set<Node<T>> attributeNodes = this.getAttributeNodes();

        Map<String, Double> reachableFeaturesWeights = this.nodeWeightingApproach
            .getFeaturesWeight(selectedFeatures, directMappedNodes,
                attributeNodes, this.featureMapping, tau,
                this.getLambda(),
                this.getUpsilon());

        return this.addUnreachableFeaturesTo(reachableFeaturesWeights);
    }

    private Map<String, Double> addUnreachableFeaturesTo(
        Map<String, Double> reachableFeaturesWeights)
    {
        Map<String, Double> result = reachableFeaturesWeights;
        for (String featureName : this.featureMapping.keySet()) {
            if ( !result.containsKey(featureName)) {
                LOGGER.debug(String.format(
                    "The feature '%s' is unreachable. Adding it to the result vector with value 0",
                    featureName));
                result.put(featureName, 0d);
            }
        }
        return result;
    }

    /**
     * Returns if this manager ignores only begotten fathers in its operations.
     *
     * @return True if ignores, false otherwise.
     */
    public Boolean getLambda()
    {
        return this.lambda;
    }

    /**
     * Returns if this manager should achieve all the other mapped nodes when
     * performing weight calculations.
     *
     * @return True if achieve, false otherwise.
     */
    public Boolean getUpsilon()
    {
        return this.upsilon;
    }

    /**
     * Returns the Node Weighting Approach object.
     *
     * @return The Node Weighting Approach object.
     */
    public NodeWeightingApproach<T> getNodeWeightingApproach()
    {
        return this.nodeWeightingApproach;
    }

    public String getNodeTreeAsString()
    {
        String result = "";

        for (Node<T> node : this.nodeMap.values()) {
            result += node.toString() + "\n\t> [\n";
            Set<Node<T>> childNodes = node.getChildren();
            Iterator<Node<T>> itNodes = childNodes.iterator();
            while (itNodes.hasNext()) {
                result += "\t\t" + itNodes.next().toString();
                if (itNodes.hasNext()) {
                    result += ",\n";
                }
            }
            result += "\n\t]";

            result += " {\n";
            Set<NodeAttribute> nodeAttributes = node.getAllAttributes();
            Iterator<NodeAttribute> itAttrs = nodeAttributes.iterator();
            while (itAttrs.hasNext()) {
                result += "\t\t" + itAttrs.next().toString();
                if (itAttrs.hasNext()) {
                    result += ",\n";
                }
            }
            result += "\n\t}\n";
        }

        return result;
    }

    public String getMappingsAsString()
    {
        String result = "[\n";

        Iterator<NodeFeatureMappingStructure<T>> it = this.featureMapping
            .values().iterator();

        while (it.hasNext()) {
            result += "\t" + it.next().toString();
            if (it.hasNext()) {
                result += ",\n";
            }
        }
        result += "\n]";

        return result;
    }
}
