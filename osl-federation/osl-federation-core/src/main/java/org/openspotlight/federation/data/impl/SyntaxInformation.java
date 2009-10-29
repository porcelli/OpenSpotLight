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

import static java.lang.Integer.valueOf;
import static java.text.MessageFormat.format;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;

import java.util.StringTokenizer;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * This is most used on source code pagination on web interface. To determine
 * the syntax highlight information on a IDE with all the source code is
 * possible. But on a web interface with pagination, when you got only a piece
 * on the middle of the code, this information is not so easy to obtain. For
 * this we have this class
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@ThreadSafe
@StaticMetadata(propertyNames = { "lineStart", "lineEnd", "columnStart",
        "columnEnd", "type" }, propertyTypes = { Integer.class, Integer.class,
        Integer.class, Integer.class,
        SyntaxInformation.SyntaxInformationType.class }, keyPropertyName = "describedKey", keyPropertyType = String.class, validParentTypes = { ArtifactAboutToChange.class })
public class SyntaxInformation implements ConfigurationNode {
    
    /**
     * Enum to guard syntax information type.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public enum SyntaxInformationType {
        /**
         * Comment or multi line comment.
         */
        COMMENT,
        /**
         * Reserved keyword.
         */
        RESERVED,
        /**
         * Number literal.
         */
        NUMBER_LITERAL,
        /**
         * String literal.
         */
        STRING_LITERAL,
        /**
         * Variable identifier.
         */
        IDENTIFIER,
        /**
         * Symbol, such as +, -, /, ...
         */
        SYMBOL,
        /**
         * Hidden information on source code, such as form information on VB
         * code.
         */
        HIDDEN
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 1092283780730455977L;
    private static final String LINE_START = "lineStart"; //$NON-NLS-1$
    private static final String LINE_END = "lineEnd"; //$NON-NLS-1$
    private static final String COLUMN_START = "columnStart"; //$NON-NLS-1$
    private static final String COLUMN_END = "columnEnd"; //$NON-NLS-1$
    
    private static final String TYPE = "type"; //$NON-NLS-1$
    
    /**
     * This method returns the described key. Its for internal use only.
     * 
     * 
     * @param lineStart
     * @param lineEnd
     * @param columnStart
     * @param columnEnd
     * @param type
     * @return the described key
     */
    static String getDescribedKey(final Integer lineStart,
            final Integer lineEnd, final Integer columnStart,
            final Integer columnEnd, final SyntaxInformationType type) {
        checkNotNull("lineStart", lineStart); //$NON-NLS-1$
        checkNotNull("lineEnd", lineEnd); //$NON-NLS-1$
        checkNotNull("columnStart", columnStart); //$NON-NLS-1$
        checkNotNull("columnEnd", columnEnd); //$NON-NLS-1$
        checkNotNull("type", type); //$NON-NLS-1$
        final String describedKey = format("{0} {1} {2} {3} {4}", //$NON-NLS-1$ 
                lineStart, lineEnd, columnStart, columnEnd, type);
        return describedKey;
    }
    
    private final InstanceMetadata instanceMetadata;
    
    /**
     * Default constructor for creating syntax information.
     * 
     * @param artifact
     * @param lineStart
     * @param lineEnd
     * @param columnStart
     * @param columnEnd
     * @param type
     */
    public SyntaxInformation(final ArtifactAboutToChange artifact, final Integer lineStart,
            final Integer lineEnd, final Integer columnStart,
            final Integer columnEnd, final SyntaxInformationType type) {
        checkNotNull("artifact", artifact); //$NON-NLS-1$
        checkNotNull("lineStart", lineStart); //$NON-NLS-1$
        checkNotNull("lineEnd", lineEnd); //$NON-NLS-1$
        checkNotNull("columnStart", columnStart); //$NON-NLS-1$
        checkNotNull("columnEnd", columnEnd); //$NON-NLS-1$
        checkNotNull("type", type); //$NON-NLS-1$
        final String describedKey = format("{0} {1} {2} {3} {4}", //$NON-NLS-1$ 
                lineStart, lineEnd, columnStart, columnEnd, type);
        this.instanceMetadata = createWithKeyProperty(this, artifact,
                describedKey);
        this.instanceMetadata.setProperty(LINE_START, lineStart);
        this.instanceMetadata.setProperty(LINE_END, lineEnd);
        this.instanceMetadata.setProperty(COLUMN_START, columnStart);
        this.instanceMetadata.setProperty(COLUMN_END, columnEnd);
        this.instanceMetadata.setProperty(TYPE, type);
        checkCondition("noSyntaxInformation", //$NON-NLS-1$
                artifact.getInstanceMetadata().getChildByKeyValue(
                        this.getClass(), describedKey) == null);
        artifact.getInstanceMetadata().addChild(this);
        
    }
    
    /**
     * This constructor is for internal use only. The message should be formated
     * in a correct way, that is done by this class in the other constructor.
     * 
     * @param artifact
     * @param describedKey
     * 
     */
    public SyntaxInformation(final ArtifactAboutToChange artifact, final String describedKey) {
        this.instanceMetadata = createWithKeyProperty(this, artifact,
                describedKey);
        final StringTokenizer tok = new StringTokenizer(describedKey, " "); //$NON-NLS-1$
        
        final String lineStartStr = tok.nextToken();
        final String lineEndStr = tok.nextToken();
        final String columnStartStr = tok.nextToken();
        final String columnEndStr = tok.nextToken();
        final String typeStr = tok.nextToken();
        
        final Integer lineStart = valueOf(lineStartStr);
        final Integer lineEnd = valueOf(lineEndStr);
        final Integer columnStart = valueOf(columnStartStr);
        final Integer columnEnd = valueOf(columnEndStr);
        final SyntaxInformationType type = SyntaxInformationType
                .valueOf(typeStr);
        
        this.instanceMetadata.setProperty(LINE_START, lineStart);
        this.instanceMetadata.setProperty(LINE_END, lineEnd);
        this.instanceMetadata.setProperty(COLUMN_START, columnStart);
        this.instanceMetadata.setProperty(COLUMN_END, columnEnd);
        this.instanceMetadata.setProperty(TYPE, type);
        checkCondition("noSyntaxInformation", //$NON-NLS-1$
                artifact.getInstanceMetadata().getChildByKeyValue(
                        this.getClass(), describedKey) == null);
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
     * @return the column end number
     */
    public Integer getColumnEnd() {
        return this.instanceMetadata.getProperty(COLUMN_END);
    }
    
    /**
     * 
     * @return the column start number
     */
    public Integer getColumnStart() {
        return this.instanceMetadata.getProperty(COLUMN_START);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public InstanceMetadata getInstanceMetadata() {
        return this.instanceMetadata;
    }
    
    /**
     * 
     * @return the line end number
     */
    public Integer getLineEnd() {
        return this.instanceMetadata.getProperty(LINE_END);
    }
    
    /**
     * 
     * @return the line start number
     */
    public Integer getLineStart() {
        return this.instanceMetadata.getProperty(LINE_START);
    }
    
    /**
     * 
     * @return the syntax information type
     */
    public SyntaxInformationType getType() {
        return this.getInstanceMetadata().getProperty(TYPE);
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
