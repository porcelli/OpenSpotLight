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
package org.openspotlight.storage;

import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openspotlight.storage.domain.key.STKeyEntry;
import org.openspotlight.storage.domain.key.STLocalKey;
import org.openspotlight.storage.domain.key.STUniqueKey;

/**
 * Created by User: feu - Date: Jun 9, 2010 - Time: 4:51:47 PM
 */
public class StringIDSupport {
    private static final String SEP = "__";

    public static String getNodeEntryName( String uniqueKeyAsString ) {
        return uniqueKeyAsString.split(SEP)[2];

    }

    public static STRepositoryPath getRepositoryPath( String uniqueKeyAsString ) {
        return repositoryPath(uniqueKeyAsString.split(SEP)[0]);

    }

    public static STPartition getPartition( String uniqueKeyAsString,
                                            STPartitionFactory factory ) {
        return factory.getPartitionByName(getPartitionName(uniqueKeyAsString));
    }

    public static String getPartitionName( String uniqueKeyAsString ) {
        return uniqueKeyAsString.split(SEP)[1];
    }

    public static String getUniqueKeyAsStringHash( STUniqueKey uniqueKey ) {
        return new StringBuilder().append(uniqueKey.getRepositoryPath().getRepositoryPathAsString()).append(SEP)
                                  .append(uniqueKey.getPartition()).append(SEP)
                                  .append(uniqueKey.getLocalKey().getNodeEntryName()).append(SEP)
                                  .append(getSha1SignatureEncodedAsBase64(getUniqueKeyAsSimpleString(uniqueKey))).toString();
    }

    public static String getLocalKeyAsStringHash( STLocalKey uniqueKey ) {
        return getSha1SignatureEncodedAsBase64(getLocalKeyAsSimpleString(uniqueKey));
    }

    private static String getLocalKeyAsSimpleString( STLocalKey localKey ) {
        StringBuilder sb = new StringBuilder();
        sb.append(localKey.getNodeEntryName());
        List<STKeyEntry> ordered = new ArrayList<STKeyEntry>(localKey.getEntries());
        Collections.sort(ordered);
        for (STKeyEntry entry : ordered) {
            sb.append(":").append(entry.getPropertyName()).append(":")
                    .append(":").append(entry.getValue());
        }
        return sb.toString();
    }

    private static String getUniqueKeyAsSimpleString( STUniqueKey uniqueKey ) {
        StringBuilder sb = new StringBuilder();
        STUniqueKey currentKey = uniqueKey;
        sb.append(uniqueKey.getRepositoryPath().getRepositoryPathAsString()).append(":");
        sb.append(uniqueKey.getParentKeyAsString()).append(":");
        sb.append(getLocalKeyAsSimpleString(uniqueKey.getLocalKey()));
        return sb.toString();
    }

}
