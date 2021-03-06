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

package org.openspotlight.storage.redis.util;

import org.openspotlight.storage.Partition;
import org.openspotlight.storage.PartitionFactory.RegularPartitions;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;

import com.google.common.collect.ImmutableMap;

/**
 * Created by User: feu - Date: Apr 26, 2010 - Time: 2:48:17 PM
 */
public enum ExampleRedisConfig implements JRedisServerDetail {

    EXAMPLE("localhost", 6379, 0, null, false);

    private final int                                         db;

    private final boolean                                     defaultConfig;

    private final ImmutableMap<Partition, JRedisServerDetail> mappedServerConfig;

    private final String                                      password;

    private final String                                      serverName;

    private final int                                         serverPort;

    private ExampleRedisConfig(final String serverName, final int serverPort, final int db,
                               final String password, final boolean defaultConfig) {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.db = db;
        this.password = password;
        this.defaultConfig = defaultConfig;
        final ImmutableMap.Builder<Partition, JRedisServerDetail> builder = ImmutableMap
                .<Partition, JRedisServerDetail>builder();
        for (final RegularPartitions p: RegularPartitions.values()) {
            builder.put(p, this);
        }
        mappedServerConfig = builder.build();
    }

    @Override
    public int getDb() {
        return db;
    }

    public ImmutableMap<Partition, JRedisServerDetail> getMappedServerConfig() {
        return mappedServerConfig;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getServerName() {
        return serverName;
    }

    @Override
    public int getServerPort() {
        return serverPort;
    }

    @Override
    public boolean isDefaultConfig() {
        return defaultConfig;
    }
}
