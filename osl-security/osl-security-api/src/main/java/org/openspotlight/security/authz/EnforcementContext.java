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
package org.openspotlight.security.authz;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A simple, and generic, data class for policy check.
 * 
 * @author porcelli
 */
public class EnforcementContext {

    /** The attributes. */
    private Map<String, Object> attributes;

    /**
     * Instantiates a new enforcement context.
     */
    public EnforcementContext() {
        this.attributes = new HashMap<String, Object>();
    }

    /**
     * Gets the attribute.
     * 
     * @param name the name
     * @return the attribute
     */
    public Object getAttribute( String name ) {
        return this.attributes.get(name);
    }

    /**
     * Sets the attribute.
     * 
     * @param name the name
     * @param attribute the attribute
     */
    public void setAttribute( String name,
                              Object attribute ) {
        this.attributes.put(name, attribute);
    }

    /**
     * Gets the names.
     * 
     * @return the names
     */
    public Set<String> getNames() {
        return this.attributes.keySet();
    }

    /**
     * Gets the values.
     * 
     * @return the values
     */
    public Object[] getValues() {
        return this.attributes.values().toArray();
    }

    /**
     * Clear.
     * 
     * @param name the name
     */
    public void clear( String name ) {
        this.attributes.remove(name);
    }

    /**
     * Clear all.
     */
    public void clearAll() {
        this.attributes.clear();
    }
}
