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
 * @author feu
 * 
 */
public class FilesTest {

	private static String LOWEST_PATH = "./target";

	private static String LOWER_PATH = LOWEST_PATH + "/resources/testData";

	private static String TEST_DIR = LOWER_PATH + "/SomeOtherDir/anotherDir";

	private static String TEST_FILE = TEST_DIR + "/temp.txt";

	private static String RELATIVE_PATH_FILE = removeBegginingFrom(LOWEST_PATH,
			TEST_FILE);

	@Before
	public void createSomeTestData() throws Exception {
		File dir = new File(TEST_DIR);
		File file = new File(TEST_FILE);

		dir.mkdirs();
		file.createNewFile();

		assertThat(dir.exists(), is(true));
		assertThat(file.exists(), is(true));

	}

	@Test
	public void shouldListFileNamesInARecursiveWay() throws Exception {
		Set<String> fileNames = listFileNamesFrom(LOWEST_PATH);
		assertThat(fileNames.contains(RELATIVE_PATH_FILE), is(true));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowExceptionWhenGettingInvalidFile() throws Exception {
		listFileNamesFrom("invalid base path");
	}

	@Test
	public void shouldDeleteValidFiles() throws Exception {
		delete(TEST_FILE);
		assertThat(new File(TEST_FILE).exists(), is(false));
	}

	@Test
	public void shouldDeleteValidDirs() throws Exception {
		delete(LOWER_PATH);
		assertThat(new File(LOWER_PATH).exists(), is(false));
	}

	@Test
	public void shouldWriteByteArrayFromStream() throws Exception {
		byte[] initialContent = "initialContent".getBytes();
		InputStream is = new ByteArrayInputStream(initialContent);
		byte[] readedContent = readBytesFromStream(is);
		assertThat(Arrays.equals(initialContent, readedContent), is(true));
	}

}
