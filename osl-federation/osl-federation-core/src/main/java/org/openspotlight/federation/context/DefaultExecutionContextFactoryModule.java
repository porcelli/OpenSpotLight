package org.openspotlight.federation.context;

import com.google.inject.AbstractModule;

/**
 * Created by User: feu - Date: Apr 26, 2010 - Time: 1:03:12 PM
 */
public class DefaultExecutionContextFactoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExecutionContextFactory.class).to(DefaultExecutionContextFactory.class);
    }
}
