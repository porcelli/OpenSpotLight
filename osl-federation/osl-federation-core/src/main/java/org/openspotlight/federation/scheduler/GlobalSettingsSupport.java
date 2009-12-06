package org.openspotlight.federation.scheduler;

import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;

public class GlobalSettingsSupport {

	public void initializeScheduleMap(final GlobalSettings settings) {
		settings.getSchedulableCommandMap().put(Group.class,
				GroupSchedulable.class);
		settings.getSchedulableCommandMap().put(ArtifactSource.class,
				ArtifactSourceSchedulable.class);
	}

}
