package org.openspotlight.structure.test.domain.type;

import java.text.Collator;

public class DatabaseView extends TableView {
    private static final long serialVersionUID = -314749684752361875L;

    public DatabaseView(
                         String key, long contextHandle ) {
        super(key, contextHandle, Collator.PRIMARY);
    }
}
