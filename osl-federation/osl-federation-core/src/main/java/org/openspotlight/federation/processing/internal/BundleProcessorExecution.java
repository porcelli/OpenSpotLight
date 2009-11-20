/**
 * 
 */
package org.openspotlight.federation.processing.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.openspotlight.common.Pair;
import org.openspotlight.common.concurrent.CautiousExecutor;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.BundleProcessorType;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.Repository.GroupVisitor;
import org.openspotlight.federation.processing.BundleExecutionException;
import org.openspotlight.federation.processing.internal.task.ArtifactTask;
import org.openspotlight.federation.processing.internal.task._1_StartingToSearchArtifactsTask;
import org.openspotlight.security.idm.AuthenticatedUser;

// TODO: Auto-generated Javadoc
/**
 * The Class BundleProcessorExecution.
 */
public class BundleProcessorExecution {

    /** The executor. */
    private final CautiousExecutor                    executor;

    /** The repositories. */
    private final Repository[]                        repositories;

    /** The context factory. */
    private final BundleProcessorContextFactory       contextFactory;

    /** The artifact types. */
    private final Set<Class<? extends Artifact>>      artifactTypes;

    /** The default sleep interval in millis. */
    private final long                                defaultSleepIntervalInMillis;

    /** The threads. */
    private final int                                 threads;

    /** The queue. */
    private final PriorityBlockingQueue<ArtifactTask> queue = new PriorityBlockingQueue<ArtifactTask>();

    /**
     * Instantiates a new bundle processor execution.
     * 
     * @param contextFactory the context factory
     * @param settings the settings
     * @param repositories the repositories
     * @param artifactTypes the artifact types
     */
    public BundleProcessorExecution(
                                     final BundleProcessorContextFactory contextFactory, final GlobalSettings settings,
                                     final Repository[] repositories, final Set<Class<? extends Artifact>> artifactTypes ) {
        this.repositories = repositories;
        this.contextFactory = contextFactory;
        this.artifactTypes = artifactTypes;
        this.threads = settings.getNumberOfParallelThreads();
        if (this.threads <= 0) {
            Exceptions.logAndThrow(new IllegalStateException("Default Thread size must be positive!"));
        }
        this.defaultSleepIntervalInMillis = settings.getDefaultSleepingIntervalInMilliseconds();
        if (this.defaultSleepIntervalInMillis <= 0) {
            Exceptions.logAndThrow(new IllegalStateException("Default Thread sleep time in millis must be positive!"));
        }
        this.executor = CautiousExecutor.newFixedThreadPool(this.threads);
        final BundleContextThreadInjector listener = new BundleContextThreadInjector(contextFactory);
        this.executor.addTaskListener(listener);
        this.executor.addThreadListener(listener);
    }

    /**
     * Execute.
     * 
     * @throws BundleExecutionException the bundle execution exception
     */
    public void execute() throws BundleExecutionException {
        final Set<Group> groupsWithBundles = new HashSet<Group>();
        final GroupVisitor visitor = new GroupVisitor() {
            public void visitGroup( final Group group ) {
                if (group.getBundleTypes() != null && group.getBundleTypes().size() > 0) {
                    groupsWithBundles.add(group);
                }
            }
        };
        for (final Repository repository : this.repositories) {
            repository.acceptGroupVisitor(visitor);
        }

        final AuthenticatedUser user = this.contextFactory.getUser();
        final Queue<Pair<Repository, Artifact>> artifacts = new ConcurrentLinkedQueue<Pair<Repository, Artifact>>();
        for (final Class<? extends Artifact> artifactType : this.artifactTypes) {
            for (final Group group : groupsWithBundles) {
                for (final BundleProcessorType processor : group.getBundleTypes()) {
                    final Repository repository = group.getRootRepository();

                    final ArtifactTask task = new _1_StartingToSearchArtifactsTask<Artifact>(repository, user, artifactType,
                                                                                             processor);
                    this.queue.add(task);
                }
            }
        }

        final List<ArtifactWorker> workers = new ArrayList<ArtifactWorker>(this.threads);

        for (int i = 0; i < this.threads; i++) {
            final ArtifactWorker worker = new ArtifactWorker(1, this.queue);
            workers.add(worker);
            this.executor.execute(worker);
        }

        monitor: while (true) {
            try {
                Thread.sleep(500);
            } catch (final InterruptedException e) {
                Exceptions.catchAndLog(e);
            }
            if (this.queue.isEmpty()) {
                for (final ArtifactWorker worker : workers) {
                    if (!worker.isWorking()) {
                        worker.stop();
                        continue monitor;
                    }
                }
                //anybore is working
                break monitor;
            }
        }

        //TODO - TEST ALL THIS USING JCR (LIKE THE FUTURE ENVIRONMENT)
        //TODO - TEST ALL THIS USING MOCK STUFF (LIKE THE ONE USED DURING PARSER DEVELOPMENT)
    }

    /**
     * Wait until done.
     * 
     * @param startingQueue the starting queue
     */
    private void waitUntilDone( final Queue<?> startingQueue ) {
        while (startingQueue.size() > 0) {
            try {
                Thread.sleep(this.defaultSleepIntervalInMillis);
            } catch (final InterruptedException e) {
                Exceptions.catchAndLog(e);
            }
        }
    }
}
