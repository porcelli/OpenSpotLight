package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STAData;
import org.openspotlight.storage.domain.node.STNodeEntry;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 2:08:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAProperty extends STAData {
    STNodeEntry getParent();
    String getPropertyName();
}
