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

package org.openspotlight.federation.data.load.db.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.EnumMap;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;
import org.junit.Test;
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.load.db.ColumnsNamesForMetadataSelect;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.ScriptType;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript.PreferedType;
import org.openspotlight.federation.data.load.db.handler.PostgresRoutineStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

/**
 * Test class for {@link DatabaseMetadataScriptManager}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class DatabaseMetadataScriptManagerTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private String createSample() {
		final XStream xstream = new XStream();
		xstream.omitField(DatabaseMetadataScript.class, "immutable");
		xstream.alias("script", DatabaseMetadataScript.class); //$NON-NLS-1$
		xstream.alias("column", ColumnsNamesForMetadataSelect.class);
		final DatabaseMetadataScript script = new DatabaseMetadataScript();
		script.setContentSelect("select text from table");
		script.setPreferedType(PreferedType.SQL);
		script.setDataSelect("select * from data");
		script
				.setTemplatesSelect("select sysdate from dual where $dummy$ = 'X'");
		script.setTemplate("create table $dummy$ ");
		script.setDatabase(DatabaseType.ORACLE);
		script.setScriptType(ScriptType.TABLE);
		script.setStreamHandlerClass(PostgresRoutineStreamHandler.class);
		final EnumMap<ColumnsNamesForMetadataSelect, String> columnAliasMap = new EnumMap<ColumnsNamesForMetadataSelect, String>(
				ColumnsNamesForMetadataSelect.class);
		columnAliasMap.put(ColumnsNamesForMetadataSelect.catalog_name,
				"newName");
		script.setColumnAliasMap(columnAliasMap);
		return xstream.toXML(script);
	}

	@Test
	public void shouldLoadScript() throws Exception {
		final DatabaseMetadataScript script = DatabaseMetadataScriptManager.INSTANCE
				.getScript(DatabaseType.H2, ScriptType.FUNCTION);
		assertThat(script, is(notNullValue()));
	}

	@Test
	public void shouldLogValidXmlFromXStream() {
		System.out.println("valid xml config script: \n" + this.createSample());
	}

	@Test
	public void shouldReplaceTemplateInACorrectWay() throws Exception {
		final DatabaseMetadataScript tableScript = DatabaseMetadataScriptManager.INSTANCE
				.getScript(DatabaseType.MYSQL, ScriptType.TABLE);
		final StringTemplate template = new StringTemplate(tableScript
				.getTemplate(), DefaultTemplateLexer.class);
		template.setAttribute("name", "example_table");
		for (int i = 0; i < 10; i++) {
			template.setAttribute("detail.{column_name,column_type,is_null}",
					"column" + i, "type" + i, "isNull" + i);
		}
		final String result = template.toString();
		assertThat(result, is(notNullValue()));
	}

}
