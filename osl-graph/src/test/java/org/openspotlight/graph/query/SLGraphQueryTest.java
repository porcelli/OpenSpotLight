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
import static org.hamcrest.Matchers.isOneOf;
import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.Files;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.common.util.StringBuilderUtil;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLMetadata;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeTypeNotInExistentHierarchy;
import org.openspotlight.graph.query.SLQuery.SortMode;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * The Class SLGraphQueryTest.
 * 
 * @author Vitor Hugo Chagas
 */
@Test
public class SLGraphQueryTest {

	/** The Constant LOGGER. */
	static final Logger LOGGER = Logger.getLogger(SLGraphQueryTest.class);
	
	/** The graph. */
	private SLGraph graph;
	
	/** The session. */
	private SLGraphSession session;
	
	/** The sort mode. */
	private SortMode sortMode = SortMode.NOT_SORTED;
	
	/** The print info. */
	private boolean printInfo = false;
	
	/**
	 * Instantiates a new sL graph query test.
	 */
	public SLGraphQueryTest() {}
	
	/**
	 * Instantiates a new sL graph query test.
	 * 
	 * @param session the session
	 * @param sortMode the sort mode
	 * @param printInfo the print info
	 */
	public SLGraphQueryTest(SLGraphSession session, SortMode sortMode, boolean printInfo) {
		this.session = session;
		this.sortMode = sortMode;
		this.printInfo = printInfo;
	}
	
	/**
	 * Populate graph.
	 */
	@BeforeClass
	public void populateGraph() {
		try {
		    SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            graph = factory.createTempGraph(false);
            session = graph.openSession();
            sortMode = SortMode.SORTED;

			SLMetadata metadata = session.getMetadata();
			if (metadata.findMetaNodeType(JavaType.class) != null) return;
			
			Collection<Class<?>> iFaces = loadClasses("java-util-interfaces.txt");
			Collection<Class<?>> classes = loadClasses("java-util-classes.txt");
			
			SLContext context = session.createContext("queryTest");
			SLNode root = context.getRootNode();
			
			Package pack = java.util.Date.class.getPackage();
			JavaPackage utilJavaPackage = root.addNode(JavaPackage.class, pack.getName());
			utilJavaPackage.setCaption(pack.getName());
			
			for (Class<?> iFace : iFaces) {
				JavaInterface javaInterface = utilJavaPackage.addNode(JavaInterface.class, iFace.getName());
				session.addLink(PackageContainsType.class, utilJavaPackage, javaInterface, false);
				javaInterface.setCaption(iFace.getName());
				addJavaInterfaceHirarchyLinks(root, iFace, javaInterface);
				addJavaInterfaceContainsJavaMethod(iFace, javaInterface);
			}
			
			for (Class<?> clazz : classes) {
//			    context = session.createContext("queryTest2");
	            root = context.getRootNode();
				JavaClass javaClass = utilJavaPackage.addNode(JavaClass.class, clazz.getName());
				session.addLink(PackageContainsType.class, utilJavaPackage, javaClass, false);
				javaClass.setCaption(clazz.getName());
				addJavaClassHirarchyLinks(root, clazz, javaClass);
				addClassImplementsInterfaceLinks(root, clazz, javaClass);
				addJavaClassContainsJavaClassMethod(clazz, javaClass);
			}
			
			session.save();
			session.close();
			
			session = graph.openSession();
		} 
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	   /**
   	 * Finish.
   	 */
    @AfterClass
    public void finish() {
        session.close();
        graph.shutdown();
    }
    

	/**
	 * Test select all packages.
	 */
	@Test
	public void testSelectAllPackages() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaPackage.class.getName())
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			new AssertResult() {
				public void execute() {
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaPackage.class.getName(), "queryTest", "java.security"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaPackage.class.getName(), "queryTest", "java.io"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaPackage.class.getName(), "queryTest", "java.util"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaPackage.class.getName(), "queryTest", "java.lang"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select all java interfaces.
	 */
	@Test
	public void testSelectAllJavaInterfaces() {

		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(19));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Cloneable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.lang.Iterable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Comparable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.EventListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.RandomAccess"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Enumeration"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Queue"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.io", "java.io.Serializable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Observer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Runnable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));			
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select all java classes.
	 */
	@Test
	public void testSelectAllJavaClasses() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaClass.class.getName())
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(45));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Observable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimerTask"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Timer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.security", "java.lang.Object"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Stack"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Random"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.security.BasicPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.security", "java.security.Permission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.StringTokenizer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Arrays"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.lang.Object"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractCollection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));			
				}
			}.execute();
			
			printResult(nodes);
			
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select all java types.
	 */
	@Test
	public void testSelectAllJavaTypes() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(64));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Observable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Cloneable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimerTask"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.security", "java.lang.Object"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Random"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Stack"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.security.BasicPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Observer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.lang.Iterable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.RandomAccess"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Enumeration"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Timer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.EventListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Comparable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Queue"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.security", "java.security.Permission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.io", "java.io.Serializable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Runnable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.StringTokenizer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Arrays"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.lang.Object"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractCollection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();

			printResult(nodes);

		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select types from java util package.
	 */
	@Test
	public void testSelectTypesFromJavaUtilPackage() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").startsWith().value("java.util")
					.typeEnd()
				.whereEnd();
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(55));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Observable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimerTask"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Random"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Stack"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Observer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.RandomAccess"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Enumeration"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Timer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.EventListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Queue"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.StringTokenizer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Arrays"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractCollection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();

			printResult(nodes);

		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select interfaces with set and classes with map.
	 */
	@Test
	public void testSelectInterfacesWithSetAndClassesWithMap() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").contains().value("Set")
					.typeEnd()
					.type(JavaClass.class.getName())
						.each().property("caption").contains().value("Map")
					.typeEnd()
				.whereEnd();
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(8));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));					
				}
			}.execute();
			
			printResult(nodes);

		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select types that contains set or list.
	 */
	@Test
	public void testSelectTypesThatContainsSetOrList() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").contains().value("Set")
						.or().each().property("caption").contains().value("List")
					.typeEnd()
				.whereEnd();
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(16));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.EventListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));					
				}
			}.execute();
			
			printResult(nodes);

		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select types from util with set list or map.
	 */
	@Test
	public void testSelectTypesFromUtilWithSetListOrMap() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").startsWith().value("java.util")
						.and()
							.openBracket()
								.each().property("caption").contains().value("Set")
								.or().each().property("caption").contains().value("List")
								.or().each().property("caption").contains().value("Map")
							.closeBracket()
					.typeEnd()
				.whereEnd();
							
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(24));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.EventListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);

		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select interfaces with set over types from util.
	 */
	@Test
	public void testSelectInterfacesWithSetOverTypesFromUtil() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").startsWith().value("java.util")
					.typeEnd()
				.whereEnd()
			
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").contains().value("Set")
					.typeEnd()
				.whereEnd();
							
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(2));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select interfaces with set over types from util with keep result.
	 */
	@Test
	public void testSelectInterfacesWithSetOverTypesFromUtilWithKeepResult() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").startsWith().value("java.util")
					.typeEnd()
				.whereEnd()
				
				.keepResult()
			
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").contains().value("Set")
					.typeEnd()
				.whereEnd();
							
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(55));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Observable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimerTask"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Random"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Stack"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Observer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.RandomAccess"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Enumeration"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Timer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.EventListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Queue"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.StringTokenizer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Arrays"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractCollection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));					
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select collection methods.
	 */
	@Test
	public void testSelectCollectionMethods() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.Collection")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(14));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "remove"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select collection methods with keep result.
	 */
	@Test
	public void testSelectCollectionMethodsWithKeepResult() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.keepResult()
				
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.Collection")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(15));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "remove"), isOneOf(wrappers));					
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select util types and colletion methods.
	 */
	@Test
	public void testSelectUtilTypesAndColletionMethods() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaPackage.class.getName()).comma()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName())
						.each().property("caption").equalsTo().value("java.util")
					.typeEnd()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.Collection")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaType.class.getName()).subTypes().comma()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b().comma()
					.byLink(PackageContainsType.class.getName()).b()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(73));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Observable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimerTask"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.security", "java.lang.Object"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Random"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Stack"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.security.BasicPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Observer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.lang.Iterable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.RandomAccess"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Enumeration"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Timer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.EventListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.security", "java.security.Permission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.StringTokenizer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Arrays"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.lang.Object"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractCollection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));					
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select types from methods with get.
	 */
	@Test
	public void testSelectTypesFromMethodsWithGet() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaTypeMethod.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaTypeMethod.class.getName())
						.each().property("caption").startsWith().value("get")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaType.class.getName()).subTypes().comma()
					.byLink(TypeContainsMethod.class.getName()).a()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(31));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select types from methods with get with keep result.
	 */
	@Test
	public void testSelectTypesFromMethodsWithGetWithKeepResult() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaTypeMethod.class.getName())
				.selectEnd()
				
				.keepResult()
				
				.where()
					.type(JavaTypeMethod.class.getName())
						.each().property("caption").startsWith().value("get")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaType.class.getName()).subTypes().comma()
					.byLink(TypeContainsMethod.class.getName()).a()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(169));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getMaximum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.LinkedList", "getLast"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getSetStateFields"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getLastJulianDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getDisplayVariantArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getTime"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SimpleTimeZone", "getTransition"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getSeconds"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getMillisOf"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeMap", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getMonth"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getTimeImpl"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getJulianCalendarSystem"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getCalendarSystem"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SimpleTimeZone", "getStart"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getClassContext"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getActualMinimum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getTime"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.HashMap", "getEntry"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getRawOffset"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Hashtable", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getLeastMaximum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getCurrentFixedDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getJulianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getGreatestMinimum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getMinutes"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getCountry"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SimpleTimeZone", "getOffset"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SimpleTimeZone", "getDSTSavings"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SimpleTimeZone", "getEnd"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ArrayList", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getYear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getSystemGMTOffsetID"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Dictionary", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getLoader"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getFixedDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getDisplayLanguage"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getWeekNumber"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getDefaultRef"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getTimezoneOffset"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getTimeInMillis"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getActualMaximum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getISO3Country"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getCutoverCalendarSystem"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getFieldName"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.LinkedList", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getDSTSavings"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.IdentityHashMap", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getMinimum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getISOCountries"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Currency", "getCurrencyCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.WeakHashMap", "getEntry"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.PropertyResourceBundle", "getKeys"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getMinimalDaysInFirstWeek"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getVariant"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.LinkedList", "getFirst"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.HashMap", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListResourceBundle", "getContents"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getDefault"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.WeakHashMap", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getActualMinimum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Currency", "getMainTableEntry"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getLeastMaximum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeMap", "getEntry"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collections", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.PropertyPermission", "getMask"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getAvailableIDs"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getFixedDateMonth1"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getLocale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getOffsets"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.BitSet", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Currency", "getDefaultFractionDigits"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeMap", "getPrecedingEntry"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeMap", "getCeilEntry"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getOffset"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Currency", "getSymbol"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getAvailableLocales"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getActualMaximum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getKeys"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getISO3Language"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getCalendarDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getDefault"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListResourceBundle", "getKeys"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getRolledValue"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getStringArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Hashtable", "getIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getISOLanguages"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.AbstractList", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getFirstDayOfWeek"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getGregorianCutoverDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getDisplayName"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.EventObject", "getSource"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.HashMap", "getForNullKey"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getDisplayName"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getYearOffsetInMillis"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getGregorianChange"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getMinimum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getAvailableLocales"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Hashtable", "getEnumeration"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Properties", "getProperty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.WeakHashMap", "getTable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.PropertyPermission", "getActions"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.LinkedHashMap", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.AbstractMap", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.AbstractSequentialList", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getDisplayCountry"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getDay"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getDisplayVariant"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getNormalizedCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getCalendarDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getGreatestMinimum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.EventListenerProxy", "getListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getString"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getMaximum"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getID"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getSystemTimeZoneID"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getHours"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SimpleTimeZone", "getRawOffset"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SimpleTimeZone", "getOffsets"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Locale", "getLanguage"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Currency", "getInstance"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getInstance"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.BitSet", "getBits"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getMillisOf"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getBundleImpl"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TimeZone", "getDisplayNames"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ResourceBundle", "getObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Vector", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.GregorianCalendar", "getFixedDateJan1"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select by link with any side.
	 */
	@Test
	public void testSelectByLinkWithAnySide() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName()).comma()
					.type(JavaTypeMethod.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaTypeMethod.class.getName())
						.each().property("caption").startsWith().value("get")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaInterface.class.getName()).comma()
					.type(JavaTypeMethod.class.getName()).subTypes().comma()
					.byLink(TypeContainsMethod.class.getName()).any()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(122));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "indexOf"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Observer", "update"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "lastIndexOf"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Comparator", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "subList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "hasNext"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "previous"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "firstKey"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "previousIndex"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "headSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "lastKey"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "subSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "put"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "nextIndex"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "values"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "tailMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "first"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Iterator", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Enumeration", "hasMoreElements"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Iterator", "hasNext"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "next"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "subMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "containsValue"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Comparator", "compare"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "hasPrevious"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "headMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "containsKey"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "listIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "last"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Iterator", "next"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Enumeration", "nextElement"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "tailSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "putAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "equals"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select by link with any side with keep result.
	 */
	@Test
	public void testSelectByLinkWithAnySideWithKeepResult() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName()).comma()
					.type(JavaTypeMethod.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaTypeMethod.class.getName())
						.each().property("caption").startsWith().value("get")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaInterface.class.getName()).comma()
					.type(JavaTypeMethod.class.getName()).subTypes().comma()
					.byLink(TypeContainsMethod.class.getName()).any()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(122));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "indexOf"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Observer", "update"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Currency"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "lastIndexOf"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Comparator", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "subList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "hasNext"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Hashtable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.SimpleTimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "previous"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "firstKey"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Properties"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "previousIndex"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "get"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "headSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "lastKey"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Vector"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Date"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "subSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Dictionary"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "put"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "nextIndex"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "values"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "tailMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "first"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Iterator", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyPermission"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Enumeration", "hasMoreElements"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TimeZone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Iterator", "hasNext"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.EventListenerProxy"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "next"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "addAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ArrayList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.WeakHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "subMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "containsValue"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Comparator", "compare"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "clear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.ListIterator", "hasPrevious"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "contains"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedMap", "headMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "containsKey"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "listIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "last"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "retainAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Calendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.IdentityHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Iterator", "next"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "removeAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "isEmpty"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "containsAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Enumeration", "nextElement"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.List", "remove"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collection", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.GregorianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "add"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "toArray"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "tailSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "size"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "putAll"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Collections"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.Locale"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.PropertyResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Set", "equals"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select date methods with tag lesser or equal to50.
	 */
	@Test
	public void testSelectDateMethodsWithTagLesserOrEqualTo50() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.Date")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b()
				.selectEnd()
				
				.where()
					.linkType(TypeContainsMethod.class.getName())
						.each().property("tag").lesserOrEqualThan().value(50)
					.linkTypeEnd()
				.whereEnd();
					
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();

			new AssertResult() {
				public void execute() {
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select date methods with tag greater than50.
	 */
	@Test
	public void testSelectDateMethodsWithTagGreaterThan50() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.Date")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b()
				.selectEnd()
				
				.where()
					.linkType(TypeContainsMethod.class.getName())
						.each().property("tag").greaterThan().value(50)
					.linkTypeEnd()
				.whereEnd();
					
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();

			new AssertResult() {
				public void execute() {
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select all date methods.
	 */
	@Test
	public void testSelectAllDateMethods() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.Date")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();

			new AssertResult() {
				public void execute() {
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select date methods with tag lesser or equal to30 or greater or equal to70.
	 */
	@Test
	public void testSelectDateMethodsWithTagLesserOrEqualTo30OrGreaterOrEqualTo70() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.Date")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b()
				.selectEnd()
				
				.where()
					.linkType(TypeContainsMethod.class.getName())
						.each().property("tag").lesserOrEqualThan().value(30)
						.or().each().property("tag").greaterOrEqualThan().value(70)
					.linkTypeEnd()
				.whereEnd();
					
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();

			new AssertResult() {
				public void execute() {
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select date methods with tag between30 and70.
	 */
	@Test
	public void testSelectDateMethodsWithTagBetween30And70() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaType.class.getName()).subTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.Date")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b()
				.selectEnd()
				
				.where()
					.linkType(TypeContainsMethod.class.getName())
						.each().property("tag").greaterThan().value(30)
						.and().each().property("tag").lesserThan().value(70)
					.linkTypeEnd()
				.whereEnd();
					
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();

			new AssertResult() {
				public void execute() {
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select sorted set hierarchy level1.
	 */
	@Test
	public void testSelectSortedSetHierarchyLevel1() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.SortedSet")
					.typeEnd()
				.whereEnd()
				
				.keepResult()
				
				.select()
					.type(JavaInterface.class.getName()).comma()
					.byLink(JavaInterfaceHierarchy.class.getName()).b()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(2));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select sorted set hierarchy level2.
	 */
	@Test
	public void testSelectSortedSetHierarchyLevel2() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.SortedSet")
					.typeEnd()
				.whereEnd()
				
				.keepResult()
				
				.select()
					.type(JavaInterface.class.getName()).comma()
					.byLink(JavaInterfaceHierarchy.class.getName()).b()
				.selectEnd()
				
				.keepResult()

				.select()
					.type(JavaInterface.class.getName()).comma()
					.byLink(JavaInterfaceHierarchy.class.getName()).b()
				.selectEnd();

			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(3));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select sorted set hierarchy level3.
	 */
	@Test
	public void testSelectSortedSetHierarchyLevel3() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.SortedSet")
					.typeEnd()
				.whereEnd()

				.keepResult()
				
				.select()
					.type(JavaInterface.class.getName()).comma()
					.byLink(JavaInterfaceHierarchy.class.getName()).b()
				.selectEnd()
				
				.keepResult()

				.select()
					.type(JavaInterface.class.getName()).comma()
					.byLink(JavaInterfaceHierarchy.class.getName()).b()
				.selectEnd()
				
				.keepResult()

				.select()
					.type(JavaInterface.class.getName()).comma()
					.byLink(JavaInterfaceHierarchy.class.getName()).b()
				.selectEnd();

			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(4));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.lang.Iterable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select sorted set hierarchy execute3 times.
	 */
	@Test
	public void testSelectSortedSetHierarchyExecute3Times() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.SortedSet")
					.typeEnd()
				.whereEnd()
				
				.keepResult()
				
				.select()
					.type(JavaInterface.class.getName()).comma()
					.byLink(JavaInterfaceHierarchy.class.getName()).b()
				.selectEnd()
				
				.keepResult()
				
				.executeXTimes(3);

			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(4));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.lang.Iterable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select sorted set hierarchy execute x times.
	 */
	@Test
	public void testSelectSortedSetHierarchyExecuteXTimes() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.type(JavaInterface.class.getName())
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName()).subTypes()
						.each().property("caption").equalsTo().value("java.util.SortedSet")
					.typeEnd()
				.whereEnd()
				
				.keepResult()
				
				.select()
					.type(JavaInterface.class.getName()).comma()
					.byLink(JavaInterfaceHierarchy.class.getName()).b()
				.selectEnd()
				
				.keepResult()
				
				.executeXTimes(3);

			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(4));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.lang.Iterable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select all types.
	 */
	@Test
	public void testSelectAllTypes() {
		
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes()
				.selectEnd()
				
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").contains().value("Set")
					.typeEnd()
					.type(JavaTypeMethod.class.getName())
						.each().property("caption").contains().value("Set")
					.typeEnd()
				.whereEnd();

			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			//final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			//printAsserts(wrappers);
			
			new AssertResult() {
				public void execute() {
				}
			}.execute();

			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select all types on where.
	 */
	@Test
	public void testSelectAllTypesOnWhere() {
		
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").contains().value("Set")
					.typeEnd()
					.type(JavaTypeMethod.class.getName())
						.each().property("caption").contains().value("Set")
					.typeEnd()
				.whereEnd();

			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(37));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "isExternallySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.AbstractMap", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "getSetStateFields"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collections", "checkedSortedSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.WeakHashMap", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "headSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collections", "synchronizedSortedSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collections", "unmodifiableSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeMap", "readTreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Hashtable", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collections", "synchronizedSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.HashMap", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.IdentityHashMap", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "isFieldSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "isSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.AbstractMap", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeMap", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "subSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.SortedSet", "tailSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.IdentityHashMap", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.HashMap", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Calendar", "internalSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collections", "checkedSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeSet", "headSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.BitSet", "nextSetBit"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Map", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.WeakHashMap", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Hashtable", "keySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeMap", "entrySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeSet", "tailSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeSet", "subSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collections", "unmodifiableSortedSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Collections", "emptySet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.TreeMap", "addAllForTreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();

			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
		
	/**
	 * Test not relational operator.
	 */
	@Test
	public void testNotRelationalOperator() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()

				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").not().contains().value("Set")
					.typeEnd()
				.whereEnd();

			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(17));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Cloneable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.lang.Iterable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Map"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Iterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedMap"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Comparable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.EventListener"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.RandomAccess"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Enumeration"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Comparator"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Queue"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.io", "java.io.Serializable"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.List"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Observer"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.lang", "java.lang.Runnable"), isOneOf(wrappers));
				}
			}.execute();

			printResult(nodes);
			
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test not conditional operator.
	 */
	@Test
	public void testNotConditionalOperator() {
		
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()

				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").contains().value("Set")
						.and().not()
							.openBracket()
								.each().property("caption").contains().value("Hash")
								.or().each().property("caption").contains().value("Bit")
							.closeBracket()
					.typeEnd()
				.whereEnd();

			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			
			final NodeWrapper[] wrappers = wrapNodes(nodes);

			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(4));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), isOneOf(wrappers));
				}
			}.execute();

			printResult(nodes);
			
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select date methods.
	 */
	@Test
	public void testSelectDateMethods() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				
				.where()
					.type(JavaClass.class.getName())
						.each().property("caption").equalsTo().value("java.util.Date")
					.typeEnd()
				.whereEnd()
				
				.select()
					.type(JavaTypeMethod.class.getName()).comma()
					.byLink(TypeContainsMethod.class.getName()).b()
				.selectEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(36));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "equals"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "toGMTString"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "setDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "setSeconds"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getSeconds"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getTimezoneOffset"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getDay"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getMonth"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getTimeImpl"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "convertToAbbr"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "setHours"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getCalendarSystem"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getCalendarDate"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "setYear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "after"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "toLocaleString"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "readObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getHours"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getTime"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "setMonth"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "UTC"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "setTime"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getJulianCalendar"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "hashCode"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getMinutes"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getMillisOf"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "before"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "setMinutes"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "writeObject"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "parse"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "getYear"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "toString"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "compareTo"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "clone"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaTypeMethod.class.getName(), "java.util.Date", "normalize"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select by link count.
	 */
	@Test
	public void testSelectByLinkCount() {
		try {

			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").contains().value("Set")
						.or().each().property("caption").contains().value("List")
						.or().each().property("caption").contains().value("Map")
					.typeEnd()
				.whereEnd()
				
				.select()
					.allTypes()
				.selectEnd()
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").not().contains().value("Sorted")
						.and().each().link(TypeContainsMethod.class.getName()).a().count().greaterThan().value(3)
						.and().each().link(TypeContainsMethod.class.getName()).a().count().lesserOrEqualThan().value(12)
					.typeEnd()
				.whereEnd();
				
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(4));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSequentialList"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.ListResourceBundle"), isOneOf(wrappers));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.ListIterator"), isOneOf(wrappers));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select order by ascending.
	 */
	@Test
	public void testSelectOrderByAscending() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").contains().value("Set")
					.typeEnd()
				.whereEnd()
				.orderBy()
					.type(JavaInterface.class.getName()).property("caption").ascending()
				.orderByEnd();
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(2));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), is(wrappers[0]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), is(wrappers[1]));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Test select order by descending.
	 */
	@Test
	public void testSelectOrderByDescending() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaClass.class.getName())
						.each().property("caption").contains().value("Set")
					.typeEnd()
				.whereEnd()
				.orderBy()
					.type(JavaClass.class.getName()).property("caption").descending()
				.orderByEnd();
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(5));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), is(wrappers[0]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), is(wrappers[1]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), is(wrappers[2]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), is(wrappers[3]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), is(wrappers[4]));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	@Test
	public void testSelectOrderByAscendingAndDescending() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").contains().value("Set")
					.typeEnd()
				.whereEnd()
				.orderBy()
					.type(JavaInterface.class.getName()).property("caption").ascending()
					.type(JavaClass.class.getName()).property("caption").descending()
				.orderByEnd();
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(7));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), is(wrappers[0]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), is(wrappers[1]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), is(wrappers[2]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), is(wrappers[3]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), is(wrappers[4]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), is(wrappers[5]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), is(wrappers[6]));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectOrderByCrossType() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaType.class.getName()).subTypes()
						.each().property("caption").contains().value("Set")
					.typeEnd()
				.whereEnd()
				.orderBy()
					.type(JavaType.class.getName()).property("caption")
				.orderByEnd();
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			new AssertResult() {
				public void execute() {
					assertThat(wrappers.length, is(7));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.BitSet"), is(wrappers[0]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.LinkedHashSet"), is(wrappers[1]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.TreeSet"), is(wrappers[2]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Set"), is(wrappers[3]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.HashSet"), is(wrappers[4]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaClass.class.getName(), "java.util", "java.util.AbstractSet"), is(wrappers[5]));
					assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.SortedSet"), is(wrappers[6]));
				}
			}.execute();
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}


	/**
	 * Test select collator primary change accent.
	 */
	@Test
	public void testSelectByCollatorKeyPrimaryChangeAccent() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.Çollection")
					.typeEnd()
				.whereEnd()
				.collator(Collator.PRIMARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(1));
			assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), is(wrappers[0]));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorKeyPrimaryChangeCase() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.CollecTION")
					.typeEnd()
				.whereEnd()
				.collator(Collator.PRIMARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(1));
			assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), is(wrappers[0]));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorKeyPrimaryChangeAccentAndCase() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.ÇollécTION")
					.typeEnd()
				.whereEnd()
				.collator(Collator.PRIMARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(1));
			assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), is(wrappers[0]));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Test select collator primary change accent.
	 */
	@Test
	public void testSelectByCollatorKeySecondaryChangeAccent() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.Çollection")
					.typeEnd()
				.whereEnd()
				.collator(Collator.SECONDARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(0));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorKeySecondaryChangeCase() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.CollecTION")
					.typeEnd()
				.whereEnd()
				.collator(Collator.SECONDARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(1));
			assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), is(wrappers[0]));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorKeySecondaryChangeAccentAndCase() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.ÇollécTION")
					.typeEnd()
				.whereEnd()
				.collator(Collator.SECONDARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(0));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorKeyTertiaryChangeAccent() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.Çollection")
					.typeEnd()
				.whereEnd()
				.collator(Collator.TERTIARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(0));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorKeyTertiaryChangeCase() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.CollecTION")
					.typeEnd()
				.whereEnd()
				.collator(Collator.TERTIARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(0));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorKeyTertiaryChangeAccentAndCase() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").equalsTo().value("java.util.ÇollécTION")
					.typeEnd()
				.whereEnd()
				.collator(Collator.TERTIARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(0));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	@Test
	public void testSelectByCollatorDescriptionPrimary() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").contains().value("Çollection")
					.typeEnd()
				.whereEnd()
				.collator(Collator.PRIMARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(1));
			assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), is(wrappers[0]));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorDescriptionSecondary() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").contains().value("CollecTION")
					.typeEnd()
				.whereEnd()
				.collator(Collator.SECONDARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(1));
			assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), is(wrappers[0]));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	@Test
	public void testSelectByCollatorDescriptionTertiary() {
		try {
			
			SLQuery query = session.createQuery();
			
			query
				.select()
					.allTypes().onWhere()
				.selectEnd()
				.where()
					.type(JavaInterface.class.getName())
						.each().property("caption").contains().value("çollecTION")
					.typeEnd()
				.whereEnd()
				.collator(Collator.TERTIARY);
			
			SLQueryResult result = query.execute(sortMode, printInfo);
			Collection<SLNode> nodes = result.getNodes();
			final NodeWrapper[] wrappers = wrapNodes(nodes);
			
			assertThat(wrappers.length, is(1));
			assertThat(new NodeWrapper(org.openspotlight.graph.query.JavaInterface.class.getName(), "java.util", "java.util.Collection"), is(wrappers[0]));
			
			printResult(nodes);
		}
		catch (SLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args the arguments
	 */
	@Test(enabled=false)
	public static void main(String[] args) {
		try {
			int count = 0;
			Map<Integer, Method> methodMap = new TreeMap<Integer, Method>();
			Method[] methods = SLGraphQueryTest.class.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("test") && method.getAnnotation(Test.class) != null) {
					methodMap.put(++count, method);
				}
			}
			SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
			SLGraph graph = factory.createTempGraph(false);
			SLGraphSession session = graph.openSession();
			SLGraphQueryTest test = new SLGraphQueryTest(session, SortMode.SORTED, true);
			int option = 0;
			Scanner in = new Scanner(System.in);
			do {
				LOGGER.info("Menu:");
				LOGGER.info("0: exit");
				for (Entry<Integer, Method> entry : methodMap.entrySet()) {
					LOGGER.info(entry.getKey() + ": " + entry.getValue().getName());
				}
				LOGGER.info("Enter any option: ");
				option = in.nextInt();
				if (option > 0 && option <= methodMap.size()) {
					Method method = methodMap.get(option);
					method.invoke(test, new Object[] {});
				}
			}
			while (option != 0);
			LOGGER.info("bye!");
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Wrap nodes.
	 * 
	 * @param nodes the nodes
	 * 
	 * @return the node wrapper[]
	 */
	private NodeWrapper[] wrapNodes(Collection<SLNode> nodes) {
		Set<NodeWrapper> wrappers = new HashSet<NodeWrapper>();
		for (SLNode node : nodes) {
			NodeWrapper wrapper = new NodeWrapper(node);
			wrappers.add(wrapper);
		}
		return wrappers.toArray(new NodeWrapper[] {});
	}
	
	/**
	 * Prints the asserts.
	 * 
	 * @param wrappers the wrappers
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	void printAsserts(NodeWrapper[] wrappers) throws SLGraphSessionException {
		StringBuilder buffer = new StringBuilder();
		StringBuilderUtil.append(buffer, '\n', "assertThat(wrappers.length, is(", wrappers.length, "));", '\n');
		for (int i = 0; i < wrappers.length; i++) {
			NodeWrapper wrapper = wrappers[i];
			String pattern = "assertThat(new NodeWrapper(${typeName}.class.getName(), \"${parentName}\", \"${name}\"), isOneOf(wrappers));";
			pattern = StringUtils.replace(pattern, "${typeName}", wrapper.getTypeName());
			pattern = StringUtils.replace(pattern, "${parentName}", wrapper.getParentName());
			pattern = StringUtils.replace(pattern, "${name}", wrapper.getName());
			buffer.append(pattern).append('\n');
		}
		buffer.append('\n');
		LOGGER.info(buffer);
	}

	/**
	 * Prints the asserts in order.
	 * 
	 * @param wrappers the wrappers
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	void printAssertsInOrder(NodeWrapper[] wrappers) throws SLGraphSessionException {
		StringBuilder buffer = new StringBuilder();
		StringBuilderUtil.append(buffer, '\n', "assertThat(wrappers.length, is(", wrappers.length, "));", '\n');
		for (int i = 0; i < wrappers.length; i++) {
			NodeWrapper wrapper = wrappers[i];
			String pattern = "assertThat(new NodeWrapper(${typeName}.class.getName(), \"${parentName}\", \"${name}\"), is(wrappers[${index}]));";
			pattern = StringUtils.replace(pattern, "${typeName}", wrapper.getTypeName());
			pattern = StringUtils.replace(pattern, "${parentName}", wrapper.getParentName());
			pattern = StringUtils.replace(pattern, "${name}", wrapper.getName());
			pattern = StringUtils.replace(pattern, "${index}", "" + i);
			buffer.append(pattern).append('\n');
		}
		buffer.append('\n');
		LOGGER.info(buffer);
	}
	
	/**
	 * Prints the result.
	 * 
	 * @param nodes the nodes
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void printResult(Collection<SLNode> nodes) throws SLGraphSessionException {
		if (printInfo && !nodes.isEmpty()) {
			StringBuilder buffer = new StringBuilder();
			StringBuilderUtil.append(buffer, "\n\nRESULTS (", nodes.size(), "):\n");
			for (SLNode node : nodes) {
				StringBuilderUtil.append(buffer, StringUtils.rightPad(node.getTypeName(), 60), StringUtils.rightPad(node.getName(), 60), node.getParent().getName(), '\n');
			}
			LOGGER.info(buffer);
		}
	}
	
	/**
	 * Adds the java interface hirarchy links.
	 * 
	 * @param root the root
	 * @param iFace the i face
	 * @param javaInterface the java interface
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void addJavaInterfaceHirarchyLinks(SLNode root, Class<?> iFace, JavaInterface javaInterface) throws SLGraphSessionException {
		Class<?>[] superIFaces = iFace.getInterfaces();
		for (Class<?> superIFace : superIFaces) {
			Package iFacePack = iFace.getPackage();
			JavaPackage javaPackage = root.addNode(JavaPackage.class, iFacePack.getName());
			javaPackage.setCaption(iFacePack.getName());
			JavaInterface superJavaInterface = javaPackage.addNode(JavaInterface.class, superIFace.getName());
			session.addLink(PackageContainsType.class, javaPackage, superJavaInterface, false);
			superJavaInterface.setCaption(superIFace.getName());
			session.addLink(JavaInterfaceHierarchy.class, javaInterface, superJavaInterface, false);
			addJavaInterfaceHirarchyLinks(root, superIFace, superJavaInterface);
		}
	}
	
	/**
	 * Adds the java class hirarchy links.
	 * 
	 * @param root the root
	 * @param clazz the clazz
	 * @param javaClass the java class
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void addJavaClassHirarchyLinks(SLNode root, Class<?> clazz, JavaClass javaClass) throws SLGraphSessionException {
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			Package classPack = clazz.getPackage();
			JavaPackage javaPackage = root.addNode(JavaPackage.class, classPack.getName());
			javaPackage.setCaption(classPack.getName());
			JavaClass superJavaClass = javaPackage.addNode(JavaClass.class, superClass.getName());
			session.addLink(PackageContainsType.class, javaPackage, superJavaClass, false);
			session.addLink(JavaClassHierarchy.class, javaClass, superJavaClass, false);
			addJavaClassHirarchyLinks(root, superClass, superJavaClass);
		}
	}
	
	/**
	 * Adds the class implements interface links.
	 * 
	 * @param root the root
	 * @param clazz the clazz
	 * @param javaClass the java class
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void addClassImplementsInterfaceLinks(SLNode root, Class<?> clazz, JavaClass javaClass) throws SLGraphSessionException {
		Class<?>[] iFaces = clazz.getInterfaces();
		for (Class<?> iFace : iFaces) {
			Package iFacePack = iFace.getPackage();
			JavaPackage javaPackage = root.addNode(JavaPackage.class, iFacePack.getName());
			javaPackage.setCaption(iFacePack.getName());
			JavaInterface javaInterface = javaPackage.addNode(JavaInterface.class, iFace.getName());
			javaInterface.setCaption(iFace.getName());
			ClassImplementsInterface link = session.addLink(ClassImplementsInterface.class, javaClass, javaInterface, false);
			link.setTag(randomTag());
		}
	}
	
	/**
	 * Adds the java class contains java class method.
	 * 
	 * @param clazz the clazz
	 * @param javaClass the java class
	 * 
	 * @throws SLNodeTypeNotInExistentHierarchy the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void addJavaClassContainsJavaClassMethod(Class<?> clazz, JavaClass javaClass) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			JavaTypeMethod javaTypeMethod = javaClass.addNode(JavaTypeMethod.class, methods[i].getName());
			javaTypeMethod.setCaption(methods[i].getName());
			TypeContainsMethod link = session.addLink(TypeContainsMethod.class, javaClass, javaTypeMethod, false);
			link.setTag(randomTag());
		}
	}

	/**
	 * Adds the java interface contains java method.
	 * 
	 * @param iFace the i face
	 * @param javaInterface the java interface
	 * 
	 * @throws SLNodeTypeNotInExistentHierarchy the SL node type not in existent hierarchy
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	private void addJavaInterfaceContainsJavaMethod(Class<?> iFace, JavaInterface javaInterface) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
		Method[] methods = iFace.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			JavaTypeMethod javaTypeMethod = javaInterface.addNode(JavaTypeMethod.class, methods[i].getName());
			javaTypeMethod.setCaption(methods[i].getName());
			TypeContainsMethod link = session.addLink(TypeContainsMethod.class, javaInterface, javaTypeMethod, false);
			link.setTag(randomTag());
		}
	}
	
	/**
	 * Load classes.
	 * 
	 * @param fileName the file name
	 * 
	 * @return the collection< class<?>>
	 * 
	 * @throws SLException the SL exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	private Collection<Class<?>> loadClasses(String fileName) throws SLException, IOException, ClassNotFoundException {
		Collection<Class<?>> classes = new ArrayList<Class<?>>();
		String packagePath = SLGraphQueryTest.class.getPackage().getName().replace('.', '/');
		String filePath = packagePath + '/' + fileName;
		InputStream inputStream = getResourceFromClassPath(filePath);
		Collection<String> names = Files.readLines(inputStream);
		inputStream.close();
		for (String name : names) {
			String className = "java.util.".concat(name).trim();
			Class<?> clazz = Class.forName(className);
			classes.add(clazz);
		}
		return classes;
	}
	
	/**
	 * Random tag.
	 * 
	 * @return the int
	 */
	private int randomTag() {
		return (int) Math.round(Math.random() * 100.0);
	}
	
	/**
	 * The Class NodeWrapper.
	 * 
	 * @author Vitor Hugo Chagas
	 */
	class NodeWrapper {
		
		/** The node. */
		private SLNode node;
		
		/** The type name. */
		private String typeName;
		
		/** The name. */
		private String name;
		
		/** The parent name. */
		private String parentName;
		
		/**
		 * Instantiates a new node wrapper.
		 * 
		 * @param typeName the type name
		 * @param parentName the parent name
		 * @param name the name
		 */
		public NodeWrapper(String typeName, String parentName, String name) {
			this.typeName = typeName;
			this.parentName = parentName;
			this.name = name;
		}
		
		/**
		 * Instantiates a new node wrapper.
		 * 
		 * @param node the node
		 */
		public NodeWrapper(SLNode node) {
			this.node = node;
		}
		
		/**
		 * Gets the type name.
		 * 
		 * @return the type name
		 * 
		 * @throws SLGraphSessionException the SL graph session exception
		 */
		public String getTypeName() throws SLGraphSessionException {
			if (typeName == null) {
				typeName = node.getTypeName();
			}
			return typeName;
		}
		
		/**
		 * Sets the type name.
		 * 
		 * @param typeName the new type name
		 */
		public void setTypeName(String typeName) {
			this.typeName = typeName;
		}
		
		/**
		 * Gets the name.
		 * 
		 * @return the name
		 * 
		 * @throws SLGraphSessionException the SL graph session exception
		 */
		public String getName() throws SLGraphSessionException {
			if (name == null) {
				name = node.getName();
			}
			return name;
		}
		
		/**
		 * Sets the name.
		 * 
		 * @param name the new name
		 */
		public void setName(String name) {
			this.name = name;
		}
		
		/**
		 * Gets the parent name.
		 * 
		 * @return the parent name
		 * 
		 * @throws SLGraphSessionException the SL graph session exception
		 */
		public String getParentName() throws SLGraphSessionException {
			if (parentName == null) {
				parentName = node.getParent().getName();
			}
			return parentName;
		}
		
		/**
		 * Sets the parent name.
		 * 
		 * @param parentName the new parent name
		 */
		public void setParentName(String parentName) {
			this.parentName = parentName;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return hashCode() == obj.hashCode();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			try {
				return HashCodes.hashOf(getTypeName(), getParentName(), getName());
			}
			catch (SLGraphSessionException e) {
				throw new SLRuntimeException(e);
			}
		}
	}
}


