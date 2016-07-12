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

import br.com.ufcg.splab.recsys.ontorec.weighting.TaunthAncestorNodeWeightingApproach;

public class NodeManagerTaunthAncestorNoIgnoreNoAchieveTest extends AbstractNodeManagerTest {

    private NodeManager<String> nmTaunthAncestorNoIgnoreNoAchieve;

    @Before
    public void setupComplexGraphManagedWithTaunthAncestorNoIgnoreNoAchieve() {
        this.nmTaunthAncestorNoIgnoreNoAchieve = new NodeManager<String>(
                new TaunthAncestorNodeWeightingApproach<String>(), false, false);

        this.buildComplexGraphAt(this.nmTaunthAncestorNoIgnoreNoAchieve);
    }

    // TODO: Repeat the test with the ignore = true:
    @Test
    public void testFeaturesTaunthWeightingWithNoIgnore() {
        Node<String> xNode = this.nmTaunthAncestorNoIgnoreNoAchieve.getNode("X");
        Node<String> yNode = this.nmTaunthAncestorNoIgnoreNoAchieve.getNode("Y");
        Node<String> structuralFeature = this.nmTaunthAncestorNoIgnoreNoAchieve.getNode("StructuralFeature");
        Node<String> deploymentTarget = this.nmTaunthAncestorNoIgnoreNoAchieve.getNode("DeploymentTarget");
        Node<String> property = this.nmTaunthAncestorNoIgnoreNoAchieve.getNode("Property");
        Node<String> port = this.nmTaunthAncestorNoIgnoreNoAchieve.getNode("Port");

        xNode.addParent(structuralFeature);
        yNode.addParent(deploymentTarget);

        try {
            this.nmTaunthAncestorNoIgnoreNoAchieve.addFeatureMapping("PSA", property, new NodeAttribute("isStatic"));
            this.nmTaunthAncestorNoIgnoreNoAchieve.addFeatureMapping("PDA", property, new NodeAttribute("isDerived"));
            this.nmTaunthAncestorNoIgnoreNoAchieve.addFeatureMapping("POP", port);
            this.nmTaunthAncestorNoIgnoreNoAchieve.addFeatureMapping("X-FEATURE", xNode);
            this.nmTaunthAncestorNoIgnoreNoAchieve.addFeatureMapping("Y-FEATURE", yNode);
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

        Map<String, Double> result = this.nmTaunthAncestorNoIgnoreNoAchieve.getFeaturesWeight(selectedFeatures, 3);

        assertTrue(correctResult.size() == result.size());
        for (String feature : correctResult.keySet()) {
            assertTrue(correctResult.get(feature).equals(result.get(feature)));
        }
    }
}
