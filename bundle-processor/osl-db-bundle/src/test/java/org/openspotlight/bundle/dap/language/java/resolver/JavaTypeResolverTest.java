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
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.Constants;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.IncludedResult;
import org.openspotlight.bundle.dap.language.java.resolver.TypeResolver.ResultOrder;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

// TODO: Auto-generated Javadoc
/**
 * Test class for {@link JavaTypeResolver}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "boxing" )
public class JavaTypeResolverTest {

    /** The java type finder. */
    static TypeResolver<JavaType> javaTypeFinder;

    /** The java type finder. */
    static TypeResolver<JavaType> java14TypeFinder;

    /** The graph. */
    static SLGraph                graph;

    /** The session. */
    static SLGraphSession         session;

    static AuthenticatedUser      user;

    // FIXME not retrieving the correct type when using inheritance. Need to retest with the new graph version

    /**
     * Creates the crud nodes.
     * 
     * @param crudFrameworkSupport the crud framework support
     * @throws Exception the exception
     */

    protected static void createCrudNodes( final JavaGraphNodeSupport crudFrameworkSupport ) throws Exception {
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeInterface.class, "com.crud.dao", "Dao", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.dao", "DaoImpl", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeInterface.class, "com.crud.dao", "CustomerDao", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.dao", "CustomerDaoImpl", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.dao", "AbstractCustomerDao",
                                                     Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addImplementsLinks("com.crud.dao", "AbstractCustomerDao", "com.crud.dao", "CustomerDao");
        crudFrameworkSupport.addExtendsLinks("com.crud.dao", "AbstractCustomerDao", "com.crud.dao", "DaoImpl");
        crudFrameworkSupport.addExtendsLinks("com.crud.dao", "CustomerDaoImpl", "com.crud.dao", "AbstractCustomerDao");
        crudFrameworkSupport.addExtendsLinks("com.crud.dao", "CustomerDao", "com.crud.dao", "Dao");
        crudFrameworkSupport.addImplementsLinks("com.crud.dao", "DaoImpl", "com.crud.dao", "Dao");
        crudFrameworkSupport.addImplementsLinks("com.crud.dao", "AbstractCustomerDao", "com.crud.dao", "CustomerDao");
        crudFrameworkSupport.addExtendsLinks("com.crud.dao", "DaoImpl", "java.lang.reflect", "Proxy");

    }

    /**
     * Creates the java nodes.
     * 
     * @param jresupport the jresupport
     * @throws Exception the exception
     */
    protected static void createJavaNodes( final JavaGraphNodeSupport jresupport ) throws Exception {
        jresupport.addTypeOnCurrentContext(JavaType.class, "java.lang", "Object", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang.reflect", "Proxy", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "String", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Number", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.util", "HashMap", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.io", "File", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.util", "AbstractMap", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.util", "AbstractMap$SimpleEntry", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeInterface.class, "java.util", "Map$Entry", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeInterface.class, "java.util", "Map", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeInterface.class, "java.io", "Serializable", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeInterface.class, "java.lang", "Comparable", Opcodes.ACC_PUBLIC);

        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Byte", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Short", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Short", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Integer", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Long", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Float", Opcodes.ACC_PUBLIC);
        jresupport.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Double", Opcodes.ACC_PUBLIC);

        jresupport.addExtendsLinks("java.lang.reflect", "Proxy", "java.lang", "Object");
        jresupport.addExtendsLinks("java.lang", "String", "java.lang", "Object");
        jresupport.addExtendsLinks("java.lang", "Number", "java.lang", "Object");
        jresupport.addExtendsLinks("java.io", "File", "java.lang", "Object");
        jresupport.addExtendsLinks("java.util", "HashMap", "java.util", "AbstractMap");
        jresupport.addExtendsLinks("java.util", "AbstractMap", "java.lang", "Object");
        jresupport.addExtendsLinks("java.lang", "Byte", "java.lang", "Number");
        jresupport.addExtendsLinks("java.lang", "Short", "java.lang", "Number");
        jresupport.addExtendsLinks("java.lang", "Integer", "java.lang", "Number");
        jresupport.addExtendsLinks("java.lang", "Long", "java.lang", "Number");
        jresupport.addExtendsLinks("java.lang", "Float", "java.lang", "Number");
        jresupport.addExtendsLinks("java.lang", "Double", "java.lang", "Number");
        jresupport.addImplementsLinks("java.util", "HashMap", "java.util", "Map");
        jresupport.addImplementsLinks("java.util", "AbstractMap", "java.util", "Map");
        jresupport.addImplementsLinks("java.util", "AbstractMap$SimpleEntry", "java.lang", "Map$Entry");
        jresupport.addImplementsLinks("java.lang", "String", "java.lang", "Comparable");
        jresupport.addImplementsLinks("java.lang", "Number", "java.lang", "Comparable");
        jresupport.addImplementsLinks("java.lang", "String", "java.io", "Serializable");
        jresupport.addImplementsLinks("java.lang", "Number", "java.io", "Serializable");
        jresupport.addTypeOnCurrentContext(JavaTypePrimitive.class, "", "int", Opcodes.ACC_PUBLIC);
        jresupport.setupJavaTypesOnCurrentContext();
    }

    /**
     * Setup java finder.
     * 
     * @throws Exception the exception
     */
    @SuppressWarnings( "deprecation" )
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

        javaTypeFinder = JavaTypeResolver.createNewUncachedAndSlow(abstractContext, orderedActiveContexts, true, session);
        java14TypeFinder = JavaTypeResolver.createNewUncachedAndSlow(abstractContext, orderedActiveContextsFor14, false, session);
    }

    @AfterClass
    public static void shutdown() throws Exception {
        session.close();
        graph.shutdown();
    }

    /**
     * Should count all children including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountAllChildrenIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final int size = javaTypeFinder.countAllChildren(type, IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(5));
    }

    /**
     * Should count all children not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountAllChildrenNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final int size = javaTypeFinder.countAllChildren(type, IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(4));
    }

    /**
     * Should count all parents including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountAllParentsIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final int size = javaTypeFinder.countAllParents(type, IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(7));
    }

    /**
     * Should count all parents not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountAllParentsNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final int size = javaTypeFinder.countAllParents(type, IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(6));
    }

    /**
     * Should count concrete children including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountConcreteChildrenIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final int size = javaTypeFinder.countConcreteChildren(type, IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(4));
    }

    /**
     * Should count concrete children not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountConcreteChildrenNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final int size = javaTypeFinder.countConcreteChildren(type, IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(3));
    }

    /**
     * Should count concrete parents including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountConcreteParentsIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final int size = javaTypeFinder.countConcreteParents(type, IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(5));
    }

    /**
     * Should count concrete parents not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountConcreteParentsNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final int size = javaTypeFinder.countConcreteParents(type, IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(4));
    }

    /**
     * Should count interface children including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountInterfaceChildrenIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final int size = javaTypeFinder.countInterfaceChildren(type, IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(2));
    }

    /**
     * Should count interface children not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountInterfaceChildrenNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final int size = javaTypeFinder.countInterfaceChildren(type, IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(1));
    }

    /**
     * Should count interface parents including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountInterfaceParentsIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final int size = javaTypeFinder.countInterfaceParents(type, IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(3));
    }

    /**
     * Should count interface parents not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldCountInterfaceParentsNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final int size = javaTypeFinder.countInterfaceParents(type, IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(size, is(2));
    }

    /**
     * Should do not find autoboxed type when its turned off.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldDoNotFindAutoboxedTypeWhenItsTurnedOff() throws Exception {
        final JavaType primitiveType = java14TypeFinder.getType("int");
        final JavaType wrapperType = java14TypeFinder.getType("java.lang.Integer");

        assertThat(java14TypeFinder.isTypeOf(primitiveType, wrapperType), is(false));
        assertThat(java14TypeFinder.isTypeOf(wrapperType, primitiveType), is(false));
    }

    /**
     * Should do not find primitive sub type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldDoNotFindPrimitiveSubType() throws Exception {
        final JavaType primitiveType = javaTypeFinder.getType("long");
        final JavaType primitiveSubType = javaTypeFinder.getType("int");

        assertThat(javaTypeFinder.isTypeOf(primitiveType, primitiveSubType), is(false));
    }

    /**
     * Should do not find type of something in wrong order.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldDoNotFindTypeOfSomethingInWrongOrder() throws Exception {
        final JavaType mapClass = javaTypeFinder.getType("java.util.Map");
        final JavaType hashMapClass = javaTypeFinder.getType("java.util.HashMap");
        assertThat(javaTypeFinder.isTypeOf(mapClass, hashMapClass), is(false));
    }

    /**
     * Should do not find type of something wrong.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldDoNotFindTypeOfSomethingWrong() throws Exception {
        final JavaType mapClass = javaTypeFinder.getType("java.util.Map");
        final JavaType daoClass = javaTypeFinder.getType("com.crud.dao.Dao");
        assertThat(javaTypeFinder.isTypeOf(mapClass, daoClass), is(false));
    }

    /**
     * Should find all children types on correct order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAllChildrenTypesOnCorrectOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getAllChildren(type, ResultOrder.ASC,
                                                                    IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(5));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
    }

    /**
     * Should find all children types on correct order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAllChildrenTypesOnCorrectOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getAllChildren(type, ResultOrder.ASC,
                                                                    IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(4));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
    }

    /**
     * Should find all children types on reverse order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAllChildrenTypesOnReverseOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getAllChildren(type, ResultOrder.DESC,
                                                                    IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(5));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
    }

    /**
     * Should find all children types on reverse order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAllChildrenTypesOnReverseOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getAllChildren(type, ResultOrder.DESC,
                                                                    IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(4));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
    }

    /**
     * Should find all parent types on correct order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAllParentTypesOnCorrectOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getAllParents(type, ResultOrder.ASC,
                                                                   IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(7));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Object"));
        assertThat(result.get(i++).getSimpleName(), is("Proxy"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
    }

    /**
     * Should find all parent types on correct order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAllParentTypesOnCorrectOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getAllParents(type, ResultOrder.ASC,
                                                                   IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(6));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Object"));
        assertThat(result.get(i++).getSimpleName(), is("Proxy"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
    }

    /**
     * Should find all parent types on reverse order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAllParentTypesOnReverseOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<? extends JavaType> result = javaTypeFinder.getAllParents(type, ResultOrder.DESC,
                                                                             IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(7));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("Proxy"));
        assertThat(result.get(i++).getSimpleName(), is("Object"));
    }

    /**
     * Should find all parent types on reverse order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAllParentTypesOnReverseOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<? extends JavaType> result = javaTypeFinder.getAllParents(type, ResultOrder.DESC,
                                                                             IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(6));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("Proxy"));
        assertThat(result.get(i++).getSimpleName(), is("Object"));
    }

    /**
     * Should find another type on the same package from implemented type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAnotherTypeOnTheSamePackageFromImplementedType() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.lang.String");
        final JavaType fileClass = javaTypeFinder.getType("File", stringClass, null);
        assertThat(fileClass, is(notNullValue()));
        assertThat(fileClass.getName(), is("File"));
        assertThat(fileClass.getPropertyValueAsString("completeName"), is("java.io.File"));

    }

    /**
     * Should find another type on the same package from super type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAnotherTypeOnTheSamePackageFromSuperType() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.util.HashMap");
        final JavaType numberClass = javaTypeFinder.getType("Number", stringClass, null);
        assertThat(numberClass, is(notNullValue()));
        assertThat(numberClass.getName(), is("Number"));
        assertThat(numberClass.getPropertyValueAsString("completeName"), is("java.lang.Number"));

    }

    /**
     * Should find another type with dolar on the same package from super type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAnotherTypeWithDolarOnTheSamePackageFromSuperType() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.util.HashMap");
        final JavaType numberClass = javaTypeFinder.getType("Map.Entry", stringClass, null);
        assertThat(numberClass, is(notNullValue()));
        assertThat(numberClass.getName(), is("Map$Entry"));
        assertThat(numberClass.getPropertyValueAsString("completeName"), is("java.util.Map$Entry"));

    }

    /**
     * Should find another type with dolar on the same package from super type with dolar.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAnotherTypeWithDolarOnTheSamePackageFromSuperTypeWithDolar() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.util.AbstractMap$SimpleEntry");
        final JavaType numberClass = javaTypeFinder.getType("Map.Entry", stringClass, null);
        assertThat(numberClass, is(notNullValue()));
        assertThat(numberClass.getName(), is("Map$Entry"));
        assertThat(numberClass.getPropertyValueAsString("completeName"), is("java.util.Map$Entry"));

    }

    /**
     * Should find autoboxed type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindAutoboxedType() throws Exception {
        final JavaType primitiveType = javaTypeFinder.getType("int");
        final JavaType wrapperType = javaTypeFinder.getType("java.lang.Integer");

        // both ways are allowed    
        assertThat(javaTypeFinder.isTypeOf(primitiveType, wrapperType), is(true));
        assertThat(javaTypeFinder.isTypeOf(wrapperType, primitiveType), is(true));
    }

    /**
     * Should find best matche for autoboxed.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindBestMatcheForAutoboxed() throws Exception {
        final JavaType t1 = javaTypeFinder.getType("short");
        final JavaType t2 = javaTypeFinder.getType("int");
        final JavaType reference = javaTypeFinder.getType("java.lang.Integer");
        final BestTypeMatch match = javaTypeFinder.bestMatch(reference, t1, t2);
        assertThat(match, is(BestTypeMatch.T2));
    }

    /**
     * Should find best matche for primitive.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindBestMatcheForPrimitive() throws Exception {
        final JavaType t1 = javaTypeFinder.getType("int");
        final JavaType t2 = javaTypeFinder.getType("java.lang.Integer");
        final JavaType reference = javaTypeFinder.getType("int");
        final BestTypeMatch match = javaTypeFinder.bestMatch(reference, t1, t2);
        assertThat(match, is(BestTypeMatch.T1));
    }

    /**
     * Should find best matche for primitives.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindBestMatcheForPrimitives() throws Exception {
        final JavaType t1 = javaTypeFinder.getType("short");
        final JavaType t2 = javaTypeFinder.getType("int");
        final JavaType reference = javaTypeFinder.getType("long");
        final BestTypeMatch match = javaTypeFinder.bestMatch(reference, t1, t2);
        assertThat(match, is(BestTypeMatch.T2));
    }

    /**
     * Should find best matche for wrapper.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindBestMatcheForWrapper() throws Exception {
        final JavaType t1 = javaTypeFinder.getType("int");
        final JavaType t2 = javaTypeFinder.getType("java.lang.Integer");
        final JavaType reference = javaTypeFinder.getType("java.lang.Integer");
        final BestTypeMatch match = javaTypeFinder.bestMatch(reference, t1, t2);
        assertThat(match, is(BestTypeMatch.T2));
    }

    /**
     * Should find best matche with one uncorrect type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindBestMatcheWithOneUncorrectType() throws Exception {
        final JavaType t1 = javaTypeFinder.getType("java.lang.String");
        final JavaType t2 = javaTypeFinder.getType("java.lang.Number");
        final JavaType reference = javaTypeFinder.getType("java.lang.Integer");
        final BestTypeMatch match = javaTypeFinder.bestMatch(reference, t1, t2);
        assertThat(match, is(BestTypeMatch.T2));
    }

    /**
     * Should find concrete children types on correct order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteChildrenTypesOnCorrectOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getConcreteChildren(type, ResultOrder.ASC,
                                                                         IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);

        assertThat(result.size(), is(4));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
    }

    /**
     * Should find concrete children types on correct order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteChildrenTypesOnCorrectOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getConcreteChildren(type, ResultOrder.ASC,
                                                                         IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);

        assertThat(result.size(), is(3));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
    }

    /**
     * Should find concrete children types on reverse order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteChildrenTypesOnReverseOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getConcreteChildren(type, ResultOrder.DESC,
                                                                         IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(4));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
    }

    /**
     * Should find concrete children types on reverse order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteChildrenTypesOnReverseOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getConcreteChildren(type, ResultOrder.DESC,
                                                                         IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(3));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
    }

    /**
     * Should find concrete class.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteClass() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.lang.String");
        assertThat(stringClass, is(notNullValue()));
        assertThat(stringClass.getName(), is("String"));
        assertThat(stringClass.getPropertyValueAsString("completeName"), is("java.lang.String"));
    }

    /**
     * Should find concrete inner class.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteInnerClass() throws Exception {
        final SLNode entryClass = javaTypeFinder.getType("java.util.Map$Entry");
        assertThat(entryClass, is(notNullValue()));
        final SLNode newEntryClass = javaTypeFinder.getType("java.util.Map.Entry");
        assertThat(newEntryClass, is(notNullValue()));
        assertThat(entryClass.getName(), is("Map$Entry"));
        assertThat(entryClass.getPropertyValueAsString("completeName"), is("java.util.Map$Entry"));

    }

    /**
     * Should find concrete parent types on correct order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteParentTypesOnCorrectOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getConcreteParents(type, ResultOrder.ASC,
                                                                        IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(5));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Object"));
        assertThat(result.get(i++).getSimpleName(), is("Proxy"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
    }

    /**
     * Should find concrete parent types on correct order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteParentTypesOnCorrectOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getConcreteParents(type, ResultOrder.ASC,
                                                                        IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(4));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Object"));
        assertThat(result.get(i++).getSimpleName(), is("Proxy"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
    }

    /**
     * Should find concrete parent types on reverse order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteParentTypesOnReverseOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getConcreteParents(type, ResultOrder.DESC,
                                                                        IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(5));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("Proxy"));
        assertThat(result.get(i++).getSimpleName(), is("Object"));
    }

    /**
     * Should find concrete parent types on reverse order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteParentTypesOnReverseOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getConcreteParents(type, ResultOrder.DESC,
                                                                        IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(4));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("Proxy"));
        assertThat(result.get(i++).getSimpleName(), is("Object"));
    }

    /**
     * Should find concrete type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindConcreteType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        assertThat(javaTypeFinder.isConcreteType(type), is(true));
    }

    /**
     * Should find direct concrete children types.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindDirectConcreteChildrenTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getDirectConcreteChildren(type);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("DaoImpl"));
    }

    /**
     * Should find direct concrete parent types.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindDirectConcreteParentTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getDirectConcreteParents(type);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("AbstractCustomerDao"));
    }

    /**
     * Should find direct interface children types.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindDirectInterfaceChildrenTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getDirectInterfaceChildren(type);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
    }

    /**
     * Should find direct interface parent types.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindDirectInterfaceParentTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getDirectInterfaceParents(type);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
    }

    /**
     * Should find interface children types on correct order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceChildrenTypesOnCorrectOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getInterfaceChildren(type, ResultOrder.ASC,
                                                                          IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);

        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));

    }

    /**
     * Should find interface children types on correct order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceChildrenTypesOnCorrectOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getInterfaceChildren(type, ResultOrder.ASC,
                                                                          IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);

        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));

    }

    /**
     * Should find interface children types on reverse order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceChildrenTypesOnReverseOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getInterfaceChildren(type, ResultOrder.DESC,
                                                                          IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
    }

    /**
     * Should find interface children types on reverse order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceChildrenTypesOnReverseOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.Dao");
        final List<JavaType> result = javaTypeFinder.getInterfaceChildren(type, ResultOrder.DESC,
                                                                          IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
    }

    /**
     * Should find interface parent types on correct order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceParentTypesOnCorrectOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getInterfaceParents(type, ResultOrder.ASC,
                                                                         IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(3));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
    }

    /**
     * Should find interface parent types on correct order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceParentTypesOnCorrectOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getInterfaceParents(type, ResultOrder.ASC,
                                                                         IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
    }

    /**
     * Should find interface parent types on reverse order including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceParentTypesOnReverseOrderIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getInterfaceParents(type, ResultOrder.DESC,
                                                                         IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(3));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDaoImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
    }

    /**
     * Should find interface parent types on reverse order not including actual type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceParentTypesOnReverseOrderNotIncludingActualType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDaoImpl");
        final List<JavaType> result = javaTypeFinder.getInterfaceParents(type, ResultOrder.DESC,
                                                                         IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerDao"));
        assertThat(result.get(i++).getSimpleName(), is("Dao"));
    }

    /**
     * Should find interface type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindInterfaceType() throws Exception {
        final SLNode mapClass = javaTypeFinder.getType("java.util.Map");
        assertThat(mapClass, is(notNullValue()));
        assertThat(mapClass.getName(), is("Map"));
        assertThat(mapClass.getPropertyValueAsString("completeName"), is("java.util.Map"));
    }

    /**
     * Should find primitive.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindPrimitive() throws Exception {
        final JavaType wrapperType = javaTypeFinder.getType("java.lang.Integer");
        final JavaType wrappedType = javaTypeFinder.getPrimitiveFor(wrapperType);
        assertThat(wrappedType.getName(), is("int"));
    }

    /**
     * Should find primitive sub type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindPrimitiveSubType() throws Exception {
        final JavaType primitiveType = javaTypeFinder.getType("long");
        final JavaType primitiveSubType = javaTypeFinder.getType("int");

        assertThat(javaTypeFinder.isTypeOf(primitiveSubType, primitiveType), is(true));
    }

    /**
     * Should find primitive type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindPrimitiveType() throws Exception {
        final SLNode intClass = javaTypeFinder.getType("int");
        assertThat(intClass, is(notNullValue()));
        assertThat(intClass.getName(), is("int"));
        assertThat(intClass.getPropertyValueAsString("completeName"), is("int"));
    }

    /**
     * Should find primitive types.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindPrimitiveTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("int");
        assertThat(javaTypeFinder.isPrimitiveType(type), is(true));
    }

    /**
     * Should find same matche for autoboxed.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindSameMatcheForAutoboxed() throws Exception {
        final JavaType t1 = javaTypeFinder.getType("java.lang.Long");
        final JavaType t2 = javaTypeFinder.getType("java.lang.Integer");
        final JavaType reference = javaTypeFinder.getType("java.lang.Number");
        final BestTypeMatch match = javaTypeFinder.bestMatch(reference, t1, t2);
        assertThat(match, is(BestTypeMatch.SAME));
    }

    /**
     * Should find same matche for uncorrect types.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindSameMatcheForUncorrectTypes() throws Exception {
        final JavaType t1 = javaTypeFinder.getType("java.lang.String");
        final JavaType t2 = javaTypeFinder.getType("java.util.Map");
        final JavaType reference = javaTypeFinder.getType("java.lang.Number");
        final BestTypeMatch match = javaTypeFinder.bestMatch(reference, t1, t2);
        assertThat(match, is(BestTypeMatch.SAME));
    }

    /**
     * Should find type of something when using concrete type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindTypeOfSomethingWhenUsingConcreteType() throws Exception {
        final JavaType superClass = javaTypeFinder.getType("java.lang.Object");
        final JavaType hashMapClass = javaTypeFinder.getType("java.util.HashMap");
        assertThat(javaTypeFinder.isTypeOf(hashMapClass, superClass), is(true));
    }

    /**
     * Should find type of something when using interface type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindTypeOfSomethingWhenUsingInterfaceType() throws Exception {
        final JavaType superClass = javaTypeFinder.getType("java.util.Map");
        final JavaType hashMapClass = javaTypeFinder.getType("java.util.HashMap");
        assertThat(javaTypeFinder.isTypeOf(hashMapClass, superClass), is(true));
    }

    /**
     * Should find wrapper.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldFindWrapper() throws Exception {
        final JavaType wrappedType = javaTypeFinder.getType("int");
        final JavaType wrapperType = javaTypeFinder.getWrapperFor(wrappedType);
        assertThat(wrapperType.getName(), is("Integer"));
    }

    /**
     * Should not find concrete type.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldNotFindConcreteType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.dao.CustomerDao");
        assertThat(javaTypeFinder.isConcreteType(type), is(false));
    }

    /**
     * Should not find concrete type when trying primitive.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldNotFindConcreteTypeWhenTryingPrimitive() throws Exception {
        final JavaType type = javaTypeFinder.getType("int");
        assertThat(javaTypeFinder.isConcreteType(type), is(false));
    }

    /**
     * Should not find primitive.
     * 
     * @throws Exception the exception
     */
    @Test( expected = InternalJavaFinderError.class )
    public void shouldNotFindPrimitive() throws Exception {
        final JavaType wrappedType = javaTypeFinder.getType("java.lang.Object");
        javaTypeFinder.getPrimitiveFor(wrappedType);
    }

    /**
     * Should not find primitive types.
     * 
     * @throws Exception the exception
     */
    @Test
    public void shouldNotFindPrimitiveTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("java.lang.Integer");
        assertThat(javaTypeFinder.isPrimitiveType(type), is(false));
    }

    /**
     * Should not find wrapper.
     * 
     * @throws Exception the exception
     */
    @Test( expected = InternalJavaFinderError.class )
    public void shouldNotFindWrapper() throws Exception {
        final JavaType wrappedType = javaTypeFinder.getType("java.lang.Object");
        javaTypeFinder.getWrapperFor(wrappedType);
    }

    @Test
    public void shouldNotFindWrongAutoboxedType() throws Exception {
        final JavaType primitiveType = javaTypeFinder.getType("int");
        final JavaType wrapperType = javaTypeFinder.getType("java.lang.Long");

        // not allowed because Integer isn't a Long
        assertThat(javaTypeFinder.isTypeOf(primitiveType, wrapperType), is(false)); //FIXME here there's a problem
    }

    @Test
    public void shouldNotFindWrongWrapperAutoboxType() throws Exception {
        final JavaType primitiveType = javaTypeFinder.getType("int");
        final JavaType wrapperType = javaTypeFinder.getType("java.lang.Long");

        // not allowed because Integer isn't a Long
        assertThat(javaTypeFinder.isTypeOf(wrapperType, primitiveType), is(false));
    }

}
