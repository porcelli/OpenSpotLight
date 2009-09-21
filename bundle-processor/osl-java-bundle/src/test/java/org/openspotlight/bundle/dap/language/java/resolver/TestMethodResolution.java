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

    @Before
    public void setup() throws SLGraphFactoryException, SLGraphException {
        methodResolver = new MethodResolver(getTypeFinder(), getGraphSession());
        setupGraph();
    }

    private TypeResolver getTypeFinder() {
        return new TypeResolver();
    }

    private SLGraphSession getGraphSession() throws SLGraphFactoryException, SLGraphException {
        SLGraphFactory factory = new SLGraphFactoryImpl();
        SLGraph graph = factory.createTempGraph(true);
        this.graphSession = graph.openSession();
        return this.graphSession;
    }

    @Test
    public void resolveSimpleMethod() throws Exception {
        Pair<JavaType, JavaMethod> typeAndMethod = createMethod("java.lang", "Object", "toString()");

        JavaMethodMethod foundMethod = (JavaMethodMethod)methodResolver.getMethod(typeAndMethod.getK1(), "toString");
        Assert.assertEquals(foundMethod.getID(), typeAndMethod.getK2().getID());

        Assert.assertEquals(false, foundMethod.getContext().equals(abstractContex));

    }

    private void setupGraph() throws SLContextAlreadyExistsException, SLGraphSessionException {
        abstractContex = graphSession.createContext("abstractJavaContext");

        SLNode currentContextRootNode = graphSession.createContext("test").getRootNode();
        SLNode abstractContextRootNode = abstractContex.getRootNode();
        helper = new JavaGraphNodeSupport(graphSession, currentContextRootNode, abstractContextRootNode);
    }

    private Pair<JavaType, JavaMethod> createMethod( String packageName,
                                                     String className,
                                                     String methodName ) throws Exception {
        JavaType newType = helper.addBeforeTypeProcessing(JavaTypeClass.class, packageName, className, Opcodes.ACC_PUBLIC);

        JavaMethod method = newType.addNode(JavaMethodMethod.class, methodName);
        helper.setMethodData(method, Opcodes.ACC_PUBLIC);
        graphSession.addLink(TypeDeclares.class, newType, method, false);
        return new Pair<JavaType, JavaMethod>(newType, method);
    }
}
