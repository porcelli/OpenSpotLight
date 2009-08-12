package org.openspotlight.graph.query;

import static org.openspotlight.common.util.ClassPathResource.getResourceFromClassPath;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class SLGraphQueryTest {

	static final Logger LOGGER = Logger.getLogger(SLGraphQueryTest.class);
	
	private SLGraph graph;
	private SLGraphSession session;

	@BeforeClass
	public void init() throws SLException {
		SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
		graph = factory.createGraph();
		session = graph.openSession();
		populateGraph();
	}
	
	@AfterClass
	public void finish() {
		graph.shutdown();
	}
	
	@Test
	public void test() {
		
	}
	
	private void populateGraph() {

		try {
			
			Collection<Class<?>> iFaces = loadClasses("java-util-interfaces.txt");
			Collection<Class<?>> classes = loadClasses("java-util-classes.txt");
			
			SLContext context = session.createContext(1L);
			SLNode root = context.getRootNode();
			
			Package pack = java.util.Date.class.getPackage();
			JavaPackage utilJavaPackage = root.addNode(JavaPackage.class, pack.getName());
			
			for (Class<?> iFace : iFaces) {
				JavaInterface javaInterface = utilJavaPackage.addNode(JavaInterface.class, iFace.getName());
				session.addLink(PackageContainsType.class, utilJavaPackage, javaInterface, false);
				javaInterface.setCaption(iFace.getName());
				addJavaInterfaceHirarchyLinks(root, iFace, javaInterface);
				addJavaInterfaceContainsJavaMethod(iFace, javaInterface);
			}
			
			for (Class<?> clazz : classes) {
				JavaClass javaClass = utilJavaPackage.addNode(JavaClass.class, clazz.getName());
				session.addLink(PackageContainsType.class, utilJavaPackage, javaClass, false);
				javaClass.setCaption(clazz.getName());
				addJavaClassHirarchyLinks(root, clazz, javaClass);
				addClassImplementsInterfaceLinks(root, clazz, javaClass);
				addJavaClassContainsJavaClassMethod(clazz, javaClass);
			}
			
		} 
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void addJavaInterfaceHirarchyLinks(SLNode root, Class<?> iFace, JavaInterface javaInterface) throws SLGraphSessionException {
		Class<?>[] superIFaces = iFace.getInterfaces();
		for (Class<?> superIFace : superIFaces) {
			Package iFacePack = iFace.getPackage();
			JavaPackage javaPackage = root.addNode(JavaPackage.class, iFacePack.getName());
			JavaInterface superJavaInterface = javaPackage.addNode(JavaInterface.class, superIFace.getName());
			session.addLink(PackageContainsType.class, javaPackage, superJavaInterface, false);
			superJavaInterface.setCaption(superIFace.getName());
			session.addLink(JavaInterfaceHierarchy.class, javaInterface, superJavaInterface, false);
			addJavaInterfaceHirarchyLinks(root, superIFace, superJavaInterface);
		}
	}
	
	private void addJavaClassHirarchyLinks(SLNode root, Class<?> clazz, JavaClass javaClass) throws SLGraphSessionException {
		Class<?> superClass = clazz.getSuperclass();
		if (superClass != null) {
			JavaPackage javaPackage = root.addNode(JavaPackage.class, clazz.getName());
			JavaClass superJavaClass = javaPackage.addNode(JavaClass.class, superClass.getName());
			session.addLink(PackageContainsType.class, javaPackage, superJavaClass, false);
			session.addLink(JavaClassHierarchy.class, javaClass, superJavaClass, false);
			addJavaClassHirarchyLinks(root, superClass, superJavaClass);
		}
	}
	
	private void addClassImplementsInterfaceLinks(SLNode root, Class<?> clazz, JavaClass javaClass) throws SLGraphSessionException {
		Class<?>[] iFaces = clazz.getInterfaces();
		for (Class<?> iFace : iFaces) {
			JavaInterface javaInterface = root.addNode(JavaInterface.class, iFace.getName());
			javaInterface.setCaption(iFace.getName());
			session.addLink(ClassImplementsInterface.class, javaClass, javaInterface, false);
		}
	}
	
	private void addJavaClassContainsJavaClassMethod(Class<?> clazz, JavaClass javaClass) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			JavaTypeMethod javaClassMethod = javaClass.addNode(JavaTypeMethod.class, methods[i].getName());
			session.addLink(TypeContainsMethod.class, javaClass, javaClassMethod, false);
		}
	}

	private void addJavaInterfaceContainsJavaMethod(Class<?> iFace, JavaInterface javaInterface) throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException {
		Method[] methods = iFace.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			JavaTypeMethod javaInterfaceMethod = javaInterface.addNode(JavaTypeMethod.class, methods[i].getName());
			session.addLink(TypeContainsMethod.class, javaInterface, javaInterfaceMethod, false);
		}
	}
	
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
}
