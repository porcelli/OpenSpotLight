package org.openspotlight.storage;

import java.util.Set;

import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;

public interface Criteria {

    Partition getPartition();

    String getNodeType();

    Set<CriteriaItem> getCriteriaItems();

    Iterable<Node> andFind(StorageSession session);

    Node andFindUnique(StorageSession session);

    public interface CriteriaItem {

        String getNodeType();

        public interface PropertyCriteriaItem extends CriteriaItem {

            String getValue();

            String getPropertyName();

        }

        public interface CompisiteKeyCriteriaItem extends CriteriaItem {
            CompositeKey getValue();
        }

        public interface PropertyEndsWithString extends CriteriaItem {
            String getValue();

            String getPropertyName();
        }

        public interface PropertyStartsWithString extends CriteriaItem {
            String getValue();

            String getPropertyName();
        }

        public interface NodeKeyAsStringCriteriaItem extends CriteriaItem {
            String getKeyAsString();

        }

        public interface NodeKeyCriteriaItem extends CriteriaItem {
            NodeKey getValue();

        }

        public interface PropertyContainsString extends CriteriaItem {
            String getValue();

            String getPropertyName();
        }

    }

    public interface CriteriaBuilder {

        CriteriaBuilder withProperty(String propertyName);

        CriteriaBuilder withNodeEntry(String nodeName);

        CriteriaBuilder equalsTo(String value);

        CriteriaBuilder containsString(String value);

        CriteriaBuilder startsWithString(String value);

        CriteriaBuilder endsWithString(String value);

        CriteriaBuilder and();

        Criteria buildCriteria();

        CriteriaBuilder withLocalKey(CompositeKey localKey);

        CriteriaBuilder withUniqueKey(NodeKey uniqueKey);

        CriteriaBuilder withUniqueKeyAsString(String uniqueKeyAsString);
    }
}
