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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * The Class SerializationUtil.
 * 
 * @author Vitor Hugo Chagas
 */
public class SerializationUtil {
	
	/**
	 * Serialize.
	 * 
	 * @param object the object
	 * 
	 * @return the input stream
	 * 
	 * @throws SerializationUtilException the serialization util exception
	 */
	public static InputStream serialize(Object object) throws SerializationUtilException {
		PipedOutputStream out = null;
		PipedInputStream in = null;
		ObjectOutputStream oos = null;
		try {
	    	out = new PipedOutputStream();
	    	in = new PipedInputStream(out);
	    	oos = new ObjectOutputStream(out);
	    	oos.writeObject(object);
	    	oos.close();
	    	return in;
		}
		catch (IOException e) {
			throw new SerializationUtilException("Error on attempt to serialize object.", e);
		}
		finally {
			close(oos, out);
		}
	}
	
	/**
	 * Deserialize.
	 * 
	 * @param inputStream the input stream
	 * 
	 * @return the object
	 * 
	 * @throws SerializationUtilException the serialization util exception
	 */
	public static Object deserialize(InputStream inputStream) throws SerializationUtilException {
		ObjectInputStream ois = null; 
		try {
			ois = new ObjectInputStream(inputStream);
			return ois.readObject();
		}
		catch (Exception e) {
			throw new SerializationUtilException("Error on attempt to deserialize object.", e);
		}
		finally {
			close(ois);
		}
	}
	
	/**
	 * Close.
	 * 
	 * @param inputStreams the input streams
	 */
	private static void close(InputStream...inputStreams) {
		for (InputStream inputStream : inputStreams) {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException e) {}
			}
		}
	}
	
	/**
	 * Close.
	 * 
	 * @param outputStreams the output streams
	 */
	private static void close(OutputStream...outputStreams) {
		for (OutputStream outputStream : outputStreams) {
			if (outputStream != null) {
				try {
					outputStream.close();
				}
				catch (IOException e) {}
			}
		}
	}

}
