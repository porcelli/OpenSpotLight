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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.bundle.dap.language.java.support.JavaTypeFinder;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQuery;
import org.openspotlight.graph.query.SLQueryResult;

/**
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class JavaTypeFinderTest {

    private JavaTypeFinder javaTypeFinder;
    @Mock
    private SLContext      abstractContext;
    @Mock
    private SLContext      contextNumberOne;
    @Mock
    private SLContext      contextNumberTwo;
    @Mock
    private SLContext      contextNumberTree;
    @Mock
    private SLGraphSession session;
    @Mock
    private SLQuery        query;
    @Mock
    private SLQueryResult  result;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        when(this.abstractContext.getID()).thenReturn(Constants.ABSTRACT_CONTEXT);
        when(this.contextNumberOne.getID()).thenReturn("ctx-1");
        when(this.contextNumberTwo.getID()).thenReturn("ctx-2");
        when(this.contextNumberTree.getID()).thenReturn("ctx-3");
        when(this.session.createQuery()).thenReturn(this.query);
        when(this.query.execute()).thenReturn(this.result);
        final List<SLContext> orderedActiveContexts = asList(this.contextNumberOne, this.contextNumberTwo, this.contextNumberTree);
        this.javaTypeFinder = new JavaTypeFinder(this.abstractContext, orderedActiveContexts, true, this.session);
    }

    @Test
    public void shouldFindConcreteClass() throws Exception {
        final List<SLNode> resultList = new ArrayList<SLNode>();
        final JavaTypeClass newType = mock(JavaTypeClass.class);
        when(newType.getContext()).thenReturn(this.contextNumberOne);
        when(newType.getCompleteName()).thenReturn("java.lang.String");
        when(this.result.getNodes()).thenReturn(resultList);
        final JavaTypeClass stringClass = this.javaTypeFinder.getType("java.lang.String");
        assertThat(stringClass, is(notNullValue()));
    }

    @Ignore
    @Test
    public void shouldFindConcreteInnerClass() throws Exception {
        final JavaTypeClass entryClass = this.javaTypeFinder.getType("java.lang.Map.Entry");
        assertThat(entryClass, is(notNullValue()));
    }

    @Ignore
    @Test
    public void shouldFindInterfaceType() throws Exception {
        final JavaTypeInterface mapClass = this.javaTypeFinder.getType("java.util.Map");
        assertThat(mapClass, is(notNullValue()));
    }

    @Ignore
    @Test
    public void shouldFindPrimitiveType() throws Exception {
        final JavaTypePrimitive intClass = this.javaTypeFinder.getType("int");
        assertThat(intClass, is(notNullValue()));
    }
}
