package org.openspotlight.federation.context;

import com.google.inject.AbstractModule;

/**
 * Created by User: feu - Date: Apr 26, 2010 - Time: 1:04:20 PM
 */
public class SingleGraphSessionExecutionContextFactoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExecutionContextFactory.class).to(SingleGraphSessionExecutionContextFactory.class);
    }
}