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

package org.openspotlight.storage.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.SLCollections.iterableToList;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.node.PropertyImpl;

import com.google.inject.Injector;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 5:08:39 PM
 */
public abstract class AbstractStorageSessionTest {

    protected enum ExamplePartition implements Partition {

        DEFAULT("DEFAULT"),
        FIRST("FIRST"),
        SECOND("SECOND");

        public static final PartitionFactory FACTORY = new PartitionFactory() {

                                                         @Override
                                                         public Partition getPartition(final String name) {
                                                             return ExamplePartition.valueOf(name.toUpperCase());
                                                         }

                                                         @Override
                                                         public Partition[] getValues() {
                                                             return ExamplePartition.values();
                                                         }
                                                     };

        private final String                 partitionName;

        private ExamplePartition(final String partitionName) {
            this.partitionName = partitionName;
        }

        public String getPartitionName() {
            return partitionName;
        }
    }

    public enum ExampleEnum {
        FIRST
    }

    protected Injector autoFlushInjector;

    protected Injector explicitFlushInjector;

    protected abstract Injector createsAutoFlushInjector();

    protected abstract Injector createsExplicitFlushInjector();

    protected abstract void internalCleanPreviousData()
        throws Exception;

    protected abstract boolean supportsAdvancedQueries();

    protected abstract boolean supportsAutoFlushInjector();

    protected abstract boolean supportsExplicitFlushInjector();

    @Before
    public void cleanPreviousData()
        throws Exception {
        setupInjectors();
        internalCleanPreviousData();
    }

    public void setupInjectors()
        throws Exception {
        if (supportsAutoFlushInjector()) {
            autoFlushInjector = createsAutoFlushInjector();
        }
        if (supportsExplicitFlushInjector()) {
            explicitFlushInjector = createsExplicitFlushInjector();
        }

    }

    @Test
    public void shouldAddAndRetriveLinksOnDifferentPartitionsWithAutoFlushInjector()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode c = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("c");
        final StorageNode b = session.withPartition(ExamplePartition.FIRST)
                               .createNewSimpleNode("c");
        final StorageNode a = session.withPartition(ExamplePartition.SECOND)
                               .createNewSimpleNode("c");

        final StorageLink aToCLink = session.addLink(a, c, "AtoC");
        final StorageLink aToALink = session.addLink(a, a, "AtoA");
        final StorageLink aToBLink = session.addLink(a, b, "AtoB");
        final StorageLink cToALink = session.addLink(c, a, "CtoA");

        assertThat(aToCLink.getSource(), is(a));
        assertThat(aToCLink.getTarget(), is(c));

        assertThat(aToALink.getSource(), is(a));
        assertThat(aToALink.getTarget(), is(a));

        assertThat(aToBLink.getSource(), is(a));
        assertThat(aToBLink.getTarget(), is(b));

        assertThat(cToALink.getSource(), is(c));
        assertThat(cToALink.getTarget(), is(a));

        final StorageLink foundCtoALink = session.getLink(c, a, "CtoA");
        assertThat(cToALink, is(foundCtoALink));

        final StorageLink foundAtoALink = session.getLink(a, a, "AtoA");
        assertThat(aToALink, is(foundAtoALink));

        final List<StorageLink> foundALinks = iterableToList(session.getLinks(a));
        assertThat(foundALinks.size(), is(3));
        assertThat(foundALinks.contains(aToCLink), is(true));
        assertThat(foundALinks.contains(aToALink), is(true));
        assertThat(foundALinks.contains(aToBLink), is(true));

        final List<StorageLink> foundBLinks = iterableToList(session.getLinks(b));
        assertThat(foundBLinks.size(), is(0));

        final List<StorageLink> foundAToCLinks = iterableToList(session.getLinks(a,
                                                                            "AtoC"));

        assertThat(foundAToCLinks.size(), is(1));
        assertThat(foundAToCLinks.contains(aToCLink), is(true));

        final List<StorageLink> foundAToBLinks = iterableToList(session.getLinks(a,
                                                                            b));
        assertThat(foundAToBLinks.size(), is(1));
        assertThat(foundAToBLinks.contains(aToBLink), is(true));
    }

    @Test
    public void shouldAddAndRetriveLinksOnDifferentPartitionsWithExplicitFlushInjector()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final StorageNode c = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("c");
        final StorageNode b = session.withPartition(ExamplePartition.FIRST)
                               .createNewSimpleNode("c");
        final StorageNode a = session.withPartition(ExamplePartition.SECOND)
                               .createNewSimpleNode("c");

        final StorageLink aToCLink = session.addLink(a, c, "AtoC");
        final StorageLink aToALink = session.addLink(a, a, "AtoA");
        final StorageLink aToBLink = session.addLink(a, b, "AtoB");
        final StorageLink cToALink = session.addLink(c, a, "CtoA");
        session.flushTransient();
        assertThat(aToCLink.getSource(), is(a));
        assertThat(aToCLink.getTarget(), is(c));

        assertThat(aToALink.getSource(), is(a));
        assertThat(aToALink.getTarget(), is(a));

        assertThat(aToBLink.getSource(), is(a));
        assertThat(aToBLink.getTarget(), is(b));

        assertThat(cToALink.getSource(), is(c));
        assertThat(cToALink.getTarget(), is(a));

        final StorageLink foundCtoALink = session.getLink(c, a, "CtoA");
        assertThat(cToALink, is(foundCtoALink));

        final StorageLink foundAtoALink = session.getLink(a, a, "AtoA");
        assertThat(aToALink, is(foundAtoALink));

        final List<StorageLink> foundALinks = iterableToList(session.getLinks(a));
        assertThat(foundALinks.size(), is(3));
        assertThat(foundALinks.contains(aToCLink), is(true));
        assertThat(foundALinks.contains(aToALink), is(true));
        assertThat(foundALinks.contains(aToBLink), is(true));

        final List<StorageLink> foundBLinks = iterableToList(session.getLinks(b));
        assertThat(foundBLinks.size(), is(0));

        final List<StorageLink> foundAToCLinks = iterableToList(session.getLinks(a,
                                                                            "AtoC"));

        assertThat(foundAToCLinks.size(), is(1));
        assertThat(foundAToCLinks.contains(aToCLink), is(true));

        final List<StorageLink> foundAToBLinks = iterableToList(session.getLinks(a,
                                                                            b));
        assertThat(foundAToBLinks.size(), is(1));
        assertThat(foundAToBLinks.contains(aToBLink), is(true));
    }

    @Test
    public void shouldAddAndRetriveLinksOnSamePartitionWithAutoFlushInjector()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode c = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("a", "b", "c");
        final StorageNode b = c.getParent(session);
        final StorageNode a = b.getParent(session);

        final StorageLink aToCLink = session.addLink(a, c, "AtoC");
        final StorageLink aToALink = session.addLink(a, a, "AtoA");
        final StorageLink aToBLink = session.addLink(a, b, "AtoB");
        final StorageLink cToALink = session.addLink(c, a, "CtoA");

        assertThat(aToCLink.getSource(), is(a));
        assertThat(aToCLink.getTarget(), is(c));

        assertThat(aToALink.getSource(), is(a));
        assertThat(aToALink.getTarget(), is(a));

        assertThat(aToBLink.getSource(), is(a));
        assertThat(aToBLink.getTarget(), is(b));

        assertThat(cToALink.getSource(), is(c));
        assertThat(cToALink.getTarget(), is(a));

        final StorageLink foundCtoALink = session.getLink(c, a, "CtoA");
        assertThat(cToALink, is(foundCtoALink));

        final StorageLink foundAtoALink = session.getLink(a, a, "AtoA");
        assertThat(aToALink, is(foundAtoALink));

        final List<StorageLink> foundALinks = iterableToList(session.getLinks(a));
        assertThat(foundALinks.size(), is(3));
        assertThat(foundALinks.contains(aToCLink), is(true));
        assertThat(foundALinks.contains(aToALink), is(true));
        assertThat(foundALinks.contains(aToBLink), is(true));

        final List<StorageLink> foundBLinks = iterableToList(session.getLinks(b));
        assertThat(foundBLinks.size(), is(0));

        final List<StorageLink> foundAToCLinks = iterableToList(session.getLinks(a,
                                                                            "AtoC"));

        assertThat(foundAToCLinks.size(), is(1));
        assertThat(foundAToCLinks.contains(aToCLink), is(true));

        final List<StorageLink> foundAToBLinks = iterableToList(session.getLinks(a,
                                                                            b));
        assertThat(foundAToBLinks.size(), is(1));
        assertThat(foundAToBLinks.contains(aToBLink), is(true));
    }

    @Test
    public void shouldAddAndRetriveLinksOnSamePartitionWithExplicitFlushInjector()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final StorageNode c = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("a", "b", "c");
        session.flushTransient();
        final StorageNode b = c.getParent(session);
        final StorageNode a = b.getParent(session);

        final StorageLink aToCLink = session.addLink(a, c, "AtoC");
        final StorageLink aToALink = session.addLink(a, a, "AtoA");
        final StorageLink aToBLink = session.addLink(a, b, "AtoB");
        final StorageLink cToALink = session.addLink(c, a, "CtoA");
        session.flushTransient();
        assertThat(aToCLink.getSource(), is(a));
        assertThat(aToCLink.getTarget(), is(c));

        assertThat(aToALink.getSource(), is(a));
        assertThat(aToALink.getTarget(), is(a));

        assertThat(aToBLink.getSource(), is(a));
        assertThat(aToBLink.getTarget(), is(b));

        assertThat(cToALink.getSource(), is(c));
        assertThat(cToALink.getTarget(), is(a));

        final StorageLink foundCtoALink = session.getLink(c, a, "CtoA");
        assertThat(cToALink, is(foundCtoALink));

        final StorageLink foundAtoALink = session.getLink(a, a, "AtoA");
        assertThat(aToALink, is(foundAtoALink));

        final List<StorageLink> foundALinks = iterableToList(session.getLinks(a));
        assertThat(foundALinks.size(), is(3));
        assertThat(foundALinks.contains(aToCLink), is(true));
        assertThat(foundALinks.contains(aToALink), is(true));
        assertThat(foundALinks.contains(aToBLink), is(true));

        final List<StorageLink> foundBLinks = iterableToList(session.getLinks(b));
        assertThat(foundBLinks.size(), is(0));

        final List<StorageLink> foundAToCLinks = iterableToList(session.getLinks(a,
                                                                            "AtoC"));

        assertThat(foundAToCLinks.size(), is(1));
        assertThat(foundAToCLinks.contains(aToCLink), is(true));

        final List<StorageLink> foundAToBLinks = iterableToList(session.getLinks(a,
                                                                            b));
        assertThat(foundAToBLinks.size(), is(1));
        assertThat(foundAToBLinks.contains(aToBLink), is(true));
    }

    @Test
    public void shouldCheckPartitionsFactoryBehavior() {
        final StorageSession session = autoFlushInjector.getInstance(StorageSession.class);
        assertThat(session, is(notNullValue()));
    }

    @Test
    public void shouldCreateAndRemoveLinksWithPropertiesOnDifferentPartitionsWithAutoFlushInjector()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        final StorageNode b = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("b");
        final StorageNode a = session.withPartition(ExamplePartition.FIRST)
                               .createNewSimpleNode("a");

        final StorageLink link = session.addLink(a, b, "AtoB");
        final StorageLink link2 = session.addLink(a, b, "AtoB2");
        link.setIndexedProperty(session, "sample", "value");

        final StorageLink foundLink = session.getLink(a, b, "AtoB");
        final StorageLink foundLink2 = session.getLink(a, b, "AtoB2");
        assertThat(foundLink, is(link));
        assertThat(foundLink2, is(link2));
        assertThat(foundLink.getPropertyValueAsString(session, "sample"), is(link
                                                                            .getPropertyValueAsString(session, "sample")));
        assertThat(foundLink.getPropertyValueAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, b, "AtoB");
        session.removeLink(link2);

        final StorageLink notFoundLink = session.getLink(a, b, "AtoB");
        final StorageLink notFoundLink2 = session.getLink(a, b, "AtoB2");

        assertThat(notFoundLink, is(nullValue()));
        assertThat(notFoundLink2, is(nullValue()));

        final StorageLink linkR = session.addLink(a, a, "AtoA");
        final StorageLink linkR2 = session.addLink(a, a, "AtoA2");
        linkR.setIndexedProperty(session, "sample", "value");

        final StorageLink foundLinkR = session.getLink(a, a, "AtoA");
        final StorageLink foundLinkR2 = session.getLink(a, a, "AtoA2");
        assertThat(foundLinkR, is(linkR));
        assertThat(foundLinkR2, is(linkR2));
        assertThat(foundLinkR.getPropertyValueAsString(session, "sample"), is(linkR
                                                                            .getPropertyValueAsString(session, "sample")));
        assertThat(foundLinkR.getPropertyValueAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, a, "AtoA");
        session.removeLink(linkR2);

        final StorageLink notfoundLinkR = session.getLink(a, a, "AtoA");
        final StorageLink notfoundLinkR2 = session.getLink(a, a, "AtoA2");

        assertThat(notfoundLinkR, is(nullValue()));
        assertThat(notfoundLinkR2, is(nullValue()));
    }

    @Test
    public void shouldCreateAndRemoveLinksWithPropertiesOnDifferentPartitionsWithExplicitFlushInjector()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);

        final StorageNode b = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("b");
        final StorageNode a = session.withPartition(ExamplePartition.FIRST)
                               .createNewSimpleNode("a");

        final StorageLink link = session.addLink(a, b, "AtoB");
        final StorageLink link2 = session.addLink(a, b, "AtoB2");
        link.setIndexedProperty(session, "sample", "value");
        session.flushTransient();
        final StorageLink foundLink = session.getLink(a, b, "AtoB");
        final StorageLink foundLink2 = session.getLink(a, b, "AtoB2");
        assertThat(foundLink, is(link));
        assertThat(foundLink2, is(link2));
        assertThat(foundLink.getPropertyValueAsString(session, "sample"), is(link
                                                                            .getPropertyValueAsString(session, "sample")));
        assertThat(foundLink.getPropertyValueAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, b, "AtoB");
        session.removeLink(link2);
        session.flushTransient();
        final StorageLink notFoundLink = session.getLink(a, b, "AtoB");
        final StorageLink notFoundLink2 = session.getLink(a, b, "AtoB2");

        assertThat(notFoundLink, is(nullValue()));
        assertThat(notFoundLink2, is(nullValue()));

        final StorageLink linkR = session.addLink(a, a, "AtoA");
        final StorageLink linkR2 = session.addLink(a, a, "AtoA2");
        linkR.setIndexedProperty(session, "sample", "value");
        session.flushTransient();

        final StorageLink foundLinkR = session.getLink(a, a, "AtoA");
        final StorageLink foundLinkR2 = session.getLink(a, a, "AtoA2");
        assertThat(foundLinkR, is(linkR));
        assertThat(foundLinkR2, is(linkR2));
        assertThat(foundLinkR.getPropertyValueAsString(session, "sample"), is(linkR
                                                                            .getPropertyValueAsString(session, "sample")));
        assertThat(foundLinkR.getPropertyValueAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, a, "AtoA");
        session.removeLink(linkR2);
        session.flushTransient();

        final StorageLink notfoundLinkR = session.getLink(a, a, "AtoA");
        final StorageLink notfoundLinkR2 = session.getLink(a, a, "AtoA2");

        assertThat(notfoundLinkR, is(nullValue()));
        assertThat(notfoundLinkR2, is(nullValue()));
    }

    @Test
    public void shouldCreateAndRemoveLinksWithPropertiesOnSamePartitionWithAutoFlushInjector()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        final StorageNode b = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("b");
        final StorageNode a = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("a");

        final StorageLink link = session.addLink(a, b, "AtoB");
        final StorageLink link2 = session.addLink(a, b, "AtoB2");
        final StorageLink linkR = session.addLink(a, a, "AtoA");
        final StorageLink linkR2 = session.addLink(a, a, "AtoA2");

        link.setIndexedProperty(session, "sample", "value");
        linkR.setIndexedProperty(session, "sample", "value");

        final StorageLink foundLink = session.getLink(a, b, "AtoB");
        final StorageLink foundLink2 = session.getLink(a, b, "AtoB2");
        assertThat(foundLink, is(link));
        assertThat(foundLink2, is(link2));
        assertThat(foundLink.getPropertyValueAsString(session, "sample"), is(link
                                                                            .getPropertyValueAsString(session, "sample")));
        assertThat(foundLink.getPropertyValueAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, b, "AtoB");
        session.removeLink(link2);

        final StorageLink notFoundLink = session.getLink(a, b, "AtoB");
        final StorageLink notFoundLink2 = session.getLink(a, b, "AtoB2");

        assertThat(notFoundLink, is(nullValue()));
        assertThat(notFoundLink2, is(nullValue()));

        final StorageLink foundLinkR = session.getLink(a, a, "AtoA");
        final StorageLink foundLinkR2 = session.getLink(a, a, "AtoA2");
        assertThat(foundLinkR, is(linkR));
        assertThat(foundLinkR2, is(linkR2));
        assertThat(foundLinkR.getPropertyValueAsString(session, "sample"), is(linkR
                                                                            .getPropertyValueAsString(session, "sample")));
        assertThat(foundLinkR.getPropertyValueAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, a, "AtoA");
        session.removeLink(linkR2);

        final StorageLink notfoundLinkR = session.getLink(a, a, "AtoA");
        final StorageLink notfoundLinkR2 = session.getLink(a, a, "AtoA2");

        assertThat(notfoundLinkR, is(nullValue()));
        assertThat(notfoundLinkR2, is(nullValue()));

    }

    @Test
    public void shouldCreateAndRemoveLinksWithPropertiesOnSamePartitionWithExplicitFlushInjector()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);

        final StorageNode b = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("b");
        final StorageNode a = session.withPartition(ExamplePartition.DEFAULT)
                               .createNewSimpleNode("a");

        final StorageLink link = session.addLink(a, b, "AtoB");
        final StorageLink link2 = session.addLink(a, b, "AtoB2");
        link.setIndexedProperty(session, "sample", "value");
        session.flushTransient();
        final StorageLink foundLink = session.getLink(a, b, "AtoB");
        final StorageLink foundLink2 = session.getLink(a, b, "AtoB2");
        assertThat(foundLink, is(link));
        assertThat(foundLink2, is(link2));
        assertThat(foundLink.getPropertyValueAsString(session, "sample"), is(link
                                                                            .getPropertyValueAsString(session, "sample")));
        assertThat(foundLink.getPropertyValueAsString(session, "sample"),
                   is("value"));

        session.removeLink(a, b, "AtoB");
        session.removeLink(link2);
        session.flushTransient();
        final StorageLink notFoundLink = session.getLink(a, b, "AtoB");
        final StorageLink notFoundLink2 = session.getLink(a, b, "AtoB2");

        assertThat(notFoundLink, is(nullValue()));
        assertThat(notFoundLink2, is(nullValue()));

    }

    @Test
    public void shouldCreateHierarchyAndLoadChildrenNodes() {

        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        final StorageNode root = session.withPartition(ExamplePartition.DEFAULT)
                                  .createNodeWithType("root").withSimpleKey("sequence", "1")
                                  .withSimpleKey("name", "name").andCreate();
        final StorageNode child1 =
            session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("child").withParent(root).withSimpleKey(
                                                                                           "sequence", "1")
                .withSimpleKey("name", "name")
                                    .andCreate();
        final StorageNode child2 =
            session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("child").withParent(root).withSimpleKey(
                                                                                           "sequence", "2")
                .withSimpleKey("name", "name")
                                    .andCreate();
        final StorageNode child3 =
            session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("child").withParent(root).withSimpleKey(
                                                                                           "sequence", "3")
                .withSimpleKey("name", "name")
                                    .andCreate();
        final StorageNode child4 =
            session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("child").withParent(root).withSimpleKey(
                                                                                           "sequence", "4")
                .withSimpleKey("name", "name")
                                    .andCreate();
        final StorageNode childAnotherType1 =
            session.withPartition(
                                                              ExamplePartition.DEFAULT).createNodeWithType("childAnotherType")
                                               .withParent(root).withSimpleKey("sequence", "1").withSimpleKey(
                                                                                                            "name", "name")
                .andCreate();
        final StorageNode childAnotherType2 =
            session.withPartition(
                                                              ExamplePartition.DEFAULT).createNodeWithType("childAnotherType")
                                               .withParent(root).withSimpleKey("sequence", "2").withSimpleKey(
                                                                                                            "name", "name")
                .andCreate();
        final StorageNode childAnotherType3 =
            session.withPartition(
                                                              ExamplePartition.DEFAULT).createNodeWithType("childAnotherType")
                                               .withParent(root).withSimpleKey("sequence", "3").withSimpleKey(
                                                                                                            "name", "name")
                .andCreate();
        final StorageNode childAnotherType4 =
            session.withPartition(
                                                              ExamplePartition.DEFAULT).createNodeWithType("childAnotherType")
                                               .withParent(root).withSimpleKey("sequence", "4").withSimpleKey(
                                                                                                            "name", "name")
                .andCreate();

        final List<StorageNode> allChildren = iterableToList(root.getChildren(
                                                                        ExamplePartition.DEFAULT, session));
        assertThat(allChildren.size(), is(8));
        assertThat(allChildren.contains(child1), is(true));
        assertThat(allChildren.contains(child2), is(true));
        assertThat(allChildren.contains(child3), is(true));
        assertThat(allChildren.contains(child4), is(true));
        assertThat(allChildren.contains(childAnotherType1), is(true));
        assertThat(allChildren.contains(childAnotherType2), is(true));
        assertThat(allChildren.contains(childAnotherType3), is(true));
        assertThat(allChildren.contains(childAnotherType4), is(true));

        final List<StorageNode> childrenType2 =
            iterableToList(root.getChildren(
                                                                               ExamplePartition.DEFAULT, session,
                "childAnotherType"));

        assertThat(childrenType2.size(), is(4));
        assertThat(childrenType2.contains(childAnotherType1), is(true));
        assertThat(childrenType2.contains(childAnotherType2), is(true));
        assertThat(childrenType2.contains(childAnotherType3), is(true));
        assertThat(childrenType2.contains(childAnotherType4), is(true));

        final List<StorageNode> childrenType1 = iterableToList(root.getChildren(
                                                                               ExamplePartition.DEFAULT, session, "child"));

        assertThat(childrenType1.size(), is(4));
        assertThat(childrenType1.contains(child1), is(true));
        assertThat(childrenType1.contains(child2), is(true));
        assertThat(childrenType1.contains(child3), is(true));
        assertThat(childrenType1.contains(child4), is(true));

    }

    @Test
    public void shouldCreateHierarchyAndLoadParentNode() {

        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("sameName").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final StorageNode newNode2 =
            session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("sameName").withParent(newNode1).withSimpleKey(
                                                                                                    "sequence", "1")
                .withSimpleKey("name", "name")
                                      .andCreate();
        final StorageNode newNode3 =
            session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("sameName").withParent(newNode2).withSimpleKey(
                                                                                                    "sequence", "3")
                .withSimpleKey("name", "name")
                                      .andCreate();

        final StorageNode foundNewNode3 =
            session.withPartition(
                                                          ExamplePartition.DEFAULT).createCriteria().withNodeType(
                                                                                                                   "sameName")
                .withProperty("sequence").equalsTo("3")
                                           .withProperty("name").equalsTo("name").buildCriteria()
                                           .andSearchUnique(session);
        assertThat(foundNewNode3, is(notNullValue()));
        final StorageNode foundNewNode2 = foundNewNode3.getParent(session);
        final StorageNode foundNewNode1 = foundNewNode2.getParent(session);
        assertThat(foundNewNode3, is(newNode3));
        assertThat(foundNewNode2, is(newNode2));
        assertThat(foundNewNode1, is(newNode1));
        assertThat(foundNewNode1.getPropertyValueAsString(session, "name"),
                   is("name"));
        assertThat(foundNewNode2.getPropertyValueAsString(session, "name"),
                   is("name"));
        assertThat(foundNewNode3.getPropertyValueAsString(session, "name"),
                   is("name"));
        assertThat(foundNewNode1.getPropertyValueAsString(session, "sequence"),
                   is("1"));
        assertThat(foundNewNode2.getPropertyValueAsString(session, "sequence"),
                   is("1"));
        assertThat(foundNewNode3.getPropertyValueAsString(session, "sequence"),
                   is("3"));
        assertThat(foundNewNode1.getType(), is("sameName"));
        assertThat(foundNewNode2.getType(), is("sameName"));
        assertThat(foundNewNode3.getType(), is("sameName"));

    }

    @Test
    public void shouldCreateTheSameKey()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode aNode = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode sameNode = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final String aKeyAsString = aNode.getKey().getKeyAsString();
        final String sameKeyAsString = sameNode.getKey().getKeyAsString();
        assertThat(aKeyAsString, is(sameKeyAsString));

    }

    @Test
    public void shouldDiscardTransientNodesOnExplicitFlush()
        throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final StorageNode newNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();
        session.flushTransient();
        final List<StorageNode> result = iterableToList(session.withPartition(
                                                                        ExamplePartition.DEFAULT).getNodes("newNode1"));

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.remove(session);
        final List<StorageNode> resultNotChanged =
            iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT)
                .getNodes("newNode1"));

        assertThat(resultNotChanged.size(), is(2));
        assertThat(resultNotChanged.contains(newNode1), is(true));
        assertThat(resultNotChanged.contains(newNode2), is(true));

        session.discardTransient();

        final List<StorageNode> resultStillNotChanged =
            iterableToList(session
                                                                        .withPartition(ExamplePartition.DEFAULT).getNodes(
                                                                            "newNode1"));

        assertThat(resultStillNotChanged.size(), is(2));
        assertThat(resultStillNotChanged.contains(newNode1), is(true));
        assertThat(resultStillNotChanged.contains(newNode2), is(true));

        session.flushTransient();
        final List<StorageNode> resultNotChangedAgain =
            iterableToList(session
                                                                        .withPartition(ExamplePartition.DEFAULT).getNodes(
                                                                            "newNode1"));

        assertThat(resultNotChangedAgain.size(), is(2));
        assertThat(resultNotChangedAgain.contains(newNode1), is(true));
        assertThat(resultNotChangedAgain.contains(newNode2), is(true));

    }

    @Test
    public void shouldExcludeParentAndChildrenOnAutoFlush()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode c1 = session.withPartition(ExamplePartition.DEFAULT)
                                .createNewSimpleNode("a1", "b1", "c1");
        final StorageNode b1 = c1.getParent(session);
        final StorageNode a1 = b1.getParent(session);
        a1.remove(session);
        final Iterable<StorageNode> foundA1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).getNodes("a1");
        final Iterable<StorageNode> foundB1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).getNodes("b1");
        final Iterable<StorageNode> foundC1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).getNodes("c1");
        assertThat(foundA1.iterator().hasNext(), is(false));
        assertThat(foundB1.iterator().hasNext(), is(false));
        assertThat(foundC1.iterator().hasNext(), is(false));

    }

    @Test
    public void shouldExcludeParentAndChildrenOnExplicitFlush()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final StorageNode c1 = session.withPartition(ExamplePartition.DEFAULT)
                                .createNewSimpleNode("a1", "b1", "c1");
        session.flushTransient();
        final StorageNode b1 = c1.getParent(session);
        final StorageNode a1 = b1.getParent(session);
        a1.remove(session);
        session.flushTransient();
        final Iterable<StorageNode> foundA1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).getNodes("a1");
        final Iterable<StorageNode> foundB1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).getNodes("b1");
        final Iterable<StorageNode> foundC1 = session.withPartition(
                                                              ExamplePartition.DEFAULT).getNodes("c1");
        assertThat(foundA1.iterator().hasNext(), is(false));
        assertThat(foundB1.iterator().hasNext(), is(false));
        assertThat(foundC1.iterator().hasNext(), is(false));

    }

    @Test
    public void shouldFindByLocalKey()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("root1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("root2").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final StorageNode aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();

        final List<StorageNode> theSameNodes =
            iterableToList(session
                .withPartition(
                                                                              ExamplePartition.DEFAULT)
                .createCriteria()
                .withLocalKey(
                                                                                                                                      aNode1
                                                                                                                                          .getKey()
                                                                                                                                          .getCompositeKey())
                .buildCriteria()
                .andSearch(
                                                                                                                                                                                                   session));
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
    }

    @Test
    public void shouldFindByLocalKeyAndProperties()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("root1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("root2").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final StorageNode aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        final List<StorageNode> theSameNodes =
            iterableToList(session
                .withPartition(
                                                                              ExamplePartition.DEFAULT)
                .createCriteria()
                .withLocalKey(
                                                                                                                                      aNode1
                                                                                                                                          .getKey()
                                                                                                                                          .getCompositeKey())
                .withProperty("parameter")
                                                               .equalsTo("value").buildCriteria().andSearch(session));
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        final List<StorageNode> onlyOneNode =
            iterableToList(session
                .withPartition(
                                                                             ExamplePartition.DEFAULT)
                .createCriteria()
                .withLocalKey(
                                                                                                                                     aNode1
                                                                                                                                         .getKey()
                                                                                                                                         .getCompositeKey())
                .withProperty("parameter1")
                                                              .equalsTo("value1").buildCriteria().andSearch(session));
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }

    @Test
    public void shouldFindByProperties()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("root1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("root2").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final StorageNode aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        final List<StorageNode> theSameNodes =
            iterableToList(session.withPartition(
                                                                              ExamplePartition.DEFAULT).createCriteria()
                                                               .withNodeType("node").withProperty("parameter").equalsTo(
                                                                                                                         "value")
                .buildCriteria().andSearch(session));
        assertThat(theSameNodes.size(), is(2));

        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        final List<StorageNode> onlyOneNode =
            iterableToList(session
                .withPartition(
                                                                             ExamplePartition.DEFAULT)
                .createCriteria()
                                                              .withNodeType("node")
                .withProperty("parameter1")
                .equalsTo(
                                                                                                                         "value1")
                .buildCriteria().andSearch(session));
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }

    @Test
    public void shouldFindByPropertiesContainingString()
        throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final StorageNode aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name1").withParent(root1).andCreate();
            final StorageNode aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "aeiou");
            root1.setIndexedProperty(session, "parameter", "foo");
            root2.setIndexedProperty(session, "parameter", "bar");
            final List<StorageNode> theSameNodes =
                iterableToList(session.withPartition(ExamplePartition.DEFAULT).createCriteria().withNodeType("node")
                    .withProperty("parameter").containsString("io").buildCriteria().andSearch(session));
            assertThat(theSameNodes.size(), is(2));
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
        }
    }

    @Test
    public void shouldFindByPropertiesContainingStringWithoutNodeType()
            throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("abc").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("def").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final StorageNode aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "ghi")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name1").withParent(root1).andCreate();
            final StorageNode aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "jkl")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "aeiou");
            root1.setIndexedProperty(session, "parameter", "foo");
            root2.setIndexedProperty(session, "parameter", "bar");
            final List<StorageNode> theSameNodes = iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                                                   .withProperty("parameter").containsString("io")
                                                                   .buildCriteria().andSearch(session));
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }

    @Test
    public void shouldFindByPropertiesEndingWithString()
        throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final StorageNode aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name1").withParent(root1).andCreate();
            final StorageNode aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "uio");
            root1.setIndexedProperty(session, "parameter", "fooiou");
            root2.setIndexedProperty(session, "parameter", "baior");
            final List<StorageNode> theSameNodes = iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                                                   .withNodeType("node").withProperty("parameter")
                                                                   .endsWithString("io").buildCriteria().andSearch(session));
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }

    @Test
    public void shouldFindByPropertiesEndingWithStringWithoutNodeType()
            throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("abc").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("def").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final StorageNode aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "ghi")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name1").withParent(root1).andCreate();
            final StorageNode aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "jkl")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "uio");
            root1.setIndexedProperty(session, "parameter", "fooiou");
            root2.setIndexedProperty(session, "parameter", "baior");
            final List<StorageNode> theSameNodes = iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                                                   .withProperty("parameter").endsWithString("io")
                                                                   .buildCriteria().andSearch(session));
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }

    @Test
    public void shouldFindByPropertiesStartingWithString()
        throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("node").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final StorageNode aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name1").withParent(root1).andCreate();
            final StorageNode aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "node")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                   "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "iou");
            root1.setIndexedProperty(session, "parameter", "fooiou");
            root2.setIndexedProperty(session, "parameter", "baior");
            final List<StorageNode> theSameNodes = iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                                                   .withNodeType("node").withProperty("parameter")
                                                                   .startsWithString("io").buildCriteria().andSearch(session));
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }

    @Test
    public void shouldFindByPropertiesStartingWithStringWithoutNodeType()
            throws Exception {
        if (supportsAdvancedQueries()) {
            final StorageSession session = autoFlushInjector
                                                      .getInstance(StorageSession.class);
            final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("abc").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name1").andCreate();
            final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                       .createNodeWithType("def").withSimpleKey("sequence", "1")
                                       .withSimpleKey("name", "name2").andCreate();
            final StorageNode aNode1 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "ghi")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name1").withParent(root1).andCreate();
            final StorageNode aNode2 =
                session
                                        .withPartition(ExamplePartition.DEFAULT)
                    .createNodeWithType(
                                                                                                "jkl")
                    .withSimpleKey("sequence", "1")
                    .withSimpleKey(
                                                                                                                                                  "name",
                        "name2").withParent(root2).andCreate();
            aNode1.setIndexedProperty(session, "parameter", "io");
            aNode2.setIndexedProperty(session, "parameter", "iou");
            root1.setIndexedProperty(session, "parameter", "fooiou");
            root2.setIndexedProperty(session, "parameter", "baior");
            final List<StorageNode> theSameNodes = iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                                                   .withProperty("parameter").startsWithString("io")
                                                                   .buildCriteria().andSearch(session));
            assertThat(theSameNodes.contains(aNode1), is(true));
            assertThat(theSameNodes.contains(aNode2), is(true));
            assertThat(theSameNodes.contains(root1), is(false));
            assertThat(theSameNodes.contains(root2), is(false));
            assertThat(theSameNodes.size(), is(2));
        }
    }

    @Test
    public void shouldFindByPropertiesWithNullValue()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("node").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "a").andCreate();
        final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("node").withSimpleKey("sequence", "2")
                                   .withSimpleKey("name", "b").andCreate();
        final StorageNode aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name1").withParent(root1).andCreate();
        final StorageNode aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name2").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "io");
        aNode2.setIndexedProperty(session, "parameter", "aeiou");
        root1.setIndexedProperty(session, "parameter", null);
        root2.setIndexedProperty(session, "parameter", null);
        final List<StorageNode> theSameNodes = iterableToList(session.withPartition(
                                                                              ExamplePartition.DEFAULT).createCriteria()
                                                               .withNodeType("node").withProperty("parameter").equalsTo(null)
                                                               .buildCriteria().andSearch(session));
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(root1), is(true));
        assertThat(theSameNodes.contains(root2), is(true));
        assertThat(theSameNodes.contains(aNode1), is(false));
        assertThat(theSameNodes.contains(aNode2), is(false));
    }

    @Test
    public void shouldFindByPropertiesWithoutNodeType()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("abc").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("def").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("ghi").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final StorageNode aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("jkl").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();
        aNode1.setIndexedProperty(session, "parameter", "value");
        aNode2.setIndexedProperty(session, "parameter", "value");
        aNode1.setIndexedProperty(session, "parameter1", "value1");
        aNode2.setIndexedProperty(session, "parameter1", "value2");
        final List<StorageNode> theSameNodes =
            iterableToList(session
                .withPartition(
                                                                              ExamplePartition.DEFAULT)
                .createCriteria()
                .withProperty(
                                                                                                                                      "parameter")
                .equalsTo("value").buildCriteria().andSearch(session));
        assertThat(theSameNodes.size(), is(2));
        assertThat(theSameNodes.contains(aNode1), is(true));
        assertThat(theSameNodes.contains(aNode2), is(true));
        assertThat(theSameNodes.contains(root1), is(false));
        assertThat(theSameNodes.contains(root2), is(false));
        final List<StorageNode> onlyOneNode =
            iterableToList(session
                .withPartition(
                                                                             ExamplePartition.DEFAULT)
                .createCriteria()
                .withProperty(
                                                                                                                                     "parameter1")
                .equalsTo("value1")
                .buildCriteria()
                .andSearch(
                                                                                                                                                                                              session));
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(true));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(false));
        assertThat(onlyOneNode.contains(root2), is(false));
    }

    @Test
    public void shouldFindByUniqueKey()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode aNode = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode theSameNode =
            session
                .withPartition(
                                                        ExamplePartition.DEFAULT)
                .createCriteria()
                .withUniqueKey(
                                                                                                                 aNode
                                                                                                                     .getKey())
                .buildCriteria().andSearchUnique(session);
        assertThat(aNode, is(theSameNode));
        assertThat(theSameNode.getProperty(session, "name").getValueAsString(
                                                                             session), is("name"));
        final StorageNode nullNode = session.withPartition(ExamplePartition.DEFAULT)
                                      .createCriteria().withUniqueKey(
                                                                      session.withPartition(ExamplePartition.DEFAULT)
                                                                             .createNodeKeyWithType("invalid").andCreate())
                                      .buildCriteria().andSearchUnique(session);
        assertThat(nullNode, is(nullValue()));

    }

    @Test
    public void shouldFindMultipleResults()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final StorageNode newNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();
        final StorageNode newNode3 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "another name").andCreate();
        final StorageNode newNode4 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("anotherName").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();
        final List<StorageNode> result =
            iterableToList(session
                .withPartition(
                                                                        ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeType(
                                                                                                                                 "newNode1")
                .withProperty("name").equalsTo("name")
                                                         .buildCriteria().andSearch(session));

        assertThat(result, is(notNullValue()));
        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));
        assertThat(result.contains(newNode3), is(false));
        assertThat(result.contains(newNode4), is(false));

    }

    @Test
    public void shouldFindNodesByType()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode root1 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("root1").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode root2 = session.withPartition(ExamplePartition.DEFAULT)
                                   .createNodeWithType("root2").withSimpleKey("sequence", "1")
                                   .withSimpleKey("name", "name").andCreate();
        final StorageNode aNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root1).andCreate();
        final StorageNode aNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                    .createNodeWithType("node").withSimpleKey("sequence", "1")
                                    .withSimpleKey("name", "name").withParent(root2).andCreate();

        final List<StorageNode> onlyOneNode = iterableToList(session.withPartition(
                                                                             ExamplePartition.DEFAULT).getNodes("root1"));
        assertThat(onlyOneNode.size(), is(1));
        assertThat(onlyOneNode.contains(aNode1), is(false));
        assertThat(onlyOneNode.contains(aNode2), is(false));
        assertThat(onlyOneNode.contains(root1), is(true));
        assertThat(onlyOneNode.contains(root2), is(false));
        final List<StorageNode> twoNodes = iterableToList(session.withPartition(
                                                                          ExamplePartition.DEFAULT).getNodes("node"));
        assertThat(twoNodes.size(), is(2));
        assertThat(twoNodes.contains(aNode1), is(true));
        assertThat(twoNodes.contains(aNode2), is(true));
        assertThat(twoNodes.contains(root1), is(false));
        assertThat(twoNodes.contains(root2), is(false));

    }

    @Test
    public void shouldFindNodeTypesOnDifferentPartitionsOnAutoFlush()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
                                                                            "a1", "b1", "c1");
        session.withPartition(ExamplePartition.FIRST).createNewSimpleNode("a2",
                                                                          "b2", "c2");
        final List<String> nodeTypes1 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).getAllNodeTypes());
        final List<String> nodeTypes2 = iterableToList(session.withPartition(
                                                                       ExamplePartition.FIRST).getAllNodeTypes());
        assertThat(nodeTypes1.contains("a1"), is(true));
        assertThat(nodeTypes1.contains("b1"), is(true));
        assertThat(nodeTypes1.contains("c1"), is(true));
        assertThat(nodeTypes2.contains("a2"), is(true));
        assertThat(nodeTypes2.contains("b2"), is(true));
        assertThat(nodeTypes2.contains("c2"), is(true));
        assertThat(nodeTypes2.contains("a1"), is(false));
        assertThat(nodeTypes2.contains("b1"), is(false));
        assertThat(nodeTypes2.contains("c1"), is(false));
        assertThat(nodeTypes1.contains("a2"), is(false));
        assertThat(nodeTypes1.contains("b2"), is(false));
        assertThat(nodeTypes1.contains("c2"), is(false));
    }

    @Test
    public void shouldFindNodeTypesOnDifferentPartitionsOnExplicitFlush()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
                                                                            "a1", "b1", "c1");
        session.withPartition(ExamplePartition.FIRST).createNewSimpleNode("a2",
                                                                          "b2", "c2");
        session.flushTransient();
        final List<String> nodeTypes1 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).getAllNodeTypes());
        final List<String> nodeTypes2 = iterableToList(session.withPartition(
                                                                       ExamplePartition.FIRST).getAllNodeTypes());
        assertThat(nodeTypes1.contains("a1"), is(true));
        assertThat(nodeTypes1.contains("b1"), is(true));
        assertThat(nodeTypes1.contains("c1"), is(true));
        assertThat(nodeTypes2.contains("a2"), is(true));
        assertThat(nodeTypes2.contains("b2"), is(true));
        assertThat(nodeTypes2.contains("c2"), is(true));
        assertThat(nodeTypes2.contains("a1"), is(false));
        assertThat(nodeTypes2.contains("b1"), is(false));
        assertThat(nodeTypes2.contains("c1"), is(false));
        assertThat(nodeTypes1.contains("a2"), is(false));
        assertThat(nodeTypes1.contains("b2"), is(false));
        assertThat(nodeTypes1.contains("c2"), is(false));
    }

    @Test
    public void shouldFindSimpleNodeWithStringIdOnAutoFlush()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createNewSimpleNode("a", "b", "c");
        final String nodeIdAsString = newNode.getKey().getKeyAsString();
        final StorageNode result = session.withPartition(ExamplePartition.DEFAULT)
                                    .createCriteria().withUniqueKeyAsString(nodeIdAsString)
                                    .buildCriteria().andSearchUnique(session);
        assertThat(result, is(newNode));

    }

    @Test
    public void shouldFindSimpleNodeWithStringIdOnExplicitFlush()
            throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final StorageNode newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createNewSimpleNode("a", "b", "c");
        session.flushTransient();
        final String nodeIdAsString = newNode.getKey().getKeyAsString();
        final StorageNode result = session.withPartition(ExamplePartition.DEFAULT)
                                    .createCriteria().withUniqueKeyAsString(nodeIdAsString)
                                    .buildCriteria().andSearchUnique(session);
        assertThat(result, is(newNode));

    }

    @Test
    public void shouldInsertNewNodeEntryAndFindUniqueWithAutoFlush() {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        StorageNode foundNewNode1 =
            session.withPartition(
                                                          ExamplePartition.DEFAULT).createCriteria().withNodeType(
                                                                                                                   "newNode1")
                .withProperty("sequence").equalsTo("1")
                                           .withProperty("name").equalsTo("name").buildCriteria()
                                           .andSearchUnique(session);
        assertThat(foundNewNode1, is(nullValue()));

        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        foundNewNode1 =
            session.withPartition(ExamplePartition.DEFAULT)
                               .createCriteria().withNodeType("newNode1").withProperty(
                                                                                        "sequence").equalsTo("1")
                .withProperty("name")
                               .equalsTo("name").buildCriteria().andSearchUnique(session);
        assertThat(foundNewNode1, is(notNullValue()));
        assertThat(foundNewNode1, is(newNode1));
    }

    @Test
    public void shouldInsertNewNodeEntryAndFindUniqueWithExplicitFlush() {

        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        StorageNode foundNewNode1 =
            session.withPartition(
                                                          ExamplePartition.DEFAULT).createCriteria().withNodeType(
                                                                                                                   "newNode1")
                .withProperty("sequence").equalsTo("1")
                                           .withProperty("name").equalsTo("name").buildCriteria()
                                           .andSearchUnique(session);
        assertThat(foundNewNode1, is(nullValue()));

        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        foundNewNode1 =
            session.withPartition(ExamplePartition.DEFAULT)
                               .createCriteria().withNodeType("newNode1").withProperty(
                                                                                        "sequence").equalsTo("1")
                .withProperty("name")
                               .equalsTo("name").buildCriteria().andSearchUnique(session);
        assertThat(foundNewNode1, is(nullValue()));
        session.flushTransient();
        final StorageNode foundNewNode2 =
            session.withPartition(
                                                          ExamplePartition.DEFAULT).createCriteria().withNodeType(
                                                                                                                   "newNode1")
                .withProperty("sequence").equalsTo("1")
                                           .withProperty("name").equalsTo("name").buildCriteria()
                                           .andSearchUnique(session);
        assertThat(foundNewNode2, is(notNullValue()));
        assertThat(foundNewNode2, is(newNode1));
    }

    @Test
    public void shouldInstantiateOneSessionPerThread()
        throws Exception {
        final StorageSession session1 = autoFlushInjector
                                                   .getInstance(StorageSession.class);
        final StorageSession session2 = autoFlushInjector
                                                   .getInstance(StorageSession.class);
        assertThat(session1, is(session2));

        final List<StorageSession> sessions = new CopyOnWriteArrayList<StorageSession>();
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread() {
            @Override
            public void run() {
                try {
                    sessions.add(autoFlushInjector
                                                  .getInstance(StorageSession.class));
                } finally {
                    latch.countDown();
                }
            }
        }.start();
        latch.await(5, TimeUnit.SECONDS);
        assertThat(sessions.size(), is(1));
        assertThat(session1, is(not(sessions.get(0))));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotSetKeyProperty()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        newNode1.setSimpleProperty(session, "sequence", "3");

        newNode1.getProperty(session, "sequence");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotSetKeyPropertyII()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();

        newNode1.getProperty(session, "sequence").setStringValue(session, "33");
    }

    @Test
    public void shouldRemoveNodesOnAutoFlush()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final StorageNode newNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();

        final List<StorageNode> result = iterableToList(session.withPartition(
                                                                        ExamplePartition.DEFAULT).getNodes("newNode1"));

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.remove(session);
        List<StorageNode> newResult = iterableToList(session.withPartition(
                                                                           ExamplePartition.DEFAULT).getNodes("newNode1"));
        assertThat(newResult.size(), is(1));
        assertThat(newResult.contains(newNode1), is(false));
        assertThat(newResult.contains(newNode2), is(true));

        newNode2.remove(session);
        newResult = iterableToList(session.withPartition(
                                                         ExamplePartition.DEFAULT).getNodes("newNode1"));
        assertThat(newResult.size(), is(0));

    }

    @Test
    public void shouldRemoveNodesOnExplicitFlush()
        throws Exception {
        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();
        final StorageNode newNode2 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "2")
                                      .withSimpleKey("name", "name").andCreate();
        session.flushTransient();
        final List<StorageNode> result = iterableToList(session.withPartition(
                                                                        ExamplePartition.DEFAULT).getNodes("newNode1"));

        assertThat(result.size(), is(2));
        assertThat(result.contains(newNode1), is(true));
        assertThat(result.contains(newNode2), is(true));

        newNode1.remove(session);
        final List<StorageNode> resultNotChanged =
            iterableToList(session
                                                                   .withPartition(ExamplePartition.DEFAULT)
                .getNodes("newNode1"));

        assertThat(resultNotChanged.size(), is(2));
        assertThat(resultNotChanged.contains(newNode1), is(true));
        assertThat(resultNotChanged.contains(newNode2), is(true));

        session.flushTransient();

        List<StorageNode> newResult = iterableToList(session.withPartition(
                                                                           ExamplePartition.DEFAULT).getNodes("newNode1"));
        assertThat(newResult.size(), is(1));
        assertThat(newResult.contains(newNode1), is(false));
        assertThat(newResult.contains(newNode2), is(true));

        newNode2.remove(session);
        session.flushTransient();
        newResult = iterableToList(session.withPartition(
                                                         ExamplePartition.DEFAULT).getNodes("newNode1"));
        assertThat(newResult.size(), is(0));

    }

    public void shouldReturnKeysOnPropertyList()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();

        assertThat(newNode1.getProperties(session).size(), is(2));
        for (final Property activeProperty: newNode1.getProperties(session)) {
            assertThat(activeProperty.isKey(), is(true));
            assertThat(activeProperty.getPropertyName().equals("sequence") ||
                       activeProperty.getPropertyName().equals("name"), is(true));
        }

        assertThat(newNode1.getProperty(session, "sequence").getValueAsString(session), is("1"));
        assertThat(newNode1.getProperty(session, "name").getValueAsString(session), is("name"));

        assertThat(newNode1.getPropertyValueAsString(session, "sequence"), is("1"));
        assertThat(newNode1.getPropertyValueAsString(session, "name"), is("name"));

        assertThat(newNode1.getPropertyValueAsStream(session, "sequence"), is(newNode1.getProperty(session, "sequence")
            .getValueAsStream(session)));
        assertThat(newNode1.getPropertyValueAsStream(session, "name"),
            is(newNode1.getProperty(session, "name").getValueAsStream(session)));

        assertThat(newNode1.getPropertyValueAsBytes(session, "sequence"), is(newNode1.getProperty(session, "sequence")
            .getValueAsBytes(session)));
        assertThat(newNode1.getPropertyValueAsBytes(session, "name"),
            is(newNode1.getProperty(session, "name").getValueAsBytes(session)));
    }

    @Test
    public void shouldSaveSimpleNodesOnAutoFlush()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT).createNewSimpleNode(
                                                                            "a", "b", "c");
        final Iterable<StorageNode> result = session.withPartition(
                                                             ExamplePartition.DEFAULT).getNodes("c");
        assertThat(result.iterator().hasNext(), is(true));

    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFindingWithUniqueAndOtherAttributes()
            throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        session.withPartition(ExamplePartition.DEFAULT).createCriteria()
                .withNodeType("newNode1").withProperty("sequence").equalsTo(
                                                                             "1").withProperty("name").equalsTo("name")
                .withUniqueKey(
                               session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeKeyWithType("sample").andCreate())
                .buildCriteria().andSearchUnique(session);
    }

    @Test
    public void shouldUpdatePropertyAndFindWithUpdatedValue()
        throws Exception {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode1 = session.withPartition(ExamplePartition.DEFAULT)
                                      .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                      .withSimpleKey("name", "name").andCreate();

        assertThat(newNode1.getProperty(session, "parameter"), is(nullValue()));

        newNode1.setIndexedProperty(session, "parameter", "firstValue");
        final List<StorageNode> found =
            iterableToList(session
                .withPartition(
                                                                       ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeType(
                                                                                                                                "newNode1")
                .withProperty("parameter").equalsTo("firstValue")
                                                        .buildCriteria().andSearch(session));
        assertThat(found.size(), is(1));
        assertThat(found.contains(newNode1), is(true));
        newNode1.getProperty(session, "parameter").setStringValue(session,
                                                                  "secondValue");

        final List<StorageNode> notFound =
            iterableToList(session
                .withPartition(
                                                                          ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeType(
                                                                                                                                   "newNode1")
                .withProperty("parameter").equalsTo("firstValue")
                                                           .buildCriteria().andSearch(session));
        assertThat(notFound.size(), is(0));

        final List<StorageNode> foundAgain =
            iterableToList(session
                .withPartition(
                                                                            ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeType(
                                                                                                                                     "newNode1")
                .withProperty("parameter").equalsTo("secondValue")
                                                             .buildCriteria().andSearch(session));
        assertThat(foundAgain.size(), is(1));
        assertThat(foundAgain.contains(newNode1), is(true));

    }

    @Test
    public void shouldWorkWithInputStreamPropertiesOnAutoFlush()
            throws Exception {

        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                     .withSimpleKey("name", "name").andCreate();

        final InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

        newNode.setSimpleProperty(session, "streamProperty", stream);

        final StorageNode loadedNode =
            session
                                        .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                        .withNodeType("newNode1").withProperty("sequence").equalsTo(
                                                                                                     "1").withProperty("name")
                .equalsTo("name")
                                        .buildCriteria().andSearchUnique(session);

        stream.reset();
        assertThat(IOUtils.contentEquals(newNode.getPropertyValueAsStream(session,
                                                                     "streamProperty"), stream), is(true));

        final InputStream loaded1 = loadedNode.getPropertyValueAsStream(session,
                                                             "streamProperty");

        final ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
        IOUtils.copy(loaded1, temporary1);
        final String asString1 = new String(temporary1.toByteArray());
        final ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
        final InputStream loaded2 = loadedNode.getPropertyValueAsStream(session,
                                                             "streamProperty");

        IOUtils.copy(loaded2, temporary2);
        final String asString2 = new String(temporary2.toByteArray());
        assertThat(asString1, is("streamValue"));
        assertThat(asString2, is("streamValue"));

    }

    @Test
    public void shouldWorkWithInputStreamPropertiesOnExplicitFlush()
            throws Exception {

        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final StorageNode newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                     .withSimpleKey("name", "name").andCreate();

        final InputStream stream = new ByteArrayInputStream("streamValue".getBytes());

        newNode.setSimpleProperty(session, "streamProperty", stream);

        final StorageNode nullNode =
            session.withPartition(ExamplePartition.DEFAULT)
                                      .createCriteria().withNodeType("newNode1").withProperty(
                                                                                               "sequence").equalsTo("1")
                .withProperty("name")
                                      .equalsTo("name").buildCriteria().andSearchUnique(session);

        assertThat(nullNode, is(nullValue()));
        session.flushTransient();
        final StorageNode loadedNode =
            session
                                        .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                        .withNodeType("newNode1").withProperty("sequence").equalsTo(
                                                                                                     "1").withProperty("name")
                .equalsTo("name")
                                        .buildCriteria().andSearchUnique(session);

        stream.reset();
        assertThat(IOUtils.contentEquals(newNode.getPropertyValueAsStream(session,
                                                                     "streamProperty"), stream), is(true));

        final InputStream loaded1 = loadedNode.getPropertyValueAsStream(session,
                                                             "streamProperty");

        final ByteArrayOutputStream temporary1 = new ByteArrayOutputStream();
        IOUtils.copy(loaded1, temporary1);
        final String asString1 = new String(temporary1.toByteArray());
        final ByteArrayOutputStream temporary2 = new ByteArrayOutputStream();
        final InputStream loaded2 = loadedNode.getPropertyValueAsStream(session,
                                                             "streamProperty");

        IOUtils.copy(loaded2, temporary2);
        final String asString2 = new String(temporary2.toByteArray());
        assertThat(asString1, is("streamValue"));
        assertThat(asString2, is("streamValue"));
    }

    @Test
    public void shouldWorkWithPartitions() {
        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);

        session.withPartition(ExamplePartition.DEFAULT).createNodeWithType("root")
                .withSimpleKey("sequence", "1").withSimpleKey("name", "name")
                .andCreate();
        session.withPartition(ExamplePartition.FIRST).createNodeWithType("root")
                .withSimpleKey("sequence", "1").withSimpleKey("name", "name")
                .andCreate();
        session.withPartition(ExamplePartition.SECOND).createNodeWithType("root")
                .withSimpleKey("sequence", "1").withSimpleKey("name", "name")
                .andCreate();
        final StorageNode root1 =
            session
                .withPartition(ExamplePartition.DEFAULT)
                                   .createCriteria()
                .withUniqueKey(
                                                                   session.withPartition(ExamplePartition.DEFAULT)
                                                                          .createNodeKeyWithType("root")
                                                                       .withSimpleKey("sequence", "1")
                                                                          .withSimpleKey("name", "name").andCreate())
                                   .buildCriteria().andSearchUnique(session);

        final StorageNode root2 =
            session
                .withPartition(ExamplePartition.FIRST)
                                   .createCriteria()
                .withUniqueKey(
                                                                   session.withPartition(ExamplePartition.FIRST)
                                                                          .createNodeKeyWithType("root")
                                                                       .withSimpleKey("sequence", "1")
                                                                          .withSimpleKey("name", "name").andCreate())
                                   .buildCriteria().andSearchUnique(session);

        final StorageNode root3 =
            session
                .withPartition(ExamplePartition.SECOND)
                                   .createCriteria()
                .withUniqueKey(
                                                                   session.withPartition(ExamplePartition.SECOND)
                                                                          .createNodeKeyWithType("root")
                                                                       .withSimpleKey("sequence", "1")
                                                                          .withSimpleKey("name", "name").andCreate())
                                   .buildCriteria().andSearchUnique(session);

        assertThat(root1, is(notNullValue()));

        assertThat(root2, is(notNullValue()));

        assertThat(root3, is(notNullValue()));

        assertThat(root1, is(not(root2)));

        assertThat(root2, is(not(root3)));

        final List<StorageNode> list1 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).getNodes("root"));
        final List<StorageNode> list2 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).getNodes("root"));
        final List<StorageNode> list3 = iterableToList(session.withPartition(
                                                                       ExamplePartition.DEFAULT).getNodes("root"));

        assertThat(list1.size(), is(1));
        assertThat(list2.size(), is(1));
        assertThat(list3.size(), is(1));
    }

    @Test
    public void shouldWorkWithSimplePropertiesOnAutoFlush()
        throws Exception {

        final StorageSession session = autoFlushInjector
                                                  .getInstance(StorageSession.class);
        final StorageNode newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                     .withSimpleKey("name", "name").andCreate();
        newNode.setIndexedProperty(session, "stringProperty", "value");

        final StorageNode loadedNode =
            session
                                        .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                        .withNodeType("newNode1").withProperty("sequence").equalsTo(
                                                                                                     "1").withProperty("name")
                .equalsTo("name")
                                        .buildCriteria().andSearchUnique(session);

        assertThat(((PropertyImpl) newNode.getProperty(session, "stringProperty"))
                                                                                 .getTransientValueAsString(session),
                   is("value"));

        assertThat(newNode.getPropertyValueAsString(session, "stringProperty"),
                   is("value"));

        assertThat(loadedNode.getPropertyValueAsString(session, "stringProperty"),
                   is("value"));

        final StorageNode anotherLoadedNode =
            session
                .withPartition(
                                                              ExamplePartition.DEFAULT)
                .createCriteria()
                .withNodeType(
                                                                                                                       "newNode1")
                .withProperty("stringProperty").equalsTo("value")
                                               .buildCriteria().andSearchUnique(session);

        assertThat(anotherLoadedNode, is(loadedNode));

        final StorageNode noLoadedNode =
            session.withPartition(
                                                         ExamplePartition.DEFAULT).createCriteria().withNodeType(
                                                                                                                  "newNode1")
                .withProperty("stringProperty").equalsTo("invalid")
                                          .buildCriteria().andSearchUnique(session);

        assertThat(noLoadedNode, is(nullValue()));
    }

    @Test
    public void shouldWorkWithSimplePropertiesOnExplicitFlush()
            throws Exception {

        final StorageSession session = explicitFlushInjector
                                                      .getInstance(StorageSession.class);
        final StorageNode newNode = session.withPartition(ExamplePartition.DEFAULT)
                                     .createNodeWithType("newNode1").withSimpleKey("sequence", "1")
                                     .withSimpleKey("name", "name").andCreate();

        final StorageNode loadedNode =
            session
                                        .withPartition(ExamplePartition.DEFAULT).createCriteria()
                                        .withNodeType("newNode1").withProperty("sequence").equalsTo(
                                                                                                     "1").withProperty("name")
                .equalsTo("name")
                                        .buildCriteria().andSearchUnique(session);

        assertThat(loadedNode, is(nullValue()));

        session.flushTransient();

        final StorageNode loadedNode1 =
            session.withPartition(
                                                        ExamplePartition.DEFAULT).createCriteria().withNodeType(
                                                                                                                 "newNode1")
                .withProperty("sequence").equalsTo("1")
                                         .withProperty("name").equalsTo("name").buildCriteria()
                                         .andSearchUnique(session);

        assertThat(loadedNode1, is(notNullValue()));

        newNode.setSimpleProperty(session, "stringProperty", "value");

        assertThat(((PropertyImpl) newNode.getProperty(session, "stringProperty"))
                          .getTransientValueAsString(session),
                   is("value"));

        assertThat(loadedNode1.getPropertyValueAsString(session, "stringProperty"),
                   is(nullValue()));

        session.flushTransient();
        final StorageNode loadedNode2 =
            session.withPartition(
                                                        ExamplePartition.DEFAULT).createCriteria().withNodeType(
                                                                                                                 "newNode1")
                .withProperty("sequence").equalsTo("1")
                                         .withProperty("name").equalsTo("name").buildCriteria()
                                         .andSearchUnique(session);

        assertThat(loadedNode1.getPropertyValueAsString(session, "stringProperty"),
                   is(nullValue()));

        assertThat(loadedNode2.getPropertyValueAsString(session, "stringProperty"),
                   is("value"));

        loadedNode1.forceReload();

        assertThat(loadedNode1.getPropertyValueAsString(session, "stringProperty"),
                   is("value"));

    }

}
