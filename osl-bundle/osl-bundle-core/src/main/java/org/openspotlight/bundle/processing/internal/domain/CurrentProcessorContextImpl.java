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
package org.openspotlight.bundle.processing.internal.domain;

import org.openspotlight.common.collection.AddOnlyConcurrentMap;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.federation.domain.artifact.ArtifactSource;
import org.openspotlight.bundle.domain.BundleProcessorType;
import org.openspotlight.bundle.domain.Group;
import org.openspotlight.bundle.domain.Repository;
import org.openspotlight.bundle.processing.CurrentProcessorContext;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.GraphReaderpotlight.graph.Nodemport java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CurrentProcessorContextImpl implements CurrentProcessorContext {

    private BundleProcessorType       bundleProcessor;

    private Group                     currentGroup;

    private ArtifactSource            artifactSource;

    private NoNoNode            currentNodeGroup;

    private Repository                currentRepository;

    private SLContext                 groupContext;

    private final Map<String, Object> transientProperties       = new AddOnlyConcurrentMap<String, Object>(
                                                                                                           new ConcurrentHashMap<String, Object>());

    private NodeNodeNode        nodeForUniqueBundleConfig = null;

    public CurrentProcessorContextImpl() {
    }

    public ArtifactSource getArtifactSource() {
        return artifactSource;
    }

    public BundleProcessorType getBundleProcessor() {
        return bundleProcessor;
    }

    public Map<String, String> getBundleProperties() {
        return bundleProcessor.getBundleProperties();
    }

    public Group getCurrentGroup() {
        return currentGroup;
    }

    public Node gNodeenNoderoup() {
        if (currentNodeGroup == null) {
            if (currentGroup != null && groupContext != null) {
                currentNodeGroup = getNodeForGroup(currentGroup);
            } else {
                Exceptions.logAndReturn(new IllegalStateException("currentGroup=" + currentGroup + " / " + "groupContext="
                                                                  + groupContext + " - anyone can't be null"));
            }
        }
        return currentNodeGroup;
    }

    public Repository getCurrentRepository() {
        return currentRepository;
    }

    public SLContext getGroupContext() {
        return groupContext;
    }

    public Node getNoderGroNodenal Group group ) {
        return groupContext.getRootNode().addChildNode(group.getUniqueName());
    }

    public Node getNoNodeniqueBNodeonfig() {

        if (nodeForUniqueBundleConfig == null) {
            synchronized (groupContext.getLockObject()) {
                try {
                    if (currentGroup != null && groupContext != null) {
                        final GraphReadGraphReadGraphReader                       sess.save();
                        final SLContext context = sess.createContext(bundleProcessor.getUniqueName().replaceAll("([ ]|[/]|[.])",
                                                                                                                "-"));
                        sess.save();
                        nodeForUniqueBundleConfig = context.getRootNode();
                    } else {
                        Exceptions.logAndReturn(new IllegalStateException("currentGroup=" + currentGroup + " / "
                                                                          + "groupContext=" + groupContext
                                                                          + " - anyone can't be null"));
                    }
                } catch (final Exception e) {
                    throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
                }

            }

        }
        return nodeForUniqueBundleConfig;
    }

    public Map<String, Object> getTransientProperties() {
        return transientProperties;
    }

    public void setArtifactSource( final ArtifactSource artifactSource ) {
        this.artifactSource = artifactSource;
    }

    public void setBundleProcessor( final BundleProcessorType bundleProcessor ) {
        this.bundleProcessor = bundleProcessor;
    }

    public void setCurrentGroup( final Group currentGroup ) {
        this.currentGroup = currentGroup;
    }

    public void setCurrentRepository( final Repository currentRepository ) {
        this.currentRepository = currentRepository;
    }

    public void setGroupContext( final SLContext groupContext ) {
        this.groupContext = groupContext;
    }

}
