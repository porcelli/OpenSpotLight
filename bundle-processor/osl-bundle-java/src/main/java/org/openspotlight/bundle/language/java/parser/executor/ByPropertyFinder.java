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
package org.openspotlight.bundle.language.java.parser.executor;

import org.openspotlight.common.concurrent.NeedsSyncronizationList;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLInvalidQueryElementException;
import org.openspotlight.graph.query.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.query.SLQueryApi;
import org.openspotlight.graph.query.SLQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ByPropertyFinder {

    private final String         completeArtifactName;

    private final SLGraphSession session;
    private final Logger         logger = LoggerFactory.getLogger(getClass());
    private final SLNode         contextRootNode;

    public ByPropertyFinder(
                             final String completeArtifactName, final SLGraphSession session, final SLNode contextRootNode ) {
        super();
        this.completeArtifactName = completeArtifactName;
        this.session = session;
        this.contextRootNode = contextRootNode;
    }

    @SuppressWarnings( "unchecked" )
    <T extends SLNode> T findByProperty( final Class<T> type,
                                         final String propertyName,
                                         final String propertyValue )
        throws SLQueryException, SLInvalidQuerySyntaxException, SLInvalidQueryElementException {
        final SLQueryApi query1 = session.createQueryApi();
        query1.select().type(type.getName()).subTypes().selectEnd().where().type(type.getName()).subTypes().each().property(
                                                                                                                            propertyName).equalsTo().value(
                                                                                                                                                           propertyValue).typeEnd().whereEnd();
        final NeedsSyncronizationList<SLNode> result1 = query1.execute().getNodes();
        if (result1.size() > 0) {
            synchronized (result1.getLockObject()) {
                for (final SLNode found : result1) {
                    if (found.getContext().getRootNode().equals(contextRootNode)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(completeArtifactName + ": " + "found on 1st try " + found.getName()
                                         + " for search on type:" + type.getSimpleName() + " with " + propertyName + "="
                                         + propertyValue);
                        }
                        return (T)found;
                    }
                }
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(completeArtifactName + ": " + "not found any node for search on type:" + type.getSimpleName() + " with "
                         + propertyName + "=" + propertyValue);
        }
        return null;
    }

}
