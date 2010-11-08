package org.openspotlight.bundle.test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;

import org.openspotlight.common.Triple;
import org.openspotlight.federation.domain.artifact.Artifact;

/**
 * Created by IntelliJ IDEA. User: feu Date: Oct 4, 2010 Time: 3:54:01 PM To change this template use File | Settings | File
 * Templates.
 */
public class ExampleExecutionHistory {
    public static void resetData() {
        data.clear();
    }

    private static List<Triple<Class<? extends Callable>, Artifact, String>> data =
                                                                                      new CopyOnWriteArrayList<Triple<Class<? extends Callable>, Artifact, String>>();

    public static List<Triple<Class<? extends Callable>, Artifact, String>> getData() {
        return data;
    }

    public static void add(final Class<? extends Callable> taskType, final Artifact artifact, final String someString) {
        data.add(Triple.<Class<? extends Callable>, Artifact, String>newTriple(taskType, artifact, someString));
    }

}
