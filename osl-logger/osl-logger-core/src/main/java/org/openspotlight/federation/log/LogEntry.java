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
     * The error code.
     */
    private DetailedLogger.ErrorCode      errorCode;

    /**
     * The type.
     */
    private DetailedLogger.LogEventType   type;

    /**
     * The message.
     */
    private String                        message;

    /**
     * The detailed message.
     */
    private String                        detailedMessage;

    /**
     * The nodes.
     */
    private List<LoggedObjectInformation> nodes;

    /**
     * The date.
     */
    private Date                          date;

    private long                          timestamp;

    /**
     * The hash code.
     */
    private int                           hashCode;

    public LogEntry() {}

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

    @KeyProperty
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
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

    /**
     * Gets the type.
     * 
     * @return the type
     */
    @KeyProperty
    public DetailedLogger.LogEventType getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode() {
        return hashCode;
    }

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

    public void setType(final DetailedLogger.LogEventType type) {
        this.type = type;
    }

}
