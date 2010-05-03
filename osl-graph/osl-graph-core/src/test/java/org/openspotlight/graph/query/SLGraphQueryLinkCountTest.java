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
package org.openspotlight.graph.query;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.graph.*;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.test.domain.link.MethodContainsParam;
import org.openspotlight.graph.test.domain.link.TypeContainsMethod;
import org.openspotlight.graph.test.domain.node.JavaInterface;
import org.openspotlight.graph.test.domain.node.JavaTypeMethod;
import org.openspotlight.graph.test.domain.node.MethodParam;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * The Class SLGraphQueryLinkCountTest.
 *
 * @author Vitor Hugo Chagas
 */

public class SLGraphQueryLinkCountTest {

    /**
     * The Constant LOGGER.
     */
    static final Logger LOGGER = Logger.getLogger(SLGraphQueryLinkCountTest.class);

    /**
     * The graph.
     */
    private static SLGraph graph;

    /**
     * The session.
     */
    private static SLGraphSession session;

    private static AuthenticatedUser user;

    /**
     * Finish.
     */
    @AfterClass
    public static void finish() {
        session.close();
        graph.shutdown();
    }

    /**
     * Gets the i face type set.
     *
     * @return the i face type set
     */
    private static Set<Class<?>> getIFaceTypeSet() {
        final Set<Class<?>> set = new HashSet<Class<?>>();
        set.add(java.util.Collection.class);
        set.add(java.util.Map.class);
        set.add(java.util.List.class);
        set.add(java.util.Set.class);
        set.add(java.util.SortedSet.class);
        return set;
    }

    /**
     * Quick graph population.
     */
    @BeforeClass
    public static void quickGraphPopulation() {
        try {

            JcrConnectionProvider.createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();


            Injector injector = Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO,
                    ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                    repositoryPath("repository")),
                    new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));


            graph = injector.getInstance(SLGraph.class);

            final SecurityFactory securityFactory = injector.getInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser("testUser");
            user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");
            session = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
            final SLContext context = session.createContext("linkCountTest");
            final SLNode root = context.getRootNode();
            final Set<Class<?>> types = getIFaceTypeSet();
            for (final Class<?> type : types) {
                final Method[] methods = type.getDeclaredMethods();
                LOGGER.info(type.getName() + ": " + methods.length + " methods");
                final JavaInterface javaInteface = root.addNode(JavaInterface.class, type.getName());
                javaInteface.setProperty(String.class, VisibilityLevel.PUBLIC, "caption", type.getName());
                for (final Method method : methods) {
                    final JavaTypeMethod javaMethod = javaInteface.addNode(JavaTypeMethod.class, method.getName());
                    javaMethod.setProperty(String.class, VisibilityLevel.PUBLIC, "caption", method.getName());
                    session.addLink(TypeContainsMethod.class, javaInteface, javaMethod, false);
                    final Class<?>[] paramTypes = method.getParameterTypes();
                    LOGGER.info("\t\t" + method.getName() + ": " + paramTypes.length + " params");
                    for (final Class<?> paramType : paramTypes) {
                        final MethodParam methodParam = javaMethod.addNode(MethodParam.class, paramType.getName());
                        methodParam.setProperty(String.class, VisibilityLevel.PUBLIC, "caption", paramType.getName());
                        session.addLink(MethodContainsParam.class, javaMethod, methodParam, false);
                    }
                }
            }
            session.save();
            session.close();
            session = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Find i face id.
     *
     * @param type the type
     * @return the string
     * @throws SLInvalidQuerySyntaxException
     */
    private String findIFaceID(final Class<?> type) throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException {
        final SLQueryApi query = session.createQueryApi();
        query.select().allTypes().onWhere().selectEnd().where().type(JavaInterface.class.getName()).each().property("caption").equalsTo().value(
                type.getName()).typeEnd().whereEnd();
        final SLQueryResult result = query.execute();
        final Collection<SLNode> nodes = result.getNodes();
        return nodes.size() > 0 ? result.getNodes().iterator().next().getID() : null;
    }

    /**
     * Select collection methods with all in caption and with one param.
     */
    @Test
    public void selectCollectionMethodsWithAllInCaptionAndWithOneParam() {

        try {

            final String id = findIFaceID(java.util.Collection.class);
            final SLNode node = session.getNodeByID(id);
            final Collection<SLNode> inputNodes = new ArrayList<SLNode>();
            inputNodes.add(node);

            final SLQueryApi query = session.createQueryApi();

            query.select().type(JavaTypeMethod.class.getName()).byLink(TypeContainsMethod.class.getName()).b().selectEnd().select().type(
                    JavaTypeMethod.class.getName()).selectEnd().where().type(
                    JavaTypeMethod.class.getName()).each().property(
                    "caption").contains().value(
                    "All").and().each().link(
                    MethodContainsParam.class.getName()).a().count().equalsTo().value(
                    1).typeEnd().whereEnd();

            final SLQueryResult result = query.execute(new String[]{id});
            final Collection<SLNode> nodes = result.getNodes();
            QueryUtil.printResult(nodes);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Select map zero param methods.
     */
    @Test
    public void selectMapZeroParamMethods() {

        try {

            final String id = findIFaceID(java.util.Map.class);
            final SLQueryApi query = session.createQueryApi();

            query.select().type(JavaTypeMethod.class.getName()).byLink(TypeContainsMethod.class.getName()).b().selectEnd().select().type(
                    JavaTypeMethod.class.getName()).selectEnd().where().type(
                    JavaTypeMethod.class.getName()).each().link(
                    MethodContainsParam.class.getName()).a().count().equalsTo().value(
                    0).typeEnd().whereEnd();

            final SLQueryResult result = query.execute(new String[]{id});
            final Collection<SLNode> nodes = result.getNodes();
            QueryUtil.printResult(nodes);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
