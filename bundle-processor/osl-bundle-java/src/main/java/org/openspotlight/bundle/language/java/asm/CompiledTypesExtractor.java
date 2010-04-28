/*
 * OpenSpotLight - Open Source IT Governance Platform
 *
 * Copyright (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA
 * or third-party contributors as indicated by the @author tags or express
 * copyright attribution statements applied by the authors.  All third-party
 * contributions are distributed under license by CARAVELATECH CONSULTORIA E
 * TECNOLOGIA EM INFORMATICA LTDA.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License  for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 *
 ***********************************************************************
 * OpenSpotLight - Plataforma de Governança de TI de Código Aberto
 *
 * Direitos Autorais Reservados (c) 2009, CARAVELATECH CONSULTORIA E TECNOLOGIA
 * EM INFORMATICA LTDA ou como contribuidores terceiros indicados pela etiqueta
 * @author ou por expressa atribuição de direito autoral declarada e atribuída pelo autor.
 * Todas as contribuições de terceiros estão distribuídas sob licença da
 * CARAVELATECH CONSULTORIA E TECNOLOGIA EM INFORMATICA LTDA.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo sob os
 * termos da Licença Pública Geral Menor do GNU conforme publicada pela Free Software
 * Foundation.
 *
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM NENHUMA
 * GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU ADEQUAÇÃO A UMA
 * FINALIDADE ESPECÍFICA. Consulte a Licença Pública Geral Menor do GNU para mais detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral Menor do GNU junto com este
 * programa; se não, escreva para:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.openspotlight.bundle.language.java.asm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.objectweb.asm.ClassReader;
import org.openspotlight.bundle.language.java.asm.model.TypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extracts from compiled artifacts all java types definitions.
 * 
 * @see TypeDefinition
 * @author porcelli
 */
public class CompiledTypesExtractor {

    /** The LOG. */
    private final Logger LOG = LoggerFactory.getLogger(CompiledTypesExtractor.class);

    /**
     * Generates a list of {@link TypeDefinition} based on a set of artifacts (.jar or .class files).
     * 
     * @param artifacts the artifacts. Allowed artifacts: .jar and .class .
     * @return the java types
     */
    public List<TypeDefinition> getJavaTypes( final InputStream inputStream,
                                              final String artifactName ) {
        int count = 0;
        final List<TypeDefinition> scannedTypes = new LinkedList<TypeDefinition>();
        try {
            if (artifactName.endsWith(".jar")) {
                LOG.info(String.format("Opening JAR Artifact \"%s\".", artifactName));
                // Open Zip file for reading
                final ZipInputStream zipStream = new ZipInputStream(inputStream);

                ZipEntry entry = null;
                // Process each entry
                while ((entry = zipStream.getNextEntry()) != null) {
                    // extract file if not a directory
                    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                        LOG.info(String.format("\tExtracting .class Type \"%s\".", entry.getName()));
                        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        for (int c = zipStream.read(); c != -1; c = zipStream.read()) {
                            baos.write(c);
                        }
                        baos.close();
                        final TypeDefinition type = processCompiledInputStream(new ByteArrayInputStream(baos.toByteArray()));
                        zipStream.closeEntry();
                        count++;
                        scannedTypes.add(type);
                    }
                }
                LOG.info(String.format("Closing JAR Artifact \"%s\"", artifactName));
                zipStream.close();
            } else if (artifactName.endsWith(".class")) {
                LOG.info(String.format("Extracting .class Type \"%s\".", artifactName));
                final TypeDefinition type = processCompiledInputStream(inputStream);
                count++;
                scannedTypes.add(type);
            }
        } catch (final Exception ex) {
            final StringWriter sWriter = new StringWriter();
            ex.printStackTrace(new PrintWriter(sWriter));
            LOG.error("Problems during parser - Stack trace:\n" + sWriter.getBuffer().toString());
        } finally {
            LOG.info(String.format("Finished processing %d types.", count));
        }
        return scannedTypes;
    }

    /**
     * Generates a list of {@link TypeDefinition} based on a set of artifacts (.jar or .class files).
     * 
     * @param artifacts the artifacts. Allowed artifacts: .jar and .class .
     * @return the java types
     */
    public List<TypeDefinition> getJavaTypes( final Set<File> artifacts ) {
        int count = 0;
        final List<TypeDefinition> scannedTypes = new LinkedList<TypeDefinition>();
        try {
            for (final File activeArtifact : artifacts) {
                if (activeArtifact.getName().endsWith(".jar")) {
                    LOG.info(String.format("Opening JAR Artifact \"%s\".", activeArtifact.getCanonicalFile()));
                    // Open Zip file for reading
                    final ZipFile zipFile = new ZipFile(activeArtifact, ZipFile.OPEN_READ);

                    // Create an enumeration of the entries in the zip file
                    final Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();

                    // Process each entry
                    while (zipFileEntries.hasMoreElements()) {
                        // grab a zip file entry
                        final ZipEntry entry = zipFileEntries.nextElement();
                        // extract file if not a directory
                        if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                            LOG.info(String.format("\tExtracting .class Type \"%s\".", entry.getName()));
                            final TypeDefinition type = processCompiledInputStream(zipFile.getInputStream(entry));
                            count++;
                            scannedTypes.add(type);
                        }
                    }
                    LOG.info(String.format("Closing JAR Artifact \"%s\"", activeArtifact.getCanonicalFile()));
                    zipFile.close();
                } else if (activeArtifact.getName().endsWith(".class")) {
                    LOG.info(String.format("Extracting .class Type \"%s\".", activeArtifact.getCanonicalPath()));
                    final TypeDefinition type = processCompiledInputStream(new FileInputStream(activeArtifact));
                    count++;
                    scannedTypes.add(type);
                }
            }
        } catch (final Exception ex) {
            final StringWriter sWriter = new StringWriter();
            ex.printStackTrace(new PrintWriter(sWriter));
            LOG.error("Problems during parser - Stack trace:\n" + sWriter.getBuffer().toString());
        } finally {
            LOG.info(String.format("Finished processing %d types.", count));
        }
        return scannedTypes;
    }

    /**
     * Process the compiled input stream using {@link TypeExtractorVisitor}.
     * 
     * @param stream the stream
     * @return the type definition
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private TypeDefinition processCompiledInputStream( final InputStream stream ) throws IOException {
        final ClassReader reader = new ClassReader(stream);
        final TypeExtractorVisitor visitor = new TypeExtractorVisitor();
        reader.accept(visitor, 0);
        stream.close();
        return visitor.getType();
    }
}
