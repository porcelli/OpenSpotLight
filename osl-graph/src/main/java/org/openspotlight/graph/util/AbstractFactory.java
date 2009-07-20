package org.openspotlight.graph.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractFactory {
	
	private static Map<Class<? extends AbstractFactory>, AbstractFactory> factoryMap = new HashMap<Class<? extends AbstractFactory>, AbstractFactory>();
	
	@SuppressWarnings("unchecked")
	public static <T extends AbstractFactory> T getDefaultInstance(Class<T> clazz) throws AbstractFactoryException {
		T factory = null;
		try {
			factory = (T) factoryMap.get(clazz);
			if (factory == null) {
				Properties props = loadProps(clazz);
				String implClassName = props.getProperty("defaultImpl");
				Class<? extends T> implClass = (Class<? extends T>) Class.forName(implClassName);
				factory = implClass.newInstance();
				factoryMap.put(clazz, factory);
			}
		}
		catch (Exception e) {
			throw new AbstractFactoryException("Error on attempt to create the factory.", e);
		}
		return factory;
	}
	
	private static Properties loadProps(Class<?> clazz) throws AbstractFactoryException {
		String resource = clazz.getName().replace('.', '/').concat(".properties");
		try {
			InputStream inputStream = AbstractFactory.class.getClassLoader().getResourceAsStream(resource);
			Properties props = new Properties();
			props.load(inputStream);
			return props;
		}
		catch (IOException e) {
			throw new AbstractFactoryException("Error on attempt to load factory properties file " + resource, e);
		}
	}
}
