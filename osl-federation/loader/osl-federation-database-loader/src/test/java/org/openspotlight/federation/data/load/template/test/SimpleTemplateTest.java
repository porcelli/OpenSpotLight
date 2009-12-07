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
