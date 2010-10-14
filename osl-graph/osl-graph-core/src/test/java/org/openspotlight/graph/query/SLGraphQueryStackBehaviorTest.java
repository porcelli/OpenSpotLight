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
package org.openspotlight.graph.query;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.not;

import java.util.concurrent.Callable;

import org.junit.Test;
import org.openspotlight.graph.exception.SLInvalidQuerySyntaxException;
import org.openspotlight.graph.manipulation.GraphReader;
import org.openspotlight.graph.test.domain.link.PackageContainsType;
import org.openspotlight.graph.test.domain.node.JavaInterface;
import org.openspotlight.graph.test.domain.node.JavaPackage;
import org.openspotlight.graph.test.domain.node.JavaType;
import org.openspotlight.security.idm.AuthenticatedUser;

public class SLGraphQueryStackBehaviorTest extends AbstractGeneralQueryTest {
	private static AuthenticatedUser user;

	@Test
	public void testVerifyWhereInStack() throws Exception {

		final QueryApi query2Input = session.createQueryApi();

		query2Input.select().type(JavaPackage.class.getName()).selectEnd()
				.where().type(JavaPackage.class.getName()).each()
				.property("caption").equalsTo().value("java.util").typeEnd()
				.whereEnd();

		final QueryResult inputResult = query2Input.execute(sortMode, true);
		final NodeWrapper[] inputWrappers = wrapNodes(inputResult.getNodes());

		new AssertResult() {
			public void execute() {
				assertThat(inputWrappers.length, is(1));
				assertThat(new NodeWrapper(JavaPackage.class.getName(),
						"queryTest", "java.util"), is(inputWrappers[0]));
			}
		}.execute();

		printInfo = true;
		printResult(inputResult.getNodes());

		final QueryApi query = session.createQueryApi();

		query.select().type(JavaInterface.class.getName()).comma()
				.byLink(PackageContainsType.class.getName()).b().selectEnd()
				.select().allTypes().selectEnd().where()
				.type(JavaType.class.getName()).subTypes().each()
				.property("caption").contains().value("Map").typeEnd()
				.whereEnd();

		final QueryResult initialData = query.execute(inputResult.getNodes(),
				sortMode, printInfo);
		final NodeWrapper[] wrappers = wrapNodes(initialData.getNodes());

		new AssertResult() {
			public void execute() {
				assertThat(wrappers.length, is(2));
				assertThat(new NodeWrapper(JavaInterface.class.getName(),
						"java.util", "java.util.Map"), isOneOf(wrappers));
				assertThat(new NodeWrapper(JavaInterface.class.getName(),
						"java.util", "java.util.SortedMap"), isOneOf(wrappers));
			}
		}.execute();

		printResult(initialData.getNodes());
	}

	@Test
	public void testVerifyWhereInStack2() throws Exception {

		final QueryApi query2Input = session.createQueryApi();

		query2Input.select().type(JavaPackage.class.getName()).selectEnd()
				.where().type(JavaPackage.class.getName()).each()
				.property("caption").equalsTo().value("java.util").typeEnd()
				.whereEnd();

		final QueryResult inputResult = query2Input.execute(sortMode, true);
		final NodeWrapper[] inputWrappers = wrapNodes(inputResult.getNodes());

		printInfo = true;
		printResult(inputResult.getNodes());

		new AssertResult() {
			public void execute() {
				assertThat(inputWrappers.length, is(1));
				assertThat(new NodeWrapper(JavaPackage.class.getName(),
						"queryTest", "java.util"), is(inputWrappers[0]));
			}
		}.execute();

		final QueryApi query = session.createQueryApi();

		query.select().type(JavaInterface.class.getName()).comma()
				.byLink(PackageContainsType.class.getName()).b().selectEnd()
				.select().allTypes().selectEnd();

		final QueryResult initialData = query.execute(inputResult.getNodes(),
				sortMode, printInfo);
		final NodeWrapper[] wrappers = wrapNodes(initialData.getNodes());

		printResult(initialData.getNodes());

		new AssertResult() {
			public void execute() {
				assertThat(wrappers.length >= 14, is(true));
			}
		}.execute();

		final QueryApi query2Compare = session.createQueryApi();

		query2Compare.select().type(JavaInterface.class.getName()).selectEnd()
				.select().allTypes().selectEnd();

		final QueryResult resultData = query2Compare.execute(sortMode,
				printInfo);
		final NodeWrapper[] wrappers2 = wrapNodes(resultData.getNodes());

		printResult(resultData.getNodes());

		new AssertResult() {
			public void execute() {
				assertThat(wrappers2.length >= 19, is(true));
			}
		}.execute();

		assertThat(wrappers.length, is(not(wrappers2.length)));
	}

	@Test
	public void testVerifyWhereInStack3() throws Exception {

		final QueryApi query1 = session.createQueryApi();

		query1.select().type(JavaInterface.class.getName()).selectEnd();

		final QueryResult resultData = query1.execute(sortMode, printInfo);

		final QueryApi query2 = session.createQueryApi();

		query2.select().type(JavaInterface.class.getName()).selectEnd()
				.select().allTypes().selectEnd();

		final QueryResult resultData2 = query2.execute(sortMode, printInfo);

		assertThat(resultData2.getNodes().size(), is(resultData.getNodes()
				.size()));
	}

	@Override
	protected Callable<Void> createStartUpHandler() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected Callable<Void> createShutdownHandler() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected GraphReader graphReader() {
		throw new UnsupportedOperationException();
	}

}
