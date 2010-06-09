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
 * ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 * *
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

package org.openspotlight.storage.mongodb;

import org.openspotlight.storage.AbstractSTStorageSession;
import org.openspotlight.storage.STPartition;
import org.openspotlight.storage.STRepositoryPath;
import org.openspotlight.storage.domain.node.STNodeEntry;
import org.openspotlight.storage.domain.node.STProperty;
import java.util.Set;

/**
 * Created by User: feu - Date: Mar 23, 2010 - Time: 4:46:25 PM
 */
public class MongoSTStorageSessionImpl extends AbstractSTStorageSession {
    
    protected MongoSTStorageSessionImpl(STFlushMode flushMode, STRepositoryPath repositoryPath) {
        super(flushMode, repositoryPath);
    }


    @Override
    protected byte[] internalPropertyGetValue(STPartition partition, STProperty stProperty) throws Exception {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalSave(Set<STPartition> partitions) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STNodeEntry> internalFindByCriteria(STPartition partition, STCriteria criteria) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void flushNewItem(STPartition partition, STNodeEntry entry) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void flushRemovedItem(STPartition partition, STNodeEntry entry) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetNamedChildren(STPartition partition, STNodeEntry stNodeEntry, String name) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected boolean internalHasSavedProperty(STPartition partition, STProperty stProperty) throws Exception {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void internalFlushSimpleProperty(STPartition partition, STProperty dirtyProperty) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STNodeEntry> internalNodeEntryGetChildren(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected STNodeEntry internalNodeEntryGetParent(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STProperty> internalNodeEntryLoadProperties(STPartition partition, STNodeEntry stNodeEntry) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Set<STNodeEntry> internalFindNamed(STPartition partition, String nodeEntryName) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
