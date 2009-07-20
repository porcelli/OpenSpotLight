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

package org.openspotlight.federation.data.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;

import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository;
import org.junit.After;
import org.junit.Before;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.JcrSessionConfigurationManager;

/**
 * Test class to see if the Jcr configuration is working ok. This test was based
 * on tests found on DNA project http://jboss.org/dna/
 * 
 * FIXME fix jcr implementation 
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */

@SuppressWarnings("all")
public class JcrSessionConfigurationManagerTest extends
        AbstractConfigurationManagerTest {
    
    public static final String TESTATA_PATH = "./src/test/resources/";
    public static final String JACKRABBIT_DATA_PATH = "./target/testdata/jackrabbittest/";
    public static final String REPOSITORY_DIRECTORY_PATH = JACKRABBIT_DATA_PATH
            + "repository";
    public static final String REPOSITORY_CONFIG_PATH = TESTATA_PATH
            + "jackrabbitDerbyTestRepositoryConfig.xml";
    public static final String DERBY_SYSTEM_HOME = JACKRABBIT_DATA_PATH
            + "/derby";
    
    private Session session;
    
    private TransientRepository repository;
    
    private ConfigurationManager implementation;
    
    @Override
    protected boolean assertAllData() {
        return true;
    }
    
    @Override
    protected ConfigurationManager createInstance() {
        return this.implementation;
    }
    
    @Before
    public void initializeSomeConfiguration() throws Exception {
        delete(JACKRABBIT_DATA_PATH);
        System.setProperty("derby.system.home", DERBY_SYSTEM_HOME);
        this.repository = new TransientRepository(REPOSITORY_CONFIG_PATH,
                REPOSITORY_DIRECTORY_PATH);
        final SimpleCredentials creds = new SimpleCredentials("jsmith",
                "password".toCharArray());
        this.session = this.repository.login(creds);
        assertThat(this.session, is(notNullValue()));
        this.implementation = new JcrSessionConfigurationManager(this.session);
    }
    
    @After
    public void shutdown() throws Exception {
        if (this.session != null) {
            this.session.logout();
        }
        if (this.repository != null) {
            this.repository.shutdown();
        }
    }
    
}
