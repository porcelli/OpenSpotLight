package org.openspotlight.graph;

public class SLInvalidLinkPropertyTypeException extends SLGraphSessionException {

	private static final long serialVersionUID = 1L;
	
	public SLInvalidLinkPropertyTypeException(String name, Class<?> invalidType, Class<?>... allowedTypes) {
		super(getMessage(name, invalidType, allowedTypes));
	}

	public SLInvalidLinkPropertyTypeException(String message) {
		super(message);
	}

	public SLInvalidLinkPropertyTypeException(Throwable cause) {
		super(cause);
	}

	private static String getMessage(String name, Class<?> invalidType, Class<?>... allowedTypes) {
		StringBuilder message = new StringBuilder();
		message.append("Value of property ")
			.append(name).append(" cannot be retrieved as ")
			.append(invalidType.getName()).append(". ");
		for (int i = 0; i < allowedTypes.length; i++) {
			if (i > 0) message.append(", ");
			message.append(allowedTypes[i].getName());
		}
		message.append(" or super type can be used instead.");
		return message.toString();
	}

}
