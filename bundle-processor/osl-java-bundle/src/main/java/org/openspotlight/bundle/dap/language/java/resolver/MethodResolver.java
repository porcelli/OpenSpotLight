package org.openspotlight.bundle.dap.language.java.resolver;

import org.openspotlight.graph.SLGraphSession;

public class MethodResolver {

    private TypeResolver   typeFinder   = null;
    private SLGraphSession graphSession = null;

    public MethodResolver(
                           TypeResolver typeFinder, SLGraphSession graphSession ) {
        this.typeFinder = typeFinder;
        this.graphSession = graphSession;
    }
}
