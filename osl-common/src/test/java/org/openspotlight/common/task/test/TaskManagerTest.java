/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.common.task.test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.common.task.exception.RunnableWithException;
import org.openspotlight.common.taskexec.TaskExec;
import org.openspotlight.common.taskexec.TaskExecGroup;
import org.openspotlight.common.taskexec.TaskExecManager;
import org.openspotlight.common.taskexec.TaskExecPool;

public class TaskManagerTest {

    private class Worker implements RunnableWithException {
        private final String       description;
        private final List<String> list;

        private final boolean      sleeping;

        public Worker(
                       final String description, final List<String> list, final boolean sleeping) {
            this.description = description;
            this.list = list;
            this.sleeping = sleeping;
        }

        public void run()
            throws Exception {
            if (sleeping) {
                Thread.currentThread();
                Thread.sleep(random.nextInt(100));
            }
            list.add(description);
        }

    }

    private static final Random random = new Random();

    public static void main(final String... args)
        throws Exception {
        new TaskManagerTest().shouldExecuteTasksOnPoolWithEigth();
    }

    private void createSleepingTasks(final TaskExecPool pool,
                                      final List<String> list) {
        createTasks(pool, list, true);
    }

    private void createTasks(final TaskExecPool pool,
                              final List<String> list) {
        createTasks(pool, list, false);
    }

    private void createTasks(final TaskExecPool pool,
                              final List<String> list,
                              final boolean sleeping) {
        final TaskExecGroup group2 = pool.createTaskGroup("group-2", 2);
        final TaskExecGroup group1 = pool.createTaskGroup("group-1", 1);
        final TaskExec task5 =
            group2
                .prepareTask()
                .withReadableDescription("thisThread")
                .withUniqueId("5")
                .withRunnable(
                                                                                                                         new Worker(
                                                                                                                                    "5",
                                                                                                                                    list,
                                                                                                                                    sleeping))
                .andPublishTask();
        final TaskExec task6 =
            group2
                .prepareTask()
                .withReadableDescription("thisThread")
                .withUniqueId("6")
                .withRunnable(
                                                                                                                         new Worker(
                                                                                                                                    "6",
                                                                                                                                    list,
                                                                                                                                    sleeping))
                .withParentTasks(
                                                                                                                                                               task5)
                .andPublishTask();
        group2
            .prepareTask()
            .withReadableDescription("thisThread")
            .withUniqueId("7")
            .withRunnable(new Worker("7", list, sleeping))
            .withParentTasks(
                                                                                                                                                   task6)
            .andPublishTask();

        final TaskExec task1 =
            group1
                .prepareTask()
                .withReadableDescription("thisThread")
                .withUniqueId("1")
                .withRunnable(
                                                                                                                         new Worker(
                                                                                                                                    "1",
                                                                                                                                    list,
                                                                                                                                    sleeping))
                .andPublishTask();

        final TaskExec task1_1 =
            group1
                .prepareTask()
                .withReadableDescription("thisThread-1_1")
                .withUniqueId("1_1")
                .withRunnable(
                                                                                                                                 new Worker(
                                                                                                                                            "1_1",
                                                                                                                                            list,
                                                                                                                                            sleeping))
                .withParentTasks(
                                                                                                                                                                       task1)
                .andPublishTask();

        final TaskExec task1_2 =
            group1
                .prepareTask()
                .withReadableDescription("thisThread-1_2")
                .withUniqueId("1_2")
                .withRunnable(
                                                                                                                                 new Worker(
                                                                                                                                            "1_2",
                                                                                                                                            list,
                                                                                                                                            sleeping))
                .withParentTasks(
                                                                                                                                                                       task1,
                                                                                                                                                                       task1_1)
                .andPublishTask();

        final TaskExec task1_3 =
            group1
                .prepareTask()
                .withReadableDescription("thisThread-1_3")
                .withUniqueId("1_3")
                .withRunnable(
                                                                                                                                 new Worker(
                                                                                                                                            "1_3",
                                                                                                                                            list,
                                                                                                                                            sleeping))
                .withParentTasks(
                                                                                                                                                                       task1,
                                                                                                                                                                       task1_2)
                .andPublishTask();

        final TaskExec task1_4 =
            group1
                .prepareTask()
                .withReadableDescription("thisThread-1_4")
                .withUniqueId("1_4")
                .withRunnable(
                                                                                                                                 new Worker(
                                                                                                                                            "1_4",
                                                                                                                                            list,
                                                                                                                                            sleeping))
                .withParentTasks(
                                                                                                                                                                       task1,
                                                                                                                                                                       task1_3)
                .andPublishTask();

        group1
            .prepareTask()
            .withReadableDescription("thisThread-taskFactory")
            .withUniqueId("thisThread-taskFactory")
            .withRunnable(
                                                                                                                                   new RunnableWithException() {

                                                                                                                                       public
                                                                                                                                           void
                                                                                                                                           run()
                                                                                                                                               throws Exception {
                                                                                                                                           final TaskExec task2_1 =
                                                                                                                                               group1
                                                                                                                                                   .prepareTask()
                                                                                                                                                   .withReadableDescription(
                                                                                                                                                                                                                 "thisThread-2_1")
                                                                                                                                                   .withUniqueId(
                                                                                                                                                                                                                                                "2_1")
                                                                                                                                                   .withRunnable(
                                                                                                                                                                                                                                                                    new Worker(
                                                                                                                                                                                                                                                                               "2_1",
                                                                                                                                                                                                                                                                               list,
                                                                                                                                                                                                                                                                               sleeping))
                                                                                                                                                   .withParentTasks(
                                                                                                                                                                                                                                                                                                          task1_1,
                                                                                                                                                                                                                                                                                                          task1_2,
                                                                                                                                                                                                                                                                                                          task1_3,
                                                                                                                                                                                                                                                                                                          task1_4)
                                                                                                                                                   .andPublishTask();

                                                                                                                                           final TaskExec task2_2 =
                                                                                                                                               group1
                                                                                                                                                   .prepareTask()
                                                                                                                                                   .withReadableDescription(
                                                                                                                                                                                                                 "thisThread-2_2")
                                                                                                                                                   .withUniqueId(
                                                                                                                                                                                                                                                "2_2")
                                                                                                                                                   .withRunnable(
                                                                                                                                                                                                                                                                    new Worker(
                                                                                                                                                                                                                                                                               "2_2",
                                                                                                                                                                                                                                                                               list,
                                                                                                                                                                                                                                                                               sleeping))
                                                                                                                                                   .withParentTasks(
                                                                                                                                                                                                                                                                                                          task1_1,
                                                                                                                                                                                                                                                                                                          task1_2,
                                                                                                                                                                                                                                                                                                          task1_3,
                                                                                                                                                                                                                                                                                                          task1_4,
                                                                                                                                                                                                                                                                                                          task2_1)
                                                                                                                                                   .andPublishTask();

                                                                                                                                           final TaskExec task2_3 =
                                                                                                                                               group1
                                                                                                                                                   .prepareTask()
                                                                                                                                                   .withReadableDescription(
                                                                                                                                                                                                                 "thisThread-2_3")
                                                                                                                                                   .withUniqueId(
                                                                                                                                                                                                                                                "2_3")
                                                                                                                                                   .withRunnable(
                                                                                                                                                                                                                                                                    new Worker(
                                                                                                                                                                                                                                                                               "2_3",
                                                                                                                                                                                                                                                                               list,
                                                                                                                                                                                                                                                                               sleeping))
                                                                                                                                                   .withParentTasks(
                                                                                                                                                                                                                                                                                                          task1_1,
                                                                                                                                                                                                                                                                                                          task1_2,
                                                                                                                                                                                                                                                                                                          task1_3,
                                                                                                                                                                                                                                                                                                          task1_4,
                                                                                                                                                                                                                                                                                                          task2_2)
                                                                                                                                                   .andPublishTask();

                                                                                                                                           final TaskExec task2_4 =
                                                                                                                                               group1
                                                                                                                                                   .prepareTask()
                                                                                                                                                   .withReadableDescription(
                                                                                                                                                                                                                 "thisThread-2_4")
                                                                                                                                                   .withUniqueId(
                                                                                                                                                                                                                                                "2_4")
                                                                                                                                                   .withRunnable(
                                                                                                                                                                                                                                                                    new Worker(
                                                                                                                                                                                                                                                                               "2_4",
                                                                                                                                                                                                                                                                               list,
                                                                                                                                                                                                                                                                               sleeping))
                                                                                                                                                   .withParentTasks(
                                                                                                                                                                                                                                                                                                          task1_1,
                                                                                                                                                                                                                                                                                                          task1_2,
                                                                                                                                                                                                                                                                                                          task1_3,
                                                                                                                                                                                                                                                                                                          task1_4,
                                                                                                                                                                                                                                                                                                          task2_3)
                                                                                                                                                   .andPublishTask();

                                                                                                                                           final TaskExec task3 =
                                                                                                                                               group1
                                                                                                                                                   .prepareTask()
                                                                                                                                                   .withReadableDescription(
                                                                                                                                                                                                               "thisThread-3")
                                                                                                                                                   .withUniqueId(
                                                                                                                                                                                                                                            "3")
                                                                                                                                                   .withRunnable(
                                                                                                                                                                                                                                                              new Worker(
                                                                                                                                                                                                                                                                         "3",
                                                                                                                                                                                                                                                                         list,
                                                                                                                                                                                                                                                                         sleeping))
                                                                                                                                                   .withParentTasks(
                                                                                                                                                                                                                                                                                                    task2_1,
                                                                                                                                                                                                                                                                                                    task2_2,
                                                                                                                                                                                                                                                                                                    task2_3,
                                                                                                                                                                                                                                                                                                    task2_4)
                                                                                                                                                   .andPublishTask();

                                                                                                                                           group1
                                                                                                                                               .prepareTask()
                                                                                                                                               .withReadableDescription(
                                                                                                                                                                                        "thisThread-4")
                                                                                                                                               .withUniqueId(
                                                                                                                                                                                                                     "4")
                                                                                                                                               .withRunnable(
                                                                                                                                                                                                                                       new Worker(
                                                                                                                                                                                                                                                  "4",
                                                                                                                                                                                                                                                  list,
                                                                                                                                                                                                                                                  sleeping))
                                                                                                                                               .withParentTasks(
                                                                                                                                                                                                                                                                             task3)
                                                                                                                                               .andPublishTask();

                                                                                                                                       }
                                                                                                                                   })
            .withParentTasks(
                                                                                                                                                      task1,
                                                                                                                                                      task1_4)
            .andPublishTask();

    }

    @Test
    public void shouldExecuteSleepingTasksOnPoolWithEigth()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 8);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteSleepingTasksOnPoolWithFive()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 5);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteSleepingTasksOnPoolWithFour()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 4);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteSleepingTasksOnPoolWithSeven()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 7);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteSleepingTasksOnPoolWithSix()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 6);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteSleepingTasksOnPoolWithSixteen()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 16);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteSleepingTasksOnPoolWithThree()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 3);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteSleepingTasksOnPoolWithTwoTasks()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 2);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteSleepingTasksOnSinglePool()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 1);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createSleepingTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnPoolWithEigth()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 8);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnPoolWithFive()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 5);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnPoolWithFour()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 4);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnPoolWithSeven()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 7);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnPoolWithSix()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 6);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnPoolWithSixteen()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 16);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnPoolWithThree()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 3);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnPoolWithTwoTasks()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 2);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @Test
    public void shouldExecuteTasksOnSinglePool()
        throws Exception {

        final TaskExecPool pool = TaskExecManager.INSTANCE.createTaskPool("test-pool", 1);
        final List<String> list = new CopyOnWriteArrayList<String>();
        createTasks(pool, list);
        pool.startExecutorBlockingUntilFinish();
        Assert.assertThat(list, Is.is(Arrays.asList("1", "1_1", "1_2", "1_3", "1_4", "2_1", "2_2", "2_3", "2_4", "3", "4", "5",
                                                    "6", "7")));
    }

    @After
    public void sleep()
        throws Exception {
        Thread.sleep(250);
    }

}
