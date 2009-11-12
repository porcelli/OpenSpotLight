package org.openspotlight.federation.domain;

import java.util.HashMap;
import java.util.Map;

public enum RoutineParameterType {
    /**
     * Wasn't possible to discover its type.
     */
    UNKNOWN(0),
    /**
     * Input parameter.
     */
    IN(1),
    /**
     * Input/Output parameter.
     */
    INOUT(2),
    /**
     * Output parameter.
     */
    OUT(4),
    /**
     * Return value as described in {@link DatabaseMetaData#procedureColumnReturn}.
     */
    RETURN_VALUE(5),
    /**
     * Column result as described in {@link DatabaseMetaData#procedureColumnResult}.
     */
    RESULT_COLUMN(3);

    /**
     * Internal cache
     */
    private static final Map<Integer, RoutineParameterType> cache = new HashMap<Integer, RoutineParameterType>();
    static {
        for (final RoutineParameterType n : values()) {
            cache.put(n.getSqlTypeValue(), n);
        }
    }

    /**
     * Static factory method
     * 
     * @param sqlType
     * @return the correct RoutineParameter type by sql int constant
     */
    public static RoutineParameterType getTypeByInt( final int sqlType ) {
        return cache.get(sqlType);
    }

    private final int sqlTypeValue;

    private RoutineParameterType(
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
