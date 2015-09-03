package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class NodeManagerTest {

    private NodeManager<String> nm;

    @Before
    public void setupComplexGraphAtDefaultNodeManager() {
        this.nm = new NodeManager<String>();

        // Creating the nodes:
        Node<String> element = this.nm.getNode("Element");
        Node<String> namedElement = this.nm.getNode("NamedElement");
        Node<String> redefinableElement = this.nm.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nm.getNode("MultiplicityElement");
        Node<String> feature = this.nm.getNode("Feature");
        Node<String> typedElement = this.nm.getNode("TypedElement");
        Node<String> structuralFeature = this.nm.getNode("StructuralFeature");
        Node<String> connectableElement = this.nm.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nm.getNode("DeploymentTarget");
        Node<String> property = this.nm.getNode("Property");
        Node<String> port = this.nm.getNode("Port");

        // Setting attributes:
        element.addAttribute(new NodeAttribute("owner"));
        element.addAttribute(new NodeAttribute("ownedElement"));
        element.addAttribute(new NodeAttribute("ownedComment"));

        namedElement.addAttribute(new NodeAttribute("name"));
        namedElement.addAttribute(new NodeAttribute("visibility"));

        feature.addAttribute(new NodeAttribute("isStatic"));
        property.addAttribute(new NodeAttribute("isDerived"));
        property.addAttribute(new NodeAttribute("qualifier"));

        // Defining relationships:
        element.addChild(multiplicityElement);
        element.addChild(namedElement);

        redefinableElement.addChild(feature);

        namedElement.addChild(redefinableElement);
        namedElement.addChild(typedElement);
        namedElement.addChild(deploymentTarget);

        multiplicityElement.addChild(structuralFeature);
        feature.addChild(structuralFeature);

        typedElement.addChild(structuralFeature);
        typedElement.addChild(connectableElement);

        property.addParent(structuralFeature);
        property.addParent(connectableElement);
        property.addParent(deploymentTarget);

        port.addParent(property);
    }

    @Test
    public void testNodeExistenceAndEquivalence() {
        NodeManager<String> nm = new NodeManager<String>();

        Node<String> element = new Node<String>("Element");

        assertFalse(nm.nodeExists("Element"));
        assertFalse(nm.nodeExists(element));

        assertTrue(nm.getNode("Element").equals(element));
        assertTrue(nm.nodeExists("Element"));
        assertTrue(nm.nodeExists(element));
    }

    @Test
    public void testInheritedAttributesAtComplexGraph() {
        Node<String> property = this.nm.getNode("Property");
        Node<String> typedElement = this.nm.getNode("TypedElement");

        Set<NodeAttribute> allPropertyAttrs = property.getAllAttributes();
        Set<NodeAttribute> allTypedElementAttrs = typedElement.getAllAttributes();

        assertTrue(allPropertyAttrs.contains(new NodeAttribute("owner")));
        assertTrue(allPropertyAttrs.contains(new NodeAttribute("ownedElement")));
        assertTrue(allPropertyAttrs.contains(new NodeAttribute("ownedComment")));
        assertTrue(allPropertyAttrs.contains(new NodeAttribute("isStatic")));
        assertTrue(allPropertyAttrs.contains(new NodeAttribute("isDerived")));

        assertFalse(allPropertyAttrs.contains(new NodeAttribute("invalidAttr")));
        assertFalse(allTypedElementAttrs.contains(new NodeAttribute("isDerived")));
    }

    @Test
    public void testThatWeCantAddTheSameAttributeAgainAtComplexGraph() {
        Node<String> property = this.nm.getNode("Property");
        Node<String> typedElement = this.nm.getNode("TypedElement");

        assertFalse(property.addAttribute(new NodeAttribute("owner")));
        assertFalse(property.addAttribute(new NodeAttribute("ownedElement")));
        assertFalse(property.addAttribute(new NodeAttribute("ownedComment")));
        assertFalse(property.addAttribute(new NodeAttribute("isStatic")));
        assertFalse(property.addAttribute(new NodeAttribute("isDerived")));

        assertFalse(typedElement.addAttribute(new NodeAttribute("owner")));
        assertFalse(typedElement.addAttribute(new NodeAttribute("ownedElement")));
        assertFalse(typedElement.addAttribute(new NodeAttribute("ownedComment")));

        assertTrue(property.addAttribute(new NodeAttribute("newAttr1")));
        assertTrue(typedElement.addAttribute(new NodeAttribute("newAttr2")));

        Node<String> element = this.nm.getNode("Element");
        assertTrue(element.addAttribute(new NodeAttribute("newAttr3")));
        assertFalse(property.addAttribute(new NodeAttribute("newAttr3")));
        assertFalse(typedElement.addAttribute(new NodeAttribute("newAttr3")));
    }

    @Test
    public void testAddAndRemoveAttrsAtComplexGraph() {
        Node<String> element = this.nm.getNode("Element");
        Node<String> property = this.nm.getNode("Property");
        Node<String> typedElement = this.nm.getNode("TypedElement");

        element.removeAttribute(new NodeAttribute("owner"));

        assertFalse(property.getAllAttributes().contains(new NodeAttribute("owner")));
        assertFalse(typedElement.getAllAttributes().contains(new NodeAttribute("owner")));

        element.addAttribute(new NodeAttribute("owner"));

        assertTrue(property.getAllAttributes().contains(new NodeAttribute("owner")));
        assertTrue(typedElement.getAllAttributes().contains(new NodeAttribute("owner")));
    }

    @Test
    public void testIfWeCanGetAllPossiblePathsFromNodeToRoot() {

        Node<String> element = this.nm.getNode("Element");
        Node<String> namedElement = this.nm.getNode("NamedElement");
        Node<String> redefinableElement = this.nm.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nm.getNode("MultiplicityElement");
        Node<String> feature = this.nm.getNode("Feature");
        Node<String> typedElement = this.nm.getNode("TypedElement");
        Node<String> structuralFeature = this.nm.getNode("StructuralFeature");
        Node<String> connectableElement = this.nm.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nm.getNode("DeploymentTarget");
        Node<String> property = this.nm.getNode("Property");

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

        for (LinkedList<Node<String>> currentPath : paths) {
        	assertTrue(correctPaths.contains(currentPath));
		}
        assertTrue(correctPaths.size() == paths.size());
    }

    @Test
    public void testSubgraphPathsWithNoIgnoreOnlyBegottenFathers() {
        Node<String> XNode = this.nm.getNode("X");
        Node<String> YNode = this.nm.getNode("Y");

        Node<String> element = this.nm.getNode("Element");
        Node<String> namedElement = this.nm.getNode("NamedElement");
        Node<String> redefinableElement = this.nm.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nm.getNode("MultiplicityElement");
        Node<String> feature = this.nm.getNode("Feature");
        Node<String> typedElement = this.nm.getNode("TypedElement");
        Node<String> structuralFeature = this.nm.getNode("StructuralFeature");
        Node<String> connectableElement = this.nm.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nm.getNode("DeploymentTarget");
        Node<String> property = this.nm.getNode("Property");
        Node<String> port = this.nm.getNode("Port");

        XNode.addParent(structuralFeature);
        YNode.addParent(deploymentTarget);

        // Creating the test set:
        Set<LinkedList<Node<String>>> correctPaths = new HashSet<LinkedList<Node<String>>>();

        LinkedList<Node<String>> path = new LinkedList<Node<String>>();
        path.add(XNode);
        path.add(structuralFeature);
        path.add(typedElement);
        path.add(namedElement);
        correctPaths.add(path);

        path = new LinkedList<Node<String>>();
        path.add(XNode);
        path.add(structuralFeature);
        path.add(feature);
        path.add(redefinableElement);
        path.add(namedElement);
        path.add(element);
        correctPaths.add(path);

        // Paths lesser than k must be ignored:
        LinkedList<Node<String>> invalidPath = new LinkedList<Node<String>>();
        invalidPath.add(XNode);
        invalidPath.add(structuralFeature);
        invalidPath.add(multiplicityElement);
        invalidPath.add(element);

        // Testing:
        Set<LinkedList<Node<String>>> paths = XNode.getSubgraphMaxHeightPaths(3, false);

        // Checking if paths lesser than k are correctly ignored:
        assertFalse(paths.contains(invalidPath));

        for (LinkedList<Node<String>> currentPath : paths) {
            assertTrue(correctPaths.contains(currentPath));
        }
        assertTrue(correctPaths.size() == paths.size());
    }

    @Test
    public void testSubgraphPathsWithIgnoreOnlyBegottenFathers() {
        Node<String> XNode = this.nm.getNode("X");
        Node<String> YNode = this.nm.getNode("Y");

        Node<String> element = this.nm.getNode("Element");
        Node<String> namedElement = this.nm.getNode("NamedElement");
        Node<String> redefinableElement = this.nm.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nm.getNode("MultiplicityElement");
        Node<String> feature = this.nm.getNode("Feature");
        Node<String> typedElement = this.nm.getNode("TypedElement");
        Node<String> structuralFeature = this.nm.getNode("StructuralFeature");
        Node<String> connectableElement = this.nm.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nm.getNode("DeploymentTarget");
        Node<String> property = this.nm.getNode("Property");
        Node<String> port = this.nm.getNode("Port");

        XNode.addParent(structuralFeature);
        YNode.addParent(deploymentTarget);

        // Creating the test set:
        Set<LinkedList<Node<String>>> correctPaths = new HashSet<LinkedList<Node<String>>>();

        LinkedList<Node<String>> path = new LinkedList<Node<String>>();
        path.add(XNode);
        path.add(structuralFeature);
        path.add(typedElement);
        path.add(namedElement);
        correctPaths.add(path);

        path = new LinkedList<Node<String>>();
        path.add(XNode);
        path.add(structuralFeature);
        path.add(namedElement);
        path.add(element);
        correctPaths.add(path);

        // Paths lesser than k must be ignored:
        LinkedList<Node<String>> invalidPath = new LinkedList<Node<String>>();
        invalidPath.add(XNode);
        invalidPath.add(structuralFeature);
        invalidPath.add(element);

        // Testing:
        Set<LinkedList<Node<String>>> paths = XNode.getSubgraphMaxHeightPaths(3);

        // Checking if paths lesser than k are correctly ignored:
        assertFalse(paths.contains(invalidPath));

        for (LinkedList<Node<String>> currentPath : paths) {
            assertTrue(correctPaths.contains(currentPath));
        }
        assertTrue(correctPaths.size() == paths.size());
    }



    @Test
    public void testDistancesBetweenNodes() {
        Node<String> XNode = this.nm.getNode("X");
        Node<String> YNode = this.nm.getNode("Y");

        Node<String> element = this.nm.getNode("Element");
        Node<String> namedElement = this.nm.getNode("NamedElement");
        Node<String> redefinableElement = this.nm.getNode("RedefinableElement");
        Node<String> multiplicityElement = this.nm.getNode("MultiplicityElement");
        Node<String> feature = this.nm.getNode("Feature");
        Node<String> typedElement = this.nm.getNode("TypedElement");
        Node<String> structuralFeature = this.nm.getNode("StructuralFeature");
        Node<String> connectableElement = this.nm.getNode("ConnectableElement");
        Node<String> deploymentTarget = this.nm.getNode("DeploymentTarget");
        Node<String> property = this.nm.getNode("Property");
        Node<String> port = this.nm.getNode("Port");

        XNode.addParent(structuralFeature);
        YNode.addParent(deploymentTarget);

        Map<Node<String>, Integer> correctDistances = new HashMap<Node<String>, Integer>();
        correctDistances.put(property, 4);
        correctDistances.put(port, 4);

        Set<Node<String>> mappedNodes = new HashSet<Node<String>>();
        mappedNodes.add(property);
        mappedNodes.add(port);

        Map<Node<String>, Integer> distances = XNode.getDistancesTo(mappedNodes, 3);

        System.out.println(distances);
        System.out.println(correctDistances);

        assertTrue(distances.equals(correctDistances));
    }

    /**
     * TODO: Complete test
     */
    @Test
    public void testSinglePathSubgraph() {
        this.nm = new NodeManager<String>();

        // Creating the nodes:
        Node<String> element = this.nm.getNode("Element");
        Node<String> multiplicityElement = this.nm.getNode("MultiplicityElement");
        Node<String> structuralFeature = this.nm.getNode("StructuralFeature");
        Node<String> property = this.nm.getNode("Property");

        Node<String> XNode = this.nm.getNode("X");
        Node<String> ZNode = this.nm.getNode("Z");
        Node<String> RootNode = this.nm.getNode("Root");
        Node<String> TNode = this.nm.getNode("T");

        XNode.addParent(structuralFeature);
        ZNode.addParent(multiplicityElement);
        TNode.addParent(RootNode);
        element.addParent(RootNode);
        property.addParent(structuralFeature);
        structuralFeature.addParent(multiplicityElement);
        multiplicityElement.addParent(element);

        // Should return "Root", not "Element" at list:
        Set<LinkedList<Node<String>>> paths = property.getSubgraphMaxHeightPaths(3);

        //System.out.println(paths);
    }

}
