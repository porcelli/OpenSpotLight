package org.openspotlight.bundle.execution;

import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.bundle.scheduler.SchedulableTaskFactory;
import org.openspotlight.bundle.scheduler.SchedulerTask;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;

/**
 * Created by IntelliJ IDEA. User: feu Date: Nov 6, 2010 Time: 2:32:41 PM To change this template use File | Settings | File
 * Templates.
 */
public class BundleExecutionSchedulableCommand implements SchedulableTaskFactory<Group> {

    private static class GroupTaskCollector implements Repository.GroupVisitor {

        //        private List

        @Override
        public void visitGroup(final Group group) {
            //            tasks.put(group,new LinkedList<Class<? extends Callable<Void>>>());
            //            group.get
        }

    }

    @Override
    public SchedulerTask[] createTasks(final Group schedulable, final ExecutionContextFactory factory) {

        final GroupTaskCollector taskCollector = new GroupTaskCollector();
        schedulable.acceptVisitor(taskCollector);

        //        iterable
        //                final corre umas 2final x o numbOfThr final e insere os artifacts da task corrente
        //                final aguarda
        //                        continua...

        return new SchedulerTask[0]; //To change body of implemented methods use File | Settings | File Templates.

    }
}
