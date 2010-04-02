package org.openspotlight.bundle.language.java.asm.test;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.bundle.language.java.asm.CompiledTypesExtractor;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinition;

import java.io.FileInputStream;
import java.util.List;

public class CompiledTypeExtractorTest {

	@Test
	public void shouldExtractTypesFromInputStream() throws Exception {
		final CompiledTypesExtractor extractor = new CompiledTypesExtractor();
		final List<TypeDefinition> javaTypes = extractor.getJavaTypes(
				new FileInputStream(
						"src/test/resources/jboss-seam-2.1.1.GA.jar"),
				"/lalala/jboss-seam-2.1.1.GA.jar");
		Assert.assertThat(javaTypes, Is.is(IsNull.notNullValue()));
		Assert.assertThat(javaTypes.size(), Is.is(IsNot.not(0)));
	}

}
