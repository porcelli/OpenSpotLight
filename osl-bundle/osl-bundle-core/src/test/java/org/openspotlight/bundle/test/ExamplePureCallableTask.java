package org.openspotlight.bundle.test;

import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 4, 2010 Time: 3:44:10 PM To change this template use File | Settings | File
 * Templates.
 */
public class ExamplePureCallableTask implements Callable<Void> {
    @Override
    public Void call()
        throws Exception {
        ExampleExecutionHistory.add(this.getClass(), null, null);
        return null;
    }
}
