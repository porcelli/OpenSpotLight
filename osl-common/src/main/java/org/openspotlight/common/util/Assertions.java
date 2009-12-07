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

import static org.openspotlight.common.util.Exceptions.logAndReturn;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;

/**
 * Helper class for parameters validation, such as not null arguments. This class uses {@link Messages} for i18n.
 *
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class Assertions {

    /**
     * Assert that this parameter is marked as valid by the condition passed as parameter.
     *
     * @param name of parameter
     * @param condition itself
     */
    public static void checkCondition( final String name,
                                       final boolean condition ) {
        if (!condition) {
            throw logAndReturn(new IllegalStateException(
                                                         MessageFormat.format(
                                                                              Messages.getString("Assertions.illegalCondition"), name))); //$NON-NLS-1$
        }
    }

    /**
     * Assert that this parameter is not null, as also each item of the array is not null.
     *
     * @param <T>
     * @param name of parameter
     * @param parameters itself
     */
    public static <T> void checkEachParameterNotNull( final String name,
                                                      final T... parameters ) {
        if (parameters == null) {
            throw logAndReturn(new IllegalArgumentException(
                                                            MessageFormat.format(
                                                                                 Messages.getString("Assertions.notNullMandatory"), name))); //$NON-NLS-1$
        }
        for (final Object parameter : parameters) {
            if (parameter == null) {
                throw logAndReturn(new IllegalArgumentException(
                                                                MessageFormat.format(
                                                                                     Messages.getString("Assertions.notNullMandatory"), name))); //$NON-NLS-1$

            }
        }

    }

    /**
     * Assert that this parameter is not empty. It will test for null and also the size of this array.
     *
     * @param name of parameter
     * @param parameter itself
     */
    public static void checkNotEmpty( final String name,
                                      final Collection<?> parameter ) {
        if (parameter == null || parameter.size() == 0) {
            throw logAndReturn(new IllegalArgumentException(
                                                            MessageFormat.format(
                                                                                 Messages.getString("Assertions.notEmptyMandatory"), name))); //$NON-NLS-1$
        }
    }

    /**
     * Assert that this parameter is not empty. It will test for null and also the size of this array.
     *
     * @param name of parameter
     * @param parameter itself
     */
    public static void checkNotEmpty( final String name,
                                      final Map<?, ?> parameter ) {
        if (parameter == null || parameter.size() == 0) {
            throw logAndReturn(new IllegalArgumentException(
                                                            MessageFormat.format(
                                                                                 Messages.getString("Assertions.notEmptyMandatory"), name))); //$NON-NLS-1$
        }
    }

    /**
     * Assert that this parameter is not empty. It trims the parameter to see if have any valid data on that.
     *
     * @param name of parameter
     * @param parameter itself
     */
    public static void checkNotEmpty( final String name,
                                      final String parameter ) {
        if (parameter == null || parameter.trim().length() == 0) {
            throw logAndReturn(new IllegalArgumentException(
                                                            MessageFormat.format(
                                                                                 Messages.getString("Assertions.notEmptyMandatory"), name))); //$NON-NLS-1$
        }
    }

    /**
     * Assert that this parameter is not empty. It will test for null and also the size of this array.
     *
     * @param <T> type of the array
     * @param name of parameter
     * @param parameter itself
     */
    public static <T> void checkNotEmpty( final String name,
                                          final T[] parameter ) {
        if (parameter == null || parameter.length == 0) {
            throw logAndReturn(new IllegalArgumentException(
                                                            MessageFormat.format(
                                                                                 Messages.getString("Assertions.notEmptyMandatory"), name))); //$NON-NLS-1$
        }
    }

    /**
     * Assert that this parameter is not null.
     *
     * @param name of parameter
     * @param parameter itself
     */
    public static void checkNotNull( final String name,
                                     final Object parameter ) {
        if (parameter == null) {
            throw logAndReturn(new IllegalArgumentException(
                                                            MessageFormat.format(
                                                                                 Messages.getString("Assertions.notNullMandatory"), name))); //$NON-NLS-1$
        }
    }

    /**
     * Assert that this parameter is null.
     *
     * @param name of parameter
     * @param parameter itself
     */
    public static void checkNullMandatory( final String name,
                                           final Object parameter ) {
        if (parameter != null) {
            throw logAndReturn(new IllegalArgumentException(
                                                            MessageFormat.format(
                                                                                 Messages.getString("Assertions.nullMandatory"), name))); //$NON-NLS-1$
        }
    }

    /**
     * Should not be instantiated
     */
    private Assertions() {
        throw logAndReturn(new IllegalStateException(Messages.getString("invalidConstructor"))); //$NON-NLS-1$
    }

}
