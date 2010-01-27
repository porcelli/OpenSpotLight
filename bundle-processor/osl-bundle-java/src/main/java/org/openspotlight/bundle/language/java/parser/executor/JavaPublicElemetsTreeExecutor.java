package org.openspotlight.bundle.language.java.parser.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.language.java.metamodel.link.AnottatedBy;
import org.openspotlight.bundle.language.java.metamodel.link.ArrayOfType;
import org.openspotlight.bundle.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.language.java.metamodel.link.InnerClass;
import org.openspotlight.bundle.language.java.metamodel.link.InterfaceExtends;
import org.openspotlight.bundle.language.java.metamodel.link.MethodParameterDefinition;
import org.openspotlight.bundle.language.java.metamodel.link.MethodReturns;
import org.openspotlight.bundle.language.java.metamodel.link.MethodThrows;
import org.openspotlight.bundle.language.java.metamodel.link.PackageType;
import org.openspotlight.bundle.language.java.metamodel.link.ParameterizedTypeClass;
import org.openspotlight.bundle.language.java.metamodel.link.References;
import org.openspotlight.bundle.language.java.metamodel.link.TypeArgument;
import org.openspotlight.bundle.language.java.metamodel.link.TypeArgumentExtends;
import org.openspotlight.bundle.language.java.metamodel.link.TypeArgumentSuper;
import org.openspotlight.bundle.language.java.metamodel.link.TypeParameter;
import org.openspotlight.bundle.language.java.metamodel.node.JavaDataField;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethodConstructor;
import org.openspotlight.bundle.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeAnnotation;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeParameterized;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeParameterizedExtended;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeParameterizedSuper;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.common.concurrent.NeedsSyncronizationList;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLInvalidQueryElementException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaPublicElemetsTreeExecutor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final HashMap<String, SLNode> importedNodeCache = new HashMap<String, SLNode>();

	private JavaPackage currentPackage;

	private final String completeArtifactName;

	private final SLNode currentContext;

	private final SLNode abstractContext;

	private final SLGraphSession session;

	private final HashMap<SLNode, SLNode> concreteAbstractCache = new HashMap<SLNode, SLNode>();

	private final List<String> includedPackages = new LinkedList<String>();

	private final List<String> includedClasses = new LinkedList<String>();

	private final List<String> includedStaticClasses = new LinkedList<String>();

	private final List<String> includedStaticMethods = new LinkedList<String>();

	public JavaPublicElemetsTreeExecutor(final SLNode currentContext,
			final SLGraphSession session, final String completeArtifactName)
			throws Exception {
		super();
		this.currentContext = currentContext;
		abstractContext = session.createContext(JavaConstants.ABSTRACT_CONTEXT)
				.getRootNode();
		this.session = session;
		concreteAbstractCache.put(currentContext, abstractContext);
		this.completeArtifactName = completeArtifactName;
		if (logger.isInfoEnabled()) {
			logger.info(completeArtifactName + ": " + "creating "
					+ getClass().getSimpleName() + " with "
					+ currentContext.getContext().getID() + ":"
					+ currentContext.getName() + "/"
					+ abstractContext.getContext().getID() + ":"
					+ abstractContext.getName());
		}
	}

	public JavaTypeEnum createEnum(final SLNode parent, final String name,
			final List<JavaModifier> modifiers,
			final List<JavaTypeAnnotation> annotations,
			final List<JavaType> interfaces) {
		final JavaType enumType = findSimpleType("Enum");
		return createInnerTypeWithSateliteData(parent, name, modifiers,
				annotations, enumType, interfaces, JavaTypeEnum.class, null);
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
			final List<JavaType> normalClassImplements10, final Class<T> type,
			final List<TypeParameterDto> typeParams) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(completeArtifactName + ": " + "creating type "
						+ type.getSimpleName() + " with parent= "
						+ peek.getName() + ", name=" + string + ", modifiers="
						+ modifiers7 + ", annotations=" + annotations8
						+ ", extends=" + normalClassExtends9 + ", implements="
						+ normalClassImplements10);
			}

			final T newClass = createNodeOnBothContexts(JavaType.class, type,
					peek, string);
			final JavaType newAbstractClass = (JavaType) concreteAbstractCache
					.get(newClass);
			if (!(peek instanceof JavaPackage)) {
				newClass.setInner(true);
				newAbstractClass.setInner(true);
				session.addLink(InnerClass.class, newClass, peek, false);
				SLNode abstractParent = concreteAbstractCache.get(peek);
				if (abstractParent == null) {
					abstractParent = peek;
				}
				session.addLink(InnerClass.class, newAbstractClass,
						abstractParent, false);
			}
			final StringBuilder qualifiedName = new StringBuilder();
			SLNode parent = peek;
			do {
				qualifiedName.append(parent.getName());
				qualifiedName.append('.');
				parent = parent.getParent();
			} while (!(parent instanceof JavaPackage) && parent != null);
			if (logger.isInfoEnabled()
					&& (currentPackage == null || newClass == null)) {
				logger.info("error on adding link "
						+ PackageType.class.getSimpleName()
						+ " with "
						+ (currentPackage != null ? currentPackage.getName()
								: "null") + " and "
						+ (newClass != null ? newClass.getName() : "null"));
			}
			session.addLink(PackageType.class, currentPackage, newClass, false);
			SLNode abstractParent = concreteAbstractCache.get(currentPackage);
			if (abstractParent == null) {
				abstractParent = currentPackage;
			}
			session.addLink(PackageType.class, newAbstractClass,
					abstractParent, false);

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
			final Class<? extends SLLink> linkType = type
					.equals(JavaTypeInterface.class) ? InterfaceExtends.class
					: Extends.class;

			if (normalClassExtends9 != null) {
				session.addLink(linkType, newClass, normalClassExtends9, false);
				SLNode superType = concreteAbstractCache
						.get(normalClassExtends9);
				if (superType == null) {
					superType = normalClassExtends9;
				}
				session.addLink(linkType, newAbstractClass, superType, false);
				final SLQueryApi packagesQuery = session.createQueryApi();
				packagesQuery.select().type(JavaType.class.getName())
						.subTypes().byLink(linkType.getName()).b().selectEnd()
						.select().type(JavaPackage.class.getName()).byLink(
								PackageType.class.getName()).selectEnd()
						.keepResult().executeXTimes();
				final NeedsSyncronizationList<SLNode> nodes = packagesQuery
						.execute(Arrays.asList(superType)).getNodes();
				if (nodes.size() > 0) {
					synchronized (nodes.getLockObject()) {
						for (final SLNode node : nodes) {
							importDeclaration(currentPackage, false, true, node
									.getName());
						}
					}
				}
			} else {
				final JavaType object = findSimpleType("Object");
				session.addLink(Extends.class, newClass, object, false);
				session.addLink(Extends.class, newAbstractClass, object, false);
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
					SLQueryApi packagesQuery = session.createQueryApi();
					packagesQuery.select().type(JavaType.class.getName())
							.subTypes().byLink(Implements.class.getName()).b()
							.selectEnd().select().type(
									JavaPackage.class.getName()).byLink(
									PackageType.class.getName()).selectEnd()
							.keepResult().executeXTimes();
					NeedsSyncronizationList<SLNode> nodes = packagesQuery
							.execute(Arrays.asList((SLNode) interfaceType))
							.getNodes();
					if (nodes.size() > 0) {
						synchronized (nodes.getLockObject()) {
							for (final SLNode node : nodes) {
								importDeclaration(currentPackage, false, true,
										node.getName());
							}
						}
					}
					session.addLink(Implements.class, newClass, interfaceType,
							false);
					session.addLink(Implements.class, newAbstractClass,
							concreteAbstractCache.get(interfaceType), false);
					packagesQuery = session.createQueryApi();
					packagesQuery.select().type(JavaType.class.getName())
							.subTypes()
							.byLink(InterfaceExtends.class.getName()).b()
							.selectEnd().select().type(
									JavaPackage.class.getName()).byLink(
									PackageType.class.getName()).selectEnd()
							.keepResult().executeXTimes();
					nodes = packagesQuery.execute(
							Arrays.asList((SLNode) interfaceType)).getNodes();
					if (nodes.size() > 0) {
						synchronized (nodes.getLockObject()) {
							for (final SLNode node : nodes) {
								importDeclaration(currentPackage, false, true,
										node.getName());
							}
						}
					}
				}
			}
			if (typeParams != null) {
				for (final TypeParameterDto typeParam : typeParams) {
					final JavaTypeParameterized typeParameterized = newClass
							.addNode(JavaTypeParameterized.class, typeParam
									.getName());
					session.addLink(TypeParameter.class, newClass,
							typeParameterized, false);
					if (typeParam.getTypeParameterExtends() != null) {
						for (final JavaType ext : typeParam
								.getTypeParameterExtends()) {
							if (ext != null) {
								session.addLink(TypeArgumentExtends.class,
										typeParameterized, ext, false);
							}
						}
					}
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
			final List<JavaType> normalInterfaceDeclarationExtends21,
			final List<TypeParameterDto> typeParameters28) {
		return createInnerTypeWithSateliteData(peek, string, modifiers19,
				annotations20, null, normalInterfaceDeclarationExtends21,
				JavaTypeInterface.class, typeParameters28);
	}

	public JavaTypeClass createJavaClass(final SLNode peek,
			final String string, final List<JavaModifier> modifiers7,
			final List<JavaTypeAnnotation> annotations8,
			final JavaType normalClassExtends9,
			final List<JavaType> normalClassImplements10,
			final List<TypeParameterDto> typeParameters11) {
		return createInnerTypeWithSateliteData(peek, string, modifiers7,
				annotations8, normalClassExtends9, normalClassImplements10,
				JavaTypeClass.class, typeParameters11);
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
			final SLNode cachedParent;
			if (parent.getContext().equals(abstractContext.getContext())) {
				cachedParent = parent;
			} else {
				cachedParent = concreteAbstractCache.get(parent);
			}
			final SLNode newAbstractNode = cachedParent.addNode(abstractType,
					name);
			concreteAbstractCache.put(newNode, newAbstractNode);
			session.addLink(AbstractTypeBind.class, newAbstractNode, newNode,
					true);
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

	@SuppressWarnings("unused")
	private <T extends SLNode> T createNodeOnBothContexts(final Class<T> type,
			final SLNode parent, final String name) {
		return this.createNodeOnBothContexts(type, type, parent, name);
	}

	private <T extends SLNode> T createNodeOnBothContexts(final Class<T> type,
			final String name) {
		return this.createNodeOnBothContexts(type, type, name);
	}

	public JavaType findArrayType(final JavaType simpleOne,
			final String dimension) {
		if (simpleOne.getArray()) {
			return simpleOne;
		}
		try {
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

	@SuppressWarnings("unchecked")
	private <T extends SLNode> T findByProperty(final Class<T> type,
			final String propertyName, final String propertyValue)
			throws SLGraphSessionException, SLQueryException,
			SLInvalidQuerySyntaxException, SLInvalidQueryElementException {
		final SLQueryApi query1 = session.createQueryApi();
		query1.select().type(type.getName()).subTypes().selectEnd().where()
				.type(type.getName()).subTypes().each().property(propertyName)
				.equalsTo().value(propertyValue).typeEnd().whereEnd();
		final NeedsSyncronizationList<SLNode> result1 = query1.execute()
				.getNodes();
		if (result1.size() > 0) {
			synchronized (result1.getLockObject()) {
				for (final SLNode found : result1) {
					if (found.getContext().getRootNode()
							.equals(abstractContext)) {
						if (logger.isInfoEnabled()) {
							logger.info(completeArtifactName + ": " + "found "
									+ found.getName() + " for search on type:"
									+ type.getSimpleName() + " with "
									+ propertyName + "=" + propertyValue);
						}
						return (T) found;
					}
				}
			}
		}

		final SLQueryApi query = session.createQueryApi();
		query.select().type(type.getName()).selectEnd().where().type(
				type.getName()).each().property(propertyName).equalsTo().value(
				propertyValue).typeEnd().whereEnd();
		final NeedsSyncronizationList<SLNode> result = query.execute()
				.getNodes();
		if (result.size() > 0) {
			synchronized (result.getLockObject()) {
				for (final SLNode found : result) {
					if (found.getContext().getRootNode()
							.equals(abstractContext)) {
						if (logger.isInfoEnabled()) {
							logger.info(completeArtifactName + ": " + "found "
									+ found.getName() + " for search on type:"
									+ type.getSimpleName() + " with "
									+ propertyName + "=" + propertyValue);
						}
						return (T) found;
					} else {
						if (logger.isInfoEnabled()) {
							logger.info(completeArtifactName + ": "
									+ "ignore found type " + found.getName()
									+ " for search on type:"
									+ type.getSimpleName() + " with "
									+ propertyName + "=" + propertyValue
									+ " due to its context "
									+ found.getContext().getID());
						}

					}
				}
			}
		}

		if (logger.isInfoEnabled()) {
			logger.info(completeArtifactName + ": "
					+ "not found any node for search on type:"
					+ type.getSimpleName() + " with " + propertyName + "="
					+ propertyValue);
		}
		return null;
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

	public JavaTypeParameterizedExtended findExtendsParameterizedType(
			final JavaType simpleOne) {
		try {
			final SLNode parent = simpleOne.getParent();
			final String parameterizedName = "<? extends "
					+ simpleOne.getName() + ">";
			JavaTypeParameterizedExtended parameterizedNode = (JavaTypeParameterizedExtended) parent
					.getNode(parameterizedName);
			if (parameterizedNode == null) {
				parameterizedNode = parent.addNode(
						JavaTypeParameterizedExtended.class, parameterizedName);
				parameterizedNode.setCompleteName("<? extends "
						+ simpleOne.getCompleteName() + ">");
				parameterizedNode.setSimpleName(parameterizedName);
				session.addLink(TypeArgumentExtends.class, parameterizedNode,
						simpleOne, false);
			}
			return parameterizedNode;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public JavaTypeParameterized findParamerizedType(final JavaType simpleOne,
			final List<JavaType> typeArguments40) {
		try {
			final SLNode parent = simpleOne.getParent();
			final StringBuilder parameterizedNameBuilder = new StringBuilder();
			parameterizedNameBuilder.append('<');
			for (int i = 0, size = typeArguments40.size(); i < size; i++) {
				parameterizedNameBuilder.append('?');
				if (i - 1 != size) {
					parameterizedNameBuilder.append(',');
				}
			}
			parameterizedNameBuilder.append('>');
			final String parameterizedName = simpleOne.getName()
					+ parameterizedNameBuilder.toString();
			JavaTypeParameterized parameterizedNode = (JavaTypeParameterized) parent
					.getNode(parameterizedName);
			if (parameterizedNode == null) {
				parameterizedNode = parent.addNode(JavaTypeParameterized.class,
						parameterizedName);
				parameterizedNode.setCompleteName(simpleOne.getCompleteName()
						+ parameterizedNameBuilder.toString());
				parameterizedNode.setSimpleName(parameterizedName);

				session.addLink(ParameterizedTypeClass.class,
						parameterizedNode, simpleOne, false);
				for (final JavaType argument : typeArguments40) {
					session.addLink(TypeArgument.class, parameterizedNode,
							argument, false);
				}
			}
			return parameterizedNode;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
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
			final JavaType cached = (JavaType) importedNodeCache.get(string);
			if (cached != null) {
				return cached;
			}
			final List<String> possibleNames = new ArrayList<String>();
			possibleNames.add(string);
			if (!includedClasses.contains(string)) {
				for (final String pack : includedPackages) {
					possibleNames.add(pack + "." + string);
				}
			}
			for (final String possibleName : possibleNames) {
				final JavaType javaType = findByProperty(JavaType.class,
						"completeName", possibleName);
				if (javaType != null) {
					importedNodeCache.put(javaType.getSimpleName(), javaType);
					importedNodeCache.put(javaType.getCompleteName(), javaType);
					return javaType;
				}
			}
			throw Exceptions.logAndReturnNew(new IllegalStateException(
					completeArtifactName + ": any node was found for type "
							+ string), SLRuntimeException.class);
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public JavaTypeParameterizedSuper findSuperParameterizedType(
			final JavaType typeReturn) {
		try {
			final JavaType simpleOne = findSimpleType(typeReturn
					.getCompleteName());
			final SLNode parent = simpleOne.getParent();
			final String parameterizedName = "<? super " + simpleOne.getName()
					+ ">";
			JavaTypeParameterizedSuper parameterizedNode = (JavaTypeParameterizedSuper) parent
					.getNode(parameterizedName);
			if (parameterizedNode == null) {
				parameterizedNode = parent.addNode(
						JavaTypeParameterizedSuper.class, parameterizedName);
				parameterizedNode.setCompleteName("<? super "
						+ simpleOne.getCompleteName() + ">");
				parameterizedNode.setSimpleName(parameterizedName);
				session.addLink(TypeArgumentSuper.class, parameterizedNode,
						simpleOne, false);
			}
			return parameterizedNode;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public JavaType findVoidType() {
		return findPrimitiveType("void");
	}

	public JavaModifier getModifier(final String string) {
		return JavaModifier.getByName(string);
	}

	public void importDeclaration(final SLNode peek, final boolean isStatic,
			final boolean starred, final String string) {
		try {
			if (isStatic) {
				if (starred) {
					includedStaticClasses.add(string);
					final JavaType classNode = findByProperty(JavaType.class,
							"completeName", string);
					session.addLink(References.class, peek, classNode, false);
					importedNodeCache.put(classNode.getCompleteName(),
							classNode);
					importedNodeCache.put(classNode.getSimpleName(), classNode);
				} else {
					includedStaticMethods.add(string);
					final JavaMethod methodNode = findByProperty(
							JavaMethod.class, "qualifiedName", string);
					session.addLink(References.class, peek, methodNode, false);
					importedNodeCache.put(methodNode.getQualifiedName(),
							methodNode);
					importedNodeCache.put(methodNode.getSimpleName(),
							methodNode);
				}
			} else {
				if (starred) {
					includedPackages.add(string);
					final JavaPackage packageNode = (JavaPackage) abstractContext
							.getNode(string);
					if (logger.isInfoEnabled()
							&& (packageNode == null || peek == null)) {
						logger.info("error on adding link "
								+ References.class.getSimpleName()
								+ " with "
								+ (peek != null ? peek.getName() : "null")
								+ " and "
								+ (packageNode != null ? packageNode.getName()
										: "null"));
					}
					session.addLink(References.class, peek, packageNode, false);
				} else {
					includedClasses.add(string);
					final JavaType classNode = findByProperty(JavaType.class,
							"completeName", string);
					if (logger.isInfoEnabled()
							&& (classNode == null || peek == null)) {
						logger.info("error on adding link "
								+ References.class.getSimpleName()
								+ " with "
								+ (peek != null ? peek.getName() : "null")
								+ " and "
								+ (classNode != null ? classNode.getName()
										: "null"));
					}

					session.addLink(References.class, peek, classNode, false);
					importedNodeCache.put(classNode.getCompleteName(),
							classNode);
					importedNodeCache.put(classNode.getSimpleName(), classNode);
				}
			}
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew("parameters passed: parent:"
					+ peek + ", static:" + isStatic + ", starred:" + starred
					+ ", name:" + string, e, SLRuntimeException.class);
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
				final VariableDeclarationDto param = formalParameters34.get(i);

				completeMethodName.append(param.getType().getCompleteName());
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
			int i = 0;
			for (final VariableDeclarationDto param : formalParameters34) {
				final MethodParameterDefinition methodParametersTypeLink = session
						.addLink(MethodParameterDefinition.class, javaMethod,
								param.getType(), false);
				methodParametersTypeLink.setOrder(i++);
				int arrayDimensions = 0;
				if (param.getArrayDimensions() != null
						&& !"".equals(param.getArrayDimensions().trim())) {
					arrayDimensions = Integer.parseInt(param
							.getArrayDimensions());
				}
				methodParametersTypeLink.setArray(arrayDimensions != 0);
				methodParametersTypeLink.setArrayDimension(arrayDimensions);
			}
			javaMethod.setCompleteName(complMethodName);
			javaMethod.setSimpleName(string);
			final StringBuilder qualifiedName = new StringBuilder();
			SLNode parent = peek;
			do {
				qualifiedName.append(parent.getName());
				qualifiedName.append('.');
				parent = parent.getParent();
			} while (!(parent instanceof JavaPackage) && parent != null);
			javaMethod.setCompleteQualifiedName(qualifiedName + "."
					+ complMethodName);
			javaMethod.setQualifiedName(qualifiedName + "." + string);
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
			return javaMethod;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	public JavaPackage packageDeclaration(final String string) {
		final String packageName = string == null ? JavaConstants.DEFAULT_PACKAGE
				: string;
		if (string != null) {
			importDeclaration(currentContext, false, true, packageName);
		}
		importDeclaration(currentContext, false, true, "java.lang");
		currentPackage = this.createNodeOnBothContexts(JavaPackage.class,
				packageName);
		return currentPackage;
	}

	public JavaTypeAnnotation resolveAnnotation(final String qualifiedName52) {
		return (JavaTypeAnnotation) findSimpleType(qualifiedName52);
	}

}
