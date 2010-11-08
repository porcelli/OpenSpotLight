package org.openspotlight.bundle.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
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
public class AfterFederationTaskTest extends AbstractBundleTest {


    @Test
    public void shouldProcessFederatedSources() throws Exception {
        getScheduler().fireSchedulable(null,null,getArtifactSource());
        Iterator<Triple<Class<? extends Callable>, Artifact, String>> it = getData().iterator();
        assertThat(it.next().getK1(),is(ExampleArtifactTask.class));
        //FIXME pegar do claspath usando a mesma forma que foi feita no SLQL
        fail();// needs more assertions
    }

    @Override
    public Repository createRepository() {
        return Repository.newRepositoryNamed("repository")
                .withGroup("group").withArtifactSource("source", "src", "/")
                .withBundles("bundle", ExampleAfterFederationTask.class).andCreate();
    }
}
