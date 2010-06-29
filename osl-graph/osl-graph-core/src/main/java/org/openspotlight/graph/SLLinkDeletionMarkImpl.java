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

/**
 * The Class SLLinkDeletionMarkImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLLinkDeletionMarkImpl implements SLLinkDeletionMark {

    /** The link type. */
    private final Class<? extends SLLink> linkType;

    /** The node. */
    private final SLNode                  node;

    /**
     * Instantiates a new sL link deletion mark impl.
     * 
     * @param linkType the link type
     * @param node the node
     */
    SLLinkDeletionMarkImpl(
                            final Class<? extends SLLink> linkType, final SLNode node ) {
        this.linkType = linkType;
        this.node = node;
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */
    public boolean equals( final Object obj ) {
        if (!(obj instanceof SLLinkDeletionMark)) {
            return false;
        }
        final SLLinkDeletionMark order = (SLLinkDeletionMark)obj;
        return linkType.getName().concat(node.getID()).equals(order.getLinkType().getName().concat(order.getNode().getID()));
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.openspotlight.graph.SLLinkDeletionMark#getLinkType()
     */
    public Class<? extends SLLink> getLinkType() {
        return linkType;
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see org.openspotlight.graph.SLLinkDeletionMark#getChildNode()
     */
    public SLNode getNode() {
        return node;
    }

    // @Override
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return linkType.getName().concat(node.getID()).hashCode();
    }
}
