package org.openspotlight.federation.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum to describe routine types. Its int values was taken from {@link DatabaseMetaData}.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@SuppressWarnings( "boxing" )
public enum RoutineType {
    /**
     * Type to describe a routine without return values from the routine itself. But it should have multiple out parameters.
     */
    PROCEDURE(1),
    /**
     * Type to descibe a routine with return value, but without out parameters.
     */
    FUNCTION(2),
    /**
     * Type to describe a routine on a situation that wasn't possible to retrieve the routine type.
     */
    DONT_KNOW(0);

    /**
     * Internal cache
     */
    private static final Map<Integer, RoutineType> cache = new HashMap<Integer, RoutineType>();
    static {
        for (final RoutineType n : values()) {
            cache.put(n.getSqlTypeValue(), n);
        }
    }

    /**
     * Static factory method
     * 
     * @param sqlType
     * @return the correct RoutineParameter type by sql int constant
     */
    public static RoutineType getTypeByInt( final int sqlType ) {
        return cache.get(sqlType);
    }

    private final int sqlTypeValue;

    private RoutineType(
                         final int sqlTypeValue ) {
        this.sqlTypeValue = sqlTypeValue;
    }

    /**
     * @return the int value equivalent to {@link Types} constants
     */
    public int getSqlTypeValue() {
        return this.sqlTypeValue;
    }
}
