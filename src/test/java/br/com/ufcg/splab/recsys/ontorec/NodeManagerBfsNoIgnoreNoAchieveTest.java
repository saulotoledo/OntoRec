/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.com.ufcg.splab.recsys.ontorec.weighting.BFSPathNodeWeightingApproach;

public class NodeManagerBfsNoIgnoreNoAchieveTest extends AbstractNodeManagerTest {

    private NodeManager<String> nmBfsNoIgnoreNoAchieve;

    @Before
    public void setupComplexGraphManagedWithBfsNoIgnoreNoAchieve() {
        this.nmBfsNoIgnoreNoAchieve = new NodeManager<String>(
                new BFSPathNodeWeightingApproach<String>(), false, false);

        this.buildComplexGraphAt(this.nmBfsNoIgnoreNoAchieve);
    }

    // TODO: Repeat the test with the ignore = true:
    @Test
    public void testFeaturesBFSWeightingWithNoIgnore() {
        Node<String> xNode = this.nmBfsNoIgnoreNoAchieve.getNode("X");
        Node<String> yNode = this.nmBfsNoIgnoreNoAchieve.getNode("Y");
        Node<String> structuralFeature = this.nmBfsNoIgnoreNoAchieve.getNode("StructuralFeature");
        Node<String> deploymentTarget = this.nmBfsNoIgnoreNoAchieve.getNode("DeploymentTarget");
        Node<String> property = this.nmBfsNoIgnoreNoAchieve.getNode("Property");
        Node<String> port = this.nmBfsNoIgnoreNoAchieve.getNode("Port");

        xNode.addParent(structuralFeature);
        yNode.addParent(deploymentTarget);

        try {
            this.nmBfsNoIgnoreNoAchieve.addFeatureMapping("PSA", property, new NodeAttribute("isStatic"));
            this.nmBfsNoIgnoreNoAchieve.addFeatureMapping("PDA", property, new NodeAttribute("isDerived"));
            this.nmBfsNoIgnoreNoAchieve.addFeatureMapping("POP", port);
            this.nmBfsNoIgnoreNoAchieve.addFeatureMapping("X-FEATURE", xNode);
            this.nmBfsNoIgnoreNoAchieve.addFeatureMapping("Y-FEATURE", yNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> selectedFeatures = new HashSet<String>();
        selectedFeatures.add("X-FEATURE");
        selectedFeatures.add("Y-FEATURE");

        Map<String, Double> correctResult = new HashMap<String, Double>();
        correctResult.put("PSA", new Double(((1d - 3d/9d) + (1d - 3d/9d))/2));
        correctResult.put("PDA", new Double(((1d - 3d/9d) + (1d - 3d/9d))/2));
        correctResult.put("POP", new Double(((1d - 3d/9d) + (1d - 3d/9d))/2));
        correctResult.put("X-FEATURE", 1d);
        correctResult.put("Y-FEATURE", 1d);

        Map<String, Double> result = this.nmBfsNoIgnoreNoAchieve.getFeaturesWeight(selectedFeatures, 3);

        assertTrue(correctResult.size() == result.size());
        for (String feature : correctResult.keySet()) {
            assertTrue(correctResult.get(feature).equals(result.get(feature)));
        }
    }

    @Test
    public void testSubgraphPathsWithNoIgnoreOnlyBegottenFathers() {
        Node<String> xNode = this.nmBfsNoIgnoreNoAchieve.getNode("X");
        Node<String> yNode = this.nmBfsNoIgnoreNoAchieve.getNode("Y");

        Node<String> element = this.nmBfsNoIgnoreNoAchieve.getNode("Element");
        Node<String> namedElement = this.nmBfsNoIgnoreNoAchieve.getNode("NamedElement");
        Node<String> redefinableElement = this.nmBfsNoIgnoreNoAchieve.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nmBfsNoIgnoreNoAchieve.getNode("MultiplicityElement");
        Node<String> feature = this.nmBfsNoIgnoreNoAchieve.getNode("Feature");
        Node<String> typedElement = this.nmBfsNoIgnoreNoAchieve.getNode("TypedElement");
        Node<String> structuralFeature = this.nmBfsNoIgnoreNoAchieve.getNode("StructuralFeature");
        Node<String> connectableElement = this.nmBfsNoIgnoreNoAchieve.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nmBfsNoIgnoreNoAchieve.getNode("DeploymentTarget");
        Node<String> property = this.nmBfsNoIgnoreNoAchieve.getNode("Property");
        Node<String> port = this.nmBfsNoIgnoreNoAchieve.getNode("Port");

        xNode.addParent(structuralFeature);
        yNode.addParent(deploymentTarget);

        // Creating the test set:
        Set<LinkedList<Node<String>>> correctPaths = new HashSet<LinkedList<Node<String>>>();

        LinkedList<Node<String>> path = new LinkedList<Node<String>>();
        path.add(xNode);
        path.add(structuralFeature);
        path.add(typedElement);
        path.add(namedElement);
        correctPaths.add(path);

        path = new LinkedList<Node<String>>();
        path.add(xNode);
        path.add(structuralFeature);
        path.add(feature);
        path.add(redefinableElement);
        correctPaths.add(path);

        path = new LinkedList<Node<String>>();
        path.add(xNode);
        path.add(structuralFeature);
        path.add(multiplicityElement);
        path.add(element);
        correctPaths.add(path);

        // Paths lesser than tau must be ignored:
        LinkedList<Node<String>> invalidPath = new LinkedList<Node<String>>();
        invalidPath.add(xNode);
        invalidPath.add(structuralFeature);
        invalidPath.add(multiplicityElement);
        invalidPath.add(element);

        // Testing (see the "false" parameter for "No Ignore"):
        Set<LinkedList<Node<String>>> paths = xNode.getSubgraphMaxHeightPaths(3, false);

        // Checking if paths lesser than tau are correctly ignored:
        assertFalse(paths.contains(invalidPath));

        assertTrue(correctPaths.size() == paths.size());
        for (LinkedList<Node<String>> currentPath : paths) {
            assertTrue(correctPaths.contains(currentPath));
        }
        assertTrue(correctPaths.size() == paths.size());
    }

}
