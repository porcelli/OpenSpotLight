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
package org.openspotlight.storage.domain.node;

import java.io.InputStream;

import org.openspotlight.storage.STStorageSession;

/**
 * Created by IntelliJ IDEA. User: feuteston Date: 28/03/2010 Time: 10:27:26 To change this template use File | Settings | File
 * Templates.
 */
public interface STProperty {

    STNodeEntry getParent();

    public boolean isIndexed();

    public boolean isKey();

    void setStringValue( STStorageSession session,
                         String value );

    void setBytesValue( STStorageSession session,
                        byte[] value );

    void setStreamValue( STStorageSession session,
                         InputStream value );

    String getValueAsString( STStorageSession session );

    byte[] getValueAsBytes( STStorageSession session );

    InputStream getValueAsStream( STStorageSession session );

    String getPropertyName();

    public STPropertyInternalMethods getInternalMethods();

    interface STPropertyInternalMethods {
        void setStringValueOnLoad( STStorageSession session,
                                   String value );

        void setBytesValueOnLoad( STStorageSession session,
                                  byte[] value );

        void setStreamValueOnLoad( STStorageSession session,
                                   InputStream value );

        void removeTransientValueIfExpensive();

        String getTransientValueAsString( STStorageSession session );

        byte[] getTransientValueAsBytes( STStorageSession session );

        InputStream getTransientValueAsStream( STStorageSession session );

    }

}
