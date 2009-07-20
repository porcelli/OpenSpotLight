package org.openspotlight.structure.test.domain.type;

import java.text.Collator;

import org.openspotlight.structure.elements.SLSimpleType;

public class DatabaseColumn extends SLSimpleType {
    private static final long serialVersionUID = 8578868341396453683L;

    public DatabaseColumn(
                           String key, long contextHandle ) {
        super(key, contextHandle, Collator.PRIMARY);
    }

    public DatabaseColumn(
                           String key, long contextHandle, int collatorLevel ) {
        super(key, contextHandle, Collator.PRIMARY);
    }
}
