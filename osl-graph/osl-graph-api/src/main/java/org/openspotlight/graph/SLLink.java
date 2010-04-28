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
import java.text.Collator;

import org.openspotlight.common.concurrent.LockContainer;
import org.openspotlight.common.concurrent.NeedsSyncronizationSet;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.exception.SLPropertyNotFoundException;
import org.openspotlight.graph.exception.SLPropertyTypeInvalidException;
import org.openspotlight.remote.annotation.DisposeMethod;

/**
 * The Interface SLLink.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLLink extends Comparable<SLLink>, LockContainer {
    /** The Constant SIDE_SOURCE. */
    public static final int SIDE_SOURCE            = 4;                                                                         // 100

    /** The Constant SIDE_TARGET. */
    public static final int SIDE_TARGET            = 2;                                                                         // 010

    /** The Constant SIDE_BOTH. */
    public static final int SIDE_BOTH              = SLLink.SIDE_SOURCE | SLLink.SIDE_TARGET;                                   // 001

    /** The Constant DIRECTION_UNI. */
    public static final int DIRECTION_UNI          = 4;                                                                         // 100

    /** The Constant DIRECTION_UNI_REVERSAL. */
    public static final int DIRECTION_UNI_REVERSAL = 2;                                                                         // 010

    /** The Constant DIRECTION_BI. */
    public static final int DIRECTION_BI           = 1;                                                                         // 001

    /** The Constant DIRECTION_ANY. */
    public static final int DIRECTION_ANY          = SLLink.DIRECTION_UNI | SLLink.DIRECTION_UNI_REVERSAL | SLLink.DIRECTION_BI;

    /**
     * Gets the iD.
     * 
     * @return the iD
     */
    public String getID();

    /**
     * Gets the link type.
     * 
     * @return the link type
     */
    public Class<? extends SLLink> getLinkType();

    /**
     * Gets the meta link.
     * 
     * @return the meta link
     */
    public SLMetaLink getMetaLink();

    /**
     * Gets the other side.
     * 
     * @param side the side
     * @return the other side
     */
    public SLNode getOtherSide( SLNode side );

    /**
     * Gets the properties.
     * 
     * @return the properties
     */
    public NeedsSyncronizationSet<SLLinkProperty<Serializable>> getProperties();

    /**
     * Gets the property.
     * 
     * @param clazz the clazz
     * @param name the name
     * @return the property
     * @throws org.openspotlight.graph.exception.SLPropertyNotFoundException the SL link property not found exception
     * @throws org.openspotlight.graph.exception.SLPropertyTypeInvalidException the SL invalid link property type exception
     */
    public <V extends Serializable> SLLinkProperty<V> getProperty( Class<V> clazz,
                                                                   String name )
        throws SLPropertyNotFoundException, SLPropertyTypeInvalidException;

    /**
     * Gets the property.
     * 
     * @param clazz the clazz
     * @param name the name
     * @param collator the collator
     * @return the property
     * @throws org.openspotlight.graph.exception.SLPropertyNotFoundException the SL link property not found exception
     * @throws org.openspotlight.graph.exception.SLPropertyTypeInvalidException the SL invalid link property type exception
     */
    public <V extends Serializable> SLLinkProperty<V> getProperty( Class<V> clazz,
                                                                   String name,
                                                                   Collator collator )
        throws SLPropertyNotFoundException, SLPropertyTypeInvalidException;

    /**
     * Gets the property value as string.
     * 
     * @param name the name
     * @return the property value as string
     * @throws org.openspotlight.graph.exception.SLPropertyNotFoundException the SL link property not found exception
     */
    public String getPropertyValueAsString( String name ) throws SLPropertyNotFoundException;

    /**
     * Gets the session.
     * 
     * @return the session
     */
    public SLGraphSession getSession();

    /**
     * Gets the sides.
     * 
     * @return the sides
     */
    public SLNode[] getSides();

    /**
     * Gets the source.
     * 
     * @return the source
     */
    public SLNode getSource();

    /**
     * Gets the target.
     * 
     * @return the target
     */
    public SLNode getTarget();

    /**
     * Checks if is bidirectional.
     * 
     * @return true, if is bidirectional
     */
    public boolean isBidirectional();

    /**
     * Removes the.
     */
    @DisposeMethod
    public void remove();

    /**
     * Sets the property.
     * 
     * @param clazz the clazz
     * @param name the name
     * @param value the value
     * @return the sL link property< v>
     */
    @Deprecated
    public <V extends Serializable> SLLinkProperty<V> setProperty( Class<V> clazz,
                                                                   String name,
                                                                   V value );

    public <V extends Serializable> SLLinkProperty<V> setProperty( Class<V> clazz,
                                                                   VisibilityLevel visibility,
                                                                   String name,
                                                                   V value );

}
