package org.openspotlight.bundle.scheduler;

import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Sep 29, 2010
 * Time: 5:33:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SchedulerTask extends Callable<Void>{
    public String getUniqueJobId();
}
