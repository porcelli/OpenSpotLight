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
package org.openspotlight.storage.redis;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Strings.concatPaths;

import java.io.File;

import org.jredis.JRedis;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.storage.Partition;
import org.openspotlight.storage.redis.guice.JRedisFactory;

/**
 * Created by User: feu - Date: May 18, 2010 - Time: 11:22:15 AM
 */
public enum RedisServerExecutor {
    INSTANCE;
    private static final String RELATIVE_REDIS_PATH   = "osl-persistence/impl/osl-persistence-redis/native/redis";
    private static final String REDIS_EXEC            = "redis-server";
    private static final String MAKE                  = "make";
    private static final String ERROR_MESSAGE         = "It is necessary to newPair an environment variable or JVM property for OSL_HOME ." +
                                                        " It is possible also to newPair a variable or JVM property for REDIS_HOME";
    private Process             currentProcess        = null;
    private static final String ERROR_COMPILING_REDIS = "Error on redis compilation. The executable wasn't found after the make call.";

    public synchronized void startServerIfNecessary( final Partition somePartition,
                                                     final JRedisFactory factory ) {
        if (currentProcess == null) {
            String oslHome = getVar("OSL_HOME");
            String redisHome = getVar("REDIS_HOME");
            if (oslHome == null && redisHome == null)
                throw new IllegalStateException(ERROR_MESSAGE);
            try {
                if (redisHome == null) redisHome = concatPaths(oslHome, RELATIVE_REDIS_PATH);
                String redisExec = concatPaths(redisHome, REDIS_EXEC);
                if (!new File(redisExec).exists()) {
                    Process make = Runtime.getRuntime().exec(MAKE, null, new File(redisHome));
                    make.waitFor();
                    if (!new File(redisExec).exists()) {
                        throw new IllegalStateException(ERROR_COMPILING_REDIS);
                    }
                }
                currentProcess = Runtime.getRuntime().exec(redisExec, null, new File(redisHome));
                Thread.sleep(500);//give some time to redis server start
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

                    @Override
                    public void run() {
                        if (currentProcess != null) {
                            try {
                                JRedis redis = factory.getFrom(somePartition);
                                redis.save();
                                redis.shutdown();
                                currentProcess = null;
                            } catch (Exception e) {
                                throw logAndReturnNew(e, SLRuntimeException.class);
                            }
                        }
                    }
                }));
            } catch (Exception e) {
                throw logAndReturnNew(e, SLRuntimeException.class);
            }
        }
    }

    private String getVar( String varName ) {
        String var = System.getenv(varName);
        if (var == null) var = System.getProperty(varName);
        return var;
    }

}
