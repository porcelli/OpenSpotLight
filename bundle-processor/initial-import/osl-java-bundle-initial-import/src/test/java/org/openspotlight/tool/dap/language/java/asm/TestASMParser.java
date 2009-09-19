package org.openspotlight.tool.dap.language.java.asm;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openspotlight.tool.dap.language.java.asm.model.MethodDeclaration;
import org.openspotlight.tool.dap.language.java.asm.model.TypeReference;

public class TestASMParser {

    @Test
    public void testType1() {
        String type = "Ljava/util/List<Ljava/lang/Integer;>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<java.lang.Integer>", types.get(0).getFullName());
    }

    @Test
    public void testType2() {
        String type = "Ljava/util/List<[Ljava/util/List<Ljava/lang/String;>;>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<java.util.List<java.lang.String>[]>", types.get(0).getFullName());
    }

    @Test
    public void testType3() {
        String type = "ZCB";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(3, types.size());
        Assert.assertEquals("boolean", types.get(0).getFullName());
        Assert.assertEquals("char", types.get(1).getFullName());
        Assert.assertEquals("byte", types.get(2).getFullName());
    }

    @Test
    public void testType4() {
        String type = "Ljava/util/HashMap<Ljava/lang/String;Ljava/lang/String;>.HashIterator<Ljava/lang/String;>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.HashMap<java.lang.String, java.lang.String>.HashIterator<java.lang.String>",
                            types.get(0).getFullName());
    }

    @Test
    public void testType5() {
        String type = "Ljava/util/List<[[[Ljava/util/List<Ljava/lang/String;>;>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<java.util.List<java.lang.String>[][][]>", types.get(0).getFullName());
    }

    @Test
    public void testType6() {
        String type = "ZLjava/lang/String;CB";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(4, types.size());
        Assert.assertEquals("boolean", types.get(0).getFullName());
        Assert.assertEquals("java.lang.String", types.get(1).getFullName());
        Assert.assertEquals("char", types.get(2).getFullName());
        Assert.assertEquals("byte", types.get(3).getFullName());
    }

    @Test
    public void testGenericType1() {
        String type = "Ljava/util/List<*>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<?>", types.get(0).getFullName());
    }

    @Test
    public void testGenericType2() {
        String type = "Ljava/util/List<+Ljava/lang/Number;>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<? extends java.lang.Number>", types.get(0).getFullName());
    }

    @Test
    public void testGenericType3() {
        String type = "Ljava/util/List<-Ljava/lang/Number;>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.List<? instanceOf java.lang.Number>", types.get(0).getFullName());
    }

    @Test
    public void testGenericType4() {
        String type = "Ljava/util/HashMap<TK;TV;>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.util.HashMap<K, V>", types.get(0).getFullName());
    }

    @Test
    public void testGenericType5() {
        String type = "Ljava/lang/Class<+TT;>;";
        ASMParser parser = new ASMParser(type);
        List<TypeReference> types = parser.types();
        Assert.assertEquals(1, types.size());
        Assert.assertEquals("java.lang.Class<? extends T>", types.get(0).getFullName());
    }

    @Test
    public void testMethod1() {
        String type = "<T:Ljava/lang/Object;>(I)Ljava/lang/Class<+TT;>;";
        ASMParser parser = new ASMParser(type);
        MethodDeclaration activeMethod = parser.method("teste", false);

        Assert.assertEquals(1, activeMethod.getParameters().size());
        Assert.assertEquals("int", activeMethod.getParameters().get(0).getDataType().getFullName());
        Assert.assertEquals("java.lang.Class<? extends T>", activeMethod.getReturnType().getFullName());
        Assert.assertEquals("T", activeMethod.getTypeParameters().get(0).getName());
        Assert.assertEquals("java.lang.Object", activeMethod.getTypeParameters().get(0).getTypeBounds().get(0).getFullName());
    }
}