package org.openspotlight.graph.query.parser;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.query.parser.SLQLQuery;
import org.openspotlight.graph.query.parser.SLQLQueryBuilder;

public class TestSLQLQueryBuilder {

    private SLQLQueryBuilder queryBuilder = new SLQLQueryBuilder();

    @Test
    public void testSelectStar() throws SLException, IOException {
        String select = "select *;";
        SLQLQuery result = queryBuilder.build(select);

        Assert.assertNotNull(result);
    }

}