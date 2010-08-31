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
package org.openspotlight.bundle.language.java.resolver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.graph.GraphReaderorg.openspotlight.graph.Node;
import org.openspotlight.graph.query.SLInvalidQueryElementException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryException;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.storage.StorageSessionimport org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class InheritedNodeTest {

    @SuppressWarnings( "unchecked" )
    private <T extends Node> T findByProperty( final GraphReadGraphReader                                         final Class<T> type,
                                                 final String propertyName,
                                                 final String propertyValue )
        throws SLQueryException, SLInvalidQuerySyntaxException, SLInvalidQueryElementException {
        final SLQueryApi query1 = session.createQueryApi();
        query1.select().type(type.getName()).subTypes().selectEnd().where().type(type.getName()).subTypes().each().property(
                                                                                                                            propertyName).equalsTo().value(
                                                                                                                                                           propertyValue).typeEnd().whereEnd();
        final List<Node> result1 = query1.execute().getNodes();
        if (result1.size() > 0) {
            synchronized (result1.getLockObject()) {
                for (final Node found : result1) {
                    return (T)found;
                }
            }
        }

        final SLQueryApi query = session.createQueryApi();
        query.select().type(type.getName()).selectEnd().where().type(type.getName()).each().property(propertyName).equalsTo().value(
                                                                                                                                    propertyValue).typeEnd().whereEnd();
        final List<Node> result = query.execute().getNodes();
        if (result.size() > 0) {
            synchronized (result.getLockObject()) {
                for (final Node found : result) {
                    return (T)found;
                }
            }
        }

        return null;
    }

    private Repository repository = new Repository();
    {
        repository.setActive(true);
        repository.setName("name");
    }

    @Test
    public void shouldFindNodesByItsProperties() throws Exception {

        Injector injector = Guice.createInjector(new JRedisStorageModule(StStorageSessionlushMode.AUTO,
                                                                         ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                                                                         repositoryPath("repository")),
                                                 new SimplePersistModule(), new DetailedLoggerModule(),
                                                 new DefaultExecutionContextFactoryModule());

        final ExecutionContextFactory factory = injector.getInstance(ExecutionContextFactory.class);
        final ExecutionContext context = factory.createExecutionContext("sa", "sa", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
                                                                        repository);
        GraphReader graphSGraphReaderGraphSession();
        JavaTypeClass newClass = graphSession.createContext("context").getRootNode().addChildNode(JavaTypeClass.class, "newClass");
        newClass.setQualifiedName("qualifiedName");
        newClass.setSimpleName("simpleName");
        graphSession.save();
        context.closeResources();
        factory.closeResources();
        graphSession = context.getGraphSession();
        newClass = graphSession.getContext("context").getRootNode().addChildNode(JavaTypeClass.class, "newClass");
        Assert.assertThat(newClass.getQualifiedName(), Is.is("qualifiedName"));
        Assert.assertThat(newClass.getSimpleName(), Is.is("simpleName"));
        newClass = (JavaTypeClass)findByProperty(context.getGraphSession(), JavaType.class, "qualifiedName", "qualifiedName");
        Assert.assertThat(newClass.getQualifiedName(), Is.is("qualifiedName"));
        Assert.assertThat(newClass.getSimpleName(), Is.is("simpleName"));

    }

}
