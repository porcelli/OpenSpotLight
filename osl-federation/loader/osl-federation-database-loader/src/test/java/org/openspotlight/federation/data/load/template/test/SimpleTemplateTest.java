package org.openspotlight.federation.data.load.template.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.junit.Test;
import org.openspotlight.federation.template.CustomizedStringTemplate;

@SuppressWarnings("all")
public class SimpleTemplateTest {

	@Test
	public void shouldCreateMultiLineFromTemplate() throws Exception {
		final CustomizedStringTemplate template = new CustomizedStringTemplate(
				"$detail:{$it.text$\n}$", DefaultTemplateLexer.class);
		template.setAttributeArray("detail.{text}", "line 1");
		template.setAttributeArray("detail.{text}", "line 2");
		template.setAttributeArray("detail.{text}", "line 3");
		template.setAttributeArray("detail.{text}", "line 4");
		template.setAttributeArray("detail.{text}", "line 5");
		assertThat(template.toString().replaceAll("\r", ""),
				is("line 1\nline 2\nline 3\nline 4\nline 5\n"));
	}

	@Test
	public void shouldLoadTemplates() throws Exception {
		final StringTemplate hello = new StringTemplate("Hello $name$",
				DefaultTemplateLexer.class);
		hello.setAttribute("name", "World");
		assertThat(hello.toString(), is("Hello World"));

	}

}
