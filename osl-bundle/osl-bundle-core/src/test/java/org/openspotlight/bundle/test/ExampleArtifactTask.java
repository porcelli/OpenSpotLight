package org.openspotlight.bundle.test;

import java.util.Map;

import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextProvider;
import org.openspotlight.bundle.task.ArtifactTask;
import org.openspotlight.federation.domain.artifact.Artifact;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 4, 2010 Time: 3:44:10 PM To change this template use File | Settings | File
 * Templates.
 */
public class ExampleArtifactTask extends ArtifactTask {

    protected ExampleArtifactTask(ExecutionContextProvider provider, Artifact artifact, Map<String, String> properties) {
        super(provider, artifact, properties);
    }

    @Override
    protected void execute()
        throws Exception {
        ExampleExecutionHistory.add(this.getClass(), getArtifact(), null);
    }

    @Override
    public boolean isValid(ExecutionContext context, Map<String, String> properties) {
        return true;
    }

}
