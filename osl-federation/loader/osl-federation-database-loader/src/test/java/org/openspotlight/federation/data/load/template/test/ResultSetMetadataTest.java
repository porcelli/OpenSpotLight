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
package org.openspotlight.federation.data.load.template.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.federation.data.load.db.test.H2Support;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.List;

import static java.lang.Class.forName;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;

@SuppressWarnings( "all" )
public class ResultSetMetadataTest {

    @BeforeClass
    public static void loadDriver() throws Exception {
        forName("org.h2.Driver");
    }

    @Before
    public void cleanAndFillFreshDatabase() throws Exception {
        delete("./target/test-data/ResultSetMetadataTest/h2");
        final Connection conn = DriverManager.getConnection("jdbc:h2:./target/test-data/ResultSetMetadataTest/h2/db");
        H2Support.fillDatabaseArtifacts(conn);
        conn.commit();
        conn.close();
    }

    @SuppressWarnings( "boxing" )
    @Test
    public void shouldReadMetadataFromSelect() throws Exception {
        final Connection conn = DriverManager.getConnection("jdbc:h2:./target/test-data/ResultSetMetadataTest/h2/db");
        final ResultSet resultSet = conn.prepareStatement(
                                                          " select TABLE_CATALOG AS CATALOG_NAME, TABLE_SCHEMA AS SCHEMA_NAME, TABLE_NAME AS NAME from INFORMATION_SCHEMA.TABLES where TABLE_TYPE='TABLE'").executeQuery();
        final ResultSetMetaData metadata = resultSet.getMetaData();
        final List<String> columnNames = Arrays.asList("CATALOG_NAME", "SCHEMA_NAME", "NAME");

        for (int i = 1, count = metadata.getColumnCount(); i <= count; i++) {
            final String name = metadata.getColumnLabel(i);
            assertThat(columnNames.contains(name), is(true));
        }

    }

}
