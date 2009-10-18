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
package org.openspotlight.graph.query;

import java.util.Collection;

import org.openspotlight.common.util.Exceptions;

/**
 * The Class SLQLVariable. This class represents a variable defined in slql external dsl.
 * 
 * @author porcelli
 */
/**
 * @author porcelli
 *
 */
public abstract class SLQLVariable {

    /** The variable name. */
    protected String name           = null;

    /** The display message. */
    protected String displayMessage = null;

    /** The value. */
    protected Object value          = null;

    /**
     * Instantiates a new SLQL variable.
     * 
     * @param name the name
     */
    public SLQLVariable(
                         String name ) {
        this.name = name;
        this.displayMessage = name;
    }

    /**
     * Gets the display message.
     * 
     * @return the display message
     */
    public String getDisplayMessage() {
        return displayMessage;
    }

    /**
     * Sets the display message.
     * 
     * @param displayMessage the new display message
     */
    public void setDisplayMessage( String displayMessage ) {
        this.displayMessage = displayMessage;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if (obj instanceof SLQLVariable) {
            return name.equalsIgnoreCase(((SLQLVariable)obj).getName());
        }
        return false;
    }

    /**
     * Sets the value.
     * 
     * @param value the new value
     */
    public void setValue( Object value ) {
        if (isValidValue(value)) {
            this.value = value;
        } else {
            Exceptions.logAndThrow(new IllegalArgumentException("Variable value invalid data type."));
        }
    }

    /**
     * Adds the all domain value.
     * 
     * @param values the values
     */
    public void addAllDomainValue( Collection<Object> values ) {
        for (Object activeValue : values) {
            addDomainValue(activeValue);
        }
    }

    /**
     * Adds the domain value.
     * 
     * @param value the value
     */
    public abstract void addDomainValue( Object value );

    /**
     * Checks for domain values.
     * 
     * @return true, if successful
     */
    public abstract boolean hasDomainValues();

    /**
     * Checks if is valid value.
     * 
     * @param value the value
     * @return true, if is valid value
     */
    public abstract boolean isValidValue( Object value );

    /**
     * Checks if is valid domain value.
     * 
     * @param value the value
     * 
     * @return true, if is valid domain value
     */
    public abstract boolean isValidDomainValue( Object value );

    /**
     * Gets the domain values.
     * 
     * @return the domain values
     */
    public abstract Collection<?> getDomainValues();

    /**
     * Gets the value.
     * 
     * @return the value
     */
    public abstract Object getValue();
}
