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
package org.openspotlight.federation.processing.internal.task;

import static org.openspotlight.common.concurrent.Priority.createPriority;

import java.util.concurrent.PriorityBlockingQueue;

import org.openspotlight.common.concurrent.Priority;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.processing.internal.domain.CurrentProcessorContextImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class _6_EndingToProcessArtifactsTask.
 */
public class _5_SaveGraphTask<T extends Artifact> implements ArtifactTask {

	private final String repositoryName;
	private ExecutionContext context;

	private final Priority priority = createPriority(5);

	/**
	 * Instantiates a new _6_ ending to process artifacts task.
	 * 
	 * @param changes
	 *            the changes
	 * @param processor
	 *            the processor
	 */
	public _5_SaveGraphTask(final String repositoryName) {
		super();
		this.repositoryName = repositoryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.federation.processing.internal.task.ArtifactTask#doTask
	 * ()
	 */
	public void doTask() throws Exception {
		this.context.getGraphSession().save();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.federation.processing.internal.task.ArtifactTask#
	 * getCurrentContext()
	 */
	public CurrentProcessorContextImpl getCurrentContext() {
		return null;
	}

	public Priority getPriority() {
		return priority;
	}

	public String getRepositoryName() {
		return this.repositoryName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.openspotlight.federation.processing.internal.task.ArtifactTask#
	 * setBundleContext
	 * (org.openspotlight.federation.processing.internal.domain.ExecutionContext
	 * )
	 */
	public void setBundleContext(final ExecutionContext context) {
		this.context = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.federation.processing.internal.task.ArtifactTask#setQueue
	 * (java.util.concurrent.PriorityBlockingQueue)
	 */
	public void setQueue(final PriorityBlockingQueue<ArtifactTask> queue) {

	}
}
