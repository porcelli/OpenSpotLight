package org.openspotlight.federation.loader;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 6, 2010 Time: 10:42:02 AM To change this template use File | Settings | File
 * Templates.
 */
public interface ConfigurationManagerFactory {

    public ImmutableConfigurationManager createImmutable();

    public MutableConfigurationManager createMutable();
}
