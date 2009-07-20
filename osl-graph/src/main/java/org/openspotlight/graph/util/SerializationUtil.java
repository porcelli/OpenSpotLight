package org.openspotlight.graph.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class SerializationUtil {
	
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
