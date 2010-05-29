package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STStorageSession;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 28/03/2010
 * Time: 10:27:26
 * To change this template use File | Settings | File Templates.
 */
public interface STProperty {

    STNodeEntry getParent();

    public boolean isIndexed();

    public boolean isKey();

    void setStringValue(STStorageSession session, String value);

    void setBytesValue(STStorageSession session, byte[] value);

    void setStreamValue(STStorageSession session, InputStream value);

    String getValueAsString(STStorageSession session);

    byte[] getValueAsBytes(STStorageSession session);

    InputStream getValueAsStream(STStorageSession session);


    String getPropertyName();

    public STPropertyInternalMethods getInternalMethods();

    interface STPropertyInternalMethods {
        void setStringValueOnLoad(STStorageSession session, String value);

        void setBytesValueOnLoad(STStorageSession session, byte[] value);

        void setStreamValueOnLoad(STStorageSession session, InputStream value);

        void removeTransientValueIfExpensive();


        String getTransientValueAsString(STStorageSession session);

        byte[] getTransientValueAsBytes(STStorageSession session);

        InputStream getTransientValueAsStream(STStorageSession session);


    }


}
