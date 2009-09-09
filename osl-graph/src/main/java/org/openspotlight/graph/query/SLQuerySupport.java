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
package org.openspotlight.graph.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLMetaNodeType;
import org.openspotlight.graph.SLMetadata;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentNodeNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;
import org.openspotlight.graph.query.info.SLSelectInfo;

/**
 * The Class SLSelectCommandSupport.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLQuerySupport {
	
	/**
	 * Gets the node wrappers.
	 * 
	 * @param treeSession the tree session
	 * @param ids the ids
	 * 
	 * @return the node wrappers
	 * 
	 * @throws SLPersistentNodeNotFoundException the SL persistent node not found exception
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	static Set<PNodeWrapper> getNodeWrappers(SLPersistentTreeSession treeSession, String[] ids) throws SLPersistentNodeNotFoundException, SLPersistentTreeSessionException {
		Set<PNodeWrapper> wrappers = null;
		if (ids != null && ids.length > 0) {
			wrappers = new HashSet<PNodeWrapper>();
			for (int i = 0; i < ids.length; i++) {
				SLPersistentNode pNode = treeSession.getNodeByID(ids[i]);
				wrappers.add(new PNodeWrapper(pNode));
			}
		}
		return wrappers;
	}

	
	/**
	 * Gets the node i ds.
	 * 
	 * @param nodes the nodes
	 * 
	 * @return the node i ds
	 * 
	 * @throws SLQueryException the SL query exception
	 */
	static String[] getNodeIDs(Collection<SLNode> nodes) throws SLQueryException {
		try {
			int count = 0;
			String[] ids = new String[nodes.size()];
			for (SLNode node : nodes) {
				ids[count++] = node.getID();
			}
			return ids;
		}
		catch (SLGraphSessionException e) {
			throw new SLQueryException("Error on attempt to get node ids.", e);
		}
	}
	
	/**
	 * Gets the select info.
	 * 
	 * @param select the select
	 * 
	 * @return the select info
	 */
	static SLSelectInfo getSelectInfo(SLSelect select) {
		SLSelectInfoGetter getter = (SLSelectInfoGetter) select;
		return getter.getSelectInfo();
	}
	
	/**
	 * Gets the hierarchy type names.
	 * 
	 * @param metadata the metadata
	 * @param typeName the type name
	 * @param subTypes the sub types
	 * 
	 * @return the hierarchy type names
	 * 
	 * @throws SLGraphSessionException the SL graph session exception
	 */
	static List<String> getHierarchyTypeNames(SLMetadata metadata, String typeName, boolean subTypes) throws SLGraphSessionException {
		List<String> types = new ArrayList<String>();
		types.add(typeName);
		if (subTypes) {
			SLMetaNodeType type = metadata.findMetaNodeType(typeName);
			for (SLMetaNodeType currentType : type.getSubMetaNodeTypes()) {
				types.add(currentType.getTypeName());
			}
		}
		return types;
	}
	
	/**
	 * Wrap nodes.
	 * 
	 * @param pNodes the nodes
	 * 
	 * @return the set< p node wrapper>
	 */
	static Set<PNodeWrapper> wrapNodes(Collection<SLPersistentNode> pNodes) {
		Set<PNodeWrapper> pNodeWrappers = new HashSet<PNodeWrapper>();
		for (SLPersistentNode pNode : pNodes) {
			PNodeWrapper pNodeWrapper = new PNodeWrapper(pNode);
			pNodeWrappers.add(pNodeWrapper);
		}
		return pNodeWrappers;
	}
	
	/**
	 * Wrap link nodes.
	 * 
	 * @param pLinkNodes the link nodes
	 * 
	 * @return the collection< p link node wrapper>
	 */
	static Collection<PLinkNodeWrapper> wrapLinkNodes(Collection<SLPersistentNode> pLinkNodes) {
		Collection<PLinkNodeWrapper> pLinkNodeWrappers = new ArrayList<PLinkNodeWrapper>();
		for (SLPersistentNode pLinkNode : pLinkNodes) {
			PLinkNodeWrapper pNodeWrapper = new PLinkNodeWrapper(pLinkNode);
			pLinkNodeWrappers.add(pNodeWrapper);
		}
		return pLinkNodeWrappers;
	}
	
	/**
	 * Map nodes by type.
	 * 
	 * @param selectNodeWrappers the select node wrappers
	 * 
	 * @return the map< string, list< p node wrapper>>
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	static Map<String, List<PNodeWrapper>> mapNodesByType(Collection<PNodeWrapper> selectNodeWrappers) throws SLPersistentTreeSessionException {
		Map<String, List<PNodeWrapper>> nodeWrapperListMap = new HashMap<String, List<PNodeWrapper>>();
		for (PNodeWrapper pNodeWrapper : selectNodeWrappers) {
			String typeName = pNodeWrapper.getTypeName();
			List<PNodeWrapper> typeNodes = nodeWrapperListMap.get(typeName);
			if (typeNodes == null) {
				typeNodes = new ArrayList<PNodeWrapper>();
				nodeWrapperListMap.put(typeName, typeNodes);
			}
			typeNodes.add(pNodeWrapper);
		}
		return nodeWrapperListMap;
	}

	/**
	 * Find p node wrapper.
	 * 
	 * @param nodeWrappers the node wrappers
	 * @param id the id
	 * 
	 * @return the p node wrapper
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	static PNodeWrapper findPNodeWrapper(List<PNodeWrapper> nodeWrappers, String id) throws SLPersistentTreeSessionException {
		PNodeWrapper nodeWrapper = null;
		for (PNodeWrapper current : nodeWrappers) {
			if (current.getId().equals(id)) {
				nodeWrapper = current;
				break;
			}
		}
		return nodeWrapper;
	}

}

