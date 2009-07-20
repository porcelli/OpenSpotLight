package org.openspotlight.graph;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.openspotlight.SLRuntimeException;
import org.openspotlight.graph.annotation.SLProperty;

public class SLNodeInvocationHandler implements InvocationHandler {
	
	private SLNode node;
	
	public SLNodeInvocationHandler(SLNode node) {
		this.node = node;
	}
	
	public SLNode getNode() {
		return node;
	}

	@SuppressWarnings("unchecked")
	//@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object result = null;
		if (!method.getDeclaringClass().equals(SLNode.class) && SLNode.class.isAssignableFrom(method.getDeclaringClass())) {
			if (isGetter(proxy, method)) {
				String propName = getPropertyName(method);
				Class<? extends Serializable> typeClass = (Class<? extends Serializable>) method.getReturnType();
				result = node.getProperty(typeClass, propName).getValue();
			}
			else if (isSetter(proxy, method)) {
				String propName = getPropertyName(method);
				node.setProperty(Serializable.class, propName, (Serializable) args[0]);
			}
		}
		else {
			result = invokeMethod(method, args);
		}
		return result;
	}
	
	private boolean isGetter(Object proxy, Method method) {
		try {
			boolean status = false;
			if (method.getName().startsWith("get") && !method.getReturnType().equals(void.class) && method.getParameterTypes().length == 0) {
				SLProperty propertyAnnotation = method.getAnnotation(SLProperty.class);
				if (propertyAnnotation == null) {
					try {
						String setterName = "set".concat(method.getName().substring(3));
						Class<?> iFace = proxy.getClass().getInterfaces()[0];
						Method setterMethod = iFace.getMethod(setterName, new Class<?>[] {method.getReturnType()});
						status = setterMethod.getAnnotation(SLProperty.class) != null && setterMethod.getReturnType().equals(void.class);
					}
					catch (NoSuchMethodException e) {}
				}
				else {
					status = true;
				}
			}
			return status;
		}
		catch (Exception e) {
			throw new SLRuntimeException("Error on attempt to verify if method is getter.", e);
		}
	}
	
	private boolean isSetter(Object proxy, Method method) {
		try {
			boolean status = false;
			if (method.getName().startsWith("set") && method.getReturnType().equals(void.class) && method.getParameterTypes().length == 1) {
				SLProperty propertyAnnotation = method.getAnnotation(SLProperty.class);
				if (propertyAnnotation == null) {
					try {
						String getterName = "get".concat(method.getName().substring(3));
						Class<?> iFace = proxy.getClass().getInterfaces()[0];
						Method getterMethod = iFace.getMethod(getterName, new Class<?>[] {});
						status = getterMethod.getAnnotation(SLProperty.class) != null 
							&& getterMethod.getReturnType().equals(method.getParameterTypes()[0]);
					}
					catch (NoSuchMethodException e) {}
				}
				else {
					status = true;
				}
			}
			return status;
		}
		catch (Exception e) {
			throw new SLRuntimeException("Error on attempt to verify if method is setter.", e);
		}
	}
	
	private String getPropertyName(Method method) {
		return method.getName().substring(3, 4).toLowerCase().concat(method.getName().substring(4));
	}
	
	private Object invokeMethod(Method method, Object[] args) throws Throwable {
		try {
			return method.invoke(node, args);
		}
		catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		catch (Exception e) {
			throw new SLRuntimeException("Error on node proxy.", e);
		}
	}
}
