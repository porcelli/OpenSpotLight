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
package org.openspotlight.graph.internal;

import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.openspotlight.common.Pair.newPair;
import static org.openspotlight.common.util.Conversion.convert;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Sha1.getNumericSha1Signature;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.common.Pair;
import org.openspotlight.common.Pair.PairEqualsMode;
import org.openspotlight.graph.ContextSLNode;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLElement;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLTreeLineReference;
import org.openspotlight.graph.annotation.SLDefineHierarchy;
import org.openspotlight.graph.exception.PropertyNotFoundException;
import org.openspotlight.storage.AbstractSTStorageSession.STUniqueKeyBuilderImpl;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STPartitionFactory;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;

import com.google.common.collect.ImmutableSet;

public class SLNodeFactory {

    public static final String WEIGHT        = "node_weigth";
    public static final String CORRECT_CLASS = "node_concrete_class";
    public static final String NAME          = "name";

    private static BigInteger findWeigth( final Class<? extends SLNode> type ) {
        Class<?> currentType = type;
        int depth = 0;
        while (currentType != null) {
            if (!SLNode.class.isAssignableFrom(currentType)) {
                throw logAndReturn(new IllegalStateException(
                                                             "No SLNode inherited type found with annotation "
                                                             + SLDefineHierarchy.class.getSimpleName()));
            }
            if (currentType.isAnnotationPresent(SLDefineHierarchy.class)) {
                return weightFromClass((Class<? extends SLNode>)currentType)
                                                                            .add(BigInteger.valueOf(depth));
            }
            currentType = currentType.getSuperclass();
            depth++;
        }
        throw logAndReturn(new IllegalStateException(
                                                     "No SLNode inherited type found with annotation "
                                                     + SLDefineHierarchy.class.getSimpleName()));
    }

    private static BigInteger weightFromClass(
                                               Class<? extends SLNode> currentType ) {
        return getNumericSha1Signature(currentType.getName());
    }

    @SuppressWarnings( "unchecked" )
    private static <T extends SLNode> T createNode( STPartitionFactory factory,
                                                    STStorageSession session,
                                                    String contextId,
                                                    String parentId,
                                                    Class<T> clazz,
                                                    String name,
                                                    Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                                    Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion,
                                                    STRepositoryPath repositoryPath ) {
        Map<String, Class<? extends Serializable>> propertyTypes = newHashMap();
        Map<String, Serializable> propertyValues = newHashMap();
        PropertyDescriptor[] descriptors = PropertyUtils
                                                        .getPropertyDescriptors(clazz);
        STNodeEntry node = null;
        if (contextId == null) {
            if (clazz.equals(ContextSLNode.class)) {
                contextId = name;
            } else {
                throw new IllegalStateException();
            }
        }
        STPartition partition = factory.getPartitionByName(contextId);
        STUniqueKey internalNodeKey;
        Class<? extends SLNode> targetNodeType = findTargetClass(clazz);

        if (session != null) {
            internalNodeKey = session.withPartition(partition).createKey(
                                                                         targetNodeType.getName()).withEntry(NAME, name)
                                     .andCreate();
            node = session.withPartition(partition).createCriteria()
                          .withUniqueKey(internalNodeKey).buildCriteria()
                          .andFindUnique(session);
        } else {
            internalNodeKey = new STUniqueKeyBuilderImpl(targetNodeType
                                                                       .getName(), partition, repositoryPath).withEntry(NAME,
                                                                                                                        name).andCreate();
        }

        for (PropertyDescriptor d : descriptors) {
            propertyTypes.put(d.getName(), (Class<? extends Serializable>)d
                                                                           .getPropertyType());
            Serializable value = node != null ? (Serializable)convert(node
                                                                          .getPropertyAsString(session, d.getName()), d
                                                                                                                       .getPropertyType()) : null;
            propertyValues.put(d.getName(), value);
        }

        SLNodeImpl internalNode = new SLNodeImpl(name, clazz, targetNodeType,
                                                 internalNodeKey.getKeyAsString(), propertyTypes,
                                                 propertyValues, parentId, contextId);
        if (node != null) {
            internalNode.cachedEntry = new WeakReference<STNodeEntry>(node);
            String weigthAsString = node.getPropertyAsString(session, WEIGHT);
            BigInteger weightFromTargetNodeType = findWeigth(clazz);
            if (weigthAsString != null) {
                BigInteger weightAsBigInteger = new BigInteger(weigthAsString);
                if (weightAsBigInteger.compareTo(weightFromTargetNodeType) > 0) {
                    setWeigthAndTypeOnNode(session, node, clazz,
                                           weightFromTargetNodeType);
                }
            } else {
                setWeigthAndTypeOnNode(session, node, clazz,
                                       weightFromTargetNodeType);
            }
        }
        final Enhancer e = new Enhancer();
        e.setSuperclass(clazz);
        e.setCallback(new SLNodeInterceptor(internalNode));
        return (T)e.create(new Class[0], new Object[0]);
    }

    private static void setWeigthAndTypeOnNode( STStorageSession session,
                                                STNodeEntry node,
                                                Class<? extends SLNode> type,
                                                BigInteger weightFromTargetNodeType ) {
        node.setIndexedProperty(session, WEIGHT, weightFromTargetNodeType
                                                                         .toString());
        node.setIndexedProperty(session, CORRECT_CLASS, type.getName());
    }

    private static Class<? extends SLNode> findTargetClass(
                                                            final Class<? extends SLNode> type ) {
        Class<?> currentType = type;
        while (currentType != null) {
            if (!SLNode.class.isAssignableFrom(currentType)) {
                throw logAndReturn(new IllegalStateException(
                                                             "No SLNode inherited type found with annotation "
                                                             + SLDefineHierarchy.class.getSimpleName()));
            }
            if (currentType.isAnnotationPresent(SLDefineHierarchy.class)) {
                return (Class<? extends SLNode>)currentType;
            }
            currentType = currentType.getSuperclass();
        }
        throw logAndReturn(new IllegalStateException(
                                                     "No SLNode inherited type found with annotation "
                                                     + SLDefineHierarchy.class.getSimpleName()));
    }

    private static STNodeEntry retrievePreviousNode( STPartitionFactory factory,
                                                     STStorageSession session,
                                                     SLContext context,
                                                     SLNodeImpl node ) {
        STNodeEntry internalNode = node.cachedEntry != null ? node.cachedEntry
                                                                              .get() : null;
        if (internalNode == null) {
            STPartition partition = factory.getPartitionByName(context.getID());
            internalNode = session.withPartition(partition).createWithName(
                                                                           node.targetNode.getName()).withKeyEntry(NAME, node.name)
                                  .withParentAsString(node.parentId).andCreate();
            node.cachedEntry = new WeakReference<STNodeEntry>(internalNode);
        }
        return internalNode;
    }

    public static <T extends SLNode> T createNode( STPartitionFactory factory,
                                                   STStorageSession session,
                                                   String contextId,
                                                   String parentId,
                                                   Class<T> clazz,
                                                   String name,
                                                   Collection<Class<? extends SLLink>> linkTypesForLinkDeletion,
                                                   Collection<Class<? extends SLLink>> linkTypesForLinkedNodeDeletion ) {
        return createNode(factory, session, contextId, parentId, clazz, name,
                          linkTypesForLinkDeletion, linkTypesForLinkedNodeDeletion, null);

    }

    private static class SLNodeImpl extends SLNode {

        private final String contextId;

        public String getContextId() {
            return contextId;
        }

        private WeakReference<STNodeEntry>                       cachedEntry;

        private final Class<? extends SLNode>                    targetNode;

        private String                                           caption;

        private final String                                     name;

        private final Class<? extends SLNode>                    type;

        private final String                                     id;
        private final String                                     parentId;

        private final String                                     typeName;

        private final BigInteger                                 weight;

        private SLContext                                        context;

        private final Map<String, Class<? extends Serializable>> propertyTypes;
        private final Map<String, Serializable>                  propertyValues;
        private final AtomicBoolean                              dirty;

        private Set<String>                                      removedProperties;

        private SLNodeImpl( String name, Class<? extends SLNode> type,
                            Class<? extends SLNode> targetNode, String id,
                            Map<String, Class<? extends Serializable>> propertyTypes,
                            Map<String, Serializable> propertyValues, String parentId,
                            String contextId ) {
            dirty = new AtomicBoolean();
            this.type = type;
            this.typeName = type.getName();
            this.name = name;
            this.weight = findWeigth(type);
            this.id = id;
            this.propertyTypes = propertyTypes;
            this.propertyValues = propertyValues;
            this.removedProperties = newHashSet();
            this.targetNode = targetNode;
            this.parentId = parentId;
            this.contextId = contextId;
        }

        public void resetDirtyFlag() {
            dirty.set(false);
            removedProperties.clear();
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption( String caption ) {
            this.caption = caption;
        }

        public String getName() {
            return name;
        }

        @Override
        public int compareTo( SLNode o ) {
            return weight.compareTo(o.getWeight());
        }

        @Override
        public void createLineReference( int beginLine,
                                         int endLine,
                                         int beginColumn,
                                         int endColumn,
                                         String statement,
                                         String artifactId ) {
            throw new UnsupportedOperationException(
                                                    "Should be lazy property? :D");

        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Set<Pair<String, Serializable>> getProperties() {
            ImmutableSet.Builder<Pair<String, Serializable>> builder = ImmutableSet
                                                                                   .builder();
            for (Map.Entry<String, ? extends Serializable> entry : propertyValues
                                                                                 .entrySet()) {
                builder.add(newPair(entry.getKey(), (Serializable)entry
                                                                       .getValue(), PairEqualsMode.K1));
            }
            return builder.build();
        }

        @Override
        public Iterable<String> getPropertyKeys() {
            return ImmutableSet.copyOf(propertyTypes.keySet());
        }

        @Override
        public <V extends Serializable> V getPropertyValue( String key )
                throws PropertyNotFoundException {
            return (V)propertyValues.get(key);
        }

        @Override
        public <V extends Serializable> V getPropertyValue( String key,
                                                            V defaultValue ) {
            V value = (V)propertyValues.get(key);
            return value == null ? defaultValue : value;
        }

        @Override
        public String getPropertyValueAsString( String key )
                throws PropertyNotFoundException {
            return convert(propertyValues.get(key), String.class);
        }

        @Override
        public SLTreeLineReference getTreeLineReferences() {
            throw new UnsupportedOperationException(
                                                    "Should be lazy property? :D");
        }

        @Override
        public SLTreeLineReference getTreeLineReferences( String artifactId ) {
            throw new UnsupportedOperationException(
                                                    "Should be lazy property? :D");
        }

        @Override
        public String getTypeName() {
            return typeName;
        }

        @Override
        public BigInteger getWeight() {
            return weight;
        }

        @Override
        public boolean hasProperty( String key ) throws IllegalArgumentException {
            return propertyTypes.containsKey(key);
        }

        @Override
        public void removeProperty( String key ) {
            propertyTypes.remove(key);
            propertyValues.remove(key);
            removedProperties.add(key);
            dirty.set(true);
        }

        @Override
        public <V extends Serializable> void setProperty( String key,
                                                          V value )
                throws IllegalArgumentException {
            if (!hasProperty(key))
                throw logAndReturn(new IllegalArgumentException(
                                                                "invalid property key " + key + " for type "
                                                                + getTypeName()));
            if (value != null && !propertyTypes.get(key).isInstance(value)) {
                throw logAndReturn(new IllegalArgumentException(
                                                                "invalid property type " + value.getClass().getName()
                                                                + " for type " + getTypeName() + " (should be "
                                                                + propertyTypes.get(key).getName() + ")"));
            }
            propertyValues.put(key, value);
            dirty.set(true);
        }

        @Override
        public boolean isDirty() {
            return dirty.get();
        }

    }

    private static class SLNodeInterceptor implements MethodInterceptor {

        private final SLNodeImpl internalNodeImpl;

        public SLNodeInterceptor( SLNodeImpl nodeImpl ) {
            this.internalNodeImpl = nodeImpl;
        }

        @Override
        public Object intercept( Object obj,
                                 Method method,
                                 Object[] args,
                                 MethodProxy proxy ) throws Throwable {

            Class<?> declarringClass = method.getDeclaringClass();
            boolean methodFromSLNode = declarringClass.equals(SLNode.class)
                                       || declarringClass.equals(SLElement.class);
            String methodName = method.getName();
            if (methodFromSLNode && Modifier.isAbstract(method.getModifiers())) {
                if (isSetterMethod(methodName, method)) {
                    updateValueOnThisImpl(obj, proxy, args);
                }
                return method.invoke(internalNodeImpl, args);
            } else {
                if (isSetterMethod(methodName, method)) {
                    updateValueOnInternalImpl(methodName, method, args);
                }
                return proxy.invokeSuper(obj, args);
            }
        }

        private void updateValueOnThisImpl( Object obj,
                                            MethodProxy method,
                                            Object[] args ) throws Throwable {
            method.invokeSuper(obj, args);
        }

        private void updateValueOnInternalImpl( String methodName,
                                                Method method,
                                                Object[] args ) {
            internalNodeImpl.setProperty(methodName.substring(3),
                                         (Serializable)args[0]);

        }

        private boolean isSetterMethod( String methodName,
                                        Method method ) {
            if (methodName.startsWith("set")
                    && method.getParameterTypes().length == 1
                    && internalNodeImpl.hasProperty(methodName.substring(3))) {
                return true;
            }
            return false;
        }

    }

}
