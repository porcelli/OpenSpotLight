package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 2:13:05 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STListProperty extends STAMultipleProperty{
    <T> List<T> getItems(STStorageSession session);
    
}
