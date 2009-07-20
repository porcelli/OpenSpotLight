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

package org.openspotlight.common.util;

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrow;
import static org.openspotlight.common.util.Exceptions.logAndThrowNew;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.openspotlight.common.exception.SLException;

/**
 * Set of static methods for serialization purposes
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 * 
 */
public class Serialization {
    
    /**
     * Reads an object from a base64 string.
     * 
     * @param <E>
     * @param string
     * @return the serialized object
     * @throws SLException
     */
    @SuppressWarnings("unchecked")
    public static <E extends Serializable> E readFromBase64(final String string)
            throws SLException {
        checkNotNull("string", string); //$NON-NLS-1$
        try {
            final byte[] base64encoded = string.getBytes();
            final byte[] base64decoded = decodeBase64(base64encoded);
            final E result = (E) readFromBytes(base64decoded);
            return result;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }
    
    /**
     * Reads an object from serialized data.
     * 
     * @param <E>
     * @param bytes
     * @return the serialized object
     * @throws SLException
     */
    @SuppressWarnings("unchecked")
    public static <E extends Serializable> E readFromBytes(final byte[] bytes)
            throws SLException {
        checkNotNull("bytes", bytes);//$NON-NLS-1$
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final E result = (E) readFromInputStream(bais);
            return result;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }
    
    /**
     * Reads an object from an output stream.
     * 
     * @param <E>
     * @param inputStream
     * @return the serialized object
     * @throws SLException
     */
    @SuppressWarnings("unchecked")
    public static <E extends Serializable> E readFromInputStream(
            final InputStream inputStream) throws SLException {
        checkNotNull("inputStream", inputStream);//$NON-NLS-1$
        try {
            final ObjectInputStream ois = new ObjectInputStream(inputStream);
            final E result = (E) ois.readObject();
            ois.close();
            return result;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }
    
    /**
     * Serialize the object passed as parameter to a base64 string.
     * 
     * @param <E>
     * @param object
     * @return a base64 string
     * @throws SLException
     */
    public static <E extends Serializable> String serializeToBase64(
            final E object) throws SLException {
        checkNotNull("object", object);//$NON-NLS-1$
        try {
            final byte[] resultAsByte = serializeToBytes(object);
            final byte[] resultAsBase64 = encodeBase64(resultAsByte);
            final String base64String = new String(resultAsBase64);
            return base64String;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }
    
    /**
     * Returns a byte array with the object serialized.
     * 
     * @param <E>
     * @param object
     * @return a byte array
     * @throws SLException
     */
    public static <E extends Serializable> byte[] serializeToBytes(
            final E object) throws SLException {
        checkNotNull("object", object);//$NON-NLS-1$
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            serializeToOutputStream(object, baos);
            final byte[] result = baos.toByteArray();
            return result;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }
    
    /**
     * Serialize the object on a output stream passed as parameter.
     * 
     * @param <E>
     * @param object
     * @param outputStream
     * @throws SLException
     */
    public static <E extends Serializable> void serializeToOutputStream(
            final E object, final OutputStream outputStream) throws SLException {
        checkNotNull("object", object);//$NON-NLS-1$
        checkNotNull("outputStream", outputStream);//$NON-NLS-1$
        try {
            final ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (final Exception e) {
            logAndThrowNew(e, SLException.class);
        }
    }
    
    /**
     * Should not be instantiated
     */
    private Serialization() {
        logAndThrow(new IllegalStateException(Messages
                .getString("invalidConstructor"))); //$NON-NLS-1$
    }
    
}
