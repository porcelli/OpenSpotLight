package org.openspotlight.graph.query;

import java.util.Collection;

import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.persistence.SLPersistentTreeSession;
import org.openspotlight.graph.query.SLQuery.SortMode;

public abstract class AbstractSLQuery {

    /** The session. */
    protected SLGraphSession          session;

    /** The tree session. */
    protected SLPersistentTreeSession treeSession;

    /**
     * Instantiates a new sL query impl.
     * 
     * @param session the session
     * @param treeSession the tree session
     */
    public AbstractSLQuery(
                            SLGraphSession session, SLPersistentTreeSession treeSession ) {
        this.session = session;
        this.treeSession = treeSession;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLQuery#execute()
     */
    public SLQueryResult execute() throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
        return execute((String[])null, SortMode.NOT_SORTED, false);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLQuery#execute(java.util.Collection)
     */
    public SLQueryResult execute( Collection<SLNode> inputNodes ) throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
        return execute(SLQuerySupport.getNodeIDs(inputNodes), SortMode.NOT_SORTED, false);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLQuery#execute(java.lang.String[])
     */
    public SLQueryResult execute( String[] inputNodesIDs ) throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
        return execute(inputNodesIDs, SortMode.NOT_SORTED, false);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLQuery#execute(org.openspotlight.graph.query.SLQuery.SortMode, boolean)
     */
    public SLQueryResult execute( SortMode sortMode,
                                  boolean showSLQL )
        throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
        return execute((String[])null, sortMode, showSLQL);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLQuery#execute(java.util.Collection, org.openspotlight.graph.query.SLQuery.SortMode, boolean)
     */
    public SLQueryResult execute( Collection<SLNode> inputNodes,
                                  SortMode sortMode,
                                  boolean showSLQL )
        throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException {
        return execute(SLQuerySupport.getNodeIDs(inputNodes), SortMode.NOT_SORTED, false);
    }

    /* (non-Javadoc)
     * @see org.openspotlight.graph.query.SLQuery#execute(org.openspotlight.graph.query.SLQuery.SortMode, boolean)
     */
    public abstract SLQueryResult execute( String[] inputNodesIDs,
                                           SortMode sortMode,
                                           boolean showSLQL )
        throws SLInvalidQuerySyntaxException, SLInvalidQueryElementException, SLQueryException;
}
