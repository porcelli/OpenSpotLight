package org.openspotlight.federation.data.load;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Set;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Assertions.checkNotEmpty;
import static org.openspotlight.common.util.Assertions.checkCondition;
import static org.openspotlight.common.util.Files.listFileNamesFrom;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import org.openspotlight.common.exception.ConfigurationException;
import org.openspotlight.common.exception.SLException;
import org.openspotlight.federation.data.Bundle;

/**
 * Artifact loader that loads Artifact for file system.
 * 
 * @author feu
 * 
 */
public class FileSystemArtifactLoader extends AbstractArtifactLoader {

	/**
	 * Return all files from bundle.initialLookup directory.
	 */
	@Override
	protected Set<String> getAllArtifactNames(Bundle bundle)
			throws ConfigurationException {
		checkNotNull("bundle", bundle);
		String basePath = bundle.getInitialLookup();
		try {
			Set<String> allFiles = listFileNamesFrom(basePath);
			return allFiles;
		} catch (SLException e) {
			throw logAndReturnNew(e, ConfigurationException.class);
		}
	}

	/**
	 * loads the content of a file found on bundle.initialLookup + artifactName
	 */
	@Override
	protected byte[] loadArtifact(Bundle bundle, String artifactName)
			throws Exception {
		checkNotNull("bundle", bundle);
		checkNotEmpty("artifactName", artifactName);
		String fileName = bundle.getInitialLookup() + artifactName;
		File file = new File(fileName);
		checkCondition("fileExists", file.exists());
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while (fis.available() > 0) {
			baos.write(fis.read());
		}
		byte[] content = baos.toByteArray();
		fis.close();
		return content;
	}

}
