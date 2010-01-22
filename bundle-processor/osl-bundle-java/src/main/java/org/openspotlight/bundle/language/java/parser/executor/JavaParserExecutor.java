package org.openspotlight.bundle.language.java.parser.executor;

import java.util.List;
import java.util.Stack;

import org.antlr.runtime.tree.Tree;
import org.openspotlight.bundle.common.metrics.SourceLineInfoAggregator;
import org.openspotlight.bundle.common.parser.SLCommonToken;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.node.JavaDataField;
import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLNode;

public class JavaParserExecutor implements JavaConstants {
	private final JavaParserNodeHelper helper;

	// <SLNodeOnAbstractContext,SLNodeOnCurrentContext>
	private final Stack<Pair<SLNode, SLNode>> typeContext = new Stack<Pair<SLNode, SLNode>>();

	private final SourceLineInfoAggregator sourceLineAggregator;

	public JavaParserExecutor(
			final SourceLineInfoAggregator sourceLineInfoAggregator,
			final JavaParserNodeHelper helper) {
		sourceLineAggregator = sourceLineInfoAggregator;
		this.helper = helper;
	}

	public void createDefaultPackage() {
		final Pair<SLNode, SLNode> newNode = helper.createDefaultPackage();
		typeContext.push(newNode);
	}

	public void createFields(final List<String> list) {
		try {
			final SLNode parent = typeContext.peek().getK1();
			for (final String s : list) {
				parent.addNode(JavaDataField.class, s);
			}

		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

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

	public void createMethodDeclare(final String string) {
		// TODO Auto-generated method stub

	}

	public void createPackageNode(final Object object) {
		final Tree qualifiedName = getTree(object);
		final StringBuilder packageName = new StringBuilder();
		for (int i = 0, count = qualifiedName.getChildCount(); i < count; i++) {
			packageName.append(qualifiedName.getChild(i));
		}
		final Pair<SLNode, SLNode> newNode = helper
				.createPackageNode(packageName.toString());
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
