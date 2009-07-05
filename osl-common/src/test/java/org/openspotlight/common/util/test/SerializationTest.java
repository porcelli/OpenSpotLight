package org.openspotlight.common.util.test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.openspotlight.common.util.Serialization.readFromBase64;
import static org.openspotlight.common.util.Serialization.readFromBytes;
import static org.openspotlight.common.util.Serialization.readFromInputStream;
import static org.openspotlight.common.util.Serialization.serializeToBase64;
import static org.openspotlight.common.util.Serialization.serializeToBytes;
import static org.openspotlight.common.util.Serialization.serializeToOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.openspotlight.common.util.Serialization;

/**
 * Test class for {@link Serialization}
 * 
 * @author feu
 * 
 */
public class SerializationTest {

	@Test
	public void shouldWriteAndReadBytes() throws Exception {
		Double value = new Double(1.0);
		byte[] valueAsBytes = serializeToBytes(value);
		Double readedValue = readFromBytes(valueAsBytes);
		assertThat(readedValue, is(value));
	}

	@Test
	public void shouldWriteAndReadStream() throws Exception {
		Double value = new Double(1.0);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		serializeToOutputStream(value, baos);
		byte[] content = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		Double readedValue = readFromInputStream(bais);
		assertThat(readedValue, is(value));
	}

	@Test
	public void shouldWriteAndReadBase64String() throws Exception {
		Double value = new Double(1.0);
		String base64 = serializeToBase64(value);
		Double readedValue = readFromBase64(base64);
		assertThat(readedValue, is(value));
	}

}
