/*
 * OpenSpotLight - Open Source IT Governance Platform
 *  
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA 
 * or third-party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  All third-party 
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E 
 * TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU 
 * Lesser General Public License, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License  for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this distribution; if not, write to: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA 
 * 
 *********************************************************************** 
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os 
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.  
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph;


import static org.openspotlight.graph.SLLink.DIRECTION_ANY;
import static org.openspotlight.graph.SLLink.DIRECTION_BI;
import static org.openspotlight.graph.SLLink.DIRECTION_UNI;
import static org.openspotlight.graph.SLLink.DIRECTION_UNI_REVERSAL;
import static org.openspotlight.graph.SLPersistenceMode.NORMAL;
import static org.openspotlight.graph.SLPersistenceMode.TRANSIENT;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.AbstractFactoryException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * The Class SLGraphTest.
 * 
 * @author Vitor Hugo Chagas
 */
@Test
public class SLGraphTest {
	
	/** The Constant LOGGER. */
	static final Logger LOGGER = Logger.getLogger(SLGraphTest.class);
	
	/** The graph. */
	private SLGraph graph;
	
	/** The session. */
	private SLGraphSession session;
	
	/** The java class node. */
	private JavaClassNode javaClassNode;
	
	/** The java method node. */
	private JavaMethodNode javaMethodNode;
	
	/** The link ab. */
	private SLLink linkAB;
	
	/** The link ba. */
	private SLLink linkBA;
	
	/** The link both. */
	private SLLink linkBoth;
	
	/**
	 * Inits the.
	 * 
	 * @throws AbstractFactoryException the abstract factory exception
	 */
	@BeforeClass
	public void init() throws AbstractFactoryException {
		SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
		graph = factory.createGraph();
	}
	
	/**
	 * Finish.
	 */
	@AfterClass
	public void finish() {
		graph.shutdown();
	}
	
	/**
	 * Before test.
	 * 
	 * @throws SLGraphException the SL graph exception
	 */
	@BeforeMethod
	public void beforeTest() throws SLGraphException {
		if (session == null) session = graph.openSession();
	}
	
	/**
	 * After test.
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	@AfterMethod
	public void afterTest() throws SLGraphSessionException {
		session.clear();
		
		//session.save();
		//session.close();
	}

	/**
	 * Test context operations.
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
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
	
	/**
	 * Test integer property.
	 */
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
	
	/**
	 * Test long property.
	 */
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
	
	/**
	 * Test float property.
	 */
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
	
	/**
	 * Test double property.
	 */
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
	
	/**
	 * Test boolean property.
	 */
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

	/**
	 * Test string property.
	 */
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
	
	/**
	 * Test any serializable property.
	 */
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
	
	/**
	 * Test get property as string.
	 */
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
	
	/**
	 * Test property value overwriting.
	 */
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
	
	/**
	 * Test property removal.
	 */
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
	
	/**
	 * Test properties retrieval.
	 */
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
	
	/**
	 * Test node operations.
	 */
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
	
	/**
	 * Test add node type hierarchy case.
	 */
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
			
			// add two types of different hierarchies ...
			JavaElementNode javaElementNode3 = root.addNode(JavaElementNode.class, "node3");
			CobolElementNode cobolElementNode3 = root.addNode(CobolElementNode.class, "node3");
			Assert.assertEquals(javaElementNode3.getName(), "node3");
			Assert.assertEquals(cobolElementNode3.getName(), "node3");
			Assert.assertNotSame(javaElementNode3, cobolElementNode3);
			
		}
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}
	
	/**
	 * Test typed node operations.
	 */
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
	
	/**
	 * Test chi ld nodes retrieval.
	 */
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
	
	/**
	 * Test add simple link empty case.
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
	 * Test add simple link existent ab case.
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
	 * Test add simple link existent ba case.
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
	 * Test add simple link existent both case.
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
	 * Test add simple link empty case actb.
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
	 * Test add simple link existent ab case actb.
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
	 * Test add simple link existent ba case actb.
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
	 * Test add simple link existent both case actb.
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
	 * Adds the add multiple link empty case.
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
	 * Adds the add multiple link existent ab case.
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
	 * Adds the add multiple link existent ba case.
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
	 * Adds the add multiple link existent both case.
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

	/**
	 * Test link properties.
	 */
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
	
	/**
	 * Test link property with annotations.
	 */
	@Test
	public void testLinkPropertyWithAnnotations() {
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");

			JavaClassJavaMethodSimpleLink link = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			
			Date creationTime = new Date();
			link.setLinkName("myLink");
			link.setCreationTime(creationTime);
			
			Assert.assertNotNull(link.getLinkName());
			Assert.assertEquals(link.getLinkName(), "myLink");
			Assert.assertNotNull(link.getCreationTime());
			Assert.assertEquals(link.getCreationTime(), creationTime);
		} 
		catch (SLGraphSessionException e) {
			LOGGER.error(e);
			Assert.fail();
		}
	}

	/**
	 * Test get links.
	 */
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
	
	/**
	 * Test get unidirectional links.
	 */
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
	
	/**
	 * Test get unidirectional links by source.
	 */
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

	/**
	 * Test get unidirectional links by target.
	 */
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
	
	/**
	 * Test get bidirectional links.
	 */
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

	/**
	 * Test get bidirectional links by side.
	 */
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
	
	/**
	 * Test transient nodes with annotation.
	 */
	@Test
	public void testTransientNodesWithAnnotation() {
		
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
			Assert.assertNotNull(javaClassNode1);
			javaMethodNode1 = javaClassNode1.getNode(JavaMethodNode.class, "javaMethodNode1");
			Assert.assertNotNull(javaMethodNode1);
			
			Assert.assertNull(javaClassNode1.getNode(TransientNode.class, "transNode1"));
			Assert.assertNull(javaMethodNode1.getNode(TransientNode.class, "transNode2"));
			
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	/**
	 * Test transient nodes without annotation.
	 */
	@Test
	public void testTransientNodesWithoutAnnotation() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1", NORMAL);
			JavaMethodNode javaMethodNode1 = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1", NORMAL);
			
			javaClassNode1.addNode(JavaClassNode.class, "transNode1", TRANSIENT);
			javaMethodNode1.addNode(JavaMethodNode.class, "transNode2", TRANSIENT);
			
			// add transNode1 as NORMAL (not PERSISTENT anymore) ...
			javaClassNode1.addNode(JavaClassNode.class, "transNode1", NORMAL);
			
			session.save();
			session = graph.openSession();
			
			root1 = session.getContext(1L).getRootNode();
			javaClassNode1 = root1.getNode(JavaClassNode.class, "javaClassNode1");
			Assert.assertNotNull(javaClassNode1);
			javaMethodNode1 = javaClassNode1.getNode(JavaMethodNode.class, "javaMethodNode1");
			Assert.assertNotNull(javaMethodNode1);
			Assert.assertNotNull(javaClassNode1.getNode(JavaClassNode.class, "transNode1"));
			Assert.assertNull(javaMethodNode1.getNode(TransientNode.class, "transNode2"));
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	/**
	 * Test transient links with annotations.
	 */
	@Test
	public void testTransientLinksWithAnnotations() {
		
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

	/**
	 * Test transient links without annotations.
	 */
	@Test
	public void testTransientLinksWithoutAnnotations() {
		
		try {

			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = root1.addNode(JavaMethodNode.class, "javaMethodNode1");
			JavaMethodNode javaMethodNode2 = root1.addNode(JavaMethodNode.class, "javaMethodNode2");
			
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false, TRANSIENT);
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode2, false, TRANSIENT);
			
			// make previous transient link persistent now ...
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode2, false, NORMAL);

			session.save();
			session = graph.openSession();
			
			root1 = session.getContext(1L).getRootNode();
			javaClassNode1 = root1.getNode(JavaClassNode.class, "javaClassNode1");
			javaMethodNode1 = root1.getNode(JavaMethodNode.class, "javaMethodNode1");
			javaMethodNode2 = root1.getNode(JavaMethodNode.class, "javaMethodNode2");
			Collection<? extends SLLink> links = session.getLinks(JavaClassJavaMethodSimpleLink.class, javaClassNode1, null);
			Assert.assertEquals(links.size(), 1);
			SLLink link = links.iterator().next();
			Assert.assertEquals(link.getTarget(), javaMethodNode2);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	/**
	 * Test get nodes by link with link type.
	 */
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

	/**
	 * Test get nodes by link without link type.
	 */
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
	
	/**
	 * Test links removal by node deletion.
	 */
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
	
	/**
	 * Test get nodes by predicate.
	 */
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
	
	/**
	 * Test line reference.
	 */
	@Test
	public void testLineReference() {
		
		try {
			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			
			SLLineReference lineRef1 = javaClassNode1.addLineReference(8, 17, 26, 44, "Hello World!", "1", "1");
			SLLineReference lineRef2 = javaClassNode1.addLineReference(71, 80, 35, 53, "Bye World!", "2", "1");
			
			Collection<SLLineReference> lineRefs = javaClassNode1.getLineReferences();
			Assert.assertNotNull(lineRefs);
			Assert.assertEquals(lineRefs.size(), 2);
			
			for (SLLineReference lineRef : lineRefs) {
				if (lineRef.getArtifactId().equals("1")) {
					Assert.assertEquals(lineRef1.getStartLine(), new Integer(8));
					Assert.assertEquals(lineRef1.getEndLine(), new Integer(17));
					Assert.assertEquals(lineRef1.getStartColumn(), new Integer(26));
					Assert.assertEquals(lineRef1.getEndColumn(), new Integer(44));
					Assert.assertEquals(lineRef1.getStatement(), "Hello World!");
					Assert.assertEquals(lineRef1.getArtifactVersion(), "1");
				}
				else if (lineRef.getArtifactId().equals("2")) {
					Assert.assertEquals(lineRef2.getStartLine(), new Integer(71));
					Assert.assertEquals(lineRef2.getEndLine(), new Integer(80));
					Assert.assertEquals(lineRef2.getStartColumn(), new Integer(35));
					Assert.assertEquals(lineRef2.getEndColumn(), new Integer(53));
					Assert.assertEquals(lineRef2.getStatement(), "Bye World!");
					Assert.assertEquals(lineRef2.getArtifactVersion(), "1");
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

	/**
	 * Test get meta node.
	 */
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

	/**
	 * Test get meta nodes.
	 */
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

	/**
	 * Test get child node node.
	 */
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
	
	/**
	 * Test get child meta nodes.
	 */
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

	/**
	 * Test get meta node properties.
	 */
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
	
	/**
	 * Test get meta node property.
	 */
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
	
	/**
	 * Test get meta link properties.
	 */
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

	/**
	 * Test get meta link property.
	 */
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
	
	/**
	 * Test link types for link deletion mark case.
	 */
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

	/**
	 * Test link types for link deletion mark and unmark case.
	 */
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

	/**
	 * Test link types for linked node deletion mark case.
	 */
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

	/**
	 * Test link types for linked node deletion mark and unmark case.
	 */
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
	
	/**
	 * Test get meta render hints.
	 */
	@Test
	public void testGetMetaRenderHints() {
		
		try {
			SLNode root1 = session.createContext(1L).getRootNode();
			root1.addNode(JavaClassNode.class, "javaClassNode1");
			SLMetadata metadata = session.getMetadata();
			SLMetaNode metaNode = metadata.getMetaNode(JavaClassNode.class);
			Collection<SLMetaRenderHint> renderHints = metaNode.getMetaRenderHints();
			Assert.assertEquals(renderHints.size(), 2);
			for (SLMetaRenderHint renderHint : renderHints) {
				if (renderHint.getName().equals("format")) {
					Assert.assertEquals(renderHint.getValue(), "cube");
				}
				else if (renderHint.getName().equals("foreground")) {
					Assert.assertEquals(renderHint.getValue(), "gold");
				}
				else {
					Assert.fail();
				}
			}
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	/**
	 * Test get meta render hint.
	 */
	@Test
	public void testGetMetaRenderHint() {
		
		try {
			
			SLNode root1 = session.createContext(1L).getRootNode();
			root1.addNode(JavaClassNode.class, "javaClassNode1");
			SLMetadata metadata = session.getMetadata();
			SLMetaNode metaNode = metadata.getMetaNode(JavaClassNode.class);
			
			SLMetaRenderHint formatRenderHint = metaNode.getMetaRenderHint("format");
			Assert.assertNotNull(formatRenderHint);
			Assert.assertEquals(formatRenderHint.getName(), "format");
			Assert.assertEquals(formatRenderHint.getValue(), "cube");

			SLMetaRenderHint foregroundRenderHint = metaNode.getMetaRenderHint("foreground");
			Assert.assertNotNull(foregroundRenderHint);
			Assert.assertEquals(foregroundRenderHint.getName(), "foreground");
			Assert.assertEquals(foregroundRenderHint.getValue(), "gold");

		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	/**
	 * Test node collator.
	 */
	@Test
	public void testNodeCollator() {

		try {
			SLNode root1 = session.createContext(1L).getRootNode();
			
			// test addNode ...
			SQLElement element1 = root1.addNode(SQLElement.class, "selecao");
			SQLElement element2 = root1.addNode(SQLElement.class, "seleção");
			Assert.assertEquals(element1, element2);
			
			// test getNode ...
			SQLElement element3 = root1.getNode(SQLElement.class, "seleção");
			Assert.assertEquals(element1, element3);
			
			// the original name remains ...
			Assert.assertEquals(element1.getName(), "selecao");
			Assert.assertEquals(element2.getName(), "selecao");
			Assert.assertEquals(element3.getName(), "selecao");
		} 
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	/**
	 * Test node property collator.
	 */
	@Test
	public void testNodePropertyCollator() {
		
		try {
			
			SLNode root1 = session.createContext(1L).getRootNode();
			SQLElement element = root1.addNode(SQLElement.class, "element");
			
			SLNodeProperty<String> prop1 = element.setProperty(String.class, "selecao", "great");
			SLNodeProperty<String> prop2 = element.getProperty(String.class, "seleção");
			Assert.assertEquals(prop1, prop2);
			Assert.assertEquals(prop1.getName(), "selecao");
			Assert.assertEquals(prop1.getName(), "selecao");
			
			try {
				Collator collator = Collator.getInstance(Locale.US);
				collator.setStrength(Collator.TERTIARY);
				element.getProperty(String.class, "seleção", collator);
				Assert.fail();
			} 
			catch (SLNodePropertyNotFoundException e) {
				Assert.assertTrue(true);
			}
		} 
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	/**
	 * Test link property collator.
	 */
	@Test
	public void testLinkPropertyCollator() {

		try {
			
			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
			JavaMethodNode javaMethodNode1 = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1");
			
			JavaClassJavaMethodSimpleLink link = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);
			
			SLLinkProperty<String> prop1 = link.setProperty(String.class, "selecao", "great");
			SLLinkProperty<String> prop2 = link.getProperty(String.class, "seleção");
			
			Assert.assertEquals(prop1, prop2);
			Assert.assertEquals(prop1.getName(), "selecao");
			Assert.assertEquals(prop1.getName(), "selecao");
			
			try {
				Collator collator = Collator.getInstance(Locale.US);
				collator.setStrength(Collator.TERTIARY);
				link.getProperty(String.class, "seleção", collator);
				Assert.fail();
			} 
			catch (SLNodePropertyNotFoundException e) {
				Assert.assertTrue(true);
			}
		} 
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}
	
	/**
	 * Test meta node get description.
	 */
	@Test
	public void testMetaNodeGetDescription() {
		try {
			SLNode root1 = session.createContext(1L).getRootNode();
			root1.addNode(JavaClassNode.class, "javaClassNode1");
			
			SLMetadata metadata = session.getMetadata();
			SLMetaNode metaNode = metadata.getMetaNode(JavaClassNode.class);
			String description = metaNode.getDescription();
			Assert.assertNotNull(description);
			Assert.assertEquals(description, "Java Class");
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}

	@Test
	public void testMetaLinkGetDescription() {
		try {
			SLNode root1 = session.createContext(1L).getRootNode();
			JavaClassNode javaClassNode = root1.addNode(JavaClassNode.class, "javaClassNode");
			JavaMethodNode javaMethodNode = root1.addNode(JavaMethodNode.class, "javaMethodNode");
			session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode, javaMethodNode, false);
			
			SLMetadata metadata = session.getMetadata();
			SLMetaLink metaLink = metadata.getMetaLinkType(JavaClassJavaMethodSimpleLink.class).getMetalinks().iterator().next();
			String description = metaLink.getDescription();
			Assert.assertNotNull(description);
			Assert.assertEquals(description, "Java Class to Java Method Link");
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail();
		}
	}


	/**
	 * Assert simple link.
	 * 
	 * @param link the link
	 * @param linkClass the link class
	 * @param source the source
	 * @param target the target
	 * @param bidirecional the bidirecional
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
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

	/**
	 * Sets the up empty link scenario.
	 */
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
	
	/**
	 * Sets the up existent ab link scenario.
	 * 
	 * @param linkClass the new up existent ab link scenario
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void setUpExistentABLinkScenario(Class<? extends SLLink> linkClass) throws SLGraphSessionException {
		session.clear();
		SLNode root = session.createContext(1L).getRootNode();
		javaClassNode = root.addNode(JavaClassNode.class, "javaClassNode");
		javaMethodNode = root.addNode(JavaMethodNode.class, "javaMethodNode");

		// empty         --> add AB   --> add AB
		linkAB = session.addLink(linkClass, javaClassNode, javaMethodNode, false);
	}
	
	/**
	 * Sets the up existent ba link scenario.
	 * 
	 * @param linkClass the new up existent ba link scenario
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void setUpExistentBALinkScenario(Class<? extends SLLink> linkClass) throws SLGraphSessionException {
		session.clear();
		SLNode root = session.createContext(1L).getRootNode();
		javaClassNode = root.addNode(JavaClassNode.class, "javaClassNode");
		javaMethodNode = root.addNode(JavaMethodNode.class, "javaMethodNode");

		// empty         --> add BA   --> add BA
		linkBA = session.addLink(linkClass, javaMethodNode, javaClassNode, false);
	}
	
	/**
	 * Sets the up existent both link scenario.
	 * 
	 * @param linkClass the new up existent both link scenario
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void setUpExistentBothLinkScenario(Class<? extends SLLink> linkClass) throws SLGraphSessionException {
		session.clear();
		SLNode root = session.createContext(1L).getRootNode();
		javaClassNode = root.addNode(JavaClassNode.class, "javaClassNode");
		javaMethodNode = root.addNode(JavaMethodNode.class, "javaMethodNode");

		// empty         --> add BOTH --> add BOTH
		linkBoth = session.addLink(linkClass, javaClassNode, javaMethodNode, true);
	}
	
	/**
	 * Assert links.
	 * 
	 * @param links the links
	 * @param expectedLinks the expected links
	 */
	private void assertLinks(Collection<? extends SLLink> links, SLLink...expectedLinks) {
		Assert.assertNotNull(links);
		Assert.assertEquals(links.size(), expectedLinks.length);
		Set<SLLink> linkSet = new TreeSet<SLLink>(links);
		Set<SLLink> expectedLinkSet = new TreeSet<SLLink>(Arrays.asList(expectedLinks));
		Assert.assertEquals(linkSet, expectedLinkSet);
	}
	
	/**
	 * Assert links in order.
	 * 
	 * @param links the links
	 * @param expectedLinks the expected links
	 */
	private void assertLinksInOrder(Collection<? extends SLLink> links, SLLink...expectedLinks) {
		Assert.assertNotNull(links);
		Assert.assertEquals(links.size(), expectedLinks.length);
		Iterator<? extends SLLink> iter = links.iterator();
		for (int i = 0; i < expectedLinks.length; i++) {
			Assert.assertEquals(expectedLinks[i], iter.next());
		}
	}

	/**
	 * Assert nodes.
	 * 
	 * @param nodes the nodes
	 * @param expectedNodes the expected nodes
	 */
	private void assertNodes(Collection<? extends SLNode> nodes, SLNode...expectedNodes) {
		Assert.assertNotNull(nodes);
		Assert.assertEquals(nodes.size(), expectedNodes.length);
		Set<SLNode> nodeSet = new TreeSet<SLNode>(nodes);
		Set<SLNode> expectedNodeSet = new TreeSet<SLNode>(Arrays.asList(expectedNodes));
		Assert.assertEquals(nodeSet, expectedNodeSet);
	}
	
	/**
	 * Assert meta nodes.
	 * 
	 * @param metaNodes the meta nodes
	 * @param expectedNodeTypes the expected node types
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void assertMetaNodes(Collection<SLMetaNode> metaNodes, Class<?>...expectedNodeTypes) throws SLGraphSessionException {
		Assert.assertNotNull(metaNodes);
		Assert.assertEquals(metaNodes.size(), expectedNodeTypes.length);
		Set<String> metaNodeTypeNameSet = new TreeSet<String>(getNodeTypeNameSet(metaNodes));
		Set<String> expectedNodeTypeNameSet = new TreeSet<String>(getNodeTypeNameSet(expectedNodeTypes));
		Assert.assertEquals(metaNodeTypeNameSet, expectedNodeTypeNameSet);
	}
	
	/**
	 * Gets the node type name set.
	 * 
	 * @param metaNodes the meta nodes
	 * 
	 * @return the node type name set
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private Set<String> getNodeTypeNameSet(Collection<SLMetaNode> metaNodes) throws SLGraphSessionException {
		Set<String> set = new TreeSet<String>();
		for (SLMetaNode metaNode : metaNodes) {
			set.add(metaNode.getType().getName());
		}
		return set;
	}
	
	/**
	 * Gets the node type name set.
	 * 
	 * @param expectedNodeTypes the expected node types
	 * 
	 * @return the node type name set
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private Set<String> getNodeTypeNameSet(Class<?>[] expectedNodeTypes) throws SLGraphSessionException {
		Set<String> set = new TreeSet<String>();
		for (Class<?> nodeType : expectedNodeTypes) {
			set.add(nodeType.getName());
		}
		return set;
	}
}
