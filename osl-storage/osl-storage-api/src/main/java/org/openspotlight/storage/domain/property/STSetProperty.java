package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 2:11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STSetProperty extends STAMultipleProperty{

    <T> Set<T> getItems(STStorageSession session);
}
