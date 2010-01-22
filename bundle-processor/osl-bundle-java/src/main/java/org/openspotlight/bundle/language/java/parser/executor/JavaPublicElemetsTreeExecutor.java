package org.openspotlight.bundle.language.java.parser.executor;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.language.java.metamodel.link.AnottatedBy;
import org.openspotlight.bundle.language.java.metamodel.link.ArrayOfType;
import org.openspotlight.bundle.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.language.java.metamodel.link.MethodReturns;
import org.openspotlight.bundle.language.java.metamodel.link.MethodThrows;
import org.openspotlight.bundle.language.java.metamodel.node.JavaDataField;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethodConstructor;
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
import org.openspotlight.graph.SLNode;

//FIXME RESOLVE ALL KINDS OF LINKS ONLY ON ABSTRACT CONTEXT, EXCEPT THE ONES OF THIS CLASSPATH
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

	public JavaTypeEnum createEnum(final SLNode parent, final String name,
			final List<JavaModifier> modifiers,
			final List<JavaTypeAnnotation> annotations,
			final List<JavaType> interfaces) {
		return createInnerTypeWithSateliteData(parent, name, modifiers,
				annotations, null, interfaces, JavaTypeEnum.class);
	}

	public void createFieldDeclaration(final SLNode peek,
			final List<JavaModifier> modifiers29,
			final List<JavaTypeAnnotation> annotations30,
			final JavaType type31, final List<VariableDeclarationDto> variables) {
		try {
			for (final VariableDeclarationDto var : variables) {
				final JavaDataField newField = peek.addNode(
						JavaDataField.class, var.getName());
				for (final JavaModifier modifier : modifiers29) {
					switch (modifier) {
					case FINAL:
						newField.setFinal(true);
						break;
					case PRIVATE:
						newField.setPrivate(true);
						break;
					case PROTECTED:
						newField.setProtected(true);
						break;
					case PUBLIC:
						newField.setPublic(true);
						break;
					case TRANSIENT:
						newField.setTransient(true);
						break;
					case VOLATILE:
						newField.setVolatile(true);
						break;
					default:
						break;
					}
				}

				if (annotations30 != null) {
					for (final JavaTypeAnnotation annotation : annotations30) {
						session.addLink(AnottatedBy.class, newField,
								annotation, false);
					}
				}

			}
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	private <T extends JavaType> T createInnerTypeWithSateliteData(
			final SLNode peek, final String string,
			final List<JavaModifier> modifiers7,
			final List<JavaTypeAnnotation> annotations8,
			final JavaType normalClassExtends9,
			final List<JavaType> normalClassImplements10, final Class<T> type) {
		try {
			final T newClass = createNodeOnBothContexts(JavaType.class, type,
					peek, string);
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
				session.addLink(Extends.class, newClass, normalClassExtends9,
						false);
				session.addLink(Extends.class, newAbstractClass,
						concreteAbstractCache.get(normalClassExtends9), false);
			}
			if (annotations8 != null) {
				for (final JavaTypeAnnotation annotation : annotations8) {
					session.addLink(AnottatedBy.class, newClass, annotation,
							false);
					session.addLink(AnottatedBy.class, newAbstractClass,
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
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public JavaTypeInterface createInterface(final SLNode peek,
			final String string, final List<JavaModifier> modifiers19,
			final List<JavaTypeAnnotation> annotations20,
			final List<JavaType> normalInterfaceDeclarationExtends21) {
		return createInnerTypeWithSateliteData(peek, string, modifiers19,
				annotations20, null, normalInterfaceDeclarationExtends21,
				JavaTypeInterface.class);
	}

	public JavaTypeClass createJavaClass(final SLNode peek,
			final String string, final List<JavaModifier> modifiers7,
			final List<JavaTypeAnnotation> annotations8,
			final JavaType normalClassExtends9,
			final List<JavaType> normalClassImplements10) {
		return createInnerTypeWithSateliteData(peek, string, modifiers7,
				annotations8, normalClassExtends9, normalClassImplements10,
				JavaTypeClass.class);
	}

	public JavaMethod createMethodConstructorDeclaration(final SLNode peek,
			final String string, final List<JavaModifier> modifiers25,
			final List<VariableDeclarationDto> formalParameters26,
			final List<JavaTypeAnnotation> annotations27,
			final List<JavaType> typeBodyDeclarationThrows28) {
		return internalCreateMethod(peek, string, modifiers25,
				formalParameters26, annotations27, null,
				typeBodyDeclarationThrows28, true);

	}

	public JavaMethod createMethodDeclaration(final SLNode peek,
			final String string, final List<JavaModifier> modifiers33,
			final List<VariableDeclarationDto> formalParameters34,
			final List<JavaTypeAnnotation> annotations35,
			final JavaType type36,
			final List<JavaType> typeBodyDeclarationThrows37) {
		return internalCreateMethod(peek, string, modifiers33,
				formalParameters34, annotations35, type36,
				typeBodyDeclarationThrows37, false);
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

	// FIXME create arrays in the same on java graph node support
	public JavaType findArrayType(final JavaType typeReturn,
			final String dimension) {
		if (typeReturn.getArray()) {
			return typeReturn;
		}
		try {
			final JavaType simpleOne = findSimpleType(typeReturn
					.getCompleteName());
			final SLNode parent = simpleOne.getParent();
			final String arrayName = simpleOne.getName() + "[]";
			JavaType arrayNode = (JavaType) parent.getNode(arrayName);
			if (arrayNode == null) {
				@SuppressWarnings("unchecked")
				final Class<? extends JavaType> sameType = simpleOne.getClass()
						.getInterfaces()[0];
				arrayNode = parent.addNode(sameType, arrayName);
				arrayNode.setArray(true);
				arrayNode.setCompleteName(simpleOne.getCompleteName() + "[]");
				arrayNode.setSimpleName(arrayName);
				session.addLink(ArrayOfType.class, arrayNode, simpleOne, false);
			}
			return arrayNode;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public JavaType findByQualifiedTypes(final List<JavaType> types) {
		final StringBuilder completeName = new StringBuilder();
		for (int i = 0, size = types.size(); i < size; i++) {
			final String name = i == 0 ? types.get(i).getCompleteName() : types
					.get(i).getSimpleName();
			completeName.append(name);
			if (i != size - 1) {
				completeName.append('.');
			}

		}
		return findSimpleType(completeName.toString());
	}

	public JavaType findExtendsParameterizedType(final JavaType typeReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaType findParamerizedType(final JavaType typeReturn,
			final List<JavaType> typeArguments40) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaType findPrimitiveType(final String string) {
		try {
			final JavaTypePrimitive primitive = abstractContext.addNode(
					JavaTypePrimitive.class, string);
			return primitive;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public JavaType findSimpleType(final String string) {

		if (JavaPrimitiveValidTypes.isPrimitive(string)) {
			return findPrimitiveType(string);
		}
		try {
			final List<String> possibleNames = new ArrayList<String>();
			possibleNames.add(string);
			if (!includedClasses.contains(string)) {
				for (final String pack : includedPackages) {
					possibleNames.add(pack + "." + string);
				}
			}

			// FIXME
			return null;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public JavaType findSuperParameterizedType(final JavaType typeReturn) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaType findVoidType() {
		return findPrimitiveType("void");
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

	private JavaMethod internalCreateMethod(final SLNode peek,
			final String string, final List<JavaModifier> modifiers33,
			final List<VariableDeclarationDto> formalParameters34,
			final List<JavaTypeAnnotation> annotations35,
			final JavaType type36,
			final List<JavaType> typeBodyDeclarationThrows37,
			final boolean constructor) {
		try {
			final StringBuilder completeMethodName = new StringBuilder();
			completeMethodName.append(string);
			completeMethodName.append('(');
			for (int i = 0, size = formalParameters34.size(); i < size; i++) {
				completeMethodName.append(formalParameters34.get(i).getType()
						.getCompleteName());
				if (i != size - 1) {
					completeMethodName.append(' ');
					completeMethodName.append(',');
				}
			}
			completeMethodName.append(')');
			final String complMethodName = completeMethodName.toString();
			final JavaMethod javaMethod;
			if (constructor) {
				javaMethod = peek.addNode(JavaMethodConstructor.class,
						complMethodName);
			} else {
				javaMethod = peek.addNode(JavaMethod.class, complMethodName);
			}
			if (annotations35 != null) {
				for (final JavaTypeAnnotation annotation : annotations35) {
					session.addLink(AnottatedBy.class, javaMethod, annotation,
							false);
				}
			}
			if (annotations35 != null) {
				for (final JavaTypeAnnotation annotation : annotations35) {
					session.addLink(AnottatedBy.class, javaMethod, annotation,
							false);
				}
			}
			if (!constructor) {
				session.addLink(MethodReturns.class, javaMethod, type36, false);
			} else {
				session.addLink(MethodReturns.class, javaMethod, peek, false);
			}
			if (typeBodyDeclarationThrows37 != null) {
				for (final JavaType annotation : typeBodyDeclarationThrows37) {
					session.addLink(MethodThrows.class, javaMethod, annotation,
							false);
				}
			}

			if (modifiers33 != null) {
				for (final JavaModifier modifier : modifiers33) {
					switch (modifier) {
					case ABSTRACT:
						javaMethod.setAbstract(true);
						break;
					case FINAL:
						javaMethod.setFinal(true);
						break;
					case NATIVE:
						javaMethod.setNative(true);
						break;
					case PRIVATE:
						javaMethod.setPrivate(true);
						break;
					case PROTECTED:
						javaMethod.setProtected(true);
						break;
					case PUBLIC:
						javaMethod.setPublic(true);
						break;
					case STATIC:
						javaMethod.setStatic(true);
						break;
					case SYNCHRONIZED:
						javaMethod.setSynchronized(true);
						break;
					}
				}

			}
			javaMethod.setSimpleName(string);
			return javaMethod;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
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
		// FIXME
		return null;
	}

}
