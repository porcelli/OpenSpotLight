/*
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
 * ***********************************************************************
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto
 * *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.
 *
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.storage;

import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STNodeEntryFactory;

import java.io.Serializable;
import java.util.Set;


/**
 * This class is an abstraction of a current state of storage session. The implementation classes must not store
 * any kind of connection state. This implementation must not be shared between threads.
 */
public interface STStorageSession extends STNodeEntryFactory {
    Set<STNodeEntry> findByCriteria(STCriteria criteria);

    STNodeEntry findUniqueByCriteria(STCriteria criteria);

    public interface STPartition {
        String getPartitionName();
    }

    public interface STCriteriaBuilder {
        STCriteriaBuilder withProperty(String propertyName);

        STCriteriaBuilder withNodeEntry(String nodeName);

        <T extends Serializable> STCriteriaBuilder equals(Class<T> type, T value);

        <T extends Serializable> STCriteriaBuilder notEquals(Class<T> type, T value);

        STCriteriaBuilder and();

        STCriteria buildCriteria();

        STCriteriaBuilder withLocalKey(STLocalKey localKey);

        STCriteriaBuilder withUniqueKey(STUniqueKey uniqueKey);
    }

    public interface STPropertyCriteriaItem<T extends Serializable> extends STCriteriaItem {
        public T getValue();

        public Class<T> getType();

        public String getPropertyName();

        public boolean isNot();
    }


    public interface STUniqueKeyCriteriaItem extends STCriteriaItem {
        public STUniqueKey getValue();

        public boolean isNot();
    }

    public interface STLocalKeyCriteriaItem extends STCriteriaItem {
        public STLocalKey getValue();

        public boolean isNot();
    }

    public interface STCriteriaItem {

        public String getNodeEntryName();



    }


    public interface STCriteria {


        public String getNodeName();

        public Set<STCriteriaItem> getCriteriaItems();

        public Set<STNodeEntry> andFind(STStorageSession session);

        public STNodeEntry andFindUnique(STStorageSession session);


    }

    public static enum STFlushMode {
        AUTO, EXPLICIT
    }

    public STFlushMode getFlushMode();

    public STPartition getCurrentPartition();

    public STStorageSession withPartition(STPartition partition);

    public STCriteriaBuilder createCriteria();

    STNodeEntryBuilder createWithName(String name);

    STStorageSessionInternalMethods getInternalMethods();

    interface STStorageSessionInternalMethods {

        public STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, String name);

    }

    interface STUniqueKeyBuilder {

        <T extends Serializable> STUniqueKeyBuilder withEntry(String propertyName, Class<T> type, Serializable value);

        STUniqueKeyBuilder withParent(String nodeEntryName);

        STUniqueKey andCreate();

    }

    STUniqueKeyBuilder createKey(String nodeEntryName);
    

    void flushTransient();
}
