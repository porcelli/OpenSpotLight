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
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeEvent;
import org.openspotlight.federation.data.InstanceMetadata.ItemChangeType;
import org.openspotlight.federation.data.InstanceMetadata.ItemEventListener;
import org.openspotlight.federation.data.InstanceMetadata.PropertyValue;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.impl.Project;
import org.openspotlight.federation.data.impl.Repository;

/**
 * Test for class {@link AbstractConfigurationNode} and
 * {@link ConfigurationNodeMetadata}
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class AbstractNodeTest extends NodeTest {
    
    private ItemChangeEvent<PropertyValue> lastPropertyChange;
    
    private ItemChangeEvent<ConfigurationNode> lastNodeChange;
    
    private Configuration createGroupWithListeners() {
        final Configuration configuration = this.createSampleData();
        configuration.getInstanceMetadata().getSharedData().markAsSaved();
        configuration.getInstanceMetadata().getSharedData()
                .addPropertyListener(new ItemEventListener<PropertyValue>() {
                    public void changeEventHappened(
                            final ItemChangeEvent<PropertyValue> event) {
                        AbstractNodeTest.this.setLastPropertyChange(event);
                    }
                });
        configuration.getInstanceMetadata().getSharedData().addNodeListener(
                new ItemEventListener<ConfigurationNode>() {
                    public void changeEventHappened(
                            final ItemChangeEvent<ConfigurationNode> event) {
                        AbstractNodeTest.this.setLastNodeChange(event);
                    }
                });
        return configuration;
    }
    
    @Before
    public void releaseChanges() {
        this.lastNodeChange = null;
        this.lastPropertyChange = null;
    }
    
    private void setLastNodeChange(
            final ItemChangeEvent<ConfigurationNode> value) {
        this.lastNodeChange = value;
    }
    
    private void setLastPropertyChange(
            final ItemChangeEvent<PropertyValue> value) {
        this.lastPropertyChange = value;
    }
    
    @Test
    public void shouldEqualsAndHashCodeWorkOk() {
        final Configuration configuration = new Configuration();
        final Configuration group1 = new Configuration();
        assertThat(configuration.equals(group1), is(true));
        assertThat(configuration.hashCode(), is(group1.hashCode()));
        assertThat(configuration.compareTo(group1), is(0));
        final Repository rep = new Repository(configuration, "a");
        final Repository rep1 = new Repository(configuration, "b");
        assertThat(rep.equals(rep1), is(false));
        assertThat(rep.hashCode(), is(not(rep1.hashCode())));
        assertThat(rep.compareTo(rep1), is(not(0)));
    }
    
    @Test
    public void shouldListenChangesOnNodes() throws Exception {
        final Configuration configuration = this.createGroupWithListeners();
        final Repository repository = configuration.getRepositoryByName("r-1");
        final Project newProject = new Project(repository, "newProject");
        assertThat(this.lastNodeChange.getType(), is(ItemChangeType.ADDED));
        assertThat((Project) this.lastNodeChange.getNewItem(), is(newProject));
        assertThat(this.lastNodeChange.getOldItem(), is(nullValue()));
        this.lastNodeChange = null;
        new Project(repository, "newProject");
        assertThat(this.lastNodeChange, is(nullValue()));
        final Bundle newBundle = new Bundle(newProject, "newBundle");
        assertThat((Bundle) this.lastNodeChange.getNewItem(), is(newBundle));
        repository.removeProject(newProject);
        assertThat(this.lastNodeChange.getType(), is(ItemChangeType.EXCLUDED));
        assertThat(this.lastNodeChange.getNewItem(), is(nullValue()));
        assertThat((Project) this.lastNodeChange.getOldItem(), is(newProject));
        assertThat(configuration.getInstanceMetadata().getSharedData()
                .isDirty(), is(true));
        assertThat(repository.getInstanceMetadata().getSharedData()
                .getNodeChangesSinceLastSave().size(), is(3));
        configuration.getInstanceMetadata().getSharedData().markAsSaved();
        assertThat(repository.getInstanceMetadata().getSharedData().isDirty(),
                is(false));
        assertThat(repository.getInstanceMetadata().getSharedData()
                .getNodeChangesSinceLastSave().size(), is(0));
    }
    
    @Test
    public void shouldListenChangesOnProperties() throws Exception {
        final Configuration configuration = this.createGroupWithListeners();
        assertThat(configuration.getInstanceMetadata().getSharedData()
                .isDirty(), is(false));
        final Repository repository = configuration.getRepositoryByName("r-1");
        repository.setActive(false);
        assertThat(this.lastPropertyChange.getType(),
                is(ItemChangeType.CHANGED));
        assertThat(this.lastPropertyChange.getNewItem().getPropertyName(),
                is("active"));
        assertThat(
                (Repository) this.lastPropertyChange.getNewItem().getOwner(),
                is(repository));
        assertThat((Boolean) this.lastPropertyChange.getNewItem()
                .getPropertyValue(), is(false));
        assertThat((Boolean) this.lastPropertyChange.getOldItem()
                .getPropertyValue(), is(true));
        this.lastPropertyChange = null;
        repository.setActive(false);
        assertThat(this.lastPropertyChange, is(nullValue()));
        repository.setNumberOfParallelThreads(null);
        assertThat(this.lastPropertyChange.getType(),
                is(ItemChangeType.EXCLUDED));
        assertThat(this.lastPropertyChange.getNewItem().getPropertyValue(),
                is(nullValue()));
        repository.setNumberOfParallelThreads(1);
        assertThat(this.lastPropertyChange.getType(), is(ItemChangeType.ADDED));
        assertThat((Integer) this.lastPropertyChange.getNewItem()
                .getPropertyValue(), is(1));
        assertThat(configuration.getInstanceMetadata().getSharedData()
                .isDirty(), is(true));
        assertThat(repository.getInstanceMetadata().getSharedData()
                .getPropertyChangesSinceLastSave().size(), is(3));
        configuration.getInstanceMetadata().getSharedData().markAsSaved();
        assertThat(repository.getInstanceMetadata().getSharedData().isDirty(),
                is(false));
        assertThat(repository.getInstanceMetadata().getSharedData()
                .getPropertyChangesSinceLastSave().size(), is(0));
    }
    
}
