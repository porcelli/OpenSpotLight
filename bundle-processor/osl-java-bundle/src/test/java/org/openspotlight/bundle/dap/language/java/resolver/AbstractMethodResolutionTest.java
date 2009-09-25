package org.openspotlight.bundle.dap.language.java.resolver;

import org.objectweb.asm.Opcodes;
import org.openspotlight.bundle.dap.language.java.Constants;
import org.openspotlight.bundle.dap.language.java.metamodel.link.Extends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.ImplicitExtends;
import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodParameterDefinition;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaPackage;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaType;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypeClass;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaTypePrimitive;
import org.openspotlight.bundle.dap.language.java.support.JavaGraphNodeSupport;
import org.openspotlight.common.Pair;
import org.openspotlight.common.exception.AbstractFactoryException;
import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphException;
import org.openspotlight.graph.SLGraphFactory;
import org.openspotlight.graph.SLGraphFactoryException;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidNodeTypeException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public abstract class AbstractMethodResolutionTest {

    protected MethodResolver<JavaType, JavaMethod> methodResolver = null;
    protected SLGraphSession                       graphSession   = null;
    protected JavaGraphNodeSupport                 helper         = null;
    protected SLContext                            abstractContex = null;
    protected SLGraph                              graph          = null;

    /**
     * Inits the Graph.
     * 
     * @throws AbstractFactoryException the abstract factory exception
     */
    @BeforeClass
    public void init() throws AbstractFactoryException {
        final SLGraphFactory factory = AbstractFactory.getDefaultInstance(SLGraphFactory.class);
        this.graph = factory.createTempGraph(true);
    }

    /**
     * Finish.
     */
    @AfterClass
    public void finish() {
        this.graph.shutdown();
    }

    /**
     * After test.
     * 
     * @throws SLGraphSessionException the SL graph session exception
     */
    @AfterMethod
    public void afterTest() throws SLGraphSessionException {
        this.graphSession.clear();
    }

    @BeforeMethod
    public void setupGraphSession() throws SLGraphFactoryException, SLGraphException {
        //FIXME this == null should be removed -> NOT I CAN'T OPEN ONE SESSION PER METHOD EXECUTION!!
        if (this.graphSession == null) {
            this.graphSession = this.graph.openSession();
        }

        abstractContex = graphSession.getContext(Constants.ABSTRACT_CONTEXT);
        if (abstractContex == null) {
            abstractContex = graphSession.createContext(Constants.ABSTRACT_CONTEXT);
        }

        if (graphSession.getContext("test") == null) {
            graphSession.createContext("test");
        }

        SLNode currentContextRootNode = graphSession.getContext("test").getRootNode();
        SLNode abstractContextRootNode = abstractContex.getRootNode();
        helper = new JavaGraphNodeSupport(graphSession, currentContextRootNode, abstractContextRootNode);
    }

    protected void setupMethodResolver( TypeResolver<JavaType> typeResolver ) throws SLGraphFactoryException, SLGraphException {
        this.methodResolver = new MethodResolver<JavaType, JavaMethod>(typeResolver, graphSession);
    }

    protected Pair<JavaType, JavaMethod> createMethod( String packageName,
                                                       String typeName,
                                                       String simpleMethodName,
                                                       String fullMethodName ) throws Exception {

        return createMethod(packageName, typeName, simpleMethodName, fullMethodName, false, null);
    }

    protected Pair<JavaType, JavaMethod> createMethod( String packageName,
                                                       String typeName,
                                                       String simpleMethodName,
                                                       String fullMethodName,
                                                       boolean isConstructor ) throws Exception {

        return createMethod(packageName, typeName, simpleMethodName, fullMethodName, isConstructor, null);
    }

    protected Pair<JavaType, JavaMethod> createMethod( String packageName,
                                                       String typeName,
                                                       String simpleMethodName,
                                                       String fullMethodName,
                                                       SLNode... methodParameters ) throws Exception {

        return createMethod(packageName, typeName, simpleMethodName, fullMethodName, false, methodParameters);
    }

    protected Pair<JavaType, JavaMethod> createMethod( String packageName,
                                                       String typeName,
                                                       String simpleMethodName,
                                                       String fullMethodName,
                                                       boolean isConstructor,
                                                       SLNode... methodParameters ) throws Exception {

        JavaType type = helper.addTypeOnCurrentContext(JavaTypeClass.class, packageName, typeName, Opcodes.ACC_PUBLIC);

        return createMethod(type, simpleMethodName, fullMethodName, isConstructor, methodParameters);
    }

    protected Pair<JavaType, JavaMethod> createMethod( JavaType type,
                                                       String simpleMethodName,
                                                       String fullMethodName,
                                                       SLNode... methodParameters ) throws Exception {

        return createMethod(type, simpleMethodName, fullMethodName, false, methodParameters);
    }

    protected Pair<JavaType, JavaMethod> createMethod( JavaType type,
                                                       String simpleMethodName,
                                                       String fullMethodName,
                                                       boolean isConstructor,
                                                       SLNode... methodParameters ) throws Exception {

        JavaMethod method = helper.createMethod(type, fullMethodName, simpleMethodName, isConstructor, Opcodes.ACC_PUBLIC);

        if (methodParameters != null) {
            int position = -1;
            for (SLNode activeParameterType : methodParameters) {
                position++;
                SLLink link = graphSession.addLink(MethodParameterDefinition.class, method, activeParameterType, false);
                link.setProperty(Integer.class, "Order", position);
            }
        }

        return new Pair<JavaType, JavaMethod>(type, method);
    }

    protected JavaType createType( String packageName,
                                   String className,
                                   JavaType extendedType,
                                   boolean isImplicit ) throws Exception {

        JavaType newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, packageName, className, Opcodes.ACC_PUBLIC);

        if (extendedType != null) {
            if (isImplicit) {
                graphSession.addLink(ImplicitExtends.class, newType, extendedType, false);
            } else {
                graphSession.addLink(Extends.class, newType, extendedType, false);
            }
        }

        return newType;
    }

    protected JavaType createTypeParameterized( String packageName,
                                                String className,
                                                JavaType parentType,
                                                JavaType extendedType,
                                                boolean isImplicit ) throws Exception {

        JavaType newType = helper.addTypeOnCurrentContext(JavaTypeClass.class, packageName, className, Opcodes.ACC_PUBLIC);

        if (extendedType != null) {
            if (isImplicit) {
                graphSession.addLink(ImplicitExtends.class, newType, extendedType, false);
            } else {
                graphSession.addLink(Extends.class, newType, extendedType, false);
            }
        }

        return newType;
    }

    protected JavaTypePrimitive createPrimitiveType( String type ) throws Exception {
        return abstractContex.getRootNode().addNode(JavaTypePrimitive.class, type);
    }

    protected JavaType getAbstractType( String packageName,
                                        String className ) throws SLInvalidNodeTypeException, SLGraphSessionException {
        JavaPackage abstractPackage = abstractContex.getRootNode().getNode(JavaPackage.class, packageName);
        if (abstractPackage != null) {
            return abstractPackage.getNode(JavaType.class, className);
        }
        return null;
    }
}
