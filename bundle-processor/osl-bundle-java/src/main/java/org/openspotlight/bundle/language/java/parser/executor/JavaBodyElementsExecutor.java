package org.openspotlight.bundle.language.java.parser.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.antlr.runtime.tree.CommonTree;
import org.openspotlight.bundle.common.parser.SLCommonTree;
import org.openspotlight.bundle.language.java.metamodel.link.DataComparison;
import org.openspotlight.bundle.language.java.metamodel.link.DataPropagation;
import org.openspotlight.bundle.language.java.metamodel.link.DataType;
import org.openspotlight.bundle.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.language.java.metamodel.node.JavaBlockSimple;
import org.openspotlight.bundle.language.java.metamodel.node.JavaDataParameter;
import org.openspotlight.bundle.language.java.metamodel.node.JavaDataVariable;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeAnnotation;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.bundle.language.java.parser.ExpressionDto;
import org.openspotlight.bundle.language.java.parser.SingleVarDto;
import org.openspotlight.common.concurrent.NeedsSyncronizationCollection;
import org.openspotlight.common.concurrent.NeedsSyncronizationList;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQueryApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaBodyElementsExecutor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final List<SLCommonTree> importedList = new ArrayList<SLCommonTree>();

	private final Stack<SLCommonTree> elementStack = new Stack<SLCommonTree>();
	private final IdentityHashMap<SLCommonTree, SLCommonTree> extendedClasses = new IdentityHashMap<SLCommonTree, SLCommonTree>();
	private final IdentityHashMap<SLCommonTree, List<SLCommonTree>> extendedInterfaces = new IdentityHashMap<SLCommonTree, List<SLCommonTree>>();
	private final Stack<SLCommonTree> currentClass = new Stack<SLCommonTree>();
	private final IdentityHashMap<SLCommonTree, List<SLCommonTree>> implementedInterfaces = new IdentityHashMap<SLCommonTree, List<SLCommonTree>>();
	private final IdentityHashMap<SLCommonTree, List<SLCommonTree>> variablesFromContext = new IdentityHashMap<SLCommonTree, List<SLCommonTree>>();
	private int currentBlock = 0;

	private final JavaExecutorSupport support;

	private final List<ByPropertyFinder> finders;

	public JavaBodyElementsExecutor(final JavaExecutorSupport support,
			final Set<String> contextNamesInOrder) throws Exception {
		this.support = support;
		finders = new ArrayList<ByPropertyFinder>(contextNamesInOrder.size());
		for (final String s : contextNamesInOrder) {
			final SLNode contextRootNode = support.session.getContext(s)
					.getRootNode();
			finders.add(new ByPropertyFinder(support.completeArtifactName,
					support.session, contextRootNode));
		}
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
			logger.debug(support.completeArtifactName + ": "
					+ "adding extend information: " + peekNode.getName());
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
			final CommonTree type, final CommonTree variableDeclarator29) {
		try {
			final SLCommonTree typedPeek = (SLCommonTree) peek;
			final SLCommonTree typedType = (SLCommonTree) type;
			final SLCommonTree typedVariableDeclarator = (SLCommonTree) variableDeclarator29;
			final SLNode parent = typedPeek.getNode();
			final JavaType typeAsNode = (JavaType) typedType.getNode();
			final JavaDataVariable variable = parent.addNode(
					JavaDataVariable.class, variableDeclarator29.getText());
			support.session
					.addLink(DataType.class, variable, typeAsNode, false);
			typedVariableDeclarator.setNode(variable);
			List<SLCommonTree> value = variablesFromContext.get(peek);
			if (value == null) {
				value = new LinkedList<SLCommonTree>();
				variablesFromContext.put(typedPeek, value);
			}
			value.add(typedVariableDeclarator);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
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
		try {
			support.session.addLink(DataPropagation.class, e4.leaf, e5.leaf,
					false);
			return new ExpressionDto(e4.resultType, e4, e5);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public ExpressionDto createBinaryExpression(final ExpressionDto e15,
			final ExpressionDto e16) {
		try {
			support.session.addLink(DataComparison.class, e15.leaf, e16.leaf,
					false);
			return new ExpressionDto(e15.resultType, e15, e16);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
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

	public ExpressionDto createBooleanExpression(final ExpressionDto e1) {
		try {
			final JavaType booleanType = support.findPrimitiveType("boolean");
			return new ExpressionDto(booleanType, e1);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public ExpressionDto createBooleanExpression(final ExpressionDto e23,
			final CommonTree commonTree) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createBooleanExpression(final ExpressionDto e1,
			final ExpressionDto e2) {
		try {
			final JavaType booleanType = support.findPrimitiveType("boolean");

			support.session.addLink(DataComparison.class, e1.leaf, e2.leaf,
					false);
			return new ExpressionDto(booleanType, e1, e2);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public ExpressionDto createBooleanLiteral() {
		final JavaType primitiveType = support.findPrimitiveType("boolean");
		return new ExpressionDto(primitiveType);
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
		final JavaType primitiveType = support.findPrimitiveType("char");
		return new ExpressionDto(primitiveType);
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
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("trying  to find from qualified: " + string);
			}
			final ExpressionDto result = internalFind(currentClass.peek()
					.getNode(), string, e54);
			if (logger.isDebugEnabled()) {
				logger.debug(result != null ? "found from qualified " + string
						+ " leaf: " + result.leaf.getName() + " and type "
						+ result.resultType.getName()
						: "not found from qualified " + string);
			}
			Assertions.checkNotNull("result", result);
			return result;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public ExpressionDto createFloatLiteral() {
		final JavaType intType = support.findPrimitiveType("float");
		return new ExpressionDto(intType);
	}

	public ExpressionDto createFromArrayInitialization(
			final List<ExpressionDto> expressions) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createIntegerLiteral() {
		final JavaType intType = support.findPrimitiveType("int");
		return new ExpressionDto(intType);
	}

	private ExpressionDto createMemberDto(final SLNode currentMember,
			final ExpressionDto e54) throws SLGraphSessionException {
		Assertions.checkNotNull("currentMember", currentMember);
		if (currentMember instanceof JavaType) {
			return new ExpressionDto((JavaType) currentMember, currentMember,
					e54);
		} else {
			final JavaType javaType = getJavaTypeFromField(currentMember);
			return new ExpressionDto(javaType, currentMember, e54);
		}
	}

	public ExpressionDto createMethodInvocation(final ExpressionDto e55,
			final String string, final List<CommonTree> ta2,
			final List<ExpressionDto> a3) {
		// TODO Auto-generated method stub
		return null;
	}

	public ExpressionDto createNullLiteral() {
		return ExpressionDto.NULL_EXPRESSION;
	}

	public ExpressionDto createNumberExpression(final ExpressionDto... e39) {
		return new ExpressionDto(e39[0].resultType, e39);
	}

	public void createParametersFromCatch(final CommonTree peek,
			final List<SingleVarDto> formalParameters) {
		try {
			final SLCommonTree parentTree = (SLCommonTree) peek;
			final SLNode parent = parentTree.getNode();
			for (final SingleVarDto dto : formalParameters) {
				final JavaDataParameter parameter = parent.addNode(
						JavaDataParameter.class, dto.identifierTreeElement
								.getText());
				dto.identifierTreeElement.setNode(parameter);
				support.session.addLink(DataType.class, parameter,
						dto.typeTreeElement.getNode(), false);
			}
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public ExpressionDto createPlusExpression(final ExpressionDto e28,
			final ExpressionDto e29) {
		return new ExpressionDto(e28.resultType, e28, e29);
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
		final JavaType stringType = support.findOnContext("java.lang.String",
				support.abstractContextFinder);
		return new ExpressionDto(stringType);
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
		final SLCommonTree typed = (SLCommonTree) commonTree;
		return new ExpressionDto((JavaType) typed.getNode());
	}

	private SLNode findParentVisibleClasses(final String string) {
		JavaType result = support.findOnContext(string,
				support.abstractContextFinder);
		if (result != null) {
			return result;
		}

		final List<String> toTry = new ArrayList<String>();

		toTry.addAll(support.includedPackages);
		toTry.addAll(support.includedStaticClasses);
		for (final String s : toTry) {
			result = support.findOnContext(s + "." + string,
					support.abstractContextFinder);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private JavaType getJavaTypeFromField(final SLNode node)
			throws SLGraphSessionException {
		JavaType resultType;
		final NeedsSyncronizationCollection<DataType> link = support.session
				.getLinks(DataType.class, node, null);
		resultType = (JavaType) link.iterator().next().getTarget();
		return resultType;
	}

	/**
	 * 
	 select org.openspotlight.bundle.language.java.metamodel.node.JavaType.*
	 * where org.openspotlight.bundle.language.java.metamodel.node.JavaType.*
	 * property qualifiedName == "example.pack.subpack.ClassWithLotsOfStuff"
	 * keep result select
	 * org.openspotlight.bundle.language.java.metamodel.node.JavaType.* by link
	 * org.openspotlight.bundle.language.java.metamodel.link.Extends (a,b,both)
	 * keep result select
	 * org.openspotlight.bundle.language.java.metamodel.node.JavaType.* by link
	 * org.openspotlight.bundle.language.java.metamodel.link.Implements
	 * (a,b,both)
	 * 
	 * @param node
	 * @param string
	 * @param e54
	 * @return
	 * @throws Exception
	 */
	private ExpressionDto internalFind(final SLNode node, final String string,
			final ExpressionDto e54) throws Exception {
		if (string == null || string.length() == 0) {
			JavaType resultType;
			if (node instanceof JavaType) {
				resultType = (JavaType) node;
			} else {
				resultType = getJavaTypeFromField(node);
			}
			return new ExpressionDto(resultType, node, e54);
		} else if (string.equals("this")) {
			return internalFind(currentClass.peek().getNode(), null, e54);
		} else if (string.equals("super")) {
			final SLNode superNode = extendedClasses.get(currentClass.peek())
					.getNode();
			return internalFind(superNode, null, e54);
		} else if (!string.contains(".")) {
			SLNode currentMember = lookForLocalVariable(string);
			if (currentMember == null) {
				final JavaType currentJavaType = (JavaType) currentClass.peek()
						.getNode();

				currentMember = lookForMember(currentJavaType, string);
			}
			if (currentMember != null) {
				return createMemberDto(currentMember, e54);
			}

		} else if (string.contains(".")) {
			final SLNode clazz = findParentVisibleClasses(string);
			if (clazz != null) {
				return createMemberDto(clazz, e54);
			}

			final String toWork = removeStartIfContains(string, "this", "super");
			if (!toWork.contains(".")) {
				return internalFind(node, toWork, e54);
			}
			final String[] splitted = toWork.contains(".") ? toWork
					.split("[.]") : new String[] { toWork };
			SLNode currentNode = lookForLocalVariable(splitted[0]);
			if (currentNode == null) {
				final JavaType currentJavaType = (JavaType) currentClass.peek()
						.getNode();

				currentNode = lookForMember(currentJavaType, string);
			}
			for (int i = 1, size = splitted.length; i < size; i++) {
				if (!(currentNode instanceof JavaType)) {
					currentNode = getJavaTypeFromField(currentNode);
				}
				currentNode = lookForMember((JavaType) currentNode, string);
			}
			return createMemberDto(currentNode, e54);
		}
		return null;
	}

	private SLNode lookForLocalVariable(final String string) throws Exception {
		if (logger.isDebugEnabled()) {
			logger.debug("looking for visible var " + string);
		}
		final Collection<List<SLCommonTree>> currentVariablesList = variablesFromContext
				.values();
		for (final List<SLCommonTree> currentVariables : currentVariablesList) {
			for (final SLCommonTree entry : currentVariables) {
				if (string.equals(entry.getText().trim())) {
					final SLNode node = entry.getNode();
					if (logger.isDebugEnabled()) {
						logger
								.debug("found visible var "
										+ string
										+ " with node "
										+ (node != null ? node.getName()
												: "null node"));
					}
					return node;
				}

			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("not found visible var " + string);
		}
		return null;

	}

	private SLNode lookForMember(final JavaType currentJavaType,
			final String string) throws Exception {
		final SLQueryApi query = support.session.createQueryApi();
		query.select().type(JavaType.class.getName()).subTypes().selectEnd()
				.where().type(JavaType.class.getName()).subTypes().each()
				.property("qualifiedName").equalsTo().value(
						currentJavaType.getQualifiedName()).typeEnd()
				.whereEnd().keepResult().select()
				.type(JavaType.class.getName()).subTypes().comma().byLink(
						Extends.class.getName()).any().selectEnd().keepResult()
				.select().type(JavaType.class.getName()).subTypes().comma()
				.byLink(Implements.class.getName()).any().selectEnd();
		final NeedsSyncronizationList<SLNode> nodes = query.execute()
				.getNodes();
		for (final SLNode currentNode : nodes) {
			final SLNode currentMember = currentNode.getNode(string);
			if (currentMember != null) {
				return currentMember;
			}
		}
		return null;

	}

	public CommonTree peek() {
		return elementStack.peek();
	}

	public CommonTree popFromElementStack() {
		final SLCommonTree element = elementStack.peek();
		if (logger.isDebugEnabled()) {
			final SLNode node = element.getNode();
			logger.debug(support.completeArtifactName + ": "
					+ "poping from stack text=" + element.getText()
					+ " nodeName=" + node.getName() + " nodeClass="
					+ node.getClass().getInterfaces()[0].getSimpleName());
		}
		currentBlock = 0;
		final SLCommonTree poped = elementStack.pop();
		extendedClasses.remove(poped);
		if (poped != null && !currentClass.isEmpty()
				&& poped.equals(currentClass.peek())) {
			currentClass.pop();
		}
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
		if (node instanceof JavaTypeClass || node instanceof JavaTypeAnnotation
				|| node instanceof JavaTypeEnum) {
			currentClass.push(typed);
		}
		Assertions.checkNotNull("node", node);
		if (logger.isDebugEnabled()) {
			logger.debug(support.completeArtifactName + ": "
					+ "pushing into stack text=" + imported.getText()
					+ " nodeName=" + node.getName() + " nodeClass="
					+ node.getClass().getInterfaces()[0].getSimpleName());
		}
		elementStack.push(typed);
	}

	private String removeStartIfContains(final String string,
			final String... whats) {
		String toWork = null;
		for (final String what : whats) {
			if (string.contains(what)) {
				toWork = string.substring(string.indexOf(what) + 1, string
						.length());
				if (toWork.length() == 0) {
					return what;
				}
			} else {
				toWork = string;
			}
		}
		return toWork;
	}

}
