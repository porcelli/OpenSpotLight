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
package org.openspotlight.bundle.common.parser;

import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;

public class SLArtifactStreamCaseInsensitiveImpl extends SLArtifactStreamBasicImpl {

    public enum CaseMode {
        LOWER,
        UPPER
    }

    private final CaseMode caseMode;

    public SLArtifactStreamCaseInsensitiveImpl(
                                                final String fedaratedArtifactPath, final List<String> artifactContent,
                                                final String version)
        throws IOException {
        super(fedaratedArtifactPath, artifactContent, version);
        caseMode = CaseMode.LOWER;
    }

    public SLArtifactStreamCaseInsensitiveImpl(
                                                final String fedaratedArtifactPath, final List<String> artifactContent,
                                                final String version, final CaseMode mode)
        throws IOException {
        super(fedaratedArtifactPath, artifactContent, version);
        caseMode = mode;
    }

    @Override
    public int LA(final int i) {
        final int result = super.LA(i);
        if (result == 0) { return 0; // undefined
        }
        if (result == CharStream.EOF) { return CharStream.EOF; }
        if (caseMode == CaseMode.UPPER) { return Character.toUpperCase(result); }
        return Character.toLowerCase(result);
    }
}
