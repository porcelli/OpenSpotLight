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

import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.openspotlight.graph.test.domain.ClassImplementsInterface;
import org.openspotlight.graph.test.domain.JavaClass;
import org.openspotlight.graph.test.domain.JavaClassHierarchy;
import org.openspotlight.graph.test.domain.JavaInterface;
import org.openspotlight.graph.test.domain.JavaInterfaceHierarchy;
import org.openspotlight.graph.test.domain.JavaPackage;
import org.openspotlight.graph.test.domain.JavaType;
import org.openspotlight.graph.test.domain.JavaTypeMethod;
import org.openspotlight.graph.test.domain.PackageContainsType;
import org.openspotlight.graph.test.domain.TypeContainsMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class AbstractGeneralQueryTest {

    /** The LOGGER. */
    protected static Logger  LOGGER;
    /** The graph. */
    protected SLGraph        graph;
    /** The session. */
    protected SLGraphSession session;
    /** The sort mode. */
    protected SortMode       sortMode  = SortMode.NOT_SORTED;
    /** The print info. */
    protected boolean        printInfo = false;

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
                //              context = session.createContext("queryTest2");
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
        } catch (Exception e) {
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
     * Wrap nodes.
     * 
     * @param nodes the nodes
     * @return the node wrapper[]
     */
    protected NodeWrapper[] wrapNodes( List<SLNode> nodes ) {
        NodeWrapper[] wrappers = new NodeWrapper[nodes.size()]; 
        
        for (int i = 0; i < wrappers.length; i++) {
            wrappers[i] = new NodeWrapper(nodes.get(i));
        }
        return wrappers;
    }

    /**
     * Prints the asserts.
     * 
     * @param wrappers the wrappers
     * @throws SLGraphSessionException the SL graph session exception
     */
    void printAsserts( NodeWrapper[] wrappers ) throws SLGraphSessionException {
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
     * @throws SLGraphSessionException the SL graph session exception
     */
    void printAssertsInOrder( NodeWrapper[] wrappers ) throws SLGraphSessionException {
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
     * @throws SLGraphSessionException the SL graph session exception
     */
    protected void printResult( Collection<SLNode> nodes ) throws SLGraphSessionException {
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
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaInterfaceHirarchyLinks( SLNode root,
                                                Class<?> iFace,
                                                JavaInterface javaInterface ) throws SLGraphSessionException {
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
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaClassHirarchyLinks( SLNode root,
                                            Class<?> clazz,
                                            JavaClass javaClass ) throws SLGraphSessionException {
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
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addClassImplementsInterfaceLinks( SLNode root,
                                                   Class<?> clazz,
                                                   JavaClass javaClass ) throws SLGraphSessionException {
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
     * @throws SLNodeTypeNotInExistentHierarchy the SL node type not in existent hierarchy
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaClassContainsJavaClassMethod( Class<?> clazz,
                                                      JavaClass javaClass )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
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
     * @throws SLNodeTypeNotInExistentHierarchy the SL node type not in existent hierarchy
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaInterfaceContainsJavaMethod( Class<?> iFace,
                                                     JavaInterface javaInterface )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
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
     * @return the collection< class<?>>
     * @throws SLException the SL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    private Collection<Class<?>> loadClasses( String fileName ) throws SLException, IOException, ClassNotFoundException {
        Collection<Class<?>> classes = new ArrayList<Class<?>>();
        String packagePath = AbstractGeneralQueryTest.class.getPackage().getName().replace('.', '/');
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
        return (int)Math.round(Math.random() * 100.0);
    }

    /**
     * The Class NodeWrapper.
     * 
     * @author Vitor Hugo Chagas
     */
    public class NodeWrapper {

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
        public NodeWrapper(
                            String typeName, String parentName, String name ) {
            this.typeName = typeName;
            this.parentName = parentName;
            this.name = name;
        }

        /**
         * Instantiates a new node wrapper.
         * 
         * @param node the node
         */
        public NodeWrapper(
                            SLNode node ) {
            this.node = node;
        }

        /**
         * Gets the type name.
         * 
         * @return the type name
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
        public void setTypeName( String typeName ) {
            this.typeName = typeName;
        }

        /**
         * Gets the name.
         * 
         * @return the name
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
        public void setName( String name ) {
            this.name = name;
        }

        /**
         * Gets the parent name.
         * 
         * @return the parent name
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
        public void setParentName( String parentName ) {
            this.parentName = parentName;
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( Object obj ) {
            return hashCode() == obj.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            try {
                return HashCodes.hashOf(getTypeName(), getParentName(), getName());
            } catch (SLGraphSessionException e) {
                throw new SLRuntimeException(e);
            }
        }
    }

    /**
     * Gets the resource content.
     * 
     * @param resourceName the resource name
     * @return the resource content
     */
    protected String getResourceContent( final String resourceName ) {
        try {
            final InputStream in = getClass().getResourceAsStream(resourceName);
            final Reader reader = new InputStreamReader(in);

            final StringBuilder text = new StringBuilder();

            final char[] buf = new char[1024];
            int len = 0;

            while ((len = reader.read(buf)) >= 0) {
                text.append(buf,
                            0,
                            len);
            }

            return text.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
