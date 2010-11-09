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
package org.openspotlight.domain;

import static com.google.common.collect.Maps.newHashMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.openspotlight.common.util.Arrays;
import org.openspotlight.common.util.Equals;
import org.openspotlight.common.util.HashCodes;
import org.openspotlight.persist.annotation.KeyProperty;
import org.openspotlight.persist.annotation.Name;
import org.openspotlight.persist.annotation.ParentProperty;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.annotation.TransientProperty;

// TODO: Auto-generated Javadoc

/**
 * The Class Group.
 */
@Name("group")
public class Group implements SimpleNodeType, Serializable, Schedulable {

    private static final long                     serialVersionUID = -722058711327567623L;

    /**
     * The active.
     */
    private boolean                               active;

    /** The artifact sources. */
    private Set<ArtifactSource>                   artifactSources  = new HashSet<ArtifactSource>();

    private List<String>                          cronInformation  = new ArrayList<String>();

    /**
     * The group.
     */
    private transient Group                       parent;

    private List<Group>                           groups           = new ArrayList<Group>();

    private volatile transient int                hashCode;

    /**
     * The name.
     */
    private String                                name;

    /**
     * The repository.
     */
    private transient Repository                  repository;

    private List<Class<? extends Callable<Void>>> tasks            = new ArrayList<Class<? extends Callable<Void>>>();

    private Map<String, String>                   properties       = newHashMap();

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * The type.
     */
    private String                    type;

    private volatile transient String uniqueName;

    public void acceptVisitor(final Repository.GroupVisitor visitor) {
        visitor.visitGroup(this);
        for (final Group g: getGroups()) {
            g.acceptVisitor(visitor);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Group)) { return false; }
        final Group that = (Group) o;
        final boolean result =
            Equals.eachEquality(Arrays.of(parent, repository, name), Arrays.andOf(that.parent, that.repository,
                that.name));
        return result;
    }

    public Set<ArtifactSource> getArtifactSources() {
        return artifactSources;
    }

    @Override
    public List<String> getCronInformation() {
        return cronInformation;
    }

    /**
     * Gets the group.
     * 
     * @return the group
     */
    @ParentProperty
    public Group getParent() {
        return parent;
    }

    public List<Group> getGroups() {
        return groups;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    @KeyProperty
    public String getName() {
        return name;
    }

    /**
     * Gets the repository.
     * 
     * @return the repository
     */
    @ParentProperty
    public Repository getRepository() {
        return repository;
    }

    @Override
    @TransientProperty
    public Repository getRepositoryForSchedulable() {
        return getRootRepository();
    }

    @TransientProperty
    public Repository getRootRepository() {
        return repository != null ? repository : getParent().getRootRepository();
    }

    public List<Class<? extends Callable<Void>>> getTasks() {
        return tasks;
    }

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public String getType() {
        return type;
    }

    public String getUniqueName() {
        String result = uniqueName;
        if (result == null) {
            result = (parent != null ? parent.getUniqueName() : repository.getName()) + "/" + getName();
            uniqueName = result;
        }
        return result;
    }

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            result = HashCodes.hashOf(parent, repository, name);
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

    public void setArtifactSources(final Set<ArtifactSource> artifactSources) {
        this.artifactSources = artifactSources;
    }

    public void setCronInformation(final List<String> cronInformation) {
        this.cronInformation = cronInformation;
    }

    /**
     * Sets the group.
     * 
     * @param group the new group
     */
    public void setParent(final Group group) {
        this.parent = group;
    }

    public void setGroups(final List<Group> groups) {
        this.groups = groups;
    }

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Sets the repository.
     * 
     * @param repository the new repository
     */
    public void setRepository(final Repository repository) {
        this.repository = repository;
    }

    public void setTasks(final List<Class<? extends Callable<Void>>> tasks) {
        this.tasks = tasks;
    }

    /**
     * Sets the type.
     * 
     * @param type the new type
     */
    public void setType(final String type) {
        this.type = type;
    }

    public void setUniqueName(final String s) {}

    @Override
    public String toString() {
        return "Group: " + getUniqueName();
    }

    @Override
    public String toUniqueJobString() {
        return getUniqueName();
    }

    public void addTask(Class<? extends Callable<Void>> task) {
        tasks.add(task);
    }

    public void addProperty(String name, String value) {
        properties.put(name, value);
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public void addArtifactSource(ArtifactSource artifactSource) {
        artifactSources.add(artifactSource);
    }

    public void addArtifactMapping(ArtifactSourceMapping sourceMapping) {
        // TODO Auto-generated method stub

    }

}
