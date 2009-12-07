package org.openspotlight.federation.processing;

import org.openspotlight.common.exception.SLRuntimeException;

public class BundleExecutionException extends SLRuntimeException {

    private static final long serialVersionUID = -8452525420205217493L;

    public BundleExecutionException() {
        super();
    }

    public BundleExecutionException(
                                     final String message ) {
        super(message);
    }

    public BundleExecutionException(
                                     final String message, final Throwable cause ) {
        super(message, cause);
    }

    public BundleExecutionException(
                                     final Throwable cause ) {
        super(cause);
    }

}
