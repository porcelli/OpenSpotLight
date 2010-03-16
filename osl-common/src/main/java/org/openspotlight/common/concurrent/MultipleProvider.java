package org.openspotlight.common.concurrent;

import java.util.concurrent.CopyOnWriteArrayList;

import org.openspotlight.common.Disposable;

/**
 * This class wraps a factory to describe if the produced item should be shared
 * between threads or not. In case of multi threaded environment it will store
 * the item on a thread local variable, but it can close all created items, if
 * this item implements {@link Disposable};
 * 
 * @author feu
 * 
 * @param <T>
 */
public class MultipleProvider<T> implements Disposable {

	public boolean useOnePerThread(){
		return useOnePerThread;
	}
	
	/**
	 * constructor
	 * 
	 * @param factory
	 */
	public MultipleProvider(ItemFactory<T> factory) {
		this.factory = factory;
		this.useOnePerThread = factory.useOnePerThread();
		if (useOnePerThread) {
			this.threadLocalItem = null;
			this.singleItem = factory.createNew();
		} else {
			this.threadLocalItem = new ThreadLocal<T>();
			this.singleItem = null;
		}
	}

	/**
	 * Item factory class to describe if the item should be shared between
	 * threads or not.
	 * 
	 * @author feu
	 * 
	 * @param <T>
	 */
	public interface ItemFactory<T> {

		public T createNew();

		public boolean useOnePerThread();

	}

	private CopyOnWriteArrayList<Disposable> openedItems = new CopyOnWriteArrayList<Disposable>();

	private final boolean useOnePerThread;

	private final ItemFactory<T> factory;

	private final ThreadLocal<T> threadLocalItem;

	private final T singleItem;

	/**
	 * returns the item. If this is a multithreaded environment it will create
	 * one if this one doesn't exists.
	 * 
	 * @return
	 */
	public synchronized T get() {
		if (useOnePerThread) {
			return singleItem;
		} else {
			T t = threadLocalItem.get();
			if (t == null) {
				t = factory.createNew();
				if (t instanceof Disposable) {
					openedItems.add((Disposable) t);
				}
				threadLocalItem.set(t);
			}
			return t;
		}
	}

	/**
	 * It will close all opened items and its factory in case of each one
	 * implemented or not {@link Disposable}.
	 */
	public synchronized void closeResources() {
		if (useOnePerThread) {
			if (singleItem instanceof Disposable) {
				((Disposable) singleItem).closeResources();
			} else {
				for (Disposable d : openedItems) {
					d.closeResources();
				}
			}
		}
		if (factory instanceof Disposable) {
			((Disposable) factory).closeResources();
		}

	}

}
