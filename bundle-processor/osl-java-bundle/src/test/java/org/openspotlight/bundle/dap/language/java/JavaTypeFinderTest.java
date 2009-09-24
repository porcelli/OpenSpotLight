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

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeInterface;
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

    @BeforeClass
    public static void setupJavaFinder() throws Exception {
        final SLGraphFactory factory = new SLGraphFactoryImpl();

        graph = factory.createTempGraph(true);
        session = graph.openSession();
        SLContext abstractContext = session.createContext(Constants.ABSTRACT_CONTEXT);
        SLContext ctx = session.createContext("JRE-util-1.5");
        final JavaGraphNodeSupport support = new JavaGraphNodeSupport(session, ctx.getRootNode(), abstractContext.getRootNode());
        support.setUsingCache(false);
        support.addBeforeTypeProcessing(JavaType.class, "java.lang", "Object", Opcodes.ACC_PUBLIC);
        support.addBeforeTypeProcessing(JavaType.class, "java.lang", "String", Opcodes.ACC_PUBLIC);
        support.addBeforeTypeProcessing(JavaType.class, "java.util", "Map$Entry", Opcodes.ACC_PUBLIC);
        support.addAfterTypeProcessing(JavaTypeClass.class, "java.lang", "String");
        support.addAfterTypeProcessing(JavaTypeClass.class, "java.lang", "String");
        support.addAfterTypeProcessing(JavaTypeClass.class, "java.util", "Map$Entry");
        support.addAfterTypeProcessing(JavaTypeInterface.class, "java.util", "Map");
        //        support.addBeforeTypeProcessing(JavaTypePrimitive.class, "", "int", Opcodes.ACC_PUBLIC);
        //        support.addAfterTypeProcessing(JavaTypePrimitive.class, "", "int");

        session.save();
        session.close();
        session = graph.openSession();
        abstractContext = session.getContext(Constants.ABSTRACT_CONTEXT);
        ctx = session.getContext("JRE-util-1.5");
        final List<SLContext> orderedActiveContexts = asList(ctx);

        javaTypeFinder = new JavaTypeFinder(abstractContext, orderedActiveContexts, true, session);

    }

    @Test
    public void shouldFindConcreteClass() throws Exception {
        final SLNode stringClass = javaTypeFinder.getType("java.lang.String");
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
}
