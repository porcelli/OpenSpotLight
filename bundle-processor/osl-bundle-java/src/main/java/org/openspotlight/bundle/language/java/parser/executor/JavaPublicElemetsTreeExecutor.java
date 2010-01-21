package org.openspotlight.bundle.language.java.parser.executor;

import java.util.IdentityHashMap;
import java.util.List;

import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeAnnotation;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

public class JavaPublicElemetsTreeExecutor {

	private final SLNode currentContext;

	private final SLNode abstractContext;
	private final SLGraphSession session;
	private final IdentityHashMap<SLNode, SLNode> concreteAbstractCache = new IdentityHashMap<SLNode, SLNode>();

	public JavaPublicElemetsTreeExecutor(final SLNode currentContext,
			final SLGraphSession session) throws Exception {
		super();
		this.currentContext = currentContext;
		abstractContext = session.createContext(JavaConstants.ABSTRACT_CONTEXT)
				.getRootNode();
		this.session = session;
		concreteAbstractCache.put(currentContext, abstractContext);
	}

	public JavaType createArrayType(final JavaType typeReturn,
			final String dimension) {
		// TODO Auto-generated method stub
		return null;
	}

	public SLNode createEnum(final SLNode parent, final String name,
			final List<JavaModifier> modifiers,
			final List<JavaTypeAnnotation> annotations,
			final List<JavaType> interfaces) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaType createExtendsParameterizedType(final JavaType typeReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public void createFieldDeclaration(final SLNode peek,
			final List<JavaModifier> modifiers29,
			final List<JavaTypeAnnotation> annotations30, final JavaType type31) {
		// TODO Auto-generated method stub

	}

	public SLNode createInterface(final SLNode peek, final String string,
			final List<JavaModifier> modifiers19,
			final List<JavaTypeAnnotation> annotations20,
			final List<JavaType> normalInterfaceDeclarationExtends21) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaTypeClass createJavaClass(final SLNode peek,
			final String string, final List<JavaModifier> modifiers7,
			final List<JavaTypeAnnotation> annotations8,
			final JavaType normalClassExtends9,
			final List<JavaType> normalClassImplements10) {
		// TODO Auto-generated method stub
		return null;
	}

	public void createMethodConstructorDeclaration(final SLNode peek,
			final String string, final List<JavaModifier> modifiers25,
			final List<VariableDeclarationDto> formalParameters26,
			final List<JavaTypeAnnotation> annotations27,
			final List<JavaType> typeBodyDeclarationThrows28) {
		// TODO Auto-generated method stub

	}

	public void createMethodDeclaration(final SLNode peek, final String string,
			final List<JavaModifier> modifiers33,
			final List<VariableDeclarationDto> formalParameters34,
			final List<JavaTypeAnnotation> annotations35,
			final JavaType type36,
			final List<JavaType> typeBodyDeclarationThrows37) {
		// TODO Auto-generated method stub

	}

	private <T extends SLNode> T createNodeOnBothContexts(
			final Class<? extends SLNode> abstractType, final Class<T> type,
			final SLNode parent, final String name) {
		try {
			final T newNode = parent.addNode(type, name);
			final SLNode cachedParent = concreteAbstractCache.get(parent);
			final SLNode newAbstractNode = cachedParent.addNode(abstractType,
					name);
			session.addLink(AbstractTypeBind.class, newAbstractNode, newNode,
					false);// FIXME is it true?
			return newNode;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	private <T extends SLNode> T createNodeOnBothContexts(
			final Class<? extends SLNode> abstractType, final Class<T> type,
			final String name) {
		return this.createNodeOnBothContexts(abstractType, type,
				currentContext, name);
	}

	private <T extends SLNode> T createNodeOnBothContexts(final Class<T> type,
			final SLNode parent, final String name) {
		return this.createNodeOnBothContexts(type, type, parent, name);
	}

	private <T extends SLNode> T createNodeOnBothContexts(final Class<T> type,
			final String name) {
		return this.createNodeOnBothContexts(type, type, name);
	}

	public JavaType createParamerizedType(final JavaType typeReturn,
			final List<JavaType> typeArguments40) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaType createPrimitiveType(final String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaType createQualifiedType(final List<JavaType> types) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaType createSuperParameterizedType(final JavaType typeReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaType createType(final String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaModifier getModifier(final String string) {
		// TODO Auto-generated method stub
		return null;
	}

	public void importDeclaration(final String string2, final String string3,
			final String string) {
		// TODO Auto-generated method stub

	}

	public JavaPackage packageDeclaration(final String string) {
		final String packageName = string == null ? JavaConstants.DEFAULT_PACKAGE
				: string;
		return this.createNodeOnBothContexts(JavaPackage.class, packageName);
	}

	public JavaTypeAnnotation resolveAnnotation(final String qualifiedName52) {
		return null;
	}

}
