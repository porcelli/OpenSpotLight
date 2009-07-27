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

import static java.lang.Class.forName;
import static java.sql.DriverManager.getConnection;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.data.load.db.DatabaseType;

/**
 * This test class is used to set up a database for metadata tests.
 * 
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public abstract class AbstractDatabaseTest {
    
    protected Connection connection;
    
    private void assertExampleDataExists() throws SQLException {
        final PreparedStatement selectStatement = this.connection
                .prepareStatement(this.getExampleTableSelect());
        final ResultSet selectResult = selectStatement.executeQuery();
        assertThat(selectResult.next(), is(true));
        selectResult.close();
    }
    
    private void assertExampleDataExistsInView() throws SQLException {
        final PreparedStatement selectStatement = this.connection
                .prepareStatement(this.getExampleViewSelect());
        final ResultSet selectResult = selectStatement.executeQuery();
        assertThat(selectResult.next(), is(true));
        selectResult.close();
    }
    
    private void assertExampleFunctionWorks() throws SQLException {
        final PreparedStatement selectStatement = this.connection
                .prepareStatement(this.callExampleFunction());
        selectStatement.setInt(1, this.getFunctionFirstParameter());
        final ResultSet selectResult = selectStatement.executeQuery();
        assertThat(selectResult.next(), is(true));
        assertThat(selectResult.getInt(1), is(this.getFunctionExpectedResult()));
    }
    
    protected void assertExampleProcedureWorks() throws SQLException {
        final CallableStatement procedureStatement = this.connection
                .prepareCall(this.callExampleProcedure());
        procedureStatement.setInt(1, this.getProcedureFirstInParameter());
        procedureStatement.setInt(2, this.getProcedureSecondInParameter());
        procedureStatement.registerOutParameter(3, Types.INTEGER);
        procedureStatement.execute();
        final int procedureOutParam = procedureStatement.getInt(3);
        assertThat(procedureOutParam, is(this
                .getProcedureExpectedResultOn3rdParameter()));
        
    }
    
    private void assertTriggerWorks() throws SQLException {
        this.fireExampleTrigger();
        final PreparedStatement selectStatement = this.connection
                .prepareStatement(this.getTriggerSelect());
        final ResultSet selectResult = selectStatement.executeQuery();
        assertThat(selectResult.next(), is(true));
        assertThat(selectResult.getInt(1), is(this
                .getExampleColumnValueAfterTrigger()));
        selectResult.close();
    }
    
    protected abstract String callExampleFunction();
    
    protected abstract String callExampleProcedure();
    
    @After
    public void closeConnection() throws Exception {
        this.tearDown();
        if ((this.connection != null) && !this.connection.isClosed()) {
            this.connection.close();
        }
        
    }
    
    @Before
    public void createConnection() throws Exception {
        if (this.connection == null) {
            this.setup();
            
            forName(this.getDriver());
            if (this.getUser() == null) {
                this.connection = getConnection(this.getUrl());
            } else {
                this.connection = getConnection(this.getUrl(), this.getUser(),
                        this.getPassword());
            }
            this.connection.setAutoCommit(true);
        }
    }
    
    private void createExampleIndex() throws SQLException {
        final PreparedStatement createIndexStatement = this.connection
                .prepareStatement(this.getExampleIndexCreation());
        final boolean createResult = createIndexStatement.execute();
        assertThat(createResult, is(false));
    }
    
    private void createExampleTable() throws SQLException {
        final PreparedStatement createTableStatement = this.connection
                .prepareStatement(this.getExampleTableCreation());
        final boolean createResult = createTableStatement.execute();
        assertThat(createResult, is(false));
    }
    
    private void createExampleView() throws SQLException {
        final PreparedStatement createViewStatement = this.connection
                .prepareStatement(this.getExampleViewCreation());
        final boolean createResult = createViewStatement.execute();
        assertThat(createResult, is(false));
    }
    
    private void createFunction() throws SQLException {
        final PreparedStatement createFunctionStatement = this.connection
                .prepareStatement(this.getExampleFunctionCreation());
        final boolean createResult = createFunctionStatement.execute();
        assertThat(createResult, is(false));
    }
    
    private void createProcedure() throws SQLException {
        final PreparedStatement createProcedureStatement = this.connection
                .prepareStatement(this.getExampleProcedureCreation());
        final boolean createResult = createProcedureStatement.execute();
        assertThat(createResult, is(false));
    }
    
    private void createTrigger() throws SQLException {
        final PreparedStatement createTriggerStatement = this.connection
                .prepareStatement(this.getExampleTriggerCreation());
        final boolean createResult = createTriggerStatement.execute();
        assertThat(createResult, is(false));
    }
    
    protected abstract void fireExampleTrigger() throws SQLException;
    
    protected float get3rdInsertValue() {
        return 3;
    }
    
    protected double get4rdInsertValue() {
        return 4;
    }
    
    protected String get5rdInsertValue() {
        return "5";
    }
    
    protected abstract DatabaseType getDefaultType();
    
    protected abstract String getDriver();
    
    protected int getExampleColumnValueAfterTrigger() {
        return 3;
    }
    
    protected abstract String getExampleFunctionCreation();
    
    protected abstract String getExampleIndexCreation();
    
    protected abstract String getExampleProcedureCreation();
    
    protected abstract String getExampleTableCreation();
    
    protected abstract String getExampleTableSelect();
    
    protected abstract String getExampleTriggerCreation();
    
    protected abstract String getExampleViewCreation();
    
    protected abstract String getExampleViewSelect();
    
    protected int getFirstInsertValue() {
        return 1;
    }
    
    protected int getFunctionExpectedResult() {
        return 2;
    }
    
    protected int getFunctionFirstParameter() {
        return 1;
    }
    
    protected abstract String getInsertExample();
    
    protected String getPassword() {
        return null;
    }
    
    protected int getProcedureExpectedResultOn3rdParameter() {
        return 4;
    }
    
    protected int getProcedureFirstInParameter() {
        return 2;
    }
    
    protected int getProcedureSecondInParameter() {
        return 2;
    }
    
    protected short getSecondInsertValue() {
        return 2;
    }
    
    protected abstract String getTriggerSelect();
    
    protected abstract String getUrl();
    
    protected String getUser() {
        return null;
    }
    
    protected final void insertExampleData() throws SQLException {
        final PreparedStatement insertStatement = this.connection
                .prepareStatement(this.getInsertExample());
        insertStatement.setInt(1, this.getFirstInsertValue());
        insertStatement.setShort(2, this.getSecondInsertValue());
        insertStatement.setFloat(3, this.get3rdInsertValue());
        insertStatement.setDouble(4, this.get4rdInsertValue());
        insertStatement.setString(5, this.get5rdInsertValue());
        final int insertResult = insertStatement.executeUpdate();
        assertThat(insertResult, is(1));
    }
    
    protected void setup() throws Exception {
        
    }
    
    @Test
    public void shouldPrepareItems() throws Exception {
        
        assertThat(this.connection.isClosed(), is(false));
        
        this.createExampleTable();
        
        this.createExampleIndex();
        
        this.insertExampleData();
        
        this.assertExampleDataExists();
        
        this.createExampleView();
        
        this.assertExampleDataExistsInView();
        
        this.createFunction();
        
        this.assertExampleFunctionWorks();
        
        this.createProcedure();
        
        this.assertExampleProcedureWorks();
        
        this.createTrigger();
        
        this.assertTriggerWorks();
        this.connection.commit();
        
    }
    
    @After
    public void tearDown() throws Exception {
        this.connection.close();
    }
}
