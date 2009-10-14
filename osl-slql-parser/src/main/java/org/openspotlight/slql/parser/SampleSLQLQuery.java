package org.openspotlight.slql.parser;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.openspotlight.graph.SLGraphSession;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.query.SLQuery;
import org.openspotlight.graph.query.SLQueryResult;

public class SampleSLQLQuery extends AbstractSLQLQuery {

    private static final long serialVersionUID = 3883553014168140077L;

    public SampleSLQLQuery(
                            final String id, final Set<SLQLVariable> variables, final String outputModelName,
                            final boolean isTarget, final SLQLQuery target ) {
        super(id, variables, outputModelName, isTarget, target);
    }

    @Override
    public Collection<SLNode> execute( final SLGraphSession session,
                                       final Map<String, ?> variableValues,
                                       final Collection<SLNode> inputNodes ) throws SLGraphSessionException {
        validate(session, variableValues, inputNodes);

        SLQuery query = session.createQuery();
        query.select().allTypes().onWhere().selectEnd().where().type("myType").each().property("myProperty").equalsTo().value("TesteValue").typeEnd().whereEnd();

        SLQueryResult result = null;
        if (inputNodes != null) {
            result = query.execute(inputNodes);
        } else {
            result = query.execute();
        }

        return result.getNodes();
    }

}
