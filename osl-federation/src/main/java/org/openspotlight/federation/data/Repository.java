package org.openspotlight.federation.data;

import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Repository extends AbstractConfigurationNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3606246260530743008L;

	public Repository(String name, Configuration configuration) {
		super(name, configuration, PROPERTY_TYPES);
	}

	private static final String NUMBER_OF_PARALLEL_THREADS = "numberOfParallelThreads";

	private static final String ACTIVE = "active";

	@SuppressWarnings("unchecked")
	private static final Map<String, Class<?>> PROPERTY_TYPES = map(ofKeys(NUMBER_OF_PARALLEL_THREADS, ACTIVE),
				andValues(Integer.class, Boolean.class));
	
	public Integer getNumberOfParallelThreads() {
		return getProperty(NUMBER_OF_PARALLEL_THREADS);
	}

	public void setNumberOfParallelThreads(Integer numberOfParallelThreads) {
		setProperty(NUMBER_OF_PARALLEL_THREADS, numberOfParallelThreads);
	}

	public Boolean getActive() {
		return getProperty(ACTIVE);
	}

	public void setActive(Boolean active) {
		setProperty(ACTIVE, active);
	}

	public Configuration getConfiguration() {
		return getParent();
	}

	public void addProject(Project project) {
		addChild(project);
	}

	public void removeProject(Project project) {
		removeChild(project);
	}

	public Collection<Project> getProjects() {
		return super.getChildrensOfType(Project.class);
	}

	public Project getProjectByName(String name) {
		return super.getChildByName(Project.class, name);
	}

	public Set<String> getProjectNames() {
		return super.getNamesFromChildrenOfType(Project.class);
	}

	private static final Set<Class<?>> CHILDREN_CLASSES = new HashSet<Class<?>>();
	static {
		CHILDREN_CLASSES.add(Project.class);
	}

	@Override
	public Set<Class<?>> getChildrenTypes() {
		return CHILDREN_CLASSES;
	}

	@Override
	public Class<?> getParentType() {
		return Configuration.class;
	}

}
