package org.openspotlight.federation.data;

import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import static java.util.Collections.emptySet;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Artifact extends AbstractConfigurationNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -889016915372708085L;

	public Artifact(String name, Bundle bundle) {
		super(name, bundle, PROPERTY_TYPES);
	}

	private static final String DATA = "data";

	private static final String DATA_SHA1 = "dataSha1";

	@SuppressWarnings("unchecked")
	private static final Map<String, Class<?>> PROPERTY_TYPES = map(ofKeys(
			DATA_SHA1, DATA), andValues(String.class, InputStream.class));

	public InputStream getData() {
		return getTransientProperty(DATA);
	}

	public void setData(InputStream data) {
		setTransientProperty(DATA, data);
	}

	public String getDataSha1() {
		return getProperty(DATA_SHA1);
	}

	public void setDataSha1(String dataSha1) {
		setProperty(DATA_SHA1, dataSha1);
	}

	public Bundle getBundle() {
		return getParent();
	}

	@Override
	public Set<Class<?>> getChildrenTypes() {
		return emptySet();
	}

	@Override
	public Class<?> getParentType() {
		return Bundle.class;
	}

}
