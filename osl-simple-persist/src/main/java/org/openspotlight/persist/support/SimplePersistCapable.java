package org.openspotlight.persist.support;

import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA. User: feuteston Date: 05/04/2010 Time: 12:15:53 To change this template use File | Settings | File
 * Templates.
 */
public interface SimplePersistCapable<N, S> {

    STStorageSession getCurrentSession();

    STPartition getCurrentPartition();

    STStorageSession.STPartitionMethods getPartitionMethods();

    public <T> Iterable<N> convertBeansToNodes( final Iterable<T> beans );

    public <T> N convertBeanToNode( final T bean ) throws Exception;

    public <T> Iterable<T> convertNodesToBeans( final Iterable<N> nodes );

    public <T> T convertNodeToBean( final N nodes ) throws Exception;

    public <T> Iterable<T> findByProperties( Class<T> beanType,
                                             String[] propertyNames,
                                             Object[] propertyValues );

    public <T> Iterable<T> findAll( Class<T> beanType );

    public <T> Iterable<T> findAll( N parentNode,
                                    Class<T> beanType );

    public <T> T findUnique( Class<T> beanType );

    public <T> T findUnique( N parentNode,
                             Class<T> beanType );

    public <T> T findUniqueByProperties( Class<T> beanType,
                                         String[] propertyNames,
                                         Object[] propertyValues );

    public <T> Iterable<N> convertBeansToNodes( N parentNode,
                                                final Iterable<T> beans );

    public <T> N convertBeanToNode( N parentNode,
                                    final T bean );

    public <T> Iterable<T> findByProperties( N parentNode,
                                             Class<T> beanType,
                                             String[] propertyNames,
                                             Object[] propertyValues );

    public <T> T findUniqueByProperties( N parentNode,
                                         Class<T> beanType,
                                         String[] propertyNames,
                                         Object[] propertyValues );

    interface InternalMethods {
        public Object beforeUnConvert( SimpleNodeType bean,
                                       Serializable value,
                                       Method readMethod );

        public String getNodeName( Class<?> nodeType );
    }

    public InternalMethods getInternalMethods();

}
