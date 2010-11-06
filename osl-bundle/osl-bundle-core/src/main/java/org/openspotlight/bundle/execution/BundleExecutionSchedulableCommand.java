package org.openspotlight.bundle.execution;

import org.openspotlight.bundle.context.ExecutionContextFactory;
import org.openspotlight.bundle.scheduler.SchedulableTaskFactory;
import org.openspotlight.bundle.scheduler.SchedulerTask;
import org.openspotlight.domain.ArtifactSourceMapping;
import org.openspotlight.domain.BundleConfig;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

    private static class GroupItem {
        private GroupItem(Group group, ArtifactSource source, ArtifactSourceMapping mapping, BundleConfig bundleConfig) {
            this.group = group;
            this.source = source;
            this.mapping = mapping;
            this.bundleConfig = bundleConfig;
        }

        private final Group group;

        private final ArtifactSource source;

        private final ArtifactSourceMapping mapping;

        private final BundleConfig bundleConfig;

        public Group getGroup() {
            return group;
        }

        public ArtifactSource getSource() {
            return source;
        }

        public ArtifactSourceMapping getMapping() {
            return mapping;
        }

        public BundleConfig getBundleConfig() {
            return bundleConfig;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GroupItem groupItem = (GroupItem) o;

            if (bundleConfig != null ? !bundleConfig.equals(groupItem.bundleConfig) : groupItem.bundleConfig != null)
                return false;
            if (group != null ? !group.equals(groupItem.group) : groupItem.group != null) return false;
            if (mapping != null ? !mapping.equals(groupItem.mapping) : groupItem.mapping != null) return false;
            if (source != null ? !source.equals(groupItem.source) : groupItem.source != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = group != null ? group.hashCode() : 0;
            result = 31 * result + (source != null ? source.hashCode() : 0);
            result = 31 * result + (mapping != null ? mapping.hashCode() : 0);
            result = 31 * result + (bundleConfig != null ? bundleConfig.hashCode() : 0);
            return result;
        }
    }

    private static class GroupTaskCollector implements Repository.GroupVisitor{

        private Map<Pair<Group,Artifac,List<Class<? extends Callable<Void>>>> tasks = newHashMap();

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
