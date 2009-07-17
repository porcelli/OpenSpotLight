package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Sha1.getSha1Signature;
import static org.openspotlight.common.util.Sha1.getSha1SignatureEncodedAsBase64;

import org.junit.Test;
import org.openspotlight.common.util.Sha1;

/**
 * Test class for {@link Sha1}
 * 
 * @author feu
 * 
 */
public class Sha1Test {

	@Test
	public void shouldCreateSha1Signature() throws Exception {
		byte[] sha1 = getSha1Signature("content".getBytes());
		assertThat(sha1, is(notNullValue()));
		assertThat(sha1.length, is(not(0)));
	}

	@Test
	public void shouldCreateSha1SignatureAsBase64String() throws Exception {
		String sha1 = getSha1SignatureEncodedAsBase64("content".getBytes());
		assertThat(sha1, is(notNullValue()));
		assertThat(sha1.length(), is(not(0)));
	}
}
