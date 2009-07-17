package org.openspotlight.federation.data;

import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class ArtifactMapping extends AbstractConfigurationNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -294480799746694450L;

	public ArtifactMapping(String name, Bundle bundle) {
		super(name, bundle, PROPERTY_TYPES);
	}

	private static final String ACTIVE = "active";

	private static final String INCLUDED = "included";

	private static final String EXCLUDED = "excluded";

	@SuppressWarnings("unchecked")
	private static final Map<String, Class<?>> PROPERTY_TYPES = map(ofKeys(
			ACTIVE, INCLUDED, EXCLUDED), andValues(Boolean.class, String.class,
			String.class));

	public String getIncluded() {
		return getProperty(INCLUDED);
	}

	public void setIncluded(String included) {
		setProperty(INCLUDED, included);
	}

	public String getExcluded() {
		return getProperty(EXCLUDED);
	}

	public void setExcluded(String excluded) {
		setProperty(EXCLUDED, excluded);
	}

	public Boolean getActive() {
		return getProperty(ACTIVE);
	}

	public void setActive(Boolean active) {
		setProperty(ACTIVE, active);
	}

	public Bundle getBundle() {
		return getParent();
	}

	private static final Set<Class<?>> CHILDREN_CLASSES = new HashSet<Class<?>>();

	@Override
	public Set<Class<?>> getChildrenTypes() {
		return CHILDREN_CLASSES;
	}

	@Override
	public Class<?> getParentType() {
		return Bundle.class;
	}

}
