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
import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.GeneratedNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;
import org.openspotlight.federation.data.impl.Artifact.Status;

/**
 * A Custom artifact is an artifact with hierarchical data. So, this artifact is easily represented with Jcr structure instead of
 * bytes to be parsed.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@ThreadSafe
@StaticMetadata( keyPropertyName = "relativeName", keyPropertyType = String.class, validParentTypes = {Bundle.class, Group.class}, propertyNames = {
    "UUID", "version", "status"}, propertyTypes = {String.class, String.class, Status.class} )
public class CustomArtifact implements ConfigurationNode, GeneratedNode, Artifact {

    /**
	 * 
	 */
    private static final long      serialVersionUID = -889016915372708085L;
    private static final String    STATUS           = "status";            //$NON-NLS-1$

    private final InstanceMetadata instanceMetadata;

    /**
     * Creates a stream artifact inside a bundle.
     * 
     * @param bundle
     * @param relativeName
     */
    public CustomArtifact(
                           final Bundle bundle, final String relativeName ) {
        this.instanceMetadata = createWithKeyProperty(this, bundle, relativeName);
        checkCondition("noCustomArtifact", //$NON-NLS-1$
                       bundle.getCustomArtifactByName(relativeName) == null);
        bundle.getInstanceMetadata().addChild(this);
        this.instanceMetadata.setProperty(STATUS, Status.ALREADY_PROCESSED);
    }

    /**
     * Constructor to create a stream artifact inside a project.
     * 
     * @param project
     * @param relativeName
     */
    public CustomArtifact(
                           final Group project, final String relativeName ) {
        this.instanceMetadata = createWithKeyProperty(this, project, relativeName);
        checkCondition("noCustomArtifact", //$NON-NLS-1$
                       project.getCustomArtifactByName(relativeName) == null);
        project.getInstanceMetadata().addChild(this);
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

    public void setStatus( final Status status ) {
        this.instanceMetadata.setProperty(STATUS, status);
    }

}
