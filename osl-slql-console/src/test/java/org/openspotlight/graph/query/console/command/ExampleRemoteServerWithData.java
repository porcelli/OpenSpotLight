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

import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.Files;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.GraphModule;
import org.openspotlight.graph.GraphSessionFactory;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.manipulation.GraphWriter;
import org.openspotlight.graph.query.console.GraphConnection;
import org.openspotlight.graph.query.console.test.domain.ClassImplementsInterface;
import org.openspotlight.graph.query.console.test.domain.JavaClass;
import org.openspotlight.graph.query.console.test.domain.JavaClassHierarchy;
import org.openspotlight.graph.query.console.test.domain.JavaInterface;
import org.openspotlight.graph.query.console.test.domain.JavaInterfaceHierarchy;
import org.openspotlight.graph.query.console.test.domain.JavaPackage;
import org.openspotlight.graph.query.console.test.domain.JavaTypeMethod;
import org.openspotlight.graph.query.console.test.domain.PackageContainsType;
import org.openspotlight.graph.query.console.test.domain.TypeContainsMethod;
import org.openspotlight.graph.server.RemoteGraphSessionServer;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.User;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class ExampleRemoteServerWithData {

    private static class ExampleDataSupport {

        /**
         * Adds the class implements interface links.
         * 
         * @param root the root
         * @param clazz the clazz
         * @param javaClass the java class
         * @param session the session
         */
        private void addClassImplementsInterfaceLinks(
                                                      final GraphWriter session, final Context root,
                                                      final Class<?> clazz, final JavaClass javaClass) {
            final Class<?>[] iFaces = clazz.getInterfaces();
            for (final Class<?> iFace: iFaces) {
                final Package iFacePack = iFace.getPackage();
                final JavaPackage javaPackage = session.addNode(root,
                        JavaPackage.class, iFacePack.getName());
                javaPackage.setCaption(iFacePack.getName());
                final JavaInterface javaInterface = session.addChildNode(
                        javaPackage, JavaInterface.class, iFace.getName());
                javaInterface.setCaption(iFace.getName());
                session.addLink(ClassImplementsInterface.class, javaClass,
                        javaInterface);
            }
        }

        /**
         * Adds the java class contains java class method.
         * 
         * @param clazz the clazz
         * @param javaClass the java class
         * @param session the session
         */
        private void addJavaClassContainsJavaClassMethod(
                                                         final GraphWriter session, final Class<?> clazz,
                                                         final JavaClass javaClass) {
            final Method[] methods = clazz.getDeclaredMethods();
            for (final Method method: methods) {
                final JavaTypeMethod javaTypeMethod = session.addChildNode(
                        javaClass, JavaTypeMethod.class, method.getName());
                javaTypeMethod.setCaption(method.getName());
                session.addLink(TypeContainsMethod.class, javaClass,
                        javaTypeMethod);
            }
        }

        /**
         * Adds the java class hirarchy links.
         * 
         * @param root the root
         * @param clazz the clazz
         * @param javaClass the java class
         * @param session the session
         */
        private void addJavaClassHirarchyLinks(final GraphWriter session,
                                               final Context root, final Class<?> clazz,
                                               final JavaClass javaClass) {
            final Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                final Package classPack = clazz.getPackage();
                final JavaPackage javaPackage = session.addNode(root,
                        JavaPackage.class, classPack.getName());
                javaPackage.setCaption(classPack.getName());
                final JavaClass superJavaClass = session.addChildNode(
                        javaPackage, JavaClass.class, superClass.getName());
                session.addLink(PackageContainsType.class, javaPackage,
                        superJavaClass);
                session.addLink(JavaClassHierarchy.class, javaClass,
                        superJavaClass);
                this.addJavaClassHirarchyLinks(session, root, superClass,
                        superJavaClass);
            }
        }

        /**
         * Adds the java interface contains java method.
         * 
         * @param iFace the i face
         * @param javaInterface the java interface
         * @param session the session
         */
        private void addJavaInterfaceContainsJavaMethod(
                                                        final GraphWriter session, final Class<?> iFace,
                                                        final JavaInterface javaInterface) {
            final Method[] methods = iFace.getDeclaredMethods();
            for (final Method method: methods) {
                final JavaTypeMethod javaTypeMethod = session.addChildNode(
                        javaInterface, JavaTypeMethod.class, method.getName());
                javaTypeMethod.setCaption(method.getName());
                session.addLink(TypeContainsMethod.class, javaInterface,
                        javaTypeMethod);
            }
        }

        /**
         * Adds the java interface hirarchy links.
         * 
         * @param root the root
         * @param iFace the i face
         * @param javaInterface the java interface
         * @param session the session
         */
        private void addJavaInterfaceHirarchyLinks(final GraphWriter session,
                                                   final Context root, final Class<?> iFace,
                                                   final JavaInterface javaInterface) {
            final Class<?>[] superIFaces = iFace.getInterfaces();
            for (final Class<?> superIFace: superIFaces) {
                final Package iFacePack = iFace.getPackage();
                final JavaPackage javaPackage = session.addNode(root,
                        JavaPackage.class, iFacePack.getName());
                javaPackage.setCaption(iFacePack.getName());
                final JavaInterface superJavaInterface = session.addChildNode(
                        javaPackage, JavaInterface.class, superIFace.getName());
                session.addLink(PackageContainsType.class, javaPackage,
                        superJavaInterface);
                superJavaInterface.setCaption(superIFace.getName());
                session.addLink(JavaInterfaceHierarchy.class, javaInterface,
                        superJavaInterface);
                this.addJavaInterfaceHirarchyLinks(session, root, superIFace,
                        superJavaInterface);
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
        private Collection<Class<?>> loadClasses(final String fileName)
                throws SLException, IOException, ClassNotFoundException {
            final Collection<Class<?>> classes = new ArrayList<Class<?>>();
            final String packagePath = GraphConnection.class.getPackage()
                    .getName().replace('.', '/');
            final String filePath = packagePath + '/' + fileName;
            final InputStream inputStream = getResourceFromClassPath(filePath);
            final Collection<String> names = Files.readLines(inputStream);
            inputStream.close();
            for (final String name: names) {
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
         * @throws SLException the SL exception
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ClassNotFoundException the class not found exception
         */
        public Pair<GraphSessionFactory, Provider<StorageSession>> populateGraph()
                throws Exception {

            Injector injector = Guice.createInjector(
                    new JRedisStorageModule(StorageSession.FlushMode.AUTO,
                            ExampleRedisConfig.EXAMPLE.getMappedServerConfig()),
                    new SimplePersistModule(), new GraphModule());

            final SecurityFactory securityFactory = injector
                    .getInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser("testUser");

            final Collection<Class<?>> iFaces = this
                    .loadClasses("java-util-interfaces.txt");
            final Collection<Class<?>> classes = this
                    .loadClasses("java-util-classes.txt");
            GraphWriter writer = injector.getInstance(GraphWriter.class);
            GraphReader reader = injector.getInstance(GraphReader.class);
            Context root = reader.getContext("queryTest");

            final Package pack = java.util.Date.class.getPackage();
            final JavaPackage utilJavaPackage = writer.addNode(root,
                    JavaPackage.class, pack.getName());
            utilJavaPackage.setCaption(pack.getName());

            int count = 0;
            final float floatValue = 0.3F;
            for (final Class<?> iFace: iFaces) {
                final JavaInterface javaInterface = writer.addChildNode(
                        utilJavaPackage, JavaInterface.class, iFace.getName());
                writer.addLink(PackageContainsType.class, utilJavaPackage,
                        javaInterface);
                javaInterface.setCaption(iFace.getName());
                javaInterface.setIntValue(count);
                javaInterface.setDecValue(new Float(count + floatValue));
                javaInterface.setBoolValue(new Boolean(true));
                this.addJavaInterfaceHirarchyLinks(writer, root, iFace,
                        javaInterface);
                this.addJavaInterfaceContainsJavaMethod(writer, iFace,
                        javaInterface);
                count++;
            }

            count = 0;
            for (final Class<?> clazz: classes) {
                // context = session.createContext("queryTest2");
                final JavaClass javaClass = writer.addChildNode(
                        utilJavaPackage, JavaClass.class, clazz.getName());
                writer.addLink(PackageContainsType.class, utilJavaPackage,
                        javaClass);
                javaClass.setCaption(clazz.getName());
                javaClass.setIntValue(count);
                javaClass.setDecValue(new Float(count + floatValue));
                javaClass.setBoolValue(new Boolean(false));
                this.addJavaClassHirarchyLinks(writer, root, clazz, javaClass);
                this.addClassImplementsInterfaceLinks(writer, root, clazz,
                        javaClass);
                this.addJavaClassContainsJavaClassMethod(writer, clazz,
                        javaClass);
                count++;
            }

            writer.flush();
            return Pair.newPair(
                    injector.getInstance(GraphSessionFactory.class),
                    injector.getProvider(StorageSession.class));
        }

    }

    public static void main(final String... args)
        throws Exception {

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

    public static RemoteGraphSessionServer populateSomeDataAndStartTheServer()
            throws Exception {
        Pair<GraphSessionFactory, Provider<StorageSession>> pair = new ExampleDataSupport()
                .populateGraph();

        return new RemoteGraphSessionServer(new UserAuthenticator() {

            public boolean canConnect(final String userName,
                                      final String password, final String clientHost) {
                return true;
            }

            public boolean equals(final Object o) {
                return this.getClass().equals(o.getClass());
            }
        }, 7070, 60 * 1000 * 10L, pair.getK1(), pair.getK2());

    }

}
