package org.openspotlight.federation.processing.internal.domain;

import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.processing.BundleProcessor.CurrentProcessorContext;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeTypeNotInExistentHierarchy;

public class CurrentProcessorContextImpl implements CurrentProcessorContext {

    private Group      currentGroup;

    private SLNode     currentNodeGroup;

    private Repository currentRepository;

    private SLContext  groupContext;

    public Group getCurrentGroup() {
        return this.currentGroup;
    }

    public SLNode getCurrentNodeGroup()
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException, SLInvalidCredentialException {
        if (this.currentNodeGroup == null) {
            if (this.currentGroup != null && this.groupContext != null) {
                this.currentNodeGroup = this.getNodeForGroup(this.currentGroup);
            }
        }
        return this.currentNodeGroup;
    }

    public Repository getCurrentRepository() {
        return this.currentRepository;
    }

    public SLContext getGroupContext() {
        return this.groupContext;
    }

    public SLNode getNodeForGroup( final Group group )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException, SLInvalidCredentialException {
        return this.groupContext.getRootNode().addNode(group.getUniqueName());
    }

    public void setCurrentGroup( final Group currentGroup ) {
        this.currentGroup = currentGroup;
    }

    public void setCurrentNodeGroup( final SLNode currentNodeGroup ) {
        this.currentNodeGroup = currentNodeGroup;
    }

    public void setCurrentRepository( final Repository currentRepository ) {
        this.currentRepository = currentRepository;
    }

    public void setGroupContext( final SLContext groupContext ) {
        this.groupContext = groupContext;
    }

}
