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
import org.openspotlight.storage.domain.node.STProperty;

import java.io.Serializable;
import java.util.Set;


/**
 * This class is an abstraction of a current state of storage session. The implementation classes must not store
 * any kind of connection state. This implementation must not be shared between threads.
 */
public interface STStorageSession {

    public STRepositoryPath getRepositoryPath();

    STPartitionMethods withPartition(STPartition partition);

    interface STPartitionMethods extends STNodeEntryFactory {

        STUniqueKeyBuilder createKey(String nodeEntryName, boolean rootKey);

        Set<STNodeEntry> findByCriteria(STCriteria criteria);

        Set<STNodeEntry> findNamed(String nodeEntryName);

        STNodeEntry findUniqueByCriteria(STCriteria criteria);

        public STCriteriaBuilder createCriteria();

        STNodeEntryBuilder createWithName(String name, boolean rootKey);

        STStorageSessionInternalMethods getInternalMethods();

        STUniqueKey createNewSimpleKey(String... nodePaths);
        STNodeEntry createNewSimpleNode(String... nodePaths);


    }


    void removeNode(org.openspotlight.storage.domain.node.STNodeEntry stNodeEntry);

    interface STCriteriaBuilder {

        STCriteriaBuilder withProperty(String propertyName);

        STCriteriaBuilder withNodeEntry(String nodeName);

        STCriteriaBuilder equalsTo(String value);

        STCriteriaBuilder containsString(String value);

        STCriteriaBuilder startsWithString(String value);

        STCriteriaBuilder endsWithString(String value);

        STCriteriaBuilder and();

        STCriteria buildCriteria();

        STCriteriaBuilder withLocalKey(STLocalKey localKey);

        STCriteriaBuilder withUniqueKey(STUniqueKey uniqueKey);
    }

    interface STPropertyCriteriaItem extends STCriteriaItem {

        String getValue();


        String getPropertyName();

    }

    interface STPropertyContainsString extends STCriteriaItem {
        String getValue();
        String getPropertyName();
    }

    interface STPropertyStartsWithString extends STCriteriaItem {
        String getValue();
        String getPropertyName();
    }

    interface STPropertyEndsWithString extends STCriteriaItem {
        String getValue();
        String getPropertyName();
    }


    interface STUniqueKeyCriteriaItem extends STCriteriaItem {
        STUniqueKey getValue();

    }

    interface STLocalKeyCriteriaItem extends STCriteriaItem {
        STLocalKey getValue();
    }

    interface STCriteriaItem {

        String getNodeEntryName();


    }


    interface STCriteria {

        STPartition getPartition();

        String getNodeName();

        Set<STCriteriaItem> getCriteriaItems();

        Set<STNodeEntry> andFind(STStorageSession session);

        STNodeEntry andFindUnique(STStorageSession session);


    }

    static enum STFlushMode {
        AUTO, EXPLICIT
    }

    STFlushMode getFlushMode();

    interface STStorageSessionInternalMethods {

        STNodeEntryFactory.STNodeEntryBuilder nodeEntryCreateWithName(STNodeEntry stNodeEntry, String name, boolean rootKey);

        void propertySetProperty(org.openspotlight.storage.domain.node.STProperty stProperty, byte[] value);

        Set<STProperty> nodeEntryLoadProperties(org.openspotlight.storage.domain.node.STNodeEntry stNodeEntry);

        STNodeEntry nodeEntryGetParent(org.openspotlight.storage.domain.node.STNodeEntry stNodeEntry);

        Set<STNodeEntry> nodeEntryGetChildren(STNodeEntry stNodeEntry);

        byte[] propertyGetValue(STProperty stProperty); 

        Set<STNodeEntry> nodeEntryGetNamedChildren(STNodeEntry stNodeEntry, String name);
    }

    interface STUniqueKeyBuilder {

        STUniqueKeyBuilder withEntry(String propertyName, String value);

        STUniqueKeyBuilder withParent(STPartition partition, String nodeEntryName, boolean rootKey);

        STUniqueKey andCreate();

    }


    void discardTransient();

    void flushTransient();
}
