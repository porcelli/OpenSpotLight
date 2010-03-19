package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 2:06:08 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STACollection<C extends Collection> extends STAMultipleProperty{
    C getItems(STStorageSession session);
    void setTransient(C items);
}
