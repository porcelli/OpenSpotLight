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
package org.openspotlight.graph;

import java.io.Serializable;

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.exception.SLGraphSessionException;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLMetaLinkPropertyImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaLinkPropertyImpl implements SLMetaLinkProperty {

    private final Lock                               lock;

    /** The meta link. */
    private final SLMetaLinkImpl                     metaLink;

    /** The p property. */
    private final SLPersistentProperty<Serializable> pProperty;

    private VisibilityLevel                          visibility = null;

    /**
     * Instantiates a new sL meta link property impl.
     * 
     * @param metaLink the meta link
     * @param pProperty the property
     */
    public SLMetaLinkPropertyImpl(
                                   final SLMetaLinkImpl metaLink, final SLPersistentProperty<Serializable> pProperty ) {
        this.metaLink = metaLink;
        this.pProperty = pProperty;
        lock = pProperty.getLockObject();
    }

    /**
     * {@inheritDoc}
     */
    public Lock getLockObject() {
        return lock;
    }

    /**
     * {@inheritDoc}
     */
    public SLMetadata getMetadata() {
        return metaLink.getMetadata();
    }

    /**
     * {@inheritDoc}
     */
    public SLMetaLink getMetaLink() {
        return metaLink;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        synchronized (lock) {

            try {
                return SLCommonSupport.toSimplePropertyName(pProperty.getName());
            } catch (final SLPersistentTreeSessionException e) {
                throw new SLGraphSessionException("Error on attempt to retrieve meta link property name.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings( "unchecked" )
    public Class<? extends Serializable> getType() {
        synchronized (lock) {
            try {
                return (Class<? extends Serializable>)Class.forName((String)pProperty.getValue());
            } catch (final Exception e) {
                throw new SLGraphSessionException("Error on attempt to retrieve meta link property type.", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public VisibilityLevel getVisibility() {
        try {
            if (visibility == null) {
                final String propName = SLCommonSupport.toInternalPropertyName(pProperty.getName() + "."
                                                                               + SLConsts.PROPERTY_NAME_VISIBILITY);

                SLPersistentProperty<String> visibilityProperty = metaLink.getNode().getProperty(String.class, propName);
                visibility = visibilityProperty == null ? VisibilityLevel.PUBLIC : VisibilityLevel.valueOf(visibilityProperty.getValue());
            }
            return visibility;
        } catch (SLPersistentTreeSessionException e) {
            throw new SLGraphSessionException("Error on attempt to retrieve meta property visibility.", e);
        }
    }
}
