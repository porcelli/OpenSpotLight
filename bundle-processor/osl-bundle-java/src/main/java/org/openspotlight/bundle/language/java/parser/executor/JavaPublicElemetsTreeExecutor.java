package org.openspotlight.bundle.language.java.parser.executor;

import java.util.IdentityHashMap;
import java.util.List;

import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.language.java.metamodel.link.Anottates;
import org.openspotlight.bundle.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeAnnotation;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.graph.SLNode;

public class JavaPublicElemetsTreeExecutor {

	private final SLNode currentContext;

	private final SLNode abstractContext;
	private final SLGraphSession session;
	private final IdentityHashMap<SLNode, SLNode> concreteAbstractCache = new IdentityHashMap<SLNode, SLNode>();

	private List<String> includedPackages;
	private List<String> includedClasses;

	private List<String> includedStaticClasses;

	private List<String> includedStaticMethods;

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

	public JavaTypeEnum createEnum(final SLNode parent, final String name,
			final List<JavaModifier> modifiers,
			final List<JavaTypeAnnotation> annotations,
			final List<JavaType> interfaces) throws SLGraphSessionException,
			SLInvalidCredentialException {
		return createInnerTypeWithSateliteData(parent, name, modifiers,
				annotations, null, interfaces, JavaTypeEnum.class);
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

	private <T extends JavaType> T createInnerTypeWithSateliteData(
			final SLNode peek, final String string,
			final List<JavaModifier> modifiers7,
			final List<JavaTypeAnnotation> annotations8,
			final JavaType normalClassExtends9,
			final List<JavaType> normalClassImplements10, final Class<T> type)
			throws SLGraphSessionException, SLInvalidCredentialException {
		final T newClass = createNodeOnBothContexts(JavaType.class, type, peek,
				string);
		final JavaType newAbstractClass = (JavaType) concreteAbstractCache
				.get(newClass);
		if (!(peek instanceof JavaPackage)) {
			newClass.setInner(true);
			newAbstractClass.setInner(true);
		}
		final StringBuilder qualifiedName = new StringBuilder();
		SLNode parent = peek;
		do {
			qualifiedName.append(parent.getName());
			qualifiedName.append('.');
			parent = parent.getParent();
		} while (!(parent instanceof JavaPackage));
		newClass.setCompleteName(qualifiedName.toString());
		newClass.setSimpleName(string);
		newAbstractClass.setSimpleName(string);
		newAbstractClass.setCompleteName(qualifiedName.toString());
		if (modifiers7 != null) {
			for (final JavaModifier modifier : modifiers7) {
				switch (modifier) {
				case ABSTRACT:
					newClass.setAbstract(true);
					break;
				case FINAL:
					newClass.setFinal(true);
					break;
				case PRIVATE:
					newClass.setPrivate(true);
					break;
				case PROTECTED:
					newClass.setProtected(true);
					break;
				case PUBLIC:
					newClass.setPublic(true);
					break;
				case STATIC:
					newClass.setStatic(true);
					break;
				}
			}
		}
		if (normalClassExtends9 != null) {
			session
					.addLink(Extends.class, newClass, normalClassExtends9,
							false);
			session.addLink(Extends.class, newAbstractClass,
					concreteAbstractCache.get(normalClassExtends9), false);
		}
		if (annotations8 != null) {
			for (final JavaTypeAnnotation annotation : annotations8) {
				session.addLink(Anottates.class, newClass, annotation, false);
				session.addLink(Anottates.class, newAbstractClass,
						concreteAbstractCache.get(annotation), false);
			}
		}
		if (normalClassImplements10 != null) {
			for (final JavaType interfaceType : normalClassImplements10) {
				session.addLink(Implements.class, newClass, interfaceType,
						false);
				session.addLink(Implements.class, newAbstractClass,
						concreteAbstractCache.get(interfaceType), false);
			}
		}
		return newClass;
	}

	public JavaTypeInterface createInterface(final SLNode peek,
			final String string, final List<JavaModifier> modifiers19,
			final List<JavaTypeAnnotation> annotations20,
			final List<JavaType> normalInterfaceDeclarationExtends21)
			throws Exception {
		return createInnerTypeWithSateliteData(peek, string, modifiers19,
				annotations20, null, normalInterfaceDeclarationExtends21,
				JavaTypeInterface.class);
	}

	public JavaTypeClass createJavaClass(final SLNode peek,
			final String string, final List<JavaModifier> modifiers7,
			final List<JavaTypeAnnotation> annotations8,
			final JavaType normalClassExtends9,
			final List<JavaType> normalClassImplements10) throws Exception {
		return createInnerTypeWithSateliteData(peek, string, modifiers7,
				annotations8, normalClassExtends9, normalClassImplements10,
				JavaTypeClass.class);
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
		final JavaTypePrimitive primitive = this.createNodeOnBothContexts(
				JavaTypePrimitive.class, string);
		return primitive;
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
		return createNodeOnBothContexts(JavaType.class, string);
	}

	public JavaModifier getModifier(final String string) {
		return JavaModifier.getByName(string);
	}

	private String getQualifiedJavaClass(final String unqualifiedClassName) {
		throw new UnsupportedOperationException();
	}

	private String getUnqualifiedJavaClass(final String unqualifiedClassName) {
		throw new UnsupportedOperationException();
	}

	public void importDeclaration(final boolean isStatic,
			final boolean starred, final String string) {
		if (isStatic) {
			if (starred) {
				includedStaticClasses.add(string);
			} else {
				includedStaticMethods.add(string);
			}
		} else {
			if (starred) {
				includedPackages.add(string);
			} else {
				includedClasses.add(string);
			}
		}
	}

	public JavaPackage packageDeclaration(final String string) {
		final String packageName = string == null ? JavaConstants.DEFAULT_PACKAGE
				: string;
		if (string != null) {
			importDeclaration(false, true, packageName);
		}
		return this.createNodeOnBothContexts(JavaPackage.class, packageName);
	}

	public JavaTypeAnnotation resolveAnnotation(final String qualifiedName52) {
		//FIXME 
		return null;
	}

}
