package org.openspotlight.bundle.test;

import java.util.Map;

import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextProvider;
import org.openspotlight.bundle.task.ProcessingTask;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 4, 2010 Time: 3:44:10 PM To change this template use File | Settings | File
 * Templates.
 */
public class ExampleProcessingTask extends ProcessingTask {

    protected ExampleProcessingTask(final ExecutionContextProvider provider, final Map<String, String> properties) {
        super(provider, properties);
    }

    @Override
    protected void execute()
        throws Exception {
        ExampleExecutionHistory.add(this.getClass(), null, null);
    }

    @Override
    public boolean isValid(final ExecutionContext context, final Map<String, String> properties) {
        return true;
    }
}
