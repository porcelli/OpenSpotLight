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
package org.openspotlight.tool.dap.language.java.asm;

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

import org.objectweb.asm.ClassReader;
import org.openspotlight.tool.dap.language.java.asm.model.TypeDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompiledTypesExtractor {

    private final Logger LOG = LoggerFactory.getLogger(CompiledTypesExtractor.class);

    public List<TypeDefinition> getJavaTypes( Set<File> artifacts ) {
        int count = 0;
        List<TypeDefinition> scannedTypes = new LinkedList<TypeDefinition>();
        try {
            for (File activeArtifact : artifacts) {
                if (activeArtifact.getName().endsWith(".jar")) {
                    LOG.info(String.format("Opening JAR Artifact \"%s\".", activeArtifact.getCanonicalFile()));
                    // Open Zip file for reading
                    ZipFile zipFile = new ZipFile(activeArtifact, ZipFile.OPEN_READ);

                    // Create an enumeration of the entries in the zip file
                    Enumeration<? extends ZipEntry> zipFileEntries = zipFile.entries();

                    // Process each entry
                    while (zipFileEntries.hasMoreElements()) {
                        // grab a zip file entry
                        ZipEntry entry = zipFileEntries.nextElement();
                        // extract file if not a directory
                        if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                            LOG.info(String.format("\tExtracting .class Type \"%s\".", entry.getName()));
                            TypeDefinition type = processCompiledInputStream(zipFile.getInputStream(entry));
                            count++;
                            scannedTypes.add(type);
                        }
                    }
                    LOG.info(String.format("Closing JAR Artifact \"%s\"", activeArtifact.getCanonicalFile()));
                    zipFile.close();
                } else if (activeArtifact.getName().endsWith(".class")) {
                    LOG.info(String.format("Extracting .class Type \"%s\".", activeArtifact.getCanonicalPath()));
                    TypeDefinition type = processCompiledInputStream(new FileInputStream(activeArtifact));
                    count++;
                    scannedTypes.add(type);
                }
            }
        } catch (Exception ex) {
            StringWriter sWriter = new StringWriter();
            ex.printStackTrace(new PrintWriter(sWriter));
            LOG.error("Problems during parser - Stack trace:\n" + sWriter.getBuffer().toString());
        } finally {
            LOG.info(String.format("Finished processing %d types.", count));
        }
        return scannedTypes;
    }

    private TypeDefinition processCompiledInputStream( InputStream stream ) throws IOException {
        ClassReader reader = new ClassReader(stream);
        TypeExtractorVisitor asmVisitor = new TypeExtractorVisitor();
        reader.accept(asmVisitor, 0);
        stream.close();
        //count++;
        return asmVisitor.getType();
    }
}
