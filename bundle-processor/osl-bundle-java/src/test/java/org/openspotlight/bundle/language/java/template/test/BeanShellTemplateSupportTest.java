package org.openspotlight.bundle.language.java.template.test;

import java.io.FileInputStream;
import java.util.List;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.bundle.language.java.asm.CompiledTypesExtractor;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinition;
import org.openspotlight.bundle.language.java.template.BeanShellTemplateSupport;
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.context.DefaultExecutionContextFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.graph.SLContext;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;

import bsh.Interpreter;

public class BeanShellTemplateSupportTest {

	public static void main(final String... args) {
		Interpreter.main(args);
	}

	@Test
	public void shouldCreateScriptFromJarInformation() throws Exception {
		final CompiledTypesExtractor extractor = new CompiledTypesExtractor();
		final List<TypeDefinition> javaTypes = extractor.getJavaTypes(
				new FileInputStream(
						"src/test/resources/dynamo-file-gen-1.0.1.jar"),
				"/lalala/dynamo-file-gen-1.0.1.jar");
		final String beanShellScript = BeanShellTemplateSupport
				.createBeanShellScriptToImpotJar(javaTypes);
		Assert.assertThat(beanShellScript, Is.is(IsNull.notNullValue()));
		Assert.assertThat(Strings.isEmpty(beanShellScript), Is.is(false));
		final Interpreter interpreter = new Interpreter();
		final ExecutionContextFactory contextFactory = DefaultExecutionContextFactory
				.createFactory();
		final ExecutionContext context = contextFactory
				.createExecutionContext("user", "pass",
						DefaultJcrDescriptor.TEMP_DESCRIPTOR, "example");
		interpreter.set("session", context.getGraphSession());
		interpreter.set("currentContextName", "exampleContext");
		SLContext exampleContext = context.getGraphSession().getContext(
				"exampleContext");
		Assert.assertThat(exampleContext, Is.is(IsNull.nullValue()));
		interpreter.eval(beanShellScript);
		exampleContext = context.getGraphSession().getContext("exampleContext");
		Assert.assertThat(exampleContext, Is.is(IsNull.notNullValue()));
	}

}
