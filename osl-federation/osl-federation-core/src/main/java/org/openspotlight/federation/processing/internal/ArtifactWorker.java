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
package org.openspotlight.federation.processing.internal;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;
import org.openspotlight.federation.processing.internal.task.ArtifactTask;
import org.openspotlight.graph.SLConsts;
import org.openspotlight.graph.SLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtifactWorker implements RunnableWithBundleContext {

	private final AtomicBoolean working = new AtomicBoolean(false);
	private final AtomicBoolean error = new AtomicBoolean(false);
	private final AtomicBoolean stopped = new AtomicBoolean(false);
	private Map<String, ExecutionContext> contextMap;

	private final PriorityBlockingQueue<ArtifactTask> queue;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public ArtifactWorker(final PriorityBlockingQueue<ArtifactTask> queue) {
		this.queue = queue;
	}

	public boolean hasError() {
		return error.get();
	}

	public boolean isWorking() {
		return working.get();
	}

	public void run() {

		ArtifactTask task = null;
		infiniteLoop: while (true) {
			try {
				task = queue.poll();
				if (task == null) {
					if (!stopped.get()) {
						continue infiniteLoop;
					} else {
						break infiniteLoop;
					}
				}
				try {
					working.set(true);
					logger.info("starting " + task.getClass().getSimpleName()
							+ " on repository " + task.getRepositoryName());
					task.setQueue(queue);
					final String repositoryName = task.getRepositoryName();
					final ExecutionContext context = contextMap
							.get(repositoryName);
					task.setBundleContext(context);
					final CurrentProcessorContextImpl currentCtx = task
							.getCurrentContext();
					if (currentCtx != null) {
						final SLContext groupContext = context
								.getGraphSession().createContext(
										SLConsts.DEFAULT_GROUP_CONTEXT);
						currentCtx.setGroupContext(groupContext);
					}
					task.doTask();
				} catch (final Exception e) {
					Exceptions.catchAndLog(e);
					error.set(true);
				}

			} finally {
				if (task != null) {
					logger.info("stopping " + task.getClass().getSimpleName()
							+ " on repository " + task.getRepositoryName());
				}
				working.set(false);
			}
		}
	}

	public void setBundleContext(final Map<String, ExecutionContext> contextMap) {
		this.contextMap = contextMap;
	}

	public void stop() {
		stopped.set(true);
	}

}
