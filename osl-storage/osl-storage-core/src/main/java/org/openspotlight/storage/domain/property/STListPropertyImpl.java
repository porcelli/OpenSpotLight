package org.openspotlight.storage.domain.property;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.node.STNodeEntry;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 5:51:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class STListPropertyImpl implements STListProperty{
    public List getItems(STStorageSession session) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setTransient(List items) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public <T> Class<T> getValueType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public STNodeEntry getParent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getPropertyName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
