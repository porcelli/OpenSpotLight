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

package org.openspotlight.federation.data.processing.test;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.junit.Test;

enum FinishMode {
	NORMAL, ERROR
}

class Sleepy implements Callable<FinishMode> {

	public FinishMode call() throws Exception {
		try {
			sleep(ThreadDistributionTest.TIME);
			return FinishMode.NORMAL;
		} catch (final Exception e) {
			return FinishMode.ERROR;
		}

	}

}

/**
 * This test is just to see if I'm using threads in a correct way.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class ThreadDistributionTest {

	static final int TIME = 100;
	static final int NUMBER_OF_THREADS = 10;

	@Test
	public void shouldSpendLessThanFiveSeconds() throws Exception {
		final List<Callable<FinishMode>> allSleepys = new ArrayList<Callable<FinishMode>>();
		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			allSleepys.add(new Sleepy());
		}

		final long start = currentTimeMillis();
		final ExecutorService executor = newFixedThreadPool(4);
		try {
			final List<Future<FinishMode>> returnStatus = new ArrayList<Future<FinishMode>>();
			for (final Callable<FinishMode> r : allSleepys) {
				final Future<FinishMode> f = executor.submit(r);
				returnStatus.add(f);
			}
			boolean finished = false;
			infinite: while (true) {
				finished = true;
				isFinished: for (final Future<FinishMode> f : returnStatus) {
					if (!f.isDone()) {
						finished = false;
						break isFinished;
					}
				}
				if (finished) {
					break infinite;
				}
				sleep(TIME / (NUMBER_OF_THREADS * 10));
			}
			final long end = currentTimeMillis();
			assertThat((end - start) < (NUMBER_OF_THREADS * TIME / 3), is(true));
		} finally {
			executor.shutdown();
		}
	}

	@Test
	public void shouldSpendTenSeconds() throws Exception {
		final List<Callable<FinishMode>> allSleepys = new ArrayList<Callable<FinishMode>>();
		for (int i = 0; i < NUMBER_OF_THREADS; i++) {
			allSleepys.add(new Sleepy());
		}

		final long start = currentTimeMillis();
		final ExecutorService executor = newSingleThreadExecutor();
		final List<Future<FinishMode>> returnStatus = new ArrayList<Future<FinishMode>>();
		for (final Callable<FinishMode> r : allSleepys) {
			final Future<FinishMode> f = executor.submit(r);
			returnStatus.add(f);
		}
		boolean finished = false;
		infinite: while (true) {
			finished = true;
			isFinished: for (final Future<FinishMode> f : returnStatus) {
				if (!f.isDone()) {
					finished = false;
					break isFinished;
				}
			}
			if (finished) {
				break infinite;
			}
			sleep(TIME / (NUMBER_OF_THREADS * 10));
		}
		final long end = currentTimeMillis();
		assertThat((end - start) >= (NUMBER_OF_THREADS * TIME), is(true));
	}

}
