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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.jcr.Node;
import javax.jcr.Session;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

@Ignore
// Just waiting a response from jackrabbit user group
public class MultithreadedSessionTest {

    enum Status {
        OK,
        ERROR
    }

    class Worker implements Callable<Status> {

        private final Session session;
        private final int     i;

        public Worker(
                       final Session session, final int i ) {
            this.session = session;
            this.i = i;
        }

        public Status call() throws Exception {
            try {

                Node parent1 = session.getRootNode().getNode("root");
                for (int j = 0; j < NODES_SIZE; j++) {
                    parent1 = parent1.addNode("node_" + i + "_" + j);
                }
                session.save();
                session.logout();
                return Status.OK;
            } catch (final Exception e) {
                e.printStackTrace();
                return Status.ERROR;
            }
        }

    }

    private final int THREAD_SIZE = 100;

    private final int NODES_SIZE  = 10;

    private Session openSession() {
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        final Session s = provider.openSession();
        return s;
    }

    @Test
    public void shouldInsertNodesInParallel() throws Exception {
        final Session s = openSession();
        s.getRootNode().addNode("root");
        s.save();
        s.logout();
        final List<Callable<Status>> workers = new ArrayList<Callable<Status>>(THREAD_SIZE);

        for (int i = 0; i < THREAD_SIZE; i++) {
            final Session session = openSession();
            workers.add(new Worker(session, i));
        }

        final ExecutorService threadPool = Executors.newFixedThreadPool(4);
        final List<Future<Status>> resultList = threadPool.invokeAll(workers);
        for (final Future<Status> result : resultList) {
            Assert.assertTrue(result.get().equals(Status.OK));
        }

    }

}