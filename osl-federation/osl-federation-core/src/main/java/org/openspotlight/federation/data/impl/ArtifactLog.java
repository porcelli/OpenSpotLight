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

package org.openspotlight.federation.data.impl;

import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Dates.dateTimeFromString;
import static org.openspotlight.common.util.Dates.stringFromDateTime;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;

import java.util.Date;
import java.util.StringTokenizer;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * This Log object will keep track of each artifact to make easy to discover
 * whats is going on with this artifact during the parsing process.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
@StaticMetadata(propertyNames = { "type", "message", "date", "detailedMessage" }, propertyTypes = {
        ArtifactLog.ArtifactLogType.class, String.class, Date.class,
        String.class }, keyPropertyName = "describedMessage", keyPropertyType = String.class, validParentTypes = { Artifact.class })
public class ArtifactLog implements ConfigurationNode {
    
    /**
     * Acceptable types for artifact logging.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static enum ArtifactLogType {
        /**
         * info
         */
        INFO,
        /**
         * warn
         */
        WARN,
        /**
         * error
         */
        ERROR,
        /**
         * fatal
         */
        FATAL,
        /**
         * Simple message
         */
        MESSAGE1,
        /**
         * Debug information
         */
        DEBUG
    }
    
    /**
	 * 
	 */
    private static final long serialVersionUID = 1092283780730455977L;
    
    private final InstanceMetadata instanceMetadata;
    
    private static final String MESSAGE = "message"; //$NON-NLS-1$
    
    private static final String TYPE = "type"; //$NON-NLS-1$
    
    private static final String DATE = "date"; //$NON-NLS-1$
    
    private static final String DETAILED_MESSAGE = "detailedMessage"; //$NON-NLS-1$
    
    /**
     * Default constructor for creating artifact log messages.
     * 
     * @param artifact
     * @param type
     * @param message
     * @param detailedMessage
     */
    public ArtifactLog(final Artifact artifact, final ArtifactLogType type,
            final String message, final String detailedMessage) {
        final Date date = new Date();
        final String dateStr = stringFromDateTime(date);
        final String key = format("{0}   {1}   {2}", dateStr, type, message); //$NON-NLS-1$
        this.instanceMetadata = createWithKeyProperty(this, artifact, key);
        this.instanceMetadata.setProperty(DETAILED_MESSAGE, detailedMessage);
        this.instanceMetadata.setProperty(MESSAGE, message);
        this.instanceMetadata.setProperty(TYPE, type);
        this.instanceMetadata.setProperty(DATE, date);
        checkCondition("noLog", //$NON-NLS-1$
                artifact.getInstanceMetadata().getChildByKeyValue(
                        this.getClass(), key) == null);
        artifact.getInstanceMetadata().addChild(this);
    }
    
    /**
     * This constructor is for internal use only. The message should be formated
     * in a correct way, that is done by this class in the other constructor.
     * 
     * @param artifact
     * @param detailedMessage
     * 
     */
    public ArtifactLog(final Artifact artifact, final String detailedMessage) {
        this.instanceMetadata = createWithKeyProperty(this, artifact,
                detailedMessage);
        final StringTokenizer tok = new StringTokenizer(detailedMessage, "   "); //$NON-NLS-1$
        final String dateStr = tok.nextToken();
        final Date date = dateTimeFromString(dateStr);
        final String typeStr = tok.nextToken();
        final ArtifactLogType type = ArtifactLogType.valueOf(typeStr);
        final String message = tok.nextToken();
        this.instanceMetadata.setProperty(MESSAGE, message);
        this.instanceMetadata.setProperty(TYPE, type);
        this.instanceMetadata.setProperty(DATE, date);
        checkCondition("noLog", //$NON-NLS-1$
                artifact.getInstanceMetadata().getChildByKeyValue(
                        this.getClass(), detailedMessage) == null);
        artifact.getInstanceMetadata().addChild(this);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public final int compareTo(final ConfigurationNode o) {
        return this.instanceMetadata.compare(this, o);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object obj) {
        return this.instanceMetadata.equals(obj);
    }
    
    /**
     * 
     * @return the creation date
     */
    public Date getDate() {
        return this.instanceMetadata.getProperty(DATE);
    }
    
    /**
     * 
     * @return the detailed message
     */
    public String getDetailedMessage() {
        return this.instanceMetadata.getProperty(DETAILED_MESSAGE);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public final InstanceMetadata getInstanceMetadata() {
        return this.instanceMetadata;
    }
    
    /**
     * 
     * @return the message itself
     */
    public String getMessage() {
        return this.instanceMetadata.getProperty(MESSAGE);
    }
    
    /**
     * 
     * @return the message type
     */
    public ArtifactLogType getType() {
        return this.instanceMetadata.getProperty(TYPE);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return this.instanceMetadata.hashCode();
    }
    
}
