package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openspotlight.common.util.TimeOutFactory;
import org.openspotlight.common.util.TimeOutFactory.TaskFinalizer;

public class TimeOutTest {

    private static class ExampleImplementation implements ExampleInterface {

        volatile boolean finalized = false;

        public boolean isFinalized() {
            return this.finalized;
        }

        public boolean returnsTrue() throws Exception {
            return true;

        }

        public void setFinalized( final boolean finalized ) {
            this.finalized = finalized;
        }

    }

    @Test
    public void shouldExecuteWhenTheresNoTimeOut() throws Exception {
        final ExampleInterface newInstance = TimeOutFactory.createTimedOutObject(ExampleInterface.class, 500,
                                                                                 new ExampleImplementation(),
                                                                                 new TaskFinalizer<ExampleInterface>() {

                                                                                     public void finalizeTask( final ExampleInterface target ) {
                                                                                         target.setFinalized(true);
                                                                                     }

                                                                                 });
        final boolean result = newInstance.returnsTrue();
        assertThat(result, is(true));
        assertThat(newInstance.isFinalized(), is(false));
    }

    @Test( expected = IllegalStateException.class )
    public void shouldNotExecuteWhenTheresATimeOut() throws Exception {
        final ExampleInterface newInstance = TimeOutFactory.createTimedOutObject(ExampleInterface.class, 500,
                                                                                 new ExampleImplementation(),
                                                                                 new TaskFinalizer<ExampleInterface>() {

                                                                                     public void finalizeTask( final ExampleInterface target ) {
                                                                                         target.setFinalized(true);
                                                                                     }

                                                                                 });
        Thread.sleep(1000);
        try {
            newInstance.returnsTrue();
        } finally {
            assertThat(newInstance.isFinalized(), is(true));
        }

    }

}
