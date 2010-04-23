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
package org.openspotlight.federation.finder;

import java.util.Set;

import org.openspotlight.common.Disposable;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ArtifactSource;

/**
 * This class persists the artifacts loaded from {@link OriginArtifactLoader} classes. So, this class unifies the {@link Artifact
 * artifacts} loaded from different sources into a single place.
 * 
 * @author feu
 */
public interface PersistentArtifactManager extends Disposable {

    /**
     * @return {@link PersistentArtifactInternalMethods} instance
     */
    public PersistentArtifactInternalMethods getInternalMethods();

    /**
     * This class have methods that should be used only on low level stuff.
     * 
     * @author feu
     */
    public interface PersistentArtifactInternalMethods {

        /**
         * @param <A>
         * @param type
         * @return true if the given type is supported
         */
        public <A extends Artifact> boolean isTypeSupported( Class<A> type );

        /**
         * This method returns the original names as it was before any mapping.
         * 
         * @param <A>
         * @param source
         * @param type
         * @return
         */
        public <A extends Artifact> Set<String> retrieveOriginalNames(
                                                                       ArtifactSource source,
                                                                       Class<A> type,
                                                                       String initialPath );

        /**
         * This method returns the current names as it is.
         * 
         * @param <A>
         * @param initialPath
         * @param type
         * @return
         */
        public <A extends Artifact> Set<String> retrieveNames( Class<A> type,
                                                               String initialPath );

        /**
         * This method finds an artifact by its name before any mapping.
         * 
         * @param <A>
         * @param source
         * @param type
         * @param originName
         * @return
         */
        public <A extends Artifact> A findByOriginalName( ArtifactSource source,
                                                          Class<A> type,
                                                          String originName );

        /**
         * @param <A>
         * @param source
         * @param type
         * @param originName
         * @return
         */
        public <A extends Artifact> Set<A> listByOriginalNames(
                                                                ArtifactSource source,
                                                                Class<A> type,
                                                                String originName );

    }

    /**
     * Adds an artifact to be saved later
     * 
     * @param <A>
     * @param artifact
     */
    public <A extends Artifact> void addTransient( A artifact );

    /**
     * Marks an artifact to be removed on next save
     * 
     * @param <A>
     * @param artifact
     */
    public <A extends Artifact> void markAsRemoved( A artifact );

    /**
     * find method
     * 
     * @param <A>
     * @param type
     * @param path
     * @return
     */
    public <A extends Artifact> A findByPath( Class<A> type,
                                              String path );

    /**
     * list method
     * 
     * @param <A>
     * @param type
     * @param path
     * @return
     */
    public <A extends Artifact> Set<A> listByPath( Class<A> type,
                                                   String path );

    /**
     * Saves or flush transient data to the persistent store
     */
    public void saveTransientData();

}
