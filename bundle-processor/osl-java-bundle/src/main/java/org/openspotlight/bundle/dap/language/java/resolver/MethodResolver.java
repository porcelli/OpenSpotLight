package org.openspotlight.bundle.dap.language.java.resolver;

import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;

public class MethodResolver {

    private TypeResolver   typeResolver = null;
    private SLGraphSession graphSession = null;

    public MethodResolver(
                           TypeResolver typeFinder, SLGraphSession graphSession ) {
        this.typeResolver = typeFinder;
        this.graphSession = graphSession;
    }

    public JavaMethodMethod getMethod( SLNode newType,
                                       String methodName ) throws SLInvalidParameterException {

        if (typeResolver.isFromAbstractContext(newType)) {
            throw new SLInvalidParameterException();
        }

        return null;
    }

}
