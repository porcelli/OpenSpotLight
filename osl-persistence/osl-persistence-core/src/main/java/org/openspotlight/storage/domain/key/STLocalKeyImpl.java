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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Iterator;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 10:46:52 AM
 */
public class STLocalKeyImpl implements STLocalKey {
    public STLocalKeyImpl(Set<STKeyEntry<?>> entries, String nodeEntryName) {
        if(nodeEntryName==null) throw new IllegalArgumentException();
        Set<String> names= newHashSet();
        for(STKeyEntry entry: entries){
            if(names.contains(entry.getPropertyName())) {
                throw new IllegalStateException("duplicated entry name");
            }
            names.add(entry.getPropertyName());
        }
        this.entryNames = ImmutableSet.copyOf(names);
        this.entries = ImmutableSortedSet.copyOf(entries);
        this.nodeEntryName = nodeEntryName;
    }

    private final Set<STKeyEntry<?>> entries;

    private final Set<String> entryNames;

    public Set<String> getEntryNames() {
        return entryNames;
    }

    private final String nodeEntryName;

    public Set<STKeyEntry<?>> getEntries() {
        return entries;
    }

    public String getNodeEntryName() {
        return nodeEntryName;
    }

    @Override
    public String toString() {
        return "STLocalKeyImpl{" +
                "entries=" + entries +
                ", nodeEntryName='" + nodeEntryName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STLocalKeyImpl that = (STLocalKeyImpl) o;

        if (entries != null ? !entries.equals(that.entries) : that.entries != null) return false;
        if (nodeEntryName != null ? !nodeEntryName.equals(that.nodeEntryName) : that.nodeEntryName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entries != null ? entries.hashCode() : 0;
        result = 31 * result + (nodeEntryName != null ? nodeEntryName.hashCode() : 0);
        return result;
    }

    public int compareTo(STLocalKey o) {
        int result = this.getNodeEntryName().compareTo(o.getNodeEntryName());
        if (result != 0) return result;
        Iterator<STKeyEntry<?>> thisIt = getEntries().iterator();
        Iterator<STKeyEntry<?>> thatIt = o.getEntries().iterator();
        while (true) {
            boolean thisHasNext = thisIt.hasNext();
            boolean thatHasNext = thatIt.hasNext();

            if (thisHasNext && thatHasNext) {
                STKeyEntry<?> thisNext = thisIt.next();
                STKeyEntry<?> thatNext = thatIt.next();
                result = thisNext.compareTo(thatNext);
                if (result != 0) return result;
            } else {
                if (thisHasNext && !thatHasNext) return 1;
                if (!thisHasNext && thatHasNext) return -1;
                return 0;
            }
        }
    }
}
