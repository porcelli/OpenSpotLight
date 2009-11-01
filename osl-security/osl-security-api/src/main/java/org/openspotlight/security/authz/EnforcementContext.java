package org.openspotlight.security.authz;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnforcementContext {
    private Map<String, Object> attributes;

    public EnforcementContext() {
        this.attributes = new HashMap<String, Object>();
    }

    public Object getAttribute( String name ) {
        return this.attributes.get(name);
    }

    public void setAttribute( String name,
                              Object attribute ) {
        this.attributes.put(name, attribute);
    }

    public Set<String> getNames() {
        return this.attributes.keySet();
    }

    public Object[] getValues() {
        return this.attributes.values().toArray();
    }

    public void clear( String name ) {
        this.attributes.remove(name);
    }

    public void clearAll() {
        this.attributes.clear();
    }
}
