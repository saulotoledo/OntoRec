package br.com.ufcg.splab.recsys.ontorec;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.ufcg.splab.recsys.ontorec.weighting.NodeWeightingApproach;

public class OWLReader
{
    /**
     * The application logger.
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(OWLReader.class);

    private Map<String, Node<String>> nodeMap = new HashMap<String, Node<String>>();
    private NodeManager<String> nm;

    public OWLReader(String filepath,
            NodeWeightingApproach<String> nodeWeightingApproach,
            Boolean lambda, Boolean upsilon)
    {

        this.nm = new NodeManager<String>(nodeWeightingApproach,
                lambda, upsilon);
        try {

            File fXmlFile = new File(filepath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            // optional, but recommended
            // read this -
            // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("owl:Class");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                org.w3c.dom.Node nNode = nList.item(temp);

                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String nodeName = eElement.getAttribute("rdf:about").split(
                            "#")[1];
                    Node<String> currentNode = this.nm.getNode(nodeName);
                    this.nodeMap.put(nodeName, currentNode);

                    NodeList subClassOfList = eElement
                            .getElementsByTagName("rdfs:subClassOf");
                    if (subClassOfList.getLength() > 0) {

                        for (int i = 0; i < subClassOfList.getLength(); i++) {
                            Element parentElement = (Element) (subClassOfList
                                    .item(i));
                            String parentElementName = parentElement
                                    .getAttribute("rdf:resource").split("#")[1];
                            Node<String> parentNode = this.nm
                                    .getNode(parentElementName);

                            this.nodeMap.put(parentElementName, parentNode);
                            currentNode.addParent(parentNode);
                        }
                    }
                }
            }

            List<String> attributesToRemoveAndToAdd = new ArrayList<String>();
            List<String> attributeNodes = new ArrayList<String>();

            nList = doc.getElementsByTagName("owl:ObjectProperty");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                org.w3c.dom.Node nNode = nList.item(temp);

                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String attrName = eElement.getAttribute("rdf:about").split(
                            "#")[1];

                    NodeList domains = eElement
                            .getElementsByTagName("rdfs:domain");
                    if (domains.getLength() > 0) {

                        for (int i = 0; i < domains.getLength(); i++) {
                            Element domain = (Element) (domains.item(i));
                            String domainElementName = domain.getAttribute(
                                    "rdf:resource").split("#")[1];

                            // NodeName -> AttributeName
                            attributeNodes.add(domainElementName + " -> "
                                    + attrName);
                            Node<String> domainNode = this.nm
                                    .getNode(domainElementName);

                            domainNode
                                    .addAttribute(new NodeAttribute(attrName));
                        }
                    }
                }
            }

            nList = doc.getElementsByTagName("owl:DatatypeProperty");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                org.w3c.dom.Node nNode = nList.item(temp);

                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String attrName = eElement.getAttribute("rdf:about").split(
                            "#")[1];

                    NodeList domains = eElement
                            .getElementsByTagName("rdfs:domain");
                    if (domains.getLength() > 0) {

                        for (int i = 0; i < domains.getLength(); i++) {
                            Element domain = (Element) (domains.item(i));
                            String domainElementName = domain.getAttribute(
                                    "rdf:resource").split("#")[1];

                            // NodeName -> AttributeName
                            attributeNodes.add(domainElementName + " -> "
                                    + attrName);
                            Node<String> domainNode = this.nm
                                    .getNode(domainElementName);

                            domainNode
                                    .addAttribute(new NodeAttribute(attrName));
                        }
                    }

                    NodeList enumValues = eElement
                            .getElementsByTagName("rdfs:subPropertyOf");
                    if (enumValues.getLength() > 0) {

                        for (int i = 0; i < enumValues.getLength(); i++) {
                            Element enumValue = (Element) (enumValues.item(i));
                            String enumValueName = enumValue.getAttribute(
                                    "rdf:resource").split("#")[1];

                            // attrName (that should be removed) ->
                            // AttributeName (that should be added)
                            attributesToRemoveAndToAdd.add(attrName + " -> "
                                    + enumValueName);

                            Node<String> domainNode = this.nm
                                    .getNode(enumValueName);

                            domainNode
                                    .addAttribute(new NodeAttribute(attrName));
                        }
                    }

                }
            }
            for (String attrInfo : attributesToRemoveAndToAdd) {
                String attrThatShouldBeRemoved = attrInfo.split(" -> ")[1];
                String attrThatShouldBeAdded = attrInfo.split(" -> ")[0];

                Node<String> node = null;
                for (String attrNodesInfo : attributeNodes) {
                    String nodeName = attrNodesInfo.split(" -> ")[0];
                    String attrAtNode = attrNodesInfo.split(" -> ")[1];

                    if (attrAtNode.equals(attrThatShouldBeRemoved)) {
                        node = this.nm.getNode(nodeName);
                        node.addAttribute(new NodeAttribute(
                                attrThatShouldBeAdded));
                    }
                }

                if (node != null) {
                    node.removeAttribute(new NodeAttribute(
                            attrThatShouldBeRemoved));
                }

            }

        } catch (Exception e) {
            LOGGER.error("An exception has occurred.", e);
        }
    }

    public NodeManager<String> getNodeManager()
    {
        return this.nm;
    }
}
