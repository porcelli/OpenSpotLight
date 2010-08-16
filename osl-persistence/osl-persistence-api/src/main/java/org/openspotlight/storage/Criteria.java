package org.openspotlight.storage;

import java.util.Set;

import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.key.LocalKey;
import org.openspotlight.storage.domain.key.UniqueKey;

public interface Criteria {

    Partition getPartition();

    String getNodeName();

    Set<CriteriaItem> getCriteriaItems();

    Iterable<Node> andFind(StorageSession session);

    Node andFindUnique(StorageSession session);

    public interface CriteriaItem {

        String getNodeEntryName();

        public interface PropertyCriteriaItem extends CriteriaItem {

            String getValue();

            String getPropertyName();

        }

        public interface LocalKeyCriteriaItem extends CriteriaItem {
            LocalKey getValue();
        }

        public interface PropertyEndsWithString extends CriteriaItem {
            String getValue();

            String getPropertyName();
        }

        public interface PropertyStartsWithString extends CriteriaItem {
            String getValue();

            String getPropertyName();
        }

        public interface UniqueKeyAsStringCriteriaItem extends CriteriaItem {
            String getKeyAsString();

        }

        public interface UniqueKeyCriteriaItem extends CriteriaItem {
            UniqueKey getValue();

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

        CriteriaBuilder withLocalKey(LocalKey localKey);

        CriteriaBuilder withUniqueKey(UniqueKey uniqueKey);

        CriteriaBuilder withUniqueKeyAsString(String uniqueKeyAsString);
    }
}
