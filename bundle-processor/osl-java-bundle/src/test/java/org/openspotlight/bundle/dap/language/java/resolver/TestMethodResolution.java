package org.openspotlight.bundle.dap.language.java.resolver;

import org.junit.Before;
import org.openspotlight.bundle.dap.language.java.resolver.MethodResolver;
import org.openspotlight.bundle.dap.language.java.resolver.TypeResolver;
import org.openspotlight.graph.SLGraphSession;
import org.testng.annotations.Test;

public class TestMethodResolution {
    private MethodResolver   methodFinder;
    private SLGraphSession graphSession;
    private final String   ABSTRACT_CONTEXT = "java_abstract";

    @Before
    public void setup() {
        methodFinder = new MethodResolver(getTypeFinder(), getGraphSession());
    }

    private TypeResolver getTypeFinder() {
        return new TypeResolver();
    }

    private SLGraphSession getGraphSession() {
        this.graphSession = null;
        return this.graphSession;
    }

    @Test
    public void resolveSimpleMethod() {
        //JavaType type
        //Type:Type, Method Name:String, Params:LinkeList<Type>
//        SLContext context = graphSession.createContext(ABSTRACT_CONTEXT);
//        JavaTypeClass object = context.getRootNode().addNode(JavaTypeClass.class, "java.lang.Object");
//        object.addNode(JavaMethod.class, "toString()");
        //        object
        //        methodFinder.getMethod(, "toString");
    }

}
