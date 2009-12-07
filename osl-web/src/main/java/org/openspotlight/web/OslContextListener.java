package org.openspotlight.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

/**
 * The listener interface for receiving oslContext events. The class that is
 * interested in processing a oslContext event implements this interface, and
 * the object created with that class is registered with a component using the
 * component's <code>addOslContextListener<code> method. When
 * the oslContext event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see OslContextEvent
 */
public class OslContextListener implements ServletContextListener,
		OslDataConstants {

	/*
	 * (non-Javadoc)
	 * 
	 * @seejavax.servlet.ServletContextListener#contextDestroyed(javax.servlet.
	 * ServletContextEvent)
	 */
	public void contextDestroyed(final ServletContextEvent arg0) {

		WebExecutionContextFactory.INSTANCE.contextStopped();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	public void contextInitialized(final ServletContextEvent arg0) {
		try {
			final JcrConnectionDescriptor descriptor = DefaultJcrDescriptor.DEFAULT_DESCRIPTOR;
			arg0.getServletContext().setAttribute(CONTEXT__JCR_DESCRIPTOR,
					descriptor);
			WebExecutionContextFactory.INSTANCE.contextStarted();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, ConfigurationException.class);
		}
	}

}
