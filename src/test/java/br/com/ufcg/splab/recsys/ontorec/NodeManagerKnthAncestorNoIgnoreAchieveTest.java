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

public class NodeManagerKnthAncestorNoIgnoreAchieveTest extends AbstractNodeManagerTest {

    private NodeManager<String> nmBfsNoIgnoreAchieve;

    @Before
    public void setupComplexGraphManagedWithKnthAncestorNoIgnoreAchieve() {
        this.nmBfsNoIgnoreAchieve = new NodeManager<String>(
                new KnthAncestorNodeWeightingApproach<String>(), false, true);

        this.buildComplexGraphAt(this.nmBfsNoIgnoreAchieve);
    }

    @Test
    public void testFeaturesKnthWeightingWithNoIgnoreAchieve() {
        Node<String> xNode = this.nmBfsNoIgnoreAchieve.getNode("X");
        Node<String> yNode = this.nmBfsNoIgnoreAchieve.getNode("Y");
        Node<String> structuralFeature = this.nmBfsNoIgnoreAchieve.getNode("StructuralFeature");
        Node<String> deploymentTarget = this.nmBfsNoIgnoreAchieve.getNode("DeploymentTarget");
        Node<String> property = this.nmBfsNoIgnoreAchieve.getNode("Property");
        Node<String> port = this.nmBfsNoIgnoreAchieve.getNode("Port");

        xNode.addParent(structuralFeature);
        yNode.addParent(deploymentTarget);

        try {
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("PSA", property, new NodeAttribute("isStatic"));
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("PDA", property, new NodeAttribute("isDerived"));
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("POP", port);
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("X-FEATURE", xNode);
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("Y-FEATURE", yNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> selectedFeatures = new HashSet<String>();
        selectedFeatures.add("X-FEATURE");
        selectedFeatures.add("Y-FEATURE");

        Map<String, Double> correctResult = new HashMap<String, Double>();
        correctResult.put("PSA", new Double(((1d - 6d/23d) + (1d - 7d/27d))/2));
        correctResult.put("PDA", new Double(((1d - 6d/23d) + (1d - 7d/27d))/2));
        correctResult.put("POP", new Double(((1d - 6d/23d) + (1d - 7d/27d))/2));
        correctResult.put("X-FEATURE", 1d);
        correctResult.put("Y-FEATURE", 1d);

        Map<String, Double> result = this.nmBfsNoIgnoreAchieve.getFeaturesWeight(selectedFeatures, 3);

        assertTrue(correctResult.size() == result.size());
        for (String feature : correctResult.keySet()) {
            assertTrue(correctResult.get(feature).equals(result.get(feature)));
        }
    }

}
