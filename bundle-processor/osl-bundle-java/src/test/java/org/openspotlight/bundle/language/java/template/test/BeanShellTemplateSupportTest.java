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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanShellTemplateSupportTest {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void shouldCreateScriptFromJarInformation() throws Exception {
		final CompiledTypesExtractor extractor = new CompiledTypesExtractor();
		final List<TypeDefinition> javaTypes = extractor.getJavaTypes(
				new FileInputStream(
						"src/test/resources/dynamo-file-gen-1.0.1.jar"),
				"/lalala/dynamo-file-gen-1.0.1.jar");
		final String beanShellScript = BeanShellTemplateSupport
				.createBeanShellScriptToImpotJar("contextName",
						"contextVersion", javaTypes);
		Assert.assertThat(beanShellScript, Is.is(IsNull.notNullValue()));
		Assert.assertThat(Strings.isEmpty(beanShellScript), Is.is(false));
		logger.info("script:");
		logger.info(beanShellScript);

	}

}
