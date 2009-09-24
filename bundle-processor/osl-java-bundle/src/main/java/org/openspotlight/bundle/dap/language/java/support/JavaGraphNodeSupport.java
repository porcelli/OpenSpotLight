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

import java.util.Map;
import java.util.TreeMap;

import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.metamodel.link.AbstractTypeBind;
import org.openspotlight.bundle.dap.language.java.metamodel.link.DataType;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Implements;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodReturns;
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
import org.openspotlight.graph.SLNode;

public class JavaGraphNodeSupport {
    private boolean                     usingCache           = true;
    private final SLNode                currentContextRootNode;
    private final SLNode                abstractContextRootNode;
    private final SLGraphSession        session;
    private final Map<String, JavaType> nodesFromThisContext = new TreeMap<String, JavaType>();

    public JavaGraphNodeSupport(
                                 final SLGraphSession session, final SLNode currentContextRootNode,
                                 final SLNode abstractContextRootNode ) {
        this.session = session;
        this.currentContextRootNode = currentContextRootNode;
        this.abstractContextRootNode = abstractContextRootNode;
    }

    public void addExtendsLinks( final String packageName,
                                 final String typeName,
                                 final String superPackageName,
                                 final String superTypeName ) throws Exception {
        final JavaType newType = this.addTypeOnAbstractContext(JavaType.class, packageName, typeName);
        final JavaType newSuperType = this.addTypeOnAbstractContext(JavaType.class, superPackageName, superTypeName);
        this.session.addLink(Extends.class, newType, newSuperType, false);
    }

    public void addImplementsLinks( final String packageName,
                                    final String typeName,
                                    final String superPackageName,
                                    final String superTypeName ) throws Exception {
        final JavaType newType = this.addTypeOnAbstractContext(JavaType.class, packageName, typeName);
        final JavaType newSuperType = this.addTypeOnAbstractContext(JavaType.class, superPackageName, superTypeName);
        this.session.addLink(Implements.class, newType, newSuperType, false);

    }

    public <T extends JavaType> T addTypeOnAbstractContext( final Class<T> nodeType,
                                                            final String packageName,
                                                            final String nodeName ) throws Exception {
        if (this.usingCache && this.nodesFromThisContext.containsKey(packageName + nodeName)) {
            return (T)this.nodesFromThisContext.get(packageName + nodeName);
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
        return newType;
    }

    public <T extends JavaType> T addTypeOnCurrentContext( final Class<T> nodeType,
                                                           final String packageName,
                                                           final String nodeName,
                                                           final int access ) throws Exception {
        if (JavaTypePrimitive.class.equals(nodeType)) {
            final T newType = this.abstractContextRootNode.addNode(nodeType, nodeName);
            newType.setSimpleName(nodeName);
            newType.setCompleteName(nodeName);
            return newType;
        }
        final JavaPackage newPackage = this.currentContextRootNode.addNode(JavaPackage.class, packageName);
        final T newType = newPackage.addNode(nodeType, nodeName);
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

        return newType;
    }

    public JavaMethod createMethod( final JavaType newType,
                                    final String methodFullName,
                                    final String methodName,
                                    final boolean constructor,
                                    final int access ) throws Exception {
        JavaMethod method;
        if (constructor) {
            method = newType.addNode(JavaMethodConstructor.class, "${method.fullName}");
        } else {
            method = newType.addNode(JavaMethodMethod.class, "${method.fullName}");
        }
        method.setSimpleName(methodName);
        this.setMethodData(method, access);
        this.session.addLink(TypeDeclares.class, newType, method, false);
        return method;
    }

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

    public void insertFieldData( final JavaDataField field,
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

    public boolean isUsingCache() {
        return this.usingCache;
    }

    public void setMethodData( final JavaMethod method,
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

    public void setUsingCache( final boolean usingCache ) {
        this.usingCache = usingCache;
    }
}
