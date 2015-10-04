/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.com.ufcg.splab.recsys.ontorec.weighting.KnthAncestorNodeWeightingApproach;

public class NodeManagerKnthAncestorNoIgnoreNoAchieveTest extends AbstractNodeManagerTest {

    private NodeManager<String> nmKnthAncestorNoIgnoreNoAchieve;

    @Before
    public void setupComplexGraphManagedWithKnthAncestorNoIgnoreNoAchieve() {
        this.nmKnthAncestorNoIgnoreNoAchieve = new NodeManager<String>(
                new KnthAncestorNodeWeightingApproach<String>(), false, false);

        this.buildComplexGraphAt(this.nmKnthAncestorNoIgnoreNoAchieve);
    }

    // TODO: Repeat the test with the ignore = true:
    @Test
    public void testFeaturesKnthWeightingWithNoIgnore() {
        Node<String> xNode = this.nmKnthAncestorNoIgnoreNoAchieve.getNode("X");
        Node<String> yNode = this.nmKnthAncestorNoIgnoreNoAchieve.getNode("Y");
        Node<String> structuralFeature = this.nmKnthAncestorNoIgnoreNoAchieve.getNode("StructuralFeature");
        Node<String> deploymentTarget = this.nmKnthAncestorNoIgnoreNoAchieve.getNode("DeploymentTarget");
        Node<String> property = this.nmKnthAncestorNoIgnoreNoAchieve.getNode("Property");
        Node<String> port = this.nmKnthAncestorNoIgnoreNoAchieve.getNode("Port");

        xNode.addParent(structuralFeature);
        yNode.addParent(deploymentTarget);

        try {
            this.nmKnthAncestorNoIgnoreNoAchieve.addFeatureMapping("PSA", property, new NodeAttribute("isStatic"));
            this.nmKnthAncestorNoIgnoreNoAchieve.addFeatureMapping("PDA", property, new NodeAttribute("isDerived"));
            this.nmKnthAncestorNoIgnoreNoAchieve.addFeatureMapping("POP", port);
            this.nmKnthAncestorNoIgnoreNoAchieve.addFeatureMapping("X-FEATURE", xNode);
            this.nmKnthAncestorNoIgnoreNoAchieve.addFeatureMapping("Y-FEATURE", yNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> selectedFeatures = new HashSet<String>();
        selectedFeatures.add("X-FEATURE");
        selectedFeatures.add("Y-FEATURE");

        Map<String, Double> correctResult = new HashMap<String, Double>();
        correctResult.put("PSA", new Double(1d - 6d/18d));
        correctResult.put("PDA", new Double(1d - 6d/18d));
        correctResult.put("POP", new Double(1d - 6d/18d));
        correctResult.put("X-FEATURE", 1d);
        correctResult.put("Y-FEATURE", 1d);

        Map<String, Double> result = this.nmKnthAncestorNoIgnoreNoAchieve.getFeaturesWeight(selectedFeatures, 3);

        assertTrue(correctResult.size() == result.size());
        for (String feature : correctResult.keySet()) {
            assertTrue(correctResult.get(feature).equals(result.get(feature)));
        }
    }
}
