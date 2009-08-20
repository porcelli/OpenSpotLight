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
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os 
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.federation.data.load.db.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;

import org.junit.Test;
import org.openspotlight.federation.data.impl.DatabaseType;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScripts;
import org.openspotlight.federation.data.load.db.ScriptType;
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
		xstream.omitField(DatabaseMetadataScripts.class, "immutable");
		xstream.alias("scripts", DatabaseMetadataScripts.class); //$NON-NLS-1$
		xstream.alias("script", DatabaseMetadataScript.class); //$NON-NLS-1$
		final DatabaseMetadataScripts scripts = new DatabaseMetadataScripts();
		scripts.setScripts(new ArrayList<DatabaseMetadataScript>());
		final DatabaseMetadataScript script = new DatabaseMetadataScript();
		script.setContentSelect("select text from table");
		script.setDataSelect("select * from data");
		script.setDatabase(DatabaseType.ORACLE);
		script.setScriptType(ScriptType.TABLE);
		final DatabaseMetadataScript script1 = new DatabaseMetadataScript();
		script1.setContentSelect("select text from table");
		script1.setDataSelect("select * from data");
		script1.setDatabase(DatabaseType.DB2);
		script1.setScriptType(ScriptType.FUNCTION);
		scripts.getScripts().add(script);
		scripts.getScripts().add(script1);
		return xstream.toXML(scripts);
	}

	@Test
	public void shouldLoadScript() throws Exception {
		DatabaseMetadataScript script = DatabaseMetadataScriptManager.INSTANCE
				.getScript(DatabaseType.H2, ScriptType.FUNCTION);
		assertThat(script, is(notNullValue()));
	}

	@Test
	public void shouldLogValidXmlFromXStream() {
		System.out.println("valid xml config script: \n" + this.createSample());
	}

}