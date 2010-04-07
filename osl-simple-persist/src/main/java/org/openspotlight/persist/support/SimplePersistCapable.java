package org.openspotlight.persist.support;

import org.openspotlight.storage.STPartition;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: feuteston
 * Date: 05/04/2010
 * Time: 12:15:53
 * To change this template use File | Settings | File Templates.
 */
public interface SimplePersistCapable<N, S> {

    public <T> Iterable<N> convertBeansToNodes(STPartition partition,
            final N parentNodeN, final S session,
            final Iterable<T> beans)throws  Exception;

    public <T> N convertBeanToNode(STPartition partition, final N parentNodeN,
                                   final S session, final T bean)throws Exception;

    public <T> Iterable<T> convertNodesToBeans(final S session,
                                               final Iterable<N> nodes) throws Exception;

    public <T> T convertNodeToBean(final S session,
                                   final N nodes)
            throws Exception;

    public <T> Set<T> findNodesByProperties(STPartition partition,
                                             final S session, N parentNodeN, final Class<T> nodeType,
                                             final String[] propertyNames, final Object[] propertyValues)throws Exception;

    
    public <T> T findNodeByProperty(STPartition partition,
                                             final S session, N parentNodeN, final Class<T> nodeType,
                                             final String[] propertyNames, final Object[] propertyValues)throws Exception;

}
