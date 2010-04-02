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
package org.openspotlight.jcr.provider.test;

import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

public class XPathTest {

    private static JcrConnectionProvider provider;
    private static Session               session;

    @AfterClass
    public static void close() throws Exception {
        XPathTest.session.logout();
    }

    @BeforeClass
    public static void setup() throws Exception {
        XPathTest.provider = JcrConnectionProvider
                                                  .createFromData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);
        XPathTest.provider.openRepository();
        XPathTest.session = XPathTest.provider.openSession();

    }

    @Test
    public void shouldExecuteXPath() throws Exception {
        final Node rootNode = XPathTest.session.getRootNode();
        rootNode.addNode("abc");
        XPathTest.session.save();
        final QueryResult result = XPathTest.session.getWorkspace()
                                                    .getQueryManager().createQuery("abc", Query.XPATH).execute();
        Assert.assertThat(result.getNodes().hasNext(), Is.is(true));

    }

    @Test
    public void shouldExecuteXPath2() throws Exception {
        final Node rootNode = XPathTest.session.getRootNode();
        rootNode.addNode("abc1").setProperty("Teste", "test");
        rootNode.addNode("abc2").setProperty("TesteA", "test");
        rootNode.addNode("abc3").setProperty("Teste", "testX");
        rootNode.addNode("abc4").setProperty("Teste", "test");
        XPathTest.session.save();
        final QueryResult result = XPathTest.session.getWorkspace()
                                                    .getQueryManager().createQuery("//element(*)[jcr:contains(.,'test')]", Query.XPATH).execute();
        Assert.assertThat(result.getNodes().getSize(), Is.is(3L));
    }

}