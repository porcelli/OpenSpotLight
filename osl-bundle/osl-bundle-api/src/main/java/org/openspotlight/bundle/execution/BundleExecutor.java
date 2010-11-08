package org.openspotlight.bundle.execution;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA. User: feu Date: Nov 6, 2010 Time: 2:36:39 PM To change this template use File | Settings | File
 * Templates.
 */
public interface BundleExecutor {

    /**
     * Execute the tasks in the <b>SAME ORDER</b> it was given
     * 
     * @param tasks
     */
    public void execute(Collection<Callable<Void>> tasks)
        throws InterruptedException;
}
