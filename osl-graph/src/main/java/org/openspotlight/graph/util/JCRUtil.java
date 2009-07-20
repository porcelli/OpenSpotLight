package org.openspotlight.graph.util;

import java.io.InputStream;
import java.io.Serializable;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;


public class JCRUtil {
	
	/**
	 * 
	 * @param node
	 * @throws RepositoryException
	 */
	public static void makeVersionable(Node node) throws RepositoryException {
		node.addMixin("mix:versionable");
	}
	
	/**
	 * 
	 * @param node
	 * @throws RepositoryException
	 */
	public static void makeReferenceable(Node node) throws RepositoryException {
		node.addMixin("mix:referenceable");
	}
	
	/**
	 * 
	 * @param node
	 * @param name
	 * @return
	 * @throws RepositoryException
	 */
	public static Node getChildNode(Node node, String name) throws RepositoryException {
		try {
			return node.getNode(name);
		}
		catch (PathNotFoundException e) {
		}
		return null;
	}
	
	/**
	 * 
	 * @param session
	 * @param value
	 * @return
	 * @throws JCRUtilException
	 */
	public static Value createValue(Session session, Object value) throws JCRUtilException {
		try {
			Value jcrValue = null;
			ValueFactory factory = session.getValueFactory();
			if (value.getClass().equals(Integer.class) || value.getClass().equals(Long.class)) {
				Number number = Number.class.cast(value);
				jcrValue = factory.createValue(number.longValue());
			}
			else if (value.getClass().equals(Float.class) || value.getClass().equals(Double.class)) {
				Number number = Number.class.cast(value);
				jcrValue = factory.createValue(number.doubleValue());
			}
			else if (value.getClass().equals(String.class)) {
				jcrValue = factory.createValue(String.class.cast(value));
			}
			else if (value.getClass().equals(Boolean.class)) {
				jcrValue = factory.createValue(Boolean.class.cast(value));
			}
			else {
				InputStream inputStream = SerializationUtil.serialize(value);
	        	jcrValue = factory.createValue(inputStream);
			}
			return jcrValue;
		}
		catch (Exception e) {
			throw new JCRUtilException("Error on attempt to create value.", e);
		}
	}
	
	/**
	 * 
	 * @param session
	 * @param value
	 * @return
	 * @throws JCRUtilException
	 */
	public static Value[] createValues(Session session, Object value) throws JCRUtilException {
		try {
			Value[] jcrValues = null;
			ValueFactory factory = session.getValueFactory();
			if (value.getClass().equals(Integer[].class) || value.getClass().equals(Long[].class)) {
				Long[] arr = (Long[]) value;
				jcrValues = new Value[arr.length];
				for (int i = 0; i < arr.length; i++) {
					jcrValues[i] = factory.createValue(arr[i]);
				}
			}
			else if (value.getClass().equals(Float[].class) || value.getClass().equals(Double[].class)) {
				Float[] arr = (Float[]) value;
				jcrValues = new Value[arr.length];
				for (int i = 0; i < arr.length; i++) {
					jcrValues[i] = factory.createValue(arr[i]);
				}
			}
			else if (value.getClass().equals(String[].class)) {
				String[] arr = (String[]) value;
				jcrValues = new Value[arr.length];
				for (int i = 0; i < jcrValues.length; i++) {
					jcrValues[i] = factory.createValue(arr[i]);
				}
			}
			else if (value.getClass().equals(Boolean[].class)) {
				Boolean[] arr = (Boolean[]) value;
				jcrValues = new Value[arr.length];
				for (int i = 0; i < jcrValues.length; i++) {
					jcrValues[i] = factory.createValue(arr[i]);
				}
			}
			else {
				Serializable[] arr = (Serializable[]) value;
				jcrValues = new Value[arr.length];
				for (int i = 0; i < jcrValues.length; i++) {
		        	InputStream inputStream = SerializationUtil.serialize(arr[i]);
		        	jcrValues[i] = factory.createValue(inputStream);
				}
			}
			return jcrValues;

		}
		catch (Exception e) {
			throw new JCRUtilException("Error on attempt to create value array.", e);
		}
	}
}
