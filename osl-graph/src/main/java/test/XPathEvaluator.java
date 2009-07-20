package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xpath.internal.NodeSet;

public class XPathEvaluator {

	public void evaluateDocument(File xmlDocument) {

		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			

			
			
			InputSource inputSource = new InputSource(new FileInputStream(
					xmlDocument));
			XPathExpression

			xPathExpression = xPath
					.compile("/catalog/journal/article[@date='January-2004']/title");
			String title = xPathExpression.evaluate(inputSource);
			System.out.println("Title: " + title);

			inputSource = new InputSource(new FileInputStream(xmlDocument));
			String publisher = xPath.evaluate("/catalog/journal/@publisher",
					inputSource);
			System.out.println("Publisher:" + publisher);

			String expression = "/catalog/journal/article";
			NodeSet nodes = (NodeSet) xPath.evaluate(expression, inputSource,
					XPathConstants.NODESET);
			NodeList nodeList = (NodeList) nodes;
		} catch (IOException e) {
		} catch (XPathExpressionException e) {
		}

	}

	public static void main(String[] argv) {

		XPathEvaluator evaluator = new XPathEvaluator();

		File xmlDocument = new File(
				"/Users/vitorchagas/local/projects/workspace/openspotlight/spotlight-graph/data/catalog.xml");
		evaluator.evaluateDocument(xmlDocument);

	}

}
