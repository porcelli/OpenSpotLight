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
package org.openspotlight.bundle.dap.language.java;

import org.junit.Test;
import org.openspotlight.bundle.dap.language.java.metamodel.link.DataType;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodParameterDefinition;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodReturns;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodThrows;
import org.openspotlight.bundle.dap.language.java.metamodel.link.PackageType;
import org.openspotlight.bundle.dap.language.java.metamodel.link.TypeDeclares;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaDataField;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaDataParameter;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethodConstructor;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.bundle.dap.language.java.support.JavaGraphNodeSupport;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

public class ExampleGraphImport {

    @Test
    public void shouldImportSomeData() throws Exception {

        final SLGraphFactory factory = new SLGraphFactoryImpl();
        final SLGraph graph = factory.createTempGraph(true);
        final SLGraphSession session = graph.openSession();
        final SLNode currentContextRootNode = session.createContext("current-Ctx").getRootNode();
        final SLNode abstractContextRootNode = session.createContext(Constants.ABSTRACT_CONTEXT).getRootNode();

        final JavaGraphNodeSupport helper = new JavaGraphNodeSupport(session, currentContextRootNode, abstractContextRootNode);

        // global variables to be reused for each type
        JavaType newType;
        final JavaPackage newPackage;
        JavaType newSuperType;
        final JavaPackage newSuperPackage;
        JavaType fieldType;
        final JavaPackage fieldPackage;
        JavaDataField field;
        JavaMethod method;
        final JavaDataParameter parameter;
        final PackageType packageTypeLink;
        final PackageType fieldPackageTypeLink;
        final DataType fieldTypeLink;
        final boolean isPublic;
        final boolean isPrivate;
        final boolean isStatic;
        final boolean isFinal;
        final boolean isProtected;
        final boolean isFieldPublic;
        final boolean isFieldPrivate;
        final boolean isFieldStatic;
        final boolean isFieldFinal;
        final boolean isFieldProtected;
        final boolean isFieldTransient;
        final boolean isFieldVolatile;
        final boolean isMethodPublic;
        final boolean isMethodPrivate;
        final boolean isMethodStatic;
        final boolean isMethodFinal;
        final boolean isMethodProtected;
        final boolean isMethodSynchronized;
        final PackageType superPackageTypeLink;
        TypeDeclares typeDeclaresMethod;
        JavaType methodReturnTypeType;
        MethodReturns methodReturnsType;
        final JavaPackage methodParameterTypePackage;
        JavaType methodParameterTypeType;
        final PackageType methodParameterTypePackageTypeLink;
        MethodParameterDefinition methodParametersType;
        final JavaPackage newExceptionPackage;
        final JavaType newExceptionType;
        final PackageType exceptionPackageTypeLink;
        final MethodThrows methodThrowsType;
        final JavaPackage methodReturnTypePackage;
        final PackageType methodReturnTypePackageTypeLink;
        final String arraySquareBrackets;
        Extends extendsSuper;
        Implements implementsSuper;
        boolean isArray = false;
        int arrayDimensions = 0;

        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam", "Component$BijectedAttribute", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$BijectedField", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$BijectedMethod", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$BijectedProperty", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$ConstantInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$ELInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam", "Component$InitialValue", 1536);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$ListInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$MapInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$SetInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "ComponentType$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam", "ComponentType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "ConcurrentRequestTimeoutException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "CyclicDependencyException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Entity$NotEntityException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Entity", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam", "Instance", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "InstantiationException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Model", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Namespace", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "NoConversationException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "RequiredException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "ScopeType$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam", "ScopeType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Seam$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Seam", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "ApplicationException",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "AutoCreate", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Begin", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Conversational", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Create", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "DataBinderClass", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "DataSelectorClass", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Destroy", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "End", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Factory", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.annotations", "FlushModeType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Import", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "In", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Install", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "JndiName", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Logger", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Name", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Namespace", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Observer", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Out", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.annotations", "Outcome", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "PerNestedConversation",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "RaiseEvent", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "ReadOnly", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Role", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Roles", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Scope", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Startup", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Synchronized", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.annotations",
                                                 "TransactionPropagationType$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.annotations", "TransactionPropagationType",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Transactional", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Unwrap", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "Asynchronous",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "Duration", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "Expiration", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "FinalExpiration",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "IntervalCron",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "IntervalDuration",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "BeginTask", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "CreateProcess", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "EndTask", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "ResumeProcess", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "StartTask", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "Transition", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.datamodel", "DataModel",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.datamodel",
                                                 "DataModelSelection", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.datamodel",
                                                 "DataModelSelectionIndex", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.exception", "HttpError",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.exception", "Redirect",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.faces", "Converter", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.faces", "Validator", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "AroundInvoke",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept",
                                                 "BypassInterceptors", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "Interceptor",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.annotations.intercept", "InterceptorType",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "Interceptors",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "PostActivate",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "PrePassivate",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Admin", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Delete", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Insert", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security",
                                                 "PermissionCheck", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Read", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Restrict", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "RoleCheck",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "TokenUsername",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "TokenValue",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Update", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "RoleConditional", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "RoleGroups", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "RoleName", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserEnabled", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserFirstName", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserLastName", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserPassword", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserPrincipal", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserRoles", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "Identifier", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "Permission", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionAction", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionDiscriminator", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionRole", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionTarget", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionUser", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "Permissions", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.web", "Filter", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.web", "RequestParameter",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "AbstractDispatcher$DispatcherParameters", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AbstractDispatcher", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "Asynchronous$ContextualAsynchronousRequest", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "Asynchronous", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousEvent$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousEvent$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousEvent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousExceptionHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousInvocation$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousInvocation$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousInvocation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "CronSchedule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.async", "Dispatcher", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.async", "LocalTimerServiceDispatcher",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "QuartzDispatcher$QuartzJob", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "QuartzDispatcher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "QuartzTriggerHandle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "Schedule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "ThreadPoolDispatcher$RunnableAsynchronous", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "ThreadPoolDispatcher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "TimerSchedule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerHandleProxy$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerHandleProxy", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$3", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$4", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$5", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "TimerServiceDispatcher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "TransactionCompletionEvent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "TransactionSuccessEvent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.async", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Actor$1$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Actor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Actor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "BusinessProcess", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "BusinessProcessInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Jbpm$SeamSubProcessResolver", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Jbpm", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "JbpmELResolver", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "ManagedJbpmContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PageflowDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PageflowDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PageflowParser", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PooledTask", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PooledTaskInstanceList", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "ProcessInstance$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "ProcessInstance", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "ProcessInstanceFinder", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamExpressionEvaluator$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamExpressionEvaluator$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamExpressionEvaluator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor$3", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor$4", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm",
                                                 "SeamUserCodeInterceptor$ContextualCall", 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstance$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstance", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstanceList", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstanceListForType", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstancePriorityList", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Transition", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.bpm", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "AbstractJBossCacheProvider", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "CacheProvider", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "EhCacheProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "JbossCache2Provider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "JbossCacheProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "JbossPojoCacheProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.cache", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.captcha", "Captcha", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.captcha", "CaptchaImage", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.captcha", "CaptchaResponse", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.captcha", "CaptchaResponseValidator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.captcha", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "AbstractEntityBeanCollection",
                                                 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "ApplicationContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "BasicContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "BusinessProcessContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.contexts", "Context", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "Contexts", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EntityBean", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EntityBeanList", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EntityBeanMap", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EntityBeanSet", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EventContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "FacesLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "Lifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "PageContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "PassivatedEntity", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "RemotingLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "ServerConversationContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "ServletLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "SessionContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "TestLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.contexts", "Wrapper", 1536);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "AbstractMutable", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "BijectionInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Contexts", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Conversation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationEntries", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationEntry", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationIdGenerator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationList", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationPropagation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationStack", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationalInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "EventInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Events", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Expressions$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Expressions$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.core", "Expressions$MethodExpression",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.core", "Expressions$ValueExpression",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Expressions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init$FactoryExpression", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init$FactoryMethod", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init$ObserverMethod", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init$ObserverMethodExpression", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Interpolator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Locale", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "LockTimeoutException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Manager$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Manager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "MethodContextInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.core", "Mutable", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.core", "PropagationType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ResourceBundle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ResourceLoader", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "SeamResourceBundle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "SynchronizationInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Validators$Key", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Validators$ValidatingResolver", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Validators", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.core", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.databinding", "DataBinder", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.databinding", "DataModelBinder", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.databinding", "DataModelIndexSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.databinding", "DataModelSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.databinding", "DataSelector", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "AbstractClassDeploymentHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "AbstractDeploymentHandler",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "AbstractScanner$Handler", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "AbstractScanner", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "AnnotationDeploymentHandler$AnnotationDeploymentHandlerMetadata", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "AnnotationDeploymentHandler",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "ClassDeploymentHandler",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "ClassDeploymentMetadata",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "ClassDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "ComponentDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "ComponentDeploymentHandler",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "ComponentsXmlDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "ComponentsXmlDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "DeploymentHandler", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "DeploymentMetadata", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "DeploymentStrategy", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "DotComponentDotXmlDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "DotComponentDotXmlDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "DotPageDotXmlDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "DotPageDotXmlDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "FileDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "ForwardingAbstractScanner",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "ForwardingDeploymentStrategy", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "GroovyDeploymentHandler$GroovyDeploymentHandlerMetadata", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "GroovyDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "GroovyHotDeploymentStrategy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "HotDeploymentStrategy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "NamespaceDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "NamespaceDeploymentHandler",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "Scanner", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "SeamDeploymentProperties", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "StandardDeploymentStrategy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "TimestampCheckForwardingDeploymentStrategy$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "TimestampCheckForwardingDeploymentStrategy", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "TimestampScanner", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "URLScanner", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "WarRootDeploymentStrategy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentData$DocumentType", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentData", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentStore", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentStorePhaseListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentStoreServlet$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentStoreServlet", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.document", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "Decision", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "DroolsActionHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "DroolsAssignmentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "DroolsDecisionHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "DroolsHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "ManagedWorkingMemory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "RuleAgent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "RuleBase", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "SeamGlobalResolver", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.drools", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.ejb", "RemoveInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.ejb", "SeamInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "EL$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "EL$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "EL", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "OptionalParameterMethodExpression",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "SeamELResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "SeamExpressionFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "SeamFunctionMapper", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "AnnotationErrorHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "AnnotationRedirectHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "ConfigErrorHandler", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "ConfigRedirectHandler", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "DebugPageHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "ErrorHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.exception", "ExceptionHandler$LogLevel",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "ExceptionHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "Exceptions$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "Exceptions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "RedirectHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "DataModels", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesExpressions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesMessages$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesMessages", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesPage", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "HttpError", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "IsUserInRole", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Navigator", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Parameters", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Redirect", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "RedirectException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Renderer", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "ResourceLoader", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Selector", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Switcher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "UiComponent$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "UiComponent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "UserPrincipal", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Validation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.faces", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "BusinessProcessController", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "Controller", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "CurrentDate", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "CurrentDatetime", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "CurrentTime", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityController", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityHome", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityIdentifier", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityNotFoundException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityQuery", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "HibernateEntityController", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "HibernateEntityHome", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "HibernateEntityIdentifier", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "HibernateEntityQuery", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "Home", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "Identifier", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "MutableController", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "MutableEntityController", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "PersistenceController", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "Query", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.framework", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init",
                                                 "ComponentDescriptor$PrecedenceComparator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "ComponentDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "DependencyManager$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "DependencyManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "DeploymentDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "EjbDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "EjbEntityDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "FactoryDescriptor", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "Initialization$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "Initialization$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init",
                                                 "Initialization$EventListenerDescriptor", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "Initialization", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "NamespaceDescriptor", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "NamespacePackageResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "AbstractInterceptor", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "ClientSideInterceptor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "ClientSideInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "EE5SeamInvocationContext", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "EJBInvocationContext", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.intercept", "EventType", 16432);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "Interceptor", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.intercept", "InvocationContext", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "JavaBeanInterceptor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "JavaBeanInterceptor$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "JavaBeanInterceptor$3", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "JavaBeanInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.intercept", "OptimizedInterceptor",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.intercept", "Proxy", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "RootInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "RootInvocationContext", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "SeamInvocationContext$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "SeamInvocationContext", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "SessionBeanInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "Locale", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "LocaleConfig", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "LocaleSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "Messages$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "Messages", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.international", "StatusMessage$Severity",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "StatusMessage", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "StatusMessages$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "StatusMessages$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "StatusMessages", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "TimeZone", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "TimeZoneSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.international", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "ManagedQueueSender", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "ManagedTopicPublisher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "QueueConnection", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "QueueSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "TopicConnection", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "TopicSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.jms", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jmx", "JBossClusterMonitor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "ArrayDataModel", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "DelegatingFacesContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "ListDataModel", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "MapDataModel$1$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "MapDataModel$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "MapDataModel", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamApplication$ConverterLocator",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamApplication", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamApplicationFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamNavigationHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamPhaseListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamStateManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamViewHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SetDataModel$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SetDataModel", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "UnifiedELMethodBinding", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "UnifiedELValueBinding", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.log", "JDKProvider", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.log", "Log", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.log", "Log4JProvider", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.log", "LogImpl", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.log", "LogProvider", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.log", "Logging", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mail", "MailSession$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mail", "MailSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mail", "Meldware", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mail", "MeldwareUser", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.mail", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractDBUnitSeamTest$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock",
                                                 "AbstractDBUnitSeamTest$DataSetOperation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.mock", "AbstractDBUnitSeamTest$Database",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractDBUnitSeamTest", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$ComponentTest",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$FacesRequest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$NonFacesRequest",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$Request$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$Request$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$Request", 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "DBUnitSeamTest", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "EmbeddedBootstrap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockApplication$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockApplication", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockApplicationFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext$3", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock",
                                                 "MockExternalContext$AttributeMap$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext$AttributeMap",
                                                 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockFacesContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockFacesContextFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockFilterConfig", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockHttpServletRequest$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockHttpServletRequest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockHttpServletResponse", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockHttpSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockLifecycleFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockLoginModule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockNavigationHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockRenderKit", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockResponseStateManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockResponseWriter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockSecureEntity", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockServletContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockStateManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockTransport", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockViewHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "SeamTest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Action", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "ConversationControl", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.navigation", "ConversationIdParameter",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Header", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Input", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation",
                                                 "NaturalConversationIdParameter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Navigation", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "NavigationHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Output", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Page", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Pages$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Pages", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Param", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "ProcessControl", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Put", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "RedirectNavigationHandler",
                                                 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "RenderNavigationHandler", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Rule", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "SafeActions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation",
                                                 "SyntheticConversationIdParameter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "TaskControl", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.navigation", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.pageflow", "Page", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.pageflow", "Pageflow", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.pageflow", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "AbstractPersistenceProvider", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "EntityManagerFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "EntityManagerProxy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "EntityManagerProxyInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "Filter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "FullTextEntityManagerProxy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "FullTextHibernateSessionProxy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "HibernatePersistenceProvider$NotHibernateException", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "HibernatePersistenceProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "HibernateSessionFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "HibernateSessionProxy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "HibernateSessionProxyInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedEntityInterceptor",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedEntityWrapper", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedHibernateSession$1",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedHibernateSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "ManagedPersistenceContext$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedPersistenceContext",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.persistence",
                                                 "PersistenceContextManager", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "PersistenceContexts", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "PersistenceProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "QueryParser", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.persistence", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "AuthorizationException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Configuration$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Configuration", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Credentials$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Credentials", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.security", "EntityAction", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "EntityPermissionChecker$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "EntityPermissionChecker", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "EntitySecurityListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "FacesSecurityEvents", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "HibernateSecurityInterceptor",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Identity", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "JpaTokenStore", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "NotLoggedInException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$BoolWrapper", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$DecodedToken", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.security", "RememberMe$Mode", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$TokenSelector", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$UsernameSelector",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Role", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RunAsOperation", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SecurityFunctions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SecurityInterceptor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security",
                                                 "SecurityInterceptor$Restriction", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SecurityInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SimpleGroup", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SimplePrincipal", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security", "TokenStore", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.digest", "DigestAuthenticator",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.digest", "DigestRequest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.digest", "DigestUtils", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.digest",
                                                 "DigestValidationException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.jaas", "SeamLoginModule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management",
                                                 "IdentityManagementException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager$1",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager$2",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager$3",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager$4",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.security.management",
                                                 "IdentityStore$Feature", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management",
                                                 "IdentityStore$FeatureSet", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.management", "IdentityStore",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "JpaIdentityStore",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "LdapIdentityStore",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management",
                                                 "NoSuchRoleException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management",
                                                 "NoSuchUserException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "PasswordHash", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management.action", "RoleAction",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management.action", "RoleSearch",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management.action", "UserAction",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management.action", "UserSearch",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.management", "package-info",
                                                 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.openid", "OpenId", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.openid", "OpenIdPhaseListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.openid", "OpenIdPrincipal", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "ClassIdentifierStrategy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "EntityIdentifierStrategy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "IdentifierPolicy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.permission",
                                                 "IdentifierStrategy", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.security.permission",
                                                 "JpaPermissionStore$Discrimination", 16432);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "JpaPermissionStore",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "Permission", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "PermissionCheck", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "PermissionManager",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "PermissionMapper",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "PermissionMetadata$ActionSet", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "PermissionMetadata",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.permission",
                                                 "PermissionResolver", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.permission",
                                                 "PermissionStore", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "PersistentPermissionResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "ResolverChain", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "RoleCheck", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "RuleBasedPermissionResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission.action",
                                                 "PermissionSearch", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.permission", "package-info",
                                                 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ContextualHttpServletRequest",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ResourceServlet", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamCharacterEncodingFilter",
                                                 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamExceptionFilter", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamFilter$FilterChainImpl", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamRedirectFilter", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamResourceServlet", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamServletFilter", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ServletApplicationMap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ServletRequestMap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ServletRequestSessionMap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ServletSessionMap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text", "SeamTextLexer", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text", "SeamTextParser$DefaultSanitizer",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text",
                                                 "SeamTextParser$HtmlRecognitionException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text", "SeamTextParser$Macro", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.text", "SeamTextParser$Sanitizer", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text", "SeamTextParser", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.text", "SeamTextParserTokenTypes", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.theme", "Theme$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.theme", "Theme", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.theme", "ThemeSelector$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.theme", "ThemeSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.theme", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "AbstractUserTransaction",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "CMTTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "EjbSynchronizations", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "EjbTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "EntityTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "HibernateTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.transaction",
                                                 "LocalEjbSynchronizations", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "NoTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "RollbackInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "SeSynchronizations", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "SynchronizationRegistry", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.transaction", "Synchronizations", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "Transaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "TransactionInterceptor$1",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction",
                                                 "TransactionInterceptor$TransactionMetadata", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "TransactionInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "UTTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.transaction", "UserTransaction", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.transaction", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "AnnotatedBeanProperty", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Base64$InputStream", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Base64$OutputStream", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Base64", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$ArrayConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util",
                                                 "Conversions$AssociativePropertyValue", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$BigDecimalConverter",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$BigIntegerConverter",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$BooleanConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$CharacterConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$ClassConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.util", "Conversions$Converter", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$DoubleConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$EnumConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$FlatPropertyValue", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$FloatConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$IntegerConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$ListConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$LongConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$MapConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$MultiPropertyValue", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$PropertiesConverter",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.util", "Conversions$PropertyValue",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$SetConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$StringArrayConverter",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$StringConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "DTDEntityResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util",
                                                 "DelegatingInvocationHandler$MethodTarget", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "DelegatingInvocationHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.util", "EJB$Dummy", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "EJB", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "EnumerationEnumeration", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "EnumerationIterator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Exceptions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Faces", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "FacesResources", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Hex", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Id", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "IteratorEnumeration", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "JSF$Dummy", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "JSF", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Naming", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "ProxyFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "RandomStringUtils", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Reflections", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Resources", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "SortItem", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Sorter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Strings", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "TypedBeanProperty", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Work", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "XML$NullEntityResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "XML", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "AbstractFilter", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "AbstractResource", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Ajax4jsfFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "AuthenticationFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.web", "AuthenticationFilter$AuthType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "AuthenticationFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "CharacterEncodingFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ContextFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ContextFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ExceptionFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "FileUploadException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "FilterConfigWrapper", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "HotDeployFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IdentityFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IdentityRequestWrapper", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IncomingPattern$IncomingRewrite", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IncomingPattern", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IsUserInRole$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IsUserInRole", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Locale", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "LoggingFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.web", "MultipartRequest", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$FileParam$1",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$FileParam", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$Param", 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.web", "MultipartRequestImpl$ReadState",
                                                 16432);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$ValueParam", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "OutgoingPattern$OutgoingRewrite", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "OutgoingPattern", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Parameters", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Pattern", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "RedirectFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "RedirectFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.web", "Rewrite", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "RewriteFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "RewritingResponse", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "SeamFilter", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ServletContexts", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ServletMapping", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Session", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "UserPrincipal", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "WicketFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "WicketFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.web", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.webservice", "SOAPRequestHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.webservice", "WSSecurityInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam", "Component$BijectedAttribute", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$BijectedField", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$BijectedMethod", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$BijectedProperty", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$ConstantInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$ELInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam", "Component$InitialValue", 1536);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$ListInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$MapInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$SetInitialValue", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "ComponentType$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam", "ComponentType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "ConcurrentRequestTimeoutException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "CyclicDependencyException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Entity$NotEntityException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Entity", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam", "Instance", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "InstantiationException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Model", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Namespace", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "NoConversationException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "RequiredException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "ScopeType$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam", "ScopeType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Seam$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Seam", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "ApplicationException",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "AutoCreate", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Begin", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Conversational", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Create", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "DataBinderClass", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "DataSelectorClass", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Destroy", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "End", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Factory", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.annotations", "FlushModeType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Import", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "In", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Install", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "JndiName", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Logger", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Name", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Namespace", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Observer", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Out", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.annotations", "Outcome", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "PerNestedConversation",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "RaiseEvent", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "ReadOnly", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Role", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Roles", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Scope", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Startup", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Synchronized", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.annotations",
                                                 "TransactionPropagationType$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.annotations", "TransactionPropagationType",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Transactional", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations", "Unwrap", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "Asynchronous",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "Duration", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "Expiration", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "FinalExpiration",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "IntervalCron",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.async", "IntervalDuration",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "BeginTask", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "CreateProcess", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "EndTask", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "ResumeProcess", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "StartTask", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.bpm", "Transition", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.datamodel", "DataModel",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.datamodel",
                                                 "DataModelSelection", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.datamodel",
                                                 "DataModelSelectionIndex", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.exception", "HttpError",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.exception", "Redirect",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.faces", "Converter", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.faces", "Validator", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "AroundInvoke",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept",
                                                 "BypassInterceptors", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "Interceptor",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.annotations.intercept", "InterceptorType",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "Interceptors",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "PostActivate",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.intercept", "PrePassivate",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Admin", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Delete", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Insert", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security",
                                                 "PermissionCheck", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Read", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Restrict", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "RoleCheck",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "TokenUsername",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "TokenValue",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security", "Update", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "RoleConditional", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "RoleGroups", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "RoleName", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserEnabled", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserFirstName", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserLastName", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserPassword", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserPrincipal", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.management",
                                                 "UserRoles", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "Identifier", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "Permission", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionAction", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionDiscriminator", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionRole", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionTarget", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "PermissionUser", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.security.permission",
                                                 "Permissions", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.web", "Filter", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.annotations.web", "RequestParameter",
                                                 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "AbstractDispatcher$DispatcherParameters", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AbstractDispatcher", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "Asynchronous$ContextualAsynchronousRequest", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "Asynchronous", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousEvent$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousEvent$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousEvent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousExceptionHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousInvocation$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousInvocation$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "AsynchronousInvocation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "CronSchedule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.async", "Dispatcher", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.async", "LocalTimerServiceDispatcher",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "QuartzDispatcher$QuartzJob", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "QuartzDispatcher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "QuartzTriggerHandle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "Schedule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "ThreadPoolDispatcher$RunnableAsynchronous", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "ThreadPoolDispatcher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "TimerSchedule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerHandleProxy$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerHandleProxy", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$3", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$4", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy$5", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async",
                                                 "TimerServiceDispatcher$TimerProxy", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "TimerServiceDispatcher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "TransactionCompletionEvent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.async", "TransactionSuccessEvent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.async", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Actor$1$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Actor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Actor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "BusinessProcess", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "BusinessProcessInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Jbpm$SeamSubProcessResolver", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Jbpm", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "JbpmELResolver", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "ManagedJbpmContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PageflowDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PageflowDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PageflowParser", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PooledTask", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "PooledTaskInstanceList", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "ProcessInstance$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "ProcessInstance", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "ProcessInstanceFinder", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamExpressionEvaluator$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamExpressionEvaluator$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamExpressionEvaluator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor$3", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor$4", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm",
                                                 "SeamUserCodeInterceptor$ContextualCall", 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "SeamUserCodeInterceptor", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstance$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstance", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstanceList", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstanceListForType", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "TaskInstancePriorityList", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.bpm", "Transition", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.bpm", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "AbstractJBossCacheProvider", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "CacheProvider", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "EhCacheProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "JbossCache2Provider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "JbossCacheProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.cache", "JbossPojoCacheProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.cache", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.captcha", "Captcha", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.captcha", "CaptchaImage", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.captcha", "CaptchaResponse", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.captcha", "CaptchaResponseValidator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.captcha", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "AbstractEntityBeanCollection",
                                                 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "ApplicationContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "BasicContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "BusinessProcessContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.contexts", "Context", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "Contexts", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EntityBean", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EntityBeanList", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EntityBeanMap", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EntityBeanSet", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "EventContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "FacesLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "Lifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "PageContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "PassivatedEntity", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "RemotingLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "ServerConversationContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "ServletLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "SessionContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.contexts", "TestLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.contexts", "Wrapper", 1536);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "AbstractMutable", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "BijectionInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Contexts", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Conversation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationEntries", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationEntry", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationIdGenerator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationList", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationPropagation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationStack", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ConversationalInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "EventInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Events", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Expressions$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Expressions$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.core", "Expressions$MethodExpression",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.core", "Expressions$ValueExpression",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Expressions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init$FactoryExpression", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init$FactoryMethod", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init$ObserverMethod", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init$ObserverMethodExpression", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Init", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Interpolator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Locale", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "LockTimeoutException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Manager$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Manager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "MethodContextInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.core", "Mutable", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.core", "PropagationType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ResourceBundle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "ResourceLoader", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "SeamResourceBundle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "SynchronizationInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Validators$Key", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Validators$ValidatingResolver", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.core", "Validators", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.core", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.databinding", "DataBinder", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.databinding", "DataModelBinder", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.databinding", "DataModelIndexSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.databinding", "DataModelSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.databinding", "DataSelector", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "AbstractClassDeploymentHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "AbstractDeploymentHandler",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "AbstractScanner$Handler", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "AbstractScanner", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "AnnotationDeploymentHandler$AnnotationDeploymentHandlerMetadata", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "AnnotationDeploymentHandler",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "ClassDeploymentHandler",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "ClassDeploymentMetadata",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "ClassDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "ComponentDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "ComponentDeploymentHandler",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "ComponentsXmlDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "ComponentsXmlDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "DeploymentHandler", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "DeploymentMetadata", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "DeploymentStrategy", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "DotComponentDotXmlDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "DotComponentDotXmlDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "DotPageDotXmlDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "DotPageDotXmlDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "FileDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "ForwardingAbstractScanner",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "ForwardingDeploymentStrategy", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "GroovyDeploymentHandler$GroovyDeploymentHandlerMetadata", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "GroovyDeploymentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "GroovyHotDeploymentStrategy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "HotDeploymentStrategy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "NamespaceDeploymentHandler$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "NamespaceDeploymentHandler",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.deployment", "Scanner", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "SeamDeploymentProperties", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "StandardDeploymentStrategy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "TimestampCheckForwardingDeploymentStrategy$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment",
                                                 "TimestampCheckForwardingDeploymentStrategy", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "TimestampScanner", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "URLScanner", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.deployment", "WarRootDeploymentStrategy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentData$DocumentType", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentData", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentStore", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentStorePhaseListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentStoreServlet$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.document", "DocumentStoreServlet", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.document", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "Decision", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "DroolsActionHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "DroolsAssignmentHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "DroolsDecisionHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "DroolsHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "ManagedWorkingMemory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "RuleAgent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "RuleBase", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.drools", "SeamGlobalResolver", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.drools", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.ejb", "RemoveInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.ejb", "SeamInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "EL$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "EL$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "EL", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "OptionalParameterMethodExpression",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "SeamELResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "SeamExpressionFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.el", "SeamFunctionMapper", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "AnnotationErrorHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "AnnotationRedirectHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "ConfigErrorHandler", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "ConfigRedirectHandler", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "DebugPageHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "ErrorHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.exception", "ExceptionHandler$LogLevel",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "ExceptionHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "Exceptions$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "Exceptions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.exception", "RedirectHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "DataModels", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesExpressions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesMessages$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesMessages", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "FacesPage", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "HttpError", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "IsUserInRole", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Navigator", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Parameters", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Redirect", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "RedirectException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Renderer", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "ResourceLoader", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Selector", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Switcher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "UiComponent$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "UiComponent", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "UserPrincipal", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.faces", "Validation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.faces", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "BusinessProcessController", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "Controller", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "CurrentDate", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "CurrentDatetime", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "CurrentTime", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityController", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityHome", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityIdentifier", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityNotFoundException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "EntityQuery", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "HibernateEntityController", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "HibernateEntityHome", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "HibernateEntityIdentifier", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "HibernateEntityQuery", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "Home", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "Identifier", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "MutableController", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "MutableEntityController", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "PersistenceController", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.framework", "Query", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.framework", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init",
                                                 "ComponentDescriptor$PrecedenceComparator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "ComponentDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "DependencyManager$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "DependencyManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "DeploymentDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "EjbDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "EjbEntityDescriptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "FactoryDescriptor", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "Initialization$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "Initialization$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init",
                                                 "Initialization$EventListenerDescriptor", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "Initialization", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "NamespaceDescriptor", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.init", "NamespacePackageResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "AbstractInterceptor", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "ClientSideInterceptor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "ClientSideInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "EE5SeamInvocationContext", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "EJBInvocationContext", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.intercept", "EventType", 16432);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "Interceptor", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.intercept", "InvocationContext", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "JavaBeanInterceptor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "JavaBeanInterceptor$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "JavaBeanInterceptor$3", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "JavaBeanInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.intercept", "OptimizedInterceptor",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.intercept", "Proxy", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "RootInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "RootInvocationContext", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "SeamInvocationContext$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "SeamInvocationContext", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.intercept", "SessionBeanInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "Locale", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "LocaleConfig", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "LocaleSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "Messages$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "Messages", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.international", "StatusMessage$Severity",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "StatusMessage", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "StatusMessages$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "StatusMessages$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "StatusMessages", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "TimeZone", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.international", "TimeZoneSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.international", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "ManagedQueueSender", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "ManagedTopicPublisher", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "QueueConnection", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "QueueSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "TopicConnection", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jms", "TopicSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.jms", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jmx", "JBossClusterMonitor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "ArrayDataModel", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "DelegatingFacesContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "ListDataModel", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "MapDataModel$1$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "MapDataModel$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "MapDataModel", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamApplication$ConverterLocator",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamApplication", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamApplicationFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamNavigationHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamPhaseListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamStateManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SeamViewHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SetDataModel$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "SetDataModel", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "UnifiedELMethodBinding", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.jsf", "UnifiedELValueBinding", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.log", "JDKProvider", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.log", "Log", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.log", "Log4JProvider", 48);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.log", "LogImpl", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.log", "LogProvider", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.log", "Logging", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mail", "MailSession$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mail", "MailSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mail", "Meldware", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mail", "MeldwareUser", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.mail", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractDBUnitSeamTest$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock",
                                                 "AbstractDBUnitSeamTest$DataSetOperation", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.mock", "AbstractDBUnitSeamTest$Database",
                                                 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractDBUnitSeamTest", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$ComponentTest",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$FacesRequest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$NonFacesRequest",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$Request$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$Request$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest$Request", 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "AbstractSeamTest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "DBUnitSeamTest", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "EmbeddedBootstrap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockApplication$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockApplication", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockApplicationFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext$2", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext$3", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock",
                                                 "MockExternalContext$AttributeMap$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext$AttributeMap",
                                                 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockExternalContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockFacesContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockFacesContextFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockFilterConfig", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockHttpServletRequest$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockHttpServletRequest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockHttpServletResponse", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockHttpSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockLifecycle", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockLifecycleFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockLoginModule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockNavigationHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockRenderKit", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockResponseStateManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockResponseWriter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockSecureEntity", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockServletContext", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockStateManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockTransport", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "MockViewHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.mock", "SeamTest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Action", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "ConversationControl", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.navigation", "ConversationIdParameter",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Header", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Input", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation",
                                                 "NaturalConversationIdParameter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Navigation", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "NavigationHandler", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Output", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Page", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Pages$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Pages", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Param", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "ProcessControl", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Put", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "RedirectNavigationHandler",
                                                 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "RenderNavigationHandler", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "Rule", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "SafeActions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation",
                                                 "SyntheticConversationIdParameter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.navigation", "TaskControl", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.navigation", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.pageflow", "Page", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.pageflow", "Pageflow", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.pageflow", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "AbstractPersistenceProvider", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "EntityManagerFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "EntityManagerProxy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "EntityManagerProxyInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "Filter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "FullTextEntityManagerProxy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "FullTextHibernateSessionProxy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "HibernatePersistenceProvider$NotHibernateException", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "HibernatePersistenceProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "HibernateSessionFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "HibernateSessionProxy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "HibernateSessionProxyInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedEntityInterceptor",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedEntityWrapper", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedHibernateSession$1",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedHibernateSession", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence",
                                                 "ManagedPersistenceContext$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "ManagedPersistenceContext",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.persistence",
                                                 "PersistenceContextManager", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "PersistenceContexts", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "PersistenceProvider", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.persistence", "QueryParser", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.persistence", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "AuthorizationException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Configuration$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Configuration", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Credentials$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Credentials", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.security", "EntityAction", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "EntityPermissionChecker$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "EntityPermissionChecker", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "EntitySecurityListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "FacesSecurityEvents", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "HibernateSecurityInterceptor",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Identity", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "JpaTokenStore", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "NotLoggedInException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$BoolWrapper", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$DecodedToken", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.security", "RememberMe$Mode", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$TokenSelector", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe$UsernameSelector",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RememberMe", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "Role", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "RunAsOperation", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SecurityFunctions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SecurityInterceptor$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security",
                                                 "SecurityInterceptor$Restriction", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SecurityInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SimpleGroup", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security", "SimplePrincipal", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security", "TokenStore", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.digest", "DigestAuthenticator",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.digest", "DigestRequest", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.digest", "DigestUtils", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.digest",
                                                 "DigestValidationException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.jaas", "SeamLoginModule", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management",
                                                 "IdentityManagementException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager$1",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager$2",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager$3",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager$4",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "IdentityManager", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.security.management",
                                                 "IdentityStore$Feature", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management",
                                                 "IdentityStore$FeatureSet", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.management", "IdentityStore",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "JpaIdentityStore",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "LdapIdentityStore",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management",
                                                 "NoSuchRoleException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management",
                                                 "NoSuchUserException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management", "PasswordHash", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management.action", "RoleAction",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management.action", "RoleSearch",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management.action", "UserAction",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.management.action", "UserSearch",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.management", "package-info",
                                                 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.openid", "OpenId", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.openid", "OpenIdPhaseListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.openid", "OpenIdPrincipal", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "ClassIdentifierStrategy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "EntityIdentifierStrategy", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "IdentifierPolicy",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.permission",
                                                 "IdentifierStrategy", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.security.permission",
                                                 "JpaPermissionStore$Discrimination", 16432);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "JpaPermissionStore",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "Permission", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "PermissionCheck", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "PermissionManager",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "PermissionMapper",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "PermissionMetadata$ActionSet", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "PermissionMetadata",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.permission",
                                                 "PermissionResolver", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.permission",
                                                 "PermissionStore", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "PersistentPermissionResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "ResolverChain", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission", "RoleCheck", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission",
                                                 "RuleBasedPermissionResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.security.permission.action",
                                                 "PermissionSearch", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.security.permission", "package-info",
                                                 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ContextualHttpServletRequest",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ResourceServlet", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamCharacterEncodingFilter",
                                                 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamExceptionFilter", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamFilter$FilterChainImpl", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamListener", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamRedirectFilter", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamResourceServlet", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "SeamServletFilter", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ServletApplicationMap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ServletRequestMap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ServletRequestSessionMap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.servlet", "ServletSessionMap", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text", "SeamTextLexer", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text", "SeamTextParser$DefaultSanitizer",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text",
                                                 "SeamTextParser$HtmlRecognitionException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text", "SeamTextParser$Macro", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.text", "SeamTextParser$Sanitizer", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.text", "SeamTextParser", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.text", "SeamTextParserTokenTypes", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.theme", "Theme$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.theme", "Theme", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.theme", "ThemeSelector$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.theme", "ThemeSelector", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.theme", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "AbstractUserTransaction",
                                                 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "CMTTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "EjbSynchronizations", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "EjbTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "EntityTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "HibernateTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.transaction",
                                                 "LocalEjbSynchronizations", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "NoTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "RollbackInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "SeSynchronizations", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "SynchronizationRegistry", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.transaction", "Synchronizations", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "Transaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "TransactionInterceptor$1",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction",
                                                 "TransactionInterceptor$TransactionMetadata", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "TransactionInterceptor", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.transaction", "UTTransaction", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.transaction", "UserTransaction", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.transaction", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "AnnotatedBeanProperty", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Base64$InputStream", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Base64$OutputStream", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Base64", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$ArrayConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util",
                                                 "Conversions$AssociativePropertyValue", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$BigDecimalConverter",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$BigIntegerConverter",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$BooleanConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$CharacterConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$ClassConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.util", "Conversions$Converter", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$DoubleConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$EnumConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$FlatPropertyValue", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$FloatConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$IntegerConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$ListConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$LongConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$MapConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$MultiPropertyValue", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$PropertiesConverter",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.util", "Conversions$PropertyValue",
                                                 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$SetConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$StringArrayConverter",
                                                 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions$StringConverter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Conversions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "DTDEntityResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util",
                                                 "DelegatingInvocationHandler$MethodTarget", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "DelegatingInvocationHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.util", "EJB$Dummy", 9729);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "EJB", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "EnumerationEnumeration", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "EnumerationIterator", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Exceptions", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Faces", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "FacesResources", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Hex", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Id", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "IteratorEnumeration", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "JSF$Dummy", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "JSF", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Naming", 49);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "ProxyFactory", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "RandomStringUtils", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Reflections", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Resources", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "SortItem", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Sorter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Strings", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "TypedBeanProperty", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "Work", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "XML$NullEntityResolver", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.util", "XML", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "AbstractFilter", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "AbstractResource", 1057);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Ajax4jsfFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "AuthenticationFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.web", "AuthenticationFilter$AuthType", 16433);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "AuthenticationFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "CharacterEncodingFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ContextFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ContextFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ExceptionFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "FileUploadException", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "FilterConfigWrapper", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "HotDeployFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IdentityFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IdentityRequestWrapper", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IncomingPattern$IncomingRewrite", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IncomingPattern", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IsUserInRole$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "IsUserInRole", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Locale", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "LoggingFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.web", "MultipartRequest", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$FileParam$1",
                                                 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$FileParam", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$Param", 1056);
        newType = helper.addBeforeTypeProcessing(JavaTypeEnum.class, "org.jboss.seam.web", "MultipartRequestImpl$ReadState",
                                                 16432);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl$ValueParam", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "MultipartRequestImpl", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "OutgoingPattern$OutgoingRewrite", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "OutgoingPattern", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Parameters", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Pattern", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "RedirectFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "RedirectFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.web", "Rewrite", 1537);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "RewriteFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "RewritingResponse", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "SeamFilter", 131105);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ServletContexts", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "ServletMapping", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "Session", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "UserPrincipal", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "WicketFilter$1", 32);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.web", "WicketFilter", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeInterface.class, "org.jboss.seam.web", "package-info", 512);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.webservice", "SOAPRequestHandler", 33);
        newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, "org.jboss.seam.webservice", "WSSecurityInterceptor", 33);

        newType = helper.addAfterTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$1");
        newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class, "java.lang", "Object");
        extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);

        // starting interface MethodFilter 
        newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class, "javassist.util.proxy", "MethodFilter");
        implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
        // ending interface MethodFilter 

        // starting method org.jboss.seam.Component$1#Component$1()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodConstructor.class, "Component$1()");
        method.setSimpleName("Component$1");
        helper.setMethodData(method, 0);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "", "void");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$1#Component$1()
        // starting method org.jboss.seam.Component$1#isHandled(java.lang.reflect.Method)
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "isHandled(java.lang.reflect.Method)");
        method.setSimpleName("isHandled");
        helper.setMethodData(method, 1);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "", "boolean");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        isArray = false;
        arrayDimensions = 0;
        // starting parameter #0
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect", "Method");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(0);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #0
        // finishing method org.jboss.seam.Component$1#isHandled(java.lang.reflect.Method)

        // finishing type org.jboss.seam.Component$1
        // #########################################################

        newType = helper.addAfterTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$2");
        newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class, "java.lang", "Object");
        extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);

        isArray = false;
        arrayDimensions = 0;
        // starting field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$ComponentType
        field = newType.addNode(JavaDataField.class, "$SwitchMap$org$jboss$seam$ComponentType");
        // starting array
        arrayDimensions = 1;
        fieldType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "", "int");
        //ending array
        helper.insertFieldData(field, fieldType, 4120, isArray, arrayDimensions);
        // finishing field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$ComponentType

        isArray = false;
        arrayDimensions = 0;
        // starting field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType
        field = newType.addNode(JavaDataField.class, "$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType");
        // starting array
        arrayDimensions = 1;
        fieldType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "", "int");
        //ending array
        helper.insertFieldData(field, fieldType, 4120, isArray, arrayDimensions);
        // finishing field org.jboss.seam.Component$2#$SwitchMap$org$jboss$seam$annotations$intercept$InterceptorType

        // finishing type org.jboss.seam.Component$2
        // #########################################################

        newType = helper.addAfterTypeProcessing(JavaTypeInterface.class, "org.jboss.seam", "Component$BijectedAttribute");
        newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class, "java.lang", "Object");
        extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);

        // starting method org.jboss.seam.Component$BijectedAttribute#getName()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "getName()");
        method.setSimpleName("getName");
        helper.setMethodData(method, 1025);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "String");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$BijectedAttribute#getName()
        // starting method org.jboss.seam.Component$BijectedAttribute#getAnnotation()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "getAnnotation()");
        method.setSimpleName("getAnnotation");
        helper.setMethodData(method, 1025);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation", "Annotation");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$BijectedAttribute#getAnnotation()
        // starting method org.jboss.seam.Component$BijectedAttribute#getType()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "getType()");
        method.setSimpleName("getType");
        helper.setMethodData(method, 1025);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Class");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$BijectedAttribute#getType()
        // starting method org.jboss.seam.Component$BijectedAttribute#set(java.lang.Object, java.lang.Object)
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "set(java.lang.Object, java.lang.Object)");
        method.setSimpleName("set");
        helper.setMethodData(method, 1025);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "", "void");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        isArray = false;
        arrayDimensions = 0;
        // starting parameter #0
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Object");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(0);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #0
        isArray = false;
        arrayDimensions = 0;
        // starting parameter #1
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Object");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(1);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #1
        // finishing method org.jboss.seam.Component$BijectedAttribute#set(java.lang.Object, java.lang.Object)
        // starting method org.jboss.seam.Component$BijectedAttribute#get(java.lang.Object)
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "get(java.lang.Object)");
        method.setSimpleName("get");
        helper.setMethodData(method, 1025);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Object");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        isArray = false;
        arrayDimensions = 0;
        // starting parameter #0
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Object");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(0);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #0
        // finishing method org.jboss.seam.Component$BijectedAttribute#get(java.lang.Object)

        // finishing type org.jboss.seam.Component$BijectedAttribute
        // #########################################################

        newType = helper.addAfterTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$BijectedField");
        newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class, "java.lang", "Object");
        extendsSuper = session.addLink(Extends.class, newType, newSuperType, false);

        // starting interface Component$BijectedAttribute 
        newSuperType = helper.addAfterTypeProcessing(JavaTypeClass.class, "org.jboss.seam", "Component$BijectedAttribute");
        implementsSuper = session.addLink(Implements.class, newType, newSuperType, false);
        // ending interface Component$BijectedAttribute 

        // ignoring field org.jboss.seam.Component$BijectedField#name 

        // ignoring field org.jboss.seam.Component$BijectedField#field 

        // ignoring field org.jboss.seam.Component$BijectedField#annotation 

        isArray = false;
        arrayDimensions = 0;
        // starting field org.jboss.seam.Component$BijectedField#this$0
        field = newType.addNode(JavaDataField.class, "this$0");
        fieldType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam", "Component");
        helper.insertFieldData(field, fieldType, 4112, isArray, arrayDimensions);
        // finishing field org.jboss.seam.Component$BijectedField#this$0

        // ignoring method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation)
        // starting method org.jboss.seam.Component$BijectedField#getName()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "getName()");
        method.setSimpleName("getName");
        helper.setMethodData(method, 1);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "String");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$BijectedField#getName()
        // starting method org.jboss.seam.Component$BijectedField#getField()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "getField()");
        method.setSimpleName("getField");
        helper.setMethodData(method, 1);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect", "Field");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$BijectedField#getField()
        // starting method org.jboss.seam.Component$BijectedField#getAnnotation()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "getAnnotation()");
        method.setSimpleName("getAnnotation");
        helper.setMethodData(method, 1);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation", "Annotation");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$BijectedField#getAnnotation()
        // starting method org.jboss.seam.Component$BijectedField#getType()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "getType()");
        method.setSimpleName("getType");
        helper.setMethodData(method, 1);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Class");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$BijectedField#getType()
        // starting method org.jboss.seam.Component$BijectedField#set(java.lang.Object, java.lang.Object)
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "set(java.lang.Object, java.lang.Object)");
        method.setSimpleName("set");
        helper.setMethodData(method, 1);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "", "void");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        isArray = false;
        arrayDimensions = 0;
        // starting parameter #0
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Object");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(0);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #0
        isArray = false;
        arrayDimensions = 0;
        // starting parameter #1
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Object");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(1);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #1
        // finishing method org.jboss.seam.Component$BijectedField#set(java.lang.Object, java.lang.Object)
        // starting method org.jboss.seam.Component$BijectedField#get(java.lang.Object)
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "get(java.lang.Object)");
        method.setSimpleName("get");
        helper.setMethodData(method, 1);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Object");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        isArray = false;
        arrayDimensions = 0;
        // starting parameter #0
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "Object");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(0);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #0
        // finishing method org.jboss.seam.Component$BijectedField#get(java.lang.Object)
        // starting method org.jboss.seam.Component$BijectedField#toString()
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(JavaMethodMethod.class, "toString()");
        method.setSimpleName("toString");
        helper.setMethodData(method, 1);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "String");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        // finishing method org.jboss.seam.Component$BijectedField#toString()
        // starting method org.jboss.seam.Component$BijectedField#Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation, org.jboss.seam.Component$1)
        isArray = false;
        arrayDimensions = 0;
        method = newType.addNode(
                                 JavaMethodConstructor.class,
                                 "Component$BijectedField(org.jboss.seam.Component, java.lang.String, java.lang.reflect.Field, java.lang.annotation.Annotation, org.jboss.seam.Component$1)");
        method.setSimpleName("Component$BijectedField");
        helper.setMethodData(method, 4096);
        typeDeclaresMethod = session.addLink(TypeDeclares.class, newType, method, false);

        // starting method return 
        methodReturnTypeType = helper.addAfterTypeProcessing(JavaTypePrimitive.class, "", "void");
        methodReturnsType = session.addLink(MethodReturns.class, method, methodReturnTypeType, false);
        methodReturnsType.setArray(isArray);
        methodReturnsType.setArrayDimension(arrayDimensions);

        // finishing method return 

        isArray = false;
        arrayDimensions = 0;
        // starting parameter #0
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam", "Component");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(0);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #0
        isArray = false;
        arrayDimensions = 0;
        // starting parameter #1
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang", "String");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(1);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #1
        isArray = false;
        arrayDimensions = 0;
        // starting parameter #2
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.reflect", "Field");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(2);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #2
        isArray = false;
        arrayDimensions = 0;
        // starting parameter #3
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "java.lang.annotation", "Annotation");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(3);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        // finishing parameter #3
        isArray = false;
        arrayDimensions = 0;
        // starting parameter #4
        methodParameterTypeType = helper.addAfterTypeProcessing(JavaType.class, "org.jboss.seam", "Component$1");
        methodParametersType = session.addLink(MethodParameterDefinition.class, method, methodParameterTypeType, false);
        methodParametersType.setOrder(4);
        methodParametersType.setArray(isArray);
        methodParametersType.setArrayDimension(arrayDimensions);

        session.close();
        graph.shutdown();

    }
}
