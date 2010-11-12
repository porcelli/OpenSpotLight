package org.openspotlight.storage;

import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;

/**
 * Builder that creates {@link NodeKey} instances.
 * 
 * @author feuteston
 * @author porcelli
 */
public interface NodeKeyBuilder {

    /**
     * Creates the {@link NodeKey} instance based on builder data stack.
     * 
     * @return the node key instance
     */
    NodeKey andCreate();

    /**
     * Adds a new {@link SimpleKey} into stack.
     * 
     * @param keyName the key name
     * @param value the key value
     * @return self builder
     */
    NodeKeyBuilder withSimpleKey(String keyName, String value);

    /**
     * Sets the parent's key of the current {@link NodeKey}.
     * 
     * @param parentKey the parent key
     * @return self builder
     */
    NodeKeyBuilder withParent(String parentKey);

    /**
     * Pushes a new {@link NodeKey} into builder stack to define the parent characteristics. <br>
     * <b>Note:</b> use this method once you already defined all data related to the node, once there is no way to pop the stack.
     * 
     * @param partition the parent partition
     * @param nodeType the parent node type
     * @return the pushed
     */
    NodeKeyBuilder withParent(Partition partition, String nodeType);
}
