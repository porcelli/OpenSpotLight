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
 * @author feu
 * 
 */
public class Serialization {

	/**
	 * Should not be instantiated
	 */
	private Serialization() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
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
			E object, OutputStream outputStream) throws SLException {
		checkNotNull("object", object);
		checkNotNull("outputStream", outputStream);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(object);
			oos.flush();
			oos.close();
		} catch (Exception e) {
			logAndThrowNew(e, SLException.class);
		}
	}

	/**
	 * Returns a byte array with the object serialized.
	 * 
	 * @param <E>
	 * @param object
	 * @return
	 * @throws SLException
	 */
	public static <E extends Serializable> byte[] serializeToBytes(E object)
			throws SLException {
		checkNotNull("object", object);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			serializeToOutputStream(object, baos);
			byte[] result = baos.toByteArray();
			return result;
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

	/**
	 * Serialize the object passed as parameter to a base64 string.
	 * 
	 * @param <E>
	 * @param object
	 * @return
	 * @throws SLException
	 */
	public static <E extends Serializable> String serializeToBase64(E object)
			throws SLException {
		checkNotNull("object", object);
		try {
			byte[] resultAsByte = serializeToBytes(object);
			byte[] resultAsBase64 = encodeBase64(resultAsByte);
			String base64String = new String(resultAsBase64);
			return base64String;
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

	/**
	 * Reads an object from serialized data.
	 * 
	 * @param <E>
	 * @param bytes
	 * @return
	 * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Serializable> E readFromBytes(byte[] bytes)
			throws SLException {
		checkNotNull("bytes", bytes);
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			E result = (E)readFromInputStream(bais);
			return result;
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

	/**
	 * Reads an object from a base64 string.
	 * 
	 * @param <E>
	 * @param string
	 * @return
	 * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Serializable> E readFromBase64(String string)
			throws SLException {
		checkNotNull("string", string);
		try {
			byte[] base64encoded = string.getBytes();
			byte[] base64decoded = decodeBase64(base64encoded);
			E result = (E)readFromBytes(base64decoded);
			return result;
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

	/**
	 * Reads an object from an output stream.
	 * 
	 * @param <E>
	 * @param inputStream
	 * @return
	 * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	public static <E extends Serializable> E readFromInputStream(
			InputStream inputStream) throws SLException {
		checkNotNull("inputStream", inputStream);
		try {
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			E result = (E) ois.readObject();
			ois.close();
			return result;
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

}
