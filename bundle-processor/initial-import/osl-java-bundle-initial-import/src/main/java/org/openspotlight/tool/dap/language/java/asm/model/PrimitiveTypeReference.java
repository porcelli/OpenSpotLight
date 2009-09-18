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
package org.openspotlight.tool.dap.language.java.asm.model;

/**
 * Represents a reference to a primitive type.
 * 
 * @author porcelli
 */
public class PrimitiveTypeReference implements TypeReference {

    /**
     * The Enum that represents all the availables primitive types.
     * 
     * @author porcelli
     */
    public enum PrimitiveType {

        /** The BOOLEAN type. */
        BOOLEAN,

        /** The CHAR type. */
        CHAR,

        /** The BYTE type. */
        BYTE,

        /** The SHORT type. */
        SHORT,

        /** The INT type. */
        INT,

        /** The FLOAT type. */
        FLOAT,

        /** The LONG type. */
        LONG,

        /** The DOUBLE type. */
        DOUBLE,

        /** The VOID type. */
        VOID;

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        public String toString() {
            return name().toLowerCase();
        }
    }

    /** The type. */
    private PrimitiveType type = null;

    /**
     * Instantiates a new primitive type reference.
     * 
     * @param type the type
     */
    public PrimitiveTypeReference(
                             PrimitiveType type ) {
        this.type = type;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public PrimitiveType getType() {
        return type;
    }

    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType( PrimitiveType type ) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see org.openspotlight.tool.dap.language.java.asm.model.TypeRef#getFullName()
     */
    public String getFullName() {
        return getName();
    }

    /* (non-Javadoc)
     * @see org.openspotlight.tool.dap.language.java.asm.model.TypeRef#getName()
     */
    public String getName() {
        return type.toString();
    }
}
