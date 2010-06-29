package org.openspotlight.persist.guice;

import com.google.inject.AbstractModule;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.persist.support.SimplePersistFactoryImpl;

/**
 * Created by User: feu - Date: Apr 26, 2010 - Time: 11:10:54 AM
 */
public class SimplePersistModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SimplePersistFactory.class).to(SimplePersistFactoryImpl.class);
    }
}
