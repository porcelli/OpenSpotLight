package org.openspotlight.storage.domain.property;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Mar 19, 2010
 * Time: 2:05:42 PM
 * To change this template use File | Settings | File Templates.
 */
public interface STAMultipleProperty extends STAProperty{
    <T> Class<T> getValueType();
}
