package test;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xml.internal.dtm.ref.DTMNodeList;

public class XPathTest2 {

	public static void main(String[] args) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			
			Document doc = parser.newDocument();
			Element hello = doc.createElement("hello");
			Element world = doc.createElement("world");
			Element planet = doc.createElement("planet");
			hello.appendChild(world);
			hello.appendChild(planet);
			world.setAttribute("message", "Hello World!");
			planet.setAttribute("message", "Hello World!");
			doc.appendChild(hello);
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("/hello/*[@message='Hello World!']");
			Object o = expr.evaluate(doc, XPathConstants.NODESET);
			DTMNodeList list = (DTMNodeList) o;
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				System.out.println(node.getNodeName() + " - " + planet.getAttribute("message"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
