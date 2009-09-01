package org.openspotlight.federation.template;

import java.util.HashMap;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 * This class is a "workarround" for setting attributes on a
 * {@link StringTemplate} using vargargs.
 * 
 * The behavior is quite the same, but it has a
 * {@link #setAttributeArray(String, Object...) new method with varargs}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class CustomizedStringTemplate extends StringTemplate {

	/**
	 * Method to set an attribute array with the arbitrary number of values.
	 * 
	 * @param name
	 *            formatted string such as "name{name1,name2}"
	 * @param values
	 *            some values for the names passed.
	 */
	public void setAttributeArray(String name, Object... values) {
		super.setAttribute(name, values);
	}

	/**
	 * {@inheritDoc}
	 */
	public CustomizedStringTemplate() {
	}

	/**
	 * {@inheritDoc}
	 */
	public CustomizedStringTemplate(String template, Class lexer) {
		super(template, lexer);
	}

	/**
	 * {@inheritDoc}
	 */
	public CustomizedStringTemplate(String template) {
		super(template);
	}

	/**
	 * {@inheritDoc}
	 */
	public CustomizedStringTemplate(StringTemplateGroup group, String template,
			HashMap attributes) {
		super(group, template, attributes);
	}

	/**
	 * {@inheritDoc}
	 */
	public CustomizedStringTemplate(StringTemplateGroup group, String template) {
		super(group, template);
	}

}
