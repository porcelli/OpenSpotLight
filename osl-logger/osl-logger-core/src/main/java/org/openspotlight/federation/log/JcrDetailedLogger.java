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
package org.openspotlight.federation.log;

import java.util.Date;
import java.util.UUID;

import javax.jcr.Session;

import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory.LogEntry;
import org.openspotlight.federation.log.DetailedJcrLoggerFactory.LoggedObjectInformation;
import org.openspotlight.jcr.util.JCRUtil;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.log.LogableObject;
import org.openspotlight.persist.support.SimplePersistSupport;
import org.openspotlight.security.idm.AuthenticatedUser;

/**
 * The JcrDetailedLogger is an implementation of {@link DetailedLogger} based on Jcr. This kind of logger was implemented 'by
 * hand' instead of using {@link SLNode} or {@link ConfigurationNode} by a simple reason. Should not be possible to log the log.
 * If it's necessary this implementation could be changed on the future.
 */
public final class JcrDetailedLogger implements DetailedLogger {

    // FIXME remove this as soon as apache's ticket JCR-2428 is solved. To test
    // it, run the bundle processor test with multiple threads enabled

    private final String  initialPath;

    /** The session. */
    private final Session session;

    public JcrDetailedLogger(
                              final Session session,
                              final LockContainer temporaryLock ) {
        try {
            this.session = session;
            final String thisSessionEntry = UUID.randomUUID().toString();
            initialPath = SharedConstants.DEFAULT_JCR_ROOT_NAME + "/log/"
                          + thisSessionEntry;
            synchronized (temporaryLock.getLockObject()) {
                JCRUtil.getOrCreateByPath(session, session.getRootNode(),
                                          initialPath);
                this.session.save();
            }
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    public void log( final AuthenticatedUser user,
                     final LogEventType type,
                     final ErrorCode errorCode,
                     final String detailedMessage,
                     final LogableObject... anotherNodes ) {

    }

    public void log( final AuthenticatedUser user,
                     final LogEventType type,
                     final ErrorCode errorCode,
                     final String message,
                     final String detailedMessage,
                     final LogableObject... anotherNodes ) {
        this.log(user, null, type, errorCode, message, detailedMessage,
                 anotherNodes);

    }

    public void log( final AuthenticatedUser user,
                     final LogEventType type,
                     final String message,
                     final LogableObject... anotherNodes ) {
        this.log(user, null, type, null, message, null, anotherNodes);

    }

    public void log( final AuthenticatedUser user,
                     final LogEventType type,
                     final String message,
                     final String detailedMessage,
                     final LogableObject... anotherNodes ) {
        this
                .log(user, null, type, null, message, detailedMessage,
                        anotherNodes);

    }

    public void log( final AuthenticatedUser user,
                     final String repository,
                     final LogEventType type,
                     final ErrorCode errorCode,
                     final String detailedMessage,
                     final LogableObject... anotherNodes ) {
        this.log(user, repository, type, errorCode, null, detailedMessage,
                 anotherNodes);

    }

    public void log( final AuthenticatedUser user,
                     final String repository,
                     final LogEventType type,
                     final ErrorCode errorCode,
                     final String message,
                     final String detailedMessage,
                     final LogableObject... anotherNodes ) {
        final LogEntry entry = new LogEntry(errorCode, new Date(), type,
                                            message, detailedMessage, LoggedObjectInformation
                                                                                             .getHierarchyFrom(anotherNodes));

        final String initialPath = this.initialPath + "/"
                                   + (repository != null ? repository : "noRepository") + "/"
                                   + (user != null ? user.getId() : "noUser") + "/log";
        SimplePersistSupport.convertBeanToJcr(initialPath, session, entry);
        try {
            session.save();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    public void log( final AuthenticatedUser user,
                     final String repository,
                     final LogEventType type,
                     final String message,
                     final LogableObject... anotherNodes ) {
        this.log(user, repository, type, null, message, null, anotherNodes);

    }

    public void log( final AuthenticatedUser user,
                     final String repository,
                     final LogEventType type,
                     final String message,
                     final String detailedMessage,
                     final LogableObject... anotherNodes ) {
        this.log(user, repository, type, null, message, detailedMessage,
                 anotherNodes);
    }

}
