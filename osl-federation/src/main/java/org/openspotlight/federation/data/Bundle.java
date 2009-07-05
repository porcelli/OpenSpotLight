package org.openspotlight.federation.data;

import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public final class Bundle extends AbstractConfigurationNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1092283780730455977L;

	public Bundle(String name, Project project) {
		super(name, project, PROPERTY_TYPES);
	}

	private static final String ACTIVE = "active";

	private static final String TYPE = "type";

	private static final String INITIAL_LOOKUP = "initialLookup";

	@SuppressWarnings("unchecked")
	private static final Map<String, Class<?>> PROPERTY_TYPES = map(ofKeys(
			ACTIVE, TYPE, INITIAL_LOOKUP), andValues(Boolean.class,
			String.class, String.class));

	public String getInitialLookup() {
		return getProperty(INITIAL_LOOKUP);
	}

	public void setInitialLookup(String initialLookup) {
		setProperty(INITIAL_LOOKUP, initialLookup);
	}

	public Boolean getActive() {
		return getProperty(ACTIVE);
	}

	public String getType() {
		return getProperty(TYPE);
	}

	public void setType(String type) {
		setProperty(TYPE, type);
	}

	public void setActive(Boolean active) {
		setProperty(ACTIVE, active);
	}

	public Project getProject() {
		return getParent();
	}

	public void addArtifactMapping(ArtifactMapping Artifact) {
		addChild(Artifact);
	}

	public void removeArtifactMapping(ArtifactMapping Artifact) {
		removeChild(Artifact);
	}

	public Collection<ArtifactMapping> getArtifactMappings() {
		return super.getChildrensOfType(ArtifactMapping.class);
	}

	public ArtifactMapping getArtifactMappingByName(String name) {
		return super.getChildByName(ArtifactMapping.class, name);
	}

	public Set<String> getArtifactMappingNames() {
		return super.getNamesFromChildrenOfType(ArtifactMapping.class);
	}

	public void addArtifact(Artifact Artifact) {
		addChild(Artifact);
	}

	public Artifact addArtifact(String ArtifactName) {
		checkNotEmpty("ArtifactName", ArtifactName);
		Artifact Artifact = getArtifactByName(ArtifactName);
		if (Artifact != null)
			return Artifact;
		Artifact = new Artifact(ArtifactName, this);
		return Artifact;
	}

	public void removeArtifact(Artifact Artifact) {
		removeChild(Artifact);
	}

	public Collection<Artifact> getArtifacts() {
		return super.getChildrensOfType(Artifact.class);
	}

	public Artifact getArtifactByName(String name) {
		return super.getChildByName(Artifact.class, name);
	}

	public Set<String> getArtifactNames() {
		return super.getNamesFromChildrenOfType(Artifact.class);
	}

	private static final Set<Class<?>> CHILDREN_CLASSES = new HashSet<Class<?>>();
	static {
		CHILDREN_CLASSES.add(ArtifactMapping.class);
		CHILDREN_CLASSES.add(Artifact.class);
	}

	@Override
	public Set<Class<?>> getChildrenTypes() {
		return CHILDREN_CLASSES;
	}

	@Override
	public Class<?> getParentType() {
		return Project.class;
	}

}
