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
package org.openspotlight.graph.meta;

import org.openspotlight.graph.SLLink;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLRecursiveMode;
import org.openspotlight.graph.annotation.SLVisibility.VisibilityLevel;
import org.openspotlight.graph.exception.SLMetaLinkTypeNotFoundException;
import org.openspotlight.graph.exception.SLMetaNodeTypeNotFoundException;

import java.util.List;

/**
 * The Interface SLMetadata.
 * 
 * @author Vitor Hugo Chagas
 */
public interface SLMetadata extends LockContainer {

    /**
     * The Enum BooleanOperator.
     * 
     * @author porcelli
     */
    public enum BooleanOperator {

        /** The OR. */
        OR,

        /** The AND. */
        AND
    }

    /**
     * The Enum LogicOperator.
     * 
     * @author porcelli
     */
    public enum LogicOperator {

        /** The EQUALS. */
        EQUALS,

        /** The LIKE begins with. */
        LIKE_BEGINS_WITH,

        /** The LIKE ends with. */
        LIKE_ENDS_WITH,

        /** The LIKE contains. */
        LIKE_CONTAINS
    }

    /**
     * The Enum MetaNodeTypeProperty.
     * 
     * @author porcelli
     */
    public enum MetaNodeTypeProperty {

        /** The NAME. */
        NAME,

        /** The DESCRIPTION. */
        DESCRIPTION
    }

    /**
     * Find meta node type.
     * 
     * @param nodeClass the node class
     * @return the sL meta node type
     */
    public SLMetaNodeType getMetaNodeType( Class<? extends SLNode> nodeClass ) throws SLMetaNodeTypeNotFoundException;

    /**
     * Find meta node type.
     * 
     * @param typeName the type name
     * @return the sL meta node type
     */
    public SLMetaNodeType getMetaNodeType( String typeName ) throws SLMetaNodeTypeNotFoundException;

    /**
     * Find meta node type by description.
     * 
     * @param description the description
     * @return the sL meta node type
     */
    public SLMetaNodeType getMetaNodeTypeByDescription( String description ) throws SLMetaNodeTypeNotFoundException;

    /**
     * Gets the meta link type.
     * 
     * @param linkType the link type
     * @return the meta link type
     */
    public SLMetaLinkType getMetaLinkType( Class<? extends SLLink> linkType ) throws SLMetaLinkTypeNotFoundException;

    /**
     * Gets the meta link type.
     * 
     * @param name the name
     * @return the meta link type
     */
    public SLMetaLinkType getMetaLinkType( String name ) throws SLMetaLinkTypeNotFoundException;

    /**
     * Gets the meta link type by description.
     * 
     * @param description the description
     * @return the meta link type by description
     */
    public SLMetaLinkType getMetaLinkTypeByDescription( String description ) throws SLMetaLinkTypeNotFoundException;

    /**
     * Gets the meta link types.
     * 
     * @return the meta link types
     */
    public Collection<SLMetaLinkType> getMetaLinkTypes();

    /**
     * Gets the meta nodes types.
     * 
     * @return the meta nodes types
     */
    public Collection<SLMetaNodeType> getMetaNodesTypes();

    /**
     * Gets the meta nodes types.
     * 
     * @param recursiveMode the recursive mode
     * @return the meta nodes types
     */
    public Collection<SLMetaNodeType> getMetaNodesTypes( SLRecursiveMode recursiveMode );

    public Collection<SLMetaNodeType> getMetaNodesTypes( SLRecursiveMode recursiveMode,
                                                                            final VisibilityLevel visibility );

    /**
     * Search meta node type.
     * 
     * @param recursiveMode the recursive mode
     * @param visibility the visibility
     * @param property2Find the property2 find
     * @param logicOp the logic op
     * @param booleanOp the boolean op
     * @param values the values
     * @return the collection< sl meta node type>
     */
    public Collection<SLMetaNodeType> searchMetaNodeType( final SLRecursiveMode recursiveMode,
                                                                             final VisibilityLevel visibility,
                                                                             final MetaNodeTypeProperty property2Find,
                                                                             final LogicOperator logicOp,
                                                                             final BooleanOperator booleanOp,
                                                                             final List<String> values );

    // TODO implement searchMetaLinkType
}
