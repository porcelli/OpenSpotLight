package org.openspotlight.tool.dap.language.java.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.objectweb.asm.ClassReader;
import org.openspotlight.tool.dap.language.java.asm.model.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class ASMExportTask extends Task {

	Logger LOG = LoggerFactory.getLogger(ASMExportTask.class);

	private Set<File> jreFileSet = new HashSet<File>();
	private ASMVisitor asmVisitor = new ASMVisitor();
	XStream xstream = new XStream();

	public void addJREFileSet(FileSet fileSet) {
		DirectoryScanner scanner = fileSet.getDirectoryScanner(getProject());
		for (String activeFileName : scanner.getIncludedFiles()) {
			File file = new File(fileSet.getDir(getProject()), activeFileName);
			jreFileSet.add(file);
		}
	}

	public void execute() {
		xstream.aliasPackage("", "org.openspotlight.tool.dap.language.java.asm.model");
		scanJRE();
	}

	private void scanJRE() {
		int count = 0;
		List<JavaType> types = new LinkedList<JavaType>();
		try {
			for (File activeArtifact : jreFileSet) {
				LOG.info("Parsing JRE File \""
						+ activeArtifact.getCanonicalPath() + "\"");
				if (activeArtifact.getName().endsWith(".jar")) {
					// Open Zip file for reading
					ZipFile zipFile = new ZipFile(activeArtifact,
							ZipFile.OPEN_READ);

					// Create an enumeration of the entries in the zip file
					Enumeration<? extends ZipEntry> zipFileEntries = zipFile
							.entries();

					// Process each entry
					while (zipFileEntries.hasMoreElements()) {
						// grab a zip file entry
						ZipEntry entry = zipFileEntries.nextElement();
						// extract file if not a directory
						if (!entry.isDirectory()
								&& entry.getName().endsWith(".class")) {
							ClassReader reader = new ClassReader(zipFile
									.getInputStream(entry));
							reader.accept(asmVisitor, 0);
							count++;
							types.add(asmVisitor.getType());
						}
					}
					zipFile.close();
				} else if (activeArtifact.getName().endsWith(".class")) {
					ClassReader reader = new ClassReader(new FileInputStream(
							activeArtifact));
					reader.accept(asmVisitor, 0);
					count++;
					types.add(asmVisitor.getType());
				}
			}
			System.out.println(count);
			xstream.toXML(types, new FileOutputStream("test.xml"));
		} catch (Exception ex) {
			StringWriter sWriter = new StringWriter();
			ex.printStackTrace(new PrintWriter(sWriter));
			LOG.error("Problems during parser - Stack trace:\n"
					+ sWriter.getBuffer().toString());
		}
	}
}