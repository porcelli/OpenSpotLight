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

package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Files.delete;
import static org.openspotlight.common.util.Files.listFileNamesFrom;
import static org.openspotlight.common.util.Files.readBytesFromStream;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.openspotlight.common.util.Files;

/**
 * Test class for {@link Files}
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
@SuppressWarnings("all")
public class FilesTest {

	private static String __LOWEST_PATH = "./target/test-data/FilesTest/"; //$NON-NLS-1$
	private static String _LOWER_PATH = __LOWEST_PATH + "resources/testData/"; //$NON-NLS-1$
	private static String _TEST_DIR = _LOWER_PATH + "SomeOtherDir/anotherDir/"; //$NON-NLS-1$

	private static String _TEST_FILE = _TEST_DIR + "temp.txt"; //$NON-NLS-1$

	private static String RELATIVE_PATH_FILE = removeBegginingFrom(
			__LOWEST_PATH, _TEST_FILE);

	@SuppressWarnings("boxing")
	@Before
	public void createSomeTestData() throws Exception {
		final File dir = new File(_TEST_DIR);
		final File file = new File(_TEST_FILE);

		dir.mkdirs();
		file.createNewFile();

		assertThat(dir.exists(), is(true));
		assertThat(file.exists(), is(true));

	}

	@SuppressWarnings("boxing")
	@Test
	public void shouldDeleteValidDirs() throws Exception {
		delete(_LOWER_PATH);
		assertThat(new File(_LOWER_PATH).exists(), is(false));
	}

	@SuppressWarnings("boxing")
	@Test
	public void shouldDeleteValidFiles() throws Exception {
		delete(_TEST_FILE);
		assertThat(new File(_TEST_FILE).exists(), is(false));
	}

	@SuppressWarnings("boxing")
	@Test
	public void shouldListFileNamesInARecursiveWay() throws Exception {
		final Set<String> fileNames = listFileNamesFrom(__LOWEST_PATH);
		final String completePath = new File(__LOWEST_PATH + RELATIVE_PATH_FILE)
				.getCanonicalPath();
		assertThat(fileNames.contains(completePath), is(true));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionWhenGettingInvalidFile() throws Exception {
		listFileNamesFrom("invalid base path"); //$NON-NLS-1$
	}

	@SuppressWarnings("boxing")
	@Test
	public void shouldWriteByteArrayFromStream() throws Exception {
		final byte[] initialContent = "initialContent".getBytes(); //$NON-NLS-1$
		final InputStream is = new ByteArrayInputStream(initialContent);
		final byte[] readedContent = readBytesFromStream(is);
		assertThat(Arrays.equals(initialContent, readedContent), is(true));
	}

}
