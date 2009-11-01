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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLCommonSupport;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialsException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.persistence.SLPersistentTree;
import org.openspotlight.graph.persistence.SLPersistentTreeException;
import org.openspotlight.graph.persistence.SLPersistentTreeFactory;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.query.SLQuery.SortMode;
import org.openspotlight.graph.test.domain.JavaInterface;
import org.openspotlight.graph.test.domain.JavaType;
import org.openspotlight.graph.test.domain.JavaTypeMethod;
import org.openspotlight.graph.test.domain.MethodContainsParam;
import org.openspotlight.graph.test.domain.MethodParam;
import org.openspotlight.graph.test.domain.TypeContainsMethod;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class SLGraphQueryCacheTest {

    /** The Constant LOGGER. */
    static final Logger                    LOGGER = Logger.getLogger(SLGraphQueryCacheTest.class);

    /** The graph. */
    private static SLGraph                 graph;

    /** The session. */
    private static SLGraphSession          session;

    private static SLPersistentTreeSession treeSession;

    private static SLQueryCacheImpl        queryCache;

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
            final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser("testUser");
            user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");

            final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            graph = factory.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
            session = graph.openSession(user);
            final SLContext context = session.createContext("cacheTest");
            final SLNode root = context.getRootNode();
            final Set<Class<?>> types = getIFaceTypeSet();
            for (final Class<?> type : types) {
                final Method[] methods = type.getDeclaredMethods();
                LOGGER.info(type.getName() + ": " + methods.length + " methods");
                final JavaInterface javaInteface = root.addNode(JavaInterface.class, type.getName());
                javaInteface.setProperty(String.class, "caption", type.getName());
                for (final Method method : methods) {
                    final JavaTypeMethod javaMethod = javaInteface.addNode(JavaTypeMethod.class, method.getName());
                    javaMethod.setProperty(String.class, "caption", method.getName());
                    session.addLink(TypeContainsMethod.class, javaInteface, javaMethod, false);
                    final Class<?>[] paramTypes = method.getParameterTypes();
                    LOGGER.info("\t\t" + method.getName() + ": " + paramTypes.length + " params");
                    for (final Class<?> paramType : paramTypes) {
                        final MethodParam methodParam = javaMethod.addNode(MethodParam.class, paramType.getName());
                        methodParam.setProperty(String.class, "caption", paramType.getName());
                        session.addLink(MethodContainsParam.class, javaMethod, methodParam, false);
                    }
                }
            }
            session.save();
            session.close();
            session = graph.openSession(user);

            final SLPersistentTreeFactory pFactory = AbstractFactory.getDefaultInstance(SLPersistentTreeFactory.class);
            final SLPersistentTree tree = pFactory.createPersistentTree(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
            treeSession = tree.openSession(SLConsts.DEFAULT_REPOSITORY_NAME);

            queryCache = new SLQueryCacheImpl(treeSession, session);

        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    //( dependsOnMethods = "selectTypes" )
    public void selectMethods() throws SLGraphSessionException, SLInvalidQuerySyntaxException, SLPersistentTreeException, SLInvalidCredentialsException {
        String queryId = null;
        assertThat(SLCommonSupport.containsQueryCache(this.treeSession), is(false));

        final SLQueryApi query = this.session.createQueryApi();

        query.select().type(JavaTypeMethod.class.getName()).selectEnd();

        final SLQueryResult result = query.execute();
        queryId = result.getQueryId();

        assertThat(SLCommonSupport.containsQueryCache(this.treeSession), is(true));
        assertThat(this.queryCache.getCache(result.getQueryId()), is(notNullValue()));

        final SLQueryApi query2 = this.session.createQueryApi();

        query2.select().type(JavaTypeMethod.class.getName()).selectEnd();

        final SLQueryResult result2 = query2.execute();

        assertThat(result2.getQueryId(), is(queryId));

        this.graph.gc(user);
        assertThat(SLCommonSupport.containsQueryCache(this.treeSession), is(false));
    }

    @Test
    public void selectTypes()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException, SLPersistentTreeException, SLInvalidCredentialsException {
        String queryId = null;
        assertThat(SLCommonSupport.containsQueryCache(this.treeSession), is(false));

        final SLQueryApi query = this.session.createQueryApi();

        query.select().type(JavaType.class.getName()).subTypes().selectEnd();

        final SLQueryResult result = query.execute(SortMode.SORTED, false);
        queryId = result.getQueryId();

        assertThat(SLCommonSupport.containsQueryCache(this.treeSession), is(true));
        assertThat(this.queryCache.getCache(result.getQueryId()), is(notNullValue()));

        QueryUtil.printResult(result.getNodes());

        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());
        assertThat(wrappers.length, is(5));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.Collection"), is(wrappers[0]));
        assertThat(new NodeWrapper(JavaInterface.class.getName(), "cacheTest",
                                   "java.util.List"), is(wrappers[1]));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.Map"), is(wrappers[2]));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.Set"), is(wrappers[3]));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.SortedSet"), is(wrappers[4]));

        final SLQueryApi query2 = this.session.createQueryApi();

        query2.select().type(JavaType.class.getName()).subTypes().selectEnd();

        final SLQueryResult result2 = query2.execute(SortMode.SORTED, false);

        assertThat(result2.getQueryId(), is(queryId));

        final NodeWrapper[] wrappers2 = this.wrapNodes(result2.getNodes());
        assertThat(wrappers2.length, is(5));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.Collection"), is(wrappers2[0]));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.List"), is(wrappers2[1]));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.Map"), is(wrappers2[2]));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.Set"), is(wrappers2[3]));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "cacheTest",
                                   "java.util.SortedSet"), is(wrappers2[4]));

        this.graph.gc(user);
        assertThat(SLCommonSupport.containsQueryCache(this.treeSession), is(false));
    }

    /**
     * Wrap nodes.
     * 
     * @param nodes the nodes
     * @return the node wrapper[]
     */
    protected NodeWrapper[] wrapNodes( final List<SLNode> nodes ) {
        final NodeWrapper[] wrappers = new NodeWrapper[nodes.size()];

        for (int i = 0; i < wrappers.length; i++) {
            wrappers[i] = new NodeWrapper(nodes.get(i));
        }
        return wrappers;
    }

}
