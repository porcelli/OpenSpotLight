package org.openspotlight.bundle.task;

import java.util.concurrent.Callable;

public interface Task extends Callable<Void>, Comparable<Task> {

}
