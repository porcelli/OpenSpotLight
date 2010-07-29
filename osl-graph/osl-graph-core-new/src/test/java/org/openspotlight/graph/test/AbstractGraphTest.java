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
package org.openspotlight.graph.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.util.SLCollections;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.FullGraphSession;
import org.openspotlight.graph.GraphLocation;
import org.openspotlight.graph.GraphSessionFactory;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.LinkType;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.SimpleGraphSession;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.manipulation.GraphWriter;
import org.openspotlight.graph.test.link.TypeExtends;
import org.openspotlight.graph.test.node.JavaMember;
import org.openspotlight.graph.test.node.JavaMemberField;
import org.openspotlight.graph.test.node.JavaType;
import org.openspotlight.graph.test.node.JavaTypeClass;
import org.openspotlight.graph.test.node.JavaTypeInterface;

import com.google.inject.Injector;

public abstract class AbstractGraphTest {

	protected Injector injector;

	protected abstract Injector createInjector() throws Exception;

	protected abstract void clearData() throws Exception;

	protected GraphLocation location() {
		return GraphLocation.SERVER;
	}

	protected String context1() {
		return "context1";
	}

	protected String context2() {
		return "context2";
	}

	private boolean firstRun = true;

	private FullGraphSession fullGraphSession;

	private SimpleGraphSession simpleGraphSession;

	@Before
	public void beforeTest() throws Exception {
		if (firstRun) {
			injector = createInjector();
			GraphSessionFactory sessionFactory = injector
					.getInstance(GraphSessionFactory.class);
			simpleGraphSession = sessionFactory.openSimple();
			fullGraphSession = sessionFactory.openFull();
			firstRun = false;
		}
		clearData();
	}

	@Test
	public void shouldaddAndFindOneNodeInManyWays() {
		String nodeName = "nodeName";
		String caption1 = "caption1";
		String transientValue = "transientValue";
		String typeName = "typeName";
		boolean publicClass = true;

		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		JavaType node1 = writer.addNode(context1, JavaType.class, nodeName);
		node1.setCaption(caption1);
		node1.setPublicClass(publicClass);
		node1.setTypeName(typeName);
		node1.setTransientValue(transientValue);
		fullGraphSession.toServer().flush();

		Iterable<JavaType> oneNode1 = simpleFromLocation.findNodesByName(
				JavaType.class, nodeName, true, context1);
		Iterable<JavaType> oneNode3 = simpleFromLocation.findNodesByName(
				JavaType.class, nodeName, true, context1);
		Iterable<JavaType> oneNode4 = simpleFromLocation.findNodesByName(
				JavaType.class, nodeName, true, context1);

		Iterator<JavaType> itOneNode1 = oneNode1.iterator();
		Iterator<JavaType> itOneNode3 = oneNode3.iterator();
		Iterator<JavaType> itOneNode4 = oneNode4.iterator();

		assertThat(itOneNode1.hasNext(), is(true));
		assertThat(itOneNode3.hasNext(), is(true));
		assertThat(itOneNode4.hasNext(), is(true));

		JavaType sameNode1 = itOneNode1.next();
		JavaType sameNode3 = itOneNode3.next();
		JavaType sameNode4 = itOneNode4.next();

		assertThat(sameNode1, is(notNullValue()));
		assertThat(sameNode3, is(notNullValue()));
		assertThat(sameNode4, is(notNullValue()));

		assertThat(sameNode1, is(sameNode3));
		assertThat(sameNode1, is(sameNode4));

		assertThat(sameNode1.getCaption(), is(caption1));
		assertThat(sameNode1.isPublicClass(), is(publicClass));
		assertThat(sameNode1.getTypeName(), is(typeName));
		assertThat(sameNode1.getTransientValue(), is(nullValue()));

		assertThat(sameNode3.getCaption(), is(caption1));
		assertThat(sameNode3.isPublicClass(), is(publicClass));
		assertThat(sameNode3.getTypeName(), is(typeName));
		assertThat(sameNode3.getTransientValue(), is(nullValue()));

		assertThat(sameNode4.getCaption(), is(caption1));
		assertThat(sameNode4.isPublicClass(), is(publicClass));
		assertThat(sameNode4.getTypeName(), is(typeName));
		assertThat(sameNode4.getTransientValue(), is(nullValue()));

		assertThat(simpleFromLocation.getContext(sameNode1), is(context1));
		assertThat(simpleFromLocation.getContext(sameNode3), is(context1));
		assertThat(simpleFromLocation.getContext(sameNode4), is(context1));
	}

	@Test
	public void shouldNotFindInvalidNode() throws Exception {
		String invalidNodeName = "invalidNodeName";
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		Iterable<Node> empty1 = simpleFromLocation.findNodesByName(
				invalidNodeName, context1);
		Iterable<JavaType> empty3 = simpleFromLocation.findNodesByName(
				JavaType.class, invalidNodeName, true, context1);
		Iterable<JavaType> empty4 = simpleFromLocation.findNodesByName(
				JavaType.class, invalidNodeName, true, context1);
		Iterator<Node> emptyIt1 = empty1.iterator();
		Iterator<JavaType> emptyIt3 = empty3.iterator();
		Iterator<JavaType> emptyIt4 = empty4.iterator();

		assertThat(emptyIt1.hasNext(), is(false));
		assertThat(emptyIt3.hasNext(), is(false));
		assertThat(emptyIt4.hasNext(), is(false));
	}

	@Test
	public void shouldInsertAndFindOneNodeAndNotFindInvalidOne()
			throws Exception {
		String nodeName = "nodeName";
		String invalidNodeName = "invalidNodeName";

		String caption1 = "caption1";
		String transientValue = "transientValue";
		String typeName = "typeName";
		boolean publicClass = true;

		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		JavaType node1 = writer.addNode(context1, JavaType.class, nodeName);
		node1.setCaption(caption1);
		node1.setPublicClass(publicClass);
		node1.setTypeName(typeName);
		node1.setTransientValue(transientValue);
		fullGraphSession.toServer().flush();

		Iterable<Node> oneNode1 = simpleFromLocation.findNodesByName(nodeName,
				context1);

		Iterator<Node> itOneNode1 = oneNode1.iterator();

		assertThat(itOneNode1.hasNext(), is(true));

		JavaType sameNode1 = (JavaType) itOneNode1.next();

		assertThat(sameNode1, is(notNullValue()));

		assertThat(sameNode1.getCaption(), is(caption1));
		assertThat(sameNode1.isPublicClass(), is(publicClass));
		assertThat(sameNode1.getTypeName(), is(typeName));
		assertThat(sameNode1.getTransientValue(), is(nullValue()));

		assertThat(simpleFromLocation.getContext(sameNode1), is(context1));

		Iterable<Node> empty1 = simpleFromLocation.findNodesByName(
				invalidNodeName, context1);
		Iterator<Node> emptyIt1 = empty1.iterator();
		assertThat(emptyIt1.hasNext(), is(false));

	}

	@Test
	public void shouldaddAnHierarchy() throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaTypeClass rootClass1Node = writer.addNode(context1,
				JavaTypeClass.class, rootClass1);
		String child1 = "child1";
		String child2 = "child2";
		String child3 = "child3";
		JavaMemberField child1Node = writer.addChildNode(rootClass1Node,
				JavaMemberField.class, child1);
		assertThat(child1Node.getParentId(),is(rootClass1Node.getId()));
		JavaMemberField child2Node = writer.addChildNode(rootClass1Node,
				JavaMemberField.class, child2);
		assertThat(child2Node.getParentId(),is(rootClass1Node.getId()));
		JavaTypeInterface child3Node = writer.addChildNode(rootClass1Node,
				JavaTypeInterface.class, child3);
		assertThat(child3Node.getParentId(),is(rootClass1Node.getId()));

		String rootClass2 = "rootClass2";
		JavaTypeClass rootClass2Node = writer.addNode(context1,
				JavaTypeClass.class, rootClass2);
		String child4 = "child4";
		String child5 = "child5";
		String child6 = "child6";
		JavaMemberField child4Node = writer.addChildNode(rootClass2Node,
				JavaMemberField.class, child4);
		JavaMemberField child5Node = writer.addChildNode(rootClass2Node,
				JavaMemberField.class, child5);
		JavaTypeInterface child6Node = writer.addChildNode(rootClass2Node,
				JavaTypeInterface.class, child6);

		writer.flush();

		Iterable<JavaTypeClass> rootNodes = simpleFromLocation.findNodesByName(
				JavaTypeClass.class, "rootClass1", true, context1);

		Iterator<JavaTypeClass> rootNodesIt = rootNodes.iterator();

		assertThat(rootNodesIt.hasNext(), is(true));
		JavaTypeClass retrievedRootClass1 = rootNodesIt.next();
		assertThat(retrievedRootClass1, is(rootClass1Node));
		List<JavaMember> children1AsSet = SLCollections.iterableToList(simpleFromLocation
				.getChildrenNodes(retrievedRootClass1, JavaMember.class));
		assertThat(children1AsSet.contains(child1Node), is(true));
		assertThat(children1AsSet.contains(child2Node), is(true));
		assertThat(children1AsSet.contains(child3Node), is(false));
		assertThat(children1AsSet.size(), is(2));

		List<JavaType> children2AsSet = SLCollections.iterableToList(simpleFromLocation
				.getChildrenNodes(retrievedRootClass1, JavaType.class));
		assertThat(children2AsSet.contains(child1Node), is(false));
		assertThat(children2AsSet.contains(child2Node), is(false));
		assertThat(children2AsSet.contains(child3Node), is(true));
		assertThat(children2AsSet.size(), is(1));

		Iterable<JavaTypeClass> rootNodes2 = simpleFromLocation
				.findNodesByName(JavaTypeClass.class, "rootClass2", true,
						context1);

		Iterator<JavaTypeClass> rootNodes2It = rootNodes2.iterator();

		assertThat(rootNodes2It.hasNext(), is(true));
		JavaTypeClass retrievedRootClass2 = rootNodes2It.next();
		assertThat(retrievedRootClass2, is(rootClass2Node));
		List<JavaMember> children3AsSet = SLCollections.iterableToList(simpleFromLocation
				.getChildrenNodes(retrievedRootClass2, JavaMember.class));
		assertThat(children3AsSet.contains(child4Node), is(true));
		assertThat(children3AsSet.contains(child5Node), is(true));
		assertThat(children3AsSet.contains(child6Node), is(false));
		assertThat(children3AsSet.size(), is(2));

		List<JavaType> children4AsSet = SLCollections.iterableToList(simpleFromLocation
				.getChildrenNodes(retrievedRootClass2, JavaType.class));
		assertThat(children4AsSet.contains(child4Node), is(false));
		assertThat(children4AsSet.contains(child5Node), is(false));
		assertThat(children4AsSet.contains(child6Node), is(true));
		assertThat(children4AsSet.size(), is(1));

	}

	@Test
	public void shouldChangeNodeTypeWhenUsingValidNodeType() throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		writer.flush();

		JavaType rootClass2Node = writer.addNode(context1, JavaTypeClass.class,
				rootClass1);
		writer.flush();
		assertThat(rootClass1Node, is(rootClass2Node));

		JavaType foundNode1 = SLCollections.firstOf(simpleFromLocation
				.findNodesByName(JavaType.class, rootClass1, true, context1));
		assertThat(foundNode1, is(rootClass2Node));
		assertThat(foundNode1 instanceof JavaTypeClass, is(true));
		JavaType rootClass3Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		assertThat(rootClass3Node instanceof JavaTypeClass, is(true));

		writer.flush();

	}

	@Ignore // TODO will be possible to add nodes with same names and different types? :-)
	@Test(expected = ClassCastException.class)
	public void shouldNotChangeNodeTypeWhenUsingInvalidNodeType()
			throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		writer.addNode(context1, JavaType.class, rootClass1);
		writer.flush();

		writer.addNode(context1, JavaMemberField.class, rootClass1);
	}

	@Test
	public void shouldRemoveNode() throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaType rootClassNode1 = writer.addNode(context1, JavaType.class,
				rootClass1);
		writer.flush();
		writer.removeNode(rootClassNode1);

		writer.flush();
		Iterable<JavaType> empty = simpleFromLocation.findNodesByName(
				JavaType.class, null, true, context1);

		assertThat(empty.iterator().hasNext(), is(false));
	}

	@Test
	public void shouldRemoveChildNode() throws Exception {

		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaType rootClassNode1 = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClassNode2 = writer.addChildNode(rootClassNode1,
				JavaType.class, rootClass1);
		writer.flush();
		List<JavaType> nodes = SLCollections.iterableToList(simpleFromLocation.findNodesByName(
				JavaType.class, null, true, context1));

		assertThat(nodes.size(), is(2));
		assertThat(nodes.contains(rootClassNode1), is(true));
		assertThat(nodes.contains(rootClassNode2), is(true));
		writer.removeNode(rootClassNode2);
		writer.flush();
		List<JavaType> nodes2 = SLCollections.iterableToList(simpleFromLocation
				.findNodesByName(JavaType.class, null, true, context1));

		assertThat(nodes2.size(), is(1));
		assertThat(nodes2.contains(rootClassNode1), is(true));
		assertThat(nodes2.contains(rootClassNode2), is(false));

	}

	@Test
	public void shouldRemoveParentAndChildNode() throws Exception {

		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaType rootClassNode1 = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClassNode2 = writer.addChildNode(rootClassNode1,
				JavaType.class, rootClass1);
		writer.flush();
		List<JavaType> nodes = SLCollections.iterableToList(simpleFromLocation.findNodesByName(
				JavaType.class, null, true, context1));

		assertThat(nodes.size(), is(2));
		assertThat(nodes.contains(rootClassNode1), is(true));
		assertThat(nodes.contains(rootClassNode2), is(true));
		writer.removeNode(rootClassNode1);
		writer.flush();
		List<JavaType> nodes2 = SLCollections.iterableToList(simpleFromLocation
				.findNodesByName(JavaType.class, null, true, context1));

		assertThat(nodes2.size(), is(0));
	}

	@Test
	public void shouldChangeNodeProperties() throws Exception {
		String firstCaption = "firstCaption";
		boolean firstPublicClass = true;
		String firstTypeName = "firstTypeName";
		String secondCaption = "secondCaption";
		boolean secondPublicClass = !firstPublicClass;
		String secondTypeName = "secondTypeName";

		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaType rootClassNode1 = writer.addNode(context1, JavaType.class,
				rootClass1);
		rootClassNode1.setCaption(firstCaption);
		rootClassNode1.setPublicClass(firstPublicClass);
		rootClassNode1.setTypeName(firstTypeName);
		writer.flush();
		JavaType firstFound = SLCollections.firstOf(simpleFromLocation
				.findNodesByName(JavaType.class, null, true, context1));
		assertThat(firstFound.getCaption(), is(firstCaption));
		assertThat(firstFound.getTypeName(), is(firstTypeName));
		assertThat(firstFound.isPublicClass(), is(firstPublicClass));
		firstFound.setCaption(secondCaption);
		firstFound.setPublicClass(secondPublicClass);
		firstFound.setTypeName(secondTypeName);
		simpleGraphSession.flushChangedProperties(firstFound);

		JavaType secondFound = SLCollections.firstOf(simpleFromLocation
				.findNodesByName(JavaType.class, null, true, context1));
		assertThat(secondFound.getCaption(), is(secondCaption));
		assertThat(secondFound.getTypeName(), is(secondTypeName));
		assertThat(secondFound.isPublicClass(), is(secondPublicClass));

	}

	@Test
	public void shouldHaveSameNodesOnDifferentContexts() throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		Context context2 = simpleFromLocation.getContext(context2());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClass2Node = writer.addNode(context2, JavaType.class,
				rootClass1);
		writer.flush();

		List<JavaType> result = SLCollections.iterableToList(simpleFromLocation
				.findNodesByName(JavaType.class, null, true, context1, context2));
		assertThat(result.size(), is(2));
		assertThat(result.contains(rootClass1Node), is(true));
		assertThat(result.contains(rootClass2Node), is(true));
		assertThat(rootClass1Node.equals(rootClass2Node), is(false));

	}

	@Test
	public void shouldHaveDifferentWeightsForDifferentNodeTypes()
			throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		Context context2 = simpleFromLocation.getContext(context2());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaMember rootClass2Node = writer.addNode(context2, JavaMember.class,
				rootClass1);
		writer.flush();
		assertThat(rootClass1Node.getNumericType().equals(
				rootClass2Node.getNumericType()), is(false));

	}

	@Test
	public void shouldHaveBiggerHeightsForInheritedTypes() throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		Context context2 = simpleFromLocation.getContext(context2());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaTypeClass rootClass2Node = writer.addNode(context2,
				JavaTypeClass.class, rootClass1);
		writer.flush();
		assertThat(rootClass1Node.getNumericType().equals(
				rootClass2Node.getNumericType()), is(false));
		assertThat(rootClass1Node.getNumericType().compareTo(
				rootClass2Node.getNumericType()) < 0, is(true));
	}

	public void shouldaddAndRetrieveUnidirectionalLinksOnSameContext()
			throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();
		String rootClass1 = "rootClass1";
		String rootClass2 = "rootClass2";
		String rootClass3 = "rootClass3";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClass2Node = writer.addNode(context1, JavaType.class,
				rootClass2);
		JavaType rootClass3Node = writer.addNode(context1, JavaType.class,
				rootClass3);
		TypeExtends link1 = writer.addLink(TypeExtends.class, rootClass1Node,
				rootClass2Node);
		TypeExtends link2 = writer.addLink(TypeExtends.class, rootClass1Node,
				rootClass3Node);

		writer.flush();

		List<Link> twoLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.UNIDIRECTIONAL));
		assertThat(twoLinks.size(), is(2));
		assertThat(twoLinks.contains(link1), is(true));
		assertThat(twoLinks.contains(link2), is(true));

		List<Link> emptyLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.BIDIRECTIONAL));
		assertThat(emptyLinks.size(), is(0));

		List<Link> linkFromNode2 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass2Node, LinkType.UNIDIRECTIONAL));
		assertThat(linkFromNode2.size(), is(1));
		assertThat(linkFromNode2.contains(link1), is(true));
		assertThat(linkFromNode2.contains(link2), is(false));

		List<Link> linkFromNode3 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass3Node, LinkType.UNIDIRECTIONAL));
		assertThat(linkFromNode3.size(), is(1));
		assertThat(linkFromNode3.contains(link2), is(true));
		assertThat(linkFromNode3.contains(link1), is(false));

	}

	public void shouldaddAndRetrieveBidirectionalLinksOnSameContext()
			throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();
		String rootClass1 = "rootClass1";
		String rootClass2 = "rootClass2";
		String rootClass3 = "rootClass3";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClass2Node = writer.addNode(context1, JavaType.class,
				rootClass2);
		JavaType rootClass3Node = writer.addNode(context1, JavaType.class,
				rootClass3);
		TypeExtends link1 = writer.addBidirectionalLink(TypeExtends.class,
				rootClass1Node, rootClass2Node);
		TypeExtends link2 = writer.addBidirectionalLink(TypeExtends.class,
				rootClass1Node, rootClass3Node);

		TypeExtends link3 = writer.addBidirectionalLink(TypeExtends.class,
				rootClass2Node, rootClass1Node);
		TypeExtends link4 = writer.addBidirectionalLink(TypeExtends.class,
				rootClass3Node, rootClass1Node);

		writer.flush();

		assertThat(link1, is(link3));
		assertThat(link2, is(link4));

		List<Link> twoLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.BIDIRECTIONAL));
		assertThat(twoLinks.size(), is(2));
		assertThat(twoLinks.contains(link1), is(true));
		assertThat(twoLinks.contains(link2), is(true));

		List<Link> emptyLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.UNIDIRECTIONAL));
		assertThat(emptyLinks.size(), is(0));

		List<Link> linkFromNode2 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass2Node, LinkType.BIDIRECTIONAL));
		assertThat(linkFromNode2.size(), is(1));
		assertThat(linkFromNode2.contains(link1), is(true));
		assertThat(linkFromNode2.contains(link2), is(false));

		List<Link> linkFromNode3 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass3Node, LinkType.BIDIRECTIONAL));
		assertThat(linkFromNode3.size(), is(1));
		assertThat(linkFromNode3.contains(link2), is(true));
		assertThat(linkFromNode3.contains(link1), is(false));
	}

	public void shouldaddAndRetrieveUniAndBidirectionalLinksOnSameContext()
			throws Exception {

		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		String rootClass2 = "rootClass2";
		String rootClass3 = "rootClass3";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClass2Node = writer.addNode(context1, JavaType.class,
				rootClass2);
		JavaType rootClass3Node = writer.addNode(context1, JavaType.class,
				rootClass3);
		TypeExtends link1 = writer.addLink(TypeExtends.class, rootClass1Node,
				rootClass2Node);
		TypeExtends link2 = writer.addLink(TypeExtends.class, rootClass1Node,
				rootClass3Node);

		String rootClass1bid = "rootClass1bid";
		String rootClass2bid = "rootClass2bid";
		String rootClass3bid = "rootClass3bid";
		JavaType rootClass1BidNode = writer.addNode(context1, JavaType.class,
				rootClass1bid);
		JavaType rootClass2BidNode = writer.addNode(context1, JavaType.class,
				rootClass2bid);
		JavaType rootClass3BidNode = writer.addNode(context1, JavaType.class,
				rootClass3bid);
		TypeExtends link1Bid = writer.addBidirectionalLink(TypeExtends.class,
				rootClass1BidNode, rootClass2BidNode);
		TypeExtends link2Bid = writer.addBidirectionalLink(TypeExtends.class,
				rootClass1BidNode, rootClass3BidNode);

		TypeExtends link3Bid = writer.addBidirectionalLink(TypeExtends.class,
				rootClass2BidNode, rootClass1BidNode);
		TypeExtends link4Bid = writer.addBidirectionalLink(TypeExtends.class,
				rootClass3BidNode, rootClass1BidNode);

		writer.flush();

		assertThat(link1Bid, is(link3Bid));
		assertThat(link2Bid, is(link4Bid));

		List<Link> twoBidLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1BidNode, null, LinkType.BIDIRECTIONAL));
		assertThat(twoBidLinks.size(), is(2));
		assertThat(twoBidLinks.contains(link1Bid), is(true));
		assertThat(twoBidLinks.contains(link2Bid), is(true));

		List<Link> bidLinkFromNode2 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass2BidNode, LinkType.BIDIRECTIONAL));
		assertThat(bidLinkFromNode2.size(), is(1));
		assertThat(bidLinkFromNode2.contains(link1Bid), is(true));
		assertThat(bidLinkFromNode2.contains(link2Bid), is(false));

		List<Link> bidLinkFromNode3 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass3BidNode, LinkType.BIDIRECTIONAL));
		assertThat(bidLinkFromNode3.size(), is(1));
		assertThat(bidLinkFromNode3.contains(link2Bid), is(true));
		assertThat(bidLinkFromNode3.contains(link1Bid), is(false));
		List<Link> twoLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.UNIDIRECTIONAL));
		assertThat(twoLinks.size(), is(2));
		assertThat(twoLinks.contains(link1), is(true));
		assertThat(twoLinks.contains(link2), is(true));

		List<Link> emptyLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.BIDIRECTIONAL));
		assertThat(emptyLinks.size(), is(0));

		List<Link> linkFromNode2 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass2Node, LinkType.UNIDIRECTIONAL));
		assertThat(linkFromNode2.size(), is(1));
		assertThat(linkFromNode2.contains(link1), is(true));
		assertThat(linkFromNode2.contains(link2), is(false));

		List<Link> linkFromNode3 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass3Node, LinkType.UNIDIRECTIONAL));
		assertThat(linkFromNode3.size(), is(1));
		assertThat(linkFromNode3.contains(link2), is(true));
		assertThat(linkFromNode3.contains(link1), is(false));

	}

	public void shouldaddAndRetrieveUnidirectionalLinksOnDiferentContext()
			throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		Context context2 = simpleFromLocation.getContext(context2());
		GraphWriter writer = fullGraphSession.toServer();
		String rootClass1 = "rootClass1";
		String rootClass2 = "rootClass2";
		String rootClass3 = "rootClass3";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClass2Node = writer.addNode(context2, JavaType.class,
				rootClass2);
		JavaType rootClass3Node = writer.addNode(context1, JavaType.class,
				rootClass3);
		TypeExtends link1 = writer.addLink(TypeExtends.class, rootClass1Node,
				rootClass2Node);
		TypeExtends link2 = writer.addLink(TypeExtends.class, rootClass1Node,
				rootClass3Node);

		writer.flush();

		List<Link> twoLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.UNIDIRECTIONAL));
		assertThat(twoLinks.size(), is(2));
		assertThat(twoLinks.contains(link1), is(true));
		assertThat(twoLinks.contains(link2), is(true));

		List<Link> emptyLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.BIDIRECTIONAL));
		assertThat(emptyLinks.size(), is(0));

		List<Link> linkFromNode2 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass2Node, LinkType.UNIDIRECTIONAL));
		assertThat(linkFromNode2.size(), is(1));
		assertThat(linkFromNode2.contains(link1), is(true));
		assertThat(linkFromNode2.contains(link2), is(false));

		List<Link> linkFromNode3 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass3Node, LinkType.UNIDIRECTIONAL));
		assertThat(linkFromNode3.size(), is(1));
		assertThat(linkFromNode3.contains(link2), is(true));
		assertThat(linkFromNode3.contains(link1), is(false));

	}

	public void shouldaddAndRetrieveBidirectionalLinksOnDiferentContext()
			throws Exception {
		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		Context context2 = simpleFromLocation.getContext(context2());
		GraphWriter writer = fullGraphSession.toServer();
		String rootClass1 = "rootClass1";
		String rootClass2 = "rootClass2";
		String rootClass3 = "rootClass3";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClass2Node = writer.addNode(context2, JavaType.class,
				rootClass2);
		JavaType rootClass3Node = writer.addNode(context1, JavaType.class,
				rootClass3);
		TypeExtends link1 = writer.addBidirectionalLink(TypeExtends.class,
				rootClass1Node, rootClass2Node);
		TypeExtends link2 = writer.addBidirectionalLink(TypeExtends.class,
				rootClass1Node, rootClass3Node);

		TypeExtends link3 = writer.addBidirectionalLink(TypeExtends.class,
				rootClass2Node, rootClass1Node);
		TypeExtends link4 = writer.addBidirectionalLink(TypeExtends.class,
				rootClass3Node, rootClass1Node);

		writer.flush();

		assertThat(link1, is(link3));
		assertThat(link2, is(link4));

		List<Link> twoLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.BIDIRECTIONAL));
		assertThat(twoLinks.size(), is(2));
		assertThat(twoLinks.contains(link1), is(true));
		assertThat(twoLinks.contains(link2), is(true));

		List<Link> emptyLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.UNIDIRECTIONAL));
		assertThat(emptyLinks.size(), is(0));

		List<Link> linkFromNode2 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass2Node, LinkType.BIDIRECTIONAL));
		assertThat(linkFromNode2.size(), is(1));
		assertThat(linkFromNode2.contains(link1), is(true));
		assertThat(linkFromNode2.contains(link2), is(false));

		List<Link> linkFromNode3 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass3Node, LinkType.BIDIRECTIONAL));
		assertThat(linkFromNode3.size(), is(1));
		assertThat(linkFromNode3.contains(link2), is(true));
		assertThat(linkFromNode3.contains(link1), is(false));
	}

	public void shouldaddAndRetrieveUniAndBidirectionalLinksOnDiferentContext()
			throws Exception {

		GraphReader simpleFromLocation = simpleGraphSession.from(location());
		Context context1 = simpleFromLocation.getContext(context1());
		Context context2 = simpleFromLocation.getContext(context2());
		GraphWriter writer = fullGraphSession.toServer();

		String rootClass1 = "rootClass1";
		String rootClass2 = "rootClass2";
		String rootClass3 = "rootClass3";
		JavaType rootClass1Node = writer.addNode(context1, JavaType.class,
				rootClass1);
		JavaType rootClass2Node = writer.addNode(context2, JavaType.class,
				rootClass2);
		JavaType rootClass3Node = writer.addNode(context1, JavaType.class,
				rootClass3);
		TypeExtends link1 = writer.addLink(TypeExtends.class, rootClass1Node,
				rootClass2Node);
		TypeExtends link2 = writer.addLink(TypeExtends.class, rootClass1Node,
				rootClass3Node);

		String rootClass1bid = "rootClass1bid";
		String rootClass2bid = "rootClass2bid";
		String rootClass3bid = "rootClass3bid";
		JavaType rootClass1BidNode = writer.addNode(context1, JavaType.class,
				rootClass1bid);
		JavaType rootClass2BidNode = writer.addNode(context1, JavaType.class,
				rootClass2bid);
		JavaType rootClass3BidNode = writer.addNode(context1, JavaType.class,
				rootClass3bid);
		TypeExtends link1Bid = writer.addBidirectionalLink(TypeExtends.class,
				rootClass1BidNode, rootClass2BidNode);
		TypeExtends link2Bid = writer.addBidirectionalLink(TypeExtends.class,
				rootClass1BidNode, rootClass3BidNode);

		TypeExtends link3Bid = writer.addBidirectionalLink(TypeExtends.class,
				rootClass2BidNode, rootClass1BidNode);
		TypeExtends link4Bid = writer.addBidirectionalLink(TypeExtends.class,
				rootClass3BidNode, rootClass1BidNode);

		writer.flush();

		assertThat(link1Bid, is(link3Bid));
		assertThat(link2Bid, is(link4Bid));

		List<Link> twoBidLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1BidNode, null, LinkType.BIDIRECTIONAL));
		assertThat(twoBidLinks.size(), is(2));
		assertThat(twoBidLinks.contains(link1Bid), is(true));
		assertThat(twoBidLinks.contains(link2Bid), is(true));

		List<Link> bidLinkFromNode2 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass2BidNode, LinkType.BIDIRECTIONAL));
		assertThat(bidLinkFromNode2.size(), is(1));
		assertThat(bidLinkFromNode2.contains(link1Bid), is(true));
		assertThat(bidLinkFromNode2.contains(link2Bid), is(false));

		List<Link> bidLinkFromNode3 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass3BidNode, LinkType.BIDIRECTIONAL));
		assertThat(bidLinkFromNode3.size(), is(1));
		assertThat(bidLinkFromNode3.contains(link2Bid), is(true));
		assertThat(bidLinkFromNode3.contains(link1Bid), is(false));
		List<Link> twoLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.UNIDIRECTIONAL));
		assertThat(twoLinks.size(), is(2));
		assertThat(twoLinks.contains(link1), is(true));
		assertThat(twoLinks.contains(link2), is(true));

		List<Link> emptyLinks = SLCollections.iterableToList(simpleFromLocation.getLinks(
				rootClass1Node, null, LinkType.BIDIRECTIONAL));
		assertThat(emptyLinks.size(), is(0));

		List<Link> linkFromNode2 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass2Node, LinkType.UNIDIRECTIONAL));
		assertThat(linkFromNode2.size(), is(1));
		assertThat(linkFromNode2.contains(link1), is(true));
		assertThat(linkFromNode2.contains(link2), is(false));

		List<Link> linkFromNode3 = SLCollections.iterableToList(simpleFromLocation.getLinks(
				null, rootClass3Node, LinkType.UNIDIRECTIONAL));
		assertThat(linkFromNode3.size(), is(1));
		assertThat(linkFromNode3.contains(link2), is(true));
		assertThat(linkFromNode3.contains(link1), is(false));

	}

}
