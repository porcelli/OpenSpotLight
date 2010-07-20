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

import java.util.Collection;
import java.util.List;

import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.exception.SLRenderHintNotFoundException;
import org.openspotlight.graph.meta.SLMetadata.BooleanOperator;
import org.openspotlight.graph.meta.SLMetadata.LogicOperator;
import org.openspotlight.graph.meta.SLMetadata.MetaNodeTypeProperty;

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
     */
    public Class<? extends SLNode> getType();

    /**
     * Gets the type name.
     * 
     * @return the type name
     */
    public String getTypeName();

    /**
     * Gets the meta properties.
     * 
     * @return the meta properties
     */
    public Collection<SLMetaNodeProperty> getMetaProperties();

    /**
     * Gets the meta property.
     * 
     * @param name the name
     * @return the meta property
     */
    public SLMetaNodeProperty getMetaProperty( String name );

    /**
     * Gets the sub meta node type.
     * 
     * @param nodeClass the node class
     * @return the sub meta node type
     */
    public SLMetaNodeType getSubMetaNodeType( Class<? extends SLNode> nodeClass );

    /**
     * Gets the sub meta node type.
     * 
     * @param name the name
     * @return the sub meta node type
     */
    public SLMetaNodeType getSubMetaNodeType( String name );

    /**
     * Gets the sub meta node types.
     * 
     * @return the sub meta node types
     */
    public Collection<SLMetaNodeType> getSubMetaNodeTypes();

    /**
     * Gets the meta render hint.
     * 
     * @param name the name
     * @return the meta render hint
     */
    public SLMetaRenderHint getMetaRenderHint( String name ) throws SLRenderHintNotFoundException;

    /**
     * Gets the meta render hints.
     * 
     * @return the meta render hints
     */
    public Collection<SLMetaRenderHint> getMetaRenderHints();

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription();

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    public SLMetaNodeType getParent();

    /**
     * Search sub meta node types.
     * 
     * @param property2Find the property2 find
     * @param logicOp the logic op
     * @param booleanOp the boolean op
     * @param values the values
     * @return the collection< sl meta node type>
     */
    public Collection<SLMetaNodeType> searchSubMetaNodeTypes( final MetaNodeTypeProperty property2Find,
                                                              final LogicOperator logicOp,
                                                              final BooleanOperator booleanOp,
                                                              final List<String> values );

}
