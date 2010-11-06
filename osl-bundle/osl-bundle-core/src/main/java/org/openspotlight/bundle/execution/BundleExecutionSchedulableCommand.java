package org.openspotlight.bundle.execution;

import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.bundle.scheduler.SchedulableTaskFactory;
import org.openspotlight.bundle.scheduler.SchedulerTask;
import org.openspotlight.domain.Group;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Nov 6, 2010
 * Time: 2:32:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class BundleExecutionSchedulableCommand implements SchedulableTaskFactory<Group> {
    @Override
    public SchedulerTask[] createTasks(Group schedulable, ExecutionContextFactory factory) {
        

        return new SchedulerTask[0];  //To change body of implemented methods use File | Settings | File Templates.
        

    }
}
