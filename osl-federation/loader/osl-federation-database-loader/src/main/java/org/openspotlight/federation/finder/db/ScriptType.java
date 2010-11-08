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
package org.openspotlight.federation.finder.db;

import java.util.Arrays;
import java.util.List;

import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.domain.artifact.db.ForeignKeyConstraintArtifact;
import org.openspotlight.federation.domain.artifact.db.RoutineArtifact;
import org.openspotlight.federation.domain.artifact.db.TableArtifact;
import org.openspotlight.federation.domain.artifact.db.ViewArtifact;

/**
 * Script types to be used inside the artifact loader implementation.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public enum ScriptType {
    /**
     * Constraint creation script.
     */
    CONSTRAINT(StringArtifact.class),
    /**
     * Foreign key information.
     */
    FK(StringArtifact.class, ForeignKeyConstraintArtifact.class),
    /**
     * Function type.
     */
    FUNCTION(StringArtifact.class, RoutineArtifact.class),
    /**
     * Index creation script.
     */
    INDEX(StringArtifact.class),
    /**
     * Package creation script.
     */
    PACKAGE(StringArtifact.class),
    /**
     * Procedure type.
     */
    PROCEDURE(StringArtifact.class, RoutineArtifact.class),
    /**
     * Sequence creation script.
     */
    SEQUENCE(StringArtifact.class),
    /**
     * Table creation script.
     */
    TABLE(StringArtifact.class, TableArtifact.class),
    /**
     * Tablespace creation script.
     */
    TABLESPACE(StringArtifact.class),
    /**
     * Trigger type.
     */
    TRIGGER(StringArtifact.class),
    /**
     * View creation script.
     */
    VIEW(StringArtifact.class, ViewArtifact.class);

    private ScriptType(
                        final Class<? extends Artifact>... classes) {
        types = Arrays.asList(classes);
    }

    private List<Class<? extends Artifact>> types;

    public boolean acceptType(final Class<? extends Artifact> type) {
        return types.contains(type);
    }

    public boolean acceptName(final String name) {
        return name != null && name.toLowerCase().contains(name().toLowerCase());
    }
}
