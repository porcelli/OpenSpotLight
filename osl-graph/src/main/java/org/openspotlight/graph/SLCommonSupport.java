package org.openspotlight.graph;

import java.util.Arrays;

import org.openspotlight.graph.annotation.SLLinkAttribute;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

public class SLCommonSupport {
	
	public static String getNameInIDForm(SLNode node) throws SLGraphSessionException {
		return "node.".concat(node.getID().replace("-", "."));
	}
	
	public static String getNodeID(String linkNodeName) {
		return linkNodeName.substring("node.".length()).replace(".", "-");
	}

	public static String toInternalPropertyName(String name) {
		return new StringBuilder().append(SLConsts.PROPERTY_PREFIX_INTERNAL).append('.').append(name).toString();
	}

	public static String toUserPropertyName(String name) {
		return new StringBuilder().append(SLConsts.PROPERTY_PREFIX_USER).append('.').append(name).toString();
	}
	
	public static String toSimpleUserPropertyName(String name) {
		return name.substring(name.indexOf('.') + 1);
	}
	
	public static String getLinkIndexNodeName(long index) {
		return "index." + index;
	}
	
	public static boolean allowsChangeToBidirectional(Class<? extends SLLink> linkClass) {
		SLLinkAttribute attribute = linkClass.getAnnotation(SLLinkAttribute.class);
		return attribute != null && Arrays.binarySearch(attribute.value(), SLLinkAttribute.ALLOWS_CHANGE_TO_BIDIRECTIONAL) > -1;
	}
	
	//osl/contexts
	public static SLPersistentNode getContextsPersistentNode(SLPersistentTreeSession treeSession) throws SLPersistentTreeSessionException {
		SLPersistentNode oslRootNode = treeSession.getRootNode();
		SLPersistentNode contextsPersistentNode = oslRootNode.getNode(SLConsts.NODE_NAME_CONTEXTS);
		if (contextsPersistentNode == null) {
			contextsPersistentNode = oslRootNode.addNode(SLConsts.NODE_NAME_CONTEXTS);
		}
		return contextsPersistentNode;
	}

	//osl/links
	public static SLPersistentNode getLinksPersistentNode(SLPersistentTreeSession treeSession) throws SLPersistentTreeSessionException {
		SLPersistentNode oslPersistentNode = treeSession.getRootNode();
		SLPersistentNode linksPersistentNode = oslPersistentNode.getNode(SLConsts.NODE_NAME_LINKS);
		if (linksPersistentNode == null) {
			linksPersistentNode = oslPersistentNode.addNode(SLConsts.NODE_NAME_LINKS);
		}
		return linksPersistentNode;
	}

	//osl/links/linkClassFullQualifiedName
	public static SLPersistentNode getLinkClassNode(SLPersistentTreeSession treeSession, Class<? extends SLLink> linkClass) throws SLPersistentTreeSessionException {
		SLPersistentNode linksNode = SLCommonSupport.getLinksPersistentNode(treeSession);
		SLPersistentNode linkClassNode = linksNode.getNode(linkClass.getName());
		if (linkClassNode == null) {
			linkClassNode = linksNode.addNode(linkClass.getName());
		}
		return linkClassNode;
	}
	
	//osl/metadata
	public static SLPersistentNode getMetadataNode(SLPersistentTreeSession treeSession) throws SLPersistentTreeSessionException {
		SLPersistentNode oslRootNode = treeSession.getRootNode();
		SLPersistentNode metadataNode = oslRootNode.getNode(SLConsts.NODE_NAME_METADATA);
		if (metadataNode == null) {
			metadataNode = oslRootNode.addNode(SLConsts.NODE_NAME_METADATA);
		}
		return metadataNode;
	}

	//osl/metadata/types
	public static SLPersistentNode getMetaTypesNode(SLPersistentTreeSession treeSession) throws SLPersistentTreeSessionException {
		SLPersistentNode oslRootNode = getMetadataNode(treeSession);
		SLPersistentNode typesNode = oslRootNode.getNode(SLConsts.NODE_NAME_TYPES);
		if (typesNode == null) {
			typesNode = oslRootNode.addNode(SLConsts.NODE_NAME_TYPES);
		}
		return typesNode;
	}

	//osl/metadata/links
	public static SLPersistentNode getMetaLinksNode(SLPersistentTreeSession treeSession) throws SLPersistentTreeSessionException {
		SLPersistentNode oslRootNode = getMetadataNode(treeSession);
		SLPersistentNode linksNode = oslRootNode.getNode(SLConsts.NODE_NAME_LINKS);
		if (linksNode == null) {
			linksNode = oslRootNode.addNode(SLConsts.NODE_NAME_LINKS);
		}
		return linksNode;
	}
	
	//osl/metadata/links/linkClassFullQualifiedName
	public static SLPersistentNode getMetaLinkClassNode(SLPersistentTreeSession treeSession, Class<? extends SLLink> linkClass) throws SLPersistentTreeSessionException {
		SLPersistentNode linksNode = getMetaLinksNode(treeSession);
		SLPersistentNode linkClassNode = linksNode.getNode(linkClass.getName());
		if (linkClassNode == null) {
			linkClassNode = linksNode.addNode(linkClass.getName());
		}
		return linkClassNode;
	}
}

