package org.openspotlight.graph.query.parser;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.graph.query.SLQueryTextInternal;

public class TestSLQueryTextInternalBuilder {

    private SLQueryTextInternalBuilder queryBuilder = new SLQueryTextInternalBuilder();

    @Test
    public void testSelectStar() throws SLException, IOException {
        String select = "select *;";
        SLQueryTextInternal result = queryBuilder.build(select);

        Assert.assertNotNull(result);
    }

}