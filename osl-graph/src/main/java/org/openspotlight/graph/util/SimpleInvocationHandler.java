package org.openspotlight.graph.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimpleInvocationHandler implements InvocationHandler {
	
	private Object target;
	
	public SimpleInvocationHandler(Object target) {
		this.target = target;
	}

	//@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(target, args);
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	public Object getTarget() {
		return target;
	}
}
