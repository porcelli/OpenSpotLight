package org.openspotlight.federation.processing;

import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeTypeNotInExistentHierarchy;

/**
 * The Interface CurrentProcessorContext.
 */
public interface CurrentProcessorContext {

	/**
	 * Gets the current group.
	 * 
	 * @return the current group
	 */
	public Group getCurrentGroup();

	/**
	 * Gets the current node group.
	 * 
	 * @return the current node group
	 * @throws SLInvalidCredentialException
	 *             the SL invalid credential exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 */
	public SLNode getCurrentNodeGroup()
			throws SLNodeTypeNotInExistentHierarchy,
			SLGraphSessionException, SLInvalidCredentialException;

	/**
	 * Gets the current repository.
	 * 
	 * @return the current repository
	 */
	public Repository getCurrentRepository();

	/**
	 * Gets the node for group.
	 * 
	 * @param group
	 *            the group
	 * @return the node for group
	 * @throws SLInvalidCredentialException
	 *             the SL invalid credential exception
	 * @throws SLGraphSessionException
	 *             the SL graph session exception
	 * @throws SLNodeTypeNotInExistentHierarchy
	 *             the SL node type not in existent hierarchy
	 */
	public SLNode getNodeForGroup(Group group)
			throws SLNodeTypeNotInExistentHierarchy,
			SLGraphSessionException, SLInvalidCredentialException;

}