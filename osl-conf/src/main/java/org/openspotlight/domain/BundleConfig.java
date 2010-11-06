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
package org.openspotlight.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

/**
 * The Class BundleConfig.
 */
@Name("bundle_processor_type")
public class BundleConfig implements SimpleNodeType, Serializable {
    public List<Class<? extends Callable<Void>>> getTasks() {
        return tasks;
    }

    public void setTasks(List<Class<? extends Callable<Void>>> tasks) {
        this.tasks = tasks;
    }

    private volatile transient String uniqueName = null;

    private String name;

    private Map<String, String> bundleProperties = new HashMap<String, String>();

    private static final long serialVersionUID = -8305990807194729295L;

    private List<Class<? extends Callable<Void>>> tasks = new ArrayList<Class<? extends Callable<Void>>>();

    /**
     * The active.
     */
    private boolean active;

    /**
     * The group.
     */
    private transient Group group;

    /**
     * The hash code.
     */
    private volatile transient int hashCode;

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equalsTo(java.lang.Object)
     */

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BundleConfig)) {
            return false;
        }
        final BundleConfig that = (BundleConfig) o;
        final boolean result = Equals.eachEquality(Arrays.of(group, name), Arrays.andOf(that.group, that.name));
        return result;
    }

    public Map<String, String> getBundleProperties() {
        return bundleProperties;
    }

    /**
     * Gets the artifact source.
     *
     * @return the artifact source
     */
    @ParentProperty
    public Group getGroup() {
        return group;
    }

    @KeyProperty
    public String getName() {
        return name;
    }

    @TransientProperty
    public String getUniqueName() {
        String temp = uniqueName;
        if (temp == null) {
            temp = getGroup().getUniqueName() + "/" + getName();
            uniqueName = temp;
        }
        return temp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */

    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(group, name);
            hashCode = result;
        }
        return result;
    }

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active.
     *
     * @param active the new active
     */
    public void setActive(final boolean active) {
        this.active = active;
    }

    public void setBundleProperties(final Map<String, String> bundleProperties) {
        this.bundleProperties = bundleProperties;
    }

    /**
     * Sets the group.
     *
     * @param group the new group
     */
    public void setGroup(final Group group) {
        this.group = group;
    }

    public void setName(final String name) {
        this.name = name;
    }

}
