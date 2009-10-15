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

import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.openspotlight.common.util.Assertions.checkNotNull;
import static org.openspotlight.common.util.Exceptions.logAndReturnNew;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

import org.jasypt.util.digest.Digester;
import org.openspotlight.common.exception.SLException;

/**
 * Class with sha1 signature method.
 * 
 * @author Luiz Fernando Teston - feu.teston@caravelatech.com
 */
public class Sha1 {

    /**
     * SHA-1 digester.
     */
    private static final Digester DIGESTER = new Digester("SHA-1"); //$NON-NLS-1$

    /**
     * Returns a sha-1 signature for that content.
     * 
     * @param content
     * @return a byte array representing the signature
     * @throws SLException
     */
    public static byte[] getSha1Signature( final byte[] content ) throws SLException {
        checkNotNull("content", content);//$NON-NLS-1$
        try {
            return DIGESTER.digest(content);
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }

    /**
     * A syntax sugar method that returns a sha-1 signature for that content.
     * 
     * @param content
     * @return a byte array representing the signature
     * @throws SLException
     */
    public static byte[] getSha1Signature( final String content ) throws SLException {
        return getSha1Signature(content.getBytes());
    }

    /**
     * Returns a sha-1 signature for that content as a base64 string.
     * 
     * @param content
     * @return a base64 string representing the signature
     * @throws SLException
     */
    public static String getSha1SignatureEncodedAsBase64( final byte[] content ) throws SLException {
        checkNotNull("content", content);//$NON-NLS-1$
        try {
            final byte[] sha1 = getSha1Signature(content);
            final String result = new String(encodeBase64(sha1));
            return result;
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }

    /**
     * A syntax sugar method that returns a sha-1 signature for that content as an Hexa string.
     * 
     * @param content
     * @return a base64 string representing the signature
     * @throws SLException
     */
    public static String getSha1SignatureEncodedAsHexa( final String content )
        throws SLException {
        return getSha1SignatureEncodedAsHexa(content.getBytes());
    }

    public static String getSha1SignatureEncodedAsBase64( final String content ) throws SLException {
        return getSha1SignatureEncodedAsBase64(content.getBytes());
    }

    /**
     * Returns a sha-1 signature for that content as an Hexa string.
     * 
     * @param content
     * @return a base64 string representing the signature
     * @throws SLException
     */
    public static String getSha1SignatureEncodedAsHexa( final byte[] content )
        throws SLException {
        checkNotNull("content", content);//$NON-NLS-1$
        try {
            final byte[] sha1 = getSha1Signature(content);
            return toHexa(sha1);
        } catch (final Exception e) {
            throw logAndReturnNew(e, SLException.class);
        }
    }

    private static String toHexa( byte[] bytes ) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
            int parteBaixa = bytes[i] & 0xf;
            if (parteAlta == 0) s.append('0');
            s.append(Integer.toHexString(parteAlta | parteBaixa));
        }
        return s.toString();

    /**
     * Should not be instantiated
     */
    private Sha1() {
        logAndThrow(new IllegalStateException(Messages.getString("invalidConstructor"))); //$NON-NLS-1$
    }

}
