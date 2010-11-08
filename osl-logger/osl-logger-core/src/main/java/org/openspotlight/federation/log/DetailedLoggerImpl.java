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
package org.openspotlight.federation.log;

import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.security.idm.AuthenticatedUser;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.StorageNode;

public final class DetailedLoggerImpl implements DetailedLogger {

    private final StorageNode                                       rootNode;

    private final SimplePersistCapable<StorageNode, StorageSession> simplePersist;

    public DetailedLoggerImpl(
                              final SimplePersistCapable<StorageNode, StorageSession> simplePersist) {
        try {
            this.simplePersist = simplePersist;
            rootNode = this.simplePersist.getCurrentSession()
                    .withPartition(this.simplePersist.getCurrentPartition())
                    .createNewSimpleNode("log");

        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    @Override
    public void log(final AuthenticatedUser user, final LogEventType type,
                    final ErrorCode errorCode, final String detailedMessage,
                    final Object... anotherNodes) {

    }

    @Override
    public void log(final AuthenticatedUser user, final LogEventType type,
                    final ErrorCode errorCode, final String message,
                    final String detailedMessage, final Object... anotherNodes) {
        this.log(user, null, type, errorCode, message, detailedMessage,
                anotherNodes);

    }

    @Override
    public void log(final AuthenticatedUser user, final LogEventType type,
                    final String message, final Object... anotherNodes) {
        this.log(user, null, type, null, message, null, anotherNodes);

    }

    @Override
    public void log(final AuthenticatedUser user, final LogEventType type,
                    final String message, final String detailedMessage,
                    final Object... anotherNodes) {
        this.log(user, null, type, null, message, detailedMessage, anotherNodes);

    }

    @Override
    public void log(final AuthenticatedUser user, final String repository,
                    final LogEventType type, final ErrorCode errorCode,
                    final String detailedMessage, final Object... anotherNodes) {
        this.log(user, repository, type, errorCode, null, detailedMessage,
                anotherNodes);

    }

    @Override
    public void log(final AuthenticatedUser user, final String repository,
                    final LogEventType type, final ErrorCode errorCode,
                    final String message, final String detailedMessage,
                    final Object... anotherNodes) {
        final org.openspotlight.federation.log.LogEntry entry = new org.openspotlight.federation.log.LogEntry(
                errorCode, System.currentTimeMillis(), type, message,
                detailedMessage,
                LoggedObjectInformation.getHierarchyFrom(anotherNodes));

        simplePersist.convertBeanToNode(rootNode, entry);
        simplePersist.getCurrentSession().flushTransient();

    }

    @Override
    public void log(final AuthenticatedUser user, final String repository,
                    final LogEventType type, final String message,
                    final Object... anotherNodes) {
        this.log(user, repository, type, null, message, null, anotherNodes);

    }

    @Override
    public void log(final AuthenticatedUser user, final String repository,
                    final LogEventType type, final String message,
                    final String detailedMessage, final Object... anotherNodes) {
        this.log(user, repository, type, null, message, detailedMessage,
                anotherNodes);
    }

}
