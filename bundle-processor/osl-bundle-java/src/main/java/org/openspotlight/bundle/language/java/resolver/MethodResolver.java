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
package org.openspotlight.bundle.language.java.resolver;

import org.openspotlight.bundle.language.java.resolver.TypeResolver.IncludedResult;
import org.openspotlight.bundle.language.java.resolver.TypeResolver.ResultOrder;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryResult;

import java.util.*;
import java.util.Map.Entry;

import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;

/**
 * This is a support class that resolves the methods.
 * 
 * @author porcelli
 */
public class MethodResolver<T extends SLNode, M extends SLNode> {

	/** The type resolver. */
	private TypeResolver<T> typeResolver = null;

	/** The graph session. */
	private SLGraphSession graphSession = null;

	/** Class that defines the method super type. */
	private Class<? extends SLNode> methodSuperType = null;

	/** Class that defines the link between type and method. */
	private Class<? extends SLLink> typeMethodLink = null;

	/** Class that defines the link between method and its parameter types. */
	private Class<? extends SLLink> methodParameterDefinitionLink = null;

	/** The property name that defines the method simple name. */
	private String propertySimpleMethodName = null;

	/** The property name that defines the order of method parameter definition. */
	private String propertyMethodDefinitionOrderName = null;

	/** The cache for fastest method resolution . */
	private Map<String, String> cache = null;

	/**
	 * Instantiates a new method resolver.
	 * 
	 * @param typeResolver
	 *            the type resolver
	 * @param graphSession
	 *            the graph session
	 * @param methodSuperType
	 *            class that defines the method super type
	 * @param typeMethodLink
	 *            class that defines the link between type and method
	 * @param methodParameterDefinitionLink
	 *            class that defines the link between method and its parameter
	 *            types
	 * @param propertySimpleMethodName
	 *            property name that defines the order of method parameter
	 *            definition
	 * @param propertyMethodDefinitionOrderName
	 *            property name that defines the order of method parameter
	 *            definition
	 */
	public MethodResolver(final AbstractTypeResolver<T> typeResolver,
			final SLGraphSession graphSession,
			final Class<? extends SLNode> methodSuperType,
			final Class<? extends SLLink> typeMethodLink,
			final Class<? extends SLLink> methodParameterDefinitionLink,
			final String propertySimpleMethodName,
			final String propertyMethodDefinitionOrderName) {
		checkNotNull("typeResolver", typeResolver);
		checkNotNull("graphSession", graphSession);
		checkNotNull("methodSuperType", methodSuperType);
		checkNotNull("typeMethodLink", typeMethodLink);
		checkNotNull("methodParameterDefinitionLink",
				methodParameterDefinitionLink);
		checkNotEmpty("propertySimpleMethodName", propertySimpleMethodName);
		checkNotEmpty("propertyMethodDefinitionOrderName",
				propertyMethodDefinitionOrderName);

		this.typeResolver = typeResolver;
		this.graphSession = graphSession;

		this.methodSuperType = methodSuperType;
		this.typeMethodLink = typeMethodLink;
		this.methodParameterDefinitionLink = methodParameterDefinitionLink;
		this.propertySimpleMethodName = propertySimpleMethodName;
		this.propertyMethodDefinitionOrderName = propertyMethodDefinitionOrderName;
		this.cache = new HashMap<String, String>();

	}

	/**
	 * Caches the found method.
	 * 
	 * @param type
	 *            the type
	 * @param methodName
	 *            the method name
	 * @param paramTypes
	 *            the param types
	 * @param foundMethod
	 *            the found method
	 * @return the found method
	 * @throws SLBundleException
	 */
	private <XM extends M> XM cacheFoundMethod(final T type,
			final String methodName, final List<T> paramTypes,
			final XM foundMethod) throws SLBundleException {
		try {
			final String cachedId = this.getUniqueId(type, methodName,
					paramTypes);
			this.cache.put(cachedId, foundMethod.getID());
			return foundMethod;
		} catch (final SLGraphSessionException e) {
			throw new SLBundleException();
		}
	}

	/**
	 * Returns the best match based on param types.
	 * 
	 * @param methods
	 *            the found methods that has the same number of parameters and
	 *            match the types
	 * @param paramTypes
	 *            the param types
	 * @return the best match
	 * @throws SLBundleException
	 *             if method not found
	 */
	public <XM extends M> XM getBestMatch(final Map<XM, T[]> methods,
			final List<T> paramTypes) throws SLBundleException {

		final Map<XM, T[]> possibleMethods = methods;

		int maxLooping = methods.size() + 1;
		int loopCount = 0;

		while (true) {
			loopCount++;
			if (loopCount == maxLooping) {
				throw new SLBundleException();
			}

			final Iterator<Entry<XM, T[]>> methodIterator = possibleMethods
					.entrySet().iterator();
			final Entry<XM, T[]> firstMethod = methodIterator.next();
			if (!methodIterator.hasNext()) {
				return firstMethod.getKey();
			}
			final Entry<XM, T[]> secondMethod = methodIterator.next();

			for (int i = 0; i < paramTypes.size(); i++) {
				final T activeParam = paramTypes.get(i);
				final T firstMethodParam = firstMethod.getValue()[i];
				final T secondMethodParam = secondMethod.getValue()[i];

				final BestTypeMatch bestTypeResult = this.typeResolver
						.bestMatch(activeParam, firstMethodParam,
								secondMethodParam);

				if (bestTypeResult == BestTypeMatch.T1) {
					methods.remove(secondMethod.getKey());
					maxLooping = methods.size() + 1;
					loopCount = 0;
				}
			}
		}
	}

	/**
	 * Gets the cache.
	 * 
	 * @return the cache
	 */
	Map<String, String> getCache() {
		return this.cache;

	}

	/**
	 * Gets the cached data.
	 * 
	 * @param type
	 *            the type
	 * @param methodName
	 *            the method name
	 * @param paramTypes
	 *            the param types
	 * @return the cached result or null if it is not cached
	 */
	@SuppressWarnings("unchecked")
	private <XM extends M> XM getCachedData(final T type,
			final String methodName, final List<T> paramTypes) {
		String cachedId = "";
		try {
			cachedId = this.getUniqueId(type, methodName, paramTypes);
			final String id = this.cache.get(cachedId);
			if (id != null) {
				return (XM) this.graphSession.getNodeByID(id);
			}
		} catch (final Exception e) {
			this.cache.remove(cachedId);
		}
		return null;
	}

	/**
	 * Returns the method based on type (that declares the method) and method
	 * name
	 * 
	 * @param type
	 *            the type
	 * @param methodName
	 *            the method name
	 * @return the found method
	 * @throws SLGraphSessionException
	 *             general graph session exception
	 * @throws SLBundleException
	 *             if method not found.
	 * @throws SLInvalidQuerySyntaxException
	 */
	public <XM extends M> XM getMethod(final T type, final String methodName)
			throws SLBundleException,
			SLInvalidQuerySyntaxException {
		return this.<XM> getMethod(type, methodName, null);
	}

	/**
	 * Returns the method based on type (that declares the method), method name
	 * and its param types.
	 * 
	 * @param type
	 *            the type
	 * @param methodName
	 *            the method name
	 * @param paramTypes
	 *            the param types
	 * @return the method
	 * @throws SLGraphSessionException
	 *             general graph session exception
	 * @throws SLBundleException
	 *             if method not found.
	 * @throws SLInvalidQuerySyntaxException
	 */
	@SuppressWarnings("unchecked")
	public <XM extends M> XM getMethod(final T type, final String methodName,
			final List<T> paramTypes) throws 
			SLBundleException, SLInvalidQuerySyntaxException {
		checkNotNull("type", type);
		checkNotEmpty("methodName", methodName);

		if (!this.isContreteType(type)) {
			throw new IllegalArgumentException("INVALID PARAMETER");
		}

		int paramSize = 0;
		if (paramTypes != null) {
			if (!this.isContreteType(paramTypes)) {
				throw new IllegalArgumentException("INVALID PARAMETER");
			}
			paramSize = paramTypes.size();
		}

		final XM chachedMethod = this.<XM> getCachedData(type, methodName,
				paramTypes);

		if (chachedMethod != null) {
			return chachedMethod;
		}

		final List<T> typeHierarchy = this.typeResolver.getAllParents(type,
				ResultOrder.DESC, IncludedResult.INCLUDE_ACTUAL_TYPE_ON_RESULT);

		final SLQueryApi query = this.graphSession.createQueryApi();

		query.select().type(this.methodSuperType.getName()).subTypes().comma()
				.byLink(this.typeMethodLink.getName()).b().selectEnd().select()
				.type(this.methodSuperType.getName()).subTypes().selectEnd()
				.where().type(this.methodSuperType.getName()).subTypes().each()
				.link(this.methodParameterDefinitionLink.getName()).a().count()
				.equalsTo().value(paramSize).and().each().property(
						this.propertySimpleMethodName).equalsTo().value(
						methodName).typeEnd().whereEnd();

		for (final T activeType : typeHierarchy) {
			final List<T> inputType = new LinkedList<T>();
			inputType.add(activeType);

			final SLQueryResult result = query
					.execute((Collection<SLNode>) inputType);

			// FIXME: where cast bug fixed..: REMOVE THIS!
			final List<XM> validMethods = new LinkedList<XM>();
			for (final SLNode activeMethod : result.getNodes()) {
				validMethods.add((XM) this.graphSession
						.getNodeByID(activeMethod.getID()));
			}

			if (paramSize == 0 && validMethods.size() > 0) {
				return this.cacheFoundMethod(type, methodName, paramTypes,
						validMethods.get(0));

			}

			final Map<XM, T[]> possibleMethods = new TreeMap<XM, T[]>();
			// Now checks for paramTypes

			for (final XM activeMethod : validMethods) {
				final T[] orderedParams = (T[]) new SLNode[paramSize];

				final Collection<? extends SLLink> links = this.graphSession
						.getUnidirectionalLinksBySource(
								this.methodParameterDefinitionLink,
								activeMethod);

				for (final SLLink methodParameterDefinition : links) {
					orderedParams[methodParameterDefinition.getProperty(
							Integer.class,
							this.propertyMethodDefinitionOrderName).getValue()] = (T) methodParameterDefinition
							.getTarget();

				}

				boolean isValidMethod = true;
				for (int i = 0; i < orderedParams.length; i++) {
					final T activeParamType = orderedParams[i];
					if (!this.typeResolver.isTypeOf(paramTypes.get(i),
							activeParamType)) {
						isValidMethod = false;
						break;
					}
				}
				if (isValidMethod) {
					possibleMethods.put(activeMethod, orderedParams);
				}
			}
			if (possibleMethods.size() == 1) {
				return this.cacheFoundMethod(type, methodName, paramTypes,
						possibleMethods.entrySet().iterator().next().getKey());
			}

			if (possibleMethods.size() > 0) {
				return this.cacheFoundMethod(type, methodName, paramTypes, this
						.getBestMatch(possibleMethods, paramTypes));
			}
		}

		throw new SLBundleException();
	}

	/**
	 * Gets the unique id based on parameters.
	 * 
	 * @param type
	 *            the type
	 * @param methodName
	 *            the method name
	 * @param paramTypes
	 *            the param types
	 * @return the unique id
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 */
	String getUniqueId(final T type, final String methodName,
			final List<T> paramTypes) {
		final StringBuilder sb = new StringBuilder();

		sb.append(type.getID());
		sb.append(":");
		sb.append(methodName);
		sb.append(":");

		if (paramTypes != null) {
			for (final T t : paramTypes) {
				sb.append(t.getID());
				sb.append(":");
			}
		}

		return sb.toString();
	}

	/**
	 * Checks if is contrete type.
	 * 
	 * @param types
	 *            the types
	 * @return true, if is contrete type
	 */
	private boolean isContreteType(final List<T> types) {
		for (final T t : types) {
			if (!(this.typeResolver.isConcreteType(t) || this.typeResolver
					.isPrimitiveType(t))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if is contrete type.
	 * 
	 * @param type
	 *            the type
	 * @return true, if is contrete type
	 */
	private boolean isContreteType(final T type) {
		return this.typeResolver.isConcreteType(type);
	}
}
