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
package org.openspotlight.graph.exception;

/**
 * The Class SLInvalidNodePropertyTypeException.
 * 
 * @author Vitor Hugo Chagas
 */
public class SLInvalidNodePropertyTypeException extends SLGraphSessionException {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instantiates a new sL invalid node property type exception.
	 * 
	 * @param name the name
	 * @param invalidType the invalid type
	 * @param allowedTypes the allowed types
	 */
	public SLInvalidNodePropertyTypeException(String name, Class<?> invalidType, Class<?>... allowedTypes) {
		super(getMessage(name, invalidType, allowedTypes));
	}

	/**
	 * Instantiates a new sL invalid node property type exception.
	 * 
	 * @param message the message
	 */
	public SLInvalidNodePropertyTypeException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new sL invalid node property type exception.
	 * 
	 * @param cause the cause
	 */
	public SLInvalidNodePropertyTypeException(Throwable cause) {
		super(cause);
	}

	/**
	 * Gets the message.
	 * 
	 * @param name the name
	 * @param invalidType the invalid type
	 * @param allowedTypes the allowed types
	 * 
	 * @return the message
	 */
	private static String getMessage(String name, Class<?> invalidType, Class<?>... allowedTypes) {
		StringBuilder message = new StringBuilder();
		message.append("Value of property ")
			.append(name).append(" cannot be retrieved as ")
			.append(invalidType.getName()).append(". ");
		for (int i = 0; i < allowedTypes.length; i++) {
			if (i > 0) message.append(", ");
			message.append(allowedTypes[i].getName());
		}
		message.append(" or super type can be used instead.");
		return message.toString();
	}
}
