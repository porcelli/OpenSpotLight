package org.openspotlight.federation.finder.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.openspotlight.common.util.Files;
import org.openspotlight.bundle.context.DefaultExecutionContextFactoryModule;
import org.openspotlight.federation.domain.artifact.Artifact;
import org.openspotlight.federation.domain.artifact.ChangeType;
import org.openspotlight.federation.domain.artifact.StringArtifact;
import org.openspotlight.federation.log.DetailedLoggerModule;
import org.openspotlight.graph.guice.SLGraphModule;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.persist.guice.SimplePersistModule;
import org.openspotlight.persist.support.SimplePersistCapable;
import org.openspotlight.persist.support.SimplePersistFactory;
import org.openspotlight.storage.StorageSessionport org.openspotlight.storage.domain.RegularPartitionitionition;
import org.openspotlight.storage.domain.node.StorageNode;
import org.openspotlight.storage.redis.guice.JRedisFactory;
import org.openspotlight.storage.redis.guice.JRedisStorageModule;
import org.openspotlight.storage.redis.util.ExampleRedisConfig;
import org.openspotlight.task.ExecutorInstance;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import static com.google.common.collect.Lists.newLinkedList;
import static org.openspotlight.common.util.Strings.concatPaths;
import static org.openspotlight.storage.STRepositoryPath.repositoryPath;

/**
 * Test class for {@link org.openspotlight.common.util.Files}
 *
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
@Ignore
public class FilesStressTest {
    private final Injector injector;

    public FilesStressTest() {
        injector = Guice.createInjector(new JRedisStorageModule(StStStorageSessionMode.AUTO,
                ExampleRedisConfig.EXAMPLE.getMappedServerConfig(),
                repositoryPath("repository")),
                new SimplePersistModule(), new DetailedLoggerModule(),
                new DefaultExecutionContextFactoryModule(), new SLGraphModule(DefaultJcrDescriptor.TEMP_DESCRIPTOR));

    }

    private static final String FROM = "../../", TO = "/tmp";


    @Test
    // 61s copying directories
    public void shouldLoadFileNamesFaster() throws Exception {


        Set<String> names = Files.listFileNamesFrom(FROM, false);
        List<Callable<Void>> callables = newLinkedList();
        for (final String fileName : names) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    File file = new File(fileName);
                    if (file.isDirectory()) return null;
                    FileInputStream fis = new FileInputStream(file);
                    String newDirName = concatPaths(TO, fileName.substring(0, fileName.lastIndexOf("/")));
                    new File(newDirName).mkdirs();
                    FileOutputStream fos = new FileOutputStream(concatPaths(TO, fileName));
                    IOUtils.copy(fis, fos);
                    return null;
                }
            });
        }
        List<Future<Void>> futures = ExecutorInstance.INSTANCE.invokeAll(callables);
        for (Future<Void> f : futures) {
            f.get();
        }

    }

    @Test
    // 16s reading directories and pasting on redis
    public void shouldLoadFileNamesUnderRedis() throws Exception {
        Set<String> names = Files.listFileNamesFrom(FROM, false);
        List<Callable<Void>> callables = newLinkedList();
        for (final String fileName : names) {
            callables.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    File file = new File(fileName);
                    if (file.isDirectory()) return null;
                    FileInputStream fis = new FileInputStream(file);
                    JRedisFactory factory = injector.getInstance(JRedisFactory.class);
                    ByteArrayOutputStream fos = new ByteArrayOutputStream();
                    IOUtils.copy(fis, fos);
                    factorRegularPartitionrPartitionrPartition.GRAPH).set(fileName.replaceAll("[ ]",""), fos.toByteArray());
                    return null;
                }
            });
        }
        List<Future<Void>> futures = ExecutorInstance.INSTANCE.invokeAll(callables);
        for (Future<Void> f : futures) {
            f.get();
        }
    }


@Test
    //30s (51s) (87s) to load 1/6 of the data
    //190s for all data 
    public void shouldLoadFileNamesUnderRedisUsingSimplePersist() throws Exception {
        Set<String> names = Files.listFileNamesFrom(FROM, false);
        List<Callable<Void>> callables = newLinkedList();
        int sizeByThree = names.size()/6;
        int i = 0;
        for (final String fileName : names) {
            if(i++>=sizeByThree) break;
            callables.add(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    File file = new File(fileName);
                    if (file.isDirectory()) return null;
                    FileInputStream fis = new FileInputStream(file);
                    StringArtifact artifact = Artifact.createArtifact(StringArtifact.class,fileName, ChangeType.INCLUDED);
                    artifact.setMappedTo(fileName);
                    artifact.setMappedFrom(fileName);
                    artifact.setLastChange(System.currentTimeMillis());
                    artifact.setOriginalName(fileName);
                    SimplePersistCapable<StorageNode, StorStorStorageSessionist = injector.getInstance(SimplePersistFactory.class).RegularPartitionegularPartitionegularPartition.GRAPH);
                    JRedisFactory factory = injector.getInstance(JRedisFactory.class);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                    String line;
                    List<String> content = newLinkedList();
                    while((line = reader.readLine())!=null){
                        content.add(line);
                    }
                    artifact.getContent().setTransient(content);
                    simplePersist.convertBeanToNode(artifact);
                    return null;
                }
            });
        }
        List<Future<Void>> futures = ExecutorInstance.INSTANCE.invokeAll(callables);
        for (Future<Void> f : futures) {
            f.get();
        }
    }

    @Test
        public void shouldLoadOneFileNamesUnderRedisUsingSimplePersist() throws Exception {
            Set<String> names = Files.listFileNamesFrom(FROM, false);
            final String fileName = names.iterator().next();

                        File file = new File(fileName);
                        FileInputStream fis = new FileInputStream(file);
                        StringArtifact artifact = Artifact.createArtifact(StringArtifact.class,fileName, ChangeType.INCLUDED);
                        artifact.setMappedTo(fileName);
                        artifact.setMappedFrom(fileName);
                        artifact.setLastChange(System.currentTimeMillis());
                        artifact.setOriginalName(fileName);
                        SimplePersistCapable<StorageNode, StoragStoragStorageSession= injector.getInstance(SimplePersistFactoRegularPartitionreatRegularPartitionist(RegularPartitions...GRAPH);
                        JRedisFactory factory = injector.getInstance(JRedisFactory.class);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                        String line;
                        List<String> content = newLinkedList();
                        while((line = reader.readLine())!=null){
                            content.add(line);
                        }
                        artifact.getContent().setTransient(content);
                        simplePersist.convertBeanToNode(artifact);

        }



}