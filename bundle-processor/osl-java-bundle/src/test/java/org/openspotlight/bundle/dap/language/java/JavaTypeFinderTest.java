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
package org.openspotlight.bundle.dap.language.java;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.bundle.dap.language.java.support.JavaGraphNodeSupport;
import org.openspotlight.bundle.dap.language.java.support.JavaTypeFinder;
import org.openspotlight.bundle.dap.language.java.support.TypeFinder.ResultOrder;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

/**
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "boxing" )
public class JavaTypeFinderTest {

    static JavaTypeFinder javaTypeFinder;

    static SLGraph        graph;
    static SLGraphSession session;

    // FIXME not retrieving the correct type

    @BeforeClass
    public static void setupJavaFinder() throws Exception {
        final SLGraphFactory factory = new SLGraphFactoryImpl();

        graph = factory.createTempGraph(true);
        session = graph.openSession();
        SLContext abstractContext = session.createContext(Constants.ABSTRACT_CONTEXT);
        SLContext jre15ctx = session.createContext("JRE-util-1.5");
        SLContext crudFrameworkCtx = session.createContext("Crud-1.2");
        final JavaGraphNodeSupport jre5support = new JavaGraphNodeSupport(session, jre15ctx.getRootNode(),
                                                                          abstractContext.getRootNode());
        final JavaGraphNodeSupport crudFrameworkSupport = new JavaGraphNodeSupport(session, crudFrameworkCtx.getRootNode(),
                                                                                   abstractContext.getRootNode());
        jre5support.addTypeOnCurrentContext(JavaType.class, "java.lang", "Object", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "String", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeClass.class, "java.lang", "Number", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeClass.class, "java.util", "HashMap", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeClass.class, "java.io", "File", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeClass.class, "java.util", "AbstractMap", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeClass.class, "java.util", "AbstractMap$SimpleEntry", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeInterface.class, "java.util", "Map$Entry", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeInterface.class, "java.util", "Map", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeInterface.class, "java.io", "Serializable", Opcodes.ACC_PUBLIC);
        jre5support.addTypeOnCurrentContext(JavaTypeInterface.class, "java.lang", "Comparable", Opcodes.ACC_PUBLIC);
        jre5support.addExtendsLinks("java.lang", "String", "java.lang", "Object");
        jre5support.addExtendsLinks("java.lang", "Number", "java.lang", "Object");
        jre5support.addExtendsLinks("java.io", "File", "java.lang", "Object");
        jre5support.addExtendsLinks("java.util", "HashMap", "java.util", "AbstractMap");
        jre5support.addExtendsLinks("java.util", "AbstractMap", "java.lang", "Object");
        jre5support.addImplementsLinks("java.util", "HashMap", "java.lang", "Map");
        jre5support.addImplementsLinks("java.util", "AbstractMap", "java.lang", "Map");
        jre5support.addImplementsLinks("java.util", "AbstractMap$SimpleEntry", "java.lang", "Map$Entry");
        jre5support.addImplementsLinks("java.lang", "String", "java.lang", "Comparable");
        jre5support.addImplementsLinks("java.lang", "Number", "java.lang", "Comparable");
        jre5support.addImplementsLinks("java.lang", "String", "java.io", "Serializable");
        jre5support.addImplementsLinks("java.lang", "Number", "java.io", "Serializable");
        jre5support.addTypeOnCurrentContext(JavaTypePrimitive.class, "", "int", Opcodes.ACC_PUBLIC);

        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeInterface.class, "com.crud.dao", "Dao", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeInterface.class, "com.crud.controller", "Controller",
                                                     Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeInterface.class, "com.crud.view", "View", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.dao.impl", "DaoImpl", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.controller.impl", "ControllerImpl",
                                                     Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.view.impl", "ViewImpl", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addExtendsLinks("com.crud.dao.impl", "DaoImpl", "java.lang", "Object");
        crudFrameworkSupport.addExtendsLinks("com.crud.controller.impl", "ControllerImpl", "java.lang", "Object");
        crudFrameworkSupport.addExtendsLinks("com.crud.view.impl", "View", "java.lang", "Object");
        crudFrameworkSupport.addImplementsLinks("com.crud.dao.impl", "DaoImpl", "com.crud.dao", "Dao");
        crudFrameworkSupport.addImplementsLinks("com.crud.controller.impl", "ControllerImpl", "com.crud.controller", "Controller");
        crudFrameworkSupport.addImplementsLinks("com.crud.view.impl", "ViewImpl", "com.crud.view", "View");

        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeInterface.class, "com.crud.dao", "CustomerDao", Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeInterface.class, "com.crud.controller", "CustomerController",
                                                     Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeInterface.class, "com.crud.view", "CustomerView", Opcodes.ACC_PUBLIC);

        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.dao.impl", "CustomerDaoImpl",
                                                     Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.controller.impl", "CustomerControllerImpl",
                                                     Opcodes.ACC_PUBLIC);
        crudFrameworkSupport.addTypeOnCurrentContext(JavaTypeClass.class, "com.crud.view.impl", "CustomerViewImpl",
                                                     Opcodes.ACC_PUBLIC);

        crudFrameworkSupport.addImplementsLinks("com.crud.dao.impl", "CustomerDaoImpl", "com.crud.dao", "CustomerDao");
        crudFrameworkSupport.addImplementsLinks("com.crud.controller.impl", "CustomerControllerImpl", "com.crud.controller",
                                                "CustomerController");
        crudFrameworkSupport.addImplementsLinks("com.crud.view.impl", "CustomerViewImpl", "com.crud.view", "CustomerView");

        crudFrameworkSupport.addImplementsLinks("com.crud.dao.impl", "CustomerDaoImpl", "com.crud.dao", "Dao");
        crudFrameworkSupport.addImplementsLinks("com.crud.controller.impl", "CustomerControllerImpl", "com.crud.controller",
                                                "Controller");
        crudFrameworkSupport.addImplementsLinks("com.crud.view.impl", "CustomerViewImpl", "com.crud.view", "View");

        crudFrameworkSupport.addExtendsLinks("com.crud.dao.impl", "CustomerDaoImpl", "com.crud.dao.impl", "DaoImpl");
        crudFrameworkSupport.addExtendsLinks("com.crud.controller.impl", "CustomerControllerImpl", "com.crud.controller.impl",
                                             "ControllerImpl");
        crudFrameworkSupport.addExtendsLinks("com.crud.view.impl", "CustomerViewImpl", "com.crud.view.impl", "ViewImpl");

        session.save();
        session.close();
        session = graph.openSession();
        abstractContext = session.getContext(Constants.ABSTRACT_CONTEXT);
        jre15ctx = session.getContext("JRE-util-1.5");
        crudFrameworkCtx = session.getContext("Crud-1.2");
        final List<SLContext> orderedActiveContexts = Arrays.asList(crudFrameworkCtx, jre15ctx);

        javaTypeFinder = new JavaTypeFinder(abstractContext, orderedActiveContexts, true, session);

    }

    @Test
    @Ignore
    public void shouldDoNotFindTypeOfSomethingInWrongOrder() throws Exception {
        final JavaType mapClass = javaTypeFinder.getType("java.util.Map");
        final JavaType hashMapClass = javaTypeFinder.getType("java.util.HashMap");
        assertThat(javaTypeFinder.isTypeOf(mapClass, hashMapClass), is(true));
    }

    @Test
    @Ignore
    public void shouldDoNotFindTypeOfSomethingWrong() throws Exception {
        final JavaType mapClass = javaTypeFinder.getType("java.util.Map");
        final JavaType daoClass = javaTypeFinder.getType("com.crud.dao.Dao");
        assertThat(javaTypeFinder.isTypeOf(mapClass, daoClass), is(false));
    }

    @Test
    @Ignore
    public void shouldFindAllChildrenTypesOnCorrectOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.View");
        final List<JavaType> result = javaTypeFinder.getAllChildren(type, ResultOrder.ASC);
        assertThat(result.size(), is(3));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerView"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerViewImpl"));
    }

    @Test
    @Ignore
    public void shouldFindAllChildrenTypesOnReverseOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.View");
        final List<JavaType> result = javaTypeFinder.getAllChildren(type, ResultOrder.DESC);
        assertThat(result.size(), is(3));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerView"));
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
    }

    @Test
    @Ignore
    public void shouldFindAllParentTypesOnCorrectOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        final List<JavaType> result = javaTypeFinder.getAllParents(type, ResultOrder.ASC);
        assertThat(result.size(), is(4));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Object"));
        assertThat(result.get(i++).getSimpleName(), is("View"));
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerView"));
    }

    @Test
    @Ignore
    public void shouldFindAllParentTypesOnReverseOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        final List<JavaType> result = javaTypeFinder.getAllParents(type, ResultOrder.DESC);
        assertThat(result.size(), is(4));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerView"));
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("View"));
        assertThat(result.get(i++).getSimpleName(), is("Object"));
    }

    @Test
    public void shouldFindAnotherTypeOnTheSamePackageFromImplementedType() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.lang.String");
        final JavaType fileClass = javaTypeFinder.getType("File", stringClass, null);
        assertThat(fileClass, is(notNullValue()));
        assertThat(fileClass.getName(), is("File"));
        assertThat(fileClass.getPropertyValueAsString("completeName"), is("java.io.File"));

    }

    @Test
    public void shouldFindAnotherTypeOnTheSamePackageFromSuperType() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.util.HashMap");
        final JavaType numberClass = javaTypeFinder.getType("Number", stringClass, null);
        assertThat(numberClass, is(notNullValue()));
        assertThat(numberClass.getName(), is("Number"));
        assertThat(numberClass.getPropertyValueAsString("completeName"), is("java.lang.Number"));

    }

    @Test
    public void shouldFindAnotherTypeWithDolarOnTheSamePackageFromSuperType() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.util.HashMap");
        final JavaType numberClass = javaTypeFinder.getType("Map.Entry", stringClass, null);
        assertThat(numberClass, is(notNullValue()));
        assertThat(numberClass.getName(), is("Map$Entry"));
        assertThat(numberClass.getPropertyValueAsString("completeName"), is("java.util.Map$Entry"));

    }

    @Test
    public void shouldFindAnotherTypeWithDolarOnTheSamePackageFromSuperTypeWithDolar() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.util.AbstractMap$SimpleEntry");
        final JavaType numberClass = javaTypeFinder.getType("Map.Entry", stringClass, null);
        assertThat(numberClass, is(notNullValue()));
        assertThat(numberClass.getName(), is("Map$Entry"));
        assertThat(numberClass.getPropertyValueAsString("completeName"), is("java.util.Map$Entry"));

    }

    @Test
    @Ignore
    public void shouldFindConcreteChildrenTypesOnCorrectOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.View");
        final List<JavaType> result = javaTypeFinder.getConcreteChildren(type, ResultOrder.ASC);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerViewImpl"));
    }

    @Test
    @Ignore
    public void shouldFindConcreteChildrenTypesOnReverseOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.View");
        final List<JavaType> result = javaTypeFinder.getConcreteChildren(type, ResultOrder.DESC);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
    }

    @Test
    public void shouldFindConcreteClass() throws Exception {
        final JavaType stringClass = javaTypeFinder.getType("java.lang.String");
        assertThat(stringClass, is(notNullValue()));
        assertThat(stringClass.getName(), is("String"));
        assertThat(stringClass.getPropertyValueAsString("completeName"), is("java.lang.String"));
    }

    @Test
    public void shouldFindConcreteInnerClass() throws Exception {
        final SLNode entryClass = javaTypeFinder.getType("java.util.Map$Entry");
        assertThat(entryClass, is(notNullValue()));
        final SLNode newEntryClass = javaTypeFinder.getType("java.util.Map.Entry");
        assertThat(newEntryClass, is(notNullValue()));
        assertThat(entryClass.getName(), is("Map$Entry"));
        assertThat(entryClass.getPropertyValueAsString("completeName"), is("java.util.Map$Entry"));

    }

    @Test
    @Ignore
    public void shouldFindConcreteParentTypesOnCorrectOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        final List<JavaType> result = javaTypeFinder.getConcreteParents(type, ResultOrder.ASC);
        assertThat(result.size(), is(3));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("Object"));
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerViewImpl"));
    }

    @Test
    @Ignore
    public void shouldFindConcreteParentTypesOnReverseOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        final List<JavaType> result = javaTypeFinder.getConcreteParents(type, ResultOrder.DESC);
        assertThat(result.size(), is(3));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("Object"));
    }

    @Test
    @Ignore
    public void shouldFindConcreteType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        assertThat(javaTypeFinder.isConcreteType(type), is(true));
    }

    @Test
    @Ignore
    public void shouldFindDirectConcreteChildrenTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.View");
        final List<JavaType> result = javaTypeFinder.getDirectConcreteChildren(type);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
    }

    @Test
    @Ignore
    public void shouldFindDirectConcreteParentTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        final List<JavaType> result = javaTypeFinder.getDirectConcreteParents(type);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
    }

    @Test
    @Ignore
    public void shouldFindDirectInterfaceChildrenTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.View");
        final List<JavaType> result = javaTypeFinder.getDirectInterfaceChildren(type);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerView"));
    }

    @Test
    @Ignore
    public void shouldFindDirectInterfaceParentTypes() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        final List<JavaType> result = javaTypeFinder.getDirectInterfaceParents(type);
        assertThat(result.size(), is(1));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("View"));
    }

    @Test
    @Ignore
    public void shouldFindInterfaceChildrenTypesOnCorrectOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.View");
        final List<JavaType> result = javaTypeFinder.getInterfaceChildren(type, ResultOrder.ASC);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerViewImpl"));
    }

    @Test
    @Ignore
    public void shouldFindInterfaceChildrenTypesOnReverseOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.View");
        final List<JavaType> result = javaTypeFinder.getInterfaceChildren(type, ResultOrder.DESC);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerViewImpl"));
        assertThat(result.get(i++).getSimpleName(), is("ViewImpl"));
    }

    @Test
    @Ignore
    public void shouldFindInterfaceParentTypesOnCorrectOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        final List<JavaType> result = javaTypeFinder.getInterfaceParents(type, ResultOrder.ASC);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("View"));
        assertThat(result.get(i++).getSimpleName(), is("CustomerView"));
    }

    @Test
    @Ignore
    public void shouldFindInterfaceParentTypesOnReverseOrder() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.impl.CustomerViewImpl");
        final List<JavaType> result = javaTypeFinder.getInterfaceParents(type, ResultOrder.DESC);
        assertThat(result.size(), is(2));
        int i = 0;
        assertThat(result.get(i++).getSimpleName(), is("CustomerView"));
        assertThat(result.get(i++).getSimpleName(), is("View"));
    }

    @Test
    public void shouldFindInterfaceType() throws Exception {
        final SLNode mapClass = javaTypeFinder.getType("java.util.Map");
        assertThat(mapClass, is(notNullValue()));
        assertThat(mapClass.getName(), is("Map"));
        assertThat(mapClass.getPropertyValueAsString("completeName"), is("java.util.Map"));
    }

    @Test
    public void shouldFindPrimitiveType() throws Exception {
        final SLNode intClass = javaTypeFinder.getType("int");
        assertThat(intClass, is(notNullValue()));
        assertThat(intClass.getName(), is("int"));
        assertThat(intClass.getPropertyValueAsString("completeName"), is("int"));
    }

    @Test
    @Ignore
    public void shouldFindTypeOfSomething() throws Exception {
        final JavaType mapClass = javaTypeFinder.getType("java.util.Map");
        final JavaType hashMapClass = javaTypeFinder.getType("java.util.HashMap");
        assertThat(javaTypeFinder.isTypeOf(hashMapClass, mapClass), is(true));
    }

    @Test
    @Ignore
    public void shouldNotFindConcreteType() throws Exception {
        final JavaType type = javaTypeFinder.getType("com.crud.view.CustomerView");
        assertThat(javaTypeFinder.isConcreteType(type), is(false));
    }

    @Test
    @Ignore
    public void shouldNotFindConcreteTypeWhenTryingPrimitive() throws Exception {
        final JavaType type = javaTypeFinder.getType("int");
        assertThat(javaTypeFinder.isConcreteType(type), is(false));
    }

}
