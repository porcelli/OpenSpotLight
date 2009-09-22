package org.openspotlight.graph;

import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.util.AbstractFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestMultipleGraphSessions {

    @Test
    public void testOpenCloseSessions() throws AbstractFactoryException, SLGraphException {
        final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        SLGraph graph = factory.createTempGraph(true);
        SLGraphSession session = graph.openSession();

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
        graph.shutdown();
    }

    @Test
    public void testMultipleSessions() throws AbstractFactoryException, SLGraphException {
        final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        SLGraph graph = factory.createTempGraph(true);
        SLGraphSession session = graph.openSession();
        SLGraphSession session2 = graph.openSession();

        SLNode abstractTestNode = session.createContext("abstractTest").getRootNode();
        SLNode node1 = abstractTestNode.addNode("teste!");
        SLNode testRootNode = session.createContext("test").getRootNode();
        SLNode node2 = testRootNode.addNode("teste!");

        Assert.assertEquals(false, node1.getID().equals(node2.getID()));
        
        session.close();

        SLNode abstractTestNode2 = session2.createContext("abstractTest").getRootNode();
        SLNode node3 = abstractTestNode2.addNode("teste!");
        SLNode testRootNode2 = session2.createContext("test").getRootNode();
        SLNode node4 = testRootNode2.addNode("teste!");

        Assert.assertEquals(false, node3.getID().equals(node4.getID()));
        Assert.assertEquals(false, node1.getID().equals(node3.getID()));
        Assert.assertEquals(false, node2.getID().equals(node4.getID()));
        graph.shutdown();
    }

}
