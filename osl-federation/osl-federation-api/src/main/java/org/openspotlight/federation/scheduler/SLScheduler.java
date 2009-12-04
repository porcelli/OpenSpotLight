package org.openspotlight.federation.scheduler;

import java.util.Set;

import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.domain.Schedulable;
import org.openspotlight.federation.domain.Schedulable.SchedulableContext;

public interface SLScheduler {

	public static interface SchedulableContextFactory {
		public SchedulableContext createContext();
	}

	public <T extends Schedulable> void fireSchedulable(final T schedulable);

	public void refreshJobs(GlobalSettings settings,
			Set<Repository> repositories);

	public void setSchedulableContextFactory(
			SchedulableContextFactory contextFactory);

	public void startScheduler();

	public void stopScheduler();

}
