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
package org.openspotlight.bundle.dap.language.java.support;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.util.Map;
import java.util.TreeMap;

import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.Constants;
import org.openspotlight.bundle.dap.language.java.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.dap.language.java.metamodel.link.DataType;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodParameterDefinition;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodReturns;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodThrows;
import org.openspotlight.bundle.dap.language.java.metamodel.link.PackageType;
import org.openspotlight.bundle.dap.language.java.metamodel.link.TypeDeclares;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaDataField;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethodConstructor;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;

/**
 * This class should be used to insert all java relationships on the OSL Graph instead of inserting nodes and links by hand. This
 * class creates all necessary links and nodes, includding the implicit ones. When receiving int parameters on access parameters
 * inside methods, the {@link Opcodes} constants from ASM should be used. First of all, all types on the current classpath should
 * be added. After
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class JavaGraphNodeSupport {

    /** The using cache. */
    private boolean                     usingCache               = true;

    /** The current context root node. */
    private final SLNode                currentContextRootNode;

    /** The abstract context root node. */
    private final SLNode                abstractContextRootNode;

    /** The session. */
    private final SLGraphSession        session;

    /** The nodes from this context. */
    private final Map<String, JavaType> nodesFromThisContext     = new TreeMap<String, JavaType>();

    /** The nodes from abstract context. */
    private final Map<String, JavaType> nodesFromAbstractContext = new TreeMap<String, JavaType>();

    /**
     * Instantiates a new java graph node support. The abstractContextRootNode will store all implicit relationships and also all
     * abstract types will be there. The concrete types will be created on currentContextRootNode. The abstractContextRootNode
     * should be used as a "global classpath". First thing to use this class in a proper way: creates all types declared in
     * current classpath on the currentContextRootNode. When adding new implicit types (on throws, or parameter, or field
     * declarations for example) this types will be created on the abstract context in case where this types doesn't exists on
     * current context.
     * 
     * @param session the session
     * @param currentContextRootNode the current context root node
     * @param abstractContextRootNode the abstract context root node
     * @throws SLGraphSessionException
     */
    public JavaGraphNodeSupport(
                                 final SLGraphSession session, final SLNode currentContextRootNode,
                                 final SLNode abstractContextRootNode ) throws SLGraphSessionException {
        checkNotNull("session", session);
        checkNotNull("currentContextRootNode", currentContextRootNode);
        checkNotNull("abstractContextRootNode", abstractContextRootNode);
        checkCondition("correctAbstractContext", Constants.ABSTRACT_CONTEXT.equals(abstractContextRootNode.getContext().getID()));
        this.session = session;
        this.currentContextRootNode = currentContextRootNode;
        this.abstractContextRootNode = abstractContextRootNode;
    }

    /**
     * Adds the extends links and also the implicit relationships.
     * 
     * @param packageName the package name
     * @param typeName the type name
     * @param superPackageName the super package name
     * @param superTypeName the super type name
     * @throws Exception the exception
     */
    public void addExtendsLinks( final String packageName,
                                 final String typeName,
                                 final String superPackageName,
                                 final String superTypeName ) throws Exception {
        final JavaType newType = this.addTypeOnAbstractContext(JavaType.class, packageName, typeName);
        final JavaType newSuperType = this.addTypeOnAbstractContext(JavaType.class, superPackageName, superTypeName);
        this.session.addLink(Extends.class, newType, newSuperType, false);
    }

    /**
     * Adds the implements links and also the implicit relationships.
     * 
     * @param packageName the package name
     * @param typeName the type name
     * @param superPackageName the super package name
     * @param superTypeName the super type name
     * @throws Exception the exception
     */
    public void addImplementsLinks( final String packageName,
                                    final String typeName,
                                    final String superPackageName,
                                    final String superTypeName ) throws Exception {
        final JavaType newType = this.addTypeOnAbstractContext(JavaType.class, packageName, typeName);
        final JavaType newSuperType = this.addTypeOnAbstractContext(JavaType.class, superPackageName, superTypeName);
        this.session.addLink(Implements.class, newType, newSuperType, false);

    }

    /**
     * Adds the throws on method and also the implicit relationships.
     * 
     * @param method the method
     * @param exceptionPackage the exception package
     * @param exceptionName the exception name
     * @throws Exception the exception
     */
    public void addThrowsOnMethod( final JavaMethod method,
                                   final String exceptionPackage,
                                   final String exceptionName ) throws Exception {
        final JavaType newExceptionType = this.addTypeOnAbstractContext(JavaType.class, exceptionPackage, exceptionName);
        this.session.addLink(MethodThrows.class, method, newExceptionType, false);

    }

    /**
     * Adds the type on abstract context to be used as a "global classphath element". The types declared on current classpath
     * should be added with method {@link #addTypeOnCurrentContext(Class, String, String, int)}
     * 
     * @param nodeType the node type
     * @param packageName the package name
     * @param nodeName the node name
     * @return the t
     * @throws Exception the exception
     */
    @SuppressWarnings( "unchecked" )
    public <T extends JavaType> T addTypeOnAbstractContext( final Class<T> nodeType,
                                                            final String packageName,
                                                            final String nodeName ) throws Exception {
        if (this.usingCache && this.nodesFromThisContext.containsKey(packageName + nodeName)) {
            return (T)this.nodesFromThisContext.get(packageName + nodeName);
        }
        if (this.usingCache && this.nodesFromAbstractContext.containsKey(packageName + nodeName)) {
            return (T)this.nodesFromAbstractContext.get(packageName + nodeName);
        }

        if (JavaTypePrimitive.class.equals(nodeType)) {
            final T newType = this.abstractContextRootNode.addNode(nodeType, nodeName);
            newType.setSimpleName(nodeName);
            newType.setCompleteName(nodeName);
            return newType;
        }
        final JavaPackage newPackage = this.abstractContextRootNode.addNode(JavaPackage.class, packageName);
        final T newType = newPackage.addNode(nodeType, nodeName);
        newType.setSimpleName(nodeName);
        newType.setCompleteName(packageName + "." + nodeName);
        this.session.addLink(PackageType.class, newPackage, newType, false);
        this.nodesFromAbstractContext.put(packageName + nodeName, newType);
        return newType;
    }

    /**
     * Adds the type on current context. This method should be used on current classpath elements only. The types declared outside
     * this classpath should be added with method {@link #addTypeOnAbstractContext(Class, String, String)}.
     * 
     * @param nodeType the node type
     * @param packageName the package name
     * @param nodeName the node name
     * @param access the access
     * @return the t
     * @throws Exception the exception
     */
    public <T extends JavaType> T addTypeOnCurrentContext( final Class<T> nodeType,
                                                           final String packageName,
                                                           final String nodeName,
                                                           final int access ) throws Exception {
        return addTypeOnCurrentContext(nodeType, packageName, nodeName, access, null);
    }

    /**
     * Adds the type on current context. This method should be used on current classpath elements only. The types declared outside
     * this classpath should be added with method {@link #addTypeOnAbstractContext(Class, String, String)}.
     * 
     * @param nodeType the node type
     * @param packageName the package name
     * @param nodeName the node name
     * @param access the access
     * @param parentType the parent type, if null will use package as parent
     * @return the t
     * @throws Exception the exception
     */
    public <T extends JavaType> T addTypeOnCurrentContext( final Class<T> nodeType,
                                                           final String packageName,
                                                           final String nodeName,
                                                           final int access,
                                                           final SLNode parentType ) throws Exception {
        if (this.usingCache && this.nodesFromThisContext.containsKey(packageName + nodeName)) {
            return (T)this.nodesFromThisContext.get(packageName + nodeName);
        }
        if (JavaTypePrimitive.class.equals(nodeType)) {
            final T newType = this.abstractContextRootNode.addNode(nodeType, nodeName);
            newType.setSimpleName(nodeName);
            newType.setCompleteName(nodeName);
            return newType;
        }
        final JavaPackage newPackage = this.currentContextRootNode.addNode(JavaPackage.class, packageName);
        T newType = null;

        if (parentType != null) {
            newType = parentType.addNode(nodeType, nodeName);
        } else {
            newType = newPackage.addNode(nodeType, nodeName);
        }

        newType.setSimpleName(nodeName);
        newType.setCompleteName(packageName + "." + nodeName);
        this.session.addLink(PackageType.class, newPackage, newType, false);
        final boolean isPublic = (access & Opcodes.ACC_PUBLIC) != 0;
        final boolean isPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        final boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        final boolean isFinal = (access & Opcodes.ACC_FINAL) != 0;
        final boolean isProtected = (access & Opcodes.ACC_PROTECTED) != 0;
        newType.setPublic(isPublic);
        newType.setPrivate(isPrivate);
        newType.setStatic(isStatic);
        newType.setFinal(isFinal);
        newType.setProtected(isProtected);

        final JavaPackage newAbstractPackage = this.abstractContextRootNode.addNode(JavaPackage.class, packageName);
        final JavaType newAbstractType = newAbstractPackage.addNode(JavaType.class, nodeName);
        this.session.addLink(PackageType.class, newPackage, newType, false);
        this.session.addLink(AbstractTypeBind.class, newAbstractType, newType, false);
        this.nodesFromThisContext.put(packageName + nodeName, newType);
        return newType;
    }

    /**
     * Creates the field and also its links.
     * 
     * @param newType the new type
     * @param fieldType the field type
     * @param fieldPackage the field package
     * @param fieldTypeName the field type name
     * @param fieldName the field name
     * @param access the access
     * @param array the array
     * @param arrayDimensions the array dimensions
     * @return the java data field
     * @throws Exception the exception
     */
    public JavaDataField createField( final JavaType newType,
                                      final Class<? extends JavaType> fieldType,
                                      final String fieldPackage,
                                      final String fieldTypeName,
                                      final String fieldName,
                                      final int access,
                                      final boolean array,
                                      final int arrayDimensions ) throws Exception {
        final JavaDataField field = newType.addNode(JavaDataField.class, fieldName);
        final JavaType fieldTypeAdded = this.addTypeOnAbstractContext(fieldType, fieldPackage, fieldTypeName);
        this.insertFieldData(field, fieldTypeAdded, access, array, arrayDimensions);
        return field;
    }

    /**
     * Creates the method and also its links. The parameters, exceptions and so on should be created with the proper methods on
     * this class.
     * 
     * @param newType the new type
     * @param methodFullName the method full name
     * @param methodName the method name
     * @param constructor the constructor
     * @param access the access
     * @return the java method
     * @throws Exception the exception
     */
    public JavaMethod createMethod( final JavaType newType,
                                    final String methodFullName,
                                    final String methodName,
                                    final boolean constructor,
                                    final int access ) throws Exception {
        JavaMethod method;
        if (constructor) {
            method = newType.addNode(JavaMethodConstructor.class, methodFullName);
        } else {
            method = newType.addNode(JavaMethodMethod.class, methodFullName);
        }
        method.setSimpleName(methodName);
        this.setMethodData(method, access);
        this.session.addLink(TypeDeclares.class, newType, method, false);
        return method;
    }

    /**
     * Creates the method parameter and its links.
     * 
     * @param method the method
     * @param parameterType the parameter type
     * @param parameterOrder the parameter order
     * @param parameterPackage the parameter package
     * @param parameterTypeName the parameter type name
     * @param array the array
     * @param arrayDimensions the array dimensions
     * @throws Exception the exception
     */
    public void createMethodParameter( final JavaMethod method,
                                       final Class<? extends JavaType> parameterType,
                                       final int parameterOrder,
                                       final String parameterPackage,
                                       final String parameterTypeName,
                                       final boolean array,
                                       final int arrayDimensions ) throws Exception {

        final JavaType methodParameterType = this.addTypeOnAbstractContext(parameterType, parameterPackage, parameterTypeName);
        final MethodParameterDefinition methodParametersTypeLink = this.session.addLink(MethodParameterDefinition.class, method,
                                                                                        methodParameterType, false);
        methodParametersTypeLink.setOrder(parameterOrder);
        methodParametersTypeLink.setArray(array);
        methodParametersTypeLink.setArrayDimension(arrayDimensions);
    }

    /**
     * Creates the method return type and its links.
     * 
     * @param method the method
     * @param returnType the return type
     * @param returnPackageName the return package name
     * @param returnTypeName the return type name
     * @param array the array
     * @param arrayDimension the array dimension
     * @throws Exception the exception
     */
    public void createMethodReturnType( final JavaMethod method,
                                        final Class<? extends JavaType> returnType,
                                        final String returnPackageName,
                                        final String returnTypeName,
                                        final boolean array,
                                        final int arrayDimension ) throws Exception {
        final JavaType methodReturnType = this.addTypeOnAbstractContext(returnType, returnPackageName, returnTypeName);
        final MethodReturns methodReturnsType = this.session.addLink(MethodReturns.class, method, methodReturnType, false);
        methodReturnsType.setArray(array);
        methodReturnsType.setArrayDimension(arrayDimension);
    }

    /**
     * Insert field data.
     * 
     * @param field the field
     * @param fieldType the field type
     * @param access the access
     * @param isArray the is array
     * @param dimension the dimension
     * @throws Exception the exception
     */
    private void insertFieldData( final JavaDataField field,
                                  final JavaType fieldType,
                                  final int access,
                                  final boolean isArray,
                                  final int dimension ) throws Exception {
        final DataType fieldTypeLink = this.session.addLink(DataType.class, field, fieldType, false);
        fieldTypeLink.setArray(isArray);
        fieldTypeLink.setArrayDimension(dimension);
        final boolean isFieldPublic = (access & Opcodes.ACC_PUBLIC) != 0;
        final boolean isFieldPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        final boolean isFieldStatic = (access & Opcodes.ACC_STATIC) != 0;
        final boolean isFieldFinal = (access & Opcodes.ACC_FINAL) != 0;
        final boolean isFieldProtected = (access & Opcodes.ACC_PROTECTED) != 0;
        final boolean isFieldTransient = (access & Opcodes.ACC_TRANSIENT) != 0;
        final boolean isFieldVolatile = (access & Opcodes.ACC_VOLATILE) != 0;
        field.setPublic(isFieldPublic);
        field.setPrivate(isFieldPrivate);
        field.setStatic(isFieldStatic);
        field.setFinal(isFieldFinal);
        field.setProtected(isFieldProtected);
        field.setTransient(isFieldTransient);
        field.setVolatile(isFieldVolatile);
    }

    /**
     * Checks if is using cache.
     * 
     * @return true, if is using cache
     */
    public boolean isUsingCache() {
        return this.usingCache;
    }

    /**
     * Sets the method data.
     * 
     * @param method the method
     * @param access the access
     */
    private void setMethodData( final JavaMethod method,
                                final int access ) {
        final boolean isMethodPublic = (access & Opcodes.ACC_PUBLIC) != 0;
        final boolean isMethodPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        final boolean isMethodStatic = (access & Opcodes.ACC_STATIC) != 0;
        final boolean isMethodFinal = (access & Opcodes.ACC_FINAL) != 0;
        final boolean isMethodProtected = (access & Opcodes.ACC_PROTECTED) != 0;
        final boolean isMethodSynchronized = (access & Opcodes.ACC_SYNCHRONIZED) != 0;
        method.setPublic(isMethodPublic);
        method.setPrivate(isMethodPrivate);
        method.setStatic(isMethodStatic);
        method.setFinal(isMethodFinal);
        method.setProtected(isMethodProtected);
        method.setSynchronized(isMethodSynchronized);
    }

    /**
     * Sets the using cache.
     * 
     * @param usingCache the new using cache
     */
    public void setUsingCache( final boolean usingCache ) {
        this.usingCache = usingCache;
    }

}
