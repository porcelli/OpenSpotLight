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
package org.openspotlight.bundle.common;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.jcr.Node;
import javax.jcr.Repository;

import org.apache.jackrabbit.rmi.remote.RemoteRepository;
import org.apache.jackrabbit.rmi.server.RemoteAdapterFactory;
import org.apache.jackrabbit.rmi.server.ServerAdapterFactory;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;
import org.openspotlight.remote.server.UserAuthenticator;

public abstract class AbstractTestServerClass {

    protected abstract void doWork( JcrConnectionProvider provider )
        throws Exception;

    public void doWorkAndExposeServers() {
        final JcrConnectionProvider provider = JcrConnectionProvider.createFromData(
                                                                    getDescriptor());
        provider.closeRepositoryAndCleanResources();
        final Repository repository = provider.openRepository();
        try {
            doWork(provider);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
        exposeJcrOnRmi(repository);
        exportDataOnXml(provider);
        exposeGraphServerAndStillWaiting();
    }

    private void exportDataOnXml( final JcrConnectionProvider provider ) {
        try {
            final SessionWithLock session = provider
                                                    .openSession();
            final Node node = session.getRootNode().getNode(
                                                            SLConsts.DEFAULT_JCR_ROOT_NAME);
            final String exportedFileName = getExportedFileName();
            if (exportedFileName != null) {
                if (exportedFileName.contains("/")) {
                    new File(exportedFileName.substring(0, exportedFileName
                                                                           .lastIndexOf("/"))).mkdirs();
                }
                final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(
                                                                                           new FileOutputStream(exportedFileName));
                session.exportSystemView(node.getPath(), bufferedOutputStream, false,
                                         false);
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    protected final void exposeGraphServerAndStillWaiting() {
        RemoteGraphSessionServer server = null;
        try {
            try {
                server = new RemoteGraphSessionServer(new UserAuthenticator() {

                    public boolean canConnect( final String userName,
                                               final String password,
                                               final String clientHost ) {
                        return true;
                    }

                    public boolean equals( final Object o ) {
                        return this.getClass().equals(o.getClass());
                    }
                }, 7070, 60 * 1000 * 10L, getDescriptor());
                System.err.println("Server waiting connections on port 7070");
                if (!shutdownAtFinish()) {
                    while (true) {
                        Thread.sleep(5000);
                    }
                }
                System.exit(0);
            } catch (final Exception e) {
                throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
            }
        } finally {
            if (server != null) {
                server.shutdown();
            }
        }

    }

    protected final void exposeJcrOnRmi( final Repository repository ) {
        try {
            final RemoteAdapterFactory saFactory = new ServerAdapterFactory();
            final RemoteRepository remote = saFactory
                                                     .getRemoteRepository(repository);

            final Registry registry = LocateRegistry
                                                    .createRegistry(Registry.REGISTRY_PORT);
            registry.bind("jackrabbit.repository", remote);
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    protected abstract JcrConnectionDescriptor getDescriptor();

    protected abstract String getExportedFileName();

    protected abstract boolean shutdownAtFinish();

}
