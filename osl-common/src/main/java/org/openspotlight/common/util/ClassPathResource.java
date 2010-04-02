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

import org.openspotlight.common.exception.SLException;

import java.io.InputStream;

import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;

/**
 * Class for resource loading.
 *
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class ClassPathResource {

    /**
     * Loads a resource from the current classpath.
     *
     * @param artifactName
     * @return a input stream from classpath
     * @throws SLException
     */
    public static InputStream getResourceFromClassPath( final String artifactName )
        throws SLException {
        checkNotEmpty("location", artifactName); //$NON-NLS-1$
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader()
                                       .getResourceAsStream(artifactName);
            if (stream == null) {
                stream = ClassLoader.getSystemClassLoader()
                                    .getResourceAsStream(artifactName);
            }
            if (stream == null) {
                stream = ClassPathResource.class.getResourceAsStream(artifactName);
            }
            return stream;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }

    /**
     * Loads a resource from the current classpath.
     *
     * @param clasz class that defines the correct place to search for resource
     * @param resourceName resource name
     * @return a input stream from classpath
     * @throws SLException
     */
    public static InputStream getResourceFromClassPath( final Class<?> clasz,
                                                        final String resourceName )
        throws SLException {
        checkNotEmpty("location", resourceName); //$NON-NLS-1$
        try {
            InputStream stream = clasz.getResourceAsStream(resourceName);
            if (stream == null) {
                stream = ClassLoader.getSystemClassLoader()
                                    .getResourceAsStream(resourceName);
            }
            if (stream == null) {
                stream = ClassPathResource.class.getResourceAsStream(resourceName);
            }
            return stream;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }
}
