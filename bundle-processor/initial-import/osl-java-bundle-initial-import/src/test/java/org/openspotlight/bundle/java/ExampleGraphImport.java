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

import org.junit.Test;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.parser.dap.language.java.link.DataType;
import org.openspotlight.parser.dap.language.java.link.Extends;
import org.openspotlight.parser.dap.language.java.link.Implements;
import org.openspotlight.parser.dap.language.java.link.MethodParameterDefinition;
import org.openspotlight.parser.dap.language.java.link.MethodReturns;
import org.openspotlight.parser.dap.language.java.link.MethodThrows;
import org.openspotlight.parser.dap.language.java.link.PackageType;
import org.openspotlight.parser.dap.language.java.link.TypeDeclares;
import org.openspotlight.parser.dap.language.java.node.JavaDataField;
import org.openspotlight.parser.dap.language.java.node.JavaDataParameter;
import org.openspotlight.parser.dap.language.java.node.JavaMethod;
import org.openspotlight.parser.dap.language.java.node.JavaMethodConstructor;
import org.openspotlight.parser.dap.language.java.node.JavaMethodMethod;
import org.openspotlight.parser.dap.language.java.node.JavaPackage;
import org.openspotlight.parser.dap.language.java.node.JavaType;
import org.openspotlight.parser.dap.language.java.node.JavaTypeClass;
import org.openspotlight.parser.dap.language.java.node.JavaTypeEnum;
import org.openspotlight.parser.dap.language.java.node.JavaTypeInterface;
import org.openspotlight.parser.dap.language.java.node.JavaTypePrimitive;
import org.openspotlight.tool.dap.language.java.JavaGraphNodeHelper;




public class ExampleGraphImport {

	@Test
	public void shouldImportSomeData() throws Exception {
		

		SLGraphFactory factory = new SLGraphFactoryImpl();
		SLGraph graph = factory.createTempGraph(true);
		SLGraphSession session = graph.openSession();
		SLNode currentContextRootNode = session.createContext("sample").getRootNode();
		SLNode abstractContextRootNode = session.createContext("abstractJavaContext").getRootNode();

		JavaGraphNodeHelper helper = new JavaGraphNodeHelper(session, currentContextRootNode,abstractContextRootNode);

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
		MethodParameterDefinition methodParametersType;
		JavaPackage newExceptionPackage;
		JavaType newExceptionType;
		PackageType exceptionPackageTypeLink;
		MethodThrows methodThrowsType;
		JavaPackage methodReturnTypePackage;
		PackageType methodReturnTypePackageTypeLink;
		String arraySquareBrackets;
		Extends extendsSuper;
		Implements implementsSuper;
		boolean isArray = false;
		int arrayDimensions = 0;

		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$2", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam", "Component$BijectedAttribute", 1537);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$BijectedField", 48);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$BijectedMethod", 48);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$BijectedProperty", 48);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$ConstantInitialValue", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$ELInitialValue", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam", "Component$InitialValue", 1536);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$ListInitialValue", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$MapInitialValue", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$SetInitialValue", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "ComponentType$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class,"org.jboss.seam", "ComponentType", 16433);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "ConcurrentRequestTimeoutException", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "CyclicDependencyException", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Entity$NotEntityException", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Entity", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam", "Instance", 1537);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "InstantiationException", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Model", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Namespace", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "NoConversationException", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "RequiredException", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "ScopeType$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class,"org.jboss.seam", "ScopeType", 16433);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Seam$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Seam", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "ApplicationException", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "AutoCreate", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Begin", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Conversational", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Create", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "DataBinderClass", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "DataSelectorClass", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Destroy", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "End", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Factory", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class,"org.jboss.seam.annotations", "FlushModeType", 16433);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Import", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "In", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Install", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "JndiName", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Logger", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Name", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Namespace", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Observer", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Out", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.annotations", "Outcome", 131105);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "PerNestedConversation", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "RaiseEvent", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "ReadOnly", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Role", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Roles", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Scope", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Startup", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Synchronized", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.annotations", "TransactionPropagationType$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class,"org.jboss.seam.annotations", "TransactionPropagationType", 16433);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Transactional", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations", "Unwrap", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.async", "Asynchronous", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.async", "Duration", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.async", "Expiration", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.async", "FinalExpiration", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.async", "IntervalCron", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.async", "IntervalDuration", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.bpm", "BeginTask", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.bpm", "CreateProcess", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.bpm", "EndTask", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.bpm", "ResumeProcess", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.bpm", "StartTask", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.bpm", "Transition", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.datamodel", "DataModel", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.datamodel", "DataModelSelection", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.datamodel", "DataModelSelectionIndex", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.exception", "HttpError", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.exception", "Redirect", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.faces", "Converter", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.faces", "Validator", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.intercept", "AroundInvoke", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.intercept", "BypassInterceptors", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.intercept", "Interceptor", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class,"org.jboss.seam.annotations.intercept", "InterceptorType", 16433);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.intercept", "Interceptors", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.intercept", "PostActivate", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.intercept", "PrePassivate", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "Admin", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "Delete", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "Insert", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "PermissionCheck", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "Read", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "Restrict", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "RoleCheck", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "TokenUsername", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "TokenValue", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security", "Update", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "RoleConditional", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "RoleGroups", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "RoleName", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "UserEnabled", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "UserFirstName", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "UserLastName", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "UserPassword", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "UserPrincipal", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.management", "UserRoles", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.permission", "Identifier", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.permission", "Permission", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.permission", "PermissionAction", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.permission", "PermissionDiscriminator", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.permission", "PermissionRole", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.permission", "PermissionTarget", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.permission", "PermissionUser", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.security.permission", "Permissions", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.web", "Filter", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.annotations.web", "RequestParameter", 9729);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AbstractDispatcher$DispatcherParameters", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AbstractDispatcher", 1057);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "Asynchronous$ContextualAsynchronousRequest", 1057);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "Asynchronous", 1057);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AsynchronousEvent$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AsynchronousEvent$2", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AsynchronousEvent", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AsynchronousExceptionHandler", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AsynchronousInterceptor", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AsynchronousInvocation$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AsynchronousInvocation$2", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "AsynchronousInvocation", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "CronSchedule", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.async", "Dispatcher", 1537);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.async", "LocalTimerServiceDispatcher", 1537);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "QuartzDispatcher$QuartzJob", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "QuartzDispatcher", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "QuartzTriggerHandle", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "Schedule", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "ThreadPoolDispatcher$RunnableAsynchronous", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "ThreadPoolDispatcher", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerSchedule", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher$TimerHandleProxy$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher$TimerHandleProxy", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher$TimerProxy$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher$TimerProxy$2", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher$TimerProxy$3", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher$TimerProxy$4", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher$TimerProxy$5", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher$TimerProxy", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TimerServiceDispatcher", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TransactionCompletionEvent", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.async", "TransactionSuccessEvent", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class,"org.jboss.seam.async", "package-info", 512);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "Actor$1$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "Actor$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "Actor", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "BusinessProcess", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "BusinessProcessInterceptor", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "Jbpm$SeamSubProcessResolver", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "Jbpm", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "JbpmELResolver", 48);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "ManagedJbpmContext", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "PageflowDeploymentHandler$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "PageflowDeploymentHandler", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "PageflowParser", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "PooledTask", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "PooledTaskInstanceList", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "ProcessInstance$1", 32);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "ProcessInstance", 33);
		newType = helper.addBeforeTypeProcessing(JavaTypeClass.class,"org.jboss.seam.bpm", "ProcessInstanceFinder", 33);
		// #########################################################

	    newType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$2");
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"java.lang","Object");
	    extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);

	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$ComponentType
		field = newType.addNode(JavaDataField.class,"$SwitchMap$org$jboss$seam$ComponentType"); 
	    // starting array
	    arrayDimensions = 1;
	    fieldType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "","int");
	    //ending array
	    helper.insertFieldData(field, fieldType, 4120, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$ComponentType
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType
		field = newType.addNode(JavaDataField.class,"$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType"); 
	    // starting array
	    arrayDimensions = 1;
	    fieldType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "","int");
	    //ending array
	    helper.insertFieldData(field, fieldType, 4120, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType


	// finishing type org.jboss.seam.Component$2
	// #########################################################

	    newType = helper.addAfterTypeProcessing(JavaTypeInterface.class,"org.jboss.seam", "Component$BijectedAttribute");
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"java.lang","Object");
	    extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);



	    // starting method org.jboss.seam.Component$BijectedAttribute#getName()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getName()");
	    method.setSimpleName("getName");
	    helper.setMethodData(method,1025);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedAttribute#getName()

	    // starting method org.jboss.seam.Component$BijectedAttribute#getAnnotation()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getAnnotation()");
	    method.setSimpleName("getAnnotation");
	    helper.setMethodData(method,1025);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedAttribute#getAnnotation()

	    // starting method org.jboss.seam.Component$BijectedAttribute#getType()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getType()");
	    method.setSimpleName("getType");
	    helper.setMethodData(method,1025);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Class");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedAttribute#getType()

	    // starting method org.jboss.seam.Component$BijectedAttribute#set(java.lang.Object, java.lang.Object)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"set(java.lang.Object, java.lang.Object)");
	    method.setSimpleName("set");
	    helper.setMethodData(method,1025);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
	    // finishing method org.jboss.seam.Component$BijectedAttribute#set(java.lang.Object, java.lang.Object)

	    // starting method org.jboss.seam.Component$BijectedAttribute#get(java.lang.Object)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"get(java.lang.Object)");
	    method.setSimpleName("get");
	    helper.setMethodData(method,1025);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
	    // finishing method org.jboss.seam.Component$BijectedAttribute#get(java.lang.Object)

	// finishing type org.jboss.seam.Component$BijectedAttribute
	// #########################################################

	    newType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$BijectedField");
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"java.lang","Object");
	    extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);
	    
	    // starting interface Component$BijectedAttribute 
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam","Component$BijectedAttribute");
	    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
	    // ending interface Component$BijectedAttribute 

	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedField#name
		field = newType.addNode(JavaDataField.class,"name"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    helper.insertFieldData(field, fieldType, 18, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedField#name
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedField#field
		field = newType.addNode(JavaDataField.class,"field"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Field");
	    helper.insertFieldData(field, fieldType, 18, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedField#field
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedField#annotation
		field = newType.addNode(JavaDataField.class,"annotation"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
	    helper.insertFieldData(field, fieldType, 18, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedField#annotation
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedField#this$0
		field = newType.addNode(JavaDataField.class,"this$0"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
	    helper.insertFieldData(field, fieldType, 4112, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedField#this$0


	    // starting method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation)");
	    method.setSimpleName("Component$BijectedField");
	    helper.setMethodData(method,2);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #2
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Field");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(2);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #2
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #3
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(3);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #3
	    // finishing method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation)

	    // starting method org.jboss.seam.Component$BijectedField#getName()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getName()");
	    method.setSimpleName("getName");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedField#getName()

	    // starting method org.jboss.seam.Component$BijectedField#getField()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getField()");
	    method.setSimpleName("getField");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Field");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedField#getField()

	    // starting method org.jboss.seam.Component$BijectedField#getAnnotation()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getAnnotation()");
	    method.setSimpleName("getAnnotation");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedField#getAnnotation()

	    // starting method org.jboss.seam.Component$BijectedField#getType()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getType()");
	    method.setSimpleName("getType");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Class");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedField#getType()

	    // starting method org.jboss.seam.Component$BijectedField#set(java.lang.Object, java.lang.Object)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"set(java.lang.Object, java.lang.Object)");
	    method.setSimpleName("set");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
	    // finishing method org.jboss.seam.Component$BijectedField#set(java.lang.Object, java.lang.Object)

	    // starting method org.jboss.seam.Component$BijectedField#get(java.lang.Object)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"get(java.lang.Object)");
	    method.setSimpleName("get");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
	    // finishing method org.jboss.seam.Component$BijectedField#get(java.lang.Object)

	    // starting method org.jboss.seam.Component$BijectedField#toString()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"toString()");
	    method.setSimpleName("toString");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedField#toString()

	    // starting method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation, org.jboss.seam.Component$1)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation, org.jboss.seam.Component$1)");
	    method.setSimpleName("Component$BijectedField");
	    helper.setMethodData(method,4096);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #2
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Field");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(2);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #2
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #3
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(3);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #3
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #4
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component$1");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(4);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #4
	    // finishing method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation, org.jboss.seam.Component$1)

	// finishing type org.jboss.seam.Component$BijectedField
	// #########################################################

	    newType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$BijectedMethod");
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"java.lang","Object");
	    extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);
	    
	    // starting interface Component$BijectedAttribute 
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam","Component$BijectedAttribute");
	    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
	    // ending interface Component$BijectedAttribute 

	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedMethod#name
		field = newType.addNode(JavaDataField.class,"name"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    helper.insertFieldData(field, fieldType, 18, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedMethod#name
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedMethod#method
		field = newType.addNode(JavaDataField.class,"method"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Method");
	    helper.insertFieldData(field, fieldType, 18, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedMethod#method
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedMethod#annotation
		field = newType.addNode(JavaDataField.class,"annotation"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
	    helper.insertFieldData(field, fieldType, 18, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedMethod#annotation
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedMethod#this$0
		field = newType.addNode(JavaDataField.class,"this$0"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
	    helper.insertFieldData(field, fieldType, 4112, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedMethod#this$0


	    // starting method org.jboss.seam.Component$BijectedMethod#Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)");
	    method.setSimpleName("Component$BijectedMethod");
	    helper.setMethodData(method,2);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #2
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Method");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(2);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #2
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #3
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(3);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #3
	    // finishing method org.jboss.seam.Component$BijectedMethod#Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)

	    // starting method org.jboss.seam.Component$BijectedMethod#getName()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getName()");
	    method.setSimpleName("getName");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedMethod#getName()

	    // starting method org.jboss.seam.Component$BijectedMethod#getMethod()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getMethod()");
	    method.setSimpleName("getMethod");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Method");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedMethod#getMethod()

	    // starting method org.jboss.seam.Component$BijectedMethod#getAnnotation()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getAnnotation()");
	    method.setSimpleName("getAnnotation");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedMethod#getAnnotation()

	    // starting method org.jboss.seam.Component$BijectedMethod#set(java.lang.Object, java.lang.Object)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"set(java.lang.Object, java.lang.Object)");
	    method.setSimpleName("set");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
	    // finishing method org.jboss.seam.Component$BijectedMethod#set(java.lang.Object, java.lang.Object)

	    // starting method org.jboss.seam.Component$BijectedMethod#get(java.lang.Object)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"get(java.lang.Object)");
	    method.setSimpleName("get");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
	    // finishing method org.jboss.seam.Component$BijectedMethod#get(java.lang.Object)

	    // starting method org.jboss.seam.Component$BijectedMethod#getType()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getType()");
	    method.setSimpleName("getType");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Class");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedMethod#getType()

	    // starting method org.jboss.seam.Component$BijectedMethod#toString()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"toString()");
	    method.setSimpleName("toString");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedMethod#toString()

	    // starting method org.jboss.seam.Component$BijectedMethod#Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation, org.jboss.seam.Component$1)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation, org.jboss.seam.Component$1)");
	    method.setSimpleName("Component$BijectedMethod");
	    helper.setMethodData(method,4096);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #2
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Method");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(2);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #2
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #3
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(3);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #3
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #4
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component$1");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(4);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #4
	    // finishing method org.jboss.seam.Component$BijectedMethod#Component$BijectedMethod(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation, org.jboss.seam.Component$1)

	// finishing type org.jboss.seam.Component$BijectedMethod
	// #########################################################

	    newType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$BijectedProperty");
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"java.lang","Object");
	    extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);
	    
	    // starting interface Component$BijectedAttribute 
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam","Component$BijectedAttribute");
	    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
	    // ending interface Component$BijectedAttribute 

	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedProperty#getter
		field = newType.addNode(JavaDataField.class,"getter"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component$BijectedMethod");
	    helper.insertFieldData(field, fieldType, 2, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedProperty#getter
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedProperty#setter
		field = newType.addNode(JavaDataField.class,"setter"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component$BijectedMethod");
	    helper.insertFieldData(field, fieldType, 2, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedProperty#setter
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$BijectedProperty#this$0
		field = newType.addNode(JavaDataField.class,"this$0"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
	    helper.insertFieldData(field, fieldType, 4112, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$BijectedProperty#this$0


	    // starting method org.jboss.seam.Component$BijectedProperty#Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.annotation.Annotation)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.annotation.Annotation)");
	    method.setSimpleName("Component$BijectedProperty");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #2
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Method");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(2);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #2
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #3
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Method");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(3);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #3
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #4
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(4);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #4
	    // finishing method org.jboss.seam.Component$BijectedProperty#Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.reflect.Method, java.lang.annotation.Annotation)

	    // starting method org.jboss.seam.Component$BijectedProperty#Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodConstructor.class,"Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)");
	    method.setSimpleName("Component$BijectedProperty");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam","Component");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #2
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Method");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(2);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #2
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #3
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(3);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #3
	    // finishing method org.jboss.seam.Component$BijectedProperty#Component$BijectedProperty(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Method, java.lang.annotation.Annotation)

	    // starting method org.jboss.seam.Component$BijectedProperty#get(java.lang.Object)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"get(java.lang.Object)");
	    method.setSimpleName("get");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
	    // finishing method org.jboss.seam.Component$BijectedProperty#get(java.lang.Object)

	    // starting method org.jboss.seam.Component$BijectedProperty#getAnnotation()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getAnnotation()");
	    method.setSimpleName("getAnnotation");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation","Annotation");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedProperty#getAnnotation()

	    // starting method org.jboss.seam.Component$BijectedProperty#getName()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getName()");
	    method.setSimpleName("getName");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedProperty#getName()

	    // starting method org.jboss.seam.Component$BijectedProperty#getType()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getType()");
	    method.setSimpleName("getType");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Class");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$BijectedProperty#getType()

	    // starting method org.jboss.seam.Component$BijectedProperty#set(java.lang.Object, java.lang.Object)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"set(java.lang.Object, java.lang.Object)");
	    method.setSimpleName("set");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
	    // finishing method org.jboss.seam.Component$BijectedProperty#set(java.lang.Object, java.lang.Object)

	// finishing type org.jboss.seam.Component$BijectedProperty
	// #########################################################

	    newType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$ConstantInitialValue");
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"java.lang","Object");
	    extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);
	    
	    // starting interface Component$InitialValue 
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam","Component$InitialValue");
	    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
	    // ending interface Component$InitialValue 

	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$ConstantInitialValue#value
		field = newType.addNode(JavaDataField.class,"value"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
	    helper.insertFieldData(field, fieldType, 2, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$ConstantInitialValue#value


	    // starting method org.jboss.seam.Component$ConstantInitialValue#Component$ConstantInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodConstructor.class,"Component$ConstantInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
	    method.setSimpleName("Component$ConstantInitialValue");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam.util","Conversions$PropertyValue");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Class");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #2
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Type");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(2);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #2
	    // finishing method org.jboss.seam.Component$ConstantInitialValue#Component$ConstantInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

	    // starting method org.jboss.seam.Component$ConstantInitialValue#getValue(java.lang.Class)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getValue(java.lang.Class)");
	    method.setSimpleName("getValue");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Class");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
	    // finishing method org.jboss.seam.Component$ConstantInitialValue#getValue(java.lang.Class)

	    // starting method org.jboss.seam.Component$ConstantInitialValue#toString()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"toString()");
	    method.setSimpleName("toString");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$ConstantInitialValue#toString()

	// finishing type org.jboss.seam.Component$ConstantInitialValue
	// #########################################################

	    newType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam", "Component$ELInitialValue");
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"java.lang","Object");
	    extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);
	    
	    // starting interface Component$InitialValue 
	    newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class,"org.jboss.seam","Component$InitialValue");
	    implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
	    // ending interface Component$InitialValue 

	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$ELInitialValue#expression
		field = newType.addNode(JavaDataField.class,"expression"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    helper.insertFieldData(field, fieldType, 2, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$ELInitialValue#expression
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$ELInitialValue#converter
		field = newType.addNode(JavaDataField.class,"converter"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam.util","Conversions$Converter");
	    helper.insertFieldData(field, fieldType, 2, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$ELInitialValue#converter
	    isArray = false;
	    arrayDimensions = 0;
	    // starting field org.jboss.seam.Component$ELInitialValue#parameterType
		field = newType.addNode(JavaDataField.class,"parameterType"); 
		fieldType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Type");
	    helper.insertFieldData(field, fieldType, 2, isArray, arrayDimensions);
	    // finishing field org.jboss.seam.Component$ELInitialValue#parameterType


	    // starting method org.jboss.seam.Component$ELInitialValue#Component$ELInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodConstructor.class,"Component$ELInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)");
	    method.setSimpleName("Component$ELInitialValue");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class,"", "void");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam.util","Conversions$PropertyValue");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #1
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Class");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(1);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #1
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #2
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect","Type");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(2);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #2
	    // finishing method org.jboss.seam.Component$ELInitialValue#Component$ELInitialValue(org.jboss.seam.util.Conversions$PropertyValue, java.lang.Class, java.lang.reflect.Type)

	    // starting method org.jboss.seam.Component$ELInitialValue#getValue(java.lang.Class)
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"getValue(java.lang.Class)");
	    method.setSimpleName("getValue");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Object");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
		    isArray = false;
		    arrayDimensions = 0;
	        // starting parameter #0
		    methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","Class");
		    methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
	        methodParametersType.setOrder(0);
	        methodParametersType.setArray(isArray);
	        methodParametersType.setArrayDimension(arrayDimensions);
	    
	        // finishing parameter #0
	    // finishing method org.jboss.seam.Component$ELInitialValue#getValue(java.lang.Class)

	    // starting method org.jboss.seam.Component$ELInitialValue#createValueExpression()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"createValueExpression()");
	    method.setSimpleName("createValueExpression");
	    helper.setMethodData(method,2);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam.core","Expressions$ValueExpression");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$ELInitialValue#createValueExpression()

	    // starting method org.jboss.seam.Component$ELInitialValue#createMethodExpression()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"createMethodExpression()");
	    method.setSimpleName("createMethodExpression");
	    helper.setMethodData(method,2);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam.core","Expressions$MethodExpression");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$ELInitialValue#createMethodExpression()

	    // starting method org.jboss.seam.Component$ELInitialValue#toString()
	    isArray = false;
	    arrayDimensions = 0;
	    method = newType.addNode(JavaMethodMethod.class,"toString()");
	    method.setSimpleName("toString");
	    helper.setMethodData(method,1);
	    typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);
	    
	    // starting method return 
	    methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang","String");
	    methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
	    methodReturnsType.setArray(isArray);
	    methodReturnsType.setArrayDimension(arrayDimensions);
	        
	    // finishing method return 
	    
	    // finishing method org.jboss.seam.Component$ELInitialValue#toString()

	// finishing type org.jboss.seam.Component$ELInitialValue
	// #########################################################

	}
}
