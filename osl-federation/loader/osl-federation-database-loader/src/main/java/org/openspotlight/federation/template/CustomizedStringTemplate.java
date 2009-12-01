package org.openspotlight.federation.template;

import org.antlr.stringtemplate.StringTemplate;

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
	 * Constructor to call the super constructor
	 * {@link StringTemplate#StringTemplate(String, Class)}
	 * 
	 * @param template
	 * @param lexer
	 */
	public CustomizedStringTemplate(final String template, final Class<?> lexer) {
		super(template, lexer);
	}

	/**
	 * Method to set an attribute array with the arbitrary number of values.
	 * 
	 * @param name
	 *            formatted string such as "name{name1,name2}"
	 * @param values
	 *            some values for the names passed.
	 */
	public void setAttributeArray(final String name, final Object... values) {
		super.setAttribute(name, values);
	}

}
