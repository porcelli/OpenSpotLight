/**
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
import java.io.InputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.common.Pair;
import org.openspotlight.common.Pair.PairEqualsMode;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Conversion;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.common.util.Reflection;
import org.openspotlight.common.util.SerializationUtil;
import org.openspotlight.common.util.Strings;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.Element;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.LinkDirection;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.PropertyContainer;
import org.openspotlight.graph.TreeLineReference;
import org.openspotlight.graph.TreeLineReference.ArtifactLineReference;
import org.openspotlight.graph.TreeLineReference.SimpleLineReference;
import org.openspotlight.graph.annotation.DefineHierarchy;
import org.openspotlight.graph.annotation.InitialWeight;
import org.openspotlight.graph.annotation.IsMetaType;
import org.openspotlight.graph.annotation.LinkAutoBidirectional;
import org.openspotlight.graph.annotation.TransientProperty;
import org.openspotlight.storage.AbstractStorageSession.NodeKeyBuilderImpl;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.StringKeysSupport;
import org.openspotlight.storage.domain.StorageLink;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.key.NodeKey;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

public class NodeAndLinkSupport {

    private static class LinkImpl extends Link
            implements
            PropertyContainerMetadata<StorageLink>,
            PropertyContainerLineReferenceData {

        private static final int            SOURCE        = 0;

        private static final int            TARGET        = 1;

        private WeakReference<StorageLink>  cachedEntry;

        private int                         count;

        private LinkDirection               linkDirection = LinkDirection.UNIDIRECTIONAL;

        private final Class<? extends Link> linkType;

        private final PropertyContainerImpl propertyContainerImpl;

        private final Node[]                sides         = new Node[2];

        public LinkImpl(final String id, final String linkName,
                        final Class<? extends Link> linkType,
                        final Map<String, Class<? extends Serializable>> propertyTypes,
                        final Map<String, Serializable> propertyValues,
                        final int initialWeigthValue, final int weightValue,
                        final Node source, final Node target,
                        final LinkDirection linkDirection) {
            propertyContainerImpl = new PropertyContainerImpl(id,
                    linkType.getName(), propertyTypes, propertyValues,
                    initialWeigthValue, weightValue, source.getContextId());
            sides[SOURCE] = source;
            sides[TARGET] = target;
            this.linkType = linkType;
            this.linkDirection = linkDirection;

        }

        @Override
        public int compareTo(final Link o) {
            return getId().compareTo(o.getId());
        }

        @Override
        public void createLineReference(final int beginLine, final int endLine,
                                        final int beginColumn, final int endColumn,
                                        final String statement, final String artifactId) {
            propertyContainerImpl.createLineReference(beginLine, endLine,
                    beginColumn, endColumn, statement, artifactId);
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Link)) { return false; }
            final Link that = (Link) obj;
            return getId().equals(that.getId());
        }

        @Override
        public StorageLink getCached() {
            return cachedEntry != null ? cachedEntry.get() : null;
        }

        @Override
        public Iterable<ArtifactLineReference> getCachedLineReference(
                                                                      final String artifactId) {
            return propertyContainerImpl.getCachedLineReference(artifactId);
        }

        @Override
        public String getContextId() {
            return propertyContainerImpl.getContextId();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public String getId() {
            return propertyContainerImpl.getId();
        }

        @Override
        public final int getInitialWeightValue() {
            return propertyContainerImpl.getInitialWeightValue();
        }

        @Override
        public LinkDirection getLinkDirection() {
            return linkDirection;
        }

        @Override
        public Class<? extends Link> getLinkType() {
            return linkType;
        }

        @Override
        public Map<String, Map<String, Set<SimpleLineReference>>> getNewLineReferenceData() {
            return propertyContainerImpl.getNewLineReferenceData();
        }

        @Override
        public Node getOtherSide(final Node node)
                throws IllegalArgumentException {
            if (node.equals(sides[SOURCE])) { return sides[TARGET]; }
            if (node.equals(sides[TARGET])) { return sides[SOURCE]; }
            throw new IllegalArgumentException();
        }

        @Override
        public Set<Pair<String, Serializable>> getProperties() {
            return propertyContainerImpl.getProperties();
        }

        @Override
        public PropertyContainerImpl getPropertyContainerImpl() {
            return propertyContainerImpl;
        }

        @Override
        public Iterable<String> getPropertyKeys() {
            return propertyContainerImpl.getPropertyKeys();
        }

        @Override
        public <V extends Serializable> V getPropertyValue(final String key) {
            return propertyContainerImpl.<V>getPropertyValue(key);
        }

        @Override
        public <V extends Serializable> V getPropertyValue(final String key,
                                                           final V defaultValue) {
            return propertyContainerImpl.getPropertyValue(key, defaultValue);
        }

        @Override
        public String getPropertyValueAsString(final String key) {
            return propertyContainerImpl.getPropertyValueAsString(key);
        }

        @Override
        public Node[] getSides() {
            return new Node[] {sides[SOURCE], sides[TARGET]};
        }

        @Override
        public Node getSource() {
            return sides[SOURCE];
        }

        @Override
        public Node getTarget() {
            return sides[TARGET];
        }

        @Override
        public String getTypeName() {
            return propertyContainerImpl.getTypeName();
        }

        @Override
        public final int getWeightValue() {
            return propertyContainerImpl.getWeightValue();
        }

        @Override
        public int hashCode() {
            return getId().hashCode();
        }

        @Override
        public boolean hasProperty(final String key)
                throws IllegalArgumentException {
            return propertyContainerImpl.hasProperty(key);
        }

        @Override
        public boolean isBidirectional() {
            return linkDirection.equals(LinkDirection.BIDIRECTIONAL);
        }

        @Override
        public boolean isDirty() {
            return propertyContainerImpl.isDirty();
        }

        @Override
        public void removeProperty(final String key) {
            propertyContainerImpl.removeProperty(key);
        }

        public void resetDirtyFlag() {
            propertyContainerImpl.resetDirtyFlag();
        }

        @Override
        public void setCached(final StorageLink entry) {
            cachedEntry = new WeakReference<StorageLink>(
                    entry);

        }

        @Override
        public void setCachedLineReference(final String artifactId,
                                           final Iterable<ArtifactLineReference> newLineReference) {
            propertyContainerImpl.setCachedLineReference(artifactId,
                    newLineReference);
        }

        @Override
        public void setCount(final int value) {
            count = value;
            propertyContainerImpl.markAsDirty();

        }

        @Override
        public <V extends Serializable> void setProperty(final String key,
                                                         final V value)
            throws IllegalArgumentException {
            propertyContainerImpl.setProperty(key, value);
        }

        @Override
        public String toString() {
            return "<" + linkDirection.name().substring(0, 3) + "> Link["
                    + getId() + "]";
        }

    }

    private static class NodeImpl extends Node
            implements
            PropertyContainerMetadata<StorageNode>,
            PropertyContainerLineReferenceData {

        private WeakReference<StorageNode>  cachedEntry;

        private String                      caption;

        private final String                contextId;

        private volatile int                hashCode = 0;

        private final String                name;

        private final BigInteger            numericType;

        private final String                parentId;

        private final PropertyContainerImpl propertyContainerImpl;

        private NodeImpl(final String name, final Class<? extends Node> type,
                         final String id,
                         final Map<String, Class<? extends Serializable>> propertyTypes,
                         final Map<String, Serializable> propertyValues,
                         final String parentId, final String contextId,
                         final int weightValue) {
            propertyContainerImpl = new PropertyContainerImpl(id,
                    type.getName(), propertyTypes, propertyValues,
                    findInitialWeight(type), weightValue, contextId);
            this.name = name;
            numericType = findNumericType(type);
            this.parentId = parentId;
            this.contextId = contextId;
        }

        @Override
        public int compareTo(final Node o) {
            return getId().compareTo(o.getId());
        }

        @Override
        public void createLineReference(final int beginLine, final int endLine,
                                        final int beginColumn, final int endColumn,
                                        final String statement, final String artifactId) {
            propertyContainerImpl.createLineReference(beginLine, endLine,
                    beginColumn, endColumn, statement, artifactId);
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof Node)) { return false; }
            final Node slnode = (Node) obj;

            final boolean result = getId().equals(slnode.getId())
                    && Equals.eachEquality(getParentId(), slnode.getParentId())
                    && Equals.eachEquality(getContextId(),
                            slnode.getContextId());
            return result;
        }

        @Override
        public StorageNode getCached() {
            return cachedEntry != null ? cachedEntry.get() : null;
        }

        @Override
        public Iterable<ArtifactLineReference> getCachedLineReference(
                                                                      final String artifactId) {
            return propertyContainerImpl.getCachedLineReference(artifactId);
        }

        @Override
        public String getCaption() {
            return caption;
        }

        @Override
        public String getContextId() {
            return contextId;
        }

        @Override
        public String getId() {
            return propertyContainerImpl.getId();
        }

        @Override
        public final int getInitialWeightValue() {
            return propertyContainerImpl.getInitialWeightValue();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Map<String, Map<String, Set<SimpleLineReference>>> getNewLineReferenceData() {
            return propertyContainerImpl.getNewLineReferenceData();
        }

        @Override
        public BigInteger getNumericType() {
            return numericType;
        }

        @Override
        public String getParentId() {
            return parentId;
        }

        @Override
        public Set<Pair<String, Serializable>> getProperties() {
            return propertyContainerImpl.getProperties();
        }

        @Override
        public PropertyContainerImpl getPropertyContainerImpl() {
            return propertyContainerImpl;
        }

        @Override
        public Iterable<String> getPropertyKeys() {
            return propertyContainerImpl.getPropertyKeys();
        }

        @Override
        public <V extends Serializable> V getPropertyValue(final String key) {
            return propertyContainerImpl.<V>getPropertyValue(key);
        }

        @Override
        public <V extends Serializable> V getPropertyValue(final String key,
                                                           final V defaultValue) {
            return propertyContainerImpl
                    .<V>getPropertyValue(key, defaultValue);
        }

        @Override
        public String getPropertyValueAsString(final String key) {
            return propertyContainerImpl.getPropertyValueAsString(key);
        }

        @Override
        public String getTypeName() {
            return propertyContainerImpl.getTypeName();
        }

        @Override
        public final int getWeightValue() {
            return propertyContainerImpl.getWeightValue();
        }

        @Override
        public int hashCode() {
            int result = hashCode;
            if (result == 0) {
                result = HashCodes.hashOf(getId(), getParentId(),
                        getContextId());
                hashCode = result;
            }
            return result;
        }

        @Override
        public boolean hasProperty(final String key)
                throws IllegalArgumentException {
            return propertyContainerImpl.hasProperty(key);
        }

        @Override
        public boolean isDirty() {
            return propertyContainerImpl.isDirty();
        }

        @Override
        public void removeProperty(final String key) {
            propertyContainerImpl.removeProperty(key);
        }

        public void resetDirtyFlag() {
            propertyContainerImpl.resetDirtyFlag();
        }

        @Override
        public void setCached(
                              final StorageNode entry) {
            cachedEntry = new WeakReference<StorageNode>(
                    entry);

        }

        @Override
        public void setCachedLineReference(final String artifactId,
                                           final Iterable<ArtifactLineReference> newLineReference) {
            propertyContainerImpl.setCachedLineReference(artifactId,
                    newLineReference);
        }

        @Override
        public void setCaption(final String caption) {
            this.caption = caption;
        }

        @Override
        public <V extends Serializable> void setProperty(final String key,
                                                         final V value)
            throws IllegalArgumentException {
            propertyContainerImpl.setProperty(key, value);
        }

        @Override
        public String toString() {
            return getName() + ":" + getId();
        }

    }

    private static class PropertyContainerInterceptor implements
            MethodInterceptor {

        private static enum MethodType {
            GETTER,
            OTHER,
            SETTER
        }

        private final PropertyContainer internalPropertyContainerImpl;

        public PropertyContainerInterceptor(
                                            final PropertyContainer propertyContainerImpl) {
            internalPropertyContainerImpl = propertyContainerImpl;
        }

        private MethodType getMethodType(final String methodName,
                                         final Method method) {
            if (method.isAnnotationPresent(TransientProperty.class)) { return MethodType.OTHER; }
            if (methodName.startsWith("set")
                    && method.getParameterTypes().length == 1
                    && internalPropertyContainerImpl.hasProperty(methodName
                            .substring(3))) {
                return MethodType.SETTER;
            } else if (methodName.startsWith("get")
                    && method.getParameterTypes().length == 0
                    && internalPropertyContainerImpl.hasProperty(methodName
                            .substring(3))) {
                return MethodType.GETTER;
            } else if (methodName.startsWith("is")
                    && method.getParameterTypes().length == 0
                    && internalPropertyContainerImpl.hasProperty(methodName
                            .substring(2))) { return MethodType.GETTER; }
            return MethodType.OTHER;
        }

        private Serializable invokeGetter(final String methodName) {

            final String propertyName = methodName.startsWith("get") ? Strings
                    .firstLetterToLowerCase(methodName.substring(3)) : Strings
                    .firstLetterToLowerCase(methodName.substring(2));// is
            return internalPropertyContainerImpl.getPropertyValue(propertyName);

        }

        private Object invokeSetter(final Object obj, final String methodName,
                                    final Method method, final Object[] args,
                                    final MethodProxy methodProxy)
            throws Throwable {
            internalPropertyContainerImpl.setProperty(methodName.substring(3),
                    (Serializable) args[0]);
            return null;
        }

        @Override
        public Object intercept(final Object obj, final Method method,
                                final Object[] args, final MethodProxy proxy)
            throws Throwable {

            final Class<?> declarringClass = method.getDeclaringClass();
            final boolean methodFromSuperClasses = declarringClass
                    .equals(Node.class)
                    || declarringClass.equals(Link.class)
                    || declarringClass.equals(PropertyContainerImpl.class)
                    || declarringClass.isInterface()
                    || declarringClass.equals(Object.class);
            final String methodName = method.getName();
            if (methodFromSuperClasses) {
                return method.invoke(internalPropertyContainerImpl, args);
            } else {
                switch (getMethodType(methodName, method)) {
                    case GETTER:
                        return invokeGetter(methodName);
                    case SETTER:
                        return invokeSetter(obj, methodName, method, args, proxy);
                }
                return proxy.invokeSuper(obj, args);
            }

        }
    }

    private static interface PropertyContainerLineReferenceData {
        Iterable<ArtifactLineReference> getCachedLineReference(String artifactId);

        Map<String, Map<String, Set<SimpleLineReference>>> getNewLineReferenceData();

        void setCachedLineReference(String artifactId,
                                    Iterable<ArtifactLineReference> newLineReference);

    }

    public static class PropertyContainerImpl implements Element,
            PropertyContainerLineReferenceData {

        private final String                                                contextId;

        private final AtomicBoolean                                         dirty;

        private final String                                                id;

        private final int                                                   initialWeightValue;
        // ArtifactId,Statement,lineData
        private final Map<String, Map<String, Set<SimpleLineReference>>>    lineReferenceNewData =
                                                                                                     new HashMap<String, Map<String, Set<SimpleLineReference>>>();
        private final Map<String, Class<? extends Serializable>>            propertyTypes;
        private final Map<String, Serializable>                             propertyValues;
        private final Set<String>                                           removedProperties;

        private SoftReference<Map<String, Iterable<ArtifactLineReference>>> treeLineReference;

        private final String                                                typeName;

        private final int                                                   weightValue;

        public PropertyContainerImpl(final String id, final String typeName,
                                     final Map<String, Class<? extends Serializable>> propertyTypes,
                                     final Map<String, Serializable> propertyValues,
                                     final int initialWeigthValue, final int weightValue,
                                     final String contextId) {
            dirty = new AtomicBoolean();
            this.typeName = typeName;
            initialWeightValue = initialWeigthValue;
            this.weightValue = weightValue;
            this.id = id;
            this.propertyTypes = propertyTypes;
            this.propertyValues = propertyValues;
            removedProperties = newHashSet();
            this.contextId = contextId;
        }

        @Override
        public void createLineReference(final int beginLine, final int endLine,
                                        final int beginColumn, final int endColumn,
                                        final String statement, final String artifactId) {
            Map<String, Set<SimpleLineReference>> artifactEntry = lineReferenceNewData
                    .get(artifactId);
            if (artifactEntry == null) {
                artifactEntry = new HashMap<String, Set<SimpleLineReference>>();
                lineReferenceNewData.put(artifactId, artifactEntry);
            }
            Set<SimpleLineReference> statementEntry = artifactEntry
                    .get(statement);
            if (statementEntry == null) {
                statementEntry = new HashSet<SimpleLineReference>();
                artifactEntry.put(statement, statementEntry);
            }
            statementEntry.add(TreeLineReferenceSupport
                    .createSimpleLineReference(beginLine, endLine, beginColumn,
                            endColumn));
            dirty.set(true);
        }

        @Override
        public Iterable<ArtifactLineReference> getCachedLineReference(
                                                                      final String artifactId) {
            final Map<String, Iterable<ArtifactLineReference>> cache = treeLineReference == null ? null
                    : treeLineReference.get();
            if (cache == null) { return null; }
            if (artifactId != null) { return cache.get(artifactId); }
            if (cache.isEmpty()) { return null; }
            final Builder<ArtifactLineReference> builder = ImmutableSet.builder();
            for (final Iterable<ArtifactLineReference> val: cache.values()) {
                for (final ArtifactLineReference r: val) {
                    builder.add(r);
                }
            }
            return builder.build();
        }

        @Override
        public String getContextId() {
            return contextId;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public final int getInitialWeightValue() {
            return initialWeightValue;
        }

        @Override
        public Map<String, Map<String, Set<SimpleLineReference>>> getNewLineReferenceData() {
            return lineReferenceNewData;
        }

        @Override
        public Set<Pair<String, Serializable>> getProperties() {
            final ImmutableSet.Builder<Pair<String, Serializable>> builder = ImmutableSet
                    .builder();
            for (final Map.Entry<String, ? extends Serializable> entry: propertyValues
                    .entrySet()) {
                builder.add(newPair(entry.getKey(),
                        (Serializable) entry.getValue(), PairEqualsMode.K1));
            }
            return builder.build();
        }

        @Override
        public Iterable<String> getPropertyKeys() {
            return ImmutableSet.copyOf(propertyTypes.keySet());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V extends Serializable> V getPropertyValue(final String key) {
            return (V) propertyValues.get(key);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V extends Serializable> V getPropertyValue(final String key,
                                                           final V defaultValue) {
            final V value = (V) propertyValues.get(key);
            return value == null ? defaultValue : value;
        }

        @Override
        public String getPropertyValueAsString(final String key) {
            return convert(propertyValues.get(key), String.class);
        }

        @Override
        public String getTypeName() {
            return typeName;
        }

        @Override
        public final int getWeightValue() {
            return weightValue;
        }

        @Override
        public boolean hasProperty(final String key)
                throws IllegalArgumentException {
            return propertyTypes.containsKey(Strings
                    .firstLetterToLowerCase(key));
        }

        @Override
        public boolean isDirty() {
            return dirty.get();
        }

        public void markAsDirty() {
            dirty.set(true);
        }

        @Override
        public void removeProperty(String key) {
            key = Strings.firstLetterToLowerCase(key);
            propertyTypes.remove(key);
            propertyValues.remove(key);
            removedProperties.add(key);
            dirty.set(true);
        }

        public void resetDirtyFlag() {
            dirty.set(false);
            removedProperties.clear();
        }

        @Override
        public void setCachedLineReference(final String artifactId,
                                           final Iterable<ArtifactLineReference> newLineReference) {
            Map<String, Iterable<ArtifactLineReference>> cache = treeLineReference == null ? null
                    : treeLineReference.get();
            if (cache == null) {
                cache = new HashMap<String, Iterable<ArtifactLineReference>>();
                treeLineReference = new SoftReference<Map<String, Iterable<ArtifactLineReference>>>(
                        cache);
            }
            cache.put(artifactId, newLineReference);
        }

        @Override
        public <V extends Serializable> void setProperty(String key,
                                                         final V value)
            throws IllegalArgumentException {
            key = Strings.firstLetterToLowerCase(key);
            if (!hasProperty(key)) { throw logAndReturn(new IllegalArgumentException(
                        "invalid property key " + key + " for type "
                                + getTypeName())); }
            final Class<? extends Serializable> propType = propertyTypes
                    .get(key);
            if (value != null) {
                final Class<?> valueType = Reflection
                        .findClassWithoutPrimitives(value.getClass());
                if (!valueType.isAssignableFrom(propType)) { throw logAndReturn(new IllegalArgumentException(
                            "invalid property type "
                                    + value.getClass().getName() + " for type "
                                    + getTypeName() + " (should be "
                                    + propertyTypes.get(key).getName() + ")"));

                }

            }
            propertyValues.put(key, value);
            dirty.set(true);
        }

    }

    public static interface PropertyContainerMetadata<T> {
        public T getCached();

        public PropertyContainerImpl getPropertyContainerImpl();

        public void setCached(T entry);

    }

    private static final String LINEREF_SUFIX          = "_lineRef";
    public static final String  BIDIRECTIONAL_LINK_IDS = "__bidirectional_link_ids";
    public static final String  CAPTION                = "__node_caption";

    public static final String  CORRECT_CLASS          = "__node_concrete_class";

    public static final String  LINK_DIRECTION         = "__link_direction";

    public static final String  NAME                   = "__node_name";

    public static final String  NODE_ID                = "__node_weigth_value";

    public static final String  NUMERIC_TYPE           = "__node_numeric_type";

    public static final String  WEIGTH_VALUE           = "__node_weigth_value";

    private static void fixTypeData(final StorageSession session,
                                    final Class<? extends Node> clazz,
                                    final StorageNode node) {
        final String numericTypeAsString = node.getPropertyValueAsString(session,
                NUMERIC_TYPE);
        final BigInteger numericTypeFromTargetNodeType = findNumericType(clazz);
        if (numericTypeAsString != null) {
            final BigInteger numericTypeAsBigInteger = new BigInteger(
                    numericTypeAsString);
            if (numericTypeFromTargetNodeType
                    .compareTo(numericTypeAsBigInteger) > 0) {
                setWeigthAndTypeOnNode(session, node, clazz,
                        numericTypeFromTargetNodeType);
            }
        } else {
            setWeigthAndTypeOnNode(session, node, clazz,
                    numericTypeFromTargetNodeType);
        }
    }

    private static BigInteger numericTypeFromClass(
                                                   final Class<? extends Node> currentType) {
        return getNumericSha1Signature(currentType.getName());
    }

    private static void setWeigthAndTypeOnNode(final StorageSession session,
                                               final StorageNode node,
                                               final Class<? extends Node> type,
                                               final BigInteger weightFromTargetNodeType) {
        node.setIndexedProperty(session, NUMERIC_TYPE,
                weightFromTargetNodeType.toString());
        node.setIndexedProperty(session, CORRECT_CLASS, type.getName());

    }

    @SuppressWarnings("unchecked")
    public static <T extends Link> T createLink(final PartitionFactory factory,
                                                final StorageSession session, final Class<T> clazz,
                                                final Node rawOrigin, final Node rawTarget,
                                                final LinkDirection direction, final boolean createIfDontExists) {
        final Map<String, Class<? extends Serializable>> propertyTypes = newHashMap();
        final Map<String, Serializable> propertyValues = newHashMap();
        final PropertyDescriptor[] descriptors = PropertyUtils
                .getPropertyDescriptors(clazz);

        StorageLink linkEntry = null;
        Node origin, target;

        if (rawOrigin.compareTo(rawTarget) == 0) { throw new IllegalStateException(); }

        if (LinkDirection.BIDIRECTIONAL.equals(direction)
                && rawOrigin.compareTo(rawTarget) < 0) {
            origin = rawTarget;
            target = rawOrigin;
        } else {
            origin = rawOrigin;
            target = rawTarget;
        }
        String linkId = null;
        if (session != null) {
            final StorageNode originAsSTNode = session
                    .findNodeByStringKey(origin.getId());
            final StorageNode targetAsSTNode = session
                    .findNodeByStringKey(target.getId());
            if (originAsSTNode == null && createIfDontExists) { throw new IllegalStateException(); }
            if (originAsSTNode != null) {
                if (clazz.isAnnotationPresent(LinkAutoBidirectional.class)
                        && LinkDirection.UNIDIRECTIONAL.equals(direction)) {

                    final StorageLink possibleLink = session.getLink(targetAsSTNode,
                            originAsSTNode, clazz.getName());
                    final StorageLink anotherPossibleLink = session.getLink(
                            originAsSTNode, targetAsSTNode, clazz.getName());
                    if (possibleLink != null && anotherPossibleLink != null) { throw new IllegalStateException(); }
                    if (possibleLink != null
                            && possibleLink.getPropertyValueAsString(session,
                                    LINK_DIRECTION).equals(
                                    LinkDirection.BIDIRECTIONAL.name())) {
                        return createLink(factory, session, clazz, rawOrigin,
                                rawTarget, LinkDirection.BIDIRECTIONAL,
                                createIfDontExists);
                    } else if (anotherPossibleLink != null
                            && anotherPossibleLink.getPropertyValueAsString(session,
                                    LINK_DIRECTION).equals(
                                    LinkDirection.BIDIRECTIONAL.name())) {
                        return createLink(factory, session, clazz, rawTarget,
                                rawOrigin, LinkDirection.BIDIRECTIONAL,
                                createIfDontExists);
                    } else if (possibleLink != null) {
                        if (createIfDontExists) {
                            session.removeLink(possibleLink);
                        }
                        return createLink(factory, session, clazz, rawOrigin,
                                rawTarget, LinkDirection.BIDIRECTIONAL,
                                createIfDontExists);

                    } else if (anotherPossibleLink != null) {
                        if (createIfDontExists) {
                            session.removeLink(anotherPossibleLink);
                        }
                        return createLink(factory, session, clazz, rawOrigin,
                                rawTarget, LinkDirection.BIDIRECTIONAL,
                                createIfDontExists);
                    }
                }
                linkEntry = session.getLink(originAsSTNode, targetAsSTNode,
                        clazz.getName());
                if (linkEntry == null) {
                    if (createIfDontExists) {
                        linkEntry = session.addLink(originAsSTNode,
                                targetAsSTNode, clazz.getName());
                    }
                    if (linkEntry != null) {
                        if (LinkDirection.BIDIRECTIONAL.equals(direction)) {
                            final InputStream objectAsStream = targetAsSTNode
                                    .getPropertyValueAsStream(session,
                                            BIDIRECTIONAL_LINK_IDS);
                            List<String> linkIds;
                            if (objectAsStream != null) {
                                linkIds = SerializationUtil
                                        .deserialize(objectAsStream);
                            } else {
                                linkIds = new ArrayList<String>();
                            }
                            linkIds.add(linkEntry.getKeyAsString());
                            targetAsSTNode.setSimpleProperty(session,
                                    BIDIRECTIONAL_LINK_IDS,
                                    SerializationUtil.serialize(linkIds));
                            targetAsSTNode.setSimpleProperty(session,
                                    LINK_DIRECTION,
                                    LinkDirection.BIDIRECTIONAL.name());
                        }
                    }
                }
            }
            linkId = StringKeysSupport.buildLinkKeyAsString(clazz.getName(), origin.getId(), target.getId());
        }

        for (final PropertyDescriptor d: descriptors) {
            if (d.getName().equals("class")) {
                continue;
            }
            propertyTypes.put(d.getName(),
                    (Class<? extends Serializable>) Reflection
                            .findClassWithoutPrimitives(d.getPropertyType()));
            final Object rawValue = linkEntry != null ? linkEntry
                    .getPropertyValueAsString(session, d.getName()) : null;
            final Serializable value = (Serializable) (rawValue != null ? Conversion
                    .convert(rawValue, d.getPropertyType()) : null);
            propertyValues.put(d.getName(), value);
        }
        int weigthValue;
        final Set<String> stNodeProperties = linkEntry != null ? linkEntry
                .getPropertyNames(session) : Collections.<String>emptySet();
        if (stNodeProperties.contains(WEIGTH_VALUE)) {
            weigthValue = Conversion.convert(
                    linkEntry.getPropertyValueAsString(session, WEIGTH_VALUE),
                    Integer.class);
        } else {
            weigthValue = findInitialWeight(clazz);
        }
        final LinkImpl internalLink = new LinkImpl(linkId, clazz.getName(),
                clazz, propertyTypes, propertyValues, findInitialWeight(clazz),
                weigthValue, origin, target, direction);
        if (linkEntry != null) {
            internalLink.setCached(linkEntry);
            internalLink.linkDirection = direction;
        }
        final Enhancer e = new Enhancer();
        e.setSuperclass(clazz);
        e.setInterfaces(new Class<?>[] {PropertyContainerMetadata.class});
        e.setCallback(new PropertyContainerInterceptor(internalLink));
        return (T) e.create(new Class[0], new Object[0]);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Node> T createNode(
                                                 final PartitionFactory factory,
                                                 final StorageSession session,
                                                 final String contextId,
                                                 final String parentId,
                                                 final Class<T> clazz,
                                                 final String name,
                                                 final boolean needsToVerifyType,
                                                 final Iterable<Class<? extends Link>> linkTypesForLinkDeletion,
                                                 final Iterable<Class<? extends Link>> linkTypesForLinkedNodeDeletion) {
        final Map<String, Class<? extends Serializable>> propertyTypes = newHashMap();
        final Map<String, Serializable> propertyValues = newHashMap();
        final PropertyDescriptor[] descriptors = PropertyUtils
                .getPropertyDescriptors(clazz);
        StorageNode node = null;
        if (contextId == null) { throw new IllegalStateException(); }
        final Partition partition = factory.getPartitionByName(contextId);
        NodeKey internalNodeKey;
        final Class<? extends Node> targetNodeType = findTargetClass(clazz);

        if (session != null) {
            internalNodeKey = session.withPartition(partition)
                    .createKey(targetNodeType.getName())
                    .withSimpleKey(NAME, name).andCreate();
            node = session.withPartition(partition).createCriteria()
                    .withUniqueKey(internalNodeKey).buildCriteria()
                    .andFindUnique(session);
        } else {
            internalNodeKey = new NodeKeyBuilderImpl(targetNodeType.getName(),
                    partition).withSimpleKey(NAME, name)
                    .andCreate();
        }

        for (final PropertyDescriptor d: descriptors) {
            if (d.getName().equals("class")) {
                continue;
            }
            propertyTypes.put(d.getName(),
                    (Class<? extends Serializable>) Reflection
                            .findClassWithoutPrimitives(d.getPropertyType()));
            final Object rawValue = node != null ? node.getPropertyValueAsString(
                    session, d.getName()) : null;
            final Serializable value = (Serializable) (rawValue != null ? Conversion
                    .convert(rawValue, d.getPropertyType()) : null);
            propertyValues.put(d.getName(), value);
        }
        int weigthValue;
        final Set<String> stNodeProperties = node != null ? node
                .getPropertyNames(session) : Collections.<String>emptySet();
        if (stNodeProperties.contains(WEIGTH_VALUE)) {
            weigthValue = Conversion.convert(
                    node.getPropertyValueAsString(session, WEIGTH_VALUE),
                    Integer.class);
        } else {
            weigthValue = findInitialWeight(clazz);
        }
        Class<? extends Node> savedClass = null;
        if (stNodeProperties.contains(CORRECT_CLASS)) {
            savedClass = Conversion.convert(
                    node.getPropertyValueAsString(session, CORRECT_CLASS),
                    Class.class);
        }
        final BigInteger savedClassNumericType = savedClass != null ? findNumericType(savedClass)
                : null;
        final BigInteger proposedClassNumericType = findNumericType(clazz);
        final Class<? extends Node> classToUse = savedClassNumericType != null
                && savedClassNumericType.compareTo(proposedClassNumericType) > 0 ? savedClass
                : clazz;

        final NodeImpl internalNode = new NodeImpl(name, classToUse,
                internalNodeKey.getKeyAsString(), propertyTypes,
                propertyValues, parentId, contextId, weigthValue);
        if (node != null) {
            internalNode.cachedEntry = new WeakReference<StorageNode>(
                    node);
            if (needsToVerifyType) {
                fixTypeData(session, classToUse, node);
            }
            final String captionAsString = node.getPropertyValueAsString(session,
                    CAPTION);
            if (captionAsString != null) {
                internalNode.setCaption(captionAsString);
            }

        }
        final Enhancer e = new Enhancer();
        e.setSuperclass(classToUse);
        e.setInterfaces(new Class<?>[] {PropertyContainerMetadata.class});
        e.setCallback(new PropertyContainerInterceptor(internalNode));
        return (T) e.create(new Class[0], new Object[0]);
    }

    public static int findInitialWeight(final Class<?> clazz) {
        return clazz.getAnnotation(InitialWeight.class).value();
    }

    @SuppressWarnings("unchecked")
    public static BigInteger findNumericType(final Class<? extends Node> type) {
        Class<?> currentType = type;
        int depth = 0;
        while (currentType != null) {
            if (!Node.class.isAssignableFrom(currentType)) { throw logAndReturn(new IllegalStateException(
                        "No SLNode inherited type found with annotation "
                                + DefineHierarchy.class.getSimpleName())); }
            if (currentType.isAnnotationPresent(DefineHierarchy.class)) { return numericTypeFromClass(
                (Class<? extends Node>) currentType)
                        .add(BigInteger.valueOf(depth)); }
            currentType = currentType.getSuperclass();
            depth++;
        }
        throw logAndReturn(new IllegalStateException(
                "No SLNode inherited type found with annotation "
                        + DefineHierarchy.class.getSimpleName() + " for type"
                        + type));
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends Node> findTargetClass(final Class<?> type) {
        Class<?> currentType = type;
        while (currentType != null) {
            if (!Node.class.isAssignableFrom(currentType)) { throw logAndReturn(new IllegalStateException(
                        "No SLNode inherited type found with annotation "
                                + DefineHierarchy.class.getSimpleName())); }
            if (currentType.isAnnotationPresent(DefineHierarchy.class)) { return (Class<? extends Node>) currentType; }
            currentType = currentType.getSuperclass();
        }
        throw logAndReturn(new IllegalStateException(
                "No SLNode inherited type found with annotation "
                        + DefineHierarchy.class.getSimpleName()));
    }

    public static TreeLineReference getTreeLineReferences(
                                                          final StorageSession session, final PartitionFactory factory,
                                                          final Element e,
                                                          final String artifactId) {
        final PropertyContainerImpl asPropertyContainer = ((PropertyContainerMetadata<?>) e)
                .getPropertyContainerImpl();
        Iterable<ArtifactLineReference> cached = asPropertyContainer
                .getCachedLineReference(artifactId);
        if (cached == null) {
            final Partition lineRefPartition = factory.getPartitionByName(e
                    .getContextId() + LINEREF_SUFIX);
            final StorageNode lineRefNode = session.withPartition(lineRefPartition)
                    .createNewSimpleNode(e.getId());
            final Set<String> artifactIds = artifactId != null ? ImmutableSet
                    .of(artifactId) : lineRefNode.getPropertyNames(session);

            final Map<String, Iterable<ArtifactLineReference>> newCacheData =
                new HashMap<String, Iterable<ArtifactLineReference>>();
            for (final String currentArtifactId: artifactIds) {
                final InputStream stream = lineRefNode.getPropertyValueAsStream(session,
                        currentArtifactId);
                if (stream != null) {
                    final ArtifactLineReference artifactLineReference = SerializationUtil
                            .deserialize(stream);
                    final ImmutableSet<ArtifactLineReference> set = ImmutableSet
                            .of(artifactLineReference);
                    newCacheData.put(currentArtifactId, set);
                    asPropertyContainer.setCachedLineReference(
                            currentArtifactId, set);
                }
            }
            cached = TreeLineReferenceSupport.copyOf(
                    lineRefNode.getKeyAsString(), cached,
                    asPropertyContainer.lineReferenceNewData, artifactId)
                    .getArtifacts();
            asPropertyContainer.setCachedLineReference(artifactId, cached);
        }
        return TreeLineReferenceSupport.createTreeLineReference(e.getId(),
                cached);
    }

    public static boolean isMetanode(final Class<? extends Node> clazz) {
        return clazz.isAnnotationPresent(IsMetaType.class);
    }

    @SuppressWarnings("unchecked")
    public static StorageNode retrievePreviousNode(
                                                                                    final PartitionFactory factory,
                                                                                    final StorageSession session,
                                                                                    final Context context, final Node node,
                                                                                    final boolean needsToVerifyType) {
        try {
            final PropertyContainerMetadata<StorageNode> metadata =
                (PropertyContainerMetadata<StorageNode>) node;
            StorageNode internalNode = metadata
                    .getCached();
            if (internalNode == null) {
                final Partition partition = factory.getPartitionByName(context
                        .getId());
                internalNode = session
                        .withPartition(partition)
                        .createWithType(
                                findTargetClass(node.getClass()).getName())
                        .withSimpleKey(NAME, node.getName())
                        .withParent(node.getParentId()).andCreate();
                if (needsToVerifyType) {
                    fixTypeData(session, (Class<? extends Node>) node
                            .getClass().getSuperclass(), internalNode);
                }
                metadata.setCached(internalNode);

            }
            internalNode
                    .setIndexedProperty(session, CAPTION, node.getCaption());
            for (final String propName: node.getPropertyKeys()) {
                final Serializable value = node.getPropertyValue(propName);
                if (!PropertyUtils.getPropertyDescriptor(node, propName)
                        .getReadMethod()
                        .isAnnotationPresent(TransientProperty.class)) {
                    internalNode.setIndexedProperty(session, propName,
                            Conversion.convert(value, String.class));

                }

            }
            return internalNode;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public static void writeTreeLineReference(final StorageSession session,
                                              final PartitionFactory factory, final Element e) {
        final TreeLineReference treeLineReferences = getTreeLineReferences(session,
                factory, e, null);
        final Partition lineRefPartition = factory.getPartitionByName(e
                .getContextId() + LINEREF_SUFIX);
        final StorageNode lineRefNode = session.withPartition(lineRefPartition)
                .createNewSimpleNode(e.getId());
        for (final ArtifactLineReference artifactLineReference: treeLineReferences
                .getArtifacts()) {
            lineRefNode.setSimpleProperty(session,
                    artifactLineReference.getArtifactId(),
                    SerializationUtil.serialize(artifactLineReference));
        }
    }

}
