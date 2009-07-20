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

import static org.openspotlight.common.util.Arrays.andValues;
import static org.openspotlight.common.util.Arrays.map;
import static org.openspotlight.common.util.Arrays.ofKeys;
import static org.openspotlight.federation.data.InstanceMetadata.Factory.createWithKeyProperty;
import static org.openspotlight.federation.data.StaticMetadata.Factory.createImmutable;
import static org.openspotlight.federation.data.StaticMetadata.Factory.createMutable;

import java.io.InputStream;

import net.jcip.annotations.ThreadSafe;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata;
import org.openspotlight.federation.data.StaticMetadata;

/**
 * A stream artifact is a artifact that can be readen as byte stream.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("unchecked")
@ThreadSafe
public final class StreamArtifact implements ConfigurationNode {
    
    /**
	 * 
	 */
    private static final long serialVersionUID = -889016915372708085L;
    
    private static final String DATA = "data"; //$NON-NLS-1$
    private static final String RELATIVE_NAME = "relativeName"; //$NON-NLS-1$
    
    private static final String DATA_SHA1 = "dataSha1"; //$NON-NLS-1$
    
    static {
        final StaticMetadata newStaticMetadata = createMutable();
        newStaticMetadata.setType(StreamArtifact.class);
        newStaticMetadata.setParentNodeValidTypes(Project.class, Bundle.class);
        newStaticMetadata.setKeyProperty(RELATIVE_NAME);
        newStaticMetadata.setPropertyTypes(map(ofKeys(DATA_SHA1, DATA,
                RELATIVE_NAME), andValues(String.class, InputStream.class,
                String.class)));
        staticMetadata = createImmutable(newStaticMetadata);
    }
    private final InstanceMetadata instanceMetadata;
    
    private static final StaticMetadata staticMetadata;
    
    /**
     * Constructor to create a stream artifact inside a bundle.
     * 
     * @param bundle
     * @param relativeName
     */
    public StreamArtifact(final Bundle bundle, final String relativeName) {
        this.instanceMetadata = createWithKeyProperty(staticMetadata, this,
                bundle, relativeName);
    }
    
    /**
     * Constructor to create a stream artifact inside a project.
     * 
     * @param project
     * @param relativeName
     */
    public StreamArtifact(final Project project, final String relativeName) {
        this.instanceMetadata = createWithKeyProperty(staticMetadata, this,
                project, relativeName);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public int compareTo(final ConfigurationNode o) {
        return this.instanceMetadata.compare(this, o);
    }
    
    /**
     * 
     * @return a data stream for this artifact as a transient property
     */
    public InputStream getData() {
        return this.instanceMetadata.getTransientProperty(DATA);
    }
    
    /**
     * 
     * @return a valid signature for this data
     */
    public String getDataSha1() {
        return this.instanceMetadata.getProperty(DATA_SHA1);
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
     * @return the relative name for this artifact.
     */
    public String getRelativeName() {
        return this.instanceMetadata.getProperty(RELATIVE_NAME);
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public StaticMetadata getStaticMetadata() {
        return staticMetadata;
    }
    
    /**
     * Sets a data stream for this artifact as a transient property.
     * 
     * @param data
     */
    public void setData(final InputStream data) {
        this.instanceMetadata.setTransientProperty(DATA, data);
    }
    
    /**
     * Sets a valid signature for this data
     * 
     * @param dataSha1
     */
    public void setDataSha1(final String dataSha1) {
        this.instanceMetadata.setProperty(DATA_SHA1, dataSha1);
    }
    
    /**
     * Sets the relative name for this artifact.
     * 
     * @param relativeName
     */
    public void setRelativeName(final String relativeName) {
        this.instanceMetadata.setProperty(RELATIVE_NAME, relativeName);
    }
    
}
