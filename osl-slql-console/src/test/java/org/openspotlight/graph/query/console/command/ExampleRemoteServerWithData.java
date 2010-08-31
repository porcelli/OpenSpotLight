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
package org.openspotlight.graph.query.console.command;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.Files;
import org.openspotlight.graph.*;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.graph.query.console.GraphConnection;
import org.openspotlight.graph.query.console.test.domain.*;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;
import org.openspotlight.storage.StorageSessionimport org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

public class ExampleRemoteServerWithData {
    private static SLGraph graph;

    private static class ExampleDataSupport {

        /**
         * Adds the class implements interface links.
         *
         * @param root      the root
         * @param clazz     the clazz
         * @param javaClass the java class
         * @param session   the session
         */
        private void addClassImplementsInterfaceLinks(final GraphReader
                                                      final SLNode root,
                                                      final Class<?> clazz,
                                                      final JavaClass javaClass) {
            final Class<?>[] iFaces = clazz.getInterfaces();
            for (final Class<?> iFace : iFaces) {
                final Package iFacePack = iFace.getPackage();
                final JavaPackage javaPackage = root.addChildNode(JavaPackage.class, iFacePack.getName());
                javaPackage.setCaption(iFacePack.getName());
                final JavaInterface javaInterface = javaPackage.addChildNode(JavaInterface.class, iFace.getName());
                javaInterface.setCaption(iFace.getName());
                session.addLink(ClassImplementsInterface.class, javaClass, javaInterface, false);
            }
        }

        /**
         * Adds the java class contains java class method.
         *
         * @param clazz     the clazz
         * @param javaClass the java class
         * @param session   the session
         */
        private void addJavaClassContainsJavaClassMethod(final GraphReadGraphReader                                                 final Class<?> clazz,
                                                         final JavaClass javaClass) {
            final Method[] methods = clazz.getDeclaredMethods();
            for (final Method method : methods) {
                final JavaTypeMethod javaTypeMethod = javaClass.addChildNode(JavaTypeMethod.class, method.getName());
                javaTypeMethod.setCaption(method.getName());
                session.addLink(TypeContainsMethod.class, javaClass, javaTypeMethod, false);
            }
        }

        /**
         * Adds the java class hirarchy links.
         *
         * @param root      the root
         * @param clazz     the clazz
         * @param javaClass the java class
         * @param session   the session
         */
        private void addJavaClassHirarchyLinks(final GraphReader sessioGraphReader                              final SLNode root,
                                               final Class<?> clazz,
                                               final JavaClass javaClass) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                final Package classPack = clazz.getPackage();
                final JavaPackage javaPackage = root.addChildNode(JavaPackage.class, classPack.getName());
                javaPackage.setCaption(classPack.getName());
                final JavaClass superJavaClass = javaPackage.addChildNode(JavaClass.class, superClass.getName());
                session.addLink(PackageContainsType.class, javaPackage, superJavaClass, false);
                session.addLink(JavaClassHierarchy.class, javaClass, superJavaClass, false);
                this.addJavaClassHirarchyLinks(session, root, superClass, superJavaClass);
            }
        }

        /**
         * Adds the java interface contains java method.
         *
         * @param iFace         the i face
         * @param javaInterface the java interface
         * @param session       the session
         */
        private void addJavaInterfaceContainsJavaMethod(final GraphReader session,
      GraphReader                              final Class<?> iFace,
                                                        final JavaInterface javaInterface) {
            final Method[] methods = iFace.getDeclaredMethods();
            for (final Method method : methods) {
                final JavaTypeMethod javaTypeMethod = javaInterface.addChildNode(JavaTypeMethod.class, method.getName());
                javaTypeMethod.setCaption(method.getName());
                session.addLink(TypeContainsMethod.class, javaInterface, javaTypeMethod, false);
            }
        }

        /**
         * Adds the java interface hirarchy links.
         *
         * @param root          the root
         * @param iFace         the i face
         * @param javaInterface the java interface
         * @param session       the session
         */
        private void addJavaInterfaceHirarchyLinks(final GraphReader session,
               GraphReader                final SLNode root,
                                                   final Class<?> iFace,
                                                   final JavaInterface javaInterface) {
            final Class<?>[] superIFaces = iFace.getInterfaces();
            for (final Class<?> superIFace : superIFaces) {
                final Package iFacePack = iFace.getPackage();
                final JavaPackage javaPackage = root.addChildNode(JavaPackage.class, iFacePack.getName());
                javaPackage.setCaption(iFacePack.getName());
                final JavaInterface superJavaInterface = javaPackage.addChildNode(JavaInterface.class, superIFace.getName());
                session.addLink(PackageContainsType.class, javaPackage, superJavaInterface, false);
                superJavaInterface.setCaption(superIFace.getName());
                session.addLink(JavaInterfaceHierarchy.class, javaInterface, superJavaInterface, false);
                this.addJavaInterfaceHirarchyLinks(session, root, superIFace, superJavaInterface);
            }
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
        private Collection<Class<?>> loadClasses(final String fileName) throws SLException, IOException, ClassNotFoundException {
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

        /**
         * Gets the populated graph. This method is temporary and used just for testing purpose!
         *
         * @return the populated graph
         * @throws SLException            the SL exception
         * @throws IOException            Signals that an I/O exception has occurred.
         * @throws ClassNotFoundException the class not found exception
         */
        public void populateGraph() throws SLException, IOException, ClassNotFoundException, IdentityException {


            Injector injector = Guice.createInjector(new JRedisStorageModule(StStorageSessionlushMode.AUTO,
                    ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                    repositoryPath(SLConsts.DEFAULT_REPOSITORY_NAME)),
                    new SimplePersistModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));


            graph = injector.getInstance(SLGraph.class);
            final SecurityFactory securityFactory = injector.getInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser("testUser");
            AuthenticatedUser user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");

            final GraphReader session = graph.openSession(user,GraphReaderPOSITORY_NAME);

            final Collection<Class<?>> iFaces = this.loadClasses("java-util-interfaces.txt");
            final Collection<Class<?>> classes = this.loadClasses("java-util-classes.txt");

            SLContext context = null;
//            if (session.getContext("queryTest") == null) {
                context = session.createContext("queryTest");
                SLNode root = context.getRootNode();

                final Package pack = java.util.Date.class.getPackage();
                final JavaPackage utilJavaPackage = root.addChildNode(JavaPackage.class, pack.getName());
                utilJavaPackage.setCaption(pack.getName());

                int count = 0;
                final float floatValue = 0.3F;
                for (final Class<?> iFace : iFaces) {
                    final JavaInterface javaInterface = utilJavaPackage.addChildNode(JavaInterface.class, iFace.getName());
                    session.addLink(PackageContainsType.class, utilJavaPackage, javaInterface, false);
                    javaInterface.setCaption(iFace.getName());
                    javaInterface.setProperty(Integer.class, VisibilityLevel.PUBLIC, "intValue", count);
                    javaInterface.setProperty(Float.class, VisibilityLevel.PUBLIC, "decValue", new Float(count + floatValue));
                    javaInterface.setProperty(Boolean.class, VisibilityLevel.PUBLIC, "boolValue", new Boolean(true));
                    this.addJavaInterfaceHirarchyLinks(session, root, iFace, javaInterface);
                    this.addJavaInterfaceContainsJavaMethod(session, iFace, javaInterface);
                    count++;
                }

                count = 0;
                for (final Class<?> clazz : classes) {
                    // context = session.createContext("queryTest2");
                    root = context.getRootNode();
                    final JavaClass javaClass = utilJavaPackage.addChildNode(JavaClass.class, clazz.getName());
                    session.addLink(PackageContainsType.class, utilJavaPackage, javaClass, false);
                    javaClass.setCaption(clazz.getName());
                    javaClass.setProperty(Integer.class, VisibilityLevel.PUBLIC, "intValue", count);
                    javaClass.setProperty(Float.class, VisibilityLevel.PUBLIC, "decValue", new Float(count + floatValue));
                    javaClass.setProperty(Boolean.class, VisibilityLevel.PUBLIC, "boolValue", new Boolean(false));
                    this.addJavaClassHirarchyLinks(session, root, clazz, javaClass);
                    this.addClassImplementsInterfaceLinks(session, root, clazz, javaClass);
                    this.addJavaClassContainsJavaClassMethod(session, clazz, javaClass);
                    count++;
                }

                session.save();
//            }
            session.close();
        }

    }

    public static void main(final String... args) throws Exception {

        RemoteGraphSessionServer server = null;
        try {
            server = populateSomeDataAndStartTheServer();
            while (true) {
                Thread.sleep(5000);
            }
        } finally {
            if (server != null) {
                server.shutdown();
            }
        }

    }

    public static RemoteGraphSessionServer populateSomeDataAndStartTheServer() throws Exception {
        new ExampleDataSupport().populateGraph();

        return new RemoteGraphSessionServer(new UserAuthenticator() {

            public boolean canConnect(final String userName,
                                      final String password,
                                      final String clientHost) {
                return true;
            }

            public boolean equals(final Object o) {
                return this.getClass().equals(o.getClass());
            }
        }, 7070, 60 * 1000 * 10L, DefaultJcrDescriptor.TEMP_DESCRIPTOR, graph);

    }

}
