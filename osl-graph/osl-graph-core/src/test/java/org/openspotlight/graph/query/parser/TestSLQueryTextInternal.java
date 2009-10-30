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
package org.openspotlight.graph.query.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.query.AbstractGeneralQueryTest;
import org.openspotlight.graph.query.AssertResult;
import org.openspotlight.graph.query.SLInvalidQueryElementException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQLVariable;
import org.openspotlight.graph.query.SLQueryResult;
import org.openspotlight.graph.query.SLQueryText;
import org.openspotlight.graph.query.SLQueryTextInternal;
import org.openspotlight.graph.query.SLQuery.SortMode;

/**
 * The Class SLQLQueryTest.
 * 
 * @author porcelli
 */

public class TestSLQueryTextInternal extends AbstractGeneralQueryTest {

    private final SLQueryTextInternalBuilder queryBuilder = new SLQueryTextInternalBuilder();

    /**
     * Instantiates a new sL graph query test.
     */
    public TestSLQueryTextInternal() {
        LOGGER = Logger.getLogger(TestSLQueryTextInternal.class);
    }

    /**
     * Instantiates a new sL graph query test.
     * 
     * @param session the session
     */
    public TestSLQueryTextInternal(
                                    final SLGraphSession sessionParam ) {
        session = sessionParam;
        LOGGER = Logger.getLogger(TestSLQueryTextInternal.class);
    }

    @Test
    public void testCheckDefineMessage() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("CheckDefineMessage.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        assertThat(query.getVariables().size(), is(4));
        for (final SLQLVariable activeVariable : query.getVariables()) {
            if (activeVariable.getName().equals("$testeString")) {
                assertThat(activeVariable.getDisplayMessage(), is("Entre com o testeString"));
            } else if (activeVariable.getName().equals("#testeInt")) {
                assertThat(activeVariable.getDisplayMessage(), is("Entre com o testeInt"));
            } else if (activeVariable.getName().equals("@testeBool")) {
                assertThat(activeVariable.getDisplayMessage(), is("Entre com o testeBool"));
            } else if (activeVariable.getName().equals("&testeFloat")) {
                assertThat(activeVariable.getDisplayMessage(), is("Entre com o testeFloat"));
            }
        }
    }

    @Test( expected = IllegalArgumentException.class )
    public void testInvalidSelectWithTarget()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException, IllegalArgumentException {
        final String slqlInput = this.getResourceContent("SelectWithTarget.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        query.execute(SortMode.SORTED, false, 20, 21);
    }

    /**
     * Test not conditional operator.
     */
    @Test
    public void testNotConditionalOperator() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("NotConditionalOperator.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test not relational operator.
     */
    @Test
    public void testNotRelationalOperator() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("NotRelationalOperator.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(17));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Cloneable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Comparable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Queue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.io",
                                           "java.io.Serializable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Runnable"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select all date methods.
     */
    @Test
    public void testSelectAllDateMethods() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectAllDateMethods.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        //final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                //FIXME check results here
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select all java classes.
     */
    @Test
    public void testSelectAllJavaClasses() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectAllJavaClasses.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(45));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.security.BasicPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.security.Permission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select all java interfaces.
     */
    @Test
    public void testSelectAllJavaInterfaces() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectAllJavaInterfaces.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(19));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Cloneable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Comparable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Queue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.io",
                                           "java.io.Serializable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Runnable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select all java types.
     */
    @Test
    public void testSelectAllJavaTypes() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectAllJavaTypes.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(64));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Cloneable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.security.BasicPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Comparable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Queue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.security.Permission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.io",
                                           "java.io.Serializable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Runnable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select all packages.
     * 
     * @throws SLInvalidQuerySyntaxException
     */
    @Test
    public void testSelectAllPackages() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectAllPackages.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.security"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.io"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.util"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.lang"), isOneOf(wrappers));
            }
        }.execute();

        final SLQueryTextInternal query2 = this.queryBuilder.build(slqlInput);

        final SLQueryResult result2 = query2.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers2 = this.wrapNodes(result2.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers2.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.security"), isOneOf(wrappers2));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.io"), isOneOf(wrappers2));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.util"), isOneOf(wrappers2));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.lang"), isOneOf(wrappers2));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select all packages.
     * 
     * @throws SLInvalidQuerySyntaxException
     */
    @Test
    public void testSelectAllPackagesUserWay() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectAllPackages.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final SLQueryResult result = query.execute();
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.security"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.io"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.util"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaPackage.class.getName(), "queryTest",
                                           "java.lang"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select all types.
     */
    @Test
    public void testSelectAllTypes() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectAllTypes.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        //final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                //FIXME check results here
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select all types on where.
     */
    @Test
    public void testSelectAllTypesOnWhere() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectAllTypesOnWhere.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(37));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "isExternallySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.AbstractMap", "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getSetStateFields"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collections", "checkedSortedSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.WeakHashMap", "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "headSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collections", "synchronizedSortedSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collections", "unmodifiableSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeMap", "readTreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Hashtable", "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collections", "synchronizedSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.HashMap", "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.IdentityHashMap", "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "isFieldSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "isSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.AbstractMap", "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeMap", "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "subSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "tailSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.IdentityHashMap", "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.HashMap", "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "internalSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collections", "checkedSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeSet", "headSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.BitSet", "nextSetBit"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.WeakHashMap", "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Hashtable", "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeMap", "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeSet", "tailSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeSet", "subSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collections", "unmodifiableSortedSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collections", "emptySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeMap", "addAllForTreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorDescriptionPrimary() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorDescriptionPrimary.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(1));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                   "java.util.Collection"), is(wrappers[0]));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorDescriptionSecondary() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorDescriptionSecondary.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(1));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                   "java.util.Collection"), is(wrappers[0]));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorDescriptionTertiary() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorDescriptionTertiary.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(1));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                   "java.util.Collection"), is(wrappers[0]));

        this.printResult(result.getNodes());
    }

    /**
     * Test select collator primary change accent.
     */
    @Test
    public void testSelectByCollatorKeyPrimaryChangeAccent() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeyPrimaryChangeAccent.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(1));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                   "java.util.Collection"), is(wrappers[0]));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorKeyPrimaryChangeAccentAndCase() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeyPrimaryChangeAccentAndCase.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(1));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                   "java.util.Collection"), is(wrappers[0]));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorKeyPrimaryChangeCase() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeyPrimaryChangeCase.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(1));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                   "java.util.Collection"), is(wrappers[0]));

        this.printResult(result.getNodes());
    }

    /**
     * Test select collator primary change accent.
     */
    @Test
    public void testSelectByCollatorKeySecondaryChangeAccent() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeySecondaryChangeAccent.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(0));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorKeySecondaryChangeAccentAndCase()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeySecondaryChangeAccentAndCase.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(0));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorKeySecondaryChangeCase() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeySecondaryChangeCase.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(1));
        assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                   "java.util.Collection"), is(wrappers[0]));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorKeyTertiaryChangeAccent() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeyTertiaryChangeAccent.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(0));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorKeyTertiaryChangeAccentAndCase()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeyTertiaryChangeAccentAndCase.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(0));

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByCollatorKeyTertiaryChangeCase() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByCollatorKeyTertiaryChangeCase.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        assertThat(wrappers.length, is(0));

        this.printResult(result.getNodes());
    }

    @Test
    @Ignore
    public void testSelectByLinkCount() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByLinkCount.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectByLinkCountIntVariable() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByLinkCountIntVariable.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#intVar", 3);

        final SLQueryResult result = query.execute(session, variableValues, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(14));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select by link with any side.
     */
    @Test
    public void testSelectByLinkWithAnySide() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectByLinkWithAnySide.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(122));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "indexOf"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Observer", "update"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "lastIndexOf"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Comparator", "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "subList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "hasNext"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "previous"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "firstKey"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "previousIndex"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "headSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "lastKey"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "subSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "put"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "nextIndex"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "values"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "tailMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "first"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Iterator", "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Enumeration", "hasMoreElements"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Iterator", "hasNext"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "next"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "subMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "containsValue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Comparator", "compare"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "hasPrevious"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "headMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "containsKey"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "listIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "last"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Iterator", "next"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Enumeration", "nextElement"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "tailSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "putAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "equals"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select by link with any side with keep result.
     */
    @Test
    public void testSelectByLinkWithAnySideWithKeepResult() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        //FIXME here there is problems! there is no keep result! its the same as the above test
        final String slqlInput = this.getResourceContent("SelectByLinkWithAnySideWithKeepResult.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(122));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "indexOf"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Observer", "update"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "lastIndexOf"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Comparator", "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "subList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "hasNext"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "previous"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "firstKey"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "previousIndex"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "headSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "lastKey"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "subSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "put"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "nextIndex"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "values"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "tailMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "first"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Iterator", "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Enumeration", "hasMoreElements"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Iterator", "hasNext"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "next"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "subMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "containsValue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Comparator", "compare"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "entrySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListIterator", "hasPrevious"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedMap", "headMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "containsKey"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "listIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "last"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Iterator", "next"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "keySet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Enumeration", "nextElement"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SortedSet", "tailSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "putAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Set",
                                           "equals"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select collection methods.
     */
    @Test
    public void testSelectCollectionMethods() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectCollectionMethods.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(14));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "remove"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select collection methods with keep result.
     * 
     * @throws SLQueryLanguageParserException
     * @throws SLGraphSessionException
     */
    @Test
    public void testSelectCollectionMethodsWithKeepResult() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectCollectionMethodsWithKeepResult.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(15));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "remove"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select date methods.
     */
    @Test
    public void testSelectDateMethods() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectDateMethods.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(36));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "toGMTString"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "setDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "setSeconds"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getSeconds"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getTimezoneOffset"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getDay"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getMonth"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getTimeImpl"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "convertToAbbr"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "setHours"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getCalendarSystem"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getCalendarDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "setYear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "after"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "toLocaleString"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "readObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getHours"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getTime"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "setMonth"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "UTC"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "setTime"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getJulianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getMinutes"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getMillisOf"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "before"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "setMinutes"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "writeObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "parse"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getYear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "toString"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "compareTo"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "clone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "normalize"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectDateMethodsLimit() {
        try {

            final String slqlInput = this.getResourceContent("SelectDateMethodsLimit.slql");
            final SLQueryText query = session.createQueryText(slqlInput);

            final SLQueryResult result = query.execute(SortMode.SORTED, false);
            final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

            new AssertResult() {
                public void execute() {
                    assertThat(wrappers.length, is(11));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                               "java.util.Date"), is(wrappers[0]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "UTC"), is(wrappers[1]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "after"), is(wrappers[2]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "before"), is(wrappers[3]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "clone"), is(wrappers[4]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "compareTo"), is(wrappers[5]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "convertToAbbr"), is(wrappers[6]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "equals"), is(wrappers[7]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getCalendarDate"), is(wrappers[8]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getCalendarSystem"), is(wrappers[9]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getDate"), is(wrappers[10]));
                }
            }.execute();

            this.printResult(result.getNodes());
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void testSelectDateMethodsLimit2() {
        try {

            final String slqlInput = this.getResourceContent("SelectDateMethodsLimit2.slql");
            final SLQueryText query = session.createQueryText(slqlInput);

            final SLQueryResult result = query.execute(SortMode.SORTED, false);
            final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

            new AssertResult() {
                public void execute() {
                    assertThat(wrappers.length, is(21));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                               "java.util.Date"), is(wrappers[0]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "UTC"), is(wrappers[1]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "after"), is(wrappers[2]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "before"), is(wrappers[3]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "clone"), is(wrappers[4]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "compareTo"), is(wrappers[5]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "convertToAbbr"), is(wrappers[6]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "equals"), is(wrappers[7]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getCalendarDate"), is(wrappers[8]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getCalendarSystem"), is(wrappers[9]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getDate"), is(wrappers[10]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getDay"), is(wrappers[11]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getHours"), is(wrappers[12]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getJulianCalendar"), is(wrappers[13]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMillisOf"), is(wrappers[14]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMinutes"), is(wrappers[15]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMonth"), is(wrappers[16]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getSeconds"), is(wrappers[17]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTime"), is(wrappers[18]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTimeImpl"), is(wrappers[19]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTimezoneOffset"), is(wrappers[20]));
                }
            }.execute();

            this.printResult(result.getNodes());
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void testSelectDateMethodsLimitGeneral() {
        try {
            final String slqlInput = this.getResourceContent("SelectDateMethodsLimitGeneral.slql");
            final SLQueryText query = session.createQueryText(slqlInput);

            final SLQueryResult result = query.execute(SortMode.SORTED, false, 10, null);
            final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

            this.printInfo = true;
            this.printResult(result.getNodes());
            new AssertResult() {
                public void execute() {
                    assertThat(wrappers.length, is(10));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                               "java.util.Date"), is(wrappers[0]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "UTC"), is(wrappers[1]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "after"), is(wrappers[2]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "before"), is(wrappers[3]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "clone"), is(wrappers[4]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "compareTo"), is(wrappers[5]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "convertToAbbr"), is(wrappers[6]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "equals"), is(wrappers[7]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getCalendarDate"), is(wrappers[8]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getCalendarSystem"), is(wrappers[9]));
                }
            }.execute();
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Test select date methods with limit.
     */
    @Test
    public void testSelectDateMethodsLimitGeneral2() {
        try {

            final String slqlInput = this.getResourceContent("SelectDateMethodsLimitGeneral.slql");
            final SLQueryText query = session.createQueryText(slqlInput);

            final SLQueryResult result = query.execute(SortMode.SORTED, false, 20, null);
            final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

            new AssertResult() {
                public void execute() {
                    assertThat(wrappers.length, is(20));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                               "java.util.Date"), is(wrappers[0]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "UTC"), is(wrappers[1]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "after"), is(wrappers[2]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "before"), is(wrappers[3]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "clone"), is(wrappers[4]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "compareTo"), is(wrappers[5]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "convertToAbbr"), is(wrappers[6]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "equals"), is(wrappers[7]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getCalendarDate"), is(wrappers[8]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getCalendarSystem"), is(wrappers[9]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getDate"), is(wrappers[10]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getDay"), is(wrappers[11]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getHours"), is(wrappers[12]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getJulianCalendar"), is(wrappers[13]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMillisOf"), is(wrappers[14]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMinutes"), is(wrappers[15]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMonth"), is(wrappers[16]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getSeconds"), is(wrappers[17]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTime"), is(wrappers[18]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTimeImpl"), is(wrappers[19]));
                }
            }.execute();

            this.printResult(result.getNodes());
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Test select date methods with limit.
     */
    @Test
    public void testSelectDateMethodsLimitOffset() {
        try {
            final String slqlInput = this.getResourceContent("SelectDateMethodsLimitOffset.slql");
            final SLQueryText query = session.createQueryText(slqlInput);

            final SLQueryResult result = query.execute(sortMode, this.printInfo);
            final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

            this.printResult(result.getNodes());

            new AssertResult() {
                public void execute() {
                    assertThat(wrappers.length, is(11));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                               "java.util.Date"), is(wrappers[0]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getDay"), is(wrappers[1]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getHours"), is(wrappers[2]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getJulianCalendar"), is(wrappers[3]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMillisOf"), is(wrappers[4]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMinutes"), is(wrappers[5]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMonth"), is(wrappers[6]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getSeconds"), is(wrappers[7]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTime"), is(wrappers[8]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTimeImpl"), is(wrappers[9]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTimezoneOffset"), is(wrappers[10]));
                }
            }.execute();

        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Test select date methods with limit.
     */
    @Test
    public void testSelectDateMethodsLimitOffset2() {
        try {

            final String slqlInput = this.getResourceContent("SelectDateMethodsLimitOffset2.slql");
            final SLQueryText query = session.createQueryText(slqlInput);

            final SLQueryResult result = query.execute(sortMode, this.printInfo);
            final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

            this.printInfo = true;
            this.printResult(result.getNodes());

            new AssertResult() {
                public void execute() {
                    assertThat(wrappers.length, is(17));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                               "java.util.Date"), is(wrappers[0]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getYear"), is(wrappers[1]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "hashCode"), is(wrappers[2]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "normalize"), is(wrappers[3]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "parse"), is(wrappers[4]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "readObject"), is(wrappers[5]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setDate"), is(wrappers[6]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setHours"), is(wrappers[7]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setMinutes"), is(wrappers[8]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setMonth"), is(wrappers[9]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setSeconds"), is(wrappers[10]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setTime"), is(wrappers[11]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setYear"), is(wrappers[12]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "toGMTString"), is(wrappers[13]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "toLocaleString"), is(wrappers[14]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "toString"), is(wrappers[15]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "writeObject"), is(wrappers[16]));
                }
            }.execute();

        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Test
    public void testSelectDateMethodsLimitOffsetGeneral() {
        try {
            final String slqlInput = this.getResourceContent("SelectDateMethodsLimitGeneral.slql");
            final SLQueryText query = session.createQueryText(slqlInput);

            final SLQueryResult result = query.execute(SortMode.SORTED, false, 10, 11);
            final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

            this.printInfo = true;
            this.printResult(result.getNodes());
            new AssertResult() {
                public void execute() {
                    assertThat(wrappers.length, is(10));
                    assertThat(wrappers.length, is(10));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getDate"), is(wrappers[0]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getDay"), is(wrappers[1]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getHours"), is(wrappers[2]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getJulianCalendar"), is(wrappers[3]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMillisOf"), is(wrappers[4]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMinutes"), is(wrappers[5]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getMonth"), is(wrappers[6]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getSeconds"), is(wrappers[7]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTime"), is(wrappers[8]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTimeImpl"), is(wrappers[9]));
                }
            }.execute();
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Test select date methods with limit.
     */
    @Test
    public void testSelectDateMethodsLimitOffsetGeneral2() {
        try {

            final String slqlInput = this.getResourceContent("SelectDateMethodsLimitGeneral.slql");
            final SLQueryText query = session.createQueryText(slqlInput);

            final SLQueryResult result = query.execute(SortMode.SORTED, false, 20, 21);
            final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

            new AssertResult() {
                public void execute() {
                    assertThat(wrappers.length, is(17));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getTimezoneOffset"), is(wrappers[0]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "getYear"), is(wrappers[1]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "hashCode"), is(wrappers[2]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "normalize"), is(wrappers[3]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "parse"), is(wrappers[4]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "readObject"), is(wrappers[5]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setDate"), is(wrappers[6]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setHours"), is(wrappers[7]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setMinutes"), is(wrappers[8]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setMonth"), is(wrappers[9]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setSeconds"), is(wrappers[10]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setTime"), is(wrappers[11]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "setYear"), is(wrappers[12]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "toGMTString"), is(wrappers[13]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "toLocaleString"), is(wrappers[14]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "toString"), is(wrappers[15]));
                    assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                               "java.util.Date", "writeObject"), is(wrappers[16]));
                }
            }.execute();

            this.printResult(result.getNodes());
        } catch (final SLException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Test select date methods with tag between30 and70.
     */
    @Test( )
    @Ignore
    public void testSelectDateMethodsWithTagBetween30And70() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        //FIXME where no link... ainda não suportado na syntax
        final String slqlInput = this.getResourceContent("SelectDateMethodsWithTagBetween30And70.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
//        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                //FIXME check results here
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select date methods with tag greater than50.
     */
    @Test( )
    @Ignore
    public void testSelectDateMethodsWithTagGreaterThan50() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        //FIXME where no link... ainda não suportado na syntax
        final String slqlInput = this.getResourceContent("SelectDateMethodsWithTagGreaterThan50.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        //final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                //FIXME check results here
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select date methods with tag lesser or equal to30 or greater or equal to70.
     */
    @Test( )
    @Ignore
    public void testSelectDateMethodsWithTagLesserOrEqualTo30OrGreaterOrEqualTo70()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        //FIXME where no link... ainda não suportado na syntax
        final String slqlInput = this.getResourceContent("SelectDateMethodsWithTagLesserOrEqualTo30OrGreaterOrEqualTo70.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        //final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                //FIXME check results here
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select date methods with tag lesser or equal to50.
     */
    @Test( )
    @Ignore
    public void testSelectDateMethodsWithTagLesserOrEqualTo50() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        //FIXME where no link... ainda não suportado na syntax
        final String slqlInput = this.getResourceContent("SelectDateMethodsWithTagLesserOrEqualTo50.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        //final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                //FIXME check results here
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select interfaces with set and classes with map.
     */
    @Test
    public void testSelectInterfacesWithSetAndClassesWithMap() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectInterfacesWithSetAndClassesWithMap.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(8));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select interfaces with set over types from util.
     * 
     * @throws SLQueryLanguageParserException
     * @throws SLGraphSessionException
     */
    @Test
    public void testSelectInterfacesWithSetOverTypesFromUtil() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectInterfacesWithSetOverTypesFromUtil.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(2));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select interfaces with set over types from util with keep result.
     */
    @Test
    public void testSelectInterfacesWithSetOverTypesFromUtilWithKeepResult()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectInterfacesWithSetOverTypesFromUtilWithKeepResult.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(55));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Queue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select order by ascending.
     */
    @Test
    public void testSelectOrderByAscending() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectOrderByAscending.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(2));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), is(wrappers[0]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), is(wrappers[1]));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectOrderByAscendingAndDescending() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectOrderByAscendingAndDescending.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(7));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), is(wrappers[0]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), is(wrappers[1]));

                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), is(wrappers[2]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), is(wrappers[3]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), is(wrappers[4]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), is(wrappers[5]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), is(wrappers[6]));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectOrderByCrossType() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectOrderByCrossType.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(7));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), is(wrappers[0]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), is(wrappers[1]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), is(wrappers[2]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), is(wrappers[3]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), is(wrappers[4]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), is(wrappers[5]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), is(wrappers[6]));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select order by descending.
     */
    @Test
    public void testSelectOrderByDescending() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectOrderByDescending.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(5));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), is(wrappers[0]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), is(wrappers[1]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), is(wrappers[2]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), is(wrappers[3]));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), is(wrappers[4]));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select sorted set hierarchy execute3 times.
     */
    @Test
    public void testSelectSortedSetHierarchyExecute3Times() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyExecute3Times.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select sorted set hierarchy execute3 times.
     */
    @Test
    public void testSelectSortedSetHierarchyExecuteVariableTimes() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyExecuteVariableTimes.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final Map<String, Integer> variableValues = new HashMap<String, Integer>();
        variableValues.put("#times", 3);

        final SLQueryResult result = query.execute(session, variableValues, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select sorted set hierarchy execute3 times.
     */
    @Test
    public void testSelectSortedSetHierarchyExecuteVariableTimesWithDomain()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyExecuteVariableTimesWithDomain.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final Map<String, Integer> variableValues = new HashMap<String, Integer>();
        variableValues.put("#times", 3);

        final SLQueryResult result = query.execute(session, variableValues, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select sorted set hierarchy execute3 times.
     */
    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectSortedSetHierarchyExecuteVariableTimesWithIncorrectDomain()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyExecuteVariableTimesWithDomain.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final Map<String, Integer> variableValues = new HashMap<String, Integer>();
        variableValues.put("#times", 4);

        query.execute(session, variableValues, null, sortMode, this.printInfo, null, null);
    }

    /**
     * Test select sorted set hierarchy execute3 times.
     */
    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectSortedSetHierarchyExecuteVariableTimesWithIncorrectValue()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyExecuteVariableTimesWithDomain.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#times", "4");

        query.execute(session, variableValues, null, sortMode, this.printInfo, null, null);
    }

    /**
     * Test select sorted set hierarchy execute3 times.
     */
    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectSortedSetHierarchyExecuteVariableTimesWithNullValue()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyExecuteVariableTimesWithDomain.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#times", "4");

        query.execute(session, variableValues, null, sortMode, this.printInfo, null, null);
    }

    /**
     * Test select types from java util package.
     */
    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectSortedSetHierarchyExecuteVariableTimesWithoutCorrectVariable()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromJavaUtilPackageUsingVariables.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#Times", 3);

        queryText.execute(variableValues);
    }

    /**
     * Test select sorted set hierarchy execute x times.
     */
    @Test
    public void testSelectSortedSetHierarchyExecuteXTimes() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyExecuteXTimes.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select sorted set hierarchy level1.
     */
    @Test
    public void testSelectSortedSetHierarchyLevel1() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyLevel1.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(2));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select sorted set hierarchy level2.
     */
    @Test
    public void testSelectSortedSetHierarchyLevel2() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyLevel2.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(3));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select sorted set hierarchy level3.
     */
    @Test
    public void testSelectSortedSetHierarchyLevel3() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectSortedSetHierarchyLevel3.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select types from java util package.
     */
    @Test
    public void testSelectTypesFromJavaUtilPackage() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromJavaUtilPackage.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(55));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Queue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select types from java util package.
     */
    @Test
    public void testSelectTypesFromJavaUtilPackageUsingVariables() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromJavaUtilPackageUsingVariables.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        final Map<String, String> variableValues = new HashMap<String, String>();
        variableValues.put("$javaPackage", "java.util");

        final SLQueryResult result = queryText.execute(variableValues);
        ;
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(55));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Queue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select types from java util package.
     */
    @Test
    public void testSelectTypesFromJavaUtilPackageUsingVariablesAndDomain()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromPackageUsingVariablesAndDomain.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        final Map<String, String> variableValues = new HashMap<String, String>();
        variableValues.put("$javaPackage", "java.util");

        final SLQueryResult result = queryText.execute(variableValues);

        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(55));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Queue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

    }

    /**
     * Test select types from java util package.
     */
    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectTypesFromJavaUtilPackageUsingVariablesAndIncorrectDomain()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromPackageUsingVariablesAndDomain.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        final Map<String, String> variableValues = new HashMap<String, String>();
        variableValues.put("$javaPackage", "java");

        queryText.execute(variableValues);
    }

    /**
     * Test select types from java util package.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testSelectTypesFromJavaUtilPackageUsingVariablesWithIncorrectValue()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromJavaUtilPackageUsingVariables.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("$javaPackage", 1);

        queryText.execute(variableValues);
    }

    /**
     * Test select types from java util package.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testSelectTypesFromJavaUtilPackageUsingVariablesWithNullValue()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromJavaUtilPackageUsingVariables.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        final Map<String, String> variableValues = new HashMap<String, String>();
        variableValues.put("$javaPackage", null);

        queryText.execute(variableValues);
    }

    /**
     * Test select types from java util package.
     */
    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectTypesFromJavaUtilPackageUsingVariablesWithoutCorrectVariable()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromJavaUtilPackageUsingVariables.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        final Map<String, String> variableValues = new HashMap<String, String>();
        variableValues.put("$JavaPackage", "java.util");

        queryText.execute(variableValues);
    }

    /**
     * Test select types from java util package.
     */
    @Test( expected = IllegalArgumentException.class )
    public void testSelectTypesFromJavaUtilPackageUsingVariablesWithoutValue()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromJavaUtilPackageUsingVariables.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        queryText.execute();
    }

    /**
     * Test select types from methods with get.
     */
    @Test
    public void testSelectTypesFromMethodsWithGet() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromMethodsWithGet.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(31));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select types from methods with get with keep result.
     */
    @Test
    public void testSelectTypesFromMethodsWithGetWithKeepResult() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromMethodsWithGetWithKeepResult.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(169));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getMaximum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.LinkedList", "getLast"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getSetStateFields"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getLastJulianDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getDisplayVariantArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getTime"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SimpleTimeZone", "getTransition"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getSeconds"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getMillisOf"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeMap", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getMonth"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getTimeImpl"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getJulianCalendarSystem"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getCalendarSystem"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SimpleTimeZone", "getStart"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getClassContext"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getActualMinimum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getTime"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.HashMap", "getEntry"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getRawOffset"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Hashtable", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getLeastMaximum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getCurrentFixedDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getJulianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getGreatestMinimum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getMinutes"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getCountry"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SimpleTimeZone", "getOffset"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.List",
                                           "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SimpleTimeZone", "getDSTSavings"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SimpleTimeZone", "getEnd"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ArrayList", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getYear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getSystemGMTOffsetID"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Dictionary", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getLoader"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getFixedDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getDisplayLanguage"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Map",
                                           "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getWeekNumber"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getDefaultRef"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getTimezoneOffset"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getTimeInMillis"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getActualMaximum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getISO3Country"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getCutoverCalendarSystem"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getFieldName"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.LinkedList", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getDSTSavings"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.IdentityHashMap", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getMinimum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getISOCountries"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Currency", "getCurrencyCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.WeakHashMap", "getEntry"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.PropertyResourceBundle", "getKeys"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getMinimalDaysInFirstWeek"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getVariant"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.LinkedList", "getFirst"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.HashMap", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListResourceBundle", "getContents"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getDefault"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.WeakHashMap", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getActualMinimum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Currency", "getMainTableEntry"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getLeastMaximum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeMap", "getEntry"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collections", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.PropertyPermission", "getMask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getAvailableIDs"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getFixedDateMonth1"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getLocale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getOffsets"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.BitSet", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Currency", "getDefaultFractionDigits"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeMap", "getPrecedingEntry"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TreeMap", "getCeilEntry"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getOffset"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Currency", "getSymbol"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getAvailableLocales"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getActualMaximum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getKeys"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getISO3Language"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getCalendarDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getDefault"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ListResourceBundle", "getKeys"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getRolledValue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getStringArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Hashtable", "getIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getISOLanguages"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.AbstractList", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getFirstDayOfWeek"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getGregorianCutoverDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getDisplayName"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.EventObject", "getSource"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.HashMap", "getForNullKey"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getDisplayName"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getYearOffsetInMillis"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getGregorianChange"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getMinimum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getAvailableLocales"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Hashtable", "getEnumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Properties", "getProperty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.WeakHashMap", "getTable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.PropertyPermission", "getActions"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.LinkedHashMap", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.AbstractMap", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.AbstractSequentialList", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getDisplayCountry"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getDay"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getDisplayVariant"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getNormalizedCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getCalendarDate"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getGreatestMinimum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.EventListenerProxy", "getListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getString"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getMaximum"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getID"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getSystemTimeZoneID"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getHours"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SimpleTimeZone", "getRawOffset"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.SimpleTimeZone", "getOffsets"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Locale", "getLanguage"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Currency", "getInstance"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Calendar", "getInstance"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.BitSet", "getBits"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(), "java.util.Date",
                                           "getMillisOf"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getBundleImpl"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.TimeZone", "getDisplayNames"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.ResourceBundle", "getObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Vector", "get"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.GregorianCalendar", "getFixedDateJan1"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select types from java util package.
     */
    @Test
    public void testSelectTypesFromPackageUsingVariablesAndDomainEmpty()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectTypesFromPackageUsingVariablesAndDomain.slql");
        final SLQueryText queryText = session.createQueryText(slqlInput);

        final Map<String, String> variableValues = new HashMap<String, String>();
        variableValues.put("$javaPackage", "java.lang");

        final SLQueryResult result = queryText.execute(variableValues);

        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        this.printResult(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(4));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Cloneable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Comparable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Runnable"), isOneOf(wrappers));
            }
        }.execute();
    }

    /**
     * Test select types from util with set list or map.
     */
    @Test
    @Ignore
    public void testSelectTypesFromUtilWithSetListOrMap() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectTypesFromUtilWithSetListOrMap.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(24));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    /**
     * Test select types that contains set or list.
     */
    @Test
    public void testSelectTypesThatContainsSetOrList() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectTypesThatContainsSetOrList.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(16));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test
    @Ignore
    public void testSelectUsingBoolVariable() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingBoolVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("@boolValue", new Boolean(true));

        final SLQueryResult result = query.execute(variableValues);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(11));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSelectUsingBoolVariableIncorrectDataType() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingBoolVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("@boolValue", "teste");

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingBoolVariableIncorrectVariable() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingBoolVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("@BoolValue", 3);

        query.execute(variableValues);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSelectUsingBoolVariableNullValue() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingBoolVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("@boolValue", null);

        query.execute(variableValues);
    }

    @Test
    public void testSelectUsingDecVariable() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("&decValue", 1.3F);

        final SLQueryResult result = query.execute(variableValues);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(11));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingDecVariableIncorrectDataType() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#DecValue", "teste");

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingDecVariableIncorrectVariable() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#DecValue", 3);

        query.execute(variableValues);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSelectUsingDecVariableNullValue() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("&decValue", null);

        query.execute(variableValues);
    }

    @Test
    public void testSelectUsingDecVariableWithDomain() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("&decValue", new Float(1.3F));

        final SLQueryResult result = query.execute(variableValues);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(11));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingDecVariableWithDomainIncorrectDataType()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#DecValue", "teste");

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingDecVariableWithDomainIncorrectDomain()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("&decValue", 22);

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingDecVariableWithDomainIncorrectVariable()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#DecValue", null);

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingDecVariableWithDomainNullValue() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingDecVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("&decValue", null);

        query.execute(variableValues);
    }

    @Test
    public void testSelectUsingIntVariable() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#intValue", 4);

        final SLQueryResult result = query.execute(variableValues);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(1));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingIntVariableIncorrectDataType() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#IntValue", "teste");

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingIntVariableIncorrectVariable() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#IntValue", 3);

        query.execute(variableValues);
    }

    @Test( expected = IllegalArgumentException.class )
    public void testSelectUsingIntVariableNullValue() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariable.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#intValue", null);

        query.execute(variableValues);
    }

    @Test
    public void testSelectUsingIntVariableWithDomain() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#intValue", 3);

        final SLQueryResult result = query.execute(variableValues);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(1));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingIntVariableWithDomainIncorrectDataType()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#IntValue", "teste");

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingIntVariableWithDomainIncorrectDomain()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#intValue", 22);

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingIntVariableWithDomainIncorrectVariable()
        throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#IntValue", null);

        query.execute(variableValues);
    }

    @Test( expected = SLInvalidQueryElementException.class )
    public void testSelectUsingIntVariableWithDomainNullValue() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUsingIntVariableWithDomain.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final Map<String, Object> variableValues = new HashMap<String, Object>();
        variableValues.put("#intValue", null);

        query.execute(variableValues);
    }

    /**
     * Test select util types and colletion methods.
     */
    @Test
    public void testSelectUtilTypesAndColletionMethods() throws SLGraphSessionException, SLInvalidQuerySyntaxException {

        final String slqlInput = this.getResourceContent("SelectUtilTypesAndColletionMethods.slql");
        final SLQueryTextInternal query = this.queryBuilder.build(slqlInput);

        final SLQueryResult result = query.execute(session, null, null, sortMode, this.printInfo, null, null);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(73));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "retainAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.security.BasicPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "isEmpty"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "containsAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "add"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "contains"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "size"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "removeAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "addAll"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "equals"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.security.Permission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "toArray"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "hashCode"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "clear"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaTypeMethod.class.getName(),
                                           "java.util.Collection", "remove"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();

        this.printResult(result.getNodes());
    }

    @Test
    public void testSelectWithTarget() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectWithTarget.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final SLQueryResult result = query.executeTarget(SortMode.SORTED, false);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(64));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Cloneable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Map"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.ListIterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.security.BasicPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Observer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.lang.Iterable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Set"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.RandomAccess"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Enumeration"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.List"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Iterator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Collection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.EventListener"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Comparable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Queue"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.security.Permission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.io",
                                           "java.io.Serializable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.lang",
                                           "java.lang.Runnable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.Comparator"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaInterface.class.getName(), "java.util",
                                           "java.util.SortedSet"), isOneOf(wrappers));
            }
        }.execute();
    }

    @Test
    public void testSelectWithTarget2() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectWithTarget2.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final SLQueryResult result = query.executeTarget(SortMode.SORTED, false);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(45));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.security.BasicPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.security.Permission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
            }
        }.execute();
    }

    @Test
    public void testSelectWithTargetWithSelect() throws SLGraphSessionException, SLInvalidQuerySyntaxException {
        final String slqlInput = this.getResourceContent("SelectWithTargetWithSelect.slql");
        final SLQueryText query = session.createQueryText(slqlInput);

        final SLQueryResult result = query.executeTarget(SortMode.SORTED, false);
        final NodeWrapper[] wrappers = this.wrapNodes(result.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers.length, is(45));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Observable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSequentialList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimerTask"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Calendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.IdentityHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Timer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Currency"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Stack"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Random"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Vector"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.security.BasicPermission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.security",
                                           "java.security.Permission"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Date"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventListenerProxy"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ArrayList"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.StringTokenizer"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.WeakHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.EventObject"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.GregorianCalendar"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.HashSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Hashtable"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.LinkedHashMap"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Arrays"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Dictionary"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.SimpleTimeZone"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.BitSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.lang.Object"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Collections"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Locale"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.PropertyResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.TreeSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ListResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractCollection"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.ResourceBundle"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.AbstractSet"), isOneOf(wrappers));
                assertThat(new NodeWrapper(org.openspotlight.graph.test.domain.JavaClass.class.getName(), "java.util",
                                           "java.util.Properties"), isOneOf(wrappers));
            }
        }.execute();

        final SLQueryResult result2 = query.execute(new String[] {result.getNodes().get(20).getID()}, SortMode.SORTED, false);
        final NodeWrapper[] wrappers2 = this.wrapNodes(result2.getNodes());

        new AssertResult() {
            public void execute() {
                assertThat(wrappers2.length, is(40));
            }
        }.execute();

    }

}
