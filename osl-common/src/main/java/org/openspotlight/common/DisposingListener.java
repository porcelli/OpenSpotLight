/**
 *
 */
package org.openspotlight.common;

public interface DisposingListener<E> {
	public void didCloseResource(E context);
}