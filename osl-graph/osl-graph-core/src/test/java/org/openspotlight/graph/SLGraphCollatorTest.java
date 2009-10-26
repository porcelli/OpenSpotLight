package org.openspotlight.graph;

import java.text.Collator;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.test.domain.JavaClassJavaMethodSimpleLink;
import org.openspotlight.graph.test.domain.JavaClassNode;
import org.openspotlight.graph.test.domain.JavaMethodNode;
import org.openspotlight.graph.test.domain.SQLElement;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SLGraphCollatorTest {

    static final Logger    LOGGER = Logger.getLogger(SLGraphTest.class);

    private SLGraph        graph;

    private SLGraphSession session;

    @BeforeClass
    public void init() throws AbstractFactoryException {
        final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        graph = factory.createTempGraph(true);
    }

    @AfterClass
    public void finish() {
        this.session.close();
        graph.shutdown();
    }

    @BeforeMethod
    public void beforeTest() throws SLGraphException {
        if (session == null) {
            session = graph.openSession();
        }
    }

    @AfterMethod
    public void afterTest() throws SLGraphSessionException {
        session.clear();
    }

    @Test
    public void testNodeCollator() {

        try {
            final SLNode root1 = session.createContext("1L").getRootNode();

            // test addNode ...
            final SQLElement element1 = root1.addNode(SQLElement.class, "selecao");
            final SQLElement element2 = root1.addNode(SQLElement.class, "seleção");
            Assert.assertEquals(element1, element2);

            // test getNode ...
            final SQLElement element3 = root1.getNode(SQLElement.class, "seleção");
            Assert.assertEquals(element1, element3);

            // the original name remains ...
            Assert.assertEquals(element1.getName(), "selecao");
            Assert.assertEquals(element2.getName(), "selecao");
            Assert.assertEquals(element3.getName(), "selecao");
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        }
    }

    @Test
    public void testLinkPropertyCollator() {

        try {

            final SLNode root1 = session.createContext("1L").getRootNode();
            final JavaClassNode javaClassNode1 = root1.addNode(JavaClassNode.class, "javaClassNode1");
            final JavaMethodNode javaMethodNode1 = javaClassNode1.addNode(JavaMethodNode.class, "javaMethodNode1");

            final JavaClassJavaMethodSimpleLink link = session.addLink(JavaClassJavaMethodSimpleLink.class, javaClassNode1, javaMethodNode1, false);

            final SLLinkProperty<String> prop1 = link.setProperty(String.class, "selecao", "great");
            final SLLinkProperty<String> prop2 = link.getProperty(String.class, "seleção");

            Assert.assertEquals(prop1, prop2);
            Assert.assertEquals(prop1.getName(), "selecao");
            Assert.assertEquals(prop1.getName(), "selecao");

            try {
                final Collator collator = Collator.getInstance(Locale.US);
                collator.setStrength(Collator.TERTIARY);
                link.getProperty(String.class, "seleção", collator);
                Assert.fail();
            } catch (final SLNodePropertyNotFoundException e) {
                Assert.assertTrue(true);
            }
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        }
    }

    @Test
    public void testNodePropertyCollator() {

        try {

            final SLNode root1 = session.createContext("1L").getRootNode();
            final SQLElement element = root1.addNode(SQLElement.class, "element");

            final SLNodeProperty<String> prop1 = element.setProperty(String.class, "selecao", "great");
            final SLNodeProperty<String> prop2 = element.getProperty(String.class, "seleção");
            Assert.assertEquals(prop1, prop2);
            Assert.assertEquals(prop1.getName(), "selecao");
            Assert.assertEquals(prop1.getName(), "selecao");

            try {
                final Collator collator = Collator.getInstance(Locale.US);
                collator.setStrength(Collator.TERTIARY);
                element.getProperty(String.class, "seleção", collator);
                Assert.fail();
            } catch (final SLNodePropertyNotFoundException e) {
                Assert.assertTrue(true);
            }
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
            Assert.fail();
        }
    }

}
