package org.openspotlight.tool.dap.language.java.asm;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.junit.Test;
import org.openspotlight.tool.dap.language.java.asm.ASMExportTask;

public class TestASMCodeGen {

	@Test
	public void testASMExtract() {
		ASMExportTask task = new ASMExportTask();
		task.setProject(new Project());

		FileSet jreFileSet = new FileSet();
		jreFileSet.setDir(new File("/Users/porcelli/Documents/dev/java-jre/"));
		jreFileSet.setIncludes("**/*.jar");
		jreFileSet.setIncludes("**/*.class");
		task.addJREFileSet(jreFileSet);

		task.execute();
	}
}