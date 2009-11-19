/**
 * 
 */
package org.openspotlight.federation.processing.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import org.openspotlight.security.idm.AuthenticatedUser;

public class BundleProcessorExecution {

    private final CautiousExecutor               executor;

    private final Repository[]                   repositories;

    private final BundleProcessorContextFactory  contextFactory;

    private final Set<Class<? extends Artifact>> artifactTypes;

    private final long                           defaultSleepIntervalInMillis;

    public BundleProcessorExecution(
                                     final BundleProcessorContextFactory contextFactory, final GlobalSettings settings,
                                     final Repository[] repositories, final Set<Class<? extends Artifact>> artifactTypes ) {
        this.repositories = repositories;
        this.contextFactory = contextFactory;
        this.artifactTypes = artifactTypes;
        final int threads = settings.getNumberOfParallelThreads();
        if (threads <= 0) {
            Exceptions.logAndThrow(new IllegalStateException("Default Thread size must be positive!"));
        }
        this.defaultSleepIntervalInMillis = settings.getDefaultSleepingIntervalInMilliseconds();
        if (this.defaultSleepIntervalInMillis <= 0) {
            Exceptions.logAndThrow(new IllegalStateException("Default Thread sleep time in millis must be positive!"));
        }
        this.executor = CautiousExecutor.newFixedThreadPool(threads);
        final BundleContextThreadInjector listener = new BundleContextThreadInjector(contextFactory);
        this.executor.addTaskListener(listener);
        this.executor.addThreadListener(listener);
    }

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

        final Queue<Pair<BundleProcessorType, Class<? extends Artifact>>> startingQueue = new ConcurrentLinkedQueue<Pair<BundleProcessorType, Class<? extends Artifact>>>();
        final Queue<ArtifactProcessingRunnable<? extends Artifact>> artifactQueue = new ConcurrentLinkedQueue<ArtifactProcessingRunnable<? extends Artifact>>();
        final AuthenticatedUser user = this.contextFactory.getUser();
        final Queue<Pair<Repository, Artifact>> artifacts = new ConcurrentLinkedQueue<Pair<Repository, Artifact>>();
        for (final Class<? extends Artifact> artifactType : this.artifactTypes) {
            for (final Group group : groupsWithBundles) {
                for (final BundleProcessorType processor : group.getBundleTypes()) {
                    final Repository repository = group.getRootRepository();

                    final Pair<BundleProcessorType, Class<? extends Artifact>> thisEntry = new Pair<BundleProcessorType, Class<? extends Artifact>>(
                                                                                                                                                    processor,
                                                                                                                                                    artifactType);
                    startingQueue.add(thisEntry);
                    this.executor.execute(new StartingRunnable<Artifact>(startingQueue, artifactQueue, thisEntry, repository,
                                                                         user));

                }
            }
        }
        this.waitUntilDone(startingQueue);
        for (final ArtifactProcessingRunnable<? extends Artifact> r : new ArrayList<ArtifactProcessingRunnable<? extends Artifact>>(
                                                                                                                                    artifactQueue)) {
            artifacts.add(new Pair<Repository, Artifact>(r.getCurrentContext().getCurrentGroup().getRootRepository(),
                                                         r.getArtifact()));
            this.executor.execute(r);
        }
        this.waitUntilDone(artifactQueue);

        //TODO - SAVE ALL ARTIFACTS 
        //TODO - TEST ALL THIS USING JCR (LIKE THE FUTURE ENVIRONMENT)
        //TODO - TEST ALL THIS USING MOCK STUFF (LIKE THE ONE USED DURING PARSER DEVELOPMENT)
        //TODO final compiler error to final tell me where do i final start working tomorrow ;-)
    }

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
