package org.openspotlight.bundle.language.java.parser.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.antlr.runtime.tree.CommonTree;
import org.openspotlight.bundle.language.java.parser.ExpressionDto;

public class JavaBodyElementsExecutor {
	private final List<CommonTree> importedList = new ArrayList<CommonTree>();
	private final Stack<CommonTree> elementStack = new Stack<CommonTree>();

	public void addExtends(final CommonTree peek,
			final CommonTree normalClassExtends4) {
		// TODO Auto-generated method stub

	}

	public void addExtends(final CommonTree peek,
			final List<CommonTree> normalInterfaceDeclarationExtends12) {
		// TODO Auto-generated method stub

	}

	public void addField(final CommonTree peek,
			final CommonTree variableDeclarator17) {
		// TODO Auto-generated method stub

	}

	public void addImplements(final CommonTree peek,
			final List<CommonTree> normalClassImplements5) {
		// TODO Auto-generated method stub

	}

	public void addLocalVariableDeclaration(final CommonTree peek,
			final CommonTree commonTree, final CommonTree variableDeclarator27) {
		// TODO Auto-generated method stub

	}

	public void addToImportedList(final CommonTree imported) {
		importedList.add(imported);
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
			final boolean b) {
		// TODO Auto-generated method stub
		return null;
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

	public void createStatementAndPushOnStack(final CommonTree commonTree) {
		// TODO Auto-generated method stub

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
		return elementStack.pop();
	}

	public void pushToElementStack(final CommonTree imported) {
		elementStack.push(imported);
	}

}
