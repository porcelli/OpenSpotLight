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

import org.apache.commons.collections15.Predicate;
import org.openspotlight.structure.elements.SLElement;

/**
 * A predicate that compares if a given item is a descendant of a given class to compare to. The checkSubtype is used just to
 * allow exact class matching
 * 
 * @author Vinicius Carvalho
 */
@SuppressWarnings( "unchecked" )
public class ClassTypePredicate implements Predicate<SLElement> {
    private Class comparable;
    private boolean checkSubType = true;

    public ClassTypePredicate(
                               Class comparable, boolean checkSubtype ) {
        this.comparable = comparable;
        this.checkSubType = checkSubtype;
    }

    public boolean evaluate( SLElement item ) {
        boolean result = false;
        if (checkSubType) {
            result = comparable.isAssignableFrom(item.getClass());
        } else {
            result = comparable.equals(item.getClass());
        }
        return result;
    }
}
