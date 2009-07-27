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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to deal with exceptions, and also to log then.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class Exceptions {
    
    private static final Logger logger = LoggerFactory
            .getLogger(Exceptions.class);
    
    /**
     * Just catch and log the exception.
     * 
     * <pre>
     * try {
     *     somethingDangerous();
     * } catch (Exception e) {
     *     catchAndLog(e);
     * }
     * </pre>
     * 
     * @param exception
     */
    public static void catchAndLog(final Exception exception) {
        logger.error(exception.getMessage(), exception);
    }
    
    /**
     * Just catch and log the exception.
     * 
     * <pre>
     * try {
     *     somethingDangerous();
     * } catch (Exception e) {
     *     catchAndLog(e);
     * }
     * </pre>
     * 
     * @param message
     * 
     * @param exception
     */
    public static void catchAndLog(final String message,
            final Exception exception) {
        logger.error(message, exception);
    }
    
    /**
     * Log the exception and return the same exception to be used in non void
     * methods.
     * 
     * <pre>
     * try {
     *     somethingDangerous();
     * } catch (Exception e) {
     *     throw logAndReturn(e);
     * }
     * </pre>
     * 
     * @param <E>
     * @param exception
     * @return the same exception
     */
    public static <E extends Throwable> E logAndReturn(final E exception) {
        logger.error(exception.getMessage(), exception);
        return exception;
    }
    
    /**
     * Log the exception and return a new exception on the described class. To
     * be used on non void methods.
     * 
     * <pre>
     * try {
     *     somethingDangerous();
     * } catch (Exception e) {
     *     throw logAndReturnNew(e, MyCheckedException.class);
     * }
     * </pre>
     * 
     * @param <E>
     * @param baseException
     * @param newExceptionClass
     *            that has a constructor to wrap the original exception
     * @return a new exception wich type is the same as passed on parameter
     */
    @SuppressWarnings("unchecked")
    public static <E extends Exception> E logAndReturnNew(
            final Exception baseException, final Class<E> newExceptionClass) {
        logger.error(baseException.getMessage(), baseException);
        if (baseException.getClass().equals(newExceptionClass)) {
            return (E) baseException;
        }
        E newException;
        try {
            newException = newExceptionClass.getDeclaredConstructor(
                    Throwable.class).newInstance(baseException);
        } catch (final Exception e) {
            logger.error(Messages.getString("Exceptions.internalError"), e);//$NON-NLS-1$
            throw new RuntimeException(e);
        }
        return newException;
    }
    
    /**
     * Log the exception and return a new exception on the described class. To
     * be used on non void methods.
     * 
     * <pre>
     * try {
     *     somethingDangerous();
     * } catch (Exception e) {
     *     throw logAndReturnNew(&quot;it was so dangerous&quot;, e, MyCheckedException.class);
     * }
     * </pre>
     * 
     * @param <E>
     * @param message
     * @param baseException
     * @param newExceptionClass
     *            that has a constructor to wrap the original exception
     * @return a new exception wich type is the same as passed on parameter
     */
    public static <E extends Exception> E logAndReturnNew(final String message,
            final Exception baseException, final Class<E> newExceptionClass) {
        logger.error(message, baseException);
        E newException;
        try {
            newException = newExceptionClass.getDeclaredConstructor(
                    String.class, Throwable.class).newInstance(message,
                    baseException);
        } catch (final Exception e) {
            logger.error(Messages.getString("Exceptions.internalError"), e);//$NON-NLS-1$
            throw new RuntimeException(e);
        }
        return newException;
    }
    
    /**
     * Log the exception and throw the same exception on a void method.
     * 
     * <pre>
     * try {
     *     somethingDangerous();
     * } catch (Exception e) {
     *     logAndThrow(e);
     * }
     * </pre>
     * 
     * @param <E>
     * @param exception
     * @throws E
     */
    public static <E extends Throwable> void logAndThrow(final E exception)
            throws E {
        logger.error(exception.getMessage(), exception);
        throw exception;
    }
    
    /**
     * Log the exception and throw a new exception on the described class. To be
     * used on void methods.
     * 
     * <pre>
     * try {
     *     somethingDangerous();
     * } catch (Exception e) {
     *     logAndThrowNew(e, MyCheckedException.class);
     * }
     * </pre>
     * 
     * @param <E>
     * @param baseException
     * @param newExceptionClass
     *            that has a constructor to wrap the original exception
     * @throws E
     *             the new exception
     */
    @SuppressWarnings("unchecked")
    public static <E extends Exception> void logAndThrowNew(
            final Exception baseException, final Class<E> newExceptionClass)
            throws E {
        logger.error(baseException.getMessage(), baseException);
        if (baseException.getClass().equals(newExceptionClass)) {
            throw (E) baseException;
        }
        E newException;
        try {
            newException = newExceptionClass.getDeclaredConstructor(
                    Throwable.class).newInstance(baseException);
        } catch (final Exception e) {
            logger.error(Messages.getString("Exceptions.internalError"), e);//$NON-NLS-1$
            throw new RuntimeException(e);
        }
        throw newException;
    }
    
    /**
     * Log the exception and throw a new exception on the described class. To be
     * used on void methods.
     * 
     * <pre>
     * try {
     *     somethingDangerous();
     * } catch (Exception e) {
     *     logAndThrowNew(&quot;it was so dangerous&quot;, e, MyCheckedException.class);
     * }
     * </pre>
     * 
     * @param <E>
     * @param message
     * @param baseException
     * @param newExceptionClass
     *            that has a constructor to wrap the original exception
     * @throws E
     *             the new exception
     */
    public static <E extends Exception> void logAndThrowNew(
            final String message, final Exception baseException,
            final Class<E> newExceptionClass) throws E {
        logger.error(message, baseException);
        E newException;
        try {
            newException = newExceptionClass.getDeclaredConstructor(
                    String.class, Throwable.class).newInstance(message,
                    baseException);
        } catch (final Exception e) {
            logger.error(Messages.getString("Exceptions.internalError"), e);//$NON-NLS-1$
            throw new RuntimeException(e);
        }
        throw newException;
    }
    
    /**
     * Should not be instantiated
     */
    private Exceptions() {
        throw new IllegalStateException(Messages
                .getString("invalidConstructor")); //$NON-NLS-1$
    }
    
}
