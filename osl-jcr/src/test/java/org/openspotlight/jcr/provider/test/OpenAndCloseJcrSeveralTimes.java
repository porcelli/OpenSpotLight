package org.openspotlight.jcr.provider.test;

import org.junit.Test;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

public class OpenAndCloseJcrSeveralTimes {

	@Test
	public void shouldOpenAndCloseJcrSeveralTimes() throws Exception {
		final JcrConnectionDescriptor desc = DefaultJcrDescriptor.TEMP_DESCRIPTOR;
		JcrConnectionProvider provider = JcrConnectionProvider
				.createFromData(desc);
		provider.openRepository();
		provider.closeRepository();
		provider = JcrConnectionProvider.createFromData(desc);
		provider.openRepository();
		provider.closeRepository();
		provider = JcrConnectionProvider.createFromData(desc);
		provider.openRepository();
		provider.closeRepository();

	}

}
