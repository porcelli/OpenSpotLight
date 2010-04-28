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
package org.openspotlight.bundle.language.java.bundle.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.junit.Ignore;
import org.openspotlight.jcr.provider.DefaultJcrDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionDescriptor;
import org.openspotlight.jcr.provider.JcrConnectionProvider;
import org.openspotlight.jcr.provider.SessionWithLock;

@Ignore
public class ClasspathImport {

    private static File extractZippedOnTempFile() throws IOException, FileNotFoundException {
        final File temp = File.createTempFile("stress-data", ".xml");
        final ZipInputStream zis = new ZipInputStream(new FileInputStream("src/test/resources/data/exported-stress-data.xml.zip"));
        final OutputStream fos = new BufferedOutputStream(new FileOutputStream(temp));
        final byte data[] = new byte[2048];
        int count;
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            if (entry.getName().endsWith("xml")) {
                while ((count = zis.read(data, 0, 2048)) != -1) {
                    fos.write(data, 0, count);
                }
                break;
            }
        }
        fos.flush();
        fos.close();
        zis.close();
        return temp;
    }

    public static void importClassPathData( final JcrConnectionDescriptor descriptor )
        throws IOException, FileNotFoundException, RepositoryException, PathNotFoundException, ItemExistsException,
        ConstraintViolationException, VersionException, InvalidSerializedDataException, LockException, AccessDeniedException,
        InvalidItemStateException, NoSuchNodeTypeException {
        final File temp = extractZippedOnTempFile();
        importDataFromFile(temp, descriptor);
    }

    private static void importDataFromFile( final File temp,
                                            final JcrConnectionDescriptor descriptor )
        throws FileNotFoundException, RepositoryException, IOException, PathNotFoundException, ItemExistsException,
        ConstraintViolationException, VersionException, InvalidSerializedDataException, LockException, AccessDeniedException,
        InvalidItemStateException, NoSuchNodeTypeException {
        final InputStream is = new FileInputStream(temp);
        final JcrConnectionProvider desc = JcrConnectionProvider.createFromData(descriptor);
        desc.closeRepositoryAndCleanResources();
        final SessionWithLock session = desc.openSession();
        final Node root = session.getRootNode();
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(is);
        session.importXML(root.getPath(), bufferedInputStream, ImportUUIDBehavior.IMPORT_UUID_COLLISION_REMOVE_EXISTING);
        session.save();
        session.logout();
    }

    public static void main( final String... args ) throws Exception {
        importClassPathData(DefaultJcrDescriptor.TEMP_DESCRIPTOR);

    }

}
