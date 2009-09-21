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
import org.openspotlight.bundle.dap.language.java.metamodel.link.PackageType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaDataField;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

public class JavaGraphNodeSupport {

    private final SLNode                currentContextRootNode;
    private final SLNode                abstractContextRootNode;
    private final SLGraphSession        session;
    private final Map<String, JavaType> nodesFromThisContext = new TreeMap<String, JavaType>();

    public JavaGraphNodeSupport(
                                 SLGraphSession session, SLNode currentContextRootNode, SLNode abstractContextRootNode ) {
        this.session = session;
        this.currentContextRootNode = currentContextRootNode;
        this.abstractContextRootNode = abstractContextRootNode;
    }

    public void insertFieldData( JavaDataField field,
                                 JavaType fieldType,
                                 int access,
                                 boolean isArray,
                                 int dimension ) throws Exception {
        DataType fieldTypeLink = session.addLink(DataType.class, field, fieldType, false);
        fieldTypeLink.setArray(isArray);
        fieldTypeLink.setArrayDimension(dimension);
        boolean isFieldPublic = (access & Opcodes.ACC_PUBLIC) != 0;
        boolean isFieldPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        boolean isFieldStatic = (access & Opcodes.ACC_STATIC) != 0;
        boolean isFieldFinal = (access & Opcodes.ACC_FINAL) != 0;
        boolean isFieldProtected = (access & Opcodes.ACC_PROTECTED) != 0;
        boolean isFieldTransient = (access & Opcodes.ACC_TRANSIENT) != 0;
        boolean isFieldVolatile = (access & Opcodes.ACC_VOLATILE) != 0;
        field.setPublic(isFieldPublic);
        field.setPrivate(isFieldPrivate);
        field.setStatic(isFieldStatic);
        field.setFinal(isFieldFinal);
        field.setProtected(isFieldProtected);
        field.setTransient(isFieldTransient);
        field.setVolatile(isFieldVolatile);
    }

    public void setMethodData( JavaMethod method,
                               int access ) {
        boolean isMethodPublic = (access & Opcodes.ACC_PUBLIC) != 0;
        boolean isMethodPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        boolean isMethodStatic = (access & Opcodes.ACC_STATIC) != 0;
        boolean isMethodFinal = (access & Opcodes.ACC_FINAL) != 0;
        boolean isMethodProtected = (access & Opcodes.ACC_PROTECTED) != 0;
        boolean isMethodSynchronized = (access & Opcodes.ACC_SYNCHRONIZED) != 0;
        method.setPublic(isMethodPublic);
        method.setPrivate(isMethodPrivate);
        method.setStatic(isMethodStatic);
        method.setFinal(isMethodFinal);
        method.setProtected(isMethodProtected);
        method.setSynchronized(isMethodSynchronized);
    }

    public <T extends JavaType> T addBeforeTypeProcessing( Class<T> nodeType,
                                                           String packageName,
                                                           String nodeName,
                                                           int access ) throws Exception {
        if (nodesFromThisContext.containsKey(packageName + nodeName)) {
            return (T)nodesFromThisContext.get(packageName + nodeName);
        }

        JavaPackage newPackage = currentContextRootNode.addNode(JavaPackage.class, packageName);
        T newType = newPackage.addNode(nodeType, nodeName);
        session.addLink(PackageType.class, newPackage, newType, false);
        nodesFromThisContext.put(packageName + nodeName, newType);
        boolean isPublic = (access & Opcodes.ACC_PUBLIC) != 0;
        boolean isPrivate = (access & Opcodes.ACC_PRIVATE) != 0;
        boolean isStatic = (access & Opcodes.ACC_STATIC) != 0;
        boolean isFinal = (access & Opcodes.ACC_FINAL) != 0;
        boolean isProtected = (access & Opcodes.ACC_PROTECTED) != 0;
        newType.setPublic(isPublic);
        newType.setPrivate(isPrivate);
        newType.setStatic(isStatic);
        newType.setFinal(isFinal);
        newType.setProtected(isProtected);

        JavaPackage newAbstractPackage = abstractContextRootNode.addNode(JavaPackage.class, packageName);
        JavaType newAbstractType = newAbstractPackage.addNode(JavaType.class, nodeName);
        session.addLink(PackageType.class, newPackage, newType, false);
        session.addLink(AbstractTypeBind.class, newAbstractType, newType, false);

        return newType;
    }

    public <T extends JavaType> T addAfterTypeProcessing( Class<T> nodeType,
                                                          String packageName,
                                                          String nodeName ) throws Exception {
        if (nodesFromThisContext.containsKey(packageName + nodeName)) {
            return (T)nodesFromThisContext.get(packageName + nodeName);
        }
        if (JavaTypePrimitive.class.equals(nodeType)) {
            T newType = abstractContextRootNode.addNode(nodeType, nodeName);
            return newType;
        }
        JavaPackage newPackage = abstractContextRootNode.addNode(JavaPackage.class, packageName);
        T newType = newPackage.addNode(nodeType, nodeName);
        session.addLink(PackageType.class, newPackage, newType, false);
        return newType;
    }
}
