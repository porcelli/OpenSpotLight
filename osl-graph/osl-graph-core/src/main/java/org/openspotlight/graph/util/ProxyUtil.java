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
package org.openspotlight.graph.util;

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.util.Assertions;
import org.openspotlight.graph.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * The Class ProxyUtil.
 * 
 * @author Vitor Hugo Chagas
 */
public class ProxyUtil {

	/**
	 * Creates the link proxy.
	 * 
	 * @param linkType
	 *            the link type
	 * @param link
	 *            the link
	 * 
	 * @return the t
	 */
	public static <T extends SLLink> T createLinkProxy(final Class<T> linkType,
			final SLLink link) {
		synchronized (link.getLockObject()) {
			final InvocationHandler handler = new SLLinkInvocationHandler(link);
			return linkType.cast(Proxy.newProxyInstance(linkType
					.getClassLoader(), new Class<?>[] { linkType }, handler));
		}
	}

	/**
	 * Creates the node proxy.
	 * 
	 * @param nodeType
	 *            the node type
	 * @param node
	 *            the node
	 * 
	 * @return the t
	 */
	public static <T extends SLNode> T createNodeProxy(final Class<T> nodeType,
			final SLNode node) {
		Assertions.checkNotNull("nodeType", nodeType);
		Assertions.checkNotNull("node", node);
		final Lock lock = node.getLockObject();
		synchronized (lock) {
			T proxyNode = null;
			if (node instanceof Proxy) {
				proxyNode = nodeType.cast(node);
			} else {
				final InvocationHandler handler = new SLNodeInvocationHandler(
						node);
				proxyNode = nodeType.cast(Proxy
						.newProxyInstance(nodeType.getClassLoader(),
								new Class<?>[] { nodeType }, handler));
			}
			return proxyNode;
		}
	}

	/**
	 * Creates the node proxy.
	 * 
	 * @param node
	 *            the node
	 * 
	 * @return the sL node
	 */
	public static SLNode createNodeProxy(final SLNode node) {
		synchronized (node.getLockObject()) {
			final Class<? extends SLNode> nodeType = SLCommonSupport
					.getNodeType(node);
			return createNodeProxy(nodeType, node);
		}
	}

	/**
	 * Creates the proxy.
	 * 
	 * @param iClass
	 *            the i class
	 * @param handler
	 *            the handler
	 * 
	 * @return the t
	 */
	public static <T> T createProxy(final Class<T> iClass,
			final InvocationHandler handler) {
		return iClass.cast(Proxy.newProxyInstance(iClass.getClassLoader(),
				new Class<?>[] { iClass }, handler));
	}

	/**
	 * Creates the proxy.
	 * 
	 * @param iClass
	 *            the i class
	 * @param target
	 *            the target
	 * 
	 * @return the t
	 */
	public static <T> T createProxy(final Class<T> iClass, final Object target) {
		final InvocationHandler handler = new SimpleInvocationHandler(target);
		return iClass.cast(Proxy.newProxyInstance(iClass.getClassLoader(),
				new Class<?>[] { iClass }, handler));
	}

	/**
	 * Gets the link from proxy.
	 * 
	 * @param proxy
	 *            the proxy
	 * 
	 * @return the link from proxy
	 */
	public static SLLink getLinkFromProxy(final Object proxy) {
		final SLLinkInvocationHandler handler = (SLLinkInvocationHandler) Proxy
				.getInvocationHandler(proxy);
		return handler.getLink();
	}

	/**
	 * Gets the node from proxy.
	 * 
	 * @param proxy
	 *            the proxy
	 * 
	 * @return the node from proxy
	 */
	public static SLNode getNodeFromProxy(final Object proxy) {
		final SLNodeInvocationHandler handler = (SLNodeInvocationHandler) Proxy
				.getInvocationHandler(proxy);
		return handler.getNode();
	}
}
