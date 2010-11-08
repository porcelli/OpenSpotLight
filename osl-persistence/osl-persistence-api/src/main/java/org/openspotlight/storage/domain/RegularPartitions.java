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

package org.openspotlight.storage.domain;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory;

/**
 * Created by User: feu - Date: Apr 20, 2010 - Time: 9:33:28 AM
 */
public enum RegularPartitions implements Partition {

    FEDERATION("federation"),
    LINE_REFERENCE("line_reference"),
    LOG("log"),
    SECURITY("security"),
    SYNTAX_HIGHLIGHT("syntax_highlight", FEDERATION);

    private static class CustomPartition implements Partition {

        private final String partitionName;

        public CustomPartition(final String partitionName) {
            this.partitionName = partitionName;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this) { return true; }
            if (!(o instanceof Partition)) { return false; }
            final Partition that = (Partition) o;
            return partitionName.equals(that.getPartitionName());
        }

        @Override
        public String getPartitionName() {
            return partitionName;
        }

        @Override
        public int hashCode() {
            return partitionName.hashCode();
        }

    }

    public static final PartitionFactory FACTORY = new PartitionFactory() {
                                                     @Override
                                                     public Partition getPartitionByName(final String name) {
                                                         try {
                                                             return valueOf(name.toUpperCase());
                                                         } catch (final IllegalArgumentException e) {
                                                             return new CustomPartition(name);
                                                         }
                                                     }

                                                     @Override
                                                     public Partition[] getValues() {
                                                         return values();
                                                     }
                                                 };

    private RegularPartitions            parent;
    private String                       partitionName;

    RegularPartitions(final String partitionName) {
        this.partitionName = partitionName;
    }

    RegularPartitions(final String partitionName, final RegularPartitions parent) {
        this.partitionName = partitionName;
        this.parent = parent;
    }

    public RegularPartitions getParent() {
        return parent;
    }

    @Override
    public String getPartitionName() {
        return partitionName;
    }
}
