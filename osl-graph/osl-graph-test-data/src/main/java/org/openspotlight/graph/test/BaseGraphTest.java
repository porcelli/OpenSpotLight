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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.LinkDirection;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.TreeLineReference;
import org.openspotlight.graph.TreeLineReference.ArtifactLineReference;
import org.openspotlight.graph.TreeLineReference.SimpleLineReference;
import org.openspotlight.graph.TreeLineReference.StatementLineReference;
import org.openspotlight.graph.exception.MetaNodeTypeNotFoundException;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.manipulation.GraphWriter;
import org.openspotlight.graph.metadata.MetaNodeType;
import org.openspotlight.graph.metadata.Metadata;
import org.openspotlight.graph.test.domain.link.JavaClassJavaMethodMultipleLink;
import org.openspotlight.graph.test.domain.link.JavaClassJavaMethodSimpleLink;
import org.openspotlight.graph.test.domain.link.JavaClassJavaMethodSimpleLinkACTB;
import org.openspotlight.graph.test.domain.link.JavaLink;
import org.openspotlight.graph.test.domain.link.JavaPackageJavaClass;
import org.openspotlight.graph.test.domain.link.JavaPackageNode;
import org.openspotlight.graph.test.domain.link.JavaPackagePublicElement;
import org.openspotlight.graph.test.domain.node.CobolElementNode;
import org.openspotlight.graph.test.domain.node.JavaClassNode;
import org.openspotlight.graph.test.domain.node.JavaElementNode;
import org.openspotlight.graph.test.domain.node.JavaInnerClassNode;
import org.openspotlight.graph.test.domain.node.JavaMethodNode;
import org.openspotlight.graph.test.domain.node.JavaType;

public abstract class BaseGraphTest {

    private JavaClassNode  javaClassNode;
    /**
     * The java method node.
     */
    private JavaMethodNode javaMethodNode;
    /**
     * The link ab.
     */
    private Link           linkAB;
    /**
     * The link ba.
     */
    private Link           linkBA;
    /**
     * The link both.
     */
    private Link           linkBoth;

    private GraphReader    reader;

    private GraphWriter    session;

    /**
     * Assert links.
     * 
     * @param links the links
     * @param expectedLinks the expected links
     */
    private void assertLinks(final Iterable<? extends Link> links,
                             final Link... expectedLinks) {
        Assert.assertNotNull(links);
        final Set<Link> linkSet = new TreeSet<Link>(
                SLCollections.iterableToList(links));
        final Set<Link> expectedLinkSet = new TreeSet<Link>(
                Arrays.asList(expectedLinks));
        Assert.assertEquals(linkSet, expectedLinkSet);
    }

    /**
     * Assert links in order.
     * 
     * @param links the links
     * @param expectedLinks the expected links
     */
    private void assertLinksInOrder(final Iterable<? extends Link> links,
                                    final Link... expectedLinks) {
        Assert.assertNotNull(links);
        final Iterator<? extends Link> iter = links.iterator();
        for (final Link expectedLink: expectedLinks) {
            Assert.assertEquals(expectedLink, iter.next());
        }
    }

    /**
     * Assert meta nodes.
     * 
     * @param metaNodes the meta nodes
     * @param expectedNodeTypes the expected node types
     */
    private void assertMetaNodes(final Iterable<MetaNodeType> metaNodes,
                                 final Class<?>... expectedNodeTypes) {
        Assert.assertNotNull(metaNodes);
        final Set<String> metaNodeTypeNameSet = new TreeSet<String>(
                this.getNodeTypeNameSet(metaNodes));
        final Set<String> expectedNodeTypeNameSet = new TreeSet<String>(
                this.getNodeTypeNameSet(expectedNodeTypes));
        Assert.assertEquals(metaNodeTypeNameSet, expectedNodeTypeNameSet);
    }

    /**
     * Assert nodes.
     * 
     * @param nodes the nodes
     * @param expectedNodes the expected nodes
     */
    private void assertNodes(final Iterable<? extends Node> nodes,
                             final Node... expectedNodes) {
        Assert.assertNotNull(nodes);
        final Set<Node> nodeSet = new TreeSet<Node>(
                SLCollections.iterableToList(nodes));
        final Set<Node> expectedNodeSet = new TreeSet<Node>(
                Arrays.asList(expectedNodes));
        Assert.assertEquals(nodeSet, expectedNodeSet);
    }

    private <T extends Link> void assertSimpleLink(final Link link,
                                                   final Class<T> linkClass, final Node source, final Node target) {
        this.<T>assertSimpleLink(link, linkClass, source, target);
    }

    /**
     * Assert simple link.
     * 
     * @param link the link
     * @param linkClass the link class
     * @param source the source
     * @param target the target
     * @param bidirecional the bidirecional
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

    /**
     * Gets the node type name set.
     * 
     * @param expectedNodeTypes the expected node types
     * @return the node type name set
     */
    private Set<String> getNodeTypeNameSet(final Class<?>[] expectedNodeTypes) {
        final Set<String> set = new TreeSet<String>();
        for (final Class<?> nodeType: expectedNodeTypes) {
            set.add(nodeType.getName());
        }
        return set;
    }

    /**
     * Gets the node type name set.
     * 
     * @param metaNodes the meta nodes
     * @return the node type name set
     */
    private Set<String> getNodeTypeNameSet(
                                           final Iterable<MetaNodeType> metaNodes) {
        final Set<String> set = new TreeSet<String>();
        for (final MetaNodeType metaNode: metaNodes) {
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
     * @param linkClass the new up existent ab link scenario
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
     * @param linkClass the new up existent ba link scenario
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
     * @param linkClass the new up existent both link scenario
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

    protected void clearSession() {}

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
     * After test.
     */
    @After
    public void afterTest() {
        clearSession();
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
     * Test typed on different contexts.
     */
    @Test
    public void testContextAndNodeCaption()
        throws Exception {
        // add new node ...
        final Context myNewContext = reader.getContext("MyNewContext");

        Assert.assertEquals(myNewContext.getCaption(), "MyNewContext");
        Assert.assertEquals(myNewContext.getCaption(), "MyNewContext");
        session.setContextCaption(myNewContext, "newContextCaption");
        session.flush();
        Assert.assertEquals(myNewContext.getCaption(), "newContextCaption");
        Assert.assertEquals(myNewContext.getCaption(), "newContextCaption");

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

        Iterable<Link> links = null;
        Iterable<Link> simpleLinks = null;
        Iterable<Link> multipleLinks = null;

        simpleLinks = reader.getLinks(javaClassNode1, javaMethodNode1,
                LinkDirection.BIDIRECTIONAL);
        assertLinksInOrder(simpleLinks, simpleLinkBoth);

        multipleLinks = reader.getLinks(javaMethodNode1, javaClassNode1,
                LinkDirection.BIDIRECTIONAL);
        assertLinksInOrder(multipleLinks, multipleLinkBoth);

        links = reader.getLinks(javaClassNode1, javaMethodNode1,
                LinkDirection.ANY);
        assertLinks(links, simpleLinkBoth, multipleLinkBoth);

        links = reader.getLinks(javaMethodNode1, javaClassNode1,
                LinkDirection.ANY);
        assertLinks(links, simpleLinkBoth, multipleLinkBoth);
    }

    /**
     * Test get bidirectional links by side.
     */
    @Test
    public void testGetBidirectionalLinksBySide() {
        final Context root1 = reader.getContext("1L");
        final JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");
        final JavaMethodNode javaMethodNode1 = session.addNode(root1,
                JavaMethodNode.class, "javaMethodNode1");

        final Link simpleLinkBoth = session.addLink(
                JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode1);
        final Link multipleLinkBoth = session.addBidirectionalLink(
                JavaClassJavaMethodMultipleLink.class, javaClassNode1,
                javaMethodNode1);
        session.flush();

        Iterable<Link> bidLinks = reader.getLinks(javaMethodNode1,
                javaClassNode1, LinkDirection.BIDIRECTIONAL);

        Iterable<Link> uniLinks = reader.getLinks(javaMethodNode1,
                javaClassNode1, LinkDirection.UNIDIRECTIONAL);

        assertLinksInOrder(bidLinks, multipleLinkBoth);
        assertLinksInOrder(uniLinks, simpleLinkBoth);
        bidLinks = reader.getLinks(javaMethodNode1, null,
                LinkDirection.BIDIRECTIONAL);

        uniLinks = reader.getLinks(javaMethodNode1, null,
                LinkDirection.UNIDIRECTIONAL);

        assertLinksInOrder(bidLinks, multipleLinkBoth);
        assertLinksInOrder(uniLinks, simpleLinkBoth);

    }

    /**
     * Test get links.
     */
    @Test
    public void testGetLinks() {
        final Context root1 = reader.getContext("1L");
        final JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");
        final JavaMethodNode javaMethodNode1 = session.addNode(root1,
                JavaMethodNode.class, "javaMethodNode1");

        final Link simpleUniLinkClass = session.addLink(
                JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode1);
        final Link simpleUniLinkMethod = session.addLink(
                JavaClassJavaMethodSimpleLink.class, javaMethodNode1,
                javaClassNode1);
        final Link simpleBidLink = session.addBidirectionalLink(
                JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode1);

        session.flush();

        Iterable<Link> bidLinks = reader.getLinks(javaMethodNode1, null,
                LinkDirection.BIDIRECTIONAL);
        assertLinksInOrder(bidLinks, simpleBidLink);

        bidLinks = reader.getLinks(javaClassNode1, null,
                LinkDirection.BIDIRECTIONAL);
        assertLinksInOrder(bidLinks, simpleBidLink);

        bidLinks = reader.getLinks(javaClassNode1, javaMethodNode1,
                LinkDirection.BIDIRECTIONAL);
        assertLinksInOrder(bidLinks, simpleBidLink);

        bidLinks = reader.getLinks(javaMethodNode1, javaClassNode1,
                LinkDirection.BIDIRECTIONAL);
        assertLinksInOrder(bidLinks, simpleBidLink);

        Iterable<Link> twoLinkTypes = reader.getLinks(javaClassNode1, null, LinkDirection.ANY);

        Assert.assertNotNull(twoLinkTypes);
        
        assertLinksInOrder(bidLinks, simpleBidLink, simpleUniLinkClass);

        twoLinkTypes = reader.getLinks(javaClassNode1, javaMethodNode1,
                LinkDirection.ANY);

        assertLinksInOrder(bidLinks, simpleBidLink, simpleUniLinkClass);

        twoLinkTypes = reader
                .getLinks(javaMethodNode1, null, LinkDirection.ANY);

        assertLinksInOrder(bidLinks, simpleBidLink, simpleUniLinkMethod);
        twoLinkTypes = reader.getLinks(javaMethodNode1, javaClassNode1,
                LinkDirection.ANY);

        assertLinksInOrder(bidLinks, simpleBidLink, simpleUniLinkMethod);

        Iterable<Link> oneLink = reader.getLinks(javaClassNode1, null,
                LinkDirection.UNIDIRECTIONAL);

        assertLinksInOrder(oneLink, simpleUniLinkClass);

        oneLink = reader.getLinks(javaClassNode1, javaMethodNode1,
                LinkDirection.UNIDIRECTIONAL);

        assertLinksInOrder(oneLink, simpleUniLinkClass);

        oneLink = reader.getLinks(javaMethodNode1, null,
                LinkDirection.UNIDIRECTIONAL);

        assertLinksInOrder(oneLink, simpleUniLinkMethod);
        oneLink = reader.getLinks(javaMethodNode1, javaClassNode1,
                LinkDirection.UNIDIRECTIONAL);

        assertLinksInOrder(oneLink, simpleUniLinkMethod);

    }

    /**
     * Test get meta node.
     */
    @Test
    public void testGetMetaNode()
        throws MetaNodeTypeNotFoundException {
        final Context root1 = reader.getContext("1L");
        session.addNode(root1, JavaPackageNode.class, "javaPackageNode1");
        session.addNode(root1, JavaPackageNode.class, "javaPackageNode2");
        session.addNode(root1, JavaClassNode.class, "javaClassNode1");
        session.addNode(root1, JavaClassNode.class, "javaClassNode2");
        session.flush();

        final Metadata metadata = reader.getMetadata();

        final MetaNodeType metaNode1 = metadata
                .getMetaNodeType(JavaPackageNode.class);
        Assert.assertNotNull(metaNode1);
        Assert.assertEquals(metaNode1.getTypeName(),
                JavaPackageNode.class.getName());

        final MetaNodeType metaNode2 = metadata
                .getMetaNodeType(JavaClassNode.class);
        Assert.assertNotNull(metaNode2);
        Assert.assertEquals(metaNode2.getTypeName(),
                JavaClassNode.class.getName());

    }

    /**
     * Test get meta nodes.
     */
    @Test
    public void testGetMetaNodes() {
        final Context root1 = reader.getContext("1L");

        session.addNode(root1, JavaPackageNode.class, "javaPackageNode1");
        session.addNode(root1, JavaPackageNode.class, "javaPackageNode2");
        session.addNode(root1, JavaClassNode.class, "javaClassNode1");
        final JavaClassNode createdNode = session.addNode(root1, JavaClassNode.class, "javaClassNode2");
        Assert.assertNotNull(createdNode);
        session.flush();

        final Metadata metadata = reader.getMetadata();
        final Iterable<MetaNodeType> metaNodes = metadata.getMetaNodesTypes();
        assertMetaNodes(metaNodes, JavaElementNode.class,
                JavaPackageNode.class, JavaClassNode.class);
    }

    /**
     * Test get meta render hint.
     */
    @Test
    public void testGetMetaRenderHint()
        throws Exception,
            MetaNodeTypeNotFoundException {
        throw new UnsupportedOperationException();
        // final Context root1 = reader.getContext("1L");
        // session.addNode(root1, JavaClassNode.class, "javaClassNode1");
        // session.flush();
        //
        // final Metadata metadata = reader.getMetadata();
        // final MetaNodeType metaNode = metadata
        // .getMetaNodeType(JavaClassNode.class);
        //
        // final MetaRenderHint formatRenderHint = metaNode.get
        // .getMetaRenderHint("format");
        // Assert.assertNotNull(formatRenderHint);
        // Assert.assertEquals(formatRenderHint.getName(), "format");
        // Assert.assertEquals(formatRenderHint.getValue(), "cube");
        //
        // final MetaRenderHint foregroundRenderHint = metaNode
        // .getMetaRenderHint("foreground");
        // Assert.assertNotNull(foregroundRenderHint);
        // Assert.assertEquals(foregroundRenderHint.getName(), "foreground");
        // Assert.assertEquals(foregroundRenderHint.getValue(), "gold");
    }

    /**
     * Test get meta render hints.
     */
    @Test
    public void testGetMetaRenderHints()
        throws Exception {
        throw new UnsupportedOperationException();

        // final Context root1 = reader.getContext("1L");
        // session.addNode(root1, JavaClassNode.class, "javaClassNode1");
        // session.flush();
        //
        // final Metadata metadata = reader.getMetadata();
        // final MetaNodeType metaNode = metadata
        // .getMetaNodeType(JavaClassNode.class);
        // final Iterable<MetaRenderHint> renderHints = metaNode
        // .getMetaRenderHints();
        // Assert.assertEquals(renderHints.size(), 2);
        // for (final MetaRenderHint renderHint : renderHints) {
        // if (renderHint.getName().equals("format")) {
        // Assert.assertEquals(renderHint.getValue(), "cube");
        // } else if (renderHint.getName().equals("foreground")) {
        // Assert.assertEquals(renderHint.getValue(), "gold");
        // } else {
        // Assert.fail();
        // }
        // }
    }

    /**
     * Test get nodes by link with link type.
     */
    @Test
    public void testGetNodesByLinkWithLinkType() {
        final Context root1 = reader.getContext("1L");
        final JavaPackageNode javaPackageNode1 = session.addNode(root1,
                JavaPackageNode.class, "javaPackageNode1");
        final JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");
        final JavaClassNode javaClassNode2 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode2");
        final JavaInnerClassNode javaInnerClassNode1 = session.addNode(root1,
                JavaInnerClassNode.class, "javaInnerClassNode1");
        final JavaInnerClassNode javaInnerClassNode2 = session.addNode(root1,
                JavaInnerClassNode.class, "javaInnerClassNode2");

        session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
                javaClassNode1);
        session.addBidirectionalLink(JavaPackageJavaClass.class,
                javaPackageNode1, javaClassNode2);
        session.addLink(JavaPackageJavaClass.class, javaClassNode1,
                javaPackageNode1);
        session.addLink(JavaPackageJavaClass.class, javaClassNode2,
                javaPackageNode1);

        session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
                javaInnerClassNode1);
        session.addBidirectionalLink(JavaPackageJavaClass.class,
                javaPackageNode1, javaInnerClassNode2);
        session.addLink(JavaPackageJavaClass.class, javaInnerClassNode1,
                javaPackageNode1);
        session.addLink(JavaPackageJavaClass.class, javaInnerClassNode2,
                javaPackageNode1);

        Iterable<? extends Node> nodes = null;
        session.flush();

        nodes = reader.getLinkedNodes(JavaPackageJavaClass.class,
                javaPackageNode1, LinkDirection.UNIDIRECTIONAL);
        assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1,
                javaInnerClassNode2);

        nodes = reader.getLinkedNodes(JavaPackageJavaClass.class,
                javaPackageNode1, LinkDirection.UNIDIRECTIONAL);
        assertNodes(nodes, javaClassNode1, javaClassNode2);

        nodes = reader.getLinkedNodes(JavaPackageJavaClass.class,
                javaPackageNode1, LinkDirection.UNIDIRECTIONAL);
        assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);

        nodes = reader.getLinkedNodes(JavaPackageJavaClass.class,
                javaPackageNode1, LinkDirection.UNIDIRECTIONAL);
        assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1,
                javaInnerClassNode2);

        nodes = reader.getLinkedNodes(JavaPackageJavaClass.class,
                javaPackageNode1, LinkDirection.UNIDIRECTIONAL);
        assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);

    }

    /**
     * Test get nodes by link without link type.
     */
    @Test
    public void testGetNodesByLinkWithoutLinkType() {
        final Context root1 = reader.getContext("1L");
        final JavaPackageNode javaPackageNode1 = session.addNode(root1,
                JavaPackageNode.class, "javaPackageNode1");
        final JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");
        final JavaClassNode javaClassNode2 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode2");
        final JavaInnerClassNode javaInnerClassNode1 = session.addNode(root1,
                JavaInnerClassNode.class, "javaInnerClassNode1");
        final JavaInnerClassNode javaInnerClassNode2 = session.addNode(root1,
                JavaInnerClassNode.class, "javaInnerClassNode2");

        session.addLink(JavaPackageJavaClass.class, javaPackageNode1,
                javaClassNode1);
        session.addBidirectionalLink(JavaPackageJavaClass.class,
                javaPackageNode1, javaInnerClassNode1);

        session.addLink(JavaPackagePublicElement.class, javaPackageNode1,
                javaClassNode2);
        session.addBidirectionalLink(JavaPackagePublicElement.class,
                javaPackageNode1, javaInnerClassNode2);
        session.flush();

        Iterable<? extends Node> nodes = null;

        nodes = reader.getLinkedNodes(javaPackageNode1, LinkDirection.ANY);
        assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1,
                javaInnerClassNode2);

        nodes = reader.getLinkedNodes(javaPackageNode1, LinkDirection.ANY);
        assertNodes(nodes, javaClassNode1, javaClassNode2);

        nodes = reader.getLinkedNodes(javaPackageNode1, LinkDirection.ANY);
        assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);

        nodes = reader.getLinkedNodes(javaPackageNode1, LinkDirection.ANY);
        assertNodes(nodes, javaClassNode1, javaClassNode2, javaInnerClassNode1,
                javaInnerClassNode2);

        nodes = reader.getLinkedNodes(javaPackageNode1, LinkDirection.ANY);
        assertNodes(nodes, javaInnerClassNode1, javaInnerClassNode2);
    }

    /**
     * Test get sub meta node type.
     */
    @Test
    public void testGetSubMetaNodeType()
        throws MetaNodeTypeNotFoundException {
        throw new UnsupportedOperationException();
        //
        // final Context root1 = reader.getContext("1L");
        // final JavaClassNode javaClassNode1 = session.addNode(root1,
        // JavaClassNode.class, "javaClassNode1");
        // session.addChildNode(javaClassNode1,JavaInnerClassNode.class,
        // "javaInnerClassNode1");
        // session.addChildNode(javaClassNode1,JavaMethodNode.class,
        // "javaMethodNode1");
        // session.flush();
        //
        // final Metadata metadata = reader.getMetadata();
        // final MetaNodeType elementType = metadata
        // .getMetaNodeType(JavaElementNode.class);
        // final MetaNodeType javaClassType = elementType
        // .getSubMetaNodeType(JavaClassNode.class);
        // final MetaNodeType javaMethodType = elementType
        // .getSubMetaNodeType(JavaMethodNode.class);
        // Assert.assertNotNull(javaClassType);
        // Assert.assertNotNull(javaMethodType);
        //
        // final MetaNodeType javaInnerClassType = javaClassType
        // .getSubMetaNodeType(JavaInnerClassNode.class);
        // Assert.assertNotNull(javaInnerClassType);
    }

    /**
     * Test get sub meta node types.
     */
    @Test
    public void testGetSubMetaNodeTypes()
        throws MetaNodeTypeNotFoundException {
        throw new UnsupportedOperationException();
        // final Context root1 = reader.getContext("1L");
        // final JavaClassNode javaClassNode1 = session.addNode(root1,
        // JavaClassNode.class, "javaClassNode1");
        // javaClassNode1.addChildNode(JavaInnerClassNode.class,
        // "javaInnerClassNode1");
        // javaClassNode1.addChildNode(JavaMethodNode.class, "javaMethodNode1");
        // session.flush();
        //
        // final Metadata metadata = reader.getMetadata();
        // final MetaNodeType elementType = metadata
        // .getMetaNodeType(JavaElementNode.class);
        //
        // final Iterable<MetaNodeType> elementSubTypes = elementType
        // .getSubMetaNodeTypes();
        // assertMetaNodes(elementSubTypes, JavaClassNode.class,
        // JavaMethodNode.class);
        //
        // final MetaNodeType javaClassType = metadata
        // .getMetaNodeType(JavaClassNode.class);
        // final Iterable<MetaNodeType> javaClassSubTypes = javaClassType
        // .getSubMetaNodeTypes();
        // assertMetaNodes(javaClassSubTypes, JavaInnerClassNode.class);
    }

    /**
     * Test line reference.
     */
    @Test
    public void testLineReference() {
        final Context root1 = reader.getContext("1L");
        final JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");

        javaClassNode1.createLineReference(8, 17, 26, 44, "Hello World!", "1");
        javaClassNode1.createLineReference(71, 80, 35, 53, "Bye World!", "2");

        final TreeLineReference lineRefs = reader
                .getTreeLineReferences(javaClassNode1);
        Assert.assertNotNull(lineRefs);
        Assert.assertEquals(
                SLCollections.iterableToList(lineRefs.getArtifacts()).size(), 2);
        int count = 0;
        for (final ArtifactLineReference artifactlineRef: lineRefs.getArtifacts()) {
            for (final StatementLineReference stmt: artifactlineRef.getStatements()) {
                for (final SimpleLineReference lineRef: stmt.getLineReferences()) {
                    if (artifactlineRef.getArtifactId().equals("1")) {
                        count++;
                        Assert.assertEquals(lineRef.getBeginLine(), 8);
                        Assert.assertEquals(lineRef.getEndLine(), 17);
                        Assert.assertEquals(lineRef.getBeginColumn(), 26);
                        Assert.assertEquals(lineRef.getEndColumn(), 44);
                        Assert.assertEquals(stmt.getStatement(), "Hello World!");

                    } else if (artifactlineRef.getArtifactId().equals("2")) {
                        count++;
                        Assert.assertEquals(lineRef.getBeginLine(), 71);
                        Assert.assertEquals(lineRef.getEndLine(), 80);
                        Assert.assertEquals(lineRef.getBeginColumn(), 35);
                        Assert.assertEquals(lineRef.getEndColumn(), 53);
                        Assert.assertEquals(stmt.getStatement(), "Bye World!");

                    } else {
                        Assert.fail();
                    }
                }
            }
        }
        Assert.assertThat(count, Is.is(2));
    }

    /**
     * Test line reference.
     */
    @Test
    public void testLineReferenceWithArtifactId() {
        final Context root1 = reader.getContext("1L");
        final JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");

        javaClassNode1.createLineReference(8, 17, 26, 44, "Hello World!", "1");
        javaClassNode1.createLineReference(71, 80, 35, 53, "Bye World!", "2");

        TreeLineReference lineRefs = reader.getTreeLineReferences(
                javaClassNode1, "1");
        Assert.assertNotNull(lineRefs);
        Assert.assertEquals(
                SLCollections.iterableToList(lineRefs.getArtifacts()).size(), 2);
        int count = 0;
        for (final ArtifactLineReference artifactlineRef: lineRefs.getArtifacts()) {
            for (final StatementLineReference stmt: artifactlineRef.getStatements()) {
                for (final SimpleLineReference lineRef: stmt.getLineReferences()) {
                    if (artifactlineRef.getArtifactId().equals("1")) {
                        count++;
                        Assert.assertEquals(lineRef.getBeginLine(), 8);
                        Assert.assertEquals(lineRef.getEndLine(), 17);
                        Assert.assertEquals(lineRef.getBeginColumn(), 26);
                        Assert.assertEquals(lineRef.getEndColumn(), 44);
                        Assert.assertEquals(stmt.getStatement(), "Hello World!");

                    } else {
                        Assert.fail();
                    }
                }
            }
        }
        Assert.assertThat(count, Is.is(1));
        lineRefs = reader.getTreeLineReferences(javaClassNode1, "2");
        Assert.assertNotNull(lineRefs);
        Assert.assertEquals(
                SLCollections.iterableToList(lineRefs.getArtifacts()).size(), 2);
        count = 0;
        for (final ArtifactLineReference artifactlineRef: lineRefs.getArtifacts()) {
            for (final StatementLineReference stmt: artifactlineRef.getStatements()) {
                for (final SimpleLineReference lineRef: stmt.getLineReferences()) {
                    if (artifactlineRef.getArtifactId().equals("2")) {
                        count++;
                        Assert.assertEquals(lineRef.getBeginLine(), 71);
                        Assert.assertEquals(lineRef.getEndLine(), 80);
                        Assert.assertEquals(lineRef.getBeginColumn(), 35);
                        Assert.assertEquals(lineRef.getEndColumn(), 53);
                        Assert.assertEquals(stmt.getStatement(), "Bye World!");

                    } else {
                        Assert.fail();
                    }
                }
            }
        }
        Assert.assertThat(count, Is.is(1));
    }

    /**
     * Test links removal by node deletion.
     */
    @Test
    public void testLinksRemovalByNodeDeletion() {
        final Context root1 = reader.getContext("1L");
        final JavaPackageNode javaPackageNode1 = session.addNode(root1,
                JavaPackageNode.class, "javaPackageNode1");
        final JavaClassNode javaClassNode1 = session.addChildNode(
                javaPackageNode1, JavaClassNode.class, "javaClassNode1");
        final JavaClassNode javaClassNode2 = session.addChildNode(
                javaPackageNode1, JavaClassNode.class, "javaClassNode2");

        final JavaMethodNode javaMethodNode1A = session.addChildNode(
                javaClassNode1, JavaMethodNode.class, "javaMethodNode1A");
        final JavaMethodNode javaMethodNode1B = session.addChildNode(
                javaClassNode1, JavaMethodNode.class, "javaMethodNode1B");

        final JavaMethodNode javaMethodNode2A = session.addChildNode(
                javaClassNode2, JavaMethodNode.class, "javaMethodNode2A");
        final JavaMethodNode javaMethodNode2B = session.addChildNode(
                javaClassNode2, JavaMethodNode.class, "javaMethodNode2B");

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
        session.flush();
        Iterable<Link> links = reader.getLinks(javaPackageNode1, null,
                LinkDirection.UNIDIRECTIONAL);
        Assert.assertEquals(SLCollections.iterableToList(links).size(), 6);
        links = reader.getLinks(javaClassNode1, null,
                LinkDirection.UNIDIRECTIONAL);
        Assert.assertEquals(SLCollections.iterableToList(links).size(), 4);
        session.removeNode(javaPackageNode1);
        session.flush();
        links = reader.getLinks(javaPackageNode1, null, LinkDirection.ANY);
        Assert.assertEquals(SLCollections.iterableToList(links).size(), 0);
        links = reader.getLinks(javaClassNode1, null, LinkDirection.ANY);
        Assert.assertEquals(SLCollections.iterableToList(links).size(), 0);
    }

    /**
     * Test link types for link deletion mark and unmark case.
     */
    @Test
    public void testLinkTypesForLinkDeletionMarkAndUnmarkCase()
            throws Exception {
        Context root1 = reader.getContext("1L");
        JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");
        JavaMethodNode javaMethodNode1 = session.addNode(root1,
                JavaMethodNode.class, "javaMethodNode1");
        final JavaMethodNode javaMethodNode2 = session.addNode(root1,
                JavaMethodNode.class, "javaMethodNode2");

        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode1);
        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode2);

        final List<Class<? extends Link>> linkTypesForLinkDeletion = new ArrayList<Class<? extends Link>>();
        linkTypesForLinkDeletion.add(JavaClassJavaMethodSimpleLink.class);
        session.addNode(root1, JavaMethodNode.class, "javaMethodNode2",
                linkTypesForLinkDeletion, null);

        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode2);

        session.flush();

        root1 = reader.getContext("1L");
        javaClassNode1 = SLCollections.firstOf(reader.findNodesByName(
                JavaClassNode.class, "javaClassNode1", true, root1));
        javaMethodNode1 = SLCollections.firstOf(reader.findNodesByName(
                JavaMethodNode.class, "javaMethodNode1", true, root1));

        final Iterable<? extends Link> links = reader.getLinks(javaClassNode1,
                null, LinkDirection.ANY);
        Assert.assertEquals(SLCollections.iterableToList(links).size(), 2);
    }

    /**
     * Test link types for link deletion mark case.
     */
    @Test
    public void testLinkTypesForLinkDeletionMarkCase()
        throws Exception {
        Context root1 = reader.getContext("1L");
        JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");
        JavaMethodNode javaMethodNode1 = session.addNode(root1,
                JavaMethodNode.class, "javaMethodNode1");
        final JavaMethodNode javaMethodNode2 = session.addNode(root1,
                JavaMethodNode.class, "javaMethodNode2");

        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode1);
        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode2);
        final List<Class<? extends Link>> linkTypesForLinkDeletion = new ArrayList<Class<? extends Link>>();
        linkTypesForLinkDeletion.add(JavaClassJavaMethodSimpleLink.class);
        session.addNode(root1, JavaMethodNode.class, "javaMethodNode2",
                linkTypesForLinkDeletion, null);

        session.flush();

        root1 = reader.getContext("1L");
        javaClassNode1 = SLCollections.firstOf(reader.findNodesByName(
                JavaClassNode.class, "javaClassNode1", true, root1));
        javaMethodNode1 = SLCollections.firstOf(reader.findNodesByName(
                JavaMethodNode.class, "javaMethodNode1", true, root1));
        final Iterable<? extends Link> links = reader.getLinks(javaClassNode1,
                null, LinkDirection.ANY);
        for (final Link k: links) {
            System.err.println(">>> " + k.getSource().getName() + " - "
                    + k.getTarget().getName());
        }

        Assert.assertEquals(SLCollections.iterableToList(links).size(), 1);

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
        JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");
        JavaMethodNode javaMethodNode1 = session.addChildNode(javaClassNode1,
                JavaMethodNode.class, "javaMethodNode1");
        final JavaMethodNode javaMethodNode2 = session.addChildNode(
                javaClassNode1, JavaMethodNode.class, "javaMethodNode2");

        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode1);
        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode2);

        final List<Class<? extends Link>> linkTypesForLinkedNodesDeletion = new ArrayList<Class<? extends Link>>();
        linkTypesForLinkedNodesDeletion
                .add(JavaClassJavaMethodSimpleLink.class);
        session.addNode(root1, JavaClassNode.class, "javaClassNode1", null,
                linkTypesForLinkedNodesDeletion);

        javaMethodNode1 = session.addChildNode(javaClassNode1,
                JavaMethodNode.class, "javaMethodNode1");

        session.flush();

        root1 = reader.getContext("1L");
        javaClassNode1 = SLCollections.firstOf(reader.findNodesByName(
                JavaClassNode.class, "javaClassNode1", true, root1));
        javaMethodNode1 = reader.getChildNode(javaClassNode1,
                JavaMethodNode.class, "javaMethodNode1");
        Assert.assertThat(javaMethodNode1, Is.is(IsNull.notNullValue()));
    }

    /**
     * Test link types for linked node deletion mark case.
     */
    @Test
    public void testLinkTypesForLinkedNodeDeletionMarkCase()
        throws Exception {
        Context root1 = reader.getContext("1L");
        JavaClassNode javaClassNode1 = session.addNode(root1,
                JavaClassNode.class, "javaClassNode1");
        final JavaMethodNode javaMethodNode1 = session.addNode(root1,
                JavaMethodNode.class, "javaMethodNode1");
        final JavaMethodNode javaMethodNode2 = session.addNode(root1,
                JavaMethodNode.class, "javaMethodNode2");

        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode1);
        session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1,
                javaMethodNode2);

        final List<Class<? extends Link>> linkTypesForLinkedNodesDeletion = new ArrayList<Class<? extends Link>>();
        linkTypesForLinkedNodesDeletion
                .add(JavaClassJavaMethodSimpleLink.class);
        session.addNode(root1, JavaClassNode.class, "javaClassNode1", null,
                linkTypesForLinkedNodesDeletion);

        session.flush();

        root1 = reader.getContext("1L");
        javaClassNode1 = SLCollections.firstOf(reader.findNodesByName(
                JavaClassNode.class, "javaClassNode1", true, root1));

        final Iterable<Node> nodes = reader.getChildrenNodes(javaClassNode1);
        ;
        Assert.assertFalse(nodes.iterator().hasNext());
    }

    @Test
    public void testMetaLinkGetDescription()
        throws Exception {
        throw new UnsupportedOperationException();
        //
        // final Context root1 = reader.getContext("1L");
        // final JavaClassNode javaClassNode = session.addNode(root1,
        // JavaClassNode.class, "javaClassNode");
        // final JavaMethodNode javaMethodNode = session.addNode(root1,
        // JavaMethodNode.class, "javaMethodNode");
        // session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode,
        // javaMethodNode);
        // session.flush();
        //
        // final Metadata metadata = reader.getMetadata();
        // final MetaLink metaLink = metadata
        // .getMetaLinkType(JavaClassJavaMethodSimpleLink.class)
        // .getMetalinks().iterator().next();
        // final String description = metaLink.getDescription();
        // Assert.assertNotNull(description);
        // Assert.assertEquals(description, "Java Class to Java Method Link");
        // Assert.assertEquals(metaLink.getVisibility(),
        // Visibility.VisibilityLevel.PUBLIC);
    }

    /**
     * Test meta node get description.
     */
    @Test
    public void testMetaNodeGetDescription()
            throws MetaNodeTypeNotFoundException {
        final Context root1 = reader.getContext("1L");
        session.addNode(root1, JavaClassNode.class, "javaClassNode1");
        session.flush();

        final Metadata metadata = reader.getMetadata();
        final MetaNodeType metaNode = metadata
                .getMetaNodeType(JavaClassNode.class);
        final String description = metaNode.getDescription();
        Assert.assertNotNull(description);
        Assert.assertEquals(description, "Java Class");
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
}
