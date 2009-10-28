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

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;

import java.io.InputStream;
import java.util.Collection;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.GeneratedNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;
import org.openspotlight.federation.data.impl.Artifact.Status;
import org.openspotlight.federation.data.impl.SyntaxInformation.SyntaxInformationType;

/**
 * A stream artifact is a artifact that can be readen as byte stream. LATER_TASK Do not replicate same artifacts
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@ThreadSafe
@StaticMetadata( keyPropertyName = "relativeName", keyPropertyType = String.class, validParentTypes = {ArtifactSource.class, Group.class}, validChildrenTypes = {SyntaxInformation.class}, propertyNames = {
    "name", "dataSha1", "UUID", "version", "data", "status"}, propertyTypes = {String.class, String.class, String.class,
    String.class, InputStream.class, Status.class} )
public final class StreamArtifact implements ConfigurationNode, GeneratedNode, Artifact {

    /**
	 * 
	 */
    private static final long      serialVersionUID = -889016915372708085L;

    private static final String    DATA             = "data";              //$NON-NLS-1$

    private static final String    STATUS           = "status";            //$NON-NLS-1$

    private static final String    DATA_SHA1        = "dataSha1";          //$NON-NLS-1$

    private final InstanceMetadata instanceMetadata;

    /**
     * Creates a stream artifact inside a bundle.
     * 
     * @param bundle
     * @param relativeName
     */
    public StreamArtifact(
                           final ArtifactSource bundle, final String relativeName ) {
        this.instanceMetadata = createWithKeyProperty(this, bundle, relativeName);
        checkCondition("noStreamArtifact", //$NON-NLS-1$
                       bundle.getStreamArtifactByName(relativeName) == null);
        bundle.getInstanceMetadata().addChild(this);
        //        this.instanceMetadata.setProperty(STATUS, Status.ALREADY_PROCESSED);
    }

    /**
     * Constructor to create a stream artifact inside a project.
     * 
     * @param project
     * @param relativeName
     */
    public StreamArtifact(
                           final Group project, final String relativeName ) {
        this.instanceMetadata = createWithKeyProperty(this, project, relativeName);
        checkCondition("noStreamArtifact", //$NON-NLS-1$
                       project.getStreamArtifactByName(relativeName) == null);
        project.getInstanceMetadata().addChild(this);
    }

    /**
     * Adds a new syntax information if this object does not contains one with the same parameters.
     * 
     * @param lineStart
     * @param lineEnd
     * @param columnStart
     * @param columnEnd
     * @param type
     */
    public void addSyntaxInformation( final Integer lineStart,
                                      final Integer lineEnd,
                                      final Integer columnStart,
                                      final Integer columnEnd,
                                      final SyntaxInformationType type ) {
        final String describedKey = SyntaxInformation.getDescribedKey(lineStart, lineEnd, columnStart, columnEnd, type);
        if (this.instanceMetadata.getChildByKeyValue(SyntaxInformation.class, describedKey) == null) {
            new SyntaxInformation(this, lineStart, lineEnd, columnStart, columnEnd, type);
        }
    }

    /**
     * This method clear the syntax information of this {@link StreamArtifact}.
     */
    public void clearSyntaxInformation() {
        this.instanceMetadata.removeAllChildrenOfType(SyntaxInformation.class);
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo( final ConfigurationNode o ) {
        return this.instanceMetadata.compare(this, o);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals( final Object obj ) {
        return this.instanceMetadata.equals(obj);
    }

    /**
     * @return all syntax information of this {@link StreamArtifact}
     */
    public Collection<SyntaxInformation> getAllSyntaxInformation() {
        return this.instanceMetadata.getChildrensOfType(SyntaxInformation.class);
    }

    /**
     * @return a data stream for this artifact as a transient property
     */
    public InputStream getData() {
        return this.instanceMetadata.getStreamProperty(DATA);
    }

    /**
     * @return a valid signature for this data
     */
    public String getDataSha1() {
        return this.instanceMetadata.getProperty(DATA_SHA1);
    }

    /**
     * {@inheritDoc}
     */
    public InstanceMetadata getInstanceMetadata() {
        return this.instanceMetadata;
    }

    /**
     * @return the relative name for this artifact.
     */
    public String getRelativeName() {
        return (String)this.instanceMetadata.getKeyPropertyValue();
    }

    public Status getStatus() {
        return this.instanceMetadata.getProperty(STATUS);
    }

    /**
     * {@inheritDoc}
     */
    public String getUUID() {
        return this.instanceMetadata.getProperty(Artifact.KeyProperties.UUID.toString());
    }

    /**
     * {@inheritDoc}
     */
    public String getVersionName() {
        return this.instanceMetadata.getProperty(Artifact.KeyProperties.version.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return this.instanceMetadata.hashCode();
    }

    /**
     * Sets a data stream for this artifact as a transient property.
     * 
     * @param data
     */
    public void setData( final InputStream data ) {
        this.instanceMetadata.setStreamProperty(DATA, data);
    }

    /**
     * Sets a valid signature for this data
     * 
     * @param dataSha1
     */
    public void setDataSha1( final String dataSha1 ) {
        this.instanceMetadata.setProperty(DATA_SHA1, dataSha1);
    }

    public void setStatus( final Status status ) {
        this.instanceMetadata.setProperty(STATUS, status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.instanceMetadata.toString();
    }

}
