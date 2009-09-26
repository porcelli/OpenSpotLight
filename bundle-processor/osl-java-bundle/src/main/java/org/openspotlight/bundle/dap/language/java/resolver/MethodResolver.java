/*
 * OpenSpotLight - Open Source IT Governance Platform
 *  
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA 
 * or third-party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  All third-party 
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E 
 * TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU 
 * Lesser General Public License, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License  for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this distribution; if not, write to: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA 
 * 
 *********************************************************************** 
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os 
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.  
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
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

import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQuery;
import org.openspotlight.graph.query.SLQueryResult;

/**
 * This is a support class that resolves the methods.
 * 
 * @author porcelli
 */
public class MethodResolver<T extends SLNode, M extends SLNode> {

    /** The type resolver. */
    private TypeResolver<T>         typeResolver                  = null;

    /** The graph session. */
    private SLGraphSession          graphSession                  = null;

    /** Class that defines the method super type. */
    private Class<? extends SLNode> methodSuperType               = null;

    /** Class that defines the link between type and method. */
    private Class<? extends SLLink> typeMethodLink                = null;

    /** Class that defines the link between method and its parameter types. */
    private Class<? extends SLLink> methodParameterDefinitionLink = null;

    /** The cache for fastest method resolution . */
    private Map<String, String>     cache                         = null;

    /**
     * Instantiates a new method resolver.
     * 
     * @param typeResolver the type resolver
     * @param graphSession the graph session
     * @param methodSuperType class that defines the method super type
     * @param typeMethodLink class that defines the link between type and method
     * @param methodParameterDefinitionLink class that defines the link between method and its parameter types
     */
    public MethodResolver(
                           final TypeResolver<T> typeResolver, final SLGraphSession graphSession,
                           final Class<? extends SLNode> methodSuperType,
                           final Class<? extends SLLink> typeMethodLink,
                           final Class<? extends SLLink> methodParameterDefinitionLink ) {
        checkNotNull("typeResolver", typeResolver);
        checkNotNull("graphSession", graphSession);
        checkNotNull("methodSuperType", methodSuperType);
        checkNotNull("typeMethodLink", typeMethodLink);
        checkNotNull("methodParameterDefinitionLink", methodParameterDefinitionLink);

        this.typeResolver = typeResolver;
        this.graphSession = graphSession;
        this.methodSuperType = methodSuperType;
        this.typeMethodLink = typeMethodLink;
        this.methodParameterDefinitionLink = methodParameterDefinitionLink;
        this.cache = new HashMap<String, String>();
    }

    /**
     * Returns the method based on type (that declares the method) and method name
     * 
     * @param type the type
     * @param methodName the method name
     * @return the found method
     * @throws SLGraphSessionException general graph session exception
     * @throws SLBundleException if method not found.
     */
    public <XM extends M> XM getMethod( final T type,
                                        final String methodName ) throws SLGraphSessionException, SLBundleException {
        return getMethod(type, methodName, null);
    }

    /**
     * Returns the method based on type (that declares the method), method name and its param types.
     * 
     * @param type the type
     * @param methodName the method name
     * @param paramTypes the param types
     * @return the method
     * @throws SLGraphSessionException general graph session exception
     * @throws SLBundleException if method not found.
     */
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

        XM chachedMethod = getCachedData(type, methodName, paramTypes);
        if (chachedMethod != null) {
            return chachedMethod;
        }

        List<T> typeHierarchy = typeResolver.getTypesLowerHigherLast(type);

        SLQuery query = graphSession.createQuery();

        //FIXME: BUG! está retornando todos os JavaMethods, não apenas os selecionados no primeiro select
        query.select()
             .type(methodSuperType.getName()).subTypes().comma()
             .byLink(typeMethodLink.getName()).b()
             .selectEnd()
             .select()
             .type(methodSuperType.getName()).subTypes()
             .selectEnd()
             .where()
             .type(methodSuperType.getName()).subTypes()
             .each().link(methodParameterDefinitionLink.getName()).a().count().equalsTo().value(paramSize)
             .typeEnd()
             .whereEnd();

        for (T activeType : typeHierarchy) {
            List<T> inputType = new LinkedList<T>();
            inputType.add(activeType);

            SLQueryResult result = query.execute((Collection<SLNode>)inputType);

            //FIXME: where cast bug fixed... : REMOVE THIS! & bug at query
            List<XM> validMethods = new LinkedList<XM>();
            for (SLNode activeMethod : result.getNodes()) {
                //FIXME 
                activeMethod = graphSession.getNodeByID(activeMethod.getID());
                if (activeMethod.getParent().getID().equals(activeType.getID())) {
                    validMethods.add((XM)activeMethod);
                }
            }

            if ((paramSize == 0) && (validMethods.size() > 0)) {
                return cacheFoundMethod(type, methodName, paramTypes, validMethods.get(0));
            }

            Map<XM, T[]> possibleMethods = new TreeMap<XM, T[]>();
            //Now checks for paramTypes
            for (XM activeMethod : validMethods) {
                T[] orderedParams = (T[])new SLNode[paramSize];

                Collection<? extends SLLink> links = graphSession.getUnidirectionalLinksBySource(
                                                                                                 methodParameterDefinitionLink,
                                                                                                 activeMethod);

                for (SLLink methodParameterDefinition : links) {
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
                return cacheFoundMethod(type, methodName, paramTypes, possibleMethods.entrySet().iterator().next().getKey());
            }

            if (possibleMethods.size() > 0) {
                return cacheFoundMethod(type, methodName, paramTypes, getBestMatch(possibleMethods, paramTypes));
            }
        }

        throw new SLBundleException();
    }

    /**
     * Returns the best match based on param types.
     * 
     * @param methods the found methods that has the same number of parameters and match the types
     * @param paramTypes the param types
     * @return the best match
     * @throws SLBundleException if method not found
     */
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

    /**
     * Checks if is contrete type.
     * 
     * @param types the types
     * @return true, if is contrete type
     */
    private boolean isContreteType( final List<T> types ) {
        for (T t : types) {
            if (!typeResolver.isContreteType(t)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if is contrete type.
     * 
     * @param type the type
     * @return true, if is contrete type
     */
    private boolean isContreteType( final T type ) {
        return typeResolver.isContreteType(type);
    }

    /**
     * Check what is the best match.
     * 
     * @param reference the reference
     * @param t1 the first type
     * @param t2 the second type
     * @return the best type match
     */
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

    /**
     * The Enum that defines the best type match.
     * 
     * @author porcelli
     */
    private enum BestTypeMatch {

        /** The First Type is the best match. */
        T1,

        /** The Second Type is the best match. */
        T2,

        /** They are same. */
        SAME
    };

    /**
     * Caches the found method.
     * 
     * @param type the type
     * @param methodName the method name
     * @param paramTypes the param types
     * @param foundMethod the found method
     * @return the found method
     * @throws SLBundleException
     */
    private <XM extends M> XM cacheFoundMethod( final T type,
                                                final String methodName,
                                                final List<T> paramTypes,
                                                XM foundMethod ) throws SLBundleException {
        try {
            String cachedId = getUniqueId(type, methodName, paramTypes);
            cache.put(cachedId, foundMethod.getID());
            return foundMethod;
        } catch (SLGraphSessionException e) {
            throw new SLBundleException();
        }
    }

    /**
     * Gets the cached data.
     * 
     * @param type the type
     * @param methodName the method name
     * @param paramTypes the param types
     * @return the cached result or null if it is not cached
     */
    @SuppressWarnings( "unchecked" )
    private <XM extends M> XM getCachedData( final T type,
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

    /**
     * Gets the unique id based on parameters.
     * 
     * @param type the type
     * @param methodName the method name
     * @param paramTypes the param types
     * @return the unique id
     * @throws SLGraphSessionException the SL graph session exception
     */
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

    /**
     * Gets the cache.
     * 
     * @return the cache
     */
    Map<String, String> getCache() {
        return cache;
    }
}
