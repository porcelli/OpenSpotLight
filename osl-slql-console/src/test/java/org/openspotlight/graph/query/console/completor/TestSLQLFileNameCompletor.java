package org.openspotlight.graph.query.console.completor;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.openspotlight.graph.query.console.command.DynamicCommandSupport;

public class TestSLQLFileNameCompletor {

    @Test
    public void testCompletorNonCondidates() {
        SLQLFileNameCompletor completor = new SLQLFileNameCompletor(null);
        List<?> candidates = new LinkedList<Object>();
        completor.complete("get ", 4, candidates);

        assertThat(candidates.size(), is(0));
    }

    @Test
    public void testCompletorNonCondidates2() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        SLQLFileNameCompletor completor = new SLQLFileNameCompletor(DynamicCommandSupport.getRegisteredDynamicCommands());
        List<?> candidates = new LinkedList<Object>();
        completor.complete("list ", 4, candidates);

        assertThat(candidates.size(), is(0));
    }

    @Test
    public void testCompletorSomeCondidates() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        SLQLFileNameCompletor completor = new SLQLFileNameCompletor(DynamicCommandSupport.getRegisteredDynamicCommands());
        List<?> candidates = new LinkedList<Object>();
        completor.complete("get ", 4, candidates);

        assertThat(candidates.size(), is(not(0)));
    }
}
