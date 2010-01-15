package org.openspotlight.bundle.language.java.parser.executor;

import java.util.Stack;

import org.antlr.runtime.tree.Tree;
import org.openspotlight.bundle.common.parser.SLCommonToken;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.ArtifactWithSyntaxInformation;
import org.openspotlight.graph.SLNode;

public class JavaParserExecutor {
	private final ExecutionContext context;
	private final String artifactContent;
	private final ArtifactWithSyntaxInformation artifact;
	private final String artifactName;
	private final String artifactVersion;
	private final Stack<SLNode> typeContext = new Stack<SLNode>();

	public JavaParserExecutor(final ExecutionContext context,
			final String artifactContent, final String artifactName,
			final String artifactVersion,
			final ArtifactWithSyntaxInformation artifact) {
		this.artifact = artifact;
		this.context = context;
		this.artifactContent = artifactContent;
		this.artifactName = artifactName;
		this.artifactVersion = artifactVersion;
	}

	public void createDefaultPackage() {
		// TODO Auto-generated method stub

	}

	public void createJavaTypeAnnotation(final SLCommonToken identifier292) {
		// TODO Auto-generated method stub
	}

	public void createJavaTypeClass(final SLCommonToken identifier35) {
		// TODO Auto-generated method stub
		// typeContext.peek();
	}

	public void createJavaTypeEnum(final SLCommonToken identifier54) {
		// TODO Auto-generated method stub

	}

	public void createJavaTypeInterface(final SLCommonToken identifier75) {
		// TODO Auto-generated method stub

	}

	public void createPackageNode(final Object object) {
		try {
			final Tree qualifiedName = getTree(object);

			// final JavaPackage packageName = getRootNode().addNode(
			// JavaPackage.class, qualifiedName.getText());
			// packageName.addLineReference(qualifiedName.getLine(),
			// qualifiedName
			// .getLine(), qualifiedName.getCharPositionInLine(),
			// qualifiedName.getCharPositionInLine()
			// + qualifiedName.getText().length(),
			// "PACKAGE DEFINITION", artifactName, artifactVersion);
		} catch (final Exception e) {
			Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	private SLNode getRootNode() {
		// try {
		// return context.getGraphSession().getContext("??").getRootNode();
		// } catch (final SLGraphSessionException e) {
		// TODO Auto-generated catch block
		return null;
		// }
	}

	private Tree getTree(final Object element) {
		return (Tree) element;
	}

	public void popContext() {
		typeContext.pop();
	}
}
