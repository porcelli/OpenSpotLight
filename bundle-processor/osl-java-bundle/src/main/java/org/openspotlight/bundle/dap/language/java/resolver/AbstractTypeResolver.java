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
/**
 * 
 */
package org.openspotlight.bundle.dap.language.java.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;

// TODO: Auto-generated Javadoc
/**
 * The Class TypeFinder.
 */
public abstract class AbstractTypeResolver<N extends SLNode> implements
		TypeResolver<N> {

	protected static enum Recursive {
		ONLY_DIRECT_PARENTS, FULLY_RECURSIVE
	}

	/** The implementation inheritance links. */
	private final Set<Class<? extends SLLink>> implementationInheritanceLinks;

	/** The interface inheritance links. */
	private final Set<Class<? extends SLLink>> interfaceInheritanceLinks;

	/** The primitive hierarchy links. */
	private final Set<Class<? extends SLLink>> primitiveHierarchyLinks;

	/** The abstract context. */
	private final SLContext abstractContext;

	/** The ordered active contexts. */
	private final List<SLContext> orderedActiveContexts;

	/** The primitive types. */
	private final Set<Class<?>> primitiveTypes;

	/** The enable boxing. */
	private final boolean enableBoxing;

	/** The session. */
	private final SLGraphSession session;

	/**
	 * Instantiates a new type finder.
	 * 
	 * @param implementationInheritanceLinks
	 *            the implementation inheritance links
	 * @param interfaceInheritanceLinks
	 *            the interface inheritance links
	 * @param primitiveHierarchyLinks
	 *            the primitive hierarchy links
	 * @param abstractContext
	 *            the abstract context
	 * @param orderedActiveContexts
	 *            the ordered active contexts
	 * @param primitiveTypes
	 *            the primitive types
	 * @param enableBoxing
	 *            the enable boxing
	 * @param session
	 *            the session
	 */
	protected AbstractTypeResolver(
			final Set<Class<? extends SLLink>> implementationInheritanceLinks,
			final Set<Class<? extends SLLink>> interfaceInheritanceLinks,
			final Set<Class<? extends SLLink>> primitiveHierarchyLinks,
			final SLContext abstractContext,
			final List<SLContext> orderedActiveContexts,
			final Set<Class<?>> primitiveTypes,
			final Set<Class<?>> concreteTypes, final boolean enableBoxing,
			final SLGraphSession session) {
		Assertions.checkNotNull("implementationInheritanceLinks",
				implementationInheritanceLinks);
		Assertions.checkNotNull("interfaceInheritanceLinks",
				interfaceInheritanceLinks);
		Assertions.checkNotNull("primitiveHierarchyLinks",
				primitiveHierarchyLinks);
		Assertions.checkNotNull("abstractContext", abstractContext);
		Assertions.checkNotNull("orderedActiveContexts", orderedActiveContexts);
		Assertions.checkNotNull("primitiveTypes", primitiveTypes);
		Assertions.checkNotNull("concreteTypes", concreteTypes);
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("implementationInheritanceLinksNotEmpty",
				implementationInheritanceLinks.size() > 0);
		Assertions.checkCondition("interfaceInheritanceLinksNotEmpty",
				interfaceInheritanceLinks.size() > 0);
		Assertions.checkCondition("primitiveHierarchyLinksNotEmpty",
				primitiveHierarchyLinks.size() > 0);
		Assertions.checkCondition("orderedActiveContextsNotEmpty",
				orderedActiveContexts.size() > 0);
		Assertions.checkCondition("primitiveTypesNotEmpty", primitiveTypes
				.size() > 0);
		this.implementationInheritanceLinks = implementationInheritanceLinks;
		this.interfaceInheritanceLinks = interfaceInheritanceLinks;
		this.primitiveHierarchyLinks = primitiveHierarchyLinks;
		this.abstractContext = abstractContext;
		final ArrayList<SLContext> all = new ArrayList<SLContext>(
				orderedActiveContexts);
		if (!all.contains(abstractContext)) {
			all.add(abstractContext);
		}
		this.orderedActiveContexts = Collections.unmodifiableList(all);
		this.primitiveTypes = primitiveTypes;
		this.enableBoxing = enableBoxing;
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#bestMatch
	 * (T, T, T)
	 */
	public <T extends N> BestTypeMatch bestMatch(final T reference, final T t1,
			final T t2) throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countAllChildren(T)
	 */
	public <T extends N> int countAllChildren(final T activeType)
			throws InternalJavaFinderError {
		return this.countAllChildren(activeType,
				IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countAllChildren(T,
	 * org.openspotlight.bundle.dap.language.java.support.AbstractTypeFinder
	 * .IncludedResult)
	 */
	public <T extends N> int countAllChildren(final T activeType,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#countAllParents
	 * (T)
	 */
	public <T extends N> int countAllParents(final T activeType)
			throws InternalJavaFinderError {
		return this.countAllParents(activeType,
				IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#countAllParents
	 * (T,
	 * org.openspotlight.bundle.dap.language.java.support.AbstractTypeFinder.
	 * IncludedResult)
	 */
	public <T extends N> int countAllParents(final T activeType,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countConcreteChildren(T)
	 */
	public <T extends N> int countConcreteChildren(final T activeType)
			throws InternalJavaFinderError {
		return this.countConcreteChildren(activeType,
				IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countConcreteChildren(T,
	 * org.openspotlight.bundle.dap.language.java.support
	 * .AbstractTypeFinder.IncludedResult)
	 */
	public <T extends N> int countConcreteChildren(final T activeType,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countConcreteParents(T)
	 */
	public <T extends N> int countConcreteParents(final T activeType)
			throws InternalJavaFinderError {
		return this.countConcreteParents(activeType,
				IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countConcreteParents(T,
	 * org.openspotlight.bundle.dap.language.java.support
	 * .AbstractTypeFinder.IncludedResult)
	 */
	public <T extends N> int countConcreteParents(final T activeType,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countInterfaceChildren(T)
	 */
	public <T extends N> int countInterfaceChildren(final T activeType)
			throws InternalJavaFinderError {
		return this.countInterfaceChildren(activeType,
				IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countInterfaceChildren(T,
	 * org.openspotlight.bundle.dap.language.java.support
	 * .AbstractTypeFinder.IncludedResult)
	 */
	public <T extends N> int countInterfaceChildren(final T activeType,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countInterfaceParents(T)
	 */
	public <T extends N> int countInterfaceParents(final T activeType)
			throws InternalJavaFinderError {
		return this.countInterfaceParents(activeType,
				IncludedResult.DO_NOT_INCLUDE_ACTUAL_TYPE_ON_RESULT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * countInterfaceParents(T,
	 * org.openspotlight.bundle.dap.language.java.support
	 * .AbstractTypeFinder.IncludedResult)
	 */
	public <T extends N> int countInterfaceParents(final T activeType,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Gets the abstract context.
	 * 
	 * @return the abstract context
	 */
	protected SLContext getAbstractContext() {
		return this.abstractContext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#getAllChildren
	 * (A,
	 * org.openspotlight.bundle.dap.language.java.support.AbstractTypeFinder.
	 * ResultOrder,
	 * org.openspotlight.bundle.dap.language.java.support.AbstractTypeFinder
	 * .IncludedResult)
	 */
	public <T extends N, A extends T> List<T> getAllChildren(
			final A activeType, final ResultOrder order,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#getAllParents
	 * (A,
	 * org.openspotlight.bundle.dap.language.java.support.AbstractTypeFinder.
	 * ResultOrder,
	 * org.openspotlight.bundle.dap.language.java.support.AbstractTypeFinder
	 * .IncludedResult)
	 */
	public <T extends N, A extends T> List<T> getAllParents(final A activeType,
			final ResultOrder order, final IncludedResult includedResult)
			throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * getConcreteChildren(A,
	 * org.openspotlight.bundle.dap.language.java.support.
	 * AbstractTypeFinder.ResultOrder,
	 * org.openspotlight.bundle.dap.language.java
	 * .support.AbstractTypeFinder.IncludedResult)
	 */
	public <T extends N, A extends T> List<T> getConcreteChildren(
			final A activeType, final ResultOrder order,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * getConcreteParents(A,
	 * org.openspotlight.bundle.dap.language.java.support.AbstractTypeFinder
	 * .ResultOrder,
	 * org.openspotlight.bundle.dap.language.java.support.AbstractTypeFinder
	 * .IncludedResult)
	 */
	public <T extends N, A extends N> List<T> getConcreteParents(
			final A activeType, final ResultOrder order,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * getDirectConcreteChildren(A)
	 */
	public <T extends N, A extends T> List<T> getDirectConcreteChildren(
			final A activeType) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * getDirectConcreteParents(A)
	 */
	public <T extends N, A extends T> List<T> getDirectConcreteParents(
			final A activeType) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * getDirectInterfaceChildren(A)
	 */
	public <T extends N, A extends T> List<T> getDirectInterfaceChildren(
			final A activeType) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * getDirectInterfaceParents(A)
	 */
	public <T extends N, A extends T> List<T> getDirectInterfaceParents(
			final A activeType) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the implementation inheritance links.
	 * 
	 * @return the implementation inheritance links
	 */
	protected Set<Class<? extends SLLink>> getImplementationInheritanceLinks()
			throws LinkNotFoundException {
		return this.implementationInheritanceLinks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * getInterfaceChildren(A,
	 * org.openspotlight.bundle.dap.language.java.support
	 * .AbstractTypeFinder.ResultOrder,
	 * org.openspotlight.bundle.dap.language.java
	 * .support.AbstractTypeFinder.IncludedResult)
	 */
	public <T extends N, A extends T> List<T> getInterfaceChildren(
			final A activeType, final ResultOrder order,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the interface inheritance links.
	 * 
	 * @return the interface inheritance links
	 */
	protected Set<Class<? extends SLLink>> getInterfaceInheritanceLinks()
			throws LinkNotFoundException {
		return this.interfaceInheritanceLinks;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.bundle.dap.language.java.support.TypeFinder#
	 * getInterfaceParents(A,
	 * org.openspotlight.bundle.dap.language.java.support.
	 * AbstractTypeFinder.ResultOrder,
	 * org.openspotlight.bundle.dap.language.java
	 * .support.AbstractTypeFinder.IncludedResult)
	 */
	public <T extends N, A extends T> List<T> getInterfaceParents(
			final A activeType, final ResultOrder order,
			final IncludedResult includedResult) throws InternalJavaFinderError {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the ordered active contexts.
	 * 
	 * @return the ordered active contexts
	 */
	protected List<SLContext> getOrderedActiveContexts() {
		return this.orderedActiveContexts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#getPrimitiveFor
	 * (A)
	 */
	public <T extends N, A extends N> T getPrimitiveFor(final A wrappedType)
			throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Gets the primitive hierarchy links.
	 * 
	 * @return the primitive hierarchy links
	 */
	protected Set<Class<? extends SLLink>> getPrimitiveHierarchyLinks() {
		return this.primitiveHierarchyLinks;
	}

	/**
	 * Gets the primitive types.
	 * 
	 * @return the primitive types
	 */
	protected Set<Class<?>> getPrimitiveTypes() {
		return this.primitiveTypes;
	}

	/**
	 * Gets the session.
	 * 
	 * @return the session
	 */
	protected SLGraphSession getSession() {
		return this.session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#getType
	 * (java.lang.String)
	 */
	public abstract <T extends N> T getType(String typeToSolve)
			throws InternalJavaFinderError;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#getType
	 * (java.lang.String, A, java.util.List)
	 */
	public abstract <T extends N, A extends T> N getType(String typeToSolve,
			A activeType, List<? extends N> parametrizedTypes)
			throws InternalJavaFinderError;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#getWrapperFor
	 * (A)
	 */
	public <T extends N, A extends N> T getWrapperFor(final A primitiveType)
			throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#isConcreteType
	 * (T)
	 */
	public <T extends N> boolean isConcreteType(final T type)
			throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/**
	 * Checks if is enable boxing.
	 * 
	 * @return true, if is enable boxing
	 */
	protected boolean isEnableBoxing() {
		return this.enableBoxing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#isPrimitiveType
	 * (T)
	 */
	public <T extends N> boolean isPrimitiveType(final T type)
			throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.bundle.dap.language.java.support.TypeFinder#isTypeOf(T,
	 * A)
	 */
	public <T extends N, A extends N> boolean isTypeOf(final T implementation,
			final A superType) throws InternalJavaFinderError {
		throw new UnsupportedOperationException("Not implemented yet");
	}

}
