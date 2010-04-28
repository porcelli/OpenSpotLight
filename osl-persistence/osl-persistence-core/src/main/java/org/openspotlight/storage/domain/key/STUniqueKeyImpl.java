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
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * *
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

package org.openspotlight.storage.domain.key;

import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STRepositoryPath;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 10:48:26 AM
 */
public class STUniqueKeyImpl implements STUniqueKey {

    public STUniqueKeyImpl(STLocalKey localKey, STUniqueKey parentKey, STPartition partition, STRepositoryPath repositoryPath) {
        this.localKey = localKey;
        this.parentKey = parentKey;
        this.partition = partition;
        this.repositoryPath = repositoryPath;
    }

    private final STRepositoryPath repositoryPath;

    private final STPartition partition;

    private final STLocalKey localKey;

    private final STUniqueKey parentKey;

    public STPartition getPartition() {
        return partition;
    }

    public STLocalKey getLocalKey() {
        return localKey;
    }

    public STUniqueKey getParentKey() {
        return parentKey;
    }

    public STRepositoryPath getRepositoryPath() {
        return repositoryPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STUniqueKeyImpl uniqueKey = (STUniqueKeyImpl) o;

        if (localKey != null ? !localKey.equals(uniqueKey.localKey) : uniqueKey.localKey != null) return false;
        if (parentKey != null ? !parentKey.equals(uniqueKey.parentKey) : uniqueKey.parentKey != null) return false;
        if (partition != null ? !partition.equals(uniqueKey.partition) : uniqueKey.partition != null) return false;
        if (repositoryPath != null ? !repositoryPath.equals(uniqueKey.repositoryPath) : uniqueKey.repositoryPath != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = repositoryPath != null ? repositoryPath.hashCode() : 0;
        result = 31 * result + (partition != null ? partition.hashCode() : 0);
        result = 31 * result + (localKey != null ? localKey.hashCode() : 0);
        result = 31 * result + (parentKey != null ? parentKey.hashCode() : 0);
        return result;
    }

    public int compareTo(STUniqueKey o) {
        STUniqueKey thisKey = this;
        STUniqueKey thatKey = o;
        while (true) {
            if (thisKey == null && thatKey == null) return 0;
            if (thisKey != null && thatKey == null) return 1;
            if (thisKey == null && thatKey != null) return -1;
            int result = thisKey.getLocalKey().compareTo(thatKey.getLocalKey());
            if (result != 0) return result;
            thisKey = thisKey.getParentKey();
            thatKey = thatKey.getParentKey();
        }
    }
}
