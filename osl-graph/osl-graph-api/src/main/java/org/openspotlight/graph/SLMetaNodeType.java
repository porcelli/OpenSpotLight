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

import java.util.Collection;

import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.persistence.SLPersistentNode;

// TODO: Auto-generated Javadoc
/**
 * The Interface SLMetaNodeType.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLMetaNodeType extends SLMetaElement {

    /**
     * Gets the type.
     * 
     * @return the type
     * @throws SLGraphSessionException the SL graph session exception
     */
    public Class<? extends SLNode> getType() throws SLGraphSessionException;

    /**
     * Gets the type name.
     * 
     * @return the type name
     * @throws SLGraphSessionException the SL graph session exception
     */
    public String getTypeName() throws SLGraphSessionException;

    /**
     * Gets the meta properties.
     * 
     * @return the meta properties
     * @throws SLGraphSessionException the SL graph session exception
     */
    public Collection<SLMetaNodeProperty> getMetaProperties() throws SLGraphSessionException;

    /**
     * Gets the meta property.
     * 
     * @param name the name
     * @return the meta property
     * @throws SLGraphSessionException the SL graph session exception
     */
    public SLMetaNodeProperty getMetaProperty( String name ) throws SLGraphSessionException;

    /**
     * Gets the sub meta node type.
     * 
     * @param nodeClass the node class
     * @return the sub meta node type
     * @throws SLGraphSessionException the SL graph session exception
     */
    public SLMetaNodeType getSubMetaNodeType( Class<? extends SLNode> nodeClass ) throws SLGraphSessionException;

    /**
     * Gets the sub meta node type.
     * 
     * @param name the name
     * @return the sub meta node type
     * @throws SLGraphSessionException the SL graph session exception
     */
    public SLMetaNodeType getSubMetaNodeType( String name ) throws SLGraphSessionException;

    /**
     * Gets the sub meta node types.
     * 
     * @return the sub meta node types
     * @throws SLGraphSessionException the SL graph session exception
     */
    public Collection<SLMetaNodeType> getSubMetaNodeTypes() throws SLGraphSessionException;

    /**
     * Gets the meta render hint.
     * 
     * @param name the name
     * @return the meta render hint
     * @throws SLGraphSessionException the SL graph session exception
     */
    public SLMetaRenderHint getMetaRenderHint( String name ) throws SLGraphSessionException;

    /**
     * Gets the meta render hints.
     * 
     * @return the meta render hints
     * @throws SLGraphSessionException the SL graph session exception
     */
    public Collection<SLMetaRenderHint> getMetaRenderHints() throws SLGraphSessionException;

    /**
     * Gets the description.
     * 
     * @return the description
     * @throws SLGraphSessionException the SL graph session exception
     */
    public String getDescription() throws SLGraphSessionException;

    /**
     * Gets the visibility.
     * 
     * @return the visibility
     * @throws SLGraphSessionException the SL graph session exception
     */
    public VisibilityLevel getVisibility() throws SLGraphSessionException;

    /**
     * Gets the parent.
     * 
     * @return the parent
     * @throws SLGraphSessionException the SL graph session exception
     */
    public SLMetaNodeType getParent() throws SLGraphSessionException;

    /**
     * Gets the node.
     * 
     * @return the node
     * @throws SLGraphSessionException the SL graph session exception
     */
    public SLPersistentNode getNode() throws SLGraphSessionException;

}
