package org.openspotlight.storage.domain.key;

import org.openspotlight.storage.STAData;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 1:51:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STLocalKey extends STAData {

    Set<STKeyEntry> getEntries();

    String getNodeEntryName();

}
