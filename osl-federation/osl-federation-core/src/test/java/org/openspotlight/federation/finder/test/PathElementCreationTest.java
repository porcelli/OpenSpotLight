package org.openspotlight.federation.finder.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.federation.domain.PathElement;

public class PathElementCreationTest {

    @Test
    public void shouldCreateCorrectPathElement() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("a/b/c/d/e");
        assertThat(pathElement.getCompletePath(), is("/a/b/c/d/e"));
    }

    @Test
    public void shouldCreateCorrectPathElementUsingDots() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("a/b/c/././d/e");
        assertThat(pathElement.getCompletePath(), is("/a/b/c/d/e"));
    }

    @Test
    public void shouldCreateCorrectPathElementUsingDoubleDots() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("a/b/c/../../d/e");
        assertThat(pathElement.getCompletePath(), is("/a/d/e"));
    }

    @Test
    public void shouldCreateCorrectPathElementWithRelativePath() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("a/b/c");
        final PathElement newRelativePathElement = PathElement.createRelativePath(pathElement, "d/e");
        assertThat(newRelativePathElement.getCompletePath(), is("/a/b/c/d/e"));
    }

    @Test
    public void shouldCreateCorrectRelativePathUsingDots() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("a/b/c/d/e/");
        final PathElement newRelativePathElement = PathElement.createRelativePath(pathElement, "././f");
        assertThat(newRelativePathElement.getCompletePath(), is("/a/b/c/d/e/f"));
    }

    @Test
    public void shouldCreateCorrectRelativePathUsingDoubleDots() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("a/b/c/d/e/");
        final PathElement newRelativePathElement = PathElement.createRelativePath(pathElement, "../../f");
        assertThat(newRelativePathElement.getCompletePath(), is("/a/b/c/f"));
    }

    @Test
    public void shouldGetCorrectParentPathElement() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("a/b/c/d/e");
        final PathElement parentPathElement = PathElement.createFromPathString("a/b/c/d");
        assertThat(pathElement.getParent(), is(parentPathElement));
    }

    @Test
    public void shouldGetCorrectParentPathElement2() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("/a/b/c/d/e");
        final PathElement parentPathElement = PathElement.createFromPathString("a/b/c/d");
        assertThat(pathElement.getParent(), is(parentPathElement));
    }

    @Test
    public void shouldGetCorrectParentPathElement3() throws Exception {
        final PathElement pathElement = PathElement.createFromPathString("a/b/c/d/e/");
        final PathElement parentPathElement = PathElement.createFromPathString("a/b/c/d");
        assertThat(pathElement.getParent(), is(parentPathElement));
    }

}
