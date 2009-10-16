package org.openspotlight.common.util.test;

import org.junit.Test;
import org.openspotlight.common.util.TimeOutFactory;
import org.openspotlight.common.util.TimeOutFactory.TaskFinalizer;

public class TimeOutTest {

    private static class ExampleImplementation implements ExampleInterface {

        public boolean returnsTrue() throws Exception {
            return true;

        }

    }

    volatile boolean finalized = false;

    @Test
    public void shouldExecuteWhenTheresNoTimeOut() throws Exception {
        final ExampleInterface newInstance = TimeOutFactory.createTimedOutObject(ExampleInterface.class, 500,
                                                                                 new ExampleImplementation(),
                                                                                 new TaskFinalizer<ExampleInterface>() {

                                                                                     public void finalizeTask( final ExampleInterface target ) {
                                                                                         TimeOutTest.this.finalized = true;
                                                                                     }

                                                                                 });
        final boolean result = newInstance.returnsTrue();
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotExecuteWhenTheresATimeOut() throws Exception {
        final ExampleInterface newInstance = TimeOutFactory.createTimedOutObject(ExampleInterface.class, 500,
                                                                                 new ExampleImplementation(),
                                                                                 new TaskFinalizer<ExampleInterface>() {

                                                                                     public void finalizeTask( final ExampleInterface target ) {
                                                                                         TimeOutTest.this.finalized = true;
                                                                                     }

                                                                                 });
        Thread.sleep(1000);
        final boolean result = newInstance.returnsTrue();

    }

}
