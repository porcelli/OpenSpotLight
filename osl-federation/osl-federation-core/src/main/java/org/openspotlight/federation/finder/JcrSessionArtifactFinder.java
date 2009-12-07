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
package org.openspotlight.federation.finder;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.openspotlight.common.LazyType;
import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.persist.support.SimplePersistSupport;

public class JcrSessionArtifactFinder<A extends Artifact> extends
		AbstractArtifactFinder<A> implements
		ArtifactFinderWithSaveCapabilitie<A> {

	private static String ROOT_PATH = SharedConstants.DEFAULT_JCR_ROOT_NAME
			+ "/{0}/artifacts";

	public static <X extends Artifact> ArtifactFinder<X> createArtifactFinder(
			final Class<X> artifactType, final Repository repository,
			final Session session) {
		Assertions.checkNotNull("session", session);
		Assertions.checkCondition("sessionAlive", session.isLive());

		return new JcrSessionArtifactFinder<X>(artifactType, session,
				repository);
	}

	public static String getArtifactRootPathFor(final Repository repository) {
		return MessageFormat.format(ROOT_PATH, repository.getName());
	}

	public final String rootPath;

	private final Class<A> artifactType;

	private final Session session;

	private JcrSessionArtifactFinder(final Class<A> artifactType,
			final Session session, final Repository repository) {
		super(repository.getName());
		Assertions.checkCondition("sessionAlive", session.isLive());
		this.session = session;
		this.artifactType = artifactType;
		this.rootPath = getArtifactRootPathFor(repository);
	}

	public boolean canAcceptArtifactSource(final ArtifactSource artifactSource) {
		return true;
	}

	public void closeResources() {

		this.session.logout();

	}

	public Class<A> getArtifactType() {
		return this.artifactType;
	}

	public Class<? extends ArtifactSource> getSourceType() {
		return null;
	}

	protected A internalFindByPath(final String path) {
		Assertions.checkNotEmpty("path", path);
		try {
			final Set<A> found = SimplePersistSupport.findNodesByProperties(
					this.rootPath, this.session, this.artifactType,
					LazyType.DO_NOT_LOAD,
					new String[] { "artifactCompleteName" },
					new Object[] { path });
			if (found.size() > 1) {
				throw new Exception("returned more than one result");
			}
			if (found.size() == 0) {
				return null;
			}
			return found.iterator().next();
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	protected Set<String> internalRetrieveAllArtifactNames(
			final String initialPath) {
		try {
			final String propertyName = MessageFormat
					.format(SimplePersistSupport.PROPERTY_VALUE,
							"artifactCompleteName");
			final String nodeName = SimplePersistSupport
					.getJcrNodeName(this.artifactType);
			final String xpath;
			if (initialPath != null) {
				xpath = MessageFormat.format(
						"{0}//{1}[jcr:contains(@{2},''{3}'')]", this.rootPath,
						nodeName, propertyName, initialPath);
			} else {
				xpath = MessageFormat.format("{0}//{1}", this.rootPath,
						nodeName);
			}

			final Query query = this.session.getWorkspace().getQueryManager()
					.createQuery(xpath, Query.XPATH);
			final QueryResult result = query.execute();
			final NodeIterator nodes = result.getNodes();
			final Set<String> names = new HashSet<String>();
			while (nodes.hasNext()) {
				final Node nextNode = nodes.nextNode();
				if (nextNode.hasProperty(propertyName)) {
					final String propVal = nextNode.getProperty(propertyName)
							.getValue().getString();
					names.add(propVal);
				}
			}
			return names;
		} catch (final Exception e) {
			throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	public void save(final A artifactToSave) {
		artifactToSave.setRepositoryName(getCurrentRepository());
		SimplePersistSupport.convertBeanToJcr(this.rootPath, this.session,
				artifactToSave);
	}
}
