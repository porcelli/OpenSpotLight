package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 2:16:43 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STASerializableProperty<T> extends STAProperty{

    InputStream getEncodedAsStream(STStorageSession session);

    T get(STStorageSession session);
    void setTransient(T newItem);
}
