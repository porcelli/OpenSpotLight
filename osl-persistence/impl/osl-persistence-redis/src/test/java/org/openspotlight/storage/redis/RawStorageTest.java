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

import org.jredis.JRedis;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by User: feu - Date: Mar 22, 2010 - Time: 4:41:46 PM
 */
public class RawStorageTest {

    private static class Singleton {

        private static ThreadLocal<JRedis> threadLocal = new ThreadLocal<JRedis>();

        public static JRedis get() {
            synchronized (Thread.currentThread()) {

                JRedis r = threadLocal.get();
                if (r == null) {
                    r = new org.jredis.ri.alphazero.JRedisClient();
                    threadLocal.set(r);
                }
                return r;
            }
        }

    }

    @Test
    public void shouldUseRedis() throws Exception {


        /*
         * name
         * ids
         * simple-properties
         * pojo-properties
         * stream-properties
         * list-properties
         * map-properties
         * set-properties
         * serializable-list-properties
         * serializable-map-properties
         * serializable-set-properties
         */

                ExecutorService
        pool = Executors.newFixedThreadPool(4);
        List<Callable<Void>> callables = new LinkedList<Callable<Void>>();


        for (int nodeHashT = 0; nodeHashT < 1000; nodeHashT++) {
            final int nodeHash = nodeHashT;
            callables.add(new Callable<Void>() {
                public Void call() throws Exception {
                    Singleton.get().sadd("5-node-hashs", nodeHash);
                    return null;
                }

            });


            callables.add(new Callable<Void>() {
                public Void call() throws Exception {
                    Singleton.get().set("5-node:" + nodeHash + "::node-name", "<node-name>" + nodeHash);
                    return null;
                }

            });
            for (int propertyT = 0; propertyT < 100; propertyT++) {
                final int property = propertyT;
                //keys
                callables.add(new Callable<Void>() {
                    public Void call() throws Exception {
                        Singleton.get().sadd("5-node:" + nodeHash + "::key-names", property);
                        return null;
                    }

                });
                callables.add(new Callable<Void>() {
                    public Void call() throws Exception {
                        Singleton.get().set("5-node:" + nodeHash + "::key:" + property + "::type", "<key-type>" + property);
                        return null;
                    }

                });
                callables.add(new Callable<Void>() {
                    public Void call() throws Exception {
                        Singleton.get().set("5-node:" + nodeHash + "::key:" + property + ":value", "<key-value>" + property);
                        return null;
                    }

                });

                //simple properties
                callables.add(new Callable<Void>() {
                    public Void call() throws Exception {
                        Singleton.get().sadd("5-node:" + nodeHash + "::simple-properties-names", property);
                        return null;
                    }

                });
                callables.add(new Callable<Void>() {
                    public Void call() throws Exception {
                        Singleton.get().set("5-node:" + nodeHash + "::simple-property:" + property + "::type", "<key-type>" + property);
                        return null;
                    }

                });
                callables.add(new Callable<Void>() {
                    public Void call() throws Exception {
                        Singleton.get().set("5-node:" + nodeHash + "::simple-property:" + property + "::value", "<key-value>" + property);
                        return null;
                    }

                });

            }

        }
        List<Future<Void>> futures = pool.invokeAll(callables);

        for (Future<Void> f : futures) {
            f.get();
        }


    }


}
