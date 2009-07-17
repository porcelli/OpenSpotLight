package org.openspotlight.common.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Internationalization class using the Eclipse <b>right
 * click->source->externalize strings</b>
 * 
 * @author feu
 * 
 */
public class Messages {
	private static final String BUNDLE_NAME = "org.openspotlight.common.util.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
