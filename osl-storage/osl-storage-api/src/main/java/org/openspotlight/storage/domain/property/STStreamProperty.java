package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;

import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 2:56:26 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STStreamProperty extends STAProperty{

    InputStream getValue(STStorageSession session);
    
}
