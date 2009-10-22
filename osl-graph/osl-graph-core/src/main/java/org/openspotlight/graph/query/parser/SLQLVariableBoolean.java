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
package org.openspotlight.graph.query.parser;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.graph.query.SLQLVariable;

/**
 * The Class SLQLVariableBoolean. This class is a boolean typed SLQLVariable.
 * 
 * @author porcelli
 */
public class SLQLVariableBoolean extends SLQLVariable {

    private static final long serialVersionUID = -9054981284364714022L;

    /** The domain value. */
    Set<Serializable>         domainValue      = new HashSet<Serializable>(2);

    /**
     * Instantiates a new sLQL variable boolean.
     * 
     * @param name the name
     */
    public SLQLVariableBoolean(
                                final String name ) {
        super(name);
        this.domainValue.add(true);
        this.domainValue.add(false);
    }

    /**
     * {@inheritDoc}
     */
    public void addDomainValue( final Serializable value ) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Serializable> getDomainValues() {
        return this.domainValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getValue() {
        return (Boolean)this.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDomainValues() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValidDomainValue( final Serializable value ) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValidValue( final Serializable value ) {
        if (value == null) {
            return false;
        }
        if (value.getClass().getName().equals(boolean.class.getName())) {
            return true;
        }
        if (value instanceof Boolean) {
            return true;
        }
        return false;
    }
}
