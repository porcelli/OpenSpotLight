package org.openspotlight.persist.test;

import javax.jcr.Node;
import javax.jcr.Session;

import org.junit.After;
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

    }

}
