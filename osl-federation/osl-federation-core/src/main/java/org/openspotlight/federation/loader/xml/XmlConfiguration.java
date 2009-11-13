package org.openspotlight.federation.loader.xml;

import java.util.HashSet;
import java.util.Set;

import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Repository;

public class XmlConfiguration {

    private GlobalSettings  settings;

    private Set<Repository> repositories = new HashSet<Repository>();

    public Set<Repository> getRepositories() {
        return this.repositories;
    }

    public GlobalSettings getSettings() {
        return this.settings;
    }

    public void setRepositories( final Set<Repository> repositories ) {
        this.repositories = repositories;
    }

    public void setSettings( final GlobalSettings settings ) {
        this.settings = settings;
    }

}
