package org.openspotlight.federation.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import net.jcip.annotations.ThreadSafe;

/**
 * This is the root node of the configuration classes that contains the
 * following structure.
 * 
 * <pre>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 * &lt;configuration&gt;
 * 	&lt;repository name=&quot;development&quot; active=&quot;true&quot;
 * 		numberOfParallelThreads=&quot;15&quot;&gt;
 * 		&lt;project name=&quot;Open SpotLight&quot; active=&quot;true&quot;&gt;
 * 			&lt;bundle name=&quot;Java sources&quot; active=&quot;true&quot; type=&quot;fileSystem&quot;
 * 				initialLookup=&quot;/usr/src/osl&quot;&gt;
 * 				&lt;artifact
 * 					name=&quot;/usr/src/osl/src/main/java/org/openspotligth/Example.java&quot;
 * 					dataSha1=&quot;sha1&quot; /&gt;
 * 				&lt;artifact
 * 					name=&quot;/usr/src/osl/src/main/java/org/openspotligth/AnotherOneExample.java&quot;
 * 					dataSha1=&quot;sha1&quot; /&gt;
 * 				&lt;artifactMapping name=&quot;all java files&quot; active=&quot;true&quot;
 * 					included=&quot;/src/main/java/ **.java&quot; /&gt;
 * 				&lt;artifactMapping name=&quot;no older types&quot; active=&quot;true&quot;
 * 					excluded=&quot;** / *.asm&quot; /&gt;
 * 			&lt;/bundle&gt;
 * 		&lt;/project&gt;
 * 	&lt;/repository&gt;
 * &lt;/configuration&gt;
 * </pre>
 * 
 * This structure are used to pass the artifacts to the parser. All the classes
 * are thread save by default.
 * 
 * @see ConfigurationNode
 * @see AbstractConfigurationNode
 * 
 * @author feu
 * 
 */
@ThreadSafe
public final class Configuration extends AbstractConfigurationNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4126478190563727620L;

	public static final String DEFAULT_CONFIGURATION_NAME = "root";

	/**
	 * Default constructor.
	 */
	public Configuration() {
		super(DEFAULT_CONFIGURATION_NAME, null, PROPERTY_TYPES);
	}

	/**
	 * static property types.
	 */
	private static final Map<String, Class<?>> PROPERTY_TYPES = new HashMap<String, Class<?>>();

	/**
	 * Adds a new repository.
	 * 
	 * @param repository to be added
	 */
	public void addRepository(Repository repository) {
		addChild(repository);
	}

	public void removeRepository(Repository repository) {
		removeChild(repository);
	}

	public Collection<Repository> getRepositories() {
		return super.getChildrensOfType(Repository.class);
	}

	public Repository getRepositoryByName(String name) {
		return super.getChildByName(Repository.class, name);
	}

	public Set<String> getRepositoryNames() {
		return super.getNamesFromChildrenOfType(Repository.class);
	}

	private static final Set<Class<?>> CHILDREN_CLASSES = new HashSet<Class<?>>();
	static {
		CHILDREN_CLASSES.add(Repository.class);
	}

	@Override
	public Set<Class<?>> getChildrenTypes() {
		return CHILDREN_CLASSES;
	}

	@Override
	public Class<?> getParentType() {
		return null;
	}

	public Artifact findByName(String initialLookup, String artifactName) {
		checkNotEmpty("initialLookup", initialLookup);
		checkNotEmpty("artifactName", artifactName);
		for (Repository repository : getRepositories()) {
			for (Project project : repository.getProjects()) {
				for (Bundle bundle : project.getBundles()) {
					if (bundle.getInitialLookup().equals(initialLookup)) {
						for (Artifact artifact : bundle.getArtifacts()) {
							if (artifactName.equals(artifact.getName())) {
								return artifact;
							}
						}
					}
				}
			}
		}
		return null;
	}

}
