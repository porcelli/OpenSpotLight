package org.openspotlight.web;

import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.scheduler.GlobalSettingsSupport;

public class WebGlobalSettingsSupport {

	public static void initializeSettings(final GlobalSettings settings) {
		GlobalSettingsSupport.initializeScheduleMap(settings);
		settings
				.setArtifactFinderRegistryClass(WebArtifactFinderRegistry.class);
	}

	private WebGlobalSettingsSupport() {
	}

}
