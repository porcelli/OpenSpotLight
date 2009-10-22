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
package org.openspotlight.graph.query.console;

import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.common.util.Files;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeTypeNotInExistentHierarchy;
import org.openspotlight.graph.client.RemoteGraphSessionFactory;
import org.openspotlight.graph.client.RemoteGraphSessionFactory.RemoteGraphFactoryConnectionDataImpl;
import org.openspotlight.graph.query.console.test.domain.ClassImplementsInterface;
import org.openspotlight.graph.query.console.test.domain.JavaClass;
import org.openspotlight.graph.query.console.test.domain.JavaClassHierarchy;
import org.openspotlight.graph.query.console.test.domain.JavaInterface;
import org.openspotlight.graph.query.console.test.domain.JavaInterfaceHierarchy;
import org.openspotlight.graph.query.console.test.domain.JavaPackage;
import org.openspotlight.graph.query.console.test.domain.JavaTypeMethod;
import org.openspotlight.graph.query.console.test.domain.PackageContainsType;
import org.openspotlight.graph.query.console.test.domain.TypeContainsMethod;

/**
 * The Class GraphConnection. This implementation should be changes as soon we get remote access to graph done.
 * 
 * @author porcelli
 */
public class GraphConnection {

    private RemoteGraphSessionFactory factory;

    /**
     * Adds the class implements interface links.
     * 
     * @param root the root
     * @param clazz the clazz
     * @param javaClass the java class
     * @param session the session
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addClassImplementsInterfaceLinks( final SLGraphSession session,
                                                   final SLNode root,
                                                   final Class<?> clazz,
                                                   final JavaClass javaClass ) throws SLGraphSessionException {
        final Class<?>[] iFaces = clazz.getInterfaces();
        for (final Class<?> iFace : iFaces) {
            final Package iFacePack = iFace.getPackage();
            final JavaPackage javaPackage = root.addNode(JavaPackage.class, iFacePack.getName());
            javaPackage.setCaption(iFacePack.getName());
            final JavaInterface javaInterface = javaPackage.addNode(JavaInterface.class, iFace.getName());
            javaInterface.setCaption(iFace.getName());
            session.addLink(ClassImplementsInterface.class, javaClass, javaInterface, false);
        }
    }

    /**
     * Adds the java class contains java class method.
     * 
     * @param clazz the clazz
     * @param javaClass the java class
     * @param session the session
     * @throws SLNodeTypeNotInExistentHierarchy the SL node type not in existent hierarchy
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaClassContainsJavaClassMethod( final SLGraphSession session,
                                                      final Class<?> clazz,
                                                      final JavaClass javaClass )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        final Method[] methods = clazz.getDeclaredMethods();
        for (final Method method : methods) {
            final JavaTypeMethod javaTypeMethod = javaClass.addNode(JavaTypeMethod.class, method.getName());
            javaTypeMethod.setCaption(method.getName());
            session.addLink(TypeContainsMethod.class, javaClass, javaTypeMethod, false);
        }
    }

    /**
     * Adds the java class hirarchy links.
     * 
     * @param root the root
     * @param clazz the clazz
     * @param javaClass the java class
     * @param session the session
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaClassHirarchyLinks( final SLGraphSession session,
                                            final SLNode root,
                                            final Class<?> clazz,
                                            final JavaClass javaClass ) throws SLGraphSessionException {
        final Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            final Package classPack = clazz.getPackage();
            final JavaPackage javaPackage = root.addNode(JavaPackage.class, classPack.getName());
            javaPackage.setCaption(classPack.getName());
            final JavaClass superJavaClass = javaPackage.addNode(JavaClass.class, superClass.getName());
            session.addLink(PackageContainsType.class, javaPackage, superJavaClass, false);
            session.addLink(JavaClassHierarchy.class, javaClass, superJavaClass, false);
            this.addJavaClassHirarchyLinks(session, root, superClass, superJavaClass);
        }
    }

    /**
     * Adds the java interface contains java method.
     * 
     * @param iFace the i face
     * @param javaInterface the java interface
     * @param session the session
     * @throws SLNodeTypeNotInExistentHierarchy the SL node type not in existent hierarchy
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaInterfaceContainsJavaMethod( final SLGraphSession session,
                                                     final Class<?> iFace,
                                                     final JavaInterface javaInterface )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        final Method[] methods = iFace.getDeclaredMethods();
        for (final Method method : methods) {
            final JavaTypeMethod javaTypeMethod = javaInterface.addNode(JavaTypeMethod.class, method.getName());
            javaTypeMethod.setCaption(method.getName());
            session.addLink(TypeContainsMethod.class, javaInterface, javaTypeMethod, false);
        }
    }

    /**
     * Adds the java interface hirarchy links.
     * 
     * @param root the root
     * @param iFace the i face
     * @param javaInterface the java interface
     * @param session the session
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaInterfaceHirarchyLinks( final SLGraphSession session,
                                                final SLNode root,
                                                final Class<?> iFace,
                                                final JavaInterface javaInterface ) throws SLGraphSessionException {
        final Class<?>[] superIFaces = iFace.getInterfaces();
        for (final Class<?> superIFace : superIFaces) {
            final Package iFacePack = iFace.getPackage();
            final JavaPackage javaPackage = root.addNode(JavaPackage.class, iFacePack.getName());
            javaPackage.setCaption(iFacePack.getName());
            final JavaInterface superJavaInterface = javaPackage.addNode(JavaInterface.class, superIFace.getName());
            session.addLink(PackageContainsType.class, javaPackage, superJavaInterface, false);
            superJavaInterface.setCaption(superIFace.getName());
            session.addLink(JavaInterfaceHierarchy.class, javaInterface, superJavaInterface, false);
            this.addJavaInterfaceHirarchyLinks(session, root, superIFace, superJavaInterface);
        }
    }

    /**
     * Connects at server and returns {@link SLGraphSession}.
     * 
     * @param serverName the server name
     * @param userName the user name
     * @param passw the passw
     * @return the graph session
     * @throws SLException the SL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    @SuppressWarnings( "boxing" )
    public SLGraphSession connect( final String serverName,
                                   final String userName,
                                   final String passw ) throws SLException, IOException, ClassNotFoundException {
        int port = RemoteGraphSessionFactory.DEFAULT_PORT;
        String realServerName = serverName;
        if (serverName.indexOf(':') > 0) {
            final String portStr = serverName.substring(serverName.indexOf(':') + 1);
            port = Integer.valueOf(portStr);
            realServerName = serverName.substring(0, serverName.indexOf(':'));
        }

        this.factory = new RemoteGraphSessionFactory(new RemoteGraphFactoryConnectionDataImpl(realServerName, userName, passw,
                                                                                              port));
        return this.factory.createRemoteGraphSession();

    }

    /**
     * Gets the populated graph. This method is temporary and used just for testing purpose!
     * 
     * @return the populated graph
     * @throws SLException the SL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    @Deprecated
    private SLGraphSession getPopulatedGraph() throws SLException, IOException, ClassNotFoundException {
        final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        final SLGraph graph = factory.createTempGraph(true);
        final SLGraphSession session = graph.openSession();

        final Collection<Class<?>> iFaces = this.loadClasses("java-util-interfaces.txt");
        final Collection<Class<?>> classes = this.loadClasses("java-util-classes.txt");

        SLContext context = null;
        if (session.getContext("queryTest") == null) {
            context = session.createContext("queryTest");
            SLNode root = context.getRootNode();

            final Package pack = java.util.Date.class.getPackage();
            final JavaPackage utilJavaPackage = root.addNode(JavaPackage.class, pack.getName());
            utilJavaPackage.setCaption(pack.getName());

            int count = 0;
            final float floatValue = 0.3F;
            for (final Class<?> iFace : iFaces) {
                final JavaInterface javaInterface = utilJavaPackage.addNode(JavaInterface.class, iFace.getName());
                session.addLink(PackageContainsType.class, utilJavaPackage, javaInterface, false);
                javaInterface.setCaption(iFace.getName());
                javaInterface.setProperty(Integer.class, "intValue", count);
                javaInterface.setProperty(Float.class, "decValue", new Float(count + floatValue));
                javaInterface.setProperty(Boolean.class, "boolValue", new Boolean(true));
                this.addJavaInterfaceHirarchyLinks(session, root, iFace, javaInterface);
                this.addJavaInterfaceContainsJavaMethod(session, iFace, javaInterface);
                count++;
            }

            count = 0;
            for (final Class<?> clazz : classes) {
                //              context = session.createContext("queryTest2");
                root = context.getRootNode();
                final JavaClass javaClass = utilJavaPackage.addNode(JavaClass.class, clazz.getName());
                session.addLink(PackageContainsType.class, utilJavaPackage, javaClass, false);
                javaClass.setCaption(clazz.getName());
                javaClass.setProperty(Integer.class, "intValue", count);
                javaClass.setProperty(Float.class, "decValue", new Float(count + floatValue));
                javaClass.setProperty(Boolean.class, "boolValue", new Boolean(false));
                this.addJavaClassHirarchyLinks(session, root, clazz, javaClass);
                this.addClassImplementsInterfaceLinks(session, root, clazz, javaClass);
                this.addJavaClassContainsJavaClassMethod(session, clazz, javaClass);
                count++;
            }

            session.save();
        }
        session.close();
        return graph.openSession();
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
    private Collection<Class<?>> loadClasses( final String fileName ) throws SLException, IOException, ClassNotFoundException {
        final Collection<Class<?>> classes = new ArrayList<Class<?>>();
        final String packagePath = GraphConnection.class.getPackage().getName().replace('.', '/');
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

}
