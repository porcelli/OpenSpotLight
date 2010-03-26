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

import com.google.common.collect.ImmutableSortedSet;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 10:48:26 AM
 */
public class STUniqueKeyImpl implements STUniqueKey {
    public STUniqueKeyImpl(Set<STLocalKey> allKeys) {
        this.allKeys = ImmutableSortedSet.copyOf(allKeys);
        this.rawKey = null;
    }

    public STUniqueKeyImpl(Set<STLocalKey> allKeys, Serializable rawKey) {
        this.allKeys = ImmutableSortedSet.copyOf(allKeys);
        this.rawKey = rawKey;
    }

    private final Set<STLocalKey> allKeys;

    private final Serializable rawKey;

    public Set<STLocalKey> getAllKeys() {
        return allKeys;
    }

    public Serializable getRawKey() {
        return rawKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STUniqueKeyImpl that = (STUniqueKeyImpl) o;

        if (allKeys != null ? !allKeys.equals(that.allKeys) : that.allKeys != null) return false;
        if (rawKey != null ? !rawKey.equals(that.rawKey) : that.rawKey != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = allKeys != null ? allKeys.hashCode() : 0;
        result = 31 * result + (rawKey != null ? rawKey.hashCode() : 0);
        return result;
    }

    public int compareTo(STUniqueKey o) {

        Iterator<STLocalKey> thisIt = getAllKeys().iterator();
        Iterator<STLocalKey> thatIt = o.getAllKeys().iterator();
        while(true){
            boolean thisHasNext = thisIt.hasNext();
            boolean thatHasNext = thatIt.hasNext();

            if(thisHasNext && thatHasNext){
                STLocalKey thisNext = thisIt.next();
                STLocalKey thatNext = thatIt.next();
                int result = thisNext.compareTo(thatNext);
                if(result!=0) return result;
            }else{
                if(thisHasNext && !thatHasNext) return 1;
                if(!thisHasNext && thatHasNext) return -1;
                return 0;
            }
        }
    }
}
