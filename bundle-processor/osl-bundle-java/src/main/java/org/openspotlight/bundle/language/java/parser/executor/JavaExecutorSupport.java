package org.openspotlight.bundle.language.java.parser.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openspotlight.bundle.common.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.common.concurrent.NeedsSyncronizationCollection;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaExecutorSupport {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final boolean quiet = true;

	final SLNode currentContext;

	final SLNode abstractContext;

	final SLGraphSession session;
	final ByPropertyFinder currentContextFinder;
	final ByPropertyFinder abstractContextFinder;
	final List<String> includedPackages = new LinkedList<String>();

	final List<String> includedClasses = new LinkedList<String>();

	final List<String> includedStaticClasses = new LinkedList<String>();

	final List<String> includedStaticMethods = new LinkedList<String>();

	final Map<SLNode, SLNode> concreteAbstractCache = new HashMap<SLNode, SLNode>();

	final Map<SLNode, SLNode> abstractConcreteCache = new HashMap<SLNode, SLNode>();
	final HashMap<String, SLNode> importedNodeCache = new HashMap<String, SLNode>();

	final String completeArtifactName;

	public JavaExecutorSupport(final SLNode currentContext,
			final SLGraphSession session, final String completeArtifactName)
	throws Exception {
		this.currentContext = currentContext;
		abstractContext = session.createContext(JavaConstants.ABSTRACT_CONTEXT)
		.getRootNode();
		this.session = session;
		this.completeArtifactName = completeArtifactName;
		currentContextFinder = new ByPropertyFinder(completeArtifactName,
				session, currentContext);
		abstractContextFinder = new ByPropertyFinder(completeArtifactName,
				session, abstractContext);
		if (logger.isDebugEnabled()) {
			logger.debug(completeArtifactName + ": " + "creating "
					+ getClass().getSimpleName() + " with "
					+ currentContext.getContext().getID() + ":"
					+ currentContext.getName() + "/"
					+ abstractContext.getContext().getID() + ":"
					+ abstractContext.getName());
		}
	}

	@SuppressWarnings("unchecked")
	<T extends SLNode, W extends SLNode> W findEquivalend(final T source,
			final WhatContext whatContext) throws Exception {
		if (!(source instanceof JavaPackage || source instanceof JavaType)) {
			if (logger.isDebugEnabled()) {
				logger.debug("returning source node for "
						+ source.getClass().getInterfaces()[0].getSimpleName()
						+ " " + source.getName() + " within " + whatContext
						+ " due to its type "
						+ source.getClass().getInterfaces()[0].getSimpleName());
			}
			return (W) source;
		}
		T cached = null;
		SLContext targetContext = null;
		switch (whatContext) {
		case ABSTRACT:
			cached = (T) concreteAbstractCache.get(source);
			targetContext = abstractContext.getContext();
			break;
		case CONCRETE:
			cached = (T) abstractConcreteCache.get(source);
			targetContext = currentContext.getContext();
			break;
		default:
			throw Exceptions.logAndReturn(new IllegalStateException(
			"Wrong number of elements on internal enum WhatContext"));
		}
		if (cached != null) {
			return (W) cached;
		}
		if (source.getContext().equals(targetContext)) {
			if (logger.isDebugEnabled()) {
				logger.debug("returning the same source for equivalent node "
						+ source.getClass().getInterfaces()[0].getSimpleName()
						+ " " + source.getName() + " within " + whatContext
						+ " due to its context");
			}
			return (W) source;
		}

		NeedsSyncronizationCollection<AbstractTypeBind> links = session
		.getLinks(AbstractTypeBind.class, source, null);
		if (links.size() == 0) {
			links = session.getLinks(AbstractTypeBind.class, null, source);
		}
		if (links.size() == 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Didn't find equivalent node for "
						+ source.getClass().getInterfaces()[0].getSimpleName()
						+ " " + source.getName() + " within " + whatContext
						+ " due to link mess");
			}

			return null;
		}
		W found = null;
		synchronized (links.getLockObject()) {
			for (final SLLink link : links) {
				W tmpFound = null;
				if (source.equals(link.getSource())) {
					tmpFound = (W) link.getTarget();
				} else if (source.equals(link.getTarget())) {
					tmpFound = (W) link.getSource();
				}
				if (tmpFound != null
						&& tmpFound.getContext().equals(targetContext)) {
					found = tmpFound;
					break;
				}
			}
		}
		if (found == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Didn't find equivalent node for "
						+ source.getClass().getInterfaces()[0].getSimpleName()
						+ " " + source.getName() + " within " + whatContext);
			}
			return null;
		}

		switch (whatContext) {
		case ABSTRACT:
			putOnBothCaches(source, found);
			break;
		case CONCRETE:
			putOnBothCaches(found, source);
			break;
		default:
			throw Exceptions.logAndReturn(new IllegalStateException(
			"Wrong number of elements on internal enum WhatContext"));
		}
		return found;

	}

	JavaType findOnContext(final String string, final ByPropertyFinder finder) {
		try {
			final JavaType cached = (JavaType) importedNodeCache.get(string);
			if (cached != null) {
				return cached;
			}
			final List<String> possibleNames = new ArrayList<String>();
			possibleNames.add(string);
			if (!includedClasses.contains(string)) {
				for (final String pack : includedPackages) {
					possibleNames.add(pack + "." + string);
				}
				for (final String pack : includedClasses) {
					possibleNames.add(pack);
				}
				for (final String pack : includedStaticClasses) {
					possibleNames.add(pack + "." + string);

				}
			}

			for (final String possibleName : possibleNames) {
				final JavaType javaType = finder.findByProperty(JavaType.class,
						"qualifiedName", possibleName);
				if (javaType != null) {
					importedNodeCache.put(javaType.getSimpleName(), javaType);
					importedNodeCache
					.put(javaType.getQualifiedName(), javaType);
					return javaType;
				}
			}
			if (logger.isDebugEnabled()) {
				logger.info(completeArtifactName
						+ ": any node was found for type " + string);
			}
			return null;
		} catch (final Exception e) {
			if (quiet) {
				Exceptions.catchAndLog(e);
				return null;
			}
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	JavaType findPrimitiveType(final String string) {
		try {
			final JavaTypePrimitive primitive = abstractContext.addNode(
					JavaTypePrimitive.class, string);
			return primitive;
		} catch (final Exception e) {
			if (quiet) {
				Exceptions.catchAndLog(e);
				return null;
			}

			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	JavaType internalFindSimpleType(final String string) {
		if (JavaPrimitiveValidTypes.isPrimitive(string)) {
			return findPrimitiveType(string);
		}
		JavaType type = findOnContext(string, currentContextFinder);
		if (type == null) {
			type = findOnContext(string, abstractContextFinder);
		}
		return type;
	}

	void putOnBothCaches(final SLNode concrete, final SLNode abstractN) {
		concreteAbstractCache.put(concrete, abstractN);
		abstractConcreteCache.put(abstractN, concrete);
	}

}
