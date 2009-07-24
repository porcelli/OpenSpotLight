/*
 * OpenSpotLight - Open Source IT Governance Platform
 *  
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA 
 * or third-party contributors as indicated by the @author tags or express 
 * copyright attribution statements applied by the authors.  All third-party 
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E 
 * TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * This copyrighted material is made available to anyone wishing to use, modify, 
 * copy, or redistribute it subject to the terms and conditions of the GNU 
 * Lesser General Public License, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU Lesser General Public License  for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this distribution; if not, write to: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA 
 * 
 *********************************************************************** 
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os 
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.  
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.graph.persistence;

import java.io.Serializable;
import java.lang.reflect.Array;

import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;

import org.openspotlight.SLException;
import org.openspotlight.SLRuntimeException;
import org.openspotlight.graph.util.JCRUtil;
import org.openspotlight.graph.util.SerializationUtil;

/**
 * The Class SLPersistentPropertyImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLPersistentPropertyImpl<V extends Serializable> implements SLPersistentProperty<V> {
	
	/** The persistent node. */
	private SLPersistentNode persistentNode;
	
	/** The jcr property. */
	private Property jcrProperty;
	
	/** The clazz. */
	private Class<V> clazz;
	
	/** The event poster. */
	private SLPersistentEventPoster eventPoster;
	
	/**
	 * Instantiates a new sL persistent property impl.
	 * 
	 * @param persistentNode the persistent node
	 * @param clazz the clazz
	 * @param jcrProperty the jcr property
	 * @param loadValue the load value
	 * @param eventPoster the event poster
	 * 
	 * @throws SLPersistentPropertyNotFoundException the SL persistent property not found exception
	 * @throws SLPersistentTreeSessionException the SL persistent tree session exception
	 */
	public SLPersistentPropertyImpl(SLPersistentNode persistentNode, Class<V> clazz, Property jcrProperty, boolean loadValue, SLPersistentEventPoster eventPoster) 
		throws SLPersistentPropertyNotFoundException, SLPersistentTreeSessionException {
		this.persistentNode = persistentNode;
		this.clazz = clazz;
		this.jcrProperty = jcrProperty;
		this.eventPoster = eventPoster;
		if (loadValue) getValue();
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#getNode()
	 */
	public SLPersistentNode getNode() {
		return persistentNode;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#getName()
	 */
	public String getName() throws SLPersistentTreeSessionException {
		try {
			return jcrProperty.getName();
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve persistent property name.", e);
		}
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#getValue()
	 */
	public V getValue() throws SLInvalidPersistentPropertyTypeException, SLPersistentTreeSessionException {
		try {
			V value = null;
			// if the property value is an array ...
			if (jcrProperty.getDefinition().isMultiple()) {
				if (!clazz.isArray()) {
					throw new SLInvalidPersistentPropertyTypeException("Persistent property is an array.");	
				}
				value = getJCRArrayPropertyValue();
			}
			// if the property value is not an array ...
			else if (!jcrProperty.getDefinition().isMultiple()) {
				if (clazz.isArray()) {
					throw new SLInvalidPersistentPropertyTypeException("Persistent property is not an array.");
				}
				value = getJCRPropertyValue();
			}
			return value;
		}
		catch (SLInvalidPersistentPropertyTypeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new SLPersistentTreeSessionException("Error on attempt to retrieve property value.", e);
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#setValue(java.io.Serializable)
	 */
	public void setValue(V value) throws SLPersistentTreeSessionException {
		try {
			Session session = jcrProperty.getSession();
			if (value.getClass().isArray()) {
				Value[] jcrValues = JCRUtil.createValues(session, value);
				jcrProperty.setValue(jcrValues);
			}
			else {
				Value jcrValue = JCRUtil.createValue(session, value);
				jcrProperty.setValue(jcrValue);
			}
			eventPoster.post(new SLPersistentPropertyEvent(SLPersistentPropertyEvent.TYPE_PROPERTY_SET, this));
		}
		catch (Exception e) {
			throw new SLPersistentTreeSessionException("Error on attempt to set persistent property.", e);
		}
	}
	
	/**
	 * Gets the jCR property value.
	 * 
	 * @return the jCR property value
	 * 
	 * @throws SLInvalidPersistentPropertyTypeException the SL invalid persistent property type exception
	 * @throws SLException the SL exception
	 * @throws RepositoryException the repository exception
	 */
	private V getJCRPropertyValue() throws SLInvalidPersistentPropertyTypeException, SLException, RepositoryException {
		V value = null;
		if (jcrProperty.getType() == PropertyType.LONG) {
			if (clazz.equals(Integer.class)) {
				Integer intValue = (int) jcrProperty.getValue().getLong();
				value = clazz.cast(intValue);
			}
			else if (clazz.isAssignableFrom(Long.class)) {
				value = clazz.cast(new Long(jcrProperty.getValue().getLong()));
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Integer.class, Long.class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.DOUBLE) {
			if (clazz.equals(Float.class)) {
				Float floatValue = (float) jcrProperty.getValue().getDouble();
				value = clazz.cast(floatValue);
			}
			else if (clazz.isAssignableFrom(Double.class)) {
				value = clazz.cast(new Double(jcrProperty.getValue().getDouble()));
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Float.class, Double.class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.BOOLEAN) {
			if (clazz.isAssignableFrom(Boolean.class)) {
				value = clazz.cast(new Boolean(jcrProperty.getBoolean()));
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Boolean.class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.STRING) {
			if (clazz.isAssignableFrom(String.class)) {
				value = clazz.cast(new String(jcrProperty.getString()));
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, String.class);
			}
		}
		else {
			Object object = SerializationUtil.deserialize(jcrProperty.getStream());
			if (clazz.isAssignableFrom(object.getClass())) {
				value = clazz.cast(object);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, object.getClass());
			}
		}
		return value;
	}
	
	/**
	 * Gets the jCR array property value.
	 * 
	 * @return the jCR array property value
	 * 
	 * @throws SLInvalidPersistentPropertyTypeException the SL invalid persistent property type exception
	 * @throws SLException the SL exception
	 * @throws RepositoryException the repository exception
	 */
	private V getJCRArrayPropertyValue() throws SLInvalidPersistentPropertyTypeException, SLException, RepositoryException {
		V value = null;
		Value[] jcrValues = jcrProperty.getValues();
		if (jcrProperty.getType() == PropertyType.LONG) {
			if (clazz.equals(Integer[].class)) {
				Integer[] intValues = new Integer[jcrValues.length];
				for (int i = 0; i < intValues.length; i++) {
					intValues[i] = (int) jcrValues[i].getLong();
				}
				value = clazz.cast(intValues);
			}
			else if (clazz.isAssignableFrom(Long[].class)) {
				Long[] longValues = new Long[jcrValues.length];
				for (int i = 0; i < longValues.length; i++) {
					longValues[i] = jcrValues[i].getLong();
				}
				value = clazz.cast(longValues);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Integer[].class, Long[].class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.DOUBLE) {
			if (clazz.equals(Float[].class)) {
				Float[] floatValues = new Float[jcrValues.length];
				for (int i = 0; i < floatValues.length; i++) {
					floatValues[i] = (float) jcrValues[i].getDouble();
				}
				value = clazz.cast(floatValues);
			}
			else if (clazz.isAssignableFrom(Double[].class)) {
				Double[] doubleValues = new Double[jcrValues.length];
				for (int i = 0; i < doubleValues.length; i++) {
					doubleValues[i] = jcrValues[i].getDouble();
				}
				value = clazz.cast(doubleValues);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Float[].class, Double[].class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.BOOLEAN) {
			if (clazz.isAssignableFrom(Boolean.class)) {
				Boolean[] booleanValues = new Boolean[jcrValues.length];
				for (int i = 0; i < booleanValues.length; i++) {
					booleanValues[i] = jcrValues[i].getBoolean();
				}
				value = clazz.cast(booleanValues);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, Boolean[].class);
			}
		}
		else if (jcrProperty.getType() == PropertyType.STRING) {
			if (clazz.isAssignableFrom(String.class)) {
				String[] stringValues = new String[jcrValues.length];
				for (int i = 0; i < stringValues.length; i++) {
					stringValues[i] = jcrValues[i].getString();
				}
				value = clazz.cast(stringValues);
			}
			else {
				throw new SLInvalidPersistentPropertyTypeException(getName(), clazz, String[].class);
			}
		}
		else {
			value = clazz.cast(Array.newInstance(clazz.getComponentType(), jcrValues.length));
			for (int i = 0; i < jcrValues.length; i++) {
				Object object = SerializationUtil.deserialize(jcrValues[i].getStream());
				if (clazz.getComponentType().isAssignableFrom(object.getClass())) {
					Array.set(value, i, object);
				}
				else {
					throw new SLInvalidPersistentPropertyTypeException("Error on attempt to set array position. Cannot cast " + 
						object.getClass() + " to " + clazz.getComponentType());
				}
			}
		}
		return value;
	}

	//@Override
	/* (non-Javadoc)
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#remove()
	 */
	public void remove() throws SLPersistentTreeSessionException {
		try {
			jcrProperty.remove();
			eventPoster.post(new SLPersistentPropertyEvent(SLPersistentPropertyEvent.TYPE_PROPERTY_REMOVED, this));
		}
		catch (RepositoryException e) {
			throw new SLPersistentTreeSessionException("Error on attempt to remove persistent property.", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	//@Override
	public boolean equals(Object obj) {
		try {
			if (obj == null) return false;
			SLPersistentProperty persistentProperty = (SLPersistentProperty) obj;
			String name1 = persistentProperty.getNode().getID() + ":" + getName();
			String name2 = getNode().getID() + ":" + getName();
			return name1.equals(name2);
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to execute persistent property equals method.");
		}
	}
	
	//@Override
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		try {
			return (getNode().getID() + ":" + getName()).hashCode();
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLRuntimeException("Error on attempt to calculate persistent property hash code.");
		}
	}
}
