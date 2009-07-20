package org.openspotlight.structure.test.domain.type;

import java.text.Collator;

public class ComputedColumn extends DatabaseColumn {
    private static final long serialVersionUID = -810060479848042430L;

    public ComputedColumn(
                           String key, long contextHandle ) {
        super(key, contextHandle, Collator.PRIMARY);
    }
}
