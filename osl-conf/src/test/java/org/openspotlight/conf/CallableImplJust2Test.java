package org.openspotlight.conf;

import java.util.concurrent.Callable;

public class CallableImplJust2Test implements Callable<Void> {

    @Override
    public Void call()
        throws Exception {
        return null;
    }

}
