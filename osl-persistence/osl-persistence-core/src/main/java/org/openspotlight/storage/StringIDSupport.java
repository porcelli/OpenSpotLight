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
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto Direitos Autorais Reservados (c) 2009, CARAVELATECH
 * CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor. Todas as contribuições de
 * terceiros estão distribuídas sob licença da CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA. Este programa é
 * software livre; você pode redistribuí-lo e/ou modificá-lo sob os termos da Licença Pública Geral Menor do GNU conforme
 * publicada pela Free Software Foundation. Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a Licença
 * Pública Geral Menor do GNU para mais detalhes. Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU
 * junto com este programa; se não, escreva para: Free Software Foundation, Inc. 51 Franklin Street, Fifth Floor Boston, MA
 * 02110-1301 USA
 */
package org.openspotlight.storage;

import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;
import static org.openspotlight.storage.RepositoryPath.repositoryPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openspotlight.storage.domain.Node;
import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;

/**
 * Created by User: feu - Date: Jun 9, 2010 - Time: 4:51:47 PM
 */
public class StringIDSupport {
    private static final String SEP          = "__";
    private static final String NODE_KEY_SEP = ":";
    private static final String LINK_KEY_SEP = "::";

    public static String getNodeEntryName(
                                          final String uniqueKeyAsString) {
        return uniqueKeyAsString.split(SEP)[2];

    }

    public static RepositoryPath getRepositoryPath(
                                                   final String uniqueKeyAsString) {
        return repositoryPath(uniqueKeyAsString.split(SEP)[0]);

    }

    public static Partition getPartition(
                                         final String uniqueKeyAsString,
                                         final PartitionFactory factory) {
        return factory.getPartitionByName(getPartitionName(uniqueKeyAsString));
    }

    public static String getPartitionName(
                                          final String uniqueKeyAsString) {
        return uniqueKeyAsString.split(SEP)[1];
    }

    public static String getUniqueKeyAsStringHash(
                                                  final NodeKey uniqueKey) {
        return new StringBuilder()
            .append(
            uniqueKey.getRepositoryPath()
            .getRepositoryPathAsString())
            .append(SEP)
            .append(uniqueKey.getPartition().getPartitionName())
            .append(SEP)
            .append(uniqueKey.getLocalKey().getNodeEntryName())
            .append(SEP)
            .append(
            getSha1SignatureEncodedAsBase64(getUniqueKeyAsSimpleString(uniqueKey)))
            .toString();
    }

    public static String getLocalKeyAsStringHash(
                                                 final CompositeKey uniqueKey) {
        return getSha1SignatureEncodedAsBase64(getLocalKeyAsSimpleString(uniqueKey));
    }

    private static String getLocalKeyAsSimpleString(
                                                    final CompositeKey localKey) {
        final StringBuilder sb = new StringBuilder();
        sb.append(localKey.getNodeEntryName());
        final List<SimpleKey> ordered = new ArrayList<SimpleKey>(localKey
            .getEntries());
        Collections.sort(ordered);
        for (final SimpleKey entry: ordered) {
            sb.append(NODE_KEY_SEP).append(entry.getPropertyName()).append(
                NODE_KEY_SEP).append(NODE_KEY_SEP).append(entry.getValue());
        }
        return sb.toString();
    }

    private static String getUniqueKeyAsSimpleString(
                                                     final NodeKey uniqueKey) {
        final StringBuilder sb = new StringBuilder();
        sb.append(uniqueKey.getRepositoryPath().getRepositoryPathAsString())
            .append(":");
        sb.append(uniqueKey.getParentKeyAsString()).append(":");
        sb.append(getLocalKeyAsSimpleString(uniqueKey.getLocalKey()));
        return sb.toString();
    }

    public static String getLinkKeyAsString(
                                            final Partition originPartition,
                                            final String linkName, final Node origin, final Node target) {
        return getLinkKeyAsString(originPartition, linkName, origin.getKeyAsString(), target.getKeyAsString());
    }

    public static String getLinkKeyAsString(
                                            final Partition originPartition,
                                            final String linkName, final String originAsString, final String targetAsString) {
        final StringBuilder sb = new StringBuilder();
        sb.append(originAsString);
        sb.append(LINK_KEY_SEP);
        sb.append(targetAsString);
        sb.append(LINK_KEY_SEP);
        sb.append(linkName);
        return sb.toString();
    }

    public static String getLinkNameFromLinkKey(
                                                final String linkKey) {
        return linkKey.split("[:][:]")[2];
    }

    public static String getOriginKeyAsStringFromLinkKey(
                                                         final String linkKey) {
        return linkKey.split("[:][:]")[0];
    }

    public static String getTargeyKeyAsStringFromLinkKey(
                                                         final String linkKey) {
        return linkKey.split("[:][:]")[1];
    }

}
