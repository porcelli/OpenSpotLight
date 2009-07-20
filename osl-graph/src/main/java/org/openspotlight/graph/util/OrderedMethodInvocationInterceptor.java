package org.openspotlight.graph.util;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.ws.jaxme.js.JavaMethod;
import org.apache.ws.jaxme.js.JavaSource;
import org.apache.ws.jaxme.js.JavaSourceFactory;
import org.apache.ws.jaxme.js.util.JavaParser;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.annotations.Test;

public class OrderedMethodInvocationInterceptor implements IMethodInterceptor {
	
	@Override
	public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context) {
		if (methods.size() > 0) {
			List<IMethodInstance> res = new ArrayList<IMethodInstance>();
			try {
				Class<?> clazz = methods.get(0).getMethod().getRealClass();
				Map<String, IMethodInstance> map = getMethodInstanceMap(methods);
				List<String> methodNamesInOrder = getMethodNamesInDeclarationOrder(clazz);
				for (String methodName : methodNamesInOrder) {
					IMethodInstance methodInstance = map.get(methodName);
					res.add(methodInstance);
				}
				return res;
			}
			catch (Exception e) {
				throw new RuntimeException("Error on attempt to execute test NG interceptor: " + this.getClass().getName(), e);
			}
		}
		return methods;
	}
	
	private Map<String, IMethodInstance> getMethodInstanceMap(List<IMethodInstance> methods) {
		Map<String, IMethodInstance> map = new HashMap<String, IMethodInstance>();
		for (IMethodInstance methodInstance : methods) {
			map.put(methodInstance.getMethod().getMethodName(), methodInstance);
		}
		return map;
	}
	
	private List<String> getMethodNamesInDeclarationOrder(Class<?> clazz) throws Exception {
		List<String> list = new ArrayList<String>();
		File javaFile = getJavaFile(clazz);
		JavaSourceFactory factory = new JavaSourceFactory();
		JavaParser parser = new JavaParser(factory);
		parser.parse(javaFile);
		JavaSource javaSource = (JavaSource) factory.getJavaSources().next();
		JavaMethod[] javaMethods = javaSource.getMethods();
		for (int i = 0; i < javaMethods.length; i++) {
			Method method = null;
			try {
				method = clazz.getMethod(javaMethods[i].getName(), new Class<?>[] {});	
			}
			catch (NoSuchMethodException e) {}
			if (method != null) {
				Annotation annotation = method.getAnnotation(Test.class);
				if (annotation != null) {
					list.add(method.getName());
				}
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private File getJavaFile(Class<?> clazz) {
		File dir = new File(".");
		String simpleFileName = clazz.getSimpleName().concat(".java");
		String fileName = clazz.getName().replace('.', '/').concat(".java");
		IOFileFilter fileFilter = FileFilterUtils.nameFileFilter(simpleFileName);
		IOFileFilter dirFilter = FileFilterUtils.directoryFileFilter();
		Collection list = FileUtils.listFiles(dir, fileFilter, dirFilter);
		for (Object current : list) {
			File file = (File) current;
			if (file.getAbsolutePath().endsWith(fileName)) {
				return file;
			}
		}
		return null;
	}
}
