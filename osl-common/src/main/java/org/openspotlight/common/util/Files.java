package org.openspotlight.common.util;

import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrowNew;
import static org.openspotlight.common.util.Strings.removeBegginingFrom;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.openspotlight.common.exception.SLException;
import org.openspotlight.common.exception.SLRuntimeException;

/**
 * Helper class to deal with multiple files
 * 
 * @author feu
 * 
 */
public class Files {

	/**
	 * Should not be instantiated
	 */
	private Files() {
		throw new IllegalStateException(Messages
				.getString("invalidConstructor")); //$NON-NLS-1$
	}

	/**
	 * Returns a relative path list from an initial directory, or the file path
	 * itself if the initialPath is a file.
	 * 
	 * @param basePath
	 * @param initialRelativePath
	 * @return a relative path list
	 * @throws SLException
	 */
	public static Set<String> listFileNamesFrom(String basePath)
			throws SLException {
		checkNotEmpty("basePath", basePath);
		File basePathAsFile = new File(basePath);
		checkCondition("basePathExists", basePathAsFile.exists());
		checkCondition("basePathIsDirectory", basePathAsFile.isDirectory());
		try {
			basePath = getNormalizedFileName(basePathAsFile);
			Set<String> result = new HashSet<String>();
			File initial = new File(basePath);
			listFileNamesFrom(result, basePath, initial);
			return result;
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

	/**
	 * Returns the normalized path (in a unix like way).
	 * 
	 * @param f
	 * @return
	 */
	public static String getNormalizedFileName(File f) {
		try {
			return f.getCanonicalPath().replaceAll("\\\\", "/");
		} catch (Exception e) {
			throw logAndReturnNew(e, SLRuntimeException.class);
		}
	}

	/**
	 * Execute the file listing using recursion to fill the file name set.
	 * 
	 * @param setOfFiles
	 * @param basePath
	 * @param file
	 * @throws Exception
	 */
	private static void listFileNamesFrom(Set<String> setOfFiles,
			String basePath, File file) throws Exception {
		checkNotNull("setOfFiles", setOfFiles);
		checkNotEmpty("basePath", basePath);
		checkNotNull("file", file);
		if (file.isFile()) {
			setOfFiles.add(removeBegginingFrom(basePath,
					getNormalizedFileName(file)));
		} else if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				listFileNamesFrom(setOfFiles, basePath, f);
			}
		}
	}

	/**
	 * Delete multiple files
	 * 
	 * @param path
	 * @throws SLException
	 */
	public static void delete(File path) throws SLException {
		checkNotNull("path", path);
		if (!path.exists())
			return;
		try {
			if (path.isFile()) {
				deleteFile(path);
			} else {
				deleteDir(path);
			}
		} catch (Exception e) {
			logAndThrowNew(e, SLException.class);
		}
	}

	/**
	 * Delete multiple files
	 * 
	 * @param path
	 * @throws SLException
	 */
	public static void delete(String path) throws SLException {
		checkNotEmpty("path", path);
		delete(new File(path));

	}

	/**
	 * Deletes directory in a recursive way, first excluding its contents
	 * 
	 * @param dir
	 * @throws Exception
	 */
	private static void deleteDir(File dir) throws Exception {
		checkNotNull("dir", dir);
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				deleteFile(f);
			} else {
				deleteDir(f);
			}
		}
		dir.delete();
	}

	/**
	 * Delete a file itself
	 * 
	 * @param file
	 * @throws Exception
	 */
	private static void deleteFile(File file) {
		checkNotNull("file", file);
		file.delete();
	}

	/**
	 * Reads an streams content and writes it on a byte array.
	 * 
	 * @param inputStream
	 * @return
	 * @throws SLException
	 */
	public static byte[] readBytesFromStream(InputStream inputStream)
			throws SLException {
		checkNotNull("inputStream", inputStream);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while (inputStream.available() > 0) {
				baos.write(inputStream.read());
			}
			byte[] content = baos.toByteArray();
			return content;
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

}
