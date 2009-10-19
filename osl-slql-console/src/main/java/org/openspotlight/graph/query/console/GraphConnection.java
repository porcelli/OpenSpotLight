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
import org.openspotlight.graph.query.console.test.domain.ClassImplementsInterface;
import org.openspotlight.graph.query.console.test.domain.JavaClass;
import org.openspotlight.graph.query.console.test.domain.JavaClassHierarchy;
import org.openspotlight.graph.query.console.test.domain.JavaInterface;
import org.openspotlight.graph.query.console.test.domain.JavaInterfaceHierarchy;
import org.openspotlight.graph.query.console.test.domain.JavaPackage;
import org.openspotlight.graph.query.console.test.domain.JavaTypeMethod;
import org.openspotlight.graph.query.console.test.domain.PackageContainsType;
import org.openspotlight.graph.query.console.test.domain.TypeContainsMethod;

public class GraphConnection {
    public SLGraphSession connect( String serverName,
                                   String userName,
                                   String passw ) throws SLException, IOException, ClassNotFoundException {
        return getPopulatedGraph();
    }

    @Deprecated
    private SLGraphSession getPopulatedGraph() throws SLException, IOException, ClassNotFoundException {
        SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        SLGraph graph = factory.createTempGraph(true);
        SLGraphSession session = graph.openSession();

        Collection<Class<?>> iFaces = loadClasses("java-util-interfaces.txt");
        Collection<Class<?>> classes = loadClasses("java-util-classes.txt");

        SLContext context = session.createContext("queryTest");
        SLNode root = context.getRootNode();

        Package pack = java.util.Date.class.getPackage();
        JavaPackage utilJavaPackage = root.addNode(JavaPackage.class, pack.getName());
        utilJavaPackage.setCaption(pack.getName());

        int count = 0;
        float floatValue = 0.3F;
        for (Class<?> iFace : iFaces) {
            JavaInterface javaInterface = utilJavaPackage.addNode(JavaInterface.class, iFace.getName());
            session.addLink(PackageContainsType.class, utilJavaPackage, javaInterface, false);
            javaInterface.setCaption(iFace.getName());
            javaInterface.setProperty(Integer.class, "intValue", count);
            javaInterface.setProperty(Float.class, "decValue", new Float(count + floatValue));
            javaInterface.setProperty(Boolean.class, "boolValue", new Boolean(true));
            addJavaInterfaceHirarchyLinks(session, root, iFace, javaInterface);
            addJavaInterfaceContainsJavaMethod(session, iFace, javaInterface);
            count++;
        }

        count = 0;
        for (Class<?> clazz : classes) {
            //              context = session.createContext("queryTest2");
            root = context.getRootNode();
            JavaClass javaClass = utilJavaPackage.addNode(JavaClass.class, clazz.getName());
            session.addLink(PackageContainsType.class, utilJavaPackage, javaClass, false);
            javaClass.setCaption(clazz.getName());
            javaClass.setProperty(Integer.class, "intValue", count);
            javaClass.setProperty(Float.class, "decValue", new Float(count + floatValue));
            javaClass.setProperty(Boolean.class, "boolValue", new Boolean(false));
            addJavaClassHirarchyLinks(session, root, clazz, javaClass);
            addClassImplementsInterfaceLinks(session, root, clazz, javaClass);
            addJavaClassContainsJavaClassMethod(session, clazz, javaClass);
            count++;
        }

        session.save();
        session.close();

        return graph.openSession();
    }

    /**
     * Adds the java interface hirarchy links.
     * 
     * @param root the root
     * @param iFace the i face
     * @param javaInterface the java interface
     * @throws SLGraphSessionException the SL graph session exception
     */
    private void addJavaInterfaceHirarchyLinks( SLGraphSession session,
                                                SLNode root,
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
            addJavaInterfaceHirarchyLinks(session, root, superIFace, superJavaInterface);
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
    private void addJavaClassHirarchyLinks( SLGraphSession session,
                                            SLNode root,
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
            addJavaClassHirarchyLinks(session, root, superClass, superJavaClass);
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
    private void addClassImplementsInterfaceLinks( SLGraphSession session,
                                                   SLNode root,
                                                   Class<?> clazz,
                                                   JavaClass javaClass ) throws SLGraphSessionException {
        Class<?>[] iFaces = clazz.getInterfaces();
        for (Class<?> iFace : iFaces) {
            Package iFacePack = iFace.getPackage();
            JavaPackage javaPackage = root.addNode(JavaPackage.class, iFacePack.getName());
            javaPackage.setCaption(iFacePack.getName());
            JavaInterface javaInterface = javaPackage.addNode(JavaInterface.class, iFace.getName());
            javaInterface.setCaption(iFace.getName());
            session.addLink(ClassImplementsInterface.class, javaClass, javaInterface, false);
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
    private void addJavaClassContainsJavaClassMethod( SLGraphSession session,
                                                      Class<?> clazz,
                                                      JavaClass javaClass )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            JavaTypeMethod javaTypeMethod = javaClass.addNode(JavaTypeMethod.class, methods[i].getName());
            javaTypeMethod.setCaption(methods[i].getName());
            session.addLink(TypeContainsMethod.class, javaClass, javaTypeMethod, false);
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
    private void addJavaInterfaceContainsJavaMethod( SLGraphSession session,
                                                     Class<?> iFace,
                                                     JavaInterface javaInterface )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
        Method[] methods = iFace.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            JavaTypeMethod javaTypeMethod = javaInterface.addNode(JavaTypeMethod.class, methods[i].getName());
            javaTypeMethod.setCaption(methods[i].getName());
            session.addLink(TypeContainsMethod.class, javaInterface, javaTypeMethod, false);
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
        String packagePath = GraphConnection.class.getPackage().getName().replace('.', '/');
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

}
