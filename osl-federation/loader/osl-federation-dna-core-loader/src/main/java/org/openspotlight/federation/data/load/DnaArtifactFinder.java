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
import org.openspotlight.federation.data.util.JcrNodeVisitor.NodeVisitor;
import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ArtifactSource;
import org.openspotlight.federation.domain.ChangeType;
import org.openspotlight.federation.domain.StreamArtifact;
import org.openspotlight.federation.finder.AbstractArtifactFinder;

/**
 * Artifact loader that loads Artifact for file system using DNA File System Connector.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public abstract class DnaArtifactFinder extends AbstractArtifactFinder<StreamArtifact> {

    /**
     * JCR visitor to fill all valid artifact names
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     */
    protected static final class FillNamesVisitor implements NodeVisitor {
        final Set<String> names;

        /**
         * Constructor to initialize final fields
         * 
         * @param names
         */
        FillNamesVisitor(
                          final Set<String> names ) {
            this.names = names;
        }

        /**
         * {@inheritDoc}
         */
        public void visiting( final Node n ) throws RepositoryException {
            final String path = n.getPath();
            if (!path.equals("")) { //$NON-NLS-1$
                this.names.add(path);
            }
        }

    }

    private static final String                  repositoryName   = "repository";                                      //$NON-NLS-1$

    private static final String                  repositorySource = "repositorySource";                                //$NON-NLS-1$

    private final Map<ArtifactSource, JcrEngine> mappingEngines   = new ConcurrentHashMap<ArtifactSource, JcrEngine>();

    private final Map<ArtifactSource, Session>   mappingSessions  = new ConcurrentHashMap<ArtifactSource, Session>();

    @Override
    public synchronized final void closeResources() {
        for (final Map.Entry<ArtifactSource, JcrEngine> entry : this.mappingEngines.entrySet()) {
            entry.getValue().shutdown();
        }
        for (final Map.Entry<ArtifactSource, Session> entry : this.mappingSessions.entrySet()) {
            entry.getValue().logout();
        }
    }

    protected abstract void configureWithBundle( RepositorySourceDefinition<JcrConfiguration> repositorySource2,
                                                 ArtifactSource source );

    public StreamArtifact findByPath( final ArtifactSource artifactSource,
                                      final String rawPath ) {
        try {
            String path;
            if (rawPath.startsWith("/")) {
                path = rawPath.substring(1);
            } else {
                path = rawPath;
            }

            final Node node = this.getSessionForSource(artifactSource).getRootNode().getNode(path);

            final Node content = node.getNode("jcr:content"); //$NON-NLS-1$
            final Value value = content.getProperty("jcr:data").getValue();//$NON-NLS-1$
            final InputStream is = value.getStream();

            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int available;
            while ((available = is.read()) != -1) {
                baos.write(available);
            }
            final StreamArtifact artifact = Artifact.createArtifact(StreamArtifact.class, path, ChangeType.INCLUDED);
            artifact.setContent(new String(baos.toByteArray()));
            return artifact;
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }

    }

    /**
     * @param name
     * @return the jcr session
     * @throws Exception
     */
    public synchronized Session getSessionForSource( final ArtifactSource source ) throws Exception {
        Session session = this.mappingSessions.get(source);
        if (session == null) {
            JcrEngine engine = this.mappingEngines.get(source);
            if (engine == null) {
                this.setupSource(source);
                engine = this.mappingEngines.get(source);
            }
            session = engine.getRepository(repositoryName).login(new SecurityContextCredentials(DefaultSecurityContext.READ_ONLY));
            this.mappingSessions.put(source, session);
        }
        return session;
    }

    public Set<String> retrieveAllArtifactNames( final ArtifactSource artifactSource,
                                                 final String initialPath ) {
        try {
            final Set<String> result = new HashSet<String>();
            final Node rootNode = this.getSessionForSource(artifactSource).getRootNode();
            final Node initial = initialPath == null ? rootNode : rootNode.getNode(initialPath);
            initial.accept(withVisitor(new FillNamesVisitor(result)));

            return result;
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }

    public synchronized void setupSource( final ArtifactSource source ) {
        try {

            final JcrConfiguration configuration = new JcrConfiguration();
            this.configureWithBundle(configuration.repositorySource(repositorySource), source);
            configuration.repository(repositoryName).setSource(repositorySource);
            configuration.save();
            final JcrEngine engine = configuration.build();
            engine.start();
            this.mappingEngines.put(source, engine);
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }

    }

}
