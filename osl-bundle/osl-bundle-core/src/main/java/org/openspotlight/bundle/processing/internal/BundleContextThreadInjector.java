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
package org.openspotlight.bundle.processing.internal;

import java.util.Map;

import org.openspotlight.bundle.domain.Repository;
import org.openspotlight.common.task.exception.RunnableWithException;
import org.openspotlight.common.taskexec.RunnableListener;

public class BundleContextThreadInjector implements RunnableListener {

    private final ExecutionContextFactory factory;
    private final Repository[]            repositories;
    private final String                  username;
    private final String                  password;
    private final JcrConnectionDescriptor descriptor;

    public BundleContextThreadInjector(
                                        final ExecutionContextFactory factory, final Repository[] repositories,
                                        final String username, final String password, final JcrConnectionDescriptor descriptor ) {
        this.factory = factory;
        this.repositories = repositories;
        this.descriptor = descriptor;
        this.username = username;
        this.password = password;
    }

    public void afterRunningTask( final Map<String, Object> threadLocalMap,
                                  final RunnableWithException r ) {
    }

    public void beforeRunningTask( final Map<String, Object> threadLocalMap,
                                   final RunnableWithException r ) {
        if (r instanceof RunnableWithBundleContext) {
            final RunnableWithBundleContext runnable = (RunnableWithBundleContext)r;
            final ExecutionContext ctx = (ExecutionContext)threadLocalMap.get(runnable.getRepositoryName());
            runnable.setBundleContext(ctx);
        }

    }

    public void beforeSetupWorker( final Map<String, Object> threadLocalMap ) {
        for (final Repository repository : repositories) {
            threadLocalMap.put(repository.getName(), factory.createExecutionContext(username, password, descriptor, repository));
        }

    }

    public void beforeShutdownWorker( final Map<String, Object> threadLocalMap ) {
        for (final Repository repository : repositories) {
            final ExecutionContext execCtx = (ExecutionContext)threadLocalMap.get(repository.getName());
            execCtx.closeResources();
        }
    }

}
