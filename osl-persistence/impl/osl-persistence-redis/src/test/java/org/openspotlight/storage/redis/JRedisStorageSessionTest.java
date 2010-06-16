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

package org.openspotlight.storage.redis;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STStorageSession;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisServerDetail;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.test.AbstractSTStorageSessionTest;

import java.util.Map;

import static org.openspotlight.storage.STRepositoryPath.repositoryPath;


/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 5:08:39 PM
 */
public class JRedisStorageSessionTest extends AbstractSTStorageSessionTest {


    private enum JRedisServerConfigExample implements JRedisServerDetail {
        DEFAULT("localhost", 6379, 0),
        FIRST("localhost", 6379, 1),
        SECOND("localhost", 6379, 2);

        private JRedisServerConfigExample(String serverName, int serverPort, int db) {
            this.serverName = serverName;
            this.serverPort = serverPort;
            this.db = db;
        }

        private final String serverName;

        private final int db;

        public int getDb() {
            return db;
        }

        public String getPassword() {
            return null;
        }

        private final int serverPort;

        public String getServerName() {
            return serverName;
        }

        public int getServerPort() {
            return serverPort;
        }
    }


    final Map<STPartition, JRedisServerDetail> mappedServerConfig = ImmutableMap.<STPartition, JRedisServerDetail>builder()
            .put(ExamplePartition.DEFAULT, JRedisServerConfigExample.DEFAULT)
            .put(ExamplePartition.FIRST, JRedisServerConfigExample.FIRST)
            .put(ExamplePartition.SECOND, JRedisServerConfigExample.SECOND).build();


    @Override
    protected Injector createsAutoFlushInjector() {
        return Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.AUTO, mappedServerConfig, repositoryPath("repositoryPath"), ExamplePartition.FACTORY));
    }

    @Override
    protected Injector createsExplicitFlushInjector() {
        return Guice.createInjector(new JRedisStorageModule(STStorageSession.STFlushMode.EXPLICIT, mappedServerConfig, repositoryPath("repositoryPath"), ExamplePartition.FACTORY));
    }

    @Override
    protected boolean supportsAutoFlushInjector() {
        return true;
    }

    @Override
    protected boolean supportsExplicitFlushInjector() {
        return true;
    }

    @Override
    protected boolean supportsAdvancedQueries() {
        return false;
    }

    @Override
    protected void internalCleanPreviousData() throws Exception {
        JRedisFactory autoFlushFactory = autoFlushInjector.getInstance(JRedisFactory.class);
        autoFlushFactory.getFrom(ExamplePartition.DEFAULT).flushall();
        autoFlushFactory.getFrom(ExamplePartition.DEFAULT).save();
    }
}
