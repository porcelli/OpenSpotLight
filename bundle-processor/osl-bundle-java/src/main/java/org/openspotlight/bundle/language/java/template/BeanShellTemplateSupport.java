/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
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
 * This helper class creates bean shell script under a string to be used to import initial data from a jar file.
 * 
 * @author feu
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
                                                          final List<TypeDefinition> scannedTypes ) {
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
     * creates a Xml {@link InputSource} to be passed to the freemarker template engine.
     * 
     * @param contextName
     * @param contextVersion
     * @param scannedTypes
     * @return
     * @throws IOException
     */
    private static InputSource createXml( final List<TypeDefinition> scannedTypes )
            throws IOException {
        final TypeDefinitionSet wrapper = new TypeDefinitionSet();
        wrapper.setTypes(scannedTypes);
        final XStream xstream = new XStream();
        xstream.aliasPackage("",
                             "org.openspotlight.bundle.language.java.asm.model");
        xstream.alias("List", LinkedList.class);

        xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()) {
            @Override
            @SuppressWarnings( "unchecked" )
            public boolean canConvert( final Class type ) {
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
