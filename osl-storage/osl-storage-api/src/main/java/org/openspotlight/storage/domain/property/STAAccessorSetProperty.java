package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:12:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorSetProperty {

    List<STSetProperty> getSetProperties(STStorageSession session);

    <T> STSetProperty setSetProperty(STStorageSession session, Class<T> valueType, String name, Set<T> value);

    <T> STSetProperty getSetProperty(STStorageSession session, Class<T> valueType, String name);

}
