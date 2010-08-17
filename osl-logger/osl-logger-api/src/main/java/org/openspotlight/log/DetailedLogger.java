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

package org.openspotlight.log;

import java.io.Serializable;

import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.security.idm.AuthenticatedUser;

/**
 * This interface describes the Detailed Logger. This logger should be used to log information related to the {@link SLNode}
 * subtypes or {@link ConfigurationNode} subtypes.
 * 
 * @author feu
 */
public interface DetailedLogger {

    /**
     * The ErrorCode describes some special kind of errors.
     */
    public static interface ErrorCode extends SimpleNodeType, Serializable {

        /**
         * Gets the description.
         * 
         * @return the description
         */
        public String getDescription();

        /**
         * Gets the error code.
         * 
         * @return the error code
         */
        public String getErrorCode();

        public void setDescription(String s);

        public void setErrorCode(String s);

    }

    /**
     * The EventType.
     */
    public static enum LogEventType {

        /** The TRACE. */
        TRACE,

        /** The DEBUG. */
        DEBUG,

        /** The INFO. */
        INFO,

        /** The WARN. */
        WARN,

        /** The ERROR. */
        ERROR,

        /** The FATAL. */
        FATAL
    }

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     */
    public void log(AuthenticatedUser user,
                     LogEventType type,
                     ErrorCode errorCode,
                     String detailedMessage,
                     LogableObject... anotherNodes);

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param message the message
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     */
    public void log(AuthenticatedUser user,
                     LogEventType type,
                     ErrorCode errorCode,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes);

    /**
     * Log.
     * 
     * @param type the type
     * @param message the message
     * @param anotherNodes the another nodes
     * @param user the user
     */
    public void log(AuthenticatedUser user,
                     LogEventType type,
                     String message,
                     LogableObject... anotherNodes);

    /**
     * Log.
     * 
     * @param type the type
     * @param message the message
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     */
    public void log(AuthenticatedUser user,
                     LogEventType type,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes);

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     * @param repository the repository
     */
    public void log(AuthenticatedUser user,
                     String repository,
                     LogEventType type,
                     ErrorCode errorCode,
                     String detailedMessage,
                     LogableObject... anotherNodes);

    /**
     * Log.
     * 
     * @param type the type
     * @param errorCode the error code
     * @param message the message
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     * @param repository the repository
     */
    public void log(AuthenticatedUser user,
                     String repository,
                     LogEventType type,
                     ErrorCode errorCode,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes);

    /**
     * Log.
     * 
     * @param type the type
     * @param message the message
     * @param anotherNodes the another nodes
     * @param user the user
     * @param repository the repository
     */
    public void log(AuthenticatedUser user,
                     String repository,
                     LogEventType type,
                     String message,
                     LogableObject... anotherNodes);

    /**
     * Log.
     * 
     * @param type the type
     * @param message the message
     * @param detailedMessage the detailed message
     * @param anotherNodes the another nodes
     * @param user the user
     * @param repository the repository
     */
    public void log(AuthenticatedUser user,
                     String repository,
                     LogEventType type,
                     String message,
                     String detailedMessage,
                     LogableObject... anotherNodes);

}
