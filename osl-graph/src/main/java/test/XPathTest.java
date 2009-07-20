package test;

import java.util.Iterator;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

public class XPathTest {

	public static void main(String[] args) {

		try {

			Element hello = new Element("hello");
			Element world = new Element("world");
			Element planet = new Element("planet");
			hello.addContent(world);
			hello.addContent(planet);
			
			world.setAttribute("message", "Hello!");
			planet.setAttribute("message", "Hello!");
			Document doc = new Document();
			doc.addContent(hello);
			
			Element element = (org.jdom.Element) (XPath.selectSingleNode(doc, "/hello/world[@message='Hello!']"));
			System.out.println(element.getAttributeValue("message"));
			
	        java.util.List nodeList = XPath.selectNodes(doc, "/hello/*[@message='Hello!']");
            Iterator iter=nodeList.iterator();

             while(iter.hasNext()){
                 Element current = (org.jdom.Element) iter.next();
                 System.out.println(current.getAttributeValue("message"));
             }
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
