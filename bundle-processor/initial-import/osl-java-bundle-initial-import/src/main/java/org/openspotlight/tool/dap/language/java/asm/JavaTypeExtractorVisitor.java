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
package org.openspotlight.tool.dap.language.java.asm;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.openspotlight.common.Pair;
import org.openspotlight.tool.dap.language.java.asm.model.Field;
import org.openspotlight.tool.dap.language.java.asm.model.JavaType;
import org.openspotlight.tool.dap.language.java.asm.model.MethodDeclaration;
import org.openspotlight.tool.dap.language.java.asm.model.SimpleTypeRef;
import org.openspotlight.tool.dap.language.java.asm.model.JavaType.JavaTypeDef;

public class JavaTypeExtractorVisitor extends AbstractTypeVisitor {

    private JavaType type = null;

    public void visit( int version,
                       int access,
                       String name,
                       String signature,
                       String superName,
                       String[] interfaces ) {
        type = new JavaType();

        Pair<String, String> packageAndTypeName = getPackageAndTypeNames(name);
        type.setPackageName(packageAndTypeName.getK1());
        type.setTypeName(packageAndTypeName.getK2());

        if ((access & Opcodes.ACC_INTERFACE) > 0) {
            type.setType(JavaTypeDef.INTERFACE);
        } else if ((access & Opcodes.ACC_ENUM) > 0) {
            type.setType(JavaTypeDef.ENUM);
        } else if ((access & Opcodes.ACC_ANNOTATION) > 0) {
            type.setType(JavaTypeDef.ANNOTATION);
        } else {
            type.setType(JavaTypeDef.CLASS);
        }
        type.setAccess(access);

        if ((access & Opcodes.ACC_PRIVATE) > 0) {
            type.setPrivate(true);
        }

        if (superName != null) {
            Pair<String, String> superPackageAndTypeName = getPackageAndTypeNames(superName);
            SimpleTypeRef superType = new SimpleTypeRef(superPackageAndTypeName.getK1(), superPackageAndTypeName.getK2());
            type.setExtendsDef(superType);
        }

        for (String interfaceName : interfaces) {
            Pair<String, String> interfacePackageAndTypeName = getPackageAndTypeNames(interfaceName);
            SimpleTypeRef interfaceType = new SimpleTypeRef(interfacePackageAndTypeName.getK1(),
                                                            interfacePackageAndTypeName.getK2());
            type.getImplementsDef().add(interfaceType);
        }
    }

    public FieldVisitor visitField( int access,
                                    String name,
                                    String desc,
                                    String signature,
                                    Object value ) {
        ASMParser asmParser = new ASMParser(desc);

        Field field = new Field();
        field.setName(name);
        field.setType(asmParser.type());
        field.setAccess(access);

        if ((access & Opcodes.ACC_PRIVATE) > 0) {
            field.setPrivate(true);
        }

        type.getFields().add(field);

        return null;
    }

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
                SimpleTypeRef exceptionType = new SimpleTypeRef(exceptionPackageAndTypeName.getK1(),
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

    private Pair<String, String> getPackageAndTypeNames( String name ) {

        String packageName = name.substring(0, name.lastIndexOf('/')).replace("/", ".");
        String className = name.substring(name.lastIndexOf('/') + 1);

        return new Pair<String, String>(packageName, className);
    }

    public JavaType getType() {
        return type;
    }
}
