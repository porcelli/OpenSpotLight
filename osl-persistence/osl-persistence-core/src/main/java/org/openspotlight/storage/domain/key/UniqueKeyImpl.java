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

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.RepositoryPath;
import org.openspotlight.storage.StringIDSupport;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 10:48:26 AM
 */
public class UniqueKeyImpl implements UniqueKey {

    private static final long serialVersionUID = 7018608184362764936L;

    private final int hashCode;

    @Override
    public String toString() {
        return "STUniqueKeyImpl{" +
                "keyAsString='" + getKeyAsString() + '\'' +
                '}';
    }

    public UniqueKeyImpl( LocalKey localKey, String parentKeyAsString, Partition partition, RepositoryPath repositoryPath ) {
        if (localKey == null) throw new IllegalArgumentException();
        if (repositoryPath == null) throw new IllegalArgumentException();
        this.localKey = localKey;
        this.parentKeyAsString = parentKeyAsString;
        this.partition = partition;
        this.repositoryPath = repositoryPath;

        int result = repositoryPath != null ? repositoryPath.hashCode() : 0;
        result = 31 * result + (partition != null ? partition.hashCode() : 0);
        result = 31 * result + (localKey != null ? localKey.hashCode() : 0);
        result = 31 * result + (parentKeyAsString != null ? parentKeyAsString.hashCode() : 0);
        hashCode = result;

    }

    private final RepositoryPath repositoryPath;

    private final Partition      partition;

    private final LocalKey       localKey;

    private final String           parentKeyAsString;

    private transient String       keyAsString = null;

    @Override
    public String getKeyAsString() {
        String value = keyAsString;
        if (value == null) {
            value = StringIDSupport.getUniqueKeyAsStringHash(this);
            keyAsString = value;
        }
        return value;
    }

    public Partition getPartition() {
        return partition;
    }

    public LocalKey getLocalKey() {
        return localKey;
    }

    public String getParentKeyAsString() {
        return parentKeyAsString;
    }

    public RepositoryPath getRepositoryPath() {
        return repositoryPath;
    }

    @Override
    public boolean equals( Object o ) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UniqueKeyImpl uniqueKey = (UniqueKeyImpl)o;
        if (this.hashCode != uniqueKey.hashCode) return false;
        if (localKey != null ? !localKey.equals(uniqueKey.localKey) : uniqueKey.localKey != null) return false;
        if (parentKeyAsString != null ? !parentKeyAsString.equals(uniqueKey.parentKeyAsString) : uniqueKey.parentKeyAsString != null)
            return false;
        if (partition != null ? !partition.equals(uniqueKey.partition) : uniqueKey.partition != null) return false;
        if (repositoryPath != null ? !repositoryPath.equals(uniqueKey.repositoryPath) : uniqueKey.repositoryPath != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public int compareTo( UniqueKey that ) {
        if (this == null && that == null) return 0;
        if (this != null && that == null) return 1;
        if (this == null && that != null) return -1;
        int result = this.getLocalKey().compareTo(that.getLocalKey());
        return result;

    }

}
