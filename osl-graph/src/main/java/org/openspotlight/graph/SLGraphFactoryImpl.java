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

import java.io.Serializable;
import java.lang.reflect.Constructor;

import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTree;
import org.openspotlight.graph.persistence.SLPersistentTreeFactory;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;

/**
 * The Class SLGraphFactoryImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLGraphFactoryImpl extends SLGraphFactory {

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphFactory#createGraph()
	 */
	public SLGraph createGraph() throws SLGraphFactoryException {
		try {
			SLPersistentTreeFactory factory = AbstractFactory.getDefaultInstance(SLPersistentTreeFactory.class);
			SLPersistentTree tree = factory.createPersistentTree();
			return new SLGraphImpl(tree);
		}
		catch (Exception e) {
			throw new SLGraphFactoryException("Couldn't create SL graph.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphFactory#createGraphSession(org.openspotlight.graph.persistence.SLPersistentTreeSession)
	 */
	SLGraphSession createGraphSession(SLPersistentTreeSession treeSession) {
		return new SLGraphSessionImpl(treeSession);
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphFactory#createNode(org.openspotlight.graph.SLContext, org.openspotlight.graph.persistence.SLPersistentNode, org.openspotlight.graph.SLGraphSessionEventPoster)
	 */
	SLNode createNode(SLContext context, SLPersistentNode persistentNode, SLGraphSessionEventPoster eventPoster) throws SLGraphFactoryException {
		return new SLNodeImpl(context, null, persistentNode, eventPoster);
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphFactory#createNode(org.openspotlight.graph.SLContext, org.openspotlight.graph.SLNode, org.openspotlight.graph.persistence.SLPersistentNode, org.openspotlight.graph.SLGraphSessionEventPoster)
	 */
	SLNode createNode(SLContext context, SLNode parent, SLPersistentNode persistentNode, SLGraphSessionEventPoster eventPoster) throws SLGraphFactoryException {
		return new SLNodeImpl(context, parent, persistentNode, eventPoster);
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphFactory#createNode(java.lang.Class, org.openspotlight.graph.SLContext, org.openspotlight.graph.SLNode, org.openspotlight.graph.persistence.SLPersistentNode)
	 */
	<T extends SLNode> T createNode(Class<T> clazz, SLContext context, SLNode parent, SLPersistentNode persistentNode) throws SLGraphFactoryException {
		try {
			Constructor<T> constructor = clazz.getConstructor(SLContext.class, SLNode.class, SLPersistentNode.class);
			return constructor.newInstance(context, parent, persistentNode);
		}
		catch (Exception e) {
			throw new SLGraphFactoryException("Couldn't instantiate node type " + clazz.getName(), e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphFactory#getContextImplClass()
	 */
	Class<? extends SLContext> getContextImplClass() throws SLGraphFactoryException {
		return SLContextImpl.class;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLGraphFactory#createProperty(org.openspotlight.graph.SLNode, org.openspotlight.graph.persistence.SLPersistentProperty)
	 */
	<V extends Serializable> SLNodeProperty<V> createProperty(SLNode node, SLPersistentProperty<V> persistentProperty) throws SLGraphFactoryException {
		return new SLNodePropertyImpl<V>(node, persistentProperty);
	}
}

