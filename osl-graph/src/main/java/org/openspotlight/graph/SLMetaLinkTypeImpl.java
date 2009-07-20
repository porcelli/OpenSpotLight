package org.openspotlight.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;

public class SLMetaLinkTypeImpl implements SLMetaLinkType {
	
	private SLMetadata metadata;
	private SLPersistentNode pNode;
	private Class<? extends SLLink> linkType;
	
	SLMetaLinkTypeImpl(SLMetadata metadata, SLPersistentNode pNode) {
		this.metadata = metadata;
		this.pNode = pNode;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<? extends SLLink> getType() throws SLGraphSessionException {
		if (linkType == null) {
			try {
				linkType = (Class<? extends SLLink>) Class.forName(pNode.getName());
			}
			catch (Exception e) {
				throw new SLGraphSessionException("Error on attempt to retrieve link type.", e);
			}
		}
		return linkType;
	}

	@Override
	public Collection<SLMetaLink> getMetaLinks(Class<? extends SLNode> sourceType,
		Class<? extends SLNode> targetType, Boolean bidirectional) throws SLGraphSessionException {
		Collection<SLMetaLink> metaLinks = getMetalinks();
		Iterator<SLMetaLink> iter = metaLinks.iterator();
		while (iter.hasNext()) {
			boolean remove = false;
			SLMetaLink metaLink = iter.next();
			if (bidirectional != null && !bidirectional) {
				if (sourceType != null && !sourceType.equals(metaLink.getSourceType())) {
					remove = true;
				}
				if (!remove && targetType != null && !targetType.equals(metaLink.getTargetType())) {
					remove = true;
				}
			}
			else {
				if (sourceType != null) {
					remove = sourceType.equals(metaLink.getSideTypes().get(0)) || sourceType.equals(metaLink.getSideTypes().get(1));
				}
				if (!remove && targetType != null) {
					remove = targetType.equals(metaLink.getSideTypes().get(0)) || targetType.equals(metaLink.getSideTypes().get(1));
				}
			}
			if (remove) iter.remove();
		}
		return metaLinks;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection<SLMetaLink> getMetalinks() throws SLGraphSessionException {
		
		try {
			Collection<SLMetaLink> metaLinks = new ArrayList<SLMetaLink>();
			Collection<SLPersistentNode> typePairNodes = pNode.getNodes();
			
			for (SLPersistentNode typePairNode : typePairNodes) {
				
				SLPersistentProperty<String> aClassNameProp = typePairNode.getProperty(String.class, SLConsts.PROPERTY_NAME_A_CLASS_NAME);
				SLPersistentProperty<String> bClassNameProp = typePairNode.getProperty(String.class, SLConsts.PROPERTY_NAME_B_CLASS_NAME);
				
				Class<? extends SLNode> aType = (Class<? extends SLNode>) Class.forName(aClassNameProp.getValue());
				Class<? extends SLNode> bType = (Class<? extends SLNode>) Class.forName(bClassNameProp.getValue());
				
				Collection<SLPersistentNode> linkNodes = typePairNode.getNodes();

				for (SLPersistentNode linkNode : linkNodes) {
					
					int direction = linkNode.getProperty(Integer.class, SLConsts.PROPERTY_NAME_DIRECTION).getValue();
					
					boolean bidirectional = false;
					Class<? extends SLNode> sourceType = null;
					Class<? extends SLNode> targetType = null;
					List<Class<? extends SLNode>> sideTypes = new ArrayList<Class<? extends SLNode>>();
					
					if (direction == SLConsts.DIRECTION_AB) {
						sourceType = aType;
						targetType = bType;
					}
					else if (direction == SLConsts.DIRECTION_BA) {
						sourceType = bType;
						targetType = aType;
					}
					else {
						bidirectional = true;
						sideTypes.add(aType);
						sideTypes.add(bType);
					}
					
					if (sourceType != null && targetType != null) {
						sideTypes = new ArrayList<Class<? extends SLNode>>();
						sideTypes.add(sourceType);
						sideTypes.add(targetType);
					}
					
					SLMetaLink metaLink = new SLMetaLinkImpl(linkNode, this, sourceType, targetType, sideTypes, bidirectional);
					metaLinks.add(metaLink);
				}
			}
			
			return metaLinks;
		}
		catch (Exception e) {
			throw new SLGraphSessionException("Error on attempt to retrieve meta links.", e);
		}
	}

	@Override
	public SLMetadata getMetadata() throws SLGraphSessionException {
		return metadata;
	}
}
