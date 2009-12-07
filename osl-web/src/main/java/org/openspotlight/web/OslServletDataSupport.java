package org.openspotlight.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

/**
 * The Class OslServletContextSupport contains methods to retrieve some useful
 * objects from {@link ServletContext}.
 */
public class OslServletDataSupport implements OslDataConstants {

	public static String getCurrentRepository(final ServletContext ctx,
			final HttpServletRequest request) {
		return (String) request.getSession().getAttribute(
				OslDataConstants.SESSION__REPOSITORY);
	}

	public static JcrConnectionDescriptor getJcrDescriptor(
			final ServletContext ctx, final HttpServletRequest request) {
		return (JcrConnectionDescriptor) ctx
				.getAttribute(OslDataConstants.CONTEXT__JCR_DESCRIPTOR);
	}

	public static String getPassword(final ServletContext ctx,
			final HttpServletRequest request) {
		return (String) request.getSession().getAttribute(
				OslDataConstants.SESSION__PASSWORD);
	}

	public static String getUserName(final ServletContext ctx,
			final HttpServletRequest request) {
		return (String) request.getSession().getAttribute(
				OslDataConstants.SESSION__USER_NAME);
	}

}
