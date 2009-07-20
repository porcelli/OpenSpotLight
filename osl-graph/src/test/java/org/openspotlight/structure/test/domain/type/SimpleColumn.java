package org.openspotlight.structure.test.domain.type;

import java.text.Collator;

public class SimpleColumn extends DatabaseColumn {
    private static final long serialVersionUID = -6603488064574426059L;

    public SimpleColumn(
                         String key, long contextHandle ) {
        super(key, contextHandle, Collator.PRIMARY);
    }
}
