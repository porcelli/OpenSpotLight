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
 * @author feu
 * 
 */
public class Sha1 {

	private static final Digester DIGESTER = new Digester("SHA-1");

	/**
	 * Should not be instantiated
	 */
	private Sha1() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Returns a sha-1 signature for that content.
	 * 
	 * @param content
	 * @return
	 * @throws SLException
	 */
	public static byte[] getSha1Signature(byte[] content) throws SLException {
		checkNotNull("content", content);
		try {
			return DIGESTER.digest(content);
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

	/**
	 * Returns a sha-1 signature for that content as a base64 string.
	 * 
	 * @param content
	 * @return
	 * @throws SLException
	 */
	public static String getSha1SignatureEncodedAsBase64(byte[] content)
			throws SLException {
		checkNotNull("content", content);
		try {
			byte[] sha1 = getSha1Signature(content);
			String result = new String(encodeBase64(sha1));
			return result;
		} catch (Exception e) {
			throw logAndReturnNew(e, SLException.class);
		}
	}

}
