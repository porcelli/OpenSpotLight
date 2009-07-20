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
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Repository;
import org.openspotlight.federation.data.load.ConfigurationManager.NodeClassHelper;

/**
 * Test for class {@link NodeClassHelper}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */

@SuppressWarnings("all")
public class NodeClassHelperTest {
    
    private NodeClassHelper nodeClassHelper;
    
    @Before
    public void createNodeHelper() {
        this.nodeClassHelper = new NodeClassHelper();
    }
    
    @Test
    public void shouldCreateInstance() throws Exception {
        final Configuration configuration = new Configuration();
        final Repository theSameRepository = new Repository("name",
                configuration);
        final Repository newRepository = this.nodeClassHelper.createInstance(
                "name", configuration, "osl:repository");
        assertThat(newRepository, is(theSameRepository));
        
    }
    
    @Test
    public void shouldCreateRootInstance() throws Exception {
        final Configuration theSameGroup = new Configuration();
        final Configuration newGroup = this.nodeClassHelper
                .createRootInstance("osl:configuration");
        assertThat(newGroup, is(theSameGroup));
    }
    
    @Test
    public void shouldGetNameFromNodeClass() throws Exception {
        final String name = this.nodeClassHelper
                .getNameFromNodeClass(Repository.class);
        assertThat(name, is("osl:repository"));
    }
    
    @Test
    public void shouldGetNodeClassFromName() throws Exception {
        final Class<? extends AbstractConfigurationNode> clazz = this.nodeClassHelper
                .getNodeClassFromName("osl:repository");
        assertThat(Repository.class.equals(clazz), is(true));
    }
    
    @Test(expected = ConfigurationException.class)
    public void shouldThrowExceptionWhenCreatingInstanceWithInvalidParameters()
            throws Exception {
        final Configuration configuration = new Configuration();
        this.nodeClassHelper.createInstance("name", configuration,
                "osl:invalidName");
    }
    
    @Test(expected = ConfigurationException.class)
    public void shouldThrowExceptionWhenCreatingRootInstanceWithInvalidParameters()
            throws Exception {
        this.nodeClassHelper.createRootInstance("osl:invalidName");
    }
    
    @Test(expected = ConfigurationException.class)
    public void shouldThrowExceptionWhenGetingNodeClassFromNameWithInvalidParameters()
            throws Exception {
        this.nodeClassHelper.getNodeClassFromName("osl:invalidName");
    }
    
}
