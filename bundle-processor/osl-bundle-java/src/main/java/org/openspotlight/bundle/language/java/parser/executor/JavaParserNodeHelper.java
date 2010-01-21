/**
 * 
 */
package org.openspotlight.bundle.language.java.parser.executor;

import org.openspotlight.bundle.language.java.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.language.java.metamodel.link.InnerClass;
import org.openspotlight.bundle.language.java.metamodel.link.PackageType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeAnnotation;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaParserNodeHelper {
	private final SLNode currentContext;

	private final SLNode abstractContext;
	private final SLGraphSession session;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public JavaParserNodeHelper(final SLNode currentContext,
			final SLGraphSession session) {
		Assertions.checkNotNull("currentContext", currentContext);
		Assertions.checkNotNull("session", session);
		try {
			this.currentContext = currentContext;
			abstractContext = session.createContext(
					JavaParserExecutor.ABSTRACT_CONTEXT).getRootNode();
			this.session = session;
			Assertions.checkNotNull("abstractContext", abstractContext);
			logger.info("using abstract context:"
					+ abstractContext.getContext().getID() + ":"
					+ abstractContext.getName());
			logger.info("using current context:"
					+ currentContext.getContext().getID() + ":"
					+ currentContext.getName());
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public Pair<SLNode, SLNode> createDefaultPackage() {
		return createPackage(JavaParserExecutor.DEFAULT_PACKAGE);
	}

	// <SLNodeOnAbstractContext,SLNodeOnCurrentContext>
	private <T extends JavaType> Pair<SLNode, SLNode> createJavaType(
			final Class<T> type, final Pair<SLNode, SLNode> parent,
			final String typeName) {
		Assertions.checkNotEmpty("typeName", typeName);
		Assertions.checkNotNull("type", type);
		Assertions.checkNotNull("parent", parent);
		Assertions.checkNotNull("parent.K1", parent.getK1());
		Assertions.checkNotNull("parent.K2", parent.getK2());

		try {
			final T abstractNode = parent.getK1().addNode(type, typeName);
			final T concreteNode = parent.getK2().addNode(type, typeName);
			session.addLink(AbstractTypeBind.class, abstractNode, concreteNode,
					false);
			final Class<? extends SLLink> linkType;
			final String completeName = parent.getK2().getName() + "."
					+ typeName;
			if (parent.getK1() instanceof JavaPackage) {
				linkType = PackageType.class;
			} else {
				linkType = InnerClass.class;
			}
			concreteNode.setSimpleName(typeName);
			concreteNode.setCompleteName(completeName);
			abstractNode.setSimpleName(typeName);
			abstractNode.setCompleteName(completeName);

			session.addLink(linkType, parent.getK2(), concreteNode, false);
			logger.info("adding node " + concreteNode.getName() + " on parent "
					+ parent.getK1().getName());

			return new Pair<SLNode, SLNode>(abstractNode, concreteNode);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public Pair<SLNode, SLNode> createJavaTypeAnnotation(
			final Pair<SLNode, SLNode> parent, final String annotationName) {
		return createJavaType(JavaTypeAnnotation.class, parent, annotationName);
	}

	public Pair<SLNode, SLNode> createJavaTypeClass(
			final Pair<SLNode, SLNode> parent, final String className) {
		return createJavaType(JavaTypeClass.class, parent, className);
	}

	public Pair<SLNode, SLNode> createJavaTypeEnum(
			final Pair<SLNode, SLNode> parent, final String enumName) {
		return createJavaType(JavaTypeEnum.class, parent, enumName);
	}

	public Pair<SLNode, SLNode> createJavaTypeInterface(
			final Pair<SLNode, SLNode> parent, final String interfaceName) {
		return createJavaType(JavaTypeInterface.class, parent, interfaceName);
	}

	// <SLNodeOnAbstractContext,SLNodeOnCurrentContext>
	private Pair<SLNode, SLNode> createPackage(final String packageName) {
		try {
			Assertions.checkNotEmpty("packageName", packageName);
			final JavaPackage abstractNode = abstractContext.addNode(
					JavaPackage.class, packageName);
			final JavaPackage concreteNode = currentContext.addNode(
					JavaPackage.class, packageName);
			session.addLink(AbstractTypeBind.class, abstractNode, concreteNode,
					false);
			logger.info("adding node " + concreteNode.getName());
			return new Pair<SLNode, SLNode>(abstractNode, concreteNode);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public Pair<SLNode, SLNode> createPackageNode(final String packageName) {
		return createPackage(packageName);
	}

}