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

import java.util.HashSet;
import java.util.Set;

import org.openspotlight.federation.domain.Artifact;
import org.openspotlight.federation.domain.ChangeType;

/**
 * The Class ArtifactFinderSupport.
 */
public class ArtifactFinderSupport {

    /**
     * Apply difference on existents.
     * 
     * @param existents the existents
     * @param newOnes the new ones
     * @return the set< t>
     */
    public static <T extends Artifact> Set<T> applyDifferenceOnExistents( final Set<T> existents,
                                                                          final Set<T> newOnes ) {
        final Set<T> result = new HashSet<T>();
        final Set<T> delta = new HashSet<T>(newOnes);
        for (final T existent : existents) {
            final T newOne = findTheEquivalent(existent, newOnes);
            delta.remove(newOne);
            if (newOne != null) {
                final ChangeType defaultChangeType = newOne.contentEquals(existent) ? ChangeType.NOT_CHANGED : ChangeType.CHANGED;
                switch (existent.getChangeType()) {
                    case INCLUDED:
                        newOne.setChangeType(ChangeType.INCLUDED);
                        break;
                    case EXCLUDED:
                        if (!ChangeType.EXCLUDED.equals(newOne.getChangeType())) {
                            newOne.setChangeType(ChangeType.CHANGED);
                        }
                        break;
                    default:
                        newOne.setChangeType(defaultChangeType);
                }
                result.add(newOne);
            } else {
                existent.setChangeType(ChangeType.EXCLUDED);
                result.add(existent);
            }
        }
        for (final T newOne : delta) {
            newOne.setChangeType(ChangeType.INCLUDED);
        }
        result.addAll(delta);
        existents.clear();
        existents.addAll(result);
        return existents;
    }

    /**
     * Find the equivalent.
     * 
     * @param artifact the artifact
     * @param setWithEquivalent the set with equivalent
     * @return the t
     */
    public static <T extends Artifact> T findTheEquivalent( final T artifact,
                                                            final Set<T> setWithEquivalent ) {
        for (final T equivalent : setWithEquivalent) {
            if (equivalent.getArtifactCompleteName().equals(artifact.getArtifactCompleteName())) {
                return equivalent;
            }
        }
        return null;
    }

    /**
     * Freeze changes after bundle processing. This will remove all artifacts marked as excluded and change all other status to
     * not changed.
     * 
     * @param existents the existents
     */
    public static <T extends Artifact> void freezeChangesAfterBundleProcessing( final Set<T> existents ) {
        final Set<T> toBeRemoved = new HashSet<T>();
        for (final T t : existents) {
            if (ChangeType.EXCLUDED.equals(t.getChangeType())) {
                toBeRemoved.add(t);
            } else {
                t.setChangeType(ChangeType.NOT_CHANGED);
            }
        }
        existents.removeAll(toBeRemoved);
    }
}
