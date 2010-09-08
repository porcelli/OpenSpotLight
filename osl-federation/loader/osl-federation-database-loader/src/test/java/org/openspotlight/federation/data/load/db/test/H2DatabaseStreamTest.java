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
package org.openspotlight.federation.data.load.db.test;

import org.junit.Before;
import org.openspotlight.bundle.domain.DbArtifactSource;
import org.openspotlight.bundle.domain.Repository;
import org.openspotlight.federation.finder.db.ScriptType;

import java.sql.Connection;
import java.util.EnumSet;
import java.util.Set;

import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.federation.data.processing.test.ConfigurationExamples.createH2DbConfiguration;

@SuppressWarnings( "all" )
public class H2DatabaseStreamTest extends DatabaseStreamTest {

    @Before
    public void cleanDatabaseFiles() throws Exception {
        delete("./target/test-data/H2DatabaseStreamTest"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DbArtifactSource createValidConfigurationWithMappings() {
        final Repository repository = createH2DbConfiguration("H2DatabaseStreamTest"); //$NON-NLS-1$
        return (DbArtifactSource)repository.getArtifactSources().iterator().next(); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fillDatabase( final Connection conn ) throws Exception {
        H2Support.fillDatabaseArtifacts(conn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<ScriptType> typesToAssert() {
        return EnumSet.of(ScriptType.VIEW, ScriptType.FUNCTION, ScriptType.PROCEDURE, ScriptType.INDEX, ScriptType.TABLE,
                          ScriptType.TRIGGER);
    }

}
