package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 1:47:34 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STSimpleProperty extends STAProperty {

    <T extends Serializable> Class<T> getType();
    
    <T extends Serializable> T getValue(STStorageSession session);

}
