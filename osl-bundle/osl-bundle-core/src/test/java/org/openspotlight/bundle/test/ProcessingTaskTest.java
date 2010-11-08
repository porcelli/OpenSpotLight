package org.openspotlight.bundle.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.bundle.test.ExampleExecutionHistory.getData;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.junit.Test;
import org.openspotlight.common.Triple;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.domain.artifact.Artifact;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Oct 4, 2010
 * Time: 3:08:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessingTaskTest extends AbstractBundleTest {

    @Test
    public void shouldProcessFederatedSources() throws Exception {
        getScheduler().fireSchedulable(null, null, getArtifactSource());
        Iterator<Triple<Class<? extends Callable>, Artifact, String>> it = getData().iterator();
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(ExampleProcessingTask.class,null,"bundle task 1")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(AnotherExampleProcessingTask.class,null,"bundle task 2")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(AnotherExampleProcessingTask.class,null,"bundle task 3")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(AnotherExampleProcessingTask.class,null,"bundle task 4")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(AnotherExampleProcessingTask.class,null,"bundle task 5")));
        assertThat(it.next(), is(Triple.<Class<? extends Callable>, Artifact, String>newTriple(AnotherOneExampleProcessingTask.class,null,"bundle task 6")));
    }

    @Override
    public Repository createRepository() {
        return Repository.newRepositoryNamed("repository")
                .withGroup("group").withArtifactSource("source", "src", "/")
                .withBundleConfig("bundle task 1", ExampleProcessingTask.class)
                .withBundleConfig("bundle task 2", AnotherExampleProcessingTask.class)
                .withBundleConfig("bundle task 3", AnotherExampleProcessingTask.class)
                .withBundleConfig("bundle task 4", AnotherExampleProcessingTask.class)
                .withBundleConfig("bundle task 5", AnotherExampleProcessingTask.class)
                .withBundleConfig("bundle task 6", AnotherOneExampleProcessingTask.class)
                .andCreate();
    }
}
