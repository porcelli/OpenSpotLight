/*
 * OpenSpotLight - Open Source IT Governance Platform Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA or third-party contributors as indicated by the @author tags or express copyright attribution statements applied by the
 * authors. All third-party contributions are distributed under license by CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA
 * LTDA. This copyrighted material is made available to anyone wishing to use, modify, copy, or redistribute it subject to the
 * terms and conditions of the GNU Lesser General Public License, as published by the Free Software Foundation. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received a
 * copy of the GNU Lesser General Public License along with this distribution; if not, write to: Free Software Foundation, Inc. 51
 * Franklin Street, Fifth Floor Boston, MA 02110-1301 USA**********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA
 * E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de terceiros
 * estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é software livre;
 * você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme publicada pela Free
 * Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA GARANTIA; nem mesmo a
 * garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU
 * para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este programa; se não,
 * escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA 02110-1301 USA
 */
package org.openspotlight.security.idm.store;

import org.jboss.identity.idm.common.exception.IdentityException;
import org.jboss.identity.idm.spi.store.IdentityStoreSession;
import org.openspotlight.common.exception.SLRuntimeException;
import org.openspotlight.common.util.Exceptions;
import org.openspotlight.persist.annotation.SimpleNodeType;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.storage.StorageSession;
import org.openspotlight.storage.domain.Node;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SLIdentityStoreSessionImpl implements IdentityStoreSession {
    private final SimplePersistCapable<Node, StorageSession> simplePersist;

    public Node getRootNode() {
        return rootNode;
    }

    public SimplePersistCapable<Node, StorageSession> getSimplePersist() {
        return simplePersist;
    }

    private final SLIdentityStoreSessionContext context = new SLIdentityStoreSessionContext(this);

    private final Node                          rootNode;

    @Inject
    public SLIdentityStoreSessionImpl(
                                       final SimplePersistCapable<Node, StorageSession> simplePersist) {
        this.simplePersist = simplePersist;
        rootNode =
            simplePersist
                .getCurrentSession()
                .withPartition(simplePersist.getCurrentPartition())
                .createNewSimpleNode(
                                                                                                                                 "security");
        this.simplePersist.getCurrentSession().flushTransient();
    }

    public void addNode(final SimpleNodeType node)
        throws Exception {
        simplePersist.convertBeanToNode(rootNode, node);
        simplePersist.getCurrentSession().flushTransient();
    }

    @Override
    public void clear()
        throws IdentityException {}

    @Override
    public void close()
        throws IdentityException {

    }

    @Override
    public void commitTransaction() {}

    @Override
    public Object getSessionContext()
        throws IdentityException {
        return context;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public boolean isTransactionActive() {
        return false;
    }

    @Override
    public boolean isTransactionSupported() {
        return false;
    }

    public void remove(final SimpleNodeType bean)
        throws Exception {
        final Node asNode = simplePersist.convertBeanToNode(rootNode, bean);
        simplePersist.getCurrentSession().removeNode(asNode);
        simplePersist.getCurrentSession().flushTransient();

    }

    @Override
    public void rollbackTransaction() {}

    @Override
    public void save()
        throws IdentityException {
        try {

            simplePersist.getCurrentSession().flushTransient();
        } catch (final Exception e) {
            throw Exceptions.logAndReturnNew(e, SLRuntimeException.class);
        }

    }

    @Override
    public void startTransaction() {

    }

}
