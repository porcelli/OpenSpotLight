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
package org.openspotlight.web;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.openspotlight.bundle.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.StorageSessionport org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public enum WebExecutionContextFactory {
    INSTANCE;

    private ExecutionContextFactory factory;

    public synchronized void contextStarted() {
        Injector injector = Guice.createInjector(new JRedisStorageModule(StStStorageSessionMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("name")), new SimplePersistModule(),
                new DetailedLoggerModule(), new DefaultExecutionContextFactoryModule());

        factory = injector.getInstance(ExecutionContextFactory.class);
    }

    public synchronized void contextStopped() {
        factory.closeResources();
        factory = null;
    }

    public ExecutionContext createExecutionContext(final ServletContext ctx,
                                                   final HttpServletRequest request) {
        final String repositoryName = OslServletDataSupport.getCurrentRepository(ctx, request);
        Repository repo = new Repository();
        repo.setActive(true);
        repo.setName(repositoryName);
        final JcrConnectionDescriptor descriptor = OslServletDataSupport.getJcrDescriptor(ctx, request);
        final String password = OslServletDataSupport.getPassword(ctx, request);
        final String username = OslServletDataSupport.getUserName(ctx, request);
        final ExecutionContext newContext = factory.createExecutionContext(username, password, descriptor, repo);
        return newContext;
    }

    public ExecutionContextFactory getFactory() {
        return factory;
    }

}
