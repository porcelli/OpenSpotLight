package org.openspotlight.federation.loader;

import com.google.inject.AbstractModule;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Oct 6, 2010
 * Time: 11:11:14 AM
 * To change this template use File | Settings | File Templates.
 */
public class PersistentConfigurationManagerModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(ConfigurationManagerFactory.class).to(PersistentConfigurationManagerFactoryImpl.class);
    }
}
