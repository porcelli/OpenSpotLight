package org.openspotlight.graph.query.parser;

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
    public Collection<SLNode> execute( final SLGraphSession $1,
                                       final Map<String, ?> $2,
                                       final Collection<SLNode> $3 ) throws SLGraphSessionException {
        validateAndInit($1, $2, $3);

        SLQuery query = $1.createQuery();
        query.select().allTypes().onWhere().selectEnd().where().type("myType").each().property("myProperty").equalsTo().value("TesteValue").typeEnd().whereEnd();

        SLQueryResult result = null;
        if ($3 != null) {
            result = query.execute($3);
        } else {
            result = query.execute();
        }

        return result.getNodes();
    }

}
