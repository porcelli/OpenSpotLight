package org.openspotlight.graph;

import static org.openspotlight.graph.SLLink.DIRECTION_ANY;
import static org.openspotlight.graph.SLLink.DIRECTION_BI;
import static org.openspotlight.graph.SLLink.DIRECTION_UNI;
import static org.openspotlight.graph.SLLink.DIRECTION_UNI_REVERSAL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.openspotlight.SLException;
import org.openspotlight.graph.util.AbstractFactory;
import org.openspotlight.graph.util.AbstractFactoryException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Test
public class SLGraphTest {
	
	static final Logger LOGGER = Logger.getLogger(SLGraphTest.class);
	
	private SLGraph graph;
	private SLGraphSession session;
	
	private JavaClassNode javaClassNode;
	private JavaMethodNode javaMethodNode;
	
	private SLLink linkAB;
	private SLLink linkBA;
	private SLLink linkBoth;
	
	@BeforeClass
	public void init() throws AbstractFactoryException {
		SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
		graph = factory.createGraph();
	}
	
	@AfterClass
	public void finish() {
		graph.shutdown();
	}
	
	@BeforeMethod
	public void beforeTest() throws SLGraphException {
		if (session == null) session = graph.openSession();
	}
	
	@AfterMethod
	public void afterTest() throws SLGraphSessionException {
		session.clear();
		
		//session.save();
		//session.close();
	}

	@Test
	public void testContextOperations() throws SLGraphSessionException {
		SLContext context1 = session.createContext(1L);
		Assert.assertNotNull(context1, "context1 should not be null.");
		SLContext context2 = session.getContext(1L);
		Assert.assertNotNull(context2, "context2 should not be null.");
		Long id1 = context1.getID();
		Long id2 = context2.getID();
		Assert.assertNotNull(id1);
		Assert.assertNotNull(id2);
		Assert.assertEquals(id1, id2);
		Assert.assertEquals(context1, context2);
	}
	
	@Test
	public void testIntegerProperty() {
		
		try {
			
			// set new property ...
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<Integer> prop1 = root.setProperty(Integer.class, "prop", 8);
			Assert.assertNotNull(prop1);
			Assert.assertNotNull(prop1.getValue());
			Assert.assertEquals(prop1.getValue().intValue(), 8);
			
			// get existent property ...
			SLNodeProperty<Integer> prop2 = root.getProperty(Integer.class, "prop");
			Assert.assertNotNull(prop2);
			Assert.assertNotNull(prop2.getValue());
			Assert.assertEquals(prop2.getValue().intValue(), 8);
			
			// get property as Long ...
			SLNodeProperty<Long> prop3 = root.getProperty(Long.class, "prop");
			Assert.assertNotNull(prop3);
			Assert.assertNotNull(prop3.getValue());
			Assert.assertEquals(prop3.getValue(), new Long(8));
			
			// get property as Number ...
			SLNodeProperty<Number> prop4 = root.getProperty(Number.class, "prop");
			Assert.assertNotNull(prop4);
			Assert.assertNotNull(prop4.getValue());
			Assert.assertEquals(prop4.getValue().intValue(), 8);
			
			// get property as Serializable ...
			SLNodeProperty<Serializable> prop5 = root.getProperty(Serializable.class, "prop");
			Assert.assertNotNull(prop5);
			Assert.assertNotNull(prop5.getValue());
			Assert.assertEquals(prop5.getValue(), new Long(8));
			
			// try to integer property as non-hierarchy class ...
			try {
				root.getProperty(String.class, "prop");
				Assert.fail();
			}
			catch (SLInvalidNodePropertyTypeException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testLongProperty() {
		
		try {
			// set new property ...
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<Long> prop1 = root.setProperty(Long.class, "prop", 8L);
			
			Assert.assertNotNull(prop1);
			Assert.assertNotNull(prop1.getValue());
			Assert.assertEquals(prop1.getValue(), new Long(8L));
			
			// get existent property ...
			SLNodeProperty<Long> prop2 = root.getProperty(Long.class, "prop");
			Assert.assertNotNull(prop2);
			Assert.assertNotNull(prop2.getValue());
			Assert.assertEquals(prop2.getValue(), new Long(8L));
			
			// get property as Integer ...
			SLNodeProperty<Integer> prop3 = root.getProperty(Integer.class, "prop");
			Assert.assertNotNull(prop3);
			Assert.assertNotNull(prop3.getValue());
			Assert.assertEquals(prop3.getValue(), new Integer(8));
			
			// get property as Number ...
			SLNodeProperty<Number> prop4 = root.getProperty(Number.class, "prop");
			Assert.assertNotNull(prop4);
			Assert.assertNotNull(prop4.getValue());
			Assert.assertEquals(prop4.getValue(), new Long(8L));
			
			// get property as Serializable ...
			SLNodeProperty<Serializable> prop5 = root.getProperty(Serializable.class, "prop");
			Assert.assertNotNull(prop5);
			Assert.assertNotNull(prop5.getValue());
			Assert.assertEquals(prop5.getValue(), new Long(8));
			
			// try to integer property as non-hierarchy class ...
			try {
				root.getProperty(String.class, "prop");
				Assert.fail();
			}
			catch (SLInvalidNodePropertyTypeException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testFloatProperty() {

		try {
			// set new property ...
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<Float> prop1 = root.setProperty(Float.class, "prop", 8.0F);
			Assert.assertNotNull(prop1);
			Assert.assertNotNull(prop1.getValue());
			Assert.assertEquals(prop1.getValue(), new Float(8.0F));
			
			// get existent property ...
			SLNodeProperty<Float> prop2 = root.getProperty(Float.class, "prop");
			Assert.assertNotNull(prop2);
			Assert.assertNotNull(prop2.getValue());
			Assert.assertEquals(prop2.getValue(), new Float(8.0F));
			
			// get property as Double ...
			SLNodeProperty<Double> prop3 = root.getProperty(Double.class, "prop");
			Assert.assertNotNull(prop3);
			Assert.assertNotNull(prop3.getValue());
			Assert.assertEquals(prop3.getValue(), new Double(8));
			
			// get property as Number ...
			SLNodeProperty<Number> prop4 = root.getProperty(Number.class, "prop");
			Assert.assertNotNull(prop4);
			Assert.assertNotNull(prop4.getValue());
			Assert.assertEquals(prop4.getValue(), new Double(8.0));
			
			// get property as Serializable ...
			SLNodeProperty<Serializable> prop5 = root.getProperty(Serializable.class, "prop");
			Assert.assertNotNull(prop5);
			Assert.assertNotNull(prop5.getValue());
			Assert.assertEquals(prop5.getValue(), new Double(8));
			
			// try to integer property as non-hierarchy class ...
			try {
				root.getProperty(Integer.class, "prop");
				Assert.fail();
			}
			catch (SLInvalidNodePropertyTypeException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testDoubleProperty() {

		try {
			// set new property ...
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<Double> prop1 = root.setProperty(Double.class, "prop", 8.0);
			Assert.assertNotNull(prop1);
			Assert.assertNotNull(prop1.getValue());
			Assert.assertEquals(prop1.getValue(), new Double(8L));
			
			// get existent property ...
			SLNodeProperty<Double> prop2 = root.getProperty(Double.class, "prop");
			Assert.assertNotNull(prop2);
			Assert.assertNotNull(prop2.getValue());
			Assert.assertEquals(prop2.getValue(), new Double(8.0));
			
			// get property as Float ...
			SLNodeProperty<Float> prop3 = root.getProperty(Float.class, "prop");
			Assert.assertNotNull(prop3);
			Assert.assertNotNull(prop3.getValue());
			Assert.assertEquals(prop3.getValue(), new Float(8));
			
			// get property as Number ...
			SLNodeProperty<Number> prop4 = root.getProperty(Number.class, "prop");
			Assert.assertNotNull(prop4);
			Assert.assertNotNull(prop4.getValue());
			Assert.assertEquals(prop4.getValue(), new Double(8.0));
			
			// get property as Serializable ...
			SLNodeProperty<Serializable> prop5 = root.getProperty(Serializable.class, "prop");
			Assert.assertNotNull(prop5);
			Assert.assertNotNull(prop5.getValue());
			Assert.assertEquals(prop5.getValue(), new Double(8));
			
			// try to integer property as non-hierarchy class ...
			try {
				root.getProperty(Long.class, "prop");
				Assert.fail();
			}
			catch (SLInvalidNodePropertyTypeException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testBooleanProperty() {

		try {
			// set new property ...
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<Boolean> prop1 = root.setProperty(Boolean.class, "prop", true);
			Assert.assertNotNull(prop1);
			Assert.assertNotNull(prop1.getValue());
			Assert.assertEquals(prop1.getValue(), new Boolean(true));
			
			// get existent property ...
			SLNodeProperty<Boolean> prop2 = root.getProperty(Boolean.class, "prop");
			Assert.assertNotNull(prop2);
			Assert.assertNotNull(prop2.getValue());
			Assert.assertEquals(prop2.getValue(), new Boolean(true));
			
			// get property as Serializable ...
			SLNodeProperty<Serializable> prop5 = root.getProperty(Serializable.class, "prop");
			Assert.assertNotNull(prop5);
			Assert.assertNotNull(prop5.getValue());
			Assert.assertEquals(prop5.getValue(), new Boolean(true));
			
			// try to integer property as non-hierarchy class ...
			try {
				root.getProperty(String.class, "prop");
				Assert.fail();
			}
			catch (SLInvalidNodePropertyTypeException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testStringProperty() {
		
		try {
			// set new property ...
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<String> prop1 = root.setProperty(String.class, "prop", "Hello");
			Assert.assertNotNull(prop1);
			Assert.assertNotNull(prop1.getValue());
			Assert.assertEquals(prop1.getValue(), new String("Hello"));
			
			// get existent property ...
			SLNodeProperty<String> prop2 = root.getProperty(String.class, "prop");
			Assert.assertNotNull(prop2);
			Assert.assertNotNull(prop2.getValue());
			Assert.assertEquals(prop2.getValue(), new String("Hello"));
			
			// get property as Serializable ...
			SLNodeProperty<Serializable> prop5 = root.getProperty(Serializable.class, "prop");
			Assert.assertNotNull(prop5);
			Assert.assertNotNull(prop5.getValue());
			Assert.assertEquals(prop5.getValue(), new String("Hello"));
			
			// try to integer property as non-hierarchy class ...
			try {
				root.getProperty(Integer.class, "prop");
				Assert.fail();
			}
			catch (SLInvalidNodePropertyTypeException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testAnySerializableProperty() {

		try {
			Date now = new Date();
			
			// set new property ...
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<Date> prop1 = root.setProperty(Date.class, "prop", now);
			Assert.assertNotNull(prop1);
			Assert.assertNotNull(prop1.getValue());
			Assert.assertEquals(prop1.getValue(), now);
			
			// get existent property ...
			SLNodeProperty<Date> prop2 = root.getProperty(Date.class, "prop");
			Assert.assertNotNull(prop2);
			Assert.assertNotNull(prop2.getValue());
			Assert.assertEquals(prop2.getValue(), now);
			
			// get property as Serializable ...
			SLNodeProperty<Serializable> prop5 = root.getProperty(Serializable.class, "prop");
			Assert.assertNotNull(prop5);
			Assert.assertNotNull(prop5.getValue());
			Assert.assertEquals(prop5.getValue(), now);
			
			// try to integer property as non-hierarchy class ...
			try {
				root.getProperty(Integer.class, "prop");
				Assert.fail();
			}
			catch (SLInvalidNodePropertyTypeException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetPropertyAsString() {
		try {
			SLNode root = session.createContext(1L).getRootNode();
			SLNode node = root.addNode("node");
			SLNodeProperty<Integer> property = node.setProperty(Integer.class, "number", new Integer(8));
			String value = node.getPropertyValueAsString("number");
			Assert.assertNotNull(value);
			Assert.assertEquals(value, "8");
			value = property.getValueAsString();
			Assert.assertNotNull(value);
			Assert.assertEquals(value, "8");
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testPropertyValueOverwriting() {
		try {
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<Integer> prop1 = root.setProperty(Integer.class, "prop", 8);
			SLNodeProperty<Integer> prop2 = root.getProperty(Integer.class, "prop");
			prop2.setValue(71);
			Assert.assertEquals(prop2.getValue(), new Integer(71));
			Assert.assertEquals(prop1.getValue(), prop2.getValue());
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testPropertyRemoval() {
		try {
			SLNode root = session.createContext(1L).getRootNode();
			SLNodeProperty<Integer> prop1 = root.setProperty(Integer.class, "property", 8);
			prop1.remove();
			try {
				root.getProperty(Integer.class, "property");
				Assert.fail();
			}
			catch (SLNodePropertyNotFoundException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testPropertiesRetrieval() {
		try {
			SLNode root = session.createContext(1L).getRootNode();
			root.setProperty(Integer.class, "integerProp", 8);
			root.setProperty(String.class, "stringProp", "Hello World!");
			Set<SLNodeProperty<Serializable>> properties = root.getProperties();
			for (SLNodeProperty<Serializable> property : properties) {
				if (property.getName().equals("integerProp")) {
					Assert.assertEquals(property.getValue(), new Long(8));
				}
				else if (property.getName().equals("stringProp")) {
					Assert.assertEquals(property.getValue(), "Hello World!");
				}
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testNodeOperations() {
		try {
			// add new node ...
			SLNode root = session.createContext(1L).getRootNode();
			SLNode node1 = root.addNode("node");
			Assert.assertNotNull(node1);
			Assert.assertEquals(node1.getName(), "node");
			
			// get node ...
			SLNode node2 = root.getNode("node");
			Assert.assertNotNull(node2);
			Assert.assertEquals(node2.getName(), "node");
			Assert.assertEquals(node1, node2);
			
			// set property on node1 ...
			SLNodeProperty<Integer> prop1 = node1.setProperty(Integer.class, "prop", 8);
			Assert.assertNotNull(prop1);
			Assert.assertEquals(prop1.getValue(), new Integer(8));
			
			// get property on node2 ...
			SLNodeProperty<Integer> prop2 = node2.getProperty(Integer.class, "prop");
			Assert.assertNotNull(prop2);
			Assert.assertEquals(prop2.getValue(), new Integer(8));
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testAddNodeTypeHierarchyCase() {
		
		try {
		
			SLNode root = session.createContext(1L).getRootNode();
		
			// add sub type, then add super type; sub type is supposed to be kept ...
			JavaClassNode javaClassNode1 = root.addNode(JavaClassNode.class, "node1");
			JavaElementNode javaElementNode1 = root.addNode(JavaElementNode.class, "node1");
			Assert.assertEquals(javaClassNode1, javaElementNode1);
			Assert.assertTrue(javaElementNode1 instanceof JavaClassNode);
			
			// add super type, then add sub type; sub type is supposed to overwrite ...
			JavaElementNode javaElementNode2 = root.addNode(JavaElementNode.class, "node2");
			JavaClassNode javaClassNode2 = root.addNode(JavaClassNode.class, "node2");
			Assert.assertEquals(javaClassNode2, javaElementNode2);
			
			// add two types of different hierarchies; SLNodeTypeNotInExistentHierarchy is expected ...
			try {
				root.addNode(JavaElementNode.class, "node3");
				root.addNode(CobolElementNode.class, "node3");
				Assert.fail();
			}
			catch (SLNodeTypeNotInExistentHierarchy e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test 
	public void testTypedNodeOperations() {
		try {
			
			// add new node ...
			SLNode root = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root.addNode(JavaClassNode.class, "javaClassNode");
			Assert.assertNotNull(javaClassNode1);
			Assert.assertEquals(javaClassNode1.getName(), "javaClassNode");
			
			// get node ...
			JavaClassNode javaClassNode2 = root.getNode(JavaClassNode.class, "javaClassNode");
			Assert.assertNotNull(javaClassNode2);
			Assert.assertEquals(javaClassNode2.getName(), "javaClassNode");
			
			// set and get custom properties ...
			javaClassNode2.setClassName("HelloWorld");
			Assert.assertEquals(javaClassNode2.getClassName(), "HelloWorld");
			javaClassNode2.setModifier(JavaClassNode.MODIFIER_PUBLIC);
			Assert.assertEquals(javaClassNode2.getModifier(), JavaClassNode.MODIFIER_PUBLIC);
			Date creationTime = new Date();
			javaClassNode2.setCreationTime(creationTime);
			Assert.assertEquals(javaClassNode2.getCreationTime(), creationTime);
			
			// get node as default type ...
			SLNode node  = root.getNode("javaClassNode");
			Assert.assertEquals(node, javaClassNode1);
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testChiLdNodesRetrieval() {
		try {
			SLNode root = session.createContext(1L).getRootNode();
			SLNode node1 = root.addNode("node1");
			SLNode node2 = root.addNode("node2");
			Set<SLNode> ch1LdNodes = root.getNodes();
			Assert.assertNotNull(ch1LdNodes);
			Iterator<SLNode> iter = ch1LdNodes.iterator();
			while (iter.hasNext()) {
				SLNode current = iter.next();
				Assert.assertTrue(current.getName().equals("node1") || current.getName().equals("node2"));
				Assert.assertTrue(current.equals(node1) || current.equals(node2));
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testInvalidNodeTypeRetrieval() {
		try {
			SLNode root = session.createContext(1L).getRootNode();
			root.addNode(JavaClassNode.class, "javaClassNode");
			try {
				root.getNode(JavaMethodNode.class, "javaClassNode");	
				Assert.fail();
			}
			catch (SLInvalidNodeTypeException e) {
				Assert.assertTrue(true);
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_CHANGE_TO_BIDIRECTIONAL NOT SET
	 * empty         --> add AB   --> add AB
	 * empty         --> add BA   --> add BA
	 * empty         --> add BOTH --> add BOTH
	 */
	@Test
	public void testAddSimpleLinkEmptyCase() {
		
		try {
			
			// empty         --> add AB   --> add AB
			setUpEmptyLinkScenario();
			SLLink linkAB = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			
			// empty         --> add BA   --> add BA
			setUpEmptyLinkScenario();
			SLLink linkBA = session.addLink(JavaClassJavaMethodSimpleLink.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLink.class, javaMethodNode, javaClassNode, false);
			
			// empty         --> add BOTH --> add BOTH
			setUpEmptyLinkScenario();
			SLLink linkBoth = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, true);
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_CHANGE_TO_BIDIRECTIONAL NOT SET
	 * existent AB   --> add AB   --> remains AB
	 * existent AB   --> add BA   --> add BA
	 * existent AB   --> add BOTH --> add BOTH
	 */
	@Test
	public void testAddSimpleLinkExistentABCase() {
		
		try {

			// existent AB   --> add AB   --> remains AB
			setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			Assert.assertEquals(linkAB, this.linkAB);
			
			// existent AB   --> add BA   --> add BA
			setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodSimpleLink.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLink.class, javaMethodNode, javaClassNode, false);
			Assert.assertNotSame(linkBA, this.linkAB);
			
			// existent AB   --> add BOTH --> add BOTH
			setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, true);
			Assert.assertNotSame(linkBoth, this.linkAB);
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_CHANGE_TO_BIDIRECTIONAL NOT SET
	 * existent BA   --> add AB   --> add AB
	 * existent BA   --> add BA   --> remains BA
	 * existent BA   --> add BOTH --> add BOTH
	 */
	@Test
	public void testAddSimpleLinkExistentBACase() {
		
		try {

			// existent BA   --> add AB   --> add AB
			setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			Assert.assertNotSame(linkAB, this.linkBA);

			// existent BA   --> add BA   --> remains BA
			setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodSimpleLink.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLink.class, javaMethodNode, javaClassNode, false);
			Assert.assertEquals(linkBA, this.linkBA);
			
			// existent BA   --> add BOTH --> add BOTH
			setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, true);
			Assert.assertNotSame(linkBoth, this.linkBA);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_CHANGE_TO_BIDIRECTIONAL NOT SET
	 * existent BOTH --> add AB   --> add AB
	 * existent BOTH --> add BA   --> add BA
	 * existent BOTH --> add BOTH --> remains BOTH
	 */
	@Test
	public void testAddSimpleLinkExistentBothCase() {
		
		try {

			// existent BOTH --> add AB   --> add AB
			setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			Assert.assertNotSame(linkAB, this.linkBoth);
			
			// existent BOTH --> add BA   --> add BA
			setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodSimpleLink.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLink.class, javaMethodNode, javaClassNode, false);
			Assert.assertNotSame(linkBA, this.linkBoth);
			
			// existent BOTH --> add BOTH --> remains BOTH
			setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLink.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, true);
			Assert.assertEquals(linkBoth, this.linkBoth);
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_CHANGE_TO_BIDIRECTIONAL (ACTB) SET
	 * empty         --> add AB   --> add AB
	 * empty         --> add BA   --> add BA
	 * empty         --> add BOTH --> add BOTH
	 */
	@Test
	public void testAddSimpleLinkEmptyCaseACTB() {
		
		try {

			// empty         --> add AB   --> add AB
			setUpEmptyLinkScenario();
			SLLink linkAB = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, false);
			
			// empty         --> add BA   --> add BA
			setUpEmptyLinkScenario();
			SLLink linkBA = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode, javaClassNode, false);
			
			// empty         --> add BOTH --> add BOTH
			setUpEmptyLinkScenario();
			SLLink linkBoth = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	/**
	 * ALLOWS_CHANGE_TO_BIDIRECTIONAL (ACTB) SET
	 * existent AB   --> add AB   --> remains AB
	 * existent AB   --> add BA   --> changes to BOTH
	 * existent AB   --> add BOTH --> changes to BOTH
	 */
	@Test
	public void testAddSimpleLinkExistentABCaseACTB() {
		
		try {
		
			// existent AB   --> add AB   --> remains AB
			setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, false);
			Assert.assertEquals(linkAB, this.linkAB);
			
			// existent AB   --> add BA   --> changes to BOTH
			setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode, javaClassNode, true);
			Assert.assertEquals(linkBA, this.linkAB);
			
			// existent AB   --> add BOTH --> changes to BOTH
			setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			Assert.assertEquals(linkBoth, this.linkAB);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	/**
	 * ALLOWS_CHANGE_TO_BIDIRECTIONAL (ACTB) SET
	 * existent BA   --> add AB   --> changes to BOTH
	 * existent BA   --> add BA   --> remains BA
	 * existent BA   --> add BOTH --> changes to BOTH
	 */
	@Test
	public void testAddSimpleLinkExistentBACaseACTB() {
		
		try {
		
			// existent BA   --> add AB   --> changes to BOTH
			setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			Assert.assertEquals(linkAB, this.linkBA);
			
			// existent BA   --> add BA   --> remains BA
			setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode, javaClassNode, false);
			Assert.assertEquals(linkBA, this.linkBA);
			
			// existent BA   --> add BOTH --> changes to BOTH
			setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			Assert.assertEquals(linkBoth, this.linkBA);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_CHANGE_TO_BIDIRECTIONAL (ACTB) SET
	 * existent BOTH --> add AB   --> remains BOTH
	 * existent BOTH --> add BA   --> remains BOTH
	 * existent BOTH --> add BOTH --> remains BOTH
	 */
	@Test
	public void testAddSimpleLinkExistentBothCaseACTB() {
		
		try {

			// existent BOTH --> add AB   --> remains BOTH
			setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			Assert.assertEquals(linkAB, this.linkBoth);
			
			// existent BOTH --> add BA   --> remains BOTH
			setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode, javaClassNode, true);
			Assert.assertEquals(linkBA, this.linkBoth);
			
			// existent BOTH --> add BOTH --> remains BOTH
			setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode, javaMethodNode, true);
			Assert.assertEquals(linkBoth, this.linkBoth);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_MULTIPLE SET
	 * empty         --> add AB   --> add AB
	 * empty         --> add BA   --> add BA
	 * empty         --> add BOTH --> add BOTH
	 */
	@Test
	public void addAddMultipleLinkEmptyCase() {
		
		try {
			
			// empty         --> add AB   --> add AB
			setUpEmptyLinkScenario();
			SLLink linkAB = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, false);
			
			// empty         --> add BA   --> add BA
			setUpEmptyLinkScenario();
			SLLink linkBA = session.addLink(JavaClassJavaMethodMultipleLink.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodMultipleLink.class, javaMethodNode, javaClassNode, false);
			
			// empty         --> add BOTH --> add BOTH
			setUpEmptyLinkScenario();
			SLLink linkBoth = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, true);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_MULTIPLE SET
	 * existent AB   --> add AB   --> add NEW AB
	 * existent AB   --> add BA   --> add BA
	 * existent AB   --> add BOTH --> add BOTH
	 */
	@Test
	public void addAddMultipleLinkExistentABCase() {
		
		try {

			// existent AB   --> add AB   --> add NEW AB
			setUpExistentABLinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, false);
			Assert.assertNotSame(linkAB, this.linkAB);
			
			// existent AB   --> add BA   --> add BA
			setUpExistentABLinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodMultipleLink.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodMultipleLink.class, javaMethodNode, javaClassNode, false);
			Assert.assertNotSame(linkBA, this.linkAB);
			
			// existent AB   --> add BOTH --> add BOTH
			setUpExistentABLinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, true);
			Assert.assertNotSame(linkBoth, this.linkAB);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * ALLOWS_MULTIPLE SET
	 * existent BA   --> add BA   --> add NEW BA
	 * existent BA   --> add AB   --> add AB
	 * existent BA   --> add BOTH --> add BOTH
	 */
	@Test
	public void addAddMultipleLinkExistentBACase() {
		
		try {
			
			// existent BA   --> add BA   --> add NEW BA
			setUpExistentBALinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, false);
			Assert.assertNotSame(linkAB, this.linkBA);
			
			// existent BA   --> add AB   --> add AB
			setUpExistentBALinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodMultipleLink.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodMultipleLink.class, javaMethodNode, javaClassNode, false);
			Assert.assertNotSame(linkBA, this.linkBA);
			
			// existent BA   --> add BOTH --> add BOTH
			setUpExistentBALinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, true);
			Assert.assertNotSame(linkBoth, this.linkBA);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}


	/**
	 * ALLOWS_MULTIPLE SET
	 * existent BOTH --> add AB   --> add AB
	 * existent BOTH --> add BA   --> add BA
	 * existent BOTH --> add BOTH --> add NEW BOTH
	 */
	@Test
	public void addAddMultipleLinkExistentBothCase() {
		
		try {
			
			// existent BOTH --> add AB   --> add AB
			setUpExistentBothLinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkAB = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, false);
			assertSimpleLink(linkAB, JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, false);
			Assert.assertNotSame(linkAB, this.linkBoth);
			
			// existent BOTH --> add BA   --> add BA
			setUpExistentBothLinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkBA = session.addLink(JavaClassJavaMethodMultipleLink.class, javaMethodNode, javaClassNode, false);
			assertSimpleLink(linkBA, JavaClassJavaMethodMultipleLink.class, javaMethodNode, javaClassNode, false);
			Assert.assertNotSame(linkBA, this.linkBoth);
			
			// existent BOTH --> add BOTH --> add NEW BOTH
			setUpExistentBothLinkScenario(JavaClassJavaMethodMultipleLink.class);
			SLLink linkBoth = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, true);
			assertSimpleLink(linkBoth, JavaClassJavaMethodMultipleLink.class, javaClassNode, javaMethodNode, true);
			Assert.assertNotSame(linkBoth, this.linkBoth);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testLinkProperties() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaPackageNode javaPackageNode1 = root1.addNode(JavaPackageNode.class, "javaPackageNode1");
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			
			String name;
			Integer value;
			SLLink link;
			SLLinkProperty<Integer> property;
			
			// test set property ...
			link = session.addLink(JavaPackageJavaClass.class, javaPackageNode1, javaClassNode1, false);
			property = link.setProperty(Integer.class, "integerProperty", 8);
			Assert.assertNotNull(property);
			name = property.getName();
			Assert.assertEquals(name, "integerProperty");
			value = property.getValue();
			Assert.assertNotNull(value);
			Assert.assertEquals(value, new Integer(8));
			
			// test get property ...
			property = link.getProperty(Integer.class, "integerProperty");
			Assert.assertNotNull(property);
			name = property.getName();
			Assert.assertEquals(name, "integerProperty");
			value = property.getValue();
			Assert.assertNotNull(value);
			Assert.assertEquals(value, new Integer(8));
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testGetLinks() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLLink simpleLinkAB = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			SLLink simpleLinkBA = session.addLink(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, false);
			SLLink simpleLinkBoth = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, true);

			Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
			
			// direction filter:
			// DIRECTION_UNI: 											AB
			// DIRECTION_UNI_REVERSAL: 									BA
			// DIRECTION_BI: 											BOTH
			// DIRECTION_UNI | DIRECTION_UNI_REVERSAL:					AB, BA
			// DIRECTION_UNI | DIRECTION_BI:							AB, BOTH
			// DIRECTION_UNI_REVERSAL | DIRECTION_BI: 					BA, BOTH
			// DIRECTION_UNI | DIRECTION_UNI_REVERSAL | DIRECTION_BI:	AB, BA, BOTH
			// DIRECTION_ANY:											AB, BA, BOTH
			
			// test getLinks between javaClassNode1 and javaMethodNode1
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, DIRECTION_UNI);
			assertLinksInOrder(simpleLinks, simpleLinkAB);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, DIRECTION_UNI);
			assertLinksInOrder(simpleLinks, simpleLinkBA);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, DIRECTION_UNI_REVERSAL);
			assertLinksInOrder(simpleLinks, simpleLinkBA);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, DIRECTION_UNI_REVERSAL);
			assertLinksInOrder(simpleLinks, simpleLinkAB);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, DIRECTION_UNI | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, DIRECTION_UNI | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, DIRECTION_UNI_REVERSAL | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, DIRECTION_UNI_REVERSAL | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, DIRECTION_ANY);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, DIRECTION_ANY);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA, simpleLinkBoth);

			
			// test getLinks between javaClassNode1 and * 
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, null, DIRECTION_UNI);
			assertLinksInOrder(simpleLinks, simpleLinkAB);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaClassNode1, DIRECTION_UNI);
			assertLinksInOrder(simpleLinks, simpleLinkBA);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, null, DIRECTION_UNI_REVERSAL);
			assertLinksInOrder(simpleLinks, simpleLinkBA);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaClassNode1, DIRECTION_UNI_REVERSAL);
			assertLinksInOrder(simpleLinks, simpleLinkAB);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, null, DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaClassNode1, DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, null, DIRECTION_UNI | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaClassNode1, DIRECTION_UNI | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, null, DIRECTION_UNI_REVERSAL | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaClassNode1, DIRECTION_UNI_REVERSAL | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, null, DIRECTION_ANY);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaClassNode1, DIRECTION_ANY);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA, simpleLinkBoth);

			
			// test getLinks between javaMethodNode1 and *
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaMethodNode1, DIRECTION_UNI);
			assertLinksInOrder(simpleLinks, simpleLinkAB);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, null, DIRECTION_UNI);
			assertLinksInOrder(simpleLinks, simpleLinkBA);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaMethodNode1, DIRECTION_UNI_REVERSAL);
			assertLinksInOrder(simpleLinks, simpleLinkBA);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, null, DIRECTION_UNI_REVERSAL);
			assertLinksInOrder(simpleLinks, simpleLinkAB);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaMethodNode1, DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, null, DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaMethodNode1, DIRECTION_UNI | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, null, DIRECTION_UNI | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);

			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaMethodNode1, DIRECTION_UNI_REVERSAL | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, null, DIRECTION_UNI_REVERSAL | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, javaMethodNode1, DIRECTION_ANY);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA, simpleLinkBoth);
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, null, DIRECTION_ANY);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA, simpleLinkBoth);

			// test getLinks between * and *
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, null, DIRECTION_UNI);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, null, DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			
			simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, null, DIRECTION_UNI | DIRECTION_BI);
			assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA, simpleLinkBoth);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetUnidirectionalLinks() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLLink simpleLinkAB = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			SLLink simpleLinkBA = session.addLink(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, false);
			SLLink multipleLinkAB = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode1, javaMethodNode1, false);
			SLLink multipleLinkBA = session.addLink(JavaClassJavaMethodMultipleLink.class, javaMethodNode1, javaClassNode1, false);

			Collection<SLLink> links = null;
			Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
			Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;
			
			simpleLinks = session.getUnidirectionalLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1);
			assertLinksInOrder(simpleLinks, simpleLinkAB);
			simpleLinks = session.getUnidirectionalLinks(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1);
			assertLinksInOrder(simpleLinks, simpleLinkBA);
			
			multipleLinks = session.getUnidirectionalLinks(JavaClassJavaMethodMultipleLink.class, javaClassNode1, javaMethodNode1);
			assertLinksInOrder(multipleLinks, multipleLinkAB);
			multipleLinks = session.getUnidirectionalLinks(JavaClassJavaMethodMultipleLink.class, javaMethodNode1, javaClassNode1);
			assertLinksInOrder(multipleLinks, multipleLinkBA);
			
			links = session.getUnidirectionalLinks(javaClassNode1, javaMethodNode1);
			assertLinks(links, simpleLinkAB, multipleLinkAB);
			links = session.getUnidirectionalLinks(javaMethodNode1, javaClassNode1);
			assertLinks(links, simpleLinkBA, multipleLinkBA);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetUnidirectionalLinksBySource() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLLink simpleLinkAB = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			SLLink simpleLinkBA = session.addLink(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, false);
			SLLink multipleLinkAB = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode1, javaMethodNode1, false);
			SLLink multipleLinkBA = session.addLink(JavaClassJavaMethodMultipleLink.class, javaMethodNode1, javaClassNode1, false);

			Collection<SLLink> links = null;
			Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
			Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;
			
			simpleLinks = session.getUnidirectionalLinksBySource(JavaClassJavaMethodSimpleLink.class, javaClassNode1);
			assertLinksInOrder(simpleLinks, simpleLinkAB);
			simpleLinks = session.getUnidirectionalLinksBySource(JavaClassJavaMethodSimpleLink.class, javaMethodNode1);
			assertLinksInOrder(simpleLinks, simpleLinkBA);
			
			multipleLinks = session.getUnidirectionalLinksBySource(JavaClassJavaMethodMultipleLink.class, javaClassNode1);
			assertLinksInOrder(multipleLinks, multipleLinkAB);
			multipleLinks = session.getUnidirectionalLinksBySource(JavaClassJavaMethodMultipleLink.class, javaMethodNode1);
			assertLinksInOrder(multipleLinks, multipleLinkBA);
			
			links = session.getUnidirectionalLinksBySource(javaClassNode1);
			assertLinks(links, simpleLinkAB, multipleLinkAB);
			links = session.getUnidirectionalLinksBySource(javaMethodNode1);
			assertLinks(links, simpleLinkBA, multipleLinkBA);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testGetUnidirectionalLinksByTarget() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLLink simpleLinkAB = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			SLLink simpleLinkBA = session.addLink(JavaClassJavaMethodSimpleLink.class, javaMethodNode1, javaClassNode1, false);
			SLLink multipleLinkAB = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode1, javaMethodNode1, false);
			SLLink multipleLinkBA = session.addLink(JavaClassJavaMethodMultipleLink.class, javaMethodNode1, javaClassNode1, false);

			Collection<SLLink> links = null;
			Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
			Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;
			
			simpleLinks = session.getUnidirectionalLinksByTarget(JavaClassJavaMethodSimpleLink.class, javaMethodNode1);
			assertLinksInOrder(simpleLinks, simpleLinkAB);
			simpleLinks = session.getUnidirectionalLinksByTarget(JavaClassJavaMethodSimpleLink.class, javaClassNode1);
			assertLinksInOrder(simpleLinks, simpleLinkBA);
			
			multipleLinks = session.getUnidirectionalLinksByTarget(JavaClassJavaMethodMultipleLink.class, javaMethodNode1);
			assertLinksInOrder(multipleLinks, multipleLinkAB);
			multipleLinks = session.getUnidirectionalLinksByTarget(JavaClassJavaMethodMultipleLink.class, javaClassNode1);
			assertLinksInOrder(multipleLinks, multipleLinkBA);
			
			links = session.getUnidirectionalLinksByTarget(javaMethodNode1);
			assertLinks(links, simpleLinkAB, multipleLinkAB);
			links = session.getUnidirectionalLinksByTarget(javaClassNode1);
			assertLinks(links, simpleLinkBA, multipleLinkBA);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetBidirectionalLinks() {

		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLLink simpleLinkBoth = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, true);
			SLLink multipleLinkBoth = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode1, javaMethodNode1, true);

			Collection<SLLink> links = null;
			Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
			Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;
			
			simpleLinks = session.getBidirectionalLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			
			multipleLinks = session.getBidirectionalLinks(JavaClassJavaMethodMultipleLink.class, javaClassNode1, javaMethodNode1);
			assertLinksInOrder(multipleLinks, multipleLinkBoth);
			
			links = session.getBidirectionalLinks(javaClassNode1, javaMethodNode1);
			assertLinks(links, simpleLinkBoth, multipleLinkBoth);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	@Test
	public void testGetBidirectionalLinksBySide() {

		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLLink simpleLinkBoth = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, true);
			SLLink multipleLinkBoth = session.addLink(JavaClassJavaMethodMultipleLink.class, javaClassNode1, javaMethodNode1, true);

			Collection<SLLink> links = null;
			Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
			Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;
			
			simpleLinks = session.getBidirectionalLinksBySide(JavaClassJavaMethodSimpleLink.class, javaClassNode1);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			simpleLinks = session.getBidirectionalLinksBySide(JavaClassJavaMethodSimpleLink.class, javaMethodNode1);
			assertLinksInOrder(simpleLinks, simpleLinkBoth);
			
			multipleLinks = session.getBidirectionalLinksBySide(JavaClassJavaMethodMultipleLink.class, javaClassNode1);
			assertLinksInOrder(multipleLinks, multipleLinkBoth);
			multipleLinks = session.getBidirectionalLinksBySide(JavaClassJavaMethodMultipleLink.class, javaMethodNode1);
			assertLinksInOrder(multipleLinks, multipleLinkBoth);
			
			links = session.getBidirectionalLinksBySide(javaClassNode1);
			assertLinks(links, simpleLinkBoth, multipleLinkBoth);
			links = session.getBidirectionalLinksBySide(javaMethodNode1);
			assertLinks(links, simpleLinkBoth, multipleLinkBoth);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	@Test
	public void testTransientNodes() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			javaClassNode1.addNode(TransientNode.class, "transNode1");
			javaMethodNode1.addNode(TransientNode.class, "transNode2");
			
			session.save();
			session = graph.openSession();
			
			root1 = session.getContext(1L).getRootNode();
			javaClassNode1 = root1.getNode(JavaClassNode.class, "javaClassNode1");
			javaMethodNode1 = javaClassNode1.getNode(JavaMethodNode.class, "javaMethodNode1");
			
			Assert.assertNull(javaClassNode1.getNode(TransientNode.class, "javaClassNode1"));
			Assert.assertNull(javaMethodNode1.getNode(TransientNode.class, "javaClassNode2"));
			
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testTransientLinks() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			JavaMethodNode javaMethodNode2 = root1.addNode(JavaMethodNode.class, "javaMethodNode2");
			
			session.addLink(TransientLink.class, javaClassNode1, javaMethodNode1, false);
			session.addLink(TransientLink.class, javaClassNode1, javaMethodNode2, false);

			session.save();
			session = graph.openSession();
			
			root1 = session.getContext(1L).getRootNode();
			javaClassNode1 = root1.getNode(JavaClassNode.class, "javaClassNode1");
			javaMethodNode1 = root1.getNode(JavaMethodNode.class, "javaMethodNode1");
			Collection<? extends SLLink> links = session.getUnidirectionalLinks(TransientLink.class, javaClassNode1, javaMethodNode1);
			Assert.assertEquals(links.size(), 0);
			
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testGetNodesByLinkWithLinkType() {
		
		try {
			
			SLNode root1 = session.createContext(1L).getRootNode();
			JavaPackageNode javaPackageNode1 = root1.addNode(JavaPackageNode.class, "javaPackageNode1");
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaClassNode javaClassNode2 = root1.addNode(JavaClassNode.class, "javaClassNode2");
			JavaInnerClassNode javaInnerClassNode1 = root1.addNode(JavaInnerClassNode.class, "javaInnerClassNode1");
			JavaInnerClassNode javaInnerClassNode2 = root1.addNode(JavaInnerClassNode.class, "javaInnerClassNode2");
			
			session.addLink(JavaPackageJavaClass.class, javaPackageNode1, javaClassNode1, false);
			session.addLink(JavaPackageJavaClass.class, javaPackageNode1, javaClassNode2, true);
			session.addLink(JavaPackageJavaClass.class, javaClassNode1, javaPackageNode1, false);
			session.addLink(JavaPackageJavaClass.class, javaClassNode2, javaPackageNode1, false);

			session.addLink(JavaPackageJavaClass.class, javaPackageNode1, javaInnerClassNode1, false);
			session.addLink(JavaPackageJavaClass.class, javaPackageNode1, javaInnerClassNode2, true);
			session.addLink(JavaPackageJavaClass.class, javaInnerClassNode1, javaPackageNode1, false);
			session.addLink(JavaPackageJavaClass.class, javaInnerClassNode2, javaPackageNode1, false);
			
			Collection<? extends SLNode> nodes = null;
			
			nodes = session.getNodesByLink(JavaPackageJavaClass.class, javaPackageNode1);
			assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1, javaInnerClassNode2);
			
			nodes = session.getNodesByLink(JavaPackageJavaClass.class, javaPackageNode1, JavaClassNode.class, false);
			assertNodes(nodes, javaClassNode1, javaClassNode2);

			nodes = session.getNodesByLink(JavaPackageJavaClass.class, javaPackageNode1, JavaInnerClassNode.class, false);
			assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);
			
			nodes = session.getNodesByLink(JavaPackageJavaClass.class, javaPackageNode1, JavaClassNode.class, true);
			assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1, javaInnerClassNode2);

			nodes = session.getNodesByLink(JavaPackageJavaClass.class, javaPackageNode1, JavaInnerClassNode.class, true);
			assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);
			
			nodes = session.getNodesByLink(JavaPackageJavaClass.class);
			assertNodes(nodes, javaPackageNode1, javaClassNode1, javaClassNode2, javaInnerClassNode1, javaInnerClassNode2);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testGetNodesByLinkWithoutLinkType() {
		
		try {
			
			SLNode root1 = session.createContext(1L).getRootNode();
			JavaPackageNode javaPackageNode1 = root1.addNode(JavaPackageNode.class, "javaPackageNode1");
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaClassNode javaClassNode2 = root1.addNode(JavaClassNode.class, "javaClassNode2");
			JavaInnerClassNode javaInnerClassNode1 = root1.addNode(JavaInnerClassNode.class, "javaInnerClassNode1");
			JavaInnerClassNode javaInnerClassNode2 = root1.addNode(JavaInnerClassNode.class, "javaInnerClassNode2");
			
			session.addLink(JavaPackageJavaClass.class, javaPackageNode1, javaClassNode1, false);
			session.addLink(JavaPackageJavaClass.class, javaPackageNode1, javaInnerClassNode1, true);

			session.addLink(JavaPackagePublicElement.class, javaPackageNode1, javaClassNode2, false);
			session.addLink(JavaPackagePublicElement.class, javaPackageNode1, javaInnerClassNode2, true);
			
			Collection<? extends SLNode> nodes = null;
			
			nodes = session.getNodesByLink(javaPackageNode1);
			assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1, javaInnerClassNode2);
			
			nodes = session.getNodesByLink(javaPackageNode1, JavaClassNode.class, false);
			assertNodes(nodes, javaClassNode1, javaClassNode2);

			nodes = session.getNodesByLink(javaPackageNode1, JavaInnerClassNode.class, false);
			assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);
			
			nodes = session.getNodesByLink(javaPackageNode1, JavaClassNode.class, true);
			assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1, javaInnerClassNode2);

			nodes = session.getNodesByLink(javaPackageNode1, JavaInnerClassNode.class, true);
			assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testLinksRemovalByNodeDeletion() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaPackageNode javaPackageNode1 = root1.addNode(JavaPackageNode.class, "javaPackageNode1");
			JavaClassNode javaClassNode1 = javaPackageNode1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaClassNode javaClassNode2 = javaPackageNode1.addNode(JavaClassNode.class, "javaClassNode2");
			
			JavaMethodNode javaMethodNode1A = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1A");
			JavaMethodNode javaMethodNode1B = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1B");
			
			JavaMethodNode javaMethodNode2A = javaClassNode2.addNode(JavaMethodNode.class, "javaMethodNode2A");
			JavaMethodNode javaMethodNode2B = javaClassNode2.addNode(JavaMethodNode.class, "javaMethodNode2B");
			
			session.addLink(JavaLink.class, javaPackageNode1, javaClassNode1, false);
			session.addLink(JavaLink.class, javaPackageNode1, javaClassNode2, false);
			session.addLink(JavaLink.class, javaPackageNode1, javaMethodNode1A, false);
			session.addLink(JavaLink.class, javaPackageNode1, javaMethodNode1B, false);
			session.addLink(JavaLink.class, javaPackageNode1, javaMethodNode2A, false);
			session.addLink(JavaLink.class, javaPackageNode1, javaMethodNode2B, false);
			
			session.addLink(JavaLink.class, javaClassNode1, javaMethodNode1A, false);
			session.addLink(JavaLink.class, javaClassNode1, javaMethodNode1B, false);
			session.addLink(JavaLink.class, javaClassNode2, javaMethodNode2A, false);
			session.addLink(JavaLink.class, javaClassNode2, javaMethodNode2B, false);
			
			Collection<JavaLink> links = session.getLinks(JavaLink.class, null, null, SLLink.DIRECTION_ANY);
			Assert.assertEquals(links.size(), 10);
			
			javaPackageNode1.remove();
			links = session.getLinks(JavaLink.class, null, null, SLLink.DIRECTION_ANY);
			Assert.assertTrue(links.isEmpty());
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetNodesByPredicate() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaPackageNode javaPackageNode1 = root1.addNode(JavaPackageNode.class, "javaPackageNode1");
			JavaClassNode javaClassNode1 = javaPackageNode1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaClassNode javaClassNode2 = javaPackageNode1.addNode(JavaClassNode.class, "javaClassNode2");
			
			JavaMethodNode javaMethodNode1A = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1A");
			JavaMethodNode javaMethodNode1B = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1B");
			
			JavaMethodNode javaMethodNode2A = javaClassNode2.addNode(JavaMethodNode.class, "javaMethodNode2A");
			JavaMethodNode javaMethodNode2B = javaClassNode2.addNode(JavaMethodNode.class, "javaMethodNode2B");
			
			Collection<SLNode> nodes = null; 
			nodes = session.getNodesByPredicate(new NamePredicate("javaPackage"));
			assertNodes(nodes, javaPackageNode1);
			
			nodes = session.getNodesByPredicate(new NamePredicate("javaClass"));
			assertNodes(nodes, javaClassNode1, javaClassNode2);
			
			nodes = session.getNodesByPredicate(new NamePredicate("javaMethod"));
			assertNodes(nodes, javaMethodNode1A, javaMethodNode1B, javaMethodNode2A, javaMethodNode2B);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testLineReference() {
		
		try {
			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			
			SLLineReference lineRef1 = javaClassNode1.addLineReference();
			lineRef1.setStartLine(8);
			lineRef1.setEndLine(17);
			lineRef1.setStartColumn(26);
			lineRef1.setEndColumn(44);
			lineRef1.setLineType(SLLineReference.LINE_TYPE_1);
			lineRef1.setStatement("Hello World!");
			
			SLLineReference lineRef2 = javaClassNode1.getLineReference();
			Assert.assertEquals(lineRef1.getStartLine(), lineRef2.getStartLine());
			Assert.assertEquals(lineRef1.getEndLine(), lineRef2.getEndLine());
			Assert.assertEquals(lineRef1.getStartColumn(), lineRef2.getStartColumn());
			Assert.assertEquals(lineRef1.getEndColumn(), lineRef2.getEndColumn());
			Assert.assertEquals(lineRef1.getLineType(), lineRef2.getLineType());
			Assert.assertEquals(lineRef1.getStatement(), lineRef2.getStatement());
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testGetMetaNode() {

		try {
			
			SLNode root1 = session.createContext(1L).getRootNode();
			root1.addNode(JavaPackageNode.class, "javaPackageNode1");
			root1.addNode(JavaPackageNode.class, "javaPackageNode2");
			root1.addNode(JavaClassNode.class, "javaClassNode1");
			root1.addNode(JavaClassNode.class, "javaClassNode2");

			SLMetadata metadata = session.getMetadata();
			
			SLMetaNode metaNode1 = metadata.getMetaNode(JavaPackageNode.class);
			Assert.assertNotNull(metaNode1);
			Assert.assertEquals(metaNode1.getType(), JavaPackageNode.class);
			
			SLMetaNode metaNode2 = metadata.getMetaNode(JavaClassNode.class);
			Assert.assertNotNull(metaNode2);
			Assert.assertEquals(metaNode2.getType(), JavaClassNode.class);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testGetMetaNodes() {

		try {
			SLNode root1 = session.createContext(1L).getRootNode();
			root1.addNode(JavaPackageNode.class, "javaPackageNode1");
			root1.addNode(JavaPackageNode.class, "javaPackageNode2");
			root1.addNode(JavaClassNode.class, "javaClassNode1");
			root1.addNode(JavaClassNode.class, "javaClassNode2");

			SLMetadata metadata = session.getMetadata();
			Collection<SLMetaNode> metaNodes = metadata.getMetaNodes();
			assertMetaNodes(metaNodes, JavaPackageNode.class, JavaClassNode.class);
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test 
	public void testGetChildNodeNode() {

		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			javaClassNode1.addNode(JavaInnerClassNode.class, "javaInnerClassNode1"); 
			javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLMetadata metadata = session.getMetadata();
			SLMetaNode javaClassMetaNode1 = metadata.getMetaNode(JavaClassNode.class);
			
			SLMetaNode javaInnerClassMetaNode1 = javaClassMetaNode1.getMetaNode(JavaInnerClassNode.class);
			Assert.assertNotNull(javaInnerClassMetaNode1);
			Assert.assertEquals(javaInnerClassMetaNode1.getType(), JavaInnerClassNode.class);
			
			SLMetaNode javaMethodMetaNode1 = javaClassMetaNode1.getMetaNode(JavaMethodNode.class);
			Assert.assertNotNull(javaMethodMetaNode1);
			Assert.assertEquals(javaMethodMetaNode1.getType(), JavaMethodNode.class);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetChildMetaNodes() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			javaClassNode1.addNode(JavaInnerClassNode.class, "javaInnerClassNode1"); 
			javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLMetadata metadata = session.getMetadata();
			SLMetaNode javaClassMetaNode1 = metadata.getMetaNode(JavaClassNode.class);
			Collection<SLMetaNode> metaNodes = javaClassMetaNode1.getMetaNodes();
			assertMetaNodes(metaNodes, JavaInnerClassNode.class, JavaMethodNode.class);

		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testGetMetaNodeProperties() {
		
		try {
			
			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			javaClassNode1.setClassName("HelloWorld");
			javaClassNode1.setModifier(JavaClassNode.MODIFIER_PUBLIC);
			javaClassNode1.setCreationTime(new Date());
			
			SLMetadata metadata = session.getMetadata();
			SLMetaNode metaNode = metadata.getMetaNodes().iterator().next();
			Collection<SLMetaNodeProperty> metaProperties = metaNode.getMetaProperties();
			Assert.assertEquals(metaProperties.size(), 3);
			
			for (SLMetaNodeProperty metaProperty : metaProperties) {
				Assert.assertNotNull(metaProperty.getName());
				if (metaProperty.getName().equals("className")) {
					Assert.assertEquals(metaProperty.getType(), String.class);
				}
				else if (metaProperty.getName().equals("modifier")) {
					Assert.assertEquals(metaProperty.getType(), Long.class);
				}
				else if (metaProperty.getName().equals("creationTime")) {
					Assert.assertEquals(metaProperty.getType(), Date.class);	
				}
				else {
					Assert.fail();
				}
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	public void testGetMetaNodeProperty() {
		
		try {
			
			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode = root1.addNode(JavaClassNode.class, "javaClassNode");
			javaClassNode.setClassName("HelloWorld");
			javaClassNode.setModifier(JavaClassNode.MODIFIER_PUBLIC);
			javaClassNode.setCreationTime(new Date());
			
			SLMetadata metadata = session.getMetadata();
			SLMetaNode metaNode = metadata.getMetaNode(JavaClassNode.class);
			
			SLMetaNodeProperty classNameMetaProperty = metaNode.getMetaProperty("className");
			Assert.assertNotNull(classNameMetaProperty);
			Assert.assertEquals(classNameMetaProperty.getName(), "className");
			Assert.assertEquals(classNameMetaProperty.getType(), String.class);
			
			SLMetaNodeProperty modifierMetaProperty = metaNode.getMetaProperty("modifer");
			Assert.assertNotNull(modifierMetaProperty);
			Assert.assertEquals(modifierMetaProperty.getName(), "modifer");
			Assert.assertEquals(modifierMetaProperty.getType(), Long.class);
			
			SLMetaNodeProperty creationTimeMetaProperty = metaNode.getMetaProperty("creationTime");
			Assert.assertNotNull(creationTimeMetaProperty);
			Assert.assertEquals(creationTimeMetaProperty.getName(), "creationTime");
			Assert.assertEquals(creationTimeMetaProperty.getType(), Date.class);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testGetMetaLinkProperties() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLLink link = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			link.setProperty(String.class, "author", "Zé Café");
			link.setProperty(Integer.class, "age", 270);
			
			SLMetadata metadata = session.getMetadata();
			SLMetaLinkType metaLinkType = metadata.getMetaLinkType(JavaClassJavaMethodSimpleLink.class);
			SLMetaLink metaLink = metaLinkType.getMetalinks().iterator().next();
			
			SLMetaLinkProperty authorMetaProperty = metaLink.getMetaProperty("author");
			Assert.assertNotNull(authorMetaProperty);
			Assert.assertEquals(authorMetaProperty.getName(), "author");
			Assert.assertEquals(authorMetaProperty.getType(), String.class);
			
			SLMetaLinkProperty ageMetaProperty = metaLink.getMetaProperty("age");
			Assert.assertNotNull(ageMetaProperty);
			Assert.assertEquals(ageMetaProperty.getName(), "age");
			Assert.assertEquals(ageMetaProperty.getType(), Integer.class);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testGetMetaLinkProperty() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			SLLink link = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			link.setProperty(String.class, "author", "Zé Café");
			link.setProperty(Integer.class, "age", 270);
			
			SLMetadata metadata = session.getMetadata();
			SLMetaLinkType metaLinkType = metadata.getMetaLinkType(JavaClassJavaMethodSimpleLink.class);
			SLMetaLink metaLink = metaLinkType.getMetalinks().iterator().next();
			
			Collection<SLMetaLinkProperty> metaProperties = metaLink.getMetaProperties();
			Assert.assertEquals(metaProperties.size(), 2);
			
			for (SLMetaLinkProperty metaProperty : metaProperties) {
				Assert.assertNotNull(metaProperty.getName());
				if (metaProperty.getName().equals("author")) {
					Assert.assertEquals(metaProperty.getType(), String.class);
				}
				else if (metaProperty.getName().equals("age")) {
					Assert.assertEquals(metaProperty.getType(), Integer.class);
				}
				else {
					Assert.fail();
				}
			}
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	@Test
	public void testLinkTypesForLinkDeletionMarkCase() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			JavaMethodNode javaMethodNode2 = root1.addNode(JavaMethodNode.class, "javaMethodNode2");
			
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode2, false);
			
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion = new ArrayList<Class<? extends SLLink>>();
			linkTypesForLinkDeletion.add(JavaClassJavaMethodSimpleLink.class);
			root1.addNode(JavaMethodNode.class, "javaMethodNode2", linkTypesForLinkDeletion, null);
			
			session.save();
			session = graph.openSession();
			
			root1 = session.getContext(1L).getRootNode();
			javaClassNode1 = root1.getNode(JavaClassNode.class, "javaClassNode1");
			javaMethodNode1 = root1.getNode(JavaMethodNode.class, "javaMethodNode1");
			Collection<? extends SLLink> links = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, null);
			Assert.assertEquals(links.size(), 1);
			
			SLLink link = links.iterator().next();
			Assert.assertEquals(link.getSource(), javaClassNode1);
			Assert.assertEquals(link.getTarget(), javaMethodNode1);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testLinkTypesForLinkDeletionMarkAndUnmarkCase() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			JavaMethodNode javaMethodNode2 = root1.addNode(JavaMethodNode.class, "javaMethodNode2");
			
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode2, false);
			
			Collection<Class<? extends SLLink>> linkTypesForLinkDeletion = new ArrayList<Class<? extends SLLink>>();
			linkTypesForLinkDeletion.add(JavaClassJavaMethodSimpleLink.class);
			root1.addNode(JavaMethodNode.class, "javaMethodNode2", linkTypesForLinkDeletion, null);
			
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode2, false);
			
			session.save();
			session = graph.openSession();
			
			root1 = session.getContext(1L).getRootNode();
			javaClassNode1 = root1.getNode(JavaClassNode.class, "javaClassNode1");
			javaMethodNode1 = root1.getNode(JavaMethodNode.class, "javaMethodNode1");
			
			Collection<? extends SLLink> links = session.getLinks(JavaClassJavaMethodSimpleLink.class, null, null);
			Assert.assertEquals(links.size(), 2);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testLinkTypesForLinkedNodeDeletionMarkCase() {

		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			JavaMethodNode javaMethodNode2 = root1.addNode(JavaMethodNode.class, "javaMethodNode2");
			
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode2, false);
			
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodesDeletion = new ArrayList<Class<? extends SLLink>>();
			linkTypesForLinkedNodesDeletion.add(JavaClassJavaMethodSimpleLink.class);
			root1.addNode(JavaClassNode.class, "javaClassNode1", null, linkTypesForLinkedNodesDeletion);
			
			session.save();
			session = graph.openSession();
			
			root1 = session.getContext(1L).getRootNode();
			javaClassNode1 = root1.getNode(JavaClassNode.class, "javaClassNode1");
			Collection<SLNode> nodes = javaClassNode1.getNodes();
			Assert.assertTrue(nodes.isEmpty());
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testLinkTypesForLinkedNodeDeletionMarkAndUnmarkCase() {

		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1");
			JavaMethodNode javaMethodNode2 = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode2");
			
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode2, false);
			
			Collection<Class<? extends SLLink>> linkTypesForLinkedNodesDeletion = new ArrayList<Class<? extends SLLink>>();
			linkTypesForLinkedNodesDeletion.add(JavaClassJavaMethodSimpleLink.class);
			root1.addNode(JavaClassNode.class, "javaClassNode1", null, linkTypesForLinkedNodesDeletion);
			
			javaMethodNode1 = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			session.save();
			session = graph.openSession();
			
			root1 = session.getContext(1L).getRootNode();
			javaClassNode1 = root1.getNode(JavaClassNode.class, "javaClassNode1");
			Collection<SLNode> nodes = javaClassNode1.getNodes();
			Assert.assertEquals(nodes.size(), 1);
			Assert.assertEquals(nodes.iterator().next().getName(), "javaMethodNode1");
			
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	private void assertSimpleLink(SLLink link, Class<? extends SLLink> linkClass, SLNode source, SLNode target, boolean bidirecional) throws SLGraphSessionException {
		Assert.assertNotNull(link);
		Assert.assertTrue(linkClass.isInstance(link));
		if (!bidirecional) {
			Assert.assertEquals(link.getSource(), source);
			Assert.assertEquals(link.getTarget(), target);
		}
		Assert.assertEquals(link.getOtherSide(source), target);
		Assert.assertEquals(link.getOtherSide(target), source);
		Assert.assertEquals(link.isBidirectional(), bidirecional);
	}

	private void setUpEmptyLinkScenario() {
		try {
			session.clear();
			SLNode root = session.createContext(1L).getRootNode();
			javaClassNode = root.addNode(JavaClassNode.class, "javaClassNode");
			javaMethodNode = root.addNode(JavaMethodNode.class, "javaMethodNode");
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	private void setUpExistentABLinkScenario(Class<? extends SLLink> linkClass) throws SLGraphSessionException {
		session.clear();
		SLNode root = session.createContext(1L).getRootNode();
		javaClassNode = root.addNode(JavaClassNode.class, "javaClassNode");
		javaMethodNode = root.addNode(JavaMethodNode.class, "javaMethodNode");

		// empty         --> add AB   --> add AB
		linkAB = session.addLink(linkClass, javaClassNode, javaMethodNode, false);
	}
	
	private void setUpExistentBALinkScenario(Class<? extends SLLink> linkClass) throws SLGraphSessionException {
		session.clear();
		SLNode root = session.createContext(1L).getRootNode();
		javaClassNode = root.addNode(JavaClassNode.class, "javaClassNode");
		javaMethodNode = root.addNode(JavaMethodNode.class, "javaMethodNode");

		// empty         --> add BA   --> add BA
		linkBA = session.addLink(linkClass, javaMethodNode, javaClassNode, false);
	}
	
	private void setUpExistentBothLinkScenario(Class<? extends SLLink> linkClass) throws SLGraphSessionException {
		session.clear();
		SLNode root = session.createContext(1L).getRootNode();
		javaClassNode = root.addNode(JavaClassNode.class, "javaClassNode");
		javaMethodNode = root.addNode(JavaMethodNode.class, "javaMethodNode");

		// empty         --> add BOTH --> add BOTH
		linkBoth = session.addLink(linkClass, javaClassNode, javaMethodNode, true);
	}
	
	private void assertLinks(Collection<? extends SLLink> links, SLLink...expectedLinks) {
		Assert.assertNotNull(links);
		Assert.assertEquals(links.size(), expectedLinks.length);
		Set<SLLink> linkSet = new TreeSet<SLLink>(links);
		Set<SLLink> expectedLinkSet = new TreeSet<SLLink>(Arrays.asList(expectedLinks));
		Assert.assertEquals(linkSet, expectedLinkSet);
	}
	
	private void assertLinksInOrder(Collection<? extends SLLink> links, SLLink...expectedLinks) {
		Assert.assertNotNull(links);
		Assert.assertEquals(links.size(), expectedLinks.length);
		Iterator<? extends SLLink> iter = links.iterator();
		for (int i = 0; i < expectedLinks.length; i++) {
			Assert.assertEquals(expectedLinks[i], iter.next());
		}
	}

	private void assertNodes(Collection<? extends SLNode> nodes, SLNode...expectedNodes) {
		Assert.assertNotNull(nodes);
		Assert.assertEquals(nodes.size(), expectedNodes.length);
		Set<SLNode> nodeSet = new TreeSet<SLNode>(nodes);
		Set<SLNode> expectedNodeSet = new TreeSet<SLNode>(Arrays.asList(expectedNodes));
		Assert.assertEquals(nodeSet, expectedNodeSet);
	}
	
	private void assertMetaNodes(Collection<SLMetaNode> metaNodes, Class<?>...expectedNodeTypes) throws SLGraphSessionException {
		Assert.assertNotNull(metaNodes);
		Assert.assertEquals(metaNodes.size(), expectedNodeTypes.length);
		Set<String> metaNodeTypeNameSet = new TreeSet<String>(getNodeTypeNameSet(metaNodes));
		Set<String> expectedNodeTypeNameSet = new TreeSet<String>(getNodeTypeNameSet(expectedNodeTypes));
		Assert.assertEquals(metaNodeTypeNameSet, expectedNodeTypeNameSet);
	}
	
	private Set<String> getNodeTypeNameSet(Collection<SLMetaNode> metaNodes) throws SLGraphSessionException {
		Set<String> set = new TreeSet<String>();
		for (SLMetaNode metaNode : metaNodes) {
			set.add(metaNode.getType().getName());
		}
		return set;
	}
	
	private Set<String> getNodeTypeNameSet(Class<?>[] expectedNodeTypes) throws SLGraphSessionException {
		Set<String> set = new TreeSet<String>();
		for (Class<?> nodeType : expectedNodeTypes) {
			set.add(nodeType.getName());
		}
		return set;
	}
}
