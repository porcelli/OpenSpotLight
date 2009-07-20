package org.openspotlight.graph.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyUtil {

	public static <T> T createProxy(Class<T> iClass, Object target) {
		InvocationHandler handler = new SimpleInvocationHandler(target);
		return iClass.cast(Proxy.newProxyInstance(iClass.getClassLoader(), new Class<?>[] {iClass}, handler));
	}
	
	public static <T> T createProxy(Class<T> iClass, InvocationHandler handler) {
		return iClass.cast(Proxy.newProxyInstance(iClass.getClassLoader(), new Class<?>[] {iClass}, handler));
	}
	
	
}
