package org.openspotlight.storage;

import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;

/**
 * Created by User: feu - Date: Jun 9, 2010 - Time: 4:51:47 PM
 */
public class StringIDSupport {

    public static String getUniqueKeyAsStringHash(STUniqueKey uniqueKey) {
        return getSha1SignatureEncodedAsBase64(getUniqueKeyAsSimpleString(uniqueKey));
    }

    public static String getLocalKeyAsStringHash(STLocalKey uniqueKey) {
        return getSha1SignatureEncodedAsBase64(getLocalKeyAsSimpleString(uniqueKey));
    }

    private static String getLocalKeyAsSimpleString(STLocalKey localKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(localKey.getNodeEntryName());
        sb.append(localKey.isRootKey());
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

        while (currentKey != null) {
            sb.append(getLocalKeyAsSimpleString(currentKey.getLocalKey())).append(":");
            currentKey = currentKey.getParentKey();
        }
        sb.append(uniqueKey.getRepositoryPath().getRepositoryPathAsString());
        return sb.toString();
    }

}
