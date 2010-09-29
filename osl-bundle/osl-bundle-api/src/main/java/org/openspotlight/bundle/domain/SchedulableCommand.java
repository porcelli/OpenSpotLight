package org.openspotlight.bundle.domain;

import java.util.concurrent.Callable;

public interface SchedulableCommand extends Callable<Void> {

    public String getJobUniqueId();


}