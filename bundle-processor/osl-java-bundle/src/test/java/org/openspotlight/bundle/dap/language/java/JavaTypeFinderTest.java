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

import java.util.Arrays;
import java.util.List;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.bundle.dap.language.java.support.JavaGraphNodeSupport;
import org.openspotlight.bundle.dap.language.java.support.JavaTypeFinder;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

/**
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class JavaTypeFinderTest {

    static JavaTypeFinder javaTypeFinder;

    static SLGraph        graph;
    static SLGraphSession session;

    // FIXME not retrieving the correct type

    @BeforeClass
    public static void setupJavaFinder() throws Exception {
        final SLGraphFactory factory = new SLGraphFactoryImpl();

        JavaTypeFinderTest.graph = factory.createTempGraph(true);
        JavaTypeFinderTest.session = JavaTypeFinderTest.graph.openSession();
        SLContext abstractContext = JavaTypeFinderTest.session.createContext(Constants.ABSTRACT_CONTEXT);
        SLContext jre15ctx = JavaTypeFinderTest.session.createContext("JRE-util-1.5");
        SLContext crudFrameworkCtx = JavaTypeFinderTest.session.createContext("Crud-1.2");
        final JavaGraphNodeSupport jre5support = new JavaGraphNodeSupport(JavaTypeFinderTest.session, jre15ctx.getRootNode(),
                                                                          abstractContext.getRootNode());
        final JavaGraphNodeSupport crudFrameworkSupport = new JavaGraphNodeSupport(JavaTypeFinderTest.session,
                                                                                   crudFrameworkCtx.getRootNode(),
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

        JavaTypeFinderTest.session.save();
        JavaTypeFinderTest.session.close();
        JavaTypeFinderTest.session = JavaTypeFinderTest.graph.openSession();
        abstractContext = JavaTypeFinderTest.session.getContext(Constants.ABSTRACT_CONTEXT);
        jre15ctx = JavaTypeFinderTest.session.getContext("JRE-util-1.5");
        crudFrameworkCtx = JavaTypeFinderTest.session.getContext("Crud-1.2");
        final List<SLContext> orderedActiveContexts = Arrays.asList(crudFrameworkCtx, jre15ctx);

        JavaTypeFinderTest.javaTypeFinder = new JavaTypeFinder(abstractContext, orderedActiveContexts, true,
                                                               JavaTypeFinderTest.session);

    }

    @Test
    public void shouldFindAnotherTypeOnTheSamePackageFromImplementedType() throws Exception {
        final JavaType stringClass = JavaTypeFinderTest.javaTypeFinder.getType("java.lang.String");
        final JavaType fileClass = JavaTypeFinderTest.javaTypeFinder.getType("File", stringClass, null);
        Assert.assertThat(fileClass, Is.is(IsNull.notNullValue()));
        Assert.assertThat(fileClass.getName(), Is.is("File"));
        Assert.assertThat(fileClass.getPropertyValueAsString("completeName"), Is.is("java.io.File"));

    }

    @Test
    public void shouldFindAnotherTypeOnTheSamePackageFromSuperType() throws Exception {
        final JavaType stringClass = JavaTypeFinderTest.javaTypeFinder.getType("java.util.HashMap");
        final JavaType numberClass = JavaTypeFinderTest.javaTypeFinder.getType("Number", stringClass, null);
        Assert.assertThat(numberClass, Is.is(IsNull.notNullValue()));
        Assert.assertThat(numberClass.getName(), Is.is("Number"));
        Assert.assertThat(numberClass.getPropertyValueAsString("completeName"), Is.is("java.lang.Number"));

    }

    @Test
    public void shouldFindAnotherTypeWithDolarOnTheSamePackageFromSuperType() throws Exception {
        final JavaType stringClass = JavaTypeFinderTest.javaTypeFinder.getType("java.util.HashMap");
        final JavaType numberClass = JavaTypeFinderTest.javaTypeFinder.getType("Map$Entry", stringClass, null);
        Assert.assertThat(numberClass, Is.is(IsNull.notNullValue()));
        Assert.assertThat(numberClass.getName(), Is.is("Map$Entry"));
        Assert.assertThat(numberClass.getPropertyValueAsString("completeName"), Is.is("java.util.Map$Entry"));

    }

    @Test
    public void shouldFindAnotherTypeWithDolarOnTheSamePackageFromSuperTypeWithDolar() throws Exception {
        final JavaType stringClass = JavaTypeFinderTest.javaTypeFinder.getType("java.util.AbstractMap$SimpleEntry");
        final JavaType numberClass = JavaTypeFinderTest.javaTypeFinder.getType("Map$Entry", stringClass, null);
        Assert.assertThat(numberClass, Is.is(IsNull.notNullValue()));
        Assert.assertThat(numberClass.getName(), Is.is("Map$Entry"));
        Assert.assertThat(numberClass.getPropertyValueAsString("completeName"), Is.is("java.util.Map$Entry"));

    }

    @Test
    public void shouldFindConcreteClass() throws Exception {
        final JavaType stringClass = JavaTypeFinderTest.javaTypeFinder.getType("java.lang.String");
        Assert.assertThat(stringClass, Is.is(IsNull.notNullValue()));
        Assert.assertThat(stringClass.getName(), Is.is("String"));
        Assert.assertThat(stringClass.getPropertyValueAsString("completeName"), Is.is("java.lang.String"));
    }

    @Test
    public void shouldFindConcreteInnerClass() throws Exception {
        final SLNode entryClass = JavaTypeFinderTest.javaTypeFinder.getType("java.util.Map$Entry");
        Assert.assertThat(entryClass, Is.is(IsNull.notNullValue()));
        final SLNode newEntryClass = JavaTypeFinderTest.javaTypeFinder.getType("java.util.Map.Entry");
        Assert.assertThat(newEntryClass, Is.is(IsNull.notNullValue()));
        Assert.assertThat(entryClass.getName(), Is.is("Map$Entry"));
        Assert.assertThat(entryClass.getPropertyValueAsString("completeName"), Is.is("java.util.Map$Entry"));

    }

    @Test
    public void shouldFindInterfaceType() throws Exception {
        final SLNode mapClass = JavaTypeFinderTest.javaTypeFinder.getType("java.util.Map");
        Assert.assertThat(mapClass, Is.is(IsNull.notNullValue()));
        Assert.assertThat(mapClass.getName(), Is.is("Map"));
        Assert.assertThat(mapClass.getPropertyValueAsString("completeName"), Is.is("java.util.Map"));
    }

    @Test
    public void shouldFindPrimitiveType() throws Exception {
        final SLNode intClass = JavaTypeFinderTest.javaTypeFinder.getType("int");
        Assert.assertThat(intClass, Is.is(IsNull.notNullValue()));
        Assert.assertThat(intClass.getName(), Is.is("int"));
        Assert.assertThat(intClass.getPropertyValueAsString("completeName"), Is.is("int"));
    }
}
