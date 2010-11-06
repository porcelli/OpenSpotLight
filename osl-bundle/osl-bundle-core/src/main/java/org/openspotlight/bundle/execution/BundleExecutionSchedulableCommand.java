package org.openspotlight.bundle.execution;

import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.bundle.scheduler.SchedulableTaskFactory;
import org.openspotlight.bundle.scheduler.SchedulerTask;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.common.collect.Lists.newLinkedList;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Nov 6, 2010
 * Time: 2:32:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class BundleExecutionSchedulableCommand implements SchedulableTaskFactory<Group> {

    private static class GroupTaskCollector implements Repository.GroupVisitor{

        private List

        @Override
        public void visitGroup(Group group) {
            tasks.put(group,new LinkedList<Class<? extends Callable<Void>>>());
            group.get

        }


    }

    @Override
    public SchedulerTask[] createTasks(Group schedulable, ExecutionContextFactory factory) {

        GroupTaskCollector taskCollector = new GroupTaskCollector();
        schedulable.acceptVisitor(taskCollector);

        iterable
                corre umas 2x o numbOfThr e insere os artifacts da task corrente
                aguarda
                        continua...
        


        return new SchedulerTask[0];  //To change body of implemented methods use File | Settings | File Templates.
        

    }
}
