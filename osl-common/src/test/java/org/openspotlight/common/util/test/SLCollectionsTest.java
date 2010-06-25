package org.openspotlight.common.util.test;

import org.junit.Test;
import org.openspotlight.common.util.SLCollections;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by User: feu - Date: Jun 25, 2010 - Time: 6:54:41 PM
 */
public class SLCollectionsTest {
    @Test
    public void shouldIterateOnVariousIterables() throws Exception {

        List<Iterable<String>> allLists = newArrayList();
        allLists.add(newArrayList("A", "B", "C"));
        allLists.add(Collections.<String>emptyList());
        allLists.add(newArrayList("A", "B", "C"));
        int count = 0;
        for (String s : SLCollections.<String>iterableOfAll(allLists)) {
            count++;
        }
        assertThat(count, is(6));

    }

    @Test
    public void shouldIterateOnOneIterables() throws Exception {

        List<Iterable<String>> allLists = newArrayList();
        allLists.add(newArrayList("A", "B", "C"));
        int count = 0;
        for (String s : SLCollections.<String>iterableOfAll(allLists)) {
            count++;
        }
        assertThat(count, is(3));

    }
}
