package org.openspotlight.bundle.dap.language.java.resolver;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodParameterDefinition;
import org.openspotlight.bundle.dap.language.java.metamodel.link.TypeDeclares;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.common.Pair;
import org.openspotlight.graph.SLLink;

@SuppressWarnings("unused")
public class TestIntegratedMethodResolution extends
		AbstractMethodResolutionTest {

	@Test()
	public void getMethodBetween2Matches() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, false);
		final JavaType longType = createType("java.lang", "Long", numberType,
				false);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(objectType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait",
						"wait(java.lang.Integer, java.lang.String, long)",
						integerType, stringType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodBetween5Matches() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, false);
		final JavaType longType = createType("java.lang", "Long", numberType,
				false);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(objectType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait",
						"wait(java.lang.Integer, java.lang.String, long)",
						integerType, stringType, longTypePrimitive);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodBetween5Matches2() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, false);
		final JavaType longType = createType("java.lang", "Long", numberType,
				false);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(stringType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(objectType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait",
						"wait(java.lang.Object, java.lang.Object, long)",
						objectType, objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodBetween5Matches3() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaType longType = createType("java.lang", "Long", numberType,
				true);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(stringType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(integerType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);
		this.createMethod(integerType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(integerType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		this.createMethod(integerType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(integerType, "wait",
						"wait(java.lang.Object, java.lang.Object, long)",
						objectType, objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodBetween5Matches4() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaType longType = createType("java.lang", "Long", numberType,
				true);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(stringType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(numberType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		this.createMethod(numberType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(numberType, "wait",
						"wait(java.lang.Object, java.lang.Object, long)",
						objectType, objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodBetween5Matches5() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaType longType = createType("java.lang", "Long", numberType,
				true);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(stringType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(numberType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(integerType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		this.createMethod(numberType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		this.createMethod(numberType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(integerType, "wait",
						"wait(java.lang.Object, java.lang.Object, long)",
						objectType, objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodBetween5Matches6() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, false);
		final JavaType longType = createType("java.lang", "Long", numberType,
				false);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(stringType);
		parameterList.add(longType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(numberType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(integerType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		this.createMethod(numberType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(numberType, "wait",
						"wait(java.lang.Object, java.lang.Object, long)",
						objectType, objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodBetween5MatchesBoxing() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, false);
		final JavaType longType = createType("java.lang", "Long", numberType,
				false);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(longTypePrimitive);
		parameterList.add(longTypePrimitive);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(objectType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait",
						"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
						longType, longType, longType);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test(expected = SLBundleException.class)
	public void getMethodBetween5MatchesBoxingNotAllowed() throws Exception {
		setupMethodResolverDisablingAutoboxing();

		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaType longType = createType("java.lang", "Long", numberType,
				true);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(longTypePrimitive);
		parameterList.add(longTypePrimitive);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(objectType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		methodResolver.getMethod(integerType, "wait", parameterList);
	}

	@Test(expected = SLBundleException.class)
	public void getMethodInfinitiLooping() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaType longType = createType("java.lang", "Long", numberType,
				true);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(objectType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", numberType,
				stringType, longType);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(nullValue()));
	}

	@Test()
	public void getMethodOneSimpleTypeParameterWithHierarchy() throws Exception {

		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaTypePrimitive longType = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait", "wait(java.lang.Number)",
						numberType);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test
	@Ignore(value = "needs to review how to address ParameterizedTypes at TypeResolution (its bases on instanced classes)")
	public void getMethodParameterizedByClass() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, false);
		final JavaType longType = createType("java.lang", "Long", numberType,
				false);
		final JavaType parameterizedType = createTypeParameterized("java.lang",
				"Long$T", longType, objectType, true);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(stringType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(
						numberType,
						"wait",
						"wait(java.lang.Long$T, java.lang.String, java.lang.Long)",
						parameterizedType, stringType, longType);
		this.createMethod(numberType, "xait",
				"xait(java.lang.Long$T, java.lang.String, java.lang.Long)",
				parameterizedType, stringType, longType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(integerType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		this.createMethod(numberType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		this.createMethod(numberType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		System.out.println("found method: " + foundMethod);
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test
	@Ignore(value = "needs to review how to address ParameterizedTypes at TypeResolution (its bases on instanced classes)")
	public void getMethodParameterizedByMethod() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, false);
		final JavaType longType = createType("java.lang", "Long", numberType,
				false);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(stringType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");

		// Start code to build Method Parameterized Type
		final JavaMethodMethod method = numberType
				.addNode(JavaMethodMethod.class,
						"wait(java.lang.Long$this$T, java.lang.String, java.lang.Long)");
		method.setSimpleName("wait");
		graphSession.addLink(TypeDeclares.class, numberType, method, false);

		final JavaType parameterizedType = createTypeParameterized("java.lang",
				"Long$this$T", method, objectType, true);

		SLLink link = graphSession.addLink(MethodParameterDefinition.class,
				method, parameterizedType, false);
		link.setProperty(Integer.class, "Order", 0);
		link = graphSession.addLink(MethodParameterDefinition.class, method,
				stringType, false);
		link.setProperty(Integer.class, "Order", 1);
		link = graphSession.addLink(MethodParameterDefinition.class, method,
				longType, false);
		link.setProperty(Integer.class, "Order", 2);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = new Pair<JavaType, JavaMethod>(
				numberType, method);
		// Method End

		this.createMethod(numberType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(objectType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(integerType, "wait",
				"wait(java.lang.String, java.lang.String, java.lang.String)",
				stringType, stringType, stringType);
		this.createMethod(numberType, "wait",
				"wait(java.lang.Long, java.lang.Long, java.lang.Long)",
				longType, longType, longType);

		this.createMethod(numberType, "wait",
				"wait(java.lang.Integer, java.lang.String, long)", integerType,
				stringType, longTypePrimitive);

		this.createMethod(objectType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		this.createMethod(numberType, "wait",
				"wait(java.lang.Object, java.lang.Object, long)", objectType,
				objectType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodSeveralTypeParameter() throws Exception {

		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaTypePrimitive longType = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);
		parameterList.add(stringType);
		parameterList.add(longType);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait",
						"wait(java.lang.Number, java.lang.String, long)",
						numberType, stringType, longType);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(objectType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void getMethodSeveralTypeParameterHierarchy() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaTypePrimitive longType = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);
		parameterList.add(stringType);
		parameterList.add(longType);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait",
						"wait(java.lang.Number, java.lang.String, long)",
						numberType, stringType, longType);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test(expected = SLBundleException.class)
	public void notFoundMethodOneSimpleTypeParameterWithHierarchy()
			throws Exception {

		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaTypePrimitive longType = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(stringType);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait", "wait(java.lang.Number)",
						numberType);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(objectType, "wait", parameterList);

		assertThat(foundMethod, is(nullValue()));
	}

	@Test()
	public void resolveExtendedMethod() throws Exception {
		final Pair<JavaType, JavaMethod> typeAndMethodObject = this
				.createMethod("java.lang", "Object", "toString", "toString()");
		final Pair<JavaType, JavaMethod> typeAndMethodSLObject = this
				.createMethod("org.openspotlight", "SLObject", "toString",
						"toString()");

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(typeAndMethodSLObject.getK1(), "toString");

		assertThat(foundMethod, is(notNullValue()));
		// assertThat(foundMethod.getID(),
		// is(not(typeAndMethodObject.getK2().getID())));
		assertThat(foundMethod.getID(), is(typeAndMethodSLObject.getK2()
				.getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveMethodOnePrimitiveParameter() throws Exception {
		final List<JavaType> parameterList = new LinkedList<JavaType>();
		final JavaTypePrimitive typeParameter = createPrimitiveType("long");
		parameterList.add(typeParameter);
		this.createMethod("java.lang", "Object", "wait", "wait()");
		final Pair<JavaType, JavaMethod> typeAndMethod = this.createMethod(
				"java.lang", "Object", "wait", "wait(long)", typeParameter);

		final JavaType stringParameter = createType("java.lang", "String",
				typeAndMethod.getK1(), true);
		final Pair<JavaType, JavaMethod> wrongTypeAndMethod = this
				.createMethod("java.lang", "Object", "wait",
						"wait(java.lang.String)", stringParameter);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(typeAndMethod.getK1(), "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(), is(not(wrongTypeAndMethod.getK2()
				.getID())));
		assertThat(foundMethod.getID(), is(typeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveMethodOneSimpleTypeParameter() throws Exception {

		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaTypePrimitive longType = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod("java.lang", "Object", "wait",
						"wait(java.lang.Integer)", parameterList.get(0));

		final Pair<JavaType, JavaMethod> wrongTypeAndMethod = this
				.createMethod("java.lang", "Object", "wait",
						"wait(java.lang.String)", stringType);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(objectType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(), is(not(wrongTypeAndMethod.getK2()
				.getID())));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveMethodOneSimpleTypeParameterWithHierarchy()
			throws Exception {

		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaTypePrimitive longType = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod("java.lang", "Object", "wait",
						"wait(java.lang.Number)", numberType);

		final Pair<JavaType, JavaMethod> wrongTypeAndMethod = this
				.createMethod("java.lang", "Object", "wait",
						"wait(java.lang.String)", stringType);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(objectType, "wait", parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(), is(not(wrongTypeAndMethod.getK2()
				.getID())));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveMethodOnParent() throws Exception {
		final Pair<JavaType, JavaMethod> typeAndMethodObject = this
				.createMethod("java.lang", "Object", "toString", "toString()");
		final JavaType typeSLObject = createType("org.openspotlight",
				"SLObject", typeAndMethodObject.getK1(), true);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(typeSLObject, "toString");

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(), is(typeAndMethodObject.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveMethodOnParentFiveLevels() throws Exception {
		final Pair<JavaType, JavaMethod> typeAndMethodObject = this
				.createMethod("java.lang", "Object", "toString", "toString()");
		final JavaType typeSLObject = createType("org.openspotlight",
				"SLObject", typeAndMethodObject.getK1(), true);
		final JavaType typeSLObject2 = createType("org.openspotlight",
				"SLObject2", typeSLObject, true);
		final JavaType typeSLObject3 = createType("org.openspotlight",
				"SLObject3", typeSLObject2, true);
		final JavaType typeSLObject4 = createType("org.openspotlight",
				"SLObject4", typeSLObject3, true);
		final JavaType typeSLObject5 = createType("org.openspotlight",
				"SLObject5", typeSLObject4, true);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(typeSLObject5, "toString");

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(), is(typeAndMethodObject.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveMethodOnParentFiveLevelsFoundOnFirstLevel()
			throws Exception {
		final Pair<JavaType, JavaMethod> typeAndMethodObject = this
				.createMethod("java.lang", "Object", "toString", "toString()");
		final JavaType typeSLObject = createType("org.openspotlight",
				"SLObject", typeAndMethodObject.getK1(), true);
		final Pair<JavaType, JavaMethod> typeAndMethodObject2 = this
				.createMethod("org.openspotlight", "SLObject2", "toString",
						"toString()");
		final JavaType typeSLObject3 = createType("org.openspotlight",
				"SLObject3", typeAndMethodObject2.getK1(), true);
		final JavaType typeSLObject4 = createType("org.openspotlight",
				"SLObject4", typeSLObject3, true);
		final Pair<JavaType, JavaMethod> typeAndMethodObject5 = this
				.createMethod("org.openspotlight", "SLObject5", "toString",
						"toString()");

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(typeAndMethodObject5.getK1(), "toString");

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(typeAndMethodObject5.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveMethodOnParentFiveLevelsFoundOnThirdLevel()
			throws Exception {
		final Pair<JavaType, JavaMethod> typeObject = this.createMethod(
				"java.lang", "Object", "toString", "toString()");
		createType("org.openspotlight", "SLObject", typeObject.getK1(), true);
		final Pair<JavaType, JavaMethod> typeAndMethodObject2 = this
				.createMethod("org.openspotlight", "SLObject2", "toString",
						"toString()");
		final JavaType typeSLObject3 = createType("org.openspotlight",
				"SLObject3", typeAndMethodObject2.getK1(), false);
		final JavaType typeSLObject4 = createType("org.openspotlight",
				"SLObject4", typeSLObject3, false);
		final JavaType typeSLObject5 = createType("org.openspotlight",
				"SLObject5", typeSLObject4, false);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(typeSLObject5, "toString");

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(typeAndMethodObject2.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveMethodOnParentSecondLevel() throws Exception {
		final Pair<JavaType, JavaMethod> typeAndMethodObject = this
				.createMethod("java.lang", "Object", "toString", "toString()");
		final JavaType typeSLObject = createType("org.openspotlight",
				"SLObject", typeAndMethodObject.getK1(), true);
		final JavaType typeSLObject2 = createType("org.openspotlight",
				"SLObject2", typeSLObject, true);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(typeSLObject2, "toString");

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(), is(typeAndMethodObject.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void resolveSimpleMethod() throws Exception {
		final Pair<JavaType, JavaMethod> typeAndMethod = this.createMethod(
				"java.lang", "Object", "toString", "toString()");

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(typeAndMethod.getK1(), "toString");

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(), is(typeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
	}

	@Test()
	public void testCache() throws Exception {
		final JavaType objectType = createType("java.lang", "Object", null,
				false);
		final JavaType stringType = createType("java.lang", "String",
				objectType, true);
		final JavaType numberType = createType("java.lang", "Number",
				objectType, true);
		final JavaType integerType = createType("java.lang", "Integer",
				numberType, true);
		final JavaType longType = createType("java.lang", "Long", numberType,
				true);
		final JavaTypePrimitive longTypePrimitive = createPrimitiveType("long");

		final List<JavaType> parameterList = new LinkedList<JavaType>();
		parameterList.add(integerType);
		parameterList.add(stringType);
		parameterList.add(longTypePrimitive);

		this.createMethod("java.lang", "Object", "wait", "wait()");
		this.createMethod(objectType, "wait",
				"wait(java.lang.Number, java.lang.String, java.lang.Long)",
				numberType, stringType, longType);

		final Pair<JavaType, JavaMethod> correctTypeAndMethod = this
				.createMethod(objectType, "wait",
						"wait(java.lang.Integer, java.lang.String, long)",
						numberType, stringType, longTypePrimitive);

		final JavaMethodMethod foundMethod = (JavaMethodMethod) methodResolver
				.getMethod(integerType, "wait", parameterList);
		final String uniqueId = methodResolver.getUniqueId(integerType, "wait",
				parameterList);

		assertThat(foundMethod, is(notNullValue()));
		assertThat(foundMethod.getID(),
				is(correctTypeAndMethod.getK2().getID()));
		assertThat(foundMethod.getContext(), is(not(abstractContex)));
		assertThat(methodResolver.getCache().get(uniqueId), is(notNullValue()));
		assertThat(methodResolver.getCache().get(uniqueId), is(foundMethod
				.getID()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidParams() throws Exception {
		this.createMethod("java.lang", "Object", "toString", "toString()");
		final JavaType abstractType = getAbstractType("java.lang", "Object");

		methodResolver.getMethod(abstractType, "toString");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidParamsNullParams() throws Exception {
		methodResolver.getMethod(null, null);
	}
}
