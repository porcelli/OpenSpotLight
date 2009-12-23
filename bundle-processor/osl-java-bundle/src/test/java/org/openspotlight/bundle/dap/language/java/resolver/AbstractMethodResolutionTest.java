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
package org.openspotlight.bundle.dap.language.java.resolver;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.Constants;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.ImplicitExtends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodParameterDefinition;
import org.openspotlight.bundle.dap.language.java.metamodel.link.TypeDeclares;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeParameterized;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.graph.SLInvalidNodeTypeException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.SecurityFactory;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityException;

public abstract class AbstractMethodResolutionTest {

	/**
	 * Finish.
	 */
	@AfterClass
	public static void finish() {
		graph.shutdown();
	}

	/**
	 * Inits the Graph.
	 * 
	 * @throws AbstractFactoryException
	 *             the abstract factory exception
	 */
	@BeforeClass
	public static void init() throws AbstractFactoryException,
			SLInvalidCredentialException, IdentityException {
		final SecurityFactory securityFactory = AbstractFactory
				.getDefaultInstance(SecurityFactory.class);
		final User simpleUser = securityFactory.createUser("testUser");
		user = securityFactory.createIdentityManager(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR).authenticate(simpleUser,
				"password");

		final SLGraphFactory factory = AbstractFactory
				.getDefaultInstance(SLGraphFactory.class);
		graph = factory.createGraph(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
	}

	protected MethodResolver<JavaType, JavaMethod> methodResolver = null;
	protected SLGraphSession graphSession = null;
	protected JavaGraphNodeSupport helper = null;

	protected SLContext abstractContex = null;

	protected static SLGraph graph = null;
	protected static AuthenticatedUser user = null;

	/**
	 * After test.
	 * 
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	@After
	public void afterTest() throws SLGraphSessionException {
		graphSession.clear();
	}

	protected Pair<JavaType, JavaMethod> createMethod(final JavaType type,
			final String simpleMethodName, final String fullMethodName,
			final boolean isConstructor, final SLNode... methodParameters)
			throws Exception {

		final JavaMethod method = helper.createMethod(type, fullMethodName,
				simpleMethodName, isConstructor, Opcodes.ACC_PUBLIC);

		if (methodParameters != null) {
			int position = -1;
			for (final SLNode activeParameterType : methodParameters) {
				position++;
				final SLLink link = graphSession.addLink(
						MethodParameterDefinition.class, method,
						activeParameterType, false);
				link.setProperty(Integer.class, "Order", position);
			}
		}

		return new Pair<JavaType, JavaMethod>(type, method);
	}

	protected Pair<JavaType, JavaMethod> createMethod(final JavaType type,
			final String simpleMethodName, final String fullMethodName,
			final SLNode... methodParameters) throws Exception {

		return this.createMethod(type, simpleMethodName, fullMethodName, false,
				methodParameters);
	}

	protected Pair<JavaType, JavaMethod> createMethod(final String packageName,
			final String typeName, final String simpleMethodName,
			final String fullMethodName) throws Exception {

		return this.createMethod(packageName, typeName, simpleMethodName,
				fullMethodName, false, (SLNode[]) null);
	}

	protected Pair<JavaType, JavaMethod> createMethod(final String packageName,
			final String typeName, final String simpleMethodName,
			final String fullMethodName, final boolean isConstructor)
			throws Exception {

		return this.createMethod(packageName, typeName, simpleMethodName,
				fullMethodName, isConstructor, (SLNode[]) null);
	}

	protected Pair<JavaType, JavaMethod> createMethod(final String packageName,
			final String typeName, final String simpleMethodName,
			final String fullMethodName, final boolean isConstructor,
			final SLNode... methodParameters) throws Exception {

		final JavaType type = helper.addTypeOnCurrentContext(
				JavaTypeClass.class, packageName, typeName, Opcodes.ACC_PUBLIC);

		return this.createMethod(type, simpleMethodName, fullMethodName,
				isConstructor, methodParameters);
	}

	protected Pair<JavaType, JavaMethod> createMethod(final String packageName,
			final String typeName, final String simpleMethodName,
			final String fullMethodName, final SLNode... methodParameters)
			throws Exception {

		return this.createMethod(packageName, typeName, simpleMethodName,
				fullMethodName, false, methodParameters);
	}

	protected JavaTypePrimitive createPrimitiveType(final String type)
			throws Exception {
		return helper.addTypeOnCurrentContext(JavaTypePrimitive.class, "",
				type, Opcodes.ACC_PUBLIC);
	}

	protected JavaType createType(final String packageName,
			final String className, final JavaType extendedType,
			final boolean isImplicit) throws Exception {

		final JavaType newType = helper
				.addTypeOnCurrentContext(JavaTypeClass.class, packageName,
						className, Opcodes.ACC_PUBLIC);

		if (extendedType != null) {
			if (isImplicit) {
				graphSession.addLink(ImplicitExtends.class, newType,
						extendedType, false);
			} else {
				graphSession.addLink(Extends.class, newType, extendedType,
						false);
			}
		}

		return newType;
	}

	protected JavaType createTypeParameterized(final String packageName,
			final String className, final SLNode parent,
			final JavaType extendedType, final boolean isImplicit)
			throws Exception {

		final JavaType newType = helper.addTypeOnCurrentContext(
				JavaTypeParameterized.class, packageName, className,
				Opcodes.ACC_PUBLIC, parent);

		if (extendedType != null) {
			if (isImplicit) {
				graphSession.addLink(ImplicitExtends.class, newType,
						extendedType, false);
			} else {
				graphSession.addLink(Extends.class, newType, extendedType,
						false);
			}
		}

		return newType;
	}

	protected JavaType getAbstractType(final String packageName,
			final String className) throws SLInvalidNodeTypeException,
			SLGraphSessionException {
		final JavaPackage abstractPackage = abstractContex.getRootNode()
				.getNode(JavaPackage.class, packageName);
		if (abstractPackage != null) {
			return abstractPackage.getNode(JavaType.class, className);
		}
		return null;
	}

	@Before
	public void setupGraphSession() throws Exception {

		JcrConnectionProvider.createFromData(
				DefaultJcrDescriptor.TEMP_DESCRIPTOR).closeRepositoryAndCleanResources();

		// FIXME this == null should be removed -> NOT I CAN'T OPEN ONE SESSION
		// PER METHOD EXECUTION!!
		if (graphSession == null) {
			graphSession = graph.openSession(user, SLConsts.DEFAULT_REPOSITORY_NAME);
		}

		abstractContex = graphSession.getContext(Constants.ABSTRACT_CONTEXT);
		if (abstractContex == null) {
			abstractContex = graphSession
					.createContext(Constants.ABSTRACT_CONTEXT);
		}

		if (graphSession.getContext("test") == null) {
			graphSession.createContext("test");
		}

		final SLContext testCtx = graphSession.getContext("test");
		final List<SLContext> contexts = new ArrayList<SLContext>();
		contexts.add(testCtx);
		final SLNode currentContextRootNode = graphSession.getContext("test")
				.getRootNode();
		final SLNode abstractContextRootNode = abstractContex.getRootNode();
		helper = new JavaGraphNodeSupport(graphSession, currentContextRootNode,
				abstractContextRootNode);
		helper.setupJavaTypesOnCurrentContext();
		final JavaTypeResolver typeResolver = new JavaTypeResolver(
				abstractContex, contexts, true, graphSession);

		methodResolver = new MethodResolver<JavaType, JavaMethod>(typeResolver,
				graphSession, JavaMethod.class, TypeDeclares.class,
				MethodParameterDefinition.class, "simpleName", "Order");

	}

	public void setupMethodResolverDisablingAutoboxing() throws Exception {
		final SLContext testCtx = graphSession.getContext("test");
		final List<SLContext> contexts = new ArrayList<SLContext>();
		contexts.add(testCtx);
		final SLNode currentContextRootNode = graphSession.getContext("test")
				.getRootNode();
		final SLNode abstractContextRootNode = abstractContex.getRootNode();
		helper = new JavaGraphNodeSupport(graphSession, currentContextRootNode,
				abstractContextRootNode);
		helper.setupJavaTypesOnCurrentContext();
		final JavaTypeResolver typeResolver = new JavaTypeResolver(
				abstractContex, contexts, false, graphSession);

		methodResolver = new MethodResolver<JavaType, JavaMethod>(typeResolver,
				graphSession, JavaMethod.class, TypeDeclares.class,
				MethodParameterDefinition.class, "simpleName", "Order");

	}

}
