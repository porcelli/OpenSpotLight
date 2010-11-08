package org.openspotlight.bundle.scheduler;

import java.util.Map;

import org.openspotlight.bundle.annotation.SchedulableCommandMap;
import org.openspotlight.domain.Schedulable;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;

/**
 * Created by IntelliJ IDEA. User: feu Date: Sep 29, 2010 Time: 4:25:20 PM To change this template use File | Settings | File
 * Templates.
 */
public class SchedulerModule extends AbstractModule {

    private final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap;

    public SchedulerModule(final Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>> schedulableMap) {
        this.schedulableMap = schedulableMap;
    }

    @Override
    protected void configure() {
        bind(Scheduler.class).to(DefaultScheduler.class);
        bind(new TypeLiteral<Map<Class<? extends Schedulable>, Class<? extends SchedulableTaskFactory>>>() {
            }).annotatedWith(SchedulableCommandMap.class).toInstance(schedulableMap);
    }
}
