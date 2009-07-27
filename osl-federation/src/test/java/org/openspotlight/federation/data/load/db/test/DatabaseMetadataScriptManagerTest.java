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

import java.util.ArrayList;

import org.junit.Test;
import org.openspotlight.federation.data.load.db.CustomTypeScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScript;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.DatabaseMetadataScripts;
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
        xstream.alias("scripts", DatabaseMetadataScripts.class); //$NON-NLS-1$
        xstream.alias("script", DatabaseMetadataScript.class); //$NON-NLS-1$
        xstream.alias("customType", CustomTypeScript.class); //$NON-NLS-1$
        final DatabaseMetadataScripts scripts = new DatabaseMetadataScripts();
        scripts.setScripts(new ArrayList<DatabaseMetadataScript>());
        final DatabaseMetadataScript script = new DatabaseMetadataScript();
        script.setFunctionCreationScript("create function");
        script.setIndexCreationScript("create index");
        script.setName("derby");
        script.setProcedureCreationScript("create proc");
        script.setTableCreationScript("create table");
        script.setTriggerCreationScript("create trigger");
        script.setViewCreationScript("create view");
        final DatabaseMetadataScript script1 = new DatabaseMetadataScript();
        script1.setFunctionCreationScript("create function");
        script1.setIndexCreationScript("create index");
        script1.setName("derby");
        script1.setProcedureCreationScript("create proc");
        script1.setTableCreationScript("create table");
        script1.setTriggerCreationScript("create trigger");
        script1.setViewCreationScript("create view");
        final CustomTypeScript custom = new CustomTypeScript();
        custom.setSql("create custom");
        final CustomTypeScript custom1 = new CustomTypeScript();
        custom1.setSql("create custom");
        script.setCustomTypeCreationScripts(new ArrayList<CustomTypeScript>());
        script.getCustomTypeCreationScripts().add(custom);
        script.getCustomTypeCreationScripts().add(custom1);
        scripts.getScripts().add(script);
        scripts.getScripts().add(script1);
        return xstream.toXML(scripts);
    }
    
    @Test
    public void shouldLoadScript() throws Exception {
        final DatabaseMetadataScripts scripts = DatabaseMetadataScriptManager.INSTANCE
                .getScripts();
        assertThat(scripts, is(notNullValue()));
        assertThat(scripts.getScripts().get(0), is(notNullValue()));
    }
    
    @Test
    public void shouldLogValidXmlFromXStream() {
        this.logger.info("valid xml config script: \n" + this.createSample());
    }
    
}
