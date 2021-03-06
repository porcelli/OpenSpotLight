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
package org.openspotlight.storage;

/**
 * The main contract here is the creation of different partition instances ({@link Partition}. Usually an application has a single
 * PartitionFactory instance and threads servicing client requests obtain Partition instances from this factory.<br>
 * The internal state of a PartitionFactory is immutable.
 * 
 * @author feuteston
 * @author porcelli
 */
public interface PartitionFactory {

    /**
     * Returns a partition based on its name. If Partition name is not found, it'll be created.
     * 
     * @param name the partition name
     * @return the partition
     */
    Partition getPartition(String name);

    /**
     * Returns an array of all the available partitions.
     * 
     * @return available partitions
     */
    Iterable<Partition> getValues();

    /**
     * Most common partitions.
     * 
     * @author feuteston
     * @author porcelli
     */
    public enum RegularPartitions implements Partition {

        /**
         * Partition that stores federation related data
         */
        FEDERATION("federation"),
        /**
         * Partition that stores line reference data
         */
        LINE_REFERENCE("line_reference"),
        /**
         * Partition dedicated to store log data
         */
        LOG("log"),
        /**
         * Partition that stored security related data
         */
        SECURITY("security"),
        /**
         * Partition that stores syntax highlight data of federated artifacts
         */
        SYNTAX_HIGHLIGHT("syntax_highlight", FEDERATION);

        private final RegularPartitions parent;
        private final String            partitionName;

        RegularPartitions(final String partitionName) {
            this.partitionName = partitionName;
            this.parent = null;
        }

        RegularPartitions(final String partitionName, final RegularPartitions parent) {
            this.partitionName = partitionName;
            this.parent = parent;
        }

        /**
         * Returns the parent partition.
         * 
         * @return the parent partition
         */
        public RegularPartitions getParent() {
            return parent;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getPartitionName() {
            return partitionName;
        }
    }

}
