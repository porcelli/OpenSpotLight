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
package org.openspotlight.bundle.language.java.asm.model;

// TODO: Auto-generated Javadoc
/**
 * Represents a array type that encloses other {@link TypeReference}. This class represents that the enclosed type is an array of
 * "N" dimensions.
 * 
 * @author porcelli
 */
public class ArrayTypeReference implements TypeReference {

    /** The array dimensions. */
    private int           arrayDimensions = -1;

    /** The enclosed type. */
    private TypeReference type            = null;

    /**
     * Instantiates a new array type reference.
     * 
     * @param arraySize the array size
     * @param type the type
     */
    public ArrayTypeReference(
                               int arraySize, TypeReference type ) {
        this.arrayDimensions = arraySize;
        this.type = type;
    }

    /**
     * Gets the array dimensions.
     * 
     * @return the array dimensions
     */
    public int getArrayDimensions() {
        return arrayDimensions;
    }

    /**
     * Sets the array dimensions.
     * 
     * @param arrayDimensions the new array dimensions
     */
    public void setArrayDimensions( int arrayDimensions ) {
        this.arrayDimensions = arrayDimensions;
    }

    /**
     * Gets the enclosed type.
     * 
     * @return the type
     */
    public TypeReference getType() {
        return type;
    }

    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType( TypeReference type ) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public String getFullName() {
        return type.getFullName() + getDimensions();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return type.getName() + getDimensions();
    }

    /**
     * Gets the dimensions in String format.
     * 
     * @return the dimensions
     */
    private String getDimensions() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrayDimensions; i++) {
            sb.append("[]");
        }
        return sb.toString();
    }
}
