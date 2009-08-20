package org.openspotlight.federation.data.impl;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum for column types from {@link Types} constants.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("boxing")
public enum ColumnType {
    
    /**
     * Enum for sql type BIT.
     */
    BIT(Types.BIT),
    /**
     * Enum for sql type TINYINT.
     */
    TINYINT(Types.TINYINT),
    /**
     * Enum for sql type SMALLINT.
     */
    SMALLINT(Types.SMALLINT),
    /**
     * Enum for sql type INTEGER.
     */
    INTEGER(Types.INTEGER),
    /**
     * Enum for sql type BIGINT.
     */
    BIGINT(Types.BIGINT),
    /**
     * Enum for sql type FLOAT.
     */
    FLOAT(Types.FLOAT),
    /**
     * Enum for sql type REAL.
     */
    REAL(Types.REAL),
    /**
     * Enum for sql type DOUBLE.
     */
    DOUBLE(Types.DOUBLE),
    /**
     * Enum for sql type NUMERIC.
     */
    NUMERIC(Types.NUMERIC),
    /**
     * Enum for sql type DECIMAL.
     */
    DECIMAL(Types.DECIMAL),
    /**
     * Enum for sql type CHAR.
     */
    CHAR(Types.CHAR),
    /**
     * Enum for sql type VARCHAR.
     */
    VARCHAR(Types.VARCHAR),
    /**
     * Enum for sql type LONGVARCHAR.
     */
    LONGVARCHAR(Types.LONGVARCHAR),
    /**
     * Enum for sql type DATE.
     */
    DATE(Types.DATE),
    /**
     * Enum for sql type TIME.
     */
    TIME(Types.TIME),
    /**
     * Enum for sql type TIMESTAMP.
     */
    TIMESTAMP(Types.TIMESTAMP),
    /**
     * Enum for sql type BINARY.
     */
    BINARY(Types.BINARY),
    /**
     * Enum for sql type VARBINARY.
     */
    VARBINARY(Types.VARBINARY),
    /**
     * Enum for sql type LONGVARBINARY.
     */
    LONGVARBINARY(Types.LONGVARBINARY),
    /**
     * Enum for sql type NULL.
     */
    NULL(Types.NULL),
    /**
     * Enum for sql type OTHER.
     */
    OTHER(Types.OTHER),
    /**
     * Enum for sql type JAVA_OBJECT.
     */
    JAVA_OBJECT(Types.JAVA_OBJECT),
    /**
     * Enum for sql type DISTINCT.
     */
    DISTINCT(Types.DISTINCT),
    /**
     * Enum for sql type STRUCT.
     */
    STRUCT(Types.STRUCT),
    /**
     * Enum for sql type ARRAY.
     */
    ARRAY(Types.ARRAY),
    /**
     * Enum for sql type BLOB.
     */
    BLOB(Types.BLOB),
    /**
     * Enum for sql type CLOB.
     */
    CLOB(Types.CLOB),
    /**
     * Enum for sql type REF.
     */
    REF(Types.REF),
    /**
     * Enum for sql type DATALINK.
     */
    DATALINK(Types.DATALINK),
    /**
     * Enum for sql type BOOLEAN.
     */
    BOOLEAN(Types.BOOLEAN);
    
    /**
     * Internal cache
     */
    private static final Map<Integer, ColumnType> typesCache = new HashMap<Integer, ColumnType>();
    static {
        for (final ColumnType t : values()) {
            typesCache.put(t.getSqlTypeValue(), t);
        }
    }
    
    /**
     * Static factory method
     * 
     * @param sqlType
     * @return the correct column type by sql int constant
     */
    public static ColumnType getTypeByInt(final int sqlType) {
        return typesCache.get(sqlType);
    }
    
    private final int sqlTypeValue;
    
    private ColumnType(final int sqlTypeValue) {
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