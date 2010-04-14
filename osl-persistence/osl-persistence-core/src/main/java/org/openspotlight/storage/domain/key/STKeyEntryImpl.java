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
 * ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * *
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

package org.openspotlight.storage.domain.key;

import java.io.Serializable;

import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Compare.npeSafeCompare;
import static org.openspotlight.common.util.Reflection.findClassWithoutPrimitives;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 10:42:37 AM
 */
public class STKeyEntryImpl<T extends Serializable> implements STKeyEntry<T> {

    public static <T extends Serializable> STKeyEntry<T> create(Class<T> type, T value, String propertyName) {
        return new STKeyEntryImpl<T>(type, value, propertyName);
    }


    private STKeyEntryImpl(Class<T> type, T value, String propertyName) {
        checkNotNull("type", type);
        checkNotEmpty("propertyName", propertyName);
        this.type = (Class<T>)findClassWithoutPrimitives(type);
        this.value = value;
        this.propertyName = propertyName;
    }

    private final Class<T> type;

    private final T value;

    private final String propertyName;

    public Class<T> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public String getPropertyName() {
        return propertyName;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        STKeyEntryImpl that = (STKeyEntryImpl) o;

        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        return result;
    }

    public int compareTo(STKeyEntry o) {
        int result = npeSafeCompare(propertyName, o.getPropertyName());
        if (result != 0) return result;
        result = type.getName().compareTo(o.getType().getName());
        if (result != 0) return result;
        result = npeSafeCompare(value, o.getValue());
        return result;
    }

    @Override
    public String toString() {
        return "STKeyEntryImpl{" +
                "type=" + type +
                ", value=" + value +
                ", propertyName='" + propertyName + '\'' +
                '}';
    }


}

