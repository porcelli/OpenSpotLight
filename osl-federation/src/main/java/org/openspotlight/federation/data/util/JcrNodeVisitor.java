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

package org.openspotlight.federation.data.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;

import java.util.LinkedList;
import java.util.List;

import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

/**
 * A class for helping visiting {@link Node Jcr Nodes}. It is
 * "stackoverflow safe", since it is not recursive.
 * 
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public final class JcrNodeVisitor implements ItemVisitor {

    /**
     * Creates an {@link ItemVisitor} to visit the nodes with no level limit.
     * 
     * 
     * @param visitor
     * @return a jcr item visitor with the {@link NodeVisitor} inside
     */
    public static ItemVisitor withVisitor(NodeVisitor visitor){
        return new JcrNodeVisitor(visitor);
    }

    /**
     * 
     * Creates an {@link ItemVisitor} to visit the nodes with a level limit.
     * 
     * @param visitor
     * @param limit
     * @return a jcr item visitor with the {@link NodeVisitor} inside 
     */
    public static ItemVisitor withVisitorAndLevelLimmit(NodeVisitor visitor, int limit){
        return new JcrNodeVisitor(visitor,limit);
    }
    
    /**
     * Internal class to store the current level and to do also minor checkings
     * about the size.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    private static class IntegerState {
        private int value = 0;
        
        private final int maxValue;
        
        private boolean isInfinite;
        
        /**
         * Creates an instance with the maxValue or infinite if the parameter is
         * null.
         * 
         * @param i
         */
        public IntegerState(final Integer i) {
            if (i == null) {
                this.isInfinite = true;
                this.maxValue = 0;
            } else {
                this.isInfinite = false;
                this.maxValue = i.intValue();
            }
        }
        
        /**
         * 
         * @return true if is infinite, or if the max value is bigger than
         *         current value
         */
        public boolean canIncrement() {
            return this.isInfinite || (this.value < this.maxValue);
        }
        
        /**
         * Increments the current value.
         */
        public void increment() {
            if (!this.isInfinite) {
                this.value++;
            }
        }
    }
    
    /**
     * Interface to be notified on each node visiting.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    public static interface NodeVisitor {
        /**
         * Callback method. It's not necessary to go inside the children here.
         * The {@link JcrNodeVisitor} will do all the job.
         * 
         * @param n
         * @throws RepositoryException
         */
        public void visiting(Node n) throws RepositoryException;
    }
    
    /**
     * Static helper class to fill the visiting queue and avoid stack overflow
     * during visiting.
     * 
     * @author Luiz Fernando Teston - feu.teston@caravelatech.com
     * 
     */
    private static class VisitorSupport {
        
        /**
         * Just add new children if the current level is not equal max levels.
         * 
         * @param n
         * @param currenctlyAddedNodes
         * @param state
         * @throws RepositoryException
         */
        private static void fillChildren(final Node n,
                final LinkedList<Node> currenctlyAddedNodes,
                final IntegerState state) throws RepositoryException {
            if (state.canIncrement()) {
                if (n.hasNodes()) {
                    state.increment();
                    final NodeIterator nodeIterator = n.getNodes();
                    while (nodeIterator.hasNext()) {
                        currenctlyAddedNodes.add(nodeIterator.nextNode());
                    }
                }
            }
        }
        
        /**
         * It will add children nodes until the current level is equal the max
         * levels.
         * 
         * @param n
         * @param queueToAdd
         * @param maxLevels
         * @throws RepositoryException
         */
        public static void fillQueue(final Node n, final List<Node> queueToAdd,
                final Integer maxLevels) throws RepositoryException {
            final IntegerState state = new IntegerState(maxLevels);
            queueToAdd.add(n);
            state.increment();
            final LinkedList<Node> currenctlyAddedNodes = new LinkedList<Node>();
            fillChildren(n, currenctlyAddedNodes, state);
            while (currenctlyAddedNodes.size() != 0) {
                queueToAdd.addAll(currenctlyAddedNodes);
                final LinkedList<Node> toIterate = new LinkedList<Node>(
                        currenctlyAddedNodes);
                currenctlyAddedNodes.clear();
                for (final Node current : toIterate) {
                    fillChildren(current, currenctlyAddedNodes, state);
                }
            }
        }
        
    }
    
    private final NodeVisitor visitor;
    
    private final Integer maxLevels;
    
    /**
     * Creates a {@link JcrNodeVisitor} with infinite max levels.
     * 
     * @param visitor
     */
    private JcrNodeVisitor(final NodeVisitor visitor) {
        checkNotNull("visitor", visitor); //$NON-NLS-1$
        this.maxLevels = null;
        this.visitor = visitor;
    }
    
    /**
     * Creates a {@link JcrNodeVisitor} with max levels.
     * 
     * @param visitor
     * @param maxLevels
     */
    private JcrNodeVisitor(final NodeVisitor visitor, final int maxLevels) {
        checkNotNull("visitor", visitor); //$NON-NLS-1$
        checkCondition("validMaxLevel", maxLevels > 0); //$NON-NLS-1$
        this.maxLevels = Integer.valueOf(maxLevels);
        this.visitor = visitor;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void visit(final Node node) throws RepositoryException {
        final LinkedList<Node> queue = new LinkedList<Node>();
        VisitorSupport.fillQueue(node, queue, this.maxLevels);
        while (queue.size() > 0) {
            final Node n = queue.removeFirst();
            this.visitor.visiting(n);
        }
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void visit(final Property property) throws RepositoryException {
        // nothing to do here
    }
    
}
