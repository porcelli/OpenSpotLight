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
package org.openspotlight.common.util;

import java.io.InputStream;
import java.io.Serializable;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.openspotlight.common.exception.JCRUtilException;


/**
 * The Class JCRUtil.
 * 
 * @author Vitor Hugo Chagas
 */
public class JCRUtil {
	
	/**
	 * Gets the parent.
	 * 
	 * @param node the node
	 * 
	 * @return the parent
	 * 
	 * @throws RepositoryException the repository exception
	 */
	public static Node getParent(Node node) throws RepositoryException {
		Node parent = null;
		try {
			parent = node.getParent();
		}
		catch (ItemNotFoundException e) {}
		return parent;
	}
	
	/**
	 * Make versionable.
	 * 
	 * @param node the node
	 * 
	 * @throws RepositoryException the repository exception
	 */
	public static void makeVersionable(Node node) throws RepositoryException {
		node.addMixin("mix:versionable");
	}
	
	/**
	 * Make referenceable.
	 * 
	 * @param node the node
	 * 
	 * @throws RepositoryException the repository exception
	 */
	public static void makeReferenceable(Node node) throws RepositoryException {
		node.addMixin("mix:referenceable");
	}
	
	/**
	 * Gets the child node.
	 * 
	 * @param node the node
	 * @param name the name
	 * 
	 * @return the child node
	 * 
	 * @throws RepositoryException the repository exception
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
	 * Creates the value.
	 * 
	 * @param session the session
	 * @param value the value
	 * 
	 * @return the value
	 * 
	 * @throws JCRUtilException the JCR util exception
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
	 * Creates the values.
	 * 
	 * @param session the session
	 * @param value the value
	 * 
	 * @return the value[]
	 * 
	 * @throws JCRUtilException the JCR util exception
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
