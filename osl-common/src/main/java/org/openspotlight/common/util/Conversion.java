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
 * OpenSpotLight - Plataforma de Governan�a de TI de C�digo Aberto 
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA 
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta 
 * @author ou por expressa atribui��o de direito autoral declarada e atribu�da pelo autor.
 * Todas as contribui��es de terceiros est�o distribu�das sob licen�a da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. 
 * 
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo sob os 
 * termos da Licen�a P�blica Geral Menor do GNU conforme publicada pela Free Software 
 * Foundation. 
 * 
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM NENHUMA 
 * GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU ADEQUA��O A UMA
 * FINALIDADE ESPEC�FICA. Consulte a Licen�a P�blica Geral Menor do GNU para mais detalhes.  
 * 
 * Voc� deve ter recebido uma c�pia da Licen�a P�blica Geral Menor do GNU junto com este
 * programa; se n�o, escreva para: 
 * Free Software Foundation, Inc. 
 * 51 Franklin Street, Fifth Floor 
 * Boston, MA  02110-1301  USA
 */

package org.openspotlight.common.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.openspotlight.common.exception.SLException;

/**
 * Utility conversion class based on PrimitiveOrWrapperConverter from Dozer
 * project http://dozer.sourceforge.net/
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class Conversion {
    
    private static final Map<Class<?>, Converter> CONVERTERS = new HashMap<Class<?>, Converter>();
    
    static {
        CONVERTERS.put(Date.class, new DateConverter());
        CONVERTERS.put(Integer.class, new IntegerConverter());
        CONVERTERS.put(Double.class, new DoubleConverter());
        CONVERTERS.put(Short.class, new ShortConverter());
        CONVERTERS.put(Character.class, new CharacterConverter());
        CONVERTERS.put(Long.class, new LongConverter());
        CONVERTERS.put(Boolean.class, new BooleanConverter());
        CONVERTERS.put(Byte.class, new ByteConverter());
        CONVERTERS.put(String.class, new StringConverter());
        CONVERTERS.put(Float.class, new FloatConverter());
        
        CONVERTERS.put(int.class, new IntegerConverter());
        CONVERTERS.put(double.class, new DoubleConverter());
        CONVERTERS.put(short.class, new ShortConverter());
        CONVERTERS.put(char.class, new CharacterConverter());
        CONVERTERS.put(long.class, new LongConverter());
        CONVERTERS.put(boolean.class, new BooleanConverter());
        CONVERTERS.put(byte.class, new ByteConverter());
        CONVERTERS.put(float.class, new FloatConverter());
        
        CONVERTERS.put(BigDecimal.class, new BigDecimalConverter());
        CONVERTERS.put(BigInteger.class, new BigIntegerConverter());
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
    public static <E> E convert(final Object rawValue, final Class<E> targetType)
            throws SLException {
        checkNotNull("rawValue", rawValue); //$NON-NLS-1$
        checkNotNull("targetType", targetType); //$NON-NLS-1$
        checkCondition("validTargetType", CONVERTERS.containsKey(targetType)); //$NON-NLS-1$
        try {
            final Converter converter = CONVERTERS.get(targetType);
            final E converted = (E) converter.convert(targetType, rawValue);
            return converted;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }
    
    /**
     * Should not be instantiated
     */
    private Conversion() {
        logAndThrow(new IllegalStateException(Messages
                .getString("invalidConstructor"))); //$NON-NLS-1$
    }
}