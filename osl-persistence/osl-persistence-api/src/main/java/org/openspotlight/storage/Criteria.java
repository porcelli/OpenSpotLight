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
package org.openspotlight.storage;

import java.util.Set;

import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;

public interface Criteria {

    Partition getPartition();

    String getNodeType();

    Set<CriteriaItem> getCriteriaItems();

    Iterable<Node> andFind(StorageSession session);

    Node andFindUnique(StorageSession session);

    public interface CriteriaItem {

        String getNodeType();

        public interface PropertyCriteriaItem extends CriteriaItem {

            String getValue();

            String getPropertyName();

        }

        public interface CompisiteKeyCriteriaItem extends CriteriaItem {
            CompositeKey getValue();
        }

        public interface PropertyEndsWithString extends CriteriaItem {
            String getValue();

            String getPropertyName();
        }

        public interface PropertyStartsWithString extends CriteriaItem {
            String getValue();

            String getPropertyName();
        }

        public interface NodeKeyAsStringCriteriaItem extends CriteriaItem {
            String getKeyAsString();

        }

        public interface NodeKeyCriteriaItem extends CriteriaItem {
            NodeKey getValue();

        }

        public interface PropertyContainsString extends CriteriaItem {
            String getValue();

            String getPropertyName();
        }

    }

    public interface CriteriaBuilder {

        CriteriaBuilder withProperty(String propertyName);

        CriteriaBuilder withNodeEntry(String nodeName);

        CriteriaBuilder equalsTo(String value);

        CriteriaBuilder containsString(String value);

        CriteriaBuilder startsWithString(String value);

        CriteriaBuilder endsWithString(String value);

        CriteriaBuilder and();

        Criteria buildCriteria();

        CriteriaBuilder withLocalKey(CompositeKey localKey);

        CriteriaBuilder withUniqueKey(NodeKey uniqueKey);

        CriteriaBuilder withUniqueKeyAsString(String uniqueKeyAsString);
    }
}
