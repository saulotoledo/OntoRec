package br.com.ufcg.splab.umlrec.approach.knowledge.graph;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NodeTest {

    @Test
    public void testIfWeCanAddAParentNode() {
        Node<String> parentNode = new Node<String>("parent");
        Node<String> childNode = new Node<String>("child");

        assertTrue(childNode.addParent(parentNode));

        assertTrue(childNode.isChildOf(parentNode));
        assertTrue(parentNode.isParentOf(childNode));
    }

    @Test
    public void testIfWeCanAddTheSameParentAgain() {
        Node<String> parentNode = new Node<String>("parent");
        Node<String> childNode = new Node<String>("child");

        childNode.addParent(parentNode);
        assertFalse(childNode.addParent(parentNode));
    }

    @Test
    public void testIfWeCanAddParentsWithTheSameName() {
        Node<String> parentNode1 = new Node<String>("parent");
        Node<String> parentNode2 = new Node<String>("parent");
        Node<String> childNode = new Node<String>("child");

        childNode.addParent(parentNode1);
        assertFalse(childNode.addParent(parentNode2));
    }

    @Test
    public void testIfWeCanRemoveParents() {
        Node<String> parentNode = new Node<String>("parent");
        Node<String> childNode = new Node<String>("child");

        childNode.addParent(parentNode);

        assertTrue(childNode.removeParent(parentNode));
        assertTrue(childNode.getParents().size() == 0);
    }


    @Test
    public void testIfWeCanAddAChildNode() {
        Node<String> parentNode = new Node<String>("parent");
        Node<String> childNode = new Node<String>("child");

        assertTrue(parentNode.addChild(childNode));

        assertTrue(childNode.isChildOf(parentNode));
        assertTrue(parentNode.isParentOf(childNode));
    }

    @Test
    public void testIfWeCanAddTheSameChildAgain() {
        Node<String> parentNode = new Node<String>("parent");
        Node<String> childNode = new Node<String>("child");

        parentNode.addChild(childNode);
        assertFalse(parentNode.addChild(childNode));
    }

    @Test
    public void testIfWeCanAddChildsWithTheSameName() {
        Node<String> parentNode = new Node<String>("parent");
        Node<String> childNode1 = new Node<String>("child");
        Node<String> childNode2 = new Node<String>("child");

        parentNode.addChild(childNode1);
        assertFalse(parentNode.addChild(childNode2));
    }

    @Test
    public void testIfWeCanRemoveChildren() {
        Node<String> parentNode = new Node<String>("parent");
        Node<String> childNode = new Node<String>("child");

        parentNode.addParent(childNode);

        assertTrue(parentNode.removeParent(childNode));
        assertTrue(parentNode.getChildren().size() == 0);
    }


    @Test
    public void testIfAddParentDoesNotAffectChildrenListAndViceVersa() {
        Node<String> parentNode1 = new Node<String>("Element");
        Node<String> childNode1  = new Node<String>("NamedElement");
        parentNode1.addChild(childNode1);
        assertTrue(parentNode1.getParents().size() == 0);

        Node<String> parentNode2 = new Node<String>("parent2");
        Node<String> childNode2 = new Node<String>("child2");
        parentNode2.addParent(childNode2);
        assertTrue(parentNode2.getChildren().size() == 0);
    }

    @Test
    public void testIfICanGetAllInheritedAttributes() {
        Node<String> grandfather = new Node<String>("grandfather");
        Node<String> father = new Node<String>("father");
        Node<String> child  = new Node<String>("child");

        child.addParent(father);
        father.addParent(grandfather);

        NodeAttribute attr1 = new NodeAttribute("isStatic");
        NodeAttribute attr2 = new NodeAttribute("isDerived");
        NodeAttribute attr3 = new NodeAttribute("invalidAttr");

        grandfather.addAttribute(attr1);
        grandfather.addAttribute(attr2);

        assertTrue(child.getAllAttributes().contains(attr1));
        assertTrue(child.getAllAttributes().contains(attr2));
        assertFalse(child.getAllAttributes().contains(attr3));
    }

    @Test
    public void testIfTwoNodeAttributeObjetsAreEqualByName() {
        NodeAttribute attr1 = new NodeAttribute("isStatic");
        NodeAttribute attr2 = new NodeAttribute("isStatic");
        NodeAttribute attr3 = new NodeAttribute("isDerived");

        assertTrue(attr1.equals(attr2));
        assertFalse(attr1.equals(attr3));
    }

    @Test
    public void testThatWeCantAddTheSameAttributeAgain() {
        Node<String> grandfather = new Node<String>("grandfather");
        Node<String> father = new Node<String>("father");
        Node<String> child  = new Node<String>("child");

        child.addParent(father);
        father.addParent(grandfather);

        NodeAttribute attr1 = new NodeAttribute("isStatic");
        NodeAttribute attr2 = new NodeAttribute("isStatic");

        grandfather.addAttribute(attr1);
        assertFalse(father.addAttribute(attr1));
        assertFalse(father.addAttribute(attr2));
        assertFalse(child.addAttribute(attr1));
        assertFalse(child.addAttribute(attr2));

        NodeAttribute attr3 = new NodeAttribute("isDerived");
        assertTrue(child.addAttribute(attr3));
        assertFalse(father.addAttribute(attr3));
        assertFalse(grandfather.addAttribute(attr3));
    }

}
