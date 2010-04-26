package org.openspotlight.graph.guice;

import com.google.inject.Inject;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.graph.SLGraph;
import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.guice.ThreadLocalProvider;
import org.openspotlight.security.idm.UserProvider;
import org.openspotlight.storage.STRepositoryPath;

import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

/**
 * Created by User: feu - Date: Apr 26, 2010 - Time: 9:27:25 AM
 */
public class SLGraphSessionProvider extends ThreadLocalProvider<SLGraphSession> {

    private final SLGraph graph;

    private final STRepositoryPath repositoryPath;

    private final UserProvider userProvider;

    @Inject
    public SLGraphSessionProvider(SLGraph graph, STRepositoryPath repositoryPath, UserProvider userProvider) {
        this.graph = graph;
        this.repositoryPath = repositoryPath;
        this.userProvider = userProvider;
    }

    @Override
    protected SLGraphSession createInstance() {
        try {
            return graph.openSession(userProvider.getCurrentUser(), repositoryPath.getRepositoryPathAsString());
        } catch (Exception e) {
            throw logAndReturnNew(e, SLRuntimeException.class);
        }
    }
}
