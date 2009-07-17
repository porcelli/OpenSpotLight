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
public final class Project extends AbstractConfigurationNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2046784379709017337L;

	public Project(String name, Repository repository) {
		super(name, repository, PROPERTY_TYPES);
	}

	private static final String ACTIVE = "active";

	@SuppressWarnings("unchecked")
	private static final Map<String, Class<?>> PROPERTY_TYPES = map(
			ofKeys(ACTIVE), andValues(Boolean.class));

	public Boolean getActive() {
		return getProperty(ACTIVE);
	}

	public void setActive(Boolean active) {
		setProperty(ACTIVE, active);
	}

	public void addBundle(Bundle bundle) {
		addChild(bundle);
	}

	public void removeBundle(Bundle bundle) {
		removeChild(bundle);
	}

	public Collection<Bundle> getBundles() {
		return super.getChildrensOfType(Bundle.class);
	}

	public Bundle getBundleByName(String name) {
		return super.getChildByName(Bundle.class, name);
	}

	public Set<String> getBundleNames() {
		return super.getNamesFromChildrenOfType(Bundle.class);
	}

	public Repository getRepository() {
		return getParent();
	}

	private static final Set<Class<?>> CHILDREN_CLASSES = new HashSet<Class<?>>();
	static {
		CHILDREN_CLASSES.add(Bundle.class);
	}

	@Override
	public Set<Class<?>> getChildrenTypes() {
		return CHILDREN_CLASSES;
	}

	@Override
	public Class<?> getParentType() {
		return Repository.class;
	}

}
