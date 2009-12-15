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
package org.openspotlight.persist.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Session;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.util.JCRUtil;
import org.openspotlight.persist.support.SimplePersistSupport;

/**
 * The Class SimplePersistSupportTest.
 */
public class SimplePersistSupportTest {

	/** The provider. */
	private static JcrConnectionProvider provider;

	/**
	 * Setup.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@BeforeClass
	public static void setup() throws Exception {
		SimplePersistSupportTest.provider = JcrConnectionProvider
				.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
	}

	/** The session. */
	private Session session = null;

	/**
	 * Close session.
	 */
	@After
	public void closeSession() {
		if (session != null) {
			session.logout();
			session = null;
		}
	}

	/**
	 * Setup session.
	 */
	@Before
	public void setupSession() {
		session = SimplePersistSupportTest.provider.openSession();
	}

	@Test
	public void shouldAddAndRemoveNodeOnAnotherNode() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj levelOne = new LevelOneObj();
		levelOne.setRootObj(root);
		final LevelTwoObj levelTwo = new LevelTwoObj();
		levelTwo.setLevelOneObj(levelOne);
		final PropertyObj propertyObj = new PropertyObj();
		propertyObj.setName("name");
		propertyObj.setValue(3);
		levelTwo.setPropertyObj(propertyObj);
		Node asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelTwo);
		LevelTwoObj anotherLevelTwo = SimplePersistSupport.convertJcrToBean(
				session, asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelTwo.getPropertyObj().getName(), Is
				.is(propertyObj.getName()));
		Assert.assertThat(anotherLevelTwo.getPropertyObj().getValue(), Is
				.is(propertyObj.getValue()));

		propertyObj.setName("anotherName");
		propertyObj.setValue(4);
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelTwo);
		anotherLevelTwo = SimplePersistSupport.convertJcrToBean(session, asJcr,
				LazyType.LAZY);
		Assert.assertThat(anotherLevelTwo.getPropertyObj().getName(), Is
				.is(propertyObj.getName()));
		Assert.assertThat(anotherLevelTwo.getPropertyObj().getValue(), Is
				.is(propertyObj.getValue()));
		levelTwo.setPropertyObj(null);
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelTwo);
		anotherLevelTwo = SimplePersistSupport.convertJcrToBean(session, asJcr,
				LazyType.LAZY);
		Assert.assertThat(anotherLevelTwo.getPropertyObj(), Is.is(IsNull
				.nullValue()));

	}

	@Test
	public void shouldAddAndRemoveNodeOnCollection() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj levelOne = new LevelOneObj();
		levelOne.setRootObj(root);
		final LevelTwoObj levelTwo = new LevelTwoObj();
		levelTwo.setLevelOneObj(levelOne);
		final LevelThreeObj levelThree = new LevelThreeObj();
		levelThree.setLevelTwoObj(levelTwo);
		levelThree.setObjList(new ArrayList<ListItemObj>());
		final ListItemObj obj1 = new ListItemObj();
		obj1.setName("obj 1");
		obj1.setValue(5);
		levelThree.getObjList().add(obj1);
		Node asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		LevelThreeObj anotherLevelThree = SimplePersistSupport
				.convertJcrToBean(session, asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getObjList().size(), Is.is(1));
		Assert.assertThat(anotherLevelThree.getObjList().get(0).getValue(), Is
				.is(obj1.getValue()));
		Assert.assertThat(anotherLevelThree.getObjList().get(0).getName(), Is
				.is(obj1.getName()));

		obj1.setName("anotherName");
		final ListItemObj obj2 = new ListItemObj();
		obj2.setName("another name 2");
		obj2.setValue(33);
		levelThree.getObjList().add(obj2);
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		anotherLevelThree = SimplePersistSupport.convertJcrToBean(session,
				asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getObjList().size(), Is.is(2));
		Assert.assertThat(anotherLevelThree.getObjList().get(0).getValue(), Is
				.is(obj1.getValue()));
		Assert.assertThat(anotherLevelThree.getObjList().get(0).getName(), Is
				.is(obj1.getName()));
		Assert.assertThat(anotherLevelThree.getObjList().get(1).getValue(), Is
				.is(obj2.getValue()));
		Assert.assertThat(anotherLevelThree.getObjList().get(1).getName(), Is
				.is(obj2.getName()));

		levelThree.getObjList().clear();
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		anotherLevelThree = SimplePersistSupport.convertJcrToBean(session,
				asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getObjList().size(), Is.is(0));
	}

	@Test
	public void shouldAddAndRemoveNodeOnMapProperty() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj levelOne = new LevelOneObj();
		levelOne.setRootObj(root);
		final LevelTwoObj levelTwo = new LevelTwoObj();
		levelTwo.setLevelOneObj(levelOne);
		final LevelThreeObj levelThree = new LevelThreeObj();
		levelThree.setLevelTwoObj(levelTwo);
		levelThree.setObjMap(new HashMap<Integer, MapValueObj>());
		final MapValueObj obj1 = new MapValueObj();
		obj1.setName("obj 1");
		obj1.setValue(5);
		levelThree.getObjMap().put(2, obj1);
		Node asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		LevelThreeObj anotherLevelThree = SimplePersistSupport
				.convertJcrToBean(session, asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getObjMap().size(), Is.is(1));
		Assert.assertThat(anotherLevelThree.getObjMap().get(2).getValue(), Is
				.is(obj1.getValue()));
		Assert.assertThat(anotherLevelThree.getObjMap().get(2).getName(), Is
				.is(obj1.getName()));

		obj1.setValue(4);
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		anotherLevelThree = SimplePersistSupport.convertJcrToBean(session,
				asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getObjMap().size(), Is.is(1));
		Assert.assertThat(anotherLevelThree.getObjMap().get(2).getValue(), Is
				.is(obj1.getValue()));
		Assert.assertThat(anotherLevelThree.getObjMap().get(2).getName(), Is
				.is(obj1.getName()));

		levelThree.getObjMap().clear();
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		anotherLevelThree = SimplePersistSupport.convertJcrToBean(session,
				asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getObjMap().size(), Is.is(0));
	}

	@Test
	public void shouldAddAndRemoveSimpleTypeOnCollection() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj levelOne = new LevelOneObj();
		levelOne.setRootObj(root);
		final LevelTwoObj levelTwo = new LevelTwoObj();
		levelTwo.setLevelOneObj(levelOne);
		final LevelThreeObj levelThree = new LevelThreeObj();
		levelThree.setLevelTwoObj(levelTwo);
		levelThree.setBooleanList(new ArrayList<Boolean>());
		levelThree.getBooleanList().add(true);
		Node asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		LevelThreeObj anotherLevelThree = SimplePersistSupport
				.convertJcrToBean(session, asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getBooleanList().size(), Is.is(1));
		Assert.assertThat(anotherLevelThree.getBooleanList().get(0), Is
				.is(true));

		levelThree.getBooleanList().add(false);
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		anotherLevelThree = SimplePersistSupport.convertJcrToBean(session,
				asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getBooleanList().size(), Is.is(2));
		Assert.assertThat(anotherLevelThree.getBooleanList().get(0), Is
				.is(true));
		Assert.assertThat(anotherLevelThree.getBooleanList().get(1), Is
				.is(false));

		levelThree.getBooleanList().clear();
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		anotherLevelThree = SimplePersistSupport.convertJcrToBean(session,
				asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getObjList().size(), Is.is(0));
	}

	@Test
	public void shouldAddAndRemoveSimpleTypeOnMapProperty() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj levelOne = new LevelOneObj();
		levelOne.setRootObj(root);
		final LevelTwoObj levelTwo = new LevelTwoObj();
		levelTwo.setLevelOneObj(levelOne);
		final LevelThreeObj levelThree = new LevelThreeObj();
		levelThree.setLevelTwoObj(levelTwo);
		levelThree.setNumberMap(new HashMap<Double, Integer>());
		levelThree.getNumberMap().put(1.0, 1);
		Node asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		LevelThreeObj anotherLevelThree = SimplePersistSupport
				.convertJcrToBean(session, asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getNumberMap().size(), Is.is(1));
		Assert.assertThat(anotherLevelThree.getNumberMap().get(1.0), Is.is(1));

		levelThree.getNumberMap().put(2.0, 2);

		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		anotherLevelThree = SimplePersistSupport.convertJcrToBean(session,
				asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getNumberMap().size(), Is.is(2));
		Assert.assertThat(anotherLevelThree.getNumberMap().get(1.0), Is.is(1));
		Assert.assertThat(anotherLevelThree.getNumberMap().get(2.0), Is.is(2));

		levelThree.getNumberMap().clear();
		asJcr = SimplePersistSupport.convertBeanToJcr("a/b/c", session,
				levelThree);
		anotherLevelThree = SimplePersistSupport.convertJcrToBean(session,
				asJcr, LazyType.LAZY);
		Assert.assertThat(anotherLevelThree.getNumberMap().size(), Is.is(0));
	}

	/**
	 * Should convert bean to jcr node.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldConvertBeanToJcrNode() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj obj1 = new LevelOneObj();
		final LevelTwoObj obj2 = new LevelTwoObj();
		final LevelThreeObj obj3 = new LevelThreeObj();
		obj1.setRootObj(root);
		obj2.setLevelOneObj(obj1);
		final PropertyObj propertyObj = new PropertyObj();
		propertyObj.setName("name");
		propertyObj.setValue(2);
		obj2.setPropertyObj(propertyObj);
		obj3.setLevelTwoObj(obj2);
		obj2.setProperty("propVal");
		final Node node = SimplePersistSupport.convertBeanToJcr(
				SharedConstants.DEFAULT_JCR_ROOT_NAME, session, obj3);
		final String path = node.getPath();
		Assert
				.assertThat(
						path,
						Is
								.is("/osl/NODE_org_openspotlight_persist_test_RootObj/NODE_org_openspotlight_persist_test_LevelOneObj/NODE_org_openspotlight_persist_test_LevelTwoObj/NODE_org_openspotlight_persist_test_LevelThreeObj"));
		Assert.assertThat(node.getProperty("node.property.property.type")
				.getString(), Is.is("java.lang.String"));
		Assert.assertThat(node.getProperty("node.typeName").getString(), Is
				.is("org.openspotlight.persist.test.LevelThreeObj"));
		Assert.assertThat(node.getProperty("node.hashValue").getString(), Is
				.is("401bb295-1e5a-349f-976a-47c9ab205eaa"));
		Assert.assertThat(node.getProperty("node.key.key.type").getString(), Is
				.is("java.lang.String"));

		final Node parentNode = node.getParent();
		Assert.assertThat(parentNode.getProperty("node.property.property.type")
				.getString(), Is.is("java.lang.String"));
		Assert.assertThat(parentNode.getProperty("node.typeName").getString(),
				Is.is("org.openspotlight.persist.test.LevelTwoObj"));
		Assert.assertThat(parentNode.getProperty("node.hashValue").getString(),
				Is.is("026dc045-a954-333e-ab47-6b5192a09134"));
		Assert.assertThat(parentNode
				.getProperty("node.property.property.value").getString(), Is
				.is("propVal"));
		Assert.assertThat(parentNode.getProperty("node.key.key.type")
				.getString(), Is.is("java.lang.String"));
		final Node nodeProperty = parentNode
				.getNode("NODE_PROPERTY_propertyObj");
		Assert.assertThat(nodeProperty.getProperty("node.key.value.type")
				.getString(), Is.is("int"));
		Assert.assertThat(nodeProperty.getProperty("node.hashValue")
				.getString(), Is.is("f9facf49-a10f-35f3-90d5-1f2babe7478f"));
		Assert.assertThat(nodeProperty.getProperty("node.property.name.type")
				.getString(), Is.is("java.lang.String"));
		Assert.assertThat(nodeProperty.getProperty("node.key.value.value")
				.getString(), Is.is("2"));
		Assert.assertThat(
				nodeProperty.getProperty("property.name").getString(), Is
						.is("propertyObj"));
		Assert.assertThat(nodeProperty.getProperty("node.property.name.value")
				.getString(), Is.is("name"));
		Assert.assertThat(
				nodeProperty.getProperty("node.typeName").getString(), Is
						.is("org.openspotlight.persist.test.PropertyObj"));
	}

	/**
	 * Should convert jcr node to bean.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void shouldConvertJcrNodeToBean() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj obj1 = new LevelOneObj();
		final LevelTwoObj obj2 = new LevelTwoObj();
		final LevelThreeObj obj3 = new LevelThreeObj();
		final ListItemObj li1 = new ListItemObj();
		li1.setName("1");
		li1.setValue(1);
		final ListItemObj li2 = new ListItemObj();
		li2.setName("2");
		li2.setValue(2);
		final ListItemObj li3 = new ListItemObj();
		li3.setName("3");
		li3.setValue(3);
		obj3.getObjList().add(li1);
		obj3.getObjList().add(li2);
		obj3.getObjList().add(li3);
		final MapValueObj mapVal1 = new MapValueObj();
		mapVal1.setName("1");
		final MapValueObj mapVal2 = new MapValueObj();
		mapVal2.setName("2");
		final MapValueObj mapVal3 = new MapValueObj();
		mapVal3.setName("3");
		obj3.getObjMap().put(1, mapVal1);
		obj3.getObjMap().put(2, mapVal2);
		obj3.getObjMap().put(3, mapVal3);
		obj1.setRootObj(root);
		obj2.setLevelOneObj(obj1);
		obj3.setLevelTwoObj(obj2);
		obj3.setBooleanList(new ArrayList<Boolean>());
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.getBooleanList().add(Boolean.FALSE);
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.setNumberMap(new HashMap<Double, Integer>());
		obj3.getNumberMap().put(1.0, 3);
		obj3.getNumberMap().put(2.0, 2);
		obj3.getNumberMap().put(3.0, 1);

		obj2.setProperty("propVal");
		final PropertyObj propertyObj = new PropertyObj();
		propertyObj.setName("name");
		propertyObj.setValue(2);
		obj2.setPropertyObj(propertyObj);

		final Node node = SimplePersistSupport.convertBeanToJcr(
				SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
				session, obj3);
		final LevelThreeObj convertedFromJcr = SimplePersistSupport
				.convertJcrToBean(session, node, LazyType.EAGER);
		Assert.assertThat(obj3.getKey(), Is.is(convertedFromJcr.getKey()));
		Assert.assertThat(obj3.getProperty(), Is.is(convertedFromJcr
				.getProperty()));
		Assert.assertThat(obj3.getLevelTwoObj().getKey(), Is
				.is(convertedFromJcr.getLevelTwoObj().getKey()));
		Assert.assertThat(obj3.getLevelTwoObj().getPropertyObj().getName(), Is
				.is(convertedFromJcr.getLevelTwoObj().getPropertyObj()
						.getName()));
		Assert.assertThat(obj3.getLevelTwoObj().getLevelOneObj().getProperty(),
				Is.is(convertedFromJcr.getLevelTwoObj().getLevelOneObj()
						.getProperty()));
		Assert.assertThat(convertedFromJcr.getBooleanList(), Is.is(Arrays
				.asList(true, false, true, true)));
		Assert.assertThat(convertedFromJcr.getNumberMap().get(1.0), Is.is(3));
		Assert.assertThat(convertedFromJcr.getNumberMap().get(2.0), Is.is(2));
		Assert.assertThat(convertedFromJcr.getNumberMap().get(3.0), Is.is(1));
		Assert.assertThat(convertedFromJcr.getObjMap().get(1).getName(), Is
				.is("1"));
		Assert.assertThat(convertedFromJcr.getObjMap().get(2).getName(), Is
				.is("2"));
		Assert.assertThat(convertedFromJcr.getObjMap().get(3).getName(), Is
				.is("3"));

		Assert.assertThat(convertedFromJcr.getObjList().size(), Is.is(3));

		Assert.assertThat(convertedFromJcr.getObjList().get(0).getName(), Is
				.is("1"));
		Assert.assertThat(convertedFromJcr.getObjList().get(1).getName(), Is
				.is("2"));
		Assert.assertThat(convertedFromJcr.getObjList().get(2).getName(), Is
				.is("3"));

	}

	@Test
	public void shouldFindJcrNodeByItsKey() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj obj1 = new LevelOneObj();
		final LevelTwoObj obj2 = new LevelTwoObj();
		final LevelThreeObj obj3 = new LevelThreeObj();
		final ListItemObj li1 = new ListItemObj();
		li1.setName("1");
		final ListItemObj li2 = new ListItemObj();
		li2.setName("2");
		final ListItemObj li3 = new ListItemObj();
		li3.setName("3");
		obj3.getObjList().add(li1);
		obj3.getObjList().add(li2);
		obj3.getObjList().add(li3);
		final MapValueObj mapVal1 = new MapValueObj();
		mapVal1.setName("1");
		final MapValueObj mapVal2 = new MapValueObj();
		mapVal2.setName("2");
		final MapValueObj mapVal3 = new MapValueObj();
		mapVal3.setName("3");
		obj3.getObjMap().put(1, mapVal1);
		obj3.getObjMap().put(2, mapVal2);
		obj3.getObjMap().put(3, mapVal3);
		obj1.setRootObj(root);
		obj2.setLevelOneObj(obj1);
		obj3.setLevelTwoObj(obj2);
		obj3.setBooleanList(new ArrayList<Boolean>());
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.getBooleanList().add(Boolean.FALSE);
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.setNumberMap(new HashMap<Double, Integer>());
		obj3.getNumberMap().put(1.0, 3);
		obj3.getNumberMap().put(2.0, 2);
		obj3.getNumberMap().put(3.0, 1);

		obj2.setProperty("propVal");
		final PropertyObj propertyObj = new PropertyObj();
		propertyObj.setName("name");
		propertyObj.setValue(2);
		obj2.setPropertyObj(propertyObj);
		obj2.setKey("1");

		final LevelTwoObj obj2_1 = new LevelTwoObj();
		obj2_1.setKey("2");
		final LevelTwoObj obj2_2 = new LevelTwoObj();
		obj2_2.setKey("3");

		SimplePersistSupport.convertBeanToJcr(
				SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
				session, obj2);
		SimplePersistSupport.convertBeanToJcr(
				SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
				session, obj2_1);
		SimplePersistSupport.convertBeanToJcr(
				SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
				session, obj2_2);

		session.save();// necessary for the xpath to work
		final Set<LevelTwoObj> result1 = SimplePersistSupport
				.findNodesByProperties(SharedConstants.DEFAULT_JCR_ROOT_NAME
						+ "/lalala/lelele", session, LevelTwoObj.class,
						LazyType.LAZY, org.openspotlight.common.util.Arrays
								.of("key"),
						org.openspotlight.common.util.Arrays.of("1"));
		final Set<LevelTwoObj> result2 = SimplePersistSupport
				.findNodesByProperties(SharedConstants.DEFAULT_JCR_ROOT_NAME
						+ "/lalala/lelele", session, LevelTwoObj.class,
						LazyType.LAZY, org.openspotlight.common.util.Arrays
								.of("key"),
						org.openspotlight.common.util.Arrays.of("2"));
		final Set<LevelTwoObj> result3 = SimplePersistSupport
				.findNodesByProperties(SharedConstants.DEFAULT_JCR_ROOT_NAME
						+ "/lalala/lelele", session, LevelTwoObj.class,
						LazyType.LAZY, org.openspotlight.common.util.Arrays
								.of("key"),
						org.openspotlight.common.util.Arrays.of("3"));

		Assert.assertThat(result1.size(), Is.is(1));
		Assert.assertThat(result3.size(), Is.is(1));
		Assert.assertThat(result2.size(), Is.is(1));
		Assert.assertThat(result1.iterator().next().getKey(), Is.is("1"));
		Assert.assertThat(result2.iterator().next().getKey(), Is.is("2"));
		Assert.assertThat(result3.iterator().next().getKey(), Is.is("3"));
		Assert.assertThat(result1.iterator().next().getLevelOneObj()
				.getRootObj(), IsNull.notNullValue());
		Assert.assertThat(result2.iterator().next().getLevelOneObj(), Is
				.is(IsNull.nullValue()));
		Assert.assertThat(result3.iterator().next().getLevelOneObj(), Is
				.is(IsNull.nullValue()));

	}

	@Test
	public void shouldFindJcrNodeByItsProperties() throws Exception {
		final RootObj root = new RootObj();
		final LevelOneObj obj1 = new LevelOneObj();
		final LevelTwoObj obj2 = new LevelTwoObj();
		final LevelThreeObj obj3 = new LevelThreeObj();
		final ListItemObj li1 = new ListItemObj();
		li1.setName("1");
		final ListItemObj li2 = new ListItemObj();
		li2.setName("2");
		final ListItemObj li3 = new ListItemObj();
		li3.setName("3");
		obj3.getObjList().add(li1);
		obj3.getObjList().add(li2);
		obj3.getObjList().add(li3);
		final MapValueObj mapVal1 = new MapValueObj();
		mapVal1.setName("1");
		final MapValueObj mapVal2 = new MapValueObj();
		mapVal2.setName("2");
		final MapValueObj mapVal3 = new MapValueObj();
		mapVal3.setName("3");
		obj3.getObjMap().put(1, mapVal1);
		obj3.getObjMap().put(2, mapVal2);
		obj3.getObjMap().put(3, mapVal3);
		obj1.setRootObj(root);
		obj2.setLevelOneObj(obj1);
		obj3.setLevelTwoObj(obj2);
		obj3.setBooleanList(new ArrayList<Boolean>());
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.getBooleanList().add(Boolean.FALSE);
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.getBooleanList().add(Boolean.TRUE);
		obj3.setNumberMap(new HashMap<Double, Integer>());
		obj3.getNumberMap().put(1.0, 3);
		obj3.getNumberMap().put(2.0, 2);
		obj3.getNumberMap().put(3.0, 1);

		obj2.setProperty("propVal");
		final PropertyObj propertyObj = new PropertyObj();
		propertyObj.setName("name");
		propertyObj.setValue(2);
		obj2.setPropertyObj(propertyObj);
		obj2.setKey("1");

		final LevelTwoObj obj2_1 = new LevelTwoObj();
		obj2_1.setKey("2");
		final LevelTwoObj obj2_2 = new LevelTwoObj();
		obj2_2.setKey("3");

		SimplePersistSupport.convertBeanToJcr(
				SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
				session, obj2);
		SimplePersistSupport.convertBeanToJcr(
				SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
				session, obj2_1);
		SimplePersistSupport.convertBeanToJcr(
				SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
				session, obj2_2);

		session.save();// necessary for the xpath to work
		final Set<LevelTwoObj> result1 = SimplePersistSupport
				.findNodesByProperties(SharedConstants.DEFAULT_JCR_ROOT_NAME
						+ "/lalala/lelele", session, LevelTwoObj.class,
						LazyType.LAZY, org.openspotlight.common.util.Arrays
								.of("key"),
						org.openspotlight.common.util.Arrays.of("1"));
		final Set<LevelTwoObj> result2 = SimplePersistSupport
				.findNodesByProperties(SharedConstants.DEFAULT_JCR_ROOT_NAME
						+ "/lalala/lelele", session, LevelTwoObj.class,
						LazyType.LAZY, org.openspotlight.common.util.Arrays
								.of("key"),
						org.openspotlight.common.util.Arrays.of("2"));
		final Set<LevelTwoObj> result3 = SimplePersistSupport
				.findNodesByProperties(SharedConstants.DEFAULT_JCR_ROOT_NAME
						+ "/lalala/lelele", session, LevelTwoObj.class,
						LazyType.LAZY, org.openspotlight.common.util.Arrays
								.of("key"),
						org.openspotlight.common.util.Arrays.of("3"));

		Assert.assertThat(result1.size(), Is.is(1));
		Assert.assertThat(result3.size(), Is.is(1));
		Assert.assertThat(result2.size(), Is.is(1));
		Assert.assertThat(result1.iterator().next().getKey(), Is.is("1"));
		Assert.assertThat(result2.iterator().next().getKey(), Is.is("2"));
		Assert.assertThat(result3.iterator().next().getKey(), Is.is("3"));
		Assert.assertThat(result1.iterator().next().getLevelOneObj()
				.getRootObj(), IsNull.notNullValue());
		Assert.assertThat(result2.iterator().next().getLevelOneObj(), Is
				.is(IsNull.nullValue()));
		Assert.assertThat(result3.iterator().next().getLevelOneObj(), Is
				.is(IsNull.nullValue()));

	}

	@Test
	public void shouldFindNodesWithSameKeyPropertyWhenUsingComposedKey()
			throws Exception {
		final ComposedKeyObject object1 = new ComposedKeyObject();
		object1.setKey1("same key");
		object1.setKey2(1);
		final ComposedKeyObject object2 = new ComposedKeyObject();
		object2.setKey1("same key");
		object2.setKey2(2);
		final ComposedKeyObject object3 = new ComposedKeyObject();
		object3.setKey1("another key");
		object3.setKey2(1);

		SimplePersistSupport.convertBeanToJcr("a/b/c", session, object1);
		SimplePersistSupport.convertBeanToJcr("a/b/c", session, object2);
		SimplePersistSupport.convertBeanToJcr("a/b/c", session, object3);
		session.save();
		final Set<ComposedKeyObject> foundNodes = SimplePersistSupport
				.findNodesByProperties("a/b/c", session,
						ComposedKeyObject.class, LazyType.EAGER,
						new String[] { "key1" }, new Object[] { "same key" });
		Assert.assertThat(foundNodes.size(), Is.is(2));
	}

	@Test
	public void shouldFindObjectsByNullParameter() throws Exception {
		final LevelOneObj obj1 = new LevelOneObj();
		obj1.setProperty("prop");
		final LevelOneObj obj2 = new LevelOneObj();
		obj2.setProperty(null);

		SimplePersistSupport.convertBeanToJcr("a/b/c", session, obj1);
		final Node node = SimplePersistSupport.convertBeanToJcr("a/b/c",
				session, obj2);
		final PropertyIterator propIt = node.getProperties();
		Property prop;
		while (propIt.hasNext()) {
			prop = propIt.nextProperty();
			System.out.print(prop.getName());
			System.out.print("=");
			System.out.println(prop.getValue().getString());
		}
		session.save();
		final Set<LevelOneObj> result = SimplePersistSupport
				.findNodesByProperties("a/b/c", session, LevelOneObj.class,
						LazyType.EAGER, new String[] { "property" },
						new Object[] { null });
		Assert.assertThat(result.size(), Is.is(1));
	}

	@Test(expected = SLRuntimeException.class)
	public void shouldNotFindWithWrongPropertyName() throws Exception {
		SimplePersistSupport.findNodesByProperties("a/b/c", session,
				RootObj.class, LazyType.EAGER,
				new String[] { "invalidProperty" }, new Object[] { null });
	}

	@Test
	public void shouldPersistTwoDifferentNodesWhenUsingComposedKeys()
			throws Exception {

		final ComposedKeyObject object1 = new ComposedKeyObject();
		object1.setKey1("same");
		object1.setKey2(1);
		final ComposedKeyObject object2 = new ComposedKeyObject();
		object2.setKey1("same");
		object2.setKey2(2);
		final ComposedKeyObject object3 = new ComposedKeyObject();
		object3.setKey1("same");
		object3.setKey2(1);

		final Node newNode1 = SimplePersistSupport.convertBeanToJcr("a/b/c",
				session, object1);
		final Node newNode2 = SimplePersistSupport.convertBeanToJcr("a/b/c",
				session, object2);
		final Node newNode3 = SimplePersistSupport.convertBeanToJcr("a/b/c",
				session, object3);
		JCRUtil.makeReferenceable(newNode1);
		JCRUtil.makeReferenceable(newNode2);
		JCRUtil.makeReferenceable(newNode3);
		session.save();
		Assert.assertThat(newNode1.getUUID(), Is.is(IsNot.not(newNode2
				.getUUID())));
		Assert.assertThat(newNode1.getUUID(), Is.is(newNode3.getUUID()));
	}

}
