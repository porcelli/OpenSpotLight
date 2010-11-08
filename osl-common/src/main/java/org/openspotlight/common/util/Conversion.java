/**
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

package org.openspotlight.common.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;

/**
 * Utility conversion class based on PrimitiveOrWrapperConverter from Dozer project http://dozer.sourceforge.net/
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class Conversion {

    /**
     * Internal map of types and converters.
     */
    private static final Map<Class<?>, Converter> CONVERTERS      = new HashMap<Class<?>, Converter>();

    private static final Map<String, Class<?>>    PRIMITIVE_TYPES = new HashMap<String, Class<?>>();

    /**
     * Should not be instantiated
     */
    private Conversion() {
        Exceptions.logAndThrow(new IllegalStateException(Messages.getString("invalidConstructor"))); //$NON-NLS-1$
    }

    static {
        Conversion.PRIMITIVE_TYPES.put("int", int.class);
        Conversion.PRIMITIVE_TYPES.put("double", double.class);
        Conversion.PRIMITIVE_TYPES.put("short", short.class);
        Conversion.PRIMITIVE_TYPES.put("char", char.class);
        Conversion.PRIMITIVE_TYPES.put("long", long.class);
        Conversion.PRIMITIVE_TYPES.put("boolean", boolean.class);
        Conversion.PRIMITIVE_TYPES.put("byte", byte.class);
        Conversion.PRIMITIVE_TYPES.put("float", float.class);
    }

    static {

        Conversion.CONVERTERS.put(Integer.class, new IntegerConverter());
        Conversion.CONVERTERS.put(Double.class, new DoubleConverter());
        Conversion.CONVERTERS.put(Short.class, new ShortConverter());
        Conversion.CONVERTERS.put(Character.class, new CharacterConverter());
        Conversion.CONVERTERS.put(Long.class, new LongConverter());
        Conversion.CONVERTERS.put(Boolean.class, new BooleanConverter());
        Conversion.CONVERTERS.put(Byte.class, new ByteConverter());
        Conversion.CONVERTERS.put(Float.class, new FloatConverter());
        Conversion.CONVERTERS.put(Date.class, new Converter() {

            @Override
            public Object convert(final Class type,
                                  final Object value) {
                try {
                    if (type.equals(Date.class) && value instanceof String
                            && ((String) value).isEmpty() == false) {
                        final String newValue = (String) value;
                        return new Date(Long.parseLong(newValue));
                    }
                } catch (final Exception e) {
                    throw Exceptions.logAndReturn(new IllegalStateException(e));
                }
                throw Exceptions.logAndReturn(new IllegalArgumentException());

            }
        });
        Conversion.CONVERTERS.put(String.class, new Converter() {

            @Override
            public Object convert(final Class type,
                                   final Object value) {
                try {
                    if (value == null) {
                        return null;
                    }
                    if (type.equals(String.class) && value instanceof Date) {
                        return Long.toString(((Date) value).getTime());
                    }
                    if (type.equals(String.class) && value instanceof Class) {
                        return ((Class) value).getName();
                    }
                    if (type.equals(String.class) && value instanceof byte[]) {
                        return new String((byte[]) value);
                    }
                    return value.toString();
                } catch (final Exception e) {
                    throw Exceptions.logAndReturn(new IllegalStateException(e));
                }

            }
        });

        Conversion.CONVERTERS.put(int.class, new IntegerConverter());
        Conversion.CONVERTERS.put(double.class, new DoubleConverter());
        Conversion.CONVERTERS.put(short.class, new ShortConverter());
        Conversion.CONVERTERS.put(char.class, new CharacterConverter());
        Conversion.CONVERTERS.put(long.class, new LongConverter());
        Conversion.CONVERTERS.put(boolean.class, new BooleanConverter());
        Conversion.CONVERTERS.put(byte.class, new ByteConverter());
        Conversion.CONVERTERS.put(float.class, new FloatConverter());

        Conversion.CONVERTERS.put(BigDecimal.class, new BigDecimalConverter());
        Conversion.CONVERTERS.put(BigInteger.class, new BigIntegerConverter());
        Conversion.CONVERTERS.put(Class.class, new Converter() {

            @Override
            public Object convert(final Class type,
                                   final Object value) {
                try {
                    if (type.equals(Class.class) && value instanceof String) {
                        String newValue = (String) value;
                        if (newValue.startsWith("class ")) {
                            newValue = Strings.removeBegginingFrom("class ", newValue);
                        }
                        return Class.forName(newValue);
                    }
                } catch (final Exception e) {
                    throw Exceptions.logAndReturn(new IllegalStateException(e));
                }
                throw Exceptions.logAndReturn(new IllegalArgumentException());

            }
        });
    }

    /**
     * Returns a new converted type based on target type parameter.
     * 
     * @param <E>
     * @param rawValue
     * @param targetType
     * @return a new value from a converted type
     * @throws SLException
     */
    @SuppressWarnings("unchecked")
    public static <E> E convert(final Object rawValue,
                                 final Class<E> targetType) {
        Assertions.checkNotNull("targetType", targetType); //$NON-NLS-1$
        Assertions
            .checkCondition(
                                  "validTargetType:" + targetType.getName(), Conversion.CONVERTERS.containsKey(targetType) || targetType.isEnum()); //$NON-NLS-1$
        if (rawValue == null) { return null; }

        try {
            if (targetType.isEnum()) {
                final String rawValueAsString = rawValue.toString();
                final Field[] flds = targetType.getDeclaredFields();
                for (final Field f: flds) {
                    if (f.isEnumConstant()) {
                        if (f.getName().equals(rawValueAsString)) {
                            final Object value = f.get(null);
                            return (E) value;

                        }
                    }
                }
                throw new IllegalStateException(MessageFormat.format("Invalid enum constant:{0} for type {1}", rawValueAsString,
                                                                     targetType));
            }
            final Converter converter = Conversion.CONVERTERS.get(targetType);
            final E converted = (E) converter.convert(targetType, rawValue);
            return converted;
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }
    }

    public static Class<?> getPrimitiveClass(final String name) {
        return Conversion.PRIMITIVE_TYPES.get(name);
    }
}
