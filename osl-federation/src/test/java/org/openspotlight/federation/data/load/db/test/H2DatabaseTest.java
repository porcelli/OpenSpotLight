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
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;

import java.sql.CallableStatement;
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
public class H2DatabaseTest extends AbstractDatabaseMetadataLoaderTest {
    
    protected void assertExampleProcedureWorks() throws SQLException {
        StaticFunctions.procedureCalled = false;
        final CallableStatement procedureStatement = this.connection
                .prepareCall(this.callExampleProcedure());
        procedureStatement.setInt(1, this.getProcedureFirstInParameter());
        procedureStatement.setInt(2, this.getProcedureSecondInParameter());
        procedureStatement.execute();
        assertThat(StaticFunctions.procedureCalled, is(true));
    }
    
    protected String callExampleFunction() {
        return Messages.getString("H2DatabaseTest.callFunction"); //$NON-NLS-1$
    }
    
    protected String callExampleProcedure() {
        return Messages.getString("H2DatabaseTest.callProcedure"); //$NON-NLS-1$
    }
    
    protected void fireExampleTrigger() throws SQLException {
        this.insertExampleData();
    }
    
    @Override
    protected DatabaseType getDefaultType() {
        return DatabaseType.H2;
    }
    
    protected String getDriver() {
        return Messages.getString("H2DatabaseTest.driverClass"); //$NON-NLS-1$
        
    }
    
    @Override
    protected int getExampleColumnValueAfterTrigger() {
        return 0; // the H2 triggers is not working
    }
    
    protected String getExampleFunctionCreation() {
        return Messages.getString("H2DatabaseTest.createFunction"); //$NON-NLS-1$
    }
    
    protected String getExampleIndexCreation() {
        return Messages.getString("H2DatabaseTest.createIndex"); //$NON-NLS-1$
    }
    
    protected String getExampleProcedureCreation() {
        return Messages.getString("H2DatabaseTest.createProcedure"); //$NON-NLS-1$
    }
    
    protected String getExampleTableCreation() {
        return Messages.getString("H2DatabaseTest.createTable"); //$NON-NLS-1$
    }
    
    protected String getExampleTableSelect() {
        return Messages.getString("H2DatabaseTest.selectTableData"); //$NON-NLS-1$
    }
    
    protected String getExampleTriggerCreation() {
        return Messages.getString("H2DatabaseTest.createTrigger"); //$NON-NLS-1$
    }
    
    protected String getExampleViewCreation() {
        return Messages.getString("H2DatabaseTest.createView"); //$NON-NLS-1$
    }
    
    protected String getExampleViewSelect() {
        return Messages.getString("H2DatabaseTest.selectView"); //$NON-NLS-1$
    }
    
    protected String getInsertExample() {
        return Messages.getString("H2DatabaseTest.insert"); //$NON-NLS-1$
    }
    
    @Override
    protected String getPassword() {
        return Messages.getString("H2DatabaseTest.password"); //$NON-NLS-1$
    }
    
    protected String getTriggerSelect() {
        return Messages.getString("H2DatabaseTest.selectTriggerColumn"); //$NON-NLS-1$
    }
    
    protected String getUrl() {
        return Messages.getString("H2DatabaseTest.url"); //$NON-NLS-1$
    }
    
    @Override
    protected String getUser() {
        return Messages.getString("H2DatabaseTest.user"); //$NON-NLS-1$
    }
    
    @Override
    protected void setup() throws Exception {
        delete("./target/test-data/H2DatabaseTest/h2/");
    }
}
