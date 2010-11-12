package org.openspotlight.storage;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import org.openspotlight.storage.domain.key.NodeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey;
import org.openspotlight.storage.domain.key.NodeKey.CompositeKey.SimpleKey;
import org.openspotlight.storage.domain.key.NodeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl;
import org.openspotlight.storage.domain.key.NodeKeyImpl.CompositeKeyImpl.SimpleKeyImpl;

/**
 * Internal (default) implementation of {@link NodeKeyBuilder}.
 * 
 * @author feuteston
 * @author porcelli
 */
public class NodeKeyBuilderImpl implements NodeKeyBuilder {

    private final NodeKeyBuilderImpl child;
    private final Set<SimpleKey>     localEntries = newHashSet();

    private String                   parentKey;

    private final Partition          partition;

    private final String             type;

    private NodeKeyBuilderImpl(final String type, final NodeKeyBuilderImpl child, final Partition partition) {
        this.type = type;
        this.child = child;
        this.partition = partition;
    }

    public NodeKeyBuilderImpl(final String type, final Partition partition) {
        this.type = type;
        this.partition = partition;
        this.child = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKey andCreate() {
        NodeKey currentKey = null;
        NodeKeyBuilderImpl currentBuilder = this;
        if (parentKey == null) {
            do {
                final CompositeKey localKey = new CompositeKeyImpl(currentBuilder.localEntries, currentBuilder.type);
                currentKey =
                    new NodeKeyImpl(localKey, currentKey != null ? currentKey.getKeyAsString() : null,
                        currentBuilder.partition);
                currentBuilder = currentBuilder.child;
            } while (currentBuilder != null);
        } else {
            final CompositeKey localKey = new CompositeKeyImpl(currentBuilder.localEntries, currentBuilder.type);
            currentKey = new NodeKeyImpl(localKey, parentKey, partition);
        }
        return currentKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKeyBuilder withParent(final Partition newPartition, final String nodeType) {
        return new NodeKeyBuilderImpl(nodeType, this, newPartition);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKeyBuilder withParent(final String parentId) {
        this.parentKey = parentId;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeKeyBuilder withSimpleKey(final String propertyName, final String value) {
        this.localEntries.add(new SimpleKeyImpl(propertyName, value));
        return this;
    }
}
