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

package org.openspotlight.storage.domain.key;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.sort;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.StringKeysSupport;

import com.google.common.collect.ImmutableSet;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 10:48:26 AM
 */
public class NodeKeyImpl implements NodeKey {

    public static class CompositeKeyImpl implements CompositeKey {

        public static class SimpleKeyImpl implements SimpleKey {

            private static final long serialVersionUID = -1370685091918627295L;

            private final int         hashCode;

            private final String      propertyName;

            private final String      value;

            public SimpleKeyImpl(final String propertyName, final String value) {
                checkNotEmpty("propertyName", propertyName);
                this.value = value;
                this.propertyName = propertyName;
                int result = value != null ? value.hashCode() : 0;
                result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
                hashCode = result;

            }

            @Override
            public int compareTo(final SimpleKey o) {
                final int result = propertyName.compareTo(o.getKeyName());
                return result;
            }

            @Override
            public boolean equals(final Object o) {
                if (this == o) { return true; }
                if (o == null || getClass() != o.getClass()) { return false; }

                final SimpleKeyImpl that = (SimpleKeyImpl) o;
                if (hashCode != that.hashCode) { return false; }

                if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) { return false; }
                if (value != null ? !value.equals(that.value) : that.value != null) { return false; }

                return true;
            }

            @Override
            public String getKeyName() {
                return propertyName;

            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public int hashCode() {
                return hashCode;
            }

            @Override
            public String toString() {
                return "SimpleKeyImpl{" +
                        ", value=" + value +
                        ", propertyName='" + propertyName + '\'' +
                        '}';
            }

        }

        private static final long    serialVersionUID = -2241511690224419686L;

        private final Set<SimpleKey> entries;

        private final Set<String>    entryNames;

        private final int            hashCode;

        private transient String     keyAsString      = null;

        private final String         nodeType;

        public CompositeKeyImpl(final Set<SimpleKey> entries, final String nodeType) {
            if (nodeType == null) { throw new IllegalArgumentException(); }
            final Set<String> names = newHashSet();
            for (final SimpleKey entry: entries) {
                if (names.contains(entry.getKeyName())) { throw new IllegalStateException("duplicated entry name"); }
                names.add(entry.getKeyName());
            }
            entryNames = ImmutableSet.copyOf(names);
            final List<SimpleKey> tempEntries = new ArrayList<SimpleKey>(entries);
            sort(tempEntries);
            this.entries = ImmutableSet.copyOf(tempEntries);
            this.nodeType = nodeType;

            int result = entries != null ? entries.hashCode() : 0;
            result = 31 * result + (nodeType != null ? nodeType.hashCode() : 0);
            hashCode = result;

        }

        @Override
        public int compareTo(final CompositeKey o) {
            int result = getNodeType().compareTo(o.getNodeType());
            if (result != 0) { return result; }
            final Iterator<SimpleKey> thisIt = getKeys().iterator();
            final Iterator<SimpleKey> thatIt = o.getKeys().iterator();
            while (true) {
                final boolean thisHasNext = thisIt.hasNext();
                final boolean thatHasNext = thatIt.hasNext();

                if (thisHasNext && thatHasNext) {
                    final SimpleKey thisNext = thisIt.next();
                    final SimpleKey thatNext = thatIt.next();
                    result = thisNext.compareTo(thatNext);
                    if (result != 0) { return result; }
                } else {
                    if (thisHasNext && !thatHasNext) { return 1; }
                    if (!thisHasNext && thatHasNext) { return -1; }
                    return 0;
                }
            }
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }

            final CompositeKeyImpl localKey = (CompositeKeyImpl) o;
            if (hashCode != localKey.hashCode) { return false; }

            if (entries != null ? !entries.equals(localKey.entries) : localKey.entries != null) { return false; }
            if (nodeType != null ? !nodeType.equals(localKey.nodeType) : localKey.nodeType != null) { return false; }

            return true;
        }

        @Override
        public String getKeyAsString() {
            String value = keyAsString;
            if (value == null) {
                value = StringKeysSupport.buildCompositeKeyAsHash(this);
                keyAsString = value;
            }
            return value;
        }

        @Override
        public Set<String> getKeyNames() {
            return entryNames;
        }

        @Override
        public Set<SimpleKey> getKeys() {
            return entries;
        }

        @Override
        public String getNodeType() {
            return nodeType;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static final long  serialVersionUID = 7018608184362764936L;

    private final int          hashCode;

    private transient String   keyAsString      = null;

    private final CompositeKey localKey;

    private final String       parentKeyAsString;

    private final Partition    partition;

    public NodeKeyImpl(final CompositeKey localKey, final String parentKeyAsString, final Partition partition) {
        if (localKey == null) { throw new IllegalArgumentException(); }
        this.localKey = localKey;
        this.parentKeyAsString = parentKeyAsString;
        this.partition = partition;

        int result = partition != null ? partition.hashCode() : 0;
        result = 31 * result + (localKey != null ? localKey.hashCode() : 0);
        result = 31 * result + (parentKeyAsString != null ? parentKeyAsString.hashCode() : 0);
        hashCode = result;

    }

    @Override
    public int compareTo(final NodeKey that) {
        if (this == null && that == null) { return 0; }
        if (this != null && that == null) { return 1; }
        if (this == null && that != null) { return -1; }
        final int result = getCompositeKey().compareTo(that.getCompositeKey());
        return result;

    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        final NodeKeyImpl uniqueKey = (NodeKeyImpl) o;
        if (hashCode != uniqueKey.hashCode) { return false; }
        if (localKey != null ? !localKey.equals(uniqueKey.localKey) : uniqueKey.localKey != null) { return false; }
        if (parentKeyAsString != null ? !parentKeyAsString.equals(uniqueKey.parentKeyAsString)
            : uniqueKey.parentKeyAsString != null) { return false; }
        if (partition != null ? !partition.equals(uniqueKey.partition) : uniqueKey.partition != null) { return false; }

        return true;
    }

    @Override
    public CompositeKey getCompositeKey() {
        return localKey;
    }

    @Override
    public String getKeyAsString() {
        String value = keyAsString;
        if (value == null) {
            value = StringKeysSupport.buildNodeKeyAsString(this);
            keyAsString = value;
        }
        return value;
    }

    @Override
    public String getParentKeyAsString() {
        return parentKeyAsString;
    }

    @Override
    public Partition getPartition() {
        return partition;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "NodeKeyImpl{" +
                "keyAsString='" + getKeyAsString() + '\'' +
                '}';
    }
}
