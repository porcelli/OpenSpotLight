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

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jcr.Session;

import org.openspotlight.common.util.SLCollections;
import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.LastProcessStatus;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.domain.artifact.SyntaxInformationType;
import org.openspotlight.federation.processing.ArtifactChanges;
import org.openspotlight.federation.processing.ArtifactsToBeProcessed;
import org.openspotlight.federation.processing.BundleProcessorSinglePhase;
import org.openspotlight.federation.processing.CurrentProcessorContext;
import org.openspotlight.federation.processing.SaveBehavior;
import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.log.DetailedLogger.LogEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleBundleProcessor implements
		BundleProcessorSinglePhase<StringArtifact> {

	public static List<LastProcessStatus> allStatus = new CopyOnWriteArrayList<LastProcessStatus>();

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public <A extends Artifact> boolean acceptKindOfArtifact(
			final Class<A> kindOfArtifact) {
		return StringArtifact.class.equals(kindOfArtifact);
	}

	public void beforeProcessArtifact(final StringArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) {
		logger.info("starting to process " + artifact);
	}

	public void didFinishProcessing(final ArtifactChanges<Artifact> changes,
			final ExecutionContext context,
			final CurrentProcessorContext currentContext) {

	}

	public void didFinishToProcessArtifact(final StringArtifact artifact,
			final LastProcessStatus status,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) {
		ExampleBundleProcessor.allStatus.add(status);

		logger.info("processed " + artifact);
	}

	public Class<StringArtifact> getArtifactType() {
		return StringArtifact.class;
	}

	@SuppressWarnings("unchecked")
	public Set<Class<? extends StringArtifact>> getArtifactTypes() {
		return SLCollections
				.<Class<? extends StringArtifact>> setOf(StringArtifact.class);
	}

	public SaveBehavior getSaveBehavior() {
		return SaveBehavior.PER_PROCESSING;
	}

	public LastProcessStatus processArtifact(final StringArtifact artifact,
			final CurrentProcessorContext currentContext,
			final ExecutionContext context) throws Exception {
		context.getLogger().log(context.getUser(), LogEventType.DEBUG,
				"another test", artifact);
		for (int i = 0; i < 10; i++) {
			final SLNode groupNode = currentContext.getCurrentNodeGroup();
			final String nodeName = artifact.getArtifactCompleteName() + i;
			final SLNode node = groupNode.addNode(nodeName);
			final SLNode node1 = node.addNode(nodeName);
			context.getGraphSession().addLink(SLLink.class, node, node1, false);
		}

		Session session = (Session) context.getPersistentArtifactManager()
				.getPersistentEngine();

		artifact.addSyntaxInformation(2, 4, 5, 6,
				SyntaxInformationType.COMMENT, session);

		return LastProcessStatus.PROCESSED;
	}

	public void selectArtifactsToBeProcessed(
			final CurrentProcessorContext currentContext,
			final ExecutionContext context,
			final ArtifactChanges<Artifact> changes,
			final ArtifactsToBeProcessed<Artifact> toBeReturned) {

	}

}
