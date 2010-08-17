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
package org.openspotlight.storage.domain.node;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openspotlight.storage.AbstractStorageSession;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Property;
import org.openspotlight.storage.domain.PropertyContainer;

import com.google.inject.internal.ImmutableSet;

public abstract class PropertyContainerImpl implements PropertyContainer {

    public void forceReload() {
        propertiesByName.clear();
        lastLoad = -1;
    }

    protected PropertyContainerImpl(final boolean resetTimeout) {
        lastLoad = resetTimeout ? -1 : System.currentTimeMillis();
    }

    private static final long           TIMEOUT          = 60 * 1000;

    protected long                      lastLoad;

    private final Map<String, Property> propertiesByName = new HashMap<String, Property>();

    @Override
    public Set<String> getPropertyNames(final StorageSession session) {
        reloadProperties(session);
        return ImmutableSet.copyOf(propertiesByName.keySet());
    }

    @Override
    public Set<Property> getProperties(final StorageSession session) {
        reloadProperties(session);
        return ImmutableSet.copyOf(propertiesByName.values());
    }

    protected void verifyBeforeSet(final String propertyName) {

    }

    @Override
    public Property setSimpleProperty(final StorageSession session,
                                       final String name,
                                       final String value) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStringValue(session, value);

        return currentProperty;
    }

    @Override
    public Property setSimpleProperty(final StorageSession session,
                                       final String name,
                                       final InputStream value) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStreamValue(session, value);

        return currentProperty;
    }

    @Override
    public Property setSimpleProperty(final StorageSession session,
                                       final String name,
                                       final byte[] value) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createSimple(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setBytesValue(session, value);

        return currentProperty;

    }

    @Override
    public Property setIndexedProperty(final StorageSession session,
                                        final String name,
                                        final String value) {
        verifyBeforeSet(name);
        Property currentProperty = getProperty(session, name);
        if (currentProperty == null) {
            currentProperty = PropertyImpl.createIndexed(name, this);
            propertiesByName.put(name, currentProperty);
        }
        currentProperty.setStringValue(session, value);

        return currentProperty;
    }

    private void reloadProperties(final StorageSession session) {
        final boolean tooOld = lastLoad < (System.currentTimeMillis() + TIMEOUT);
        final boolean empty = propertiesByName.isEmpty();
        if (tooOld && empty) {
            final Set<Property> result =
                ((AbstractStorageSession<?>) session).propertyContainerLoadProperties(this);
            for (final Property property: result) {
                propertiesByName.put(property.getPropertyName(), property);
            }
            lastLoad = System.currentTimeMillis();
        }
    }

    private void loadPropertiesOnce(final StorageSession session) {
        if (propertiesByName.isEmpty()) {
            reloadProperties(session);
        }
    }

    @Override
    public String getPropertyAsString(final StorageSession session,
                                       final String name) {
        final Property prop = getProperty(session, name);
        if (prop != null) { return prop.getValueAsString(session); }
        return null;
    }

    @Override
    public InputStream getPropertyAsStream(final StorageSession session,
                                            final String name) {

        final Property prop = getProperty(session, name);
        if (prop != null) { return prop.getValueAsStream(session); }
        return null;
    }

    @Override
    public byte[] getPropertyAsBytes(final StorageSession session,
                                      final String name) {

        final Property prop = getProperty(session, name);
        if (prop != null) { return prop.getValueAsBytes(session); }
        return null;
    }

    @Override
    public Property getProperty(final StorageSession session,
                                 final String name) {
        loadPropertiesOnce(session);
        Property result = propertiesByName.get(name);
        if (result == null) {
            reloadProperties(session);
            result = propertiesByName.get(name);
        }
        return result;
    }

}
