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

package org.openspotlight.federation.data.load;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.federation.data.util.JcrNodeVisitor.withVisitor;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.jboss.dna.jcr.JcrConfiguration;
import org.jboss.dna.jcr.JcrEngine;
import org.jboss.dna.jcr.SecurityContextCredentials;
import org.jboss.dna.repository.DnaConfiguration.RepositorySourceDefinition;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.ArtifactSource;
import org.openspotlight.federation.data.util.JcrNodeVisitor.NodeVisitor;

/**
 * Artifact loader that loads Artifact for file system using DNA File System
 * Connector.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public abstract class DnaArtifactLoader extends AbstractArtifactLoader {

	/**
	 * JCR visitor to fill all valid artifact names
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	protected static final class FillNamesVisitor implements NodeVisitor {
		final Set<String> names;

		/**
		 * Constructor to initialize final fields
		 * 
		 * @param names
		 */
		FillNamesVisitor(final Set<String> names) {
			this.names = names;
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		public void visiting(final Node n) throws RepositoryException {
			String path = n.getPath();
			if (path.startsWith("/")) { //$NON-NLS-1$
				path = path.substring(1);
			}
			if (!path.equals("")) { //$NON-NLS-1$
				this.names.add(path);
			}
		}

	}

	/**
	 * This {@link GlobalDnaResourceContext} will store all JCR data needed
	 * during the processing, and after the processing it will shutdown all
	 * necessary resources.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 *         FIXME starts only one configuration to improve performance
	 * 
	 */
	protected abstract static class GlobalDnaResourceContext extends
			DefaultGlobalExecutionContext {

		private static final String repositoryName = "repository"; //$NON-NLS-1$
		private static final String repositorySource = "repositorySource"; //$NON-NLS-1$

		private final Map<String, JcrEngine> mappingEngines = new ConcurrentHashMap<String, JcrEngine>();

		private final Map<String, Session> mappingSessions = new ConcurrentHashMap<String, Session>();

		/**
		 * Abstract method to setup the DNA Repository Source
		 * 
		 * @param sourceDefinition
		 * 
		 * @param configuration
		 * @param bundle
		 * @param relative
		 */
		protected abstract void configureWithBundle(
				RepositorySourceDefinition<?> sourceDefinition, ArtifactSource bundle,
				ArtifactMapping relative);

		/**
		 * Creates a new {@link Session jcr session}. The client class is
		 * responsible to close this session when it finish its work.
		 * 
		 * @param name
		 * @return a new and fresh {@link Session}
		 * @throws Exception
		 */
		public Session createSessionForMapping(final String name)
				throws Exception {
			final JcrEngine engine = this.mappingEngines.get(name);
			final Session session = engine.getRepository(repositoryName).login(
					new SecurityContextCredentials(
							DefaultSecurityContext.READ_ONLY));
			return session;
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		public Set<String> getAllArtifactNames(final ArtifactSource bundle,
				final ArtifactMapping mapping) throws ConfigurationException {

			final Set<String> names = new HashSet<String>();
			try {

				final Session session = this.getSessionForMapping(mapping
						.getRelative());
				session.getRootNode().accept(
						withVisitor(new FillNamesVisitor(names)));
				return names;
			} catch (final Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}
		}

		/**
		 * 
		 * @param name
		 * @return the jcr session
		 * @throws Exception
		 */
		public Session getSessionForMapping(final String name) throws Exception {
			Session session = this.mappingSessions.get(name);
			if (session == null) {
				final JcrEngine engine = this.mappingEngines.get(name);
				session = engine.getRepository(repositoryName).login(
						new SecurityContextCredentials(
								DefaultSecurityContext.READ_ONLY));
				this.mappingSessions.put(name, session);
			}
			return session;
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		@Override
		public void globalExecutionAboutToStart(final ArtifactSource bundle) {

			try {
				for (final ArtifactMapping relative : bundle
						.getArtifactMappings()) {

					final JcrConfiguration configuration = new JcrConfiguration();
					this.configureWithBundle(configuration
							.repositorySource(repositorySource), bundle,
							relative);
					configuration.repository(repositoryName).setSource(
							repositorySource);
					configuration.save();
					final JcrEngine engine = configuration.build();
					engine.start();
					this.mappingEngines.put(relative.getRelative(), engine);

				}
			} catch (final Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		@Override
		public void globalExecutionFinished(final ArtifactSource bundle) {
			this.shutdown();
		}

		/**
		 * Finalizes all necessary resources
		 */
		public void shutdown() {
			for (final Map.Entry<String, JcrEngine> entry : this.mappingEngines
					.entrySet()) {
				entry.getValue().shutdown();
			}
			for (final Map.Entry<String, Session> entry : this.mappingSessions
					.entrySet()) {
				entry.getValue().logout();
			}
		}
	}

	protected static final class SingleThreadDnaResourceContext extends
			DefaultThreadExecutionContext {

		private Session session;

		public Session getSession() {
			return this.session;
		}

		/**
		 * 
		 * {@inheritDoc}
		 */
		public byte[] loadArtifactOrReturnNullToIgnore(final ArtifactSource bundle,
				final ArtifactMapping mapping, final String artifactName,
				final GlobalExecutionContext globalContext) throws Exception {
			try {
				final Node node = this.getSession().getRootNode().getNode(
						artifactName);

				final Node content = node.getNode("jcr:content"); //$NON-NLS-1$
				final Value value = content.getProperty("jcr:data").getValue();//$NON-NLS-1$
				final InputStream is = value.getStream();

				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int available;
				while ((available = is.read()) != -1) {
					baos.write(available);
				}
				return baos.toByteArray();
			} catch (final Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}
		}

		@Override
		public void threadExecutionAboutToStart(final ArtifactSource bundle,
				final ArtifactMapping mapping,
				final GlobalExecutionContext globalContext) {
			final GlobalDnaResourceContext context = (GlobalDnaResourceContext) globalContext;

			try {
				this.session = context.getSessionForMapping(mapping
						.getRelative());
			} catch (final Exception e) {
				throw logAndReturnNew(e, ConfigurationException.class);
			}

		}

		@Override
		public void threadExecutionFinished(final ArtifactSource bundle,
				final ArtifactMapping mapping,
				final GlobalExecutionContext globalContext) {
			this.session.logout();
		}

	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	@Override
	protected ThreadExecutionContext createThreadExecutionContext() {
		return new SingleThreadDnaResourceContext();
	}

}
