package org.openspotlight.graph.query.parser;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

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

        assertThat(result, notNullValue());
        Assert.assertNotNull(result);
    }

    @Test
    public void testCheckQueryExists() throws SLException, IOException {
        String select = "select *;";
        SLQueryTextInternal result = queryBuilder.build(select);

        String select2 = "     select *      ;   ";
        SLQueryTextInternal result2 = queryBuilder.build(select2);

        assertThat(result.getClass().getName(), is(result2.getClass().getName()));
    }

}
