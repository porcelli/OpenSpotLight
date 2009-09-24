package org.openspotlight.bundle.dap.language.java.resolver;

import junit.framework.Assert;

import org.junit.Before;
import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.metamodel.link.TypeDeclares;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethodMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.support.JavaGraphNodeSupport;
import org.openspotlight.common.Pair;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLContextAlreadyExistsException;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphException;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryException;
import org.openspotlight.graph.SLGraphFactoryImpl;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.testng.annotations.Test;

public class TestMethodResolution {
    private MethodResolver       methodResolver = null;
    private SLGraphSession       graphSession   = null;
    private JavaGraphNodeSupport helper         = null;
    private SLContext            abstractContex = null;

    private Pair<JavaType, JavaMethod> createMethod( final String packageName,
                                                     final String className,
                                                     final String methodName ) throws Exception {
        final JavaType newType = this.helper.addTypeOnCurrentContext(JavaTypeClass.class, packageName, className,
                                                                     Opcodes.ACC_PUBLIC);

        final JavaMethod method = newType.addNode(JavaMethodMethod.class, methodName);
        // FIXME use the other helper methods
        //helper.setMethodData(method, Opcodes.ACC_PUBLIC);
        this.graphSession.addLink(TypeDeclares.class, newType, method, false);
        return new Pair<JavaType, JavaMethod>(newType, method);
    }

    private SLGraphSession getGraphSession() throws SLGraphFactoryException, SLGraphException {
        final SLGraphFactory factory = new SLGraphFactoryImpl();
        final SLGraph graph = factory.createTempGraph(true);
        this.graphSession = graph.openSession();
        return this.graphSession;
    }

    private TypeResolver getTypeFinder() {
        return new TypeResolver();
    }

    @Test
    public void resolveSimpleMethod() throws Exception {
        final Pair<JavaType, JavaMethod> typeAndMethod = this.createMethod("java.lang", "Object", "toString()");

        final JavaMethodMethod foundMethod = this.methodResolver.getMethod(typeAndMethod.getK1(), "toString");
        Assert.assertEquals(foundMethod.getID(), typeAndMethod.getK2().getID());

        Assert.assertEquals(false, foundMethod.getContext().equals(this.abstractContex));

    }

    @Before
    public void setup() throws SLGraphFactoryException, SLGraphException {
        this.methodResolver = new MethodResolver(this.getTypeFinder(), this.getGraphSession());
        this.setupGraph();
    }

    private void setupGraph() throws SLContextAlreadyExistsException, SLGraphSessionException {
        this.abstractContex = this.graphSession.createContext("abstractJavaContext");

        final SLNode currentContextRootNode = this.graphSession.createContext("test").getRootNode();
        final SLNode abstractContextRootNode = this.abstractContex.getRootNode();
        this.helper = new JavaGraphNodeSupport(this.graphSession, currentContextRootNode, abstractContextRootNode);
    }
}
