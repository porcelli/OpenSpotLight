package org.openspotlight.bundle.language.java.template;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.openspotlight.bundle.language.java.asm.model.MethodDeclaration;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinition;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinitionSet;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.xml.sax.InputSource;

import template.ClassOnTemplatePath;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;

import dynamo.string.StringTool;
import freemarker.ext.dom.NodeModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

/**
 * This helper class creates bean shell script under a string to be used to
 * import initial data from a jar file.
 * 
 * @author feu
 * 
 */
public class BeanShellTemplateSupport {

	/**
	 * freemarker configuration
	 */
	private static Configuration cfg = new Configuration();
	static {
		cfg.setClassForTemplateLoading(ClassOnTemplatePath.class, "ftl");
		cfg.setObjectWrapper(new DefaultObjectWrapper());
	}

	/**
	 * returns a bean shell script to import a jar file information.
	 * 
	 * @param contextName
	 * @param contextVersion
	 * @param scannedTypes
	 * @return
	 */
	public static String createBeanShellScriptToImpotJar(
			final List<TypeDefinition> scannedTypes) {
		try {
			final InputSource source = createXml(scannedTypes);
			final Template temp = cfg.getTemplate("jar-import-script-base.ftl");
			final Map<String, Object> root = new HashMap<String, Object>();
			root.put("t", new StringTool());
			root.put("doc", NodeModel.parse(source));
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final Writer out = new OutputStreamWriter(baos, "UTF8");
			temp.process(root, out);
			out.flush();
			return new String(baos.toByteArray());
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}

	}

	/**
	 * creates a Xml {@link InputSource} to be passed to the freemarker template
	 * engine.
	 * 
	 * @param contextName
	 * @param contextVersion
	 * @param scannedTypes
	 * @return
	 * @throws IOException
	 */
	private static InputSource createXml(final List<TypeDefinition> scannedTypes)
			throws IOException {
		final TypeDefinitionSet wrapper = new TypeDefinitionSet();
		wrapper.setTypes(scannedTypes);
		final XStream xstream = new XStream();
		xstream.aliasPackage("",
				"org.openspotlight.bundle.language.java.asm.model");
		xstream.alias("List", LinkedList.class);

		xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()) {
			@Override
			@SuppressWarnings("unchecked")
			public boolean canConvert(final Class type) {
				return type.getName() == MethodDeclaration.class.getName();
			}
		});
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		xstream.toXML(wrapper, outputStream);
		outputStream.flush();
		outputStream.close();

		final byte[] contentAsBytes = outputStream.toByteArray();
		final InputSource source = new InputSource(new ByteArrayInputStream(
				contentAsBytes));
		return source;
	}

}
