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
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.jboss.dna.connector.filesystem.FileSystemSource;
import org.jboss.dna.jcr.JcrConfiguration;
import org.jboss.dna.jcr.JcrEngine;
import org.jboss.dna.jcr.SecurityContextCredentials;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.federation.data.impl.ArtifactMapping;
import org.openspotlight.federation.data.impl.Bundle;
import org.openspotlight.federation.data.util.JcrNodeVisitor.NodeVisitor;

/**
 * Artifact loader that loads Artifact for file system using DNA File System
 * Connector.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class DnaFileSystemArtifactLoader extends AbstractArtifactLoader {
    
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
    protected static final class GlobalDnaResourceContext implements
            GlobalExecutionContext {
        private static final String repositorySource = "repositorySource"; //$NON-NLS-1$
        private static final String repositoryName = "repository"; //$NON-NLS-1$
        
        private final Map<String, Session> mappingSessions = new ConcurrentHashMap<String, Session>();
        
        private final Map<String, JcrEngine> mappingEngines = new ConcurrentHashMap<String, JcrEngine>();
        
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
        public void globalExecutionAboutToStart(final Bundle bundle) {
            final String initialLookup = bundle.getInitialLookup();
            final String[] relativePaths = bundle.getArtifactMappingNames()
                    .toArray(new String[0]);
            try {
                this.setup(initialLookup, relativePaths);
            } catch (final Exception e) {
                throw logAndReturnNew(e, ConfigurationException.class);
            }
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public void globalExecutionFinished(final Bundle bundle) {
            this.shutdown();
        }
        
        /**
         * Setups all necessary resources.
         * 
         * @param rootPath
         * @param relativePaths
         * @throws Exception
         */
        private void setup(final String rootPath, final String... relativePaths)
                throws Exception {
            
            for (final String relative : relativePaths) {
                
                final JcrConfiguration configuration = new JcrConfiguration();
                configuration.repositorySource(repositorySource).usingClass(
                        FileSystemSource.class).setProperty(
                        "workspaceRootPath", rootPath).setProperty( //$NON-NLS-1$ 
                        "creatingWorkspacesAllowed", true).setProperty( //$NON-NLS-1$
                        "defaultWorkspaceName", relative); //$NON-NLS-1$
                
                configuration.repository(repositoryName).setSource(
                        repositorySource);
                configuration.save();
                final JcrEngine engine = configuration.build();
                engine.start();
                this.mappingEngines.put(relative, engine);
                
            }
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
    
    protected static final class SingleThreadDnaResourceContext implements
            ThreadExecutionContext {
        
        private Session session;
        
        public Session getSession() {
            return this.session;
        }
        
        public void threadExecutionAboutToStart(final Bundle bundle,
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
        
        public void threadExecutionFinished(final Bundle bundle,
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
    protected GlobalDnaResourceContext createGlobalExecutionContext() {
        return new GlobalDnaResourceContext();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    protected ThreadExecutionContext createThreadExecutionContext() {
        return new SingleThreadDnaResourceContext();
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Set<String> getAllArtifactNames(final Bundle bundle,
            final ArtifactMapping mapping, final GlobalExecutionContext context)
            throws ConfigurationException {
        
        final Set<String> names = new HashSet<String>();
        try {
            
            final GlobalDnaResourceContext globalDnaResourceContext = (GlobalDnaResourceContext) context;
            final Session session = globalDnaResourceContext
                    .getSessionForMapping(mapping.getRelative());
            session.getRootNode().accept(
                    withVisitor(new FillNamesVisitor(names)));
            return names;
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public byte[] loadArtifact(final Bundle bundle,
            final ArtifactMapping mapping, final String artifactName,
            final GlobalExecutionContext globalContext,
            final ThreadExecutionContext localContext) throws Exception {
        try {
            final SingleThreadDnaResourceContext context = (SingleThreadDnaResourceContext) localContext;
            final Session session = context.getSession();
            final Node node = session.getRootNode().getNode(artifactName);
            
            final Node content = node.getNode("jcr:content"); //$NON-NLS-1$
            try {
                final InputStream is = content
                        .getProperty("jcr:data").getStream(); //$NON-NLS-1$
                
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int available;
                while ((available = is.read()) != 0) {
                    baos.write(available);
                }
                return baos.toByteArray();
            } catch (final Exception e) {
                if (content.hasProperties()) {
                    final PropertyIterator it = content.getProperties();
                    while (it.hasNext()) {
                        System.out.println(it.nextProperty().getName());
                    }
                }
                throw e;
            }
        } catch (final Exception e) {
            throw logAndReturnNew(e, ConfigurationException.class);
        }
    }
    
}
