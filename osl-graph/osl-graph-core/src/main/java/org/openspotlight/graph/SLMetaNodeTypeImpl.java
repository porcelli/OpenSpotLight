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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentPropertyNotFoundException;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

/**
 * The Class SLMetaNodeTypeImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLMetaNodeTypeImpl implements SLMetaNodeType {

	/** The metadata. */
	private final SLMetadata metadata;

	/** The p meta node. */
	private final SLPersistentNode pMetaNode;

	/**
	 * Instantiates a new sL meta node type impl.
	 * 
	 * @param metadata the metadata
	 * @param pNode the node
	 */
	SLMetaNodeTypeImpl(final SLMetadata metadata, final SLPersistentNode pNode) {
		this.metadata = metadata;
		this.pMetaNode = pNode;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SLMetaNodeTypeImpl)) {
			return false;
		}
		final SLMetaNodeTypeImpl metaNode = (SLMetaNodeTypeImpl) obj;
		return this.pMetaNode.equals(metaNode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaNode#getDescription()
	 */
	public String getDescription() throws SLGraphSessionException {
		try {
			final String propName = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_DESCRIPTION);
			final SLPersistentProperty<String> prop = SLCommonSupport.getProperty(this.pMetaNode, String.class, propName);
			return prop == null ? null : prop.getValue();
		}
		catch (final SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node description.", e);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaElement#getMetadata()
	 */
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return this.metadata;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaNode#getMetaNode(java.lang.Class)
	 */
	public SLMetaNodeType getSubMetaNodeType(final Class<? extends SLNode> nodeClass) throws SLGraphSessionException {
		return  getSubMetaNodeType(nodeClass.getName());
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNodeType#getSubMetaNodeType(java.lang.String)
	 */
	public SLMetaNodeType getSubMetaNodeType(String name) throws SLGraphSessionException {
		try {
			SLMetaNodeType metaNode = null;
			final SLPersistentNode pChildMetaNode = this.pMetaNode.getNode(name);
			if (pChildMetaNode != null) {
				metaNode = new SLMetaNodeTypeImpl(this.metadata, pChildMetaNode);
			}
			return metaNode;
		}
		catch (final SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node.", e);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaNode#getMetaNodes()
	 */
	public Collection<SLMetaNodeType> getSubMetaNodeTypes() throws SLGraphSessionException {
		try {
			final Collection<SLMetaNodeType> subMetaNodeTypes = new ArrayList<SLMetaNodeType>();
			final Collection<SLPersistentNode> pMetaNodes = this.pMetaNode.getNodes();
			for (final SLPersistentNode pMetaNode : pMetaNodes) {
				final SLMetaNodeType metaNode = new SLMetaNodeTypeImpl(this.metadata, pMetaNode);
				subMetaNodeTypes.add(metaNode);
			}
			return subMetaNodeTypes;
		}
		catch (final SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta nodes.", e);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaNode#getMetaProperties()
	 */
	public Collection<SLMetaNodeProperty> getMetaProperties() throws SLGraphSessionException {
		try {
			final Collection<SLMetaNodeProperty> metaProperties = new HashSet<SLMetaNodeProperty>();
			final Collection<SLPersistentProperty<Serializable>> pProperties = this.pMetaNode.getProperties(SLConsts.PROPERTY_PREFIX_USER.concat(".*"));
			for (final SLPersistentProperty<Serializable> pProperty : pProperties) {
				final SLMetaNodeProperty metaProperty = new SLMetaNodePropertyImpl(this.metadata, this, pProperty);
				metaProperties.add(metaProperty);
			}
			return metaProperties;
		}
		catch (final SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node properties.", e);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaNode#getMetaProperty(java.lang.String)
	 */
	public SLMetaNodeProperty getMetaProperty(final String name) throws SLGraphSessionException {
		try {
			final String propName = SLCommonSupport.toUserPropertyName(name);
			SLPersistentProperty<Serializable> pProperty = null;
			try {
				pProperty = this.pMetaNode.getProperty(Serializable.class, propName);
			}
			catch (final SLPersistentPropertyNotFoundException e) {
			}
			SLMetaNodeProperty metaProperty = null;
			if (pProperty != null) {
				metaProperty = new SLMetaNodePropertyImpl(this.metadata, this, pProperty);
			}
			return metaProperty;
		}
		catch (final SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node property.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.SLMetaNode#getMetaRenderHint(java.lang.String)
	 */
	public SLMetaRenderHint getMetaRenderHint(final String name) throws SLGraphSessionException {
		try {
			SLMetaRenderHint renderHint = null;
			final String pattern = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_RENDER_HINT + "." + name);
			final SLPersistentProperty<Serializable> pProperty = SLCommonSupport.getProperty(this.pMetaNode, Serializable.class, pattern);
			if (pProperty != null) {
				renderHint = new SLMetaRenderHintImpl(this, pProperty);
			}
			return renderHint;
		}
		catch (final SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta render hint.", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaNode#getMetaRenderHints()
	 */
	public Collection<SLMetaRenderHint> getMetaRenderHints() throws SLGraphSessionException {
		try {
			final Collection<SLMetaRenderHint> renderHints = new ArrayList<SLMetaRenderHint>();
			final String pattern = SLCommonSupport.toInternalPropertyName(SLConsts.PROPERTY_NAME_RENDER_HINT) + ".*";
			final Set<SLPersistentProperty<Serializable>> pProperties = this.pMetaNode.getProperties(pattern);
			for (final SLPersistentProperty<Serializable> pProperty : pProperties) {
				final SLMetaRenderHint renderHint = new SLMetaRenderHintImpl(this, pProperty);
				renderHints.add(renderHint);
			}
			return renderHints;
		}
		catch (final SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta render hints.", e);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.SLMetaNode#getType()
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends SLNode> getType() throws SLGraphSessionException {
		try {
			return (Class<? extends SLNode>) Class.forName(this.pMetaNode.getName());
		}
		catch (final Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node type.", e);
		}
	}
	
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNodeType#getTypeName()
	 */
	public String getTypeName() throws SLGraphSessionException {
		try {
			return this.pMetaNode.getName();
		}
		catch (final Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve node type name.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.SLMetaNodeType#getParent()
	 */
	public SLMetaNodeType getParent() throws SLGraphSessionException {
		try {
			SLMetaNodeType parentMetaNodeType = null;
			SLPersistentTreeSession treeSession = pMetaNode.getSession();
			SLPersistentNode pMetaTypesNode = SLCommonSupport.getMetaTypesNode(treeSession);
			SLPersistentNode pParentNode = pMetaNode.getParent();
			if (!pParentNode.equals(pMetaTypesNode)) {
				parentMetaNodeType = new SLMetaNodeTypeImpl(metadata, pParentNode);
			}
			return parentMetaNodeType;
		}
		catch (SLException e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta node type parent.", e);
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.pMetaNode.hashCode();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		try {
			return getType().toString();
		}
		catch (SLGraphSessionException e) {
			throw new SLRuntimeException("Error on attempt to string meta node type.", e);
		}
	}

}
