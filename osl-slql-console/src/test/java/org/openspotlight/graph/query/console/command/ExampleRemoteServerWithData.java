package org.openspotlight.graph.query.console.command;

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
import org.openspotlight.graph.SLInvalidCredentialsException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeTypeNotInExistentHierarchy;
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
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.remote.server.UserAuthenticator;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;

public class ExampleRemoteServerWithData {

    private static class ExampleDataSupport {
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
                                                       final JavaClass javaClass )
            throws SLGraphSessionException, SLInvalidCredentialsException {
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
            throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException, SLInvalidCredentialsException {
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
                                                final JavaClass javaClass )
            throws SLGraphSessionException, SLInvalidCredentialsException {
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
            throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException, SLInvalidCredentialsException {
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
                                                    final JavaInterface javaInterface )
            throws SLGraphSessionException, SLInvalidCredentialsException {
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

        /**
         * Gets the populated graph. This method is temporary and used just for testing purpose!
         * 
         * @return the populated graph
         * @throws SLException the SL exception
         * @throws IOException Signals that an I/O exception has occurred.
         * @throws ClassNotFoundException the class not found exception
         */
        public void populateGraph() throws SLException, IOException, ClassNotFoundException, SLInvalidCredentialsException {
            final SecurityFactory securityFactory = AbstractFactory.getDefaultInstance(SecurityFactory.class);
            final User simpleUser = securityFactory.createUser("testUser");
            AuthenticatedUser user = securityFactory.createIdentityManager(DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser, "password");

            final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
            final SLGraph graph = factory.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
            final SLGraphSession session = graph.openSession(user);

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
        }

    }

    public static void main( final String... args ) throws Exception {

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

            public boolean canConnect( final String userName,
                                       final String password,
                                       final String clientHost ) {
                return true;
            }

            public boolean equals( final Object o ) {
                return this.getClass().equals(o.getClass());
            }
        }, 7070, 60 * 1000 * 10L, DefaultJcrDescriptor.TEMP_DESCRIPTOR);

    }

}
