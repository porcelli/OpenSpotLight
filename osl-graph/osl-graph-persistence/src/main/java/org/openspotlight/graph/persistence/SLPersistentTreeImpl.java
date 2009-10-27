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
package org.openspotlight.graph.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Credentials;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.openspotlight.jcr.provider.JcrConnectionDescriptor;

/**
 * The Class SLPersistentTreeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLPersistentTreeImpl implements SLPersistentTree {

    /** The repository. */
    private final Repository                    repository;

    /** The credentials. */
    private final Credentials                   credentials;

    private final JcrConnectionDescriptor       jcrConnectionDescription;

    private final List<SLPersistentTreeSession> sessions;

    /**
     * Instantiates a new sL persistent tree impl.
     * 
     * @param repository the repository
     * @param credentials the credentials
     */
    public SLPersistentTreeImpl(
                                 final Repository repository, final Credentials credentials,
                                 final JcrConnectionDescriptor jcrConnectionDescription ) {
        this.repository = repository;
        this.credentials = credentials;
        this.jcrConnectionDescription = jcrConnectionDescription;
        this.sessions = new ArrayList<SLPersistentTreeSession>();
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.persistence.SLPersistentTree#openSession()
     */
    public SLPersistentTreeSession openSession() throws SLPersistentTreeException {
        try {
            final Session session = this.repository.login(this.credentials);
            final SLPersistentTreeSession newSession = new SLPersistentTreeSessionImpl(session);
            this.sessions.add(newSession);
            return newSession;
        } catch (final RepositoryException e) {
            throw new SLPersistentTreeException("Error on attempt to open persistent tree session.", e);
        }
    }

    //@Override
    /* (non-Javadoc)
     * @see org.openspotlight.graph.persistence.SLPersistentTree#shutdown()
     */
    public void shutdown() {
        for (final SLPersistentTreeSession activeSession : this.sessions) {
            activeSession.close();
        }
        //        JcrConnectionProvider.invalidateCache(jcrConnectionDescription);
    }
}
