package org.openspotlight.bundle.dap.language.java.resolver;

import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.openspotlight.bundle.dap.language.java.metamodel.link.MethodParameterDefinition;
import org.openspotlight.bundle.dap.language.java.metamodel.link.TypeDeclares;
import org.openspotlight.bundle.dap.language.java.metamodel.node.JavaMethod;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQuery;
import org.openspotlight.graph.query.SLQueryResult;

public class MethodResolver<T extends SLNode, M extends SLNode> {

    private TypeResolver<T>     typeResolver = null;
    private SLGraphSession      graphSession = null;
    private Map<String, String> cache        = null;

    public MethodResolver(
                           final TypeResolver<T> typeResolver, final SLGraphSession graphSession ) {
        checkNotNull("typeResolver", typeResolver);
        checkNotNull("graphSession", graphSession);

        this.typeResolver = typeResolver;
        this.graphSession = graphSession;
        cache = new HashMap<String, String>();
    }

    public <XM extends M> XM getMethod( final T type,
                                        final String methodName ) throws SLGraphSessionException, SLBundleException {
        return getMethod(type, methodName, null);
    }

    public <XM extends M> XM getMethod( final T type,
                                        final String methodName,
                                        final List<T> paramTypes ) throws SLGraphSessionException, SLBundleException {
        checkNotNull("type", type);
        checkNotEmpty("methodName", methodName);

        if (!isContreteType(type)) {
            throw new IllegalArgumentException("INVALID PARAMETER");
        }

        int paramSize = 0;
        if (paramTypes != null) {
            if (!isContreteType(paramTypes)) {
                throw new IllegalArgumentException("INVALID PARAMETER");
            }
            paramSize = paramTypes.size();
        }

        XM chachedMethod = getCachedResult(type, methodName, paramTypes);
        if (chachedMethod != null) {
            return chachedMethod;
        }

        List<T> typeHierarchy = typeResolver.getTypesLowerHigherLast(type);

        SLQuery query = graphSession.createQuery();

        query.selectByLinkType().type(JavaMethod.class.getName()).subTypes().comma().byLink(TypeDeclares.class.getName()).b().selectEnd();
        //TODO enable after VITOR implements WHERE ByLinkCount 
        //        .selectByLinkCount()
        //                    .type(JavaMethod.class.getName()).subTypes()
        //                .selectEnd()
        //                .where()
        //                    .type(JavaMethod.class.getName()).subTypes()
        //                        .each().link(MethodParameterDefinition.class.getName()).b().count().equalsTo().value(paramSize)
        //                    .typeEnd()
        //                .whereEnd();

        for (T activeType : typeHierarchy) {
            List<T> inputType = new LinkedList<T>();
            inputType.add(activeType);

            SLQueryResult result = query.execute((Collection<SLNode>)inputType);

            //TODO: where link count available: REMOVE THIS!
            List<XM> validMethods = new LinkedList<XM>();
            for (SLNode activeMethod : result.getNodes()) {
                //FIXME report this error to VITOR! - SHOULD USE DIRECT activeMethod, but it does not WORK! INVALID CAST EXCEPTION! -> se tiver no classpath ele tem q retornar o Type COrreto, caso n‹o-> retornar SLNode
                activeMethod = graphSession.getNodeByID(activeMethod.getID());
                if (graphSession.getNodesByLink(MethodParameterDefinition.class, activeMethod).size() == paramSize) {
                    validMethods.add((XM)activeMethod);
                }
            }

            if ((paramSize == 0) && (validMethods.size() > 0)) {
                //WTF?!
                validMethods.get(0);
            }

            Map<XM, T[]> possibleMethods = new TreeMap<XM, T[]>();
            //Now checks for paramTypes
            for (XM activeMethod : validMethods) {
                T[] orderedParams = (T[])new SLNode[paramSize];
                Collection<MethodParameterDefinition> links = graphSession.getUnidirectionalLinksBySource(
                                                                                                          MethodParameterDefinition.class,
                                                                                                          activeMethod);

                for (MethodParameterDefinition methodParameterDefinition : links) {
                    orderedParams[methodParameterDefinition.getProperty(Integer.class, "Order").getValue()] = (T)methodParameterDefinition.getTarget();
                }

                boolean isValidMethod = true;
                for (int i = 0; i < orderedParams.length; i++) {
                    T activeParamType = orderedParams[i];
                    if (!typeResolver.isTypeOf(paramTypes.get(i), activeParamType)) {
                        isValidMethod = false;
                        break;
                    }
                }
                if (isValidMethod) possibleMethods.put(activeMethod, orderedParams);
            }
            if (possibleMethods.size() == 1) {
                return foundMethod(type, methodName, paramTypes, possibleMethods.entrySet().iterator().next().getKey());
            }

            if (possibleMethods.size() > 0) {
                return foundMethod(type, methodName, paramTypes, getBestMatch(possibleMethods, paramTypes));
            }
        }

        throw new SLBundleException();
    }

    public <XM extends M> XM getBestMatch( final Map<XM, T[]> methods,
                                           final List<T> paramTypes ) throws SLBundleException {

        Map<XM, T[]> possibleMethods = methods;

        int maxLooping = methods.size() + 1;
        int loopCount = 0;

        while (true) {
            loopCount++;
            if (loopCount == maxLooping) {
                throw new SLBundleException();
            }

            Iterator<Entry<XM, T[]>> methodIterator = possibleMethods.entrySet().iterator();
            Entry<XM, T[]> firstMethod = methodIterator.next();
            if (!methodIterator.hasNext()) {
                return firstMethod.getKey();
            }
            Entry<XM, T[]> secondMethod = methodIterator.next();

            for (int i = 0; i < paramTypes.size(); i++) {
                T activeParam = paramTypes.get(i);
                T firstMethodParam = firstMethod.getValue()[i];
                T secondMethodParam = secondMethod.getValue()[i];

                BestTypeMatch bestTypeResult = bestMatch(activeParam, firstMethodParam, secondMethodParam);

                if (bestTypeResult == BestTypeMatch.T1) {
                    methods.remove(secondMethod.getKey());
                    maxLooping = methods.size() + 1;
                    loopCount = 0;
                }
            }
        }
    }

    private boolean isContreteType( final List<T> types ) {
        for (T t : types) {
            if (!typeResolver.isContreteType(t)) {
                return false;
            }
        }
        return true;
    }

    private boolean isContreteType( final T type ) {
        return typeResolver.isContreteType(type);
    }

    private BestTypeMatch bestMatch( T reference,
                                     T t1,
                                     T t2 ) {

        //This prevent problem with boxing and unboxing
        if (reference.equals(t1)) {
            if (t1.equals(t2)) {
                return BestTypeMatch.SAME;
            }
            return BestTypeMatch.T1;
        }

        int referenceParentsCount = typeResolver.countParents(reference);
        int t1ParentsCount = typeResolver.countParents(t1);
        int t2ParentsCount = typeResolver.countParents(t2);

        int x = referenceParentsCount - t1ParentsCount;
        int y = referenceParentsCount - t2ParentsCount;

        if (y == x) {
            return BestTypeMatch.SAME;
        } else if (y > x) {
            return BestTypeMatch.T1;
        }

        return BestTypeMatch.T2;
    }

    private enum BestTypeMatch {
        T1,
        T2,
        SAME
    };

    private <XM extends M> XM foundMethod( final T type,
                                           final String methodName,
                                           final List<T> paramTypes,
                                           XM foundMethod ) {
        try {
            String cachedId = getUniqueId(type, methodName, paramTypes);
            cache.put(cachedId, foundMethod.getID());
            return foundMethod;
        } catch (SLGraphSessionException e) {
            return null;
        }
    }

    @SuppressWarnings( "unchecked" )
    private <XM extends M> XM getCachedResult( final T type,
                                               final String methodName,
                                               final List<T> paramTypes ) {
        String cachedId = "";
        try {
            cachedId = getUniqueId(type, methodName, paramTypes);
            String id = cache.get(cachedId);
            if (id != null) {
                return (XM)graphSession.getNodeByID(id);
            }
        } catch (Exception e) {
            cache.remove(cachedId);
        }
        return null;
    }

    String getUniqueId( T type,
                        String methodName,
                        List<T> paramTypes ) throws SLGraphSessionException {
        StringBuilder sb = new StringBuilder();
        sb.append(type.getID());
        sb.append(":");
        sb.append(methodName);
        sb.append(":");

        if (paramTypes != null) {
            for (T t : paramTypes) {
                sb.append(t.getID());
                sb.append(":");
            }
        }

        return sb.toString();
    }

    Map<String, String> getCache() {
        return cache;
    }

}
