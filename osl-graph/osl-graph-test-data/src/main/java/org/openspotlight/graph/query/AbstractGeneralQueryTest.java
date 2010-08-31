/**
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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.Files;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.common.util.StringBuilderUtil;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.GraphReaderpotlight.graph.meta.SLMetadata;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.query.SLQuery.SortMode;
import org.openspotlight.graph.test.domain.link.*;
import org.openspotlight.graph.test.domain.node.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;

public abstract class AbstractGeneralQueryTest {

    protected abstract Callable<Void> createStartUpHandler();

    protected abstract Callable<Void> createShutdownHandler();

    protected abstract GraphReadGraphReadGraphReader    private static Callable<Void> shutdownHandler;

    private boolean didItRun = false;


    protected boolean appearsAfter(NodeWrapper[] wrappers, String firstType, String firstStr, String afterType, String afterStr) {
        return appearsAfter(wrappers, firstType, "java.util", firstStr, afterType, "java.util", afterStr);
    }

    protected boolean appearsAfter(NodeWrapper[] wrappers, String firstType, String firstParent, String firstStr, String afterType, String afterParent, String afterStr) {
        NodeWrapper first = new NodeWrapper(firstType, firstParent, firstStr);
        NodeWrapper after = new NodeWrapper(afterType, afterParent, afterStr);
        int firstIndex = -1, afterIndex = -1;
        for (int i = 0, size = wrappers.length; i < size; i++) {
            if (wrappers[i].equals(first)) firstIndex = i;
            if (wrappers[i].equals(after)) afterIndex = i;
            if (firstIndex != -1 && afterIndex != -1) break;
        }
        return afterIndex > firstIndex;

    }


    /**
     * The Class NodeWrapper.
     *
     * @author Vitor Hugo Chagas
     */
    public class NodeWrapper {

        /**
         * The node.
         */
        private Node node;

        /**
         * The type name.
         */
        private String typeName;

        /**
         * The name.
         */
        private String name;

        /**
         * The parent name.
         */
        private String parentName;

        /**
         * Instantiates a new node wrapper.
         *
         * @param node the node
         */
        public NodeWrapper(
                final Node node) {
            this.node = node;
        }

        /**
         * Instantiates a new node wrapper.
         *
         * @param typeName   the type name
         * @param parentName the parent name
         * @param name       the name
         */
        public NodeWrapper(
                final String typeName, final String parentName, final String name) {
            this.typeName = typeName;
            this.parentName = parentName;
            this.name = name;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equalsTo(java.lang.Object)
         */

        @Override
        public boolean equals(final Object obj) {
            return hashCode() == obj.hashCode();
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        public String getName() {
            if (name == null) {
                name = node.getName();
            }
            return name;
        }

        /**
         * Gets the parent name.
         *
         * @return the parent name
         */
        public String getParentName() {
            if (parentName == null) {
                parentName = session.getParentNode(node).getName();
            }
            return parentName;
        }

        /**
         * Gets the type name.
         *
         * @return the type name
         */
        public String getTypeName() {
            if (typeName == null) {
                typeName = node.getTypeName();
            }
            return typeName;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */

        @Override
        public int hashCode() {
            return HashCodes.hashOf(getTypeName(), getParentName(), getName());
        }

        /**
         * Sets the name.
         *
         * @param name the new name
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * Sets the parent name.
         *
         * @param parentName the new parent name
         */
        public void setParentName(final String parentName) {
            this.parentName = parentName;
        }

        /**
         * Sets the type name.
         *
         * @param typeName the new type name
         */
        public void setTypeName(final String typeName) {
            this.typeName = typeName;
        }
    }

    /**
     * The LOGGER.
     */
    protected static Logger LOGGER = Logger.getLogger(AbstractGeneralQueryTest.class);

    /**
     * The session.
     */
    protected GraphReader sessioGraphReaderThe sorGraphReaderprotected static SortMode sortMode = SortMode.NOT_SORTED;

    /**
     * Adds the class implements interface links.
     *
     * @param root      the root
     * @param clazz     the clazz
     * @param javaClass the java class
     */
    private void addClassImplementsInterfaceLinks(final Node root,
                                                  final Class<?> clazz,
                                                  final JavaClass javaClass) {
        final Class<?>[] iFaces = clazz.getInterfaces();
        for (final Class<?> iFace : iFaces) {
            final Package iFacePack = iFace.getPackage();
            final JavaPackage javaPackage = root.addChildNode(JavaPackage.class, iFacePack.getName());
            // javaPackage.setCaption(iFacePack.getName());
            final JavaInterface javaInterface = javaPackage.addChildNode(JavaInterface.class, iFace.getName());
            // javaInterface.setCaption(iFace.getName());
            final ClassImplementsInterface link = session.addLink(ClassImplementsInterface.class, javaClass, javaInterface, false);
            link.setTag(randomTag());
        }
    }

    /**
     * Adds the java class contains java class method.
     *
     * @param clazz     the clazz
     * @param javaClass the java class
     */
    private void addJavaClassContainsJavaClassMethod(final Class<?> clazz,
                                                     final JavaClass javaClass) {
        final Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods) {
            final JavaTypeMethod javaTypeMethod = javaClass.addChildNode(JavaTypeMethod.class, method.getName());
            // javaTypeMethod.setCaption(method.getName());
            final TypeContainsMethod link = session.addLink(TypeContainsMethod.class, javaClass, javaTypeMethod, false);
            link.setTag(randomTag());
        }
    }

    /**
     * Adds the java class hirarchy links.
     *
     * @param root      the root
     * @param clazz     the clazz
     * @param javaClass the java class
     */
    private void addJavaClassHirarchyLinks(final Node root,
                                           final Class<?> clazz,
                                           final JavaClass javaClass) {
        final Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            final Package classPack = clazz.getPackage();
            final JavaPackage javaPackage = root.addChildNode(JavaPackage.class, classPack.getName());
            // javaPackage.setCaption(classPack.getName());
            final JavaClass superJavaClass = javaPackage.addChildNode(JavaClass.class, superClass.getName());
            session.addLink(PackageContainsType.class, javaPackage, superJavaClass, false);
            session.addLink(JavaClassHierarchy.class, javaClass, superJavaClass, false);
            addJavaClassHirarchyLinks(root, superClass, superJavaClass);
        }
    }

    /**
     * Adds the java interface contains java method.
     *
     * @param iFace         the i face
     * @param javaInterface the java interface
     */
    private void addJavaInterfaceContainsJavaMethod(final Class<?> iFace,
                                                    final JavaInterface javaInterface) {
        final Method[] methods = iFace.getDeclaredMethods();
        for (final Method method : methods) {
            final JavaTypeMethod javaTypeMethod = javaInterface.addChildNode(JavaTypeMethod.class, method.getName());
            // javaTypeMethod.setCaption(method.getName());
            final TypeContainsMethod link = session.addLink(TypeContainsMethod.class, javaInterface, javaTypeMethod, false);
            link.setTag(randomTag());
        }
    }

    /**
     * Adds the java interface hirarchy links.
     *
     * @param root          the root
     * @param iFace         the i face
     * @param javaInterface the java interface
     */
    private void addJavaInterfaceHirarchyLinks(final Node root,
                                               final Class<?> iFace,
                                               final JavaInterface javaInterface) {
        final Class<?>[] superIFaces = iFace.getInterfaces();
        for (final Class<?> superIFace : superIFaces) {
            final Package iFacePack = iFace.getPackage();
            final JavaPackage javaPackage = root.addChildNode(JavaPackage.class, iFacePack.getName());
            // javaPackage.setCaption(iFacePack.getName());
            final JavaInterface superJavaInterface = javaPackage.addChildNode(JavaInterface.class, superIFace.getName());
            session.addLink(PackageContainsType.class, javaPackage, superJavaInterface, false);
            // superJavaInterface.setCaption(superIFace.getName());
            session.addLink(JavaInterfaceHierarchy.class, javaInterface, superJavaInterface, false);
            addJavaInterfaceHirarchyLinks(root, superIFace, superJavaInterface);
        }
    }

    /**
     * Finish.
     */
    @AfterClass
    public static void finish() throws Exception {

        shutdownHandler.call();
        shutdownHandler = null;
    }

    /**
     * Load classes.
     *
     * @param fileName the file name
     * @return the collection< class<?>>
     * @throws SLException            the SL exception
     * @throws IOException            Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    private static Collection<Class<?>> loadClasses(final String fileName)
            throws SLException, IOException, ClassNotFoundException {
        final Collection<Class<?>> classes = new ArrayList<Class<?>>();
        final String packagePath = AbstractGeneralQueryTest.class.getPackage().getName().replace('.', '/');
        final String filePath = packagePath + '/' + fileName;
        final InputStream inputStream = getResourceFromClassPath(filePath);
        final Collection<String> names = Files.readLines(inputStream);
        inputStream.close();
        for (final String name : names) {
            final String className = "java.util.".concat(name).trim();
            final Class<?> clazz = Class.forName(className);
            classes.add(clazz);
        }
        return classes;
    }


    /**
     * Gets the resource content. Populate graph.
     */
    @Before
    public void populateGraph() throws Exception {
        if (!didItRun) {
            this.createStartUpHandler().call();
            shutdownHandler = createShutdownHandler();
            this.session = createSession();

            sortMode = SortMode.SORTED;

            final SLMetadata metadata = session.getMetadata();
            if (metadata.getMetaNodeType(JavaType.class) != null) {
                return;
            }

            final Collection<Class<?>> iFaces = loadClasses("java-util-interfaces.txt");
            final Collection<Class<?>> classes = loadClasses("java-util-classes.txt");

            final SLContext context = session.createContext("queryTest");
            Node root = context.getRootNode();

            final Package pack = java.util.Date.class.getPackage();
            final JavaPackage utilJavaPackage = root.addChildNode(JavaPackage.class, pack.getName());
            // utilJavaPackage.setCaption(pack.getName());

            int count = 0;
            final float floatValue = 0.3F;
            for (final Class<?> iFace : iFaces) {
                final JavaInterface javaInterface = utilJavaPackage.addChildNode(JavaInterface.class, iFace.getName());
                session.addLink(PackageContainsType.class, utilJavaPackage, javaInterface, false);
                // javaInterface.setCaption(iFace.getName());
                javaInterface.setProperty(Integer.class, VisibilityLevel.PUBLIC, "intValue", count);
                javaInterface.setProperty(Float.class, VisibilityLevel.PUBLIC, "decValue", new Float(count + floatValue));
                javaInterface.setProperty(Boolean.class, VisibilityLevel.PUBLIC, "boolValue", new Boolean(true));
                addJavaInterfaceHirarchyLinks(root, iFace, javaInterface);
                addJavaInterfaceContainsJavaMethod(iFace, javaInterface);
                count++;
            }

            count = 0;
            for (final Class<?> clazz : classes) {
                // context = session.createContext("queryTest2");
                root = context.getRootNode();
                final JavaClass javaClass = utilJavaPackage.addChildNode(JavaClass.class, clazz.getName());
                session.addLink(PackageContainsType.class, utilJavaPackage, javaClass, false);
                // javaClass.setCaption(clazz.getName());
                javaClass.setProperty(Integer.class, VisibilityLevel.PUBLIC, "intValue", count);
                javaClass.setProperty(Float.class, VisibilityLevel.PUBLIC, "decValue", new Float(count + floatValue));
                javaClass.setProperty(Boolean.class, VisibilityLevel.PUBLIC, "boolValue", new Boolean(false));
                addJavaClassHirarchyLinks(root, clazz, javaClass);
                addClassImplementsInterfaceLinks(root, clazz, javaClass);
                addJavaClassContainsJavaClassMethod(clazz, javaClass);
                count++;
            }

            final JavaInnerInterface javaInnerInterface = utilJavaPackage.addChildNode(JavaInnerInterface.class,
                    java.util.Map.Entry.class.getName());

            session.save();
            session.close();

            session = createSession();
        }

    }

    /**
     * Random tag.
     *
     * @return the int
     */
    private static int randomTag() {
        return (int) Math.round(Math.random() * 100.0);
    }


    /**
     * The print info.
     */
    protected boolean printInfo = false;

    /**
     * Gets the resource content.
     *
     * @param resourceName the resource name
     * @return the resource content
     */
    protected String getResourceContent(final String resourceName) {
        try {
            final InputStream in = this.getClass().getResourceAsStream(resourceName);
            final Reader reader = new InputStreamReader(in);

            final StringBuilder text = new StringBuilder();

            final char[] buf = new char[1024];
            int len = 0;

            while ((len = reader.read(buf)) >= 0) {
                text.append(buf, 0, len);
            }

            return text.toString();
        } catch (final Exception e) {
            return "";
        }
    }

    /**
     * Prints the asserts.
     *
     * @param wrappers the wrappers
     */
    void printAsserts(final NodeWrapper[] wrappers) {
        final StringBuilder buffer = new StringBuilder();
        StringBuilderUtil.append(buffer, '\n', "assertThat(wrappers.length, is(", wrappers.length, "));", '\n');
        for (final NodeWrapper wrapper : wrappers) {
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
     */
    void printAssertsInOrder(final NodeWrapper[] wrappers) {
        final StringBuilder buffer = new StringBuilder();
        StringBuilderUtil.append(buffer, '\n', "assertThat(wrappers.length, is(", wrappers.length, "));", '\n');
        for (int i = 0; i < wrappers.length; i++) {
            final NodeWrapper wrapper = wrappers[i];
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
     */
    protected void printResult(final Collection<Node> nodes) {
        if (printInfo && !nodes.isEmpty()) {
            final StringBuilder buffer = new StringBuilder();
            StringBuilderUtil.append(buffer, "\n\nRESULTS (", nodes.size(), "):\n");
            for (final Node node : nodes) {
                StringBuilderUtil.append(buffer, StringUtils.rightPad(node.getTypeName(), 60),
                        StringUtils.rightPad(node.getName(), 60), session.getParentNode(node).getName(), '\n');
            }
            LOGGER.info(buffer);
        }
    }

    /**
     * Wrap nodes.
     *
     * @param nodes the nodes
     * @return the node wrapper[]
     */
    protected NodeWrapper[] wrapNodes(final List<Node> nodes) {
        final NodeWrapper[] wrappers = new NodeWrapper[nodes.size()];

        for (int i = 0; i < wrappers.length; i++) {
            wrappers[i] = new NodeWrapper(nodes.get(i));
        }
        return wrappers;
    }
}
