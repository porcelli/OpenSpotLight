package org.openspotlight.jcr.provider.test;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.JCRUtil;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class JcrVersioningTest {

    private static final String          VERSIONABLE_NODE = "newVersionableNode";
    private static JcrConnectionProvider provider;

    @BeforeClass
    public static void setup() throws Exception {
        JcrVersioningTest.provider = JcrConnectionProvider
                                                          .createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        final Session session = JcrVersioningTest.provider.openSession();
        Node newVersionableNode;
        try {
            newVersionableNode = session.getRootNode().getNode(
                                                               JcrVersioningTest.VERSIONABLE_NODE);
        } catch (final PathNotFoundException e) {
            newVersionableNode = session.getRootNode().addNode(
                                                               JcrVersioningTest.VERSIONABLE_NODE);
            JCRUtil.makeReferenceable(newVersionableNode);
            JCRUtil.makeVersionable(newVersionableNode);
            session.save();
        }
        session.logout();
    }

    @Test
    public void shouldIncrementVersions2() throws
        Exception {

        Session sessionA = provider.openSession();
        Session sessionB = provider.openSession();

        Node nodeA = sessionA.getRootNode().getNode(VERSIONABLE_NODE);
        nodeA.checkout();
        nodeA.setProperty("something", "other");
        nodeA.save();
        Node versionA = nodeA.checkin();

        Node nodeB = sessionB.getRootNode().getNode(VERSIONABLE_NODE);
        nodeB.checkout();
        nodeB.setProperty("something", "xxx");
        //        nodeB.save();
        //        Node versionB = nodeB.checkin();

        System.out.println("nodeA : " + nodeA.getUUID());
        System.out.println("nodeB : " + nodeB.getUUID());

        System.out.println("versionA: " + versionA.getName() + " : " + versionA.getUUID());
        //        System.out.println("versionB: " + versionB.getName() + " : " + versionA.getUUID());

        System.out.println("nodeA prop: " + nodeA.getProperty("something").getValue().getString());
        System.out.println("nodeB prop: " + nodeB.getProperty("something").getValue().getString());

        System.out.println("nodeA:");
        VersionIterator iter = nodeA.getVersionHistory().getAllVersions();
        while (iter.hasNext()) {
            final Version version = iter.nextVersion();
            if (!version.getName().equals("jcr:rootVersion")) {
                System.out.println("\t" + version.getName());
            }
        }

        System.out.println("nodeB:");
        iter = nodeB.getVersionHistory().getAllVersions();
        while (iter.hasNext()) {
            final Version version = iter.nextVersion();
            if (!version.getName().equals("jcr:rootVersion")) {
                System.out.println("\t" + version.getName());
            }
        }

        sessionA.save();
        sessionB.logout();

        Session sessionC = provider.openSession();
        Node nodeC = sessionC.getRootNode().getNode(VERSIONABLE_NODE);

        System.out.println("nodeC prop: " + nodeC.getProperty("something").getValue().getString());

        System.out.println("nodeC:");
        iter = nodeC.getVersionHistory().getAllVersions();
        while (iter.hasNext()) {
            final Version version = iter.nextVersion();
            if (!version.getName().equals("jcr:rootVersion")) {
                System.out.println("\t" + version.getName());
            }
        }

    }
}