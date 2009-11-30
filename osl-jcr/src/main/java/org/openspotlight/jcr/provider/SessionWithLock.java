package org.openspotlight.jcr.provider;

import javax.jcr.Session;

import org.openspotlight.common.concurrent.LockContainer;

public interface SessionWithLock extends Session, LockContainer {

}
