package org.openspotlight.storage.domain.key;

import org.openspotlight.storage.STAData;

import java.io.Serializable;
import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 1:57:04 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STUniqueKey extends STAData {

    List<STLocalKey> getAllKeys();

    Serializable getRawKey();

}
