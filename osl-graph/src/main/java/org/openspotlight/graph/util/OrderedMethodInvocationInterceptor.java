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

/**
 * The Class OrderedMethodInvocationInterceptor.
 * 
 * @author Vitor Hugo Chagas
 */
public class OrderedMethodInvocationInterceptor implements IMethodInterceptor {
	
	//@Override
	/* (non-Javadoc)
	 * @see org.testng.IMethodInterceptor#intercept(java.util.List, org.testng.ITestContext)
	 */
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
	
	/**
	 * Gets the method instance map.
	 * 
	 * @param methods the methods
	 * 
	 * @return the method instance map
	 */
	private Map<String, IMethodInstance> getMethodInstanceMap(List<IMethodInstance> methods) {
		Map<String, IMethodInstance> map = new HashMap<String, IMethodInstance>();
		for (IMethodInstance methodInstance : methods) {
			map.put(methodInstance.getMethod().getMethodName(), methodInstance);
		}
		return map;
	}
	
	/**
	 * Gets the method names in declaration order.
	 * 
	 * @param clazz the clazz
	 * 
	 * @return the method names in declaration order
	 * 
	 * @throws Exception the exception
	 */
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
	
	/**
	 * Gets the java file.
	 * 
	 * @param clazz the clazz
	 * 
	 * @return the java file
	 */
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
