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

package org.openspotlight.persist.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.storage.RepositoryPath.repositoryPath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.jredis.JRedis;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistImpl;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.RegularPartitions;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * The Class SimplePersistSupportTest.
 */
public class SimplePersistSupportTest {

    SimplePersistCapable<Node, StorageSession> simplePersist;
    private StorageSession                     session;

    public SimplePersistSupportTest() {
        autoFlushInjector = Guice.createInjector(new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                                                                         ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                                                                         repositoryPath("repositoryPath")));
    }

    final Injector autoFlushInjector;

    /**
     * Setup session.
     */
    @Before
    public void setupSession()
        throws Exception {
        final JRedis jRedis = autoFlushInjector.getInstance(JRedisFactory.class).getFrom(RegularPartitions.FEDERATION);
        jRedis.flushall();
        session = autoFlushInjector.getInstance(StorageSession.class);
        simplePersist = new SimplePersistImpl(session, RegularPartitions.FEDERATION);
    }

    @Test
    public void shouldAddAndRemoveNodeOnAnotherNode()
        throws Exception {
        final RootObj root = new RootObj();
        final LevelOneObj levelOne = new LevelOneObj();
        levelOne.setRootObj(root);
        final LevelTwoObj levelTwo = new LevelTwoObj();
        levelTwo.setLevelOneObj(levelOne);
        final PropertyObj propertyObj = new PropertyObj();
        propertyObj.setName("name");
        propertyObj.setValue(3);
        levelTwo.setPropertyObj(propertyObj);
        Node asJcr = simplePersist.convertBeanToNode(levelTwo);
        LevelTwoObj anotherLevelTwo = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelTwo.getPropertyObj().getName(), Is.is(propertyObj.getName()));
        assertThat(anotherLevelTwo.getPropertyObj().getValue(), Is.is(propertyObj.getValue()));

        propertyObj.setName("anotherName");
        propertyObj.setValue(4);
        asJcr = simplePersist.convertBeanToNode(levelTwo);
        anotherLevelTwo = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelTwo.getPropertyObj().getName(), Is.is(propertyObj.getName()));
        assertThat(anotherLevelTwo.getPropertyObj().getValue(), Is.is(propertyObj.getValue()));
        levelTwo.setPropertyObj(null);
        asJcr = simplePersist.convertBeanToNode(levelTwo);
        anotherLevelTwo = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelTwo.getPropertyObj(), Is.is(IsNull.nullValue()));

    }

    @Test
    public void shouldAddAndRemoveNodeOnCollection()
        throws Exception {
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
        Node asJcr = simplePersist.convertBeanToNode(levelThree);
        LevelThreeObj anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelThree.getObjList().size(), Is.is(1));
        assertThat(anotherLevelThree.getObjList().get(0).getValue(), Is.is(obj1.getValue()));
        assertThat(anotherLevelThree.getObjList().get(0).getName(), Is.is(obj1.getName()));

        obj1.setName("anotherName");
        final ListItemObj obj2 = new ListItemObj();
        obj2.setName("another name 2");
        obj2.setValue(33);
        levelThree.getObjList().add(obj2);
        asJcr = simplePersist.convertBeanToNode(levelThree);
        anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelThree.getObjList().size(), Is.is(2));
        assertThat(anotherLevelThree.getObjList().get(0).getValue(), Is.is(obj1.getValue()));
        assertThat(anotherLevelThree.getObjList().get(0).getName(), Is.is(obj1.getName()));
        assertThat(anotherLevelThree.getObjList().get(1).getValue(), Is.is(obj2.getValue()));
        assertThat(anotherLevelThree.getObjList().get(1).getName(), Is.is(obj2.getName()));

        levelThree.getObjList().clear();
        asJcr = simplePersist.convertBeanToNode(levelThree);
        anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelThree.getObjList().size(), Is.is(0));
    }

    @Test
    public void shouldAddAndRemoveNodeOnMapProperty()
        throws Exception {
        final RootObj root = new RootObj();
        final LevelOneObj levelOne = new LevelOneObj();
        levelOne.setRootObj(root);
        final LevelTwoObj levelTwo = new LevelTwoObj();
        levelTwo.setLevelOneObj(levelOne);
        final LevelThreeObj levelThree = new LevelThreeObj();
        levelThree.setLevelTwoObj(levelTwo);
        final MapValueObj obj1 = new MapValueObj();
        obj1.setName("obj 1");
        obj1.setValue(5);
        Node asJcr = simplePersist.convertBeanToNode(levelThree);
        LevelThreeObj anotherLevelThree = simplePersist.convertNodeToBean(asJcr);

        obj1.setValue(4);
        asJcr = simplePersist.convertBeanToNode(levelThree);
        anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
        asJcr = simplePersist.convertBeanToNode(levelThree);
        anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
    }

    @Test
    public void shouldAddAndRemoveSimpleTypeOnCollection()
        throws Exception {
        final RootObj root = new RootObj();
        final LevelOneObj levelOne = new LevelOneObj();
        levelOne.setRootObj(root);
        final LevelTwoObj levelTwo = new LevelTwoObj();
        levelTwo.setLevelOneObj(levelOne);
        final LevelThreeObj levelThree = new LevelThreeObj();
        levelThree.setLevelTwoObj(levelTwo);
        levelThree.setBooleanList(new ArrayList<Boolean>());
        levelThree.getBooleanList().add(true);
        Node asJcr = simplePersist.convertBeanToNode(levelThree);
        LevelThreeObj anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelThree.getBooleanList().size(), Is.is(1));
        assertThat(anotherLevelThree.getBooleanList().get(0), Is.is(true));

        levelThree.getBooleanList().add(false);
        asJcr = simplePersist.convertBeanToNode(levelThree);
        anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelThree.getBooleanList().size(), Is.is(2));
        assertThat(anotherLevelThree.getBooleanList().get(0), Is.is(true));
        assertThat(anotherLevelThree.getBooleanList().get(1), Is.is(false));

        levelThree.getBooleanList().clear();
        asJcr = simplePersist.convertBeanToNode(levelThree);
        anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelThree.getObjList().size(), Is.is(0));
    }

    // @Test
    // public void shouldAddAndRemoveSimpleTypeOnMapProperty() throws Exception
    // {
    // final RootObj root = new RootObj();
    // final LevelOneObj levelOne = new LevelOneObj();
    // levelOne.setRootObj(root);
    // final LevelTwoObj levelTwo = new LevelTwoObj();
    // levelTwo.setLevelOneObj(levelOne);
    // final LevelThreeObj levelThree = new LevelThreeObj();
    // levelThree.setLevelTwoObj(levelTwo);
    // levelThree.setNumberMap(new HashMap<Double, Integer>());
    // levelThree.getNumberMap().put(1.0, 1);
    // STNodeEntry asJcr = simplePersist.convertBeanToNode(
    // levelThree);
    // LevelThreeObj anotherLevelThree = simplePersist
    // .convertNodeToBean( asJcr);
    // assertThat(anotherLevelThree.getNumberMap().size(), Is.is(1));
    // assertThat(anotherLevelThree.getNumberMap().get(1.0), Is.is(1));
    //
    // levelThree.getNumberMap().put(2.0, 2);
    //
    // asJcr = simplePersist.convertBeanToNode(
    // levelThree);
    // anotherLevelThree = simplePersist.convertNodeToBean(
    // asJcr);
    // assertThat(anotherLevelThree.getNumberMap().size(), Is.is(2));
    // assertThat(anotherLevelThree.getNumberMap().get(1.0), Is.is(1));
    // assertThat(anotherLevelThree.getNumberMap().get(2.0), Is.is(2));
    //
    // levelThree.getNumberMap().clear();
    // asJcr = simplePersist.convertBeanToNode(
    // levelThree);
    // anotherLevelThree = simplePersist.convertNodeToBean(
    // asJcr);
    // assertThat(anotherLevelThree.getNumberMap().size(), Is.is(0));
    // }

    // /**
    // * Should convert bean to jcr node.
    // *
    // * @throws Exception
    // * the exception
    // */
    // @Test
    // public void shouldconvertBeanToNodeNode() throws Exception {
    // final RootObj root = new RootObj();
    // final LevelOneObj obj1 = new LevelOneObj();
    // final LevelTwoObj obj2 = new LevelTwoObj();
    // final LevelThreeObj obj3 = new LevelThreeObj();
    // obj1.setRootObj(root);
    // obj2.setLevelOneObj(obj1);
    // final PropertyObj propertyObj = new PropertyObj();
    // propertyObj.setName("name");
    // propertyObj.setValue(2);
    // obj2.setPropertyObj(propertyObj);
    // obj3.setLevelTwoObj(obj2);
    // obj2.setProperty("propVal");
    // final STNodeEntry STNodeEntry = simplePersist.convertBeanToNode(
    // SharedConstants.DEFAULT_JCR_ROOT_NAME, obj3);
    // final String path = node.getPath();
    // assertThat(
    // path,
    // Is
    // .is("/osl/NODE_org_openspotlight_persist_test_RootObj/NODE_org_openspotlight_persist_test_LevelOneObj/NODE_org_openspotlight_persist_test_LevelTwoObj/NODE_org_openspotlight_persist_test_LevelThreeObj"));
    // assertThat(node.getProperty("node_property_property_type")
    // .getString(), Is.is("java.lang.String"));
    // assertThat(node.getProperty("node_typeName").getString(), Is
    // .is("org.openspotlight.persist.test.LevelThreeObj"));
    // assertThat(node.getProperty("node_hashValue").getString(), Is
    // .is("401bb295-1e5a-349f-976a-47c9ab205eaa"));
    // assertThat(node.getProperty("node_key_key_type").getString(), Is
    // .is("java.lang.String"));
    //
    // final STNodeEntry parentSTNodeEntry = node.getParent();
    // assertThat(parentNode.getProperty("node_property_property_type")
    // .getString(), Is.is("java.lang.String"));
    // assertThat(parentNode.getProperty("node_typeName").getString(),
    // Is.is("org.openspotlight.persist.test.LevelTwoObj"));
    // assertThat(parentNode.getProperty("node_hashValue").getString(),
    // Is.is("026dc045-a954-333e-ab47-6b5192a09134"));
    // assertThat(parentNode
    // .getProperty("node_property_property_value").getString(), Is
    // .is("propVal"));
    // assertThat(parentNode.getProperty("node_key_key_type")
    // .getString(), Is.is("java.lang.String"));
    // final STNodeEntry nodeProperty = parentNode
    // .getNode("NODE_PROPERTY_propertyObj");
    // assertThat(nodeProperty.getProperty("node_key_value_type")
    // .getString(), Is.is("int"));
    // assertThat(nodeProperty.getProperty("node_hashValue")
    // .getString(), Is.is("f9facf49-a10f-35f3-90d5-1f2babe7478f"));
    // assertThat(nodeProperty.getProperty("node_property_name_type")
    // .getString(), Is.is("java.lang.String"));
    // assertThat(nodeProperty.getProperty("node_key_value_value")
    // .getString(), Is.is("2"));
    // assertThat(
    // nodeProperty.getProperty("property_name").getString(), Is
    // .is("propertyObj"));
    // assertThat(nodeProperty.getProperty("node_property_name_value")
    // .getString(), Is.is("name"));
    // assertThat(
    // nodeProperty.getProperty("node_typeName").getString(), Is
    // .is("org.openspotlight.persist.test.PropertyObj"));
    // }
    //

    /**
     * Should convert jcr STNodeEntry to bean.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldConvertJcrNodeToBean()
        throws Exception {
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

        final Node node = simplePersist.convertBeanToNode(

        obj3);
        final LevelThreeObj convertedFromJcr = simplePersist.convertNodeToBean(node);
        assertThat(obj3.getKey(), Is.is(convertedFromJcr.getKey()));
        assertThat(obj3.getProperty(), Is.is(convertedFromJcr.getProperty()));
        assertThat(obj3.getLevelTwoObj().getKey(), Is.is(convertedFromJcr.getLevelTwoObj().getKey()));
        assertThat(obj3.getLevelTwoObj().getPropertyObj().getName(),
                   Is.is(convertedFromJcr.getLevelTwoObj().getPropertyObj().getName()));
        assertThat(obj3.getLevelTwoObj().getLevelOneObj().getProperty(),
                   Is.is(convertedFromJcr.getLevelTwoObj().getLevelOneObj().getProperty()));
        assertThat(convertedFromJcr.getBooleanList(), Is.is(Arrays.asList(true, false, true, true)));
        assertThat(convertedFromJcr.getNumberMap().get(1.0), Is.is(3));
        assertThat(convertedFromJcr.getNumberMap().get(2.0), Is.is(2));
        assertThat(convertedFromJcr.getNumberMap().get(3.0), Is.is(1));

        assertThat(convertedFromJcr.getObjList().size(), Is.is(3));

        assertThat(convertedFromJcr.getObjList().get(0).getName(), Is.is("1"));
        assertThat(convertedFromJcr.getObjList().get(1).getName(), Is.is("2"));
        assertThat(convertedFromJcr.getObjList().get(2).getName(), Is.is("3"));

    }

    @Test
    public void shouldFindCollectionItems()
        throws Exception {
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
        final Node asJcr = simplePersist.convertBeanToNode(levelThree);
        final LevelThreeObj anotherLevelThree = simplePersist.convertNodeToBean(asJcr);
        assertThat(anotherLevelThree.getObjList().size(), Is.is(1));
        assertThat(anotherLevelThree.getObjList().get(0).getValue(), Is.is(obj1.getValue()));
        assertThat(anotherLevelThree.getObjList().get(0).getName(), Is.is(obj1.getName()));
        final Iterable<ListItemObj> result = simplePersist.findByProperties(ListItemObj.class, new String[] {"name"},
                                                                            new Object[] {"obj 1"});
        final ListItemObj item = result.iterator().next();
        assertThat(item.getName(), Is.is("obj 1"));
        assertThat(item.getValue(), Is.is(5));

    }

    @Test
    public void shouldFindJcrNodeByItsKey()
        throws Exception {
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

        simplePersist.convertBeanToNode(obj2);
        simplePersist.convertBeanToNode(

        obj2_1);
        simplePersist.convertBeanToNode(obj2_2);

        final Iterable<LevelTwoObj> result1 = simplePersist.findByProperties(LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("1"));
        final Iterable<LevelTwoObj> result2 = simplePersist.findByProperties(LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("2"));
        final Iterable<LevelTwoObj> result3 = simplePersist.findByProperties(LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("3"));
        final LevelTwoObj result1Item = result1.iterator().next();
        assertThat(result1Item.getKey(), Is.is("1"));
        assertThat(result2.iterator().next().getKey(), Is.is("2"));
        assertThat(result3.iterator().next().getKey(), Is.is("3"));
        assertThat(result1.iterator().next().getLevelOneObj().getRootObj(), IsNull.notNullValue());
        assertThat(result2.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));
        assertThat(result3.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));

    }

    @Test
    public void shouldFindJcrNodeByItsProperties()
        throws Exception {
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

        simplePersist.convertBeanToNode(

        obj2);
        simplePersist.convertBeanToNode(

        obj2_1);
        simplePersist.convertBeanToNode(

        obj2_2);

        final Iterable<LevelTwoObj> result1 = simplePersist.findByProperties(LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("1"));
        final Iterable<LevelTwoObj> result2 = simplePersist.findByProperties(LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("2"));
        final Iterable<LevelTwoObj> result3 = simplePersist.findByProperties(LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("3"));
        final LevelTwoObj item = result1.iterator().next();
        assertThat(item.getKey(), Is.is("1"));
        assertThat(result2.iterator().next().getKey(), Is.is("2"));
        assertThat(result3.iterator().next().getKey(), Is.is("3"));
        assertThat(result1.iterator().next().getLevelOneObj().getRootObj(), IsNull.notNullValue());
        assertThat(result2.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));
        assertThat(result3.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));

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

        simplePersist.convertBeanToNode(object1);
        simplePersist.convertBeanToNode(object2);
        simplePersist.convertBeanToNode(object3);
        final Iterable<ComposedKeyObject> foundNodes = simplePersist.findByProperties(ComposedKeyObject.class,
                                                                                      new String[] {"key1"},
                                                                                      new Object[] {"same key"});
        final Iterator<ComposedKeyObject> it = foundNodes.iterator();
        assertThat(it.next(), is(notNullValue()));
        assertThat(it.next(), is(notNullValue()));
        assertThat(it.hasNext(), is(false));
    }

    @Test
    public void shouldFindObjectsByNullParameter()
        throws Exception {
        final LevelOneObj obj1 = new LevelOneObj();
        obj1.setProperty("prop");
        final LevelOneObj obj2 = new LevelOneObj();
        obj2.setProperty(null);

        simplePersist.convertBeanToNode(obj1);
        simplePersist.convertBeanToNode(obj2);
        final Iterable<LevelOneObj> result = simplePersist.findByProperties(LevelOneObj.class, new String[] {"property"},
                                                                            new Object[] {null});
        final Iterator<LevelOneObj> it = result.iterator();
        assertThat(it.next(), is(notNullValue()));
        assertThat(it.hasNext(), is(false));
    }

    @Test
    public void shouldFindPropertyItems()
        throws Exception {

        final LevelTwoObj levelTwo = new LevelTwoObj();
        final PropertyObj propertyObj = new PropertyObj();
        propertyObj.setName("obj 1");
        propertyObj.setValue(5);
        levelTwo.setPropertyObj(propertyObj);
        simplePersist.convertBeanToNode(levelTwo);

        final Iterable<PropertyObj> result = simplePersist.findByProperties(PropertyObj.class, new String[] {"name"},
                                                                            new Object[] {"obj 1"});
        final Iterator<PropertyObj> it = result.iterator();
        final PropertyObj item = it.next();
        assertThat(item.getName(), Is.is("obj 1"));
        assertThat(item.getValue(), Is.is(5));
        assertThat(it.hasNext(), Is.is(false));

    }

    @Test
    @Ignore
    //this test becames invalid since it can't iterate all items to reorder
        public
        void shouldMaintainOrder()
            throws Exception {
        final int count = 20;

        final ArrayList<SimpleObject> objs = new ArrayList<SimpleObject>();

        for (int i = 0; i < count; i++) {
            final SimpleObject root = new SimpleObject();
            root.setId(i);
            objs.add(root);
        }

        simplePersist.convertBeansToNodes(objs);

        final Iterable<SimpleObject> nodes = simplePersist.findByProperties(SimpleObject.class, new String[] {}, new Object[] {});

        int i = 0;
        for (final SimpleObject obj: nodes) {
            assertThat(obj.getId(), Is.is(i));
            i++;
        }

    }

    @Test(expected = SLRuntimeException.class)
    public void shouldNotFindWithWrongPropertyName()
        throws Exception {
        simplePersist.findByProperties(RootObj.class, new String[] {"invalidProperty"}, new Object[] {null});
    }

    @Test
    public void shouldPersistAndReadStreamProperty()
        throws Exception {
        final ObjectWithInputStream pojo = new ObjectWithInputStream();
        final String contentAsString = "content";
        final InputStream content = new ByteArrayInputStream(contentAsString.getBytes());
        pojo.setStream(content);
        final Node jcrSTNodeEntry = simplePersist.convertBeanToNode(pojo);
        final ObjectWithInputStream convertedPojo = simplePersist.convertNodeToBean(jcrSTNodeEntry);
        final byte[] contentAsBytes = new byte[convertedPojo.getStream().available()];
        convertedPojo.getStream().read(contentAsBytes);
        final String newContentAsString = new String(contentAsBytes);
        assertThat(contentAsString, Is.is(newContentAsString));
    }

    @Test
    public void shouldPersistPropertyAsStream()
        throws Exception {
        final RootObj obj = new RootObj();
        final ObjectThatDoesntImplementSimpleNodeType objectThatDoesntImplementSimpleNodeType =
            new ObjectThatDoesntImplementSimpleNodeType();
        objectThatDoesntImplementSimpleNodeType.setName("name");
        objectThatDoesntImplementSimpleNodeType.setNumber(3);
        objectThatDoesntImplementSimpleNodeType.setParent(obj);
        obj.setObjectThatDoesntImplementSimpleNodeType(objectThatDoesntImplementSimpleNodeType);
        final Node jcrSTNodeEntry = simplePersist.convertBeanToNode(obj);
        final RootObj fromJcr = simplePersist.convertNodeToBean(jcrSTNodeEntry);
        assertThat(fromJcr.getObjectThatDoesntImplementSimpleNodeType().getName(),
                   Is.is(obj.getObjectThatDoesntImplementSimpleNodeType().getName()));
        assertThat(fromJcr.getObjectThatDoesntImplementSimpleNodeType().getNumber(),
                   Is.is(obj.getObjectThatDoesntImplementSimpleNodeType().getNumber()));
        assertThat(fromJcr.getObjectThatDoesntImplementSimpleNodeType().getParent() == fromJcr, Is.is(true));
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

        final Node newNode1 = simplePersist.convertBeanToNode(object1);
        final Node newNode2 = simplePersist.convertBeanToNode(object2);
        final Node newNode3 = simplePersist.convertBeanToNode(object3);
        assertThat(newNode1.getKey(), Is.is(IsNot.not(newNode2.getKey())));
        assertThat(newNode1.getKey(), Is.is(newNode3.getKey()));
    }

    @Test
    public void shouldConvertJcrNodeToBeanWithParent()
        throws Exception {
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
        final Node parentNode = session.withPartition(RegularPartitions.FEDERATION).createNewSimpleNode("a", "b", "c");

        final Node node = simplePersist.convertBeanToNode(parentNode, obj3);

        final LevelThreeObj convertedFromJcr = simplePersist.convertNodeToBean(node);
        assertThat(obj3.getKey(), Is.is(convertedFromJcr.getKey()));
        assertThat(obj3.getProperty(), Is.is(convertedFromJcr.getProperty()));
        assertThat(obj3.getLevelTwoObj().getKey(), Is.is(convertedFromJcr.getLevelTwoObj().getKey()));
        assertThat(obj3.getLevelTwoObj().getPropertyObj().getName(),
                   Is.is(convertedFromJcr.getLevelTwoObj().getPropertyObj().getName()));
        assertThat(obj3.getLevelTwoObj().getLevelOneObj().getProperty(),
                   Is.is(convertedFromJcr.getLevelTwoObj().getLevelOneObj().getProperty()));
        assertThat(convertedFromJcr.getBooleanList(), Is.is(Arrays.asList(true, false, true, true)));
        assertThat(convertedFromJcr.getNumberMap().get(1.0), Is.is(3));
        assertThat(convertedFromJcr.getNumberMap().get(2.0), Is.is(2));
        assertThat(convertedFromJcr.getNumberMap().get(3.0), Is.is(1));

        assertThat(convertedFromJcr.getObjList().size(), Is.is(3));

        assertThat(convertedFromJcr.getObjList().get(0).getName(), Is.is("1"));
        assertThat(convertedFromJcr.getObjList().get(1).getName(), Is.is("2"));
        assertThat(convertedFromJcr.getObjList().get(2).getName(), Is.is("3"));

    }

    @Test
    public void shouldFindCollectionItemsWithParent()
        throws Exception {
        final Node parentNode = session.withPartition(RegularPartitions.FEDERATION).createNewSimpleNode("a", "b", "c");

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
        final Node asJcr = simplePersist.convertBeanToNode(parentNode, levelThree);
        final LevelThreeObj anotherLevelThree = simplePersist.convertNodeToBean(asJcr);

        assertThat(anotherLevelThree.getObjList().size(), Is.is(1));
        assertThat(anotherLevelThree.getObjList().get(0).getValue(), Is.is(obj1.getValue()));
        assertThat(anotherLevelThree.getObjList().get(0).getName(), Is.is(obj1.getName()));
        final Iterable<ListItemObj> result = simplePersist.findByProperties(parentNode, ListItemObj.class, new String[] {"name"},
                                                                            new Object[] {"obj 1"});
        final ListItemObj item = result.iterator().next();
        assertThat(item.getName(), Is.is("obj 1"));
        assertThat(item.getValue(), Is.is(5));

    }

    @Test
    public void shouldFindJcrNodeByItsKeyWithParent()
        throws Exception {
        final Node parentNode = session.withPartition(RegularPartitions.FEDERATION).createNewSimpleNode("a", "b", "c");

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

        simplePersist.convertBeanToNode(parentNode, obj2);
        simplePersist.convertBeanToNode(parentNode,

        obj2_1);
        simplePersist.convertBeanToNode(parentNode, obj2_2);

        final Iterable<LevelTwoObj> result1 = simplePersist.findByProperties(parentNode, LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("1"));
        final Iterable<LevelTwoObj> result2 = simplePersist.findByProperties(parentNode, LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("2"));
        final Iterable<LevelTwoObj> result3 = simplePersist.findByProperties(parentNode, LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("3"));
        final LevelTwoObj result1Item = result1.iterator().next();
        assertThat(result1Item.getKey(), Is.is("1"));
        assertThat(result2.iterator().next().getKey(), Is.is("2"));
        assertThat(result3.iterator().next().getKey(), Is.is("3"));
        assertThat(result1.iterator().next().getLevelOneObj().getRootObj(), IsNull.notNullValue());
        assertThat(result2.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));
        assertThat(result3.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));

    }

    @Test
    public void shouldFindJcrNodeByItsPropertiesWithParent()
        throws Exception {
        final Node parentNode = session.withPartition(RegularPartitions.FEDERATION).createNewSimpleNode("a", "b", "c");
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

        simplePersist.convertBeanToNode(parentNode,

        obj2);
        simplePersist.convertBeanToNode(parentNode,

        obj2_1);
        simplePersist.convertBeanToNode(parentNode,

        obj2_2);

        final Iterable<LevelTwoObj> result1 = simplePersist.findByProperties(parentNode, LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("1"));
        final Iterable<LevelTwoObj> result2 = simplePersist.findByProperties(parentNode, LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("2"));
        final Iterable<LevelTwoObj> result3 = simplePersist.findByProperties(parentNode, LevelTwoObj.class,
                                                                             org.openspotlight.common.util.Arrays.of("key"),
                                                                             org.openspotlight.common.util.Arrays.of("3"));
        final LevelTwoObj item = result1.iterator().next();
        assertThat(item.getKey(), Is.is("1"));
        assertThat(result2.iterator().next().getKey(), Is.is("2"));
        assertThat(result3.iterator().next().getKey(), Is.is("3"));
        assertThat(result1.iterator().next().getLevelOneObj().getRootObj(), IsNull.notNullValue());
        assertThat(result2.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));
        assertThat(result3.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));

    }

    @Test
    public void shouldFindNodesWithSameKeyPropertyWhenUsingComposedKeyWithParent()
        throws Exception {
        final Node parentNode = session.withPartition(RegularPartitions.FEDERATION).createNewSimpleNode("a", "b", "c");

        final ComposedKeyObject object1 = new ComposedKeyObject();
        object1.setKey1("same key");
        object1.setKey2(1);
        final ComposedKeyObject object2 = new ComposedKeyObject();
        object2.setKey1("same key");
        object2.setKey2(2);
        final ComposedKeyObject object3 = new ComposedKeyObject();
        object3.setKey1("another key");
        object3.setKey2(1);

        simplePersist.convertBeanToNode(parentNode, object1);
        simplePersist.convertBeanToNode(parentNode, object2);
        simplePersist.convertBeanToNode(object3);
        final Iterable<ComposedKeyObject> foundNodes = simplePersist.findByProperties(parentNode, ComposedKeyObject.class,
                                                                                      new String[] {"key1"},
                                                                                      new Object[] {"same key"});
        final Iterator<ComposedKeyObject> it = foundNodes.iterator();
        assertThat(it.next(), is(notNullValue()));
        assertThat(it.next(), is(notNullValue()));
        assertThat(it.hasNext(), is(false));
    }

    @Test
    public void shouldFindObjectsByNullParameterWithParent()
        throws Exception {
        final Node parentNode = session.withPartition(RegularPartitions.FEDERATION).createNewSimpleNode("a", "b", "c");

        final LevelOneObj obj1 = new LevelOneObj();
        obj1.setProperty("prop");
        final LevelOneObj obj2 = new LevelOneObj();
        obj2.setProperty(null);

        simplePersist.convertBeanToNode(parentNode, obj1);
        simplePersist.convertBeanToNode(parentNode, obj2);
        final Iterable<LevelOneObj> result = simplePersist.findByProperties(parentNode, LevelOneObj.class,
                                                                            new String[] {"property"}, new Object[] {null});
        final Iterator<LevelOneObj> it = result.iterator();
        assertThat(it.next(), is(notNullValue()));
        assertThat(it.hasNext(), is(false));
    }

    @Test
    public void shouldFindPropertyItemsWithParent()
        throws Exception {
        final Node parentNode = session.withPartition(RegularPartitions.FEDERATION).createNewSimpleNode("a", "b", "c");

        final LevelTwoObj levelTwo = new LevelTwoObj();
        final PropertyObj propertyObj = new PropertyObj();
        propertyObj.setName("obj 1");
        propertyObj.setValue(5);
        levelTwo.setPropertyObj(propertyObj);
        final Node newnode = simplePersist.convertBeanToNode(parentNode, levelTwo);
        System.err.println(">>> " + newnode.getKey().getKeyAsString());
        System.err.println(">>> " + newnode.getParent(session));
        System.err.println(">>> " + newnode.getParent(session).getParent(session));

        final Iterable<PropertyObj> result = simplePersist.findByProperties(parentNode, PropertyObj.class, new String[] {"name"},
                                                                            new Object[] {"obj 1"});
        final Iterator<PropertyObj> it = result.iterator();
        final PropertyObj item = it.next();
        assertThat(item.getName(), Is.is("obj 1"));
        assertThat(item.getValue(), Is.is(5));
        assertThat(it.hasNext(), Is.is(false));

    }

}
