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

public class NodeManagerBfsIgnoreNoAchieveTest extends AbstractNodeManagerTest {

    private NodeManager<String> nmBfsIgnoreNoAchieve;

    @Before
    public void setupComplexGraphManagedWithBfsIgnoreNoAchieve() {
        this.nmBfsIgnoreNoAchieve = new NodeManager<String>(
                new BFSPathNodeWeightingApproach<String>(), true, false);

        this.buildComplexGraphAt(this.nmBfsIgnoreNoAchieve);
    }

    @Test
    public void testIfWeCanGetAllPossiblePathsFromNodeToRoot() {

        Node<String> element = this.nmBfsIgnoreNoAchieve.getNode("Element");
        Node<String> namedElement = this.nmBfsIgnoreNoAchieve.getNode("NamedElement");
        Node<String> redefinableElement = this.nmBfsIgnoreNoAchieve.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nmBfsIgnoreNoAchieve.getNode("MultiplicityElement");
        Node<String> feature = this.nmBfsIgnoreNoAchieve.getNode("Feature");
        Node<String> typedElement = this.nmBfsIgnoreNoAchieve.getNode("TypedElement");
        Node<String> structuralFeature = this.nmBfsIgnoreNoAchieve.getNode("StructuralFeature");
        Node<String> connectableElement = this.nmBfsIgnoreNoAchieve.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nmBfsIgnoreNoAchieve.getNode("DeploymentTarget");
        Node<String> property = this.nmBfsIgnoreNoAchieve.getNode("Property");

        // Creating the test set:
        Set<LinkedList<Node<String>>> correctPaths = new HashSet<LinkedList<Node<String>>>();

        LinkedList<Node<String>> path = new LinkedList<Node<String>>();
        path.add(property);
        path.add(structuralFeature);
        path.add(feature);
        path.add(redefinableElement);
        path.add(namedElement);
        path.add(element);
        correctPaths.add(path);

        path = new LinkedList<Node<String>>();
        path.add(property);
        path.add(structuralFeature);
        path.add(multiplicityElement);
        path.add(element);
        correctPaths.add(path);

        path = new LinkedList<Node<String>>();
        path.add(property);
        path.add(structuralFeature);
        path.add(typedElement);
        path.add(namedElement);
        path.add(element);
        correctPaths.add(path);

        path = new LinkedList<Node<String>>();
        path.add(property);
        path.add(connectableElement);
        path.add(typedElement);
        path.add(namedElement);
        path.add(element);
        correctPaths.add(path);

        path = new LinkedList<Node<String>>();
        path.add(property);
        path.add(deploymentTarget);
        path.add(namedElement);
        path.add(element);
        correctPaths.add(path);

        // Testing:
        Set<LinkedList<Node<String>>> paths = property.getAllPathsToRoot();

        assertTrue(correctPaths.size() == paths.size());
        for (LinkedList<Node<String>> currentPath : paths) {
            assertTrue(correctPaths.contains(currentPath));
        }
        assertTrue(correctPaths.size() == paths.size());
    }

    @Test
    public void testSubgraphPathsWithIgnoreOnlyBegottenFathers() {
        Node<String> xNode = this.nmBfsIgnoreNoAchieve.getNode("X");
        Node<String> yNode = this.nmBfsIgnoreNoAchieve.getNode("Y");

        Node<String> element = this.nmBfsIgnoreNoAchieve.getNode("Element");
        Node<String> namedElement = this.nmBfsIgnoreNoAchieve.getNode("NamedElement");
        Node<String> redefinableElement = this.nmBfsIgnoreNoAchieve.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nmBfsIgnoreNoAchieve.getNode("MultiplicityElement");
        Node<String> feature = this.nmBfsIgnoreNoAchieve.getNode("Feature");
        Node<String> typedElement = this.nmBfsIgnoreNoAchieve.getNode("TypedElement");
        Node<String> structuralFeature = this.nmBfsIgnoreNoAchieve.getNode("StructuralFeature");
        Node<String> connectableElement = this.nmBfsIgnoreNoAchieve.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nmBfsIgnoreNoAchieve.getNode("DeploymentTarget");
        Node<String> property = this.nmBfsIgnoreNoAchieve.getNode("Property");
        Node<String> port = this.nmBfsIgnoreNoAchieve.getNode("Port");

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
        path.add(namedElement);
        path.add(element);
        correctPaths.add(path);

        // Paths lesser than tau must be ignored:
        LinkedList<Node<String>> invalidPath = new LinkedList<Node<String>>();
        invalidPath.add(xNode);
        invalidPath.add(structuralFeature);
        invalidPath.add(element);

        // Testing:
        Set<LinkedList<Node<String>>> paths = xNode.getSubgraphMaxHeightPaths(3);

        // Checking if paths lesser than tau are correctly ignored:
        assertFalse(paths.contains(invalidPath));

        assertTrue(correctPaths.size() == paths.size());
        for (LinkedList<Node<String>> currentPath : paths) {
            assertTrue(correctPaths.contains(currentPath));
        }
        assertTrue(correctPaths.size() == paths.size());
    }

    @Test
    public void testIfRootNodeIsIgnoredIfThereIsOnlyOneChildForIt() {
        NodeManager<String> nm = new NodeManager<String>(
                new BFSPathNodeWeightingApproach<String>(), true, false);

        // Creating the nodes:
        Node<String> element = nm.getNode("Element");
        Node<String> multiplicityElement = nm.getNode("MultiplicityElement");
        Node<String> structuralFeature = nm.getNode("StructuralFeature");
        Node<String> property = nm.getNode("Property");

        Node<String> xNode = nm.getNode("X");
        Node<String> zNode = nm.getNode("Z");
        Node<String> rootNode = nm.getNode("Root");

        xNode.addParent(structuralFeature);
        zNode.addParent(multiplicityElement);
        element.addParent(rootNode);
        property.addParent(structuralFeature);
        structuralFeature.addParent(multiplicityElement);
        multiplicityElement.addParent(element);

        // Should return "Root", not "Element" at list:
        Set<LinkedList<Node<String>>> paths = property.getSubgraphMaxHeightPaths(3);
        assertTrue(paths.size() == 0);
    }

    @Test
    public void testIfLastElementIsIgnoreIfIsOnlyBegottenFather() {
        NodeManager<String> nm = new NodeManager<String>(
                new BFSPathNodeWeightingApproach<String>(), true, false);

        // Creating the nodes:
        Node<String> element = nm.getNode("Element");
        Node<String> multiplicityElement = nm.getNode("MultiplicityElement");
        Node<String> structuralFeature = nm.getNode("StructuralFeature");
        Node<String> property = nm.getNode("Property");

        Node<String> xNode = nm.getNode("X");
        Node<String> zNode = nm.getNode("Z");
        Node<String> rootNode = nm.getNode("Root");
        Node<String> tNode = nm.getNode("T");

        xNode.addParent(structuralFeature);
        zNode.addParent(multiplicityElement);
        tNode.addParent(rootNode);
        element.addParent(rootNode);
        property.addParent(structuralFeature);
        structuralFeature.addParent(multiplicityElement);
        multiplicityElement.addParent(element);

        // Should return "Root", not "Element" at list:
        Set<LinkedList<Node<String>>> paths = property.getSubgraphMaxHeightPaths(3);

        for (LinkedList<Node<String>> list : paths) {
            assertTrue(list.get(list.size() - 1).equals(rootNode));
        }
    }

    @Test
    public void testDistancesBetweenNodes() {
        Node<String> xNode = this.nmBfsIgnoreNoAchieve.getNode("X");
        Node<String> yNode = this.nmBfsIgnoreNoAchieve.getNode("Y");

        Node<String> element = this.nmBfsIgnoreNoAchieve.getNode("Element");
        Node<String> namedElement = this.nmBfsIgnoreNoAchieve.getNode("NamedElement");
        Node<String> redefinableElement = this.nmBfsIgnoreNoAchieve.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nmBfsIgnoreNoAchieve.getNode("MultiplicityElement");
        Node<String> feature = this.nmBfsIgnoreNoAchieve.getNode("Feature");
        Node<String> typedElement = this.nmBfsIgnoreNoAchieve.getNode("TypedElement");
        Node<String> structuralFeature = this.nmBfsIgnoreNoAchieve.getNode("StructuralFeature");
        Node<String> connectableElement = this.nmBfsIgnoreNoAchieve.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nmBfsIgnoreNoAchieve.getNode("DeploymentTarget");
        Node<String> property = this.nmBfsIgnoreNoAchieve.getNode("Property");
        Node<String> port = this.nmBfsIgnoreNoAchieve.getNode("Port");

        xNode.addParent(structuralFeature);
        yNode.addParent(deploymentTarget);

        Map<Node<String>, Integer> correctDistancesIgnoringOnlyBegottenFathers = new HashMap<Node<String>, Integer>();
        correctDistancesIgnoringOnlyBegottenFathers.put(property, 2);
        correctDistancesIgnoringOnlyBegottenFathers.put(port, 2);

        Map<Node<String>, Integer> correctDistancesIgnoringNoOne = new HashMap<Node<String>, Integer>();
        correctDistancesIgnoringNoOne.put(property, 2);
        correctDistancesIgnoringNoOne.put(port, 3);

        Set<Node<String>> mappedNodes = new HashSet<Node<String>>();
        mappedNodes.add(property);
        mappedNodes.add(port);

        Map<Node<String>, Integer> distancesIgnoringOnlyBegottenFathers = xNode.getDistancesTo(mappedNodes, 3);
        Map<Node<String>, Integer> distancesIgnoringIgnoringNoOne = xNode.getDistancesTo(mappedNodes, 3, false);

        assertTrue(distancesIgnoringOnlyBegottenFathers.equals(correctDistancesIgnoringOnlyBegottenFathers));
        assertTrue(distancesIgnoringIgnoringNoOne.equals(correctDistancesIgnoringNoOne));
    }

    @Test
    public void testIfTheReferenceNodeIsAlwaysAtSubgraphMaxHeightPaths() {
        Node<String> structuralFeature = this.nmBfsIgnoreNoAchieve.getNode("StructuralFeature");
        Node<String> property = this.nmBfsIgnoreNoAchieve.getNode("Property");

        Node<String> xNode = this.nmBfsIgnoreNoAchieve.getNode("X");
        xNode.addParent(structuralFeature);

        Set<LinkedList<Node<String>>> paths1 = xNode.getSubgraphMaxHeightPaths(3);
        Set<LinkedList<Node<String>>> paths2 = xNode.getSubgraphMaxHeightPaths(2);
        Set<LinkedList<Node<String>>> paths3 = property.getSubgraphMaxHeightPaths(3);
        Set<LinkedList<Node<String>>> paths4 = property.getSubgraphMaxHeightPaths(2);

        for (LinkedList<Node<String>> list : paths1) {
            assertTrue(list.get(0).equals(xNode));
        }

        for (LinkedList<Node<String>> list : paths2) {
            assertTrue(list.get(0).equals(xNode));
        }

        for (LinkedList<Node<String>> list : paths3) {
            assertTrue(list.get(0).equals(property));
        }

        for (LinkedList<Node<String>> list : paths4) {
            assertTrue(list.get(0).equals(property));
        }
    }


}
