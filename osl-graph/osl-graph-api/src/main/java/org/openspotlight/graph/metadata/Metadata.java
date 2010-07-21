/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 *  Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 *  or third-party contributors as indicated by the @author tags or express
 *  copyright attribution statements applied by the authors.  All third-party
 *  contributions are distributed under license by CARAVELATECH CONSULTORIA E
 *  TECNOLOGIA EM INFORMATICA LTDA.
 *
 *  This copyrighted material is made available to anyone wishing to use, modify,
 *  copy, or redistribute it subject to the terms and conditions of the GNU
 *  Lesser General Public License, as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *  See the GNU Lesser General Public License  for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this distribution; if not, write to:
 *  Free Software Foundation, Inc.
 *  51 Franklin Street, Fifth Floor
 *  Boston, MA  02110-1301  USA
 *
 * **********************************************************************
 *  OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 *  Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 *  EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 *  @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 *  Todas as contribuições de terceiros estão distribuídas sob licença da
 *  CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 *  Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 *  termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 *  Foundation.
 *
 *  Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 *  GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 *  FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 *  Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 *  programa; se não, escreva para:
 *  Free Software Foundation, Inc.
 *  51 Franklin Street, Fifth Floor
 *  Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph.metadata;

import org.openspotlight.graph.Link;
import org.openspotlight.graph.Node;

/**
 * This interface is composed by sugar methods for most simple and common serach around metadata context. If its necessary a more
 * detailed information around metadata you can query the metadata context using
 * {@link org.openspotlight.graph.manipulation.GraphReader#createQueryApi} or
 * {@link org.openspotlight.graph.manipulation.GraphReader#createQueryText} methods.
 * 
 * @author porcelli
 * @author feuteston
 * @author Vitor Hugo Chagas
 */
public interface Metadata {

    /**
     * Returns the metadata context id.
     * 
     * @return the context id
     */
    public String getMetaContextId();

    /**
     * Returns a meta node type based on specific node type class
     * 
     * @param nodeType the node type class
     * @return the meta node type or null if not found
     */
    public MetaNodeType getMetaNodeType( Class<? extends Node> nodeType );

    /**
     * Returns a meta node type based on specific node type name
     * 
     * @param nodeTypeName the node type name
     * @return the meta node type or null if not found
     */
    public MetaNodeType getMetaNodeType( String nodeTypeName );

    /**
     * Returns a meta link type based on specific link type class
     * 
     * @param linkType the link type class
     * @return the meta link type or null if not found
     */
    public MetaLinkType getMetaLinkType( Class<? extends Link> linkType );

    /**
     * Returns a meta link type based on specific link type name
     * 
     * @param linkTypeName the link type name
     * @return the meta link type or null if not found
     */
    public MetaLinkType getMetaLinkType( String linkTypeName );

    /**
     * Returns an iterable of all meta link types registered into metadata context.
     * 
     * @return all registered metadata link types
     */
    public Iterable<MetaLinkType> getMetaLinkTypes();

    /**
     * Returns an iterable of all meta node types registered into metadata context.
     * 
     * @return all registered metadata node types
     */
    public Iterable<MetaNodeType> getMetaNodesTypes();

}
