package org.openspotlight.persist.test;

import javax.jcr.Node;
import javax.jcr.Session;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.support.SimplePersistSupport;

public class SimplePersistSupportTest {

    private static JcrConnectionProvider provider;

    @BeforeClass
    public static void setup() throws Exception {
        provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
    }

    private Session session = null;

    @After
    public void closeSession() {
        if (this.session != null) {
            this.session.logout();
            this.session = null;
        }
    }

    @Before
    public void setupSession() {
        this.session = provider.openSession();
    }

    @Test
    public void shouldConvertBeanToJcrNode() throws Exception {
        final RootObj root = new RootObj();
        final LevelOneObj obj1 = new LevelOneObj();
        final LevelTwoObj obj2 = new LevelTwoObj();
        final LevelThreeObj obj3 = new LevelThreeObj();
        obj1.setRootObj(root);
        obj2.setLevelOneObj(obj1);
        obj3.setLevelTwoObj(obj2);
        obj2.setProperty("propVal");
        final Node node = SimplePersistSupport.convertBeanToJcr(this.session, obj3);
        final String path = node.getPath();
        Assert.assertThat(
                          path,
                          Is.is("/osl/org_openspotlight_persist_test_RootObj/org_openspotlight_persist_test_LevelOneObj/org_openspotlight_persist_test_LevelTwoObj/org_openspotlight_persist_test_LevelThreeObj"));
        Assert.assertThat(node.getProperty("internal.property.property.type").getString(), Is.is("java.lang.String"));
        Assert.assertThat(node.getProperty("internal.typeName").getString(),
                          Is.is("org.openspotlight.persist.test.LevelThreeObj"));
        Assert.assertThat(node.getProperty("internal.hashValue").getString(), Is.is("189cb273-86af-3350-885c-243376daea19"));
        Assert.assertThat(node.getProperty("internal.key.key.type").getString(), Is.is("java.lang.String"));

        final Node parentNode = node.getParent();
        Assert.assertThat(parentNode.getProperty("internal.property.property.type").getString(), Is.is("java.lang.String"));
        Assert.assertThat(parentNode.getProperty("internal.typeName").getString(),
                          Is.is("org.openspotlight.persist.test.LevelTwoObj"));
        Assert.assertThat(parentNode.getProperty("internal.hashValue").getString(), Is.is("fc6406df-60ae-331a-8ebb-b6269be079bb"));
        Assert.assertThat(parentNode.getProperty("internal.property.property.value").getString(), Is.is("propVal"));
        Assert.assertThat(parentNode.getProperty("internal.key.key.type").getString(), Is.is("java.lang.String"));
    }

    @Test
    public void shouldConvertJcrNodeToBean() throws Exception {
        final RootObj root = new RootObj();
        final LevelOneObj obj1 = new LevelOneObj();
        final LevelTwoObj obj2 = new LevelTwoObj();
        final LevelThreeObj obj3 = new LevelThreeObj();
        obj1.setRootObj(root);
        obj2.setLevelOneObj(obj1);
        obj3.setLevelTwoObj(obj2);
        obj2.setProperty("propVal");
        final Node node = SimplePersistSupport.convertBeanToJcr(this.session, obj3);
        final LevelThreeObj convertedFromJcr = SimplePersistSupport.convertJcrToBean(this.session, node);
        Assert.assertThat(obj3.getKey(), Is.is(convertedFromJcr.getKey()));
        Assert.assertThat(obj3.getProperty(), Is.is(convertedFromJcr.getProperty()));
        Assert.assertThat(obj3.getLevelTwoObj().getKey(), Is.is(convertedFromJcr.getLevelTwoObj().getKey()));
        Assert.assertThat(obj3.getLevelTwoObj().getLevelOneObj().getProperty(),
                          Is.is(convertedFromJcr.getLevelTwoObj().getLevelOneObj().getProperty()));
    }

}
