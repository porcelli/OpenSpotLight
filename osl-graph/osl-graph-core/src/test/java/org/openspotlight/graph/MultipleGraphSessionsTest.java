package org.openspotlight.graph;

import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.util.AbstractFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class MultipleGraphSessionsTest {

    private SLGraph graph = null;
    private SLGraphSession session = null;

    @BeforeClass
    public void init() throws AbstractFactoryException{
        final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        graph = factory.createTempGraph(true);
    }
    
    @AfterClass
    public void finish(){
        session.close();
        graph.shutdown();
    }
    
    @Test
    public void testOpenCloseSessions() throws AbstractFactoryException, SLGraphException {
        session = graph.openSession();

        SLNode abstractTestNode = session.createContext("abstractTest").getRootNode();
        SLNode node1 = abstractTestNode.addNode("teste!");
        SLNode testRootNode = session.createContext("test").getRootNode();
        SLNode node2 = testRootNode.addNode("teste!");

        Assert.assertEquals(false, node1.getID().equals(node2.getID()));
        session.close();
        session = graph.openSession();

        abstractTestNode = session.createContext("abstractTest").getRootNode();
        node1 = abstractTestNode.addNode("teste!");
        testRootNode = session.createContext("test").getRootNode();
        node2 = testRootNode.addNode("teste!");

        Assert.assertEquals(false, node1.getID().equals(node2.getID()));
    }
    
    @Test
    public void testMultipleSessions() throws AbstractFactoryException, SLGraphException {
        session = graph.openSession();
        SLGraphSession session2 = graph.openSession();

        SLNode abstractTestNode = session.createContext("abstractTest").getRootNode();
        SLNode node1 = abstractTestNode.addNode("teste!");
        SLNode testRootNode = session.createContext("test").getRootNode();
        SLNode node2 = testRootNode.addNode("teste!");

        Assert.assertEquals(false, node1.getID().equals(node2.getID()));
        
        String node1ID = node1.getID();
        String node2ID = node2.getID();

        session.close();

        SLNode abstractTestNode2 = session2.createContext("abstractTest").getRootNode();
        SLNode node3 = abstractTestNode2.addNode("teste!");
        SLNode testRootNode2 = session2.createContext("test").getRootNode();
        SLNode node4 = testRootNode2.addNode("teste!");

        Assert.assertEquals(false, node3.getID().equals(node4.getID()));

        // I've commeted out the asserts having node1 and node2,
        // because they belong to a closed session. Nodes of a closed session cannot be used any more.
        // Thus, before closing the session I've got the IDs to bo asserted later on ;)
        Assert.assertEquals(false, node1ID.equals(node3.getID()));
        Assert.assertEquals(false, node2ID.equals(node4.getID()));
        //Assert.assertEquals(false, node1.getID().equals(node3.getID()));
        //Assert.assertEquals(false, node2.getID().equals(node4.getID()));
        
        session2.close();
        
    }

}
