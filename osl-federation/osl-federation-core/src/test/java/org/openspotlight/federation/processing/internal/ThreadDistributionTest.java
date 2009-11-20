package org.openspotlight.federation.processing.internal;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

import org.junit.Test;

public class ThreadDistributionTest {

    private static class FirstPriority extends Priority {

    }

    private static class Priority {
    }

    private static class PriorityComparator implements Comparator<Priority> {

        public int compare( final Priority o1,
                            final Priority o2 ) {
            final int o1points = o1 instanceof FirstPriority ? 1 : o1 instanceof SecondPriority ? 2 : 3;
            final int o2points = o2 instanceof FirstPriority ? 1 : o2 instanceof SecondPriority ? 2 : 3;
            return o1points < o2points ? -1 : o1points == o2points ? 0 : 1;
        }

    }

    private static class SecondPriority extends Priority {

    }

    private static class ThirdPriority extends Priority {

    }

    @Test
    public void shouldGetItemsInOrder() throws Exception {
        final PriorityBlockingQueue<Priority> queue = new PriorityBlockingQueue<Priority>(10, new PriorityComparator());

        queue.add(new ThirdPriority());
        queue.add(new SecondPriority());
        queue.add(new ThirdPriority());
        queue.add(new SecondPriority());
        queue.add(new ThirdPriority());
        queue.add(new FirstPriority());
        queue.add(new FirstPriority());
        queue.add(new ThirdPriority());
        queue.add(new FirstPriority());
        queue.add(new SecondPriority());
        queue.add(new SecondPriority());
        queue.add(new ThirdPriority());
        queue.add(new SecondPriority());
        queue.add(new ThirdPriority());
        queue.add(new FirstPriority());
        queue.add(new ThirdPriority());
        queue.add(new FirstPriority());
        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }

    }
}
