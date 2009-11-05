package org.openspotlight.persist.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Session;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.support.SimplePersistSupport;

/**
 * The Class SimplePersistSupportTest.
 */
public class SimplePersistSupportTest {

    //FIXME testAddNodePropertyOnCollection

    //FIXME testAddSimplePropertyOnMap

    //FIXME testAddNodePropertyOnMap

    //FIXME testRemoveNodePropertyOnCollection

    //FIXME testRemoveNodePropertyOnMap

    /** The provider. */
    private static JcrConnectionProvider provider;

    /**
     * Setup.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setup() throws Exception {
        provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
    }

    /** The session. */
    private Session session = null;

    /**
     * Close session.
     */
    @After
    public void closeSession() {
        if (this.session != null) {
            this.session.logout();
            this.session = null;
        }
    }

    /**
     * Setup session.
     */
    @Before
    public void setupSession() {
        this.session = provider.openSession();
    }

    /**
     * Should convert bean to jcr node.
     * 
     * @throws Exception the exception
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
        final Node node = SimplePersistSupport.convertBeanToJcr(SharedConstants.DEFAULT_JCR_ROOT_NAME, this.session, obj3);
        final String path = node.getPath();
        Assert.assertThat(
                          path,
                          Is.is("/osl/NODE_org_openspotlight_persist_test_RootObj/NODE_org_openspotlight_persist_test_LevelOneObj/NODE_org_openspotlight_persist_test_LevelTwoObj/NODE_org_openspotlight_persist_test_LevelThreeObj"));
        Assert.assertThat(node.getProperty("node.property.property.type").getString(), Is.is("java.lang.String"));
        Assert.assertThat(node.getProperty("node.typeName").getString(), Is.is("org.openspotlight.persist.test.LevelThreeObj"));
        Assert.assertThat(node.getProperty("node.hashValue").getString(), Is.is("401bb295-1e5a-349f-976a-47c9ab205eaa"));
        Assert.assertThat(node.getProperty("node.key.key.type").getString(), Is.is("java.lang.String"));

        final Node parentNode = node.getParent();
        Assert.assertThat(parentNode.getProperty("node.property.property.type").getString(), Is.is("java.lang.String"));
        Assert.assertThat(parentNode.getProperty("node.typeName").getString(),
                          Is.is("org.openspotlight.persist.test.LevelTwoObj"));
        Assert.assertThat(parentNode.getProperty("node.hashValue").getString(), Is.is("026dc045-a954-333e-ab47-6b5192a09134"));
        Assert.assertThat(parentNode.getProperty("node.property.property.value").getString(), Is.is("propVal"));
        Assert.assertThat(parentNode.getProperty("node.key.key.type").getString(), Is.is("java.lang.String"));
        final Node nodeProperty = parentNode.getNode("NODE_PROPERTY_propertyObj");
        Assert.assertThat(nodeProperty.getProperty("node.key.value.type").getString(), Is.is("int"));
        Assert.assertThat(nodeProperty.getProperty("node.hashValue").getString(), Is.is("f9facf49-a10f-35f3-90d5-1f2babe7478f"));
        Assert.assertThat(nodeProperty.getProperty("node.property.name.type").getString(), Is.is("java.lang.String"));
        Assert.assertThat(nodeProperty.getProperty("node.key.value.value").getString(), Is.is("2"));
        Assert.assertThat(nodeProperty.getProperty("property.name").getString(), Is.is("propertyObj"));
        Assert.assertThat(nodeProperty.getProperty("node.property.name.value").getString(), Is.is("name"));
        Assert.assertThat(nodeProperty.getProperty("node.typeName").getString(),
                          Is.is("org.openspotlight.persist.test.PropertyObj"));
    }

    /**
     * Should convert jcr node to bean.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldConvertJcrNodeToBean() throws Exception {
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

        final Node node = SimplePersistSupport.convertBeanToJcr(SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
                                                                this.session, obj3);
        final LevelThreeObj convertedFromJcr = SimplePersistSupport.convertJcrToBean(this.session, node, LazyType.EAGER);
        Assert.assertThat(obj3.getKey(), Is.is(convertedFromJcr.getKey()));
        Assert.assertThat(obj3.getProperty(), Is.is(convertedFromJcr.getProperty()));
        Assert.assertThat(obj3.getLevelTwoObj().getKey(), Is.is(convertedFromJcr.getLevelTwoObj().getKey()));
        Assert.assertThat(obj3.getLevelTwoObj().getPropertyObj().getName(),
                          Is.is(convertedFromJcr.getLevelTwoObj().getPropertyObj().getName()));
        Assert.assertThat(obj3.getLevelTwoObj().getLevelOneObj().getProperty(),
                          Is.is(convertedFromJcr.getLevelTwoObj().getLevelOneObj().getProperty()));
        Assert.assertThat(obj3.getBooleanList(), Is.is(Arrays.asList(true, false, true, true)));
        Assert.assertThat(obj3.getNumberMap().get(1.0), Is.is(3));
        Assert.assertThat(obj3.getNumberMap().get(2.0), Is.is(2));
        Assert.assertThat(obj3.getNumberMap().get(3.0), Is.is(1));
        Assert.assertThat(obj3.getObjList().get(0).getName(), Is.is("1"));
        Assert.assertThat(obj3.getObjList().get(1).getName(), Is.is("2"));
        Assert.assertThat(obj3.getObjList().get(2).getName(), Is.is("3"));
        Assert.assertThat(obj3.getObjMap().get(1).getName(), Is.is("1"));
        Assert.assertThat(obj3.getObjMap().get(2).getName(), Is.is("2"));
        Assert.assertThat(obj3.getObjMap().get(3).getName(), Is.is("3"));

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
        obj2_1.setKey("3");

        final Node node1 = SimplePersistSupport.convertBeanToJcr(SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
                                                                 this.session, obj3);
        final Node node2 = SimplePersistSupport.convertBeanToJcr(SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
                                                                 this.session, obj2_1);
        final Node node3 = SimplePersistSupport.convertBeanToJcr(SharedConstants.DEFAULT_JCR_ROOT_NAME + "/lalala/lelele",
                                                                 this.session, obj2_2);
        System.out.println(node1.getPath() + " " + node1.getProperty("node.pkonly.hashValue").getString());
        System.out.println(node2.getPath() + " " + node2.getProperty("node.pkonly.hashValue").getString());
        System.out.println(node3.getPath() + " " + node3.getProperty("node.pkonly.hashValue").getString());

        this.session.save();//necessary for the xpath to work
        final Set<LevelTwoObj> result1 = SimplePersistSupport.findNodesByPrimaryKeyElements(
                                                                                            this.session,
                                                                                            LevelTwoObj.class,
                                                                                            LazyType.LAZY,
                                                                                            org.openspotlight.common.util.Arrays.of("key"),
                                                                                            org.openspotlight.common.util.Arrays.of("1"));
        final Set<LevelTwoObj> result2 = SimplePersistSupport.findNodesByPrimaryKeyElements(
                                                                                            this.session,
                                                                                            LevelTwoObj.class,
                                                                                            LazyType.LAZY,
                                                                                            org.openspotlight.common.util.Arrays.of("key"),
                                                                                            org.openspotlight.common.util.Arrays.of("2"));
        final Set<LevelTwoObj> result3 = SimplePersistSupport.findNodesByPrimaryKeyElements(
                                                                                            this.session,
                                                                                            LevelTwoObj.class,
                                                                                            LazyType.LAZY,
                                                                                            org.openspotlight.common.util.Arrays.of("key"),
                                                                                            org.openspotlight.common.util.Arrays.of("3"));

        Assert.assertThat(result1.size(), Is.is(1));
        Assert.assertThat(result2.size(), Is.is(1));
        Assert.assertThat(result3.size(), Is.is(1));
        Assert.assertThat(result1.iterator().next().getKey(), Is.is("1"));
        Assert.assertThat(result2.iterator().next().getKey(), Is.is("2"));
        Assert.assertThat(result3.iterator().next().getKey(), Is.is("3"));
        Assert.assertThat(result1.iterator().next().getLevelOneObj().getRootObj(), Is.is(root));
        Assert.assertThat(result2.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));
        Assert.assertThat(result3.iterator().next().getLevelOneObj(), Is.is(IsNull.nullValue()));

    }

}
