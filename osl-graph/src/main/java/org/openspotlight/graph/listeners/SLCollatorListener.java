package org.openspotlight.graph.listeners;

import static org.openspotlight.graph.SLCommonSupport.toSimplePropertyName;

import java.io.Serializable;
import java.text.Collator;

import org.openspotlight.graph.SLAbstractGraphSessionEventListener;
import org.openspotlight.graph.SLCollatorSupport;
import org.openspotlight.graph.SLGraphSessionException;
import org.openspotlight.graph.SLNodePropertyEvent;
import org.openspotlight.graph.persistence.SLPersistentNode;
import org.openspotlight.graph.persistence.SLPersistentProperty;
import org.openspotlight.graph.persistence.SLPersistentTreeSessionException;

public class SLCollatorListener extends SLAbstractGraphSessionEventListener {
	
	@Override
	public void nodePropertySet(SLNodePropertyEvent event) throws SLGraphSessionException {
		try {
			SLPersistentProperty<? extends Serializable> pProperty = event.getPersistentProperty();
			if (pProperty.getValue() instanceof String) {
				String name = toSimplePropertyName(pProperty.getName());
				String value = pProperty.getValue().toString();
				
				String primaryKey = SLCollatorSupport.getCollatorKey(Collator.PRIMARY, value);
				String secondaryKey = SLCollatorSupport.getCollatorKey(Collator.SECONDARY, value);
				String tertiaryKey = SLCollatorSupport.getCollatorKey(Collator.TERTIARY, value);
				
				String primaryPropName = SLCollatorSupport.getCollatorPropName(name, Collator.PRIMARY);
				String secondaryPropName = SLCollatorSupport.getCollatorPropName(name, Collator.SECONDARY);
				String tertiaryPropName = SLCollatorSupport.getCollatorPropName(name, Collator.TERTIARY);
				
				SLPersistentNode pNode = pProperty.getNode();
				pNode.setProperty(String.class, primaryPropName, primaryKey);
				pNode.setProperty(String.class, secondaryPropName, secondaryKey);
				pNode.setProperty(String.class, tertiaryPropName, tertiaryKey);
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to update callation property data.", e);
		}
	}
	
	@Override
	public void nodePropertyRemoved(SLNodePropertyEvent event) throws SLGraphSessionException {
		try {
			if (event.isString()) {
				String name = event.getPropertyName();
				String primaryPropName = SLCollatorSupport.getCollatorPropName(name, Collator.PRIMARY);
				String secondaryPropName = SLCollatorSupport.getCollatorPropName(name, Collator.SECONDARY);
				String tertiaryPropName = SLCollatorSupport.getCollatorPropName(name, Collator.TERTIARY);
				SLPersistentNode pNode = event.getPNode();
				pNode.getProperty(String.class, primaryPropName).remove();
				pNode.getProperty(String.class, secondaryPropName).remove();
				pNode.getProperty(String.class, tertiaryPropName).remove();
			}
		}
		catch (SLPersistentTreeSessionException e) {
			throw new SLGraphSessionException("Error on attempt to remove callation property data.", e);
		}
	}
}

