package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:06:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorListProperty {

    List<STListProperty> getListProperties(STStorageSession session);

    <T> STListProperty setListProperty(STStorageSession session, Class<T> valueType, String name, List<T> value);

    <T> STListProperty getListProperty(STStorageSession session, Class<T> valueType, String name);


}
