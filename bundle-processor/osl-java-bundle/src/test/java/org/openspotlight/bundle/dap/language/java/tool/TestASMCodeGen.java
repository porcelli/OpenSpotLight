package org.openspotlight.bundle.dap.language.java.tool;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.junit.Test;
import org.openspotlight.bundle.dap.language.java.asm.tool.CompiledTypesExtractorTask;

public class TestASMCodeGen {

    @Test
    public void testASMExtract() {
        final CompiledTypesExtractorTask task = new CompiledTypesExtractorTask();
        task.setProject(new Project());

        final FileSet jreFileSet = new FileSet();
        jreFileSet.setDir(new File("."));
        jreFileSet.setIncludes("**/jboss-seam*.jar");
        //        jreFileSet.setIncludes("**/*.class");
        task.addCompiledArtifacts(jreFileSet);
        task.setContextName("Seam");
        task.setContextVersion("2.1.1-GA");
        task.setXmlOutputFileName("./target/test-data/big-result.xml");

        task.execute();
    }
}
