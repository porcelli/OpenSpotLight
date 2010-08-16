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
package org.openspotlight.security;

import org.openspotlight.common.util.AbstractFactory;
import org.openspotlight.security.authz.PolicyEnforcement;
import org.openspotlight.security.idm.SystemUser;
import org.openspotlight.security.idm.User;
import org.openspotlight.security.idm.auth.IdentityManager;
import org.openspotlight.storage.StorageSession;

/**
 * A factory for creating Security related components.
 */
public abstract class SecurityFactory extends AbstractFactory {

    /** The Constant SYSTEM_USER_NAME. */
    static final String SYSTEM_USER_NAME = "sysuser";

    /**
     * Creates a new Identity Manager.
     * 
     * @param session
     * @return the identity manager
     */
    public abstract IdentityManager createIdentityManager(StorageSession session);

    /**
     * Creates a new specialized PolicyEnforcement for Graphs.
     * 
     * @param session
     * @return the policy enforcement
     */
    public abstract PolicyEnforcement createGraphPolicyEnforcement(StorageSession session);

    /**
     * Creates a System User.
     * 
     * @return the system user
     */
    public abstract SystemUser createSystemUser();

    /**
     * Creates a simple User based on its id.
     * 
     * @param id the id
     * @return the user
     */
    public abstract User createUser(String id);

}
