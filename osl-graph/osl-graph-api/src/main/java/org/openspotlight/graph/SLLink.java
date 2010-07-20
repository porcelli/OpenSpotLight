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
package org.openspotlight.graph;

/**
 * The SLLink is the way you correlate informations (nodes) in OpenSpotLight Graph. Any relationship between nodes are
 * materialized by a creation of a link. <br>
 * A SLLink is uniquely identified by three data: Type, Source Node and Target Node, and based on these data an algorithm is used
 * to generate an unique id.
 * <p>
 * We have two types of links: <br>
 * &nbsp;- <i>Unidirectional</i> links creates a link between a source and a target node that can be represented as: source ->
 * target<br>
 * &nbsp;- <i>Bidirectional</i> links creates a link between two nodes that can be represented as: node1 <-> node2
 * </p>
 * <p>
 * Links can be promoted from unidirectional to bidirectional, but to allow this promotion the link type should use the
 * {@link org.openspotlight.graph.annotation.SLLinkAutoBidirectional} annotation. <br>
 * The mechanism that creates this promotion is really simple and can be described as: if you create a link with a link type that
 * allows auto-bidirectional bettwen a source and a target nodes (node1 -> node2) and later creates the same link with the same
 * type bettwen target and source (node2 -> node1) it will convert the original link to a bidirectional link (node1 <-> node2).
 * </p>
 * <p>
 * To secure the data consistency its not possible change the unique identifiers of a SLLink. If you need so, you'll have to
 * delete it and create a new one.
 * </p>
 * <p>
 * Links can be created as transients by {@link org.openspotlight.graph.manipulation.SLGraphTransientWriter#createTransientLink}
 * or {@link org.openspotlight.graph.manipulation.SLGraphTransientWriter#createTransientBidirectionalLink} methods or as permanent
 * by {@link org.openspotlight.graph.manipulation.SLGraphWriter#createLink} or
 * {@link org.openspotlight.graph.manipulation.SLGraphWriter#createBidirectionalLink} methods wich are the most common use.
 * </p>
 * <p>
 * Along with {@link org.openspotlight.graph.SLNode}, links are are the core of OpenSpotLight Graph data model.
 * </p>
 * 
 * @author porcelli
 * @author feuteston
 */
public abstract class SLLink implements SLElement, Comparable<SLLink> {

    /**
     * Gets the other side.
     * 
     * @param side the side
     * @return the other side
     */
    public abstract SLNode getOtherSide( SLNode side );

    /**
     * Gets the sides.
     * 
     * @return the sides
     */
    public abstract SLNode[] getSides();

    /**
     * Gets the source.
     * 
     * @return the source
     */
    public abstract SLNode getSource();

    /**
     * Gets the target.
     * 
     * @return the target
     */
    public abstract SLNode getTarget();

    /**
     * Checks if is bidirectional.
     * 
     * @return true, if is bidirectional
     */
    public abstract boolean isBidirectional();

    public abstract int getCount();

    public abstract int setCount();
}
