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

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.log.DetailedLogger;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.PersistPropertyAsStream;
import org.openspotlight.persist.annotation.SimpleNodeType;

/**
 * The Class LogEntry is used to represent a new log entry.
 */
@Name("log_entry")
public class LogEntry implements SimpleNodeType, Serializable {

    /**
     *
     */
    private static final long             serialVersionUID = -1429744150741798679L;

    /**
     * The date.
     */
    private Date                          date;

    /**
     * The detailed message.
     */
    private String                        detailedMessage;

    /**
     * The error code.
     */
    private DetailedLogger.ErrorCode      errorCode;

    /**
     * The hash code.
     */
    private int                           hashCode;

    /**
     * The message.
     */
    private String                        message;

    /**
     * The nodes.
     */
    private List<LoggedObjectInformation> nodes;

    private long                          timestamp;

    /**
     * The type.
     */
    private DetailedLogger.LogEventType   type;

    /**
     * Instantiates a new log entry.
     * 
     * @param errorCode the error code
     * @param type the type
     * @param message the message
     * @param detailedMessage the detailed message
     * @param nodes the nodes
     */
    LogEntry(
             final DetailedLogger.ErrorCode errorCode, final long timestamp, final DetailedLogger.LogEventType type,
             final String message,
             final String detailedMessage, final List<LoggedObjectInformation> nodes) {
        this.errorCode = errorCode;
        this.type = type;
        this.message = message;
        this.detailedMessage = detailedMessage;
        this.nodes = Collections.unmodifiableList(nodes);
        this.timestamp = timestamp;
        date = new Date(timestamp);
        hashCode = HashCodes.hashOf(this.type, this.message, this.detailedMessage, this.nodes, date, this.errorCode);
    }

    public LogEntry() {}

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) { return true; }
        if (!(obj instanceof LogEntry)) { return false; }
        final LogEntry that = (LogEntry) obj;
        return Equals.eachEquality(Arrays.of(type, message, detailedMessage, nodes, date, errorCode),
                Arrays.andOf(that.type, that.message, that.detailedMessage, that.nodes, that.date,
                        that.errorCode));
    }

    /**
     * Gets the date.
     * 
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Gets the detailed message.
     * 
     * @return the detailed message
     */
    public String getDetailedMessage() {
        return detailedMessage;
    }

    public DetailedLogger.ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Gets the message.
     * 
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the nodes.
     * 
     * @return the nodes
     */
    @PersistPropertyAsStream
    public List<LoggedObjectInformation> getNodes() {
        return nodes;
    }

    @KeyProperty
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    @KeyProperty
    public DetailedLogger.LogEventType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    public void setDate(final Date date) {
        this.date = date;
    }

    public void setDetailedMessage(final String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

    public void setErrorCode(final DetailedLogger.ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public void setNodes(final List<LoggedObjectInformation> nodes) {
        this.nodes = nodes;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(final DetailedLogger.LogEventType type) {
        this.type = type;
    }

}
