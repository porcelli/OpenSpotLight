package org.openspotlight.bundle.language.java.parser.executor;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.antlr.runtime.tree.CommonTree;
import org.openspotlight.bundle.common.parser.SLCommonTree;
import org.openspotlight.bundle.language.java.metamodel.node.JavaBlockSimple;
import org.openspotlight.bundle.language.java.parser.ExpressionDto;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaBodyElementsExecutor {

	private final String artifactName;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<SLCommonTree> importedList = new ArrayList<SLCommonTree>();

	private final Stack<SLCommonTree> elementStack = new Stack<SLCommonTree>();
	private final IdentityHashMap<SLCommonTree, SLCommonTree> extendedClasses = new IdentityHashMap<SLCommonTree, SLCommonTree>();
	private final IdentityHashMap<SLCommonTree, List<SLCommonTree>> extendedInterfaces = new IdentityHashMap<SLCommonTree, List<SLCommonTree>>();
	private final IdentityHashMap<SLCommonTree, List<SLCommonTree>> implementedInterfaces = new IdentityHashMap<SLCommonTree, List<SLCommonTree>>();
	private final IdentityHashMap<SLCommonTree, List<SLCommonTree>> variablesFromContext = new IdentityHashMap<SLCommonTree, List<SLCommonTree>>();
	private int currentBlock = 0;

	public JavaBodyElementsExecutor(final String artifactName) {
		this.artifactName = artifactName;
	}

	public void addExtends(final CommonTree peek, final CommonTree extended) {
		Assertions.checkCondition("peekInstanceOfSLTree",
				peek instanceof SLCommonTree);
		final SLCommonTree typedPeek = (SLCommonTree) peek;
		final SLNode peekNode = typedPeek.getNode();
		Assertions.checkNotNull("peekNode", peekNode);

		Assertions.checkCondition("extendedInstanceOfSLTree",
				extended instanceof SLCommonTree);
		final SLCommonTree typedExtended = (SLCommonTree) extended;
		final SLNode extendedNode = typedExtended.getNode();
		Assertions.checkNotNull("extendedNode", extendedNode);
		if (logger.isDebugEnabled()) {
			logger.debug(artifactName + ": " + "adding extend information: "
					+ peekNode.getName());
		}
		extendedClasses.put(typedPeek, typedExtended);
	}

	public void addExtends(final CommonTree peek,
			final List<CommonTree> extendeds) {
		Assertions.checkCondition("peekInstanceOfSLTree",
				peek instanceof SLCommonTree);
		final SLCommonTree typedPeek = (SLCommonTree) peek;
		final SLNode peekNode = typedPeek.getNode();
		Assertions.checkNotNull("peekNode", peekNode);
		final List<SLCommonTree> typedExtends = new ArrayList<SLCommonTree>(
				extendeds.size());
		for (final CommonTree extended : extendeds) {
			Assertions.checkCondition("extendedInstanceOfSLTree",
					extended instanceof SLCommonTree);
			final SLCommonTree typedExtended = (SLCommonTree) extended;
			final SLNode extendedNode = typedExtended.getNode();
			Assertions.checkNotNull("extendedNode", extendedNode);
			typedExtends.add(typedExtended);
		}

		extendedInterfaces.put(typedPeek, typedExtends);

	}

	public void addField(final CommonTree peek,
			final CommonTree variableDeclarator) {
		Assertions.checkCondition("peekInstanceOfSLTree",
				peek instanceof SLCommonTree);
		Assertions.checkCondition("variableDeclaratorInstanceOfSLTree",
				variableDeclarator instanceof SLCommonTree);
		final SLCommonTree typedVariableDeclarator = (SLCommonTree) variableDeclarator;
		final SLNode field = typedVariableDeclarator.getNode();
		Assertions.checkNotNull("field", field);
		final SLCommonTree typedPeek = (SLCommonTree) peek;
		List<SLCommonTree> value = variablesFromContext.get(peek);
		if (value == null) {
			value = new LinkedList<SLCommonTree>();
			variablesFromContext.put(typedPeek, value);
		}
		value.add(typedVariableDeclarator);
	}

	public void addImplements(final CommonTree peek,
			final List<CommonTree> extendeds) {
		Assertions.checkCondition("peekInstanceOfSLTree",
				peek instanceof SLCommonTree);
		final SLCommonTree typedPeek = (SLCommonTree) peek;
		final SLNode peekNode = typedPeek.getNode();
		Assertions.checkNotNull("peekNode", peekNode);
		final List<SLCommonTree> typedExtends = new ArrayList<SLCommonTree>(
				extendeds.size());
		for (final CommonTree extended : extendeds) {
			Assertions.checkCondition("extendedInstanceOfSLTree",
					extended instanceof SLCommonTree);
			final SLCommonTree typedExtended = (SLCommonTree) extended;
			final SLNode extendedNode = typedExtended.getNode();
			Assertions.checkNotNull("extendedNode", extendedNode);
			typedExtends.add(typedExtended);
		}
		implementedInterfaces.put(typedPeek, typedExtends);
	}

	public void addLocalVariableDeclaration(final CommonTree peek,
			final CommonTree commonTree, final CommonTree variableDeclarator29) {
		// session.

	}

	public void addParameterDeclaration(final CommonTree peek,
			final CommonTree typeTreeElement,
			final CommonTree parameterTreeElement) {
		final SLCommonTree typedPeek = (SLCommonTree) peek;
		final SLCommonTree typedParameterTreeElement = (SLCommonTree) parameterTreeElement;
		List<SLCommonTree> value = variablesFromContext.get(peek);
		if (value == null) {
			value = new LinkedList<SLCommonTree>();
			variablesFromContext.put(typedPeek, value);
		}
		value.add(typedParameterTreeElement);

	}

	public void addToImportedList(final CommonTree imported) {
		Assertions.checkCondition("importedInstanceOfSLTree",
				imported instanceof SLCommonTree);
		importedList.add((SLCommonTree) imported);
	}

	public ExpressionDto createArrayExpression(final CommonTree commonTree,
			final ExpressionDto arrayInitializer29) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createArrayExpression(final ExpressionDto e52) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createAssignExpression(final ExpressionDto e4,
			final ExpressionDto e5) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createBinaryExpression(final ExpressionDto e15,
			final ExpressionDto e16) {
		// TODO Auto-generated method stub
		return null;
	}

	public CommonTree createBlockAndReturnTree(final CommonTree peek,
			final CommonTree blockTreeElement, final boolean isStatic) {
		try {
			final SLCommonTree typed = (SLCommonTree) peek;
			final SLNode parent = typed.getNode();
			final String name = (isStatic ? "staticBlockDeclaration"
					: "blockDeclaration")
					+ ++currentBlock;
			final JavaBlockSimple newBlock = parent.addNode(
					JavaBlockSimple.class, name);
			final SLCommonTree typedBlockTreeElement = (SLCommonTree) blockTreeElement;
			typedBlockTreeElement.setNode(newBlock);
			return typedBlockTreeElement;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public ExpressionDto createBooleanExpression(final ExpressionDto... e1) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createBooleanExpression(final ExpressionDto e23,
			final CommonTree commonTree) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createBooleanLiteral() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createCastExpression(final ExpressionDto e46,
			final CommonTree commonTree) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createCastExpression(final ExpressionDto e47,
			final ExpressionDto e48) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createCharLiteral() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createClassInstanceExpression(final ExpressionDto e53,
			final CommonTree commonTree, final List<ExpressionDto> a2,
			final CommonTree commonTree2) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createConditionalExpression(final ExpressionDto e6,
			final ExpressionDto e7, final ExpressionDto e8) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createExpressionFromQualified(final String string,
			final ExpressionDto e54) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createFloatLiteral() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createFromArrayInitialization(
			final List<ExpressionDto> expressions) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createIntegerLiteral() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createMethodInvocation(final ExpressionDto e55,
			final String string, final List<CommonTree> ta2,
			final List<ExpressionDto> a3) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createNullLiteral() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createNumberExpression(final ExpressionDto... e39) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createPlusExpression(final ExpressionDto e28,
			final ExpressionDto e29) {
		// TODO Auto-generated method stub
		return null;
	}

	public void createStatement(final CommonTree peek,
			final CommonTree statement) {
		try {
			final SLCommonTree typed = (SLCommonTree) peek;
			final SLNode parent = typed.getNode();
			final JavaBlockSimple newBlock = parent.addNode(
					JavaBlockSimple.class, statement.getText());
			final SLCommonTree typedStatement = (SLCommonTree) statement;
			typedStatement.setNode(newBlock);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public ExpressionDto createStringLiteral() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createSuperConstructorExpression(
			final ExpressionDto e48, final List<ExpressionDto> a1) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createSuperFieldExpression(final ExpressionDto e50,
			final String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createSuperInvocationExpression(
			final ExpressionDto e49, final List<CommonTree> ta1,
			final List<ExpressionDto> a2, final String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createThisExpression(final ExpressionDto e51) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createTypeLiteralExpression(final CommonTree commonTree) {
		// TODO Auto-generated method stub
		return null;
	}

	public CommonTree peek() {
		return elementStack.peek();
	}

	public CommonTree popFromElementStack() {
		final SLCommonTree element = elementStack.peek();
		if (logger.isDebugEnabled()) {
			logger.debug(artifactName + ": " + "poping from stack "
					+ element.getText() + " " + element.getNode().getName()
					+ " " + element.getNode().getClass().getInterfaces()[0]);
		}
		currentBlock = 0;
		final SLCommonTree poped = elementStack.pop();
		extendedClasses.remove(poped);
		extendedInterfaces.remove(poped);
		implementedInterfaces.remove(poped);
		variablesFromContext.remove(poped);
		return poped;
	}

	public void pushToElementStack(final CommonTree imported) {
		Assertions.checkCondition("treeInstanceOfSLTree",
				imported instanceof SLCommonTree);
		final SLCommonTree typed = (SLCommonTree) imported;
		final SLNode node = typed.getNode();
		Assertions.checkNotNull("node", node);
		if (logger.isDebugEnabled()) {
			logger.debug(artifactName + ": " + "pushing into stack "
					+ imported.getText() + " " + node.getName()
					+ node.getClass().getInterfaces()[0].getSimpleName());
		}
		elementStack.push(typed);
	}

}
