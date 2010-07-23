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
package org.openspotlight.graph.manipulation;

import org.openspotlight.graph.Context;
import org.openspotlight.graph.Link;
import org.openspotlight.graph.Node;

/**
 * This interfaces has a list of method that can create transient data.
 * 
 * @see org.openspotlight.graph.SimpleGraphSession
 * @see org.openspotlight.graph.FullGraphSession
 * @author porcelli
 * @author feuteston
 */
public interface GraphTransientWriter {

    /**
     * Create a new transient node, based on the parameter node type, inside the given context.<br>
     * <b>Note</b> that if node, transient or not, already exists inside context its not duplicated.
     * 
     * @param <T> node type
     * @param context the target context
     * @param clazz the node type to be created
     * @param name the node name
     * @return the transient node
     */
    <T extends Node> T createTransientNode( Context context,
                                            Class<T> clazz,
                                            String name );

    /**
     * Add a new transient child node for the parametered parent of the specified type inside the parents context. <br>
     * <b>Note</b> that if child node, transient or not, already exists its not duplicated.
     * 
     * @param <T> node type
     * @param parent the parent node
     * @param clazz the node type to be created
     * @param name the node name
     * @return the transient node
     */
    <T extends Node> T createTransientNode( Node parent,
                                            Class<T> clazz,
                                            String name );

    /**
     * Creates a transient unidirectional link between the source and target nodes with the specified link type.
     * <p>
     * If the link type is marked with {@link org.openspotlight.graph.annotation.LinkAutoBidirectional} annotation and the link
     * already exists between target and source (target -> source), its automatically converted to a bidirectional link.
     * <p>
     * <b>Note</b> that if link already exists, transient or not, its not duplicated.
     * 
     * @param <L> link type
     * @param linkClass the link type to be created
     * @param source the source node
     * @param target the target node
     * @return the created link
     */
    <L extends Link> L createTransientLink( Class<L> linkClass,
                                            Node source,
                                            Node target );

    /**
     * Creates a transient bidirectional link between nodes with the specified link type. <br>
     * <b>Note</b> that if link already exists, transient or not, its not duplicated.
     * 
     * @param <L> link type
     * @param linkClass the link type to be created
     * @param nodea the node
     * @param nodeb the node
     * @return the created link
     */
    <L extends Link> L createTransientBidirectionalLink( Class<L> linkClass,
                                                         Node nodea,
                                                         Node nodeb );

}
