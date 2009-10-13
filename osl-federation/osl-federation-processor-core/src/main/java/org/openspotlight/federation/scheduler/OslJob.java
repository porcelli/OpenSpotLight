package org.openspotlight.federation.scheduler;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.util.Arrays;

import org.openspotlight.common.LazyType;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.impl.Configuration;
import org.openspotlight.federation.data.load.ConfigurationManager;
import org.openspotlight.federation.data.load.ConfigurationManagerProvider;
import org.openspotlight.federation.data.processing.BundleProcessorManager;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

/**
 * The Class OslJob.
 */
public class OslJob implements Job {

    /**
     * {@inheritDoc}
     */
    public void execute( final JobExecutionContext ctx ) throws org.quartz.JobExecutionException {
        final JobDataMap dataMap = ctx.getJobDetail().getJobDataMap();
        final BundleProcessorManager bundleProcessorManager = (BundleProcessorManager)dataMap.get(Scheduler.Factory.QuartzScheduler.BUNDLE_PROCESSOR_MANAGER);
        final String bundleId = (String)dataMap.get(Scheduler.Factory.QuartzScheduler.BUNDLE_TO_PROCESS_ID);
        final String versionId = (String)dataMap.get(Scheduler.Factory.QuartzScheduler.BUNDLE_VERSION_ID);
        final ConfigurationManagerProvider provider = (ConfigurationManagerProvider)dataMap.get(Scheduler.Factory.QuartzScheduler.CONFIGURATION_MANAGER_PROVIDER);
        final ConfigurationManager manager = provider.getNewInstance();

        try {
            final Configuration configuration = manager.load(LazyType.LAZY);
            final Bundle bundle = manager.findNodeByUuidAndVersion(configuration, Bundle.class, bundleId, versionId);

            bundleProcessorManager.processBundles(Arrays.asList(bundle));
        } catch (final Exception e) {
            throw logAndReturnNew(e, org.quartz.JobExecutionException.class);
        } finally {
            manager.closeResources();
        }
    }

}
