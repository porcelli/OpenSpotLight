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

package org.openspotlight.common.util.test;

import org.junit.Test;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.util.Exceptions;

import static org.openspotlight.common.util.Exceptions.*;

/**
 * Test class for {@link Exceptions}
 *
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 *
 */
@SuppressWarnings("all")
public class ExceptionsTest {

    public void dangerousMethod() throws Exception {
        throw new ConfigurationException("Am I going to be thrown?"); //$NON-NLS-1$
    }

    @Test
    public void shouldCatchException() throws Exception {
        try {
            this.dangerousMethod();
        } catch (final Exception e) {
            catchAndLog(e);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCatchExceptionAndThrowAnotherKind() throws Exception {
        try {
            this.dangerousMethod();
        } catch (final Exception e) {
            logAndThrowNew(e, IllegalArgumentException.class);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCatchExceptionAndThrowAnotherKindThatReturned()
            throws Exception {
        try {
            this.dangerousMethod();
        } catch (final Exception e) {
            throw logAndReturnNew(e, IllegalArgumentException.class);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCatchExceptionAndThrowAnotherKindThatReturnedWithMessage()
            throws Exception {
        try {
            this.dangerousMethod();
        } catch (final Exception e) {
            throw logAndReturnNew("it was so dangerous!", e, //$NON-NLS-1$
                    IllegalArgumentException.class);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCatchExceptionAndThrowAnotherKindWithMessage()
            throws Exception {
        try {
            this.dangerousMethod();
        } catch (final Exception e) {
            logAndThrowNew("it was so dangerous!", e, //$NON-NLS-1$
                    IllegalArgumentException.class);
        }
    }

    @Test(expected = ConfigurationException.class)
    public void shouldCatchExceptionAndThrowTheSame()
            throws ConfigurationException {
        try {
            this.dangerousMethod();
        } catch (final Exception e) {
            logAndThrowNew(e, ConfigurationException.class);
        }
    }

    @Test(expected = ConfigurationException.class)
    public void shouldCatchExceptionAndThrowTheSameThatReturned()
            throws Exception {
        try {
            this.dangerousMethod();
        } catch (final Exception e) {
            throw logAndReturn(e);
        }
    }

}
