package org.openspotlight.federation.data.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.federation.data.AbstractConfigurationNode;
import org.openspotlight.federation.data.Bundle;
import org.openspotlight.federation.data.Configuration;
import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.Project;
import org.openspotlight.federation.data.Repository;
import org.openspotlight.federation.data.ConfigurationNode.ItemChangeEvent;
import org.openspotlight.federation.data.ConfigurationNode.ItemChangeType;
import org.openspotlight.federation.data.ConfigurationNode.ItemEventListener;
import org.openspotlight.federation.data.ConfigurationNode.PropertyValue;

/**
 * Test for class {@link AbstractConfigurationNode} and {@link ConfigurationNode}
 * 
 * @author feu
 * 
 */
public class AbstractNodeTest extends NodeTest {

	@Test
	public void shouldEqualsAndHashCodeWorkOk() {
		Configuration configuration = new Configuration();
		Configuration group1 = new Configuration();
		assertThat(configuration.equals(group1), is(true));
		assertThat(configuration.hashCode(), is(group1.hashCode()));
		assertThat(configuration.compareTo(group1), is(0));
		Repository rep = new Repository("a", configuration);
		Repository rep1 = new Repository("b", configuration);
		assertThat(rep.equals(rep1), is(false));
		assertThat(rep.hashCode(), is(not(rep1.hashCode())));
		assertThat(rep.compareTo(rep1), is(not(0)));
	}

	@Before
	public void releaseChanges() {
		this.lastNodeChange = null;
		this.lastPropertyChange = null;
	}

	private ItemChangeEvent<PropertyValue> lastPropertyChange;

	private ItemChangeEvent<ConfigurationNode> lastNodeChange;

	private void setLastPropertyChange(ItemChangeEvent<PropertyValue> value) {
		this.lastPropertyChange = value;
	}

	private void setLastNodeChange(ItemChangeEvent<ConfigurationNode> value) {
		this.lastNodeChange = value;
	}

	private Configuration createGroupWithListeners() {
		Configuration configuration = createSampleData();
		configuration.markAsSaved();
		configuration.addPropertyListener(new ItemEventListener<PropertyValue>() {
			public void changeEventHappened(ItemChangeEvent<PropertyValue> event) {
				setLastPropertyChange(event);
			}
		});
		configuration.addNodeListener(new ItemEventListener<ConfigurationNode>() {
			public void changeEventHappened(ItemChangeEvent<ConfigurationNode> event) {
				setLastNodeChange(event);
			}
		});
		return configuration;
	}

	@Test
	public void shouldListenChangesOnProperties() throws Exception {
		Configuration configuration = createGroupWithListeners();
		assertThat(configuration.isDirty(), is(false));
		Repository repository = configuration.getRepositoryByName("r-1");
		repository.setActive(false);
		assertThat(lastPropertyChange.getType(), is(ItemChangeType.CHANGED));
		assertThat(lastPropertyChange.getNewItem().getPropertyName(),
				is("active"));
		assertThat((Repository) lastPropertyChange.getNewItem().getOwner(),
				is(repository));
		assertThat(
				(Boolean) lastPropertyChange.getNewItem().getPropertyValue(),
				is(false));
		assertThat(
				(Boolean) lastPropertyChange.getOldItem().getPropertyValue(),
				is(true));
		lastPropertyChange = null;
		repository.setActive(false);
		assertThat(lastPropertyChange, is(nullValue()));
		repository.setNumberOfParallelThreads(null);
		assertThat(lastPropertyChange.getType(), is(ItemChangeType.EXCLUDED));
		assertThat(lastPropertyChange.getNewItem().getPropertyValue(),
				is(nullValue()));
		repository.setNumberOfParallelThreads(1);
		assertThat(lastPropertyChange.getType(), is(ItemChangeType.ADDED));
		assertThat((Integer) lastPropertyChange.getNewItem().getPropertyValue(),
				is(1));
		assertThat(configuration.isDirty(), is(true));
		assertThat(repository.getPropertyChangesSinceLastSave().size(), is(3));
		configuration.markAsSaved();
		assertThat(repository.isDirty(), is(false));
		assertThat(repository.getPropertyChangesSinceLastSave().size(), is(0));
	}

	@Test
	public void shouldListenChangesOnNodes() throws Exception {
		Configuration configuration = createGroupWithListeners();
		Repository repository = configuration.getRepositoryByName("r-1");
		Project newProject = new Project("newProject", repository);
		assertThat(lastNodeChange.getType(), is(ItemChangeType.ADDED));
		assertThat((Project) lastNodeChange.getNewItem(), is(newProject));
		assertThat(lastNodeChange.getOldItem(), is(nullValue()));
		lastNodeChange = null;
		new Project("newProject", repository);
		assertThat(lastNodeChange, is(nullValue()));
		Bundle newBundle = new Bundle("newBundle", newProject);
		assertThat((Bundle) lastNodeChange.getNewItem(), is(newBundle));
		repository.removeProject(newProject);
		assertThat(lastNodeChange.getType(), is(ItemChangeType.EXCLUDED));
		assertThat(lastNodeChange.getNewItem(), is(nullValue()));
		assertThat((Project) lastNodeChange.getOldItem(), is(newProject));
		assertThat(configuration.isDirty(), is(true));
		assertThat(repository.getNodeChangesSinceLastSave().size(), is(3));
		configuration.markAsSaved();
		assertThat(repository.isDirty(), is(false));
		assertThat(repository.getNodeChangesSinceLastSave().size(), is(0));
	}

}
