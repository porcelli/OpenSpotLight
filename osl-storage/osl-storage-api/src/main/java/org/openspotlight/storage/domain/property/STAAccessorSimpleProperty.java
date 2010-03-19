package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 3:44:57 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorSimpleProperty {

    List<STSimpleProperty> getSimpleProperties(STStorageSession session);

    <T> STSimpleProperty setSimpleProperty(STStorageSession session, Class<T> type, String name, T value);

    <T> STSimpleProperty getSimpleProperty(STStorageSession session, Class<T> type, String name);
}
