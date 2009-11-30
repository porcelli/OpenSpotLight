package org.openspotlight.common.concurrent;

/**
 * Interface to be used to share the same lock object when there's a complex
 * object three with the same lock.
 * 
 * <ul>
 * <li>When the new object contains another {@link LockContainer}, use its
 * "parent" container as a lock object</li>
 * <li>Synchronize public methods. Private ones are not mandatory</li>
 * <li>If the method is simple, avoid synchronization</li>
 * <li>If you are uncertain about the synchronization needs, just synchronize</li>
 * <li>If the public method contains only a simple delegation to an synchronized
 * object with the same lock, there's no need to synchronize it</li>
 * <li>If it call any external method or constructor, just synchronize</li>
 * 
 * </ul>
 * How to use it? As the old fashion way:
 * 
 * <pre>
 * private final Object lock;
 * 
 * Constructor(){
 *   this.lock = new Object();
 * }
 * //or
 * Constructor(LockContainer parent){
 *   this.lock = parent.getLockObject();
 * }
 * synchronized(lock){
 * 	//...
 * }
 * </pre>
 * 
 * <b>WARNING:</b> Use the {@link #getLockObject()} method to get the lock
 * object. Do not use the parent itself NEVER!
 * 
 * @author feu
 * 
 */
public interface LockContainer {

	/**
	 * Returns the lock object to be used on synchronized statements.
	 * 
	 * @return
	 */
	Object getLockObject();

}
