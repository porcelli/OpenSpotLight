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
package org.openspotlight.federation.processing.internal.domain;

import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Repository;
import org.openspotlight.federation.processing.CurrentProcessorContext;
import org.openspotlight.graph.SLContext;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLInvalidCredentialException;
import org.openspotlight.graph.SLNode;
import org.openspotlight.graph.SLNodeTypeNotInExistentHierarchy;

public class CurrentProcessorContextImpl implements CurrentProcessorContext {

    private Group      currentGroup;

    private SLNode     currentNodeGroup;

    private Repository currentRepository;

    private SLContext  groupContext;

    public Group getCurrentGroup() {
        return this.currentGroup;
    }

    public SLNode getCurrentNodeGroup()
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException, SLInvalidCredentialException {
        if (this.currentNodeGroup == null) {
            if (this.currentGroup != null && this.groupContext != null) {
                this.currentNodeGroup = this.getNodeForGroup(this.currentGroup);
            }
        }
        return this.currentNodeGroup;
    }

    public Repository getCurrentRepository() {
        return this.currentRepository;
    }

    public SLContext getGroupContext() {
        return this.groupContext;
    }

    public SLNode getNodeForGroup( final Group group )
        throws SLNodeTypeNotInExistentHierarchy, SLGraphSessionException, SLInvalidCredentialException {
        return this.groupContext.getRootNode().addNode(group.getUniqueName());
    }

    public void setCurrentGroup( final Group currentGroup ) {
        this.currentGroup = currentGroup;
    }

    public void setCurrentNodeGroup( final SLNode currentNodeGroup ) {
        this.currentNodeGroup = currentNodeGroup;
    }

    public void setCurrentRepository( final Repository currentRepository ) {
        this.currentRepository = currentRepository;
    }

    public void setGroupContext( final SLContext groupContext ) {
        this.groupContext = groupContext;
    }

}
