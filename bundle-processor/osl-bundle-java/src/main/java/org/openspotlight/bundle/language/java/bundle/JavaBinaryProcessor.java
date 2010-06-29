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
package org.openspotlight.bundle.language.java.bundle;

import org.apache.commons.io.IOUtils;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.asm.CompiledTypesExtractor;
import org.openspotlight.bundle.language.java.asm.model.*;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinition.JavaTypes;
import org.openspotlight.bundle.language.java.metamodel.node.*;
import org.openspotlight.bundle.language.java.resolver.JavaGraphNodeSupport;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.util.InvocationCacheFactory;
import org.openspotlight.common.util.Sha1;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StreamArtifact;
import org.openspotlight.federation.processing.BundleProcessorArtifactPhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JavaBinaryProcessor implements BundleProcessorArtifactPhase<StreamArtifact> {
    private static final String[] OBJ_NAMES = new String[] {"java.lang", "Object"};

    private static void addArrayField( final JavaGraphNodeSupport helper,
                                       final JavaType newType,
                                       final FieldDeclaration field,
                                       final TypeReference fieldType ) throws Exception {
        final ArrayTypeReference fieldTypeAsArray = (ArrayTypeReference)field.getType();
        final TypeReference innerFieldType = fieldTypeAsArray.getType();
        if (innerFieldType instanceof SimpleTypeReference) {
            final String[] names = getNames(innerFieldType.getFullName());
            helper.createField(newType, JavaType.class, names[0], names[1], field.getName(), field.getAccess(), true,
                               fieldTypeAsArray.getArrayDimensions());

        } else if (innerFieldType instanceof PrimitiveTypeReference) {
            helper.createField(newType, JavaTypePrimitive.class, "", getPrimitiveName(innerFieldType), field.getName(),
                               field.getAccess(), true, fieldTypeAsArray.getArrayDimensions());
        }
    }

    private static void addExtendsInfo( final JavaGraphNodeSupport helper,
                                        final TypeDefinition definition ) throws Exception {
        final String[] names;
        if (definition.getExtendsDef() != null) {
            names = getNames(definition.getExtendsDef().getFullName());
        } else {
            names = OBJ_NAMES;
        }
        helper.addExtendsLinks(definition.getPackageName(), definition.getTypeName(), names[0], names[1]);
    }

    private static void addFieldInformation( final JavaGraphNodeSupport helper,
                                             final TypeDefinition definition,
                                             final JavaType newType ) throws Exception {
        for (final FieldDeclaration field : definition.getFields()) {
            if (!field.isPrivate()) {
                final TypeReference fieldType = field.getType();
                if (fieldType instanceof SimpleTypeReference) {
                    final String[] names = getNames(fieldType.getFullName());
                    helper.createField(newType, JavaType.class, names[0], names[1], field.getName(), field.getAccess(), false, 0);
                } else if (fieldType instanceof PrimitiveTypeReference) {
                    helper.createField(newType, JavaTypePrimitive.class, "", getPrimitiveName(fieldType), field.getName(),
                                       field.getAccess(), false, 0);

                } else if (fieldType instanceof ArrayTypeReference) {
                    addArrayField(helper, newType, field, fieldType);
                }
            }
        }
    }

    private static void addImplementsInfo( final JavaGraphNodeSupport helper,
                                           final TypeDefinition definition ) throws Exception {
        for (final TypeReference iface : definition.getImplementsDef()) {
            final String[] names = getNames(iface.getFullName());
            helper.addImplementsLinks(definition.getPackageName(), definition.getTypeName(), names[0], names[1]);
        }
    }

    private static void createMethodArrayReturn( final JavaGraphNodeSupport helper,
                                                 final JavaMethod method,
                                                 final TypeReference methodReturType ) throws Exception {
        final ArrayTypeReference methodReturTypeAsArr = (ArrayTypeReference)methodReturType;
        final TypeReference innerType = methodReturTypeAsArr.getType();
        if (innerType instanceof SimpleTypeReference) {
            final String[] names = getNames(innerType.getFullName());
            helper.createMethodReturnType(method, JavaType.class, names[0], names[1], true,
                                          methodReturTypeAsArr.getArrayDimensions());
        } else if (innerType instanceof PrimitiveTypeReference) {
            helper.createMethodReturnType(method, JavaTypePrimitive.class, "", getPrimitiveName(innerType), true,
                                          methodReturTypeAsArr.getArrayDimensions());
        }
    }

    private static void createMethodReturn( final JavaGraphNodeSupport helper,
                                            final MethodDeclaration methodDecl,
                                            final JavaMethod method ) throws Exception {
        final TypeReference methodReturType = methodDecl.getReturnType();
        if (methodReturType instanceof SimpleTypeReference) {
            final String[] names = getNames(methodReturType.getFullName());
            helper.createMethodReturnType(method, JavaType.class, names[0], names[1], false, 0);
        } else if (methodReturType instanceof PrimitiveTypeReference) {
            helper.createMethodReturnType(method, JavaTypePrimitive.class, "", getPrimitiveName(methodReturType), false, 0);
        } else if (methodReturType instanceof ArrayTypeReference) {
            createMethodArrayReturn(helper, method, methodReturType);
        }
    }

    private static void createNodesFromJarData( final List<TypeDefinition> types,
                                                final JavaGraphNodeSupport helper,
                                                final SLGraphSession session ) throws Exception {
        final Map<TypeDefinition, JavaType> map = createTypes(types, helper);
        // int count = 0;
        for (final TypeDefinition definition : types) {
            if (!definition.isPrivate()) {
                // if (++count % 50 == 0) {
                // session.save();
                // }
                addExtendsInfo(helper, definition);
                addImplementsInfo(helper, definition);
                final JavaType newType = map.get(definition);
                addFieldInformation(helper, definition, newType);
                for (final MethodDeclaration methodDecl : definition.getMethods()) {
                    if (!methodDecl.isPrivate()) {
                        final JavaMethod method = helper.createMethod(newType, methodDecl.getFullName(), methodDecl.getName(),
                                                                      methodDecl.isConstructor(), methodDecl.getAccess());
                        createMethodReturn(helper, methodDecl, method);
                        createThrownExceptionsOnMethod(helper, methodDecl, method);
                        for (final MethodParameterDefinition paramDecl : methodDecl.getParameters()) {
                            final TypeReference paramType = paramDecl.getDataType();
                            if (paramType instanceof SimpleTypeReference) {
                                final String[] names = getNames(paramType.getFullName());
                                helper.createMethodParameter(method, JavaType.class, paramDecl.getPosition(), names[0], names[1],
                                                             false, 0);
                            } else if (paramType instanceof PrimitiveTypeReference) {
                                helper.createMethodParameter(method, JavaTypePrimitive.class, paramDecl.getPosition(), "",
                                                             getPrimitiveName(paramType), false, 0);

                            } else if (paramType instanceof ArrayTypeReference) {
                                final ArrayTypeReference paramTypeAsArr = (ArrayTypeReference)paramType;
                                final TypeReference innerType = paramTypeAsArr.getType();
                                if (innerType instanceof SimpleTypeReference) {
                                    final String[] names = getNames(innerType.getFullName());
                                    helper.createMethodParameter(method, JavaType.class, paramDecl.getPosition(), names[0],
                                                                 names[1], true, paramTypeAsArr.getArrayDimensions());
                                } else if (innerType instanceof PrimitiveTypeReference) {
                                    helper.createMethodParameter(method, JavaTypePrimitive.class, paramDecl.getPosition(), "",
                                                                 getPrimitiveName(innerType), true,
                                                                 paramTypeAsArr.getArrayDimensions());

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void createThrownExceptionsOnMethod( final JavaGraphNodeSupport helper,
                                                        final MethodDeclaration methodDecl,
                                                        final JavaMethod method ) throws Exception {
        for (final TypeReference ex : methodDecl.getThrownExceptions()) {
            final String[] names = getNames(ex.getFullName());
            helper.addThrowsOnMethod(method, names[0], names[1]);
        }
    }

    private static Map<TypeDefinition, JavaType> createTypes( final List<TypeDefinition> types,
                                                              final JavaGraphNodeSupport helper ) throws Exception {
        final IdentityHashMap<TypeDefinition, JavaType> map = new IdentityHashMap<TypeDefinition, JavaType>();
        for (final TypeDefinition definition : types) {
            if (!definition.isPrivate()) {
                final Class<? extends JavaType> nodeType = getNodeType(definition.getType());
                final JavaType newType = helper.addTypeOnCurrentContext(nodeType, definition.getPackageName(),
                                                                        definition.getTypeName(), definition.getAccess());
                map.put(definition, newType);
            }
        }
        return map;
    }

    public static String discoverContextName( final StreamArtifact artifact,
                                              SimplePersistCapable<STNodeEntry, STStorageSession> artifactSimplePersist )
        throws IOException, SLException {

        InputStream is = artifact.getContent().get(artifactSimplePersist);
        if (is.markSupported()) {
            is.reset();
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        IOUtils.copy(is, baos);
        final String uniqueContextName = UUID.nameUUIDFromBytes(Sha1.getSha1Signature(baos.toByteArray())).toString();
        return uniqueContextName;
    }

    private static String[] getNames( final String fullName ) {
        final String packageName = fullName.substring(0, fullName.lastIndexOf('.'));
        final String className = fullName.substring(packageName.length() + 1);
        return new String[] {packageName, className};
    }

    private static Class<? extends JavaType> getNodeType( final JavaTypes type ) {
        switch (type) {
            case ANNOTATION:
                return JavaTypeAnnotation.class;

            case CLASS:
                return JavaTypeClass.class;

            case ENUM:
                return JavaTypeEnum.class;
            case INTERFACE:
                return JavaTypeInterface.class;
            default:
                return JavaType.class;
        }
    }

    private static String getPrimitiveName( final TypeReference fieldType ) {
        return ((PrimitiveTypeReference)fieldType).getType().toString().toLowerCase();
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void beforeProcessArtifact( final StreamArtifact artifact,
                                       final CurrentProcessorContext currentContext,
                                       final ExecutionContext context ) {

    }

    public void didFinishToProcessArtifact( final StreamArtifact artifact,
                                            final LastProcessStatus status,
                                            final CurrentProcessorContext currentContext,
                                            final ExecutionContext context ) {

    }

    public Class<StreamArtifact> getArtifactType() {
        return StreamArtifact.class;
    }

    public LastProcessStatus processArtifact( final StreamArtifact artifact,
                                              final CurrentProcessorContext currentContext,
                                              final ExecutionContext context ) throws Exception {
        System.err.println(">>> " + artifact);
        if (logger.isDebugEnabled()) {
            logger.debug(" starting to process artifact " + artifact);
        }
        try {
            SimplePersistCapable<STNodeEntry, STStorageSession> artifactSimplePersist = context.getPersistentArtifactManager().getSimplePersist();
            final CompiledTypesExtractor extractor = new CompiledTypesExtractor();
            final List<TypeDefinition> types = extractor.getJavaTypes(artifact.getContent().get(artifactSimplePersist),
                                                                      artifact.getArtifactCompleteName());
            final String uniqueContextName = discoverContextName(artifact, artifactSimplePersist);
            artifact.setUniqueContextName(uniqueContextName);
            final SLContext slContext = context.getGraphSession().createContext(uniqueContextName);
            slContext.getRootNode().setProperty(String.class, "classPathArtifactPath", artifact.getArtifactCompleteName());
            logger.info("creating context " + uniqueContextName + " for artifact " + artifact.getArtifactCompleteName());
            final SLGraphSession session = context.getGraphSession();
            final SLNode currentContextRootNode = session.createContext(uniqueContextName).getRootNode();
            final SLNode abstractContextRootNode = session.createContext(JavaConstants.ABSTRACT_CONTEXT).getRootNode();
            final JavaGraphNodeSupport helper = InvocationCacheFactory.createIntoCached(JavaGraphNodeSupport.class,
                                                                                        new Class<?>[] {SLGraphSession.class,
                                                                                            SLNode.class, SLNode.class},
                                                                                        new Object[] {session,
                                                                                            currentContextRootNode,
                                                                                            abstractContextRootNode});

            createNodesFromJarData(types, helper, session);
            return LastProcessStatus.PROCESSED;
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("ending to process artifact " + artifact);
            }
        }

    }

}
