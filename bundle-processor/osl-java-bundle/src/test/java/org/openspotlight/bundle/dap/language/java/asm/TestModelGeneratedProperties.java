package org.openspotlight.bundle.dap.language.java.asm;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.openspotlight.bundle.dap.language.java.asm.model.ArrayTypeReference;
import org.openspotlight.bundle.dap.language.java.asm.model.ParameterizedTypeReference;
import org.openspotlight.bundle.dap.language.java.asm.model.PrimitiveTypeReference;
import org.openspotlight.bundle.dap.language.java.asm.model.QualifiedTypeReference;
import org.openspotlight.bundle.dap.language.java.asm.model.SimpleTypeReference;
import org.openspotlight.bundle.dap.language.java.asm.model.TypeReference;
import org.openspotlight.bundle.dap.language.java.asm.model.WildcardTypeReference;
import org.openspotlight.bundle.dap.language.java.asm.model.PrimitiveTypeReference.JavaPrimitiveTypes;

public class TestModelGeneratedProperties {

    @Test
    public void testSimpleTypeRef() {
        SimpleTypeReference simpleType = new SimpleTypeReference("org.openspotlight", "Test");
        Assert.assertEquals("Test", simpleType.getName());
        Assert.assertEquals("org.openspotlight", simpleType.getPackageName());
        Assert.assertEquals("org.openspotlight.Test", simpleType.getFullName());
    }

    @Test
    public void testPrimitiveTypeRef() {
        PrimitiveTypeReference primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.BOOLEAN);
        Assert.assertEquals("boolean", primitiveType.getName());
        Assert.assertEquals("boolean", primitiveType.getFullName());

        primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.BYTE);
        Assert.assertEquals("byte", primitiveType.getName());
        Assert.assertEquals("byte", primitiveType.getFullName());

        primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.CHAR);
        Assert.assertEquals("char", primitiveType.getName());
        Assert.assertEquals("char", primitiveType.getFullName());

        primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.DOUBLE);
        Assert.assertEquals("double", primitiveType.getName());
        Assert.assertEquals("double", primitiveType.getFullName());

        primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.FLOAT);
        Assert.assertEquals("float", primitiveType.getName());
        Assert.assertEquals("float", primitiveType.getFullName());

        primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.INT);
        Assert.assertEquals("int", primitiveType.getName());
        Assert.assertEquals("int", primitiveType.getFullName());

        primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.LONG);
        Assert.assertEquals("long", primitiveType.getName());
        Assert.assertEquals("long", primitiveType.getFullName());

        primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.SHORT);
        Assert.assertEquals("short", primitiveType.getName());
        Assert.assertEquals("short", primitiveType.getFullName());

        primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.VOID);
        Assert.assertEquals("void", primitiveType.getName());
        Assert.assertEquals("void", primitiveType.getFullName());
    }

    @Test
    public void testArrayTypeRef() {
        SimpleTypeReference simpleType = new SimpleTypeReference("org.openspotlight", "Test");
        ArrayTypeReference arrayType = new ArrayTypeReference(1, simpleType);

        Assert.assertEquals(1, arrayType.getArrayDimensions());
        Assert.assertEquals("Test[]", arrayType.getName());
        Assert.assertEquals("org.openspotlight.Test[]", arrayType.getFullName());

        arrayType.setArrayDimensions(3);
        Assert.assertEquals(3, arrayType.getArrayDimensions());
        Assert.assertEquals("Test[][][]", arrayType.getName());
        Assert.assertEquals("org.openspotlight.Test[][][]", arrayType.getFullName());

        PrimitiveTypeReference primitiveType = new PrimitiveTypeReference(JavaPrimitiveTypes.BOOLEAN);
        arrayType = new ArrayTypeReference(1, primitiveType);
        Assert.assertEquals(1, arrayType.getArrayDimensions());
        Assert.assertEquals("boolean[]", arrayType.getName());
        Assert.assertEquals("boolean[]", arrayType.getFullName());

        arrayType.setArrayDimensions(3);
        Assert.assertEquals(3, arrayType.getArrayDimensions());
        Assert.assertEquals("boolean[][][]", arrayType.getName());
        Assert.assertEquals("boolean[][][]", arrayType.getFullName());
    }

    @Test
    public void testQualifiedTypeRef() {
        SimpleTypeReference simpleType = new SimpleTypeReference("org.openspotlight", "Test");
        QualifiedTypeReference qualifiedType = new QualifiedTypeReference(simpleType, "SecondTest");

        Assert.assertEquals("Test.SecondTest", qualifiedType.getName());
        Assert.assertEquals("org.openspotlight.Test.SecondTest", qualifiedType.getFullName());
    }

    @Test
    public void testWildcardTypeRef() {
        WildcardTypeReference wildcardType = new WildcardTypeReference();

        Assert.assertEquals("?", wildcardType.getName());
        Assert.assertEquals("?", wildcardType.getFullName());

        wildcardType = new WildcardTypeReference(true, new SimpleTypeReference("org.openspotlight", "Test"));

        Assert.assertEquals("? extends org.openspotlight.Test", wildcardType.getName());
        Assert.assertEquals("? extends org.openspotlight.Test", wildcardType.getFullName());

        wildcardType = new WildcardTypeReference(false, new SimpleTypeReference("org.openspotlight", "Test"));

        Assert.assertEquals("? instanceOf org.openspotlight.Test", wildcardType.getName());
        Assert.assertEquals("? instanceOf org.openspotlight.Test", wildcardType.getFullName());
    }

    @Test
    public void testParameterizedTypeRef() {
        List<TypeReference> typeReferences = new LinkedList<TypeReference>();
        WildcardTypeReference wildcardType = new WildcardTypeReference(true, new SimpleTypeReference("org.openspotlight", "Test"));
        typeReferences.add(wildcardType);
        SimpleTypeReference simpleType = new SimpleTypeReference("org.openspotlight", "Test2");

        ParameterizedTypeReference parameterizedType = new ParameterizedTypeReference(typeReferences, simpleType);
        Assert.assertEquals("Test2<? extends org.openspotlight.Test>", parameterizedType.getName());
        Assert.assertEquals("org.openspotlight.Test2<? extends org.openspotlight.Test>", parameterizedType.getFullName());

        typeReferences.add(new SimpleTypeReference("java.lang", "String"));
        
        parameterizedType = new ParameterizedTypeReference(typeReferences, simpleType);
        Assert.assertEquals("Test2<? extends org.openspotlight.Test, java.lang.String>", parameterizedType.getName());
        Assert.assertEquals("org.openspotlight.Test2<? extends org.openspotlight.Test, java.lang.String>", parameterizedType.getFullName());

    }

}
