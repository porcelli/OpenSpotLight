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
package org.openspotlight.bundle.dap.language.java.asm;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.asm.model.FieldDeclaration;
import org.openspotlight.bundle.dap.language.java.asm.model.MethodDeclaration;
import org.openspotlight.bundle.dap.language.java.asm.model.SimpleTypeReference;
import org.openspotlight.bundle.dap.language.java.asm.model.TypeDefinition;
import org.openspotlight.bundle.dap.language.java.asm.model.TypeDefinition.JavaTypes;
import org.openspotlight.common.Pair;

// TODO: Auto-generated Javadoc
/**
 * Visits a compiled java type (.class) and builds a {@link TypeDefinition}.
 * 
 * @author porcelli
 */
public class TypeExtractorVisitor extends AbstractTypeVisitor {

    /** The type. */
    private TypeDefinition type = null;

    /**
     * {@inheritDoc}
     */
    public void visit( int version,
                       int access,
                       String name,
                       String signature,
                       String superName,
                       String[] interfaces ) {
        type = new TypeDefinition();

        Pair<String, String> packageAndTypeName = getPackageAndTypeNames(name);
        type.setPackageName(packageAndTypeName.getK1());
        type.setTypeName(packageAndTypeName.getK2());

        if ((access & Opcodes.ACC_INTERFACE) > 0) {
            type.setType(JavaTypes.INTERFACE);
        } else if ((access & Opcodes.ACC_ENUM) > 0) {
            type.setType(JavaTypes.ENUM);
        } else if ((access & Opcodes.ACC_ANNOTATION) > 0) {
            type.setType(JavaTypes.ANNOTATION);
        } else {
            type.setType(JavaTypes.CLASS);
        }
        type.setAccess(access);

        if ((access & Opcodes.ACC_PRIVATE) > 0) {
            type.setPrivate(true);
        }

        if (superName != null) {
            Pair<String, String> superPackageAndTypeName = getPackageAndTypeNames(superName);
            SimpleTypeReference superType = new SimpleTypeReference(superPackageAndTypeName.getK1(),
                                                                    superPackageAndTypeName.getK2());
            type.setExtendsDef(superType);
        }

        for (String interfaceName : interfaces) {
            Pair<String, String> interfacePackageAndTypeName = getPackageAndTypeNames(interfaceName);
            SimpleTypeReference interfaceType = new SimpleTypeReference(interfacePackageAndTypeName.getK1(),
                                                                        interfacePackageAndTypeName.getK2());
            type.getImplementsDef().add(interfaceType);
        }
    }

    /**
     * {@inheritDoc}
     */
    public FieldVisitor visitField( int access,
                                    String name,
                                    String desc,
                                    String signature,
                                    Object value ) {
        ASMParser asmParser = new ASMParser(desc);

        FieldDeclaration field = new FieldDeclaration();
        field.setName(name);
        field.setType(asmParser.type());
        field.setAccess(access);

        if ((access & Opcodes.ACC_PRIVATE) > 0) {
            field.setPrivate(true);
        }

        type.getFields().add(field);

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public MethodVisitor visitMethod( int access,
                                      String name,
                                      String desc,
                                      String signature,
                                      String[] exceptions ) {
        if (name.equals("<clinit>")) {
            return null;
        }

        ASMParser asmParser = new ASMParser(desc);

        MethodDeclaration methodDeclaration = new MethodDeclaration();
        if (name.equals("<init>")) {
            methodDeclaration.setConstructor(true);
            methodDeclaration.setName(type.getTypeName());
        } else {
            methodDeclaration.setName(name);
        }

        methodDeclaration = asmParser.method(methodDeclaration.getName(), methodDeclaration.isConstructor());

        if (exceptions != null) {
            for (String exceptionName : exceptions) {
                Pair<String, String> exceptionPackageAndTypeName = getPackageAndTypeNames(exceptionName);
                SimpleTypeReference exceptionType = new SimpleTypeReference(exceptionPackageAndTypeName.getK1(),
                                                                            exceptionPackageAndTypeName.getK2());
                methodDeclaration.getThrownExceptions().add(exceptionType);
            }
        }

        methodDeclaration.setAccess(access);
        if ((access & Opcodes.ACC_PRIVATE) > 0) {
            methodDeclaration.setPrivate(true);
        }

        type.getMethods().add(methodDeclaration);

        return null;
    }

    /**
     * Visit inner clasz. Checks if inner class is the same of the active type, if yes the active type is an inner class.
     * 
     * @param name the name
     * @param outerName the outer name
     * @param innerName the inner name
     * @param access the access
     */
    public void visitInnerClasz( String name,
                                 String outerName,
                                 String innerName,
                                 int access ) {
        Pair<String, String> packageAndType = getPackageAndTypeNames(name);
        if ((type.getPackageName().equals(packageAndType.getK1())) && (type.getTypeName().equals(packageAndType.getK2()))) {
            type.setInnerClass(true);
        }
    }

    /**
     * Gets the package and type names (in this order).
     * 
     * @param name the name
     * @return the package and type names
     */
    private Pair<String, String> getPackageAndTypeNames( String name ) {

        String packageName = name.substring(0, name.lastIndexOf('/')).replace("/", ".");
        String className = name.substring(name.lastIndexOf('/') + 1);

        return new Pair<String, String>(packageName, className);
    }

    /**
     * Exposes the type.
     * 
     * @return the type
     */
    public TypeDefinition getType() {
        return type;
    }
}
