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

import static org.openspotlight.common.util.Exceptions.logAndThrow;
import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openspotlight.common.util.Messages;
import org.openspotlight.storage.domain.StorageNode;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;

/**
 * Helper methods that helps build encoded strings that represents nodes and links keys as well extract data from these keys.
 * 
 * @author feuteston
 * @author porcelli
 */
public final class StringKeysSupport {
    private static final String LINK_KEY_SEP = "::";
    private static final String NODE_KEY_SEP = ":";
    private static final String SEP          = "__";

    /**
     * Should not be instantiated
     */
    private StringKeysSupport() {
        logAndThrow(new IllegalStateException(Messages.getString("invalidConstructor"))); //$NON-NLS-1$
    }

    /**
     * Builds a string that represents the composite key encoding inside it all information inside the input {@link CompositeKey}.
     * 
     * @param compositeKey the composite key
     * @return encoded string that represents the composite key
     */
    private static String buildCompositeKeyAsString(final CompositeKey compositeKey) {
        final StringBuilder sb = new StringBuilder();
        sb.append(compositeKey.getNodeType());
        final List<SimpleKey> ordered = new ArrayList<SimpleKey>(compositeKey.getKeys());
        Collections.sort(ordered);
        for (final SimpleKey entry: ordered) {
            sb.append(NODE_KEY_SEP)
                .append(entry.getKeyName())
                .append(NODE_KEY_SEP)
                .append(NODE_KEY_SEP)
                .append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * Builds a string that represents the node key encoding inside it the parent key and its composite key.
     * 
     * @param nodeKey the node key
     * @return encoded string that represents the node key
     */
    private static String buildNodeKeyAsStringBasedOnParentAndCompositeKey(final NodeKey nodeKey) {
        return new StringBuilder()
            .append(nodeKey.getParentKeyAsString())
            .append(":")
            .append(buildCompositeKeyAsString(nodeKey.getCompositeKey()))
            .toString();
    }

    /**
     * Builds a hash string that represents the composite key.
     * 
     * @param compositeKey the composite key
     * @return hash string that represents the composite key
     */
    public static String buildCompositeKeyAsHash(final CompositeKey compositeKey) {
        return getSha1SignatureEncodedAsBase64(buildCompositeKeyAsString(compositeKey));
    }

    /**
     * Builds a link key based on input.
     * 
     * @param linkType the link type
     * @param origin the origin node
     * @param target the target node
     * @return the link key
     */
    public static String buildLinkKeyAsString(final String linkType, final StorageNode origin,
                                              final StorageNode target) {
        return buildLinkKeyAsString(linkType, origin.getKeyAsString(), target.getKeyAsString());
    }

    /**
     * Builds a link key based on link input.
     * 
     * @param linkType the link type
     * @param origin the origin key
     * @param target the target key
     * @return the link key
     */
    public static String buildLinkKeyAsString(final String linkType, final String origin, final String target) {
        return new StringBuilder()
            .append(origin)
            .append(LINK_KEY_SEP)
            .append(target)
            .append(LINK_KEY_SEP)
            .append(linkType)
            .toString();
    }

    /**
     * Builds a node key that encodes all {@link NodeKey} data inside it.
     * 
     * @param nodeKey the node key
     * @return encoded string that represents the node key
     */
    public static String buildNodeKeyAsString(final NodeKey nodeKey) {
        return new StringBuilder()
            .append(nodeKey.getPartition().getPartitionName())
            .append(SEP)
            .append(nodeKey.getCompositeKey().getNodeType())
            .append(SEP)
            .append(getSha1SignatureEncodedAsBase64(buildNodeKeyAsStringBasedOnParentAndCompositeKey(nodeKey)))
            .toString();
    }

    /**
     * Returns the link type that is encoded inside link key.
     * 
     * @param linkKey a link key
     * @return the link type
     */
    public static String getLinkTypeFromLinkKey(final String linkKey) {
        return linkKey.split("[:][:]")[2];
    }

    /**
     * Returns the node type that is encoded into node key.
     * 
     * @param nodeKey the node key
     * @return the node type
     */
    public static String getNodeType(final String nodeKey) {
        return nodeKey.split(SEP)[1];
    }

    /**
     * Returns the origin node key that is encoded inside link key.
     * 
     * @param linkKey a link key
     * @return the origin node key
     */
    public static String getOriginKeyAsStringFromLinkKey(final String linkKey) {
        return linkKey.split("[:][:]")[0];
    }

    /**
     * Syntax sugar that returns the partition (using {@link PartitionFactory}) that is encoded inside node key.
     * 
     * @param nodeKey the node key
     * @param factory the parition factory
     * @return the partition
     */
    public static Partition getPartition(final String nodeKey,
                                         final PartitionFactory factory) {
        return factory.getPartition(getPartitionName(nodeKey));
    }

    /**
     * Returns the partition name that is encoded into node key.
     * 
     * @param nodeKey the node key
     * @return the partition name
     */
    public static String getPartitionName(final String nodeKey) {
        return nodeKey.split(SEP)[0];
    }

    /**
     * Returns the target node key that is encoded inside link key.
     * 
     * @param linkKey a link key
     * @return the target node key
     */
    public static String getTargeyKeyAsStringFromLinkKey(final String linkKey) {
        return linkKey.split("[:][:]")[1];
    }
}
