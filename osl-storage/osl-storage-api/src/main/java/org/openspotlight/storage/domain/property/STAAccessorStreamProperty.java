package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 4:12:22 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAAccessorStreamProperty {
    
    List<STStreamProperty> getStreamProperties(STStorageSession session);

    <T> STStreamProperty setStreamProperty(STStorageSession session, String name, T value);

    <T> STStreamProperty getStreamProperty(STStorageSession session, String name);

    
}
