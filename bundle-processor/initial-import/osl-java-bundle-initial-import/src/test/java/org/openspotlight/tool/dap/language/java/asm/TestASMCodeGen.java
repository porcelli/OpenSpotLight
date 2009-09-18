package org.openspotlight.tool.dap.language.java.asm;

import java.io.File;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.junit.Test;

public class TestASMCodeGen {

    @Test
    public void testASMExtract() {
        final CompiledTypesExtractorTask task = new CompiledTypesExtractorTask();
        task.setProject(new Project());

        final FileSet jreFileSet = new FileSet();
        jreFileSet.setDir(new File("."));
        jreFileSet.setIncludes("**/*.jar");
        //        jreFileSet.setIncludes("**/*.class");
        task.addCompiledArtifacts(jreFileSet);
        task.setName("SomeLibraryName");
        task.setVersion("1.0-GA");
        task.setXmlOutputFileName("./target/test-data/result.xml");

        task.execute();
    }
}
