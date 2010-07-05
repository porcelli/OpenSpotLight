package org.openspotlight.storage;

import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * Created by User: feu - Date: Jun 9, 2010 - Time: 4:51:47 PM
 */
public class StringIDSupport {
    private static final String SEP = "__";

    public static String getNodeEntryName(String uniqueKeyAsString) {
        return uniqueKeyAsString.split(SEP)[2];

    }

    public static STRepositoryPath getRepositoryPath(String uniqueKeyAsString) {
        return repositoryPath(uniqueKeyAsString.split(SEP)[0]);

    }

    public static STPartition getPartition(String uniqueKeyAsString, STPartitionFactory factory) {
        return factory.getPartitionByName(getPartitionName(uniqueKeyAsString));
    }

    public static String getPartitionName(String uniqueKeyAsString) {
        return uniqueKeyAsString.split(SEP)[1];
    }


    public static String getUniqueKeyAsStringHash(STUniqueKey uniqueKey) {
        return new StringBuilder().append(uniqueKey.getRepositoryPath().getRepositoryPathAsString()).append(SEP)
                .append(uniqueKey.getPartition()).append(SEP)
                .append(uniqueKey.getLocalKey().getNodeEntryName()).append(SEP)
                .append(getSha1SignatureEncodedAsBase64(getUniqueKeyAsSimpleString(uniqueKey))).toString();
    }

    public static String getLocalKeyAsStringHash(STLocalKey uniqueKey) {
        return getSha1SignatureEncodedAsBase64(getLocalKeyAsSimpleString(uniqueKey));
    }

    private static String getLocalKeyAsSimpleString(STLocalKey localKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(localKey.getNodeEntryName());
        List<STKeyEntry> ordered = new ArrayList<STKeyEntry>(localKey.getEntries());
        Collections.sort(ordered);
        for (STKeyEntry entry : ordered) {
            sb.append(":").append(entry.getPropertyName()).append(":")
                    .append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    private static String getUniqueKeyAsSimpleString(STUniqueKey uniqueKey) {
        StringBuilder sb = new StringBuilder();
        STUniqueKey currentKey = uniqueKey;
        sb.append(uniqueKey.getRepositoryPath().getRepositoryPathAsString()).append(":");
        sb.append(uniqueKey.getParentKeyAsString()).append(":");
        sb.append(getLocalKeyAsSimpleString(uniqueKey.getLocalKey()));
        return sb.toString();
    }

}
