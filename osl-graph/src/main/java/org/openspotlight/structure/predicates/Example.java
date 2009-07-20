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

import java.text.Collator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.PropertyUtils;
import org.openspotlight.structure.elements.SLSimpleType;

/**
 * This class was inspired by the Hibernate Example API, and the Query By Example term. An Example uses a target object to create
 * criterias to compare two objects. This class is used by the ExamplePredicate in order to perform comparisons between two
 * SLSimpleType items.
 * 
 * By default the example ignores all empty (null, zero lenth strings and zero numbers) properties. It also does not compare
 * Collections, Maps or java.lang.Class types.
 * 
 * For example:
 * 
 * <pre>
 * SLSimpleType type = new SLSimpleType();
 * type.setHandle(2L);
 * Example.createExample(type);
 * </pre>
 * 
 * would create an example that only performs comparisons on the handle property. To allow other properties to be compared, for
 * instance contextHandle, you must inform it, since the default value for those properties are 0.
 * 
 * <pre>
 * SLSimpleType type = new SLSimpleType();
 * type.setHandle(2L);
 * Example.createExample(type).allowEmpty(&quot;contextHandle&quot;);
 * </pre>
 * 
 * @author Vinicius Carvalho
 * 
 * @see ExamplePredicate
 */
public class Example {
    private final SLSimpleType example;
    private Set<String> ignoreProperties = new HashSet<String>();
    private Set<String> allowEmpty = new HashSet<String>();
    private Set<String> targetProperties = new HashSet<String>();
    private Collator collator;
    private boolean checkSubType = true;

    /**
     * The properties enlisted here are going to be ignored during comparison of objects
     * 
     * @param properties
     * @return
     */
    public Example ignoreProperties( String... properties ) {
        for (String s : properties) {
            ignoreProperties.add(s);
        }
        return this;
    }

    /**
     * Properties enlisted here are going to allow empty values, 0 for numbers and zero size length strings
     * 
     * @param properties
     * @return
     */
    public Example allowEmpty( String... properties ) {
        for (String s : properties) {
            allowEmpty.add(s);
        }
        return this;
    }

    /**
     * Allows empty for all properties
     * 
     * @return
     */
    public Example allowEmpty() {
        allowEmpty.addAll(targetProperties);
        return this;
    }

    /**
     * This property enables the use of descendants of the given target class
     * 
     * @param flag
     * @return
     */
    public Example useSubType( boolean flag ) {
        this.checkSubType = flag;
        return this;
    }

    /**
     * Use the specified parameter to perform a test that returns true or false.
     * 
     * @param compare the object to evaluate
     * @return true or false
     */
    public boolean evaluate( SLSimpleType compare ) {
        boolean result = true;
        if (checkSubType) {
            if (!example.getClass().isAssignableFrom(compare.getClass())) {
                return false;
            }
        } else {
            if (!example.getClass().equals(compare.getClass())) {
                return false;
            }
        }
        try {
            for (String s : targetProperties) {
                if (this.ignoreProperties.contains(s)) {
                    continue;
                }
                Object property = PropertyUtils.getProperty(example, s);
                if (!isEmpty(property, s) && !(property instanceof Collection) && !(property instanceof Map)
                    && !s.equals("class")) {
                    Object targetProperty = PropertyUtils.getProperty(compare, s);
                    result = result && compare(property, targetProperty);
                    if (!result) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not perform comparison", e);
        }
        return result;
    }

    /**
     * Creates a new example based on a target SLSimpleType
     * 
     * @param example
     * @return
     */
    public static Example create( SLSimpleType example ) {
        return new Example(example);
    }

    @SuppressWarnings( "unchecked" )
    private Example(
                     SLSimpleType example ) {
        this.example = example;
        this.collator = Collator.getInstance();
        try {
            this.targetProperties = PropertyUtils.describe(example).keySet();
        } catch (Exception e) {
            throw new RuntimeException("Could not describe target object", e);
        }
    }

    /**
     * Compare object, in case of String objects, it uses Collator to compare.
     * 
     * @see Collator
     * @param source
     * @param target
     * @return true or false
     */
    private boolean compare( Object source,
                             Object target ) {
        boolean result = false;
        if (!(source instanceof String)) {
            result = source.equals(target);
        } else {
            collator.setStrength(this.example.getCollatorLevel());
            result = (collator.compare(source, target) == 0);
        }
        return result;
    }

    @SuppressWarnings( "unchecked" )
    private boolean isEmpty( Object o,
                             String propertyName ) {
        boolean result = false;
        if (o == null) result = true;
        if (o instanceof Number) {
            result = this.allowEmpty.contains(propertyName) ? false : ((Number)o).intValue() == 0;
        }
        if (o instanceof String) {
            result = this.allowEmpty.contains(propertyName) ? false : ((String)o).trim().length() == 0;
        }
        if (o instanceof Collection) {
            result = ((Collection)o).size() == 0;
        }
        if (o instanceof Map) {
            result = ((Map)o).size() == 0;
        }
        return result;
    }
}
