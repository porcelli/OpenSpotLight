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
        jreFileSet.setIncludes("**/java-util-only.jar");
        //        jreFileSet.setIncludes("**/*.class");
        task.addCompiledArtifacts(jreFileSet);
        task.setContextName("JRE-util");
        task.setContextVersion("1.5");
        task.setXmlOutputFileName("./target/test-data/java-util-only.xml");

        task.execute();
    }
}
