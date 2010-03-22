package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 2:14:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STMapProperty extends STAMultipleProperty{
    <T> Class<T> getKeyType();
    <K,T> Map<K,T> getMap(STStorageSession session);
}
