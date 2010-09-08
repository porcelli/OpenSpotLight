/**
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
package org.openspotlight.graph.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.TreeLineReference;
import org.openspotlight.graph.exception.MetaNodeTypeNotFoundException;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.manipulation.GraphWriter;
import org.openspotlight.graph.metadata.MetaLinkType;
import org.openspotlight.graph.metadata.MetaNodeType;
import org.openspotlight.graph.metadata.MetaRenderHint;
import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.graph.test.domain.link.JavaClassJavaMethodMultipleLink;
import org.openspotlight.graph.test.domain.link.JavaClassJavaMethodSimpleLink;
import org.openspotlight.graph.test.domain.link.JavaClassJavaMethodSimpleLinkACTB;
import org.openspotlight.graph.test.domain.link.JavaClassJavaMethodSimpleLinkPrivate;
import org.openspotlight.graph.test.domain.link.JavaLink;
import org.openspotlight.graph.test.domain.link.JavaPackageJavaClass;
import org.openspotlight.graph.test.domain.link.JavaPackageNode;
import org.openspotlight.graph.test.domain.link.JavaPackagePublicElement;
import org.openspotlight.graph.test.domain.node.CobolElementNode;
import org.openspotlight.graph.test.domain.node.JavaClassNode;
import org.openspotlight.graph.test.domain.node.JavaClassNodeInternal;
import org.openspotlight.graph.test.domain.node.JavaClassNodePublic;
import org.openspotlight.graph.test.domain.node.JavaElementNode;
import org.openspotlight.graph.test.domain.node.JavaInnerClassNode;
import org.openspotlight.graph.test.domain.node.JavaMethodNode;
import org.openspotlight.graph.test.domain.node.JavaPackageNodePrivate;
import org.openspotlight.graph.test.domain.node.JavaType;

import com.google.inject.internal.BytecodeGen.Visibility;

public abstract class BaseGraphTest {

	private JavaClassNode javaClassNode;
	/**
	 * The java method node.
	 */
	private JavaMethodNode javaMethodNode;
	/**
	 * The link ab.
	 */
	private Link linkAB;
	/**
	 * The link ba.
	 */
	private Link linkBA;
	/**
	 * The link both.
	 */
	private Link linkBoth;

	protected void clearSession() {
	}

	/**
	 * After test.
	 */
	@After
	public void afterTest() {
		clearSession();
	}

	private GraphWriter session;

	private GraphReader reader;

	/**
	 * Adds the add multiple link empty case.
	 */
	@Test
	public void addAddMultipleLinkEmptyCase() {
		// empty --> add AB --> add AB
		setUpEmptyLinkScenario();

		final Link linkAB = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodMultipleLink.class,
				javaClassNode, javaMethodNode);

		// empty --> add BA --> add BA
		setUpEmptyLinkScenario();

		final Link linkBA = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodMultipleLink.class,
				javaMethodNode, javaClassNode);

		// empty --> add BOTH --> add BOTH
		setUpEmptyLinkScenario();

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkBoth, JavaClassJavaMethodMultipleLink.class,
				javaClassNode, javaMethodNode, true);
	}

	/**
	 * Adds the add multiple link existent ba case.
	 */
	@Test
	public void addAddMultipleLinkExistentBACase() {
		// existent BA --> add BA --> add NEW BA
		setUpExistentBALinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkAB = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodMultipleLink.class,
				javaClassNode, javaMethodNode);
		Assert.assertNotSame(linkAB, linkBA);

		// existent BA --> add AB --> add AB
		setUpExistentBALinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkBA = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodMultipleLink.class,
				javaMethodNode, javaClassNode);
		Assert.assertNotSame(linkBA, this.linkBA);

		// existent BA --> add BOTH --> add BOTH
		setUpExistentBALinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkBoth, JavaClassJavaMethodMultipleLink.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertNotSame(linkBoth, this.linkBA);
	}

	/**
	 * Adds the add multiple link existent ab case.
	 */
	@Test
	public void addAddMultipleLinkExistentABCase() {
		// existent AB --> add AB --> add NEW AB
		setUpExistentABLinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkAB = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodMultipleLink.class,
				javaClassNode, javaMethodNode);
		Assert.assertNotSame(linkAB, this.linkAB);

		// existent AB --> add BA --> add BA
		setUpExistentABLinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkBA = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodMultipleLink.class,
				javaMethodNode, javaClassNode);
		Assert.assertNotSame(linkBA, this.linkAB);

		// existent AB --> add BOTH --> add BOTH
		setUpExistentABLinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkBoth, JavaClassJavaMethodMultipleLink.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertNotSame(linkBoth, this.linkAB);
	}

	/**
	 * Adds the add multiple link existent both case.
	 */
	@Test
	public void addAddMultipleLinkExistentBothCase() {
		// existent BOTH --> add AB --> add AB
		setUpExistentBothLinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkAB = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodMultipleLink.class,
				javaClassNode, javaMethodNode);
		Assert.assertNotSame(linkAB, linkBoth);

		// existent BOTH --> add BA --> add BA
		setUpExistentBothLinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkBA = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodMultipleLink.class,
				javaMethodNode, javaClassNode);
		Assert.assertNotSame(linkBA, linkBoth);

		// existent BOTH --> add BOTH --> add NEW BOTH
		setUpExistentBothLinkScenario(JavaClassJavaMethodMultipleLink.class);

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkBoth, JavaClassJavaMethodMultipleLink.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertNotSame(linkBoth, this.linkBoth);
	}

	/**
	 * Assert links.
	 * 
	 * @param links
	 *            the links
	 * @param expectedLinks
	 *            the expected links
	 */
	private void assertLinks(final Collection<? extends Link> links,
			final Link... expectedLinks) {
		Assert.assertNotNull(links);
		Assert.assertEquals(links.size(), expectedLinks.length);
		final Set<Link> linkSet = new TreeSet<Link>(links);
		final Set<Link> expectedLinkSet = new TreeSet<Link>(
				Arrays.asList(expectedLinks));
		Assert.assertEquals(linkSet, expectedLinkSet);
	}

	/**
	 * Assert links in order.
	 * 
	 * @param links
	 *            the links
	 * @param expectedLinks
	 *            the expected links
	 */
	private void assertLinksInOrder(final Collection<? extends Link> links,
			final Link... expectedLinks) {
		Assert.assertNotNull(links);
		Assert.assertEquals(links.size(), expectedLinks.length);
		final Iterator<? extends Link> iter = links.iterator();
		for (final Link expectedLink : expectedLinks) {
			Assert.assertEquals(expectedLink, iter.next());
		}
	}

	/**
	 * Assert meta nodes.
	 * 
	 * @param metaNodes
	 *            the meta nodes
	 * @param expectedNodeTypes
	 *            the expected node types
	 */
	private void assertMetaNodes(final Collection<MetaNodeType> metaNodes,
			final Class<?>... expectedNodeTypes) {
		Assert.assertNotNull(metaNodes);
		Assert.assertEquals(metaNodes.size(), expectedNodeTypes.length);
		final Set<String> metaNodeTypeNameSet = new TreeSet<String>(
				this.getNodeTypeNameSet(metaNodes));
		final Set<String> expectedNodeTypeNameSet = new TreeSet<String>(
				this.getNodeTypeNameSet(expectedNodeTypes));
		Assert.assertEquals(metaNodeTypeNameSet, expectedNodeTypeNameSet);
	}

	/**
	 * Assert nodes.
	 * 
	 * @param nodes
	 *            the nodes
	 * @param expectedNodes
	 *            the expected nodes
	 */
	private void assertNodes(final Collection<? extends Node> nodes,
			final Node... expectedNodes) {
		Assert.assertNotNull(nodes);
		Assert.assertEquals(nodes.size(), expectedNodes.length);
		final Set<Node> nodeSet = new TreeSet<Node>(nodes);
		final Set<Node> expectedNodeSet = new TreeSet<Node>(
				Arrays.asList(expectedNodes));
		Assert.assertEquals(nodeSet, expectedNodeSet);
	}

	/**
	 * Assert simple link.
	 * 
	 * @param link
	 *            the link
	 * @param linkClass
	 *            the link class
	 * @param source
	 *            the source
	 * @param target
	 *            the target
	 * @param bidirecional
	 *            the bidirecional
	 */

	private <T extends Link> void assertSimpleLink(final Link link,
			final Class<T> linkClass, final Node source, final Node target,
			final boolean bidirecional) {
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

	private <T extends Link> void assertSimpleLink(final Link link,
			final Class<T> linkClass, final Node source, final Node target) {
		this.<T> assertSimpleLink(link, linkClass, source, target);
	}

	/**
	 * Gets the node type name set.
	 * 
	 * @param expectedNodeTypes
	 *            the expected node types
	 * @return the node type name set
	 */
	private Set<String> getNodeTypeNameSet(final Class<?>[] expectedNodeTypes) {
		final Set<String> set = new TreeSet<String>();
		for (final Class<?> nodeType : expectedNodeTypes) {
			set.add(nodeType.getName());
		}
		return set;
	}

	/**
	 * Gets the node type name set.
	 * 
	 * @param metaNodes
	 *            the meta nodes
	 * @return the node type name set
	 */
	private Set<String> getNodeTypeNameSet(
			final Collection<MetaNodeType> metaNodes) {
		final Set<String> set = new TreeSet<String>();
		for (final MetaNodeType metaNode : metaNodes) {
			set.add(metaNode.getTypeName());
		}
		return set;
	}

	/**
	 * Sets the up empty link scenario.
	 */
	private void setUpEmptyLinkScenario() {
		clearSession();

		final Context root = reader.getContext("1L");
		javaClassNode = session.addNode(root, JavaClassNode.class,
				"javaClassNode");
		javaMethodNode = session.addNode(root, JavaMethodNode.class,
				"javaMethodNode");
	}

	/**
	 * Sets the up existent ab link scenario.
	 * 
	 * @param linkClass
	 *            the new up existent ab link scenario
	 */
	private void setUpExistentABLinkScenario(
			final Class<? extends Link> linkClass) {
		clearSession();
		final Context root = reader.getContext("1L");
		javaClassNode = session.addNode(root, JavaClassNode.class,
				"javaClassNode");
		javaMethodNode = session.addNode(root, JavaMethodNode.class,
				"javaMethodNode");

		// empty --> add AB --> add AB
		linkAB = session.addLink(linkClass, javaClassNode, javaMethodNode);
	}

	/**
	 * Sets the up existent ba link scenario.
	 * 
	 * @param linkClass
	 *            the new up existent ba link scenario
	 */
	private void setUpExistentBALinkScenario(
			final Class<? extends Link> linkClass) {
		clearSession();
		final Context root = reader.getContext("1L");
		javaClassNode = session.addNode(root, JavaClassNode.class,
				"javaClassNode");
		javaMethodNode = session.addNode(root, JavaMethodNode.class,
				"javaMethodNode");

		// empty --> add BA --> add BA
		linkBA = session.addLink(linkClass, javaMethodNode, javaClassNode);
	}

	/**
	 * Sets the up existent both link scenario.
	 * 
	 * @param linkClass
	 *            the new up existent both link scenario
	 */
	private void setUpExistentBothLinkScenario(
			final Class<? extends Link> linkClass) {
		clearSession();
		final Context root = reader.getContext("1L");
		javaClassNode = session.addNode(root, JavaClassNode.class,
				"javaClassNode");
		javaMethodNode = session.addNode(root, JavaMethodNode.class,
				"javaMethodNode");

		// empty --> add BOTH --> add BOTH
		linkBoth = session.addBidirectionalLink(linkClass, javaClassNode,
				javaMethodNode);
	}

	/**
	 * Test add and get node with strange chars on name.
	 */
	@Test
	public void testAddAndGetNodeWithStrangeCharsOnName() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = session.addNode(root1,
				JavaClassNode.class, "/home/feuteston/accept-strange-chars.sh");
		Assert.assertNotNull(javaClassNode1);
		final JavaClassNode javaClassNode2 = reader.getChildNode(
				javaClassNode1, JavaClassNode.class,
				"/home/feuteston/accept-strange-chars.sh");
		Assert.assertNotNull(javaClassNode2);
		Assert.assertEquals(javaClassNode1, javaClassNode2);
	}

	/**
	 * Test add node type hierarchy case.
	 */
	@Test
	public void testAddNodeTypeHierarchyCase() {
		final Context root = reader.getContext("1L");

		// add sub type, then add super type; sub type is supposed to be
		// kept ...
		final JavaClassNode javaClassNode1 = session.addNode(root,
				JavaClassNode.class, "node1");
		final JavaElementNode javaElementNode1 = session.addNode(root,
				JavaElementNode.class, "node1");
		Assert.assertEquals(javaClassNode1, javaElementNode1);
		Assert.assertTrue(javaElementNode1 instanceof JavaClassNode);

		// add super type, then add sub type; sub type is supposed to
		// overwrite ...
		final JavaElementNode javaElementNode2 = session.addNode(root,
				JavaElementNode.class, "node2");
		final JavaClassNode javaClassNode2 = session.addNode(root,
				JavaClassNode.class, "node2");
		Assert.assertEquals(javaClassNode2, javaElementNode2);

		// add two types of different hierarchies ...
		final JavaElementNode javaElementNode3 = session.addNode(root,
				JavaElementNode.class, "node3");
		final CobolElementNode cobolElementNode3 = session.addNode(root,
				CobolElementNode.class, "node3");
		Assert.assertEquals(javaElementNode3.getName(), "node3");
		Assert.assertEquals(cobolElementNode3.getName(), "node3");
		Assert.assertNotSame(javaElementNode3, cobolElementNode3);
	}

	/**
	 * Test add simple link empty case.
	 */
	@Test
	public void testAddSimpleLinkEmptyCase() {
		// empty --> add AB --> add AB
		setUpEmptyLinkScenario();

		final Link linkAB = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLink.class,
				javaClassNode, javaMethodNode);

		// empty --> add BA --> add BA
		setUpEmptyLinkScenario();

		final Link linkBA = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLink.class,
				javaMethodNode, javaClassNode);

		// empty --> add BOTH --> add BOTH
		setUpEmptyLinkScenario();

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLink.class,
				javaClassNode, javaMethodNode, true);
	}

	/**
	 * Test add simple link empty case actb.
	 */
	@Test
	public void testAddSimpleLinkEmptyCaseACTB() {
		// empty --> add AB --> add AB
		setUpEmptyLinkScenario();

		final Link linkAB = session.addLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLinkACTB.class,
				javaClassNode, javaMethodNode);

		// empty --> add BA --> add BA
		setUpEmptyLinkScenario();

		final Link linkBA = session.addLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLinkACTB.class,
				javaMethodNode, javaClassNode);

		// empty --> add BOTH --> add BOTH
		setUpEmptyLinkScenario();

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode,
				javaMethodNode);
		assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLinkACTB.class,
				javaClassNode, javaMethodNode, true);
	}

	/**
	 * Test add simple link existent ab case.
	 */
	@Test
	public void testAddSimpleLinkExistentABCase() {
		// existent AB --> add AB --> remains AB
		setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLink.class);

		final Link linkAB = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLink.class,
				javaClassNode, javaMethodNode);
		Assert.assertEquals(linkAB, this.linkAB);

		// existent AB --> add BA --> add BA
		setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLink.class);

		final Link linkBA = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLink.class,
				javaMethodNode, javaClassNode);
		Assert.assertNotSame(linkBA, this.linkAB);

		// existent AB --> add BOTH --> add BOTH
		setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLink.class);

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLink.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertNotSame(linkBoth, this.linkAB);
	}

	/**
	 * Test add simple link existent ab case actb.
	 */
	@Test
	public void testAddSimpleLinkExistentABCaseACTB() {
		// existent AB --> add AB --> remains AB
		setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);

		final Link linkAB = session.addLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLinkACTB.class,
				javaClassNode, javaMethodNode);
		Assert.assertEquals(linkAB, this.linkAB);

		// existent AB --> add BA --> changes to BOTH
		setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);

		final Link linkBA = session.addLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLinkACTB.class,
				javaMethodNode, javaClassNode, true);
		Assert.assertEquals(linkBA, this.linkAB);

		// existent AB --> add BOTH --> changes to BOTH
		setUpExistentABLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode,
				javaMethodNode);
		assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLinkACTB.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertEquals(linkBoth, this.linkAB);
	}

	/**
	 * Test add simple link existent ba case.
	 */
	@Test
	public void testAddSimpleLinkExistentBACase() {
		// existent BA --> add AB --> add AB
		setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLink.class);

		final Link linkAB = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLink.class,
				javaClassNode, javaMethodNode);
		Assert.assertNotSame(linkAB, linkBA);

		// existent BA --> add BA --> remains BA
		setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLink.class);

		final Link linkBA = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLink.class,
				javaMethodNode, javaClassNode);
		Assert.assertEquals(linkBA, this.linkBA);

		// existent BA --> add BOTH --> add BOTH
		setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLink.class);
		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLink.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertNotSame(linkBoth, this.linkBA);
	}

	/**
	 * Test add simple link existent ba case actb.
	 */
	@Test
	public void testAddSimpleLinkExistentBACaseACTB() {
		// existent BA --> add AB --> changes to BOTH
		setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
		final Link linkAB = session.addLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLinkACTB.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertEquals(linkAB, linkBA);

		// existent BA --> add BA --> remains BA
		setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
		final Link linkBA = session.addLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode,
				javaClassNode);
		assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLinkACTB.class,
				javaMethodNode, javaClassNode);
		Assert.assertEquals(linkBA, this.linkBA);

		// existent BA --> add BOTH --> changes to BOTH
		setUpExistentBALinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);

		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode,
				javaMethodNode);
		assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLinkACTB.class,
				javaClassNode, javaMethodNode, true);

		Assert.assertEquals(linkBoth, this.linkBA);
	}

	/**
	 * Test add simple link existent both case.
	 */
	@Test
	public void testAddSimpleLinkExistentBothCase() {
		// existent BOTH --> add AB --> add AB
		setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLink.class);

		final Link linkAB = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);

		assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLink.class,
				javaClassNode, javaMethodNode);
		Assert.assertNotSame(linkAB, linkBoth);

		// existent BOTH --> add BA --> add BA
		setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLink.class);

		final Link linkBA = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode,
				javaClassNode);

		assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLink.class,
				javaMethodNode, javaClassNode);
		Assert.assertNotSame(linkBA, linkBoth);

		// existent BOTH --> add BOTH --> remains BOTH
		setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLink.class);
		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);
		assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLink.class,
				javaClassNode, javaMethodNode, true);

		Assert.assertEquals(linkBoth, this.linkBoth);
	}

	/**
	 * Test add simple link existent both case actb.
	 */
	@Test
	public void testAddSimpleLinkExistentBothCaseACTB() {
		// existent BOTH --> add AB --> remains BOTH
		setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);

		final Link linkAB = session.addLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode,
				javaMethodNode);
		assertSimpleLink(linkAB, JavaClassJavaMethodSimpleLinkACTB.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertEquals(linkAB, linkBoth);

		// existent BOTH --> add BA --> remains BOTH
		setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
		final Link linkBA = session.addLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaMethodNode,
				javaClassNode);
		assertSimpleLink(linkBA, JavaClassJavaMethodSimpleLinkACTB.class,
				javaMethodNode, javaClassNode, true);
		Assert.assertEquals(linkBA, linkBoth);

		// existent BOTH --> add BOTH --> remains BOTH
		setUpExistentBothLinkScenario(JavaClassJavaMethodSimpleLinkACTB.class);
		final Link linkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLinkACTB.class, javaClassNode,
				javaMethodNode);
		assertSimpleLink(linkBoth, JavaClassJavaMethodSimpleLinkACTB.class,
				javaClassNode, javaMethodNode, true);
		Assert.assertEquals(linkBoth, this.linkBoth);
	}

	/**
	 * Test chi ld nodes retrieval.
	 */
	@Test
	public void testChiLdNodesRetrieval() {
		final Context root = reader.getContext("1L");
		final JavaType node1 = session.addNode(root, JavaType.class, "node1");
		final JavaType node2 = session.addNode(root, JavaType.class, "node2");
		final Iterable<Node> ch1LdNodes = reader.getChildrenNodes(root, null);
		Assert.assertNotNull(ch1LdNodes);
		final Iterator<Node> iter = ch1LdNodes.iterator();
		while (iter.hasNext()) {
			final Node current = iter.next();
			Assert.assertTrue(current.getName().equals("node1")
					|| current.getName().equals("node2"));
			Assert.assertTrue(current.equals(node1) || current.equals(node2));
		}
	}

	/**
	 * Test context operations.
	 */
	@Test
	public void testContextOperations() {
		final Context context1 = reader.getContext("1L");
		Assert.assertNotNull("context1 should not be null.", context1);
		final Context context2 = reader.getContext("1L");
		Assert.assertNotNull("context2 should not be null.", context2);
		final String id1 = context1.getId();
		final String id2 = context2.getId();
		Assert.assertNotNull(id1);
		Assert.assertNotNull(id2);
		Assert.assertEquals(id1, id2);
		Assert.assertEquals(context1, context2);
	}

	/**
	 * Test get bidirectional links.
	 */
	@Test
	public void testGetBidirectionalLinks() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = session.addNode(root1,
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = session.addNode(root1,
				JavaMethodNode.class, "javaMethodNode1");

		final Link simpleLinkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		final Link multipleLinkBoth = session.addBidirectionalLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1,
				javaMethodNode1);
		session.flush();

		Collection<Link> links = null;
		Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
		Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;

		simpleLinks = session.getBidirectionalLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);

		multipleLinks = session.getBidirectionalLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1,
				javaMethodNode1);
		assertLinksInOrder(multipleLinks, multipleLinkBoth);

		links = session.getBidirectionalLinks(javaClassNode1, javaMethodNode1);
		assertLinks(links, simpleLinkBoth, multipleLinkBoth);
	}

	/**
	 * Test get bidirectional links by side.
	 */
	@Test
	public void testGetBidirectionalLinksBySide() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		final Link simpleLinkBoth = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1, true);
		final Link multipleLinkBoth = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1,
				javaMethodNode1, true);
		session.save();

		Collection<Link> links = null;
		Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
		Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;

		simpleLinks = session.getBidirectionalLinksBySide(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);
		simpleLinks = session.getBidirectionalLinksBySide(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode1);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);

		multipleLinks = session.getBidirectionalLinksBySide(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1);
		assertLinksInOrder(multipleLinks, multipleLinkBoth);
		multipleLinks = session.getBidirectionalLinksBySide(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode1);
		assertLinksInOrder(multipleLinks, multipleLinkBoth);

		links = session.getBidirectionalLinksBySide(javaClassNode1);
		assertLinks(links, simpleLinkBoth, multipleLinkBoth);
		links = session.getBidirectionalLinksBySide(javaMethodNode1);
		assertLinks(links, simpleLinkBoth, multipleLinkBoth);
	}

	/**
	 * Test get links.
	 */
	@Test
	public void testGetLinks() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		final Link simpleLinkAB = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		final Link simpleLinkBA = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode1,
				javaClassNode1);
		final Link simpleLinkBoth = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1, true);

		Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;

		// direction filter:
		// DIRECTION_UNI: AB
		// DIRECTION_UNI_REVERSAL: BA
		// DIRECTION_BI: BOTH
		// DIRECTION_UNI | DIRECTION_UNI_REVERSAL: AB, BA
		// DIRECTION_UNI | DIRECTION_BI: AB, BOTH
		// DIRECTION_UNI_REVERSAL | DIRECTION_BI: BA, BOTH
		// DIRECTION_UNI | DIRECTION_UNI_REVERSAL | DIRECTION_BI: AB, BA,
		// BOTH
		// DIRECTION_ANY: AB, BA, BOTH

		// test getLink between javaClassNode1 and javaMethodNode1
		session.save();

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, javaMethodNode1, DIRECTION_UNI);
		assertLinksInOrder(simpleLinks, simpleLinkAB);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, javaClassNode1, DIRECTION_UNI);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, javaMethodNode1, DIRECTION_UNI_REVERSAL);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, javaClassNode1, DIRECTION_UNI_REVERSAL);
		assertLinksInOrder(simpleLinks, simpleLinkAB);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, javaMethodNode1, DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, javaClassNode1, DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, javaMethodNode1, DIRECTION_UNI | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, javaClassNode1, DIRECTION_UNI | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, javaMethodNode1, DIRECTION_UNI_REVERSAL
						| DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, javaClassNode1, DIRECTION_UNI_REVERSAL
						| DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, javaMethodNode1, DIRECTION_ANY);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA,
				simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, javaClassNode1, DIRECTION_ANY);

		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA,
				simpleLinkBoth);

		// test getLink between javaClassNode1 and *

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, null, DIRECTION_UNI);
		assertLinksInOrder(simpleLinks, simpleLinkAB);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaClassNode1, DIRECTION_UNI);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, null, DIRECTION_UNI_REVERSAL);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaClassNode1, DIRECTION_UNI_REVERSAL);
		assertLinksInOrder(simpleLinks, simpleLinkAB);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, null, DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaClassNode1, DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, null, DIRECTION_UNI | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaClassNode1, DIRECTION_UNI | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, null, DIRECTION_UNI_REVERSAL | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaClassNode1, DIRECTION_UNI_REVERSAL | DIRECTION_BI);

		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, null, DIRECTION_ANY);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA,
				simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaClassNode1, DIRECTION_ANY);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA,
				simpleLinkBoth);

		// test getLink between javaMethodNode1 and *

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaMethodNode1, DIRECTION_UNI);
		assertLinksInOrder(simpleLinks, simpleLinkAB);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, null, DIRECTION_UNI);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaMethodNode1, DIRECTION_UNI_REVERSAL);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, null, DIRECTION_UNI_REVERSAL);

		assertLinksInOrder(simpleLinks, simpleLinkAB);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaMethodNode1, DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, null, DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaMethodNode1, DIRECTION_UNI | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, null, DIRECTION_UNI | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaMethodNode1, DIRECTION_UNI_REVERSAL | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBA, simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, null, DIRECTION_UNI_REVERSAL | DIRECTION_BI);

		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, javaMethodNode1, DIRECTION_ANY);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA,
				simpleLinkBoth);
		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				javaMethodNode1, null, DIRECTION_ANY);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA,
				simpleLinkBoth);

		// test getLink between * and *

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, null, DIRECTION_UNI);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, null, DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkBoth);

		simpleLinks = session.getLinks(JavaClassJavaMethodSimpleLink.class,
				null, null, DIRECTION_UNI | DIRECTION_BI);
		assertLinksInOrder(simpleLinks, simpleLinkAB, simpleLinkBA,
				simpleLinkBoth);
	}

	/**
	 * Test get meta link properties.
	 */
	@Test
	public void testGetMetaLinkProperties()
			throws MetaLinkTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		final Link link = session.addLink(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, javaMethodNode1);
		link.setProperty(String.class, Visibility.VisibilityLevel.PUBLIC,
				"author", "Zé Café");
		link.setProperty(Integer.class, Visibility.VisibilityLevel.PUBLIC,
				"age", 270);
		session.save();
		final Metadata metadata = session.getMetadata();
		final MetaLinkType metaLinkType = metadata
				.getMetaLinkType(JavaClassJavaMethodSimpleLink.class);
		final MetaLink metaLink = metaLinkType.getMetalinks().iterator().next();

		final MetaLinkProperty authorMetaProperty = metaLink
				.getMetaProperty("author");
		Assert.assertNotNull(authorMetaProperty);
		Assert.assertEquals(authorMetaProperty.getName(), "author");
		Assert.assertEquals(authorMetaProperty.getType(), String.class);

		final MetaLinkProperty ageMetaProperty = metaLink
				.getMetaProperty("age");
		Assert.assertNotNull(ageMetaProperty);
		Assert.assertEquals(ageMetaProperty.getName(), "age");
		Assert.assertEquals(ageMetaProperty.getType(), Integer.class);
	}

	/**
	 * Test get meta link property.
	 */
	@Test
	public void testGetMetaLinkProperty() throws MetaLinkTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		final Link link = session.addLink(JavaClassJavaMethodSimpleLink.class,
				javaClassNode1, javaMethodNode1);
		link.setProperty(String.class, Visibility.VisibilityLevel.PUBLIC,
				"author", "Zé Café");
		link.setProperty(Integer.class, Visibility.VisibilityLevel.PUBLIC,
				"age", 270);
		session.save();
		final Metadata metadata = session.getMetadata();
		final MetaLinkType metaLinkType = metadata
				.getMetaLinkType(JavaClassJavaMethodSimpleLink.class);
		final MetaLink metaLink = metaLinkType.getMetalinks().iterator().next();

		final Collection<SLMetaLinkProperty> metaProperties = metaLink
				.getMetaProperties();
		Assert.assertEquals(metaProperties.size(), 2);

		for (final MetaLinkProperty metaProperty : metaProperties) {
			Assert.assertNotNull(metaProperty.getName());
			if (metaProperty.getName().equals("author")) {
				Assert.assertEquals(metaProperty.getType(), String.class);
			} else if (metaProperty.getName().equals("age")) {
				Assert.assertEquals(metaProperty.getType(), Integer.class);
			} else {
				Assert.fail();
			}
		}
	}

	/**
	 * Test get meta node.
	 */
	@Test
	public void testGetMetaNode() throws MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		root1.addChildNode(JavaPackageNode.class, "javaPackageNode1");
		root1.addChildNode(JavaPackageNode.class, "javaPackageNode2");
		root1.addChildNode(JavaClassNode.class, "javaClassNode1");
		root1.addChildNode(JavaClassNode.class, "javaClassNode2");
		session.save();

		final Metadata metadata = session.getMetadata();

		final MetaNodeType metaNode1 = metadata
				.getMetaNodeType(JavaPackageNode.class);
		Assert.assertNotNull(metaNode1);
		Assert.assertEquals(metaNode1.getType(), JavaPackageNode.class);
		Assert.assertEquals(metaNode1.getVisibility(),
				Visibility.VisibilityLevel.PUBLIC);

		final MetaNodeType metaNode2 = metadata
				.getMetaNodeType(JavaClassNode.class);
		Assert.assertNotNull(metaNode2);
		Assert.assertEquals(metaNode2.getType(), JavaClassNode.class);
		Assert.assertEquals(metaNode2.getVisibility(),
				Visibility.VisibilityLevel.PUBLIC);
	}

	@Test
	public void testGetMetaNodeByVisibility() {
		final Context root1 = reader.getContext("AAAA1L");
		root1.addChildNode(JavaPackageNode.class, "javaPackageNode1");
		root1.addChildNode(JavaPackageNode.class, "javaPackageNode2");
		root1.addChildNode(JavaClassNode.class, "javaClassNode1");
		root1.addChildNode(JavaClassNodePublic.class, "javaClassNode2");
		root1.addChildNode(JavaPackageNodePrivate.class, "javaPackageNode1X");
		root1.addChildNode(JavaPackageNodePrivate.class, "javaPackageNode2X");
		root1.addChildNode(JavaClassNodeInternal.class, "javaClassNode1X");
		root1.addChildNode(JavaClassNodeInternal.class, "javaClassNode2X");
		session.save();

		final Metadata metadata = session.getMetadata();

		final Collection<MetaNodeType> publicMetaNodes = metadata
				.getMetaNodesTypes(SLRecursiveMode.RECURSIVE,
						Visibility.VisibilityLevel.PUBLIC);
		Assert.assertEquals(4, publicMetaNodes.size());
		final Collection<MetaNodeType> publicNotRecursiveMetaNodes = metadata
				.getMetaNodesTypes(RecursiveMode.NOT_RECURSIVE,
						Visibility.VisibilityLevel.PUBLIC);
		Assert.assertEquals(2, publicNotRecursiveMetaNodes.size());
		final Collection<MetaNodeType> privateMetaNodes = metadata
				.getMetaNodesTypes(SLRecursiveMode.NOT_RECURSIVE,
						Visibility.VisibilityLevel.PRIVATE);
		Assert.assertEquals(1, privateMetaNodes.size());
		final Collection<MetaNodeType> internalMetaNodes = metadata
				.getMetaNodesTypes(SLRecursiveMode.NOT_RECURSIVE,
						Visibility.VisibilityLevel.INTERNAL);
		Assert.assertEquals(1, internalMetaNodes.size());
	}

	/**
	 * Test get meta node properties.
	 */
	@Test
	public void testGetMetaNodeProperties()
			throws MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		javaClassNode1.setClassName("HelloWorld");
		javaClassNode1.setModifier(JavaClassNode.MODIFIER_PUBLIC);
		javaClassNode1.setCreationTime(new Date());
		javaClassNode1.setProperty(String.class, "newProperty", "someãCc");

		final Metadata metadata = session.getMetadata();
		final MetaNodeType metaNode = metadata
				.getMetaNodeType(JavaClassNode.class);
		final Collection<SLMetaNodeProperty> metaProperties = metaNode
				.getMetaProperties();
		Assert.assertEquals(metaProperties.size(), 5);

		for (final MetaNodeProperty metaProperty : metaProperties) {
			Assert.assertNotNull(metaProperty.getName());
			if (metaProperty.getName().equals("className")) {
				Assert.assertEquals(metaProperty.getType(), String.class);
			} else if (metaProperty.getName().equals("modifier")) {
				Assert.assertEquals(metaProperty.getType(), Long.class);
			} else if (metaProperty.getName().equals("creationTime")) {
				Assert.assertEquals(metaProperty.getType(), Date.class);
			} else if (metaProperty.getName().equals("caption")) {
				Assert.assertEquals(metaProperty.getType(), String.class);
			} else if (metaProperty.getName().equals("newProperty")) {
				Assert.assertEquals(metaProperty.getType(), String.class);
			} else {
				Assert.fail();
			}
		}

		for (NodeProperty<Serializable> activeProperty : javaClassNode1
				.getProperties()) {
			MetaNodeProperty metaProperty = session.getMetadata()
					.getMetaNodeType(JavaClassNode.class)
					.getMetaProperty(activeProperty.getName());
			// System.out.println(activeProperty.getName() + ":" +
			// activeProperty.getValueAsString() + " -> " + metaProperty);
			Assert.assertNotNull("Property " + activeProperty.getName()
					+ " not found on metadata registry.", metaProperty);
		}
	}

	/**
	 * Test get meta node property.
	 */
	@Test
	public void testGetMetaNodeProperty() throws MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode = root1.addChildNode(
				JavaClassNode.class, "javaClassNode");
		javaClassNode.setClassName("HelloWorld");
		javaClassNode.setModifier(JavaClassNode.MODIFIER_PUBLIC);
		javaClassNode.setCreationTime(new Date());

		final Metadata metadata = session.getMetadata();
		final MetaNodeType metaNode = metadata
				.getMetaNodeType(JavaClassNode.class);

		final MetaNodeProperty classNameMetaProperty = metaNode
				.getMetaProperty("className");
		Assert.assertNotNull(classNameMetaProperty);
		Assert.assertEquals(classNameMetaProperty.getName(), "className");
		Assert.assertEquals(classNameMetaProperty.getType(), String.class);

		final MetaNodeProperty modifierMetaProperty = metaNode
				.getMetaProperty("modifier");
		Assert.assertNotNull(modifierMetaProperty);
		Assert.assertEquals(modifierMetaProperty.getName(), "modifier");
		Assert.assertEquals(modifierMetaProperty.getType(), Long.class);

		final MetaNodeProperty creationTimeMetaProperty = metaNode
				.getMetaProperty("creationTime");
		Assert.assertNotNull(creationTimeMetaProperty);
		Assert.assertEquals(creationTimeMetaProperty.getName(), "creationTime");
		Assert.assertEquals(creationTimeMetaProperty.getType(), Date.class);
	}

	/**
	 * Test get meta nodes.
	 */
	@Test
	public void testGetMetaNodes() {
		final Context root1 = reader.getContext("1L");
		final Node nonTypedNode = root1.addChildNode("XX");

		Assert.assertNull(nonTypedNode.getMetaType());

		root1.addChildNode(JavaPackageNode.class, "javaPackageNode1");
		root1.addChildNode(JavaPackageNode.class, "javaPackageNode2");
		root1.addChildNode(JavaClassNode.class, "javaClassNode1");
		final JavaClassNode createdNode = root1.addChildNode(
				JavaClassNode.class, "javaClassNode2");
		session.save();

		Assert.assertNotNull(createdNode.getMetaType());

		Assert.assertEquals("Java Class", createdNode.getMetaType()
				.getDescription());

		final Metadata metadata = session.getMetadata();
		final Collection<MetaNodeType> metaNodes = metadata
				.getMetaNodesTypes(RECURSIVE);
		assertMetaNodes(metaNodes, JavaElementNode.class,
				JavaPackageNode.class, JavaClassNode.class);
	}

	/**
	 * Test get meta node.
	 */
	@Test
	public void testGetMetaNodeVisibilityPrivate()
			throws MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		root1.addChildNode(JavaPackageNodePrivate.class, "javaPackageNode1");
		root1.addChildNode(JavaPackageNodePrivate.class, "javaPackageNode2");
		root1.addChildNode(JavaClassNodeInternal.class, "javaClassNode1");
		root1.addChildNode(JavaClassNodeInternal.class, "javaClassNode2");
		session.save();
		final Metadata metadata = session.getMetadata();

		final MetaNodeType metaNode1 = metadata
				.getMetaNodeType(JavaPackageNodePrivate.class);
		Assert.assertNotNull(metaNode1);
		Assert.assertEquals(metaNode1.getType(), JavaPackageNodePrivate.class);
		Assert.assertEquals(metaNode1.getVisibility(),
				Visibility.VisibilityLevel.PRIVATE);

		final MetaNodeType metaNode2 = metadata
				.getMetaNodeType(JavaClassNodeInternal.class);
		Assert.assertNotNull(metaNode2);
		Assert.assertEquals(metaNode2.getType(), JavaClassNodeInternal.class);
		Assert.assertEquals(metaNode2.getVisibility(),
				Visibility.VisibilityLevel.INTERNAL);
	}

	/**
	 * Test get meta render hint.
	 */
	@Test
	public void testGetMetaRenderHint() throws RenderHintNotFoundException,
			MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		root1.addChildNode(JavaClassNode.class, "javaClassNode1");
		session.save();

		final Metadata metadata = session.getMetadata();
		final MetaNodeType metaNode = metadata
				.getMetaNodeType(JavaClassNode.class);

		final MetaRenderHint formatRenderHint = metaNode
				.getMetaRenderHint("format");
		Assert.assertNotNull(formatRenderHint);
		Assert.assertEquals(formatRenderHint.getName(), "format");
		Assert.assertEquals(formatRenderHint.getValue(), "cube");

		final MetaRenderHint foregroundRenderHint = metaNode
				.getMetaRenderHint("foreground");
		Assert.assertNotNull(foregroundRenderHint);
		Assert.assertEquals(foregroundRenderHint.getName(), "foreground");
		Assert.assertEquals(foregroundRenderHint.getValue(), "gold");
	}

	/**
	 * Test get meta render hints.
	 */
	@Test
	public void testGetMetaRenderHints() throws MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		root1.addChildNode(JavaClassNode.class, "javaClassNode1");
		session.save();

		final Metadata metadata = session.getMetadata();
		final MetaNodeType metaNode = metadata
				.getMetaNodeType(JavaClassNode.class);
		final Collection<SLMetaRenderHint> renderHints = metaNode
				.getMetaRenderHints();
		Assert.assertEquals(renderHints.size(), 2);
		for (final MetaRenderHint renderHint : renderHints) {
			if (renderHint.getName().equals("format")) {
				Assert.assertEquals(renderHint.getValue(), "cube");
			} else if (renderHint.getName().equals("foreground")) {
				Assert.assertEquals(renderHint.getValue(), "gold");
			} else {
				Assert.fail();
			}
		}
	}

	/**
	 * Test get nodes by link with link type.
	 */
	@Test
	public void testGetNodesByLinkWithLinkType() {
		final Context root1 = reader.getContext("1L");
		final JavaPackageNode javaPackageNode1 = root1.addChildNode(
				JavaPackageNode.class, "javaPackageNode1");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaClassNode javaClassNode2 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode2");
		final JavaInnerClassNode javaInnerClassNode1 = root1.addChildNode(
				JavaInnerClassNode.class, "javaInnerClassNode1");
		final JavaInnerClassNode javaInnerClassNode2 = root1.addChildNode(
				JavaInnerClassNode.class, "javaInnerClassNode2");

		session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
				javaClassNode1);
		session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
				javaClassNode2, true);
		session.addLink(JavaPackageJavaClass.class, javaClassNode1,
				javaPackageNode1);
		session.addLink(JavaPackageJavaClass.class, javaClassNode2,
				javaPackageNode1);

		session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
				javaInnerClassNode1);
		session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
				javaInnerClassNode2, true);
		session.addLink(JavaPackageJavaClass.class, javaInnerClassNode1,
				javaPackageNode1);
		session.addLink(JavaPackageJavaClass.class, javaInnerClassNode2,
				javaPackageNode1);

		Collection<? extends Node> nodes = null;
		session.save();

		nodes = session.getLinkedNodes(JavaPackageJavaClass.class,
				javaPackageNode1);
		assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1,
				javaInnerClassNode2);

		nodes = session.getNodesByLink(JavaPackageJavaClass.class,
				javaPackageNode1, JavaClassNode.class);
		assertNodes(nodes, javaClassNode1, javaClassNode2);

		nodes = session.getNodesByLink(JavaPackageJavaClass.class,
				javaPackageNode1, JavaInnerClassNode.class);
		assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);

		nodes = session.getNodesByLink(JavaPackageJavaClass.class,
				javaPackageNode1, JavaClassNode.class, true);
		assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1,
				javaInnerClassNode2);

		nodes = session.getNodesByLink(JavaPackageJavaClass.class,
				javaPackageNode1, JavaInnerClassNode.class, true);
		assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);

		nodes = session.getNodesByLink(JavaPackageJavaClass.class);
		assertNodes(nodes, javaPackageNode1, javaClassNode1, javaClassNode2,
				javaInnerClassNode1, javaInnerClassNode2);
	}

	/**
	 * Test get nodes by link without link type.
	 */
	@Test
	public void testGetNodesByLinkWithoutLinkType() {
		final Context root1 = reader.getContext("1L");
		final JavaPackageNode javaPackageNode1 = root1.addChildNode(
				JavaPackageNode.class, "javaPackageNode1");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaClassNode javaClassNode2 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode2");
		final JavaInnerClassNode javaInnerClassNode1 = root1.addChildNode(
				JavaInnerClassNode.class, "javaInnerClassNode1");
		final JavaInnerClassNode javaInnerClassNode2 = root1.addChildNode(
				JavaInnerClassNode.class, "javaInnerClassNode2");

		session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
				javaClassNode1);
		session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
				javaInnerClassNode1, true);

		session.addLink(JavaPackagePublicElement.class, javaPackageNode1,
				javaClassNode2);
		session.addLink(JavaPackagePublicElement.class, javaPackageNode1,
				javaInnerClassNode2, true);
		session.save();

		Collection<? extends Node> nodes = null;

		nodes = session.getLinkedNodes(javaPackageNode1);
		assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1,
				javaInnerClassNode2);

		nodes = session.getLinkedNodes(javaPackageNode1, JavaClassNode.class);
		assertNodes(nodes, javaClassNode1, javaClassNode2);

		nodes = session.getLinkedNodes(javaPackageNode1,
				JavaInnerClassNode.class);
		assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);

		nodes = session.getLinkedNodes(javaPackageNode1, JavaClassNode.class,
				true);
		assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1,
				javaInnerClassNode2);

		nodes = session.getLinkedNodes(javaPackageNode1,
				JavaInnerClassNode.class, true);
		assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);
	}

	/**
	 * Test get nodes by predicate.
	 */
	@Test
	public void testGetNodesByPredicate() {
		final Context root1 = reader.getContext("1L");
		final JavaPackageNode javaPackageNode1 = root1.addChildNode(
				JavaPackageNode.class, "javaPackageNode1");
		final JavaClassNode javaClassNode1 = javaPackageNode1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaClassNode javaClassNode2 = javaPackageNode1.addChildNode(
				JavaClassNode.class, "javaClassNode2");

		final JavaMethodNode javaMethodNode1A = javaClassNode1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1A");
		final JavaMethodNode javaMethodNode1B = javaClassNode1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1B");

		final JavaMethodNode javaMethodNode2A = javaClassNode2.addChildNode(
				JavaMethodNode.class, "javaMethodNode2A");
		final JavaMethodNode javaMethodNode2B = javaClassNode2.addChildNode(
				JavaMethodNode.class, "javaMethodNode2B");
		session.save();

		Collection<Node> nodes = null;
		nodes = session.getNodesByPredicate(new NamePredicate("javaPackage"));
		assertNodes(nodes, javaPackageNode1);

		nodes = session.getNodesByPredicate(new NamePredicate("javaClass"));
		assertNodes(nodes, javaClassNode1, javaClassNode2);

		nodes = session.getNodesByPredicate(new NamePredicate("javaMethod"));
		assertNodes(nodes, javaMethodNode1A, javaMethodNode1B,
				javaMethodNode2A, javaMethodNode2B);
	}

	/**
	 * Test get property as string.
	 */
	@Test
	public void testGetPropertyAsString() throws PropertyNotFoundException {
		final Context root = reader.getContext("1L");
		final Node node = session.addNode(root, "node");
		final NodeProperty<Integer> property = node.setProperty(Integer.class,
				Visibility.VisibilityLevel.PUBLIC, "number", new Integer(8));
		String value = node.getPropertyValueAsString("number");
		Assert.assertNotNull(value);
		Assert.assertEquals(value, "8");
		value = property.getValueAsString();
		Assert.assertNotNull(value);
		Assert.assertEquals(value, "8");
	}

	/**
	 * Test get sub meta node type.
	 */
	@Test
	public void testGetSubMetaNodeType() throws MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		javaClassNode1.addChildNode(JavaInnerClassNode.class,
				"javaInnerClassNode1");
		javaClassNode1.addChildNode(JavaMethodNode.class, "javaMethodNode1");
		session.save();

		final Metadata metadata = session.getMetadata();
		final MetaNodeType elementType = metadata
				.getMetaNodeType(JavaElementNode.class);
		final MetaNodeType javaClassType = elementType
				.getSubMetaNodeType(JavaClassNode.class);
		final MetaNodeType javaMethodType = elementType
				.getSubMetaNodeType(JavaMethodNode.class);
		Assert.assertNotNull(javaClassType);
		Assert.assertNotNull(javaMethodType);

		final MetaNodeType javaInnerClassType = javaClassType
				.getSubMetaNodeType(JavaInnerClassNode.class);
		Assert.assertNotNull(javaInnerClassType);
	}

	/**
	 * Test get sub meta node types.
	 */
	@Test
	public void testGetSubMetaNodeTypes() throws MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		javaClassNode1.addChildNode(JavaInnerClassNode.class,
				"javaInnerClassNode1");
		javaClassNode1.addChildNode(JavaMethodNode.class, "javaMethodNode1");
		session.save();

		final Metadata metadata = session.getMetadata();
		final MetaNodeType elementType = metadata
				.getMetaNodeType(JavaElementNode.class);

		final Collection<MetaNodeType> elementSubTypes = elementType
				.getSubMetaNodeTypes();
		assertMetaNodes(elementSubTypes, JavaClassNode.class,
				JavaMethodNode.class);

		final MetaNodeType javaClassType = metadata
				.getMetaNodeType(JavaClassNode.class);
		final Collection<MetaNodeType> javaClassSubTypes = javaClassType
				.getSubMetaNodeTypes();
		assertMetaNodes(javaClassSubTypes, JavaInnerClassNode.class);
	}

	/**
	 * Test get unidirectional links.
	 */
	@Test
	public void testGetUnidirectionalLinks() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		final Link simpleLinkAB = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		final Link simpleLinkBA = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode1,
				javaClassNode1);
		final Link multipleLinkAB = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1,
				javaMethodNode1);
		final Link multipleLinkBA = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode1,
				javaClassNode1);

		Collection<Link> links = null;
		Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
		Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;
		session.save();

		simpleLinks = session.getUnidirectionalLinks(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		assertLinksInOrder(simpleLinks, simpleLinkAB);
		simpleLinks = session.getUnidirectionalLinks(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode1,
				javaClassNode1);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		multipleLinks = session.getUnidirectionalLinks(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1,
				javaMethodNode1);
		assertLinksInOrder(multipleLinks, multipleLinkAB);
		multipleLinks = session.getUnidirectionalLinks(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode1,
				javaClassNode1);

		assertLinksInOrder(multipleLinks, multipleLinkBA);

		links = session.getUnidirectionalLinks(javaClassNode1, javaMethodNode1);
		assertLinks(links, simpleLinkAB, multipleLinkAB);
		links = session.getUnidirectionalLinks(javaMethodNode1, javaClassNode1);
		assertLinks(links, simpleLinkBA, multipleLinkBA);
	}

	/**
	 * Test get unidirectional links by source.
	 */
	@Test
	public void testGetUnidirectionalLinksBySource() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		final Link simpleLinkAB = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		final Link simpleLinkBA = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode1,
				javaClassNode1);
		final Link multipleLinkAB = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1,
				javaMethodNode1);
		final Link multipleLinkBA = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode1,
				javaClassNode1);

		Collection<Link> links = null;
		Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
		Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;
		session.save();

		simpleLinks = session.getUnidirectionalLinksBySource(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1);
		assertLinksInOrder(simpleLinks, simpleLinkAB);
		simpleLinks = session.getUnidirectionalLinksBySource(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode1);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		multipleLinks = session.getUnidirectionalLinksBySource(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1);
		assertLinksInOrder(multipleLinks, multipleLinkAB);
		multipleLinks = session.getUnidirectionalLinksBySource(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode1);
		assertLinksInOrder(multipleLinks, multipleLinkBA);

		links = session.getUnidirectionalLinksBySource(javaClassNode1);
		assertLinks(links, simpleLinkAB, multipleLinkAB);
		links = session.getUnidirectionalLinksBySource(javaMethodNode1);
		assertLinks(links, simpleLinkBA, multipleLinkBA);
	}

	/**
	 * Test get unidirectional links by target.
	 */
	@Test
	public void testGetUnidirectionalLinksByTarget() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		final Link simpleLinkAB = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		final Link simpleLinkBA = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode1,
				javaClassNode1);
		final Link multipleLinkAB = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1,
				javaMethodNode1);
		final Link multipleLinkBA = session.addLink(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode1,
				javaClassNode1);

		Collection<Link> links = null;
		Collection<JavaClassJavaMethodSimpleLink> simpleLinks = null;
		Collection<JavaClassJavaMethodMultipleLink> multipleLinks = null;
		session.save();
		simpleLinks = session.getUnidirectionalLinksByTarget(
				JavaClassJavaMethodSimpleLink.class, javaMethodNode1);
		assertLinksInOrder(simpleLinks, simpleLinkAB);
		simpleLinks = session.getUnidirectionalLinksByTarget(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1);
		assertLinksInOrder(simpleLinks, simpleLinkBA);

		multipleLinks = session.getUnidirectionalLinksByTarget(
				JavaClassJavaMethodMultipleLink.class, javaMethodNode1);
		assertLinksInOrder(multipleLinks, multipleLinkAB);
		multipleLinks = session.getUnidirectionalLinksByTarget(
				JavaClassJavaMethodMultipleLink.class, javaClassNode1);
		assertLinksInOrder(multipleLinks, multipleLinkBA);

		links = session.getUnidirectionalLinksByTarget(javaMethodNode1);
		assertLinks(links, simpleLinkAB, multipleLinkAB);
		links = session.getUnidirectionalLinksByTarget(javaClassNode1);
		assertLinks(links, simpleLinkBA, multipleLinkBA);
	}

	/**
	 * Test integer property.
	 */
	@Test
	public void testIntegerProperty() throws PropertyNotFoundException {
		// set new property ...
		final Context root = reader.getContext("1L");
		final NodeProperty<Integer> prop1 = root.setProperty(Integer.class,
				Visibility.VisibilityLevel.PUBLIC, "prop", 8);
		Assert.assertNotNull(prop1);
		Assert.assertNotNull(prop1.getValue());
		Assert.assertEquals(prop1.getValue().intValue(), 8);

		// get existent property ...
		final NodeProperty<Integer> prop2 = root.getProperty(Integer.class,
				"prop");
		Assert.assertNotNull(prop2);
		Assert.assertNotNull(prop2.getValue());
		Assert.assertEquals(prop2.getValue().intValue(), 8);

		// get property as Long ...
		final NodeProperty<Long> prop3 = root.getProperty(Long.class, "prop");
		Assert.assertNotNull(prop3);
		Assert.assertNotNull(prop3.getValue());
		Assert.assertEquals(prop3.getValue(), new Long(8));

		// get property as Number ...
		final NodeProperty<Number> prop4 = root.getProperty(Number.class,
				"prop");
		Assert.assertNotNull(prop4);
		Assert.assertNotNull(prop4.getValue());
		Assert.assertEquals(prop4.getValue().intValue(), 8);

		// get property as Serializable ...
		final NodeProperty<Serializable> prop5 = root.getProperty(
				Serializable.class, "prop");
		Assert.assertNotNull(prop5);
		Assert.assertNotNull(prop5.getValue());
		Assert.assertEquals(prop5.getValue(), new Long(8));

		// try to integer property as non-hierarchy class ...
		try {
			root.getProperty(String.class, "prop");
			Assert.fail();
		} catch (final PropertyTypeInvalidException e) {
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test line reference.
	 */
	@Test
	public void testLineReference() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");

		final LineReference lineRef1 = javaClassNode1.addLineReference(8, 17,
				26, 44, "Hello World!", "1", "1");
		final LineReference lineRef2 = javaClassNode1.addLineReference(71, 80,
				35, 53, "Bye World!", "2", "1");

		final Collection<SLLineReference> lineRefs = javaClassNode1
				.getLineReferences();
		Assert.assertNotNull(lineRefs);
		Assert.assertEquals(lineRefs.size(), 2);

		for (final LineReference lineRef : lineRefs) {
			if (lineRef.getArtifactId().equals("1")) {
				Assert.assertEquals(lineRef1.getStartLine(), 8);
				Assert.assertEquals(lineRef1.getEndLine(), 17);
				Assert.assertEquals(lineRef1.getStartColumn(), 26);
				Assert.assertEquals(lineRef1.getEndColumn(), 44);
				Assert.assertEquals(lineRef1.getStatement(), "Hello World!");
				Assert.assertEquals(lineRef1.getArtifactVersion(), "1");
			} else if (lineRef.getArtifactId().equals("2")) {
				Assert.assertEquals(lineRef2.getStartLine(), 71);
				Assert.assertEquals(lineRef2.getEndLine(), 80);
				Assert.assertEquals(lineRef2.getStartColumn(), 35);
				Assert.assertEquals(lineRef2.getEndColumn(), 53);
				Assert.assertEquals(lineRef2.getStatement(), "Bye World!");
				Assert.assertEquals(lineRef2.getArtifactVersion(), "1");
			} else {
				Assert.fail();
			}
		}
	}

	/**
	 * Test line reference.
	 */
	@Test
	public void testLineReferenceWithArtifactId() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final String artifactId = "targetId";

		final LineReference lineRef1 = javaClassNode1.addLineReference(8, 17,
				26, 44, "Hello World!", artifactId, "1");
		javaClassNode1.addLineReference(71, 80, 35, 53, "Bye World!", "2", "1");
		javaClassNode1.addLineReference(4, 8, 15, 16, "Hello Again!", "3", "1");
		final Collection<SLLineReference> lineRefs = javaClassNode1
				.getLineReferences(artifactId);
		Assert.assertNotNull(lineRefs);
		Assert.assertEquals(lineRefs.size(), 1);

		for (final LineReference lineRef : lineRefs) {
			if (lineRef.getArtifactId().equals(artifactId)) {
				Assert.assertEquals(lineRef1.getStartLine(), 8);
				Assert.assertEquals(lineRef1.getEndLine(), 17);
				Assert.assertEquals(lineRef1.getStartColumn(), 26);
				Assert.assertEquals(lineRef1.getEndColumn(), 44);
				Assert.assertEquals(lineRef1.getStatement(), "Hello World!");
				Assert.assertEquals(lineRef1.getArtifactVersion(), "1");
			} else {
				Assert.fail();
			}
		}
	}

	/**
	 * Test link properties.
	 */
	@Test
	public void testLinkProperties() throws PropertyNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaPackageNode javaPackageNode1 = root1.addChildNode(
				JavaPackageNode.class, "javaPackageNode1");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");

		String name;
		Integer value;
		Link link;
		LinkProperty<Integer> property;

		// test set property ...
		link = session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
				javaClassNode1);
		property = link.setProperty(Integer.class,
				Visibility.VisibilityLevel.PUBLIC, "integerProperty", 8);
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

	/**
	 * Test link property with annotations.
	 */
	@Test
	public void testLinkPropertyWithAnnotations() {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		final JavaClassJavaMethodSimpleLink link = session.addLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);

		final Date creationTime = new Date();
		link.setLinkName("myLink");
		link.setCreationTime(creationTime);

		Assert.assertNotNull(link.getLinkName());
		Assert.assertEquals(link.getLinkName(), "myLink");
		Assert.assertNotNull(link.getCreationTime());
		Assert.assertEquals(link.getCreationTime(), creationTime);
	}

	/**
	 * Test links removal by node deletion.
	 */
	@Test
	public void testLinksRemovalByNodeDeletion() {
		final Context root1 = reader.getContext("1L");
		final JavaPackageNode javaPackageNode1 = root1.addChildNode(
				JavaPackageNode.class, "javaPackageNode1");
		final JavaClassNode javaClassNode1 = javaPackageNode1.addChildNode(
				JavaClassNode.class, "javaClassNode1");
		final JavaClassNode javaClassNode2 = javaPackageNode1.addChildNode(
				JavaClassNode.class, "javaClassNode2");

		final JavaMethodNode javaMethodNode1A = javaClassNode1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1A");
		final JavaMethodNode javaMethodNode1B = javaClassNode1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1B");

		final JavaMethodNode javaMethodNode2A = javaClassNode2.addChildNode(
				JavaMethodNode.class, "javaMethodNode2A");
		final JavaMethodNode javaMethodNode2B = javaClassNode2.addChildNode(
				JavaMethodNode.class, "javaMethodNode2B");

		session.addLink(JavaLink.class, javaPackageNode1, javaClassNode1);
		session.addLink(JavaLink.class, javaPackageNode1, javaClassNode2);
		session.addLink(JavaLink.class, javaPackageNode1, javaMethodNode1A);
		session.addLink(JavaLink.class, javaPackageNode1, javaMethodNode1B);
		session.addLink(JavaLink.class, javaPackageNode1, javaMethodNode2A);
		session.addLink(JavaLink.class, javaPackageNode1, javaMethodNode2B);

		session.addLink(JavaLink.class, javaClassNode1, javaMethodNode1A);
		session.addLink(JavaLink.class, javaClassNode1, javaMethodNode1B);
		session.addLink(JavaLink.class, javaClassNode2, javaMethodNode2A);
		session.addLink(JavaLink.class, javaClassNode2, javaMethodNode2B);
		session.save();
		Collection<JavaLink> links = session.getLinks(JavaLink.class, null,
				null, Link.DIRECTION_ANY);
		Assert.assertEquals(links.size(), 10);

		javaPackageNode1.remove();
		links = session
				.getLinks(JavaLink.class, null, null, Link.DIRECTION_ANY);
		Assert.assertTrue(links.isEmpty());
	}

	/**
	 * Test link types for link deletion mark and unmark case.
	 */
	@Test
	public void testLinkTypesForLinkDeletionMarkAndUnmarkCase()
			throws Exception {
		Context root1 = reader.getContext("1L");
		JavaClassNode javaClassNode1 = root1.addChildNode(JavaClassNode.class,
				"javaClassNode1");
		JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");
		final JavaMethodNode javaMethodNode2 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode2");

		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode2);

		final Collection<Class<? extends Link>> linkTypesForLinkDeletion = new ArrayList<Class<? extends Link>>();
		linkTypesForLinkDeletion.add(JavaClassJavaMethodSimpleLink.class);
		root1.addChildNode(JavaMethodNode.class, "javaMethodNode2",
				linkTypesForLinkDeletion, null);

		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode2);

		session.save();
		session.close();
		session = openSession();

		root1 = reader.getContext("1L");
		javaClassNode1 = root1.getChildNode(JavaClassNode.class,
				"javaClassNode1");
		javaMethodNode1 = root1.getChildNode(JavaMethodNode.class,
				"javaMethodNode1");

		final Collection<? extends Link> links = session.getLink(
				JavaClassJavaMethodSimpleLink.class, null, null);
		Assert.assertEquals(links.size(), 2);
	}

	/**
	 * Test link types for link deletion mark case.
	 */
	@Test
	public void testLinkTypesForLinkDeletionMarkCase() throws Exception {
		Context root1 = reader.getContext("1L");
		JavaClassNode javaClassNode1 = root1.addChildNode(JavaClassNode.class,
				"javaClassNode1");
		JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");
		final JavaMethodNode javaMethodNode2 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode2");

		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode2);
		final Collection<Class<? extends Link>> linkTypesForLinkDeletion = new ArrayList<Class<? extends Link>>();
		linkTypesForLinkDeletion.add(JavaClassJavaMethodSimpleLink.class);
		root1.addChildNode(JavaMethodNode.class, "javaMethodNode2",
				linkTypesForLinkDeletion, null);

		session.save();
		session = openSession();

		root1 = reader.getContext("1L");
		javaClassNode1 = root1.getChildNode(JavaClassNode.class,
				"javaClassNode1");
		javaMethodNode1 = root1.getChildNode(JavaMethodNode.class,
				"javaMethodNode1");
		final Collection<? extends Link> links = session.getLink(
				JavaClassJavaMethodSimpleLink.class, null, null);
		for (final Link k : links) {
			System.err.println(">>> " + k.getSource().getName() + " - "
					+ k.getTarget().getName());
		}

		Assert.assertEquals(links.size(), 1);

		final Link link = links.iterator().next();
		Assert.assertEquals(link.getSource(), javaClassNode1);
		Assert.assertEquals(link.getTarget(), javaMethodNode1);
	}

	/**
	 * Test link types for linked node deletion mark and unmark case.
	 */
	@Test
	public void testLinkTypesForLinkedNodeDeletionMarkAndUnmarkCase()
			throws Exception {
		Context root1 = reader.getContext("1L");
		JavaClassNode javaClassNode1 = root1.addChildNode(JavaClassNode.class,
				"javaClassNode1");
		JavaMethodNode javaMethodNode1 = javaClassNode1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");
		final JavaMethodNode javaMethodNode2 = javaClassNode1.addChildNode(
				JavaMethodNode.class, "javaMethodNode2");

		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode2);

		final Collection<Class<? extends Link>> linkTypesForLinkedNodesDeletion = new ArrayList<Class<? extends Link>>();
		linkTypesForLinkedNodesDeletion
				.add(JavaClassJavaMethodSimpleLink.class);
		root1.addChildNode(JavaClassNode.class, "javaClassNode1", null,
				linkTypesForLinkedNodesDeletion);

		javaMethodNode1 = javaClassNode1.addChildNode(JavaMethodNode.class,
				"javaMethodNode1");

		session.save();
		session = openSession();

		root1 = reader.getContext("1L");
		javaClassNode1 = root1.getChildNode(JavaClassNode.class,
				"javaClassNode1");
		final Collection<Node> nodes = javaClassNode1.getNodes();
		Assert.assertEquals(nodes.size(), 1);
		Assert.assertEquals(nodes.iterator().next().getName(),
				"javaMethodNode1");
	}

	/**
	 * Test link types for linked node deletion mark case.
	 */
	@Test
	public void testLinkTypesForLinkedNodeDeletionMarkCase() throws Exception {
		Context root1 = reader.getContext("1L");
		JavaClassNode javaClassNode1 = root1.addChildNode(JavaClassNode.class,
				"javaClassNode1");
		final JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");
		final JavaMethodNode javaMethodNode2 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode2");

		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1);
		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode2);

		final Collection<Class<? extends Link>> linkTypesForLinkedNodesDeletion = new ArrayList<Class<? extends Link>>();
		linkTypesForLinkedNodesDeletion
				.add(JavaClassJavaMethodSimpleLink.class);
		root1.addChildNode(JavaClassNode.class, "javaClassNode1", null,
				linkTypesForLinkedNodesDeletion);

		session.save();
		session = openSession();

		root1 = reader.getContext("1L");
		javaClassNode1 = root1.getChildNode(JavaClassNode.class,
				"javaClassNode1");
		final Collection<Node> nodes = javaClassNode1.getNodes();
		Assert.assertTrue(nodes.isEmpty());
	}

	/**
	 * Test long property.
	 */
	@Test
	public void testLongProperty() throws PropertyNotFoundException {
		// set new property ...
		final Context root = reader.getContext("1L");
		final NodeProperty<Long> prop1 = root.setProperty(Long.class,
				Visibility.VisibilityLevel.PUBLIC, "prop", 8L);

		Assert.assertNotNull(prop1);
		Assert.assertNotNull(prop1.getValue());
		Assert.assertEquals(prop1.getValue(), new Long(8L));

		// get existent property ...
		final NodeProperty<Long> prop2 = root.getProperty(Long.class, "prop");
		Assert.assertNotNull(prop2);
		Assert.assertNotNull(prop2.getValue());
		Assert.assertEquals(prop2.getValue(), new Long(8L));

		// get property as Integer ...
		final NodeProperty<Integer> prop3 = root.getProperty(Integer.class,
				"prop");
		Assert.assertNotNull(prop3);
		Assert.assertNotNull(prop3.getValue());
		Assert.assertEquals(prop3.getValue(), new Integer(8));

		// get property as Number ...
		final NodeProperty<Number> prop4 = root.getProperty(Number.class,
				"prop");
		Assert.assertNotNull(prop4);
		Assert.assertNotNull(prop4.getValue());
		Assert.assertEquals(prop4.getValue(), new Long(8L));

		// get property as Serializable ...
		final NodeProperty<Serializable> prop5 = root.getProperty(
				Serializable.class, "prop");
		Assert.assertNotNull(prop5);
		Assert.assertNotNull(prop5.getValue());
		Assert.assertEquals(prop5.getValue(), new Long(8));

		// try to integer property as non-hierarchy class ...
		try {
			root.getProperty(String.class, "prop");
			Assert.fail();
		} catch (final PropertyTypeInvalidException e) {
			Assert.assertTrue(true);
		}
	}

	@Test
	public void testMetaLinkGetDescription()
			throws MetaNodeTypeNotFoundException, MetaLinkTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode = root1.addChildNode(
				JavaClassNode.class, "javaClassNode");
		final JavaMethodNode javaMethodNode = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode");
		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode,
				javaMethodNode);
		session.save();

		final Metadata metadata = session.getMetadata();
		final MetaLink metaLink = metadata
				.getMetaLinkType(JavaClassJavaMethodSimpleLink.class)
				.getMetalinks().iterator().next();
		final String description = metaLink.getDescription();
		Assert.assertNotNull(description);
		Assert.assertEquals(description, "Java Class to Java Method Link");
		Assert.assertEquals(metaLink.getVisibility(),
				Visibility.VisibilityLevel.PUBLIC);
	}

	@Test
	public void testMetaLinkGetVisibility()
			throws MetaNodeTypeNotFoundException, MetaLinkTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode = root1.addChildNode(
				JavaClassNode.class, "javaClassNode");
		final JavaMethodNode javaMethodNode = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode");
		final JavaClassJavaMethodSimpleLinkPrivate link = session.addLink(
				JavaClassJavaMethodSimpleLinkPrivate.class, javaClassNode,
				javaMethodNode);
		session.save();
		Assert.assertNotNull(link.getMetaLink());
		Assert.assertEquals(link.getMetaLink().getVisibility(),
				Visibility.VisibilityLevel.PRIVATE);

		final Metadata metadata = session.getMetadata();
		final MetaLink metaLink = metadata
				.getMetaLinkType(JavaClassJavaMethodSimpleLinkPrivate.class)
				.getMetalinks().iterator().next();
		Assert.assertEquals(metaLink.getVisibility(),
				Visibility.VisibilityLevel.PRIVATE);
	}

	/**
	 * Test meta node get description.
	 */
	@Test
	public void testMetaNodeGetDescription()
			throws MetaNodeTypeNotFoundException {
		final Context root1 = reader.getContext("1L");
		root1.addChildNode(JavaClassNode.class, "javaClassNode1");
		session.save();

		final Metadata metadata = session.getMetadata();
		final MetaNodeType metaNode = metadata
				.getMetaNodeType(JavaClassNode.class);
		final String description = metaNode.getDescription();
		Assert.assertNotNull(description);
		Assert.assertEquals(description, "Java Class");
	}

	/**
	 * Test node operations.
	 */
	@Test
	public void testNodeOperations() throws PropertyNotFoundException {
		// add new node ...
		final Context root = reader.getContext("1L");
		final Node node1 = session.addNode(root, "node");
		Assert.assertNotNull(node1);
		Assert.assertEquals(node1.getName(), "node");

		// get node ...
		final Node node2 = root.getNode("node");
		Assert.assertNotNull(node2);
		Assert.assertEquals(node2.getName(), "node");
		Assert.assertEquals(node1, node2);

		// set property on node1 ...
		final NodeProperty<Integer> prop1 = node1.setProperty(Integer.class,
				Visibility.VisibilityLevel.PUBLIC, "prop", 8);
		Assert.assertNotNull(prop1);
		Assert.assertEquals(prop1.getValue(), new Integer(8));

		// get property on node2 ...
		final NodeProperty<Integer> prop2 = node2.getProperty(Integer.class,
				"prop");
		Assert.assertNotNull(prop2);
		Assert.assertEquals(prop2.getValue(), new Integer(8));
	}

	/**
	 * Test properties retrieval.
	 */
	@Test
	public void testPropertiesRetrieval() {
		final Context root = reader.getContext("1L");
		root.setProperty(Integer.class, Visibility.VisibilityLevel.PUBLIC,
				"integerProp", 8);
		root.setProperty(String.class, Visibility.VisibilityLevel.PUBLIC,
				"stringProp", "Hello World!");
		final Set<NodeProperty<Serializable>> properties = root.getProperties();
		for (final NodeProperty<Serializable> property : properties) {
			if (property.getName().equals("integerProp")) {
				Assert.assertEquals(property.getValue(), new Long(8));
			} else if (property.getName().equals("stringProp")) {
				Assert.assertEquals(property.getValue(), "Hello World!");
			}
		}
	}

	/**
	 * Test property removal.
	 */
	@Test
	public void testPropertyRemoval() throws PropertyNotFoundException {
		final Context root = reader.getContext("1L");
		final NodeProperty<Integer> prop1 = root.setProperty(Integer.class,
				Visibility.VisibilityLevel.PUBLIC, "property", 8);
		prop1.remove();
		final NodeProperty<Integer> property = root.getProperty(Integer.class,
				"property");
		Assert.assertTrue(property == null);
	}

	/**
	 * Test property value overwriting.
	 */
	@Test
	public void testPropertyValueOverwriting() throws PropertyNotFoundException {
		final Context root = reader.getContext("1L");
		final NodeProperty<Integer> prop1 = root.setProperty(Integer.class,
				Visibility.VisibilityLevel.PUBLIC, "prop", 8);
		final NodeProperty<Integer> prop2 = root.getProperty(Integer.class,
				"prop");
		prop2.setValue(71);
		Assert.assertEquals(prop2.getValue(), new Integer(71));
		Assert.assertEquals(prop1.getValue(), prop2.getValue());
	}

	/**
	 * Test string property.
	 */
	@Test
	public void testStringProperty() throws PropertyNotFoundException {
		// set new property ...
		final Context root = reader.getContext("1L");
		final NodeProperty<String> prop1 = root.setProperty(String.class,
				Visibility.VisibilityLevel.PUBLIC, "prop", "Hello");
		Assert.assertNotNull(prop1);
		Assert.assertNotNull(prop1.getValue());
		Assert.assertEquals(prop1.getValue(), new String("Hello"));

		// get existent property ...
		final NodeProperty<String> prop2 = root.getProperty(String.class,
				"prop");
		Assert.assertNotNull(prop2);
		Assert.assertNotNull(prop2.getValue());
		Assert.assertEquals(prop2.getValue(), new String("Hello"));

		// get property as Serializable ...
		final NodeProperty<Serializable> prop5 = root.getProperty(
				Serializable.class, "prop");
		Assert.assertNotNull(prop5);
		Assert.assertNotNull(prop5.getValue());
		Assert.assertEquals(prop5.getValue(), new String("Hello"));

		// try to integer property as non-hierarchy class ...
		try {
			root.getProperty(Integer.class, "prop");
			Assert.fail();
		} catch (final PropertyTypeInvalidException e) {
			Assert.assertTrue(true);
		}
	}

	/**
	 * Test transient links with annotations.
	 */
	@Test
	public void testTransientLinksWithAnnotations() throws Exception {
		Context root1 = reader.getContext("1L");
		JavaClassNode javaClassNode1 = root1.addChildNode(JavaClassNode.class,
				"javaClassNode1");
		JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");
		final JavaMethodNode javaMethodNode2 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode2");

		session.addLink(TransientLink.class, javaClassNode1, javaMethodNode1);
		session.addLink(TransientLink.class, javaClassNode1, javaMethodNode2);

		session.save();
		session = openSession();

		root1 = reader.getContext("1L");
		javaClassNode1 = root1.getChildNode(JavaClassNode.class,
				"javaClassNode1");
		javaMethodNode1 = root1.getChildNode(JavaMethodNode.class,
				"javaMethodNode1");
		final Collection<? extends Link> links = session
				.getUnidirectionalLinks(TransientLink.class, javaClassNode1,
						javaMethodNode1);
		Assert.assertEquals(links.size(), 0);
	}

	/**
	 * Test transient links without annotations.
	 */
	@Test
	public void testTransientLinksWithoutAnnotations() throws Exception {
		Context root1 = reader.getContext("1L");
		JavaClassNode javaClassNode1 = root1.addChildNode(JavaClassNode.class,
				"javaClassNode1");
		JavaMethodNode javaMethodNode1 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");
		JavaMethodNode javaMethodNode2 = root1.addChildNode(
				JavaMethodNode.class, "javaMethodNode2");

		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode1, false, TRANSIENT);
		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode2, false, TRANSIENT);

		// make previous transient link persistent now ...
		session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
				javaMethodNode2, false, NORMAL);

		session.save();
		session = openSession();

		root1 = reader.getContext("1L");
		javaClassNode1 = root1.getChildNode(JavaClassNode.class,
				"javaClassNode1");
		javaMethodNode1 = root1.getChildNode(JavaMethodNode.class,
				"javaMethodNode1");
		javaMethodNode2 = root1.getChildNode(JavaMethodNode.class,
				"javaMethodNode2");
		final Collection<? extends Link> links = session.getLink(
				JavaClassJavaMethodSimpleLink.class, javaClassNode1, null);
		Assert.assertEquals(links.size(), 1);
		final Link link = links.iterator().next();
		Assert.assertEquals(link.getTarget(), javaMethodNode2);
	}

	/**
	 * Test transient nodes with annotation.
	 */
	@Test
	public void testTransientNodesWithAnnotation() throws Exception {
		Context root1 = reader.getContext("1L");
		JavaClassNode javaClassNode1 = root1.addChildNode(JavaClassNode.class,
				"javaClassNode1");
		JavaMethodNode javaMethodNode1 = javaClassNode1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1");

		javaClassNode1.addChildNode(TransientNode.class, "transNode1");
		javaMethodNode1.addChildNode(TransientNode.class, "transNode2");

		session.save();
		session = openSession();

		root1 = reader.getContext("1L");
		javaClassNode1 = root1.getChildNode(JavaClassNode.class,
				"javaClassNode1");
		Assert.assertNotNull(javaClassNode1);
		javaMethodNode1 = javaClassNode1.getChildNode(JavaMethodNode.class,
				"javaMethodNode1");
		Assert.assertNotNull(javaMethodNode1);

		Assert.assertNull(javaClassNode1.getChildNode(TransientNode.class,
				"transNode1"));
		Assert.assertNull(javaMethodNode1.getChildNode(TransientNode.class,
				"transNode2"));
	}

	/**
	 * Test transient nodes without annotation.
	 */
	@Test
	public void testTransientNodesWithoutAnnotation() throws Exception {
		Context root1 = reader.getContext("1L");
		JavaClassNode javaClassNode1 = root1.addChildNode(JavaClassNode.class,
				"javaClassNode1", NORMAL);
		JavaMethodNode javaMethodNode1 = javaClassNode1.addChildNode(
				JavaMethodNode.class, "javaMethodNode1", NORMAL);

		javaClassNode1.addChildNode(JavaClassNode.class, "transNode1",
				TRANSIENT);
		javaMethodNode1.addChildNode(JavaMethodNode.class, "transNode2",
				TRANSIENT);

		// add transNode1 as NORMAL (not PERSISTENT anymore) ...
		javaClassNode1.addChildNode(JavaClassNode.class, "transNode1", NORMAL);

		session.save();
		session = openSession();

		root1 = reader.getContext("1L");
		javaClassNode1 = root1.getChildNode(JavaClassNode.class,
				"javaClassNode1");
		Assert.assertNotNull(javaClassNode1);
		javaMethodNode1 = javaClassNode1.getChildNode(JavaMethodNode.class,
				"javaMethodNode1");
		Assert.assertNotNull(javaMethodNode1);
		Assert.assertNotNull(javaClassNode1.getChildNode(JavaClassNode.class,
				"transNode1"));
		Assert.assertNull(javaMethodNode1.getChildNode(TransientNode.class,
				"transNode2"));
	}

	@Test
	public void testTreeLineReference() throws Exception {
		final Context root1 = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");

		javaClassNode1
				.addLineReference(8, 17, 26, 44, "Hello World!", "1", "1");
		javaClassNode1
				.addLineReference(9, 16, 26, 44, "Hello World!", "1", "1");
		javaClassNode1.addLineReference(22, 16, 26, 44, "Hello World!", "1",
				"1");
		javaClassNode1.addLineReference(22, 16, 26, 32, "Hello World!", "1",
				"1");
		javaClassNode1.addLineReference(22, 16, 26, 32, "New Hello!", "1", "1");
		javaClassNode1.addLineReference(22, 16, 26, 32, "New Hello!", "1", "1");
		javaClassNode1.addLineReference(71, 80, 35, 53, "Bye World!", "2", "1");
		javaClassNode1.addLineReference(71, 80, 35, 53, "Bye World!", "2", "2");
		javaClassNode1.addLineReference(71, 80, 35, 53, "Bye World!", "2", "1");

		final TreeLineReference treeLineRefs = javaClassNode1
				.getTreeLineReferences();
		Assert.assertEquals(treeLineRefs.getArtifacts().size(), 3);

		for (final TreeLineReference.SLArtifactLineReference artifactLine : treeLineRefs
				.getArtifacts()) {
			if (artifactLine.getArtifactId().equals("1")) {
				Assert.assertEquals(artifactLine.getStatements().size(), 2);
				for (final TreeLineReference.SLStatementLineReference activeStatement : artifactLine
						.getStatements()) {
					if (activeStatement.getStatement().equals("Hello World!")) {
						Assert.assertEquals(activeStatement.getLineReferences()
								.size(), 4);
					} else if (activeStatement.getStatement().equals(
							"New Hello!")) {
						Assert.assertEquals(activeStatement.getLineReferences()
								.size(), 1);
					} else {
						Assert.fail();
					}
				}
			} else if (artifactLine.getArtifactId().equals("2")
					&& artifactLine.getArtifactVersion().equals("1")) {
				Assert.assertEquals(artifactLine.getStatements().size(), 1);
			} else if (artifactLine.getArtifactId().equals("2")
					&& artifactLine.getArtifactVersion().equals("2")) {
				Assert.assertEquals(artifactLine.getStatements().size(), 1);
			} else {
				Assert.fail();
			}
		}
	}

	@Test
	public void testTreeLineReferenceWithArtifactId() {
		final Context root1 = reader.getContext("1L");

		final String artifactId = "targetId";
		final String statement = "Hello World 1!";

		final JavaClassNode javaClassNode1 = root1.addChildNode(
				JavaClassNode.class, "javaClassNode1");

		javaClassNode1.addLineReference(8, 17, 26, 44, statement, artifactId,
				"1");
		javaClassNode1.addLineReference(22, 16, 26, 32, statement, artifactId,
				"1");
		javaClassNode1.addLineReference(9, 16, 26, 44, "Hello World 2!",
				artifactId, "1");
		javaClassNode1.addLineReference(22, 16, 26, 44, "Hello World 3!",
				artifactId, "1");
		javaClassNode1.addLineReference(22, 16, 26, 32, statement, "1", "1");
		javaClassNode1.addLineReference(22, 16, 26, 32, "New Hello!", "1", "1");
		javaClassNode1.addLineReference(71, 80, 35, 53, statement, "2", "1");
		javaClassNode1.addLineReference(71, 80, 35, 53, "Bye World!", "2", "2");
		javaClassNode1.addLineReference(71, 80, 35, 53, "Bye World!", "2", "1");

		final TreeLineReference treeLineRefs = javaClassNode1
				.getTreeLineReferences(artifactId);
		Assert.assertEquals(treeLineRefs.getArtifacts().size(), 1);

		for (final TreeLineReference.SLArtifactLineReference artifactLine : treeLineRefs
				.getArtifacts()) {
			if (artifactLine.getArtifactId().equals(artifactId)) {
				Assert.assertEquals(artifactLine.getStatements().size(), 3);
				for (final TreeLineReference.SLStatementLineReference activeStatement : artifactLine
						.getStatements()) {
					if (activeStatement.getStatement().equals(statement)) {
						Assert.assertEquals(activeStatement.getLineReferences()
								.size(), 2);
					} else {
						Assert.assertEquals(activeStatement.getLineReferences()
								.size(), 1);
					}
				}
			}
		}
	}

	/**
	 * Test typed node operations.
	 */
	@Test
	public void testTypedNodeOperations() {
		// add new node ...
		final Context root = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = session.addNode(root,
				JavaClassNode.class, "javaClassNode");
		Assert.assertNotNull(javaClassNode1);
		Assert.assertEquals(javaClassNode1.getName(), "javaClassNode");

		// get node ...
		final JavaClassNode javaClassNode2 = root.getChildNode(
				JavaClassNode.class, "javaClassNode");
		Assert.assertNotNull(javaClassNode2);
		Assert.assertEquals(javaClassNode2.getName(), "javaClassNode");

		// set and get custom properties ...
		javaClassNode2.setClassName("HelloWorld");
		Assert.assertEquals(javaClassNode2.getClassName(), "HelloWorld");
		javaClassNode2.setModifier(JavaClassNode.MODIFIER_PUBLIC);
		Assert.assertEquals(javaClassNode2.getModifier(),
				JavaClassNode.MODIFIER_PUBLIC);
		final Date creationTime = new Date();
		javaClassNode2.setCreationTime(creationTime);
		Assert.assertEquals(javaClassNode2.getCreationTime(), creationTime);

		// get node as default type ...
		final Node node = root.getNode("javaClassNode");
		Assert.assertEquals(node, javaClassNode1);
	}

	/**
	 * Test typed on different contexts.
	 */
	@Test
	public void testTypedOnDifferentContexts() {
		// add new node ...
		final Context root = reader.getContext("1L");
		final JavaClassNode javaClassNode1 = session.addNode(root,
				JavaClassNode.class, "javaClassNode");
		Assert.assertNotNull(javaClassNode1);
		Assert.assertEquals(javaClassNode1.getName(), "javaClassNode");

		// add new node ...
		final Context root2 = reader.getContext("2L");
		final JavaClassNode javaClassNode2 = session.addNode(root2,
				JavaClassNode.class, "javaClassNode");
		Assert.assertNotNull(javaClassNode2);
		Assert.assertEquals(javaClassNode2.getName(), "javaClassNode");

		Assert.assertFalse(javaClassNode1.getId()
				.equals(javaClassNode2.getId()));
	}

	/**
	 * Test typed on different contexts.
	 */
	@Test
	public void testContextAndNodeCaption() throws Exception {
		// add new node ...
		Context myNewContext = reader.getContext("MyNewContext");

		Assert.assertEquals(myNewContext.getCaption(), "MyNewContext");
		Assert.assertEquals(myNewContext.getCaption(), "MyNewContext");

		myNewContext.setCaption("newContextCaption");
		Assert.assertEquals(myNewContext.getCaption(), "newContextCaption");
		Assert.assertEquals(myNewContext.getCaption(), "newContextCaption");

		session.save();
		session.close();
		session = openSession();

		myNewContext = reader.getContext("newContextCaption");
		Assert.assertEquals(myNewContext.getCaption(), "newContextCaption");
		Assert.assertEquals(myNewContext.getCaption(), "newContextCaption");

	}

}
