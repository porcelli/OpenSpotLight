package org.openspotlight.security.idm.store;

import com.google.inject.Injector;

/**
 * Created by User: feu - Date: Apr 19, 2010 - Time: 7:35:02 PM
 */
public enum StaticInjector {
    INSTANCE;

    private Injector injector;

    public Injector getInjector() {
        return injector;
    }

    public void setInjector( Injector injector ) {
        this.injector = injector;
    }
}
