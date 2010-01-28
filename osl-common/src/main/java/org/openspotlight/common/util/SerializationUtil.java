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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

import org.openspotlight.common.exception.SerializationUtilException;

/**
 * The Class SerializationUtil.
 * 
 * @author Vitor Hugo Chagas
 */
public class SerializationUtil {

	private static class CloneInput extends ObjectInputStream {
		private final CloneOutput output;

		CloneInput(final InputStream in, final CloneOutput output)
				throws IOException {
			super(in);
			this.output = output;
		}

		@Override
		protected Class<?> resolveClass(final ObjectStreamClass osc)
				throws IOException, ClassNotFoundException {
			final Class<?> c = output.classQueue.poll();
			final String expected = osc.getName();
			final String found = c == null ? null : c.getName();
			if (!expected.equals(found)) {
				throw new InvalidClassException("Classes desynchronized: "
						+ "found " + found + " when expecting " + expected);
			}
			return c;
		}

		@Override
		protected Class<?> resolveProxyClass(final String[] interfaceNames)
				throws IOException, ClassNotFoundException {
			return output.classQueue.poll();
		}
	}

	private static class CloneOutput extends ObjectOutputStream {
		Queue<Class<?>> classQueue = new LinkedList<Class<?>>();

		CloneOutput(final OutputStream out) throws IOException {
			super(out);
		}

		@Override
		protected void annotateClass(final Class<?> c) {
			classQueue.add(c);
		}

		@Override
		protected void annotateProxyClass(final Class<?> c) {
			classQueue.add(c);
		}
	}

	/**
	 * Clone.
	 * 
	 * @param x
	 *            the x
	 * 
	 * @return the t
	 */
	public static <T> T clone(final T x) {
		try {
			return cloneX(x);
		} catch (final IOException e) {
			throw new IllegalArgumentException(e);
		} catch (final ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * Clone x.
	 * 
	 * @param x
	 *            the x
	 * 
	 * @return the t
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	private static <T> T cloneX(final T x) throws IOException,
			ClassNotFoundException {
		final ByteArrayOutputStream bout = new ByteArrayOutputStream();
		final CloneOutput cout = new CloneOutput(bout);
		cout.writeObject(x);
		final byte[] bytes = bout.toByteArray();

		final ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
		final CloneInput cin = new CloneInput(bin, cout);

		@SuppressWarnings("unchecked")
		final// thanks to Bas de Bakker for the tip!
		T clone = (T) cin.readObject();
		return clone;
	}

	/**
	 * Close.
	 * 
	 * @param inputStreams
	 *            the input streams
	 */
	private static void close(final InputStream... inputStreams) {
		for (final InputStream inputStream : inputStreams) {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (final IOException e) {
				}
			}
		}
	}

	/**
	 * Deserialize.
	 * 
	 * @param inputStream
	 *            the input stream
	 * 
	 * @return the object
	 * 
	 * @throws SerializationUtilException
	 *             the serialization util exception
	 */
	public static Object deserialize(final InputStream inputStream)
			throws SerializationUtilException {
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(inputStream);
			return ois.readObject();
		} catch (final Exception e) {
			throw new SerializationUtilException(
					"Error on attempt to deserialize object.", e);
		} finally {
			close(ois);
		}
	}

	/**
	 * Serialize.
	 * 
	 * @param object
	 *            the object
	 * 
	 * @return the input stream
	 * 
	 * @throws SerializationUtilException
	 *             the serialization util exception
	 */
	public static InputStream serialize(final Object object)
			throws SerializationUtilException {
		try {
			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			final ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
			oos.close();
			final ByteArrayInputStream bais = new ByteArrayInputStream(baos
					.toByteArray());
			return bais;
		} catch (final IOException e) {
			throw new SerializationUtilException(
					"Error on attempt to serialize object.", e);
		}
	}

}
