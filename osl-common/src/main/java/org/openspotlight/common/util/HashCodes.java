package org.openspotlight.common.util;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Float.floatToIntBits;
import static org.openspotlight.common.util.Exceptions.logAndThrow;

/**
 * Helper class to build hashCode methods in a secure and concise way. All the
 * hash functions for primitive types was created based on Effective Java book.
 * 
 * to be used like this...
 * 
 * <pre>
 * private volatile hashcode;
 * 
 * public int hashCode(){
 *  int result = hashcode;
 *  if(result = 0){
 *   result = hashOf(attribute1,attribute2,..);
 *   hashcode = result;
 *  }
 *  return result;
 * }
 * 
 * </pre>
 * 
 * or like this...
 * 
 * <pre>
 * public int hashCode(){
 *  int result = hashcode;
 *  if(result = 0){
 *   result = hashOf(attribute1,attribute2,..);
 *   hashcode = result;
 *  }
 *  return result;
 * }
 * </pre>
 * 
 * 
 * @author feu
 * 
 */
public class HashCodes {

	/**
	 * Should not be instantiated
	 */
	private HashCodes() {
		logAndThrow(new IllegalStateException(Messages
				.getString("invalidConstructor"))); //$NON-NLS-1$
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(boolean b) {
		return b ? 1 : 0;
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(char c) {
		return (int) c;
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(short s) {
		return (int) s;
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(byte b) {
		return (int) b;
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(int i) {
		return i;
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(long l) {
		return (int) (l ^ (l >>> 32));
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(float f) {
		return floatToIntBits(f);
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(double d) {
		return hashOf(doubleToLongBits(d));
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book):
	 * 
	 * <pre>
	 * private volatile hashcode;
	 * 
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = 17;
	 *   result = 31 * result + hashOf(attribute1);
	 *   result = 31 * result + hashOf(attribute2);
	 *   result = 31 * result + hashOf(attribute3);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * 
	 * </pre>
	 * 
	 * @param b
	 * @return
	 */
	public static int hashOf(Object o) {
		return o == null ? 0 : o.hashCode();
	}

	/**
	 * Hash helper method to be used like this (based on Effective Java book).
	 * To be used like this:
	 * 
	 * <pre>
	 * public int hashCode(){
	 *  int result = hashcode;
	 *  if(result = 0){
	 *   result = hashOf(attribute1,attribute2,..);
	 *   hashcode = result;
	 *  }
	 *  return result;
	 * }
	 * </pre>
	 * 
	 * @param attributes
	 * @return
	 */
	public static int hashOf(Object... attributes) {
		int result = 17;
		for (Object attribute : attributes) {
			result = 31 * result + hashOf(attribute);
		}
		return result;
	}

}
