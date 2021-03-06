/*
 * OntoRec, Ontology Based Recommender Systems Algorithm License: GNU Lesser
 * General Public License (LGPL), version 3. See the LICENSE file in the root
 * directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.com.ufcg.splab.recsys.ontorec.weighting.BFSPathNodeWeightingApproach;

public class NodeManagerBfsNoIgnoreAchieveTest extends AbstractNodeManagerTest
{

    private NodeManager<String> nmBfsNoIgnoreAchieve;

    @Before
    public void setupComplexGraphManagedWithBfsNoIgnoreAchieve()
    {
        this.nmBfsNoIgnoreAchieve = new NodeManager<String>(
            new BFSPathNodeWeightingApproach<String>(), false, true);

        this.buildComplexGraphAt(this.nmBfsNoIgnoreAchieve);
    }

    @Test
    public void testFeaturesBFSWeightingWithNoIgnoreAchieve()
    {
        Node<String> xNode = this.nmBfsNoIgnoreAchieve.getNode("X");
        Node<String> yNode = this.nmBfsNoIgnoreAchieve.getNode("Y");
        Node<String> structuralFeature = this.nmBfsNoIgnoreAchieve
            .getNode("StructuralFeature");
        Node<String> deploymentTarget = this.nmBfsNoIgnoreAchieve
            .getNode("DeploymentTarget");
        Node<String> property = this.nmBfsNoIgnoreAchieve.getNode("Property");
        Node<String> port = this.nmBfsNoIgnoreAchieve.getNode("Port");

        xNode.addParent(structuralFeature);
        yNode.addParent(deploymentTarget);

        try {
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("PSA", property,
                new NodeAttribute("isStatic"));
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("PDA", property,
                new NodeAttribute("isDerived"));
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
        /*
         * TODO: TAUNTH: correctResult.put("PSA", new Double(((1d - 6d/23d) + (1d
         * - 7d/27d))/2)); correctResult.put("PDA", new Double(((1d - 6d/23d) +
         * (1d - 7d/27d))/2)); correctResult.put("POP", new Double(((1d -
         * 6d/23d) + (1d - 7d/27d))/2));
         */
        /*
         * TODO: Check if the error is at example, or at graphic:
         * correctResult.put("PSA", new Double(((1d - 3d/14d) + (1d -
         * 4d/17d))/2)); correctResult.put("PDA", new Double(((1d - 3d/14d) +
         * (1d - 4d/17d))/2)); correctResult.put("POP", new Double(((1d -
         * 3d/14d) + (1d - 4d/17d))/2));
         */
        correctResult.put("PSA",
            new Double( ( (1d - 3d / 13d) + (1d - 3d / 13d)) / 2));
        correctResult.put("PDA",
            new Double( ( (1d - 3d / 13d) + (1d - 3d / 13d)) / 2));
        correctResult.put("POP",
            new Double( ( (1d - 3d / 13d) + (1d - 3d / 13d)) / 2));
        correctResult.put("X-FEATURE", 1d);
        correctResult.put("Y-FEATURE", 1d);

        Map<String, Double> result = this.nmBfsNoIgnoreAchieve
            .getFeaturesWeight(selectedFeatures, 3);

        assertTrue(correctResult.size() == result.size());
        for (String feature : correctResult.keySet()) {
            assertTrue(correctResult.get(feature).equals(result.get(feature)));
        }
    }

    @Test
    public void testDistancesStartingFromAttributes()
    {
        Node<String> structuralFeature = this.nmBfsNoIgnoreAchieve
            .getNode("StructuralFeature");
        Node<String> property = this.nmBfsNoIgnoreAchieve.getNode("Property");
        Node<String> xNode = this.nmBfsNoIgnoreAchieve.getNode("X");
        xNode.addParent(structuralFeature);

        try {
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("PSA", property,
                new NodeAttribute("isStatic"));
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("PDA", property,
                new NodeAttribute("isDerived"));
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("X-FEATURE", xNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> selectedFeatures = new HashSet<String>();
        selectedFeatures.add("PSA");

        Map<String, Double> result = this.nmBfsNoIgnoreAchieve
            .getFeaturesWeight(selectedFeatures, 1);

        Map<String, Double> correctResult = new HashMap<String, Double>();
        correctResult.put("PSA", new Double(1d));
        correctResult.put("PDA", new Double(1d - 2d / 2d));

        
        System.out.println(result);
        System.out.println(correctResult);
        
        assertTrue(correctResult.size() == result.size());
        for (String feature : correctResult.keySet()) {
            assertTrue(correctResult.get(feature).equals(result.get(feature)));
        }

        // Creates a new attribute for "property":
        NodeAttribute aAttr = new NodeAttribute("A-ATTR");
        property.addAttribute(aAttr);
        try {
            this.nmBfsNoIgnoreAchieve.addFeatureMapping("A-ATTR-FEATURE",
                property, aAttr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        correctResult.remove(correctResult.get("PDA"));
        correctResult.put("PDA", new Double(1d - 2d / 4d));
        correctResult.put("A-ATTR-FEATURE", new Double(1d - 2d / 4d));

        result = this.nmBfsNoIgnoreAchieve.getFeaturesWeight(selectedFeatures,
            1);

        assertTrue(correctResult.size() == result.size());
        for (String feature : correctResult.keySet()) {
            assertTrue(correctResult.get(feature).equals(result.get(feature)));
        }
    }

}
