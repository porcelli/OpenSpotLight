/**
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.persist.support;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.openspotlight.common.Disposable;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StorageSession;

/**
 * Created by IntelliJ IDEA. User: feuteston Date: 05/04/2010 Time: 12:15:53 To change this template use File | Settings | File
 * Templates.
 */
public interface SimplePersistCapable<N, S> extends Disposable{
	
	StorageSession getCurrentSession();

    Partition getCurrentPartition();

    StorageSession.PartitionMethods getPartitionMethods();

    public <T> Iterable<N> convertBeansToNodes(final Iterable<T> beans);

    public <T> N convertBeanToNode(final T bean)
        throws Exception;

    public <T> Iterable<T> convertNodesToBeans(final Iterable<N> nodes);

    public <T> T convertNodeToBean(final N nodes)
        throws Exception;

    public <T> Iterable<T> findByProperties(Class<T> beanType,
                                             String[] propertyNames,
                                             Object[] propertyValues);

    public <T> Iterable<T> findAll(Class<T> beanType);

    public <T> Iterable<T> findAll(N parentNode,
                                    Class<T> beanType);

    public <T> T findUnique(Class<T> beanType);

    public <T> T findUnique(N parentNode,
                             Class<T> beanType);

    public <T> T findUniqueByProperties(Class<T> beanType,
                                         String[] propertyNames,
                                         Object[] propertyValues);

    public <T> Iterable<N> convertBeansToNodes(N parentNode,
                                                final Iterable<T> beans);

    public <T> N convertBeanToNode(N parentNode,
                                    final T bean);

    public <T> Iterable<T> findByProperties(N parentNode,
                                             Class<T> beanType,
                                             String[] propertyNames,
                                             Object[] propertyValues);

    public <T> T findUniqueByProperties(N parentNode,
                                         Class<T> beanType,
                                         String[] propertyNames,
                                         Object[] propertyValues);

    interface InternalMethods {
        public Object beforeUnConvert(SimpleNodeType bean,
                                       Serializable value,
                                       Method readMethod);

        public String getNodeName(Class<?> nodeType);
    }

    public InternalMethods getInternalMethods();

}
