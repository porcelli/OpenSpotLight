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
package org.openspotlight.bundle.domain;

import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.federation.domain.artifact.db.DatabaseType;

public class DbArtifactSource extends ArtifactSource {
    private static final long serialVersionUID = -430683831296857466L;
    private String            serverName;
    private String            databaseName;
    private String            driverClass;

    private String            user;

    private String            password;

    private DatabaseType      type;

    private int               maxConnections;

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public String getPassword() {
        return password;
    }

    public String getServerName() {
        return serverName;
    }

    public DatabaseType getType() {
        return type;
    }

    public String getUser() {
        return user;
    }

    public void setDatabaseName( final String databaseName ) {
        this.databaseName = databaseName;
    }

    public void setDriverClass( final String driverClass ) {
        this.driverClass = driverClass;
    }

    public void setMaxConnections( final int maxConnections ) {
        this.maxConnections = maxConnections;
    }

    public void setPassword( final String password ) {
        this.password = password;
    }

    public void setServerName( final String serverName ) {
        this.serverName = serverName;
    }

    public void setType( final DatabaseType type ) {
        this.type = type;
    }

    public void setUser( final String user ) {
        this.user = user;
    }
}
