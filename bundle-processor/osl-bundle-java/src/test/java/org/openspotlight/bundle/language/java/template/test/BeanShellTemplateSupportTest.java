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
package org.openspotlight.bundle.language.java.template.test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.List;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.bundle.language.java.JavaConstants;
import org.openspotlight.bundle.language.java.asm.CompiledTypesExtractor;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinition;
import org.openspotlight.bundle.language.java.resolver.JavaGraphNodeSupport;
import org.openspotlight.bundle.language.java.template.BeanShellTemplateSupport;
import org.openspotlight.common.util.Strings;
import org.openspotlight.federation.context.DefaultExecutionContextFactory;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.graph.SLContext;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;

import bsh.Interpreter;

public class BeanShellTemplateSupportTest {

    public static void main( final String... args ) {
        Interpreter.main(args);
    }

    private Repository repository = new Repository();
    {
        repository.setActive(true);
        repository.setName("name");
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
        final ExecutionContext context = contextFactory.createExecutionContext(
                                                                               "user", "pass", DefaultJcrDescriptor.TEMP_DESCRIPTOR,
                                                                               repository);
        interpreter.set("session", context.getGraphSession());
        interpreter.set("currentContextName", "exampleContext");
        SLContext exampleContext = context.getGraphSession().getContext(
                                                                        "exampleContext");
        Assert.assertThat(exampleContext, Is.is(IsNull.nullValue()));
        final JavaGraphNodeSupport helper = new JavaGraphNodeSupport(context
                                                                            .getGraphSession(), context.getGraphSession().createContext(
                                                                                                                                        "exampleContext").getRootNode(), context.getGraphSession()
                                                                                                                                                                                .createContext(JavaConstants.ABSTRACT_CONTEXT).getRootNode());
        interpreter.set("helper", helper);
        final BufferedReader reader = new BufferedReader(new StringReader(
                                                                          beanShellScript));
        String line;
        while ((line = reader.readLine()) != null) {
            interpreter.eval(line);
        }
        exampleContext = context.getGraphSession().getContext("exampleContext");
        Assert.assertThat(exampleContext, Is.is(IsNull.notNullValue()));
    }

}
