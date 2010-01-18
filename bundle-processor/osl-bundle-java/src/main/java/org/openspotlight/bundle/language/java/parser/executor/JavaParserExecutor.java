package org.openspotlight.bundle.language.java.parser.executor;

import java.util.Stack;

import org.antlr.runtime.tree.Tree;
import org.openspotlight.bundle.common.metrics.SourceLineInfoAggregator;
import org.openspotlight.bundle.common.parser.SLCommonToken;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeAnnotation;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.common.Pair;
import org.openspotlight.graph.SLNode;

public class JavaParserExecutor implements JavaConstants {
	private static class JavaParserNodeHelper {
		private final SLNode currentContext;
		private final SLNode abstractContext;

		public JavaParserNodeHelper(final SLNode currentContext,
				final SLNode abstractContext) {
			super();
			this.currentContext = currentContext;
			this.abstractContext = abstractContext;
		}

		public Pair<SLNode, SLNode> createDefaultPackage() {
			return createPackage(DEFAULT_PACKAGE);
		}

		private Pair<SLNode, SLNode> createJavaType(
				final Class<? extends JavaType> class1,
				final Pair<SLNode, SLNode> parent, final String annotationName) {
			// TODO Auto-generated method stub
			return null;
		}

		public Pair<SLNode, SLNode> createJavaTypeAnnotation(
				final Pair<SLNode, SLNode> parent, final String annotationName) {
			return createJavaType(JavaTypeAnnotation.class, parent,
					annotationName);
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
			return createJavaType(JavaTypeInterface.class, parent,
					interfaceName);
		}

		private Pair<SLNode, SLNode> createPackage(final String defaultPackage) {
			// TODO Auto-generated method stub
			return null;
		}

		public Pair<SLNode, SLNode> createPackageNode(final String packageName) {
			return createPackageNode(packageName);
		}

	}

	private JavaParserNodeHelper helper;

	// <SLNodeOnCurrentContext,SLNodeOnAbstractContext>
	private final Stack<Pair<SLNode, SLNode>> typeContext = new Stack<Pair<SLNode, SLNode>>();

	private final SourceLineInfoAggregator sourceLineAggregator;

	public JavaParserExecutor(
			final SourceLineInfoAggregator sourceLineInfoAggregator) {
		sourceLineAggregator = sourceLineInfoAggregator;
	}

	public void createDefaultPackage() {
		final Pair<SLNode, SLNode> newNode = helper.createDefaultPackage();
		typeContext.push(newNode);
	}

	public void createJavaTypeAnnotation(final SLCommonToken identifier292) {
		final Pair<SLNode, SLNode> newNode = helper.createJavaTypeAnnotation(
				typeContext.peek(), identifier292.getText());
		typeContext.push(newNode);
	}

	public void createJavaTypeClass(final SLCommonToken identifier35) {
		final Pair<SLNode, SLNode> newNode = helper.createJavaTypeClass(
				typeContext.peek(), identifier35.getText());
		typeContext.push(newNode);
	}

	public void createJavaTypeEnum(final SLCommonToken identifier54) {
		final Pair<SLNode, SLNode> newNode = helper.createJavaTypeEnum(
				typeContext.peek(), identifier54.getText());
		typeContext.push(newNode);

	}

	public void createJavaTypeInterface(final SLCommonToken identifier75) {
		final Pair<SLNode, SLNode> newNode = helper.createJavaTypeInterface(
				typeContext.peek(), identifier75.getText());
		typeContext.push(newNode);

	}

	public void createPackageNode(final Object object) {
		final Tree qualifiedName = getTree(object);
		final Pair<SLNode, SLNode> newNode = helper
				.createPackageNode(qualifiedName.getText());
		typeContext.push(newNode);
	}

	private Tree getTree(final Object element) {
		return (Tree) element;
	}

	public void popContext() {
		typeContext.pop();
	}

	public SourceLineInfoAggregator sourceLine() {
		return sourceLineAggregator;
	}
}
