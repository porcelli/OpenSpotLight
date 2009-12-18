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

import org.openspotlight.common.concurrent.Lock;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.SerializationUtil;
import org.openspotlight.jcr.util.JCRUtil;

/**
 * The Class SLPersistentPropertyImpl.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLPersistentPropertyImpl<V extends Serializable> implements
		SLPersistentProperty<V> {

	private final Lock lock;

	/** The persistent node. */
	private final SLPersistentNode persistentNode;

	/** The jcr property. */
	private final Property jcrProperty;

	/** The clazz. */
	private final Class<V> clazz;

	/** The event poster. */
	private final SLPersistentEventPoster eventPoster;

	/**
	 * Instantiates a new sL persistent property impl.
	 * 
	 * @param persistentNode
	 *            the persistent node
	 * @param clazz
	 *            the clazz
	 * @param jcrProperty
	 *            the jcr property
	 * @param loadValue
	 *            the load value
	 * @param eventPoster
	 *            the event poster
	 * 
	 * @throws SLPersistentPropertyNotFoundException
	 *             the SL persistent property not found exception
	 * @throws SLPersistentTreeSessionException
	 *             the SL persistent tree session exception
	 * @throws RepositoryException
	 */
	public SLPersistentPropertyImpl(final SLPersistentNode persistentNode,
			final Class<V> clazz, final Property jcrProperty,
			final boolean loadValue, final SLPersistentEventPoster eventPoster)
			throws SLPersistentPropertyNotFoundException,
			SLPersistentTreeSessionException, RepositoryException {
		this.persistentNode = persistentNode;
		this.clazz = clazz;
		this.jcrProperty = jcrProperty;
		this.eventPoster = eventPoster;
		this.lock = persistentNode.getLockObject();
		if (loadValue) {
			this.getValue();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	// @Override
	public boolean equals(final Object obj) {
		synchronized (this.lock) {

			try {
				if (obj == null) {
					return false;
				}
				final SLPersistentProperty persistentProperty = (SLPersistentProperty) obj;
				final String name1 = persistentProperty.getNode().getID() + ":"
						+ this.getName();
				final String name2 = this.getNode().getID() + ":"
						+ this.getName();
				return name1.equals(name2);
			} catch (final SLPersistentTreeSessionException e) {
				throw new SLRuntimeException(
						"Error on attempt to execute persistent property equals method.");
			}
		}
	}

	/**
	 * Gets the jCR array property value.
	 * 
	 * @return the jCR array property value
	 * 
	 * @throws SLInvalidPersistentPropertyTypeException
	 *             the SL invalid persistent property type exception
	 * @throws SLException
	 *             the SL exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private V getJCRArrayPropertyValue()
			throws SLInvalidPersistentPropertyTypeException, SLException,
			RepositoryException {
		V value = null;

		final Value[] jcrValues = this.jcrProperty.getValues();
		if (this.jcrProperty.getType() == PropertyType.LONG) {
			if (this.clazz.equals(Integer[].class)) {
				final Integer[] intValues = new Integer[jcrValues.length];
				for (int i = 0; i < intValues.length; i++) {
					intValues[i] = (int) jcrValues[i].getLong();
				}
				value = this.clazz.cast(intValues);
			} else if (this.clazz.isAssignableFrom(Long[].class)) {
				final Long[] longValues = new Long[jcrValues.length];
				for (int i = 0; i < longValues.length; i++) {
					longValues[i] = jcrValues[i].getLong();
				}
				value = this.clazz.cast(longValues);
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, Integer[].class, Long[].class);
			}
		} else if (this.jcrProperty.getType() == PropertyType.DOUBLE) {
			if (this.clazz.equals(Float[].class)) {
				final Float[] floatValues = new Float[jcrValues.length];
				for (int i = 0; i < floatValues.length; i++) {
					floatValues[i] = (float) jcrValues[i].getDouble();
				}
				value = this.clazz.cast(floatValues);
			} else if (this.clazz.isAssignableFrom(Double[].class)) {
				final Double[] doubleValues = new Double[jcrValues.length];
				for (int i = 0; i < doubleValues.length; i++) {
					doubleValues[i] = jcrValues[i].getDouble();
				}
				value = this.clazz.cast(doubleValues);
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, Float[].class, Double[].class);
			}
		} else if (this.jcrProperty.getType() == PropertyType.BOOLEAN) {
			if (this.clazz.isAssignableFrom(Boolean.class)) {
				final Boolean[] booleanValues = new Boolean[jcrValues.length];
				for (int i = 0; i < booleanValues.length; i++) {
					booleanValues[i] = jcrValues[i].getBoolean();
				}
				value = this.clazz.cast(booleanValues);
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, Boolean[].class);
			}
		} else if (this.jcrProperty.getType() == PropertyType.STRING) {
			if (this.clazz.isAssignableFrom(String.class)) {
				final String[] stringValues = new String[jcrValues.length];
				for (int i = 0; i < stringValues.length; i++) {
					stringValues[i] = jcrValues[i].getString();
				}
				value = this.clazz.cast(stringValues);
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, String[].class);
			}
		} else {
			value = this.clazz.cast(Array.newInstance(this.clazz
					.getComponentType(), jcrValues.length));
			for (int i = 0; i < jcrValues.length; i++) {
				final Object object = SerializationUtil
						.deserialize(jcrValues[i].getStream());
				if (this.clazz.getComponentType().isAssignableFrom(
						object.getClass())) {
					Array.set(value, i, object);
				} else {
					throw new SLInvalidPersistentPropertyTypeException(
							"Error on attempt to set array position. Cannot cast "
									+ object.getClass() + " to "
									+ this.clazz.getComponentType());
				}
			}
		}
		return value;
	}

	/**
	 * Gets the jCR property value.
	 * 
	 * @return the jCR property value
	 * 
	 * @throws SLInvalidPersistentPropertyTypeException
	 *             the SL invalid persistent property type exception
	 * @throws SLException
	 *             the SL exception
	 * @throws RepositoryException
	 *             the repository exception
	 */
	private V getJCRPropertyValue()
			throws SLInvalidPersistentPropertyTypeException, SLException,
			RepositoryException {
		V value = null;

		if (this.jcrProperty.getType() == PropertyType.LONG) {
			if (this.clazz.equals(Integer.class)) {
				final Integer intValue = (int) this.jcrProperty.getValue()
						.getLong();
				value = this.clazz.cast(intValue);
			} else if (this.clazz.isAssignableFrom(Long.class)) {
				value = this.clazz.cast(new Long(this.jcrProperty.getValue()
						.getLong()));
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, Integer.class, Long.class);
			}
		} else if (this.jcrProperty.getType() == PropertyType.DOUBLE) {
			if (this.clazz.equals(Float.class)) {
				final Float floatValue = (float) this.jcrProperty.getValue()
						.getDouble();
				value = this.clazz.cast(floatValue);
			} else if (this.clazz.isAssignableFrom(Double.class)) {
				value = this.clazz.cast(new Double(this.jcrProperty.getValue()
						.getDouble()));
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, Float.class, Double.class);
			}
		} else if (this.jcrProperty.getType() == PropertyType.BOOLEAN) {
			if (this.clazz.isAssignableFrom(Boolean.class)) {
				value = this.clazz.cast(new Boolean(this.jcrProperty
						.getBoolean()));
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, Boolean.class);
			}
		} else if (this.jcrProperty.getType() == PropertyType.STRING) {
			if (this.clazz.isAssignableFrom(String.class)) {
				value = this.clazz
						.cast(new String(this.jcrProperty.getString()));
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, String.class);
			}
		} else {
			final Object object = SerializationUtil
					.deserialize(this.jcrProperty.getStream());
			if (this.clazz.isAssignableFrom(object.getClass())) {
				value = this.clazz.cast(object);
			} else {
				throw new SLInvalidPersistentPropertyTypeException(this
						.getName(), this.clazz, object.getClass());
			}
		}
		return value;
	}

	public Lock getLockObject() {
		return this.lock;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#getName()
	 */
	public String getName() throws SLPersistentTreeSessionException {
		synchronized (this.lock) {
			try {

				return this.jcrProperty.getName();

			} catch (final RepositoryException e) {
				throw new SLPersistentTreeSessionException(
						"Error on attempt to retrieve persistent property name.",
						e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#getNode()
	 */
	public SLPersistentNode getNode() {
		return this.persistentNode;
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#getValue()
	 */
	public V getValue() throws SLInvalidPersistentPropertyTypeException,
			SLPersistentTreeSessionException {
		synchronized (this.lock) {
			try {

				V value = null;
				// if the property value is an array ...
				if (this.jcrProperty.getDefinition().isMultiple()) {
					if (!this.clazz.isArray()) {
						throw new SLInvalidPersistentPropertyTypeException(
								"Persistent property is an array.");
					}
					value = this.getJCRArrayPropertyValue();
				}
				// if the property value is not an array ...
				else if (!this.jcrProperty.getDefinition().isMultiple()) {
					if (this.clazz.isArray()) {
						throw new SLInvalidPersistentPropertyTypeException(
								"Persistent property is not an array.");
					}
					value = this.getJCRPropertyValue();
				}
				return value;
			} catch (final SLInvalidPersistentPropertyTypeException e) {
				throw e;
			} catch (final Exception e) {
				throw new SLPersistentTreeSessionException(
						"Error on attempt to retrieve property value.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		synchronized (this.lock) {
			try {

				return (this.getNode().getID() + ":" + this.getName())
						.hashCode();

			} catch (final SLPersistentTreeSessionException e) {
				throw new SLRuntimeException(
						"Error on attempt to calculate persistent property hash code.");
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openspotlight.graph.persistence.SLPersistentProperty#remove()
	 */
	public void remove() throws SLPersistentTreeSessionException {
		synchronized (this.lock) {
			try {

				this.jcrProperty.remove();
				this.eventPoster.post(new SLPersistentPropertyEvent(
						SLPersistentPropertyEvent.TYPE_PROPERTY_REMOVED, this));

			} catch (final RepositoryException e) {
				throw new SLPersistentTreeSessionException(
						"Error on attempt to remove persistent property.", e);
			}
		}
	}

	// @Override
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.openspotlight.graph.persistence.SLPersistentProperty#setValue(java
	 * .io.Serializable)
	 */
	public void setValue(final V value) throws SLPersistentTreeSessionException {
		synchronized (this.lock) {
			try {
				final Session session = this.jcrProperty.getSession();
				if (value.getClass().isArray()) {
					final Value[] jcrValues = JCRUtil.createValues(session,
							value);
					this.jcrProperty.setValue(jcrValues);
				} else {
					final Value jcrValue = JCRUtil.createValue(session, value);
					this.jcrProperty.setValue(jcrValue);
				}
				this.eventPoster.post(new SLPersistentPropertyEvent(
						SLPersistentPropertyEvent.TYPE_PROPERTY_SET, this));
			} catch (final Exception e) {
				throw new SLPersistentTreeSessionException(
						"Error on attempt to set persistent property.", e);
			}
		}
	}
}
