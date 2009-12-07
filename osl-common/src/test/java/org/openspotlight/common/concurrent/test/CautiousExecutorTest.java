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
package org.openspotlight.common.concurrent.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.openspotlight.common.concurrent.CautiousExecutor;
import org.openspotlight.common.concurrent.CautiousExecutor.TaskListener;
import org.openspotlight.common.concurrent.CautiousExecutor.ThreadListener;

public class CautiousExecutorTest {

    public static class CustomRunnable implements Runnable {

        public void run() {

        }

    }

    private static class CustomTaskListener implements TaskListener {

        final AtomicInteger integer = new AtomicInteger();

        public void afterExecutingTask( final Runnable r,
                                        final Throwable t ) {
            if (r instanceof CustomRunnable) {
                this.integer.incrementAndGet();
            }

        }

        public void beforeExecutingTask( final Thread t,
                                         final Runnable r ) {
            // TODO Auto-generated method stub

        }

    }

    private static class CustomThreadListener implements ThreadListener {

        final AtomicInteger integer = new AtomicInteger();

        public void afterCreatingThread( final Thread t ) {
            this.integer.incrementAndGet();

        }
    };

    private final CustomThreadListener threadListener = new CustomThreadListener();
    private final CustomTaskListener   taskListener   = new CustomTaskListener();
    private CautiousExecutor           executor;

    @Test
    public void setup() throws Exception {

        this.executor = CautiousExecutor.newFixedThreadPool(4);
        this.executor.addTaskListener(this.taskListener);
        this.executor.addThreadListener(this.threadListener);
        for (int i = 0; i < 100; i++) {
            this.executor.execute(new CustomRunnable());
        }
        Thread.sleep(100);

        this.executor.shutdown();

        assertThat(this.taskListener.integer.get(), is(100));

        assertThat(this.threadListener.integer.get(), is(4));
    }

}
