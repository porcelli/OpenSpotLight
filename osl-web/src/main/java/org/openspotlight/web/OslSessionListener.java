package org.openspotlight.web;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class OslSessionListener implements HttpSessionListener,
		OslDataConstants {

	public void sessionCreated(final HttpSessionEvent se) {
		// FIXME remove this and add a login servlet
		se.getSession().setAttribute(SESSION__USER_NAME, "example_user");
		se.getSession().setAttribute(SESSION__PASSWORD, "example_password");
		se.getSession().setAttribute(SESSION__REPOSITORY, "example_repository");
	}

	public void sessionDestroyed(final HttpSessionEvent se) {

	}

}
