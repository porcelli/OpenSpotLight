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
package org.openspotlight.storage;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Set;

import org.openspotlight.storage.Criteria.CriteriaItem.CompositeKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyAsStringCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyContainsString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyEndsWithString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyStartsWithString;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;

import com.google.common.collect.ImmutableSet;

public class CriteriaImpl implements Criteria {

    private CriteriaImpl(final String nodeName,
                         final Set<CriteriaItem> criteriaItems, final Partition partition) {
        this.nodeName = nodeName;
        this.partition = partition;
        this.criteriaItems = ImmutableSet.copyOf(criteriaItems);
    }

    @Override
    public boolean equals(
                          final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final CriteriaImpl that = (CriteriaImpl) o;

        if (criteriaItems != null ? !criteriaItems
            .equals(that.criteriaItems) : that.criteriaItems != null) { return false; }
        if (nodeName != null ? !nodeName.equals(that.nodeName)
            : that.nodeName != null) { return false; }
        if (partition != null ? !partition.equals(that.partition)
            : that.partition != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = nodeName != null ? nodeName.hashCode() : 0;
        result = 31 * result
            + (partition != null ? partition.hashCode() : 0);
        result = 31 * result
            + (criteriaItems != null ? criteriaItems.hashCode() : 0);
        return result;
    }

    private final String            nodeName;

    private final Partition         partition;

    private final Set<CriteriaItem> criteriaItems;

    @Override
    public String getNodeType() {
        return nodeName;
    }

    @Override
    public Partition getPartition() {
        return partition;
    }

    @Override
    public Set<CriteriaItem> getCriteriaItems() {
        return criteriaItems;
    }

    @Override
    public Iterable<StorageNode> andFind(
                                         final StorageSession session) {
        return session.withPartition(partition).findByCriteria(this);
    }

    @Override
    public StorageNode andFindUnique(
                                     final StorageSession session) {
        return session.withPartition(partition).findUniqueByCriteria(this);
    }

    private static class UniqueKeyAsStringCriteriaItemImpl implements
        NodeKeyAsStringCriteriaItem {

        public UniqueKeyAsStringCriteriaItemImpl(final String keyAsString) {
            this.keyAsString = keyAsString;
            nodeType = StringKeysSupport.getNodeType(keyAsString);
        }

        private final String keyAsString;

        private final String nodeType;

        @Override
        public String getKeyAsString() {
            return keyAsString;
        }

        @Override
        public String getNodeType() {
            return nodeType;
        }

    }

    private static class UniqueKeyCriteriaItemImpl implements
        NodeKeyCriteriaItem {
        private UniqueKeyCriteriaItemImpl(final NodeKey value,
                                          final String nodeType) {
            this.value = value;
            this.nodeType = nodeType;
        }

        private final NodeKey value;

        private final String  nodeType;

        @Override
        public NodeKey getValue() {
            return value;
        }

        @Override
        public String getNodeType() {
            return nodeType;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final UniqueKeyCriteriaItemImpl that = (UniqueKeyCriteriaItemImpl) o;

            if (nodeType != null ? !nodeType
                .equals(that.nodeType) : that.nodeType != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result
                + (nodeType != null ? nodeType.hashCode() : 0);
            return result;
        }

    }

    private static class LocalKeyCriteriaItemImpl implements
        CompositeKeyCriteriaItem {
        private LocalKeyCriteriaItemImpl(final CompositeKey value,
                                         final String nodeType) {
            this.value = value;
            this.nodeType = nodeType;
        }

        private final CompositeKey value;

        private final String       nodeType;

        @Override
        public CompositeKey getValue() {
            return value;
        }

        @Override
        public String getNodeType() {
            return nodeType;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final LocalKeyCriteriaItemImpl that = (LocalKeyCriteriaItemImpl) o;

            if (nodeType != null ? !nodeType
                .equals(that.nodeType) : that.nodeType != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result
                + (nodeType != null ? nodeType.hashCode() : 0);
            return result;
        }

    }

    private static class PropertyCriteriaItemImpl implements
        PropertyCriteriaItem {
        private PropertyCriteriaItemImpl(final String propertyName, final String value,
                                         final String nodeType) {
            this.value = value;
            this.propertyName = propertyName;
            this.nodeType = nodeType;
        }

        private final String value;

        private final String propertyName;

        private final String nodeType;

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getNodeType() {
            return nodeType;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyCriteriaItemImpl that = (PropertyCriteriaItemImpl) o;

            if (nodeType != null ? !nodeType
                .equals(that.nodeType) : that.nodeType != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result
                + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result
                + (nodeType != null ? nodeType.hashCode() : 0);
            return result;
        }

    }

    private static class PropertyEndsWithStringImpl implements
        PropertyEndsWithString {

        private PropertyEndsWithStringImpl(final String nodeType,
                                           final String propertyName, final String value) {
            this.nodeType = nodeType;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeType;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeType() {
            return nodeType;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyEndsWithStringImpl that = (PropertyEndsWithStringImpl) o;

            if (nodeType != null ? !nodeType
                .equals(that.nodeType) : that.nodeType != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeType != null ? nodeType.hashCode() : 0;
            result = 31 * result
                + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class PropertyStartsWithStringImpl implements
        PropertyStartsWithString {

        private PropertyStartsWithStringImpl(final String nodeType,
                                             final String propertyName, final String value) {
            this.nodeType = nodeType;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeType;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeType() {
            return nodeType;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getValue() {
            return value;

        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyStartsWithStringImpl that = (PropertyStartsWithStringImpl) o;

            if (nodeType != null ? !nodeType
                .equals(that.nodeType) : that.nodeType != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeType != null ? nodeType.hashCode() : 0;
            result = 31 * result
                + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class PropertyContainsStringImpl implements
        PropertyContainsString {

        private PropertyContainsStringImpl(final String nodeType,
                                           final String propertyName, final String value) {
            this.nodeType = nodeType;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeType;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeType() {
            return nodeType;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyContainsStringImpl that = (PropertyContainsStringImpl) o;

            if (nodeType != null ? !nodeType
                .equals(that.nodeType) : that.nodeType != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeType != null ? nodeType.hashCode() : 0;
            result = 31 * result
                + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    public static class CriteriaBuilderImpl implements CriteriaBuilder {

        private final Partition partition;

        private String          transientNodeType;

        private String          transientPropertyName;

        private NodeKey         transientUniqueKey;

        private CompositeKey    transientLocalKey;

        private String          transientPropertyValue;

        private String          transientIdAsString;

        private String          startsWith;
        private String          endsWith;
        private String          contains;

        Set<CriteriaItem>       items;

        public CriteriaBuilderImpl(final Partition partition) {
            this.partition = partition;
            items = newLinkedHashSet();
        }

        private void breakIfNotNull(
                                    final Object o) {
            if (o != null) { throw new IllegalStateException(); }
        }

        private void breakIfNull(
                                 final Object o) {
            if (o == null) { throw new IllegalStateException(); }
        }

        @Override
        public CriteriaBuilder withProperty(
                                            final String propertyName) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientPropertyValue);
            breakIfNotNull(transientLocalKey);

            breakIfNotNull(transientPropertyName);

            transientPropertyName = propertyName;
            return this;
        }

        @Override
        public CriteriaBuilder withNodeEntry(
                                             final String nodeName) {
            breakIfNotNull(transientNodeType);
            transientNodeType = nodeName;
            return this;
        }

        @Override
        public CriteriaBuilder equalsTo(
                                        final String value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            breakIfNotNull(transientPropertyValue);
            transientPropertyValue = value;
            and();
            return this;
        }

        @Override
        public CriteriaBuilder containsString(
                                              final String value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            contains = value;
            and();
            return this;

        }

        @Override
        public CriteriaBuilder startsWithString(
                                                final String value) {

            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            startsWith = value;
            and();
            return this;

        }

        @Override
        public CriteriaBuilder endsWithString(
                                              final String value) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientLocalKey);

            breakIfNull(transientPropertyName);
            endsWith = value;
            and();
            return this;
        }

        @Override
        public CriteriaBuilder and() {
            CriteriaItem item = null;
            if (transientUniqueKey != null) {
                breakIfNull(transientNodeType);

                item = new UniqueKeyCriteriaItemImpl(transientUniqueKey,
                    transientNodeType);

            } else if (transientLocalKey != null) {
                breakIfNull(transientNodeType);

                item = new LocalKeyCriteriaItemImpl(transientLocalKey,
                    transientNodeType);

            } else if (transientIdAsString != null) {

                item = new UniqueKeyAsStringCriteriaItemImpl(
                    transientIdAsString);

            } else if (transientPropertyName != null) {
                if (startsWith != null) {
                    item = new PropertyStartsWithStringImpl(
                        transientNodeType, transientPropertyName,
                        startsWith);
                } else if (endsWith != null) {
                    item = new PropertyEndsWithStringImpl(
                        transientNodeType, transientPropertyName,
                        endsWith);
                } else if (contains != null) {
                    item = new PropertyContainsStringImpl(
                        transientNodeType, transientPropertyName,
                        contains);
                } else {
                    item = new PropertyCriteriaItemImpl(
                        transientPropertyName, transientPropertyValue,
                        transientNodeType);
                }
            }
            transientPropertyName = null;
            transientUniqueKey = null;
            transientLocalKey = null;
            transientPropertyValue = null;
            transientIdAsString = null;
            if (item != null) {
                items.add(item);
            }

            return this;
        }

        @Override
        public Criteria buildCriteria() {
            and();
            final CriteriaImpl result = new CriteriaImpl(transientNodeType,
                items, partition);

            return result;
        }

        @Override
        public CriteriaBuilder withLocalKey(
                                            final CompositeKey localKey) {
            breakIfNotNull(transientUniqueKey);
            breakIfNotNull(transientPropertyName);
            breakIfNotNull(transientPropertyValue);

            breakIfNotNull(transientLocalKey);

            transientLocalKey = localKey;
            transientNodeType = localKey.getNodeType();
            and();
            return this;
        }

        @Override
        public CriteriaBuilder withUniqueKey(
                                             final NodeKey uniqueKey) {

            breakIfNotNull(transientLocalKey);
            breakIfNotNull(transientPropertyName);
            breakIfNotNull(transientPropertyValue);

            breakIfNotNull(transientUniqueKey);

            transientUniqueKey = uniqueKey;
            transientNodeType = uniqueKey.getCompositeKey().getNodeType();
            and();
            return this;
        }

        @Override
        public CriteriaBuilder withUniqueKeyAsString(
                                                     final String uniqueKeyAsString) {
            transientIdAsString = uniqueKeyAsString;
            and();
            return this;
        }
    }

}
