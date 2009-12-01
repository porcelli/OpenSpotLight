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
/**
 * 
 */
package org.openspotlight.bundle.dap.language.java.resolver;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.bundle.dap.language.java.Constants;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult;
import org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.ResultOrder;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.User;

// TODO: Auto-generated Javadoc
/**
 * Test class for {@link JavaTypeResolver}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */

public class CachedJavaTypeResolverTest extends JavaTypeResolverTest {
    private static TypeResolver<JavaType> anotherCachedJavaTypeFinder;

    /**
     * Setup java finder.
     * 
     * @throws Exception the exception
     */
    @BeforeClass
    public static void setupJavaFinder() throws Exception {
        final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
        final User simpleUser = securityFactory.createUser("testUser");
        user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");

        final SLGraphFactory factory = new SLGraphFactoryImpl();

        graph = factory.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        session = graph.openSession(user);
        SLContext abstractContext = session.createContext(Constants.ABSTRACT_CONTEXT);
        SLContext jre14ctx = session.createContext("JRE-util-1.4");
        SLContext jre15ctx = session.createContext("JRE-util-1.5");
        SLContext crudFrameworkCtx = session.createContext("Crud-1.2");
        final SLContext crudFrameworkLegacyCtx = session.createContext("Crud-0.5-legacy");
        final JavaGraphNodeSupport jre5support = new JavaGraphNodeSupport(session, jre15ctx.getRootNode(),
                                                                          abstractContext.getRootNode());
        final JavaGraphNodeSupport jre4support = new JavaGraphNodeSupport(session, jre14ctx.getRootNode(),
                                                                          abstractContext.getRootNode());

        createJavaNodes(jre4support);
        createJavaNodes(jre5support);

        final JavaGraphNodeSupport crudFrameworkSupport = new JavaGraphNodeSupport(session, crudFrameworkCtx.getRootNode(),
                                                                                   abstractContext.getRootNode());
        final JavaGraphNodeSupport crudFrameworkLegacySupport = new JavaGraphNodeSupport(session,
                                                                                         crudFrameworkLegacyCtx.getRootNode(),
                                                                                         abstractContext.getRootNode());
        createCrudNodes(crudFrameworkSupport);
        createCrudNodes(crudFrameworkLegacySupport);
        session.save();
        session.close();
        session = graph.openSession(user);
        abstractContext = session.getContext(Constants.ABSTRACT_CONTEXT);
        jre15ctx = session.getContext("JRE-util-1.5");
        jre14ctx = session.getContext("JRE-util-1.4");
        crudFrameworkCtx = session.getContext("Crud-1.2");
        final List<SLContext> orderedActiveContexts = Arrays.asList(crudFrameworkCtx, jre15ctx);
        final List<SLContext> orderedActiveContextsFor14 = Arrays.asList(crudFrameworkCtx, jre14ctx);

        javaTypeFinder = JavaTypeResolver.createNewCached(abstractContext, orderedActiveContexts, true, session);
        java14TypeFinder = JavaTypeResolver.createNewCached(abstractContext, orderedActiveContextsFor14, false, session);
        anotherCachedJavaTypeFinder = JavaTypeResolver.createNewCached(abstractContext, orderedActiveContexts, true, session);

    }

    @Test
    public void shouldWorkFasterOnSecondInvocation() {
        final long start1 = System.currentTimeMillis();
        final JavaType type1 = anotherCachedJavaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<? extends JavaType> result1 = javaTypeFinder.getAllParents(type1, ResultOrder.DESC,
                                                                              IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        final long end1 = System.currentTimeMillis();
        final long diff1 = end1 - start1;
        final long start2 = System.currentTimeMillis();
        final JavaType type2 = anotherCachedJavaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<? extends JavaType> result2 = javaTypeFinder.getAllParents(type2, ResultOrder.DESC,
                                                                              IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        final long end2 = System.currentTimeMillis();
        final long diff2 = end2 - start2;
        //faster
        assertThat(diff2 < diff1, is(true));
        //same instance
        assertThat(type1 == type2, is(true));
        //same instance
        assertThat(result1 == result2, is(true));
    }

}
