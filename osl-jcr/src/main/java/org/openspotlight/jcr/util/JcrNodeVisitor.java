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
package org.openspotlight.jcr.util;
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



import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturn;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

import java.util.LinkedList;

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
@SuppressWarnings("synthetic-access")
public final class JcrNodeVisitor implements ItemVisitor {

	/**
	 * Just do not handle any error and re-throw it.
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	private static class DoNothingErrorHandler implements ErrorHandler {

		/**
		 * 
		 * {@inheritDoc}
		 */
		public <E extends Exception> void handleError(final Node nodeWithError,
				final E exception) throws E {
			throw logAndReturn(exception);
		}

	}

	/**
	 * Error handler for node visiting
	 * 
	 * 
	 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
	 * 
	 */
	public static interface ErrorHandler {

		/**
		 * Method to handle error during {@link NodeVisitor#visiting(Node)}
		 * method call.
		 * 
		 * @param <E>
		 * @param nodeWithError
		 * @param exception
		 * @throws E
		 */
		public <E extends Exception> void handleError(Node nodeWithError,
				E exception) throws E;
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
		 * @param limit
		 * @param handler
		 * @param visitor
		 * @param state
		 * @throws Exception
		 */
		private static void fillChildren(final Node n,
				final LinkedList<Node> currenctlyAddedNodes,
				final Integer limit, final ErrorHandler handler,
				final NodeVisitor visitor) throws Exception {
			try {
				if ((limit == null) || (n.getDepth() < limit.intValue())) {
					if (n.hasNodes()) {
						final NodeIterator nodeIterator = n.getNodes();
						while (nodeIterator.hasNext()) {
							try {
								final Node newNode = nodeIterator.nextNode();
								currenctlyAddedNodes.add(newNode);
								visitor.visiting(newNode);
							} catch (final Exception e) {
								handler.handleError(n, e);
							}
						}
					}
				}
			} catch (final Exception e) {
				handler.handleError(n, e);
			}
		}

		/**
		 * It will visit children nodes until the current level is equal the max
		 * levels.
		 * 
		 * @param n
		 * @param visitor
		 * @param maxLevels
		 * @param handler
		 * @throws Exception
		 */
		public static void fillQueue(final Node n, final NodeVisitor visitor,
				final Integer maxLevels, final ErrorHandler handler)
				throws Exception {
			visitor.visiting(n);
			final LinkedList<Node> currenctlyAddedNodes = new LinkedList<Node>();
			fillChildren(n, currenctlyAddedNodes, maxLevels, handler, visitor);
			while (currenctlyAddedNodes.size() != 0) {
				final LinkedList<Node> toIterate = new LinkedList<Node>(
						currenctlyAddedNodes);
				currenctlyAddedNodes.clear();
				for (final Node current : toIterate) {
					fillChildren(current, currenctlyAddedNodes, maxLevels,
							handler, visitor);
				}
			}

		}

	}

	private static final ErrorHandler DEFAULT_HANDLER = new DoNothingErrorHandler();

	/**
	 * Creates an {@link ItemVisitor} to visit the nodes with no level limit.
	 * 
	 * 
	 * @param visitor
	 * @return a jcr item visitor with the {@link NodeVisitor} inside
	 */
	public static ItemVisitor withVisitor(final NodeVisitor visitor) {
		checkNotNull("visitor", visitor); //$NON-NLS-1$
		return new JcrNodeVisitor(visitor, null, null);
	}

	/**
	 * Creates an {@link ItemVisitor} to visit the nodes with no level limit.
	 * 
	 * 
	 * @param visitor
	 * @param handler
	 * @return a jcr item visitor with the {@link NodeVisitor} inside
	 */
	public static ItemVisitor withVisitorAndErrorHandler(
			final NodeVisitor visitor, final ErrorHandler handler) {

		checkNotNull("visitor", visitor); //$NON-NLS-1$
		checkNotNull("handler", handler); //$NON-NLS-1$
		return new JcrNodeVisitor(visitor, null, handler);
	}

	/**
	 * 
	 * Creates an {@link ItemVisitor} to visit the nodes with a level limit.
	 * 
	 * @param visitor
	 * @param limit
	 * @return a jcr item visitor with the {@link NodeVisitor} inside
	 */
	public static ItemVisitor withVisitorAndLevelLimmit(
			final NodeVisitor visitor, final int limit) {
		checkNotNull("visitor", visitor); //$NON-NLS-1$
		checkCondition("validMaxLevel", limit > 0); //$NON-NLS-1$
		return new JcrNodeVisitor(visitor, Integer.valueOf(limit), null);
	}

	/**
	 * 
	 * Creates an {@link ItemVisitor} to visit the nodes with a level limit and
	 * a {@link ErrorHandler} to handle internal errors.
	 * 
	 * @param visitor
	 * @param limit
	 * @param handler
	 * @return a jcr item visitor with the {@link NodeVisitor} inside
	 */
	public static ItemVisitor withVisitorLevelLimmitAndErrorHandler(
			final NodeVisitor visitor, final int limit,
			final ErrorHandler handler) {
		checkNotNull("visitor", visitor); //$NON-NLS-1$
		checkNotNull("handler", handler); //$NON-NLS-1$
		checkCondition("validMaxLevel", limit > 0); //$NON-NLS-1$
		return new JcrNodeVisitor(visitor, Integer.valueOf(limit), handler);
	}

	private final ErrorHandler errorHandler;

	private final Integer maxLevels;

	private final NodeVisitor visitor;

	/**
	 * Creates a {@link JcrNodeVisitor} with max levels.
	 * 
	 * @param visitor
	 * @param maxLevels
	 * @param handler
	 */
	private JcrNodeVisitor(final NodeVisitor visitor, final Integer maxLevels,
			final ErrorHandler handler) {
		this.errorHandler = handler != null ? handler : DEFAULT_HANDLER;
		this.maxLevels = maxLevels;
		this.visitor = visitor;
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void visit(final Node node) throws RepositoryException {
		try {
			VisitorSupport.fillQueue(node, this.visitor, this.maxLevels,
					this.errorHandler);
		} catch (final Exception e) {
			throw logAndReturnNew(e, RepositoryException.class);
		}
	}

	/**
	 * 
	 * {@inheritDoc}
	 */
	public void visit(final Property property) {
		// nothing to do here
	}

}
