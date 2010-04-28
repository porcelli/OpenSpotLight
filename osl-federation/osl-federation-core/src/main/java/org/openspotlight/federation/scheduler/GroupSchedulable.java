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
package org.openspotlight.federation.scheduler;

import java.util.LinkedHashSet;
import java.util.Set;

import org.openspotlight.federation.context.ExecutionContext;
import org.openspotlight.federation.context.ExecutionContextFactory;
import org.openspotlight.federation.domain.GlobalSettings;
import org.openspotlight.federation.domain.Group;
import org.openspotlight.federation.domain.Schedulable.SchedulableCommandWithContextFactory;
import org.openspotlight.federation.processing.DefaultBundleProcessorManager;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.persist.annotation.TransientProperty;
import org.openspotlight.persist.util.SimpleNodeTypeVisitor;
import org.openspotlight.persist.util.SimpleNodeTypeVisitorSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupSchedulable implements SchedulableCommandWithContextFactory<Group> {

    private static class GroupVisitor implements SimpleNodeTypeVisitor<Group> {

        private final Set<Group> groupsWithBundles = new LinkedHashSet<Group>();

        public Set<Group> getGroupsWithBundles() {
            return groupsWithBundles;
        }

        public void visitBean( Group bean ) {
            if (bean.getBundleTypes().size() != 0) {
                groupsWithBundles.add(bean);
                if (logger.isDebugEnabled()) {
                    logger.debug("adding group " + bean + " because it has " + bean.getBundleTypes().size() + "  bundles");
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("ignoring group " + bean + " because it has no bundles");
                }

            }
        }

    }

    private ExecutionContextFactory factory;

    private JcrConnectionDescriptor descriptor;
    private String                  username;
    private String                  password;
    private GlobalSettings          settings;

    private static final Logger     logger = LoggerFactory.getLogger(GroupSchedulable.class);

    @SuppressWarnings( "unchecked" )
    public void execute( final GlobalSettings settigns,
                         final ExecutionContext ctx,
                         final Group schedulable ) throws Exception {
        final GroupVisitor visitor = new GroupVisitor();
        SimpleNodeTypeVisitorSupport.acceptVisitorOn(Group.class, schedulable, visitor, TransientProperty.class);
        final Group[] groupsToExecute = visitor.getGroupsWithBundles().toArray(new Group[0]);
        if (logger.isDebugEnabled()) {
            logger.debug("about to execute bundles " + visitor.getGroupsWithBundles() + " found inside group " + schedulable);
        }
        DefaultBundleProcessorManager.INSTANCE.executeBundles(username, password, descriptor, factory, settings, groupsToExecute);

    }

    public String getRepositoryNameBeforeExecution( final Group schedulable ) {
        return schedulable.getRootRepository().getName();
    }

    public void setContextFactoryBeforeExecution( final GlobalSettings settings,
                                                  final JcrConnectionDescriptor descriptor,
                                                  final String username,
                                                  final String password,
                                                  final String repository,
                                                  final ExecutionContextFactory factory ) {
        this.descriptor = descriptor;
        this.username = username;
        this.password = password;
        this.factory = factory;
        this.settings = settings;
    }

}
