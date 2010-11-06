package org.openspotlight.bundle.test;

import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.bundle.context.ExecutionContextProvider;
import org.openspotlight.bundle.task.ProcessingTask;

/**
 * Created by IntelliJ IDEA.
 * User: feu
 * Date: Oct 4, 2010
 * Time: 3:44:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnotherExampleProcessingTask extends ProcessingTask {
    protected AnotherExampleProcessingTask(ExecutionContextProvider provider) {
        super(provider);
    }

    @Override
    protected void execute(ExecutionContext context) throws Exception {
        ExampleExecutionHistory.add(this.getClass(), null, null);
    }
}
