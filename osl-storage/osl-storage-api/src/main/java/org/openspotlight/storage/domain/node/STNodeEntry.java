package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STAData;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.property.*;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 1:46:28 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STNodeEntry extends STAData, STANodeEntryFactory, STAAccessorListProperty,
        STAAccessorMapProperty,
        STAAccessorPojoProperty,
        STAAccessorSerializableListProperty,
        STAAccessorSerializableMapProperty,
        STAAccessorSerializableSetProperty,
        STAAccessorSetProperty,
        STAAccessorSimpleProperty,
        STAAccessorStreamProperty {

    String getNodeEntryName();

    STLocalKey getLocalKey();

    STUniqueKey getUniqueKey();


}
