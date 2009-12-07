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