/*
 * Copyright (c) 2008, Alexandre Porcelli or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors. All third-party contributions are
 * distributed under license by Alexandre Porcelli.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 */
package org.openspotlight.structure.predicates;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections15.Predicate;

/**
 * Checks if propertyName of the collection to be compared has the propertyValue
 * 
 * @author Vinicius Carvalho
 */
@SuppressWarnings( "unchecked" )
public class SimplePropertyPredicate implements Predicate {
    private Object propertyValue;
    private String propertyName;

    public SimplePropertyPredicate(
                                    String propertyName, Object propertyValue ) {
        this.propertyName = propertyName;
        this.propertyValue = propertyValue;
    }

    public boolean evaluate( Object item ) {
        boolean result = false;
        try {
            Object compareTo = PropertyUtils.getProperty(item, propertyName);
            result = propertyValue.equals(compareTo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
