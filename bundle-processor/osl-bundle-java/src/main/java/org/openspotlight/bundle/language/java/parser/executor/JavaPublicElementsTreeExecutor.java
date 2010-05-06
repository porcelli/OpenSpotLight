/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.bundle.language.java.parser.executor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.runtime.tree.CommonTree;
import org.openspotlight.bundle.common.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.common.parser.ParsingSupport;
import org.openspotlight.bundle.common.parser.SLCommonTree;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.metamodel.link.AnottatedBy;
import org.openspotlight.bundle.language.java.metamodel.link.ArrayOfType;
import org.openspotlight.bundle.language.java.metamodel.link.DataType;
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
import org.openspotlight.bundle.language.java.metamodel.link.TypeDeclares;
import org.openspotlight.bundle.language.java.metamodel.link.TypeParameter;
import org.openspotlight.bundle.language.java.metamodel.node.JavaDataField;
import org.openspotlight.bundle.language.java.metamodel.node.JavaDataParameter;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethodConstructor;
import org.openspotlight.bundle.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.bundle.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeAnnotation;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeEnum;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeInterface;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeParameterized;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeParameterizedExtended;
import org.openspotlight.bundle.language.java.metamodel.node.JavaTypeParameterizedSuper;
import org.openspotlight.common.concurrent.NeedsSyncronizationList;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.Strings;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.SLPartition;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaPublicElementsTreeExecutor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ParsingSupport getParsingSupport() {
        return parsingSupport;
    }

    private final ParsingSupport                                parsingSupport;

    private JavaPackage                                         currentPackage;
    private final String                                        artifactVersion;
    private final String                                        completeArtifactName;
    private final int                                           currentAnonymousInnerClassName = 1;

    private final JavaExecutorSupport                           support;

    private final boolean                                       quiet                          = false;
    private SimplePersistCapable<STNodeEntry, STStorageSession> simplePersist;

    public JavaPublicElementsTreeExecutor(
                                           SimplePersistFactory factory, final JavaExecutorSupport support,
                                           final String artifactVersion, ParsingSupport parsingSupport ) throws Exception {
        super();
        this.support = support;
        this.parsingSupport = parsingSupport;
        simplePersist = factory.createSimplePersist(SLPartition.LINE_REFERENCE);
        completeArtifactName = support.completeArtifactName;
        this.artifactVersion = artifactVersion;

    }

    private void addIncludedClass( final String name ) {
        support.includedStaticClasses.add(fixIncludedName(name));
    }

    private void addIncludedStaticClass( final String name ) {
        support.includedClasses.add(fixIncludedName(name));
    }

    public void addLineReference( final CommonTree commonTree,
                                  final SLNode node ) {
        Assertions.checkNotNull("commonTree", commonTree);
        Assertions.checkNotNull("node", node);
        try {
            if (!(commonTree instanceof SLCommonTree)) {
                throw Exceptions.logAndReturn(new IllegalStateException("wrong type of tree " + commonTree.getClass()));
            }

            final SLCommonTree typed = (SLCommonTree)commonTree;
            Assertions.checkCondition("validLineStart:" + typed.getLine(), typed.getLine() > 0);
            Assertions.checkCondition("validStartCharOffset:" + typed.getStartCharOffset(), typed.getStartCharOffset() > 0);
            Assertions.checkCondition("validEndCharOffset:" + typed.getEndCharOffset(),
                                      typed.getEndCharOffset() > 0 && typed.getEndCharOffset() >= typed.getStartCharOffset());
            Assertions.checkNotEmpty("text", typed.getText());
            node.addLineReference( typed.getLine(), typed.getLine(), typed.getStartCharOffset(),
                                  typed.getEndCharOffset(), typed.getText(), completeArtifactName, artifactVersion);
            typed.setNode(node);
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public SLNode createAnnotation( final SLNode peek,
                                    final String string,
                                    final List<JavaModifier> modifiers64,
                                    final List<JavaType> annotations65 ) {
        return createInnerTypeWithSateliteData(peek, string, modifiers64, annotations65, null, null, JavaTypeAnnotation.class,
                                               null);
    }

    public JavaType createAnonymousClass( final SLNode peek,
                                          final JavaType superType ) {
        try {
            return createInnerTypeWithSateliteData(peek, peek.getName() + "$" + currentAnonymousInnerClassName, null, null,
                                                   superType, null, JavaTypeClass.class, null);
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public JavaTypeEnum createEnum( final SLNode parent,
                                    final String name,
                                    final List<JavaModifier> modifiers,
                                    final List<JavaType> annotations,
                                    final List<JavaType> interfaces ) {
        final JavaType enumType = findSimpleType("Enum");
        return createInnerTypeWithSateliteData(parent, name, modifiers, annotations, enumType, interfaces, JavaTypeEnum.class,
                                               null);
    }

    public List<SLNode> createFieldDeclaration( final SLNode peek,
                                                final List<JavaModifier> modifiers29,
                                                final List<JavaType> annotations30,
                                                final JavaType type31,
                                                final List<VariableDeclarationDto> variables ) {
        try {
            final String qualifiedParent = ((JavaType)peek).getQualifiedName() + ".";
            final SLNode concreteParent = support.findEquivalend(peek, WhatContext.CONCRETE);
            final List<SLNode> nodes = new ArrayList<SLNode>();
            for (final VariableDeclarationDto var : variables) {
                final JavaDataField newField = concreteParent.addNode(JavaDataField.class, var.getName());
                support.session.addLink(DataType.class, newField, type31, false);

                nodes.add(newField);
                support.session.addLink(TypeDeclares.class, concreteParent, newField, false);
                newField.setQualifiedName(qualifiedParent + var.getName());
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
                    for (final JavaType annotation : annotations30) {
                        support.session.addLink(AnottatedBy.class, newField, annotation, false);
                    }
                }

            }
            return nodes;
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    private <T extends JavaType> T createInnerTypeWithSateliteData( final SLNode peek,
                                                                    final String string,
                                                                    final List<JavaModifier> modifiers7,
                                                                    final List<JavaType> annotations8,
                                                                    final JavaType normalClassExtends9,
                                                                    final List<JavaType> normalClassImplements10,
                                                                    final Class<T> type,
                                                                    final List<TypeParameterDto> typeParams ) {
        try {
            if (logger.isDebugEnabled()) {
                String implementsStr = "";
                if (normalClassImplements10 != null) {
                    final StringBuilder sb = new StringBuilder();
                    for (final JavaType t : normalClassImplements10) {
                        sb.append(t.getName());
                        sb.append(",");
                    }
                    implementsStr = sb.toString();
                }
                String annotationsStr = "";
                if (annotations8 != null) {
                    final StringBuilder sb = new StringBuilder();
                    for (final JavaType t : normalClassImplements10) {
                        sb.append(t.getName());
                        sb.append(",");
                    }
                    annotationsStr = sb.toString();
                }
                logger.debug(completeArtifactName + ": " + "creating type " + type.getSimpleName() + " with parent= "
                             + peek.getName() + ", name=" + string + ", modifiers=" + modifiers7 + ", annotations="
                             + annotationsStr + ", extends=" + (normalClassExtends9 != null ? normalClassExtends9.getName() : "")
                             + ", implements=" + implementsStr);
            }
            final SLNode concreteParent = support.findEquivalend(peek, WhatContext.CONCRETE);
            final T newClass = createNodeOnBothContexts(JavaType.class, type, concreteParent, string);
            final JavaType newAbstractClass = support.findEquivalend(newClass, WhatContext.ABSTRACT);
            final StringBuilder qualifiedNameBuff = new StringBuilder();
            if (!(concreteParent instanceof JavaPackage)) {
                final JavaType typedType = (JavaType)concreteParent;
                newClass.setInner(true);
                newAbstractClass.setInner(true);
                support.session.addLink(InnerClass.class, newClass, concreteParent, false);
                SLNode abstractParent = support.findEquivalend(concreteParent, WhatContext.ABSTRACT);
                if (abstractParent == null) {
                    abstractParent = concreteParent;
                }
                support.session.addLink(InnerClass.class, newAbstractClass, abstractParent, false);
                qualifiedNameBuff.append(typedType.getQualifiedName());
            } else {
                qualifiedNameBuff.append(concreteParent.getName());
            }
            if (logger.isDebugEnabled() && (currentPackage == null || newClass == null)) {
                logger.debug("error on adding link " + PackageType.class.getSimpleName() + " with "
                             + (currentPackage != null ? currentPackage.getName() : "null") + " and "
                             + (newClass != null ? newClass.getName() : "null"));
            }
            support.session.addLink(PackageType.class, currentPackage, newClass, false);
            SLNode abstractParent = support.findEquivalend(currentPackage, WhatContext.ABSTRACT);
            if (abstractParent == null) {
                abstractParent = currentPackage;
            }
            support.session.addLink(PackageType.class, newAbstractClass, abstractParent, false);
            qualifiedNameBuff.append('.');
            qualifiedNameBuff.append(string);
            final String qualifiedName = Strings.tryToRemoveBegginingFrom(JavaConstants.DEFAULT_PACKAGE + ".",
                                                                          qualifiedNameBuff.toString());
            newClass.setQualifiedName(qualifiedName);

            newClass.setSimpleName(string);
            newAbstractClass.setSimpleName(string);
            newAbstractClass.setQualifiedName(qualifiedName);
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
            final Class<? extends SLLink> linkType = type.equals(JavaTypeInterface.class) ? InterfaceExtends.class : Extends.class;

            if (normalClassExtends9 != null) {
                support.session.addLink(linkType, newClass, normalClassExtends9, false);
                addIncludedStaticClass(normalClassExtends9.getQualifiedName());
                SLNode superType = support.findEquivalend(normalClassExtends9, WhatContext.ABSTRACT);
                if (superType == null) {
                    superType = normalClassExtends9;
                }
                support.session.addLink(linkType, newAbstractClass, superType, false);
                final SLQueryApi packagesQuery = support.session.createQueryApi();
                packagesQuery.select().type(JavaType.class.getName()).subTypes().byLink(linkType.getName()).b().selectEnd().select().type(
                                                                                                                                          JavaPackage.class.getName()).byLink(
                                                                                                                                                                              PackageType.class.getName()).selectEnd().keepResult().executeXTimes();
                final NeedsSyncronizationList<SLNode> nodes = packagesQuery.execute(Arrays.asList(superType)).getNodes();
                if (nodes.size() > 0) {
                    synchronized (nodes.getLockObject()) {
                        for (final SLNode node : nodes) {
                            importDeclaration(currentPackage, false, true, node.getName());
                        }
                    }
                }
            } else {
                final JavaType object = findSimpleType("Object");
                support.session.addLink(Extends.class, newClass, object, false);
                support.session.addLink(Extends.class, newAbstractClass, object, false);
            }
            if (annotations8 != null) {
                for (final JavaType annotation : annotations8) {
                    support.session.addLink(AnottatedBy.class, newClass, annotation, false);
                    final JavaType abstractAnnotation = support.findEquivalend(annotation, WhatContext.ABSTRACT);

                    support.session.addLink(AnottatedBy.class, newAbstractClass, abstractAnnotation, false);
                }
            }
            if (normalClassImplements10 != null) {
                for (final JavaType interfaceType : normalClassImplements10) {
                    addIncludedStaticClass(interfaceType.getQualifiedName());
                    support.session.addLink(Implements.class, newClass, interfaceType, false);
                    final JavaType abstractInterfaceType = support.findEquivalend(interfaceType, WhatContext.ABSTRACT);

                    support.session.addLink(Implements.class, newAbstractClass, abstractInterfaceType, false);
                    SLQueryApi packagesQuery = support.session.createQueryApi();
                    packagesQuery.select().type(JavaType.class.getName()).subTypes().byLink(Implements.class.getName()).b().selectEnd().select().type(
                                                                                                                                                      JavaPackage.class.getName()).byLink(
                                                                                                                                                                                          PackageType.class.getName()).selectEnd().keepResult().executeXTimes();
                    NeedsSyncronizationList<SLNode> nodes = packagesQuery.execute(Arrays.asList((SLNode)interfaceType)).getNodes();
                    if (nodes.size() > 0) {
                        synchronized (nodes.getLockObject()) {
                            for (final SLNode node : nodes) {
                                importDeclaration(currentPackage, false, true, node.getName());
                            }
                        }
                    }
                    support.session.addLink(Implements.class, newClass, interfaceType, false);
                    final JavaType abstractInterface = support.findEquivalend(interfaceType, WhatContext.ABSTRACT);
                    support.session.addLink(Implements.class, newAbstractClass, abstractInterface, false);
                    packagesQuery = support.session.createQueryApi();
                    packagesQuery.select().type(JavaType.class.getName()).subTypes().byLink(InterfaceExtends.class.getName()).b().selectEnd().select().type(
                                                                                                                                                            JavaPackage.class.getName()).byLink(
                                                                                                                                                                                                PackageType.class.getName()).selectEnd().keepResult().executeXTimes();
                    nodes = packagesQuery.execute(Arrays.asList((SLNode)interfaceType)).getNodes();
                    if (nodes.size() > 0) {
                        synchronized (nodes.getLockObject()) {
                            for (final SLNode node : nodes) {
                                importDeclaration(currentPackage, false, true, node.getName());
                            }
                        }
                    }
                }
            }
            if (typeParams != null) {
                for (final TypeParameterDto typeParam : typeParams) {
                    final JavaTypeParameterized typeParameterized = newClass.addNode(JavaTypeParameterized.class,
                                                                                     typeParam.getName());
                    support.session.addLink(TypeParameter.class, newClass, typeParameterized, false);
                    if (typeParam.getTypeParameterExtends() != null) {
                        for (final JavaType ext : typeParam.getTypeParameterExtends()) {
                            if (ext != null) {
                                support.session.addLink(TypeArgumentExtends.class, typeParameterized, ext, false);
                            }
                        }
                    }
                }
            }
            addIncludedClass(newClass.getQualifiedName());

            return newClass;
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    public JavaTypeInterface createInterface( final SLNode peek,
                                              final String string,
                                              final List<JavaModifier> modifiers19,
                                              final List<JavaType> annotations20,
                                              final List<JavaType> normalInterfaceDeclarationExtends21,
                                              final List<TypeParameterDto> typeParameters28 ) {
        return createInnerTypeWithSateliteData(peek, string, modifiers19, annotations20, null,
                                               normalInterfaceDeclarationExtends21, JavaTypeInterface.class, typeParameters28);
    }

    public JavaTypeClass createJavaClass( final SLNode peek,
                                          final String string,
                                          final List<JavaModifier> modifiers7,
                                          final List<JavaType> annotations8,
                                          final JavaType normalClassExtends9,
                                          final List<JavaType> normalClassImplements10,
                                          final List<TypeParameterDto> typeParameters11 ) {
        final JavaTypeClass javaClass = createInnerTypeWithSateliteData(peek, string, modifiers7, annotations8,
                                                                        normalClassExtends9, normalClassImplements10,
                                                                        JavaTypeClass.class, typeParameters11);
        return javaClass;
    }

    public JavaMethod createMethodConstructorDeclaration( final SLNode peek,
                                                          final String string,
                                                          final List<JavaModifier> modifiers25,
                                                          final List<VariableDeclarationDto> formalParameters26,
                                                          final List<JavaType> annotations27,
                                                          final List<JavaType> typeBodyDeclarationThrows28 ) {
        return internalCreateMethod(peek, string, modifiers25, formalParameters26, annotations27, null,
                                    typeBodyDeclarationThrows28, true);

    }

    public JavaMethod createMethodDeclaration( final SLNode peek,
                                               final String string,
                                               final List<JavaModifier> modifiers33,
                                               final List<VariableDeclarationDto> formalParameters34,
                                               final List<JavaType> annotations35,
                                               final JavaType type36,
                                               final List<JavaType> typeBodyDeclarationThrows37 ) {
        return internalCreateMethod(peek, string, modifiers33, formalParameters34, annotations35, type36,
                                    typeBodyDeclarationThrows37, false);
    }

    private <T extends SLNode> T createNodeOnBothContexts( final Class<? extends SLNode> abstractType,
                                                           final Class<T> type,
                                                           final SLNode parent,
                                                           final String name ) {
        try {
            final T newNode = parent.addNode(type, name);
            SLNode cachedParent;
            if (parent.getContext().equals(support.abstractContext.getContext())) {
                cachedParent = parent;
            } else {
                cachedParent = support.findEquivalend(parent, WhatContext.ABSTRACT);
            }
            if (cachedParent == null) {
                cachedParent = support.findEquivalend(parent, WhatContext.ABSTRACT);
            }
            final SLNode newAbstractNode = cachedParent.addNode(abstractType, name);
            support.putOnBothCaches(newNode, newAbstractNode);
            support.session.addLink(AbstractTypeBind.class, newAbstractNode, newNode, false);
            return newNode;
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    private <T extends SLNode> T createNodeOnBothContexts( final Class<? extends SLNode> abstractType,
                                                           final Class<T> type,
                                                           final String name ) {
        return this.createNodeOnBothContexts(abstractType, type, support.currentContext, name);
    }

    @SuppressWarnings( "unused" )
    private <T extends SLNode> T createNodeOnBothContexts( final Class<T> type,
                                                           final SLNode parent,
                                                           final String name ) {
        return this.createNodeOnBothContexts(type, type, parent, name);
    }

    private <T extends SLNode> T createNodeOnBothContexts( final Class<T> type,
                                                           final String name ) {
        return this.createNodeOnBothContexts(type, type, name);
    }

    public JavaType findArrayType( final JavaType simpleOne,
                                   final String dimension ) {
        if (simpleOne.getArray()) {
            return simpleOne;
        }
        try {
            final SLNode parent = simpleOne.getParent();
            final String arrayName = simpleOne.getName() + "[]";
            JavaType arrayNode = (JavaType)parent.getNode(arrayName);
            if (arrayNode == null) {
                @SuppressWarnings( "unchecked" )
                final Class<? extends JavaType> sameType = (Class<? extends JavaType>) simpleOne.getClass().getInterfaces()[0];
                arrayNode = parent.addNode(sameType, arrayName);
                arrayNode.setArray(true);
                arrayNode.setQualifiedName(simpleOne.getQualifiedName() + "[]");
                arrayNode.setSimpleName(arrayName);
                support.session.addLink(ArrayOfType.class, arrayNode, simpleOne, false);
            }
            return arrayNode;
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public JavaType findByQualifiedTypes( final List<JavaType> types ) {
        final StringBuilder qualifiedName = new StringBuilder();
        for (int i = 0, size = types.size(); i < size; i++) {
            final JavaType currentType = types.get(i);
            final String name = i == 0 ? currentType.getQualifiedName() : currentType.getSimpleName();
            qualifiedName.append(name);
            if (i != size - 1) {
                qualifiedName.append('.');
            }

        }
        return findSimpleType(qualifiedName.toString());
    }

    public JavaTypeParameterizedExtended findExtendsParameterizedType( final JavaType simpleOne ) {
        try {
            final SLNode parent = simpleOne.getParent();
            final String parameterizedName = "<? extends " + simpleOne.getName() + ">";
            JavaTypeParameterizedExtended parameterizedNode = (JavaTypeParameterizedExtended)parent.getNode(parameterizedName);
            if (parameterizedNode == null) {
                parameterizedNode = parent.addNode(JavaTypeParameterizedExtended.class, parameterizedName);
                parameterizedNode.setQualifiedName("<? extends " + simpleOne.getQualifiedName() + ">");
                parameterizedNode.setSimpleName(parameterizedName);
                support.session.addLink(TypeArgumentExtends.class, parameterizedNode, simpleOne, false);
            }
            return parameterizedNode;
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public JavaTypeParameterized findParamerizedType( final JavaType simpleOne,
                                                      final List<JavaType> typeArguments40 ) {
        try {
            final SLNode parent = simpleOne.getParent();
            final StringBuilder parameterizedNameBuilder = new StringBuilder();
            parameterizedNameBuilder.append('<');
            for (int i = 0, size = typeArguments40.size(); i < size; i++) {
                parameterizedNameBuilder.append('?');
                if (i + 1 != size) {
                    parameterizedNameBuilder.append(',');
                }
            }
            parameterizedNameBuilder.append('>');
            final String parameterizedName = simpleOne.getName() + parameterizedNameBuilder.toString();
            JavaTypeParameterized parameterizedNode = (JavaTypeParameterized)parent.getNode(parameterizedName);
            if (parameterizedNode == null) {
                parameterizedNode = parent.addNode(JavaTypeParameterized.class, parameterizedName);
                parameterizedNode.setQualifiedName(simpleOne.getQualifiedName() + parameterizedNameBuilder.toString());
                parameterizedNode.setSimpleName(parameterizedName);

                support.session.addLink(ParameterizedTypeClass.class, parameterizedNode, simpleOne, false);
                for (final JavaType argument : typeArguments40) {
                    if (argument != null) {
                        support.session.addLink(TypeArgument.class, parameterizedNode, argument, false);
                    }
                }
            }
            return parameterizedNode;
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public JavaType findPrimitiveType( final String string ) {
        return support.findPrimitiveType(string);
    }

    public JavaType findSimpleType( final String string ) {
        final JavaType foundType = support.internalFindSimpleType(string);
        Assertions.checkNotNull("foundType:" + string, foundType);
        return foundType;
    }

    public JavaTypeParameterizedSuper findSuperParameterizedType( final JavaType typeReturn ) {
        try {
            final JavaType simpleOne = findSimpleType(typeReturn.getQualifiedName());
            final SLNode parent = simpleOne.getParent();
            final String parameterizedName = "<? super " + simpleOne.getName() + ">";
            JavaTypeParameterizedSuper parameterizedNode = (JavaTypeParameterizedSuper)parent.getNode(parameterizedName);
            if (parameterizedNode == null) {
                parameterizedNode = parent.addNode(JavaTypeParameterizedSuper.class, parameterizedName);
                parameterizedNode.setQualifiedName("<? super " + simpleOne.getQualifiedName() + ">");
                parameterizedNode.setSimpleName(parameterizedName);
                support.session.addLink(TypeArgumentSuper.class, parameterizedNode, simpleOne, false);
            }
            return parameterizedNode;
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public JavaType findVoidType() {
        return findPrimitiveType("void");
    }

    private String fixIncludedName( final String name ) {
        if (name.contains("[")) {
            return name.substring(0, name.indexOf("["));
        } else if (name.contains("<")) {
            return name.substring(0, name.indexOf("<"));
        }
        return name;
    }

    public JavaModifier getModifier( final String string ) {
        return JavaModifier.getByName(string);
    }

    public SLNode importDeclaration( final SLNode peek,
                                     final boolean isStatic,
                                     final boolean starred,
                                     final String string ) {
        try {
            if (isStatic) {
                if (starred) {
                    support.includedStaticClasses.add(string);
                    JavaType classNode = support.currentContextFinder.findByProperty(JavaType.class, "qualifiedName", string);
                    if (classNode == null) {
                        classNode = support.abstractContextFinder.findByProperty(JavaType.class, "qualifiedName", string);
                    }

                    support.session.addLink(References.class, peek, classNode, false);
                    support.importedNodeCache.put(classNode.getQualifiedName(), classNode);
                    support.importedNodeCache.put(classNode.getSimpleName(), classNode);
                    return classNode;
                } else {
                    support.includedStaticMethods.add(string);
                    final JavaMethod methodNode = support.abstractContextFinder.findByProperty(JavaMethod.class, "qualifiedName",
                                                                                               string);
                    support.session.addLink(References.class, peek, methodNode, false);
                    support.importedNodeCache.put(methodNode.getQualifiedName(), methodNode);
                    support.importedNodeCache.put(methodNode.getSimpleName(), methodNode);
                    return methodNode;
                }
            } else {
                if (starred) {
                    support.includedPackages.add(string);
                    final JavaPackage packageNode = (JavaPackage)support.abstractContext.getNode(string);
                    if (logger.isDebugEnabled() && (packageNode == null || peek == null)) {
                        logger.debug("error on adding link " + References.class.getSimpleName() + " with "
                                     + (peek != null ? peek.getName() : "null") + " and "
                                     + (packageNode != null ? packageNode.getName() : "null"));
                    }
                    support.session.addLink(References.class, peek, packageNode, false);
                    return packageNode;
                } else {
                    support.includedClasses.add(string);
                    JavaType classNode = support.currentContextFinder.findByProperty(JavaType.class, "qualifiedName", string);
                    if (classNode == null) {
                        classNode = support.abstractContextFinder.findByProperty(JavaType.class, "qualifiedName", string);
                    }

                    if (logger.isDebugEnabled() && (classNode == null || peek == null)) {
                        logger.debug("error on adding link " + References.class.getSimpleName() + " with "
                                     + (peek != null ? peek.getName() : "null") + " and "
                                     + (classNode != null ? classNode.getName() : "null") + " and string to find classnode = "
                                     + string);
                    }

                    support.session.addLink(References.class, peek, classNode, false);
                    support.importedNodeCache.put(classNode.getQualifiedName(), classNode);
                    support.importedNodeCache.put(classNode.getSimpleName(), classNode);
                    return classNode;
                }
            }
        } catch (final Exception e) {
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew("parameters passed: parent:" + peek + ", static:" + isStatic + ", starred:"
                                             + starred + ", name:" + string, e, SLRuntimeException.class);
        }

    }

    private JavaMethod internalCreateMethod( final SLNode peek,
                                             final String string,
                                             final List<JavaModifier> modifiers33,
                                             final List<VariableDeclarationDto> formalParameters34,
                                             final List<JavaType> annotations35,
                                             final JavaType type36,
                                             final List<JavaType> typeBodyDeclarationThrows37,
                                             final boolean constructor ) {
        try {
            final SLNode concreteParent = support.findEquivalend(peek, WhatContext.CONCRETE);

            final StringBuilder completeMethodName = new StringBuilder();
            completeMethodName.append(string);
            completeMethodName.append('(');
            for (int i = 0, size = formalParameters34.size(); i < size; i++) {
                final VariableDeclarationDto param = formalParameters34.get(i);

                completeMethodName.append(param.getType().getQualifiedName());
                if (i != size - 1) {
                    completeMethodName.append(' ');
                    completeMethodName.append(',');
                }

            }
            completeMethodName.append(')');
            final String complMethodName = completeMethodName.toString();
            final JavaMethod javaMethod;
            if (constructor) {
                javaMethod = concreteParent.addNode(JavaMethodConstructor.class, complMethodName);
            } else {
                javaMethod = concreteParent.addNode(JavaMethodMethod.class, complMethodName);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("creating method " + complMethodName + " inside its parent " + concreteParent.getName() + " (id "
                             + concreteParent.getID() + ")");
            }
            support.session.addLink(TypeDeclares.class, concreteParent, javaMethod, false);
            javaMethod.setNumberOfParameters(formalParameters34.size());
            // final int i = 0;
            for (final VariableDeclarationDto param : formalParameters34) {
                final MethodParameterDefinition methodParametersTypeLink = support.session.addLink(
                                                                                                   MethodParameterDefinition.class,
                                                                                                   javaMethod, param.getType(),
                                                                                                   false);
                // methodParametersTypeLink.setOrder(i++);
                // int arrayDimensions = 0;
                // if (param.getArrayDimensions() != null
                // && !"".equals(param.getArrayDimensions().trim())) {
                // arrayDimensions = Integer.parseInt(param
                // .getArrayDimensions());
                // }
                // methodParametersTypeLink.setArray(arrayDimensions != 0);
                // methodParametersTypeLink.setArrayDimension(arrayDimensions);
                final JavaDataParameter parameter = javaMethod.addNode(JavaDataParameter.class, param.getName());
                param.getTreeElement().setNode(parameter);
                support.session.addLink(DataType.class, parameter, param.getType(), false);

            }
            javaMethod.setQualifiedName(complMethodName);
            javaMethod.setSimpleName(string);
            final StringBuilder qualifiedNameBuff = new StringBuilder();
            SLNode parent = concreteParent;
            do {
                qualifiedNameBuff.append(parent.getName());
                qualifiedNameBuff.append('.');
                parent = parent.getParent();
            } while (parent instanceof JavaType);
            final String qualifiedName = Strings.tryToRemoveBegginingFrom(JavaConstants.DEFAULT_PACKAGE + ".",
                                                                          qualifiedNameBuff.toString().replaceAll("[$]", "."));

            javaMethod.setCompleteQualifiedName(qualifiedName + complMethodName);

            javaMethod.setQualifiedName(qualifiedName + string);
            if (annotations35 != null) {
                for (final JavaType annotation : annotations35) {
                    support.session.addLink(AnottatedBy.class, javaMethod, annotation, false);
                }
            }
            if (annotations35 != null) {
                for (final JavaType annotation : annotations35) {
                    support.session.addLink(AnottatedBy.class, javaMethod, annotation, false);
                }
            }
            if (!constructor) {
                support.session.addLink(MethodReturns.class, javaMethod, type36, false);
            } else {
                support.session.addLink(MethodReturns.class, javaMethod, concreteParent, false);
            }
            if (typeBodyDeclarationThrows37 != null) {
                for (final JavaType annotation : typeBodyDeclarationThrows37) {
                    support.session.addLink(MethodThrows.class, javaMethod, annotation, false);
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
            if (quiet) {
                Exceptions.catchAndLog(e);
                return null;
            }
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    public JavaPackage packageDeclaration( final String string,
                                           final CommonTree tree ) {
        final String packageName = string == null ? JavaConstants.DEFAULT_PACKAGE : string;
        if (string != null) {
            importDeclaration(support.currentContext, false, true, packageName);
        }
        importDeclaration(support.currentContext, false, true, "java.lang");
        currentPackage = this.createNodeOnBothContexts(JavaPackage.class, packageName);
        addLineReference(tree, currentPackage);
        return currentPackage;
    }

    public JavaType resolveAnnotation( final String qualifiedName52 ) {
        final JavaType type = findSimpleType(qualifiedName52);
        return type;

    }
}
