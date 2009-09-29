package org.openspotlight.bundle.dap.language.java.resolver;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.IsNot;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodParameterDefinition;
import org.openspotlight.bundle.dap.language.java.metamodel.link.TypeDeclares;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.common.Pair;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.testng.annotations.Test;

@Test
public class TestMethodResolution extends AbstractMethodResolutionTest {

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void testInvalidParamsNullParams() throws Exception {

        setupMethodResolver(null);
        methodResolver.getMethod(null, null);
    }

    @Test( expectedExceptions = IllegalArgumentException.class )
    public void testInvalidParams() throws Exception {
        createMethod("java.lang", "Object", "toString", "toString()");
        JavaType abstractType = getAbstractType("java.lang", "Object");

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(abstractType)).thenReturn(false);

        setupMethodResolver(mockTypeResolver);

        methodResolver.getMethod(abstractType, "toString");
    }

    @Test
    public void resolveSimpleMethod() throws Exception {
        Pair<JavaType, JavaMethod> typeAndMethod = createMethod("java.lang", "Object", "toString", "toString()");

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(typeAndMethod.getK1())).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(typeAndMethod.getK1());
        when(mockTypeResolver.getTypesLowerHigherLast(typeAndMethod.getK1())).thenReturn(nodesHierarchy);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeAndMethod.getK1(), "toString");

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(typeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveExtendedMethod() throws Exception {
        Pair<JavaType, JavaMethod> typeAndMethodObject = createMethod("java.lang", "Object", "toString", "toString()");
        Pair<JavaType, JavaMethod> typeAndMethodSLObject = createMethod("org.openspotlight", "SLObject", "toString", "toString()");

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(typeAndMethodObject.getK1())).thenReturn(true);
        when(mockTypeResolver.isContreteType(typeAndMethodSLObject.getK1())).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(typeAndMethodSLObject.getK1());
        nodesHierarchy.add(typeAndMethodObject.getK1());

        when(mockTypeResolver.getTypesLowerHigherLast(typeAndMethodSLObject.getK1())).thenReturn(nodesHierarchy);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeAndMethodSLObject.getK1(), "toString");

        assertThat(foundMethod, is(notNullValue()));
        //        assertThat(foundMethod.getID(), is(not(typeAndMethodObject.getK2().getID())));
        assertThat(foundMethod.getID(), is(typeAndMethodSLObject.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveMethodOnParent() throws Exception {
        Pair<JavaType, JavaMethod> typeAndMethodObject = createMethod("java.lang", "Object", "toString", "toString()");
        JavaType typeSLObject = createType("org.openspotlight", "SLObject", typeAndMethodObject.getK1(), true);

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(typeAndMethodObject.getK1())).thenReturn(true);
        when(mockTypeResolver.isContreteType(typeSLObject)).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(typeSLObject);
        nodesHierarchy.add(typeAndMethodObject.getK1());

        when(mockTypeResolver.getTypesLowerHigherLast(typeSLObject)).thenReturn(nodesHierarchy);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeSLObject, "toString");

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(typeAndMethodObject.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveMethodOnParentSecondLevel() throws Exception {
        Pair<JavaType, JavaMethod> typeAndMethodObject = createMethod("java.lang", "Object", "toString", "toString()");
        JavaType typeSLObject = createType("org.openspotlight", "SLObject", typeAndMethodObject.getK1(), true);
        JavaType typeSLObject2 = createType("org.openspotlight", "SLObject2", typeSLObject, true);

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(typeAndMethodObject.getK1())).thenReturn(true);
        when(mockTypeResolver.isContreteType(typeSLObject2)).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();

        nodesHierarchy.add(typeSLObject2);
        nodesHierarchy.add(typeSLObject);
        nodesHierarchy.add(typeAndMethodObject.getK1());

        when(mockTypeResolver.getTypesLowerHigherLast(typeSLObject2)).thenReturn(nodesHierarchy);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeSLObject2, "toString");

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(typeAndMethodObject.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveMethodOnParentFiveLevels() throws Exception {
        Pair<JavaType, JavaMethod> typeAndMethodObject = createMethod("java.lang", "Object", "toString", "toString()");
        JavaType typeSLObject = createType("org.openspotlight", "SLObject", typeAndMethodObject.getK1(), true);
        JavaType typeSLObject2 = createType("org.openspotlight", "SLObject2", typeSLObject, true);
        JavaType typeSLObject3 = createType("org.openspotlight", "SLObject3", typeSLObject2, true);
        JavaType typeSLObject4 = createType("org.openspotlight", "SLObject4", typeSLObject3, true);
        JavaType typeSLObject5 = createType("org.openspotlight", "SLObject5", typeSLObject4, true);

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(typeAndMethodObject.getK1())).thenReturn(true);
        when(mockTypeResolver.isContreteType(typeSLObject5)).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();

        nodesHierarchy.add(typeSLObject5);
        nodesHierarchy.add(typeSLObject4);
        nodesHierarchy.add(typeSLObject3);
        nodesHierarchy.add(typeSLObject2);
        nodesHierarchy.add(typeSLObject);
        nodesHierarchy.add(typeAndMethodObject.getK1());

        when(mockTypeResolver.getTypesLowerHigherLast(typeSLObject5)).thenReturn(nodesHierarchy);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeSLObject5, "toString");

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(typeAndMethodObject.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveMethodOnParentFiveLevelsFoundOnThirdLevel() throws Exception {
        Pair<JavaType, JavaMethod> typeAndMethodObject = createMethod("java.lang", "Object", "toString", "toString()");
        JavaType typeSLObject = createType("org.openspotlight", "SLObject", typeAndMethodObject.getK1(), true);
        Pair<JavaType, JavaMethod> typeAndMethodObject2 = createMethod("org.openspotlight", "SLObject2", "toString", "toString()");
        JavaType typeSLObject3 = createType("org.openspotlight", "SLObject3", typeAndMethodObject2.getK1(), true);
        JavaType typeSLObject4 = createType("org.openspotlight", "SLObject4", typeSLObject3, true);
        JavaType typeSLObject5 = createType("org.openspotlight", "SLObject5", typeSLObject4, true);

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(typeAndMethodObject.getK1())).thenReturn(true);
        when(mockTypeResolver.isContreteType(typeSLObject5)).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();

        nodesHierarchy.add(typeSLObject5);
        nodesHierarchy.add(typeSLObject4);
        nodesHierarchy.add(typeSLObject3);
        nodesHierarchy.add(typeAndMethodObject2.getK1());
        nodesHierarchy.add(typeSLObject);
        nodesHierarchy.add(typeAndMethodObject.getK1());

        when(mockTypeResolver.getTypesLowerHigherLast(typeSLObject5)).thenReturn(nodesHierarchy);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeSLObject5, "toString");

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(typeAndMethodObject2.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveMethodOnParentFiveLevelsFoundOnFirstLevel() throws Exception {
        Pair<JavaType, JavaMethod> typeAndMethodObject = createMethod("java.lang", "Object", "toString", "toString()");
        JavaType typeSLObject = createType("org.openspotlight", "SLObject", typeAndMethodObject.getK1(), true);
        Pair<JavaType, JavaMethod> typeAndMethodObject2 = createMethod("org.openspotlight", "SLObject2", "toString", "toString()");
        JavaType typeSLObject3 = createType("org.openspotlight", "SLObject3", typeAndMethodObject2.getK1(), true);
        JavaType typeSLObject4 = createType("org.openspotlight", "SLObject4", typeSLObject3, true);
        Pair<JavaType, JavaMethod> typeAndMethodObject5 = createMethod("org.openspotlight", "SLObject5", "toString", "toString()");

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(typeAndMethodObject.getK1())).thenReturn(true);
        when(mockTypeResolver.isContreteType(typeAndMethodObject5.getK1())).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();

        nodesHierarchy.add(typeAndMethodObject5.getK1());
        nodesHierarchy.add(typeSLObject4);
        nodesHierarchy.add(typeSLObject3);
        nodesHierarchy.add(typeAndMethodObject2.getK1());
        nodesHierarchy.add(typeSLObject);
        nodesHierarchy.add(typeAndMethodObject.getK1());

        when(mockTypeResolver.getTypesLowerHigherLast(typeAndMethodObject5.getK1())).thenReturn(nodesHierarchy);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeAndMethodObject5.getK1(), "toString");

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(typeAndMethodObject5.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveMethodOnePrimitiveParameter() throws Exception {
        List<JavaType> parameterList = new LinkedList<JavaType>();
        JavaTypePrimitive typeParameter = createPrimitiveType("long");
        parameterList.add(typeParameter);
        createMethod("java.lang", "Object", "wait", "wait()");
        Pair<JavaType, JavaMethod> typeAndMethod = createMethod("java.lang", "Object", "wait", "wait(long)", typeParameter);

        JavaType stringParameter = createType("java.lang", "String", typeAndMethod.getK1(), true);
        Pair<JavaType, JavaMethod> wrongTypeAndMethod = createMethod("java.lang", "Object", "wait", "wait(java.lang.String)",
                                                                     stringParameter);

        @SuppressWarnings( "unchecked" )
        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(typeAndMethod.getK1())).thenReturn(true);
        when(mockTypeResolver.isContreteType(typeParameter)).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(typeAndMethod.getK1());
        when(mockTypeResolver.getTypesLowerHigherLast(typeAndMethod.getK1())).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isTypeOf(typeParameter, typeParameter)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringParameter, stringParameter)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeAndMethod.getK1(), "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(not(wrongTypeAndMethod.getK2().getID())));
        assertThat(foundMethod.getID(), is(typeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveMethodOneSimpleTypeParameter() throws Exception {

        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaTypePrimitive longType = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);

        createMethod("java.lang", "Object", "wait", "wait()");
        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod("java.lang", "Object", "wait", "wait(java.lang.Integer)",
                                                                       parameterList.get(0));

        Pair<JavaType, JavaMethod> wrongTypeAndMethod = createMethod("java.lang", "Object", "wait", "wait(java.lang.String)",
                                                                     stringType);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(objectType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(objectType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(not(wrongTypeAndMethod.getK2().getID())));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void resolveMethodOneSimpleTypeParameterWithHierarchy() throws Exception {

        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaTypePrimitive longType = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);

        createMethod("java.lang", "Object", "wait", "wait()");
        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod("java.lang", "Object", "wait", "wait(java.lang.Number)",
                                                                       numberType);

        Pair<JavaType, JavaMethod> wrongTypeAndMethod = createMethod("java.lang", "Object", "wait", "wait(java.lang.String)",
                                                                     stringType);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(objectType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(objectType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(not(wrongTypeAndMethod.getK2().getID())));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test( expectedExceptions = SLBundleException.class )
    public void notFoundMethodOneSimpleTypeParameterWithHierarchy() throws Exception {

        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaTypePrimitive longType = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(stringType);

        createMethod("java.lang", "Object", "wait", "wait()");
        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait", "wait(java.lang.Number)", numberType);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);
        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(objectType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(objectType, "wait", parameterList);

        assertThat(foundMethod, is(nullValue()));
    }

    @Test
    public void getMethodOneSimpleTypeParameterWithHierarchy() throws Exception {

        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaTypePrimitive longType = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);

        createMethod("java.lang", "Object", "wait", "wait()");
        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait", "wait(java.lang.Number)", numberType);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodSeveralTypeParameter() throws Exception {

        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaTypePrimitive longType = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);
        parameterList.add(stringType);
        parameterList.add(longType);

        createMethod("java.lang", "Object", "wait", "wait()");
        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait",
                                                                       "wait(java.lang.Number, java.lang.String, long)",
                                                                       numberType, stringType, longType);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);
        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(objectType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);

        when(mockTypeResolver.countParents(objectType)).thenReturn(0);
        when(mockTypeResolver.countParents(stringType)).thenReturn(1);
        when(mockTypeResolver.countParents(numberType)).thenReturn(1);
        when(mockTypeResolver.countParents(integerType)).thenReturn(2);
        when(mockTypeResolver.countParents(longType)).thenReturn(0);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(objectType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodSeveralTypeParameterHierarchy() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaTypePrimitive longType = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);
        parameterList.add(stringType);
        parameterList.add(longType);

        createMethod("java.lang", "Object", "wait", "wait()");
        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait",
                                                                       "wait(java.lang.Number, java.lang.String, long)",
                                                                       numberType, stringType, longType);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodBetween2Matches() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(objectType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait",
                                                                       "wait(java.lang.Integer, java.lang.String, long)",
                                                                       numberType, stringType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(longType);
        when(mockTypeResolver.getTypesLowerHigherLast(longType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodBetween5Matches() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(objectType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);
        createMethod(objectType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(objectType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait",
                                                                       "wait(java.lang.Integer, java.lang.String, long)",
                                                                       numberType, stringType, longTypePrimitive);

        createMethod(objectType, "wait", "wait(java.lang.Object, java.lang.Object, long)", objectType, objectType,
                     longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(longType);
        when(mockTypeResolver.getTypesLowerHigherLast(longType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodBetween5Matches2() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(stringType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(objectType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);
        createMethod(objectType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(objectType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        createMethod(objectType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait",
                                                                       "wait(java.lang.Object, java.lang.Object, long)",
                                                                       objectType, objectType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodBetween5MatchesBoxing() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(longTypePrimitive);
        parameterList.add(longTypePrimitive);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(objectType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);
        createMethod(objectType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait",
                                                                       "wait(java.lang.Long, java.lang.Long, java.lang.Long)",
                                                                       longType, longType, longType);

        createMethod(objectType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        createMethod(objectType, "wait", "wait(java.lang.Object, java.lang.Object, long)", objectType, objectType,
                     longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test( expectedExceptions = SLBundleException.class )
    public void getMethodBetween5MatchesBoxingNotAllowed() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(longTypePrimitive);
        parameterList.add(longTypePrimitive);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(objectType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);
        createMethod(objectType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(objectType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        createMethod(objectType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        createMethod(objectType, "wait", "wait(java.lang.Object, java.lang.Object, long)", objectType, objectType,
                     longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        methodResolver.getMethod(integerType, "wait", parameterList);
    }

    @Test
    public void getMethodBetween5Matches3() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(stringType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(integerType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);
        createMethod(integerType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(integerType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        createMethod(integerType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(integerType, "wait",
                                                                       "wait(java.lang.Object, java.lang.Object, long)",
                                                                       objectType, objectType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodBetween5Matches4() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(stringType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(numberType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);
        createMethod(numberType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(numberType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        createMethod(numberType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(numberType, "wait",
                                                                       "wait(java.lang.Object, java.lang.Object, long)",
                                                                       objectType, objectType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodBetween5Matches5() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(stringType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(numberType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);
        createMethod(numberType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(objectType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(integerType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(numberType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        createMethod(numberType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        createMethod(objectType, "wait", "wait(java.lang.Object, java.lang.Object, long)", objectType, objectType,
                     longTypePrimitive);

        createMethod(numberType, "wait", "wait(java.lang.Object, java.lang.Object, long)", objectType, objectType,
                     longTypePrimitive);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(integerType, "wait",
                                                                       "wait(java.lang.Object, java.lang.Object, long)",
                                                                       objectType, objectType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodBetween5Matches6() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, false);
        JavaType longType = createType("java.lang", "Long", numberType, false);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(stringType);
        parameterList.add(longType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(numberType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);
        createMethod(numberType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(objectType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(integerType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(numberType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        createMethod(numberType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        createMethod(objectType, "wait", "wait(java.lang.Object, java.lang.Object, long)", objectType, objectType,
                     longTypePrimitive);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(numberType, "wait",
                                                                       "wait(java.lang.Object, java.lang.Object, long)",
                                                                       objectType, objectType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test( expectedExceptions = SLBundleException.class )
    public void getMethodInfinitiLooping() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(objectType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);

        createMethod(objectType, "wait", "wait(java.lang.Integer, java.lang.String, long)", numberType, stringType, longType);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(longType);
        when(mockTypeResolver.getTypesLowerHigherLast(longType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(nullValue()));
    }

    @Test
    public void testCache() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, true);
        JavaType longType = createType("java.lang", "Long", numberType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(integerType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        createMethod(objectType, "wait", "wait(java.lang.Number, java.lang.String, java.lang.Long)", numberType, stringType,
                     longType);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(objectType, "wait",
                                                                       "wait(java.lang.Integer, java.lang.String, long)",
                                                                       numberType, stringType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(integerType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(objectType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(longType);
        when(mockTypeResolver.getTypesLowerHigherLast(longType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);
        String uniqueId = methodResolver.getUniqueId(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
        assertThat(methodResolver.getCache().get(uniqueId), is(notNullValue()));
        assertThat(methodResolver.getCache().get(uniqueId), is(foundMethod.getID()));
    }

    @Test
    public void getMethodParameterizedByClass() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, false);
        JavaType longType = createType("java.lang", "Long", numberType, false);
        JavaType parameterizedType = createTypeParameterized("java.lang", "Long$T", longType, objectType, true);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(stringType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");
        Pair<JavaType, JavaMethod> correctTypeAndMethod = createMethod(numberType, "wait", "wait(java.lang.Long$T, java.lang.String, java.lang.Long)", parameterizedType, stringType,
                                                                       longType);
        createMethod(numberType, "xait", "xait(java.lang.Long$T, java.lang.String, java.lang.Long)", parameterizedType, stringType,
                     longType);
        createMethod(numberType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(objectType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(integerType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(numberType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        createMethod(numberType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        createMethod(objectType, "wait", "wait(java.lang.Object, java.lang.Object, long)", objectType, objectType,
                     longTypePrimitive);

        createMethod(numberType, "wait",
                     "wait(java.lang.Object, java.lang.Object, long)",
                     objectType, objectType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(parameterizedType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(parameterizedType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(objectType, parameterizedType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, parameterizedType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);
        
        
        // Alexandre,
        // Notice I've commented out the foundMethod assigment,
        // because of a casting exception. The getMethod() method returns a JavaTypeClass,
        // not a JavaMethodMethod (the type you're trying to cast to)
        SLNode node = methodResolver.getMethod(integerType, "wait", parameterList);
    	Class<?> nodeType = node.getClass().getInterfaces()[0];
    	System.out.println("I am " + nodeType.getName());
    	
    	assertThat(node, not(instanceOf(JavaTypeClass.class)));
    	assertThat(node, instanceOf(JavaMethodMethod.class));
    	
    	JavaMethodMethod foundMethod = (JavaMethodMethod) node;	
    	
    	//JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }

    @Test
    public void getMethodParameterizedByMethod() throws Exception {
        JavaType objectType = createType("java.lang", "Object", null, false);
        JavaType stringType = createType("java.lang", "String", objectType, true);
        JavaType numberType = createType("java.lang", "Number", objectType, true);
        JavaType integerType = createType("java.lang", "Integer", numberType, false);
        JavaType longType = createType("java.lang", "Long", numberType, false);
        JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

        List<JavaType> parameterList = new LinkedList<JavaType>();
        parameterList.add(stringType);
        parameterList.add(stringType);
        parameterList.add(longTypePrimitive);

        createMethod("java.lang", "Object", "wait", "wait()");

        //Start code to build Method Parameterized Type
        JavaMethodMethod method = numberType.addNode(JavaMethodMethod.class, "wait(java.lang.Long$this$T, java.lang.String, java.lang.Long)");
        method.setSimpleName("wait");
        graphSession.addLink(TypeDeclares.class, numberType, method, false);

        JavaType parameterizedType = createTypeParameterized("java.lang", "Long$this$T", method, objectType, true);

        SLLink link = graphSession.addLink(MethodParameterDefinition.class, method, parameterizedType, false);
        link.setProperty(Integer.class, "Order", 0);
        link = graphSession.addLink(MethodParameterDefinition.class, method, stringType, false);
        link.setProperty(Integer.class, "Order", 1);
        link = graphSession.addLink(MethodParameterDefinition.class, method, longType, false);
        link.setProperty(Integer.class, "Order", 2);

        Pair<JavaType, JavaMethod> correctTypeAndMethod = new Pair<JavaType, JavaMethod>(numberType, method);
        //Method End

        createMethod(numberType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(objectType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(integerType, "wait", "wait(java.lang.String, java.lang.String, java.lang.String)", stringType, stringType,
                     stringType);
        createMethod(numberType, "wait", "wait(java.lang.Long, java.lang.Long, java.lang.Long)", longType, longType, longType);

        createMethod(numberType, "wait", "wait(java.lang.Integer, java.lang.String, long)", integerType, stringType,
                     longTypePrimitive);

        createMethod(objectType, "wait", "wait(java.lang.Object, java.lang.Object, long)", objectType, objectType,
                     longTypePrimitive);

        createMethod(numberType, "wait",
                     "wait(java.lang.Object, java.lang.Object, long)",
                     objectType, objectType, longTypePrimitive);

        TypeResolver<JavaType> mockTypeResolver = mock(TypeResolver.class);

        List<JavaType> nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy = new LinkedList<JavaType>();
        nodesHierarchy.add(integerType);
        nodesHierarchy.add(numberType);
        nodesHierarchy.add(objectType);
        when(mockTypeResolver.getTypesLowerHigherLast(integerType)).thenReturn(nodesHierarchy);

        when(mockTypeResolver.isContreteType(objectType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isContreteType(integerType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(stringType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(longType)).thenReturn(true);
        when(mockTypeResolver.isContreteType(parameterizedType)).thenReturn(true);

        when(mockTypeResolver.isTypeOf(objectType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, stringType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, integerType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(integerType, numberType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(numberType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longTypePrimitive, longType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, longTypePrimitive)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(longType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(parameterizedType, objectType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(objectType, parameterizedType)).thenReturn(true);
        when(mockTypeResolver.isTypeOf(stringType, parameterizedType)).thenReturn(true);

        setupMethodResolver(mockTypeResolver);

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(integerType, "wait", parameterList);

        assertThat(foundMethod, is(notNullValue()));
        assertThat(foundMethod.getID(), is(correctTypeAndMethod.getK2().getID()));
        assertThat(foundMethod.getContext(), is(not(abstractContex)));
    }
}
