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

import static org.openspotlight.common.util.Files.delete;

import java.sql.SQLException;

import org.openspotlight.federation.data.load.db.DatabaseMetadataScriptManager;
import org.openspotlight.federation.data.load.db.DatabaseType;

/**
 * Test class for {@link DatabaseMetadataScriptManager}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class DerbyDatabaseTest extends AbstractDatabaseTest {
    
    private static String DERBY_DATA_PATH = "./target/test-data/DerbyDatabaseTest/derby/"; //$NON-NLS-1$
    
    protected String callExampleFunction() {
        return Messages.getString("DerbyDatabaseTest.callFunction"); //$NON-NLS-1$
    }
    
    protected String callExampleProcedure() {
        return Messages.getString("DerbyDatabaseTest.callProcedure"); //$NON-NLS-1$
    }
    
    protected void fireExampleTrigger() throws SQLException {
        this.insertExampleData();
    }
    
    @Override
    protected DatabaseType getDefaultType() {
        return null;
    }
    
    protected String getDriver() {
        return Messages.getString("DerbyDatabaseTest.driverClass"); //$NON-NLS-1$
        
    }
    
    protected String getExampleFunctionCreation() {
        return Messages.getString("DerbyDatabaseTest.createFunction"); //$NON-NLS-1$
    }
    
    protected String getExampleIndexCreation() {
        return Messages.getString("DerbyDatabaseTest.createIndex"); //$NON-NLS-1$
    }
    
    protected String getExampleProcedureCreation() {
        return Messages.getString("DerbyDatabaseTest.createProcedure"); //$NON-NLS-1$
    }
    
    protected String getExampleTableCreation() {
        return Messages.getString("DerbyDatabaseTest.createTable"); //$NON-NLS-1$
    }
    
    protected String getExampleTableSelect() {
        return Messages.getString("DerbyDatabaseTest.selectTableData"); //$NON-NLS-1$
    }
    
    protected String getExampleTriggerCreation() {
        return Messages.getString("DerbyDatabaseTest.createTrigger"); //$NON-NLS-1$
    }
    
    protected String getExampleViewCreation() {
        return Messages.getString("DerbyDatabaseTest.createView"); //$NON-NLS-1$
    }
    
    protected String getExampleViewSelect() {
        return Messages.getString("DerbyDatabaseTest.selectView"); //$NON-NLS-1$
    }
    
    protected String getInsertExample() {
        return Messages.getString("DerbyDatabaseTest.insert"); //$NON-NLS-1$
    }
    
    protected String getTriggerSelect() {
        return Messages.getString("DerbyDatabaseTest.selectTriggerColumn"); //$NON-NLS-1$
    }
    
    protected String getUrl() {
        return Messages.getString("DerbyDatabaseTest.url"); //$NON-NLS-1$
    }
    
    protected void setup() throws Exception {
        delete(DERBY_DATA_PATH);
        System.setProperty("derby.system.home", DERBY_DATA_PATH); //$NON-NLS-1$
    }
    
}
