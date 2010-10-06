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
package org.openspotlight.web.command;

import net.sf.json.JSONObject;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.bundle.context.ExecutionContext;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;
import org.openspotlight.bundle.scheduler.Scheduler;
import org.openspotlight.federation.util.AggregateVisitor;
import org.openspotlight.federation.util.RepositorySet;
import org.openspotlight.persist.util.SimpleNodeTypeVisitorSupport;
import org.openspotlight.web.MessageWebException;
import org.openspotlight.web.OslContextListener;
import org.openspotlight.web.WebException;
import org.openspotlight.web.json.Message;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Class ImediateBundleProcessingWebCommand.
 */
public class ImediateBundleProcessingWebCommand implements WebCommand {

    /**
     * {@inheritDoc}
     */
    public String execute( final ExecutionContext context,
                           final Map<String, String> parameters ) throws WebException {
        try {
            final Iterable<Repository> allRepositories = context.getDefaultConfigurationManager().getAllRepositories();
            final RepositorySet repositorySet = new RepositorySet();
            repositorySet.setRepositories(allRepositories);
            final Scheduler scheduler = OslContextListener.scheduler;
            final Set<Group> groups = new HashSet<Group>();
            SimpleNodeTypeVisitorSupport.acceptVisitorOn(Group.class, repositorySet, new AggregateVisitor<Group>(groups));
            scheduler.fireSchedulable(context.getUserName(), context.getPassword(), groups.toArray(new Group[0]));
            final Message message = new Message();
            message.setMessage("execution fired");
            return JSONObject.fromObject(message).toString();
        } catch (final Exception e) {
            Exceptions.catchAndLog(e);
            throw new MessageWebException("There's something wrong during the firing action: " + e.getMessage());
        }

    }
}
