package org.openspotlight.bundle.scheduler;

import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.bundle.domain.Schedulable;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Sep 29, 2010
 * Time: 3:59:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SchedulableTaskFactory<S extends Schedulable> {

    public SchedulerTask[] createTasks(S schedulable, ExecutionContextFactory factory);


    public static class TaskSupport{
        public static SchedulerTask[] wrapTask(SchedulerTask task){
            return new SchedulerTask[]{task};
        }
    }

}
