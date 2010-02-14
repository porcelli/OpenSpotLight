package org.openspotlight.bundle.language.java.bundle.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.junit.Ignore;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;

@Ignore
public class ClasspathImport {

	private static File extractZippedOnTempFile() throws IOException,
			FileNotFoundException {
		final File temp = File.createTempFile("stress-data", ".xml");
		final ZipInputStream zis = new ZipInputStream(new FileInputStream(
				"src/test/resources/data/exported-stress-data.xml.zip"));
		final OutputStream fos = new BufferedOutputStream(new FileOutputStream(
				temp));
		final byte data[] = new byte[2048];
		int count;
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null) {
			if (entry.getName().endsWith("xml")) {
				while ((count = zis.read(data, 0, 2048)) != -1) {
					fos.write(data, 0, count);
				}
				break;
			}
		}
		fos.flush();
		fos.close();
		zis.close();
		return temp;
	}

	public static void importClassPathData(
			final JcrConnectionDescriptor descriptor) throws IOException,
			FileNotFoundException, RepositoryException, PathNotFoundException,
			ItemExistsException, ConstraintViolationException,
			VersionException, InvalidSerializedDataException, LockException,
			AccessDeniedException, InvalidItemStateException,
			NoSuchNodeTypeException {
		final File temp = extractZippedOnTempFile();
		importDataFromFile(temp, descriptor);
	}

	private static void importDataFromFile(final File temp,
			final JcrConnectionDescriptor descriptor)
			throws FileNotFoundException, RepositoryException, IOException,
			PathNotFoundException, ItemExistsException,
			ConstraintViolationException, VersionException,
			InvalidSerializedDataException, LockException,
			AccessDeniedException, InvalidItemStateException,
			NoSuchNodeTypeException {
		final InputStream is = new FileInputStream(temp);
		final JcrConnectionProvider desc = JcrConnectionProvider
				.createFromData(descriptor);
		desc.closeRepositoryAndCleanResources();
		final SessionWithLock session = desc.openSession();
		final Node root = session.getRootNode();
		final BufferedInputStream bufferedInputStream = new BufferedInputStream(
				is);
		session.importXML(root.getPath(), bufferedInputStream,
				ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
		session.save();
		session.logout();
	}

	public static void main(final String... args) throws Exception {
		importClassPathData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

	}

}
