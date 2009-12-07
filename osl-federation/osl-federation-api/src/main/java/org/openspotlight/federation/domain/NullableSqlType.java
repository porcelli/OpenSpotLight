package org.openspotlight.federation.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL nullable type. The int values came from {@link DatabaseMetaData}
 * javadoc. Theres some constant fields to describe nullability, but it
 * wasn't exported.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("boxing")
public enum NullableSqlType {
    
    /**
     * Can not be null.
     */
    NOT_NULL(0),
    /**
     * Can be null.
     */
    NULL(1),
    /**
     * Wasn't possible to find if it is nullable or not
     */
    DONT_KNOW(2);
    
    /**
     * Internal cache
     */
    private static final Map<Integer, NullableSqlType> nullableCache = new HashMap<Integer, NullableSqlType>();
    static {
        for (final NullableSqlType n : values()) {
            nullableCache.put(n.getSqlTypeValue(), n);
        }
    }
    
    /**
     * Static factory method
     * 
     * @param sqlType
     * @return the correct column type by sql int constant
     */
    public static NullableSqlType getNullableByInt(final int sqlType) {
        return nullableCache.get(sqlType);
    }
    
    private final int sqlTypeValue;
    
    private NullableSqlType(final int sqlTypeValue) {
        this.sqlTypeValue = sqlTypeValue;
    }
    
    /**
     * 
     * @return the int value equivalent to {@link Types} constants
     */
    public int getSqlTypeValue() {
        return this.sqlTypeValue;
    }
    
}