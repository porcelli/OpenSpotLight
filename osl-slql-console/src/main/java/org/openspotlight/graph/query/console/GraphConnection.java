/**
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
package org.openspotlight.graph.query.console;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.SLSimpleGraphSession;
import org.openspotlight.graph.client.RemoteGraphSessionFactory;
import org.openspotlight.graph.client.RemoteGraphSessionFactory.RemoteGraphFactoryConnectionDataImpl;

import java.io.IOException;

/**
 * The Class GraphConnection. This implementation should be changes as soon we get remote access to graph done.
 * 
 * @author porcelli
 */
public class GraphConnection {

    private RemoteGraphSessionFactory factory;

    /**
     * Connects at server and returns {@link org.openspotlight.graph.SLSimpleGraphSession}.
     * 
     * @param serverName the server name
     * @param portNumber the port number
     * @param userName the user name
     * @param passw the passw
     * @param repositoryName the repository Name
     * @return the graph session
     * @throws SLException the SL exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws ClassNotFoundException the class not found exception
     */
    public SLSimpleGraphSession connect( final String serverName,
                                   final int portNumber,
                                   final String userName,
                                   final String passw,
                                   final String repositoryName ) throws SLException, IOException, ClassNotFoundException {

        this.factory = new RemoteGraphSessionFactory(new RemoteGraphFactoryConnectionDataImpl(serverName, userName, passw,
                                                                                              portNumber));
        return this.factory.createRemoteGraphSession(userName, passw, repositoryName);

    }

}
