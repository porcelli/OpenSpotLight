package org.openspotlight.conf;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;
import org.openspotlight.domain.ArtifactSource;
import org.openspotlight.domain.ArtifactSourceMapping;
import org.openspotlight.domain.Group;
import org.openspotlight.domain.Repository;
import org.openspotlight.domain.RepositoryBuilder;

public class RespoitoryBuilderTest {

    @Test
    public void testSimpleFileBuilder() {
        Repository repositorty = RepositoryBuilder.newRepositoryNamed("test").withGroup("OSL")
            .withArtifactSource("file://Users/porcelli/Documents/dev", "/osl")
                .withEncoding("UTF-8")
                .withArtifactMapping("osl-common")
                    .withIncludes("**/*.*")
                    .withExcludes("**/.DS_Store")
                    .withTask(CallableImplJust2Test.class)
            .andCreate();

        Assert.assertEquals("test", repositorty.getName());
        Assert.assertEquals(1, repositorty.getGroups().size());
        Group group = repositorty.getGroups().iterator().next();
        Assert.assertEquals("OSL", group.getName());
        Assert.assertEquals(1, group.getArtifactSources().size());
        ArtifactSource artifactSource = group.getArtifactSources().iterator().next();

        Assert.assertEquals("file://Users/porcelli/Documents/dev", artifactSource.getUrl());
        Assert.assertEquals("UTF-8", artifactSource.getEncodingForFileContent());
        Assert.assertEquals("/osl", artifactSource.getInitialLookup());

        Assert.assertEquals(1, artifactSource.getMappings().size());

        ArtifactSourceMapping mapping = artifactSource.getMappings().iterator().next();

        Assert.assertEquals(1, mapping.getIncludeds().size());
        Assert.assertEquals("osl-common", mapping.getFrom());
        Assert.assertEquals("**/*.*", mapping.getIncludeds().iterator().next());
        Assert.assertEquals("**/.DS_Store", mapping.getExcludeds().iterator().next());
        Assert.assertEquals(1, mapping.getTasks().size());
        Assert.assertEquals(CallableImplJust2Test.class, mapping.getTasks().get(0));
    }

    @Test
    public void testSimpleFileBuilder2() {
        Repository repositorty = RepositoryBuilder.newRepositoryNamed("test").withGroup("OSL")
            .withArtifactSource("file://Users/porcelli/Documents/dev", "/osl")
                .withEncoding("UTF-8")
                .withArtifactMapping("osl-common")
                    .withIncludes("**/*.*")
                    .withExcludes("**/.DS_Store")
                    .withTask(CallableImplJust2Test.class)
                .andWithAnotherArtifactMapping("osl-conf")
                    .withIncludes("**/*.*")
                    .withExcludes("**/.DS_Store")
                    .withTask(CallableImplJust2Test.class)
            .andCreate();

        Assert.assertEquals("test", repositorty.getName());
        Assert.assertEquals(1, repositorty.getGroups().size());
        Group group = repositorty.getGroups().iterator().next();
        Assert.assertEquals("OSL", group.getName());
        Assert.assertEquals(1, group.getArtifactSources().size());
        ArtifactSource artifactSource = group.getArtifactSources().iterator().next();

        Assert.assertEquals("file://Users/porcelli/Documents/dev", artifactSource.getUrl());
        Assert.assertEquals("UTF-8", artifactSource.getEncodingForFileContent());
        Assert.assertEquals("/osl", artifactSource.getInitialLookup());

        Assert.assertEquals(2, artifactSource.getMappings().size());

        for (ArtifactSourceMapping mapping: artifactSource.getMappings()) {
            Assert.assertEquals(1, mapping.getIncludeds().size());
            if (!mapping.getFrom().equals("osl-common") && !mapping.getFrom().equals("osl-conf")) {
                Assert.fail();
            }
            Assert.assertEquals("**/*.*", mapping.getIncludeds().iterator().next());
            Assert.assertEquals("**/.DS_Store", mapping.getExcludeds().iterator().next());
            Assert.assertEquals(1, mapping.getTasks().size());
            Assert.assertEquals(CallableImplJust2Test.class, mapping.getTasks().get(0));
        }
    }

    @Test
    public void testSimpleFileBuilder3() {
        Repository repositorty = RepositoryBuilder.newRepositoryNamed("test").withGroup("OSL")
            .withArtifactSource("file://Users/porcelli/Documents/dev", "/osl")
                .withEncoding("UTF-8")
                .withTask(CallableImplJust2Test.class)
                .withArtifactMapping("osl-common")
                    .withIncludes("**/*.*")
                    .withExcludes("**/.DS_Store")
                .andWithAnotherArtifactMapping("osl-conf")
                    .withIncludes("**/*.*")
                    .withExcludes("**/.DS_Store")
            .andCreate();

        Assert.assertEquals("test", repositorty.getName());
        Assert.assertEquals(1, repositorty.getGroups().size());
        Group group = repositorty.getGroups().iterator().next();
        Assert.assertEquals("OSL", group.getName());
        Assert.assertEquals(1, group.getArtifactSources().size());
        ArtifactSource artifactSource = group.getArtifactSources().iterator().next();

        Assert.assertEquals("file://Users/porcelli/Documents/dev", artifactSource.getUrl());
        Assert.assertEquals("UTF-8", artifactSource.getEncodingForFileContent());
        Assert.assertEquals("/osl", artifactSource.getInitialLookup());
        Assert.assertEquals(1, artifactSource.getTasks().size());
        Assert.assertEquals(CallableImplJust2Test.class, artifactSource.getTasks().get(0));

        Assert.assertEquals(2, artifactSource.getMappings().size());

        for (ArtifactSourceMapping mapping: artifactSource.getMappings()) {
            Assert.assertEquals(1, mapping.getIncludeds().size());
            if (!mapping.getFrom().equals("osl-common") && !mapping.getFrom().equals("osl-conf")) {
                Assert.fail();
            }
            Assert.assertEquals("**/*.*", mapping.getIncludeds().iterator().next());
            Assert.assertEquals("**/.DS_Store", mapping.getExcludeds().iterator().next());
            Assert.assertEquals(0, mapping.getTasks().size());
        }
    }

    @Test
    public void testFileBuilderWithMoreGroups() {
        Repository repositorty = RepositoryBuilder.newRepositoryNamed("test")
            .withGroup("MyCompanyName")
                .withSubGroup("MyItDepartment")
                    .withSubGroup("OSL")
                        .withArtifactSource("file://Users/porcelli/Documents/dev", "/osl")
                            .withEncoding("UTF-8")
                            .withTask(CallableImplJust2Test.class)
                            .withArtifactMapping("osl-common")
                                .withIncludes("**/*.*")
                                .withExcludes("**/.DS_Store")
                            .andWithAnotherArtifactMapping("osl-conf")
                                .withIncludes("**/*.*")
                                .withExcludes("**/.DS_Store")
                    .andWithAnotherGroup("OSLInterface")
                        .withArtifactSource("file://Users/porcelli/Documents/dev", "/osl-interface")
                            .withEncoding("UTF-8")
                            .withTask(CallableImplJust2Test.class)
                            .withArtifactMapping("osl-common")
                                .withIncludes("**/*.*")
                                .withExcludes("**/.DS_Store")
            .andCreate();

        Assert.assertEquals("test", repositorty.getName());
        Assert.assertEquals(1, repositorty.getGroups().size());
        Group companyGroup = repositorty.getGroups().iterator().next();
        Assert.assertEquals("MyCompanyName", companyGroup.getName());
        Assert.assertEquals(0, companyGroup.getArtifactSources().size());

        Assert.assertEquals(1, companyGroup.getGroups().size());
        Group itGroup = companyGroup.getGroups().iterator().next();
        Assert.assertEquals("MyItDepartment", itGroup.getName());
        Assert.assertEquals(0, itGroup.getArtifactSources().size());

        Assert.assertEquals(2, itGroup.getGroups().size());

        Iterator<Group> oslIt = itGroup.getGroups().iterator();
        Group oslGroup = oslIt.next();

        Assert.assertEquals("OSL", oslGroup.getName());
        Assert.assertEquals(1, oslGroup.getArtifactSources().size());

        ArtifactSource artifactSource = oslGroup.getArtifactSources().iterator().next();

        Assert.assertEquals("file://Users/porcelli/Documents/dev", artifactSource.getUrl());
        Assert.assertEquals("UTF-8", artifactSource.getEncodingForFileContent());
        Assert.assertEquals("/osl", artifactSource.getInitialLookup());
        Assert.assertEquals(1, artifactSource.getTasks().size());
        Assert.assertEquals(CallableImplJust2Test.class, artifactSource.getTasks().get(0));

        Assert.assertEquals(2, artifactSource.getMappings().size());

        for (ArtifactSourceMapping mapping: artifactSource.getMappings()) {
            Assert.assertEquals(1, mapping.getIncludeds().size());
            if (!mapping.getFrom().equals("osl-common") && !mapping.getFrom().equals("osl-conf")) {
                Assert.fail();
            }
            Assert.assertEquals("**/*.*", mapping.getIncludeds().iterator().next());
            Assert.assertEquals("**/.DS_Store", mapping.getExcludeds().iterator().next());
            Assert.assertEquals(0, mapping.getTasks().size());
        }

        Group oslInterfaceGroup = oslIt.next();

        Assert.assertEquals("OSLInterface", oslInterfaceGroup.getName());
        Assert.assertEquals(1, oslInterfaceGroup.getArtifactSources().size());

        ArtifactSource artifactSourceInterface = oslInterfaceGroup.getArtifactSources().iterator().next();

        Assert.assertEquals("file://Users/porcelli/Documents/dev", artifactSourceInterface.getUrl());
        Assert.assertEquals("UTF-8", artifactSourceInterface.getEncodingForFileContent());
        Assert.assertEquals("/osl-interface", artifactSourceInterface.getInitialLookup());
        Assert.assertEquals(1, artifactSourceInterface.getTasks().size());
        Assert.assertEquals(CallableImplJust2Test.class, artifactSourceInterface.getTasks().get(0));

        Assert.assertEquals(1, artifactSourceInterface.getMappings().size());

        Iterator<ArtifactSourceMapping> itInterface = artifactSourceInterface.getMappings().iterator();
        ArtifactSourceMapping mappingInterface = itInterface.next();

        Assert.assertEquals(1, mappingInterface.getIncludeds().size());
        Assert.assertEquals("osl-common", mappingInterface.getFrom());
        Assert.assertEquals("**/*.*", mappingInterface.getIncludeds().iterator().next());
        Assert.assertEquals("**/.DS_Store", mappingInterface.getExcludeds().iterator().next());
    }

    @Test
    public void testFileBuilderWithMoreGroupsUsingBackOperations() {
        Repository repositorty = RepositoryBuilder.newRepositoryNamed("test")
            .withGroup("MyCompanyName")
                .withSubGroup("MyItDepartment")
                    .withSubGroup("OSL")
                        .withArtifactSource("file://Users/porcelli/Documents/dev", "/osl")
                            .withEncoding("UTF-8")
                            .withTask(CallableImplJust2Test.class)
                            .withArtifactMapping("osl-common")
                                .withIncludes("**/*.*")
                                .withExcludes("**/.DS_Store")
                            .andWithAnotherArtifactMapping("osl-conf")
                                .withIncludes("**/*.*")
                                .withExcludes("**/.DS_Store")
                    .back2Group()
                .back2Group()
            .back2Group()
                .andWithAnotherGroup("MyMktDepartment")
                    .andWithAnotherGroup("OSLInterface")
                        .withArtifactSource("file://Users/porcelli/Documents/dev", "/osl-interface")
                            .withEncoding("UTF-8")
                            .withTask(CallableImplJust2Test.class)
                            .withArtifactMapping("osl-common")
                                .withIncludes("**/*.*")
                                .withExcludes("**/.DS_Store")
            .andCreate();

        Assert.assertEquals("test", repositorty.getName());
        Assert.assertEquals(1, repositorty.getGroups().size());
        Group companyGroup = repositorty.getGroups().iterator().next();
        Assert.assertEquals("MyCompanyName", companyGroup.getName());
        Assert.assertEquals(0, companyGroup.getArtifactSources().size());

        Assert.assertEquals(2, companyGroup.getGroups().size());
        Iterator<Group> itCompany = companyGroup.getGroups().iterator();

        Group itGroup = itCompany.next();

        Assert.assertEquals("MyItDepartment", itGroup.getName());
        Assert.assertEquals(0, itGroup.getArtifactSources().size());

        Assert.assertEquals(1, itGroup.getGroups().size());

        Group oslGroup = itGroup.getGroups().iterator().next();

        Assert.assertEquals("OSL", oslGroup.getName());
        Assert.assertEquals(1, oslGroup.getArtifactSources().size());

        ArtifactSource artifactSource = oslGroup.getArtifactSources().iterator().next();

        Assert.assertEquals("file://Users/porcelli/Documents/dev", artifactSource.getUrl());
        Assert.assertEquals("UTF-8", artifactSource.getEncodingForFileContent());
        Assert.assertEquals("/osl", artifactSource.getInitialLookup());
        Assert.assertEquals(1, artifactSource.getTasks().size());
        Assert.assertEquals(CallableImplJust2Test.class, artifactSource.getTasks().get(0));

        Assert.assertEquals(2, artifactSource.getMappings().size());

        for (ArtifactSourceMapping mapping: artifactSource.getMappings()) {
            Assert.assertEquals(1, mapping.getIncludeds().size());
            if (!mapping.getFrom().equals("osl-common") && !mapping.getFrom().equals("osl-conf")) {
                Assert.fail();
            }
            Assert.assertEquals("**/*.*", mapping.getIncludeds().iterator().next());
            Assert.assertEquals("**/.DS_Store", mapping.getExcludeds().iterator().next());
            Assert.assertEquals(0, mapping.getTasks().size());
        }

        Group mktGroup = itCompany.next();

        Assert.assertEquals("MyMktDepartment", mktGroup.getName());
        Assert.assertEquals(0, mktGroup.getArtifactSources().size());

        Assert.assertEquals(1, mktGroup.getGroups().size());

        Group oslInterfaceGroup = mktGroup.getGroups().iterator().next();

        Assert.assertEquals("OSLInterface", oslInterfaceGroup.getName());
        Assert.assertEquals(1, oslInterfaceGroup.getArtifactSources().size());

        ArtifactSource artifactSourceInterface = oslInterfaceGroup.getArtifactSources().iterator().next();

        Assert.assertEquals("file://Users/porcelli/Documents/dev", artifactSourceInterface.getUrl());
        Assert.assertEquals("UTF-8", artifactSourceInterface.getEncodingForFileContent());
        Assert.assertEquals("/osl-interface", artifactSourceInterface.getInitialLookup());
        Assert.assertEquals(1, artifactSourceInterface.getTasks().size());
        Assert.assertEquals(CallableImplJust2Test.class, artifactSourceInterface.getTasks().get(0));

        Assert.assertEquals(1, artifactSourceInterface.getMappings().size());

        Iterator<ArtifactSourceMapping> itInterface = artifactSourceInterface.getMappings().iterator();
        ArtifactSourceMapping mappingInterface = itInterface.next();

        Assert.assertEquals(1, mappingInterface.getIncludeds().size());
        Assert.assertEquals("osl-common", mappingInterface.getFrom());
        Assert.assertEquals("**/*.*", mappingInterface.getIncludeds().iterator().next());
        Assert.assertEquals("**/.DS_Store", mappingInterface.getExcludeds().iterator().next());
    }

}
