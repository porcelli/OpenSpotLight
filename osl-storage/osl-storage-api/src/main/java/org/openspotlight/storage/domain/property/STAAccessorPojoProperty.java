package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:08:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorPojoProperty {

    List<STPojoProperty> getPojoProperties(STStorageSession session);

    <T> STPojoProperty setPojoProperty(STStorageSession session, Class<T> type, String name, T value);

    <T> STPojoProperty getPojoProperty(STStorageSession session, Class<T> type, String name);
}
