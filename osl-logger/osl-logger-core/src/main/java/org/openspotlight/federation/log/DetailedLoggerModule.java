package org.openspotlight.federation.log;

import org.openspotlight.log.DetailedLogger;

import com.google.inject.AbstractModule;

/**
 * Created by User: feu - Date: Apr 26, 2010 - Time: 12:56:30 PM
 */
public class DetailedLoggerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DetailedLogger.class).toProvider(DetailedLoggerProvider.class);
    }
}
