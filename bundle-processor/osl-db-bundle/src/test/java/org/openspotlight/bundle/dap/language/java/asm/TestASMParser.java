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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openspotlight.bundle.dap.language.java.asm.model.MethodDeclaration;
import org.openspotlight.bundle.dap.language.java.asm.model.TypeReference;

public class TestASMParser {

    @Test
    public void testGenericType1() {
        final String type = "Ljava/util/List<*>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<?>", types.get(0).getFullName());
    }

    @Test
    public void testGenericType2() {
        final String type = "Ljava/util/List<+Ljava/lang/Number;>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<? extends java.lang.Number>", types.get(0).getFullName());
    }

    @Test
    public void testGenericType3() {
        final String type = "Ljava/util/List<-Ljava/lang/Number;>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<? instanceOf java.lang.Number>", types.get(0).getFullName());
    }

    @Test
    public void testGenericType4() {
        final String type = "Ljava/util/HashMap<TK;TV;>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.HashMap<K, V>", types.get(0).getFullName());
    }

    @Test
    public void testGenericType5() {
        final String type = "Ljava/lang/Class<+TT;>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.lang.Class<? extends T>", types.get(0).getFullName());
    }

    @Test
    public void testMethod1() {
        final String type = "<T:Ljava/lang/Object;>(I)Ljava/lang/Class<+TT;>;";
        final ASMParser parser = new ASMParser(type);
        final MethodDeclaration activeMethod = parser.method("teste", false);

        Assert.assertEquals(1, activeMethod.getParameters().size());
        Assert.assertEquals("int", activeMethod.getParameters().get(0).getDataType().getFullName());
        Assert.assertEquals("java.lang.Class<? extends T>", activeMethod.getReturnType().getFullName());
        Assert.assertEquals("T", activeMethod.getTypeParameters().get(0).getName());
        Assert.assertEquals("java.lang.Object", activeMethod.getTypeParameters().get(0).getTypeBounds().get(0).getFullName());
    }

    @Test
    public void testType1() {
        final String type = "Ljava/util/List<Ljava/lang/Integer;>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<java.lang.Integer>", types.get(0).getFullName());
    }

    @Test
    public void testType2() {
        final String type = "Ljava/util/List<[Ljava/util/List<Ljava/lang/String;>;>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<java.util.List<java.lang.String>[]>", types.get(0).getFullName());
    }

    @Test
    public void testType3() {
        final String type = "ZCB";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(3, types.size());
        Assert.assertEquals("boolean", types.get(0).getFullName());
        Assert.assertEquals("char", types.get(1).getFullName());
        Assert.assertEquals("byte", types.get(2).getFullName());
    }

    @Test
    public void testType4() {
        final String type = "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>.HashIterator<Ljava/lang/String;>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.HashMap<java.lang.String, java.lang.String>.HashIterator<java.lang.String>",
                            types.get(0).getFullName());
    }

    @Test
    public void testType5() {
        final String type = "Ljava/util/List<[[[Ljava/util/List<Ljava/lang/String;>;>;";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<java.util.List<java.lang.String>[][][]>", types.get(0).getFullName());
    }

    @Test
    public void testType6() {
        final String type = "ZLjava/lang/String;CB";
        final ASMParser parser = new ASMParser(type);
        final List<TypeReference> types = parser.types();
        Assert.assertEquals(4, types.size());
        Assert.assertEquals("boolean", types.get(0).getFullName());
        Assert.assertEquals("java.lang.String", types.get(1).getFullName());
        Assert.assertEquals("char", types.get(2).getFullName());
        Assert.assertEquals("byte", types.get(3).getFullName());
    }

    //
    //    @Test
    //    public void testDataType2() {
    //        String type = "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Integer;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(3, typeList.size());
    //        assertEquals(true, typeList.contains("java/lang/String"));
    //        assertEquals(true, typeList.contains("java/lang/Integer"));
    //        assertEquals(true, typeList.contains("java/util/HashMap"));
    //    }
    //
    //    @Test
    //    public void testDataType3() {
    //        String type = "Z";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(0, typeList.size());
    //    }
    //
    //    @Test
    //    public void testDataType4() {
    //        String type = "Ljava/util/List<[Ljava/util/List<Ljava/lang/String;>;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(2, typeList.size());
    //        assertEquals(true, typeList.contains("java/util/List"));
    //        assertEquals(true, typeList.contains("java/lang/String"));
    //    }
    //
    //    @Test
    //    public void testDataType5() {
    //        String type = "Ljava/util/List<Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/Integer;>;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(4, typeList.size());
    //        assertEquals(true, typeList.contains("java/util/List"));
    //        assertEquals(true, typeList.contains("java/lang/String"));
    //        assertEquals(true, typeList.contains("java/util/HashMap"));
    //        assertEquals(true, typeList.contains("java/lang/Integer"));
    //    }
    //
    //    @Test
    //    public void testDataType6() {
    //        String type = "Ljava/util/List<+Ljava/lang/Number;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(2, typeList.size());
    //        assertEquals(true, typeList.contains("java/util/List"));
    //        assertEquals(true, typeList.contains("java/lang/Number"));
    //    }
    //
    //    @Test
    //    public void testDataType7() {
    //        String type = "Ljava/util/List<*>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(1, typeList.size());
    //        assertEquals(true, typeList.contains("java/util/List"));
    //    }
    //
    //    @Test
    //    public void testDataType8() {
    //        String type = "Ljava/util/List<-Ljava/lang/Number;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(2, typeList.size());
    //        assertEquals(true, typeList.contains("java/util/List"));
    //        assertEquals(true, typeList.contains("java/lang/Number"));
    //    }
    //
    //    @Test
    //    public void testDataType9() {
    //        String type = "Ljava/util/HashMap<TK;TV;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(1, typeList.size());
    //        assertEquals(true, typeList.contains("java/util/HashMap"));
    //    }
    //
    //    @Test
    //    public void testDataType10() {
    //        String type = "Ljava/lang/Class<+TT;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(1, typeList.size());
    //        assertEquals(true, typeList.contains("java/lang/Class"));
    //    }
    //
    //    @Test
    //    public void testDataType11() {
    //        String type = "Ljava/lang/Class<-TS;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(1, typeList.size());
    //        assertEquals(true, typeList.contains("java/lang/Class"));
    //        assertEquals("S", types.get(0).getGeneric().getTypes().get(0).getDataType());
    //    }
    //
    //    @Test
    //    public void testDataType12() {
    //        String type = "Lcom/google/common/collect/StandardMultimap<TK;TV;>.WrappedCollection<Ljava/lang/Class;>.Test;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<DataType> types = extract.types();
    //        Set<String> typeList = types.get(0).getObjectTypes();
    //        assertEquals(4, typeList.size());
    //        assertEquals(true, typeList.contains("com/google/common/collect/StandardMultimap"));
    //        assertEquals(true, typeList.contains("com/google/common/collect/StandardMultimap$WrappedCollection"));
    //        assertEquals(true, typeList.contains("java/lang/Class"));
    //        assertEquals(true, typeList.contains("com/google/common/collect/StandardMultimap$WrappedCollection$Test"));
    //    }
    //
    //    @Test
    //    public void testDataType13() {
    //        String type = "Lcom/google/common/collect/StandardMultimap<TK;TV;>.WrappedCollection<Ljava/lang/Class;>.Test<Ljava/lang/String;>;";
    //        ASMTypeParser extract = new ASMTypeParser(type);
    //        List<Type> types = extract.types();
    //        assertEquals("com/google/common/collect/StandardMultimap$WrappedCollection$Test", ((SLType)types.get(0)).toStringASM());
    //    }
}
