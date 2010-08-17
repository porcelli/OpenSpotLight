/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH
 * CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de
 * terceiros estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é
 * software livre; você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme
 * publicada pela Free Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença
 * Pública Geral Menor do GNU para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU
 * junto com este programa; se não, escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA
 * 02110-1301 USA
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
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
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
import org.openspotlight.common.util.Strings;
import org.openspotlight.graph.Context;
import org.openspotlight.graph.Element;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.LinkType;
import org.openspotlight.graph.Node;
import org.openspotlight.graph.PropertyContainer;
import org.openspotlight.graph.TreeLineReference;
import org.openspotlight.graph.annotation.DefineHierarchy;
import org.openspotlight.graph.annotation.InitialWeight;
import org.openspotlight.graph.annotation.IsMetaType;
import org.openspotlight.graph.annotation.TransientProperty;
import org.openspotlight.storage.AbstractStorageSession.NodeKeyBuilderImpl;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;
import org.openspotlight.storage.RepositoryPath;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.StringIDSupport;
import org.openspotlight.storage.domain.key.NodeKey;

import com.google.common.collect.ImmutableSet;

public class NodeAndLinkSupport {

    public static interface PropertyContainerMetadata<T> {
        public T getCached();

        public void setCached(
                              T entry);

    }

    public static final String NUMERIC_TYPE  = "__node_numeric_type";
    public static final String CAPTION       = "__node_caption";
    public static final String CORRECT_CLASS = "__node_concrete_class";
    public static final String NAME          = "__node_name";
    public static final String WEIGTH_VALUE  = "__node_weigth_value";
    public static final String NODE_ID       = "__node_weigth_value";

    public static int findInitialWeight(
                                        final Class<?> clazz) {
        return clazz.getAnnotation(InitialWeight.class).value();
    }

    public static boolean isMetanode(
                                     final Class<? extends Node> clazz) {
        return clazz.isAnnotationPresent(IsMetaType.class);
    }

    public static BigInteger findNumericType(
                                             final Class<? extends Node> type) {
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

    private static BigInteger numericTypeFromClass(
                                                   final Class<? extends Node> currentType) {
        return getNumericSha1Signature(currentType.getName());
    }

    @SuppressWarnings("unchecked")
    private static <T extends Node> T createNode(
                                                 final PartitionFactory factory,
                                                 final StorageSession session, final String contextId, final String parentId,
                                                 final Class<T> clazz, final String name, final boolean needsToVerifyType,
                                                 final Collection<Class<? extends Link>> linkTypesForLinkDeletion,
                                                 final Collection<Class<? extends Link>> linkTypesForLinkedNodeDeletion,
                                                 final RepositoryPath repositoryPath) {
        final Map<String, Class<? extends Serializable>> propertyTypes = newHashMap();
        final Map<String, Serializable> propertyValues = newHashMap();
        final PropertyDescriptor[] descriptors = PropertyUtils
            .getPropertyDescriptors(clazz);
        org.openspotlight.storage.domain.Node node = null;
        if (contextId == null) { throw new IllegalStateException(); }
        final Partition partition = factory.getPartitionByName(contextId);
        NodeKey internalNodeKey;
        final Class<? extends Node> targetNodeType = findTargetClass(clazz);

        if (session != null) {
            internalNodeKey = session.withPartition(partition).createKey(
                targetNodeType.getName()).withSimpleKey(NAME, name).andCreate();
            node = session.withPartition(partition).createCriteria()
                .withUniqueKey(internalNodeKey).buildCriteria()
                .andFindUnique(session);
        } else {
            internalNodeKey = new NodeKeyBuilderImpl(targetNodeType
                .getName(), partition, repositoryPath)
                .withSimpleKey(NAME, name).andCreate();
        }

        for (final PropertyDescriptor d: descriptors) {
            if (d.getName().equals("class")) {
                continue;
            }
            propertyTypes.put(d.getName(),
                (Class<? extends Serializable>) Reflection
                .findClassWithoutPrimitives(d.getPropertyType()));
            final Object rawValue = node != null ? node.getPropertyAsString(session,
                d.getName()) : null;
            final Serializable value = (Serializable) (rawValue != null ? Conversion
                .convert(rawValue, d.getPropertyType()) : null);
            propertyValues.put(d.getName(), value);
        }
        int weigthValue;
        final Set<String> stNodeProperties = node != null ? node
            .getPropertyNames(session) : Collections.<String>emptySet();
        if (stNodeProperties.contains(WEIGTH_VALUE)) {
            weigthValue = Conversion.convert(node.getPropertyAsString(session,
                WEIGTH_VALUE), Integer.class);
        } else {
            weigthValue = findInitialWeight(clazz);
        }
        Class<? extends Node> savedClass = null;
        if (stNodeProperties.contains(CORRECT_CLASS)) {
            savedClass = Conversion.convert(node.getPropertyAsString(session,
                CORRECT_CLASS), Class.class);
        }
        final BigInteger savedClassNumericType = savedClass != null ? findNumericType(savedClass)
            : null;
        final BigInteger proposedClassNumericType = findNumericType(clazz);
        final Class<? extends Node> classToUse = savedClassNumericType != null
            && savedClassNumericType.compareTo(proposedClassNumericType) > 0 ? savedClass
            : clazz;

        final NodeImpl internalNode = new NodeImpl(name, classToUse, targetNodeType,
            internalNodeKey.getKeyAsString(), propertyTypes,
            propertyValues, parentId, contextId, weigthValue);
        if (node != null) {
            internalNode.cachedEntry = new WeakReference<org.openspotlight.storage.domain.Node>(node);
            if (needsToVerifyType) {
                fixTypeData(session, classToUse, node);
            }
            final String captionAsString = node.getPropertyAsString(session, CAPTION);
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

    private static void fixTypeData(
                                    final StorageSession session,
                                    final Class<? extends Node> clazz, final org.openspotlight.storage.domain.Node node) {
        final String numericTypeAsString = node.getPropertyAsString(session,
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

    private static void setWeigthAndTypeOnNode(
                                               final StorageSession session,
                                               final org.openspotlight.storage.domain.Node node,
                                               final Class<? extends Node> type,
                                               final BigInteger weightFromTargetNodeType) {
        node.setIndexedProperty(session, NUMERIC_TYPE, weightFromTargetNodeType
            .toString());
        node.setIndexedProperty(session, CORRECT_CLASS, type.getName());

    }

    public static Class<? extends Node> findTargetClass(
                                                        final Class<?> type) {
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

    public static org.openspotlight.storage.domain.Node retrievePreviousNode(
                                                                             final PartitionFactory factory,
                                                                             final StorageSession session, final Context context,
                                                                             final Node node,
                                                                             final boolean needsToVerifyType) {
        try {
            final PropertyContainerMetadata<org.openspotlight.storage.domain.Node> metadata = (PropertyContainerMetadata<org.openspotlight.storage.domain.Node>) node;
            org.openspotlight.storage.domain.Node internalNode = metadata.getCached();
            if (internalNode == null) {
                final Partition partition = factory.getPartitionByName(context
                    .getId());
                internalNode = session.withPartition(partition).createWithType(
                    findTargetClass(node.getClass()).getName())
                    .withSimpleKey(NAME, node.getName()).withParentAsString(
                    node.getParentId()).andCreate();
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
                    .getReadMethod().isAnnotationPresent(
                    TransientProperty.class)) {
                    internalNode.setIndexedProperty(session, propName,
                        Conversion.convert(value, String.class));

                }

            }
            return internalNode;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public static <T extends Link> T createLink(
                                                final PartitionFactory factory,
                                                final StorageSession session, final Class<T> clazz, final Node rawOrigin,
                                                final Node rawTarget, final LinkType type, final boolean createIfDontExists) {
        final Map<String, Class<? extends Serializable>> propertyTypes = newHashMap();
        final Map<String, Serializable> propertyValues = newHashMap();
        final PropertyDescriptor[] descriptors = PropertyUtils
            .getPropertyDescriptors(clazz);

        org.openspotlight.storage.domain.Link linkEntry = null;
        Node origin, target;

        if (LinkType.BIDIRECTIONAL.equals(type)
            && rawOrigin.compareTo(rawTarget) < 0) {
            origin = rawTarget;
            target = rawOrigin;
        } else {
            origin = rawOrigin;
            target = rawTarget;
        }
        String linkId = null;
        if (session != null) {
            final org.openspotlight.storage.domain.Node originAsSTNode = session.findNodeByStringId(origin
                .getId());
            final org.openspotlight.storage.domain.Node targetAsSTNode = session.findNodeByStringId(target
                .getId());
            if (originAsSTNode != null) {
                linkEntry = session.getLink(originAsSTNode, targetAsSTNode, clazz
                    .getName());
                if (linkEntry == null && createIfDontExists) {

                    linkEntry = session.addLink(originAsSTNode, targetAsSTNode,
                        clazz.getName());
                }
            }
            linkId = StringIDSupport.getLinkKeyAsString(StringIDSupport.getPartition(origin.getId(), factory)
                , clazz.getName(), origin.getId(),
                target.getId());
        }

        for (final PropertyDescriptor d: descriptors) {
            if (d.getName().equals("class")) {
                continue;
            }
            propertyTypes.put(d.getName(),
                (Class<? extends Serializable>) Reflection
                .findClassWithoutPrimitives(d.getPropertyType()));
            final Object rawValue = linkEntry != null ? linkEntry
                .getPropertyAsString(session, d.getName()) : null;
            final Serializable value = (Serializable) (rawValue != null ? Conversion
                .convert(rawValue, d.getPropertyType()) : null);
            propertyValues.put(d.getName(), value);
        }
        int weigthValue;
        final Set<String> stNodeProperties = linkEntry != null ? linkEntry
            .getPropertyNames(session) : Collections.<String>emptySet();
        if (stNodeProperties.contains(WEIGTH_VALUE)) {
            weigthValue = Conversion.convert(linkEntry.getPropertyAsString(
                session, WEIGTH_VALUE), Integer.class);
        } else {
            weigthValue = findInitialWeight(clazz);
        }
        final LinkImpl internalLink = new LinkImpl(linkId, clazz.getName(), clazz,
            propertyTypes, propertyValues, findInitialWeight(clazz),
            weigthValue, origin, target, LinkType.BIDIRECTIONAL
            .equals(type));
        if (linkEntry != null) {
            internalLink.cachedEntry = new WeakReference<org.openspotlight.storage.domain.Link>(linkEntry);

        }
        final Enhancer e = new Enhancer();
        e.setSuperclass(clazz);
        e.setInterfaces(new Class<?>[] {PropertyContainerMetadata.class});
        e.setCallback(new PropertyContainerInterceptor(internalLink));
        return (T) e.create(new Class[0], new Object[0]);
    }

    public static <T extends Node> T createNode(
                                                final PartitionFactory factory,
                                                final StorageSession session, final String contextId, final String parentId,
                                                final Class<T> clazz, final String name, final boolean needsToVerifyType,
                                                final Collection<Class<? extends Link>> linkTypesForLinkDeletion,
                                                final Collection<Class<? extends Link>> linkTypesForLinkedNodeDeletion) {
        return createNode(factory, session, contextId, parentId, clazz, name,
            needsToVerifyType, linkTypesForLinkDeletion,
            linkTypesForLinkedNodeDeletion, null);

    }

    private static class LinkImpl extends Link {

        private WeakReference<org.openspotlight.storage.domain.Link> cachedEntry;

        private final Class<? extends Link>                          linkType;

        public LinkImpl(final String id, final String linkName,
                        final Class<? extends Link> linkType,
                        final Map<String, Class<? extends Serializable>> propertyTypes,
                        final Map<String, Serializable> propertyValues,
                        final int initialWeigthValue, final int weightValue, final Node source,
                        final Node target, final boolean bidirectional) {
            propertyContainerImpl = new PropertyContainerImpl(id, linkType
                .getName(), propertyTypes, propertyValues,
                initialWeigthValue, weightValue);
            sides[SOURCE] = source;
            sides[TARGET] = target;
            this.linkType = linkType;

        }

        private final PropertyContainerImpl propertyContainerImpl;

        @Override
        public void createLineReference(
                                        final int beginLine, final int endLine,
                                        final int beginColumn, final int endColumn, final String statement,
                                        final String artifactId) {
            propertyContainerImpl.createLineReference(beginLine, endLine,
                beginColumn, endColumn, statement, artifactId);
        }

        @Override
        public boolean equals(
                              final Object obj) {
            if(!obj instanceof Link){
                
            }
            
            return propertyContainerImpl.equals(obj);
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
        public Set<Pair<String, Serializable>> getProperties() {
            return propertyContainerImpl.getProperties();
        }

        @Override
        public Iterable<String> getPropertyKeys() {
            return propertyContainerImpl.getPropertyKeys();
        }

        @Override
        public <V extends Serializable> V getPropertyValue(
                                                           final String key,
                                                           final V defaultValue) {
            return propertyContainerImpl.getPropertyValue(key, defaultValue);
        }

        @Override
        public <V extends Serializable> V getPropertyValue(
                                                           final String key) {
            return propertyContainerImpl.getPropertyValue(key);
        }

        @Override
        public String getPropertyValueAsString(
                                               final String key) {
            return propertyContainerImpl.getPropertyValueAsString(key);
        }

        @Override
        public TreeLineReference getTreeLineReferences() {
            return propertyContainerImpl.getTreeLineReferences();
        }

        @Override
        public TreeLineReference getTreeLineReferences(
                                                       final String artifactId) {
            return propertyContainerImpl.getTreeLineReferences(artifactId);
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
            return propertyContainerImpl.hashCode();
        }

        @Override
        public boolean hasProperty(
                                   final String key)
            throws IllegalArgumentException {
            return propertyContainerImpl.hasProperty(key);
        }

        @Override
        public boolean isDirty() {
            return propertyContainerImpl.isDirty();
        }

        @Override
        public void removeProperty(
                                   final String key) {
            propertyContainerImpl.removeProperty(key);
        }

        public void resetDirtyFlag() {
            propertyContainerImpl.resetDirtyFlag();
        }

        @Override
        public <V extends Serializable> void setProperty(
                                                         final String key, final V value)
            throws IllegalArgumentException {
            propertyContainerImpl.setProperty(key, value);
        }

        @Override
        public String toString() {
            return propertyContainerImpl.toString();
        }

        private static final int SOURCE = 0;
        private static final int TARGET = 1;

        private int              count;
        private boolean          bidirectional;

        private final Node[]     sides  = new Node[2];

        @Override
        public Node getOtherSide(
                                 final Node node)
            throws IllegalArgumentException {
            if (node.equals(sides[SOURCE])) { return sides[TARGET]; }
            if (node.equals(sides[TARGET])) { return sides[SOURCE]; }
            throw new IllegalArgumentException();
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
        public int getCount() {
            return count;
        }

        @Override
        public boolean isBidirectional() {
            return bidirectional;
        }

        @Override
        public void setCount(
                             final int value) {
            count = value;
            propertyContainerImpl.markAsDirty();

        }

        @Override
        public int compareTo(
                             final Link o) {
            return getId().compareTo(o.getId());
        }

        @Override
        public Class<? extends Link> getLinkType() {
            return linkType;
        }

    }

    private static class PropertyContainerImpl implements Element {

        @Override
        public final int getInitialWeightValue() {
            return initialWeightValue;
        }

        @Override
        public final int getWeightValue() {
            return weightValue;
        }

        private final String                                     id;

        private final String                                     typeName;

        private final Map<String, Class<? extends Serializable>> propertyTypes;
        private final Map<String, Serializable>                  propertyValues;
        private final AtomicBoolean                              dirty;

        public void markAsDirty() {
            dirty.set(true);
        }

        private final Set<String> removedProperties;

        public PropertyContainerImpl(final String id, final String typeName,
                                     final Map<String, Class<? extends Serializable>> propertyTypes,
                                     final Map<String, Serializable> propertyValues,
                                     final int initialWeigthValue, final int weightValue) {
            dirty = new AtomicBoolean();
            this.typeName = typeName;
            initialWeightValue = initialWeigthValue;
            this.weightValue = weightValue;
            this.id = id;
            this.propertyTypes = propertyTypes;
            this.propertyValues = propertyValues;
            removedProperties = newHashSet();
        }

        public void resetDirtyFlag() {
            dirty.set(false);
            removedProperties.clear();
        }

        @Override
        public void createLineReference(
                                        final int beginLine, final int endLine,
                                        final int beginColumn, final int endColumn, final String statement,
                                        final String artifactId) {
            throw new UnsupportedOperationException(
                "Should be lazy property? :D");
            // TODO implement
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public Set<Pair<String, Serializable>> getProperties() {
            final ImmutableSet.Builder<Pair<String, Serializable>> builder = ImmutableSet
                .builder();
            for (final Map.Entry<String, ? extends Serializable> entry: propertyValues
                .entrySet()) {
                builder.add(newPair(entry.getKey(), (Serializable) entry
                    .getValue(), PairEqualsMode.K1));
            }
            return builder.build();
        }

        @Override
        public Iterable<String> getPropertyKeys() {
            return ImmutableSet.copyOf(propertyTypes.keySet());
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V extends Serializable> V getPropertyValue(
                                                           final String key) {
            return (V) propertyValues.get(key);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V extends Serializable> V getPropertyValue(
                                                           final String key,
                                                           final V defaultValue) {
            final V value = (V) propertyValues.get(key);
            return value == null ? defaultValue : value;
        }

        @Override
        public String getPropertyValueAsString(
                                               final String key) {
            return convert(propertyValues.get(key), String.class);
        }

        @Override
        public TreeLineReference getTreeLineReferences() {
            throw new UnsupportedOperationException(
                "Should be lazy property? :D");
        }

        @Override
        public TreeLineReference getTreeLineReferences(
                                                       final String artifactId) {
            throw new UnsupportedOperationException(
                "Should be lazy property? :D");
        }

        @Override
        public String getTypeName() {
            return typeName;
        }

        @Override
        public boolean hasProperty(
                                   final String key)
            throws IllegalArgumentException {
            return propertyTypes.containsKey(Strings
                .firstLetterToLowerCase(key));
        }

        @Override
        public void removeProperty(
                                   String key) {
            key = Strings.firstLetterToLowerCase(key);
            propertyTypes.remove(key);
            propertyValues.remove(key);
            removedProperties.add(key);
            dirty.set(true);
        }

        @Override
        public <V extends Serializable> void setProperty(
                                                         String key, final V value)
            throws IllegalArgumentException {
            key = Strings.firstLetterToLowerCase(key);
            if (!hasProperty(key)) { throw logAndReturn(new IllegalArgumentException(
                "invalid property key " + key + " for type "
                + getTypeName())); }
            final Class<? extends Serializable> propType = propertyTypes.get(key);
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

        @Override
        public boolean isDirty() {
            return dirty.get();
        }

        private final int initialWeightValue;

        private final int weightValue;

    }

    private static class NodeImpl extends Node implements PropertyContainerMetadata<org.openspotlight.storage.domain.Node>  {

        private final PropertyContainerImpl propertyContainerImpl;

        @Override
        public void createLineReference(
                                        final int beginLine, final int endLine,
                                        final int beginColumn, final int endColumn, final String statement,
                                        final String artifactId) {
            propertyContainerImpl.createLineReference(beginLine, endLine,
                beginColumn, endColumn, statement, artifactId);
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
        public Set<Pair<String, Serializable>> getProperties() {
            return propertyContainerImpl.getProperties();
        }

        @Override
        public Iterable<String> getPropertyKeys() {
            return propertyContainerImpl.getPropertyKeys();
        }

        @Override
        public <V extends Serializable> V getPropertyValue(
                                                           final String key,
                                                           final V defaultValue) {
            return propertyContainerImpl.getPropertyValue(key, defaultValue);
        }

        @Override
        public <V extends Serializable> V getPropertyValue(
                                                           final String key) {
            return propertyContainerImpl.getPropertyValue(key);
        }

        @Override
        public String getPropertyValueAsString(
                                               final String key) {
            return propertyContainerImpl.getPropertyValueAsString(key);
        }

        @Override
        public TreeLineReference getTreeLineReferences() {
            return propertyContainerImpl.getTreeLineReferences();
        }

        @Override
        public TreeLineReference getTreeLineReferences(
                                                       final String artifactId) {
            return propertyContainerImpl.getTreeLineReferences(artifactId);
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
        public boolean hasProperty(
                                   final String key)
            throws IllegalArgumentException {
            return propertyContainerImpl.hasProperty(key);
        }

        @Override
        public boolean isDirty() {
            return propertyContainerImpl.isDirty();
        }

        @Override
        public void removeProperty(
                                   final String key) {
            propertyContainerImpl.removeProperty(key);
        }

        @Override
        public <V extends Serializable> void setProperty(
                                                         final String key, final V value)
            throws IllegalArgumentException {
            propertyContainerImpl.setProperty(key, value);
        }

        @Override
        public BigInteger getNumericType() {
            return numericType;
        }

        @Override
        public boolean equals(
                              final Object obj) {
            if (!(obj instanceof Node)) { return false; }
            final Node slnode = (Node) obj;

            final boolean result = getId().equals(slnode.getId())
                && Equals.eachEquality(getParentId(), slnode.getParentId())
                && Equals.eachEquality(getContextId(), slnode
                .getContextId());
            return result;
        }

        private volatile int hashCode = 0;

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
        public String toString() {
            return getName() + ":" + getId();
        }

        private final String contextId;

        @Override
        public String getContextId() {
            return contextId;
        }

        private WeakReference<org.openspotlight.storage.domain.Node> cachedEntry;

        private final Class<? extends Node>                          targetNode;

        private String                                               caption;

        private final String                                         name;

        private final Class<? extends Node>                          type;

        private final String                                         parentId;

        @Override
        public String getParentId() {
            return parentId;
        }

        private NodeImpl(final String name, final Class<? extends Node> type,
                         final Class<? extends Node> targetNode, final String id,
                         final Map<String, Class<? extends Serializable>> propertyTypes,
                         final Map<String, Serializable> propertyValues, final String parentId,
                         final String contextId, final int weightValue) {
            propertyContainerImpl = new PropertyContainerImpl(id, type
                .getName(), propertyTypes, propertyValues,
                findInitialWeight(type), weightValue);
            this.type = type;
            this.name = name;
            numericType = findNumericType(type);
            this.targetNode = targetNode;
            this.parentId = parentId;
            this.contextId = contextId;
        }

        public void resetDirtyFlag() {
            propertyContainerImpl.resetDirtyFlag();
        }

        @Override
        public String getCaption() {
            return caption;
        }

        @Override
        public void setCaption(
                               final String caption) {
            this.caption = caption;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int compareTo(
                             final Node o) {
            return numericType.compareTo(o.getNumericType());
        }

        @Override
        public org.openspotlight.storage.domain.Node getCached() {
            return cachedEntry != null ? cachedEntry.get() : null;
        }

        @Override
        public void setCached(
                              final org.openspotlight.storage.domain.Node entry) {
            cachedEntry = new WeakReference<org.openspotlight.storage.domain.Node>(entry);

        }

        private final BigInteger numericType;

    }

    private static class PropertyContainerInterceptor implements
        MethodInterceptor {

        private final PropertyContainer internalPropertyContainerImpl;

        public PropertyContainerInterceptor(
                                            final PropertyContainer propertyContainerImpl) {
            internalPropertyContainerImpl = propertyContainerImpl;
        }

        @Override
        public Object intercept(
                                final Object obj, final Method method, final Object[] args,
                                final MethodProxy proxy)
            throws Throwable {

            final Class<?> declarringClass = method.getDeclaringClass();
            final boolean methodFromSuperClasses = declarringClass.equals(Node.class)
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

        private Object invokeSetter(
                                    final Object obj, final String methodName,
                                    final Method method, final Object[] args, final MethodProxy methodProxy)
            throws Throwable {
            internalPropertyContainerImpl.setProperty(methodName.substring(3),
                (Serializable) args[0]);
            return null;
        }

        private Serializable invokeGetter(
                                          final String methodName) {

            final String propertyName = methodName.startsWith("get") ? Strings
                .firstLetterToLowerCase(methodName.substring(3)) : Strings
                .firstLetterToLowerCase(methodName.substring(2));// is
            return internalPropertyContainerImpl.getPropertyValue(propertyName);

        }

        private MethodType getMethodType(
                                         final String methodName, final Method method) {
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

        private static enum MethodType {
            SETTER,
            GETTER,
            OTHER
        }
    }

}
