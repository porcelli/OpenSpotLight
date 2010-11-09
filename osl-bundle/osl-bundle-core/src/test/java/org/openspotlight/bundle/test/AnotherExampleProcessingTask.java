package org.openspotlight.bundle.test;

import java.util.Map;

import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextProvider;
import org.openspotlight.bundle.task.ProcessingTask;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 4, 2010 Time: 3:44:10 PM To change this template use File | Settings | File
 * Templates.
 */
public class AnotherExampleProcessingTask extends ProcessingTask {

    protected AnotherExampleProcessingTask(ExecutionContextProvider provider, Map<String, String> properties) {
        super(provider, properties);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isValid(ExecutionContext context, Map<String, String> properties) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void execute()
        throws Exception {
        // TODO Auto-generated method stub

    }
}
