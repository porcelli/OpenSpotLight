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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.openspotlight.tool.dap.language.java.asm.model.JavaType;
import org.openspotlight.tool.dap.language.java.asm.model.MethodDeclaration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.javabean.JavaBeanConverter;

public class CompiledTypesExtractorTask extends Task {

    private final Logger LOG               = LoggerFactory.getLogger(CompiledTypesExtractorTask.class);
    private Set<File>    compiledArtifacts = new HashSet<File>();
    private String       xmlOutputFileName = "";

    public void setXmlOutputFileName( String xmlOutputFileName ) {
        this.xmlOutputFileName = xmlOutputFileName;
    }

    public void addCompiledArtifacts( FileSet artifactSet ) {
        DirectoryScanner scanner = artifactSet.getDirectoryScanner(getProject());
        for (String activeFileName : scanner.getIncludedFiles()) {
            File file = new File(artifactSet.getDir(getProject()), activeFileName);
            compiledArtifacts.add(file);
        }
    }

    public void execute() {
        if (isValid()) {
            try {
                CompiledTypesExtractor typeExtractor = new CompiledTypesExtractor();
                List<JavaType> scannedTypes = typeExtractor.getJavaTypes(compiledArtifacts);

                //XML Output Generation
                LOG.info("Starting XML Output generation.");
                XStream xstream = new XStream();
                xstream.aliasPackage("", "org.openspotlight.tool.dap.language.java.asm.model");
                xstream.alias("List", LinkedList.class);

                xstream.registerConverter(new JavaBeanConverter(xstream.getMapper()) {
                    @SuppressWarnings( "unchecked" )
                    public boolean canConvert( Class type ) {
                        return type.getName() == MethodDeclaration.class.getName();
                    }
                });
                xstream.toXML(scannedTypes, new FileOutputStream(xmlOutputFileName));
                LOG.info("Finished XML output file.");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        } else {
            LOG.error("Invalid State: Missing XmlOutputFileName.");
        }
    }

    private boolean isValid() {
        if (xmlOutputFileName == null || xmlOutputFileName.trim().length() == 0) {
            return false;
        }
        return true;
    }
}
