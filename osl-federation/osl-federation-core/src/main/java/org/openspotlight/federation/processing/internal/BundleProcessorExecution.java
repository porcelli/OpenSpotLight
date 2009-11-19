/**
 * 
 */
package org.openspotlight.federation.processing.internal;

import java.util.Set;

import javax.xml.bind.Marshaller.Listener;

import org.openspotlight.common.concurrent.CautiousExecutor;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.finder.ArtifactTypeRegistry;
import org.openspotlight.federation.processing.BundleExecutionException;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.security.idm.AuthenticatedUser;

public class BundleProcessorExecution {

    private final CautiousExecutor               executor;

    private final Repository[]                   repositories;

    private final BundleProcessorContextFactory  contextFactory;

    private final Set<Class<? extends Artifact>> artifactTypes;

    public BundleProcessorExecution(
                                     final BundleProcessorContextFactory contextFactory, final GlobalSettings settings,
                                     final Repository[] repositories, final Set<Class<? extends Artifact>> artifactTypes ) {
        this.repositories = repositories;
        this.contextFactory = contextFactory;
        this.artifactTypes = artifactTypes;
        int threads = settings.getNumberOfParallelThreads();
        if (threads <= 0) {
            threads = 4;
        }
        this.executor = CautiousExecutor.newFixedThreadPool(threads);
        final BundleContextThreadInjector listener = new BundleContextThreadInjector(contextFactory);
        this.executor.addTaskListener(listener);
        this.executor.addThreadListener(listener);
    }

    public void execute() throws BundleExecutionException {
        //TODO - START THE SINGLE THREAD QUEUE
        //TODO - SAVE ALL ARTIFACTS 
        //TODO - TEST ALL THIS USING JCR (LIKE THE FUTURE ENVIRONMENT)
        //TODO - TEST ALL THIS USING MOCK STUFF (LIKE THE ONE USED DURING PARSER DEVELOPMENT)
        final compiler error to final tell me where do i final start working tomorrow ;-)
    }
}
