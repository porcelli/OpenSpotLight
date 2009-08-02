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
import java.util.ArrayList;
import java.util.List;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.annotation.SLDescription;
import org.openspotlight.graph.annotation.SLRenderHint;
import org.openspotlight.graph.annotation.SLRenderHints;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentQuery;
import org.openspotlight.graph.persistence.SLPersistentQueryResult;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The listener interface for receiving SLMetadata events.
 * The class that is interested in processing a SLMetadata
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addSLMetadataListener<code> method. When
 * the SLMetadata event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see SLMetadataEvent
 * @author Vitor Hugo Chagas
 */
public class SLMetadataListener extends SLAbstractGraphSessionEventListener {
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#nodeAdded(org.openspotlight.graph.SLNodeEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void nodeAdded(SLNodeEvent event) throws SLGraphSessionException {
		try {
			
			SLPersistentNode pNode = event.getPersistentNode();
			Class<? extends SLNode> nodeType = (Class<? extends SLNode>) event.getNode().getClass().getInterfaces()[0];
			if (nodeType.equals(SLNode.class)) return;
			String path = buildMetadataTypeNodePath(pNode);
			SLPersistentTreeSession treeSession = pNode.getSession();
			SLPersistentNode pMetaNode = treeSession.getNodeByPath(path);
			if (pMetaNode == null) {
				String parentPath = buildMetadataTypeNodePath(pNode.getParent());
				SLPersistentNode parentTypeNode;
				if (parentPath.equals("//osl/metadata/types")) {
					parentTypeNode = SLCommonSupport.getMetaTypesNode(treeSession); 
				}
				else {
					parentTypeNode = treeSession.getNodeByPath(parentPath);
				}
				pMetaNode = parentTypeNode.addNode(nodeType.getName());
				String idPropName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
				pNode.setProperty(String.class, idPropName, pMetaNode.getID());
			}
			
			addRenderHints(nodeType, pMetaNode);
			addDescription(nodeType, pMetaNode);
		} 
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to add node metadata.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#linkAdded(org.openspotlight.graph.SLLinkEvent)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void linkAdded(SLLinkEvent event) throws SLGraphSessionException {
		
		try {

			SLLink link = event.getLink();
			SLNode[] sides = link.getSides();
			SLNode source = sides[0];
			SLNode target = sides[1];
			Class<? extends SLLink> linkType = (Class<? extends SLLink>) link.getClass().getInterfaces()[0];
			SLPersistentNode linkNode = event.getLinkNode();
			SLPersistentTreeSession treeSession = linkNode.getSession();
			
			Class<?> sourceClass = source.getClass().getInterfaces()[0];
			Class<?> targetClass = target.getClass().getInterfaces()[0];

			SLPersistentNode classPairKeyNode = getClassPairKeyNode(treeSession, linkType, sourceClass, targetClass);
			int direction = getMetaLinkDirection(sourceClass, targetClass, link.isBidirectional());
			
			StringBuilder statement = new StringBuilder();
			statement.append(classPairKeyNode.getPath())
				.append("/*[").append(SLConsts.PROPERTY_NAME_DIRECTION).append("=").append(direction).append(']');

			SLPersistentNode metaLinkNode = null;
			SLPersistentQuery query = treeSession.createQuery(statement.toString(), SLPersistentQuery.TYPE_XPATH);
			SLPersistentQueryResult result = query.execute();
			if (result.getRowCount() == 0) {
				metaLinkNode = addLinkNode(classPairKeyNode, direction);
			}

			String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
			linkNode.setProperty(String.class, propName, metaLinkNode.getID());
			
			addDescription(linkType, metaLinkNode);
		} 
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to add meta link node.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#nodePropertySet(org.openspotlight.graph.SLNodePropertyEvent)
	 */
	@Override
	public void nodePropertySet(SLNodePropertyEvent event) throws SLGraphSessionException {
		try {
			SLPersistentProperty<? extends Serializable> pProperty = event.getPersistentProperty();
			
			SLPersistentNode pNode = pProperty.getNode();
			SLPersistentProperty<String> metaNodeIDProp = null;
			try {
				String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
				metaNodeIDProp = pNode.getProperty(String.class, propName);
			}
			catch (SLPersistentPropertyNotFoundException e) {}
			if (metaNodeIDProp != null) {
				SLPersistentTreeSession session = pNode.getSession();
				SLPersistentNode metaNode = session.getNodeByID(metaNodeIDProp.getValue());
				metaNode.setProperty(String.class, pProperty.getName(), pProperty.getValue().getClass().getName());
			}
		} 
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to set meta node property.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLAbstractGraphSessionEventListener#linkPropertySet(org.openspotlight.graph.SLLinkPropertyEvent)
	 */
	@Override
	public void linkPropertySet(SLLinkPropertyEvent event) throws SLGraphSessionException {
		
		try {
			
			SLPersistentProperty<? extends Serializable> pProperty = event.getPersistentProperty();
			
			SLPersistentNode linkNode = pProperty.getNode();
			String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_META_NODE_ID);
			SLPersistentProperty<String> metaNodeIDProp = null;
			try {
				metaNodeIDProp = linkNode.getProperty(String.class, propName);	
			}
			catch (SLPersistentPropertyNotFoundException e) {}
			SLPersistentTreeSession treeSession = linkNode.getSession();
			SLPersistentNode metaLinkNode = treeSession.getNodeByID(metaNodeIDProp.getValue());
			metaLinkNode.setProperty(String.class, pProperty.getName(), pProperty.getValue().getClass().getName());
			
		} 
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to set meta link property.", e);
		}
	}
	
	/**
	 * Builds the metadata type node path.
	 * 
	 * @param pNode the node
	 * 
	 * @return the string
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private String buildMetadataTypeNodePath(SLPersistentNode pNode) throws SLPersistentTreeSessionException {
		String typePropName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_TYPE);
		StringBuilder statement = new StringBuilder();
		List<String> typeNames = new ArrayList<String>();
		do {
			try {
				SLPersistentProperty<String> typeProp = pNode.getProperty(String.class, typePropName);
				statement.insert(0, typeProp.getValue()).insert(0, '/');
				typeNames.add(0, typeProp.getValue());
			}
			catch (SLPersistentPropertyNotFoundException e) {
				break;
			}
		}
		while ((pNode = pNode.getParent()) != null);
		statement.insert(0, "//osl/metadata/types");
		return statement.toString();
	}
	
	/**
	 * Gets the class pair key node.
	 * 
	 * @param treeSession the tree session
	 * @param linkClass the link class
	 * @param sourceClass the source class
	 * @param targetClass the target class
	 * 
	 * @return the class pair key node
	 * 
	 * @throws SLException the SL exception
	 */
	private SLPersistentNode getClassPairKeyNode(SLPersistentTreeSession treeSession, Class<? extends SLLink> linkClass, Class<?> sourceClass, Class<?> targetClass) throws SLException {

		Class<?> aClass = getAClass(sourceClass, targetClass); 
		Class<?> bClass = getBClass(sourceClass, targetClass);
		
		StringBuilder pairKey = new StringBuilder();
		pairKey.append(aClass.getName()).append('.').append(bClass.getName());
		SLPersistentNode linkClassNode = SLCommonSupport.getMetaLinkClassNode(treeSession, linkClass);
		SLPersistentNode pairKeyNode = linkClassNode.getNode(pairKey.toString());
		
		if (pairKeyNode == null) {
			pairKeyNode = linkClassNode.addNode(pairKey.toString());
			pairKeyNode.setProperty(String.class, SLConsts.PROPERTY_NAME_A_CLASS_NAME, aClass.getName());
			pairKeyNode.setProperty(String.class, SLConsts.PROPERTY_NAME_B_CLASS_NAME, bClass.getName());
			pairKeyNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT, 0L);
		}
		
		return pairKeyNode;
	}

	/**
	 * Gets the a class.
	 * 
	 * @param sourceClass the source class
	 * @param targetClass the target class
	 * 
	 * @return the a class
	 * 
	 * @throws SLException the SL exception
	 */
	private Class<?> getAClass(Class<?> sourceClass, Class<?> targetClass) throws SLException {
		return sourceClass.getName().compareTo(targetClass.getName()) < 0 ? sourceClass : targetClass; 
	}

	/**
	 * Gets the b class.
	 * 
	 * @param sourceClass the source class
	 * @param targetClass the target class
	 * 
	 * @return the b class
	 * 
	 * @throws SLException the SL exception
	 */
	private Class<?> getBClass(Class<?> sourceClass, Class<?> targetClass) throws SLException {
		return sourceClass.getName().compareTo(targetClass.getName()) < 0 ? targetClass : sourceClass; 
	}
	
	/**
	 * Gets the meta link direction.
	 * 
	 * @param sourceClass the source class
	 * @param targetClass the target class
	 * @param bidirecional the bidirecional
	 * 
	 * @return the meta link direction
	 * 
	 * @throws SLException the SL exception
	 */
	private int getMetaLinkDirection(Class<?> sourceClass, Class<?> targetClass, boolean bidirecional) throws SLException {
		if (bidirecional) return SLConsts.DIRECTION_BOTH;
		else return getAClass(sourceClass, targetClass).equals(sourceClass) ? SLConsts.DIRECTION_AB : SLConsts.DIRECTION_BA;
	}

	/**
	 * Adds the link node.
	 * 
	 * @param pairKeyNode the pair key node
	 * @param direction the direction
	 * 
	 * @return the sL persistent node
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private SLPersistentNode addLinkNode(SLPersistentNode pairKeyNode, int direction) throws SLPersistentTreeSessionException {
		long linkCount = incLinkCount(pairKeyNode);
		String name = SLCommonSupport.getLinkIndexNodeName(linkCount);
		SLPersistentNode linkNode = pairKeyNode.addNode(name);
		linkNode.setProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT, linkCount);
		linkNode.setProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION, direction);
		return linkNode;
	}
	
	/**
	 * Inc link count.
	 * 
	 * @param linkKeyPairNode the link key pair node
	 * 
	 * @return the long
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private long incLinkCount(SLPersistentNode linkKeyPairNode) throws SLPersistentTreeSessionException {
		SLPersistentProperty<Long> linkCountProp = linkKeyPairNode.getProperty(Long.class, SLConsts.PROPERTY_NAME_LINK_COUNT);
		long linkCount = linkCountProp.getValue() + 1;
		linkCountProp.setValue(linkCount);
		return linkCount;
	}
	
	/**
	 * Adds the render hints.
	 * 
	 * @param nodeType the node type
	 * @param pMetaNode the meta node
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private void addRenderHints(Class<? extends SLNode> nodeType, SLPersistentNode pMetaNode) throws SLPersistentTreeSessionException {
		SLRenderHints renderHints = nodeType.getAnnotation(SLRenderHints.class);
		if (renderHints != null) {
			SLRenderHint[] renderHintArr = renderHints.value();
			for (SLRenderHint renderHint : renderHintArr) {
				String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_RENDER_HINT + "." + renderHint.name());
				SLPersistentProperty<String> prop = SLCommonSupport.getProperty(pMetaNode, String.class, propName);
				if (prop == null) {
					pMetaNode.setProperty(String.class, propName, renderHint.value());
				}
			}
		}
	}
	
	/**
	 * Adds the description.
	 * 
	 * @param type the type
	 * @param pNode the node
	 * 
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	private void addDescription(Class<?> type, SLPersistentNode pNode) throws SLPersistentTreeSessionException {
		SLDescription description = type.getAnnotation(SLDescription.class);
		if (description != null) {
			String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_DESCRIPTION);
			SLPersistentProperty<String> prop = SLCommonSupport.getProperty(pNode, String.class, propName);
			if (prop == null) {
				pNode.setProperty(String.class, propName, description.value());
			}
		}
	}
}

