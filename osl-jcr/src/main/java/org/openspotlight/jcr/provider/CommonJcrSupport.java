package org.openspotlight.jcr.provider;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.openspotlight.common.SharedConstants;
import org.openspotlight.common.util.Assertions;

/**
 * The Class CommonJcrSupport.
 */
public class CommonJcrSupport implements SharedConstants {

    /**
     * Creates the repository nodes.
     * 
     * @param jcrSession the jcr session
     * @param repositoryNames the repository names
     * @throws RepositoryException the repository exception
     */
    public static void createRepositoryNodes( final Session jcrSession,
                                              final String... repositoryNames ) throws RepositoryException {
        Assertions.checkNotNull("jcrSession", jcrSession);
        for (final String repoName : repositoryNames) {
            Assertions.checkNotEmpty("repositoryNameNotEmpty:" + repoName, repoName);
            Assertions.checkCondition("validRepositoryNameForJcr:" + repoName, repoName.matches(VALID_JCR_NODE_NAME_REGEXP));
        }
        Node rootNode;
        try {
            rootNode = jcrSession.getRootNode().getNode(DEFAULT_JCR_ROOT_NAME);
        } catch (final PathNotFoundException e) {
            rootNode = jcrSession.getRootNode().addNode(DEFAULT_JCR_ROOT_NAME);
        }
        for (final String repoName : repositoryNames) {
            try {
                rootNode.getNode(repoName);
            } catch (final PathNotFoundException e) {
                rootNode.addNode(repoName);
            }
        }
    }

}
