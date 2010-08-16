/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.openspotlight.common.exception.AbstractFactoryException;

/**
 * A factory for creating Abstract objects.
 */
public abstract class AbstractFactory {

    /** The factory map. */
    private static Map<Class<? extends AbstractFactory>, AbstractFactory> factoryMap =
                                                                                         new HashMap<Class<? extends AbstractFactory>, AbstractFactory>();

    /**
     * Gets the default instance.
     * 
     * @param clazz the clazz
     * @return the default instance
     * @throws AbstractFactoryException the abstract factory exception
     */
    @SuppressWarnings("unchecked")
    public static <T extends AbstractFactory> T getDefaultInstance(final Class<T> clazz)
        throws AbstractFactoryException {
        T factory = null;
        try {
            factory = (T) factoryMap.get(clazz);
            if (factory == null) {
                final Properties props = loadProps(clazz);
                final String implClassName = props.getProperty("defaultImpl");
                final Class<? extends T> implClass = (Class<? extends T>) Class.forName(implClassName, true,
                                                                                       clazz.getClassLoader());
                factory = implClass.newInstance();
                factoryMap.put(clazz, factory);
            }
        } catch (final Exception e) {
            throw new AbstractFactoryException("Error on attempt to newPair the factory.", e);
        }
        return factory;
    }

    /**
     * Load props.
     * 
     * @param clazz the clazz
     * @return the properties
     * @throws AbstractFactoryException the abstract factory exception
     */
    private static Properties loadProps(final Class<?> clazz)
        throws AbstractFactoryException {
        final String resource = clazz.getName().replace('.', '/').concat(".properties");
        try {
            final InputStream inputStream = AbstractFactory.class.getClassLoader().getResourceAsStream(resource);
            final Properties props = new Properties();
            props.load(inputStream);
            return props;
        } catch (final IOException e) {
            throw new AbstractFactoryException("Error on attempt to load factory properties file " + resource, e);
        }
    }
}
