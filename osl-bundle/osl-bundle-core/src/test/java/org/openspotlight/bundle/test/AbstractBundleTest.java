package org.openspotlight.bundle.test;

import com.google.inject.Injector;
import org.junit.Before;
import org.openspotlight.bundle.scheduler.Scheduler;
import org.openspotlight.domain.ArtifactSourceMapping;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;
import org.openspotlight.federation.domain.artifact.ArtifactSource;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Oct 5, 2010
 * Time: 2:55:10 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBundleTest {

    public abstract Repository createRepository();

    private Repository repository;

    private Scheduler scheduler;

    private Group group;

    private ArtifactSource artifactSource;

    public Repository getRepository() {
        return repository;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public Group getGroup() {
        return group;
    }

    public ArtifactSource getArtifactSource() {
        return artifactSource;
    }

    @Before
    public void setup() throws Exception {


        Injector injector = createInjector();

        repository = createRepository();
        group = repository.getGroups().iterator().next();
        artifactSource = group.getArtifactSources().iterator().next();
        scheduler = injector.getInstance(Scheduler.class);

        ExampleExecutionHistory.resetData();

    }


    protected Injector createInjector() {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }


}
