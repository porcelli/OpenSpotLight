/**
 * 
 */
package org.openspotlight.federation.util;

import org.openspotlight.federation.data.ConfigurationNode;
import org.openspotlight.federation.data.InstanceMetadata.ConfigurationNodeVisitor;

/**
 * This class marks all configuration nodes as dirty. It should be useful on importing scenarios.
 * 
 * @author feu
 */
public final class MarkAllAsDirtyVisitor implements ConfigurationNodeVisitor {
    public void visitNode( final ConfigurationNode node ) {
        node.getInstanceMetadata().getSharedData().fireNodeChange(node, node);
    }
}
