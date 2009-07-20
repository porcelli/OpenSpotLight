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
import org.openspotlight.structure.elements.SLSimpleType;

/**
 * This class uses an Example as a comparator for items it iterates through.
 * 
 * @author Vinicius Carvalho
 * 
 * @see Example
 */
public class ExamplePredicate implements Predicate<org.openspotlight.structure.elements.SLSimpleType> {
    private Example example;

    public ExamplePredicate(
                             Example example ) {
        this.example = example;
    }

    public boolean evaluate( SLSimpleType item ) {
        return example.evaluate(item);
    }
}
