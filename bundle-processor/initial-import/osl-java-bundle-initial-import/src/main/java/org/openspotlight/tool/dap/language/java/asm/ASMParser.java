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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.antlr.runtime.ANTLRStringStream;
import org.openspotlight.common.Pair;
import org.openspotlight.tool.dap.language.java.asm.model.ArrayTypeReference;
import org.openspotlight.tool.dap.language.java.asm.model.MethodDeclaration;
import org.openspotlight.tool.dap.language.java.asm.model.MethodParameter;
import org.openspotlight.tool.dap.language.java.asm.model.ParameterizedTypeReference;
import org.openspotlight.tool.dap.language.java.asm.model.PrimitiveTypeReference;
import org.openspotlight.tool.dap.language.java.asm.model.QualifiedTypeReference;
import org.openspotlight.tool.dap.language.java.asm.model.SimpleTypeReference;
import org.openspotlight.tool.dap.language.java.asm.model.TypeParameter;
import org.openspotlight.tool.dap.language.java.asm.model.TypeReference;
import org.openspotlight.tool.dap.language.java.asm.model.WildcardTypeReference;
import org.openspotlight.tool.dap.language.java.asm.model.PrimitiveTypeReference.PrimitiveType;

public class ASMParser {
    private ANTLRStringStream input;

    public ASMParser(
                            String type ) {
        input = new ANTLRStringStream(type);
    }

    public List<TypeReference> types() {
        List<TypeReference> typeList = new LinkedList<TypeReference>();
        while (true) {
            if (input.LA(1) == ANTLRStringStream.EOF) {
                break;
            }
            typeList.add(mTYPE(true));
        }
        return typeList;
    }

    public TypeReference type() {
        return mTYPE(true);
    }

    public MethodDeclaration method( String name,
                                     boolean isConstructor ) {
        return mMETHOD(name, isConstructor);
    }

    private MethodDeclaration mMETHOD( String name,
                                       boolean isConstructor ) {
        MethodDeclaration activeMethod = new MethodDeclaration();
        try {
            activeMethod.setName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        activeMethod.setConstructor(isConstructor);

        if (input.LA(1) == '<') {
            // Generic
            activeMethod.getTypeParameters().add(mTYPE_PARAM());
        }

        input.consume();
        int position = 0;
        while (true) {
            if (input.LA(1) == ')') {
                break;
            }
            // Parameters
            MethodParameter parameter = new MethodParameter();
            parameter.setDataType(mTYPE(true));
            parameter.setPosition(position);
            activeMethod.getParameters().add(parameter);
            position++;
        }
        input.consume();

        // Return
        activeMethod.setReturnType(mTYPE(true));

        while (true) {
            if (input.LA(1) == '^') {
                activeMethod.getThrownExceptions().add(mEXCEPTION());
            } else {
                break;
            }
        }

        return activeMethod;
    }

    private TypeReference mEXCEPTION() {
        input.consume();
        return mTYPE(true);
    }

    private TypeParameter mTYPE_PARAM() {
        TypeParameter typeParameter = new TypeParameter();
        input.consume();
        typeParameter.setName(mID().poll());
        input.consume();
        if (input.LA(1) != '>') {
            typeParameter.getTypeBounds().add(mTYPE(true));
        }
        while (true) {
            if (input.LA(1) != ':') {
                break;
            }
            input.consume();
            typeParameter.getTypeBounds().add(mTYPE(true));
        }
        input.consume();
        return typeParameter;
    }

    private TypeReference mTYPE( boolean consumeLast ) {
        TypeReference newType = null;
        int arraySize = -1;
        if (input.LA(1) == '[') {
            arraySize = mARRAY_SIZE();
        }

        switch (input.LA(1)) {
            case 'Z':
                newType = new PrimitiveTypeReference(PrimitiveType.BOOLEAN);
                break;
            case 'C':
                newType = new PrimitiveTypeReference(PrimitiveType.CHAR);
                break;
            case 'B':
                newType = new PrimitiveTypeReference(PrimitiveType.BYTE);
                break;
            case 'S':
                newType = new PrimitiveTypeReference(PrimitiveType.SHORT);
                break;
            case 'I':
                newType = new PrimitiveTypeReference(PrimitiveType.INT);
                break;
            case 'F':
                newType = new PrimitiveTypeReference(PrimitiveType.FLOAT);
                break;
            case 'J':
                newType = new PrimitiveTypeReference(PrimitiveType.LONG);
                break;
            case 'D':
                newType = new PrimitiveTypeReference(PrimitiveType.DOUBLE);
                break;
            case 'V':
                newType = new PrimitiveTypeReference(PrimitiveType.VOID);
                break;
            case 'L':
                newType = mOBJECT();
                break;
            case '*':
                newType = new WildcardTypeReference();
                break;
            case '+':
                input.consume();
                newType = new WildcardTypeReference(true, mTYPE(false));
                break;
            case '-':
                input.consume();
                newType = new WildcardTypeReference(false, mTYPE(false));
                break;
            case 'T':
                input.consume();
                newType = new WildcardTypeReference();
                mID();
                break;
        }
        if (arraySize != -1) {
            newType = new ArrayTypeReference(arraySize, newType);
        }
        if (consumeLast) {
            input.consume();
        }
        return newType;
    }

    private int mARRAY_SIZE() {
        int arraySize = 0;
        while (true) {
            if (input.LA(1) != '[') {
                break;
            }
            arraySize++;
            input.consume();
        }
        return arraySize;
    }

    private TypeReference mOBJECT() {
        input.consume();
        TypeReference rootType = null;

        int contLoop = 0;
        List<Queue<String>> nameList = new ArrayList<Queue<String>>();
        List<List<TypeReference>> genericTypeList = new ArrayList<List<TypeReference>>();
        while (true) {
            nameList.add(contLoop, mID());
            if (input.LA(1) == '<') {
                genericTypeList.add(contLoop, mGENERICS());
            }
            // o '.' indica subclass
            if (input.LA(1) == ';' && input.LA(2) != '.') {
                break;
            }
            input.consume();
            contLoop++;
        }

        Pair<String, String> packageAndClassName = separatePackage(nameList.get(0));
        nameList.remove(0);

        rootType = buildType(packageAndClassName.getK1(), packageAndClassName.getK2(), nameList, genericTypeList);

        return rootType;
    }

    private Pair<String, String> separatePackage( Queue<String> queue ) {
        StringBuffer sb = new StringBuffer();

        for (Iterator<String> iterator = queue.iterator(); iterator.hasNext();) {
            String simpleName = iterator.next();
            sb.append(simpleName);
            if (iterator.hasNext()) {
                sb.append('/');
            }
        }
        String tempValue = sb.toString();
        String packageName = tempValue.substring(0, tempValue.lastIndexOf('/')).replace("/", ".");
        String className = tempValue.substring(tempValue.lastIndexOf('/') + 1);

        return new Pair<String, String>(packageName, className);
    }

    private TypeReference buildType( String packageName,
                               String typeName,
                               List<Queue<String>> nameList,
                               List<List<TypeReference>> genericTypeList ) {
        TypeReference resultType = null;

        resultType = new SimpleTypeReference(packageName, typeName);
        if (genericTypeList.size() > 0 && genericTypeList.get(0) != null) {
            resultType = new ParameterizedTypeReference(genericTypeList.get(0), resultType);
        }

        for (int a = 0; a < nameList.size(); a++) {
            // TODO check this for
            for (int i2 = 0; i2 < nameList.get(a).size(); i2++) {
                resultType = new QualifiedTypeReference(resultType, nameList.get(a).poll());
            }
            if (genericTypeList.size() > a + 1 && genericTypeList.get(a + 1) != null) {
                resultType = new ParameterizedTypeReference(genericTypeList.get(a + 1), resultType);
            }
        }

        return resultType;
    }

    private Queue<String> mID() {
        int start = input.index();
        Queue<String> resultList = new LinkedBlockingQueue<String>();
        while (true) {
            if (input.LA(1) == ':' || input.LA(1) == ';' || input.LA(1) == '<') {
                break;
            }
            if (input.LA(1) == '/') {
                resultList.add(input.substring(start, input.index() - 1));
                input.consume();
                start = input.index();
            }
            input.consume();
        }
        resultList.add(input.substring(start, input.index() - 1));
        return resultList;
    }

    private List<TypeReference> mGENERICS() {
        List<TypeReference> listType = new LinkedList<TypeReference>();
        input.consume();
        while (true) {
            if (input.LA(1) == '>') {
                break;
            }
            listType.add(mTYPE(true));
        }
        input.consume();
        return listType;
    }
}
