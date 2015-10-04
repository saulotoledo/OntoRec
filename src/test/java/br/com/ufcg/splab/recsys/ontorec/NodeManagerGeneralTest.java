/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import br.com.ufcg.splab.recsys.ontorec.weighting.BFSPathNodeWeightingApproach;

public class NodeManagerGeneralTest extends AbstractNodeManagerTest {

    private NodeManager<String> nm;

    @Before
    public void setupComplexGraph() {
        this.nm = new NodeManager<String>(
                new BFSPathNodeWeightingApproach<String>(), false, false);

        this.buildComplexGraphAt(this.nm);
    }

    @Test
    public void testNodeExistenceAndEquivalence() {
        NodeManager<String> nm = new NodeManager<String>(
                new BFSPathNodeWeightingApproach<String>(), false, false);

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
    public void testIfFirstElementAtPathsIsTheOriginNode() {
        Node<String> xNode = this.nm.getNode("X");
        Node<String> property = this.nm.getNode("Property");

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
