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
package org.openspotlight.bundle.java;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.Opcodes;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.parser.dap.language.java.link.*;
import org.openspotlight.parser.dap.language.java.node.*;

class StringUtils {

	public static String repeatString(String s, int n) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<n;i++){
			sb.append(s);
		}
		return sb.toString();
	}
}

public class ExampleGraphImport {

	@Test
	public void shouldImportSomeData() throws Exception {
		
		SLGraphFactory factory = new SLGraphFactoryImpl();
		SLGraph graph = factory.createTempGraph(true);
		SLGraphSession session = graph.openSession();
		SLNode rootNode = session.createContext("sample").getRootNode();
		
		// global variables to be reused for each type

		JavaType newType;
		JavaPackage newPackage;
		JavaType newSuperType;
		JavaPackage newSuperPackage;
		JavaType fieldType;
		JavaPackage fieldPackage;
		JavaDataField field;
		JavaMethod method;
		JavaDataParameter parameter;
		PackageType packageTypeLink;
		PackageType fieldPackageTypeLink;
		DataType fieldTypeLink;
		boolean isPublic;
		boolean isPrivate;
		boolean isStatic;
		boolean isFinal;
		boolean isProtected;
		boolean isFieldPublic;
		boolean isFieldPrivate;
		boolean isFieldStatic;
		boolean isFieldFinal;
		boolean isFieldProtected;
		boolean isFieldTransient;
		boolean isFieldVolatile;
		boolean isMethodPublic;
		boolean isMethodPrivate;
		boolean isMethodStatic;
		boolean isMethodFinal;
		boolean isMethodProtected;
		boolean isMethodSynchronized;
		PackageType superPackageTypeLink;
		TypeDeclares typeDeclaresMethod;
		JavaType methodReturnTypeType;
		MethodReturns methodReturnsType;
		JavaPackage methodParameterTypePackage;
		JavaType methodParameterTypeType;
		PackageType methodParameterTypePackageTypeLink;
		MethodParameterDeclare methodParametersType;
		JavaPackage newExceptionPackage;
		JavaTypeClass newExceptionType;
		PackageType exceptionPackageTypeLink;
		MethodThrows methodThrowsType;
		JavaPackage methodReturnTypePackage;
		PackageType methodReturnTypePackageTypeLink;
		String arraySquareBrackets;
		Extends extendsSuper;
		Implements implementsSuper;

		    
		// starting type org.jboss.seam.Component$1
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$1");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (32 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (32 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (32 & Opcodes.ACC_STATIC) != 0;
		isFinal = (32 & Opcodes.ACC_FINAL) != 0;
		isProtected = (32 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface MethodFilter 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "javassist.util.proxy");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "MethodFilter");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface MethodFilter 



		    // starting method org.jboss.seam.Component$1#Component$1()
		    method = newType.addNode(JavaMethodConstructor.class,"Component$1()");
		    method.setSimpleName("Component$1");
		    isMethodPublic = (0 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (0 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (0 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (0 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (0 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (0 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$1#Component$1()

		    // starting method org.jboss.seam.Component$1#isHandled(java.lang.reflect.Method)
		    method = newType.addNode(JavaMethodMethod.class,"isHandled(java.lang.reflect.Method)");
		    method.setSimpleName("isHandled");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Method");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$1#isHandled(java.lang.reflect.Method)

		// finishing type org.jboss.seam.Component$1
		// #########################################################

		// starting type org.jboss.seam.Component$2
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$2");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (32 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (32 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (32 & Opcodes.ACC_STATIC) != 0;
		isFinal = (32 & Opcodes.ACC_FINAL) != 0;
		isProtected = (32 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);

		    // starting field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$ComponentType
			field = newType.addNode(JavaDataField.class,"$SwitchMap$org$jboss$seam$ComponentType"); 
		    // starting array
		    arraySquareBrackets = StringUtils.repeatString("[]",1);
		    fieldType = rootNode.addNode(JavaTypePrimitive.class, "int"+arraySquareBrackets);
		    //ending array
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (4120 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (4120 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (4120 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (4120 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (4120 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (4120 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (4120 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$ComponentType

		    // starting field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType
			field = newType.addNode(JavaDataField.class,"$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType"); 
		    // starting array
		    arraySquareBrackets = StringUtils.repeatString("[]",1);
		    fieldType = rootNode.addNode(JavaTypePrimitive.class, "int"+arraySquareBrackets);
		    //ending array
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (4120 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (4120 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (4120 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (4120 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (4120 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (4120 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (4120 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType



		// finishing type org.jboss.seam.Component$2
		// #########################################################

		// starting type org.jboss.seam.Component$BijectedAttribute
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeInterface.class, "Component$BijectedAttribute");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (1537 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (1537 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (1537 & Opcodes.ACC_STATIC) != 0;
		isFinal = (1537 & Opcodes.ACC_FINAL) != 0;
		isProtected = (1537 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);



		    // starting method org.jboss.seam.Component$BijectedAttribute#getName()
		    method = newType.addNode(JavaMethodMethod.class,"getName()");
		    method.setSimpleName("getName");
		    isMethodPublic = (1025 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1025 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1025 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1025 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1025 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1025 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedAttribute#getName()

		    // starting method org.jboss.seam.Component$BijectedAttribute#getAnnotation()
		    method = newType.addNode(JavaMethodMethod.class,"getAnnotation()");
		    method.setSimpleName("getAnnotation");
		    isMethodPublic = (1025 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1025 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1025 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1025 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1025 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1025 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Annotation");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedAttribute#getAnnotation()

		    // starting method org.jboss.seam.Component$BijectedAttribute#getType()
		    method = newType.addNode(JavaMethodMethod.class,"getType()");
		    method.setSimpleName("getType");
		    isMethodPublic = (1025 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1025 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1025 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1025 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1025 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1025 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Class");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedAttribute#getType()

		    // starting method org.jboss.seam.Component$BijectedAttribute#set(java.lang.Object, java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"set(java.lang.Object, java.lang.Object)");
		    method.setSimpleName("set");
		    isMethodPublic = (1025 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1025 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1025 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1025 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1025 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1025 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component$BijectedAttribute#set(java.lang.Object, java.lang.Object)

		    // starting method org.jboss.seam.Component$BijectedAttribute#get(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"get(java.lang.Object)");
		    method.setSimpleName("get");
		    isMethodPublic = (1025 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1025 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1025 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1025 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1025 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1025 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$BijectedAttribute#get(java.lang.Object)

		// finishing type org.jboss.seam.Component$BijectedAttribute
		// #########################################################

		// starting type org.jboss.seam.Component$BijectedField
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$BijectedField");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (48 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (48 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (48 & Opcodes.ACC_STATIC) != 0;
		isFinal = (48 & Opcodes.ACC_FINAL) != 0;
		isProtected = (48 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface Component$BijectedAttribute 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "Component$BijectedAttribute");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface Component$BijectedAttribute 

		    // starting field org.jboss.seam.Component$BijectedField#name
			field = newType.addNode(JavaDataField.class,"name"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "String");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (18 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (18 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (18 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (18 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (18 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (18 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (18 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedField#name

		    // starting field org.jboss.seam.Component$BijectedField#field
			field = newType.addNode(JavaDataField.class,"field"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Field");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (18 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (18 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (18 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (18 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (18 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (18 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (18 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedField#field

		    // starting field org.jboss.seam.Component$BijectedField#annotation
			field = newType.addNode(JavaDataField.class,"annotation"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			fieldType = fieldPackage.addNode(JavaType.class, "Annotation");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (18 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (18 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (18 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (18 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (18 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (18 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (18 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedField#annotation

		    // starting field org.jboss.seam.Component$BijectedField#this$0
			field = newType.addNode(JavaDataField.class,"this$0"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			fieldType = fieldPackage.addNode(JavaType.class, "Component");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (4112 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (4112 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (4112 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (4112 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (4112 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (4112 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (4112 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedField#this$0



		    // starting method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation)");
		    method.setSimpleName("Component$BijectedField");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Field");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Annotation");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		    // finishing method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation)

		    // starting method org.jboss.seam.Component$BijectedField#getName()
		    method = newType.addNode(JavaMethodMethod.class,"getName()");
		    method.setSimpleName("getName");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedField#getName()

		    // starting method org.jboss.seam.Component$BijectedField#getField()
		    method = newType.addNode(JavaMethodMethod.class,"getField()");
		    method.setSimpleName("getField");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Field");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedField#getField()

		    // starting method org.jboss.seam.Component$BijectedField#getAnnotation()
		    method = newType.addNode(JavaMethodMethod.class,"getAnnotation()");
		    method.setSimpleName("getAnnotation");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Annotation");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedField#getAnnotation()

		    // starting method org.jboss.seam.Component$BijectedField#getType()
		    method = newType.addNode(JavaMethodMethod.class,"getType()");
		    method.setSimpleName("getType");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Class");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedField#getType()

		    // starting method org.jboss.seam.Component$BijectedField#set(java.lang.Object, java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"set(java.lang.Object, java.lang.Object)");
		    method.setSimpleName("set");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component$BijectedField#set(java.lang.Object, java.lang.Object)

		    // starting method org.jboss.seam.Component$BijectedField#get(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"get(java.lang.Object)");
		    method.setSimpleName("get");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$BijectedField#get(java.lang.Object)

		    // starting method org.jboss.seam.Component$BijectedField#toString()
		    method = newType.addNode(JavaMethodMethod.class,"toString()");
		    method.setSimpleName("toString");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedField#toString()

		    // starting method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation, org.jboss.seam.Component$1)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation, org.jboss.seam.Component$1)");
		    method.setSimpleName("Component$BijectedField");
		    isMethodPublic = (4096 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4096 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4096 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4096 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4096 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4096 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Field");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Annotation");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		        // starting parameter #4
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component$1");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(4);
		        // finishing parameter #4
		    // finishing method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation, org.jboss.seam.Component$1)

		// finishing type org.jboss.seam.Component$BijectedField
		// #########################################################

		// starting type org.jboss.seam.Component$BijectedMethod
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$BijectedMethod");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (48 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (48 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (48 & Opcodes.ACC_STATIC) != 0;
		isFinal = (48 & Opcodes.ACC_FINAL) != 0;
		isProtected = (48 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface Component$BijectedAttribute 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "Component$BijectedAttribute");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface Component$BijectedAttribute 

		    // starting field org.jboss.seam.Component$BijectedMethod#name
			field = newType.addNode(JavaDataField.class,"name"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "String");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (18 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (18 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (18 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (18 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (18 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (18 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (18 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedMethod#name

		    // starting field org.jboss.seam.Component$BijectedMethod#method
			field = newType.addNode(JavaDataField.class,"method"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (18 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (18 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (18 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (18 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (18 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (18 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (18 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedMethod#method

		    // starting field org.jboss.seam.Component$BijectedMethod#annotation
			field = newType.addNode(JavaDataField.class,"annotation"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			fieldType = fieldPackage.addNode(JavaType.class, "Annotation");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (18 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (18 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (18 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (18 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (18 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (18 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (18 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedMethod#annotation

		    // starting field org.jboss.seam.Component$BijectedMethod#this$0
			field = newType.addNode(JavaDataField.class,"this$0"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			fieldType = fieldPackage.addNode(JavaType.class, "Component");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (4112 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (4112 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (4112 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (4112 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (4112 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (4112 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (4112 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedMethod#this$0



		    // starting method org.jboss.seam.Component$BijectedMethod#Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)");
		    method.setSimpleName("Component$BijectedMethod");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Method");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Annotation");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		    // finishing method org.jboss.seam.Component$BijectedMethod#Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)

		    // starting method org.jboss.seam.Component$BijectedMethod#getName()
		    method = newType.addNode(JavaMethodMethod.class,"getName()");
		    method.setSimpleName("getName");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedMethod#getName()

		    // starting method org.jboss.seam.Component$BijectedMethod#getMethod()
		    method = newType.addNode(JavaMethodMethod.class,"getMethod()");
		    method.setSimpleName("getMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Method");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedMethod#getMethod()

		    // starting method org.jboss.seam.Component$BijectedMethod#getAnnotation()
		    method = newType.addNode(JavaMethodMethod.class,"getAnnotation()");
		    method.setSimpleName("getAnnotation");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Annotation");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedMethod#getAnnotation()

		    // starting method org.jboss.seam.Component$BijectedMethod#set(java.lang.Object, java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"set(java.lang.Object, java.lang.Object)");
		    method.setSimpleName("set");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component$BijectedMethod#set(java.lang.Object, java.lang.Object)

		    // starting method org.jboss.seam.Component$BijectedMethod#get(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"get(java.lang.Object)");
		    method.setSimpleName("get");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$BijectedMethod#get(java.lang.Object)

		    // starting method org.jboss.seam.Component$BijectedMethod#getType()
		    method = newType.addNode(JavaMethodMethod.class,"getType()");
		    method.setSimpleName("getType");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Class");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedMethod#getType()

		    // starting method org.jboss.seam.Component$BijectedMethod#toString()
		    method = newType.addNode(JavaMethodMethod.class,"toString()");
		    method.setSimpleName("toString");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedMethod#toString()

		    // starting method org.jboss.seam.Component$BijectedMethod#Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation, org.jboss.seam.Component$1)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation, org.jboss.seam.Component$1)");
		    method.setSimpleName("Component$BijectedMethod");
		    isMethodPublic = (4096 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4096 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4096 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4096 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4096 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4096 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Method");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Annotation");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		        // starting parameter #4
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component$1");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(4);
		        // finishing parameter #4
		    // finishing method org.jboss.seam.Component$BijectedMethod#Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation, org.jboss.seam.Component$1)

		// finishing type org.jboss.seam.Component$BijectedMethod
		// #########################################################

		// starting type org.jboss.seam.Component$BijectedProperty
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$BijectedProperty");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (48 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (48 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (48 & Opcodes.ACC_STATIC) != 0;
		isFinal = (48 & Opcodes.ACC_FINAL) != 0;
		isProtected = (48 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface Component$BijectedAttribute 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "Component$BijectedAttribute");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface Component$BijectedAttribute 

		    // starting field org.jboss.seam.Component$BijectedProperty#getter
			field = newType.addNode(JavaDataField.class,"getter"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			fieldType = fieldPackage.addNode(JavaType.class, "Component$BijectedMethod");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedProperty#getter

		    // starting field org.jboss.seam.Component$BijectedProperty#setter
			field = newType.addNode(JavaDataField.class,"setter"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			fieldType = fieldPackage.addNode(JavaType.class, "Component$BijectedMethod");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedProperty#setter

		    // starting field org.jboss.seam.Component$BijectedProperty#this$0
			field = newType.addNode(JavaDataField.class,"this$0"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			fieldType = fieldPackage.addNode(JavaType.class, "Component");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (4112 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (4112 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (4112 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (4112 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (4112 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (4112 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (4112 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$BijectedProperty#this$0



		    // starting method org.jboss.seam.Component$BijectedProperty#Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.annotation.Annotation)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.annotation.Annotation)");
		    method.setSimpleName("Component$BijectedProperty");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Method");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Method");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		        // starting parameter #4
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Annotation");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(4);
		        // finishing parameter #4
		    // finishing method org.jboss.seam.Component$BijectedProperty#Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.annotation.Annotation)

		    // starting method org.jboss.seam.Component$BijectedProperty#Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)");
		    method.setSimpleName("Component$BijectedProperty");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Method");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Annotation");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		    // finishing method org.jboss.seam.Component$BijectedProperty#Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)

		    // starting method org.jboss.seam.Component$BijectedProperty#get(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"get(java.lang.Object)");
		    method.setSimpleName("get");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$BijectedProperty#get(java.lang.Object)

		    // starting method org.jboss.seam.Component$BijectedProperty#getAnnotation()
		    method = newType.addNode(JavaMethodMethod.class,"getAnnotation()");
		    method.setSimpleName("getAnnotation");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Annotation");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedProperty#getAnnotation()

		    // starting method org.jboss.seam.Component$BijectedProperty#getName()
		    method = newType.addNode(JavaMethodMethod.class,"getName()");
		    method.setSimpleName("getName");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedProperty#getName()

		    // starting method org.jboss.seam.Component$BijectedProperty#getType()
		    method = newType.addNode(JavaMethodMethod.class,"getType()");
		    method.setSimpleName("getType");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Class");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$BijectedProperty#getType()

		    // starting method org.jboss.seam.Component$BijectedProperty#set(java.lang.Object, java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"set(java.lang.Object, java.lang.Object)");
		    method.setSimpleName("set");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component$BijectedProperty#set(java.lang.Object, java.lang.Object)

		// finishing type org.jboss.seam.Component$BijectedProperty
		// #########################################################

		// starting type org.jboss.seam.Component$ConstantInitialValue
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$ConstantInitialValue");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (32 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (32 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (32 & Opcodes.ACC_STATIC) != 0;
		isFinal = (32 & Opcodes.ACC_FINAL) != 0;
		isProtected = (32 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface Component$InitialValue 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "Component$InitialValue");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface Component$InitialValue 

		    // starting field org.jboss.seam.Component$ConstantInitialValue#value
			field = newType.addNode(JavaDataField.class,"value"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Object");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$ConstantInitialValue#value



		    // starting method org.jboss.seam.Component$ConstantInitialValue#Component$ConstantInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$ConstantInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
		    method.setSimpleName("Component$ConstantInitialValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Conversions$PropertyValue");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Type");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		    // finishing method org.jboss.seam.Component$ConstantInitialValue#Component$ConstantInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

		    // starting method org.jboss.seam.Component$ConstantInitialValue#getValue(java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"getValue(java.lang.Class)");
		    method.setSimpleName("getValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$ConstantInitialValue#getValue(java.lang.Class)

		    // starting method org.jboss.seam.Component$ConstantInitialValue#toString()
		    method = newType.addNode(JavaMethodMethod.class,"toString()");
		    method.setSimpleName("toString");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$ConstantInitialValue#toString()

		// finishing type org.jboss.seam.Component$ConstantInitialValue
		// #########################################################

		// starting type org.jboss.seam.Component$ELInitialValue
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$ELInitialValue");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (32 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (32 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (32 & Opcodes.ACC_STATIC) != 0;
		isFinal = (32 & Opcodes.ACC_FINAL) != 0;
		isProtected = (32 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface Component$InitialValue 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "Component$InitialValue");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface Component$InitialValue 

		    // starting field org.jboss.seam.Component$ELInitialValue#expression
			field = newType.addNode(JavaDataField.class,"expression"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "String");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$ELInitialValue#expression

		    // starting field org.jboss.seam.Component$ELInitialValue#converter
			field = newType.addNode(JavaDataField.class,"converter"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Conversions$Converter");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$ELInitialValue#converter

		    // starting field org.jboss.seam.Component$ELInitialValue#parameterType
			field = newType.addNode(JavaDataField.class,"parameterType"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Type");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$ELInitialValue#parameterType



		    // starting method org.jboss.seam.Component$ELInitialValue#Component$ELInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$ELInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
		    method.setSimpleName("Component$ELInitialValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Conversions$PropertyValue");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Type");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		    // finishing method org.jboss.seam.Component$ELInitialValue#Component$ELInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

		    // starting method org.jboss.seam.Component$ELInitialValue#getValue(java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"getValue(java.lang.Class)");
		    method.setSimpleName("getValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$ELInitialValue#getValue(java.lang.Class)

		    // starting method org.jboss.seam.Component$ELInitialValue#createValueExpression()
		    method = newType.addNode(JavaMethodMethod.class,"createValueExpression()");
		    method.setSimpleName("createValueExpression");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.core");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Expressions$ValueExpression");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$ELInitialValue#createValueExpression()

		    // starting method org.jboss.seam.Component$ELInitialValue#createMethodExpression()
		    method = newType.addNode(JavaMethodMethod.class,"createMethodExpression()");
		    method.setSimpleName("createMethodExpression");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.core");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Expressions$MethodExpression");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$ELInitialValue#createMethodExpression()

		    // starting method org.jboss.seam.Component$ELInitialValue#toString()
		    method = newType.addNode(JavaMethodMethod.class,"toString()");
		    method.setSimpleName("toString");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$ELInitialValue#toString()

		// finishing type org.jboss.seam.Component$ELInitialValue
		// #########################################################

		// starting type org.jboss.seam.Component$InitialValue
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeInterface.class, "Component$InitialValue");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (1536 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (1536 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (1536 & Opcodes.ACC_STATIC) != 0;
		isFinal = (1536 & Opcodes.ACC_FINAL) != 0;
		isProtected = (1536 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);



		    // starting method org.jboss.seam.Component$InitialValue#getValue(java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"getValue(java.lang.Class)");
		    method.setSimpleName("getValue");
		    isMethodPublic = (1025 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1025 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1025 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1025 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1025 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1025 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$InitialValue#getValue(java.lang.Class)

		// finishing type org.jboss.seam.Component$InitialValue
		// #########################################################

		// starting type org.jboss.seam.Component$ListInitialValue
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$ListInitialValue");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (32 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (32 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (32 & Opcodes.ACC_STATIC) != 0;
		isFinal = (32 & Opcodes.ACC_FINAL) != 0;
		isProtected = (32 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface Component$InitialValue 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "Component$InitialValue");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface Component$InitialValue 

		    // starting field org.jboss.seam.Component$ListInitialValue#initialValues
			field = newType.addNode(JavaDataField.class,"initialValues"); 
		    // starting array
		    arraySquareBrackets = StringUtils.repeatString("[]",1);
		    fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    fieldType = fieldPackage.addNode(JavaType.class, "Component$InitialValue"+arraySquareBrackets);
		    fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    //ending array
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$ListInitialValue#initialValues

		    // starting field org.jboss.seam.Component$ListInitialValue#elementType
			field = newType.addNode(JavaDataField.class,"elementType"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Class");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$ListInitialValue#elementType

		    // starting field org.jboss.seam.Component$ListInitialValue#isArray
			field = newType.addNode(JavaDataField.class,"isArray"); 
			fieldType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$ListInitialValue#isArray

		    // starting field org.jboss.seam.Component$ListInitialValue#collectionClass
			field = newType.addNode(JavaDataField.class,"collectionClass"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Class");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$ListInitialValue#collectionClass



		    // starting method org.jboss.seam.Component$ListInitialValue#Component$ListInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$ListInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
		    method.setSimpleName("Component$ListInitialValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Conversions$PropertyValue");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Type");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		    // finishing method org.jboss.seam.Component$ListInitialValue#Component$ListInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

		    // starting method org.jboss.seam.Component$ListInitialValue#getValue(java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"getValue(java.lang.Class)");
		    method.setSimpleName("getValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$ListInitialValue#getValue(java.lang.Class)

		    // starting method org.jboss.seam.Component$ListInitialValue#toString()
		    method = newType.addNode(JavaMethodMethod.class,"toString()");
		    method.setSimpleName("toString");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$ListInitialValue#toString()

		// finishing type org.jboss.seam.Component$ListInitialValue
		// #########################################################

		// starting type org.jboss.seam.Component$MapInitialValue
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$MapInitialValue");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (32 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (32 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (32 & Opcodes.ACC_STATIC) != 0;
		isFinal = (32 & Opcodes.ACC_FINAL) != 0;
		isProtected = (32 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface Component$InitialValue 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "Component$InitialValue");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface Component$InitialValue 

		    // starting field org.jboss.seam.Component$MapInitialValue#initialValues
			field = newType.addNode(JavaDataField.class,"initialValues"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Map");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$MapInitialValue#initialValues

		    // starting field org.jboss.seam.Component$MapInitialValue#elementType
			field = newType.addNode(JavaDataField.class,"elementType"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Class");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$MapInitialValue#elementType

		    // starting field org.jboss.seam.Component$MapInitialValue#keyType
			field = newType.addNode(JavaDataField.class,"keyType"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Class");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$MapInitialValue#keyType

		    // starting field org.jboss.seam.Component$MapInitialValue#collectionClass
			field = newType.addNode(JavaDataField.class,"collectionClass"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Class");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$MapInitialValue#collectionClass



		    // starting method org.jboss.seam.Component$MapInitialValue#Component$MapInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$MapInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
		    method.setSimpleName("Component$MapInitialValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Conversions$PropertyValue");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Type");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		    // finishing method org.jboss.seam.Component$MapInitialValue#Component$MapInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

		    // starting method org.jboss.seam.Component$MapInitialValue#getValue(java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"getValue(java.lang.Class)");
		    method.setSimpleName("getValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$MapInitialValue#getValue(java.lang.Class)

		    // starting method org.jboss.seam.Component$MapInitialValue#toString()
		    method = newType.addNode(JavaMethodMethod.class,"toString()");
		    method.setSimpleName("toString");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$MapInitialValue#toString()

		// finishing type org.jboss.seam.Component$MapInitialValue
		// #########################################################

		// starting type org.jboss.seam.Component$SetInitialValue
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component$SetInitialValue");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (32 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (32 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (32 & Opcodes.ACC_STATIC) != 0;
		isFinal = (32 & Opcodes.ACC_FINAL) != 0;
		isProtected = (32 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
		    
		    // starting interface Component$InitialValue 
		    newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    newSuperType = newSuperPackage.addNode(JavaTypeInterface.class, "Component$InitialValue");
		    superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
		    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
		    // ending interface Component$InitialValue 

		    // starting field org.jboss.seam.Component$SetInitialValue#initialValues
			field = newType.addNode(JavaDataField.class,"initialValues"); 
		    // starting array
		    arraySquareBrackets = StringUtils.repeatString("[]",1);
		    fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    fieldType = fieldPackage.addNode(JavaType.class, "Component$InitialValue"+arraySquareBrackets);
		    fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    //ending array
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$SetInitialValue#initialValues

		    // starting field org.jboss.seam.Component$SetInitialValue#elementType
			field = newType.addNode(JavaDataField.class,"elementType"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Class");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$SetInitialValue#elementType

		    // starting field org.jboss.seam.Component$SetInitialValue#collectionClass
			field = newType.addNode(JavaDataField.class,"collectionClass"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Class");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component$SetInitialValue#collectionClass



		    // starting method org.jboss.seam.Component$SetInitialValue#Component$SetInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
		    method = newType.addNode(JavaMethodConstructor.class,"Component$SetInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
		    method.setSimpleName("Component$SetInitialValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Conversions$PropertyValue");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Type");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		    // finishing method org.jboss.seam.Component$SetInitialValue#Component$SetInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

		    // starting method org.jboss.seam.Component$SetInitialValue#getValue(java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"getValue(java.lang.Class)");
		    method.setSimpleName("getValue");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component$SetInitialValue#getValue(java.lang.Class)

		    // starting method org.jboss.seam.Component$SetInitialValue#toString()
		    method = newType.addNode(JavaMethodMethod.class,"toString()");
		    method.setSimpleName("toString");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component$SetInitialValue#toString()

		// finishing type org.jboss.seam.Component$SetInitialValue
		// #########################################################

		// starting type org.jboss.seam.Component
		newPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		newType = newPackage.addNode(JavaTypeClass.class, "Component");
		packageTypeLink = session.addLink(PackageType.class, newPackage, newType, false);
		isPublic = (33 & Opcodes.ACC_PUBLIC) != 0;
		isPrivate = (33 & Opcodes.ACC_PRIVATE) != 0;
		isStatic = (33 & Opcodes.ACC_STATIC) != 0;
		isFinal = (33 & Opcodes.ACC_FINAL) != 0;
		isProtected = (33 & Opcodes.ACC_PROTECTED) != 0;
		newType.setPublic(isPublic);
		newType.setPrivate(isPrivate);
		newType.setStatic(isStatic);
		newType.setFinal(isFinal);
		newType.setProtected(isProtected);
			//superclass
			newSuperPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			newSuperType = newSuperPackage.addNode(JavaTypeClass.class, "Model");
			superPackageTypeLink = session.addLink(PackageType.class, newSuperPackage, newSuperType, false);
			extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);
			//ending superclass

		    // starting field org.jboss.seam.Component#PROPERTIES
			field = newType.addNode(JavaDataField.class,"PROPERTIES"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "String");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (25 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (25 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (25 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (25 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (25 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (25 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (25 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#PROPERTIES

		    // starting field org.jboss.seam.Component#log
			field = newType.addNode(JavaDataField.class,"log"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.log");
			fieldType = fieldPackage.addNode(JavaType.class, "LogProvider");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (26 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (26 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (26 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (26 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (26 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (26 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (26 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#log

		    // starting field org.jboss.seam.Component#type
			field = newType.addNode(JavaDataField.class,"type"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			fieldType = fieldPackage.addNode(JavaType.class, "ComponentType");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#type

		    // starting field org.jboss.seam.Component#name
			field = newType.addNode(JavaDataField.class,"name"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "String");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#name

		    // starting field org.jboss.seam.Component#scope
			field = newType.addNode(JavaDataField.class,"scope"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			fieldType = fieldPackage.addNode(JavaType.class, "ScopeType");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#scope

		    // starting field org.jboss.seam.Component#jndiName
			field = newType.addNode(JavaDataField.class,"jndiName"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "String");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#jndiName

		    // starting field org.jboss.seam.Component#interceptionEnabled
			field = newType.addNode(JavaDataField.class,"interceptionEnabled"); 
			fieldType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#interceptionEnabled

		    // starting field org.jboss.seam.Component#startup
			field = newType.addNode(JavaDataField.class,"startup"); 
			fieldType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#startup

		    // starting field org.jboss.seam.Component#dependencies
			field = newType.addNode(JavaDataField.class,"dependencies"); 
		    // starting array
		    arraySquareBrackets = StringUtils.repeatString("[]",1);
		    fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    fieldType = fieldPackage.addNode(JavaType.class, "String"+arraySquareBrackets);
		    fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    //ending array
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#dependencies

		    // starting field org.jboss.seam.Component#synchronize
			field = newType.addNode(JavaDataField.class,"synchronize"); 
			fieldType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#synchronize

		    // starting field org.jboss.seam.Component#timeout
			field = newType.addNode(JavaDataField.class,"timeout"); 
			fieldType = rootNode.addNode(JavaTypePrimitive.class, "long");
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#timeout

		    // starting field org.jboss.seam.Component#secure
			field = newType.addNode(JavaDataField.class,"secure"); 
			fieldType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#secure

		    // starting field org.jboss.seam.Component#businessInterfaces
			field = newType.addNode(JavaDataField.class,"businessInterfaces"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Set");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#businessInterfaces

		    // starting field org.jboss.seam.Component#destroyMethod
			field = newType.addNode(JavaDataField.class,"destroyMethod"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#destroyMethod

		    // starting field org.jboss.seam.Component#createMethod
			field = newType.addNode(JavaDataField.class,"createMethod"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#createMethod

		    // starting field org.jboss.seam.Component#unwrapMethod
			field = newType.addNode(JavaDataField.class,"unwrapMethod"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#unwrapMethod

		    // starting field org.jboss.seam.Component#defaultRemoveMethod
			field = newType.addNode(JavaDataField.class,"defaultRemoveMethod"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#defaultRemoveMethod

		    // starting field org.jboss.seam.Component#preDestroyMethod
			field = newType.addNode(JavaDataField.class,"preDestroyMethod"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#preDestroyMethod

		    // starting field org.jboss.seam.Component#postConstructMethod
			field = newType.addNode(JavaDataField.class,"postConstructMethod"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#postConstructMethod

		    // starting field org.jboss.seam.Component#prePassivateMethod
			field = newType.addNode(JavaDataField.class,"prePassivateMethod"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#prePassivateMethod

		    // starting field org.jboss.seam.Component#postActivateMethod
			field = newType.addNode(JavaDataField.class,"postActivateMethod"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			fieldType = fieldPackage.addNode(JavaType.class, "Method");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#postActivateMethod

		    // starting field org.jboss.seam.Component#removeMethods
			field = newType.addNode(JavaDataField.class,"removeMethods"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Map");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#removeMethods

		    // starting field org.jboss.seam.Component#lifecycleMethods
			field = newType.addNode(JavaDataField.class,"lifecycleMethods"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Set");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#lifecycleMethods

		    // starting field org.jboss.seam.Component#conversationManagementMethods
			field = newType.addNode(JavaDataField.class,"conversationManagementMethods"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Set");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#conversationManagementMethods

		    // starting field org.jboss.seam.Component#inAttributes
			field = newType.addNode(JavaDataField.class,"inAttributes"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#inAttributes

		    // starting field org.jboss.seam.Component#outAttributes
			field = newType.addNode(JavaDataField.class,"outAttributes"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#outAttributes

		    // starting field org.jboss.seam.Component#parameterSetters
			field = newType.addNode(JavaDataField.class,"parameterSetters"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#parameterSetters

		    // starting field org.jboss.seam.Component#dataModelGetters
			field = newType.addNode(JavaDataField.class,"dataModelGetters"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#dataModelGetters

		    // starting field org.jboss.seam.Component#pcAttributes
			field = newType.addNode(JavaDataField.class,"pcAttributes"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#pcAttributes

		    // starting field org.jboss.seam.Component#dataModelSelectionSetters
			field = newType.addNode(JavaDataField.class,"dataModelSelectionSetters"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Map");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#dataModelSelectionSetters

		    // starting field org.jboss.seam.Component#interceptors
			field = newType.addNode(JavaDataField.class,"interceptors"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#interceptors

		    // starting field org.jboss.seam.Component#clientSideInterceptors
			field = newType.addNode(JavaDataField.class,"clientSideInterceptors"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#clientSideInterceptors

		    // starting field org.jboss.seam.Component#initializerSetters
			field = newType.addNode(JavaDataField.class,"initializerSetters"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Map");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#initializerSetters

		    // starting field org.jboss.seam.Component#initializerFields
			field = newType.addNode(JavaDataField.class,"initializerFields"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Map");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#initializerFields

		    // starting field org.jboss.seam.Component#logFields
			field = newType.addNode(JavaDataField.class,"logFields"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#logFields

		    // starting field org.jboss.seam.Component#logInstances
			field = newType.addNode(JavaDataField.class,"logInstances"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "List");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#logInstances

		    // starting field org.jboss.seam.Component#imports
			field = newType.addNode(JavaDataField.class,"imports"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.util");
			fieldType = fieldPackage.addNode(JavaType.class, "Collection");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#imports

		    // starting field org.jboss.seam.Component#namespace
			field = newType.addNode(JavaDataField.class,"namespace"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			fieldType = fieldPackage.addNode(JavaType.class, "Namespace");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#namespace

		    // starting field org.jboss.seam.Component#perNestedConversation
			field = newType.addNode(JavaDataField.class,"perNestedConversation"); 
			fieldType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#perNestedConversation

		    // starting field org.jboss.seam.Component#factory
			field = newType.addNode(JavaDataField.class,"factory"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "java.lang");
			fieldType = fieldPackage.addNode(JavaType.class, "Class");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (2 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (2 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (2 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (2 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#factory

		    // starting field org.jboss.seam.Component#FINALIZE_FILTER
			field = newType.addNode(JavaDataField.class,"FINALIZE_FILTER"); 
			fieldPackage = rootNode.addNode(JavaPackage.class, "javassist.util.proxy");
			fieldType = fieldPackage.addNode(JavaType.class, "MethodFilter");
			fieldPackageTypeLink = session.addLink(PackageType.class, fieldPackage, fieldType, false);
		    fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
			isFieldPublic = (26 & Opcodes.ACC_PUBLIC) != 0;
			isFieldPrivate = (26 & Opcodes.ACC_PRIVATE) != 0;
			isFieldStatic = (26 & Opcodes.ACC_STATIC) != 0;
			isFieldFinal = (26 & Opcodes.ACC_FINAL) != 0;
			isFieldProtected = (26 & Opcodes.ACC_PROTECTED) != 0;
			isFieldTransient = (26 & Opcodes.ACC_TRANSIENT) != 0;
			isFieldVolatile = (26 & Opcodes.ACC_VOLATILE) != 0;
			field.setPublic(isFieldPublic);
			field.setPrivate(isFieldPrivate);
			field.setStatic(isFieldStatic);
			field.setFinal(isFieldFinal);
			field.setProtected(isFieldProtected);
			field.setTransient(isFieldTransient);
			field.setVolatile(isFieldVolatile);
		    // finishing field org.jboss.seam.Component#FINALIZE_FILTER



		    // starting method org.jboss.seam.Component#Component(java.lang.Class)
		    method = newType.addNode(JavaMethodConstructor.class,"Component(java.lang.Class)");
		    method.setSimpleName("Component");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#Component(java.lang.Class)

		    // starting method org.jboss.seam.Component#Component(java.lang.Class, java.lang.String)
		    method = newType.addNode(JavaMethodConstructor.class,"Component(java.lang.Class, java.lang.String)");
		    method.setSimpleName("Component");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#Component(java.lang.Class, java.lang.String)

		    // starting method org.jboss.seam.Component#Component(java.lang.Class, org.jboss.seam.contexts.Context)
		    method = newType.addNode(JavaMethodConstructor.class,"Component(java.lang.Class, org.jboss.seam.contexts.Context)");
		    method.setSimpleName("Component");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#Component(java.lang.Class, org.jboss.seam.contexts.Context)

		    // starting method org.jboss.seam.Component#Component(java.lang.Class, java.lang.String, org.jboss.seam.ScopeType, boolean, java.lang.String[], java.lang.String)
		    method = newType.addNode(JavaMethodConstructor.class,"Component(java.lang.Class, java.lang.String, org.jboss.seam.ScopeType, boolean, java.lang.String[], java.lang.String)");
		    method.setSimpleName("Component");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "ScopeType");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		        // starting parameter #4
			    // starting array
			    arraySquareBrackets = StringUtils.repeatString("[]",1);
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String"+arraySquareBrackets);
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    //ending array
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(4);
		        // finishing parameter #4
		        // starting parameter #5
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(5);
		        // finishing parameter #5
		    // finishing method org.jboss.seam.Component#Component(java.lang.Class, java.lang.String, org.jboss.seam.ScopeType, boolean, java.lang.String[], java.lang.String)

		    // starting method org.jboss.seam.Component#Component(java.lang.Class, java.lang.String, org.jboss.seam.ScopeType, boolean, java.lang.String[], java.lang.String, org.jboss.seam.contexts.Context)
		    method = newType.addNode(JavaMethodConstructor.class,"Component(java.lang.Class, java.lang.String, org.jboss.seam.ScopeType, boolean, java.lang.String[], java.lang.String, org.jboss.seam.contexts.Context)");
		    method.setSimpleName("Component");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "ScopeType");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		        // starting parameter #4
			    // starting array
			    arraySquareBrackets = StringUtils.repeatString("[]",1);
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String"+arraySquareBrackets);
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    //ending array
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(4);
		        // finishing parameter #4
		        // starting parameter #5
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(5);
		        // finishing parameter #5
		        // starting parameter #6
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(6);
		        // finishing parameter #6
		    // finishing method org.jboss.seam.Component#Component(java.lang.Class, java.lang.String, org.jboss.seam.ScopeType, boolean, java.lang.String[], java.lang.String, org.jboss.seam.contexts.Context)

		    // starting method org.jboss.seam.Component#checkName()
		    method = newType.addNode(JavaMethodMethod.class,"checkName()");
		    method.setSimpleName("checkName");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#checkName()

		    // starting method org.jboss.seam.Component#checkNonabstract()
		    method = newType.addNode(JavaMethodMethod.class,"checkNonabstract()");
		    method.setSimpleName("checkNonabstract");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#checkNonabstract()

		    // starting method org.jboss.seam.Component#initStartup()
		    method = newType.addNode(JavaMethodMethod.class,"initStartup()");
		    method.setSimpleName("initStartup");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#initStartup()

		    // starting method org.jboss.seam.Component#initSynchronize()
		    method = newType.addNode(JavaMethodMethod.class,"initSynchronize()");
		    method.setSimpleName("initSynchronize");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#initSynchronize()

		    // starting method org.jboss.seam.Component#registerConverterOrValidator(org.jboss.seam.contexts.Context)
		    method = newType.addNode(JavaMethodMethod.class,"registerConverterOrValidator(org.jboss.seam.contexts.Context)");
		    method.setSimpleName("registerConverterOrValidator");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#registerConverterOrValidator(org.jboss.seam.contexts.Context)

		    // starting method org.jboss.seam.Component#initNamespace(java.lang.String, org.jboss.seam.contexts.Context)
		    method = newType.addNode(JavaMethodMethod.class,"initNamespace(java.lang.String, org.jboss.seam.contexts.Context)");
		    method.setSimpleName("initNamespace");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#initNamespace(java.lang.String, org.jboss.seam.contexts.Context)

		    // starting method org.jboss.seam.Component#initImports(org.jboss.seam.contexts.Context)
		    method = newType.addNode(JavaMethodMethod.class,"initImports(org.jboss.seam.contexts.Context)");
		    method.setSimpleName("initImports");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#initImports(org.jboss.seam.contexts.Context)

		    // starting method org.jboss.seam.Component#addImport(org.jboss.seam.core.Init, org.jboss.seam.annotations.Import)
		    method = newType.addNode(JavaMethodMethod.class,"addImport(org.jboss.seam.core.Init, org.jboss.seam.annotations.Import)");
		    method.setSimpleName("addImport");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.core");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Init");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.annotations");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Import");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#addImport(org.jboss.seam.core.Init, org.jboss.seam.annotations.Import)

		    // starting method org.jboss.seam.Component#checkScopeForComponentType()
		    method = newType.addNode(JavaMethodMethod.class,"checkScopeForComponentType()");
		    method.setSimpleName("checkScopeForComponentType");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#checkScopeForComponentType()

		    // starting method org.jboss.seam.Component#checkSynchronizedForComponentType()
		    method = newType.addNode(JavaMethodMethod.class,"checkSynchronizedForComponentType()");
		    method.setSimpleName("checkSynchronizedForComponentType");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#checkSynchronizedForComponentType()

		    // starting method org.jboss.seam.Component#checkSerializableForComponentType()
		    method = newType.addNode(JavaMethodMethod.class,"checkSerializableForComponentType()");
		    method.setSimpleName("checkSerializableForComponentType");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#checkSerializableForComponentType()

		    // starting method org.jboss.seam.Component#getJndiName(org.jboss.seam.contexts.Context)
		    method = newType.addNode(JavaMethodMethod.class,"getJndiName(org.jboss.seam.contexts.Context)");
		    method.setSimpleName("getJndiName");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#getJndiName(org.jboss.seam.contexts.Context)

		    // starting method org.jboss.seam.Component#initInitializers(org.jboss.seam.contexts.Context)
		    method = newType.addNode(JavaMethodMethod.class,"initInitializers(org.jboss.seam.contexts.Context)");
		    method.setSimpleName("initInitializers");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#initInitializers(org.jboss.seam.contexts.Context)

		    // starting method org.jboss.seam.Component#getInitialValueHonoringExceptions(java.lang.String, org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
		    method = newType.addNode(JavaMethodMethod.class,"getInitialValueHonoringExceptions(java.lang.String, org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
		    method.setSimpleName("getInitialValueHonoringExceptions");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Component$InitialValue");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Conversions$PropertyValue");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Type");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		    // finishing method org.jboss.seam.Component#getInitialValueHonoringExceptions(java.lang.String, org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

		    // starting method org.jboss.seam.Component#getInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
		    method = newType.addNode(JavaMethodMethod.class,"getInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
		    method.setSimpleName("getInitialValue");
		    isMethodPublic = (10 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (10 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (10 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (10 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (10 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (10 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Component$InitialValue");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Conversions$PropertyValue");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Type");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		    // finishing method org.jboss.seam.Component#getInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

		    // starting method org.jboss.seam.Component#initMembers(java.lang.Class, org.jboss.seam.contexts.Context)
		    method = newType.addNode(JavaMethodMethod.class,"initMembers(java.lang.Class, org.jboss.seam.contexts.Context)");
		    method.setSimpleName("initMembers");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#initMembers(java.lang.Class, org.jboss.seam.contexts.Context)

		    // starting method org.jboss.seam.Component#checkDefaultRemoveMethod()
		    method = newType.addNode(JavaMethodMethod.class,"checkDefaultRemoveMethod()");
		    method.setSimpleName("checkDefaultRemoveMethod");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#checkDefaultRemoveMethod()

		    // starting method org.jboss.seam.Component#scanMethod(org.jboss.seam.contexts.Context, java.util.Map, java.util.Set, java.lang.reflect.Method)
		    method = newType.addNode(JavaMethodMethod.class,"scanMethod(org.jboss.seam.contexts.Context, java.util.Map, java.util.Set, java.lang.reflect.Method)");
		    method.setSimpleName("scanMethod");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.contexts");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Context");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Map");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Set");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Method");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		    // finishing method org.jboss.seam.Component#scanMethod(org.jboss.seam.contexts.Context, java.util.Map, java.util.Set, java.lang.reflect.Method)

		    // starting method org.jboss.seam.Component#scanField(java.util.Map, java.util.Set, java.lang.reflect.Field)
		    method = newType.addNode(JavaMethodMethod.class,"scanField(java.util.Map, java.util.Set, java.lang.reflect.Field)");
		    method.setSimpleName("scanField");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Map");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Set");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Field");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		    // finishing method org.jboss.seam.Component#scanField(java.util.Map, java.util.Set, java.lang.reflect.Field)

		    // starting method org.jboss.seam.Component#checkPersistenceContextForComponentType()
		    method = newType.addNode(JavaMethodMethod.class,"checkPersistenceContextForComponentType()");
		    method.setSimpleName("checkPersistenceContextForComponentType");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#checkPersistenceContextForComponentType()

		    // starting method org.jboss.seam.Component#getDataModelSelectionName(java.util.Set, boolean, java.lang.String, java.lang.annotation.Annotation)
		    method = newType.addNode(JavaMethodMethod.class,"getDataModelSelectionName(java.util.Set, boolean, java.lang.String, java.lang.annotation.Annotation)");
		    method.setSimpleName("getDataModelSelectionName");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Set");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		        // starting parameter #2
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(2);
		        // finishing parameter #2
		        // starting parameter #3
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.annotation");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Annotation");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(3);
		        // finishing parameter #3
		    // finishing method org.jboss.seam.Component#getDataModelSelectionName(java.util.Set, boolean, java.lang.String, java.lang.annotation.Annotation)

		    // starting method org.jboss.seam.Component#checkDataModelScope(org.jboss.seam.annotations.datamodel.DataModel)
		    method = newType.addNode(JavaMethodMethod.class,"checkDataModelScope(org.jboss.seam.annotations.datamodel.DataModel)");
		    method.setSimpleName("checkDataModelScope");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.annotations.datamodel");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "DataModel");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#checkDataModelScope(org.jboss.seam.annotations.datamodel.DataModel)

		    // starting method org.jboss.seam.Component#initInterceptors()
		    method = newType.addNode(JavaMethodMethod.class,"initInterceptors()");
		    method.setSimpleName("initInterceptors");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#initInterceptors()

		    // starting method org.jboss.seam.Component#addInterceptor(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"addInterceptor(java.lang.Object)");
		    method.setSimpleName("addInterceptor");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#addInterceptor(java.lang.Object)

		    // starting method org.jboss.seam.Component#addInterceptor(org.jboss.seam.intercept.Interceptor)
		    method = newType.addNode(JavaMethodMethod.class,"addInterceptor(org.jboss.seam.intercept.Interceptor)");
		    method.setSimpleName("addInterceptor");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.intercept");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Interceptor");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#addInterceptor(org.jboss.seam.intercept.Interceptor)

		    // starting method org.jboss.seam.Component#newSort(java.util.List)
		    method = newType.addNode(JavaMethodMethod.class,"newSort(java.util.List)");
		    method.setSimpleName("newSort");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "List");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "List");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#newSort(java.util.List)

		    // starting method org.jboss.seam.Component#initDefaultInterceptors()
		    method = newType.addNode(JavaMethodMethod.class,"initDefaultInterceptors()");
		    method.setSimpleName("initDefaultInterceptors");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#initDefaultInterceptors()

		    // starting method org.jboss.seam.Component#initSecurity()
		    method = newType.addNode(JavaMethodMethod.class,"initSecurity()");
		    method.setSimpleName("initSecurity");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#initSecurity()

		    // starting method org.jboss.seam.Component#hasAnnotation(java.lang.Class, java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"hasAnnotation(java.lang.Class, java.lang.Class)");
		    method.setSimpleName("hasAnnotation");
		    isMethodPublic = (10 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (10 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (10 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (10 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (10 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (10 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#hasAnnotation(java.lang.Class, java.lang.Class)

		    // starting method org.jboss.seam.Component#hasAnnotation(java.lang.Class, java.lang.String)
		    method = newType.addNode(JavaMethodMethod.class,"hasAnnotation(java.lang.Class, java.lang.String)");
		    method.setSimpleName("hasAnnotation");
		    isMethodPublic = (10 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (10 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (10 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (10 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (10 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (10 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#hasAnnotation(java.lang.Class, java.lang.String)

		    // starting method org.jboss.seam.Component#beanClassHasAnnotation(java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"beanClassHasAnnotation(java.lang.Class)");
		    method.setSimpleName("beanClassHasAnnotation");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#beanClassHasAnnotation(java.lang.Class)

		    // starting method org.jboss.seam.Component#beanClassHasAnnotation(java.lang.String)
		    method = newType.addNode(JavaMethodMethod.class,"beanClassHasAnnotation(java.lang.String)");
		    method.setSimpleName("beanClassHasAnnotation");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#beanClassHasAnnotation(java.lang.String)

		    // starting method org.jboss.seam.Component#businessInterfaceHasAnnotation(java.lang.Class)
		    method = newType.addNode(JavaMethodMethod.class,"businessInterfaceHasAnnotation(java.lang.Class)");
		    method.setSimpleName("businessInterfaceHasAnnotation");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Class");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#businessInterfaceHasAnnotation(java.lang.Class)

		    // starting method org.jboss.seam.Component#getName()
		    method = newType.addNode(JavaMethodMethod.class,"getName()");
		    method.setSimpleName("getName");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "String");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getName()

		    // starting method org.jboss.seam.Component#getType()
		    method = newType.addNode(JavaMethodMethod.class,"getType()");
		    method.setSimpleName("getType");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "ComponentType");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getType()

		    // starting method org.jboss.seam.Component#getScope()
		    method = newType.addNode(JavaMethodMethod.class,"getScope()");
		    method.setSimpleName("getScope");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "ScopeType");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getScope()

		    // starting method org.jboss.seam.Component#getInterceptors(org.jboss.seam.annotations.intercept.InterceptorType)
		    method = newType.addNode(JavaMethodMethod.class,"getInterceptors(org.jboss.seam.annotations.intercept.InterceptorType)");
		    method.setSimpleName("getInterceptors");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "List");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.annotations.intercept");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "InterceptorType");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#getInterceptors(org.jboss.seam.annotations.intercept.InterceptorType)

		    // starting method org.jboss.seam.Component#createUserInterceptors(org.jboss.seam.annotations.intercept.InterceptorType)
		    method = newType.addNode(JavaMethodMethod.class,"createUserInterceptors(org.jboss.seam.annotations.intercept.InterceptorType)");
		    method.setSimpleName("createUserInterceptors");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "List");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam.annotations.intercept");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "InterceptorType");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#createUserInterceptors(org.jboss.seam.annotations.intercept.InterceptorType)

		    // starting method org.jboss.seam.Component#getServerSideInterceptors()
		    method = newType.addNode(JavaMethodMethod.class,"getServerSideInterceptors()");
		    method.setSimpleName("getServerSideInterceptors");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "List");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getServerSideInterceptors()

		    // starting method org.jboss.seam.Component#getClientSideInterceptors()
		    method = newType.addNode(JavaMethodMethod.class,"getClientSideInterceptors()");
		    method.setSimpleName("getClientSideInterceptors");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "List");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getClientSideInterceptors()

		    // starting method org.jboss.seam.Component#getDestroyMethod()
		    method = newType.addNode(JavaMethodMethod.class,"getDestroyMethod()");
		    method.setSimpleName("getDestroyMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Method");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getDestroyMethod()

		    // starting method org.jboss.seam.Component#getRemoveMethods()
		    method = newType.addNode(JavaMethodMethod.class,"getRemoveMethods()");
		    method.setSimpleName("getRemoveMethods");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Collection");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getRemoveMethods()

		    // starting method org.jboss.seam.Component#getRemoveMethod(java.lang.String)
		    method = newType.addNode(JavaMethodMethod.class,"getRemoveMethod(java.lang.String)");
		    method.setSimpleName("getRemoveMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Method");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "String");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#getRemoveMethod(java.lang.String)

		    // starting method org.jboss.seam.Component#hasPreDestroyMethod()
		    method = newType.addNode(JavaMethodMethod.class,"hasPreDestroyMethod()");
		    method.setSimpleName("hasPreDestroyMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#hasPreDestroyMethod()

		    // starting method org.jboss.seam.Component#hasPostConstructMethod()
		    method = newType.addNode(JavaMethodMethod.class,"hasPostConstructMethod()");
		    method.setSimpleName("hasPostConstructMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#hasPostConstructMethod()

		    // starting method org.jboss.seam.Component#hasPrePassivateMethod()
		    method = newType.addNode(JavaMethodMethod.class,"hasPrePassivateMethod()");
		    method.setSimpleName("hasPrePassivateMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#hasPrePassivateMethod()

		    // starting method org.jboss.seam.Component#hasPostActivateMethod()
		    method = newType.addNode(JavaMethodMethod.class,"hasPostActivateMethod()");
		    method.setSimpleName("hasPostActivateMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#hasPostActivateMethod()

		    // starting method org.jboss.seam.Component#hasDestroyMethod()
		    method = newType.addNode(JavaMethodMethod.class,"hasDestroyMethod()");
		    method.setSimpleName("hasDestroyMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#hasDestroyMethod()

		    // starting method org.jboss.seam.Component#hasCreateMethod()
		    method = newType.addNode(JavaMethodMethod.class,"hasCreateMethod()");
		    method.setSimpleName("hasCreateMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#hasCreateMethod()

		    // starting method org.jboss.seam.Component#getCreateMethod()
		    method = newType.addNode(JavaMethodMethod.class,"getCreateMethod()");
		    method.setSimpleName("getCreateMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Method");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getCreateMethod()

		    // starting method org.jboss.seam.Component#hasUnwrapMethod()
		    method = newType.addNode(JavaMethodMethod.class,"hasUnwrapMethod()");
		    method.setSimpleName("hasUnwrapMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#hasUnwrapMethod()

		    // starting method org.jboss.seam.Component#getUnwrapMethod()
		    method = newType.addNode(JavaMethodMethod.class,"getUnwrapMethod()");
		    method.setSimpleName("getUnwrapMethod");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang.reflect");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Method");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getUnwrapMethod()

		    // starting method org.jboss.seam.Component#getOutAttributes()
		    method = newType.addNode(JavaMethodMethod.class,"getOutAttributes()");
		    method.setSimpleName("getOutAttributes");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "List");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getOutAttributes()

		    // starting method org.jboss.seam.Component#getInAttributes()
		    method = newType.addNode(JavaMethodMethod.class,"getInAttributes()");
		    method.setSimpleName("getInAttributes");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.util");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "List");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getInAttributes()

		    // starting method org.jboss.seam.Component#needsInjection()
		    method = newType.addNode(JavaMethodMethod.class,"needsInjection()");
		    method.setSimpleName("needsInjection");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#needsInjection()

		    // starting method org.jboss.seam.Component#needsOutjection()
		    method = newType.addNode(JavaMethodMethod.class,"needsOutjection()");
		    method.setSimpleName("needsOutjection");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#needsOutjection()

		    // starting method org.jboss.seam.Component#instantiate()
		    method = newType.addNode(JavaMethodMethod.class,"instantiate()");
		    method.setSimpleName("instantiate");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		    
		    // finishing method org.jboss.seam.Component#instantiate()

		    // starting method org.jboss.seam.Component#postConstruct(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"postConstruct(java.lang.Object)");
		    method.setSimpleName("postConstruct");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#postConstruct(java.lang.Object)

		    // starting method org.jboss.seam.Component#instantiateSessionBean()
		    method = newType.addNode(JavaMethodMethod.class,"instantiateSessionBean()");
		    method.setSimpleName("instantiateSessionBean");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		        // starting throws exception javax.naming.NamingException
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "javax.naming");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "NamingException");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception javax.naming.NamingException
		    
		    // finishing method org.jboss.seam.Component#instantiateSessionBean()

		    // starting method org.jboss.seam.Component#postConstructSessionBean(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"postConstructSessionBean(java.lang.Object)");
		    method.setSimpleName("postConstructSessionBean");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		        // starting throws exception javax.naming.NamingException
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "javax.naming");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "NamingException");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception javax.naming.NamingException
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#postConstructSessionBean(java.lang.Object)

		    // starting method org.jboss.seam.Component#instantiateEntityBean()
		    method = newType.addNode(JavaMethodMethod.class,"instantiateEntityBean()");
		    method.setSimpleName("instantiateEntityBean");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		    
		    // finishing method org.jboss.seam.Component#instantiateEntityBean()

		    // starting method org.jboss.seam.Component#postConstructEntityBean(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"postConstructEntityBean(java.lang.Object)");
		    method.setSimpleName("postConstructEntityBean");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#postConstructEntityBean(java.lang.Object)

		    // starting method org.jboss.seam.Component#instantiateJavaBean()
		    method = newType.addNode(JavaMethodMethod.class,"instantiateJavaBean()");
		    method.setSimpleName("instantiateJavaBean");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		    
		    // finishing method org.jboss.seam.Component#instantiateJavaBean()

		    // starting method org.jboss.seam.Component#postConstructJavaBean(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"postConstructJavaBean(java.lang.Object)");
		    method.setSimpleName("postConstructJavaBean");
		    isMethodPublic = (4 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (4 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (4 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (4 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (4 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (4 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#postConstructJavaBean(java.lang.Object)

		    // starting method org.jboss.seam.Component#destroy(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"destroy(java.lang.Object)");
		    method.setSimpleName("destroy");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#destroy(java.lang.Object)

		    // starting method org.jboss.seam.Component#wrap(java.lang.Object, javassist.util.proxy.MethodHandler)
		    method = newType.addNode(JavaMethodMethod.class,"wrap(java.lang.Object, javassist.util.proxy.MethodHandler)");
		    method.setSimpleName("wrap");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Object");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "javassist.util.proxy");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "MethodHandler");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#wrap(java.lang.Object, javassist.util.proxy.MethodHandler)

		    // starting method org.jboss.seam.Component#getProxyFactory()
		    method = newType.addNode(JavaMethodMethod.class,"getProxyFactory()");
		    method.setSimpleName("getProxyFactory");
		    isMethodPublic = (34 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (34 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (34 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (34 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (34 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (34 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
		    methodReturnTypeType = methodReturnTypePackage.addNode(JavaType.class, "Class");
		    methodReturnTypePackageTypeLink = session.addLink(PackageType.class, methodReturnTypePackage, methodReturnTypeType, false);
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		    // finishing method org.jboss.seam.Component#getProxyFactory()

		    // starting method org.jboss.seam.Component#initialize(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"initialize(java.lang.Object)");
		    method.setSimpleName("initialize");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		        // starting throws exception java.lang.Exception
		        newExceptionPackage = rootNode.addNode(JavaPackage.class, "java.lang");
		        newExceptionType = newExceptionPackage.addNode(JavaTypeClass.class, "Exception");
		        exceptionPackageTypeLink = session.addLink(PackageType.class, newExceptionPackage, newExceptionType, false);
		        methodThrowsType = session.addLink(MethodThrows.class, method, newExceptionType, false);
		        // ending throws exception java.lang.Exception
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#initialize(java.lang.Object)

		    // starting method org.jboss.seam.Component#inject(java.lang.Object, boolean)
		    method = newType.addNode(JavaMethodMethod.class,"inject(java.lang.Object, boolean)");
		    method.setSimpleName("inject");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#inject(java.lang.Object, boolean)

		    // starting method org.jboss.seam.Component#disinject(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"disinject(java.lang.Object)");
		    method.setSimpleName("disinject");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#disinject(java.lang.Object)

		    // starting method org.jboss.seam.Component#injectLog(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"injectLog(java.lang.Object)");
		    method.setSimpleName("injectLog");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#injectLog(java.lang.Object)

		    // starting method org.jboss.seam.Component#injectParameters(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"injectParameters(java.lang.Object)");
		    method.setSimpleName("injectParameters");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#injectParameters(java.lang.Object)

		    // starting method org.jboss.seam.Component#outject(java.lang.Object, boolean)
		    method = newType.addNode(JavaMethodMethod.class,"outject(java.lang.Object, boolean)");
		    method.setSimpleName("outject");
		    isMethodPublic = (1 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (1 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (1 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (1 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (1 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (1 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypeType = rootNode.addNode(JavaTypePrimitive.class, "boolean");
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#outject(java.lang.Object, boolean)

		    // starting method org.jboss.seam.Component#injectDataModelSelections(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"injectDataModelSelections(java.lang.Object)");
		    method.setSimpleName("injectDataModelSelections");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#injectDataModelSelections(java.lang.Object)

		    // starting method org.jboss.seam.Component#injectDataModelSelection(java.lang.Object, org.jboss.seam.Component$BijectedAttribute)
		    method = newType.addNode(JavaMethodMethod.class,"injectDataModelSelection(java.lang.Object, org.jboss.seam.Component$BijectedAttribute)");
		    method.setSimpleName("injectDataModelSelection");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		        // starting parameter #1
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "org.jboss.seam");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Component$BijectedAttribute");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(1);
		        // finishing parameter #1
		    // finishing method org.jboss.seam.Component#injectDataModelSelection(java.lang.Object, org.jboss.seam.Component$BijectedAttribute)

		    // starting method org.jboss.seam.Component#outjectDataModels(java.lang.Object)
		    method = newType.addNode(JavaMethodMethod.class,"outjectDataModels(java.lang.Object)");
		    method.setSimpleName("outjectDataModels");
		    isMethodPublic = (2 & Opcodes.ACC_PUBLIC) != 0;
		    isMethodPrivate = (2 & Opcodes.ACC_PRIVATE) != 0;
		    isMethodStatic = (2 & Opcodes.ACC_STATIC) != 0;
		    isMethodFinal = (2 & Opcodes.ACC_FINAL) != 0;
		    isMethodProtected = (2 & Opcodes.ACC_PROTECTED) != 0;
		    isMethodSynchronized = (2 & Opcodes.ACC_SYNCHRONIZED) != 0;
		    method.setPublic(isMethodPublic);
		    method.setPrivate(isMethodPrivate);
		    method.setStatic(isMethodStatic);
		    method.setFinal(isMethodFinal);
		    method.setProtected(isMethodProtected);
		    method.setSynchronized(isMethodSynchronized);
		    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
		    
		    // starting method return 
		    methodReturnTypeType = rootNode.addNode(JavaTypePrimitive.class, "void");
		    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
		    // finishing method return 
		    
		        // starting parameter #0
			    methodParameterTypePackage = rootNode.addNode(JavaPackage.class, "java.lang");
			    methodParameterTypeType = methodParameterTypePackage.addNode(JavaType.class, "Object");
			    methodParameterTypePackageTypeLink = session.addLink(PackageType.class, methodParameterTypePackage, methodParameterTypeType, false);
			    methodParametersType = session.addLink(MethodParameterDeclare.class, method, methodParameterTypeType, false);
		        methodParametersType.setOrder(0);
		        // finishing parameter #0
		    // finishing method org.jboss.seam.Component#outjectDataModels(java.lang.Object)


	}
}
