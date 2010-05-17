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

package org.openspotlight.storage.domain.node;

import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.domain.STAData;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface STNodeEntry extends STAData, STNodeEntryFactory,
        Comparable<STNodeEntry> {

    public boolean isChildOf(STNodeEntry possibleParent);

    String getNodeEntryName();

    STLocalKey getLocalKey();

    STUniqueKey getUniqueKey();

    STProperty getProperty(STStorageSession session, String name);

    <T> T getPropertyValue(STStorageSession session, String name);

    Set<STNodeEntry> getChildren(STStorageSession session);

    Set<STNodeEntry> getChildrenNamed(STStorageSession session, String name);

    Set<STNodeEntry> getChildrenForcingReload(STStorageSession session);

    Set<STNodeEntry> getChildrenNamedForcingReload(STStorageSession session, String name);

    STNodeEntry getParent(STStorageSession session);

    void removeNode(STStorageSession session);

    Set<String> getPropertyNames(STStorageSession session);

    Set<STProperty> getProperties(STStorageSession session);

    /**
     * Here, for every set property, it the value type or other internal property been set doesn't match the previous one,
     * it gonna throw a runtime exception
     *
     * @return
     */
    STPropertyOperation getVerifiedOperations();

    /**
     * Here, for every set property, it the value type or other internal property been set doesn't match the previous one,
     * it gonna change the internal property
     *
     * @return
     */
    STPropertyOperation getUnverifiedOperations();


    interface STPropertyOperation {

        <T extends Serializable> STProperty setSimpleProperty(STStorageSession session, String name, Class<? super T> propertyType, T value);
        <T extends Serializable> STProperty setIndexedProperty(STStorageSession session, String name, Class<? super T> propertyType, T value);

        <V extends Serializable> STProperty setSerializedListProperty(STStorageSession session, String name, Class<? super V> parameterizedType, List<V> value);

        <V extends Serializable> STProperty setSerializedSetProperty(STStorageSession session, String name, Class<? super V> parameterizedType, Set<V> value);

        <K extends Serializable, V extends Serializable> STProperty setSerializedMapProperty(STStorageSession session, String name, Class<? super K> keyType, Class<? super V> valueType, Map<K, V> value);

        <S extends Serializable> STProperty setSerializedPojoProperty(STStorageSession session, String name, Class<? super S> propertyType, S value);

        STProperty setInputStreamProperty(STStorageSession session, String name, InputStream value);

    }


}
