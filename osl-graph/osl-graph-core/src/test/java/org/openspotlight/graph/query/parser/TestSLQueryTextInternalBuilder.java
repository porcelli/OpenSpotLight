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

    private final SLQueryTextInternalBuilder queryBuilder = new SLQueryTextInternalBuilder();

    @Test
    public void testCheckQueryExists() throws SLException, IOException {
        final String select = "select *;";
        final SLQueryTextInternal result = this.queryBuilder.build(select);

        final String select2 = "     select *      ;   ";
        final SLQueryTextInternal result2 = this.queryBuilder.build(select2);

        assertThat(result.getClass().getName(), is(result2.getClass().getName()));
    }

    @Test
    public void testSelectStar() throws SLException, IOException {
        final String select = "select *;";
        final SLQueryTextInternal result = this.queryBuilder.build(select);

        assertThat(result, notNullValue());
        Assert.assertNotNull(result);
    }

}
