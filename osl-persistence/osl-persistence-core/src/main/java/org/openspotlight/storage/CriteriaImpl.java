package org.openspotlight.storage;

import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.Set;

import org.openspotlight.storage.Criteria.CriteriaItem.CompisiteKeyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyContainsString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyEndsWithString;
import org.openspotlight.storage.Criteria.CriteriaItem.PropertyStartsWithString;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyAsStringCriteriaItem;
import org.openspotlight.storage.Criteria.CriteriaItem.NodeKeyCriteriaItem;
import org.openspotlight.storage.domain.Node;
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
    public Iterable<Node> andFind(
                                  final StorageSession session) {
        return session.withPartition(partition).findByCriteria(this);
    }

    @Override
    public Node andFindUnique(
                              final StorageSession session) {
        return session.withPartition(partition).findUniqueByCriteria(this);
    }

    private static class UniqueKeyAsStringCriteriaItemImpl implements
        NodeKeyAsStringCriteriaItem {

        public UniqueKeyAsStringCriteriaItemImpl(final String keyAsString) {
            this.keyAsString = keyAsString;
            nodeEntryName = StringIDSupport.getNodeEntryName(keyAsString);
        }

        private final String keyAsString;

        private final String nodeEntryName;

        @Override
        public String getKeyAsString() {
            return keyAsString;
        }

        @Override
        public String getNodeType() {
            return nodeEntryName;
        }

    }

    private static class UniqueKeyCriteriaItemImpl implements
        NodeKeyCriteriaItem {
        private UniqueKeyCriteriaItemImpl(final NodeKey value,
                                          final String nodeEntryName) {
            this.value = value;
            this.nodeEntryName = nodeEntryName;
        }

        private final NodeKey value;

        private final String    nodeEntryName;

        @Override
        public NodeKey getValue() {
            return value;
        }

        @Override
        public String getNodeType() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final UniqueKeyCriteriaItemImpl that = (UniqueKeyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result
                + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }

    }

    private static class LocalKeyCriteriaItemImpl implements
        CompisiteKeyCriteriaItem {
        private LocalKeyCriteriaItemImpl(final CompositeKey value,
                                         final String nodeEntryName) {
            this.value = value;
            this.nodeEntryName = nodeEntryName;
        }

        private final CompositeKey value;

        private final String   nodeEntryName;

        @Override
        public CompositeKey getValue() {
            return value;
        }

        @Override
        public String getNodeType() {
            return nodeEntryName;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final LocalKeyCriteriaItemImpl that = (LocalKeyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result
                + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }

    }

    private static class PropertyCriteriaItemImpl implements
        PropertyCriteriaItem {
        private PropertyCriteriaItemImpl(final String propertyName, final String value,
                                         final String nodeEntryName) {
            this.value = value;
            this.propertyName = propertyName;
            this.nodeEntryName = nodeEntryName;
        }

        private final String value;

        private final String propertyName;

        private final String nodeEntryName;

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
            return nodeEntryName;
        }

        @Override
        public boolean equals(
                              final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final PropertyCriteriaItemImpl that = (PropertyCriteriaItemImpl) o;

            if (nodeEntryName != null ? !nodeEntryName
                .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
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
                + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
            return result;
        }

    }

    private static class PropertyEndsWithStringImpl implements
        PropertyEndsWithString {

        private PropertyEndsWithStringImpl(final String nodeEntryName,
                                           final String propertyName, final String value) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeType() {
            return nodeEntryName;
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

            if (nodeEntryName != null ? !nodeEntryName
                .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result
                + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class PropertyStartsWithStringImpl implements
        PropertyStartsWithString {

        private PropertyStartsWithStringImpl(final String nodeEntryName,
                                             final String propertyName, final String value) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeType() {
            return nodeEntryName;
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

            if (nodeEntryName != null ? !nodeEntryName
                .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result
                + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class PropertyContainsStringImpl implements
        PropertyContainsString {

        private PropertyContainsStringImpl(final String nodeEntryName,
                                           final String propertyName, final String value) {
            this.nodeEntryName = nodeEntryName;
            this.propertyName = propertyName;
            this.value = value;
        }

        private final String nodeEntryName;

        private final String propertyName;

        private final String value;

        @Override
        public String getNodeType() {
            return nodeEntryName;
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

            if (nodeEntryName != null ? !nodeEntryName
                .equals(that.nodeEntryName) : that.nodeEntryName != null) { return false; }
            if (propertyName != null ? !propertyName.equals(that.propertyName)
                : that.propertyName != null) { return false; }
            if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

            return true;
        }

        @Override
        public int hashCode() {
            int result = nodeEntryName != null ? nodeEntryName.hashCode() : 0;
            result = 31 * result
                + (propertyName != null ? propertyName.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    public static class CriteriaBuilderImpl implements CriteriaBuilder {

        private final Partition partition;

        private String          transientNodeEntryName;

        private String          transientPropertyName;

        private NodeKey       transientUniqueKey;

        private CompositeKey        transientLocalKey;

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
            breakIfNotNull(transientNodeEntryName);
            transientNodeEntryName = nodeName;
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
                breakIfNull(transientNodeEntryName);

                item = new UniqueKeyCriteriaItemImpl(transientUniqueKey,
                    transientNodeEntryName);

            } else if (transientLocalKey != null) {
                breakIfNull(transientNodeEntryName);

                item = new LocalKeyCriteriaItemImpl(transientLocalKey,
                    transientNodeEntryName);

            } else if (transientIdAsString != null) {

                item = new UniqueKeyAsStringCriteriaItemImpl(
                    transientIdAsString);

            } else if (transientPropertyName != null) {

                if (startsWith != null) {
                    item = new PropertyStartsWithStringImpl(
                        transientNodeEntryName, transientPropertyName,
                        startsWith);
                } else if (endsWith != null) {
                    item = new PropertyEndsWithStringImpl(
                        transientNodeEntryName, transientPropertyName,
                        endsWith);
                } else if (contains != null) {
                    item = new PropertyContainsStringImpl(
                        transientNodeEntryName, transientPropertyName,
                        contains);
                } else {
                    item = new PropertyCriteriaItemImpl(
                        transientPropertyName, transientPropertyValue,
                        transientNodeEntryName);
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
            final CriteriaImpl result = new CriteriaImpl(transientNodeEntryName,
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
            transientNodeEntryName = localKey.getNodeName();
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
            transientNodeEntryName = uniqueKey.getCompositeKey().getNodeName();
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
