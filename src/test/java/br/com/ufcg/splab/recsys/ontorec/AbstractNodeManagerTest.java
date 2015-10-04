/*
 * OntoRec, Ontology Based Recommender Systems Algorithm
 *
 * License: GNU Lesser General Public License (LGPL), version 3.
 * See the LICENSE file in the root directory or <http://www.gnu.org/licenses/lgpl.html>.
 */
package br.com.ufcg.splab.recsys.ontorec;

public abstract class AbstractNodeManagerTest {

    protected void buildComplexGraphAt(NodeManager<String> nm) {

        // Creating the nodes:
        Node<String> element = nm.getNode("Element");
        Node<String> namedElement = nm.getNode("NamedElement");
        Node<String> redefinableElement = nm.getNode("RedefinableElement");
        Node<String> multiplicityElement = nm.getNode("MultiplicityElement");
        Node<String> feature = nm.getNode("Feature");
        Node<String> typedElement = nm.getNode("TypedElement");
        Node<String> structuralFeature = nm.getNode("StructuralFeature");
        Node<String> connectableElement = nm.getNode("ConnectableElement");
        Node<String> deploymentTarget = nm.getNode("DeploymentTarget");
        Node<String> property = nm.getNode("Property");
        Node<String> port = nm.getNode("Port");

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

}
