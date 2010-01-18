package org.openspotlight.bundle.language.java.parser.executor;

import java.util.Stack;

import org.antlr.runtime.tree.Tree;
import org.openspotlight.bundle.common.metrics.SourceLineInfoAggregator;
import org.openspotlight.bundle.common.parser.SLCommonToken;
import org.openspotlight.common.Pair;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.graph.SLNode;

public class JavaParserExecutor {
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
			return null;
		}

		public Pair<SLNode, SLNode> createJavaTypeAnnotation(
				final Pair<SLNode, SLNode> parent, final String annotationName) {
			return null;
		}

		public Pair<SLNode, SLNode> createJavaTypeClass(
				final Pair<SLNode, SLNode> parent, final String className) {
			return null;
		}

		public Pair<SLNode, SLNode> createJavaTypeEnum(
				final Pair<SLNode, SLNode> parent, final String enumName) {
			return null;
		}

		public Pair<SLNode, SLNode> createJavaTypeInterface(
				final Pair<SLNode, SLNode> parent, final String interfaceName) {
			return null;
		}

		public Pair<SLNode, SLNode> createPackageNode(final String packageName) {
			return null;
		}

	}

	private JavaParserNodeHelper helper;
	private final ExecutionContext context;
	private final String artifactContent;
	private final ArtifactWithSyntaxInformation artifact;
	private final String artifactName;
	private final String artifactVersion;

	// <SLNodeOnCurrentContext,SLNodeOnAbstractContext>
	private final Stack<Pair<SLNode, SLNode>> typeContext = new Stack<Pair<SLNode, SLNode>>();

	private final SourceLineInfoAggregator sourceLineAggregator;

	public JavaParserExecutor(final ExecutionContext context,
			final String artifactContent, final String artifactName,
			final String artifactVersion,
			final ArtifactWithSyntaxInformation artifact,
			final SourceLineInfoAggregator sourceLineInfoAggregator) {
		this.artifact = artifact;
		this.context = context;
		this.artifactContent = artifactContent;
		this.artifactName = artifactName;
		this.artifactVersion = artifactVersion;
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
		final Pair<SLNode, SLNode> newNode = helper.createJavaTypeEnum(
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
